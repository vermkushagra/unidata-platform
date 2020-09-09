/**
 * имя dq
 *
 * @author Aleksandr Bavin
 * @date 2018-02-13
 */
Ext.define('Unidata.module.search.term.dq.RuleNameFormField', {

    extend: 'Unidata.module.search.term.dq.FormField',

    termName: 'dq.ruleName',

    config: {
        name: '$dq_errors.ruleName'
    }

});
