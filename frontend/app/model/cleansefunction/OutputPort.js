Ext.define('Unidata.model.cleansefunction.OutputPort', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'required',
            type: 'boolean'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'dataType',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        }
    ],

    getPortType: function () {
        return 'output';
    }

});
