/**
 * Контейнер реализующий отображение связи типа reference
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.reference.Reference', {

    extend: 'Ext.panel.Panel',

    mixins: {
        searchCmp: 'Unidata.view.steward.dataentity.mixin.SearchableComponent',
        searchCont: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.reference.ReferenceController',
        'Unidata.view.steward.relation.reference.ReferenceModel',
        'Unidata.view.steward.dataentity.attribute.SimpleAttribute'
    ],

    alias: 'widget.relation.reference',

    controller: 'relation.reference',

    viewModel: {
        type: 'relation.reference'
    },

    // TODO: Унифицировать имена переменных relationMeta, relationData в файлах Reference.js, M2MRecord.js, ContainsRecord.js
    config: {
        metaRecord: null,
        dataRecord: null,
        referenceData: null,
        referenceMeta: null,
        readOnly: null,
        relationName: null,// QA использует имя связи для поиска
        valid: true
    },

    ui: 'un-card',
    cls: 'un-relation-reference un-relation-record',

    layout: {
        type: 'vbox',
        align: 'left'
    },

    collapsible: true,
    titleCollapse: true,
    collapsed: true,

    methodMapper: [
        {
            method: 'checkDirty'
        },
        {
            method: 'checkValid'
        },
        {
            method: 'getRelationEtalonId'
        },
        {
            method: 'isChanged'
        },
        {
            method: 'getCurrentEtalonId'
        },
        {
            method: 'getRelationReferenceToUpdate'
        },
        {
            method: 'getRelationReferenceToDelete'
        },
        {
            method: 'updateValid'
        }
    ],

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel(),
            items     = this.items;

        viewModel.set('readOnly', readOnly);

        if (!items) {
            return;
        }

        items.each(function (item) {
            item.setReadOnly(readOnly);
        });
    },

    search: function (text) {
        return this.getController().search(text);
    },

    saveReferenceRelations: function () {
        return this.getController().saveReferenceRelations();
    }
});
