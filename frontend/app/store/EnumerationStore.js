/**
 * Enumeration store
 *
 * @author Sergey Shishigin
 * @date 2016-10-20
 */
Ext.define('Unidata.store.EnumerationStore', {

    extend: 'Ext.data.Store',

    alias: 'store.un.enumeration',

    model: 'Unidata.model.entity.Enumeration',

    proxy: {
        type: 'un.enumeration',

        limitParam: '',
        startParam: '',
        pageParam: ''
    }
});
