/**
 *
 * @author Sergey Shishigin
 * @date 2015-05-17
 */
Ext.define('Unidata.model.search.SearchPreview', {
    extend: 'Unidata.model.Base',

    fields: [
        {name: 'field', type: 'string'},
        {name: 'value', type: 'string'}
    ]
});
