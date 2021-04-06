/**
 * Контейнер реализующий отображение связи типа reference
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.contains.Contains', {

    extend: 'Ext.panel.Panel',

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.contains.ContainsRecord',
        'Unidata.view.steward.relation.contains.ContainsController',
        'Unidata.view.steward.relation.contains.ContainsModel'
    ],

    alias: 'widget.relation.contains',

    controller: 'relation.contains',

    viewModel: {
        type: 'relation.contains'
    },

    config: {
        drafts: false,
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        metaRelation: null,
        metaRelationRecord: null,
        readOnly: null,
        carouselItemCount: 0,
        relationName: null // QA использует имя связи для поиска
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'updateReadOnly'
        },
        {
            method: 'updateCarouselItemCount'
        }
    ],

    relationsLoaded: false, // связи загружены
    relationsLoading: false, // связи загружаются

    listeners: {
        beforeexpand: 'onBeforeExpandPanel'
    },

    referenceHolder: true,

    ui: 'un-card',
    cls: 'un-relation-contains',

    title: 'Contains Tablet',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    collapsible: true,
    collapsed: true,
    titleCollapse: true,

    tools: [
        {
            xclass: 'Unidata.view.component.dataentity.IndicatorRound',
            color: 'red',
            hidden: true,
            bind: {
                hidden: '{!anyDqErrors}'
            }
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

    items: [
        {
            xtype: 'carouselpanel',
            reference: 'carouselRelation',
            listeners: {
                itemcountchanged: 'onCarouselPanelItemCountChanged'
            }
        }
    ]

});
