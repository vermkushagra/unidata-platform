/**
 * Diff по связям типа Contains (для atomic upsert)
 *
 * @author Sergey Shishigin
 * @date 2018-06-22
 */
Ext.define('Unidata.model.data.RelationContainsDiff', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false}
    ],

    hasMany: [
        {
            name: 'toUpdate',
            model: 'data.RelationContains'
        },
        {
            name: 'toDelete',
            model: 'data.RelationContains'
        }
    ]
});
