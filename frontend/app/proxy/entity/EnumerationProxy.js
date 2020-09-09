/**
 * Прокси для работы с перечислениями
 *
 * @author Sergey Shishigin
 * @date 2016-10-20
 */
Ext.define('Unidata.proxy.entity.EnumerationProxy', {
    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.un.enumeration',

    url: Unidata.Config.getMainUrl() + 'internal/meta/enumerations',

    api: {
        read: Unidata.Api.getEnumerationsUrl()
    },

    reader: {
        type: 'json',
        model: 'Unidata.model.entity.Enumeration',
        rootProperty: 'content'
    },
    writer: {
        writeAllFields: true
    }
});
