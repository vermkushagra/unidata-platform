/**
 * Модель набора сохраненных поисковых запросов
 *
 * @author Sergey Shishigin
 * @date 2017-02-02
 */
Ext.define('Unidata.model.search.QueryPreset', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'entityName',
            type: 'string'
        },
        {
            name: 'extraParams',
            type: 'auto'
        }
    ]
});
