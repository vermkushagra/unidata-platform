/**
 * критичность dq
 *
 * @author Aleksandr Bavin
 * @date 2018-02-13
 */
Ext.define('Unidata.module.search.term.dq.SeverityFormField', {

    extend: 'Unidata.module.search.term.dq.FormField',

    termName: 'dq.severity',

    config: {
        name: '$dq_errors.severity'
    }

});
