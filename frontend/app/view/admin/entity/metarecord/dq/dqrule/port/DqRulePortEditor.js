/**
 * Редактор правила качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-15
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortEditor', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.port.dqruleporteditor',

    requires: [
        'Unidata.view.component.TreeSearchComboBox',
        'Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortHelpPanel',
        'Unidata.util.upath.UPath',
        'Unidata.view.admin.entity.metarecord.dq.dqrule.ExecutionContextRadioGroup'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        upathValue: null,
        uPath: null,
        metaRecord: null,
        dataType: null,
        isUPathValid: false,
        portApplicationMode: null,
        portType: null,
        useFilter: true,    //необходимость использования фильтра по типам
        executionContext: null,
        executionContextMode: false,
        supportedExecutionContexts: null
    },

    referenceHolder: true,

    attributeTreePanel: null,
    attributeComboBox: null,
    pathInput: null,

    cls: 'un-dq-rule-port-editor',

    viewModelAccessors: ['upathValue'],

    viewModel: {
        data: {
            upathValue: null
        }
    },

    constructor: function (config) {
        // значения по умолчанию
        config.useFilter = Ext.isBoolean(config.useFilter) ? config.useFilter : true;
        config.executionContextMode = Ext.isBoolean(config.executionContextMode) ? config.executionContextMode : false;

        this.callParent(arguments);
    },

    getUPath: function () {
        var UPath,
            metaRecord;

        UPath = this.callParent(arguments);

        if (!UPath) {
            metaRecord = this.getMetaRecord();
            UPath = Ext.create('Unidata.util.upath.UPath', {
                entity: metaRecord
            });
            this.setUPath(UPath);
        }

        return UPath;
    },

    initItems: function () {
        var metaRecord,
            items,
            portApplicationMode,
            executionContext,
            executionContextMode,
            supportedExecutionContexts,
            portApplicationModeHtml;

        this.callParent(arguments);

        portApplicationMode = this.getPortApplicationMode();
        executionContext = this.getExecutionContext();
        executionContextMode = this.getExecutionContextMode();
        supportedExecutionContexts = this.getSupportedExecutionContexts();
        metaRecord = this.getMetaRecord();
        portApplicationModeHtml = this.buildPortApplicationModeHtml(portApplicationMode);

        items  = [
            {
                xtype: 'admin.entity.metarecord.dq.executioncontextradiogroup',
                height: 25,
                fieldLabel: Unidata.i18n.t('admin.dq>executionContextMode'),
                labelWidth: 130,
                margin: '5 10 0 10',
                layout: {
                    type: 'hbox',
                    align: 'left'
                },
                hidden: !executionContextMode,
                executionContext: executionContext,
                supportedExecutionContexts: supportedExecutionContexts,
                listeners: {
                    change: this.onExecutionContextChange.bind(this)
                }
            },
            {
                xtype: 'panel',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                flex: 1,
                items: [
                    {
                        xtype: 'panel',
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        flex: 1,
                        margin: 10,
                        items: [
                            {
                                xtype: 'un.treesearchcombobox',
                                reference: 'attributeComboBox',
                                margin: '0 0 5 0',
                                height: 30
                            },
                            {
                                xtype: 'component.attributeTree',
                                reference: 'attributeTreePanel',
                                flex: 1,
                                overflowY: 'auto',
                                listeners: {
                                    beforerender: 'onAttributeTreeBeforeRender',
                                    render: 'onAttributeTreeRender',
                                    select: 'onAttributeTreeSelect',
                                    deselect: 'onAttributeTreeDeselect',
                                    beforeselect: 'onAttributeTreeBeforeSelect',
                                    viewready: 'onAttributeTreeViewReady',
                                    scope: this
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'panel',
                        layout: {
                            type: 'vbox',
                            align: 'stretch',
                            pack: 'top'
                        },
                        flex: 1,
                        margin: '10 10 10 0',
                        scrollable: 'vertical',
                        items: [
                            {
                                xtype: 'admin.entity.metarecord.dq.port.dqruleportupathfield',
                                metaRecord: metaRecord,
                                reference: 'pathInput',
                                readOnly: true,
                                margin: '0 0 2 0'
                            },
                            {
                                xtype: 'component',
                                reference: 'portApplicationModeLabel',
                                baseCls: 'un-dq-rule-port-warning',
                                html: portApplicationModeHtml,
                                margin: '0 0 10 0'
                            },
                            {
                                xtype: 'textarea',
                                reference: 'upathInput',
                                labelAlign: 'top',
                                fieldLabel: 'Выражение UPath',
                                listeners: {
                                    change: this.onUPathInputChange.bind(this)
                                },
                                bind: {
                                    value: '{upathValue}'
                                },
                                height: 150,
                                margin: 0
                            },
                            {
                                xtype: 'admin.entity.metarecord.dq.port.dqruleporthelppanel',
                                width: 300,
                                margin: '20 0 0 0'
                            }
                        ]
                    }
                ]
            }
        ];

        this.add(items);
        this.initReferences();
        this.attributeTreePanel.on('datacomplete', function () {
            this.useAttributeTreeFilter();
        }, this);
        this.attributeTreePanel.setData(metaRecord);
    },

    buildPortApplicationModeHtml: function (portApplicationMode) {
        return Unidata.util.DataQuality.portApplicationModeLabels[portApplicationMode];
    },

    onExecutionContextChange: function (self, value) {
        this.setExecutionContext(value['execution_context']);
    },

    showNoAttributesInfo: function () {
        this.removeAll();
        this.add(this.createNoAttributeWarning());
        this.fireEvent('attributetreeempty', this);
    },

    /**
     * Создать текст предупреждения об отсутствии атрибутов в tree store
     *
     * @param customCfg {Object|undefined}
     * @return {Ext.container.Container}
     */
    createNoAttributeWarning: function (customCfg) {
        var dataType = this.getDataType(),
            container,
            cfg,
            html,
            cls,
            text,
            dataTypeDisplayName;

        customCfg = customCfg || {};
        cls = 'un-dq-rule-port-editor-no-attributes-warning';
        dataTypeDisplayName = Unidata.model.dataquality.DqRule.getDataTypeDisplayName(dataType);
        text = Ext.String.format('<div>{0}</div><div>{1}</div>', Unidata.i18n.t('admin.dq>entityNotContainAttribute', {dataType: dataTypeDisplayName}), Unidata.i18n.t('admin.dq>attributeMustBeSet'));

        html = Ext.String.format('<div class="{0}">{1}</div>', cls, text);
        cfg = {
            html: html
        };

        cfg = Ext.apply(cfg, customCfg);

        container = Ext.create('Ext.container.Container', cfg);

        return container;
    },

    /**
     * Применить фильтр для дерева
     */
    useAttributeTreeFilter: function () {
        var attributeTreePanel = this.attributeTreePanel,
            store = attributeTreePanel.getStore(),
            dataType = this.getDataType(),
            useFilter = this.getUseFilter(),
            attributeTreeFilters;

        // фильтруем
        if (dataType && useFilter) {
            attributeTreeFilters = this.buildAttributeTreeFilters(dataType);
            store.setFilters(attributeTreeFilters);

            if (this.checkIfStoreEmpty()) {
                this.showNoAttributesInfo();
            }
        }
    },

    /**
     * Проверка есть ли в tree store компоненты определенного типа
     *
     * @return boolean Признак того, что tree store пуст
     */
    checkIfStoreEmpty: function () {
        var DqRuleModel     = Unidata.model.dataquality.DqRule,
            MetaAttributeUtil = Unidata.util.MetaAttribute,
            attributeTreePanel = this.attributeTreePanel,
            store = attributeTreePanel.getStore(),
            dataType = this.getDataType(),
            rootNode = store.getRootNode(),
            notEmpty = false;

        if (dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.ANY && rootNode.childNodes.length > 0) {
            return false;
        }

        // для простых типов должен быть хотя бы один узел типа не комлексный атрибут
        if (dataType !== DqRuleModel.DQ_RULE_PORT_DATA_TYPES.RECORD) {
            notEmpty = Ext.Array.some(store.getRange(), function (item) {
                return !MetaAttributeUtil.isComplexAttribute(item);
            });
        }

        return !notEmpty;
    },

    /**
     * Сформировать фильтры для дерева атрибутов на основании типа данных порта
     *
     * @param dataType
     * @return {*[]|*}
     */
    buildAttributeTreeFilters: function (dataType) {
        var MetaAttributeUtil = Unidata.util.MetaAttribute,
            portType = this.getPortType(),
            DqRuleModel          = Unidata.model.dataquality.DqRule,
            filterFn,
            filters,
            MetaAttribute   = Unidata.util.MetaAttribute,
            simpleDataTypes = [DqRuleModel.DQ_RULE_PORT_DATA_TYPES.STRING,
                DqRuleModel.DQ_RULE_PORT_DATA_TYPES.INTEGER,
                DqRuleModel.DQ_RULE_PORT_DATA_TYPES.BOOLEAN,
                DqRuleModel.DQ_RULE_PORT_DATA_TYPES.NUMBER,
                DqRuleModel.DQ_RULE_PORT_DATA_TYPES.DATE,
                DqRuleModel.DQ_RULE_PORT_DATA_TYPES.TIMESTAMP,
                DqRuleModel.DQ_RULE_PORT_DATA_TYPES.TIME];

        filterFn = function (node) {
            var metaAttribute = node.get('record');

            // не отображаем кодовые атрибуты
            if (portType === Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT &&
                (MetaAttributeUtil.isCodeAttribute(metaAttribute) || MetaAttributeUtil.isAliasCodeAttribute(metaAttribute))) {
                return false;
            }

            if (!metaAttribute) {
                return false;
            }

            if (dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.ANY || MetaAttributeUtil.isComplexAttribute(metaAttribute)) {
                // для any порта можно указать любой атрибут + показываем (пока) все комплексные
                return true;
            } else if (dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.STRING && metaAttribute.isEnumDataType()) {
                // для string порта можно указать enum
                return true;
            } else if ((dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.INTEGER || dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.STRING) &&
                  ((metaAttribute.isLookupEntityType() && metaAttribute.get('lookupEntityCodeAttributeType') === dataType) ||
                  (metaAttribute.isArrayDataType() && !Ext.isEmpty(metaAttribute.get('lookupEntityType')) && metaAttribute.get('lookupEntityCodeAttributeType') === dataType))) {
                // для string или integer порта можно указать lookup, array lookup
                return true;
            } else if (metaAttribute.isSimpleDataType() && Ext.Array.contains(simpleDataTypes, dataType)) {
                // можно выбрать простой атрибут соответствующий типу порта
                return metaAttribute.get('simpleDataType') === dataType;
            } else if (metaAttribute.isArrayDataType() && Ext.Array.contains(simpleDataTypes, dataType)) {
                // можно выбрать массив-атрибут соответствующий типу порта
                return metaAttribute.get('arrayDataType') === dataType;
            } else if (dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.RECORD && MetaAttribute.isComplexAttribute(metaAttribute)) {
                // для типа record можно выбрать комплексный атрибут
                return true;
            }

            return false;
        };

        filters = [
            {
                filterFn: filterFn
            }
        ];

        return filters;
    },

    onAttributeTreeBeforeSelect: function (self, node) {
        var MetaAttributeUtil = Unidata.util.MetaAttribute,
            DqRuleModel = Unidata.model.dataquality.DqRule,
            dataType = this.getDataType(),
            record = node.get('record');

        if (!record) {
            return false;
        } else if (dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.RECORD) {
            // для типа RECORD даем выбрать только весь рекорд или комплексный атрибуты
            return Unidata.util.MetaRecord.isEntity(record) || MetaAttributeUtil.isComplexAttribute(record);
        } else if (dataType === DqRuleModel.DQ_RULE_PORT_DATA_TYPES.ANY) {
            return true;
        } else {
            // если не ANY и не RECORD, то разрешаем выбирать все кроме рекордов и нестедов
            return !Unidata.util.MetaRecord.isEntity(record) && !MetaAttributeUtil.isComplexAttribute(record);
        }
    },

    initReferences: function () {
        this.attributeTreePanel = this.lookupReference('attributeTreePanel');
        this.attributeComboBox = this.lookupReference('attributeComboBox');
        this.pathInput = this.lookupReference('pathInput');
        this.upathInput = this.lookupReference('upathInput');
    },

    onAttributeTreeRender: function () {
        var upathValue = this.getUpathValue();

        Ext.defer(function () {
            this.useUPathValue(upathValue);
        }, 1, this);
    },

    onAttributeTreeBeforeRender: function () {
        var attributeTree = this.attributeTreePanel,
            store = attributeTree.getStore();

        if (store.count()) {
            this.attributeComboBox.setAttributeTree(attributeTree);
        } else {
            this.attributeComboBox.hide();
        }
    },

    applySelectedNode: function (node, freezeUPathInput) {
        var upathInput = this.upathInput,
            path;

        if (this.isNodeMetarecord(node)) {
            path = Unidata.util.upath.UPath.fullRecordPath;
        } else {
            path = node.get('path');
        }

        freezeUPathInput = Ext.isBoolean(freezeUPathInput) ? freezeUPathInput : false;

        if (!freezeUPathInput) {
            upathInput.setValue(path);
        }

        this.updateLayout();
    },

    isNodeMetarecord: function (node) {
        var record = node.get('record');

        return node.isRoot() && Unidata.util.MetaRecord.isEntity(record);
    },

    onAttributeTreeSelect: function (tree, node) {
        this.applySelectedNode(node);
    },

    onAttributeTreeDeselect: function () {
        var pathInput = this.pathInput;

        pathInput.setValue(null);
    },

    useUPathValue: function (upathValue) {
        var uPath = this.getUPath(),
            attributeTreePanel = this.attributeTreePanel,
            pathInput = this.pathInput,
            executionContextMode = this.getExecutionContextMode(),
            selection,
            path = null,
            elements,
            isUPathValid;

        elements = uPath.fromUPath(upathValue);

        if (elements) {
            path = uPath.toCanonicalPath();
        }

        pathInput.setUpathValue(upathValue);
        attributeTreePanel.suspendEvent('select');
        attributeTreePanel.suspendEvent('deselect');
        attributeTreePanel.selectAttributeByPath(path);
        selection = attributeTreePanel.getSelection();

        if (selection.length === 1) {
            this.applySelectedNode(selection[0], true);
        }

        // в режиме executionContextMode разрешается сохранять контекст выполнения без выбора узла дерева
        if (executionContextMode && Ext.isEmpty(upathValue)) {
            isUPathValid = true;
        } else {
            isUPathValid = Boolean(elements) && selection.length === 1;
        }

        this.fireEvent('upathvalidchange', this, isUPathValid, this.getIsUPathValid());
        this.setIsUPathValid(isUPathValid);

        attributeTreePanel.resumeEvent('select');
        attributeTreePanel.resumeEvent('deselect');
    },

    onUPathInputChange: function (self, upathValue) {
        this.useUPathValue(upathValue);
    },

    onAttributeTreeViewReady: function (grid) {
        this.initAttributeTreeTooltip(grid);
    },

    initAttributeTreeTooltip: function (grid) {
        var gridView        = grid.getView();

        this.toolTip = Ext.create('Ext.tip.ToolTip', {
            target: gridView.el,
            delegate: '.x-grid-cell',
            trackMouse: true,
            renderTo: Ext.getBody(),
            listeners: {
                beforeshow: function (tip) {
                    var row,
                        node,
                        displayName,
                        name,
                        tipTemplate,
                        metaAttribute,
                        dataType,
                        tipHtml;

                    if (!gridView.rendered) {
                        return false;
                    }

                    row = tip.triggerElement.parentElement;
                    node = gridView.getRecord(row);

                    if (!node || node.isRoot()) {
                        return false;
                    }

                    metaAttribute = node.get('record');

                    displayName = metaAttribute.get('displayName');
                    name = metaAttribute.get('name');
                    dataType = Unidata.util.MetaAttributeFormatter.buildDataTypeDisplayValue(metaAttribute);

                    tipTemplate = Ext.create('Ext.Template', [
                        '{displayName:htmlEncode}',
                        '<span class="un-dataentity-attribute-tip-title"><i>{type:htmlEncode}</i></span>',
                        '<br/>',
                        '<b>{name:htmlEncode}</b>'
                    ]);
                    tipTemplate.compile();
                    tipHtml = tipTemplate.apply({
                        displayName: displayName,
                        name: name,
                        type: dataType
                    });

                    tip.update(tipHtml);
                }
            }
        });
    }
});
