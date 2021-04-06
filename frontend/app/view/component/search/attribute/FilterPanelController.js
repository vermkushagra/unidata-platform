/**
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.FilterPanelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.attribute.filterpanel',

    searchableAttributesCfg: null,

    init: function () {
        this.searchableAttributesCfg = [];
        this.callParent(arguments);
    },

    destroy: function () {
        this.searchableAttributesCfg = null;
        this.callParent(arguments);
    },

    initSearchableAttributesStore: function (entityRecord) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            searchableAttributesStore = viewModel.getStore('searchableAttributes'),
            searchableAttributes = this.getSearchableAttributes(entityRecord),
            storeData = [];

        this.searchableAttributesCfg = searchableAttributes;

        Ext.Array.each(searchableAttributes, function (attr) {
            storeData.push(attr.attribute);
        });

        searchableAttributesStore.on('load', view.updateAddFilterTabletButtonVisibility, view, {single: true});

        searchableAttributesStore.loadData(storeData);
    },

    onAddFilterTabletClick: function (button) {
        var view = this.getView();

        view.expand();
        view.searchableAttributesField.showBy(button, 'r-r', [5, 0]);
    },

    onSearchableAttributesFieldSelect: function (field, attributes) {
        Ext.Array.each(attributes, this.getTabletGroup, this);
        field.hide();
    },

    /**
     * Вставляет элемент фильтра в нужную группу
     * @param attribute
     */
    insertTabletGroupItem: function (attribute) {
        var tabletGroup = this.getTabletGroup(attribute),
            tabletItemCfg,
            tablet;

        // находим нужный конфиг
        Ext.Array.each(this.searchableAttributesCfg, function (cfg) {
            if (cfg.attribute === attribute) {
                tabletItemCfg = cfg;

                return false;
            }
        });

        tablet = tabletGroup.add(this.getTabletItemByAttribute(tabletItemCfg));

        tablet.on('change', this.onChangeTablet, this);

        return tablet;
    },

    findTabletGroupByAttribute: function (attribute) {
        var view = this.getView(),
            searchFilterItemsCollection = view.getSearchFilterItemsCollection(),
            findFn = Ext.bind(this.findTabletGroup, this, [attribute], true),
            tabletGroup = searchFilterItemsCollection.findBy(findFn);

        return tabletGroup;
    },

    createTabletGroup: function (attribute, createItem) {
        var view = this.getView(),
            tabletGroup;

        createItem = Ext.isBoolean(createItem) ? createItem : true;

        tabletGroup = Ext.create('Unidata.view.component.search.attribute.tablet.TabletGroup', {
            attribute: attribute
        });

        tabletGroup.on('beforedestroy', this.onTabletGroupBeforeDestroy, this);
        tabletGroup.on('destroy', this.onChangeTablet, this);
        tabletGroup.on('addfilteritem', this.onAddFilterItem, this);
        tabletGroup.on('moveup', this.moveTabletGroupUp, this);
        tabletGroup.on('movedown', this.moveTabletGroupDown, this);
        tabletGroup.on('change', this.onChangeTablet, this);

        view.insert(0, tabletGroup);

        if (createItem) {
            // сразу вставляем пустой элемент
            this.insertTabletGroupItem(attribute);
        }

        return tabletGroup;
    },

    /**
     * Возвращает tabletGroup на основе attribute, создаёт новый, если нужно
     *
     * @param attribute
     * @returns {Unidata.view.component.search.attribute.tablet.TabletGroup}
     */
    getTabletGroup: function (attribute) {
        var tabletGroup;

        tabletGroup = this.findTabletGroupByAttribute(attribute);

        if (!tabletGroup) {
            this.createTabletGroup(attribute);
        }

        return tabletGroup;
    },

    moveTabletGroup: function (tabletGroup, offset) {
        var view = this.getView(),
            currentIndex = view.items.indexOf(tabletGroup);

        view.insert(currentIndex + offset, tabletGroup);
    },

    moveTabletGroupUp: function (tabletGroup) {
        this.moveTabletGroup(tabletGroup, -1);
    },

    moveTabletGroupDown: function (tabletGroup) {
        this.moveTabletGroup(tabletGroup, 1);
    },

    /**
     * Возвращаем атрибут в список searchableAttributesField
     * @param tabletGroup
     */
    onTabletGroupBeforeDestroy: function (tabletGroup) {
        var view = this.getView(),
            searchableAttributesField = view.searchableAttributesField;

        searchableAttributesField.removeValue(tabletGroup.getAttribute().get('tempId'));
    },

    /**
     * Обработчик для tabletGroup, который добавляет элементы фильтра
     * @param tabletGroup
     */
    onAddFilterItem: function (tabletGroup) {
        this.insertTabletGroupItem(tabletGroup.getAttribute());
    },

    /**
     * Поиск по коллекции
     */
    findTabletGroup: function (tabletGroup, key, attribute) {
        return tabletGroup.getAttribute() === attribute;
    },

    removeSearchParamsPanel: function () {
        var view = this.getView();

        view.removeAll(true);
    },

    getSearchableAttributes: function (entityRecord) {
        var path = [];

        return this.getSearchableAttributeFromComplex(path, entityRecord);
    },

    getSearchableAttributeFromSimple: function (path, nested) {
        var attributes = [],
            me = this;

        nested.simpleAttributes().each(function (attribute) {
            attributes = me.addAbstractAttribute(attributes, attribute, path);
        });
        nested.arrayAttributes().each(function (attribute) {
            attributes = me.addAbstractAttribute(attributes, attribute, path);
        });

        return attributes;
    },

    addAbstractAttribute: function (attributes, attribute, path) {
        var attributePath;

        if (attribute.get('searchable')) {
            attributePath = Ext.Array.merge(path, attribute.get('name')).join('.');

            // цепляем path в атрибут, т.к. протаскивать его везде отдельно - нет смысла
            attribute.set('path', attributePath);

            attributes.push({
                path: attributePath,
                attribute: attribute
            });
        }

        return attributes;
    },

    getSearchableAttributeFromAliasCode: function (path, nested) {
        var attributes = [],
            me = this;

        nested.aliasCodeAttributes().each(function (attribute) {
            attributes = me.addAbstractAttribute(attributes, attribute, path);
        });

        return attributes;
    },

    getSearchableAttributeFromCode: function (path, nested) {
        var attributes = [],
            attribute;

        attribute = nested.getCodeAttribute();

        attributes = this.addAbstractAttribute(attributes, attribute, path);

        return attributes;
    },

    //TODO: refactoring, apply UPath lib
    getSearchableAttributeFromComplex: function (path, nested) {
        var me = this,
            attributes = [],
            clsWSimple,
            clsWComplex,
            clsWCode,
            clsWAliasCode,
            nestedClassName = Ext.getClassName(nested);

        clsWSimple  = ['Unidata.model.entity.Entity', 'Unidata.model.entity.LookupEntity', 'Unidata.model.entity.NestedEntity'];
        clsWComplex = ['Unidata.model.entity.Entity', 'Unidata.model.entity.NestedEntity'];
        clsWCode    = ['Unidata.model.entity.LookupEntity'];
        clsWAliasCode    = ['Unidata.model.entity.LookupEntity'];

        if (Ext.Array.contains(clsWCode, nestedClassName)) {
            attributes = Ext.Array.merge(attributes, this.getSearchableAttributeFromCode(path, nested));
        }

        if (Ext.Array.contains(clsWAliasCode, nestedClassName)) {
            attributes = Ext.Array.merge(attributes, this.getSearchableAttributeFromAliasCode(path, nested));
        }

        if (Ext.Array.contains(clsWSimple, nestedClassName)) {
            attributes = Ext.Array.merge(attributes, this.getSearchableAttributeFromSimple(path, nested));
        }

        if (Ext.Array.contains(clsWComplex, nestedClassName)) {
            nested.complexAttributes().each(function (attribute) {
                var pathTmp = Ext.Array.merge(path, attribute.get('name'));

                attributes = Ext.Array.merge(attributes, me.getSearchableAttributeFromComplex(pathTmp, attribute.getNestedEntity()));
            });
        }

        return attributes;
    },

    getTabletItemByAttribute: function (attrInfo) {
        var tabletDataType,
            tablet,
            view = this.getView(),
            attribute = attrInfo.attribute,
            path = attrInfo.path;

        if (attribute.isSimpleDataType()) {
            tabletDataType = attribute.get('simpleDataType');
        } else if (attribute.isArrayDataType()) {
            switch (attribute.get('typeCategory')) {
                case 'arrayDataType':
                    tabletDataType = attribute.get('arrayDataType');
                    break;
                case 'lookupEntityType':
                    tabletDataType = 'Lookup';
                    break;
                default:
                    Ext.Error.raise('Type of attribute ' + attribute.get('name') + ' is not supported');
                    break;
            }
        } else if (attribute.isLookupEntityType()) {
            tabletDataType = 'Lookup';
        } else if (attribute.isEnumDataType()) {
            tabletDataType = 'Enumeration';
        } else {
            Ext.Error.raise('Type of attribute ' + attribute.get('name') + ' is not supported');
        }

        tablet = {
            xtype: 'component.search.attribute.tablet.tablet',
            tabletDataType: tabletDataType,
            attributeName: attribute.get('name'),
            attributeDisplayName: attribute.get('displayName'),
            path: path,
            attribute: attribute
        };

        return tablet;
    },

    onChangeTablet: function () {
        var view = this.getView();

        if (view.destroying || view.destroyed) {
            return;
        }

        view.fireEvent('change', this);
    }
});
