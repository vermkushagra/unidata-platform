/**
 * Панель реализующая представление инстанса рекорда связи многие-ко-многим
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.m2m.edit.M2mRecord', {
    extend: 'Ext.panel.Panel',

    mixins: {
        //searchCmp: 'Unidata.view.steward.dataentity.mixin.SearchableComponent',
        //searchCont: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.m2m.edit.M2mRecordController',
        'Unidata.view.steward.relation.m2m.edit.M2mRecordModel',

        'Unidata.view.steward.dataentity.DataEntity'
    ],

    alias: 'widget.relation.m2mrecord',

    controller: 'relation.m2mrecord',

    viewModel: {
        type: 'relation.m2mrecord'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-card',

    cls: 'un-relation-m2mrecord un-relation-record',

    collapsible: true,
    titleCollapse: true,
    collapsed: true,

    referenceHolder: true,

    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
    collapseFirst: true,
    header: {
        titlePosition: 1
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        },
        {
            method: 'displayRelationRecord'
        }
    ],

    viewModelAccessors: ['dataRelation'],

    pickerField: null,   // ссылка на поле выбора связанной записи
    dataEntity: null,    // ссылка на dataEntity
    saveButton: null,    // кнопка сохранения инстанса связи
    removeButton: null,  // кнопка удаления инстанса связи
    periodPanel: null,   // ссылка на компонент с выбором дат
    fromDate: null,      // ссылка на поле ввода с датой "С"
    toDate: null,        // ссылка на поле ввода с датой "По"

    config: {
        metaRecord: null,
        dataRecord: null,
        metaRelation: null,
        dataRelation: null,
        readOnly: null
    },

    relationEtalonId: null, // хринит etalonId записи для нужд QA отдела

    bind: {
        title: '{titleFormula}'
    },

    tools: [
        {
            xtype: 'un.fontbutton.save',
            reference: 'saveButton',
            handler: 'onSaveRelationClick',
            shadow: false,
            disabled: true,
            hidden: true,
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

    /**
     * Подчищаем свои ссылки
     */
    onDestroy: function () {
        this.pickerField  = null;
        this.dataEntity   = null;

        this.periodPanel  = null;
        this.fromDate     = null;
        this.toDate       = null;

        this.saveButton   = null;
        this.removeButton = null;

        this.callParent(arguments);
    },

    updateDataRecord: function (dataRecord) {
        var viewModel;

        if (this.getViewModel) {
            viewModel = this.getViewModel();

            viewModel.set('dataRecord', dataRecord);
        }
    },

    updateDataRelation: function () {
        this.relationEtalonId = this.getRelationEtalonId();
    },

    /**
     * Возвращает etalonId связанной записи. Методы добавлены для QA отдела. Используются в автотестах
     *
     * добавлены по задаче UN-3928
     * @returns {*}
     */
    getRelationEtalonId: function () {
        var dataRelation = this.getDataRelation(),
            etalonId = null;

        if (dataRelation) {
            etalonId = dataRelation.get('etalonId');
        }

        return etalonId;
    }
});
