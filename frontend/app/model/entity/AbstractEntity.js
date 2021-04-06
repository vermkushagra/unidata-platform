Ext.define('Unidata.model.entity.AbstractEntity', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'hasData',
            type: 'boolean'
        },
        {
            name: 'groupName',
            type: 'string'
        },
        {
            name: 'type',
            type: 'string',
            persist: false
        },
        {
            name: 'validityPeriod',
            type: 'auto',
            convert: function (value) {
                var dateFormatRead = 'Y-m-d\\TH:i:s.u',
                    newValue = value;

                if (Ext.isObject(value)) {
                    if (Ext.isString(value.start)) {
                        newValue.start = Ext.Date.parse(value.start, dateFormatRead);
                    }

                    if (Ext.isString(value.end)) {
                        newValue.end = Ext.Date.parse(value.end, dateFormatRead);
                    }
                }

                return newValue;
            },
            serialize: function (value) {
                var dateFormatWrite = 'Y-m-d\\TH:i:s.u';

                if (!value || !Ext.isObject(value)) {
                    return value;
                }

                if (Ext.isDate(value.start)) {
                    value.start = Ext.Date.format(value.start, dateFormatWrite);
                }

                if (Ext.isDate(value.end)) {
                    value.end.setHours(23);
                    value.end.setMinutes(59);
                    value.end.setSeconds(59);
                    value.end.setMilliseconds(999);
                    value.end = Ext.Date.format(value.end, dateFormatWrite);
                }

                return value;
            }
        },
        {
            name: 'classifiers'
        },
        {
            name: 'version',
            type: 'string'
        },
        {
            name: 'dashboardVisible',
            type: 'boolean'
        },
        {
            name: 'draft',
            type: 'boolean'
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
        displayName: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            }
        ],
        groupName: [
            {
                type: 'presence',
                message: Unidata.i18n.t('model>groupRequired')
            }
        ]
    },

    getType: function () {
        return this instanceof Unidata.model.entity.Entity ? 'Entity' : 'LookupEntity';
    },

    /**
     * Сформировать metaRecordKey
     *
     * @returns {{entityName: String, entityType: String}}
     */
    getMetaRecordKey: function () {
        var metaRecordKey;

        metaRecordKey = {
            entityName: this.get('name'),
            entityType: this.getType()
        };

        return metaRecordKey;
    },

    statics: {
        getTypeIconCls: function (type) {
            switch (type) {
                case 'Entity':
                    return 'fa-book';
                case 'LookupEntity':
                    return 'fa-file-text';
            }
        },
        createTypeIcon: function (type) {
            return '<i class="fa ' + this.getTypeIconCls(type) + '"></i> ';
        }
    },

    getIdGenerationStrategyType: function () {
        var type,
            idGenerationStrategy = this.getExternalIdGenerationStrategy();

        if (idGenerationStrategy) {
            type = idGenerationStrategy.get('@type');
        }

        type = type || Unidata.model.entity.GenerationStrategy.generationStrategyType.NONE.value;

        return type;
    }
});
