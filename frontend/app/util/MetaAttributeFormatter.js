/**
 * Утилитный класс для генерации описаний типов атрибутов
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.util.MetaAttributeFormatter', {
    singleton: true,

    /**
     * Get type value for metamodel
     *
     * @param meta              metamodel
     * @param lookupEntities    lookupEntities store
     * @param enumerations      enumerations store
     * @returns {*}
     */
    getTypeValue: function (meta, lookupEntities, enumerations, measurementValues) {
        var typeValue = meta.get('typeValue'),
            valueId,
            measurementValue,
            typeCategory = meta.get('typeCategory');

        switch (typeCategory) {
            case 'simpleDataType':
            case 'arrayDataType':
                typeValue = this.getSimpleDataDisplayName(typeValue);

                if (meta instanceof Unidata.model.attribute.ArrayAttribute) {
                    typeValue += '[]';
                }
                valueId = meta.get('valueId');
                // информации о измеряемой величине, если она используется
                if (measurementValues && valueId) {
                    measurementValue = this.getDisplayName(measurementValues, valueId, 'id', 'name');

                    if (measurementValue) {
                        typeValue = typeValue + ': ' + measurementValue;
                    }
                }

                typeValue = typeValue.toLowerCase();

                break;
            case 'lookupEntityType':
                if (lookupEntities) {
                    typeValue = this.getDisplayName(lookupEntities, typeValue);

                    if (meta instanceof Unidata.model.attribute.ArrayAttribute) {
                        typeValue += '[]';
                    }
                }
                break;
            case 'enumDataType':
                if (enumerations) {
                    typeValue = this.getDisplayName(enumerations, typeValue);
                }
                break;
        }

        return typeValue;
    },

    /**
     * Get 'display name' of store item by 'name'
     *
     * @param  store
     * @param name
     * @param nameField
     * @param displayNameField
     * @returns {*}
     */
    getDisplayName: function (store, name, nameField, displayNameField) {
        var index,
            displayName = name;

        nameField = nameField || 'name';
        displayNameField = displayNameField || 'displayName';

        index = store.findExact(nameField, name);

        if (index >= 0) {
            displayName = store.getAt(index).get(displayNameField);
            displayName = displayName ? displayName : name;
        }

        return displayName;
    },

    getSimpleDataDisplayName: function (name) {
        var simpleDataTypes = Unidata.Constants.getSimpleDataTypes(),
            found,
            displayName = name;

        found = Ext.Array.findBy(simpleDataTypes, function (simpleDataType) {
            return simpleDataType.name === name;
        });

        if (found) {
            displayName = found.displayName;
            displayName = displayName ? displayName : name;
        }

        return displayName;
    },

    buildTypeDisplayText: function (meta, lookupEntities, enumerations, measurementValues) {
        var typeCategory = meta.get('typeCategory'),
            typeValue = this.getTypeValue(meta, lookupEntities, enumerations, measurementValues),
            dataTypePrefixes = {
                'arrayDataType': '',
                'simpleDataType': '',
                'enumDataType': Unidata.i18n.t('util>enum') + ': ',
                'lookupEntityType': Unidata.i18n.t('util>lookupEntity') + ': ',
                'linkDataType': Unidata.i18n.t('util>link')
            },
            dataTypePostfixes = {
                'arrayDataType': '',
                'simpleDataType': '',
                'enumDataType': '',
                'lookupEntityType': '',
                'linkDataType': ''
            },
            tpl = dataTypePrefixes[typeCategory] + '{0}' + dataTypePostfixes[typeCategory];

        return typeCategory && typeValue ? Ext.String.format(tpl, typeValue) : '';
    },

    /**
     * Получить массив имен гл. отображаемых атрибутов
     * @param metaRecord
     * @returns {String[]}
     */
    getMainDisplayableAttributeDisplayNames: function (metaRecord) {
        var UPathMetaUtil = Unidata.util.UPathMeta,
            paths,
            displayNames,
            metaAttributes;

        paths = UPathMetaUtil.buildSimpleAttributePaths(metaRecord, [{property: 'mainDisplayable', value: true}]);

        metaAttributes = paths.map(function (path) {
            var metaAttribute;

            metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, path);

            return metaAttribute;
        });

        // сортируем в порядке определения атрибутов на метамодели связи
        metaAttributes = Ext.Array.sort(metaAttributes, function (a, b) {
            return a.order - b.order;
        });

        displayNames = metaAttributes.map(function (metaAttribute) {
            return metaAttribute.get('displayName');
        });

        return displayNames;
    }
});
