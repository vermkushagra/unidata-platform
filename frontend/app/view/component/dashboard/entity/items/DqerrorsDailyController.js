/**
 * @author Aleksandr Bavin
 * @date 2018-08-23
 */
Ext.define('Unidata.view.component.dashboard.entity.items.DqerrorsDailyController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.component.dashboard.entity.dqerrors.daily',

    init: function () {
        this.callParent(arguments);

        this.initInputValues();
        this.loadData();
    },

    initInputValues: function () {
        var dateToday = new Date(),
            dateWeekBefore;

        dateWeekBefore = Ext.Date.add(dateToday, Ext.Date.DAY, -6);

        this.lookupReference('startDate').setValue(dateWeekBefore);
        this.lookupReference('endDate').setValue(dateToday);
    },

    getDqerrorsStore: function () {
        return this.getViewModel().getStore('dqerrors');
    },

    onSearchButtonClick: function () {
        this.loadData();
    },

    loadData: function () {
        var view = this.getView(),
            store = this.getDqerrorsStore();

        view.setLoading(true);

        store.load();
    },

    /**
     * @param store
     */
    onDataLoad: function (store) {
        var me = this;

        this.lookupReference('entities').getLoadedStore()
            .then(
                function (store) {
                    var entityMap = {};

                    store.each(function (item) {
                        entityMap[item.get('name')] = item.get('displayName');
                    });

                    return entityMap;
                }
            )
            .then(
                function (entityMap) {
                    var view = me.getView(),
                        today = Ext.Date.format(new Date(), 'd.m.Y'),
                        dataMap = {},
                        plainData = [],
                        dateColumns = [],
                        severityMap = {};

                    Ext.Array.each(Unidata.model.dataquality.DqRaise.getSeverityList(), function (severityItem) {
                        severityMap[severityItem['value']] = severityItem['text'];
                    });

                    store.each(function (item) {
                        var entityName = item.get('entityName'),
                            category = item.get('category') || '-',
                            severity = item.get('severity') || '-',
                            atDate = item.get('atDate'),
                            dateFormatted = Ext.Date.format(atDate, 'd.m.Y'),

                            count = item.get('count'),
                            dataItem;

                        // инициализация данных
                        dataItem = Ext.Object.setValueByPath(
                            dataMap,
                            [entityName, category, severity],
                            {
                                entityName: entityName,
                                entityDisplayName: entityMap[entityName],
                                category: category,
                                severity: severity,
                                severityDisplayName: severityMap[severity],
                                today: today,
                                totalCount: 0
                            },
                            true
                        );

                        dataItem[dateFormatted] = count;
                        dataItem.totalCount += count;

                        Ext.Array.include(plainData, dataItem);
                        Ext.Array.include(dateColumns, dateFormatted);
                    }, me);

                    // сортируем колонки с датами
                    Ext.Array.sort(dateColumns, function (a, b) {
                        a = a.split('.').reverse().join('');
                        b = b.split('.').reverse().join('');

                        return a > b ? 1 : a < b ? -1 : 0;
                    });

                    me.lookupReference('tableContainer').update({
                        dateColumns: dateColumns,
                        plainData: plainData
                    });

                    view.setLoading(false);
                }
            )
            .done();
    },

    setProxyValue: function (name, value) {
        var store = this.getDqerrorsStore(),
            proxy = store.getProxy();

        proxy.setExtraParam(name, value);
    },

    onStartDateChange: function (dateField) {
        var value = dateField.getValue();

        this.setProxyValue('startDate', value || null);
        this.lookupReference('endDate').setMinValue(value);
    },

    onEndDateChange: function (dateField) {
        var value = dateField.getValue();

        this.setProxyValue('endDate', value || null);
        this.lookupReference('startDate').setMaxValue(value);
    },

    onEntitiesChange: function (field) {
        this.setProxyValue('entities', field.getValue() || null);
    },

    onSeverityChange: function (field) {
        this.setProxyValue('dimension1', field.getValue() || null);
    }

});
