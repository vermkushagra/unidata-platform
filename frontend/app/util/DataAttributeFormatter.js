/**
 * Утилитный класс для генерации описаний типов атрибутов
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.util.DataAttributeFormatter', {
    singleton: true,

    /**
     * Построить название сущности
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Справочник/реестр
     * @param dataRecord {Unidata.model.data.Record} Запись данных
     * @param [externalDirty] - внешний признак "грязной" записи
     * @param displayAttributes {String[]} Массив имен отображаемых атрибутов
     * @param useAttributeNameForDisplay {Boolean} Конкатенировать название атрибута к значению
     * @returns {string} Заголовок
     */
    buildEntityTitleFromDataRecord: function (metaRecord, dataRecord, externalDirty, displayAttributes, useAttributeNameForDisplay) {
        var title = '',
            values,
            delimiter = ' | ',
            dirty,
            DIRTY_PREFIX = this.getDirtyPrefix();

        externalDirty = externalDirty === undefined ? false : externalDirty;

        values = this.getMainDisplayableValues(metaRecord, dataRecord, displayAttributes, useAttributeNameForDisplay);

        // не забываем предотвращение XSS
        values = Ext.Array.htmlEncode(values);

        if (useAttributeNameForDisplay) {
            delimiter = ' ';
        } else {
            delimiter = ' | ';
        }

        if (values.length > 0) {
            title = values.join(delimiter);
        }

        // apply dirty symbol
        if (dataRecord && Ext.isFunction(dataRecord.checkDirty)) {
            dirty = dataRecord.checkDirty();
        }

        if (dirty || externalDirty) {
            title = DIRTY_PREFIX + title;
        }

        return title;
    },

    getDirtyPrefix: function () {
        return '<span class="un-dirty-symbol">* </span>';
    },

    /**
     * Построить название сущности
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Справочник/реестр
     * @param searchHit {Unidata.model.search.SearchHit} Результат поиска
     * @param displayAttributes {String[]} Массив имен отображаемых атрибутов
     * @param useAttributeNameForDisplay {Boolean} Конкатенировать название атрибута к значению
     * @returns {string} Заголовок
     */
    buildEntityTitleFromSearchHit: function (metaRecord, searchHit, displayAttributes, useAttributeNameForDisplay) {
        // TODO: apply date, timestamp, time formatter
        var paths,
            UPathMetaUtil = Unidata.util.UPathMeta,
            preview      = searchHit.getAssociatedData()['preview'],
            parseFormats = {
                Date: 'Y-m-d',
                Timestamp: 'Y-m-d\\TH:i:s.uP',
                Time: '\\TH:i:s'
            },
            delimiter,
            values;

        paths = UPathMetaUtil.buildAttributePaths(metaRecord, [{
            fn: Ext.bind(
                this.displayableAttributesFilter,
                this,
                [displayAttributes],
                true
            )
        }]);
        preview = preview.filter(function (item) {
            return Ext.Array.contains(paths, item.field);
        });

        function mapItemToFormatItem (metaRecord, item) {
            var value = item.value,
                metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.field);

            item.displayName = metaAttribute.get('displayName');
            item.value = Unidata.util.DataAttributeFormatter.formatValueByAttribute(metaAttribute, value, parseFormats);

            return item;
        }

        preview = preview.map(mapItemToFormatItem.bind(this, metaRecord));
        values = Ext.Array.map(preview, function (item) {
            var value = item.value;

            if (useAttributeNameForDisplay) {
                value = Ext.String.format('{0}: {1}', item.displayName, value);
            }

            return value;
        });

        if (useAttributeNameForDisplay) {
            delimiter = ' ';
        } else {
            delimiter = ' | ';
        }

        return values.join(delimiter);
    },

    /**
     * Фильтр для отображаемых атрибутов
     *
     * @param attribute
     * @param {string[]} [displayAttributes] - атрибуты для отображения
     * @returns {boolean}
     */
    displayableAttributesFilter: function (attribute, displayAttributes) {
        var mainDisplayable = attribute.get('mainDisplayable'),
            attributeName = attribute.get('name');

        if (!Ext.isEmpty(displayAttributes)) {
            return (displayAttributes.indexOf(attributeName) !== -1);
        }

        return mainDisplayable;
    },

    /**
     * Получить массив главных отображаемых значений
     *
     * @param metaRecord
     * @param dataRecord
     * @param {string[]} [displayAttributes] - атрибуты для отображения
     * @returns {Array}
     */
    getMainDisplayableValues: function (metaRecord, dataRecord, displayAttributes, useAttributeNameForDisplay) {
        var values,
            UPathMetaUtil = Unidata.util.UPathMeta,
            paths;

        paths = UPathMetaUtil.buildAttributePaths(metaRecord, [{
            fn: Ext.bind(
                this.displayableAttributesFilter,
                this,
                [displayAttributes],
                true
            )
        }]);

        values = Ext.Array.map(paths, this.getMainDisplayableValue.bind(this, metaRecord, dataRecord, useAttributeNameForDisplay), this);
        values = Ext.Array.filter(values, function (value) {
            return !Ext.isEmpty(value);
        });

        return values;
    },

    getMainDisplayableValue: function (metaRecord, dataRecord, useAttributeNameForDisplay, path) {
        var value = null,
            displayValue = null,
            values = [],
            dataAttribute,
            metaAttribute,
            displayName;

        dataAttribute = Unidata.util.UPathData.findFirstAttributeByPath(dataRecord, path);
        metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, path);
        displayName = metaAttribute.get('displayName');

        if (dataAttribute) {
            value = dataAttribute.get('value');
            displayValue = dataAttribute.get('displayValue');
        }

        // если у атрибута указано displayValue, то его нужно выводить вместо value
        if (metaAttribute && !Ext.isEmpty(displayValue)) {
            value = displayValue;
        }

        value = Unidata.util.DataAttributeFormatter.formatValueByAttribute(metaAttribute, value);

        if (useAttributeNameForDisplay) {
            value = Ext.String.format('{0}: {1}', displayName, value);
        }

        values.push(value);

        return value;
    },

    /**
     * Отформатировать значение в соответствии с моделью атрибута (в частности, типа)
     *
     * @param metaAttribute Мета-модель атрибута
     * @param value Значение
     * @param parseFormats Форматы парсинга входного значения
     * @returns {*}
     */
    formatValueByAttribute: function (metaAttribute, value, parseFormats) {
        var typeValue,
            tempValue,
            dateFormat,
            dateParseFormat;

        parseFormats = parseFormats || {};

        if (metaAttribute && value !== null && value !== undefined) {
            typeValue = metaAttribute.get('typeValue');

            if (typeValue === 'Date' || typeValue === 'Timestamp' || typeValue === 'Time') {
                switch (typeValue) {
                    case 'Date':
                        dateParseFormat = parseFormats['Date'] || Unidata.Config.getDateTimeFormatServer();
                        dateFormat      = Unidata.Config.getDateFormat();
                        break;
                    case 'Timestamp':
                        dateParseFormat = parseFormats['Timestamp'] || Unidata.Config.getDateTimeFormatServer();
                        dateFormat      = Unidata.Config.getDateTimeFormat();
                        break;
                    case 'Time':
                        dateParseFormat = parseFormats['Time'] || Unidata.Config.getDateTimeFormatServer();
                        dateFormat      = Unidata.Config.getTimeFormat();
                        break;
                }

                if (Ext.isString(value)) {
                    tempValue = Ext.Date.parse(value, dateParseFormat);
                } else {
                    tempValue = value;
                }

                if (tempValue) {
                    value = Ext.Date.format(tempValue, dateFormat);
                }
            } else if (typeValue === 'Boolean') {
                if (Ext.isString(value)) {
                    value = (value === 'true');
                }
                value = value ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();
            } else if (typeValue === 'Number') {
                if (!Ext.isEmpty(value)) {
                    value = value.toString().replace('.', Ext.util.Format.decimalSeparator);
                }
            }
        }

        return value;
    },

    /**
     * Производит форматирование для массива значений
     *
     * @param metaAttribute
     * @param values
     * @param parseFormats
     * @returns {Array}
     */
    formatValuesByAttribute: function (metaAttribute, values, parseFormats) {
        var formatedValues = [];

        if (!Ext.isArray(values)) {
            return formatedValues;
        }

        Ext.Array.each(values, function (value) {
            formatedValues.push(Unidata.util.DataAttributeFormatter.formatValueByAttribute(metaAttribute, value, parseFormats));
        });

        return formatedValues;
    }
});
