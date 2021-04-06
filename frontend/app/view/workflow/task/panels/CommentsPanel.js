/**
 * @author Aleksandr Bavin
 * @date 2016-08-16
 */
Ext.define('Unidata.view.workflow.task.panels.CommentsPanel', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanel',

    requires: [
        'Unidata.view.workflow.task.panels.CommentsPanelController',
        'Unidata.view.workflow.task.panels.CommentsPanelModel'
    ],

    alias: 'widget.workflow.task.panels.comments',

    viewModel: {
        type: 'comments'
    },

    controller: 'comments',

    /**
     * @param {Unidata.model.workflow.Comment} comment
     */
    addPanelItem: function (comment) {
        var title = comment.get('username') + ' ' + Ext.Date.format(comment.get('dateTime'), 'd.m.Y H:i'),
            value = comment.get('message');

        this.add({
            xtype: 'workflow.task.property',
            title: title,
            value: value
        });
    }

});
