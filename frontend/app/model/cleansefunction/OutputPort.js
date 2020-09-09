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
        },
        {
            name: 'portType', // OUTPUT|OUTPUT_TRUE|OUTPUT_FALSE
            type: 'string'
        },
        {
            name: 'portApplicationMode',
            type: 'enum',
            enumList: Unidata.util.DataQuality.portApplicationModeEnumList,
            defaultValue: Unidata.util.DataQuality.portApplicationModeEnumList.MODE_ALL
        }
    ],

    getPortType: function () {
        return Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT;
    }

});
