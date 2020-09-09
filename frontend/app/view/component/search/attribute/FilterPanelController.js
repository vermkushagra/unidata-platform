/**
 * @author Ivan Marshalkin
 * 2015-09-01
 */

Ext.define('Unidata.view.component.search.attribute.FilterPanelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.attribute.filterpanel',

    TermClass: Unidata.module.search.term.attribute.FormField,

    searchableAttributesCfg: null,
    searchableAttributesStoreDeferred: null,

    init: function () {
        this.searchableAttributesStoreDeferred = new Ext.Deferred();
        this.searchableAttributesCfg = [];
        this.callParent(arguments);

        this.updateSearchableAttributesStoreFilters();
    },

    updateSearchableAttributesStoreFilters: function () {
        var view = this.getView(),
            toEntityDefaultSearchAttributes = view.getToEntityDefaultSearchAttributes(),
            useToEntityDefaultSearchAttributes = view.getUseToEntityDefaultSearchAttributes(),
            viewModel = this.getViewModel(),
            searchableAttributesStore = viewModel.getStore('searchableAttributes');

        searchableAttributesStore.clearFilter(true);

        searchableAttributesStore.addFilter({
            fn: Ext.bind(
                this.searchableAttributesFilter,
                this,
                [useToEntityDefaultSearchAttributes, toEntityDefaultSearchAttributes],
                0
            )
        });

        view.updateAddFilterTabletButtonVisibility();
    },

    searchableAttributesFilter: function (useToEntityDefaultSearchAttributes, toEntityDefaultSearchAttributes, attribute) {
        var name = attribute.get('name'),
            searchable = attribute.get('searchable');

        // для классификаторов пока что все атрибуты считаются поисковыми
        // т.к. признак "поисковый" не кофигурируется для них
        searchable = true;

        if (useToEntityDefaultSearchAttributes) {
            if (Ext.isArray(toEntityDefaultSearchAttributes) && toEntityDefaultSearchAttributes.length) {
                searchable = toEntityDefaultSearchAttributes.indexOf(name) !== -1;
            } else {
                searchable = false;
            }
        }

        return searchable;
    },

    updateToEntityDefaultSearchAttributes: function () {
        this.updateSearchableAttributesStoreFilters();
    },

    updateUseToEntityDefaultSearchAttributes: function () {
        var view = this.getView(),
            toEntityDefaultSearchAttributes = view.getToEntityDefaultSearchAttributes(),
            searchFilterItemsCollection = view.getSearchFilterItemsCollection(),
            viewModel = this.getViewModel(),
            searchableAttributesStore = viewModel.getStore('searchableAttributes');

        this.updateSearchableAttributesStoreFilters();

        if (toEntityDefaultSearchAttributes && toEntityDefaultSearchAttributes.length) {
            Ext.Array.each(searchFilterItemsCollection.getRange(), function (item) {
                if (searchableAttributesStore.findExact('name', item.attributeName) === -1) {
                    item.destroy();
                }
            });
        }
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

        searchableAttributesStore.loadData(storeData);

        view.updateAddFilterTabletButtonVisibility();
        this.searchableAttributesStoreDeferred.resolve(searchableAttributesStore);
    },

    getLoadedSearchableAttributesStore: function () {
        return this.searchableAttributesStoreDeferred.promise;
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
     * @param term
     */
    insertTabletGroupItem: function (attribute, term) {
        var tabletGroup = this.getTabletGroup(attribute, false),
            tabletItemCfg,
            tablet;

        // находим нужный конфиг
        Ext.Array.each(this.searchableAttributesCfg, function (cfg) {
            if (cfg.attribute === attribute) {
                tabletItemCfg = cfg;

                return false;
            }
        });

        tablet = tabletGroup.add(this.getTabletItemByAttribute(tabletItemCfg, term));

        tabletGroup.expand();

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
            searchQuery: view.getSearchQuery(),
            attribute: attribute
        });

        tabletGroup.on('beforedestroy', this.onTabletGroupBeforeDestroy, this);
        tabletGroup.on('destroy', this.onChangeTablet, this);
        tabletGroup.on('addfilteritem', this.onAddFilterItem, this);
        tabletGroup.on('moveup', this.moveTabletGroupUp, this);
        tabletGroup.on('movedown', this.moveTabletGroupDown, this);
        tabletGroup.on('change', this.onChangeTablet, this);

        view.insert(0, tabletGroup);

        Ext.defer(view.expand, 10, view);

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
     * @param createItem
     * @returns {Unidata.view.component.search.attribute.tablet.TabletGroup}
     */
    getTabletGroup: function (attribute, createItem) {
        var tabletGroup;

        tabletGroup = this.findTabletGroupByAttribute(attribute);

        if (!tabletGroup) {
            tabletGroup = this.createTabletGroup(attribute, createItem);
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

        attributePath = Ext.Array.merge(path, attribute.get('name')).join('.');

        // цепляем path в атрибут, т.к. протаскивать его везде отдельно - нет смысла
        attribute.set('path', attributePath);

        attributes.push({
            path: attributePath,
            attribute: attribute
        });

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

    updateSearchQuery: function (searchQuery) {
        var view = this.getView(),
            promises = [];

        view.items.each(function (item) {
            if (item instanceof Unidata.view.component.search.attribute.tablet.TabletGroup) {
                item.destroy();
            }
        });

        searchQuery.getTermsCollection().each(function (term) {
            if (term instanceof this.TermClass) {

                promises.push(this.createTabletFromTerm(term));
            }
        }, this);

        // отображаем ошибку, если каких-то атрибутов нет в метаданных
        Ext.Deferred.all(promises).then(
            function (result) {
                var notFoundAttributes = [];

                Ext.Array.each(result, function (resultItem) {
                    if (!resultItem.success) {
                        notFoundAttributes.push(resultItem.attributePath);
                    }
                });

                if (notFoundAttributes.length) {
                    if (notFoundAttributes.length > 1) {
                        Unidata.showError(Unidata.i18n.t('search>query.error.attributesNotFound', {
                            attributeNames: notFoundAttributes.join(', ')
                        }));
                    } else {
                        Unidata.showError(Unidata.i18n.t('search>query.error.attributeNotFound', {
                            attributeName: notFoundAttributes[0]
                        }));
                    }
                }
            }
        ).done();

    },

    /**
     * Создаёт теблеты на основе терма
     *
     * @param {Unidata.module.search.term.FormField} term
     */
    createTabletFromTerm: function (term) {
        var me = this,
            view = this.getView(),
            attributePath = term.getName(),
            deferred = new Ext.Deferred();

        this.getLoadedSearchableAttributesStore()
            .then(
                function (store) {
                    var success,
                        attribute;

                    attribute = store.getDataSource().findBy(function (attribute) {
                        return attributePath === me.getAttributePath(attribute);
                    });

                    if (attribute) {
                        me.insertTabletGroupItem(attribute, term);
                        view.searchableAttributesField.select(attribute);
                        success = true;
                    } else {
                        term.destroy();
                        success = false;
                    }

                    deferred.resolve({
                        attributePath: attributePath,
                        success: success
                    });
                }
            ).done();

        return deferred.promise;
    },

    getAttributePath: function (attribute) {
        return attribute.get('path');
    },

    getTabletItemByAttribute: function (attrInfo, term) {
        var tabletDataType,
            tablet,
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

        if (!term) {
            term = new this.TermClass();
        }

        tablet = {
            xtype: 'component.search.attribute.tablet.tablet',
            term: term,
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
