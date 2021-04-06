/**
 * Компонент для построения кнопок типа "ссылка"
 *
 * @author Sergey Shishigin
 * date: 2016-04-08
 */
Ext.define('Unidata.view.component.HrefLabel', {
    extend: 'Ext.form.Label',

    xtype: 'un.hreflabel',

    cls: 'un-hreflabel',

    config: {
        title: null,
        handlerParams: null
    },

    initComponent: function () {
        this.html = '<a href="javascript:void(0)">' + this.config.title + '</a>';
        this.callParent(arguments);
        this.on('render', this.onHrefLabelRender);
    },

    configHrefHandler: function () {
        var handlerParams = this.getHandlerParams(),
            handler,
            args,
            scope,
            el,
            a;

        if (!handlerParams) {
            return;
        }

        handler       = handlerParams.handler;
        args          = handlerParams.args;
        scope         = handlerParams.scope;

        scope = scope ? scope : this;

        el = this.getEl();

        if (!el) {
            return;
        }

        a = el.down('a');

        if (!a) {
            return;
        }

        a.on('click', function (e) {
            e.stopEvent();
        });

        a.on('click', handler, scope, {
            args: args
        });
    },

    onHrefLabelRender: function () {
        this.configHrefHandler();
    },

    updateTitle: function (title) {
        var el = this.getEl(),
            a;

        if (!el) {
            this.setHtml('<a href="javascript:void(0)">' + title + '</a>');
        } else {
            a           = el.query('a')[0];
            a.innerHTML = title;
        }
    }
});
