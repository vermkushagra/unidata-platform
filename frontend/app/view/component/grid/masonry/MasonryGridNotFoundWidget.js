/**
 * Виджет, который создаётся вместо ненайденного и содержит конфигурацию того виджета, который пытались создать
 *
 * @author Aleksandr Bavin
 * @date 2017-11-02
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridNotFoundWidget', {

    extend: 'Unidata.view.component.AbstractComponent',

    baseCls: 'un-masonry-grid-not-fount-widget',

    config: {
        notFoundWidgetConfig: null
    },

    html: Unidata.i18n.t('masonryGrid>error.notFoundWidget')

});
