/**
 * Атомарная запись для atomic upsert
 *
 * @author Sergey Shishigin
 * @date 2018-06-22
 */
Ext.define('Unidata.model.data.AtomicRecord', {
    extend: 'Unidata.model.Base',

    hasOne: [
        {
            name: 'dataRecord',
            model: 'data.Record'
        },
        {
            name: 'relationReference',
            model: 'data.RelationReferenceDiff'
        },
        {
            name: 'relationContains',
            model: 'data.RelationContainsDiff'
        },
        {
            name: 'relationManyToMany',
            model: 'data.RelationReferenceDiff'
        }
    ]
});
