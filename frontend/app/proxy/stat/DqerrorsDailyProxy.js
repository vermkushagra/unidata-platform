/**
 * Прокси для получения ежедневной статистики по dq
 *
 * @author Aleksandr Bavin
 * @date 2018-08-23
 */
Ext.define('Unidata.proxy.stat.DqerrorsDailyProxy', {

    extend: 'Ext.data.proxy.Ajax',

    alias: 'proxy.stat.dqerrors.daily',

    model: 'Unidata.model.dashboard.Stats',

    limitParam: '',
    startParam: '',
    pageParam: '',

    url: Unidata.Config.getMainUrl() + 'internal/data/stat/get/DQ_ERRORS',

    extraParams: {
        // startDate: null,
        // endDate: null,
        // entities: null,
        // dimension1: null // severity
    },

    reader: {
        type: 'json',
        rootProperty: 'content.data'
    },

    setSeverity: function (value) {
        this.setExtraParam('dimension1', value);
    }

});
