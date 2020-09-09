/**
 * Модель связи реестра, процесса и типа процесса
 * @author Aleksandr Bavin
 * @date 2016-08-05
 */
Ext.define('Unidata.model.workflow.Assignment', {

    extend: 'Unidata.model.Base',

    idProperty: 'id',

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'entityName', // реестр - Unidata.model.entity.Entity.name
            type: 'string'
        },
        {
            name: 'displayName', // реестр - Unidata.model.entity.Entity.name
            type: 'string',
            persist: false
        },
        {
            name: 'processType', // Unidata.model.workflow.Process.type / Unidata.model.workflow.Type.code
            type: 'string'
        },
        {
            name: 'triggerType',
            type: 'string'
        },
        {
            name: 'processDefinitionId', // Unidata.model.workflow.Process.id
            type: 'string'
        },
        {
            name: 'createDate',
            type: 'date',
            persist: false
        },
        {
            name: 'updateDate',
            type: 'date',
            persist: false
        },
        {
            name: 'createdBy',
            type: 'string',
            persist: false
        },
        {
            name: 'updatedBy',
            type: 'string',
            persist: false
        }
    ],

    proxy: {
        type: 'ajax',
        url: Unidata.Config.getMainUrl() + 'internal/data/workflow/assignments',
        pageParam: '',
        startParam: '',
        limitParam: '',
        reader: {
            type: 'json',
            rootProperty: 'content'
        }
    }

});
