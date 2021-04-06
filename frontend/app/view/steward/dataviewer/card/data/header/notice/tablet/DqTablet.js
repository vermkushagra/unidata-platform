/**
 *
 * Таблетка для панельки с уведомлениями для правил качества данных
 *
 * @author Ivan Marshalkin
 * @date 2016-03-15
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.notice.tablet.DqTablet', {
    extend: 'Ext.container.Container',

    requires: [],

    alias: 'widget.steward.datacard.header.dqtablet',

    referenceHolder: true,

    iconContainer: null,        // контейнер содержащий иконку
    ruleNameLabel: null,        // текст: имя правила
    messageLabel: null,         // текст: текст сообщения

    expanded: false,            // признак указывающий на то что таблетка развернута

    config: {
        dqError: null
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    cls: 'un-dataentity-dqtablet',
    severityCls: '',

    items: [
        {
            xtype: 'container',
            reference: 'iconContainer',
            cls: 'un-dataentity-dqtablet-iconcontainer',
            style: {
                'font-size': '25px',
                'text-align': 'center'
            },
            html: '&nbsp;',
            width: 50
        },
        {
            xtype: 'container',
            cls: 'un-dataentity-dqtablet-textcontainer',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'label',
                    cls: 'un-dataentity-dqtablet-messagelabel',
                    reference: 'messageLabel'
                },
                {
                    xtype: 'label',
                    cls: 'un-dataentity-dqtablet-label',
                    reference: 'ruleNameLabel'
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();

        this.on('beforerender', this.onBeforeRender, this, {single: true});
        this.on('afterrender', this.onAfterRender, this, {single: true});
    },

    onDestroy: function () {
        var me = this;

        me.iconContainer = null;
        me.ruleNameLabel = null;
        me.messageLabel  = null;

        me.callParent(arguments);
    },

    initComponentReference: function () {
        var me = this;

        me.iconContainer = me.lookupReference('iconContainer');
        me.ruleNameLabel = me.lookupReference('ruleNameLabel');
        me.messageLabel  = me.lookupReference('messageLabel');
    },

    onBeforeRender: function () {
        this.updateExpandCls();
    },

    onAfterRender: function () {
        var el      = this.getEl(),
            dqError = this.getDqError();

        if (dqError) {
            this.updateDqError(dqError);
        }

        el.on('click', this.onElementClick, this);

        this.fireEvent('completerender', this);
    },

    updateDqError: function (dqError) {
        var me = this,
            icon;

        if (!me.rendered) {
            return;
        }

        icon = this.getDqIconHtml(dqError);
        this.updateRotateCls(dqError);

        this.iconContainer.setHtml(icon);
        me.ruleNameLabel.setText(dqError.get('ruleName'));
        me.messageLabel.setText(dqError.get('message'));
    },

    onElementClick: function () {
        this.toggle();
    },

    updateExpanded: function (expanded) {
        this.expanded = Boolean(expanded);
        this.updateExpandCls();
    },

    isExpanded: function () {
        return this.expanded === true;
    },

    isCollapsed: function () {
        return !this.isExpanded();
    },

    getDqName: function () {
        var name    = null,
            dqError = this.getDqError();

        if (dqError) {
            name = dqError.get('ruleName');
        }

        return name;
    },

    expand: function (silent) {
        if (this.isCollapsed()) {
            this.updateExpanded(true);

            if (!silent) {
                this.fireEvent('expand', this, this.expanded, this.getDqName());
            }
        }
    },

    collapse: function (silent) {
        if (this.isExpanded()) {
            this.updateExpanded(false);

            if (!silent) {
                this.fireEvent('collapse', this, this.expanded, this.getDqName());
            }
        }
    },

    toggle: function (silent) {
        if (this.isExpanded()) {
            this.collapse(silent);
        } else {
            this.expand(silent);
        }
    },

    updateExpandCls: function () {
        var cls = 'un-dataentity-dqtablet-expanded';

        if (this.expanded) {
            this.addCls(cls);
        } else {
            this.removeCls(cls);
        }
    },

    getDqIconHtml: function (dqError) {
        var severity = dqError.get('severity'),
            iconName = 'rank',
            iconHtml;

        switch (severity) {
            case 'CRITICAL':
                iconName = 'rank3';
                break;
            case 'HIGH':
                iconName = 'rank2';
                break;
            case 'NORMAL':
                iconName = 'rank';
                break;
        }

        iconHtml = Unidata.util.Icon.getLinearIcon(iconName);

        return iconHtml;
    },

    updateRotateCls: function (dqError) {
        var severity = dqError.get('severity'),
            cls      = 'rotate',
            severityCls;

        this.iconContainer.removeCls(cls);

        severityCls = 'dq-severity-' + severity.toLowerCase();

        if (severity === 'LOW') {
            this.iconContainer.addCls(cls);
        }

        this.iconContainer.removeCls(this.severityCls);
        this.iconContainer.addCls(severityCls);
        this.severityCls = severityCls;
    }
});
