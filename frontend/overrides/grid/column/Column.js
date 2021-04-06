/**
 * override для клонок грида
 *
 * @author Ivan Marshalkin
 * 2017-05-22
 */

Ext.define('Unidata.overrides.grid.column.Column', {
    override: 'Ext.grid.column.Column',

    renderTpl: [
        '<div id="{id}-titleEl" data-ref="titleEl" {tipMarkup}class="', Ext.baseCSSPrefix, 'column-header-inner<tpl if="!$comp.isContainer"> ', Ext.baseCSSPrefix, 'leaf-column-header</tpl>',
            '<tpl if="empty"> ', Ext.baseCSSPrefix, 'column-header-inner-empty</tpl>">',
            //
            // TODO:
            // When IE8 retires, revisit https://jsbin.com/honawo/quiet for better way to center header text
            //
            '<span class="', Ext.baseCSSPrefix, 'column-header-text-container">',
                '<span class="', Ext.baseCSSPrefix, 'column-header-text-wrapper">',
                    '<span id="{id}-textEl" data-ref="textEl" class="', Ext.baseCSSPrefix, 'column-header-text',
                        '{childElCls}" title="{text}">', // добавлен title
                        '{text}',
                    '</span>',
                '</span>',
            '</span>',
            '<tpl if="!menuDisabled">',
                '<div id="{id}-triggerEl" data-ref="triggerEl" role="presentation" class="', Ext.baseCSSPrefix, 'column-header-trigger',
                '{childElCls}" style="{triggerStyle}"></div>',
            '</tpl>',
        '</div>',
        '{%this.renderContainer(out,values)%}'
    ],

    // подробнее https://stackoverflow.com/questions/6073037/how-to-escape-html-entities-in-grid
    defaultRenderer: Ext.util.Format.htmlEncode, // не забываем предотвращение XSS
    config: {
        // если заголовок не активный, то он не меняется при наведении
        titleInactive: false,
        // отменить live-обновление текста в колонке грида при его изменении в модели, пока значение не сохранено
        disableBindUpdate: false
    },

    titleInactiveCls: 'un-column-title-inactive',

    initComponent: function () {
        var disableBindUpdate = this.config.disableBindUpdate;

        if (disableBindUpdate) {
            // сохраняем пользовательский renderer
            this.customRenderer = this.renderer;
            // подменяем специальным renderer внутри которого вызывается customRenderer
            this.config.renderer = this.rendererWrapper.bind(this);
        }

        this.callParent(arguments);
    },

    rendererWrapper: function (value, columnMeta, record) {
        var dataIndex = this.dataIndex;

        value = record.isModified(dataIndex) ? record.getModified(dataIndex) : value;

        if (Ext.isFunction(this.customRenderer)) {
            arguments[0] = value;
            value = this.customRenderer.apply(this, arguments);
        }

        return value;
    },

    updateTitleInactive: function (titleInactive) {
        var titleInactiveCls = this.titleInactiveCls;

        if (titleInactive) {
            this.addCls(titleInactiveCls);
        } else {
            this.removeCls(titleInactiveCls);
        }
    }

});

