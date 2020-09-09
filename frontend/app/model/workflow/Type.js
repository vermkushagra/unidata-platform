/**
 * Тип действий
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.model.workflow.Type', {

    extend: 'Unidata.model.Base',

    idProperty: 'code',

    fields: [
        {
            name: 'code',   // пример: RECORD_EDIT
            type: 'string'
        },
        {
            name: 'name', // пример: Подтверждение изменений
            type: 'string'
        },
        {
            name: 'description', // пример: Подтверждение изменений для записи данных общего назначения
            type: 'string'
        }
    ],

    proxy: {
        type: 'ajax',
        url: Unidata.Config.getMainUrl() + 'internal/data/workflow/types',
        pageParam: '',
        startParam: '',
        limitParam: '',
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    }

});
