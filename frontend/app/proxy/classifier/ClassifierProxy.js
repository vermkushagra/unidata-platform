/**
 * Прокси для работы с классификатором
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.proxy.classifier.ClassifierProxy', {

    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.un.classifier',

    url: Unidata.Config.getMainUrl() + 'internal/data/classifier',

    api: {
        create:  Unidata.Api.getClassifierUrl(),
        read:    Unidata.Api.getClassifierUrl(),
        update:  Unidata.Api.getClassifierUrl(),
        destroy: Unidata.Api.getClassifierUrl()
    },

    reader: {
        type: 'json',
        model: 'Unidata.model.classifier.Classifier',
        rootProperty: 'content'
    },
    writer: {
        writeAllFields: true
    }
});
