/**
 * Окно для просмотра ошибки выполнения операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-04
 */

Ext.define('Unidata.view.admin.job.part.JobExecutionErrorWindow', {

    extend: 'Ext.window.Window',

    alias: 'widget.admin.job.execution.error.wnd',

    requires: [
        'Unidata.view.admin.job.part.JobExecutionError'
    ],

    config: {
        title: Unidata.i18n.t('admin.job>execOperationError'),
        exitCode: '',
        exitDescription: '',
        minWidth: 500,
        maxWidth: 900,
        scrollable: true,
        modal: true,
        autoShow: true
    },

    initItems: function () {

        this.callParent(arguments);

        this.add({
            xtype: 'admin.job.execution.error',
            exitCode: this.getExitCode(),
            exitDescription: this.getExitDescription()
        });

    }
});
