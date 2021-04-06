/**
 * Класс представления пейджера в панели результатов (поисковой выдачи)
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.toolbar.ResultPaging', {
    extend: 'Ext.toolbar.Paging',

    alias: 'widget.un.resultpaging',

    layout: {
        type: 'hbox',
        pack: 'left'
    },

    cls: ['un-toolbar-paging', 'un-toolbar-result-paging'],

    hideRefreshButton: true,
    hideFirstButton: false,
    hideLastButton: false,
    hideAfterPageText: true,
    hideBeforePageText: false,

    beforePageText: Unidata.i18n.t('common:page') + ':',

    inputItemMargin: '-1 2 3 0',
    beforePageTextMargin: '-1 0 3 2',
    displayInfo: false,
    // поле ввода страницы по умолчанию (нормально влезает 6 разрядов)
    inputItemWidth: 50,

    config: {
        outOfLimit: false
    },

    getPagingItems: function () {
        var me = this,
            pagingItems = this.callParent(arguments),
            tooltip;

        Ext.Array.each(pagingItems, function (pagingItem, index, array) {
            if (pagingItem['itemId'] === 'inputItem') {
                pagingItem['ui'] = 'un-field-default';

                pagingItem['listeners']['render'] = function (textfield) {
                    tooltip = Ext.create('Ext.tip.ToolTip', {
                        dismissDelay: 0,
                        target: textfield.getEl(),
                        listeners: {
                            beforeshow: function (tooltip) {
                                var pageData = me.getPageData(),
                                    tooltipText;

                                tooltipText = Ext.String.format(
                                    Unidata.i18n.t('common:countOfCount'),
                                    pageData.currentPage,
                                    pageData.pageCount
                                );

                                if (me.getOutOfLimit()) {
                                    tooltipText += '<br>' + Unidata.i18n.t('search>resultset.outOfLimit');
                                }

                                tooltip.update(tooltipText);
                            }
                        }
                    });
                };

                pagingItem['listeners']['destroy'] = function () {
                    tooltip.destroy();
                };
            }
        }, this);

        return pagingItems;
    }

});
