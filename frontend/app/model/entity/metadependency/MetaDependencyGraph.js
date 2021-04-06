/**
 * Метамодель графа мета-зависимостей
 *
 * @author Sergey Shishigin
 * @date 2017-03-01
 */
Ext.define('Unidata.model.entity.metadependency.MetaDependencyGraph', {

    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.entity.metadependency.Vertex',
        'Unidata.model.entity.metadependency.Edge'
    ],

    idProperty: 'tempId',

    hasMany: [
        {
            name: 'vertexes',
            model: 'entity.metadependency.Vertex'
        },
        {
            name: 'edges',
            model: 'entity.metadependency.Edge'
        }
    ]
});
