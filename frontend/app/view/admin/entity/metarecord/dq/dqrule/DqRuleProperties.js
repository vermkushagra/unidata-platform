/**
 * Секция настройки основных свойств экрана настройки правил качества данных
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleProperties', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.dqruleprops',

    controller: 'admin.entity.metarecord.dq.dqruleprops',
    viewModel: {
        type: 'admin.entity.metarecord.dq.dqruleprops'
    },

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.dqrule.DqRulePropertiesController',
        'Unidata.view.admin.entity.metarecord.dq.dqrule.DqRulePropertiesModel'
    ],

    cls: 'un-dq-rule-properties',

    config: {
        dqRule: null,
        dqRuleActive: false,
        sourceSystems: null
    },

    title: Unidata.i18n.t('admin.dq>mainConfig'),

    viewModelAccessors: ['dqRule', 'dqRuleActive'],

    methodMapper: [
        {
            method: 'buildSourceSystemCheckboxes'
        },
        {
            method: 'getSourceSystemNames'
        },
        {
            method: 'onSourceSystemCheckBoxGroupChange'
        },
        {
            method: 'handleSourceSystemCheckboxes'
        }
    ],

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    masterDataCheckbox: null,
    sourceSystemCheckBoxGroup: null,
    allOriginsCheckbox: null,
    dqRuleName: null,
    activeToggleButton: null,
    runTypeCombo: null,

    referenceHolder: true,

    initItems: function () {
        var DqRule = Unidata.model.dataquality.DqRule,
            DQ_RULE_RUN_TYPES = DqRule.DQ_RULE_RUN_TYPES,
            items,
            sourceSystems,
            dqRule,
            origins,
            fieldName = 'origins',
            all,
            dqRuleSourceSystems,
            dqRuleActive = this.getDqRuleActive(),
            masterData,
            runType,
            originsValue = null;

        this.callParent(arguments);

        sourceSystems = this.getSourceSystems();
        dqRule = this.getDqRule();
        runType = dqRule.get('runType');
        origins = dqRule.getOrigins();
        masterData = dqRule.get('masterData');

        if (!Ext.Array.contains(DqRule.DQ_RULE_ACTIVE_RUN_TYPES, runType)) {
            runType = DQ_RULE_RUN_TYPES.RUN_ON_REQUIRED_PRESENT; // default runType
        }

        if (origins) {
            originsValue = {
                origins: []
            };

            all = origins.get('all');

            if (all) {
                originsValue.origins.push('all');
            } else {
                dqRuleSourceSystems = origins.get('sourceSystems');
                originsValue.origins = originsValue.origins.concat(dqRuleSourceSystems);
            }

        }

        items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'vbox',
                    align: 'top'
                },
                flex: 2,
                margin: '10 10 10 10',
                defaults: {
                    labelWidth: 160
                },
                items: [
                    {
                        xtype: 'textfield',
                        reference: 'dqRuleName',
                        fieldLabel: Unidata.i18n.t('admin.metamodel>ruleName'),
                        width: '100%',
                        bind: {
                            value: '{dqRule.name}',
                            disabled: '{!dqRule}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        },
                        modelValidation: true,
                        msgTarget: 'under'
                    },
                    {
                        xtype: 'un.tagtextarea',
                        cls: 'un-dq-rule-description',
                        reference: 'dqFieldDescription',
                        fieldLabel: Unidata.i18n.t('glossary:description'),
                        width: '100%',
                        height: 90,
                        padding: 0,
                        margin: '5 0 10 0',
                        bind: {
                            value: '{dqRule.description}',
                            disabled: '{!dqRule}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        }
                    },
                    {
                        xtype: 'keyvalue.input',
                        fieldLabel: Unidata.i18n.t('admin.metamodel>customProperties'),
                        name: 'customProperties',
                        width: '100%',
                        bind: {
                            gridStore: '{dqRule.customProperties}',
                            readOnly: '{dqRuleEditorReadOnly}'
                        }
                    }
                ]
            },
            {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                flex: 1,
                margin: '10 10 10 10',
                items: [
                    this.buildRunTypeComboCfg({
                        value: runType,
                        reference: 'runTypeCombo',
                        disabled: true, //by default
                        bind: {
                            disabled: '{dqRuleRunTypeDisabled}'
                        },
                        listeners: {
                            change: this.onRunTypeComboChange.bind(this)
                        }
                    }),
                    {
                        xtype: 'fieldset',
                        title: Unidata.i18n.t('admin.metamodel>uses'),
                        flex: 1,
                        margin: 0,
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        },
                        scrollable: true,
                        sourceSystems: sourceSystems,
                        items: [
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'hbox',
                                    align: 'stretch'
                                },
                                cls: 'un-dq-rule-properties-use-special',
                                padding: 0,
                                height: 20,
                                margin: '0 15 0 4',
                                items: [
                                    {
                                        xtype: 'checkbox',
                                        boxLabel: Unidata.i18n.t('admin.metamodel>masterData'),
                                        reference: 'masterDataCheckbox',
                                        name: 'topping',
                                        inputValue: 'MASTER_DATA',
                                        checked: masterData,
                                        flex: 1,
                                        padding: 0,
                                        margin: 0,
                                        listeners: {
                                            change: 'onMasterDataCheckboxChange'
                                        },
                                        bind: {
                                            readOnly: '{dqRuleEditorReadOnly}'
                                        }
                                    },
                                    {
                                        xtype: 'checkbox',
                                        reference: 'allOriginsCheckbox',
                                        boxLabel: Unidata.i18n.t('admin.metamodel>allSystems'),
                                        name: fieldName,
                                        checked: all,
                                        flex: 1,
                                        inputValue: 'all',
                                        padding: 0,
                                        margin: 0,
                                        listeners: {
                                            change: 'onAllOriginsCheckboxChange'
                                        },
                                        bind: {
                                            readOnly: '{dqRuleEditorReadOnly}'
                                        }
                                    }
                                ]
                            },
                            {
                                xtype: 'checkboxgroup',
                                reference: 'sourceSystemCheckBoxGroup',
                                cls: 'un-dq-rule-properties-source-systems',
                                margin: '5 10 0 0',
                                padding: '5 0 0 0',
                                columns: 2,
                                vertical: false,
                                bind: {
                                    hidden: '{!dqRule}'
                                }
                            }
                        ]
                    }
                ]
            }
        ];

        this.add(items);
        this.initReferences();
        this.buildSourceSystemCheckboxes(dqRuleSourceSystems);

        if (all) {
            this.handleSourceSystemCheckboxes(originsValue);
        } else {
            this.sourceSystemCheckBoxGroup.setValue({
                origins: dqRuleSourceSystems
            });
        }
        this.sourceSystemCheckBoxGroup.on('change', this.onSourceSystemCheckBoxGroupChange, this);
        this.relayEvents(this.dqRuleName, ['change'], 'dqrulename');
        this.calcAndUseDqRuleRunType(dqRuleActive, runType);
    },

    buildRunTypeComboCfg: function (customCfg) {
        var cfg,
            store,
            data;

        data = this.buildRunTypeStoreData();
        store = this.buildRunTypeStoreCfg({
            data: data
        });
        cfg = {
            xtype: 'combo',
            msgTarget: 'under',
            queryMode: 'local',
            fieldLabel: Unidata.i18n.t('admin.dq>runCondition'),
            labelWidth: 120,
            editable: false,
            valueField: 'name',
            displayField: 'displayName',
            store: store
        };
        cfg = Ext.apply(cfg, customCfg);

        return cfg;
    },

    buildRunTypeStoreCfg: function (customCfg) {
        var cfg;

        cfg = {
            fields: ['name', 'displayName']
        };

        cfg = Ext.apply(cfg, customCfg);

        return cfg;
    },

    buildRunTypeStoreData: function () {
        var data;

        data = [
            {
                name: 'RUN_ALWAYS',
                displayName: Unidata.i18n.t('admin.dq>runTypeAlways')
            },
            {
                name: 'RUN_ON_REQUIRED_PRESENT',
                displayName: Unidata.i18n.t('admin.dq>runTypeRequiredPresent')
            },
            {
                name: 'RUN_ON_ALL_PRESENT',
                displayName: Unidata.i18n.t('admin.dq>runTypeRequiredAll')
            }
        ];

        return data;
    },

    initComponent: function () {
        var dqRule = this.config.dqRule,
            dqRuleActive;

        if (dqRule) {
            this.setDqRuleActive(dqRule.get('active'));
        }

        dqRuleActive = this.getDqRuleActive();

        this.activeToggleButton = Ext.create('Unidata.view.admin.entity.metarecord.dq.dqrule.ToggleButton', {
                fieldLabel: Unidata.i18n.t('admin.dq>dqRuleActive'),
                labelWidth: 150,
                pressed: dqRuleActive,
                disabled: true, // by default
                // reference: 'activeToggleButton',
                bind: {
                    pressed: '{dqRuleActive}',
                    disabled: '{dqRuleEditorReadOnly}'
                },
                listeners: {
                    toggle: this.onDqRuleActiveButtonToggle.bind(this)
                }
            }
        );

        this.header = {
            items: [this.activeToggleButton]
        };

        this.callParent(arguments);
    },

    initReferences: function () {
        this.sourceSystemCheckBoxGroup = this.lookupReference('sourceSystemCheckBoxGroup');
        this.masterDataCheckbox = this.lookupReference('masterDataCheckbox');
        this.allOriginsCheckbox = this.lookupReference('allOriginsCheckbox');
        this.dqRuleName = this.lookupReference('dqRuleName');
        this.runTypeCombo = this.lookupReference('runTypeCombo');
        this.on('collapse', this.onPanelCollapse.bind(this));
        this.on('expand', this.onPanelExpand.bind(this));
    },

    onDqRuleActiveButtonToggle: function (self) {
        var dqRule = this.getDqRule(),
            runTypeCombo = this.runTypeCombo,
            runType;

        dqRule.set('active', self.pressed);
        runType = runTypeCombo.getValue();
        this.calcAndUseDqRuleRunType(self.pressed, runType);
    },

    onRunTypeComboChange: function (self, value) {
        var activeToggleButton = this.activeToggleButton;

        this.calcAndUseDqRuleRunType(activeToggleButton.pressed, value);
    },

    calcAndUseDqRuleRunType: function (active, runType) {
        var dqRule = this.getDqRule();

        this.runTypeCombo.setDisabled(!active);

        if (!active) {
            runType = Unidata.model.dataquality.DqRule.DQ_RULE_RUN_TYPES.RUN_NEVER;
        }

        dqRule.set('runType', runType);
    },

    buildTitle: function () {
        var collapsed     = this.getCollapsed(),
            dqRule        = this.getDqRule(),
            title = Unidata.i18n.t('admin.dq>mainConfig'),
            name          = dqRule.get('name'),
            origins       = dqRule.getOrigins(),
            all           = origins.get('all'),
            sourceSystems = origins.get('sourceSystems'),
            applicable    = dqRule.get('applicable'),
            sourceSystemsInfoText,
            parts         = [],
            titleInfo;

        if (collapsed) {
            if (all) {
                sourceSystemsInfoText = '(выбраны все системы-источники';
            } else if (sourceSystems.length > 0) {
                sourceSystemsInfoText = '(выбрано систем-источников ' + sourceSystems.length;
            } else {
                sourceSystemsInfoText = '(не выбрано систем-источников';
            }

            if (Ext.isArray(applicable) && Ext.Array.contains(applicable, 'ETALON')) {
                sourceSystemsInfoText = sourceSystemsInfoText + ', ' + Unidata.i18n.t('admin.dq>masterData').toLowerCase();
            }

            sourceSystemsInfoText = sourceSystemsInfoText + ')';

            if (name) {
                parts.push(name);
            }

            parts.push(sourceSystemsInfoText);

            titleInfo = parts.join(' ');

            title = Ext.String.format('{0}: <span class="un-dq-rule-title-info">{1}</span>', title, titleInfo);
        }

        return title;
    },

    buildAndUpdateTitle: function () {
        var title = this.buildTitle();

        this.setTitle(title);
    },

    onPanelCollapse: function () {
        this.buildAndUpdateTitle();
    },

    onPanelExpand: function () {
        this.buildAndUpdateTitle();
    }
});
