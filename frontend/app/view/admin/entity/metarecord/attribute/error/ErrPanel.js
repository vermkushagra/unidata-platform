Ext.define('Unidata.view.admin.entity.metarecord.attribute.error.ErrPanel', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.component.WarningMessage'
    ],

    alias: 'widget.attribute.errpanel',

    cls: 'un-attribute-errorpanel',

    referenceHolder: true,

    systemText: null,

    errors: null,

    errorCollapsed: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'warning-message',
            reference: 'systemText',
            iconHtml: Unidata.util.Icon.getLinearIcon('warning'),
            text: Unidata.i18n.t('admin.job>wizard>overrideSameNameOperations'),
            padding: 0
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();

        this.updateErrorPanel();
    },

    initComponentReference: function () {
        this.systemText = this.lookupReference('systemText');
    },

    initComponentEvent: function () {
        this.on('afterrender', this.onComponentAfterRender, this);
    },

    onDestroy: function () {
        this.systemText = null;

        this.callParent(arguments);
    },

    onComponentAfterRender: function () {
        var panel = this;

        this.el.on('click', function (e) {
            panel.toggleCollapse();

            e.stopEvent();
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
        return this.getListErrorHtml(this.errors);
    },

    getHeaderError: function () {
        return this.getListErrorHtml([this.getFirstError()]);
    },

    getListErrorHtml: function (errors) {
        var tpl;

        tpl = new Ext.XTemplate(
            '<div>',
            '<ul>',
            '<tpl for=".">',
            '<li>',
            '{.}',
            '</li>',
            '</tpl>',
            '</ul>',
            '</div>'
        );

        return tpl.apply(errors);
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

        if (this.errorCollapsed) {
            html = this.getHeaderError();
        } else {
            html = this.getListError();
        }

        this.systemText.setText(html);
    }
});
