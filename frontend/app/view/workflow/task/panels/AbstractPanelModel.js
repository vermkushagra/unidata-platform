/**
 * @author Aleksandr Bavin
 * @date 2016-08-17
 */
Ext.define('Unidata.view.workflow.task.panels.AbstractPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.abstract',

    data: {
        title: '',
        addItemText: '',
        itemsCount: 0
    },

    stores: {
        itemsStore: null
    },

    formulas: {
        savebuttonHidden: {
            bind: {
                finished: '{task.finished}',
                currentUserIsAssignee: '{currentUserIsAssignee}'
            },
            get: function (data) {
                if (data.finished) {
                    return true;
                }

                if (!data.currentUserIsAssignee) {
                    return true;
                }

                return false;
            }
        }
    }

});
