/**
 * Секция настройки функции редактора правил качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleFunction', {
    extend: 'Ext.panel.Panel',

    requires: ['Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortPanel'],

    alias: 'widget.admin.entity.metarecord.dq.dqrulefunction',

    config: {
        dqRule: null,
        metaRecord: null,
        cleanseFunctions: null,
        cleanseFunction: null,
        cleanseFunctionPath: null
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-dq-rule-function',
    title: Unidata.i18n.t('admin.dq>dqRuleConfig'),
    referenceHolder: true,

    viewModel: {
        data: {
            cleanseFunction: null
        }
    },

    viewModelAccessors: ['cleanseFunction'],

    cleanseFunctionPickerField: null,
    dqRulePortPanel: null,
    cbGroupValidation: null,
    dqRuleSelectWarning: null,
    cbGroupEnrich: null,
    executionContextLabel: null,

    initItems: function () {
        var DqRuleModel = Unidata.model.dataquality.DqRule,
            view        = this,
            items,
            cleanseFunctions,
            attributeTree,
            dqRule,
            cleanseFunction,
            executionContextPath,
            executionContext,
            executionContextLabelHtml,
            supportedExecutionContexts,
            metaRecord;

        this.callParent(arguments);
        dqRule                    = this.getDqRule();
        cleanseFunction           = this.getCleanseFunction();
        cleanseFunctions          = this.getCleanseFunctions();
        executionContextPath      = dqRule.get('executionContextPath');
        executionContext          = dqRule.get('executionContext');
        executionContextLabelHtml = this.buildExecutionContextLabelHtml(executionContext);
        metaRecord = this.getMetaRecord();

        if (cleanseFunction) {
            supportedExecutionContexts = cleanseFunction.get('supportedExecutionContexts');
        }

        items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'vbox',
                    align: 'middle'
                },
                padding: '10 0 0 0',
                items: [
                    {
                        xtype: 'warning-message',
                        reference: 'dqRuleSelectWarning',
                        width: 600,
                        iconHtml: Unidata.util.Icon.getLinearIcon('warning'),
                        text: '',
                        padding: 0,
                        hidden: true
                    }
                    ]
            },
            {
                xtype: 'panel',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                margin: '5 10 5 10',
                items: [
                    {
                        xtype: 'panel',
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        flex: 5,
                        margin: '0 17 0 0',
                        items: [
                            {
                                xtype: 'pickerfield',
                                fieldLabel: Unidata.i18n.t('admin.metamodel>function'),
                                // maxWidth: 730,
                                labelWidth: 150,
                                flex: 2,
                                editable: false,
                                reference: 'cleanseFunctionPickerField',
                                modelValidation: true,
                                msgTarget: 'under',
                                margin: '0 0 10 0',
                                bind: {
                                    value: '{dqRule.cleanseFunctionName}',
                                    disabled: '{!dqRule}',
                                    readOnly: '{dqRuleEditorReadOnly}'
                                },
                                createPicker: function () {
                                    var me = this,
                                        picker;

                                    picker = new Ext.panel.Panel({
                                        pickerField: me,
                                        floating: true,
                                        hidden: true,
                                        ownerCt: this.ownerCt,
                                        renderTo: document.body,
                                        anchor: '100%',
                                        height: 300,
                                        referenceHolder: true,
                                        overflowY: 'auto',
                                        items: [
                                            {
                                                xtype: 'component.attributeTree',
                                                reference: 'attributeTree'
                                            }
                                        ]
                                    });

                                    attributeTree = picker.lookupReference('attributeTree');
                                    attributeTree.setData(cleanseFunctions);
                                    view.relayEvents(attributeTree, ['itemclick'], 'cleansefunction');
                                    picker.on('show', view.onCleanseFunctionPickerShow.bind(view));

                                    return picker;
                                }
                            },
                            {

                                xtype: 'admin.entity.metarecord.dq.port.dqruleportupathfield',
                                // width: '100%',
                                fieldLabel: 'Контекст выполнения',
                                // maxWidth: 730,
                                margin: '0 0 2 0',
                                labelAlign: 'left',
                                labelWidth: 150,
                                reference: 'executionContextPathInput',
                                msgTarget: 'under',
                                metaRecord: metaRecord,
                                useFilter: false,
                                dataType: DqRuleModel.DQ_RULE_PORT_DATA_TYPES.RECORD,
                                upathValue: executionContextPath,
                                executionContext: executionContext,
                                executionContextMode: true,
                                supportedExecutionContexts: supportedExecutionContexts,
                                bind: {
                                    readOnly: '{dqRuleEditorReadOnly}',
                                    disabled: '{!cleanseFunction}'
                                },
                                listeners: {
                                    upathvaluechange: 'onExecutionContextPathChange',
                                    executioncontextchange: 'onExecutionContextChange',
                                    scope: this
                                }
                            },
                            {
                                xtype: 'component',
                                reference: 'executionContextLabel',
                                baseCls: 'un-dq-rule-port-warning',
                                margin: '0 0 0 157',
                                html: executionContextLabelHtml
                            }
                        ]
                    },
                    {
                        xtype: 'checkboxgroup',
                        reference: 'dqTypeCBGroup',
                        msgTarget: 'under',
                        validateOnChange: false,
                        layout: {
                            type: 'hbox',
                            align: 'left'
                        },
                        flex: 2,
                        items: [
                            {
                                boxLabel: Unidata.i18n.t('common:validation'),
                                reference: 'cbGroupValidation',
                                margin: 0,
                                padding: 0,
                                bind: {
                                    disabled: '{!dqRule}',
                                    value: '{dqRule.isValidation}',
                                    readOnly: '{dqRuleEditorReadOnly}'
                                }
                            },
                            {
                                boxLabel: Unidata.i18n.t('admin.metamodel>enrich'),
                                reference: 'cbGroupEnrich',
                                margin: '0 0 0 10',
                                padding: 0,
                                bind: {
                                    disabled: '{!dqRule}',
                                    value: '{dqRule.isEnrichment}',
                                    readOnly: '{dqRuleEditorReadOnly}'
                                }
                            }
                        ]
                        // listeners: {
                        //     change: 'onDqTypeCBGroupChange'
                        // }
                    }
                ]
            },
            {
                xtype: 'admin.entity.metarecord.dq.port.dqruleportpanel',
                flex: 1,
                reference: 'dqRulePortPanel',
                dqRule: dqRule,
                cleanseFunction: cleanseFunction,
                metaRecord: metaRecord
            }
        ];

        this.add(items);
        this.initReferences();
        this.initPanelEvents();
        this.relayPortEvents();
    },

    buildExecutionContextLabelHtml: function (executionContext) {
        var DataQualityUtil = Unidata.util.DataQuality,
            html;

        switch (executionContext) {
            case DataQualityUtil.executionContextEnumList.GLOBAL:
                html = Unidata.i18n.t('admin.dq>executionContextGlobalFull');
                break;
            case DataQualityUtil.executionContextEnumList.LOCAL:
                html = Unidata.i18n.t('admin.dq>executionContextLocalFull');
                break;
        }

        return html;
    },

    relayPortEvents: function () {
        this.relayEvents(this.dqRulePortPanel, ['portupathchanged']);
        this.relayEvents(this.cbGroupValidation, ['change'], 'isvalidation');
        this.relayEvents(this.cbGroupEnrich, ['change'], 'isenrich');
    },

    initReferences: function () {
        this.cleanseFunctionPickerField = this.lookupReference('cleanseFunctionPickerField');
        this.dqRulePortPanel = this.lookupReference('dqRulePortPanel');
        this.dqRuleSelectWarning = this.lookupReference('dqRuleSelectWarning');
        this.cbGroupValidation = this.lookupReference('cbGroupValidation');
        this.cbGroupEnrich = this.lookupReference('cbGroupEnrich');
        this.executionContextPathInput = this.lookupReference('executionContextPathInput');
        this.executionContextLabel = this.lookupReference('executionContextLabel');
    },

    initPanelEvents: function () {
        this.on('collapse', this.onPanelCollapse.bind(this));
        this.on('expand', this.onPanelExpand.bind(this));
    },

    updateCleanseFunction: function (cleanseFunction) {
        var dqRulePortPanel = this.dqRulePortPanel,
            dqRule = this.getDqRule(),
            executionContextPathInput = this.executionContextPathInput,
            executionContext,
            supportedExecutionContexts;

        if (!dqRulePortPanel) {
            return;

        }
        this.clearExecutionContext();
        dqRulePortPanel.setCleanseFunction(cleanseFunction);

        if (cleanseFunction) {
            supportedExecutionContexts = cleanseFunction.get('supportedExecutionContexts');
            executionContext = dqRule.get('executionContext');

            if (supportedExecutionContexts && !Ext.Array.contains(supportedExecutionContexts, executionContext)) {
                this.useExecutionContext(supportedExecutionContexts[0]);
            }
        }
        executionContextPathInput.setSupportedExecutionContexts(supportedExecutionContexts);
    },

    clearExecutionContext: function () {
        var executionContextPathInput = this.executionContextPathInput,
            dqRule = this.getDqRule();

        executionContextPathInput.clearValue(true);
        dqRule.set('executionContextPath', null);
        dqRule.set('executionContext', null);

        return executionContextPathInput;
    },

    updateCleanseFunctionPath: function (cleanseFunctionPath) {
        if (!this.cleanseFunctionPickerField) {
            return;
        }

        this.cleanseFunctionPickerField.setValue(cleanseFunctionPath);

        if (!this.cleanseFunctionPickerField.picker) {
            return;
        }

        this.cleanseFunctionPickerField.picker.setHidden(true);
    },

    onCleanseFunctionPickerShow: function (picker) {
        var attributeTree = picker.lookupReference('attributeTree'),
            value = this.cleanseFunctionPickerField.getValue();

        Ext.defer(function () {
            attributeTree.selectCleanseFunctionByName(value);
            this.setLoading(false);
        }, 1, this);
    },

    buildTitle: function () {
        var collapsed = this.getCollapsed(),
            dqRule = this.getDqRule(),
            title = Unidata.i18n.t('admin.dq>dqRuleConfig'),
            cleanseFunctionName = dqRule.get('cleanseFunctionName'),
            isValidation = dqRule.get('isValidation'),
            isEnrichment = dqRule.get('isEnrichment'),
            dqRuleTypeInfoText,
            dqRuleTypeInfoParts = [],
            parts = [],
            titleInfo;

        if (collapsed) {
            if (isValidation) {
                dqRuleTypeInfoParts.push(Unidata.i18n.t('admin.dq>validation').toLowerCase());
            }

            if (isEnrichment) {
                dqRuleTypeInfoParts.push(Unidata.i18n.t('admin.dq>enrichment').toLowerCase());
            }

            if (dqRuleTypeInfoParts.length > 0) {
                dqRuleTypeInfoText = Ext.String.format('({0})', dqRuleTypeInfoParts.join(', '));
            }

            if (cleanseFunctionName) {
                parts.push(cleanseFunctionName);
            }

            if (dqRuleTypeInfoText) {
                parts.push(dqRuleTypeInfoText);
            }

            titleInfo = parts.join(' ');

            if (titleInfo) {
                title = Ext.String.format('{0}: <span class="un-dq-rule-title-info">{1}</span>', title, titleInfo);
            }
        }

        return title;
    },

    buildAndUpdateTitle: function () {
        var title = this.buildTitle();

        this.setTitle(title);
    },

    getValidationWarningTemplate: function () {
        var html;

        html = '<div>' +
        '<b>' + Unidata.i18n.t('admin.dq>validationCannotBeApplied') + '</b></br>' +
        '<div>' + Unidata.i18n.t('admin.dq>forRuleSave') + '</div>' +
        '<div>' +
        '<ul style="margin : 0">' +
        '<li>' + Unidata.i18n.t('admin.dq>selectFunctionWithBooleanOutputPort') + '</li>' +
        '<li>' + Unidata.i18n.t('admin.dq>addBooleanOutputPort') + '</li>' +
        '</ul>' +
        '</div>' +
        '</div>';

        return html;
    },
    getEnrichWarningTemplate: function () {
        var html;

        html = '<div>' +
            '<b>' + Unidata.i18n.t('admin.dq>enrichCannotBeApplied') + '</b></br>' +
            '<div>' + Unidata.i18n.t('admin.dq>forRuleSave') + '</div>' +
            '<div>' +
            '<ul style="margin : 0">' +
            '<li>' + Unidata.i18n.t('admin.dq>selectFunction') + '</li>' +
            '<li>' + Unidata.i18n.t('admin.dq>addOutputPort') + '</li>' +
            '</ul>' +
            '</div>' +
            '</div>';

        return html;
    },

    onPanelCollapse: function () {
        this.buildAndUpdateTitle();
    },

    onPanelExpand: function () {
        this.buildAndUpdateTitle();
    },

    /**
     * Обработчик события измемнения executionContextPath
     *
     * @param self
     * @param uPathValue
     * @param oldUPathValue
     */
    onExecutionContextPathChange: function (self, uPathValue) {
        var metaRecord = this.getMetaRecord(),
            dqRule = this.getDqRule(),
            oldUPathValue = dqRule.get('executionContextPath'),
            canonicalPath,
            oldCanonicalPath,
            UPath;

        UPath    = Ext.create('Unidata.util.upath.UPath', {
            entity: metaRecord
        });

        UPath.fromUPath(oldUPathValue);
        oldCanonicalPath = UPath.toCanonicalPath();
        UPath.fromUPath(uPathValue);

        canonicalPath = UPath.toCanonicalPath();
        dqRule.set('executionContextPath', uPathValue);

        if (canonicalPath !== oldCanonicalPath) {
            this.fireEvent('executioncontextpathchanged', this, dqRule, canonicalPath, oldCanonicalPath);
        }
    },

    useExecutionContext: function (executionContext) {
        var dqRule = this.getDqRule(),
            executionContextLabelHtml;

        executionContextLabelHtml = this.buildExecutionContextLabelHtml(executionContext);
        dqRule.set('executionContext', executionContext);

        if (this.executionContextLabel) {
            this.executionContextLabel.setHtml(executionContextLabelHtml);
        }

        if (this.executionContextPathInput) {
            this.executionContextPathInput.setExecutionContext(executionContext);
        }
    },

    onExecutionContextChange: function (self, executionContext) {
        this.useExecutionContext(executionContext);
    }
});
