/**
 * formFields для поиска по атрибутам связей
 *
 * @author Aleksandr Bavin
 * @date 2018-02-13
 */
Ext.define('Unidata.module.search.term.relation.EtalonIdFormField', {

    extend: 'Unidata.module.search.term.relation.FormField',

    config: {
        calculatedDisplayName: null,
        name: '$etalon_id_to'
    }

});
