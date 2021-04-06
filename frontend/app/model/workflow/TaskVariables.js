/**
 * @author Aleksandr Bavin
 * @date 17.06.2016
 */
Ext.define('Unidata.model.workflow.TaskVariables', {

    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'wfCreateDate',
            type: 'date',
            dateReadFormat: 'Y-m-d\\TH:i:s.uZ'
        }
    ]
});
