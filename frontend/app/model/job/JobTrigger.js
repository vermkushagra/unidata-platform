/**
 * Job trigger model
 *
 * @author Sergey Shishigin
 * @date 2017-12-19
 */

Ext.define('Unidata.model.job.JobTrigger', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'tempId',
            type: 'auto',
            persist: false
        },
        {
            name: 'id',
            type: 'int',
            allowNull: true
        },
        {
            name: 'startJobId',
            type: 'int'
        },
        {
            name: 'successRule',
            type: 'boolean'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        }
    ]
});
