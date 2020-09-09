/**
 * Константы для
 * @see {Unidata.module.search.term.FormField}
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.module.search.term.FormFieldStatics', {

    singleton: true,

    type: {
        BOOLEAN: 'Boolean',
        STRING: 'String',
        NUMBER: 'Number',
        INTEGER: 'Integer',
        DATE: 'Date',
        TIMESTAMP: 'Timestamp',
        TIME: 'Time',
        BLOB: 'Blob',
        CLOB: 'Clob'
    },

    searchType: {
        EXACT: 'EXACT',
        FUZZY: 'FUZZY',
        MORPHOLOGICAL: 'MORPHOLOGICAL',
        EXIST: 'EXIST',
        START_WITH: 'START_WITH',
        LIKE: 'LIKE',
        RANGE: 'RANGE'
    }

});
