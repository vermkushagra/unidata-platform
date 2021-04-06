/**
 * Записи из списка записей
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.search.query.etaloncluster.recordlist.EtalonClusterRecordList', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.etaloncluster.recordlist.EtalonClusterRecordListController'
    ],

    alias: 'widget.component.search.query.etaloncluster.recordlist.etalonclusterrecordlist',

    controller: 'component.search.query.etaloncluster.recordlist.etalonclusterrecordlist',
    viewModel: 'component.search.query.etaloncluster.recordlist.etalonclusterrecordlist',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        etalonCluster: null,
        selectedCount: 0
    },

    viewModelAccessors: ['selectedCount'],

    referenceHolder: true,

    methodMapper: [
        {
            method: 'onEtalonClusterRecordGridSelectionChange'
        }
    ],

    items: [
        {
            xtype: 'component.search.query.relation.component.recordgrid',
            reference: 'etalonClusterRecordGrid',
            flex: 1
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            padding: 0,
            //hidden: true,
            layout: {
                type: 'hbox',
                pack: 'center',
                align: 'middle'
            },
            defaults: {
                margin: 10,
                buttonSize: 'medium'
            },
            items: [
                {
                    xtype: 'un.roundbtn.delete',
                    reference: 'deleteButton',
                    buttonSize: 'extrasmall',
                    shadow: false,
                    tooltip: Unidata.i18n.t('common:delete'),
                    listeners: {
                        click: 'deleteEtalonClusterRecord'
                    },
                    bind: {
                        disabled: '{!deleteEtalonClusterRecordEnabled}'
                    }
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initReferences: function () {
        this.etalonClusterRecordGrid = this.lookupReference('etalonClusterRecordGrid');
    },

    initListeners: function () {
        this.etalonClusterRecordGrid.on('selectionchange', this.onEtalonClusterRecordGridSelectionChange, this);
    },

    updateEtalonCluster: function (etalonCluster) {
        var etalonClusterRecordGrid = this.etalonClusterRecordGrid;

        if (!etalonClusterRecordGrid) {
            return;
        }

        etalonClusterRecordGrid.setEtalonCluster(etalonCluster);
    },

    deleteEtalonClusterRecord: function () {
        var EtalonClusterStorage = Unidata.module.storage.EtalonClusterStorage,
            etalonCluster = this.getEtalonCluster(),
            etalonClusterRecords;

        etalonClusterRecords = this.etalonClusterRecordGrid.getSelection();

        EtalonClusterStorage.removeEtalonClusterRecords(etalonCluster, etalonClusterRecords);
    }
});
