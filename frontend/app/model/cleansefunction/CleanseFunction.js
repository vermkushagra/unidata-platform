Ext.define('Unidata.model.cleansefunction.CleanseFunction', {
    extend: 'Unidata.model.ExtendedBase',

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
        }
    },

    statics: {
        createTypeIcon: function (type) {
            var faType, title;

            faType = '';

            switch (type){
                case 'BASIC_FUNCTION':
                    faType = 'fa-gear';
                    title = Unidata.i18n.t('model>simpleFunction');
                    break;
                case 'COMPOSITE_FUNCTION':
                    faType = 'fa-gears';
                    title = Unidata.i18n.t('model>compositeFunction');
                    break;
                case 'CUSTOM_FUNCTION':
                    faType = 'fa-briefcase';
                    title = Unidata.i18n.t('model>customFunction');
                    break;
            }

            return '<span title="' + title + '" class="fa ' + faType + '"></span> ';
        }
    },

    findOutputPortByName: function (name) {
        return this.outputPorts().findRecord('name', name, 0, false, false, true);
    }
});
