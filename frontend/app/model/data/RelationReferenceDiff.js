/**
 * Diff по связям типа Reference (для atomic upsert)
 *
 * @author Sergey Shishigin
 * @date 2018-06-22
 */
Ext.define('Unidata.model.data.RelationReferenceDiff', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false}
    ],

    hasMany: [
        {
            name: 'toUpdate',
            model: 'data.RelationReference'
        },
        {
            name: 'toDelete',
            model: 'data.RelationReferenceDelete'
        }
    ]
});
