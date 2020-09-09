Ext.define('Unidata.view.admin.entity.metarecord.MetaRecordController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord',

    metaDependencyGraph: null,

    init: function () {
        var view = this.getView(),
            viewModel = this.getViewModel();

        viewModel.getStore('lookupEntities').getProxy().on('endprocessresponse', this.onEndProcessResponse, this);
        viewModel.getStore('entities').getProxy().on('endprocessresponse', this.onEndProcessResponse, this);

        viewModel.bind('{readOnly}', this.updateReadOnlyTrigger, this);

        this.syncDraftMode(view.getDraftMode());
    },

    updateMetaRecordDataTabs: function () {
        var viewModel = this.getViewModel(),
            metaRecord = viewModel.get('currentRecord'),
            dqTab = this.lookupReference('dataQuality');

        if (dqTab) {
            dqTab.setMetaRecord(metaRecord);
        }
    },

    onEndProcessResponse: function (proxy, options, operation) {
        if (options.request.options.action === 'read' && operation.getError()) {
            this.closeView();
        }
    },

    checkSimpleAttributes: function (record, property) {
        var paths = Unidata.util.UPathMeta.buildSimpleAttributePaths(record, [{property: property, value: true}]);

        return paths.length > 0;
    },

    checkAttributesValid: function (attrType, record) {
        //TODO: return errorMsgs if record is invalid
        var attr,
            attrs,
            isValid = true;

        if (attrType === 'simpleAttributes') {
            attrs = record.simpleAttributes();
        } else if (attrType === 'complexAttributes') {
            attrs = record.complexAttributes();
        } else if (attrType === 'codeAttribute') {
            attr = record.getCodeAttribute();
        } else if (attrType === 'aliasCodeAttributes') {
            attr = record.aliasCodeAttributes();
        }

        if (attrType === 'codeAttribute') {
            isValid = attr.isValid();
        } else {
            attrs.each(function (attr) {
                if (!attr.isValid()) {
                    isValid = false;

                    return false;
                }
            });
        }

        return isValid;
    },

    checkConsolidationValid: function () {
        var view = this.lookupReference('consolidationView');

        return view.getController().isValid();
    },

    checkRecordValid: function (record) {
        var view = this.getView(),
            // dataQualityPanel = this.lookupReference('dataQualityPanel'),
            errorMsg,
            errorMsgs,
            isLookupEntity,
            attributePanel = this.getView().lookupReference('attributePanel'),
            propertyPanel = this.getView().lookupReference('propertyPanel'),
            externalIdGenerationStrategyForm = propertyPanel.generationStrategyForm,
            codeAttribute,
            codeAttributeGenerationStrategyForm = attributePanel.generationStrategyForm,
            newDqErrors,
            newDqErrorCodes,
            errorNestedEntityDisplayNames,
            draft;

        errorMsgs = [];

        if (!record.isValid()) {
            if (view.hidePropertyTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidPropertySettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidPropertySettings')]);
            }

            propertyPanel.getController().validateRequiredFields();

            return errorMsgs;
        }

        if (!externalIdGenerationStrategyForm.validateAllFields()) {
            if (view.hidePropertyTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidPropertySettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>generationStrategy.invalidExternalIdGeneration')]);
            }

            return errorMsgs;
        }

        isLookupEntity = this.getViewModel().get('isLookupEntity');

        if (isLookupEntity) {
            codeAttribute = record.getCodeAttribute();

            if (!codeAttribute ||
                this.checkAttributesValid('codeAttribute', record) !== true) {

                if (view.hideAttributeTab) {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
                } else {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>unefinedCodeAttribute')]);
                }

                return errorMsgs;
            }

            if (!codeAttributeGenerationStrategyForm.validateAllFields()) {
                if (view.hideAttributeTab) {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
                } else {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>generationStrategy.invalidCodeAttributeGeneration')]);
                }

                return errorMsgs;
            }
        }

        if ((errorMsg = this.checkGeneratorStrategiesConsistency()) !== true) {
            return errorMsg;
        }

        if (attributePanel.isErrors) {
            if (view.hideAttributeTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>badAttributes')]);
            }

            return errorMsgs;
        }

        if (this.checkAttributesValid('simpleAttributes', record) !== true) {
            if (view.hideAttributeTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>badAttributes')]);
            }

            return errorMsgs;
        }

        if (!isLookupEntity) {
            if (this.checkAttributesValid('complexAttributes', record) !== true) {
                if (view.hideAttributeTab) {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
                } else {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>badAttributes')]);
                }

                return errorMsgs;
            }

            if (!this.checkRelation()) {
                if (view.hideRelationTab) {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidRelationSettingsAskAdministrator')]);
                } else {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidRelations')]);
                }

                return errorMsgs;
            }

            if (!this.checkConsolidationValid()) {
                if (view.hideConsolidationTab) {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidConsolidationSettingsAskAdministrator')]);
                } else {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidConsolidationSettings')]);
                }

                return errorMsgs;
            }

            errorNestedEntityDisplayNames = this.checkComplexAttributeNameUnique(record);

            if (errorNestedEntityDisplayNames.length) {
                if (view.hideAttributeTab) {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
                } else {
                    errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>complexAttributeAlreadyExists', {names: Ext.Array.unique(errorNestedEntityDisplayNames).join(', ')})]);
                }
            }
        }

        draft = view.getDraftMode();
        newDqErrors = Unidata.util.DataQuality.getMetaRecordDqErrorList(record, draft);

        if (newDqErrors.length) {
            if (view.hideAttributeTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
            } else {
                newDqErrorCodes = Unidata.util.DataQuality.getErrorCodeList(newDqErrors);

                if (Ext.Array.contains(newDqErrorCodes, Unidata.util.DataQuality.errorCodes.DQ_EMPTY_TYPE)) {
                    errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>selectTypeRule'));
                }

                if (Ext.Array.contains(newDqErrorCodes, Unidata.util.DataQuality.errorCodes.DQ_EMPTY_USER_MESSAGE)) {
                    errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>notSetUserMessage'));
                }

                if (Ext.Array.contains(newDqErrorCodes, Unidata.util.DataQuality.errorCodes.DQ_EMPTY_DATASOURCE)) {
                    errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>notSetDataSource'));
                }

                if (Ext.Array.contains(newDqErrorCodes, Unidata.util.DataQuality.errorCodes.DQ_REQUIRED_PORT_INVALID)) {
                    errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>requiredPortsInvalid'));
                }

                if (Ext.Array.contains(newDqErrorCodes, Unidata.util.DataQuality.errorCodes.DQ_APPLICABLE_INVALID)) {
                    errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>sourceSystemInvalid'));
                }

                Ext.Array.each(newDqErrors, function (error) {
                    var codes = [
                        Unidata.util.DataQuality.errorCodes.DQ_VALIDATOR_INVALID,
                        Unidata.util.DataQuality.errorCodes.DQ_RAISE_INVALID,
                        Unidata.util.DataQuality.errorCodes.DQ_ENRICH_INVALID
                    ];

                    if (Ext.Array.contains(codes, error.code)) {
                        errorMsgs = Ext.Array.merge(errorMsgs, Ext.String.format('{0}: {1}', error.dqName, error.message));
                    }
                });

            }

            return errorMsgs;
        }

        if (!this.checkSimpleAttributes(record, 'displayable')) {
            if (view.hideAttributeTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>notSetDisplayedAttribute')]);
            }

            return errorMsgs;
        }

        if (!this.checkSimpleAttributes(record, 'mainDisplayable')) {
            if (view.hideAttributeTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>notSetMainDisplayedAttribute')]);
            }

            return errorMsgs;
        }

        if (!this.checkSimpleAttributes(record, 'searchable')) {
            if (view.hideAttributeTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>netSetSearchAttribute')]);
            }

            return errorMsgs;
        }

        if (!this.checkAttributeNameUnique(record)) {
            if (view.hideAttributeTab) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator')]);
            } else {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>attributeNamesNotUnique')]);
            }
        }

        return errorMsgs.length > 0 ? errorMsgs : true;
    },

    checkRelation: function () {
        var view = this.getView(),
            referencePanel = view.lookupReference('referencePanel');

        return referencePanel.getController().checkRelation();
    },

    checkAttributeNameUnique: function (record) {
        var isLookupEntity,
            result = true;

        isLookupEntity = this.getViewModel().get('isLookupEntity');

        if (isLookupEntity) {
            result = this.isAttributeNameUniqueLookupEntity(record);
        } else {
            result = this.isAttributeNameUniqueEntity(record);
        }

        return result;
    },

    /**
     * Проверка уникальности имени комплексного атрибута (вложенной сущности, nested entity)
     * на уровне системы
     * Если уже сложилась ситуация, что некий атрибут является р
     *
     * @param localMetaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель
     * @returns {boolean} Признак успешного прохождения проверки
     */
    checkComplexAttributeNameUnique: function (localMetaRecord, topMetaRecord) {
        var MetaDependencyGraphUtil = Unidata.util.MetaDependencyGraph,
            MetaRecordUtil = Unidata.util.MetaRecord,
            VertexType = Unidata.model.entity.metadependency.Vertex.type,
            metaDependencyGraph = this.metaDependencyGraph,
            nestedEntities,
            errorNames = [],
            result = true;

        topMetaRecord = topMetaRecord || localMetaRecord;

        if ((!MetaRecordUtil.isEntity(localMetaRecord) && !MetaRecordUtil.isNested(localMetaRecord)) || !metaDependencyGraph) {
            return true;
        }

        localMetaRecord.complexAttributes().each(function (item) {
            var name = item.get('name'),
                nestedEntityNewName = item.isNameModifiedOrPhantom(),
                nestedEntityVertex,
                topEntityName,
                isSharedNestedEntity;

            nestedEntityVertex = MetaDependencyGraphUtil.findVertexes(metaDependencyGraph, name, VertexType.NESTED_ENTITY);
            topEntityName = this.findNestedEntityTopParentName(name);
            nestedEntities = this.findAllNestedEntitiesByName(localMetaRecord, name);
            isSharedNestedEntity = topEntityName === null;

            // если создаем новую метамодель, то проверяем не существует ли NESTED_ENTITY с таким же именем среди имеющихся в системе
            // если редактируем существующую метамодель, то проверяем не существует ли NESTED_ENTITY с таким же именем среди имеющихся в системе, если такая NESTED_ENTITY не является разделяемой
            if (isSharedNestedEntity) {
                result = true;
            } else if (nestedEntities.length > 1 ||
                      (nestedEntityNewName && nestedEntityVertex.length > 0) ||
                      (!nestedEntityNewName && nestedEntityVertex.length > 1)) {
                result = false;
                errorNames.push(name);
            }
        }, this);

        localMetaRecord.complexAttributes().each(function (item) {
            errorNames = this.checkComplexAttributeNameUnique(item.getNestedEntity(), topMetaRecord).concat(errorNames);
        }.bind(this));

        return errorNames;
    },

    /**
     * Найти все комплексные атрибуты по имени
     * @param metaRecord
     * @param name
     * @return {*}
     */
    findAllComplexAttributesByName: function (metaRecord, name) {
        var complexAttributes;

        complexAttributes = Unidata.util.MetaRecord.getAllComplexAttributes(metaRecord);
        complexAttributes = Ext.Array.filter(complexAttributes, function (complexAttribute) {
            return complexAttribute.get('name') === name;
        });

        return complexAttributes;
    },

    /**
     * Найти все комплексные атрибуты по имени
     * @param metaRecord
     * @param name
     * @return {*}
     */
    findAllNestedEntitiesByName: function (metaRecord, name) {
        var complexAttributes,
            nestedEntities;

        complexAttributes = Unidata.util.MetaRecord.getAllComplexAttributes(metaRecord);
        nestedEntities = Ext.Array.map(complexAttributes, function (complexAttribute) {
            return complexAttribute.getNestedEntity();
        });
        nestedEntities = Ext.Array.filter(nestedEntities, function (nestedEntity) {
            return nestedEntity && nestedEntity.get('name') === name;
        });

        return nestedEntities;
    },

    /**
     * Проверка что среди новых комплексных атрибутов нет дубликатов
     * @param metaRecord
     * @return {boolean}
     */
    checkPhantomComplexAttributeNameUnique: function (metaRecord) {
        var phantomComplexAttributes,
            names;

        phantomComplexAttributes = Unidata.util.MetaRecord.getAllComplexAttributes(metaRecord, true);
        names = Ext.Array.map(phantomComplexAttributes, function (attr) {
            return attr.get('name');
        });

        // если длины массивов расходятся, значит есть дубликаты
        return names.length === Ext.Array.unique(names).length;
    },

    /**
     * Ищем имя реестра (ENTITY) для NESTED_ENTITY
     *
     * Примечание: если NESTED_ENTITY входит в несколько ENTITY, то возвращаем null
     * В этом случае NESTED_ENTITY является разделяемой (shared)
     *
     * @param entityName
     * @returns {*}
     */
    findNestedEntityTopParentName: function (entityName) {
        var MetaDependencyGraph = Unidata.util.MetaDependencyGraph,
            metaDependencyGraph = this.metaDependencyGraph,
            vertexesRelated;

        vertexesRelated = MetaDependencyGraph.findVertexesRelatedToId(metaDependencyGraph, entityName, 'INBOUND', ['ENTITY', 'NESTED_ENTITY'], ['NESTED_ENTITY']);

        if (vertexesRelated.length === 1) {
            entityName = this.findNestedEntityTopParentName(vertexesRelated[0].get('id'));
        } else if (vertexesRelated.length > 1) {
            // имеется несколько путей к NESTED_ENTITY
            return null;
        }

        return entityName;
    },
    isAttributeNameUniqueEntity: function (record) {
        var result = true,
            name,
            names = [];

        record.simpleAttributes().each(function (item) {
            name = item.get('name');

            if (Ext.Array.contains(names, name)) {
                result = false;
            }

            names.push(name);
        });

        record.complexAttributes().each(function (item) {
            name = item.get('name');

            if (Ext.Array.contains(names, name)) {
                result = false;
            }

            names.push(name);
        });

        if (result !== false) {
            record.complexAttributes().each(function (item) {
                if (result !== false) {
                    result = this.isAttributeNameUniqueEntity(item.getNestedEntity());
                }
            }.bind(this));
        }

        return result;
    },

    isAttributeNameUniqueLookupEntity: function (record) {
        var result = true,
            name,
            names = [],
            codeAttribute;

        codeAttribute = record.getCodeAttribute();

        if (codeAttribute) {
            name = codeAttribute.get('name');

            names.push(name);
        }

        //TODO: refactoring me
        record.simpleAttributes().each(function (item) {
            name = item.get('name');

            if (Ext.Array.contains(names, name)) {
                result = false;
            }

            names.push(name);
        });

        if (typeof record.aliasCodeAttributes === 'function') {
            record.aliasCodeAttributes().each(function (item) {
                name = item.get('name');

                if (Ext.Array.contains(names, name)) {
                    result = false;
                }

                names.push(name);
            });
        }

        return result;
    },

    onButtonSaveRecordClick: function () {
        var MetaDependencyGraphApi = Unidata.util.api.MetaDependencyGraph,
            viewModel = this.getViewModel(),
            view = this.getView(),
            currentRecord = viewModel.get('currentRecord'),
            currentRecordName = currentRecord.get('name'),
            isPhantom = currentRecord.phantom,
            tPanel = view.lookupReference('metaTabPanel'),
            activeTab = tPanel.getActiveTab(),
            tabController = activeTab.getController(),
            recordNameExistsLookup = null,
            recordNameExistsEntity = null,
            me = this,
            forTypes = ['ENTITY', 'NESTED_ENTITY'],
            draftMode = view.getDraftMode();

        view.setStatus(Unidata.StatusConstant.LOADING);

        function storeLoadCallback (type, records) {
            var recordNameExists = false;

            Ext.Array.each(records, function (record) {
                if (record.get('name') === currentRecordName) {
                    recordNameExists = true;

                    return false;
                }
            });

            switch (type) {
                case 'lookup':
                    recordNameExistsLookup = recordNameExists;
                    break;
                case 'entity':
                    recordNameExistsEntity = recordNameExists;
                    break;
            }

            if (Ext.isBoolean(recordNameExistsLookup) && Ext.isBoolean(recordNameExistsEntity)) {
                if (recordNameExistsLookup || recordNameExistsEntity) {
                    view.setStatus(Unidata.StatusConstant.READY);
                    Unidata.showError(Unidata.i18n.t('admin.metamodel>entityOrLookupEntityWithNameExists'));
                } else {
                    MetaDependencyGraphApi.getMetaDependencyGraph(forTypes, null, draftMode).then(function (metaDependencyGraph) {
                            me.metaDependencyGraph = metaDependencyGraph;
                            me.saveRecord();
                            view.setStatus(Unidata.StatusConstant.READY);
                        },
                        function () {
                            Unidata.showError(Unidata.i18n.t('admin.common>loadDependencyGraphError'));
                            view.setStatus(Unidata.StatusConstant.NONE);
                        })
                        .done();
                }
            }
            //TODO: add failure handling
        }

        if (tabController && tabController.validateRequiredFields) {
            tabController.validateRequiredFields();
        }

        if (isPhantom || currentRecord.isModified('name')) {
            this.getStore('lookupEntities').load({
                callback: storeLoadCallback.bind(this, 'lookup'),
                params: {
                    draft: draftMode
                }
            });

            this.getStore('entities').load({
                callback: storeLoadCallback.bind(this, 'entity'),
                params: {
                    draft: draftMode
                }
            });
        } else {
            MetaDependencyGraphApi.getMetaDependencyGraph(forTypes, null, draftMode).then(function (metaDependencyGraph) {
                    me.metaDependencyGraph = metaDependencyGraph;
                    me.saveRecord();
                    view.setStatus(Unidata.StatusConstant.READY);
                },
                function () {
                    Unidata.showError(Unidata.i18n.t('admin.common>loadDependencyGraphError'));
                    view.setStatus(Unidata.StatusConstant.NONE);
                })
                .done();
        }
    },

    saveRecord: function () {
        var me = this,
            view = this.getView(),
            draftMode = view.draftMode,
            viewModel,
            record,
            i,
            result,
            metarecordAttribute,
            nestedData;

        viewModel = this.getViewModel();
        record = viewModel.get('currentRecord');
        metarecordAttribute = me.getView().down('admin\\.entity\\.metarecord\\.attribute');

        // если выбор отображаемых атрибутов в режиме редактирования, то принудительно завершаем редактирование
        metarecordAttribute.completeAllEditors();

        viewModel.set('saving', true);

        nestedData = record.getAssociatedData(null, {serialize: true, associated: true, persist: true});

        if ((result = this.checkRecordValid(record)) === true) {
            for (i in nestedData) {
                if (nestedData.hasOwnProperty(i)) {
                    record.set(i, nestedData[i]);
                }
            }

            record.setId(record.data.name);

            if (record.modified.name) {
                record.setId(record.modified.name);
            }

            Unidata.view.component.DropdownPickerField.resetMetaRecordCacheByName(record.get('name'));

            record.save({
                params: {
                    draft: draftMode
                },
                success: function () {
                    var key, association, store, resultsetGrid, parent;

                    for (key in record.associations) {
                        if (record.associations.hasOwnProperty(key)) {
                            association = record.associations[key];
                            store = record[association.getterName]();

                            if (store instanceof Ext.data.Store) {
                                store.commitChanges();
                            }
                        }
                    }

                    parent = me.getView().ownerCt.ownerCt;
                    resultsetGrid = parent.lookupReference('resultsetPanel').lookupReference('resultsetGrid');

                    resultsetGrid.saveTreeState({field: 'name'});

                    resultsetGrid.getStore().load({
                        callback: resultsetGrid.restoreTreeState.bind(resultsetGrid)
                    });
                    me.clearDirty(viewModel);

                    //обновляем propertrygrid
                    metarecordAttribute.getController().refreshAttributePropertyGrid();

                    view.lookupReference('dataQuality').commitChanges();

                    view.setStatus(Unidata.StatusConstant.READY);
                    me.showMessage(Unidata.i18n.t('admin.common>dataSaveSuccess'));
                },
                failure: function () {
                    view.setStatus(Unidata.StatusConstant.NONE);
                },
                callback: function () {
                    viewModel.set('saving', false);
                }
            });
        } else {
            view.setStatus(Unidata.StatusConstant.READY);
            Unidata.showError(result);
            viewModel.set('saving', false);
        }
    },

    /**
     * Проверяем консистентность стратегий генераций
     *
     * Выбранные атрибуты в стратегии "Объединение" должны существовать в реестре/справочнике
     * @returns {*}
     */
    checkGeneratorStrategiesConsistency: function () {
        var view                                = this.getView(),
            attributePanel                      = view.lookupReference('attributePanel'),
            propertyPanel                       = view.lookupReference('propertyPanel'),
            externalIdGenerationStrategyForm    = propertyPanel.generationStrategyForm,
            codeAttributeGenerationStrategyForm = attributePanel.generationStrategyForm,
            errorTplCodeAttr                    = Unidata.i18n.t('admin.metamodel>generationStrategy.errorTplExternalID'),
            errorTplExternalID                  = Unidata.i18n.t('admin.metamodel>generationStrategy.errorTplCodeAttr'),
            viewModel                           = this.getViewModel(),
            isLookupEntity                      = viewModel.get('isLookupEntity'),
            metaRecord                          = viewModel.get('currentRecord'),
            codeAttribute,
            attributeFiltersCodeAttr,
            attributeFiltersExternalId,
            errorMsgCodeAttr = true,
            errorMsgExternalId,
            errorMsgs  = [];

        attributeFiltersExternalId = attributePanel.buildGenerationStrategyAttributeFilters();
        externalIdGenerationStrategyForm.fillDisplayAttributeStore(attributeFiltersExternalId);
        errorMsgExternalId = this.checkGeneratorStrategyConsistency(externalIdGenerationStrategyForm, errorTplExternalID);

        if (isLookupEntity) {
            codeAttribute = metaRecord.getCodeAttribute();

            if (codeAttribute.get('simpleDataType') === 'String') {
                attributeFiltersCodeAttr = propertyPanel.buildGenerationStrategyAttributeFilters();
                codeAttributeGenerationStrategyForm.fillDisplayAttributeStore(attributeFiltersCodeAttr);
                errorMsgCodeAttr = this.checkGeneratorStrategyConsistency(codeAttributeGenerationStrategyForm, errorTplCodeAttr);
            }
        }

        if (errorMsgCodeAttr !== true) {
            if (view.hideAttributeTab) {
                errorMsgs.push(Unidata.i18n.t('admin.metamodel>invalidAttributeSettingsAskAdministrator'));
            } else {
                errorMsgs.push(errorMsgCodeAttr);
            }
        }

        if (errorMsgExternalId !== true) {
            if (view.hidePropertyTab) {
                errorMsgs.push(Unidata.i18n.t('admin.metamodel>invalidPropertySettings'));
            } else {
                errorMsgs.push(errorMsgExternalId);
            }
        }

        return errorMsgs.length ? errorMsgs : true;
    },

    checkGeneratorStrategyConsistency: function (generationStrategyForm, errorTpl) {
        var type = generationStrategyForm.getType(),
            diff,
            errorMsg;

        if (type !== Unidata.model.entity.GenerationStrategy.generationStrategyType.CONCAT.value) {
            return true;
        }

        diff = generationStrategyForm.calcAttributesDiff();

        if (diff.length > 0) {
            errorMsg = Ext.String.format(errorTpl, diff.join(', '));

            return errorMsg;
        } else {
            return true;
        }
    },

    onDeleteConfirmClick: function (btn) {
        var msgBox = Ext.window.MessageBox.create({});

        msgBox.show({
            title: Unidata.i18n.t('admin.metamodel>removingEntityOrLookupEntity'),
            message: Unidata.i18n.t('admin.metamodel>confirmRemoveEntityOrLookupEntity'),
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: Unidata.i18n.t('common:yes'),
                no: Unidata.i18n.t('common:no')
            },
            scope: this,
            animateTarget: btn,
            defaultFocus: 3,
            fn: function (btn) {
                if (btn === 'yes') {
                    this.deleteRecord();
                }
            }
        });
    },

    deleteRecord: function () {
        var view = this.getView(),
            record = this.getViewModel().get('currentRecord'),
            resultsetGrid = this.getView().ownerCt.ownerCt.lookupReference('resultsetPanel').lookupReference('resultsetGrid'),
            draftMode = view.getDraftMode();

        function clean () {
            this.getView().close();
            resultsetGrid.getStore().load();
        }

        if (!record.phantom) {
            record.erase({
                params: {
                    draft: draftMode
                },
                success: function (r) {
                    clean.call(this, r);
                },
                scope: this
            });
        } else {
            clean.call(this, record);
        }
    },

    onServerException: function () {
        this.closeView();
    },

    onActivateConsolidationTab: function (tab) {
        var tabController = tab.getController();

        tabController.updateUnselectedBvtAttributeStore();
    },

    onActivatePresentationTab: function (tab) {
        var tabController = tab.getController();

        tabController.initAttributes();
    },

    onLoadAllStoreAttributeTab: function () {
        var viewModel = this.getViewModel();

        viewModel.set('tabAttributeDone', true);
    },

    onLoadAllStoreConsolidationTab: function () {
        var viewModel = this.getViewModel();

        viewModel.set('tabConsolidationDone', true);
    },

    onLoadAllStoreProperty: function () {
        var viewModel = this.getViewModel();

        viewModel.set('tabPropertyDone', true);
    },

    updateDraftMode: function (draftMode) {
        this.syncDraftMode(draftMode);
    },

    syncDraftMode: function (draftMode) {
        var viewModel = this.getViewModel();

        if (viewModel) {
            viewModel.set('draftMode', draftMode);
        }
    },

    onOpenDraftButtonClick: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            currentRecord = viewModel.get('currentRecord'),
            entityName = currentRecord.get('name'),
            entityType = 'Entity';

        if (Unidata.util.MetaRecord.isLookup(currentRecord)) {
            entityType = 'LookupEntity';
        }

        view.fireEvent('opendraft', entityName, entityType);
    },

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);
        viewModel.set('metaRecordViewReadOnly', readOnly);
    },

    updateReadOnlyTrigger: function () {
        var view = this.getView(),
            readOnly = view.getReadOnly(),
            lookupReference;

        lookupReference = this.lookupReference('attributePanel');

        if (!lookupReference) {
            return;
        }
        lookupReference.setReadOnly(readOnly);
    }
});
