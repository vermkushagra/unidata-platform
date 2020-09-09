/**
 * Процесс
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.model.workflow.Process', {

    extend: 'Unidata.model.Base',

    idProperty: 'id',

    fields: [
        {
            name: 'id',   // пример: approvalProcess
            type: 'string'
        },
        {
            name: 'name', // пример: Стандартный процесс подтверждения изменений записи
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'type', // пример: RECORD_APPROVE
            type: 'string'
        },
        {
            name: 'path', // пример: wf/record-change-approval.bpmn20.xml
            type: 'string'
        }
    ],

    proxy: {
        type: 'ajax',
        url: Unidata.Config.getMainUrl() + 'internal/data/workflow/processes',
        pageParam: '',
        startParam: '',
        limitParam: '',
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    }

});
