/**
 * Позволяет в url подставлять значения полей записи
 * Пример: /some/url/path/{fieldName}/{otherFieldName}
 *
 * Кроме того, добавился baseUrl, который добавляется в начало
 *
 * @author Aleksandr Bavin
 * @date 2016-07-08
 */
Ext.define('Unidata.proxy.rest.Extended', {
    extend: 'Ext.data.proxy.Rest',

    mixins: [
        'Unidata.mixin.proxy.UrlTemplateParams'
    ],

    alias: 'proxy.rest.extended',

    appendId: false,

    buildUrl: function (request) {
        var url       = this.callParent(arguments),
            operation = request.getOperation(),
            records   = operation.getRecords(),
            record    = records ? records[0] : null;

        if (record) {
            this.setUrlParams(record.getData());
        }

        return url;
    }

});
