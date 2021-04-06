Ext.define('Unidata.proxy.Base', {
    extend: 'Ext.data.proxy.Rest',

    alias: 'proxy.base',

    writer: {
        type: 'json',
        writeAllFields: true
    },
    reader: {
        type: 'json',
        rootProperty: 'content'
    }
});
