/**
 * @author Aleksandr Bavin
 * @date 2016-08-18
 */
Ext.define('Unidata.view.workflow.tasksearch.query.QueryModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.query',

    data: {
        searchMy: true,
        searchAvailable: false,
        searchHistorical: false, // поиск по истории
        searchComplex: false // расширенный поиск
    }

});
