/**
 * Переопределение класса для Ext.panel.Panel
 *
 * @author Ivan Marshalkin
 * @date 2016-02-25
 */

Ext.define('Ext.overrides.panel.Panel', {
    override: 'Ext.panel.Panel',

    collapseFirst: false,

    initComponent: function () {
        this.callParent(arguments);

        // для кнопки сворачивания / разворачивания добавляем класс по которому мы сможем различать этот элемент
        this.on('render', function () {
            var tool = this.collapseTool;

            if (tool) {
                tool.addCls('x-tool-collapse-el');
            }
        }, this);

        if (this.collapsible && this.titleCollapse) {
            // отменяем схлопывание панельки если кликают по элементам хидера
            this.on('afterrender', function () {
                var header = this.getHeader(),
                    headerEl,
                    titleEl,
                    targetEl;

                if (header) {
                    header.removeListener('click', this.toggleCollapse, this);

                    headerEl = header.getEl();

                    headerEl.on('click', function (event) {

                        if (header && header.rendered) {
                            headerEl = header.getEl();
                            titleEl  = header.titleCmp.getEl();
                            targetEl = Ext.get(event.target);

                            // отменяем событие если кликнули не по дом элементу, например по задизабленому tools
                            if (Ext.Array.contains([headerEl.component, titleEl.component], targetEl.component)) {
                                this.toggleCollapse();
                            }
                        }

                    }, this);
                }
            }, this, {single: true});
        }
    }
});
