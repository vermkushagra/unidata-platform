/**
 * Настройка удаления связей КОТОРЫЕ ссылаются на запись
 *
 * @author Ivan Marshalkin
 * @date 2018-02-19
 */

Ext.define('Unidata.view.steward.search.bulk.wizard.settings.RemoveRelationFrom', {
    extend: 'Unidata.view.steward.search.bulk.wizard.settings.Default',

    alias: 'widget.steward.search.bulk.wizard.settings.removerelationfrom',

    requiredExternalData: [
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [],

    referenceHolder: true,

    relationStore: null,
    relationList: null,

    initComponent: function () {
        this.callParent(arguments);

        this.on('added', this.updateConfirmStep, this);
    },

    initItems: function () {
        this.callParent(arguments);

        this.initRelationStore();

        this.add([
            {
                xtype: 'grid',
                reference: 'relationList',
                store: this.relationStore,
                columns: [
                    {
                        text: Unidata.i18n.t('search>wizard.entityName'),
                        dataIndex: 'entityDisplayNameFrom',
                        flex: 1
                    },
                    {
                        text: Unidata.i18n.t('search>wizard.relationName'),
                        dataIndex: 'relationDisplayName',
                        flex: 1
                    }
                ],
                selModel: {
                    type: 'checkboxmodel'
                },
                flex: 1,
                listeners: {
                    selectionchange: this.onSelectionChange,
                    scope: this
                }
            }
        ]);

        this.relationList = this.lookupReference('relationList');
    },

    onSelectionChange: function () {
        this.updateConfirmStep();
    },

    initRelationStore: function () {
        var me = this,
            wizard = this.getWizard(),
            metaRecord = wizard.getMetarecord(),
            data = [],
            promise,
            cfg;

        cfg = {
            metaName: metaRecord.get('name'),
            relDirection: 'from'
        };

        me.setLoading(true);

        promise = Unidata.util.api.RelationViewMetaRecord.loadRelationViewMetaRecords(cfg);
        promise.then(
            function (metaRecordsFrom) {
                Ext.Array.each(metaRecordsFrom, function (metaRecordFrom) {
                    metaRecordFrom.relations().each(function (relation) {
                        if (relation.get('toEntity') === metaRecord.get('name')) {
                            data.push({
                                relationName: relation.get('name'),
                                relationDisplayName: relation.get('displayName'),
                                entityNameFrom: metaRecordFrom.get('name'),
                                entityDisplayNameFrom: metaRecordFrom.get('displayName')
                            });
                        }
                    });

                    me.relationStore.getProxy().setData(data);
                    me.relationStore.load();

                    me.setLoading(false);
                });
            },
            function () {
                me.setLoading(false);
            }
        ).done();

        this.relationStore = Ext.create('Ext.data.Store', {
            fields: [
                'relationName',
                'relationDisplayName',
                'entityNameFrom',
                'entityDisplayNameFrom'
            ],
            data: data
        });
    },

    updateConfirmStep: function () {
        var step = this.getStep(),
            sm = this.relationList.getSelectionModel();

        if (!sm.getCount()) {
            step.disallowNextStep(true);
        } else {
            step.allowNextStep();
        }
    },

    beforeGetOperationSettings: function (operationSettings) {
        var sm = this.relationList.getSelectionModel(),
            data = [];

        Ext.Array.each(sm.getSelection(), function (selected) {
            data.push(selected.get('relationName'));
        });

        operationSettings.relationsNames = data;
    }

});
