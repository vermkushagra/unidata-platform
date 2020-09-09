/**
 * Карусельный компонент представления коллекции узлов классификации
 * @author Sergey Shishigin
 * @date 2018-05-031
 */

Ext.define('Unidata.view.steward.dataentity.classifier.carousel.CarouselClassifierNodeTablet', {
    extend: 'Unidata.view.steward.dataentity.classifier.abstraction.AbstractClassifierNodeTablet',

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableContainer'
    },

    requires: [
        'Unidata.view.steward.dataentity.classifier.carousel.CarouselClassifierNodeTabletController',
        'Unidata.view.steward.dataentity.classifier.carousel.CarouselClassifierNodeTabletModel'
    ],

    alias: 'widget.steward.dataentity.classifier.carouselclassifiernodetablet',

    controller: 'steward.dataentity.classifier.carouselclassifiernodetablet',
    viewModel: {
        type: 'steward.dataentity.classifier.carouselclassifiernodetablet'
    },

    config: {
        carouselItemCount: 0
    },

    bodyPadding: 0,

    methodMapper: [
        {
            method: 'updateCarouselItemCount'
        }
    ],

    carouselPanel: null,

    collapsed: true,
    animCollapse: false
});
