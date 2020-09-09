/**
 * Модель шага выполнения операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.model.job.execution.ExecutionStep', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'jobExecutionId',
            type: 'int'
        },
        {
            name: 'stepName',
            type: 'string'
        },
        {
            name: 'startTime',
            type: 'date',
            dateReadFormat: 'c'
        },
        {
            name: 'endTime',
            type: 'date',
            dateReadFormat: 'c'
        },
        {
            name: 'status',
            type: 'string'
        }
    ],

    getStatusText: function () {
        return Unidata.model.job.execution.Execution.getStatusText(this.get('status'));
    }

});
