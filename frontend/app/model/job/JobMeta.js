/**
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */
Ext.define('Unidata.model.job.JobMeta', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.job.parameter.MetaParameter'
    ],

    fields: [
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'parameters',
            type: 'auto'
        }
    ],

    hasMany: [
        {
            name: 'parameters',
            model: 'job.parameter.MetaParameter'
        }
    ],

    getType: function () {
        return this.get('name');
    }

});
