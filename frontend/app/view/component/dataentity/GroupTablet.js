/**
 *
 * Компонент для DataEntity
 *
 * @author Ivan Marshalkin
 * @date 2016-01-26
 */

Ext.define('Unidata.view.component.dataentity.GroupTablet', {
    extend: 'Ext.form.Panel',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    scrollable: true,
    collapsible: true,

    /**
     * Возвращает высоту в пикселях тела панели.
     * Возвращаемая высота НЕ ВКЛЮЧАЕТ в себя высоту header
     *
     * @returns {number|*}
     */
    getContentHeight: function () {
        var scrollEl = attrPanel.getScrollerEl(),
            height   = scrollEl ? scrollEl.getHeight() : 0;

        return height;
    },

    /**
     * Возвращает высоту в пикселях панели.
     * Возвращаемая высота ВКЛЮЧАЕТ в себя высоту header
     *
     * @returns {*}
     */
    getPanelHeightByContent: function () {
        var header       = this.getHeader(),
            headerHeight = header.getHeight(),
            scrollHeight = this.getContentHeight();

        return scrollHeight + headerHeight;
    },

    /**
     * Устанавливает высоту панели по высоте контента содержащегося в ней
     */
    setHeightByContent: function () {
        var height;

        height = this.getPanelHeightByContent();

        this.setHeight(height);
    },

    /**
     * Отображает заголовок панели
     */
    showHeader: function () {
        var header = this.getHeader();

        header.show();
    },

    /**
     * Скрывает заголовок панели
     */
    hideHeader: function () {
        var header = this.getHeader();

        header.hide();
    }
});

