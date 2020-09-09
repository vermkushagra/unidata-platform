/**
 * @author Aleksandr Bavin
 * @date 2016-07-01
 */
Ext.define('Unidata.view.admin.audit.log.AuditLogController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.log',

    lastRequestParams: null,

    auditLogState: null, // сохранённое состояние экрана

    init: function () {
        var view;

        this.callParent(arguments);

        view = this.getView();

        this.initAuditEventTypeStore();
        view.on('beforerender', this.initGridColumnsState, this);
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

    onAuditStoreLoad: function (store, records, successful, operation) {
        if (successful) {
            this.lastRequestParams = operation.getRequest().getParams();
        }
    },

    /**
     * Инициализация состояния грида из данных с BE
     */
    initGridColumnsState: function () {
        var searchResultGrid = this.lookupReference('searchResultGrid'),
            columns = searchResultGrid.columns,
            auditLogState = this.getCurrentAuditLogState();

        if (auditLogState) {
            Ext.Array.each(columns, function (column) {
                var savedColumnData = auditLogState.columns[column.dataIndex];

                if (!savedColumnData) {
                    return;
                }

                column.setHidden(savedColumnData.hidden);
            });
        }

        searchResultGrid.on('columnhide', this.saveColumnStateDelayed, this);
        searchResultGrid.on('columnshow', this.saveColumnStateDelayed, this);
    },

    /**
     * Сохранение состояния колонок на BE
     */
    saveColumnsState: function () {
        var searchResultGrid = this.lookupReference('searchResultGrid'),
            columns = searchResultGrid.columns,
            saveData = this.getCurrentAuditLogState() || this.getDefaultAuditLogState();

        saveData.columns = {};

        Ext.Array.each(columns, function (column) {
            if (!column.dataIndex) {
                return;
            }

            saveData.columns[column.dataIndex] = {
                hidden: column.hidden
            };
        });

        this.auditLogState = saveData;

        Unidata.BackendStorage.setCurrentUserValue(
            Unidata.BackendStorageKeys.AUDIT_LOG,
            Ext.JSON.encode(saveData)
        );

        Unidata.BackendStorage.save();
    },

    saveColumnStateDelayed: function () {
        clearTimeout(this.saveTimer);
        this.saveTimer = Ext.defer(this.saveColumnsState, 3000, this);
    },

    /**
     * Структура данных для сохранения на BE
     */
    getDefaultAuditLogState: function () {
        return {
            columns: {} // карта колонок по dataIndex
        };
    },

    /**
     * Получает состояние и кэширует его
     *
     * @return {*}
     */
    getCurrentAuditLogState: function () {
        if (this.auditLogState) {
            return this.auditLogState;
        }

        this.auditLogState = Ext.JSON.decode(
            Unidata.BackendStorage.getCurrentUserValue(
                Unidata.BackendStorageKeys.AUDIT_LOG
            )
        );

        return this.auditLogState;
    },

    onExportClick: function () {
        Unidata.showMessage(Unidata.i18n.t('admin.audit>exportStarted'));

        Ext.Ajax.unidataRequest({
            url: Unidata.Config.getMainUrl() + 'internal/audit/export',
            method: 'POST',
            headers: {
                'Accept':       'application/json',
                'Content-Type': 'application/json'
            },
            jsonData: Ext.util.JSON.encode(this.lastRequestParams),
            success: function () {
            }
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
