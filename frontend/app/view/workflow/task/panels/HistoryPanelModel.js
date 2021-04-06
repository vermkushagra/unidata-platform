/**
 * @author Aleksandr Bavin
 * @date 2016-08-25
 */
Ext.define('Unidata.view.workflow.task.panels.HistoryPanelModel', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanelModel',
    alias: 'viewmodel.history',

    data: {
        title: Unidata.i18n.t('workflow>processHistory'),
        addItemText: Unidata.i18n.t('workflow>processState')
    },

    stores: {
        itemsStore: {
            model: 'Unidata.model.workflow.History',
            proxy: 'workflow.history'
        }
    }
});
