/**
 * Модель справочника для использования в древовидной структуре
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-19
 */
Ext.define('Unidata.model.entity.catalog.LookupEntity', {

    extend: 'Unidata.model.entity.LookupEntity',

    requires: [
        'Ext.data.NodeInterface'
    ],

    fields: [
        {
            name: 'entityName',
            type: 'string',
            persist: false,
            convert: function (value, model) {
                return model.get('name');
            },
            depends: ['name']
        },
        {
            name: 'iconCls',
            type: 'string',
            persist: false,
            calculate: function () {
                return Unidata.model.entity.AbstractEntity.getTypeIconCls('LookupEntity');
            }
        }
    ]
},
function () {
    Ext.data.NodeInterface.decorate(this);
});
