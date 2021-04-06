/**
 * Панель отображающая уведомления пользователя
 *
 * @author Ivan Marshalkin
 * @date 2016-06-22
 */

Ext.define('Unidata.view.steward.notification.NotificationWindow', {
    extend: 'Ext.window.Window',

    requires: [
        'Unidata.view.steward.notification.NotificationArea'
    ],

    referenceHolder: true,

    cls: 'un-notificationwindow',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    modal: false,
    closable: false,
    resizable: false,
    header: false,
    shadow: false,

    padding: 0,

    items: [
        {
            xtype: 'container',
            width: 54
        },
        {
            xtype: 'steward.notificationarea',
            reference: 'notificationArea',
            padding: '5 0 0 10',
            width: 400
        },
        {
            xtype: 'container',
            cls: 'un-notificationwindow-rightcontainer',
            flex: 1
        }
    ],

    listeners: {
        beforeshow: function () {
            this.resizeWindow();
        },
        show: function () {
            Ext.get(window).on('click', this.onDocumentClick, this);
        }
    },

    resizeWindow: function () {
        var size = Ext.getBody().getViewSize();

        this.setHeight(size.height);
        this.setWidth(size.width);

        this.setPosition(0, 0);
    },

    onResizeWindow: function () {
        this.resizeWindow();
    },

    onDocumentClick: function (e) {
        var area = this.lookupReference('notificationArea');

        if (area.rendered && (e.within(area.el) || area.getPreventClose())) {
            e.preventDefault();
            e.stopPropagation();
        } else {
            this.close();
        }
    },

    initEvents: function () {
        this.callParent(arguments);

        Ext.fly(window).on('resize', this.onResizeWindow, this);
    },

    onDestroy: function () {
        Ext.fly(window).un('resize', this.onResizeWindow, this);
        Ext.get(window).on('click', this.onDocumentClick, this);

        this.callParent(arguments);
    }
});
