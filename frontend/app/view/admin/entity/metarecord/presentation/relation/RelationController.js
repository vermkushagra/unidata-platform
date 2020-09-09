Ext.define('Unidata.view.admin.entity.metarecord.presentation.relation.RelationController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.presentation.relation',

    onRender: function () {
        var viewModel = this.getViewModel(),
            metaRecord = viewModel.get('currentRecord'),
            entityType = metaRecord.getType();

        if (entityType === 'Entity') {
            this.redrawRelationGroups();
        }
    },

    /**
     * Построить контейнер для типа
     * @param relationGroup
     * @param customCfg
     * @returns {Ext.panel.Panel|*}
     */
    buildRelationTypeTablet: function (relationGroup, customCfg) {
        var SorterUtil = Unidata.util.Sorter,
            RelationModel = Unidata.model.data.RelationTimeline,
            relType = relationGroup.get('relType'),
            relationNamesOrdered = relationGroup.get('relations'),
            viewModel = this.getViewModel(),
            metaRecord = viewModel.get('currentRecord'),
            relations = metaRecord.relations(),
            me = this,
            tablet,
            cfg,
            store,
            title;

        customCfg = customCfg || {};

        title = RelationModel.getRelationTypeDisplayName(relType);

        store = this.buildRelationFilteredChainedStore(relations, relationGroup);

        // сортируем связи в соответствии со списком
        if (Ext.isArray(relationNamesOrdered) && relationNamesOrdered.length > 0) {
            store.setSorters({
                sorterFn: SorterUtil.byListSorterFn.bind(this, relationNamesOrdered, 'name', 'displayName'),
                direction: 'ASC'
            });
        }

        store.sorters.removeAll();

        cfg = {
            xtype: 'panel',
            title: title,
            cls: 'un-presentation-relation-type',
            ui: 'un-presentation-panel',
            scrollable: 'vertical',
            relationGroup: relationGroup,
            relType: relationGroup.get('relType'),  // добавлено для работы тестового фреймворка. Не удалять!
            referenceHolder: true,
            relationGrid: null,
            items: [
                {
                    xtype: 'grid',
                    store: store,
                    reference: 'relationGrid',
                    hideHeaders: true,
                    selectionModel: {
                        locked: true
                    },
                    columns: [
                        {
                            dataIndex: 'displayName',
                            flex: 1
                        }
                    ],
                    listeners: {
                        viewready: this.onGridViewReady.bind(this)
                    },
                    viewConfig: {
                        plugins: {
                            ptype: 'gridviewdragdrop',
                            pluginId: 'ddplugin',
                            ddGroup: relType + 'RelationGroupsDDGroup',
                            dragText: Unidata.i18n.t('admin.metamodel>moveRelation')
                        },
                        listeners: {
                            drop: function () {
                                var relationTypeGrid = this.up('grid'),
                                    relationTypePanel = this.up('panel[cls=un-presentation-relation-type]'),
                                    relationGroup = relationTypePanel.relationGroup,
                                    relations = relationTypeGrid.getStore().getRange(),
                                    relationNames;

                                relationNames = me.mapRelationsToRelationNames(relations);
                                me.setRelationGroupRelations(relationGroup, relationNames);
                            }
                        }
                    }

                }
            ],
            width: 350,
            height: 150,
            listeners: {
                render: this.relationTypeOnRender.bind(this)
            },
            tools: [
                {
                    xtype: 'button',
                    reference: 'moveButton',
                    cls: 'un-btn-move',
                    iconCls: 'icon-move',
                    scale: 'small',
                    ui: 'un-toolbar-block-panel',
                    text: '',
                    tooltip: Unidata.i18n.t('admin.metamodel>move')
                }
            ]
        };

        cfg = Ext.apply(cfg, customCfg);
        //cfg = {
        //    xtype: 'container',
        //    items: [cfg]
        //};

        tablet = Ext.create(cfg);
        tablet.relationGrid = tablet.lookupReference('relationGrid');

        return tablet;
    },

    onGridViewReady: function (grid) {
        this.initTooltip(grid);
        this.syncReadOnly(grid);
    },

    initTooltip: function (grid) {
        var me       = this,
            gridView = grid.view;

        this.toolTip = Ext.create('Ext.tip.ToolTip', {
            target: gridView.el,
            delegate: '.x-grid-cell',
            trackMouse: true,
            renderTo: Ext.getBody(),
            listeners: {
                beforeshow: function (tip) {
                    var row                    = tip.triggerElement.parentElement,
                        relation              = gridView.getRecord(row),
                        value,
                        tipTemplate,
                        tipHtml;

                    if (!relation) {
                        return false;
                    }

                    value = relation.get('displayName');

                    if (!value) {
                        return false;
                    }

                    tipTemplate = Ext.create('Ext.Template', [
                        '{value:htmlEncode}'
                    ]);
                    tipTemplate.compile();
                    tipHtml = tipTemplate.apply({
                        value: value
                    });

                    tip.update(tipHtml);
                }
            }
        });
    },

    relationTypeOnRender: function (item) {
        var dd,
            el,
            overrides,
            me = this;

        overrides = {
            // Called the instance the element is dragged.
            b4StartDrag: function () {
                // Cache the drag element
                if (!this.el) {
                    this.el = Ext.get(this.getEl());
                }

                //Cache the original XY Coordinates of the element, we'll use this later.
                this.originalXY = this.el.getXY();
                me.setDisabledAllRelationPanels(true);
            },
            // Called when element is dropped in a spot without a dropzone, or in a dropzone without matching a ddgroup.
            onInvalidDrop: function () {
                // Set a flag to invoke the animated repair
                this.invalidDrop = true;
            },
            // Called when the drag operation completes
            endDrag: function () {
                var animCfgObj;

                // Invoke the animation if the invalidDrop flag is set to true
                if (this.invalidDrop === true) {
                    // Create the animation configuration object
                    animCfgObj = {
                        easing: 'elasticOut',
                        duration: 1,
                        scope: this,
                        callback: function () {
                            // Remove the position attribute
                            this.el.dom.style.position = '';
                        }
                    };

                    // Apply the repair animation
                    this.el.setXY(this.originalXY, animCfgObj);
                    delete this.invalidDrop;
                }

                me.setDisabledAllRelationPanels(false);
            },
            // Called upon successful drop of an element on a DDTarget with the same
            onDragDrop: function (evtObj, targetElId) {
                // Wrap the drop target element with Ext.Element
                var dropEl = Ext.get(targetElId),
                    animCfgObj,
                    dragRelationType,
                    dropRelationType,
                    dragRelationGroup,
                    dropRelationGroup;

                // Create the animation configuration object
                animCfgObj = {
                    easing: 'elasticOut',
                    duration: 1,
                    scope: this,
                    callback: function () {
                        // Remove the position attribute
                        this.el.dom.style.position = '';
                    }
                };

                dragRelationType  = this.el.component;
                dropRelationType = dropEl.component;
                dragRelationGroup = dragRelationType.relationGroup;
                dropRelationGroup = dropRelationType.relationGroup;

                this.removeInvitation(dropEl);
                me.moveRelationGroup(dragRelationGroup.get('row'), dropRelationGroup.get('row'), this.isDropAfter(dropEl));
            },
            onDragOver: function (evtObj, targetElId) {
                var dropInvitationBaseCls = 'un-drop-invitation',
                    dropEl = Ext.get(targetElId),
                    addCls,
                    removeCls;

                if (this.isDropAfter(dropEl)) {
                    addCls = Ext.String.format('{0}-{1}', dropInvitationBaseCls, 'after');
                    removeCls = Ext.String.format('{0}-{1}', dropInvitationBaseCls, 'before');
                } else {
                    addCls = Ext.String.format('{0}-{1}', dropInvitationBaseCls, 'before');
                    removeCls = Ext.String.format('{0}-{1}', dropInvitationBaseCls, 'after');
                }

                dropEl.addCls(addCls);
                dropEl.removeCls(removeCls);
            },
            // Only called when element is dragged out of a dropzone with the same ddgroup
            onDragOut: function (evtObj, targetElId) {
                var dropEl = Ext.get(targetElId);

                this.removeInvitation(dropEl);
            },
            removeInvitation: function (dropEl) {
                var dropInvitationBaseCls = 'un-drop-invitation',
                    beforeCls,
                    afterCls;

                beforeCls = Ext.String.format('{0}-{1}', dropInvitationBaseCls, 'before');
                afterCls = Ext.String.format('{0}-{1}', dropInvitationBaseCls, 'after');

                dropEl.removeCls(beforeCls);
                dropEl.removeCls(afterCls);
            },
            isDropAfter: function (dropEl) {
                var dropElHeight = dropEl.getHeight(),
                    dropElY = dropEl.getY(),
                    dropElCenterY = dropElY + dropElHeight / 2,
                    elY = this.el.getY();

                return elY > dropElCenterY;
            }
        };

        el = item.getEl();
        dd = Ext.create('Ext.dd.DD', el, 'relationGroupsDDGroup', {
            isTarget: true
        });

        Ext.apply(dd, overrides);

        item.customDragAndDrop = dd;
    },

    /**
     * Построить chainedStore для связей определенного типа на базе store всех связей
     *
     * @param relations
     * @param relationGroup
     * @returns {Ext.data.ChainedStore|*}
     */
    buildRelationFilteredChainedStore: function (relations, relationGroup) {
        var chainedStore,
            relationType = relationGroup.get('relType');

        chainedStore = Ext.create('Ext.data.ChainedStore', {
            source: relations,
            filters: [
                {
                    property: 'relType',
                    value: relationType
                }
            ]
        });

        return chainedStore;
    },

    /**
     * Переместить панель группы типа связей с одной позиции (oldIndex) на другую (newIndex)
     *
     * @param oldIndex Старая позиция (строка, row)
     * @param newIndex Новая позиция (строка, row)
     * @param isDropAfter Признак того, что надо вставлять после newIndex
     */
    moveRelationGroup: function (oldIndex, newIndex, isDropAfter) {
        var viewModel = this.getViewModel(),
            metaRecord = viewModel.get('currentRecord'),
            relationGroups = metaRecord.relationGroups(),
            count = relationGroups.count(),
            relationGroupsArray;

        isDropAfter = Ext.isBoolean(isDropAfter) ? isDropAfter : false;

        if (count === 0) {
            return;
        }

        // тащим снизу вверх
        if (oldIndex > newIndex) {
            if (isDropAfter) {
                newIndex = newIndex + 1;
                newIndex = newIndex < count ? newIndex : count - 1;
            } else {
                newIndex = newIndex > 0 ? newIndex : 0;
            }
        } else {
            if (isDropAfter) {
                //newIndex = newIndex + 1;
                newIndex = newIndex < count ? newIndex : count - 1;
            } else {
                newIndex = newIndex - 1;
                newIndex = newIndex > 0 ? newIndex : 0;
            }
        }

        relationGroupsArray = relationGroups.getRange();
        relationGroupsArray = Ext.Array.move(relationGroupsArray, oldIndex, newIndex);
        relationGroups.removeAll();
        Ext.Array.each(relationGroupsArray, function (relationGroup, index) {
            // reindex
            relationGroup.set('row', index);
            relationGroups.add(relationGroup);
        });
        this.redrawRelationGroups();
    },

    /**
     * Проставить новый список связей для relationGroup
     * @param relationGroup
     * @param relations
     */
    setRelationGroupRelations: function (relationGroup, relations) {
        var found;

        found = this.findRelationGroup(relationGroup);

        found.set('relations', relations);
    },

    /**
     * Перерисовать панели RelationGroup
     */
    redrawRelationGroups: function () {
        var items = [],
            view = this.getView(),
            viewModel = this.getViewModel(),
            metaRecord = viewModel.get('currentRecord'),
            relationGroups = metaRecord.relationGroups(),
            relationGroupDefaultNames = ['References', 'Contains', 'ManyToMany'];

        if (relationGroups.count() !== 3) {
            // create default relationGroupsArray
            relationGroups.removeAll();
            Ext.Array.each(relationGroupDefaultNames, function (name, index) {
                var relationGroup;

                relationGroup = Ext.create('Unidata.model.presentation.RelationGroup', {
                    relType: name,
                    row: index
                });

                relationGroups.add(relationGroup);
            });
        }

        relationGroups.each(function (relationGroup, index) {
            // на всякий случай принудительно прописываем index
            relationGroup.set('row', index);
            items.push(this.buildRelationTypeTablet(relationGroup));
        }, this);

        view.suspendLayouts();
        view.removeAll();

        if (items.length > 0) {
            view.add(items);
            view.hideNoData();
        } else {
            view.showNoData(view.noDataText);
        }
        view.resumeLayouts();
        view.updateLayout({isRoot: true});
    },

    /**
     * Найти relationGroup в store по объекту типа Unidata.model.presentation.RelationGroup
     * @param findRelationGroup {Unidata.model.presentation.RelationGroup}
     * @returns {*}
     */
    findRelationGroup: function (findRelationGroup) {
        var viewModel      = this.getViewModel(),
            metaRecord  = viewModel.get('currentRecord'),
            relationGroups = metaRecord.relationGroups(),
            found = null,
            index;

        index = relationGroups.findBy(function (relationGroup) {
            return relationGroup.get('row') === findRelationGroup.get('row') &&
                relationGroup.get('column') === findRelationGroup.get('column') &&
                relationGroup.get('relType') === findRelationGroup.get('relType');
        });

        if (index > -1) {
            found = relationGroups.getAt(index);
        }

        return found;
    },

    mapRelationsToRelationNames: function (relations) {
        var relationNames;

        relationNames = Ext.Array.map(relations, function (relation) {
            return relation.get('name');
        });

        return relationNames;
    },

    setDisabledAllRelationPanels: function (disabled) {
        var view = this.getView(),
            items = view.items;

        if (!Ext.isBoolean(disabled)) {
            return;
        }

        items.each(function (item) {
            var grid = item.lookupReference('relationGrid'),
                isDisabled;

            if (grid) {
                isDisabled = grid.isDisabled();

                if (isDisabled !== disabled) {
                    grid.setDisabled(disabled);
                }
            }
        });
    },

    updateReadOnly: function () {
        var view = this.getView();

        if (view.isConfiguring) {
            return;
        }

        this.syncReadOnly();
    },

    syncReadOnly: function () {
        var me = this,
            view = this.getView();

        view.items.each(function (tablet) {
            me.syncReadOnlyTablet(tablet);
        });
    },

    syncReadOnlyTablet: function (tablet) {
        var view = this.getView(),
            readOnly = view.getReadOnly(),
            gridView = tablet.relationGrid.getView(),
            plugin;

        plugin = gridView.getPlugin('ddplugin');

        if (readOnly) {
            plugin.dragZone.lock();
            plugin.dropZone.lock();
            tablet.customDragAndDrop.lock();
        } else {
            plugin.dragZone.unlock();
            plugin.dropZone.unlock();
            tablet.customDragAndDrop.unlock();
        }
    }
});
