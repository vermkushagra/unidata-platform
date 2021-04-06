/**
 * @author Aleksandr Bavin
 * @date 2016-07-01
 */
Ext.define('Unidata.view.admin.audit.log.AuditLogController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.log',

    init: function () {
        this.callParent(arguments);
        this.initAuditEventTypeStore();
    },

    /**
     * Заполняем стор с типами событий по мапперу
     */
    initAuditEventTypeStore: function () {
        var view = this.getView(),
            auditEventTypeMapper = view.auditEventTypeMapper,
            auditEventTypeStore = this.getStore('auditEventTypeStore');

        Ext.Object.each(auditEventTypeMapper, function (key, value) {
            auditEventTypeStore.add({
                text: value,
                value: key
            });
        });
    },

    subSystemRenderer: function (value) {
        var view = this.getView(),
            subSystemMapper = view.subSystemMapper,
            newValue = subSystemMapper[value];

        return newValue ? newValue : value;
    },

    auditEventTypeRenderer: function (value) {
        var view = this.getView(),
            auditEventTypeMapper = view.auditEventTypeMapper,
            newValue = auditEventTypeMapper[value];

        return newValue ? newValue : value;
    },

    onFilterChange: function () {
        var store = this.getStore('auditStore'),
            proxy = store.getProxy(),
            view = this.getView(),
            values = view.getValues();

        Ext.Object.clear(proxy.getExtraParams());

        Ext.Object.each(values, function (key, value) {
            if (!value) {
                return;
            }
            proxy.setExtraParam(key, value);
        });

        if (this.timer) {
            clearTimeout(this.timer);
        }

        this.timer = Ext.Function.defer(function () {
            store.loadPage(1);
        }, 1000);

    }

});
