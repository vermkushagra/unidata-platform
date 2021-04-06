/**
 * Утилитный класс построения системных атрибутов
 *
 * @author Sergey Shishigin
 * @date 2016-03-29
 */

Ext.define('Unidata.view.util.AbstractSystemAttributeEntity', {
    /**
     * Построение параметров для создания system attribute entity
     *
     * @param dataRecord
     * @returns {Object}
     */
    buildSystemEntityParams: function (dataRecord) {
        var systemAttributeEntity = {},
            metaAttrsCfg,
            dataAttrsCfg;

        if (!Ext.isFunction(this.buildMetaAttributesCfg)) {
            Ext.Error.raise('Method buildMetaAttributesCfg is not defined');
        }

        if (!Ext.isFunction(this.buildDataAttributesCfg)) {
            Ext.Error.raise('Method buildDataAttributesCfg is not defined');
        }

        metaAttrsCfg = this.buildMetaAttributesCfg();
        dataAttrsCfg = this.buildDataAttributesCfg(dataRecord);

        systemAttributeEntity.metaRecord = this.buildMetaRecord(metaAttrsCfg);
        systemAttributeEntity.dataRecord = this.buildDataRecord(dataAttrsCfg);

        return systemAttributeEntity;
    },

    /**
     * Построение metaRecord
     *
     * @private
     * @param attributeCfg {Object}
     * @returns {Unidata.model.entity.Entity}
     */
    buildMetaRecord: function (attributeCfg) {
        var metaRecord,
            simpleAttributes,
            complexAttributes,
            complexAttribute,
            nestedEntity,
            nestedSimpleAttributes;

        if (!Ext.isObject(attributeCfg)) {
            return null;
        }

        metaRecord = Ext.create('Unidata.model.entity.Entity', {
            name: 'systemAttributeEntity',
            displayName: Unidata.i18n.t('glossary:systemAttributes')
        });

        simpleAttributes = metaRecord.simpleAttributes();
        complexAttributes = metaRecord.complexAttributes();

        if (Ext.isArray(attributeCfg.simpleAttributes)) {
            Ext.Array.forEach(attributeCfg.simpleAttributes, this.addMetaSimpleAttribute.bind(this, simpleAttributes), this);
        }

        complexAttribute = Ext.create('Unidata.model.attribute.ComplexAttribute', {
            name: 'systemAttributeNestedEntity',
            displayName: Unidata.i18n.t('glossary:systemAttributes'),
            order: 2,
            minCount: 1,
            maxCount: 1
        });

        nestedEntity = Ext.create('Unidata.model.entity.NestedEntity', {
            name: 'systemAttributeNestedEntity',
            displayName: Unidata.i18n.t('glossary:systemAttributes')
        });

        nestedSimpleAttributes = nestedEntity.simpleAttributes();

        if (Ext.isArray(attributeCfg.nestedSimpleAttributes)) {
            Ext.Array.forEach(attributeCfg.nestedSimpleAttributes, this.addMetaSimpleAttribute.bind(this, nestedSimpleAttributes), this);
        }

        complexAttribute.setNestedEntity(nestedEntity);

        complexAttributes.add(complexAttribute);

        return metaRecord;
    },

    /**
     * Построение dataRecord для панели системных атрибутов
     *
     * @private
     * @param attributeCfg {Object}
     * @returns {Unidata.model.data.Record}
     */
    buildDataRecord: function (attributeCfg) {
        var dataRecord,
            simpleAttributes,
            complexAttributes,
            complexAttribute,
            nestedRecords,
            nestedRecord,
            nestedSimpleAttributes;

        dataRecord = Ext.create('Unidata.model.data.Record', {
            etalonId: 0,
            dqErrors: [],
            status: 'ACTIVE',
            approval: 'APPROVED',
            entityName: 'systemAttributeEntity',
            duplicateIds: [],
            validFrom: null,
            validTo: null
        });

        simpleAttributes = dataRecord.simpleAttributes();
        complexAttributes = dataRecord.complexAttributes();

        if (Ext.isArray(attributeCfg.simpleAttributes)) {
            Ext.Array.forEach(attributeCfg.simpleAttributes, this.addDataSimpleAttribute.bind(this, simpleAttributes), this);
        }

        complexAttribute = Ext.create('Unidata.model.data.ComplexAttribute', {
            name: 'systemAttributeNestedEntity'
        });

        nestedRecords = complexAttribute.nestedRecords();
        nestedRecord = Ext.create('Unidata.model.data.NestedRecord', {});
        nestedSimpleAttributes = nestedRecord.simpleAttributes();

        if (Ext.isArray(attributeCfg.nestedSimpleAttributes)) {
            Ext.Array.forEach(attributeCfg.nestedSimpleAttributes, this.addDataSimpleAttribute.bind(this, nestedSimpleAttributes), this);
        }

        nestedRecords.add(nestedRecord);
        complexAttributes.add(complexAttribute);

        return dataRecord;
    },

    /**
     *
     * @private
     * @param cfg {Object}
     * @returns {Object}
     */
    buildMetaAttributeConfig: function (cfg) {
        var name = cfg.name,
            customCfg;

        switch (name) {
            case 'sourceSystem':
                customCfg = {
                    name: 'sourceSystem',
                    displayName: Unidata.i18n.t('glossary:dataSource'),
                    simpleDataType: 'String'
                };
                break;
            case 'timeInterval':
                customCfg = {
                    name: 'timeInterval',
                    displayName: Unidata.i18n.t('glossary:timeintervals'),
                    simpleDataType: 'String'
                };
                break;
            case 'revision':
                customCfg = {
                    name: 'revision',
                    displayName: Unidata.i18n.t('glossary:revision'),
                    simpleDataType: 'String'
                };
                break;
            case 'created':
                customCfg = {
                    name: 'created',
                    displayName: Unidata.i18n.t('common:created'),
                    simpleDataType: 'String'
                };
                break;
            case 'updated':
                customCfg = {
                    name: 'updated',
                    displayName: Unidata.i18n.t('common:updated'),
                    simpleDataType: 'String'
                };
                break;
            case 'status':
                customCfg = {
                    name: 'status',
                    displayName: Unidata.i18n.t('glossary:status'),
                    simpleDataType: 'String'
                };
                break;
            case 'originId':
                customCfg = {
                    name: 'originId',
                    displayName: 'Origin Id',
                    simpleDataType: 'String'
                };
                break;
            case 'etalonId':
                customCfg = {
                    name: 'etalonId',
                    displayName: Unidata.i18n.t('util>recordId'),
                    simpleDataType: 'String'
                };
                break;
            case 'externalId':
                customCfg = {
                    name: 'externalId',
                    displayName: 'External Id',
                    simpleDataType: 'String'
                };
                break;
            default:
        }

        cfg = Ext.apply(cfg, customCfg);

        return cfg;
    },

    /**
     *
     * @private
     * @param cfg {Object}
     * @returns {Object}
     */
    buildDataAttributeConfig: function (cfg) {
        cfg = Ext.apply(cfg, {
            type: 'String'
        });

        return cfg;
    },

    /**
     *
     * @private
     * @param collection
     * @param simpleAttributeCfg
     */
    addMetaSimpleAttribute: function (collection, simpleAttributeCfg) {
        collection.add(this.buildMetaAttributeConfig(simpleAttributeCfg));
    },

    /**
     *
     * @private
     * @param collection
     * @param simpleAttributeCfg
     */
    addDataSimpleAttribute: function (collection, simpleAttributeCfg) {
        collection.add(this.buildDataAttributeConfig(simpleAttributeCfg));
    },

    /**
     * @protected
     * @param date {Ext.Date}
     * @returns {*}
     */
    validDateConvert: function (date) {
        var dateFormat = Unidata.Config.getDateFormat();

        return Ext.Date.format(date, dateFormat);
    },

    /**
     * @protected
     * @param date {Ext.Date}
     * @returns {*}
     */
    createUpdateDateConvert: function (date) {
        var dateTimeFormat = Unidata.Config.getDateTimeFormat();

        return Ext.Date.format(date, dateTimeFormat);
    },

    /**
     * @protected
     * @param dataRecord
     * @returns {string|*}
     */
    buildTimeIntervalValue: function  (dataRecord) {
        var validFrom = dataRecord.get('validFrom'),
            validTo   = dataRecord.get('validTo'),
            minDateSymbol = Unidata.Config.getMinDateSymbol(),
            maxDateSymbol = Unidata.Config.getMaxDateSymbol(),
            value;

        if (!validFrom || !validTo) {
            return null;
        }

        validFrom = validFrom ? this.validDateConvert(validFrom) : minDateSymbol;
        validTo   = validTo ? this.validDateConvert(validTo) : maxDateSymbol;

        value = validFrom + ' - ' + validTo;

        return value;
    },

    /**
     * @public
     * @param eventDate {Ext.Date}
     * @param eventByWhom {String}
     * @returns {*}
     */
    buildDateValue: function (eventDate, eventByWhom) {
        var value;

        eventDate = this.createUpdateDateConvert(eventDate);

        if (eventDate) {
            value = Ext.String.format('{0} ({1})', eventDate, eventByWhom);
        }

        return value;
    },

    /**
     * @protected
     * @param status {String}
     * @returns {*}
     */
    buildStatusValue: function (status) {
        var translations,
            statusTranslation;

        if (!status) {
            return '';
        }

        translations = {
            ACTIVE: Unidata.i18n.t('util>active'),
            INACTIVE: Unidata.i18n.t('util>inactive'),
            MERGED: Unidata.i18n.t('util>merged')
        };

        statusTranslation = translations[status];
        statusTranslation = statusTranslation ? statusTranslation : status;

        return statusTranslation;
    }
});
