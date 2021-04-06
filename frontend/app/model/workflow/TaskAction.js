/**
 * Доступные действия для задачи
 * @author Aleksandr Bavin
 * @date 2016-08-12
 */
Ext.define('Unidata.model.workflow.TaskAction', {

    extend: 'Unidata.model.Base',

    idProperty: 'code',

    fields: [
        {
            name: 'code', // APPROVE
            type: 'string'
        },
        {
            name: 'description', // Подтвердить изменения записи
            type: 'string'
        },
        {
            name: 'name', // Подтвердить изменения
            type: 'string'
        }
    ]

});
