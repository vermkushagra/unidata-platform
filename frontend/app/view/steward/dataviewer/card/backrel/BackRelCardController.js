Ext.define('Unidata.view.steward.dataviewer.card.backrel.BackRelCardController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataviewer.backrelcard',

    accessDeniedText: Unidata.i18n.t('dataviewer>accessDeniedHasBackRels'),

    loadAndDisplayBackRelsCard: function () {
        var view        = this.getView(),
            loadCfg;

        loadCfg = {
            metaRecord: view.getMetaRecord(),
            etalonId: view.getEtalonId(),
            dateFrom: null,
            dateTo: null,
            relDirection: 'from'
        };

        Unidata.view.steward.dataviewer.DataViewerLoader.loadBackRelations(loadCfg)
            .then(function (relationsDigest) {
                    view.setRelationsDigest(relationsDigest);
                    view.displayBackRels();
                    view.fireEvent('load', view, relationsDigest);
                },
                function () {
                    //TODO: implement me
                    view.fireEvent('refreshend');
                    throw new Error(Unidata.i18n.t('validation:loadFailed', {name: Unidata.i18n.t('glossary:backRels')}));
                })
            .done();

        //TODO: Может вместо events load возвращать promise?
    },

    /**
     * Отобразить содержимое экрана "Обратные ссылки"
     */
    displayBackRels: function () {
        var view            = this.getView(),
            contentContainer = view.contentContainer,
            relationsDigest = view.getRelationsDigest(),
            me              = this,
            isBackRelsExist;

        contentContainer.removeAll();

        isBackRelsExist = this.isBackRelationsExist(relationsDigest);

        if (!isBackRelsExist) {
            // TODO: Использовать нормальный компонент
            contentContainer.add({
                xtype: 'container',
                html: Unidata.i18n.t('dataviewer>backRelsNotExist')
            });
        } else {
            relationsDigest.forEach(me.displayRelationDigestPanel.bind(this));
        }

        view.fireEvent('refreshend');
    },

    isBackRelationsExist: function (relationsDigest) {
        var status;

        status = Ext.Array.some(relationsDigest, function (relationDigest) {
            return relationDigest.hasRecords;
        });

        return status;
    },

    /**
     * Построить обратные ссылки для одной метасвязи
     *
     * @param relationDigest
     */
    displayRelationDigestPanel: function (relationDigest) {
        var view                              = this.getView(),
            contentContainer                  = view.contentContainer,
            fields,
            store                             = relationDigest.store,
            relation                          = relationDigest.relation,
            relationViewMetaRecord            = relationDigest.relationViewMetaRecord,
            relPanelContainer,
            relDigestView,
            filters                           = [{property: 'displayable', value: true}],
            relationViewMetaRecordName        = relationViewMetaRecord.get('name'),
            totalCount                        = store.totalCount;

        if (!relationDigest.hasRecords) {
            return;
        }

        fields = Unidata.util.UPathMeta.buildAttributePaths(relationViewMetaRecord, filters);
        relPanelContainer = this.buildMetaRelationPanel(relation, relationViewMetaRecord);

        if (this.isUserHasMetaReadRights(relationViewMetaRecordName) && totalCount > 0) {
            relDigestView = this.buildRelationDigestPanel(relationViewMetaRecord, fields, store);
            relPanelContainer.add(relDigestView);
            contentContainer.add(relPanelContainer);
            relDigestView.lookupReference('pagingToolbar').doRefresh();
        } else {
            this.makeMetaRelationPanelAccessDenied(relPanelContainer, store.totalCount);
            contentContainer.add(relPanelContainer);
        }
    },

    /**
     * Создать панель обратных ссылок для определенной метасвязи
     *
     * @param relationViewMetaRecordDisplayName Отображаемое имя метарекорда
     * @param relationDisplayName Отображаемое имя метасвязи
     * @returns {Ext.panel.Panel|*}
     */
    buildMetaRelationPanel: function (relation, relationViewMetaRecord) {
        var relationViewMetaRecordDisplayName = relationViewMetaRecord.get('displayName'),
            relationDisplayName = relation.get('displayName'),
            panelView,
            title;

        title = Ext.String.format('{0} | {1}', relationViewMetaRecordDisplayName, relationDisplayName);

        panelView = Ext.create('Ext.panel.Panel', {
            title: title,
            collapsible: true,
            collapsed: true,
            titleCollapse: true,
            ui: 'un-card',
            referenceHolder: true,
            relationName: relation.get('name'), // для нужд QA отдела
            relationType: relation.get('relType'), // для нужд QA отдела
            relationFromEntity: relation.get('fromEntity'), // для нужд QA отдела
            relationToEntity: relation.get('toEntity'), // для нужд QA отдела
            bodyPadding: 10
        });

        return panelView;
    },

    /**
     * Создать
     *
     * @param relationViewMetaRecord
     * @param paths
     * @param store
     * @returns {*|Ext.grid.Panel}
     */
    buildRelationDigestPanel: function (relationViewMetaRecord, paths, store) {
        var view,
            columns;

        columns = Unidata.util.ColumnConfigurator.buildColumnsByAttributesPaths(paths, relationViewMetaRecord);
        view = this.buildRelationDigestGridPanel(paths, columns, store);

        return view;
    },

    /**
     * Создать grid panel для обратных ссылок, относящихся к определенной связи
     *
     * @param paths
     * @param columns
     * @param store
     * @returns {Ext.grid.Panel|*}
     */
    buildRelationDigestGridPanel: function (paths, columns, store) {
        var view = this.getView(),
            panel;

        //TODO: implement another record opening in a tab
        function onItemClick (panel, item) {
            var cfg,
                DataRecordBundleUtil = Unidata.util.DataRecordBundle,
                dataRecordBundle;

            dataRecordBundle = DataRecordBundleUtil.buildDataRecordBundle({
                etalonId: item.get('etalonId')
            });

            // metaRecord - null, т.к. в digest у нас нет attributeGroups
            cfg = {
                dataRecordBundle: dataRecordBundle,
                metaRecord: null
            };

            // datarecordopen event cfg:
            //
            // dataRecordBundle {Unidata.util.DataRecordBundle} Набор структур по отображению записей
            // searchHit {Unidata.model.search.SearchHit} Результат поиска записи
            // metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель (optional)
            // saveCallback {function} Функция, вызываемая после сохранения открытой записи
            view.fireEvent('datarecordopen', cfg, item);
        }

        function onGridPanelRefresh (self) {
            var grid    = self.findParentByType('grid'),
                toolbar = grid.lookupReference('pagingToolbar');

            if (grid.rendered && toolbar && toolbar.rendered) {
                if (grid.getStore().getTotalCount() <= store.getPageSize()) {
                    toolbar.hide();
                } else {
                    toolbar.show();
                }
            }
        }

        panel = Ext.create('Ext.grid.Panel', {
            fields: paths,
            columns: columns,
            store: store,
            emptyText: Unidata.i18n.t('dataviewer>backRelsNotExist').toLowerCase(),
            cls: 'un-grid-searchresult',
            referenceHolder: true,
            viewConfig: {
                deferEmptyText: false,
                listeners: {
                    refresh: onGridPanelRefresh
                }
            },
            disableSelection: true,
            minHeight: 70,
            //enableColumnResize: false,
            listeners: {
                itemclick: onItemClick
            },
            bbar: {
                xtype: 'pagingtoolbar',
                reference: 'pagingToolbar',
                store: store,
                cls: 'paging-toolbar',
                hidden: true,
                displayInfo: true,
                emptyMsg: Unidata.i18n.t('dataviewer>selectNoRecords'),
                displayMsg: Unidata.i18n.t('glossary:displayCounter'),
                hideRefreshButton: true
            }
        });

        return panel;
    },

    /**
     * Имеет ли пользователь права на чтение реестра
     *
     * @returns {*|boolean}
     */
    isUserHasMetaReadRights: function (relationViewMetaRecordName) {
        return Unidata.Config.isUserAdmin() || Unidata.Config.userHasRight(relationViewMetaRecordName, 'read');
    },

    /**
     * Перевести панель в состояние "Доступ запрещен" (если у пользователь отсутствуют права на чтение реестра)
     *
     * @param relPanelView
     * @param backRelCount
     */
    makeMetaRelationPanelAccessDenied: function (relPanelView) {
        relPanelView.addBodyCls('info-text');
        relPanelView.setHtml(this.accessDeniedText);
    },

    updateReadOnly: function () {
    }
});
