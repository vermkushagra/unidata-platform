/**
 * Класс реализует "плитку" (tablet). Плитка состоит из заголовка и контейнера, который содержит
 * поля для указания фильтров
 *
 * events:
 *        change - событие изменения фильтра в плитке
 *
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.tablet.Tablet', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.search.attribute.tablet.tablet',

    requires: [
        'Unidata.view.component.search.attribute.tablet.String',
        'Unidata.view.component.search.attribute.tablet.Numeric',
        'Unidata.view.component.search.attribute.tablet.Date',
        'Unidata.view.component.search.attribute.tablet.Boolean',
        'Unidata.view.component.search.attribute.tablet.Binary',
        'Unidata.view.component.search.attribute.tablet.Lookup',
        'Unidata.view.component.search.attribute.tablet.Enumeration'
    ],

    config: {
        tabletPrototypeId: null
    },

    tabletDataType: '',
    attributeName: '',
    attributeDisplayName: '',
    path: '',
    attribute: null,
    metaRecord: null,
    value: null, // объект описывающий дефолтные значения для полей таблетки

    tabletItem: null,
    findType: null,
    warningLabel: null,

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    expanded: false,

    cls: 'un-search-attribute-tablet',

    items: [
        {
            xtype: 'container',
            reference: 'left',
            cls: 'un-search-attribute-tablet-left',
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'combobox',
                    ui: 'un-field-default',
                    reference: 'findType',
                    valueField: 'name',
                    displayField: 'displayName',
                    editable: false,
                    flex: 1,
                    store: {
                        fields: [
                            'name',
                            'displayName'
                        ],
                        data: []
                    },
                    value: 'exact',
                    listConfig: {
                        itemTpl: '<tpl switch="name"><tpl case="like"><div style="float: left; width: 80%">{displayName}</div><div style="float: right; vertical-align: center;"><span class="un-search-attribute-tablet-warning-icon icon-warning"></span></div><div style="clear:both"></div><tpl default>{displayName}</tpl>'// jscs:ignore maximumLineLength
                    }
                },
                {
                    xtype: 'container',
                    reference: 'tabletItemContainer'
                }
            ]
        },
        {
            xtype: 'container',
            reference: 'right',
            cls: 'un-search-attribute-tablet-right',
            layout: {
                type: 'vbox',
                pack: 'center'
            },
            items: {
                xtype: 'un.fontbutton',
                reference: 'deleteButton',
                color: 'lightgray',
                iconCls: 'icon-circle-minus'
            }
        }
    ],

    statics: {
        tabletsCounter: {}, // количество таблетов по path
        tablets: new Ext.util.Collection(), // кэшируем все созданные таблеты в коллекцию,
        warnings: {         // предупреждения, которые возникают на таблетках
            LIKE_SLOW_REQUEST: {
                id: 'LIKE_SLOW_REQUEST',
                text: Unidata.i18n.t('search>query.slowQuery')
            }
        }
    },

    initComponent: function () {
        var tabletItemContainer,
            findType,
            item;

        this.callParent(arguments);

        findType = this.lookupReference('findType');
        this.findType = findType;

        tabletItemContainer = this.lookupReference('tabletItemContainer');

        findType.on('change', this.onChangeFindType, this);

        this.initFindTypeStore(this.tabletDataType);

        item = this.factoryTabletItem(this.tabletDataType, this.attributeName, this.attributeDisplayName, this.path);

        if (Ext.Object.isEmpty(item)) {
            this.hide(); //скрываем плитки, для которых пока не реализовано "содержимое"

            return;
        }

        this.tabletItem = tabletItemContainer.add(item);

        this.tabletItem.on('change', this.onTabletItemChange, this);

        if (!this.getTabletPrototypeId()) {
            this.setTabletPrototypeId(this.getId());
        }

        this.lookupReference('deleteButton').on('click', this.destroy, this);
    },

    onAdded: function () {
        var samePathTablets;

        this.callParent(arguments);
        this.updateTabletWarning(samePathTablets);
    },

    onDestroy: function () {
        this.callParent(arguments);
    },

    initFindTypeStore: function (dataType) {
        var data = [],
            itemNull,
            itemExact,
            itemExactAndRange,
            itemNotNull,
            itemNotExact,
            itemNotExactAndRange,
            itemStartWith,
            itemLike,
            itemFuzzy,
            itemMorphologically;

        itemNull = {
            name: 'null',
            displayName: Unidata.i18n.t('search>query.empty')
        };

        itemExactAndRange = {
            name: 'exact',
            displayName: Unidata.i18n.t('search>query.exactAndRangeValue')
        };

        itemExact = {
            name: 'exact',
            displayName: Unidata.i18n.t('search>query.exactValue')
        };

        itemNotNull = {
            name: 'notnull',
            displayName: Unidata.i18n.t('search>query.notEmpty')
        };

        itemNotExactAndRange = {
            name: 'notexact',
            displayName: Unidata.i18n.t('search>query.excludeValueAndRange')
        };

        itemNotExact = {
            name: 'notexact',
            displayName: Unidata.i18n.t('search>query.excludeValue')
        };

        itemStartWith = {
            name: 'startwith',
            displayName: Unidata.i18n.t('search>query.startWith')
        };

        itemLike = {
            name: 'like',
            displayName: Unidata.i18n.t('search>query.contains')
        };

        itemFuzzy = {
            name: 'fuzzy',
            displayName: Unidata.i18n.t('search>query.bySimilarity')
        };

        itemMorphologically = {
            name: 'morphological',
            displayName: Unidata.i18n.t('search>query.byMorphologically')
        };

        switch (dataType) {
            case 'Boolean':
                data = Ext.Array.merge([], itemNull, itemExactAndRange, itemNotNull);
                break;
            case 'Blob':
            case 'Clob':
                data = Ext.Array.merge([], itemNull, itemExactAndRange, itemNotNull, itemStartWith, itemLike);
                break;
            case 'String':
                data = Ext.Array.merge([], itemNull, itemExact, itemNotNull, itemNotExact, itemStartWith, itemLike, itemFuzzy);

                if (this.attribute.get('searchMorphologically')) {
                    data = Ext.Array.merge(data, itemMorphologically);
                }
                break;
            default:
                data = Ext.Array.merge([], itemNull, itemExactAndRange, itemNotNull, itemNotExactAndRange);
                break;
        }

        this.findType.getStore().getProxy().data =  Ext.Array.merge(this.findType.getStore().getProxy().data, data);
        this.findType.getStore().load();
    },

    factoryTabletItem: function (dataType, name, displayName, path) {
        var item = {};

        switch (dataType) {
            case 'String':
                item = {
                    xtype: 'component.search.attribute.tablet.string'
                };
                break;
            case 'Integer':
                item = {
                    xtype: 'component.search.attribute.tablet.numeric',
                    allowDecimals: false
                };
                break;
            case 'Number':
                item = {
                    xtype: 'component.search.attribute.tablet.numeric',
                    decimalPrecision: Unidata.Config.getDecimalPrecision(),
                    allowDecimals: true
                };
                break;
            case 'Boolean':
                item = {
                    xtype: 'component.search.attribute.tablet.boolean'
                };
                break;
            case 'Date':
                item = {
                    xtype: 'component.search.attribute.tablet.date'
                };
                break;
            case 'Timestamp':
                item = {
                    xtype: 'component.search.attribute.tablet.datetime'
                };
                break;
            case 'Time':
                item = {
                    xtype: 'component.search.attribute.tablet.time'
                };
                break;
            case 'Lookup':
                item = {
                    xtype: 'component.search.attribute.tablet.lookup'
                };
                break;
            case 'Blob':
                item = {
                    xtype: 'component.search.attribute.tablet.binary',
                    binary: true
                };
                break;
            case 'Clob':
                item = {
                    xtype: 'component.search.attribute.tablet.binary',
                    binary: false
                };
                break;
            case 'Enumeration':
                item = {
                    xtype: 'component.search.attribute.tablet.enumeration'
                };
                break;
        }

        if (!Ext.Object.isEmpty(item)) {
            item.attributePath = path;
            item.attribute = this.attribute;
        }

        return item;
    },

    /**
     * Валидировать tabletItem
     *
     * Имеет побочный эффект - вывод сообщений в поля сообщений для tabletItem
     * @returns {boolean}
     */
    validate: function () {
        var isValid = true;

        if (this.tabletItem && Ext.isFunction(this.tabletItem.validate)) {
            isValid = this.tabletItem.validate();
        }

        return isValid;
    },

    getFilter: function () {
        var tabletParam = {},
            isNullable  = this.isNullable(),
            isInverted  = this.isInverted(),
            isStartWith = this.isStartWith(),
            isLike      = this.isLike(),
            isFuzzy     = this.isFuzzy(),
            isMorphological = this.isMorphological(),
            options;

        options = {
            isNullable: isNullable,
            isInverted: isInverted,
            isStartWith: isStartWith,
            isLike: isLike,
            isFuzzy: isFuzzy,
            isMorphological: isMorphological
        };

        if (this.tabletItem) {
            tabletParam = this.tabletItem.getFilter(options);
        }

        return tabletParam;
    },

    isEmptyFilter: function () {
        var result = true;

        if (this.tabletItem && this.tabletItem.rendered) {
            result = this.tabletItem.isEmptyFilter();
        }

        if (result) {
            result = this.isEmpty();
        }

        return result;
    },

    onTabletItemChange: function () {
        this.updateTabletWarning();
        this.fireEvent('change', this);
    },

    /**
     * Обновить предупреждение
     */
    updateTabletWarning: function () {
        var allWarnings = this.getWarnings();

        if (allWarnings.length > 0) {
            this.showWarnings(allWarnings);
        } else {
            this.hideWarnings();
        }
    },

    setDisabled: function (disabled) {
        if (this.tabletItem) {
            this.tabletItem.setDisabled(disabled);
        }

        if (this.findType) {
            this.findType.setDisabled(disabled);
        }

        this.callParent(arguments);
    },

    onChangeFindType: function (combobox, value) {
        if (Ext.Array.contains(['null', 'notnull'], value)) {
            this.tabletItem.hide();
        } else {
            this.tabletItem.show();
        }

        this.onTabletItemChange();
    },

    isNullable: function () {
        var value = this.findType.getValue();

        return Ext.Array.contains(['null', 'notnull'], value);
    },

    isInverted: function () {
        var value = this.findType.getValue();

        return Ext.Array.contains(['notexact', 'notnull'], value);
    },

    isStartWith: function () {
        var value = this.findType.getValue();

        return value === 'startwith';
    },

    isLike: function () {
        var value = this.findType.getValue();

        return value === 'like';
    },

    isFuzzy: function () {
        var value = this.findType.getValue();

        return value === 'fuzzy';
    },

    isMorphological: function () {
        var value = this.findType.getValue();

        return value === 'morphological';
    },

    isEmpty: function () {
        return !this.isNullable();
    },

    /**
     * Возвращает массив типов предупреждений для данной таблетки
     *
     * @returns {String[]}
     */
    getWarnings: function () {
        var Tablet   = Unidata.view.component.search.attribute.tablet.Tablet,
            isLike   = this.isLike(),
            warnings = [];

        if (isLike) {
            warnings.push(Tablet.warnings.LIKE_SLOW_REQUEST);
        }

        return warnings;
    },

    /**
     * Создать лейбл предупреждения для группы таблеток
     * @param html
     */
    createWarningLabel: function (html) {
        this.warningLabel = this.lookupReference('tabletItemContainer').add({
            xtype: 'container',
            cls: 'un-search-attribute-tablet-warning',
            html: html,
            reference: 'warningLabel',
            minHeight: 20
        });
    },

    /**
     * Построить html с предупреждениями
     *
     * @param allWarnings {Object[]}
     * @returns String
     */
    buildWarningsHtml: function (allWarnings) {
        var html,
            warningTexts;

        warningTexts = Ext.Array.pluck(allWarnings, 'text');

        if (warningTexts.length > 0) {
            html = '<span class="un-search-attribute-tablet-warning-icon icon-warning"></span>';
            html += warningTexts.join('<br>');
        }

        return html;
    },

    /**
     * Отобразить предупреждения
     *
     * @param allWarnings {Object[]}
     */
    showWarnings: function (allWarnings) {
        var warningLabel = this.warningLabel,
            html;

        html = this.buildWarningsHtml(allWarnings);

        if (!warningLabel) {
            this.createWarningLabel(html);
        } else {
            warningLabel.setHtml(html);
        }
    },

    /**
     * Скрыть предупреждения
     */
    hideWarnings: function () {
        if (this.warningLabel) {
            this.warningLabel.destroy();
            this.warningLabel = null;
        }
    }
});
