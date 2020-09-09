Ext.define('Unidata.model.dataquality.DqRule', {
    extend: 'Unidata.model.Base',

    requires: [
        'Unidata.model.KeyValuePair'
    ],

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
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'applicable',
            type: 'auto',
            defaultValue: []
        },
        {
            name: 'active',
            type: 'boolean',
            persist: false,
            calculate: function (data) {
                return Ext.Array.contains(Unidata.model.dataquality.DqRule.DQ_RULE_ACTIVE_RUN_TYPES, data['runType']);
            },
            depends: ['runType']
        },
        {
            name: 'runType',
            type: 'string',
            defaultValue: 'RUN_ON_REQUIRED_PRESENT'
        },
        {
            name: 'executionContextPath',
            type: 'string'
        },
        {
            name: 'executionContext',
            type: 'enum',
            enumList: Unidata.util.DataQuality.executionContextEnumList
        },
        {
            name: 'masterData',
            type: 'boolean',
            persist: false,
            calculate: function (data) {
                return Ext.Array.contains(data.applicable, 'ETALON');
            }
        }
    ],

    hasOne: [
        {
            name: 'origins',
            model: 'dataquality.DqOrigins',
            deepDirty: true
        },
        {
            name: 'raise',
            model: 'dataquality.DqRaise',
            deepDirty: true
        },
        {
            name: 'enrich',
            model: 'dataquality.DqEnrich',
            deepDirty: true
        }
    ],

    hasMany: [
        {
            name: 'inputs',
            model: 'dataquality.Input',
            deepDirty: true
        },
        {
            name: 'outputs',
            model: 'dataquality.Output',
            deepDirty: true
        },
        {
            name: 'customProperties',
            model: 'KeyValuePair'
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
    },

    statics: {
        DQ_RULE_PORT_DATA_TYPES: {
            STRING: 'String',
            DATE: 'Date',
            TIMESTAMP: 'Timestamp',
            TIME: 'Time',
            NUMBER: 'Number',
            INTEGER: 'Integer',
            BOOLEAN: 'Boolean',
            ANY: 'Any',
            RECORD: 'Record'
        },

        DQ_RULE_RUN_TYPES: {
            RUN_ALWAYS: 'RUN_ALWAYS',
            RUN_NEVER: 'RUN_NEVER',
            RUN_ON_REQUIRED_PRESENT: 'RUN_ON_REQUIRED_PRESENT',
            RUN_ON_ALL_PRESENT: 'RUN_ON_ALL_PRESENT'
        },

        DQ_RULE_ACTIVE_RUN_TYPES: [
            'RUN_ON_REQUIRED_PRESENT',
            'RUN_ALWAYS',
            'RUN_ON_ALL_PRESENT'
        ],

        getDataTypeDisplayName: function (dataType) {
            var DqRuleModel = Unidata.model.dataquality.DqRule,
                portTypeDisplayNames,
                displayName;

            portTypeDisplayNames = DqRuleModel.getDataTypeDisplayNames();

            displayName = portTypeDisplayNames[dataType];

            displayName = displayName || Unidata.i18n.t('admin.dq>dataType.unknown');

            return displayName;
        },

        getDataTypeDisplayNames: function () {
            var DqRuleModel = Unidata.model.dataquality.DqRule,
                portTypeDisplayNames = {};

            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.STRING] = Unidata.i18n.t('admin.dq>dataType.string');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.BOOLEAN] = Unidata.i18n.t('admin.dq>dataType.boolean');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.NUMBER] = Unidata.i18n.t('admin.dq>dataType.number');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.INTEGER] = Unidata.i18n.t('admin.dq>dataType.integer');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.DATE] = Unidata.i18n.t('admin.dq>dataType.date');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.TIMESTAMP] = Unidata.i18n.t('admin.dq>dataType.timestamp');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.TIME] = Unidata.i18n.t('admin.dq>dataType.time');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.RECORD]  = Unidata.i18n.t('admin.dq>dataType.record');
            portTypeDisplayNames[DqRuleModel.DQ_RULE_PORT_DATA_TYPES.ANY]     = Unidata.i18n.t('admin.dq>dataType.any');

            return portTypeDisplayNames;
        },

        /**
         * Формирует массив объектов "Простой тип данных"
         *
          * @return {Object[]}
         */
        buildSimpleDataTypeObjectList: function () {
            var DqRuleModel = Unidata.model.dataquality.DqRule,
                DQ_RULE_PORT_DATA_TYPES = DqRuleModel.DQ_RULE_PORT_DATA_TYPES,
                types,
                simpleTypes,
                complexTypes,
                dataTypeDisplayNames,
                objectList;

            dataTypeDisplayNames = this.getDataTypeDisplayNames();
            types      = Ext.Object.getValues(DQ_RULE_PORT_DATA_TYPES);
            complexTypes = [DqRuleModel.DQ_RULE_PORT_DATA_TYPES.ANY, DqRuleModel.DQ_RULE_PORT_DATA_TYPES.RECORD];
            simpleTypes = Ext.Array.difference(types, complexTypes);
            objectList = Ext.Array.map(simpleTypes, function (type) {
                return {name: type, displayName: dataTypeDisplayNames[type]};
            });

            return objectList;
        }
    }
});
