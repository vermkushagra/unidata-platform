/**
 * Прокси для получения/сохранения данных в backend хранилище
 *
 * @author Aleksandr Bavin
 * @date 2017-10-19
 */
Ext.define('Unidata.proxy.storage.BackendStorageProxy', {

    extend: 'Ext.data.proxy.Rest',

    mixins: [
        'Unidata.mixin.proxy.UrlTemplateParams'
    ],

    alias: 'proxy.storage.backend',

    model: 'Unidata.model.settings.BackendStorageItem',

    limitParam: '',
    startParam: '',
    pageParam: '',

    paramsAsJson: false,
    batchActions: true,
    appendId: false,

    baseUrl: Unidata.Config.getMainUrl(),

    actionMethods: {
        create:  'POST',
        read:    'GET',
        update:  'PUT',
        destroy: 'DELETE'
    },

    api: {
        create:  'internal/custom-storage',
        update:  'internal/custom-storage',
        destroy: 'internal/custom-storage',
        /**
         * Для read нужно использовать только один из параметров key или user_name, в случае,
         * если нужно получить все данные по key или user_name
         *
         * Для получения данных по key + user_name нужно использовать extraParams
         */
        read:    'internal/custom-storage<tpl if="key">/key/{key}</tpl><tpl if="user_name">/user_name/{user_name}</tpl>'
    },

    // Для получения данных по key + user_name
    extraParams: {
        // key: null,
        // user_name: null
    },

    reader: {
        type: 'json',
        rootProperty: 'content'
    },

    writer: {
        type: 'json',
        allowSingle: false,
        writeAllFields: true,
        writeRecordId: false
    }

});
