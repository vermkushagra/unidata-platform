/**
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.view.workflow.task.panels.CommentsPanelController', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanelController',
    alias: 'controller.comments',

    onAddItemClick: function (button) {
        var viewModel = this.getViewModel();

        Ext.MessageBox.show({
            title: viewModel.get('addItemText'),
            width: 500,
            buttons: Ext.MessageBox.OKCANCEL,
            multiline: true,
            scope: this,
            fn: this.submitItem,
            animateTarget: button
        });
    },

    submitItem: function (button, text) {
        var view = this.getView(),
            comment;

        if (button == 'ok' && Ext.String.trim(text) != '') {
            comment = Ext.create('Unidata.model.workflow.Comment', {
                processInstanceId: this.getView().getProcessId(),
                message: text
            });

            comment.save({
                callback: function () {
                    view.updateProcessId(view.getProcessId());
                    view.expand();
                }
            });
        }
    }

});
