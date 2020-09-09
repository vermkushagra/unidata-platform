/**
 * Утилитный класс для построения конфигураций колонок таблиц
 *
 * @author Sergey Shishigin
 * @date 2016-10-11
 */

Ext.define('Unidata.util.ColumnConfigurator', {
    singleton: true,

    requires: [
        'Unidata.util.ColumnGroupOrderConstant'
    ],

    /**
     * Построить объект "Колонка" на основании атрибута и пути к нему
     *
     * @param attribute
     * @param path
     * @returns {{text: *, attribute: *, dataIndex: *, renderer: render, align: string, flex: number}|*}
     */
    getColumnFromAttribute: function (attribute, path) {
        var text = attribute.get('displayName'),
            columnCfg;

        // имена атрибутов - массивов, отображаются с квадратными скобками
        if (attribute.isArrayDataType()) {
            text = '[' + text + ']';
        }

        columnCfg = {
            text: text,
            attribute: attribute,
            dataIndex: path,
            renderer: this.renderColumn.bind(this),
            flex: 1,
            hideable: false,
            draggable: true,
            resizable: true,
            sortable: false
        };

        return columnCfg;
    },

    buildAttributeGroupColumnCfg: function (groupOrder) {
        var columns = [],
            ColumnGroupOrderConstant = Unidata.util.ColumnGroupOrderConstant,
            baseColumnCfg;

        baseColumnCfg = {
            resizable: false,
            draggable: false,
            hideable: false,
            sortable: false,
            cls: 'un-column-group-header',
            columns: []
        };

        switch (groupOrder) {
            case ColumnGroupOrderConstant.CLASSIFIER_ATTRS_FIRST:
                columns.push(Ext.apply(Ext.clone(baseColumnCfg), {
                    text: Unidata.i18n.t('search>query.classifierAttributes')
                }));

                columns.push(Ext.apply(Ext.clone(baseColumnCfg), {
                    text: Unidata.i18n.t('glossary:entityOrLookupEntityAttributes')
                }));
                break;
            case ColumnGroupOrderConstant.CLASSIFIER_ATTRS_LAST:
                columns.push(Ext.apply(Ext.clone(baseColumnCfg), {
                    text: Unidata.i18n.t('glossary:entityOrLookupEntityAttributes')
                }));

                columns.push(Ext.apply(Ext.clone(baseColumnCfg), {
                    text: Unidata.i18n.t('search>query.classifierAttributes')
                }));
                break;
        }

        return columns;
    },

    renderColumn: function (value, metaData, searchHit) {
        var preview = searchHit.preview(),
            status = searchHit.get('status'),
            value,
            values,
            showCount = false,
            attribute = metaData.column.attribute,
            parseFormats;

        parseFormats = {
            Date: 'Y-m-d',
            Timestamp: 'Y-m-d\\TH:i:s.uP',
            Time: '\\TH:i:s'
        };

        if (preview.count() > 0 && status) {
            value = searchHit.mapToObject()[metaData.column.dataIndex];

            if (attribute.isArrayDataType()) {
                values = searchHit.mapToObjectValues()[metaData.column.dataIndex];

                // если занчений больше одного, отображаем их количество
                if (values && values.length > 1) {
                    showCount = true;
                }
            }

            if (showCount) {
                value = '[' + values.length + ']';
            } else {
                value = Unidata.util.DataAttributeFormatter.formatValueByAttribute(attribute, value, parseFormats);
            }

        } else {
            // если нет значений, то выводим спец.текст в первой колонке
            if (metaData.columnIndex === 0) {
                value = Unidata.i18n.t('util>noDataToday');
            } else {
                value = '—';
            }
        }

        return value;
    },

    /**
     * Построить колонки на основании путей к атрибутам (и метаинформации)
     *
     * @param paths
     * @param meta
     * @returns {Array}
     */
    buildColumnsByAttributesPaths: function (paths, meta) {
        var columns = [];

        // TODO: use Ext.Array.map instead
        paths.forEach(function (path) {
            var columnCfg,
                attr;

            attr = Unidata.util.UPathMeta.findAttributeByPath(meta, path);
            columnCfg = Unidata.util.ColumnConfigurator.getColumnFromAttribute(attr, path);

            columns.push(columnCfg);
        });

        return columns;
    },

    /**
     * Построить колонки на основании atributeMap
     *
     * @param attributeMap (path -> attribute)
     * @returns {Array}
     */
    buildColumnsByAttributeMap: function (attributeMap, groupOrder) {
        var columns,
            ColumnGroupOrderConstant = Unidata.util.ColumnGroupOrderConstant;

        groupOrder = groupOrder || ColumnGroupOrderConstant.NONE;

        columns = this.buildAttributeGroupColumnCfg(groupOrder);

        Ext.Object.each(attributeMap, function (path, attribute) {
            var ColumnConfiguratorUtil = Unidata.util.ColumnConfigurator,
                columnCfg,
                columnIndex;

            columnCfg = ColumnConfiguratorUtil.getColumnFromAttribute(attribute, path);

            switch (groupOrder) {
                case ColumnGroupOrderConstant.NONE:
                    columns.push(columnCfg);
                    break;
                case ColumnGroupOrderConstant.CLASSIFIER_ATTRS_FIRST:
                    columnIndex = attribute.isClassifierNodeAttribute() ? 0 : 1;
                    columns[columnIndex].columns.unshift(columnCfg);
                    break;
                case ColumnGroupOrderConstant.CLASSIFIER_ATTRS_LAST:
                    columnIndex = attribute.isClassifierNodeAttribute() ? 1 : 0;
                    columns[columnIndex].columns.unshift(columnCfg);
                    break;
            }
        });

        return columns;
    }
});
