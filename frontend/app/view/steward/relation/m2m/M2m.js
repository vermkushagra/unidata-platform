/**
 * Панель реализующая представление инстанса связи многие-ко-многим
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.m2m.M2m', {
    extend: 'Ext.panel.Panel',

    mixins: {
        // функционал поиска по записи временно отключен
        //searchCmp: 'Unidata.view.steward.dataentity.mixin.SearchableComponent',
        //searchCont: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.m2m.M2mController',
        'Unidata.view.steward.relation.m2m.M2mModel',

        'Unidata.view.steward.relation.RelationViewType',
        'Unidata.view.steward.relation.m2m.carousel.M2mCarousel',
        'Unidata.view.steward.relation.m2m.table.M2mTable'
    ],

    alias: 'widget.relation.m2mrelation',

    controller: 'relation.m2mrelation',

    viewModel: {
        type: 'relation.m2mrelation'
    },

    referenceHolder: true,

    ui: 'un-card',

    cls: 'un-relation-m2m',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        },
        {
            method: 'updateMetaRelation'
        },
        {
            method: 'displayRelationRecord'
        },
        {
            method: 'checkPanelsDirty'
        },
        {
            method: 'checkPanelsValid'
        },
        {
            method: 'getRelationReferenceToUpdate'
        },
        {
            method: 'getRelationReferenceToDelete'
        },
        {
            method: 'clearRemovedRelationReferences'
        },
        {
            method: 'updateValid'
        },
        {
            method: 'reset'
        }
    ],

    toggleRelationViewTypeButton: null,                     // ссылка на кнопку переключения внешнего вида связи

    relationViewType: Unidata.view.steward.relation.RelationViewType.m2m.TABLE, // текущий вид отображения из списка значение по умолчанию определяется в контролере в методе initDefaultRelationView
    relationView: null,                                                         // ссылка на компонент осуществляющий текущее отображение связи

    config: {
        drafts: false,
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        metaRelation: null,
        dataRelation: null,
        readOnly: null,
        relationName: null, // QA использует имя связи для поиска
        dataRelationTotalCount: 0,
        removedRelationReferences: null,
        valid: true,
        saveAtomic: true
    },

    getRemovedRelationReferences: function () {
        var removedRelationReferences = this.removedRelationReferences;

        if (!removedRelationReferences) {
            this.setRemovedRelationReferences([]);
        }

        return this.removedRelationReferences;
    },

    collapsible: true,
    titleCollapse: true,
    collapsed: false,

    tools: [
        {
            xtype: 'un.fontbutton',
            handler: 'onToggleRelationViewTypeButtonClick',
            reference: 'toggleRelationViewTypeButton',
            iconCls: 'icon-grid',
            tooltip: Unidata.i18n.t('relation>switchRelationType'),
            text: ''
        },
        {
            xtype: 'un.fontbutton.additem',
            handler: 'onCreateRelationClick',
            buttonSize: 'extrasmall',
            shadow: false,
            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:relation')}),
            hidden: true,
            bind: {
                hidden: '{!createButtonVisible}'
            }
        }
    ],

    /**
     * Подчищаем свои ссылки
     */
    onDestroy: function () {
        this.toggleRelationViewTypeButton = null;

        this.callParent(arguments);
    },

    onRender: function () {
        var controller = this.getController();

        this.callParent(arguments);

        // tools создаются во время рендеринга
        this.toggleRelationViewTypeButton = this.lookupReference('toggleRelationViewTypeButton');

        controller.onViewRender();
    }
});
