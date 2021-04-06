Ext.define('Unidata.view.admin.entity.metarecord.attribute.error.ErrPanel', {
    extend: 'Ext.container.Container',

    alias: 'widget.attribute.errpanel',

    maxHeight: 150,

    errors: null,
    html: '&nbsp;',

    cls: 'unidata-admin-metarecord-attribute-errpanel',

    scrollable: 'y',

    errorCollapsed: true,

    initComponent: function () {
        this.callParent(arguments);

        this.on('afterrender', this.onContainerAfterRender, this);

        this.updateErrorPanel();
    },

    onContainerAfterRender: function () {
        var panel = this;

        this.el.on('click', function () {
            panel.toggleCollapse();
        });
    },

    expand: function () {
        this.errorCollapsed = false;

        this.setHeight(this.getMaxHeight());

        this.updateErrorPanel();
    },

    collapse: function () {
        this.errorCollapsed = true;

        this.setHeight(null);

        this.updateErrorPanel();
    },

    toggleCollapse: function () {
        if (this.errorCollapsed) {
            this.expand();
        } else {
            this.collapse();
        }
    },

    setErrors: function (list) {
        this.errors = [];

        if (Ext.isArray(list)) {
            this.errors = list;
        } else if (Ext.isString(list)) {
            this.errors = [list];
        }

        this.updateErrorPanel();
    },

    getFirstError: function () {
        var error = '';

        if (this.getErrorCount()) {
            error = this.errors[0];
        }

        return error;
    },

    getListError: function () {
        var tpl;

        tpl = new Ext.XTemplate(
            '<ul class="errorlist-list">',
            '<tpl for=".">',
            '<li>',
            '{.}',
            '</li>',
            '</tpl></ul>'
        );

        return tpl.apply(this.errors);
    },

    getHeaderError: function () {
        var tpl,
            error;

        error = this.getFirstError();

        tpl = new Ext.XTemplate(
            '<div class="errorlist-header">',
            error,
            '</div>'
        );

        return tpl.apply(this.errors);
    },

    getErrorCount: function () {
        var length = 0;

        if (Ext.isArray(this.errors)) {
            length = this.errors.length;
        }

        return length;
    },

    updateErrorPanel: function () {
        var html;

        this.setHtml('');
        this.removeAll();

        if (this.errorCollapsed) {
            html = this.getHeaderError();

            if (this.getErrorCount()) {
                this.add({
                    xtype: 'container',
                    layout: {
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'container',
                            flex: 1,
                            html: html
                        },
                        {
                            xtype: 'container',
                            cls: 'errorlist-allerror',
                            padding: '0 10 0 0',
                            html: '<span>(' + Unidata.i18n.t('admin.metamodel>allErrors') + ')</span>'
                        }
                    ]
                });
            }
        } else {
            html = this.getListError();
            this.setHtml(html);
        }
    }
});
