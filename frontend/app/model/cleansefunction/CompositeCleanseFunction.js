Ext.define('Unidata.model.cleansefunction.CompositeCleanseFunction', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'javaClass',
            type: 'string'
        },
        {
            name: 'type',
            type: 'string'
        }
    ],

    hasOne: [{
        name: 'logic',
        model: 'cleansefunction.Logic'
    }],

    hasMany: [
        {
            name: 'inputPorts',
            model: 'cleansefunction.InputPort'
        },
        {
            name: 'outputPorts',
            model: 'cleansefunction.OutputPort'
        }
    ],

    proxy: {
        type: 'rest',
        url: Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions/',
        reader: {
            type: 'json'
            //rootProperty: 'content'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    },

    findOutputPortByName: function (name) {
        return this.outputPorts().findRecord('name', name, 0, false, false, true);
    }
});
