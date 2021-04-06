/**
 * Компонент для просмотра ошибки выполнения операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-04
 */

Ext.define('Unidata.view.admin.job.part.JobExecutionError', {

    extend: 'Ext.Component',

    alias: 'widget.admin.job.execution.error',

    config: {
        exitCode: '',
        exitDescription: ''
    },

    baseCls: 'un-job-execution-error',

    renderTpl: [
        '<div class="{baseCls}-title">exitCode:</div>',
        '<div class="{baseCls}-exitCode">{exitCode:htmlEncode}</div>',
        '<div class="{baseCls}-title">exitDescription:</div>',
        '<div class="{baseCls}-exitDescription">{exitDescription:htmlEncode}</div>'
    ],

    constructor: function () {
        this.renderData = {};
        this.callParent(arguments);
    },

    setExitCode: function (message) {
        this.renderData.exitCode = message;
    },

    setExitDescription: function (description) {
        this.renderData.exitDescription = description;
    }

});
