    /**
 * @author Aleksandr Bavin
 * @date 19.05.2016
 */
Ext.define('Unidata.view.steward.relation.contains.ContainsRecord', {

    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.timeinterval.TimeInterval',
        'Unidata.view.steward.dataentity.DataEntity',
        'Unidata.view.steward.relation.contains.ContainsRecordModel',
        'Unidata.view.steward.relation.contains.ContainsRecordController'
    ],

    viewModel: {
        type: 'containsrecord'
    },

    controller: 'containsrecord',

    referenceHolder: true,

    ui: 'un-card',
    cls: 'un-relation-containsrecord un-relation-record',

    flex: 1,

    config: {
        drafts: false,
        operationId: null,
        etalonId: null,
        metaRecord: null,
        dataRecord: null,
        metaRelation: null,
        dataRelation: null,
        metaRelationRecord: null,
        dataRelationRecord: null,
        readOnly: null,
        minDate: null,
        maxDate: null,
        createtimeintervalbuttonhidden: false
    },

    relationEtalonId: null, // хринит etalonId записи для нужд QA отдела

    timeIntervalContainer: null,

    updateMinDate: function (value) {
        if (this.timeIntervalContainer) {
            this.timeIntervalContainer.setMinDate(value);
        }
    },

    updateMaxDate: function () {
        if (this.timeIntervalContainer) {
            this.timeIntervalContainer.setMaxDate(value);
        }
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        },
        {
            method: 'updateCreatetimeintervalbuttonhidden'
        }
    ],

    collapsible: true,
    collapsed: false,
    titleCollapse: true,

    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
    collapseFirst: true,
    header: {
        bind: {
            title: '{containsRecordTitle}'
        },
        titlePosition: 1
    },

    deleteTimeIntervalSuccessText: Unidata.i18n.t('relation>removeTimeIntervalSuccess'),
    deleteTimeIntervalFailedText: Unidata.i18n.t('relation>removeTimeIntervalFailure'),
    fieldsInvalidText: Unidata.i18n.t('validation:form.requiredFields'),
    securityInvalidText: Unidata.i18n.t('validation:form.securityLabel'),

    items: [
        {
            xtype: 'timeinterval',
            reference: 'timeIntervalContainer',
            referenceHolder: true,
            tools: [
                {
                    type: 'plus',
                    cls: 'un-timeinterval-add',
                    tooltip: Unidata.i18n.t('dataviewer>makeTimeInterval'),
                    reference: 'addTimeIntervalButton',
                    listeners: {
                        click: 'onAddTimeIntervalButtonClick'
                    }
                }
            ],
            bind: {
                readOnly: '{readOnly}'
            },
            dataViewConfig: {
                autoSelectTimeInterval: false
            },
            hidden: !Unidata.Config.getTimeintervalEnabled(),
            width: 335
        },
        {
            xtype: 'dataentity',
            reference: 'dataEntity',
            useCarousel: false,
            classifierHidden: true,
            depth: 1,
            bind: {
                readOnly: '{readOnly}'
            }
        }
    ],

    tools: [
        {
            xtype: 'steward.relation.contains.dqbar',
            reference: 'dqBar',
            margin: '0 10 0 10',
            hidden: true,
            bind: {
                hidden: '{!dqErrors}'
            },
            listeners: {
                click: function (e) {
                    e.stopPropagation();
                }
            }
        },
        {
            xtype: 'un.fontbutton.save',
            reference: 'saveButton',
            handler: 'onSaveRelationClick',
            shadow: false,
            disabled: true,
            hidden: true,
            buttonSize: 'extrasmall',
            tooltip: Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:relation')}),
            bind: {
                disabled: '{!saveButtonEnabled}',
                hidden: '{!saveActionVisible}'
            }
        },
        {
            xtype: 'un.fontbutton.delete',
            reference: 'removeButton',
            handler: 'onRemoveRelationClick',
            disabled: true,
            hidden: true,
            tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:relation')}),
            bind: {
                disabled: '{!removeButtonEnabled}',
                hidden: '{!removeButtonVisible}'
            }
        }
    ],

    initComponent: function () {
        var viewModel = this.getViewModel();

        this.callParent(arguments);
        this.initReferences();

        viewModel.bind('{!createTimeIntervalButtonVisible}', function (value) {
            this.setCreatetimeintervalbuttonhidden(value);
        }, this, {deep: true});
    },

    initReferences: function () {
        this.timeIntervalContainer = this.lookupReference('timeIntervalContainer');
        this.dataEntity = this.lookupReference('dataEntity');
    },

    /**
     * @see Unidata.view.steward.dataviewer.card.data.DataCard.getDirty
     * @returns {boolean}
     */
    getDirty: function () {
        //TODO: implement me
        return false;
    },

    setDataRelationRecord: function (dataRelationRecord) {
        var viewModel = this.getViewModel();

        viewModel.set('dataRelationRecord', dataRelationRecord);

        this.relationEtalonId = this.getRelationEtalonId();

        viewModel.notify();
    },

    getDataRelationRecord: function () {
        var viewModel = this.getViewModel();

        return viewModel.get('dataRelationRecord');
    },

    /**
     * Возвращает etalonId записи. Методы добавлены для QA отдела. Используются в автотестах
     *
     * добавлены по задаче UN-3928
     * @returns {*}
     */
    getRelationEtalonId: function () {
        var dataRelationRecord = this.getDataRelationRecord(),
            etalonId = null;

        if (dataRelationRecord) {
            etalonId = dataRelationRecord.get('etalonId');
        }

        return etalonId;
    }
});
