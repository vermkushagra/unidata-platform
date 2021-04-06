Ext.define('Unidata.view.admin.entity.metarecord.MetaRecordController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord',

    metaDependencyGraph: null,

    onJsonModeClick: function () {
        var viewModel = this.getViewModel(),
            currentRecord = viewModel.get('currentRecord');

        new Ext.window.Window({
            autoShow: true,
            title: Unidata.i18n.t('admin.metamodel>jsonModel'),
            modal: false,
            width: 800,
            height: 400,
            layout: 'fit',
            items: {
                xtype: 'textarea',
                readOnly: true,
                value: JSON.stringify(currentRecord.getData(true), null, 4)
            }
        });
    },

    init: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            dataQualityPanel = this.lookupReference('dataQualityPanel');

        viewModel.getStore('lookupEntities').getProxy().on('endprocessresponse', this.onEndProcessResponse, this);
        viewModel.getStore('entities').getProxy().on('endprocessresponse', this.onEndProcessResponse, this);

        viewModel.bind('{currentRecord}', this.updateAttributesStore, this);
        viewModel.bind('{readOnly}', this.updateReadOnlyTrigger, this);

        // Флаг загрузки данных правил качества
        dataQualityPanel.getViewModel().bind('{dqLoading}', function (dqLoading) {
            viewModel.set('dqLoading', dqLoading);
        });

        this.getStore('attributesStore').refreshDataFromMetaRecord = this.updateAttributesStore.bind(this);

        this.syncDraftMode(view.getDraftMode());
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
        var dataQualityPanel = this.lookupReference('dataQualityPanel'),
            errorMsg,
            errorMsgs,
            isLookupEntity,
            attributePanel = this.getView().lookupReference('attributePanel'),
            propertyPanel = this.getView().lookupReference('propertyPanel'),
            externalIdGenerationStrategyForm = propertyPanel.generationStrategyForm,
            codeAttribute,
            codeAttributeGenerationStrategyForm = attributePanel.generationStrategyForm,
            result;

        errorMsgs = [];

        if (!record.isValid()) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidPropertySettings')]);

            propertyPanel.getController().validateRequiredFields();

            return errorMsgs;
        }

        if (!externalIdGenerationStrategyForm.validateAllFields()) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>generationStrategy.invalidExternalIdGeneration')]);

            return errorMsgs;
        }

        isLookupEntity = this.getViewModel().get('isLookupEntity');

        if (isLookupEntity) {
            codeAttribute = record.getCodeAttribute();

            if (!codeAttribute ||
                this.checkAttributesValid('codeAttribute', record) !== true) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>unefinedCodeAttribute')]);

                return errorMsgs;
            }

            if (!codeAttributeGenerationStrategyForm.validateAllFields()) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>generationStrategy.invalidCodeAttributeGeneration')]);

                return errorMsgs;
            }
        }

        if ((errorMsg = this.checkGeneratorStrategiesConsistency()) !== true) {
            return errorMsg;
        }

        if (attributePanel.isErrors) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>badAttributes')]);

            return errorMsgs;
        }

        if (this.checkAttributesValid('simpleAttributes', record) !== true) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>badAttributes')]);

            return errorMsgs;
        }

        if (!isLookupEntity) {
            if (this.checkAttributesValid('complexAttributes', record) !== true) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>badAttributes')]);

                return errorMsgs;
            }

            if (!this.checkRelation()) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidRelations')]);

                return errorMsgs;
            }

            if (!this.checkConsolidationValid()) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>invalidConsolidationSettings')]);

                return errorMsgs;
            }

            if (!this.checkComplexAttributeNameUnique(record)) {
                errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>complexAttributeAlreadyExists')]);
            }
        }

        if ((result = dataQualityPanel.checkDataQualityValid(record)) !== true) {
            errorMsgs = Ext.Array.merge(errorMsgs, result);

            return errorMsgs;
        }

        if (!this.checkSimpleAttributes(record, 'displayable')) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>notSetDisplayedAttribute')]);

            return errorMsgs;
        }

        if (!this.checkSimpleAttributes(record, 'mainDisplayable')) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>notSetMainDisplayedAttribute')]);

            return errorMsgs;
        }

        if (!this.checkSimpleAttributes(record, 'searchable')) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>netSetSearchAttribute')]);

            return errorMsgs;
        }

        if (!this.checkAttributeNameUnique(record)) {
            errorMsgs = Ext.Array.merge(errorMsgs, [Unidata.i18n.t('admin.metamodel>attributeNamesNotUnique')]);
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
     * @param record {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity} Метамодель
     * @returns {boolean} Признак успешного прохождения проверки
     */
    checkComplexAttributeNameUnique: function (record) {
        var MetaDependencyGraphUtil = Unidata.util.MetaDependencyGraph,
            MetaRecordUtil = Unidata.util.MetaRecord,
            VertexType = Unidata.model.entity.metadependency.Vertex.type,
            metaDependencyGraph = this.metaDependencyGraph,
            names = [],
            result = true,
            viewModel = this.getViewModel(),
            entityMetaRecord = viewModel.get('currentRecord'),
            currentEntityName = entityMetaRecord.get('name'),
            isPhantom = entityMetaRecord.phantom;

        if ((!MetaRecordUtil.isEntity(record) && !MetaRecordUtil.isNested(record)) || !metaDependencyGraph) {
            return true;
        }

        record.complexAttributes().each(function (item) {
            var name = item.get('name'),
                nestedEntityVertex,
                topEntityName,
                isSharedNestedEntity;

            nestedEntityVertex = MetaDependencyGraphUtil.findVertex(metaDependencyGraph, name, VertexType.NESTED_ENTITY);
            topEntityName = this.findNestedEntityTopParentName(name);
            isSharedNestedEntity = topEntityName === null;

            // если создаем новую метамодель, то проверяем не существует ли NESTED_ENTITY с таким же именем среди имеющихся в системе
            // если редактируем существующую метамодель, то проверяем не существует ли NESTED_ENTITY с таким же именем среди имеющихся в системе, если такая NESTED_ENTITY не является разделяемой
            if (((!isPhantom && !isSharedNestedEntity) || isPhantom) &&
                topEntityName !== currentEntityName &&
                nestedEntityVertex) {
                result = false;
            }

            names.push(name);
        }, this);

        if (result !== false) {
            record.complexAttributes().each(function (item) {
                if (result !== false) {
                    result = this.checkComplexAttributeNameUnique(item.getNestedEntity());
                }
            }.bind(this));
        }

        return result;
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

    /**
     * Обновляет store аттрибутов, который будет использоваться в правилах качества и поиске дубликатов
     */
    updateAttributesStore: function () {

        var currentRecord = this.getViewModel().get('currentRecord'),
            result = [];

        function formatDisplayName (attr) {
            return '<span class="un-data-quality-htmlcombo-treeItem">' +
                Ext.String.htmlEncode(attr.get('displayName')) +
                '</span>';
        }

        function buildLevel (nestedEntity, level, viewLevel) {
            var attr;

            function buildAttr (attr) {
                result.push({
                    path: level + attr.get('name'),
                    displayName: viewLevel + formatDisplayName(attr)
                });
            }

            if (Ext.isFunction(nestedEntity.getCodeAttribute)) {
                attr = nestedEntity.getCodeAttribute();

                if (attr) {
                    // при создании справочника кодового аттрибута может и не быть
                    buildAttr(attr);
                }
            }

            if (Ext.isFunction(nestedEntity.aliasCodeAttributes)) {
                nestedEntity.aliasCodeAttributes().each(buildAttr);
            }

            nestedEntity.simpleAttributes().each(buildAttr);

            if (Ext.isFunction(nestedEntity.complexAttributes)) {

                nestedEntity.complexAttributes().each(function (attr) {

                    var path = level + attr.get('name'),
                        displayName = viewLevel + formatDisplayName(attr);

                    result.push({
                        path: path,
                        displayName: displayName
                    });

                    buildLevel(attr.getNestedEntity(), path + '.', displayName + ' &gt; ');

                });

            }
        }

        if (currentRecord) {
            buildLevel(currentRecord, '', '');
        }

        this.getStore('attributesStore').setData(result);

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
            forTypes = ['ENTITY', 'NESTED_ENTITY'];

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
                    MetaDependencyGraphApi.getMetaDependencyGraph(forTypes).then(function (metaDependencyGraph) {
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
            this.getStore('lookupEntities').load(storeLoadCallback.bind(this, 'lookup'));
            this.getStore('entities').load(storeLoadCallback.bind(this, 'entity'));
        } else {
            MetaDependencyGraphApi.getMetaDependencyGraph(forTypes).then(function (metaDependencyGraph) {
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
            dqController,
            i,
            result,
            metarecordAttribute,
            editor;

        viewModel = this.getViewModel();
        record = viewModel.get('currentRecord');
        dqController = new Unidata.view.admin.entity.metarecord.dataquality.DataQualityController();
        metarecordAttribute = me.getView().down('admin\\.entity\\.metarecord\\.attribute');

        // если выбор отображаемых атрибутов в режиме редактирования, то принудительно завершаем редактирование
        editor = metarecordAttribute.getLookupEntityDisplayAttributesEditor();

        if (editor.editing) {
            editor.completeEdit();
        }

        viewModel.set('saving', true);

        // set raise/enrich to null if isisValidation/isEnrichment are false
        record.dataQualityRules().each(function (dqrule) {
            dqController.cleanDqRule(dqrule);
            dqController.updateApplicable(dqrule);
        });

        //TODO: Why use getAssociatedData ??? Have problems with persist field here
        var nestedData = record.getAssociatedData(null, {serialize: true, associated: true});

        nestedData.dataQualityRules.forEach(function (dqRule) {

            if (Boolean(dqRule.raise)) {
                if (dqRule.raise.messagePort) {
                    dqRule.raise.messageText = '';
                }

                if (dqRule.raise.severityPort) {
                    dqRule.raise.severityValue = '';
                }

                if (dqRule.raise.categoryPort) {
                    dqRule.raise.categoryText = '';
                }
            }
        });

        //hack for dataQualityRules.origins.sourceSystems: create array
        function fillSourceSystem (sourceSystem) {
            dataQualityRuleDest.origins.sourceSystems.push(sourceSystem.data);
        }

        //TODO: remove hack
        if (nestedData.dataQualityRules !== undefined) {
            var dataQualityRuleDest, dataQualityRuleSrc;

            for (i = 0; i < nestedData.dataQualityRules.length; i++) {
                if (nestedData.dataQualityRules[i].origins !== undefined &&
                    nestedData.dataQualityRules[i].origins !== null) {
                    dataQualityRuleDest = nestedData.dataQualityRules[i];
                    dataQualityRuleDest.origins.sourceSystems = [];

                    dataQualityRuleSrc = record.dataQualityRules().getAt(i);
                    dataQualityRuleSrc.getOrigins().sourceSystems().each(fillSourceSystem);
                }
            }
        }

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
                    resultsetGrid.getStore().load();
                    me.clearDirty(viewModel);

                    //обновляем propertrygrid
                    metarecordAttribute.getController().refreshAttributePropertyGrid();

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
            errorMsgs.push(errorMsgCodeAttr);
        }

        if (errorMsgExternalId !== true) {
            errorMsgs.push(errorMsgExternalId);
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

    onLoadAllStoreAttributeTab: function () {
        var viewModel = this.getViewModel();

        viewModel.set('tabAttributeDone', true);
    },

    onLoadAllStoreDqTab: function () {
        var viewModel = this.getViewModel();

        viewModel.set('tabDqDone', true);
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
            readOnly = view.getReadOnly();

        this.lookupReference('attributePanel').setReadOnly(readOnly);
    }
});
