Ext.define('Unidata.model.dataquality.DqRule', {
    extend: 'Unidata.model.Base',

    idProperty: 'id',
    identifier: 'uuid',

    fields: [
        {
            name: 'id',
            type: 'string',
            required: true,
            unique: true
        },
        {
            name: 'name',
            type: 'string',
            required: true,
            unique: true
        },
        {
            name: 'description',
            type: 'string',
            required: true
        },
        {
            name: 'order',
            type: 'int',
            required: true
        },
        {
            name: 'complexAttributeName',
            unique: true
        },
        {
            name: 'cleanseFunctionName',
            unique: true
        },
        {
            name: 'isValidation',
            type: 'boolean'
        },
        {
            name: 'isEnrichment',
            type: 'boolean'
        },
        {
            name: 'special',
            type: 'boolean'
        },
        {
            name: 'applicable',
            defaultValue: []
        }
        //TODO: dqType is absent. Why?
    ],

    hasOne: [
        {
            name: 'origins',
            model: 'dataquality.DqOrigins'
        },
        {
            name: 'raise',
            model: 'dataquality.DqRaise'
        },
        {
            name: 'enrich',
            model: 'dataquality.DqEnrich'
        },
        {
            name: 'cleanseFunction',
            model: 'cleansefunction.CleanseFunction',
            persist: false
        }

    ],

    hasMany: [
        {
            name: 'inputs',
            model: 'dataquality.Input'
        },
        {
            name: 'outputs',
            model: 'dataquality.Output'
        }
    ],

    validators: {
        name: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            },
            {
                type: 'latinalphanumber'
            }
        ],
        cleanseFunctionName: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>shouldSelectFunction')
            }
        ]
    },

    /**
     * Возвращает true, если атрибут использует в правилах качетсва input/output порты
     * @param attributePath
     */
    isAttributeUsed: function (attributePath, skipSpecial) {
        var result = false;

        if (this.get('special') && skipSpecial) {
            return result;
        }

        function isAttributeUsedInPort (port) {
            var portAttributeName = port.get('attributeName');

            return !Ext.isEmpty(portAttributeName) && portAttributeName === attributePath;
        }

        result = Ext.Array.some(this.inputs().getRange(), isAttributeUsedInPort) ||
            Ext.Array.some(this.outputs().getRange(), isAttributeUsedInPort);

        return result;
    },

    /**
     * Возвращает true, если атрибут использует в правилах качетсва input/output порты (учитывается путь)
     *
     * Пример: правило качество использует атрибут attr1.complex.attr2
     *
     * Тогда комплексный атрибут attr1.complex - тоже используется
     *
     * @param attributePath
     */
    isAttributeUsedByPath: function (attributePath, skipSpecial) {
        var result = false;

        if (this.get('special') && skipSpecial) {
            return result;
        }

        function isAttributeUsedInPort (port) {
            var portAttributeName  = port.get('attributeName') || '',
                splitAttributeName = portAttributeName.split('.'),
                attributesName     = [];

            Ext.Array.each(splitAttributeName, function (partName, index) {
                var name = splitAttributeName.slice(0, index + 1);

                name = name.join('.');

                attributesName.push(name);
            });

            result = Ext.Array.some(attributesName, function (name) {
                return !Ext.isEmpty(name) && name === attributePath;
            });

            return result;
        }

        result = Ext.Array.some(this.inputs().getRange(), isAttributeUsedInPort) ||
            Ext.Array.some(this.outputs().getRange(), isAttributeUsedInPort);

        return result;
    }
});
