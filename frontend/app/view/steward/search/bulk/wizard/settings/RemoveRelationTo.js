/**
 * Настройка удаления связей из записи
 *
 * @author Ivan Marshalkin
 * @date 2018-02-19
 */

Ext.define('Unidata.view.steward.search.bulk.wizard.settings.RemoveRelationTo', {
    extend: 'Unidata.view.steward.search.bulk.wizard.settings.Default',

    alias: 'widget.steward.search.bulk.wizard.settings.removerelationto',

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
                        text: Unidata.i18n.t('search>wizard.relationName'),
                        dataIndex: 'displayName',
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
        var wizard = this.getWizard(),
            metaRecord = wizard.getMetarecord(),
            data = [];

        metaRecord.relations().each(function (relation) {
            data.push({
                name: relation.get('name'),
                displayName: relation.get('displayName')
            });
        });

        this.relationStore = Ext.create('Ext.data.Store', {
            fields: [
                'name',
                'displayName'
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
            data.push(selected.get('name'));
        });

        operationSettings.relationsNames = data;
    }

});
