Ext.define('Unidata.view.admin.entity.metarecord.attribute.AttributeModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.attribute',

    enableExperimentalOverride: true,

    data: {
        readOnly: false,
        allowOnlySimpleType: false,
        lookupEntityRecord: null,
        attributeSimpleDataType: null
    },

    stores: {
        lookupEntities: {
            model: 'Unidata.model.entity.LookupEntity',
            pageSize: 10000,
            autoLoad: false
        },
        entities: {
            model: 'Unidata.model.entity.Entity',
            pageSize: 10000,
            autoLoad: false
        },
        simpleDataTypes: {
            fields: ['name', 'displayName'],
            data: Unidata.Constants.getSimpleDataTypes()
        },
        typeCategories: {
            fields: ['name', 'value'],
            data: [] // заполняется в controller в зависимости от типа атрибута
        },
        arrayDataTypes: {
            fields: ['name', 'displayName'],
            data: Unidata.Constants.getArrayDataTypes()
        },
        cacheMeasurementValues: {
            type: 'un.measurementvalues'
        },
        measurementValues: {
            fields: [
                {
                    name: 'id',
                    type: 'string'
                },
                'name',
                'units'
            ],
            data: []
        },
        measurementUnits: {
            fields: [
                {
                    name: 'id',
                    type: 'string'
                },
                'name',
                'shortName',
                {
                    name: 'displayName',
                    depends: ['name', 'shortName'],

                    convert: function (v, rec) {
                        return rec.get('name') + ' (' + rec.get('shortName') + ')';
                    }
                }
            ],
            data: []
        }
    },

    formulas: {
        metaAttribute: {
            bind: {
                bindTo: '{attributeTreePanel.selection}',
                deep: true
            },
            get: function (selection) {
                var metaAttribute;

                if (selection) {
                    metaAttribute = selection.get('record');

                    if (metaAttribute) {
                        return metaAttribute;
                    }
                }

                return null;
            }
        },
        isAttributeSelected: {
            bind: {
                bindTo: '{attributeTreePanel.selection}',
                deep: true
            },
            get: function (record) {
                return record && !record.isRoot();
            }
        },
        isCurrentAttributeCanDeleted: {
            bind: {
                readOnly: '{readOnly}',
                attribute: '{attributeTreePanel.selection}',
                metaRecord: '{currentRecord}'
            },
            get: function (getter) {
                var result = true,
                    attributeNode = getter.attribute,
                    metaRecord = getter.metaRecord,
                    attribute,
                    isCodeAttribute;

                if (getter.readOnly) {
                    return false;
                }

                if (!attributeNode || !metaRecord) {
                    return false;
                }

                // если атрибут только создан то его можно удалять
                if (attributeNode.phantom) {
                    return true;
                }

                attribute = attributeNode.get('record');

                isCodeAttribute = attribute instanceof Unidata.model.attribute.CodeAttribute;

                if (attributeNode.isRoot() || (isCodeAttribute && metaRecord.get('hasData'))) {
                    result = false;
                }

                return result;
            }
        },
        currentAttributeEditForm: {
            bind: {
                bindTo: '{attributeTreePanel.selection}',
                deep: true
            },
            get: function (attributeTreeItem) {
                var view,
                    controller,
                    data,
                    source                      = null,
                    simpleAttributeFieldsGroup1 = ['nullable', 'readOnly', 'hidden'],
                    simpleAttributeAddonGroup2  = ['searchable', 'displayable', 'mainDisplayable'],
                    blobOrClobAttributeAddonGroup2 = ['searchable', 'displayable'],
                    complexAttributeFieldsGroup = [/*'readOnly',*/ 'hidden'], // для комплексных атрибутов временно не доступен ReadOnly см UN-2652 и описание проблематики UN-2654
                    hiddenAttributeFields       = this.getView().hiddenAttributeFields,
                    lookupEntityRecord          = this.get('lookupEntityRecord'),
                    record;

                view = this.getView();

                if (view) {
                    controller = view.getController();
                }

                if (!attributeTreeItem) {
                    return;
                }

                record = attributeTreeItem.get('record');

                if (!record) {
                    return source;
                }

                function isBlobOrClobAttribute (record) {
                    return record.isSimpleDataType() &&
                        record.get('simpleDataType') === 'Blob' || record.get('simpleDataType') === 'Clob';
                }

                function isLinkDataTypeAttribute (record) {
                    return record.isLinkDataType();
                }

                simpleAttributeFieldsGroup1 = Ext.Array.difference(simpleAttributeFieldsGroup1, hiddenAttributeFields);
                simpleAttributeAddonGroup2 = Ext.Array.difference(simpleAttributeAddonGroup2, hiddenAttributeFields);
                complexAttributeFieldsGroup = Ext.Array.difference(complexAttributeFieldsGroup, hiddenAttributeFields);

                data = record.getData();
                source = {
                    name: data.name,
                    displayName: data.displayName,
                    description: data.description
                };

                // фейковое значение, для отображения количества значений
                source.customProperties = Ext.String.format(
                    Unidata.i18n.t('component>form.field.gridvalues.givenValues'),
                    record.customProperties().getCount()
                );

                if (record instanceof Unidata.model.attribute.SimpleAttribute ||
                    record instanceof Unidata.model.attribute.CodeAttribute ||
                    record instanceof Unidata.model.attribute.AliasCodeAttribute) {

                    if (record instanceof Unidata.model.attribute.SimpleAttribute &&
                        !isBlobOrClobAttribute(record) && !isLinkDataTypeAttribute(record) &&
                        attributeTreeItem.get('parentId') === 'root') {
                        if (!Ext.Array.contains(hiddenAttributeFields, 'unique')) {
                            source['unique'] = data['unique'];
                        }
                    }

                    if (record instanceof Unidata.model.attribute.CodeAttribute ||
                        record instanceof Unidata.model.attribute.AliasCodeAttribute) {
                        if (!Ext.Array.contains(hiddenAttributeFields, 'unique')) {
                            source['unique'] = true;
                        }
                    }

                    // для поля типа "Ссылка на веб-ресурс" свойства "Обязательное", "Только для чтения" не имеют смысла
                    if (!isLinkDataTypeAttribute(record)) {
                        simpleAttributeFieldsGroup1.forEach(function (name) {
                            source[name] = data[name];
                        });
                    } else {
                        if (!Ext.Array.contains(hiddenAttributeFields, 'hidden')) {
                            source['hidden'] = data['hidden'];
                        }
                    }

                    if (!isBlobOrClobAttribute(record) && !isLinkDataTypeAttribute(record)) {
                        simpleAttributeAddonGroup2.forEach(function (name) {
                            source[name] = data[name];
                        });
                    } else if (isBlobOrClobAttribute(record)) {
                        blobOrClobAttributeAddonGroup2.forEach(function (name) {
                            source[name] = data[name];
                        });
                    }

                    if (record instanceof Unidata.model.attribute.SimpleAttribute) {
                        source.typeCategory = record.get('typeCategory');
                        source[source.typeCategory] = data[source.typeCategory];
                    } else {
                        source.simpleDataType = data.simpleDataType;
                    }

                    if (data.simpleDataType === 'String') {
                        // показываем маску если простой строковый тип
                        source.mask = data.mask;
                        // если для строкового атрибута дополнительная натройка - морфологический поиск
                        source.searchMorphologically = data.searchMorphologically;
                    }

                    // если численное то отображаем величину измерения и единицу измерения
                    if (data.simpleDataType === 'Number') {
                        source.valueId = data.valueId;
                        source.defaultUnitId = data.defaultUnitId;
                    }
                } else if (record instanceof Unidata.model.attribute.ComplexAttribute) {
                    source.minCount = data.minCount;
                    source.maxCount = data.maxCount;

                    complexAttributeFieldsGroup.forEach(function (name) {
                        source[name] = data[name];
                    });

                    //disabled until next releases
                    //source.nestedEntityType = data.nestedEntityType;

                    // ключ вложенной сущности пока не нужен UN-1825
                    // source.subEntityKeyAttribute = data.subEntityKeyAttribute;
                } else if (record instanceof Unidata.model.attribute.ArrayAttribute) {
                    source['hidden'] = data['hidden'];
                    source['nullable'] = data['nullable'];
                    source['readOnly'] = data['readOnly'];
                    source['searchable'] = data['searchable'];
                    source['typeCategory'] = data['typeCategory'];
                    source['exchangeSeparator'] = data['exchangeSeparator'];

                    if (data['typeCategory'] === 'arrayDataType') {
                        if (data['arrayDataType']) {
                            source['arrayDataType'] = data['arrayDataType'];
                        }
                    } else if (data['typeCategory'] === 'lookupEntityType') {
                        source['lookupEntityType'] = data['lookupEntityType'];
                        source['lookupEntityDisplayAttributes'] = data['lookupEntityDisplayAttributes'];
                        source['lookupEntitySearchAttributes'] = data['lookupEntitySearchAttributes'];
                        source['useAttributeNameForDisplay'] = data['useAttributeNameForDisplay'];
                    }

                    if (data.arrayDataType === 'String') {
                        // показываем маску если простой строковый тип
                        source.mask = data.mask;
                        // если для строкового атрибута дополнительная натройка - морфологический поиск
                        source.searchMorphologically = data.searchMorphologically;
                    }
                }

                if (record instanceof Unidata.model.attribute.SimpleAttribute ||
                    record instanceof Unidata.model.attribute.CodeAttribute ||
                    record instanceof Unidata.model.attribute.AliasCodeAttribute ||
                    record instanceof Unidata.model.attribute.ArrayAttribute) {

                    if (data.typeCategory === 'lookupEntityType') {
                        if (!lookupEntityRecord || lookupEntityRecord.get('name') !== source.lookupEntityType) {
                            view.changeLookupMetaRecord({
                                entityName: source.lookupEntityType,
                                entityType: 'LookupEntity',
                                draft: view.draftMode
                            });
                        }
                        source.lookupEntityDisplayAttributes = data.lookupEntityDisplayAttributes;
                        source.lookupEntitySearchAttributes = data.lookupEntitySearchAttributes;
                        source.useAttributeNameForDisplay = data.useAttributeNameForDisplay;
                    }
                }

                if (this.get('allowOnlySimpleType')) {
                    delete source.typeCategory;

                    source.simpleDataType = data.simpleDataType;
                }

                // упорядочиваем порядок свойств
                if (controller) {
                    source = controller.sortPropertyGridSource(source);
                }

                return source;
            }
        },
        isPreviousSiblingExists: {
            bind: {
                selection: '{attributeTreePanel.selection}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var record = getter.selection;

                if (getter.readOnly) {
                    return false;
                }

                return record && record.previousSibling !== null && record.previousSibling !== undefined;
            }
        },
        isNextSiblingExists: {
            bind: {
                selection: '{attributeTreePanel.selection}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var record = getter.selection;

                if (getter.readOnly) {
                    return false;
                }

                return record && record.nextSibling !== null && record.nextSibling !== undefined;
            }
        },
        // используется для поля "Ключ вложенной сущности"
        // TODO: расширить для случая альтернативных кодовых атрибутов, если необходимо
        childSimpleAttributes: {
            bind: {
                bindTo: '{attributeTreePanel.selection.record}',
                deep: true
            },
            get: function (record) {
                var store = Ext.create('Ext.data.Store', {
                    model: 'Unidata.model.attribute.SimpleAttribute',
                    proxy: {
                        type: 'memory'
                    }
                });

                if (record !== null &&
                    record instanceof Unidata.model.attribute.ComplexAttribute &&
                    record.getNestedEntity() !== null &&
                    typeof record.getNestedEntity().simpleAttributes === 'function' &&
                    record.getNestedEntity().simpleAttributes().count() > 0) {
                    store = record.getNestedEntity().simpleAttributes();
                }

                return store;
            }
        },
        isComplexAttributesHidden: {
            bind: {
                bindTo: '{isLookupEntity}',
                deep: true
            },
            get: function (isLookupEntity) {
                var isComplexAttrHidden;

                isComplexAttrHidden = this.getView().isComplexAttributesHidden;

                return Boolean(isLookupEntity) || Boolean(isComplexAttrHidden);
            }
        },
        isArrayAttributesHidden: {
            get: function () {
                var view = this.getView(),
                    hidden;

                hidden = view.isArrayAttributesHidden;

                return Ext.coalesceDefined(hidden, false);
            }
        },
        isSimpleOrCodeAttributeSelected: {
            bind: {
                bindTo: '{attributeTreePanel.selection.record}',
                deep: true
            },
            get: function (record) {
                return record &&
                    (record instanceof Unidata.model.attribute.SimpleAttribute ||
                    record instanceof Unidata.model.attribute.CodeAttribute ||
                    record instanceof Unidata.model.attribute.AliasCodeAttribute);
            }
        },
        isCodeAttributeExists: {
            bind: {
                bindTo: '{attributeTreePanel.selection.record}',
                deep: true
            },
            get: function () {
                var record = this.get('currentRecord');

                return record &&
                    record instanceof Unidata.model.entity.LookupEntity &&
                    record.getCodeAttribute() !== null &&
                    record.getCodeAttribute() !== undefined ;
            }
        },
        isCodeAttributeDisabled: {
            bind: {
                bindTo: '{attributeTreePanel.selection.record}',
                deep: true
            },
            get: function () {
                var isCodeAttributeExists, isSimpleOrCodeAttributeSelected;

                isCodeAttributeExists = this.get('isCodeAttributeExists');
                isSimpleOrCodeAttributeSelected = this.get('isSimpleOrCodeAttributeSelected');

                return isCodeAttributeExists || isSimpleOrCodeAttributeSelected;
            }
        },
        isCodeAttributeStringSelected: {
            bind: {
                attribute: '{attributeTreePanel.selection.record}',
                simpleDataType: '{attributeSimpleDataType}'
            },
            get: function (getter) {
                var attribute = getter.attribute,
                    simpleDataType = getter.simpleDataType,
                    isCodeAttribute,
                    isString;

                if (!attribute) {
                    return false;
                }

                isCodeAttribute = Unidata.util.MetaAttribute.isCodeAttribute(attribute);
                isString = simpleDataType === 'String';

                return isCodeAttribute && isString;
            }
        }
    }
});
