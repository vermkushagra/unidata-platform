/**
 * @class: AttributeController
 */

Ext.define('Unidata.view.admin.entity.metarecord.attribute.AttributeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.entity.metarecord.attribute',
    attributeProperty: Unidata.util.MetaAttribute.ATTRIBUTE_PROPERTY,

    focusPropertyGridTask: null,

    init: function () {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            draftMode = view.draftMode,
            lookupEntitiesStore = viewModel.getStore('lookupEntities'),
            entitiesStore = viewModel.getStore('entities'),
            stores;

        stores = [
            lookupEntitiesStore,
            entitiesStore
        ];

        Ext.Array.each(stores, function (store) {
            store.getProxy().on('endprocessresponse', me.onEndProcessResponse, me);

            store.load({
                params: {
                    draft: draftMode
                }
            });
        });

        viewModel.bind('{currentAttributeEditForm}', this.onPropertyGripSourceChange, this, {deep: true});

        this.updateMeasurementValuesStore();

        this.initDelayedTask();
    },

    /**
     * Инициализирует отложеные задачи
     */
    initDelayedTask: function () {
        this.focusPropertyGridTask = Ext.create('Ext.util.DelayedTask');
    },

    /**
     * Запуск отложенной задачи на фокусирование записи в редакторе свойств
     *
     * @param delay - задержка перед запуском в миллисекундах
     */
    runFocusPropertyGridDelayedTask: function (delay, recordId, enableEditor) {
        var delayedTask = this.focusPropertyGridTask;

        delayedTask.cancel();
        delayedTask.delay(delay, this.focusPropertyGridRowByName, this, [recordId, enableEditor]);
    },

    onEndProcessResponse: function (proxy, options, operation) {
        if (options.request.options.action === 'read' && operation.getError()) {
            this.fireViewEvent('serverexception');
        }
    },

    /**
     * Отобразить в редакторе отображаемых атрибутов текст по умолчанию (список гл. отобр)
     * @param defaultValueText Текст
     */
    fillLookupEntityDefaultValueText: function (defaultValueText) {
        var tagfield, editor;

        editor = this.getLookupEntityDisplayAttributesEditor();
        tagfield = editor.field;

        if (editor instanceof Ext.Editor) {
            tagfield = editor.field;
            tagfield.setDefaultValueText(defaultValueText);
        } else if (Ext.isObject(editor)) {
            editor.defaultValueText = defaultValueText;
        }
    },

    getMaskHelpTooltip: function () {
        var tooltip;

        tooltip = Unidata.i18n.t('admin.metamodel>maskTooltip.description') +
            '<ul>' +
            '<li>9 - ' + Unidata.i18n.t('admin.metamodel>maskTooltip.number') + '</li>' +
            '<li>L - ' + Unidata.i18n.t('admin.metamodel>maskTooltip.capitalLetter') + '</li>' +
            '<li>l - ' + Unidata.i18n.t('admin.metamodel>maskTooltip.lowercaseLetter') + '</li>' +
            '<li>A - ' + Unidata.i18n.t('admin.metamodel>maskTooltip.alphanumeric') + '</li>' +
            '<li>X - ' + Unidata.i18n.t('admin.metamodel>maskTooltip.regexp') + '</li>' +
            '</ul>' +
            '<br>' + Unidata.i18n.t('admin.metamodel>maskTooltip.example1') +
            '<br>' + Unidata.i18n.t('admin.metamodel>maskTooltip.example2');

        return tooltip;
    },

    getExchangeSeparatorHelpTooltip: function () {
        var tooltip;

        tooltip = Unidata.i18n.t('admin.metamodel>exchangeSeparatorTooltip.msg') +
            '<br><br>' + Unidata.i18n.t('admin.metamodel>exchangeSeparatorTooltip.example') +
            '<br><br>' + Unidata.i18n.t('admin.metamodel>exchangeSeparatorTooltip.excel') +
            '<br><br>' + Unidata.i18n.t('admin.metamodel>exchangeSeparatorTooltip.view');

        return tooltip;
    },

    /**
     * Сгенерировать текст по умолчанию для редактора атрибутов
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity}
     * @param property {String} свойство по которому строим текст по умолчанию (mainDisplayable, searchable)
     * @returns {String}
     */
    buildLookupEntityDefaultValueText: function (metaRecord, property) {
        var MetaAttributeFormatter = Unidata.util.MetaAttributeFormatter,
            displayNames,
            defaultValueText;

        if (!metaRecord) {
            return '';
        }

        displayNames = MetaAttributeFormatter.getAttributesDisplayNamesByProperty(metaRecord, property);
        defaultValueText = displayNames.join(', ');

        return defaultValueText;
    },

    buildAttributePropertyGridCfg: function (customCfg) {
        var me = this,
            view = this.getView(),
            draftMode = view.draftMode,
            storeLoadCfg,
            enumDataTypeEditorStore,
            cfg;

        enumDataTypeEditorStore = Ext.createByAlias('store.un.enumeration');

        storeLoadCfg = {
            params: {
                draft: draftMode
            }
        };
        Unidata.util.api.Enumeration.loadStore(enumDataTypeEditorStore, true, storeLoadCfg);

        cfg =  {
            xtype: 'propertygrid',
            reference: 'attributePropertyGrid',
            sortableColumns: false,
            cls: 'attribute-property-grid',
            scrollable: 'vertical',
            bind: {
                source: '{currentAttributeEditForm}',
                hidden: '{!isAttributeSelected}'
            },
            nameColumnWidth: 235,
            hideHeaders: true,
            viewConfig: {
                listeners: {
                    refresh: function () {
                        var elements = this.all.elements,
                            i,
                            rowNameCell;

                        for (i in elements) {
                            if (!elements.hasOwnProperty(i)) {
                                continue;
                            }

                            rowNameCell = Ext.get(elements[i].rows[0].cells[0]);
                            rowNameCell.addCls('un-attribute-property-title');

                            rowNameCell = Ext.get(elements[i].rows[0].cells[1]);
                            rowNameCell.addCls('un-attribute-property-value');
                        }
                    },
                    render: function (view) {
                        view.tip = Ext.create('Ext.tip.ToolTip', {
                            target: view.el,
                            delegate: '.un-attribute-property-title',
                            trackMouse: true,
                            hideDelay: 0,
                            renderTo: Ext.getBody(),
                            listeners: {
                                beforeshow: function (tip) {
                                    var record = view.getRecord(tip.triggerElement),
                                        sourceConfig = view.grid.sourceConfig[record.getId()];

                                    if (sourceConfig && sourceConfig.tooltip) {
                                        tip.update(sourceConfig.tooltip);
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        });

                        view.tipValue = Ext.create('Ext.tip.ToolTip', {
                            target: view.el,
                            delegate: '.un-attribute-property-value .x-grid-cell-inner',
                            trackMouse: true,
                            hideDelay: 0,
                            renderTo: Ext.getBody(),
                            listeners: {
                                beforeshow: function (tip) {
                                    var trigger = tip.triggerElement,
                                        record = view.getRecord(trigger),
                                        sourceConfig = view.grid.sourceConfig[record.getId()];

                                    if (sourceConfig && sourceConfig.valueTooltip && trigger.scrollWidth > trigger.offsetWidth) {
                                        tip.update(record.get('value'));
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        });
                    }
                },
                getRowClass: function (record) {
                    var delimiterFields = ['description', 'hidden', 'mainDisplayable', 'maxCount'],
                        cls             = [],
                        prefix = 'un-attribute-row-',
                        canEditProperty,
                        attributeView,
                        propName = record.get('name');

                    // всегда добавлять имя css-класса, содержащее имя атрибута
                    if (Ext.isString(propName)) {
                        cls.push(prefix + propName.toLowerCase());
                    }

                    attributeView   = this.grid.up('admin\\.entity\\.metarecord\\.attribute');
                    canEditProperty = attributeView.getController().isAttributePropertyEditable(record.get('name'));

                    if (Ext.Array.contains(delimiterFields, record.get('name'))) {
                        cls.push('group-delimiter-row');
                    }

                    //если атрибут не разрешено редактировать - делаем его серым
                    if (!canEditProperty) {
                        cls.push('row-readonly');
                    }

                    return cls.join(' ');
                }
            },
            sourceConfig: {
                name: {
                    displayName: Unidata.i18n.t('glossary:name') + ' *',
                    valueTooltip: true,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        metaData.unselectableAttr = '';
                        metaData.tdCls = 'x-selectable';
                        view.enableTextSelection = true;

                        return Ext.util.Format.htmlEncode(value);
                    },
                    editor: {
                        xtype: 'textfield',
                        listeners: {
                            specialkey: 'onSpecialKey'
                        }
                    }
                },
                displayName: {
                    displayName: Unidata.i18n.t('glossary:displayName') + ' *',
                    valueTooltip: true,
                    editor: {
                        xtype: 'textfield',
                        listeners: {
                            specialkey: 'onSpecialKey',
                            blur: function (cmp) {
                                cmp.up('editor').completeEdit();
                            }
                        }
                    }
                },
                description: {
                    displayName: Unidata.i18n.t('glossary:description'),
                    valueTooltip: true,
                    editor: {
                        xtype: 'textfield',
                        listeners: {
                            specialkey: 'onSpecialKey'
                        }
                    }
                },
                mask: {
                    displayName: Unidata.i18n.t('admin.metamodel>mask'),
                    editor: {
                        xtype: 'textfield',
                        enableKeyEvents: true,
                        listeners: {
                            specialkey: 'onSpecialKey',
                            // какой-то треш в проперти гриде, многие нажатия кнопок не отрабатывают
                            keypress: function (editor, e) {
                                e.stopPropagation();
                            },
                            keydown: function (editor, e) {
                                e.stopPropagation();
                            },
                            render: function () {
                                var tooltip = me.getMaskHelpTooltip();

                                this.tip = Ext.create('Ext.tip.ToolTip', {
                                    target: this.getEl(),
                                    html: tooltip
                                });

                            }
                        }
                    },
                    renderer: function (v, metadata) {
                        var tooltip = me.getMaskHelpTooltip();

                        metadata.tdAttr = 'data-qtip="' + tooltip + '"';

                        return Ext.htmlEncode(v);
                    }
                },
                unique: {
                    displayName: Unidata.i18n.t('admin.metamodel>unique'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                nullable: {
                    displayName: Unidata.i18n.t('glossary:required'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        inverted: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:no').toLowerCase() : Unidata.i18n.t('common:yes').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                readOnly: {
                    displayName: Unidata.i18n.t('admin.metamodel>readOnly'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                hidden: {
                    displayName: Unidata.i18n.t('admin.metamodel>hidden'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                minCount: {
                    displayName: Unidata.i18n.t('admin.metamodel>min'),
                    editor: {
                        xtype: 'numberfield',
                        allowDecimals: false,
                        minValue: 0,
                        emptyText: '0',
                        listeners: {
                            focus: 'onMinTextFieldFocus',
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? v : '0';

                        return v;
                    }
                },
                maxCount: {
                    displayName: Unidata.i18n.t('admin.metamodel>max'),
                    editor: {
                        xtype: 'numberfield',
                        allowDecimals: false,
                        minValue: 1,
                        emptyText: '\u221E',
                        listeners: {
                            focus: 'onMaxTextFieldFocus',
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = (v === '' || v === null) ? '&#8734;' : v;

                        return v;
                    }
                },
                nestedEntityType: {
                    displayName: Unidata.i18n.t('admin.metamodel>nestedEntityName')
                },
                subEntityKeyAttribute: {
                    displayName: Unidata.i18n.t('admin.metamodel>insertEntityKey'),
                    editor: {
                        xtype: 'combobox',
                        expandOnFocus: true,
                        bind: {
                            store: '{childSimpleAttributes}'
                        },
                        displayField: 'displayName',
                        valueField: 'name',
                        emptyText: Unidata.i18n.t('admin.metamodel>selectEntityOrLookupEntity'),
                        forceSelection: true,
                        editable: false,
                        allowBlank: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v !== null ? '<b>' + v + '</b>' : '';

                        return Ext.htmlEncode(v);
                    }
                },
                simpleDataType: {
                    displayName: Unidata.i18n.t('glossary:valueType') + ' *',
                    editor: {
                        xtype: 'combobox',
                        expandOnFocus: true,
                        bind: {
                            store: '{simpleDataTypes}'
                        },
                        valueField: 'name',
                        displayField: 'displayName',
                        emptyText: Unidata.i18n.t('admin.common>defaultSelect', {entity: Unidata.i18n.t('admin.metamodel>type')}),
                        editable: false,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            focus: 'onSimpleDateTypeFieldFocus',
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: this.buildRendererFunction('simpleDataTypes')
                },
                arrayDataType: {
                    displayName: Unidata.i18n.t('glossary:valueType') + ' *',
                    editor: {
                        xtype: 'combobox',
                        expandOnFocus: true,
                        bind: {
                            store: '{arrayDataTypes}'
                        },
                        valueField: 'name',
                        displayField: 'displayName',
                        emptyText: Unidata.i18n.t('admin.common>defaultSelect', {entity: Unidata.i18n.t('admin.metamodel>type')}),
                        editable: false,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            focus: 'onArrayDateTypeFieldFocus',
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: this.buildRendererFunction('arrayDataTypes')
                },
                valueId: {
                    displayName: Unidata.i18n.t('admin.metamodel>measuredValue'),
                    editor: {
                        xtype: 'combobox',
                        expandOnFocus: true,
                        bind: {
                            store: '{measurementValues}'
                        },
                        valueField: 'id',
                        displayField: 'name',
                        emptyText: Unidata.i18n.t('admin.common>defaultSelect', {entity: Unidata.i18n.t('admin.metamodel>oneMeasuredValue')}),
                        editable: false,
                        triggers: {
                            reset: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.setValue(null);
                                    this.up('editor').completeEdit();
                                }
                            }
                        },
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: this.buildRendererFunction('measurementValues', 'id', 'name')
                },
                defaultUnitId: {
                    displayName: Unidata.i18n.t('glossary:unit'),
                    editor: {
                        xtype: 'combobox',
                        expandOnFocus: true,
                        bind: {
                            store: '{measurementUnits}'
                        },
                        valueField: 'id',
                        displayField: 'displayName',
                        emptyText: Unidata.i18n.t('admin.common>defaultSelect', {entity: Unidata.i18n.t('admin.metamodel>selectUnit')}),
                        editable: false,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: this.buildRendererFunction('measurementUnits', 'id', 'displayName')
                    // renderer is in AttributeController
                },
                enumDataType: {
                    displayName: Unidata.i18n.t('glossary:enum') + ' *',
                    editor: {
                        xtype: 'combobox',
                        expandOnFocus: true,
                        displayField: 'displayName',
                        valueField: 'name',
                        emptyText: Unidata.i18n.t('admin.common>defaultSelect', {entity: Unidata.i18n.t('admin.metamodel>enumeration')}),
                        forceSelection: true,
                        allowBlank: true,
                        queryMode: 'local',
                        editable: false,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            specialkey: 'onSpecialKey'
                        },
                        store: enumDataTypeEditorStore
                    },
                    renderer: this.buildRendererFunction(enumDataTypeEditorStore)
                },
                lookupEntityType: {
                    displayName: Unidata.i18n.t('glossary:lookupEntityLink') + ' *',
                    editor: {
                        xtype: 'un.entitycombo',
                        expandOnFocus: true,
                        draftMode: draftMode,
                        emptyText: Unidata.i18n.t('admin.common>defaultSelect', {entity: Unidata.i18n.t('glossary:lookupEntity').toLowerCase()}),
                        forceSelection: true,
                        allowBlank: true,
                        showEntities: false,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            endprocessresponse: 'onEndProcessResponse',
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: this.buildRendererFunction('lookupEntities')
                },
                linkDataType: {
                    displayName: Unidata.i18n.t('glossary:urlLink') + ' *',
                    editor: {
                        xtype: 'textfield',
                        emptyText: 'http://www.example.ru/{attr_name}',
                        listeners: {
                            specialkey: 'onSpecialKey'
                        }

                    }
                },
                typeCategory: {
                    displayName: Unidata.i18n.t('glossary:attributeType') + ' *',
                    editor: {
                        xtype: 'combobox',
                        expandOnFocus: true,
                        bind: {
                            store: '{typeCategories}'
                        },
                        displayField: 'name',
                        valueField: 'value',
                        forceSelection: true,
                        editable: false,
                        allowBlank: true,
                        emptyText: Unidata.i18n.t('admin.common>defaultSelect', {entity: Unidata.i18n.t('admin.metamodel>attributeType')}),
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            focus: 'onTypeCategoryFieldFocus',
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        var displayNames = {
                            simpleDataType: Unidata.i18n.t('glossary:simpleDataType'),
                            arrayDataType: Unidata.i18n.t('glossary:simpleDataType'),
                            lookupEntityType: Unidata.i18n.t('glossary:lookupEntityLink'),
                            enumDataType: Unidata.i18n.t('glossary:enum'),
                            linkDataType: Unidata.i18n.t('glossary:urlLink')
                        };

                        v = v !== '' && v !== null ? displayNames[v] : '';

                        return Ext.htmlEncode(v);
                    }
                },
                searchable: {
                    displayName: Unidata.i18n.t('admin.metamodel>searchable'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                displayable: {
                    displayName: Unidata.i18n.t('admin.metamodel>displayable'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                mainDisplayable: {
                    displayName: Unidata.i18n.t('admin.metamodel>mainDisplayable'),
                    tooltip: Unidata.i18n.t('admin.metamodel>mainDisplayable.tooltip'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                exchangeSeparator: {
                    displayName: Unidata.i18n.t('admin.metamodel>delimeter'),
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var tooltip = me.getExchangeSeparatorHelpTooltip();

                        metaData.unselectableAttr = '';
                        metaData.tdCls = 'x-selectable';
                        view.enableTextSelection = true;

                        metaData.tdAttr = 'data-qtip="' + tooltip + '"';

                        return value;
                    },
                    editor: {
                        xtype: 'textfield',
                        listeners: {
                            specialkey: 'onSpecialKey',
                            render: function () {
                                var tooltip = me.getExchangeSeparatorHelpTooltip();

                                this.tip = Ext.create('Ext.tip.ToolTip', {
                                    target: this.getEl(),
                                    html: tooltip
                                });
                            }
                        }
                    }
                },
                useAttributeNameForDisplay: {
                    displayName: Unidata.i18n.t('admin.metamodel>showAttributes'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                lookupEntityDisplayAttributes: {
                    displayName: Unidata.i18n.t('admin.metamodel>displayedAttributes'),
                    editor: {
                        xtype: 'un.attributetagfield',
                        expandOnFocus: true,
                        store: view.lookupEntityDisplayAttributesStore,
                        triggers: {
                            edit: {
                                cls: 'un-form-edit-trigger',
                                handler: function () {
                                    this.ownerCt.completeEdit();
                                }
                            },
                            clear: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        listeners: {
                            render: function (field) {
                                var sorters = [
                                    {
                                        property: 'order',
                                        direction: 'ASC'
                                    }
                                ];

                                // настраиваем CellEditor
                                field.up('editor').allowBlur = false;
                                field.up('editor').alignment = 'tl?';
                                field.valueCollection.setSorters(sorters);
                            }
                            // задел для сортировки тэгов. необходимо доработать
                            //select: function (field, records) {
                            //    records = Ext.Array.sort(records, function (a, b) {
                            //        return a.get('order') - b.get('order');
                            //    });
                            //    this.suspendEvents();
                            //    field.setValue();
                            //    this.resumeEvents();
                            //
                            //    field.setValue(records);
                            //}
                        }
                    },
                    renderer: this.buildRendererFunction(view.lookupEntityDisplayAttributesStore, null, null, {
                        fn: function () {
                            var viewModel  = me.getViewModel(),
                                metaRecord = viewModel.get('lookupEntityRecord'),
                                defaultValueText;

                            defaultValueText = me.buildLookupEntityDefaultValueText(metaRecord, me.attributeProperty.MAIN_DISPLAYABLE);

                            return defaultValueText;
                        }
                    })
                },
                customProperties: {
                    displayName: Unidata.i18n.t('admin.metamodel>customProperties'),
                    editor: {
                        emptyText: 'emptyText',
                        listeners: {
                            focus: function (field) {
                                var view = me.getView(),
                                    viewModel = me.getViewModel(),
                                    metaAttribute = viewModel.get('metaAttribute'),
                                    customPropertiesStore = metaAttribute.customProperties(),
                                    editor = field.up('editor'),
                                    gridWindow,
                                    readOnly;

                                readOnly = view.getReadOnly();

                                if (!field.customPropertiesInput) {
                                    field.customPropertiesInput = Ext.create({
                                        xtype: 'keyvalue.input',
                                        hidden: true,
                                        name: 'customProperties',
                                        readOnly: readOnly
                                    });

                                    field.on('destroy', function (field) {
                                        field.customPropertiesInput.destroy();
                                        field.customPropertiesInput = null;
                                    });
                                }

                                field.customPropertiesInput.setGridStore(customPropertiesStore);

                                gridWindow = field.customPropertiesInput.getGridWindow();

                                gridWindow.on('beforehide', function () {
                                    // фейковое значение для отображение
                                    field.setValue(Ext.String.format(
                                        Unidata.i18n.t('component>form.field.gridvalues.givenValues'),
                                        gridWindow.getGridStore().getCount()
                                    ));

                                    editor.completeEdit();
                                }, {single: true});

                                gridWindow.show();
                            }
                        }
                    }
                },
                searchMorphologically: {
                    displayName: Unidata.i18n.t('admin.metamodel>searchMorphologically'),
                    editor: {
                        xtype: 'yesnocombobox',
                        expandOnFocus: true,
                        listeners: {
                            select: function (combobox) {
                                combobox.up('editor').completeEdit();
                            },
                            render: function (combobox) {
                                combobox.inputEl.addCls('x-unselectable');
                            },
                            specialkey: 'onSpecialKey'
                        }
                    },
                    renderer: function (v) {
                        v = v ? Unidata.i18n.t('common:yes').toLowerCase() : Unidata.i18n.t('common:no').toLowerCase();

                        return Ext.htmlEncode(v);
                    }
                },
                lookupEntitySearchAttributes: {
                    displayName: Unidata.i18n.t('admin.metamodel>searchAttributes'),
                    editor: {
                        xtype: 'un.attributetagfield',
                        expandOnFocus: true,
                        store: view.lookupEntitySearchAttributesStore,
                        triggers: {
                            edit: {
                                cls: 'un-form-edit-trigger',
                                handler: function () {
                                    this.ownerCt.completeEdit();
                                }
                            },
                            clear: {
                                cls: 'x-form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        listeners: {
                            render: function (field) {
                                var sorters = [
                                    {
                                        property: 'order',
                                        direction: 'ASC'
                                    }
                                ];

                                // настраиваем CellEditor
                                field.up('editor').allowBlur = false;
                                field.up('editor').alignment = 'tl?';
                                field.valueCollection.setSorters(sorters);
                            }
                        }
                    },
                    renderer: this.buildRendererFunction(view.lookupEntitySearchAttributesStore, null, null, {
                        fn: function () {
                            var viewModel  = me.getViewModel(),
                                metaRecord = viewModel.get('lookupEntityRecord'),
                                defaultValueText;

                            defaultValueText = me.buildLookupEntityDefaultValueText(metaRecord, me.attributeProperty.SEARCHABLE);

                            return defaultValueText;
                        }
                    })
                }

            },
            listeners: {
                propertychange: 'onAttributePropertyChange',
                beforeedit: 'onAttributePropertyBeforeEdit',
                rowkeydown: 'onAttributePropertyRowKeydown',
                itemmousedown: function (cmp, record, item, index, e) {
                    e = e || {};

                    // активируем только на клик колонке с редактором
                    if (e.position && e.position.colIdx === 1) {
                        me.runFocusPropertyGridDelayedTask(10, record.id, true);
                    }
                }
            }
        };

        cfg = Ext.apply(cfg, customCfg);

        return cfg;
    },

    onAttributeTreeBeforeRender: function () {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            attributeComboBox  = this.lookupReference('attributeComboBox');

        attributeComboBox.setAttributeTree(attributeTreePanel);
    },

    /**
     * Строит функцию рендеринга отображаемых значений на основе ключей
     *
     * По ключам осуществляется поиск значения(значений) в store
     * @param store
     * @param valueField
     * @param displayField
     * @param emptyTextCallback {Object|String}
     * @returns {Function}
     */
    buildRendererFunction: function (store, valueField, displayField, emptyTextCallback) {
        var viewModel = this.getViewModel(),
            emptyText;

        if (Ext.isString(store)) {
            store = viewModel.get(store);
        }

        if (!(store instanceof Ext.data.AbstractStore)) {
            throw new Error(Unidata.i18n.t('admin.metamodel>buildRendererFunctionBadStore'));
        }

        valueField = valueField || 'name';
        displayField = displayField || 'displayName';

        return function (value) {
            var displayValue,
                //index,
                found,
                values,
                displayNames = [],
                source;

            source = Ext.isFunction(store.getDataSource) ? store.getDataSource() : store.getData();

            if (Boolean(value)) {
                if (Ext.isArray(value)) {
                    values = value;
                } else {
                    values = [value];
                }

                Ext.Array.each(values, function (value) {
                    var displayName;

                    if (value instanceof Unidata.model.attribute.AbstractSimpleAttribute) {
                        displayName = value.get(displayField);
                    } else {
                        found = source.find(valueField, value);

                        if (found) {
                            displayName = found.get(displayField);
                        }
                    }

                    if (displayName) {
                        displayNames.push(displayName);
                    }
                });
            }

            if (Ext.isString(emptyTextCallback)) {
                emptyText = emptyTextCallback;
            } else if (Ext.isObject(emptyTextCallback)) {
                emptyText = emptyTextCallback.fn.call(emptyTextCallback.scope);
            } else {
                emptyText = '';
            }

            displayValue = displayNames.join(', ');

            if (!displayValue) {
                if (Ext.isEmpty(emptyText)) {
                    emptyText = '&nbsp;';
                } else {
                    emptyText = Ext.htmlEncode(emptyText);
                }

                displayValue = Ext.String.format('<span class="un-empty-text">{0}</span>', emptyText);
            } else {
                displayValue = Ext.htmlEncode(displayValue);
            }

            return displayValue;
        };
    },

    //TODO: implement code attribute handle feature
    onChangeOrderAttributeButtonClick: function (button) {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            selection = attributeTreePanel.getSelection()[0];

        switch (button.typeOrderButton) {
            case 'down':
                attributeTreePanel.orderDown(selection);
                break;
            case 'up':
                attributeTreePanel.orderUp(selection);
                break;
        }

        //TODO:  how to use correct  viewmodel formulas for this?
        this.lookupReference('orderAttributeButtonDown').setDisabled(!selection.nextSibling);
        this.lookupReference('orderAttributeButtonUp').setDisabled(!selection.previousSibling);
    },

    showErrors: function (msgArray) {
        var errorPanel;

        errorPanel = this.getView().lookupReference('errorPanel');

        errorPanel.setErrors(msgArray);
        errorPanel.show();
    },

    hideErrors: function () {
        var errorPanel;

        errorPanel = this.getView().lookupReference('errorPanel');

        errorPanel.setErrors();
        errorPanel.hide();
    },

    /**
     * Attribute property change handler
     *
     * @param source
     * @param recordId
     * @param value
     * @param oldValue
     */
    onAttributePropertyChange: function (source, recordId, value, oldValue) {
        var me = this,
            viewModel          = this.getViewModel(),
            view               = this.getView(),
            grid               = view.down('propertygrid'),
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            attributeTreeItem = attributeTreePanel.getSelection()[0],
            record             = attributeTreeItem.get('record'),
            attributeComboBox  = this.lookupReference('attributeComboBox'),
            currentRecord      = viewModel.get('currentRecord'),
            hasData            = currentRecord.get('hasData'),
            lookupEntityRecord = viewModel.get('lookupEntityRecord'),
            MetaAttributeUtil = Unidata.util.MetaAttribute,
            generationStrategyForm = view.generationStrategyForm,
            nested,
            attributeFilters;

        // copy properties from propertyGrid.source to record
        function fillRecordProperties () {
            var property,
                sourceProperty;

            for (property in source) {
                if (source.hasOwnProperty(property) && record.getData().hasOwnProperty(property)) {
                    sourceProperty = source[property];

                    if (Ext.isArray(sourceProperty)) {
                        // отмапить массив атрибутов на массив их имен
                        sourceProperty = Ext.Array.map(sourceProperty, function (sourceProp) {
                            if (sourceProp instanceof Unidata.model.attribute.AbstractSimpleAttribute) {
                                sourceProp = sourceProp.get('name');
                            }

                            return sourceProp;
                        });
                    }

                    record.set(property, sourceProperty);
                }
            }

            attributeTreePanel.getView().refresh();
        }

        function isCodeAttribute (record) {
            return record instanceof Unidata.model.attribute.CodeAttribute ||
                record instanceof Unidata.model.attribute.AliasCodeAttribute;
        }

        function updateSourceIfNullableChanged () {
            if (source.readOnly !== undefined) {
                source.readOnly = source.nullable ? source.readOnly : false;
            }

            if (source.hidden !== undefined) {
                source.hidden = source.nullable ? source.readOnly : false;
            }
        }

        function updateSourceIfHiddenChanged () {
            if (source.typeCategory === 'linkDataType') {
                return;
            }

            if (source.readOnly !== undefined) {
                source.readOnly = source.hidden ? true : source.readOnly;
            }
        }

        attributeTreeItem.beginEdit();

        switch (recordId) {
            case 'typeCategory':
                // сбрасываем маску / измеряемую величину / единицы измерения
                // всегда при смене категории типа
                delete source.mask;

                record.set('valueId', null);
                delete source.valueId;

                record.set('defaultUnitId', null);
                delete source.defaultUnitId;

                // удаляем морфологический поиск
                record.set('searchMorphologically', false);
                delete source.searchMorphologically;

                if (Unidata.util.MetaAttribute.isArrayAttribute(record)) {
                    record.set('lookupEntityType', null);
                    delete source['lookupEntityType'];

                    record.set('arrayDataType', null);
                    delete source['arrayDataType'];

                    if (value === 'lookupEntityType') {
                        source['lookupEntityType'] = '';
                    } else if (value === 'arrayDataType') {
                        source['arrayDataType'] = '';
                    }

                } else {
                    Unidata.Constants.getTypeCategories().forEach(function (typeCategory) {
                        delete source[typeCategory];
                        record.set(typeCategory, '');
                    });

                    if (value !== null) {
                        source[value] = '';
                    }

                    if (oldValue === 'simpleDataType' &&
                        (record.get('simpleDataType') === 'Blob' || record.get('simpleDataType') === 'Clob')) {
                        // возвращаем поля обратно если был тип файла

                        record.set('mainDisplayable', false);
                        record.set('displayable', false);
                        record.set('searchable', false);
                        record.set('unique', false);

                        source.mainDisplayable = false;
                        source.displayable = false;
                        source.searchable = false;
                        source.unique = false;
                    }

                    // проставляем дефолтные значения для скрытых полей
                    if (value === 'linkDataType') {
                        record.set('mainDisplayable', false);
                        record.set('displayable', false);
                        record.set('searchable', false);
                        record.set('nullable', true);
                        record.set('readOnly', true);

                        delete source.mainDisplayable;
                        delete source.displayable;
                        delete source.searchable;
                        delete source.nullable;
                        delete source.readOnly;

                        if (attributeTreeItem.get('parentId') === 'root') {
                            record.set('unique', false);
                            delete source.unique;
                        }
                    } else if (oldValue === 'linkDataType') {
                        // возвращаем поля обратно если был тип "Ссылка на веб-ресурс"
                        record.set('mainDisplayable', false);
                        record.set('displayable', false);
                        record.set('searchable', false);
                        record.set('nullable', true);
                        record.set('readOnly', false);

                        source.mainDisplayable = false;
                        source.displayable = false;
                        source.searchable = false;
                        source.searchable = false;
                        source.nullable = true;
                        source.readOnly = false;

                        if (attributeTreeItem.get('parentId') === 'root') {
                            record.set('unique', false);
                            source.unique = false;
                        }
                    }
                }

                if (source.typeCategory === 'lookupEntityType' && source.lookupEntityType) {
                    source.lookupEntityDisplayAttributes = [];
                    source.lookupEntitySearchAttributes = [];
                    source.useAttributeNameForDisplay = false;
                } else {
                    delete source.lookupEntityDisplayAttributes;
                    delete source.lookupEntitySearchAttributes;
                    delete source.useAttributeNameForDisplay;
                }

                break;

            case 'unique':
                if (oldValue === false && value === true && hasData) {
                    source.unique = oldValue;
                }

                if (source.nullable !== undefined) {
                    source.nullable = source.unique ? false : source.nullable;
                    updateSourceIfNullableChanged();
                }
                break;

            case 'nullable':
                updateSourceIfNullableChanged();
                break;

            case 'mainDisplayable':
                if (source.displayable !== undefined) {
                    source.displayable = source.mainDisplayable ? true : source.displayable;
                }

                if (source.nullable !== undefined) {
                    source.nullable = source.mainDisplayable ? false : source.nullable;
                    updateSourceIfNullableChanged();
                }
                break;

            case 'displayable':
                if (source.mainDisplayable !== undefined) {
                    source.mainDisplayable = source.displayable ? source.mainDisplayable : false;
                }

                if (source.hidden !== undefined) {
                    source.hidden = source.displayable ? false : source.hidden;
                    updateSourceIfHiddenChanged();
                }
                break;

            case 'hidden':
                updateSourceIfHiddenChanged();
                break;

            case 'readOnly':
                if (source.hidden !== undefined) {
                    source.hidden = source.readOnly ? source.hidden : false;
                }
                break;

            case 'maxCount':
                if (source.maxCount === '' || source.maxCount === null) {
                    source.maxCount = null;
                }
                break;

            case 'minCount':
                if (!source.minCount) {
                    source.minCount = 0;
                }
                break;
            case 'lookupEntityType':
                // загружаем модель справочника и проставляем значения в store tag поля
                if (source.lookupEntityType && (!lookupEntityRecord || lookupEntityRecord.get('name') !== source.lookupEntityType)) {
                    this.changeLookupMetaRecord({
                        entityName: source.lookupEntityType,
                        entityType: 'LookupEntity',
                        draft: view.draftMode
                    });
                }

                if (source.lookupEntityType) {
                    source.lookupEntityDisplayAttributes = [];
                    source.lookupEntitySearchAttributes = [];
                    source.useAttributeNameForDisplay = false;
                } else {
                    delete source.lookupEntityDisplayAttributes;
                    delete source.lookupEntitySearchAttributes;
                    delete source.useAttributeNameForDisplay;
                }
                break;
            case 'simpleDataType':
                if (source.simpleDataType !== 'String') {
                    record.set('searchMorphologically', false);

                    delete source.mask;
                    delete source.searchMorphologically;
                }

                if (source.simpleDataType !== 'Number') {
                    record.set('valueId', null);
                    record.set('defaultUnitId', null);

                    delete source.valueId;
                    delete source.defaultUnitId;
                }

                if (source.simpleDataType === 'String') {
                    source.mask = '';
                    source.searchMorphologically = false;
                    // при смене типа на Строковый переинициализируем форму генерации стратегии
                    if (MetaAttributeUtil.isCodeAttribute(record)) {
                        attributeFilters = this.buildGenerationStrategyAttributeFilters();

                        // инициализируем форму настройки правил генерации значений атрибута
                        generationStrategyForm.initGenerationStrategy(currentRecord, record, attributeFilters);
                    }
                } else if (source.simpleDataType === 'Number') {
                    source.valueId = null;
                    source.defaultUnitId = null;
                } else if (source.simpleDataType === 'Blob' || source.simpleDataType === 'Clob') {
                    record.set('unique', false);
                    record.set('mainDisplayable', false);

                    source.mainDisplayable = false;
                    delete source.unique;
                }
                viewModel.set('attributeSimpleDataType', source.simpleDataType);
                break;
            case 'arrayDataType':
                if (source.arrayDataType !== 'String') {
                    delete source.mask;
                }

                if (source.arrayDataType !== 'Number') {
                    record.set('valueId', null);
                    record.set('defaultUnitId', null);

                    delete source.valueId;
                    delete source.defaultUnitId;
                }

                if (source.arrayDataType === 'String') {
                    source.mask = '';
                }
                break;
            case 'valueId':
                source.defaultUnitId = this.getBaseMeasurementUnitId(value);

                this.updateMeasurementUnitsStore(value);
                break;
            case 'searchMorphologically':
                break;
        }

        source = this.sortPropertyGridSource(source);

        grid.setSource(source);

        if (isCodeAttribute(record)) {
            record.set('unique', true);
        }

        fillRecordProperties();

        if (typeof record.getNestedEntity === 'function') {
            nested = record.getNestedEntity();

            nested.set('name', record.get('name'));
            nested.set('displayName', record.get('displayName'));
            nested.set('description', record.get('description'));
        }

        if (recordId !== 'typeCategory') {
            // notify store about property change to render
            attributeTreeItem.endEdit();
        }

        if (recordId === 'displayName') {
            attributeComboBox.updateAttributeComboBoxStore();
        }

        this.checkRecordDirty(record, this.getViewModel().getParent());
        this.checkSelectedRecordError();

        grid.getView().refresh();

        me.runFocusPropertyGridDelayedTask(10, recordId, false);
    },

    getBaseMeasurementUnitId: function (valueId) {
        var unitId = null,
            record;

        record = this.getMeasurementValueBuId(valueId);

        if (record) {
            unitId = record.get('record').getBaseMeasurementUnitId();
        }

        return unitId;
    },

    sortPropertyGridSource: function (source) {
        var order,
            clone = {};

        order = [
            'name',
            'displayName',
            'description',
            'unique',
            'nullable',
            'readOnly',
            'hidden',
            'minCount',
            'maxCount',
            'searchable',
            'displayable',
            'mainDisplayable',
            'typeCategory',
            'simpleDataType',
            'arrayDataType',
            'enumDataType',
            'searchMorphologically',
            'mask',
            'valueId',
            'defaultUnitId',
            'lookupEntityType',
            'lookupEntityDisplayAttributes',
            'lookupEntitySearchAttributes',
            'useAttributeNameForDisplay',
            'exchangeSeparator',
            'customProperties',
            'useAttributeNameForDisplay'
        ];

        Ext.Array.each(order, function (value) {
            if (Ext.isObject(source) && source.hasOwnProperty(value)) {
                clone[value] = source[value];

                delete source[value];
            }
        });

        // мержим если какие то ключи описаны в массиве :)
        clone = Ext.Object.merge(clone, source);

        return clone;
    },

    onPropertyGripSourceChange: function (source) {
        if (source && Ext.isDefined(source.valueId)) {
            this.updateMeasurementUnitsStore(source.valueId);
        }
    },

    updateMeasurementValuesStore: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            measurementValues = viewModel.getStore('measurementValues'),
            cacheMeasurementValues = viewModel.getStore('cacheMeasurementValues'),
            records = [],
            draftMode = view.draftMode,
            storeLoadCfg,
            promise;

        storeLoadCfg = {
            params: {
                draft: draftMode
            }
        };

        promise = Unidata.util.api.MeasurementValues.loadStore(cacheMeasurementValues, true, storeLoadCfg);
        promise
            .then(
                function (cacheMeasurementValues) {
                    var proxy;

                    cacheMeasurementValues.each(function (measurementValue) {
                        records.push({
                            id: measurementValue.get('id'),
                            name: measurementValue.get('name'),
                            record: measurementValue,
                            units: measurementValue.measurementUnits().getRange()
                        });
                    });

                    proxy = measurementValues.getProxy();

                    if (proxy) {
                        proxy.setData(records);
                        measurementValues.load();
                    }
                })
            .done();
    },

    getMeasurementValueBuId: function (valueId) {
        var viewModel         = this.getViewModel(),
            measurementValues = viewModel.getStore('measurementValues'),
            record            = null,
            index;

        index = measurementValues.findExact('id', valueId);

        if (index !== -1) {
            record = measurementValues.getAt(index);
        }

        return record;
    },

    updateMeasurementUnitsStore: function (valueId) {
        var viewModel         = this.getViewModel(),
            measurementUnits  = viewModel.getStore('measurementUnits'),
            records           = [],
            units,
            record;

        measurementUnits.removeAll();

        record = this.getMeasurementValueBuId(valueId);

        if (record) {
            units = record.get('units');

            Ext.Array.each(units, function (unit) {
                records.push({
                    id: unit.get('id'),
                    name: unit.get('name'),
                    shortName: unit.get('shortName')
                });
            });

            measurementUnits.getProxy().setData(records);
            measurementUnits.load();
        }
    },

    updateTypeCategoriesStore: function () {
        var viewModel = this.getViewModel(),
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            selectionModel = attributeTreePanel.getSelectionModel(),
            record,
            data,
            store;

        store = viewModel.getStore('typeCategories');

        // фильтры добавляются в onTypeCategoryFieldFocus
        store.clearFilter();

        data = Unidata.model.attribute.SimpleAttribute.getTypesList();

        if (selectionModel.getCount()) {
            record = selectionModel.getSelection()[0].get('record');

            //для array атрибута допустимые типы "простой тип" и "ссылка на справочник"
            if (record instanceof Unidata.model.attribute.ArrayAttribute) {
                data = Unidata.model.attribute.SimpleAttribute.getArrayTypesList();
            }
        }

        store.getProxy().setData(data);
        store.load();
    },

    onMaxTextFieldFocus: function () {
        var view = this.getView(),
            propertyGrid = view.attributePropertyGrid,
            editorMaxCfg = propertyGrid.getConfigProp('maxCount', 'editor');

        editorMaxCfg.field.setMinValue(
            // берём максимальное из минимальных значений
            Math.max(propertyGrid.source.minCount, editorMaxCfg.field.config.minValue)
        );
    },

    onMinTextFieldFocus: function () {
        var view = this.getView(),
            propertyGrid = view.attributePropertyGrid,
            editorMinCfg = propertyGrid.getConfigProp('minCount', 'editor');

        editorMinCfg.field.setMaxValue(propertyGrid.source.maxCount);
    },

    onSimpleDateTypeFieldFocus: function () {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            selectionModel     = attributeTreePanel.getSelectionModel(),
            record,
            store;

        store = this.getViewModel().getStore('simpleDataTypes');

        store.clearFilter();

        function isCodeAttribute (record) {
            return record instanceof Unidata.model.attribute.CodeAttribute ||
                   record instanceof Unidata.model.attribute.AliasCodeAttribute;
        }

        if (selectionModel.getCount()) {
            record = selectionModel.getSelection()[0].get('record');

            //для кодового атрибута допустимые типы Integer, String
            if (isCodeAttribute(record)) {
                store.filterBy(function filterFn (record) {
                    return Ext.Array.contains(['Integer', 'String'], record.get('name'));
                });
            }
        }
    },

    onTypeCategoryFieldFocus: function () {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            selectionModel = attributeTreePanel.getSelectionModel(),
            attribute,
            store;

        store = this.getViewModel().getStore('typeCategories');

        store.clearFilter();

        if (selectionModel.getCount()) {
            attribute = selectionModel.getSelection()[0];

            if (attribute.get('depth') === 1) {
                return;
            }

            //тип "ссылка на веб ресурс" доступна только для атрибутов первого уровня
            store.filterBy(function filterFn (record) {
                return record.get('value') !== 'linkDataType';
            });
        }
    },

    onArrayDateTypeFieldFocus: function () {
    },

    isRequiredFieldCorrect: function (record, parentHiddenOrReadOnly) {
        return record.get('nullable') === undefined ||
            (record.get('nullable') === true) ||
            (record.get('hidden') === false && record.get('readOnly') === false && !parentHiddenOrReadOnly);
    },

    isChildrenAttributesRequiredCorrect: function (complexAttribute, parentHiddenOrReadOnly) {
        var me = this,
            isValid = true,
            nestedEntity = complexAttribute.getNestedEntity(),
            hiddenOrReadOnly = complexAttribute.get('hidden') || complexAttribute.get('readOnly');

        parentHiddenOrReadOnly = parentHiddenOrReadOnly && hiddenOrReadOnly;

        nestedEntity.simpleAttributes().each(function (attribute) {
            isValid = isValid && me.isRequiredFieldCorrect(attribute, parentHiddenOrReadOnly);
        });

        if (Ext.isFunction(nestedEntity.aliasCodeAttributes)) {
            nestedEntity.aliasCodeAttributes().each(function (attribute) {
                isValid = isValid && me.isRequiredFieldCorrect(attribute, parentHiddenOrReadOnly);
            });
        }

        nestedEntity.complexAttributes().each(function (complexAttributeNested) {
            isValid = isValid && me.isChildrenAttributesRequiredCorrect(complexAttributeNested, parentHiddenOrReadOnly);
        });

        return isValid;
    },

    getRecordErrorMessages: function (record) {
        var errorMsg = [],
            parentHiddenOrReadOnly;

        if (!this.isAttributeNameUnique(record.get('name'))) {
            errorMsg.push(Unidata.i18n.t('admin.metamodel>attributeNameShouldBeUnique'));
        }

        if (!this.isRequiredFieldCorrect(record)) {
            errorMsg.push(Unidata.i18n.t('admin.metamodel>requiredAttributeCantBeHidden'));
        }

        if (record instanceof Unidata.model.attribute.ComplexAttribute) {
            parentHiddenOrReadOnly = record.get('hidden') || record.get('readOnly');

            if (!this.isChildrenAttributesRequiredCorrect(record, parentHiddenOrReadOnly)) {
                errorMsg.push(Unidata.i18n.t('admin.metamodel>childAttributesCantBeRequired'));
            }
        }

        errorMsg = Ext.Array.merge(errorMsg, record.getErrorMessages());

        return errorMsg;
    },

    getSelectedAttribute: function () {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            record;

        if (attributeTreePanel.getSelection().length === 0) {
            return null;
        }

        record = attributeTreePanel.getSelection()[0].get('record');

        return record;
    },

    getSelectedRecordErrorMessages: function () {
        var record = this.getSelectedAttribute();

        return this.getRecordErrorMessages(record);
    },

    checkSelectedRecordError: function () {
        var view = this.getView(),
            readOnly = view.getReadOnly(),
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            node = attributeTreePanel.getSelection()[0],
            record = node.get('record'),
            errorMsg = this.getSelectedRecordErrorMessages(),
            addBtnDisabled;

        if (!node.isRoot() && (!record.isValid() || errorMsg.length)) {
            errorMsg = Ext.Array.merge(errorMsg, record.getErrorMessages());
            this.showErrors(errorMsg);
            addBtnDisabled = true;
            this.getView().isErrors = true;
        } else {
            this.hideErrors();
            addBtnDisabled = false;
            this.getView().isErrors = false;
        }

        if (readOnly) {
            addBtnDisabled = true;
        }

        this.lookupReference('addCodeAttrButton').setDisabled(addBtnDisabled);
        this.lookupReference('addSimpleAttrButton').setDisabled(addBtnDisabled);
        this.lookupReference('addComplexAttrButton').setDisabled(addBtnDisabled);
        this.lookupReference('addArrayAttrButton').setDisabled(addBtnDisabled);
    },

    isAttributeNameUnique: function (attributeName) {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            selectedNode = attributeTreePanel.getSelection()[0],
            parentNode = selectedNode.parentNode,
            record,
            result = true;

        if (!parentNode) {
            return result;
        }

        Ext.Array.each(parentNode.childNodes , function (item) {
            record = item.get('record');

            if (item === selectedNode) {
                return;
            }

            if (record.get('name') === attributeName) {
                result = false;

                return false;
            }
        });

        return result;
    },

    isAttributeNamesUnique: function () {
        var result = true,
            names = [],
            attributeTreePanel,
            rootNode,
            treeStore,
            nodes;

        attributeTreePanel = this.lookupReference('attributeTreePanel');
        rootNode = attributeTreePanel.getRootNode();
        treeStore = rootNode.getTreeStore();
        nodes = treeStore.getRange();

        nodes = Ext.Array.remove(nodes, rootNode);

        Ext.Array.each(nodes, function (node) {
            var record = node.get('record');

            if (Ext.Array.contains(names, record.get('name'))) {
                result = false;

                return false; //окончание итерации
            }

            names.push(record.get('name'));
        });

        return result;
    },

    attributePhantom: function () {
        var metaAttribute = this.getAttribute();

        return metaAttribute && metaAttribute.phantom;
    },

    attributeUsed: function (metaAttribute) {
        var DataQuality = Unidata.util.DataQuality,
            metaRecord = this.getMetaRecord();

        if (DataQuality.attributeUsedDq(metaAttribute, metaRecord)) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>attributeUsesInQualityRules'));

            return true;
        }

        // для новых атрибутов (phantom) не сработает, т.к. используется entityDependencies, расчитываемые на бекенде
        if (this.attributeUsedMatchingRules()) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>attributeUsesInSearchDuplicatesRules'));

            return true;
        }

        // для новых атрибутов (phantom) не сработает, т.к. используется entityDependencies, расчитываемые на бекенде
        if (this.attributeUsedLookupDisplayableAttributes()) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>attributeUsesInLookupEntityList'));

            return true;
        }

        // для новых атрибутов (phantom) не сработает, т.к. используется entityDependencies, расчитываемые на бекенде
        if (this.attributeUsedRelationDisplayableAttributes()) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>attributeUsesInRelationsList'));

            return true;
        }

        // для новых атрибутов (phantom) не сработает, т.к. используется entityDependencies, расчитываемые на бекенде
        if (this.attributeUsedConsolidation()) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>attributeUsesInConsolidationSettings'));

            return true;
        }

        if (this.attributeUsedAttributeGroup()) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>attributeUsesInAttributesGroup'));

            return true;
        }

        return false;
    },

    onDeleteAttributeButtonClick: function (btn) {
        var title = Unidata.i18n.t('glossary:removeAttribute'),
            msg   = Unidata.i18n.t('admin.metamodel>confirmRemoveAttribute'),
            metaAttribute;

        metaAttribute = this.getSelectedAttribute();

        if (!metaAttribute) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>noSelectAttribute'));
        }

        if (!this.isAttributeNameUnique(metaAttribute.get('name')) || !this.attributeUsed(metaAttribute)) {
            this.showPrompt(title, msg, this.deleteAttribute, this, btn);
        }
    },

    deleteAttribute: function () {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            selection = attributeTreePanel.getSelection()[0],
            attributeComboBox = this.lookupReference('attributeComboBox'),
            record;

        if (!selection) {
            return;
        }
        //TODO: move to callback
        attributeTreePanel.removeAttributeNode(selection);
        record = selection.get('record');

        if (record instanceof Unidata.model.attribute.CodeAttribute) {
            record.drop();
        } else if (record instanceof Unidata.model.attribute.AliasCodeAttribute) {
            record.store.remove(record);    //TODO: do it better
        } else {
            record.store.remove(record);    //TODO: do it better
        }

        attributeComboBox.updateAttributeComboBoxStore();

        this.checkRecordDirty(record, this.getViewModel().getParent());
    },

    onAddAttributeButtonClick: function (me) {
        var nestedEntity,
            record,
            records,
            codeAttribute,
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            node = attributeTreePanel.getNodeToAppend(),
            abstractEntity = attributeTreePanel.getNodeNestedEntity(node);

        //TODO: refactoring use reference instead itemId
        switch (me.itemId) {
            case 'simple-attribute-add-button':
                record = Ext.create('Unidata.model.attribute.SimpleAttribute', {});
                records = abstractEntity.simpleAttributes().insert(0, record);
                record = records[0];
                break;
            case 'code-attribute-add-button':
                if (!(abstractEntity instanceof Unidata.model.entity.LookupEntity)) {

                    return;
                }
                codeAttribute = abstractEntity.getCodeAttribute();

                if (!codeAttribute) {
                    record = Ext.create('Unidata.model.attribute.CodeAttribute', {});
                    abstractEntity.setCodeAttribute(record);
                    record = abstractEntity.getCodeAttribute();
                } else {
                    record = Ext.create('Unidata.model.attribute.AliasCodeAttribute', {});
                    abstractEntity.aliasCodeAttributes().add(record);
                }
                break;
            case 'complex-attribute-add-button':
                record = Ext.create('Unidata.model.attribute.ComplexAttribute', {});
                nestedEntity = Ext.create('Unidata.model.entity.NestedEntity', {});
                record.setNestedEntity(nestedEntity);
                records = abstractEntity.complexAttributes().insert(0, record);
                record = records[0];
                break;
            case 'array-attribute-add-button':
                record = Ext.create('Unidata.model.attribute.ArrayAttribute', {});
                records = abstractEntity.arrayAttributes().insert(0, record);
                record = records[0];
                break;
            default:
                console.error('error: element with itemId=' + me.itemId + ' does not exists');

                return;
        }

        attributeTreePanel.appendChildAttributeToNode(node, record);
    },

    onAttributeTreeNodeBeforeDeselect: function (tree, node) {
        var view = this.getView(),
            isRootNode = node.isRoot(),
            isValidRecord = node.get('record').isValid(),
            isAttributeNameUnique,
            propertyGrid = view.attributePropertyGrid,
            store = propertyGrid.getStore(),
            itemIndex,
            record;

        itemIndex = store.findExact('name', 'name');
        record = store.getAt(itemIndex);

        isAttributeNameUnique = record ? this.isAttributeNameUnique(record.get('value')) : true;

        // если выбор отображаемых атрибутов в режиме редактирования, то принудительно завершаем редактирование
        view.completeAllEditors();

        return (isValidRecord || isRootNode) && isAttributeNameUnique;
    },

    onAttributeTreeNodeDeselect: function () {
        this.hideErrors();
        this.getView().isErrors = false;

        this.lookupReference('addCodeAttrButton').setDisabled(false);
        this.lookupReference('addSimpleAttrButton').setDisabled(false);
        this.lookupReference('addComplexAttrButton').setDisabled(false);
        this.lookupReference('addArrayAttrButton').setDisabled(false);
    },

    onAttributeTreeNodeSelect: function () {
        var viewModel     = this.getViewModel(),
            attribute = this.getSelectedAttribute(),
            view = this.getView(),
            generationStrategyForm = view.generationStrategyForm,
            attributeFilters;

        this.checkSelectedRecordError();
        this.updateTypeCategoriesStore();

        viewModel.set('attributeSimpleDataType', attribute.get('simpleDataType'));

        if (generationStrategyForm.getMetaRecord()) {
            attributeFilters = this.buildGenerationStrategyAttributeFilters();
            generationStrategyForm.fillDisplayAttributeStore(attributeFilters);
        }
    },

    /**
     * Построить фильтр для отображения атрибутов
     * @param excludeAttribute
     * @returns {Array}
     */
    buildGenerationStrategyAttributeFilters: function () {
        var attributeFilters,
            codeAttribute,
            viewModel = this.getViewModel(),
            metaRecord = viewModel.get('currentRecord');

        if (!metaRecord) {
            return [];
        }

        if (Unidata.util.MetaRecord.isLookup(metaRecord)) {
            codeAttribute = metaRecord.getCodeAttribute();
        }

        attributeFilters = [
            function (item) {
                var simpleDataType = item.get('simpleDataType'),
                    typeCategory = item.get('typeCategory'),
                    isSpecificSimpleDataType,
                    isLookupEntityType,
                    excludeCondition = true;

                isSpecificSimpleDataType = simpleDataType === 'String' || simpleDataType === 'Integer';
                isLookupEntityType = typeCategory === 'lookupEntityType';

                if (codeAttribute) {
                    excludeCondition = codeAttribute.get('name') !== item.get('name');
                }

                return excludeCondition &&
                       (isSpecificSimpleDataType || isLookupEntityType);
            }
        ];

        return attributeFilters;
    },

    onAttributeBeforeRender: function () {
        var managedStoreLoader = this.getView().getPlugin('managedstoreloader');

        return !managedStoreLoader || managedStoreLoader.isEveryStoreIsLoaded();
    },

    onNecessaryStoresLoad: function () {
        var view               = this.getView(),
            viewModel          = this.getViewModel(),
            attributeTreePanel = this.lookupReference('attributeTreePanel');

        attributeTreePanel.setLookupEntities(viewModel.getStore('lookupEntities'));

        view.fireEvent('loadallstore');
    },

    onAttributePropertyBeforeEdit: function (editor, context) {
        var DataQuality = Unidata.util.DataQuality,
            me = this,
            view = this.getView(),
            canEdit = true,
            checkFieldName = ['name', 'typeCategory', 'simpleDataType', 'lookupEntityType'],
            displayableReadOnly = ['customProperties'],
            propCodeName = context.record.getId(),
            metaRecord = this.getMetaRecord(),
            metaAttribute = this.getSelectedAttribute(),
            readOnly =  view.getReadOnly();

        if (!metaAttribute) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>noSelectAttribute'));
        }

        if (Ext.Array.contains(displayableReadOnly, propCodeName)) {
            readOnly = false;
        }

        if (readOnly) {
            return false;
        }

        if (Ext.Array.contains(checkFieldName, propCodeName)) {
            if (DataQuality.attributeUsedDq(metaAttribute, metaRecord)) {
                canEdit = false;
            }

            if (me.attributeUsedMatchingRules()) {
                canEdit = false;
            }

            if (me.attributeUsedLookupDisplayableAttributes()) {
                canEdit = false;
            }

            if (me.attributeUsedConsolidation()) {
                canEdit = false;
            }
        }

        if (propCodeName === 'name' && me.attributeUsedAttributeGroup()) {
            canEdit = false;
        }

        if (propCodeName === 'name' && !this.isAttributeNameUnique(metaAttribute.get('name'))) {
            canEdit = true;
        }

        if (canEdit) {
            canEdit = me.isAttributePropertyEditable(propCodeName);
        }

        return canEdit;
    },

    isAttributePropertyEditable: function (propCodeName) {
        var DataQuality = Unidata.util.DataQuality,
            me = this,
            metaRecord,
            hasData,
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            metaAttribute,
            importantFields     = ['name', 'typeCategory', 'simpleDataType', 'lookupEntityType', 'valueId', 'arrayDataType'],
            codeAttributeFields = ['nullable', 'displayable', 'searchable', 'unique'],
            aliasCodeAttributeFields = ['displayable', 'searchable', 'unique'],
            mainDisplayableFields = ['nullable'],
            displayableFields = ['hidden'],
            notNullableFields = ['readOnly', 'hidden'];

        metaRecord    = this.getMetaRecord();
        hasData       = metaRecord.get('hasData');

        function isMainDisplayableAttribute (record) {
            return record.get('mainDisplayable') === true;
        }

        function isDisplayableAttribute (record) {
            return record.get('displayable') === true;
        }

        function isNotNullableAttribute (record) {
            return record.get('nullable') === false;
        }

        function isNotUniqueAttribute (record) {
            return record.get('unique') === false;
        }

        function isCodeAttribute (record) {
            return record instanceof Unidata.model.attribute.CodeAttribute;
        }

        function isAliasCodeAttribute (record) {
            return record instanceof Unidata.model.attribute.AliasCodeAttribute;
        }

        if (attributeTreePanel.getSelection().length === 0) {
            return false;
        }

        metaAttribute = this.getSelectedAttribute();

        if (!metaAttribute) {
            Unidata.showError(Unidata.i18n.t('admin.metamodel>noSelectAttribute'));
        }

        // Тип атрибута и наименование разрешено изменять только
        // 1) Если атрибут создается новый
        // 2) справочник/реестр не содержит данных (hasData = false)
        // 3) атрибут не используется в правилах dq
        // 4) атрибут не используется в консолидации
        if (!metaAttribute.phantom &&
            Ext.Array.contains(importantFields, propCodeName) &&
            (hasData || DataQuality.attributeUsedDq(metaAttribute, metaRecord) || me.attributeUsedConsolidation() ||
            me.attributeUsedMatchingRules() || me.attributeUsedLookupDisplayableAttributes())) {
            return false;
        }

        if (!metaAttribute.phantom && propCodeName === 'name' && me.attributeUsedAttributeGroup()) {
            return false;
        }

        // если тип атрибута численный, есть данные и не задача измеряемая величина то единица имзерения недоступна
        // если величина задана, то единицу измерения допустимо изменять
        if (!metaAttribute.phantom && hasData && propCodeName === 'defaultUnitId' &&
            !metaAttribute.get('valueId') && metaAttribute.get('simpleDataType') === 'Number') {
            return false;
        }

        if (isAliasCodeAttribute(metaAttribute)) {
            if (Ext.Array.contains(aliasCodeAttributeFields, propCodeName)) {
                return false;
            }
        } else if (isCodeAttribute(metaAttribute)) {
            if (Ext.Array.contains(codeAttributeFields, propCodeName)) {
                return false;
            }
        }

        if (isMainDisplayableAttribute(metaAttribute)) {
            if (Ext.Array.contains(mainDisplayableFields, propCodeName)) {
                return false;
            }
        }

        if (isDisplayableAttribute(metaAttribute)) {
            if (Ext.Array.contains(displayableFields, propCodeName)) {
                return false;
            }
        }

        if (isNotNullableAttribute(metaAttribute)) {
            if (Ext.Array.contains(notNullableFields, propCodeName)) {
                return false;
            }
        }

        if (propCodeName === 'unique' && isNotUniqueAttribute(metaAttribute) && hasData) {
            return false;
        }

        return true;
    },

    getMetaRecord: function () {
        var viewModel,
            metaRecord;

        viewModel = this.getViewModel();
        metaRecord = viewModel.get('currentRecord');

        return metaRecord;
    },

    /**
     * Выбрать только нужные entityDependency
     * MATCHING_RULE -> ATTRIBUTE
     *
     * @private
     * @param entityDependency {Unidata.model.entity.EntityDependency}
     * @returns {boolean}
     */
    filterDependencies: function (sourceType, targetType, entityDependency) {
        var currentSourceType,
            currentTargetType,
            result,
            EntityDependencyClass = Unidata.model.entity.EntityDependency;

        if (!entityDependency || !(entityDependency instanceof EntityDependencyClass)) {
            return false;
        }

        currentSourceType = entityDependency.get('sourceType');
        currentTargetType = entityDependency.get('targetType');

        result = currentSourceType === sourceType && currentTargetType === targetType;

        return result;
    },

    /**
     * Найти EntityDependency, соответствующее attrPathSearch
     *
     * @private
     * @param entityDependencies {Unidata.model.entity.EntityDependency}
     * @param attrPathSearch {String}
     * @returns {*}
     */
    findEntityDependencyByTargetFullAttributeName: function (entityDependencies, attrPathSearch) {
        var found;

        found = Ext.Array.findBy(entityDependencies, function (entityDependency) {
            var targetKey,
                attrPath,
                EntityDependencyUtil = Unidata.util.EntityDependency;

            if (!EntityDependencyUtil.checkEntityDependency(entityDependency)) {
                Ext.Error.raise(Unidata.i18n.t('admin.metamodel>incorrectEntityDependencyFormat'));
            }

            targetKey = entityDependency.get('targetKey');
            attrPath = targetKey.fullAttributeName;

            return attrPath === attrPathSearch;
        });

        return found;
    },

    /**
     * Проверить участвует ли атрибут в правилах матчинга
     *
     * @returns {boolean}
     */
    attributeUsedMatchingRules: function () {
        var viewModel          = this.getViewModel(),
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            metaRecord         = viewModel.get('currentRecord'),
            isAttributeUsed    = false,
            entityDependencies,
            metaAttribute,
            attrPath,
            MetaRecordUtil     = Unidata.util.MetaRecord,
            selection          = attributeTreePanel.getSelection(),
            EntityDependencyType = Unidata.model.entity.EntityDependencyType;

        if (selection.length === 0 || !MetaRecordUtil.isEntity(metaRecord)) {
            return false;
        }

        metaAttribute = selection[0].get('record');
        attrPath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);

        entityDependencies = metaRecord.entityDependency().getRange();
        entityDependencies = Ext.Array.filter(entityDependencies, this.filterDependencies.bind(this, EntityDependencyType.MATCHING_RULE , EntityDependencyType.ATTRIBUTE), this);
        isAttributeUsed = Boolean(this.findEntityDependencyByTargetFullAttributeName(entityDependencies, attrPath));

        return isAttributeUsed;
    },

    /**
     * Вычисляет признак того, что атрибут используется в списках отображаемых атрибутов для ссылок на справочник
     */
    attributeUsedLookupDisplayableAttributes: function () {
        var viewModel          = this.getViewModel(),
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            metaRecord         = viewModel.get('currentRecord'),
            isAttributeUsed    = false,
            entityDependencies,
            metaAttribute,
            attrPath,
            MetaRecordUtil     = Unidata.util.MetaRecord,
            selection          = attributeTreePanel.getSelection(),
            EntityDependencyType = Unidata.model.entity.EntityDependencyType;

        if (selection.length === 0 || !MetaRecordUtil.isLookup(metaRecord)) {
            return false;
        }

        metaAttribute = selection[0].get('record');
        attrPath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);

        entityDependencies = metaRecord.entityDependency().getRange();
        entityDependencies = Ext.Array.filter(entityDependencies, this.filterDependencies.bind(this, EntityDependencyType.ATTRIBUTE , EntityDependencyType.ATTRIBUTE), this);
        isAttributeUsed = Boolean(this.findEntityDependencyByTargetFullAttributeName(entityDependencies, attrPath));

        return isAttributeUsed;
    },

    /**
     * Вычисляет признак того, что атрибут используется в списках отображаемых атрибутов для ссылок на справочник
     */
    attributeUsedRelationDisplayableAttributes: function () {
        var viewModel          = this.getViewModel(),
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            metaRecord         = viewModel.get('currentRecord'),
            isAttributeUsed    = false,
            entityDependencies,
            metaAttribute,
            attrPath,
            MetaRecordUtil     = Unidata.util.MetaRecord,
            selection          = attributeTreePanel.getSelection(),
            EntityDependencyType = Unidata.model.entity.EntityDependencyType;

        if (selection.length === 0 || !MetaRecordUtil.isEntity(metaRecord)) {
            return false;
        }

        metaAttribute = selection[0].get('record');
        attrPath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);

        entityDependencies = metaRecord.entityDependency().getRange();
        entityDependencies = Ext.Array.filter(entityDependencies, this.filterDependencies.bind(this, EntityDependencyType.RELATION , EntityDependencyType.ATTRIBUTE), this);
        isAttributeUsed = Boolean(this.findEntityDependencyByTargetFullAttributeName(entityDependencies, attrPath));

        return isAttributeUsed;
    },

    attributeUsedConsolidation: function () {
        var viewModel,
            currentRecord,
            attributeRecord,
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            isAttributeUsed    = false,
            mergeSettings,
            bvtMergeSettings,
            bvtAttributes;

        viewModel     = this.getViewModel();
        currentRecord = viewModel.get('currentRecord');

        if (attributeTreePanel.getSelection().length === 0) {
            return false;
        }

        attributeRecord = attributeTreePanel.getSelection()[0].get('record');

        if (Unidata.util.MetaRecord.isEntity(currentRecord)) {
            mergeSettings = currentRecord.getMergeSettings();

            if (mergeSettings) {
                bvtMergeSettings = mergeSettings.getBvtMergeSettings();
                bvtAttributes    = bvtMergeSettings.attributes();

                bvtAttributes.each(function (bvtAttribute) {
                    isAttributeUsed = isAttributeUsed || bvtAttribute.get('name') === attributeRecord.get('name');
                });
            }
        }

        return isAttributeUsed;
    },

    attributeUsedAttributeGroup: function () {
        var isAttributeUsed    = false,
            attributeTreePanel = this.lookupReference('attributeTreePanel'),
            viewModel          = this.getViewModel(),
            currentRecord      = viewModel.get('currentRecord'),
            attributeRecord,
            attributeGroups;

        if (!Ext.isFunction(currentRecord.attributeGroups) || attributeTreePanel.getSelection().length === 0) {
            return false;
        }

        attributeGroups = currentRecord.attributeGroups();
        attributeRecord = attributeTreePanel.getSelection()[0].get('record');

        attributeGroups.each(function (attributeGroup) {
            var attributes = attributeGroup.get('attributes');

            if (Ext.Array.contains(attributes, attributeRecord.get('name'))) {
                isAttributeUsed = true;
            }
        });

        return isAttributeUsed;
    },

    refreshAttributePropertyGrid: function () {
        var view = this.getView(),
            propertyGrid = view.attributePropertyGrid,
            gridView = propertyGrid.getView();

        if (gridView) {
            gridView.refresh();
        }
    },

    onSpecialKey: function (field, event) {
        var editor = field.up('editor');

        if (event.getKey() === event.TAB) {
            event.stopEvent();

            if (editor) {
                editor.completeEdit();
            }

            this.processTabKey(event, editor.editorId);

            return false; //без возврата false  editor остается видимым
        }
    },

    getNextPropertyName: function (propertyName) {
        var view = this.getView(),
            propertyGrid = view.attributePropertyGrid,
            store = propertyGrid.getStore(),
            nextPropertyName = null,
            record;

        store.each(function (item, index) {
            var nextIndex = index + 1;

            if (item.get('name') === propertyName) {

                // перепрыгиваем на первую для редактирования
                if (nextIndex >= store.getCount()) {
                    nextIndex = 0 ;
                }

                record = store.getAt(nextIndex);

                if (record) {
                    nextPropertyName = record.get('name');
                }

                return false; // остановка итерации
            }
        });

        return nextPropertyName;
    },

    getPreviousPropertyName: function (propertyName) {
        var view = this.getView(),
            propertyGrid = view.attributePropertyGrid,
            store = propertyGrid.getStore(),
            nextPropertyName = null,
            record;

        store.each(function (item, index) {
            var prevIndex = index - 1;

            if (item.get('name') === propertyName) {

                // перепрыгиваем на последнюю для редактирования
                if (prevIndex < 0) {
                    prevIndex = store.getCount() - 1;
                }

                record = store.getAt(prevIndex);

                if (record) {
                    nextPropertyName = record.get('name');
                }

                return false; // остановка итерации
            }
        });

        return nextPropertyName;
    },

    focusPropertyGridRowByName: function (propertyName, enableEditor) {
        var view = this.getView(),
            propertyGrid = view.attributePropertyGrid,
            gridView = propertyGrid.getView(),
            store = propertyGrid.getStore(),
            plugins = propertyGrid.getPlugins(),
            plugin;

        plugin = Ext.Array.findBy(plugins, function (item) {
            return Ext.getClassName(item) === 'Ext.grid.plugin.CellEditing';
        });

        store.each(function (item, index) {
            if (item.get('name') === propertyName) {
                gridView.select(item);
                gridView.focusRow(index, 0);

                if (enableEditor) {
                    plugin.startEdit(item);
                }

                return false; // остановка итерации
            }
        });
    },

    onAttributePropertyRowKeydown: function (a, b, c, d, event) {
        var currentPropertyName = b.getId();

        if (event.getKey() === event.TAB) {
            event.stopEvent();

            this.processTabKey(event, currentPropertyName);

            return false;
        }
    },

    processTabKey: function (event, currentPropertyName) {
        var propertyName;

        if (event.shiftKey) {
            propertyName = this.getPreviousPropertyName(currentPropertyName);
        } else {
            propertyName = this.getNextPropertyName(currentPropertyName);
        }

        if (propertyName) {
            this.runFocusPropertyGridDelayedTask(10, propertyName, true);
        }
    },

    /**
     * Получить editor для поля "Отображаемые атрибуты"
     *
     * @returns {Ext.grid.CellEditor}
     */
    getLookupEntityDisplayAttributesEditor: function () {
        var view = this.getView(),
            attributePropertyGrid = view.attributePropertyGrid,
            propName = 'lookupEntityDisplayAttributes',
            editor;

        editor = attributePropertyGrid.getConfigProp(propName, 'editor');

        return editor;
    },

    /**
     * Получить editor для поля "Поисковые атрибуты"
     *
     * @returns {Ext.grid.CellEditor}
     */
    getLookupEntitySearchAttributesEditor: function () {
        var view = this.getView(),
            attributePropertyGrid = view.attributePropertyGrid,
            propName = 'lookupEntitySearchAttributes',
            editor;

        editor = attributePropertyGrid.getConfigProp(propName, 'editor');

        return editor;
    },

    getAllEditors: function () {
        var editors = [];

        editors.push(this.getLookupEntityDisplayAttributesEditor());
        editors.push(this.getLookupEntitySearchAttributesEditor());

        return editors;
    },

    /**
     * Подгрузить метамодель для Lookup
     *
     * @param metaRecordKey
     */
    changeLookupMetaRecord: function (metaRecordKey) {
        var AttributeTagField = Unidata.view.component.AttributeTagField,
            view = this.getView(),
            viewModel = this.getViewModel(),
            me = this,
            attributePropertyGrid = view.attributePropertyGrid,
            displayAttributesEditor,
            searchAttributesEditor,
            displayTagfield,
            searchTagfield;

        displayAttributesEditor = this.getLookupEntityDisplayAttributesEditor();
        searchAttributesEditor = this.getLookupEntitySearchAttributesEditor();
        displayTagfield = displayAttributesEditor.field;
        searchTagfield = searchAttributesEditor.field;

        if (displayTagfield) {
            displayTagfield.clearValue();
        }

        if (searchTagfield) {
            searchTagfield.clearValue();
        }

        Unidata.util.api.MetaRecord.getMetaRecord(metaRecordKey).then(function (metaRecord) {
            var defaultValueText;

            defaultValueText = me.buildLookupEntityDefaultValueText(metaRecord, me.attributeProperty.MAIN_DISPLAYABLE);
            me.fillLookupEntityDefaultValueText(defaultValueText);
            viewModel.set('lookupEntityRecord', metaRecord);

            AttributeTagField.fillStore(view.lookupEntityDisplayAttributesStore, metaRecord);
            AttributeTagField.fillStore(view.lookupEntitySearchAttributesStore, metaRecord);

            attributePropertyGrid.getView().refresh();
        }, function () {
            Unidata.showError(view.metaRecordLoadFailedText);
        }).done();
    },

    updateReadOnly: function (readOnly) {
        var viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);
    }
});
