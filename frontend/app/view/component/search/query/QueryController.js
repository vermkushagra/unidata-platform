Ext.define('Unidata.view.component.search.query.QueryController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query',

    dataSearchToken: null,

    init: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            useRouting = view.getUseRouting(),
            queryPresetPanelVisible = Unidata.Config.getQueryPresetPanelVisible();

        viewModel.set('queryPresetPanelVisible', queryPresetPanelVisible);

        if (useRouting) {
            this.initRouter();
        }
    },

    initRouter: function () {
        var view = this.getView(),
            entityCombo = this.lookupReference('entityCombo');

        view.on('destroy', this.removeRouterTokens, this);
        Unidata.util.Router.on('main', this.onMainTokenChange, this);
        Unidata.util.Router.on('dataSearch', this.initComponentsRouting, this);
        Unidata.util.Router.removeTokenValue('main', 'reset');

        this.initComponentsRouting();
    },

    onMainTokenChange: function (tokenValues, oldTokenValues) {
        if (tokenValues.section === oldTokenValues.section) {
            return;
        }

        if (oldTokenValues.section && oldTokenValues.section === 'data') {
            this.removeRouterTokens();
        } else if (tokenValues.section && tokenValues.section === 'data') {
            if (tokenValues.reset) {
                this.dataSearchTokenValues = null;
                Unidata.util.Router.removeTokenValue('main', 'reset');
            } else {
                this.restoreRouterTokens();
            }
        }
    },

    removeRouterTokens: function () {
        this.dataSearchTokenValues = Unidata.util.Router.getTokenValues('dataSearch');
        Unidata.util.Router.removeToken('dataSearch');
    },

    restoreRouterTokens: function () {
        if (Ext.isObject(this.dataSearchTokenValues) && !Ext.Object.isEmpty(this.dataSearchTokenValues)) {
            Unidata.util.Router.setToken('dataSearch', this.dataSearchTokenValues);
        }
    },

    /**
     * Заполняем филды значениями из роутинга
     */
    initComponentsRouting: function () {
        var view = this.getView(),
            entityCombo = this.lookupReference('entityCombo'),
            metaRecord,
            entityName,
            entityType;

        if (entityCombo.dataLoading) {
            entityCombo.on('load', function () {
                this.initComponentsRouting();
            }, this);
        } else {
            if (view.rendered) {
                entityName = Unidata.util.Router.getTokenValue('dataSearch', 'entityName');

                if (entityName) {
                    entityCombo.suspendEvent('change');
                    entityCombo.setValue(entityName);
                    metaRecord = entityCombo.selection;

                    if (!metaRecord) {
                        entityCombo.setValue(null);
                    } else {
                        entityType = metaRecord.get('type');
                        this.loadAndUseMetaRecord(entityName, entityType, {dataQuality: true});
                    }

                    entityCombo.resumeEvent('change');
                }

                this.updateFieldRouterComponents();
            } else {
                view.on('afterrender', this.initComponentsRouting, this, {delay: 1});
            }
        }
    },

    updateFieldRouterComponents: function () {
        var view = this.getView();

        view.fieldRouterComponents.each(function (fieldRouterComponent) {
            var routerPlugin = fieldRouterComponent.findPlugin('form.field.router'),
                routerTokenName = routerPlugin.routerTokenName,
                routerValueName = routerPlugin.routerValueName,
                value = Unidata.util.Router.getTokenValue(routerTokenName, routerValueName);

            if (value) {
                fieldRouterComponent.setValue(value);
            }
        });
    },

    onContentExpand: function () {
        this.lookupReference('extendedSearchContainer').setHidden(false);
    },

    onSearchButtonClick: function () {
        var view = this.getView(),
            entityCombo = this.lookupReference('entityCombo'),
            useRouting = view.getUseRouting();

        this.doSearch();

        if (useRouting) {
            // обновляем роутинг
            Unidata.util.Router.suspendTokenEvents();
            Unidata.util.Router.removeToken('dataSearch');

            Unidata.util.Router.setTokenValue('dataSearch', 'entityName', entityCombo.getValue());

            this.getView().fieldRouterComponents.each(function (fieldRouterComponent) {
                var routerPlugin    = fieldRouterComponent.findPlugin('form.field.router'),
                    routerTokenName = routerPlugin.routerTokenName,
                    routerValueName = routerPlugin.routerValueName,
                    value           = fieldRouterComponent.getValue();

                Unidata.util.Router.setTokenValue(routerTokenName, routerValueName, value);
            });
            Unidata.util.Router.resumeTokenEvents();
        }
    },

    onSearchTextFieldKeyPress: function (component, e) {
        if (e.getKey() === Ext.event.Event.ENTER) {
            this.doSearch();
        }
    },

    onExtendedSearchClick: function () {
        var extendedSearchContainer = this.lookupReference('extendedSearchContainer'),
            placeholderContainer = this.lookupReference('placeholderContainer');

        extendedSearchContainer.setHidden(!extendedSearchContainer.isHidden());
        placeholderContainer.setHidden(!extendedSearchContainer.isHidden());
    },

    onClearButtonClick: function () {
        var systemSearchContainer = this.lookupReference('systemSearchContainer'),
            filterPanel = this.lookupReference('filterPanel');

        this.clearQueryForm();
        systemSearchContainer.collapse();
        filterPanel.collapse();
        Unidata.showMessage(Unidata.i18n.t('search>query.searchParamsCleaned'));
    },

    showExtendedSearchContainer: function () {
        var extendedSearchContainer = this.lookupReference('extendedSearchContainer'),
            placeholderContainer = this.lookupReference('placeholderContainer');

        extendedSearchContainer.setHidden(false);
        placeholderContainer.setHidden(!extendedSearchContainer.isHidden());
    },

    /**
     * Поискать
     *
     * @param extraParams Опциональный. Можно передать снаружи
     * @return searchHits
     */
    doSearch: function (extraParams) {
        var SearchDataApi = Unidata.util.api.SearchData,
            view           = this.getView();

        if (!extraParams) {
            extraParams = view.getExtraParams();
        }

        if (!this.validateSearchForm()) {
            Unidata.showError(Unidata.i18n.t('search>query.searchFormInvalid'));

            return;
        }

        if (!extraParams) {
            return;
        }

        // по успешному/неуспешному поиску кидать событие
        SearchDataApi.search(extraParams, view.store)
            .then(function (params) {
                var searchHits = params.searchHits,
                    extraParams = params.extraParams;

                view.fireEvent('searchsuccess', searchHits, extraParams);
            }, function () {
                console.log(view.searchErrorText);
            })
            .done();
    },

    disableSearch: function () {
        this.lookupReference('duplicatesOnlyCheckbox').disable();

        this.lookupReference('queryTextfield').disable();
        this.lookupReference('queryButton').disable();

        this.lookupReference('searchOptionsContainer').query().forEach(function (component) {
            component.disable();
        });
    },

    enableSearch: function () {
        var duplicatesCheckbox = this.lookupReference('duplicatesOnlyCheckbox'),
            duplicatesValue    = duplicatesCheckbox.getValue();

        // duplicatesCheckbox.enable();

        this.lookupReference('queryTextfield').enable();
        this.lookupReference('queryButton').enable();

        this.lookupReference('searchOptionsContainer').query().forEach(function (component) {
            component.enable();
        });

        duplicatesCheckbox.setValue(!duplicatesValue);
        duplicatesCheckbox.setValue(duplicatesValue);
    },

    disableSearchDuplicatesOnly: function () {
        var view = this.getView();

        this.lookupReference('queryTextfield').disable();
        this.lookupReference('queryButton').disable();

        this.lookupReference('searchOptionsContainer').query().forEach(function (component) {
            component.disable();
        });

        this.lookupReference('filterPanel').setDisabled(true);
        view.classifierFilterList.setDisabled(true);
    },

    enableSearchDuplicatesOnly: function () {
        var view = this.getView();

        this.lookupReference('queryTextfield').enable();
        this.lookupReference('queryButton').enable();

        this.lookupReference('searchOptionsContainer').query().forEach(function (component) {
            component.enable();
        });

        this.lookupReference('filterPanel').setDisabled(false);
        view.classifierFilterList.setDisabled(false);
    },

    onChangeDuplicatesOnlyCheckbox: function (checkbox, value) {
        if (value) {
            this.disableSearchDuplicatesOnly();
        } else {
            this.enableSearchDuplicatesOnly();
        }

        this.lookupReference('queryButton').enable();
    },

    onChangeDateAsOf: function (dateField, newValue) {
        var viewModel = this.getViewModel(),
            allPeriodActual = this.lookupReference('allPeriodActual');

        if (viewModel.get('metarecord')) {
            allPeriodActual.enable();
        }

        if (newValue) {
            allPeriodActual.disable();
        }
    },

    onChangeAllPeriodActualCheckbox: function (checkbox, value) {
        var view = this.getView(),
            dateAsOf = this.lookupReference('dateAsOf');

        // не совместим с поиском "на дату"
        if (value) {
            dateAsOf.disable();
        } else {
            dateAsOf.enable();
        }

        view.fireEvent('allperiodactualsearchchange', value);
    },

    onEntityChange: function (component) {
        var metaRecord           = component.selection,
            entityName,
            entityType;

        if (!metaRecord) {
            return;
        }

        entityName = metaRecord.get('name');
        entityType = metaRecord.get('type');
        this.loadAndUseMetaRecord(entityName, entityType, {system: true});
    },

    /**
     * Загрузить и применить metaRecord
     *
     * @param entityName {String} Имя реестра/справочника
     * @param entityType {String} Тип (Entity, LookupEntity)
     * @param preserve {Object} Признак необходимости очистки формы
     */
    loadAndUseMetaRecord: function (entityName, entityType, preserve) {
        var me                   = this,
            viewModel            = this.getViewModel(),
            view                 = this.getView(),
            newMetaRecord;

        this.disableSearch();

        newMetaRecord  = Ext.create('Unidata.model.entity.' + entityType);

        if (!newMetaRecord) {
            return;
        }

        newMetaRecord.setId(entityName);
        newMetaRecord.load({
            success: function (metaRecord) {
                viewModel.set('metarecord', metaRecord);
                me.useNewMetaRecord(metaRecord, preserve);
                me.enableSearch();
                view.fireEvent('entitychange', metaRecord);
            },
            scope: this
        });
    },

    /**
     * Применить новый metaRecord
     * @param metaRecord
     * @param preserve {Object} Какие секции не сбрасывать при смене metaRecord
     * dataQuality: true/false
     * system: true/false
     */
    useNewMetaRecord: function (metaRecord, preserve) {
        var view                 = this.getView(),
            filterPanel          = view.lookupReference('filterPanel'),
            dataQualitySearch    = this.lookupReference('dataQualitySearch'),
            relationSearchList   = view.relationSearchList,
            classifierFilterList = view.classifierFilterList;

        preserve = preserve || {};

        if (preserve['dataQuality'] !== true) {
            dataQualitySearch.resetFields(true);
        }

        if (preserve['system'] !== true) {
            this.clearSystemQuery();
        }

        relationSearchList.removeAll();

        filterPanel.setEntityRecord(metaRecord);
        view.setAttributeTabletsCount(filterPanel.getSearchableAttributesCount());

        classifierFilterList.setAllowedClassifiers(Ext.Array.clone(metaRecord.get('classifiers')));
        relationSearchList.setMetaRecord(metaRecord);
        dataQualitySearch.setMetaRecord(metaRecord);
        this.updatePanelsVisibility(metaRecord);
        view.queryPresetGrid.setEntityName(metaRecord.get('name'));
    },

    clearQueryForm: function () {
        var view                 = this.getView(),
            filterPanel          = view.lookupReference('filterPanel'),
            dataQualitySearch    = this.lookupReference('dataQualitySearch'),
            queryTextfield       = this.lookupReference('queryTextfield'),
            relationSearchList   = view.relationSearchList,
            classifierFilterList = view.classifierFilterList;

        queryTextfield.setValue(null);
        this.clearSystemQuery();
        filterPanel.resetFields();
        classifierFilterList.resetList();
        relationSearchList.removeAll();
        dataQualitySearch.resetFields(true);
    },

    clearSystemQuery: function () {
        var dateAsOf = this.lookupReference('dateAsOf'),
            dateCreated = this.lookupReference('dateCreated'),
            dateUpdated = this.lookupReference('dateUpdated'),
            allPeriodActual = this.lookupReference('allPeriodActual'),
            inactiveOnlyCheckbox = this.lookupReference('inactiveOnlyCheckbox'),
            pendingOnlyCheckbox = this.lookupReference('pendingOnlyCheckbox'),
            duplicatesOnlyCheckbox = this.lookupReference('duplicatesOnlyCheckbox');

        dateAsOf.setValue(null);
        dateCreated.setValue(null);
        dateUpdated.setValue(null);
        allPeriodActual.setValue(null);
        inactiveOnlyCheckbox.setValue(null);
        pendingOnlyCheckbox.setValue(null);
        duplicatesOnlyCheckbox.setValue(null);
    },

    updatePanelsVisibility: function (metaRecord) {
        var view = this.getView(),
            searchSectionVisible = view.getSearchSectionVisible(),
            relations = metaRecord.relations ? metaRecord.relations() : false,
            relationsVisible = (!view.hideRelationsSearch && relations && relations.getCount()),
            classifiers = metaRecord.get('classifiers'),
            classifiersVisible = (classifiers && classifiers.length);

        searchSectionVisible.relations = relationsVisible;
        searchSectionVisible.classifiers = classifiersVisible;

        view.setSearchSectionVisible(searchSectionVisible);
    },

    onFilterChange: function () {
        var view                  = this.getView(),
            filterPanel           = view.lookupReference('filterPanel'),
            relationSearchList    = view.lookupReference('relationSearchList'),
            dataQualitySearch     = view.dataQualitySearch,
            classifierFilterList  = view.classifierFilterList,
            queryButton           = view.lookupReference('queryButton');

        if (filterPanel.isEmptyFilter() &&
            classifierFilterList.isEmptyFilter() &&
            dataQualitySearch.isEmptyFilter() &&
            relationSearchList.isEmptyFilter()) {
            queryButton.enable();
            view.lookupReference('queryTextfield').enable();
        } else {
            view.lookupReference('queryTextfield').disable();

            if (!filterPanel.validate()) {
                queryButton.disable();
            } else {
                queryButton.enable();
            }
        }
    },

    // TODO: переписать через viewModel
    onSearchTextFieldChange: function () {
        var view                  = this.getView(),
            queryButton           = view.lookupReference('queryButton'),
            filterPanel           = view.lookupReference('filterPanel'),
            relationSearchList = view.relationSearchList,
            dataQualitySearch = view.dataQualitySearch,
            isFilterEmpty,
            isSearchTextEmpty;

        isFilterEmpty     = this.isFilterEmpty();
        isSearchTextEmpty = this.isSearchTextEmpty();

        if (isFilterEmpty && isSearchTextEmpty) {
            filterPanel.setDisabled(false);
            relationSearchList.setDisabled(false);
            dataQualitySearch.setDisabled(false);
        } else {
            filterPanel.setDisabled(true);
            relationSearchList.setDisabled(true);
            dataQualitySearch.setDisabled(true);
        }
    },

    isFilterEmpty: function () {
        var result,
            view                  = this.getView(),
            filterPanel           = view.lookupReference('filterPanel'),
            dataQualitySearch     = view.lookupReference('dataQualitySearch'),
            classifierFilterList = view.classifierFilterList;

        result = (filterPanel.isEmptyFilter() &&
                classifierFilterList.isEmptyFilter() &&
                dataQualitySearch.isEmptyFilter());

        return result;
    },

    isSearchTextEmpty: function () {
        var result,
            view           = this.getView(),
            queryTextfield = view.lookupReference('queryTextfield');

        result = (queryTextfield.getValue() === '') ? true : false;

        return result;
    },

    onSortFieldExclude: function (attributePath) {
        var view                  = this.getView(),
            filterPanel           = view.lookupReference('filterPanel'),
            classifierFilterList  = view.classifierFilterList;

        filterPanel.excludeField(attributePath);
        classifierFilterList.excludeField(attributePath);
    },

    getMetaRecord: function () {
        var viewModel = this.getViewModel(),
            metaRecord;

        metaRecord = viewModel.get('metarecord');

        return metaRecord;
    },

    onAllowedEntitiesChange: function (allowedEntities) {
        var view = this.getView(),
            entityCombo = view.entityCombo;

        entityCombo.applyFilterByEntityNames(allowedEntities);
    },

    onDestroy: function () {
        Ext.destroy(this.relayers);

        // здень не нужно вызывать this.callParent(arguments); т.к. его нет
    },

    /**
     * @public
     * @returns {Array|null}
     */
    getExtraParams: function () {
        var duplicatesOnlyCheckbox = this.lookupReference('duplicatesOnlyCheckbox'),
            isDuplicatesOnly = duplicatesOnlyCheckbox.getValue(),
            extraParams;

        if (isDuplicatesOnly) {
            extraParams = this.getExtraParamsDuplicates();
        } else {
            extraParams = this.getExtraParamsSearch();
        }

        return extraParams;
    },

    validateSearchForm: function () {
        var view = this.getView(),
            filterPanel = view.filterPanel;

        return filterPanel.validate();
    },

    /**
     * @private
     * @returns {Array|null}
     */
    getExtraParamsSearch: function () {
        var extraParams = {},
            facets = [],
            view = this.getView(),
            viewModel = this.getViewModel(),
            metarecord = viewModel.get('metarecord'),
            toEntityDefaultDisplayAttributes = view.getToEntityDefaultDisplayAttributes(),
            returnFields = [],
            isTableSearch = view.getTableSearch(),
            dateAsOf = this.lookupReference('dateAsOf').getValue(),
            dateCreated = this.lookupReference('dateCreated').getValue(),
            dateUpdated = this.lookupReference('dateUpdated').getValue(),
            dataQualitySearch = view.lookupReference('dataQualitySearch'),
            classifierFilterList = view.classifierFilterList,
            relationSearchList = view.relationSearchList,
            filterPanel = view.filterPanel,
            isFilterPanelsEmpty = true,
            formFields = [],
            entityName,
            dateCreatedFrom,
            dateCreatedTo,
            dateUpdatedFrom,
            dateUpdatedTo,
            classifierNodes,
            relationSearchItems,
            searchFields,
            sortFields,
            duplicatesOnly,
            allPeriodActual;

        // TODO isTableResultSetVisible уже выпилен разобраться как оно и зачем работало и как сейчас работает =)

        if (!metarecord) {
            return null;
        }

        classifierNodes       = classifierFilterList.getClassifierNodes();
        relationSearchItems   = relationSearchList.getRelationSearchItems();

        sortFields = filterPanel.getSortFields();

        searchFields = Unidata.util.UPathMeta.buildAttributePaths(metarecord, [
            {
                property: 'searchable',
                value: true
            },
            // поля с типом boolean не включаем см. UN-1477
            {
                filterFn: function (record) {
                    return record.get('typeValue') !== 'Boolean';
                }
            }
        ]);

        // в табличном результате поиска массив возвращаемых полей более широкий т.к. пользователь
        // может выбирать для отображения поля
        if (isTableSearch) {
            returnFields = Unidata.util.UPathMeta.buildSimpleAttributePaths(metarecord);

            if (typeof metarecord.complexAttributes === 'function') {
                metarecord.complexAttributes().each(function (compl) {
                    var fields,
                        nestedEntity = compl.getNestedEntity();

                    fields = Unidata.util.UPathMeta.buildSimpleAttributePaths(nestedEntity, null, [compl.get('name')]);
                    returnFields = Ext.Array.merge(returnFields, fields);
                });
            }
        } else {
            returnFields = Unidata.util.UPathMeta.buildAttributePaths(metarecord, [{
                fn: Ext.bind(
                    this.displayableAttributesFilter,
                    this,
                    [toEntityDefaultDisplayAttributes],
                    true
                )
            }]);
        }

        if (dataQualitySearch.lookupReference('errorsOnlyCheckbox').getValue()) {
            facets.push('errors_only');
        }

        duplicatesOnly = this.lookupReference('duplicatesOnlyCheckbox').getValue();
        allPeriodActual = this.lookupReference('allPeriodActual').getValue();

        if (duplicatesOnly) {
            facets.push('duplicates_only');
        }

        if (this.lookupReference('inactiveOnlyCheckbox').getValue()) {
            facets.push('inactive_only');
        }

        if (this.lookupReference('pendingOnlyCheckbox').getValue()) {
            facets.push('pending_only');
        }

        if (allPeriodActual) {
            facets.push('un_ranged');

            returnFields = Ext.Array.merge(returnFields, ['$from', '$to']);
        }

        entityName = metarecord.get('name');

        extraParams['entity']       = entityName;
        extraParams['returnFields'] = returnFields;
        extraParams['facets']       = facets;
        extraParams['fetchAll']     = false;
        extraParams['operator']     = 'AND';
        extraParams['qtype']        = 'FUZZY';
        extraParams['sortFields']   = sortFields;

        // пустая панель фильтрации по модели?
        if (filterPanel && !filterPanel.isEmptyFilter()) {
            isFilterPanelsEmpty = false;
        }

        // пустая панель фильтрации по правилам качества?
        if (dataQualitySearch && !dataQualitySearch.isEmptyFilter()) {
            isFilterPanelsEmpty = false;
        }

        // по дате создания
        if (dateCreated) {
            dateCreatedFrom = Ext.Date.format(dateCreated, Unidata.Config.getDateTimeFormatProxy());

            dateCreated.setDate(dateCreated.getDate() + 1);
            dateCreatedTo = Ext.Date.format(dateCreated, Unidata.Config.getDateTimeFormatProxy());

            formFields.push({
                name: '$created_at',
                type: 'Date',
                inverted: false,
                range: [
                    dateCreatedFrom,
                    dateCreatedTo
                ]
            });
            searchFields.push('$created_at');
        }

        // по дате обновления
        if (dateUpdated) {
            dateUpdatedFrom = Ext.Date.format(dateUpdated, Unidata.Config.getDateTimeFormatProxy());

            dateUpdated.setDate(dateUpdated.getDate() + 1);
            dateUpdatedTo = Ext.Date.format(dateUpdated, Unidata.Config.getDateTimeFormatProxy());

            formFields.push({
                name: '$updated_at',
                type: 'Date',
                inverted: false,
                range: [
                    dateUpdatedFrom,
                    dateUpdatedTo
                ]
            });
            searchFields.push('$updated_at');
        }

        extraParams['formFields'] = formFields;

        if (!isFilterPanelsEmpty && !duplicatesOnly) {
            formFields = Ext.Array.merge(formFields, filterPanel.getFilter());

            if (dataQualitySearch) {
                formFields = Ext.Array.merge(formFields, dataQualitySearch.getFilter());
            }

            extraParams['formFields'] = formFields;
            extraParams['@type']      = 'FORM';  // нечто нужное бекенду
        } else {
            extraParams['searchFields'] = searchFields;
            extraParams['text']         = this.lookupReference('queryTextfield').getValue();
            extraParams['fetchAll']     = !(extraParams['text'].trim());
            extraParams['@type']        = 'SIMPLE';  // нечто нужное бекенду

            if (formFields.length) {
                extraParams['fetchAll'] = false;
                extraParams['@type'] = 'COMBO';
            }
        }

        if (Ext.isArray(classifierNodes) && classifierNodes.length > 0 && !duplicatesOnly) {
            extraParams['@type']      = 'COMPLEX';  // нечто нужное бекенду

            if (!Ext.isArray(extraParams['supplementaryRequests'])) {
                extraParams['supplementaryRequests'] = [];
            }

            extraParams['supplementaryRequests'] = Ext.Array.merge(
                extraParams['supplementaryRequests'],
                this.buildClassifierSearchSupplementaryRequests(classifierNodes, entityName)
            );
        }

        // is complex relation search
        if (Ext.isArray(relationSearchItems) && relationSearchItems.length > 0 && !duplicatesOnly) {
            relationSearchItems = Ext.Array.filter(relationSearchItems, this.isRelationSearchItemEnable, this);

            if (!Ext.isArray(extraParams['supplementaryRequests'])) {
                extraParams['supplementaryRequests'] = [];
            }

            extraParams['supplementaryRequests'] = Ext.Array.merge(
                extraParams['supplementaryRequests'],
                this.buildRelationSearchSupplementaryRequests(
                    dateAsOf,
                    relationSearchItems,
                    allPeriodActual
                )
            );

            extraParams['@type'] = 'COMPLEX';  // нечто нужное бекенду
        }

        // если поиск по классификаторам или связям но форма пустая то fetchAll = true
        if (!Ext.isEmpty(extraParams['supplementaryRequests']) &&
            Ext.isEmpty(extraParams['formFields']) &&
            Ext.isEmpty(extraParams['text'])) {

            extraParams['fetchAll'] = true;
        }

        if (dateAsOf) {
            extraParams['asOf'] = Ext.Date.format(dateAsOf, Unidata.Config.getDateTimeFormatProxy());
        }

        return extraParams;
    },

    loadFromExtraParams: function (extraParams) {
        var sectionsUpdated,
            attributesUpdated,
            systemParamsUpdated;

        systemParamsUpdated = this.loadSystemParams(extraParams);
        attributesUpdated = this.loadAttributes(extraParams);

        sectionsUpdated = {
            attributes: attributesUpdated,
            system: systemParamsUpdated
        };

        return sectionsUpdated;
    },

    loadSystemParams: function (extraParams) {
        var dateAsOf = this.lookupReference('dateAsOf'),
            dateCreated = this.lookupReference('dateCreated'),
            dateUpdated = this.lookupReference('dateUpdated'),
            allPeriodActual = this.lookupReference('allPeriodActual'),
            inactiveOnlyCheckbox = this.lookupReference('inactiveOnlyCheckbox'),
            formFields = extraParams.formFields,
            foundCreatedAt,
            foundUpdatedAt,
            foundCreatedAtValue,
            foundUpdatedAtValue,
            dateAsOfValue = null,
            systemParamsUpdated,
            isInactiveOnly,
            isUnUnrange;

        if (extraParams.asOf) {
            dateAsOfValue = Ext.Date.parse(extraParams.asOf, 'Y-m-dTH:i:s.u');
        }
        dateAsOf.setValue(dateAsOfValue);

        isInactiveOnly = Ext.Array.contains(extraParams.facets, 'inactive_only');
        inactiveOnlyCheckbox.setValue(isInactiveOnly);

        isUnUnrange = Ext.Array.contains(extraParams.facets, 'un_range');
        allPeriodActual.setValue(isUnUnrange);

        foundCreatedAt = Ext.Array.findBy(formFields, function (formField) {return formField.name === '$created_at';});

        if (foundCreatedAt) {
            foundCreatedAtValue = Ext.Date.parse(foundCreatedAt.range[0], 'Y-m-dTH:i:s.u');
            dateCreated.setValue(foundCreatedAtValue);
        }

        foundUpdatedAt = Ext.Array.findBy(formFields, function (formField) {return formField.name === '$updated_at';});

        if (foundUpdatedAt) {
            foundUpdatedAtValue = Ext.Date.parse(foundUpdatedAt.range[0], 'Y-m-dTH:i:s.u');
            dateUpdated.setValue(foundUpdatedAtValue);
        }

        systemParamsUpdated = extraParams.asOf || isInactiveOnly || isUnUnrange || foundCreatedAt || foundUpdatedAt;

        return systemParamsUpdated;
    },

    loadAttributes: function (extraParams) {
        var me = this,
            viewModel = this.getViewModel(),
            filterPanel = this.lookupReference('filterPanel'),
            metaRecord = viewModel.get('metarecord'),
            searchableAttributes,
            attributeFormFields = extraParams.formFields,
            attributesUpdated = false;

        searchableAttributes = filterPanel.getSearchableAttributes(metaRecord);

        Ext.Array.each(attributeFormFields, function (formField) {
            var allowedTypes,
                tablet,
                tabletItem,
                inputEqValue,
                inputFindType,
                inputLeftRange,
                inputRightRange,
                findType,
                found,
                parseFormats,
                value,
                range = [null, null];

            parseFormats = {
                Date: 'Y-m-dTH:i:s.u',
                Time: 'TH:i:s.uZ'
            };

            allowedTypes = ['String', 'Boolean', 'Number', 'Integer', 'Date', 'Timestamp', 'Time'];

            if (!Ext.Array.contains(allowedTypes, formField.type)) {
                return;
            }

            found = Ext.Array.findBy(searchableAttributes, function (attr) {
                return attr.path === formField.name;
            });

            if (!found) {
                return;
            }

            tablet = filterPanel.findTabletGroupByAttribute(found.attribute);

            if (!tablet) {
                filterPanel.createTabletGroup(found.attribute, false);
                tablet = filterPanel.insertTabletGroupItem(found.attribute);
            } else {
                tablet = filterPanel.insertTabletGroupItem(found.attribute);
            }
            tabletItem = tablet.tabletItem;

            // пока такой хак чтобы не проставлять значения в лукапы
            if (tabletItem instanceof  Unidata.view.component.search.attribute.tablet.Lookup) {
                return;
            }

            inputFindType = tablet.lookupReference('findType');

            if (formField.hasOwnProperty('value')) {
                if (formField.value !== null) {
                    // точное значение, исключить значение

                    // определяем findType
                    if (formField.like === true) {
                        findType = 'like';
                    } else if (formField.startWith === true) {
                        findType = 'startwith';
                    } else if (formField.fuzzy === true) {
                        findType = 'fuzzy';
                    } else {
                        findType = (formField.inverted ? 'not' : '') + 'exact';
                    }

                    // проставляем точное значение
                    inputEqValue = tabletItem.lookupReference('eqValue');

                    if (formField.type === 'Date') {
                        value = Ext.Date.parse(formField.value, parseFormats[formField.type]);
                    } else if (formField.type === 'Time') {
                        value = Ext.util.Format.substr(formField.value, 1, 8);
                    } else {
                        value = formField.value;
                    }
                    inputEqValue.setValue(value);
                } else {
                    // пустое/не пустое
                    findType = (formField.inverted ? 'not' : '') + 'null';
                }
            } else if (Ext.isArray(formField.range)) {
                // диапазон, исключить диапазон

                // определяем findType
                findType = (formField.inverted ? 'not' : '') + 'exact';

                // проставляем диапазон
                inputLeftRange = tabletItem.lookupReference('leftRange');
                inputRightRange = tabletItem.lookupReference('rightRange');

                if (formField.type === 'Date') {
                    range[0] = Ext.Date.parse(formField.range[0], parseFormats[formField.type]);
                    range[1] = Ext.Date.parse(formField.range[1], parseFormats[formField.type]);
                } else if (formField.type === 'Time') {
                    range[0] = Ext.util.Format.substr(formField.range[0], 1, 8);
                    range[1] = Ext.util.Format.substr(formField.range[1], 1, 8);
                } else {
                    range = formField.range;
                }
                inputLeftRange.setValue(range[0]);
                inputRightRange.setValue(range[1]);
            } else {
                throw new Error(Unidata.i18n.t('search>query.unknownSearchParamType'));
            }

            inputFindType.setValue(findType);

            attributesUpdated = true;
        });

        return attributesUpdated;
    },

    /**
     *
     * @param formField
     * @returns {Unidata.view.component.search.attribute.tablet.Tablet}
     */
    findAttributeTabletByFormField: function () {
        var attributeTablet;

        return attributeTablet;
    },

    /**
     * Фильтр для отображаемых атрибутов
     *
     * @param attribute
     * @param {string[]} [displayAttributes] - атрибуты для отображения
     * @returns {boolean}
     */
    displayableAttributesFilter: function (attribute, displayAttributes) {
        var displayable = attribute.get('displayable'),
            attributeName = attribute.get('name');

        if (!Ext.isEmpty(displayAttributes)) {
            return (displayAttributes.indexOf(attributeName) !== -1);
        }

        return displayable;
    },

    isRelationSearchItemEnable: function (relationSearchItem) {
        var relName;

        if (relationSearchItem && !relationSearchItem.isDisabled()) {
            relName = relationSearchItem.getRelationName();
        } else {
            return false;
        }

        if (!relName) {
            return false;
        }

        return true;
    },

    /**
     * @private
     * @returns {*|{}}
     */
    getExtraParamsDuplicates: function () {
        var extraParams = this.getExtraParamsSearch();

        extraParams['facets'] = ['duplicates_only'];
        extraParams['text'] = '';
        extraParams['fetchAll'] = true;

        return extraParams;
    },

    /**
     * @private
     * @param dateAsOf
     * @param relationSearchItems
     * @param allPeriodActual
     * @returns {Array|*|Ext.promise.Promise|{}}
     */
    buildRelationSearchSupplementaryRequests: function (dateAsOf, relationSearchItems, allPeriodActual) {
        return Ext.Array.map(relationSearchItems, this.buildRelationSearchSupplementaryRequest.bind(this, dateAsOf, allPeriodActual), this);
    },

    /**
     * @private
     * @param dateAsOf
     * @param allPeriodActual
     * @param relationSearchItem
     * @returns {{}}
     */
    buildRelationSearchSupplementaryRequest: function (dateAsOf, allPeriodActual, relationSearchItem) {
        var request = {},
            relName = relationSearchItem.getRelationName(),
            etalonIds = relationSearchItem.getEtalonIds(),
            metaRecord = relationSearchItem.getMetaRecord(),
            dateAsOfStr;

        request['@type'] = 'FORM';
        request['dataType'] = 'ETALON_REL';
        request['entity'] = metaRecord.get('name');
        request['formFields'] = [];
        request['facets'] = [];

        if (!relName) {
            return null;
        }

        if (allPeriodActual) {
            request['facets'].push('un_ranged');
        } else {
            dateAsOf = dateAsOf ? dateAsOf : new Date();

            dateAsOfStr = Ext.Date.format(dateAsOf, Unidata.Config.getDateTimeFormatProxy());
            request['formFields'].push({
                inverted: false,
                name: '$from',
                type: 'Date',
                range: [null, dateAsOfStr]
            });
            request['formFields'].push({
                inverted: false,
                name: '$to',
                type: 'Date',
                range: [dateAsOfStr, null]
            });
        }

        request['formFields'].push({
            inverted: false,
            name: '$rel_name',
            type: 'String',
            value: relName
        });

        Ext.Array.each(etalonIds, function (etalonId) {
            request['formFields'].push({
                inverted: false,
                name: '$etalon_id_to',
                type: 'String',
                value: etalonId
            });
        });

        return request;
    },

    buildClassifierSearchSupplementaryRequests: function (classifierNodes, entityName) {
        var supplementaryRequests;

        supplementaryRequests = Ext.Array.map(classifierNodes, this.buildClassifierSearchSupplementaryRequest.bind(this, entityName));

        return supplementaryRequests;
    },

    buildClassifierSearchSupplementaryRequest: function (entityName, classifierNode) {
        var request = {},
            view = this.getView(),
            classifierFilterList = view.classifierFilterList,
            formFields = classifierFilterList.getFilter(classifierNode);

        request['@type'] = 'FORM';  // нечто нужное бекенду
        request['dataType'] = 'CLASSIFIER';  // нечто нужное бекенду
        request['entity'] = entityName;

        request['searchFields'] = [];
        request['returnFields'] = [];
        request['fetchAll'] = false;
        request['qtype'] = 'TERM';
        request['operator'] = 'AND';

        request['formFields'] = Ext.Array.merge(formFields, this.buildClassifierNodeSearchField(classifierNode));

        return request;
    },

    /**
     * @private
     * @param classifierNodes
     * @returns {Array|*|Ext.promise.Promise|{}}
     */
    buildClassifierNodeSearchFields: function (classifierNodes) {
        return Ext.Array.map(classifierNodes, this.buildClassifierNodeSearchField, this);
    },

    /**
     *
     * @private
     * @param classifierNode
     */
    buildClassifierNodeSearchField: function (classifierNode) {
        var field,
            classifierName = classifierNode.get('classifierName'),
            classifierNodeId = classifierNode.get('id');

        field = {
            inverted: false,
            name: [classifierName, '$nodes.$node_id'].join('.'),
            type: 'String',
            value: classifierNodeId
        };

        return field;
    },

    /**
     * @private
     * @param selectedEntityName
     */
    updateSelectedEntityName: function (selectedEntityName) {
        var view = this.getView(),
            entityCombo = view.entityCombo,
            picker = entityCombo.getPicker(),
            store = picker.getStore();

        if (store.isLoaded()) {
            this.selectEntityByName(selectedEntityName);
        } else {
            store.on('load', this.onEntityStoreLoad.bind(this, selectedEntityName), this);
        }
    },

    onEntityStoreLoad: function (entityName) {
        this.selectEntityByName(entityName);
    },

    /**
     * @private
     * @param entityName
     */
    selectEntityByName: function (entityName) {
        var view = this.getView(),
            entityCombo = view.entityCombo,
            picker = entityCombo.getPicker(),
            store = picker.getStore(),
            entity;

        entity = store.findRecord('entityName', entityName, 0, false, false, true);

        if (entity) {
            picker.setSelection(entity);
        }
    },

    onAddQueryPreset: function () {
        this.showQueryPresetDialog();
    },

    /**
     * Отобразить диалог создания списка записей
     */
    showQueryPresetDialog: function () {
        var QueryPresetStorage   = Unidata.module.storage.QueryPresetStorage,
            title                = Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:searchQuery')}),
            message              = Unidata.i18n.t('search>query.enterName'),
            view                 = this.getView(),
            metaRecord           = view.getMetaRecord(),
            entityName,
            defaultName = Unidata.i18n.t('glossary:searchQuery');

        if (!metaRecord) {
            return;
        }

        entityName = metaRecord.get('name');

        //defaultName = QueryPresetStorage.buildQueryPresetDefaultName(entityName);

        Ext.Msg.prompt(title, message, this.onAddQueryPresetPromptButtonClick, this, false, defaultName);
    },

    onAddQueryPresetPromptButtonClick: function (btn, name) {
        var queryPresetPanel = this.lookupReference('queryPresetPanel'),
            QueryPresetStorage = Unidata.module.storage.QueryPresetStorage,
            metaRecord = this.getMetaRecord(),
            entityName = metaRecord.get('name'),
            result;

        if (btn === 'ok') {
            result = QueryPresetStorage.validateQueryPresetName(name, entityName);

            if (result === true) {
                this.createQueryPreset(name);

                if (queryPresetPanel.getCollapsed()) {
                    queryPresetPanel.expand();
                }
                Unidata.showMessage(Unidata.i18n.t('search>preset.searchQuerySaved', {name: name}));
            } else {
                this.showQueryPresetDialog();
                Unidata.showWarning(result);
            }
        }
    },

    createQueryPreset: function (name) {
        var QueryPresetStorage = Unidata.module.storage.QueryPresetStorage,
            queryPreset;

        // проверка что поисковый запрос существует
        queryPreset = this.buildQueryPreset(name);
        QueryPresetStorage.addQueryPreset(queryPreset);
        //this.deselectAll();
    },

    buildQueryPreset: function (name) {
        var view       = this.getView(),
            metaRecord = view.getMetaRecord(),
            entityName = metaRecord.get('name'),
            extraParams,
            queryPreset;

        extraParams = this.getExtraParams();

        queryPreset = Ext.create('Unidata.model.search.QueryPreset', {
            name: name,
            entityName: entityName,
            extraParams: extraParams
        });

        return queryPreset;
    },

    /**
     *
     * @returns {Unidata.model.search.SearchHit[]}
     */
    getSelectedSearchHits: function () {
        var resultsetGrid  = this.lookupReference('resultsetGrid'),
            selectionModel = resultsetGrid.getSelectionModel(),
            selected;

        selected = selectionModel.getSelected().getRange();

        return selected;
    },

    onQueryPresetItemClick: function (btn, td, cellIndex, queryPreset) {
        var extraParams = queryPreset.get('extraParams'),
            name = queryPreset.get('name');

        // обрабатываем только клик на первую колонку
        if (cellIndex !== 0) {
            return;
        }

        this.useQueryPreset(queryPreset);
    },

    useQueryPreset: function (queryPreset) {
        var extraParams = queryPreset.get('extraParams'),
            systemSearchContainer = this.lookupReference('systemSearchContainer'),
            filterPanel = this.lookupReference('filterPanel'),
            sectionsUpdated;

        this.clearQueryForm();
        this.showExtendedSearchContainer();
        sectionsUpdated = this.loadFromExtraParams(extraParams);

        if (sectionsUpdated.system) {
            systemSearchContainer.expand();
        } else {
            systemSearchContainer.collapse();
        }

        if (sectionsUpdated.attributes) {
            filterPanel.expand();
        } else {
            filterPanel.collapse();
        }
    }
});
