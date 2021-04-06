/**
 * Метамодель ребер графа мета-зависимостей
 *
 * @author Sergey Shishigin
 * @date 2017-03-01
 */
Ext.define('Unidata.model.entity.metadependency.Edge', {

    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.entity.metadependency.Vertex'
    ],

    idProperty: 'tempId',

    fields: [
        {
            name: 'existence',
            type: 'string'
        }
    ],

    hasOne: [
        {
            name: 'from',
            model: 'entity.metadependency.Vertex'
        },
        {
            name: 'to',
            model: 'entity.metadependency.Vertex'
        }
    ],

    statics: {
        existence: {
            NEW: 'NEW',             // есть только в импортируемых данных
            EXIST: 'EXIST',         // есть только в системе
            UPDATE: 'UPDATE',       // есть и в системе и в импортируемых данных
            NOT_FOUND: 'NOT_FOUND'  // нет нигде
        }
    }
});
