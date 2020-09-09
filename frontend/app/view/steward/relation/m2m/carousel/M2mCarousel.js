/**
 * Панель реализующая представление инстанса связи многие-ко-многим в виде карусельки
 *
 * @author Ivan Marshalkin
 * @date 2017-05-10
 */

Ext.define('Unidata.view.steward.relation.m2m.carousel.M2mCarousel', {
    extend: 'Ext.panel.Panel',

    mixins: {
        //searchCmp: 'Unidata.view.steward.dataentity.mixin.SearchableComponent',
        //searchCont: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.relation.m2m.carousel.M2mCarouselController',
        'Unidata.view.steward.relation.m2m.carousel.M2mCarouselModel',
        'Unidata.view.steward.relation.m2m.edit.M2mRecord'
    ],

    alias: 'widget.relation.m2mcarousel',

    controller: 'relation.m2mcarousel',

    viewModel: {
        type: 'relation.m2mcarousel'
    },

    referenceHolder: true,

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
            method: 'displayRelations'
        },
        {
            method: 'createRelationTo'
        },
        {
            method: 'getRelationReferences'
        },
        {
            method: 'checkPanelsValid'
        }
    ],

    carousel: null,                                     // ссылка на карусельку

    config: {
        drafts: false,
        operationId: null,
        metaRecord: null,
        dataRecord: null,
        metaRelation: null,
        dataRelation: null,
        readOnly: null,
        relationName: null, // QA использует имя связи для поиска
        saveAtomic: true
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        this.carousel = this.lookupReference('carouselPanel');
    },

    /**
     * Подчищаем свои ссылки
     */
    onDestroy: function () {
        this.carousel = null;

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'carouselpanel',
            reference: 'carouselPanel'
        }
    ]
});
