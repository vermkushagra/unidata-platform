/**
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.view.workflow.task.panels.CommentsPanelModel', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanelModel',
    alias: 'viewmodel.comments',

    data: {
        title: Unidata.i18n.t('workflow>comment'),
        addItemText: Unidata.i18n.t('workflow>addComment')
    },

    stores: {
        itemsStore: {
            model: 'Unidata.model.workflow.Comment',
            proxy: 'workflow.comments'
        }
    }
});
