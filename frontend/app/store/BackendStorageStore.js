/**
 * Стор для хранения данных для {@see Unidata.module.storage.BackendStorageManager}
 *
 * @author Aleksandr Bavin
 * @date 2017-10-01
 */
Ext.define('Unidata.store.BackendStorageStore', {

    extend: 'Ext.data.Store',

    mixins: [
        'Unidata.mixin.data.StoreLoadUpdate'
    ],

    requires: [
        'Unidata.model.settings.BackendStorageItem'
    ],

    model: 'Unidata.model.settings.BackendStorageItem',

    proxy: 'storage.backend'

});
