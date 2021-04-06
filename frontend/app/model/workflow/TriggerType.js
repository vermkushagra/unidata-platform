/**
 * Типы условий запуска
 *
 * @author Ivan Marshalkin
 * @date 2017-09-06
 */
Ext.define('Unidata.model.workflow.TriggerType', {
    extend: 'Unidata.model.Base',

    idProperty: 'name',

    fields: [
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        }
    ]
});
