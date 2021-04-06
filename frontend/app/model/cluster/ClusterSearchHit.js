/**
 *
 * @author Ivan Marshalkin
 * @date 2016-10-28
 */

Ext.define('Unidata.model.cluster.ClusterSearchHit', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {
            name: 'entityName',
            type: 'string'
        },
        {
            name: 'groupId',
            type: 'int'
        },
        {
            name: 'ruleId',
            type: 'int'
        },
        {
            name: 'clusterId',
            type: 'int'
        },
        {
            name: 'matchingDate',
            type: 'string'
        },
        {
            name: 'recordsCount',
            type: 'int'
        }
    ],

    hasMany: [
        {
            name: 'records',
            model: 'data.DataRecordKey'
        },
        {
            name: 'preview',
            model: 'search.SearchPreview'
        }
    ]
});
