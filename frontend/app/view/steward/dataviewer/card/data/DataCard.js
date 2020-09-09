/**
 * Класс реализует представление для просмотра / редактирования записи реестра / справочника
 *
 * @author Ivan Marshalkin
 * @date 2016-02-21
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.DataCard', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataviewer.card.data.DataCardController',
        'Unidata.view.steward.dataviewer.card.data.DataCardModel',
        'Unidata.view.steward.dataviewer.card.data.DataCardConst',
        'Unidata.view.steward.dataviewer.DataViewerConst',

        'Unidata.view.steward.dataentity.DataEntity',
        'Unidata.view.steward.dataclassifier.ClassifierPanel',
        'Unidata.view.steward.relation.ReferencePanel',
        'Unidata.view.steward.relation.ContainsPanel',
        'Unidata.view.steward.relation.M2mPanel',
        'Unidata.view.steward.dataviewer.card.data.header.HeaderBar'
    ],

    alias: 'widget.steward.dataviewer.datacard',

    controller: 'steward.dataviewer.datacard',
    viewModel: {
        type: 'steward.dataviewer.datacard'
    },

    eventBusHolder: true,

    refreshLoadingText: Unidata.i18n.t('dataviewer>refreshLoading'),
    dataRecordSaveSuccessText: Unidata.i18n.t('dataviewer>recordSaveSuccess'),
    dataRecordSaveDQFailureText: Unidata.i18n.t('dataviewer>recordSaveDQFailure'),
    dataRecordSaveFailureText: Unidata.i18n.t('dataviewer>recordSaveFailure'),
    dataRecordDeleteSuccessText: Unidata.i18n.t('dataviewer>recordDeleteSuccess'),
    referenceRelationsSaveFailureText: Unidata.i18n.t('dataviewer>recordRelationsSaveFailure'),
    deleteTimeIntervalSuccessText: Unidata.i18n.t('dataviewer>timeIntervalDeleteSuccess'),
    restoreTimeIntervalSuccessText: Unidata.i18n.t('dataviewer>timeIntervalRestored'),
    deleteTimeIntervalFailedText: Unidata.i18n.t('dataviewer>timeIntervalDeleteFailure'),
    intersectionTimeIntervalMsgText: Unidata.i18n.t('dataviewer>intersectionTimeInterval'),
    publishJmsSuccessText: Unidata.i18n.t('dataviewer>publishJmsSuccess'),
    publishJmsFailedText: Unidata.i18n.t('dataviewer>publishJmsFailure'),

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'displayDataCard'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'getSimpleAttributeContainers'
        },
        {
            method: 'updateApprovebarhidden'
        },
        {
            method: 'updateDqbarhidden'
        },
        {
            method: 'updateCreatetimeintervalbuttonhidden'
        },
        {
            method: 'updateDeletetimeintervalbuttonhidden'
        },
        {
            method: 'updateDottedmenubuttonhidden'
        },
        {
            method: 'saveDataRecord'
        },
        {
            method: 'deleteDataRecord'
        },
        {
            method: 'setTimeIntervalValidityPeriod'
        },
        {
            method: 'showDqErrorsIndicator'
        },
        {
            method: 'checkRecordConsistencyAndRestore'
        },
        {
            method: 'updateDqErrorCount'
        },
        {
            method: 'getTimeIntervalContainer'
        },
        {
            method: 'onRefreshButtonClick'
        },
        {
            method: 'onEtalonInfoMenuItemClick'
        },
        {
            method: 'onJmsPublishButtonClick'
        },
        {
            method: 'updateClusterCount'
        },
        {
            method: 'checkValid'
        },
        {
            method: 'saveReferenceRelations'
        },
        {
            method: 'refreshDataCard'
        },
        {
            method: 'saveAllAtomic'
        },
        {
            method: 'saveAllOneByOne'
        },
        {
            method: 'buildAtomicRecordJson'
        },
        {
            method: 'reset'
        }

    ],

    referenceHolder: true,

    headerBar: null,                       // шапка
    dataEntity: null,                      // контейнер отображающий запись
    referencePanel: null,                  // панель связей типа сылка
    containsPanel: null,                   // панель связей типа включение
    m2mPanel: null,                        // панель связей типа многие-ко-многим
    contentContainer: null,                // контейнер обертка (все что не подвал и не шапка)

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        diffToDraft: false,
        drafts: false, // параметр для получении записи - черновика
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        etalonId: null,
        timeInterval: null,
        timeIntervalDate: null,
        timeIntervalStore: null,
        referenceRelations: null,
        classifierNodes: null,
        readOnly: null,
        approvebarhidden: false,
        dqbarhidden: false,
        createtimeintervalbuttonhidden: false,
        deletetimeintervalbuttonhidden: false,
        dottedmenubuttonhidden: false,
        dqErrorCount: null,
        clusterCount: 0,
        saveAtomic: true
    },

    unidataLayoutManagerEnabled: true,
    unidataLayoutManagerText: 'datacard updatelayout',
    unidataLayoutManagerDelay: 100,

    initComponent: function () {
        var referenceRelayEventsList = ['referencedirtychange'],
            m2mRelayEventsList = ['m2mdirtychange'];

        this.callParent(arguments);
        this.relayEvents(this.referencePanel, referenceRelayEventsList);
        this.relayEvents(this.m2mPanel, m2mRelayEventsList);
    },

    onDestroy: function () {
        var me = this;

        me.headerBar           = null;
        me.dataEntity          = null;
        me.referencePanel      = null;
        me.containsPanel       = null;
        me.m2mPanel            = null;
        me.contentContainer    = null;

        me.callParent(arguments);
    },

    initItems: function () {
        var referenceRelayEventsList = ['referencedirtychange'];

        this.callParent(arguments);

        this.add([
            this.buildHeaderBarCfg(),
            this.buildContentContainerCfg()
        ]);

        this.initComponentReference();
        this.relayEvents(this.referencePanel, referenceRelayEventsList);
    },

    buildContentContainerCfg: function () {
        var metaRecord = this.getMetaRecord(),
            entityType = metaRecord.getType(),
            relationGroups = null,
            relTypesSorted = ['reference', 'contains', 'm2m'],
            cfg,
            contentItems;

        if (entityType === 'Entity') {
            relationGroups = metaRecord.relationGroups();
        }

        contentItems = [
            this.buildDataEntityCfg()
        ];

        // если заполнена секция relationGroups
        if (relationGroups && relationGroups.count() === 3) {
            relTypesSorted = Ext.Array.map(relationGroups.getRange(), function (relationGroup) {
                var RelationModel = Unidata.model.data.RelationTimeline,
                    relType;

                relType = RelationModel.getRelationTypeAlias(relationGroup.get('relType'));
                relType = relType.toLowerCase();

                return relType;
            }, this);

            // если не содержит всех типов связей то откатываемся к дефолтному списку
            if (!Ext.Array.contains(relTypesSorted, 'reference') ||
                !Ext.Array.contains(relTypesSorted, 'contains') ||
                !Ext.Array.contains(relTypesSorted, 'm2m')) {
                relTypesSorted = ['reference', 'contains', 'm2m'];
            }
        }

        Ext.Array.each(relTypesSorted, function (relType) {
            contentItems.push(this.buildRelationPanelCfg(relType));
        }, this);

        cfg = {
            xtype: 'container',
            reference: 'contentContainer',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            scrollable: true,
            cls: 'un-content-inner',
            flex: 1,
            items: contentItems
        };

        return cfg;
    },

    buildHeaderBarCfg: function () {
        var dataCard = this,
            cfg;

        cfg = {
            xtype: 'steward.datacard.header',
            reference: 'headerBar',
            minHeight: 50,
            dataCard: dataCard,
            listeners: {
                recordrefresh: 'onRefreshButtonClick',
                togglehiddenattribute: 'onToggleHiddenAttribute'
            }
        };

        return cfg;
    },

    buildDataEntityCfg: function () {
        var view = this,
            readOnly = this.getReadOnly(),
            cfg;

        cfg = {
            xtype: 'dataentity',
            reference: 'dataEntity',
            useCarousel: true,
            useAttributeGroup: true,
            hiddenAttribute: false,
            readOnly: readOnly,
            listeners: {
                refreshstart: function () {
                    view.fireEvent('refreshstart');
                }
            }
        };

        return cfg;
    },

    buildRelationPanelCfg: function (type) {
        var drafts      = this.getDrafts(),
            operationId = this.getOperationId(),
            readOnly    = this.getReadOnly(),
            saveAtomic     = this.getSaveAtomic(),
            cfg,
            xtype,
            reference,
            hiddenFormula;

        xtype = Ext.String.format('relation.{0}panel', type);
        reference = Ext.String.format('{0}Panel', type);
        hiddenFormula = Ext.String.format('{!{0}PanelVisible}', type);

        cfg = {
            xtype: xtype,
            reference: reference,
            drafts: drafts,
            operationId: operationId,
            readOnly: readOnly,
            saveAtomic: saveAtomic,
            bind: {
                hidden: hiddenFormula
            }
        };

        return cfg;
    },

    initComponentReference: function () {
        var me = this;

        me.headerBar           = me.lookupReference('headerBar');
        me.dataEntity          = me.lookupReference('dataEntity');
        me.referencePanel      = me.lookupReference('referencePanel');
        me.containsPanel       = me.lookupReference('containsPanel');
        me.m2mPanel            = me.lookupReference('m2mPanel');
        me.contentContainer    = me.lookupReference('contentContainer');
    },

    setMetaRecord: function (metaRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('metaRecord', metaRecord);
        viewModel.notify();
    },

    getMetaRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('metaRecord');
    },

    setDataRecord: function (dataRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('dataRecord', dataRecord);
        viewModel.notify();

        this.setEtalonId(dataRecord.get('etalonId'));
    },

    getDataRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('dataRecord');
    },

    setEtalonId: function (etalonId) {
        var viewModel = this.getViewModel();

        viewModel.set('etalonId', etalonId);
        viewModel.notify();
    },

    getEtalonId: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('etalonId');
    },

    setTimeIntervalDate: function (timeIntervalDate) {
        var viewModel = this.getViewModel();

        viewModel.set('timeIntervalDate', timeIntervalDate);
        viewModel.notify();
    },

    getTimeIntervalDate: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeIntervalDate');
    },

    setTimeInterval: function (timeInterval) {
        var viewModel = this.getViewModel();

        viewModel.set('timeInterval', timeInterval);
        viewModel.notify();
    },

    getTimeInterval: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeInterval');
    },

    setReferenceRelations: function (referenceRelations) {
        var viewModel = this.getViewModel();

        viewModel.set('referenceRelations', referenceRelations);
        viewModel.notify();
    },

    getReferenceRelations: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('referenceRelations');
    },

    setTimeIntervalStore: function (timeIntervalStore) {
        var viewModel = this.getViewModel();

        viewModel.set('timeIntervalStore', timeIntervalStore);
        viewModel.notify();
    },

    getTimeIntervalStore: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('timeIntervalStore');
    },

    getDirty: function () {
        var referencePanel = this.referencePanel,
            dataRecord = this.getDataRecord(),
            dirty;

        // apply dirty symbol
        if (dataRecord && Ext.isFunction(dataRecord.checkDirty)) {
            dirty = dataRecord.checkDirty();
        }

        if (referencePanel) {
            dirty = dirty || referencePanel.getDirty();
        }

        return dirty;
    },

    getHeaderBar: function () {
        return this.headerBar;
    },

    getDottedMenuButton: function () {
        return this.headerBar.getDottedMenuButton();
    }
});
