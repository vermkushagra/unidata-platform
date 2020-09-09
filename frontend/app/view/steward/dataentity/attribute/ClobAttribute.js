/**
 * Класс, реализующий редактирование аттрибута типа clob
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.ClobAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.BlobAttribute',

    statics: {
        TYPE: 'Clob'
    },

    config: {
        alwaysShowInput: true
    },

    extensions: ['txt']

});
