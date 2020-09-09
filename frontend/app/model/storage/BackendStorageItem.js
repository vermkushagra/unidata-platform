/**
 * Единица данных, из backend хранилища
 * @see Unidata.module.storage.BackendStorageManager
 *
 * @author Aleksandr Bavin
 * @date 2017-10-19
 */
Ext.define('Unidata.model.settings.BackendStorageItem', {

    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'user',
            type: 'string'
        },
        {
            name: 'key',
            type: 'string'
        },
        {
            name: 'value',
            type: 'string'
        },
        {
            name: 'updateDate',
            type: 'date',
            persist: false
        },
        {
            name: 'compoundKey',
            type: 'string',
            persist: false,
            convert: function (value, record) {
                var compoundKey = Unidata.BackendStorage.modelIdGenerator(record.get('key'), record.get('user')),
                    model = this.owner;

                if (model.data) {
                    model.data[model.idField.name] = compoundKey;
                }

                model.id = compoundKey;

                return compoundKey;
            },
            depends: ['key', 'user']
        }
    ],

    constructor: function () {
        var idProperty = this.idField.name;

        this.callParent(arguments);

        this.data[idProperty] = this.id = this.get('compoundKey');
    }

});
