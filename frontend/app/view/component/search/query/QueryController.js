Ext.define('Unidata.view.component.search.query.QueryController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.component.search.query',

    dataSearchToken: null,

    init: function () {
        var view = this.getView(),
            useRouting = view.getUseRouting();

        if (useRouting) {
            this.initRouter();
        }
    },

    initRouter: function () {
        var view = this.getView();

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
        var viewModel = this.getViewModel(),
            bindingEntity,
            bindingTerms,
            entityName;

        entityName = Unidata.util.Router.getTokenValue('dataSearch', 'entityName');

        if (entityName) {
            bindingEntity = viewModel.bind('{searchQuery.term.entity}', function (entityTerm) {
                if (entityTerm) {
                    entityTerm.setName(entityName);

                    bindingEntity.destroy();
                }
            });

            // инициализация значений термов по токенам роутинга
            bindingTerms = viewModel.bind('{searchQuery}', function (searchQuery) {
                var dataSearchToken =  Unidata.util.Router.getToken('dataSearch');

                if (searchQuery) {
                    Ext.Object.each(dataSearchToken.values, function (key, value) {
                        var token = searchQuery.findTerm(key);

                        if (token && token.setValue) {
                            token.setValue(value);
                        }
                    });

                    bindingTerms.destroy();
                }
            });
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

            Unidata.util.Router.resumeTokenEvents();
        }
    },

    onSearchTextFieldKeyPress: function (component, e) {
        if (e.getKey() === Ext.event.Event.ENTER) {
            this.doSearch();
        }
    },

    onExtendedSearchClick: function () {
        this.extendedToggle();
    },

    clearQuery: function () {
        var view = this.getView(),
            currentSearchQuery = view.getSearchQuery(),
            currentEntityTerm = currentSearchQuery.getEntityTerm(),
            currentEntityName = currentEntityTerm.getName(),
            newSearchQuery = new Unidata.module.search.DataSearchQuery();

        newSearchQuery.bind('term.entity', function (entityTerm) {
            entityTerm.setName(currentEntityName);
        }, this, {single: true});

        view.setSearchQuery(newSearchQuery);

        Unidata.showMessage(Unidata.i18n.t('search>query.searchParamsCleaned'));
    },

    onClearButtonClick: function () {
        this.clearQuery();
    },

    extendedToggle: function () {
        this.extendedVisible ? this.hideExtended() : this.showExtended();
    },

    showExtended: function () {
        var filtersContainer = this.lookupReference('filtersContainer'),
            systemWrap = this.lookupReference('systemWrap');

        filtersContainer.expand();
        systemWrap.expand();
        this.extendedVisible = true;
    },

    hideExtended: function () {
        var filtersContainer = this.lookupReference('filtersContainer'),
            systemWrap = this.lookupReference('systemWrap');

        filtersContainer.collapse();
        systemWrap.collapse();
        this.extendedVisible = false;
    },

    /**
     * Поискать
     *
     * @param extraParams Опциональный. Можно передать снаружи
     * @return searchHits
     */
    doSearch: function (extraParams) {
        var SearchDataApi = Unidata.util.api.SearchData,
            view = this.getView();

        if (this.lookupReference('useNewApi').getValue() === true) {
            this.doSearchNewApi();

            return;
        } else {
            view.store.setProxy(view.originalStoreProxy);
        }

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

    doSearchNewApi: function () {
        var view = this.getView(),
            searchQuery = view.getSearchQuery(),
            metaRecordTerm = searchQuery.findTerm('entity');

        // не нужно отправлять поисковый запрос, если не указано имя реестра / справочника
        if (!metaRecordTerm || !metaRecordTerm.getName()) {
            return;
        }

        searchQuery.getTermsData()
            .then(
                function (termsData) {
                    var currentPage = view.store.currentPage,
                        page = currentPage > (Math.ceil(view.store.totalCount / view.store.pageSize)) ? 1 : currentPage,
                        proxy;

                    proxy = new Unidata.proxy.data.SearchQueryProxy();

                    proxy.setExtraParams(termsData);

                    view.store.setProxy(proxy);

                    view.store.loadPage(page, {
                        scope: view.store,
                        callback: function (searchHits) {
                            view.fireEvent('searchsuccess', searchHits, termsData);
                        }
                    });
                }
            )
            .done();
    },

    disableSearch: function () {
        this.lookupReference('duplicatesOnlyCheckbox').disable();

        this.lookupReference('queryTextfield').disable();

        this.lookupReference('searchOptionsContainer').query().forEach(function (component) {
            component.disable();
        });
    },

    enableSearch: function () {
        var duplicatesCheckbox = this.lookupReference('duplicatesOnlyCheckbox'),
            duplicatesValue = duplicatesCheckbox.getValue();

        // duplicatesCheckbox.enable();

        this.lookupReference('queryTextfield').enable();

        this.lookupReference('searchOptionsContainer').query().forEach(function (component) {
            component.enable();
        });

        duplicatesCheckbox.setValue(!duplicatesValue);
        duplicatesCheckbox.setValue(duplicatesValue);
    },

    disableSearchDuplicatesOnly: function () {
        var view = this.getView();

        this.lookupReference('queryTextfield').disable();

        this.lookupReference('searchOptionsContainer').query().forEach(function (component) {
            component.disable();
        });

        this.lookupReference('filterPanel').setDisabled(true);
        view.classifierFilterList.setDisabled(true);
    },

    enableSearchDuplicatesOnly: function () {
        var view = this.getView();

        this.lookupReference('queryTextfield').enable();

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

    },

    onChangeDateAsOf: function (dateField, newValue) {
        var allPeriodActual = this.lookupReference('allPeriodActual'),
            includeInactive = this.lookupReference('includeInactiveCheckbox');

        if (newValue) {
            allPeriodActual.disable();
            includeInactive.show();
        } else {
            allPeriodActual.enable();
            includeInactive.hide();
        }
    },

    onAllPeriodActualToggle: function (checkbox, value) {
        var dateAsOf = this.lookupReference('dateAsOf');

        // не совместим с поиском "на дату"
        if (value) {
            dateAsOf.disable();
        } else {
            dateAsOf.enable();
        }

        this.fireFacetsChange();
    },

    onIncludeInactiveToggle: function () {
        this.fireFacetsChange();
    },

    onPendingOnlyToggle: function () {
        this.fireFacetsChange();
    },

    onInactiveOnlyToggle: function () {
        this.fireFacetsChange();
    },

    fireFacetsChange: function () {
        var view = this.getView(),
            allPeriodActual = this.lookupReference('allPeriodActual'),
            inactiveOnly = this.lookupReference('inactiveOnlyCheckbox'),
            includeInactive = this.lookupReference('includeInactiveCheckbox'),
            pendingOnly = this.lookupReference('pendingOnlyCheckbox'),
            facets;

        facets = {
            allPeriodActual: allPeriodActual.pressed,
            inactiveOnly: inactiveOnly.pressed,
            includeInactive: includeInactive.pressed,
            pendingOnly: pendingOnly.pressed
        };

        view.fireEvent('facetschange', view, facets);
    },

    updateTableSearch: function (tableSearch) {
        var viewModel = this.getViewModel(),
            binding;

        binding = viewModel.bind('{searchQuery.term.entity}', function (entityTerm) {
            if (entityTerm) {
                entityTerm.setFilterDisplayable(!tableSearch);

                binding.destroy();
            }
        });
    },

    updateSearchQuery: function (newSearchQuery, oldSearchQuery) {
        var view = this.getView();

        this.disableSearch();

        if (oldSearchQuery) {
            oldSearchQuery.unbind('entityTerm.metaRecord', this.onMetaRecordChange, this);
        }

        if (newSearchQuery) {
            newSearchQuery.bind('entityTerm.metaRecord', this.onMetaRecordChange, this);
            newSearchQuery.getEntityTerm().setFilterDisplayable(!view.getTableSearch());
        }
    },

    /**
     * Применить новый metaRecord
     * @param metaRecord
     */
    onMetaRecordChange: function (metaRecord) {
        var view = this.getView(),
            relationSearchList = view.relationSearchList;

        if (!metaRecord) {
            this.disableSearch();

            return;
        }

        this.enableSearch();
        view.fireEvent('entitychange', metaRecord);

        relationSearchList.setMetaRecord(metaRecord);
        this.updatePanelsVisibility(metaRecord);
    },

    updatePanelsVisibility: function (metaRecord) {
        var view = this.getView(),
            searchSectionVisible = view.getSearchSectionVisible(),
            externalSearchSectionVisible = view.getExternalSearchSectionVisible(),
            relations = metaRecord.relations ? metaRecord.relations() : false,
            classifiers = metaRecord.get('classifiers'),
            relationsVisible = false,
            classifiersVisible = false;

        if (externalSearchSectionVisible.classifiers) {
            classifiersVisible = Boolean(classifiers && classifiers.length);
        }

        if (externalSearchSectionVisible.relations) {
            relationsVisible = Boolean(!view.hideRelationsSearch && relations && relations.getCount());
        }

        searchSectionVisible.relations = relationsVisible;
        searchSectionVisible.classifiers = classifiersVisible;

        view.setSearchSectionVisible(searchSectionVisible);
    },

    onFilterChange: function () {
        var view = this.getView(),
            filterPanel = view.lookupReference('filterPanel'),
            relationSearchList = view.lookupReference('relationSearchList'),
            dataQualitySearch = view.dataQualitySearch,
            classifierFilterList = view.classifierFilterList;

        if (filterPanel.isEmptyFilter() &&
            classifierFilterList.isEmptyFilter() &&
            dataQualitySearch.isEmptyFilter() &&
            relationSearchList.isEmptyFilter()) {
            view.lookupReference('queryTextfield').enable();
        } else {
            view.lookupReference('queryTextfield').disable();
        }
    },

    // TODO: переписать через viewModel
    onSearchTextFieldChange: function () {
        var view = this.getView(),
            filterPanel = view.lookupReference('filterPanel'),
            relationSearchList = view.relationSearchList,
            isFilterEmpty,
            isSearchTextEmpty;

        isFilterEmpty = this.isFilterEmpty();
        isSearchTextEmpty = this.isSearchTextEmpty();

        if (isFilterEmpty && isSearchTextEmpty) {
            filterPanel.setDisabled(false);
            relationSearchList.setDisabled(false);
        } else {
            filterPanel.setDisabled(true);
            relationSearchList.setDisabled(true);
        }
    },

    isFilterEmpty: function () {
        var result,
            view = this.getView(),
            filterPanel = view.lookupReference('filterPanel'),
            dataQualitySearch = view.lookupReference('dataQualitySearch'),
            classifierFilterList = view.classifierFilterList;

        result = (filterPanel.isEmptyFilter() &&
            classifierFilterList.isEmptyFilter() &&
            dataQualitySearch.isEmptyFilter());

        return result;
    },

    isSearchTextEmpty: function () {
        var result,
            view = this.getView(),
            queryTextfield = view.lookupReference('queryTextfield');

        result = (queryTextfield.getValue() === '') ? true : false;

        return result;
    },

    onSortFieldExclude: function (attributePath) {
        var view = this.getView(),
            filterPanel = view.lookupReference('filterPanel'),
            classifierFilterList = view.classifierFilterList;

        filterPanel.excludeField(attributePath);
        classifierFilterList.excludeField(attributePath);
    },

    getMetaRecord: function () {
        var view = this.getView(),
            searchQuery = view.getSearchQuery(),
            metaRecord;

        metaRecord = searchQuery.getEntityTerm().getMetaRecord();

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

    defaultDisplayAttributesSwitch: function (button, flag) {
        this.getView().setUseToEntityDefaultSearchAttributes(flag);
    },

    /**
     * @private
     * @returns {Array|null}
     */
    getExtraParamsSearch: function () {
        var extraParams = {},
            facets = [],
            view = this.getView(),
            searchQuery = view.getSearchQuery(),
            metarecord = searchQuery.getMetaRecord(),
            toEntityDefaultDisplayAttributes = view.getToEntityDefaultDisplayAttributes(),
            toEntityDefaultSearchAttributes = view.getToEntityDefaultSearchAttributes(),
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

        if (!view.getUseToEntityDefaultSearchAttributes()) {
            toEntityDefaultSearchAttributes = null;
        }

        classifierNodes = classifierFilterList.getClassifierNodes();
        relationSearchItems = relationSearchList.getRelationSearchItems();

        sortFields = filterPanel.getSortFields();

        searchFields = Unidata.util.UPathMeta.buildAttributePaths(metarecord, [
            {
                fn: Ext.bind(
                    this.searchableAttributesFilter,
                    this,
                    [toEntityDefaultSearchAttributes],
                    true
                )
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

        if (this.lookupReference('includeInactiveCheckbox').getValue()) {
            facets.push('include_inactive_periods');
        }

        if (this.lookupReference('pendingOnlyCheckbox').getValue()) {
            facets.push('pending_only');
        }

        if (allPeriodActual) {
            facets.push('un_ranged');

            returnFields = Ext.Array.merge(returnFields, ['$from', '$to']);
        }

        entityName = metarecord.get('name');

        extraParams['entity'] = entityName;
        extraParams['returnFields'] = returnFields;
        extraParams['facets'] = facets;
        extraParams['fetchAll'] = false;
        extraParams['operator'] = 'AND';
        extraParams['qtype'] = 'FUZZY';
        extraParams['sortFields'] = sortFields;

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
            extraParams['@type'] = 'FORM';  // нечто нужное бекенду
        } else {
            extraParams['searchFields'] = searchFields;
            extraParams['text'] = this.lookupReference('queryTextfield').getValue();
            extraParams['fetchAll'] = !(extraParams['text'].trim());
            extraParams['@type'] = 'SIMPLE';  // нечто нужное бекенду

            if (formFields.length) {
                extraParams['fetchAll'] = false;
                extraParams['@type'] = 'COMBO';
            }
        }

        if (Ext.isArray(classifierNodes) && classifierNodes.length > 0 && !duplicatesOnly) {
            extraParams['@type'] = 'COMPLEX';  // нечто нужное бекенду

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

    /**
     * Фильтр для поисковых атрибутов
     *
     * @param attribute
     * @param {string[]} [searchAttributes] - атрибуты для отображения
     * @returns {boolean}
     */
    searchableAttributesFilter: function (attribute, searchAttributes) {
        var searchable = attribute.get('searchable'),
            attributeName = attribute.get('name');

        if (!Ext.isEmpty(searchAttributes)) {
            return (searchAttributes.indexOf(attributeName) !== -1);
        }

        return searchable;
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
            searchQuery = view.getSearchQuery();

        searchQuery.getEntityTerm().setName(selectedEntityName);
    },

    onAddQueryPreset: function () {
        this.showQueryPresetDialog();
    },

    /**
     * Отобразить диалог создания списка записей
     */
    showQueryPresetDialog: function () {
        var title = Unidata.i18n.t('common:saveSomething', {name: Unidata.i18n.t('glossary:searchQuery')}),
            message = Unidata.i18n.t('search>query.enterName'),
            view = this.getView(),
            metaRecord = view.getMetaRecord(),
            defaultName = Unidata.i18n.t('glossary:searchQuery');

        if (!metaRecord) {
            return;
        }

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
    },

    buildQueryPreset: function (name) {
        var view = this.getView(),
            metaRecord = view.getMetaRecord(),
            entityName = metaRecord.get('name'),
            searchQuery = view.getSearchQuery(),
            extraParams = searchQuery.getSaveData(),
            queryPreset;

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
        var resultsetGrid = this.lookupReference('resultsetGrid'),
            selectionModel = resultsetGrid.getSelectionModel(),
            selected;

        selected = selectionModel.getSelected().getRange();

        return selected;
    },

    onQueryPresetItemClick: function (btn, td, cellIndex, queryPreset) {
        // обрабатываем только клик на первую колонку
        if (cellIndex !== 0) {
            return;
        }

        this.useQueryPreset(queryPreset);
    },

    useQueryPreset: function (queryPreset) {
        var view = this.getView(),
            extraParams = queryPreset.get('extraParams');

        view.setSearchQuery(Ext.create(extraParams));
    }
});
