/**
 * @author Aleksandr Bavin
 * @date 2018-08-23
 */
Ext.define('Unidata.view.component.dashboard.entity.items.DqerrorsDailyModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.dashboard.entity.dqerrors.daily',

    stores: {
        dqerrors: {
            model: 'Unidata.model.dashboard.DqerrorsDaily',
            proxy: 'stat.dqerrors.daily',
            sorters: ['entityName', 'category'],
            listeners: {
                load: 'onDataLoad'
            }
        }
    }
});
