/**
 * Класс реализует представление комплексного атрибута типа карусель (группа инстансов комплексного атрибута)
 *
 * @author Sergey Shishigin
 * @date 2016-03-01
 */

Ext.define('Unidata.view.steward.dataentity.complex.carousel.CarouselAttributeTablet', {
    extend: 'Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTablet',

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.dataentity.complex.carousel.CarouselAttributeTabletController',
        'Unidata.view.steward.dataentity.complex.carousel.CarouselAttributeTabletModel'
    ],

    alias: 'widget.steward.dataentity.complex.carouselattributetablet',

    controller: 'steward.dataentity.complex.carouselattributetablet',
    viewModel: {
        type: 'steward.dataentity.complex.carouselattributetablet'
    },

    // cls: 'un-abstract-attribute-tablet un-card un-card__transparent',

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'applyReadOnly'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'getSimpleAttributeContainers'
        },
        {
            method: 'updateHiddenAttribute'
        },
        {
            method: 'updatePreventMarkField'
        },
        {
            method: 'updateCarouselItemCount'
        }
    ],

    config: {
        carouselItemCount: 0
    },

    carouselPanel: null,

    bodyPadding: 0,

    title: 'ComplexAttributeTablet',
    collapsed: true,
    animCollapse: false
});
