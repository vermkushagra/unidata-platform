/**
 * Навигация по списку правил качества
 *
 * @author Ivan Marshalkin
 * @date 2018-01-29
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigation', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigationController',
        'Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigationModel',

        'Unidata.view.admin.entity.metarecord.dq.testwizard.DqTestWizardWnd'
    ],

    alias: 'widget.admin.entity.metarecord.dq.dqnavigation',

    controller: 'admin.entity.metarecord.dq.dqnavigation',

    viewModel: {
        type: 'admin.entity.metarecord.dq.dqnavigation'
    },

    referenceHolder: true,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    statics: {
        filterType: {
            NONE: 'NONE',                                             // без фильтрации
            SPECIAL: 'SPECIAL',                                       // только системные правила
            NONSPECIAL: 'NONSPECIAL'                                  // только правила созданные пользователем
        },
        DQ_RULE_RUN_TYPES_DISPLAY_SHORT: {
            RUN_ALWAYS: Unidata.i18n.t('admin.dq>runTypeAlwaysShort'),
            RUN_NEVER: Unidata.i18n.t('admin.dq>runTypeNeverShort'),
            RUN_ON_REQUIRED_PRESENT: Unidata.i18n.t('admin.dq>runTypeRequiredPresentShort'),
            RUN_ON_ALL_PRESENT: Unidata.i18n.t('admin.dq>runTypeRequiredAllShort')
        }

    },

    config: {
        metaRecord: null,
        readOnly: null,
        specialFilterType: null,
        dqTestMode: false
    },

    viewModelAccessors: ['metaRecord'],

    methodMapper: [
        {
            method: 'refreshViewByDqRule'
        },
        {
            method: 'updateSpecialFilterType'
        },
        {
            method: 'updateReadOnly'
        },
        {
            method: 'updateDqTestMode'
        },
        {
            method: 'refreshAttributeTree'
        }
    ],

    dqList: null,
    dqListFooterContainer: null,
    attributeTree: null,
    attributeComboBox: null,
    showDqTestWizardButton: null,

    cls: 'un-dq-nav',
    scrollable: 'vertical',

    header: {
        items: [
            {
                xtype: 'tbspacer',
                flex: 1
            },
            {
                xtype: 'button',
                reference: 'createDqButton',
                scale: 'small',
                color: 'lightgray2',
                text: '+ ' + Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:rule')}),
                listeners: {
                    click: 'onCreateDqButtonClick'
                },
                hidden: true,
                bind: {
                    hidden: '{readOnly}',
                    disabled: '{!isCreateDqRuleEnabled}'
                }
            },
            {
                xtype: 'button',
                reference: 'testDqModeButton',
                scale: 'small',
                color: 'lightgray2',
                margin: '0 0 0 10',
                text: Unidata.i18n.t('admin.dqtest>testDqModeButtonText'),
                listeners: {
                    click: 'onTestDqModeButtonClick'
                },
                disabled: true,
                bind: {
                    disabled: '{!isTestDqModeButtonEnabled}'
                }
            }
        ]
    },

    initItems: function () {
        var items,
            runTypeColumnCfg;

        this.callParent(arguments);

        runTypeColumnCfg = this.buildRunTypeColumnCfg();

        items = [
          {
              xtype: 'container',
              padding: 10,
              layout: {
                  type: 'vbox',
                  align: 'stretch'
              },
              flex: 1,
              items: [
                  {
                      xtype: 'un.treesearchcombobox',
                      reference: 'attributeComboBox',
                      height: 30,
                      padding: '0 0 10 0'
                  },
                  {
                      xtype: 'component.attributeTree',
                      reference: 'attributeTree',
                      cls: 'attribute-tree-component un-dq-nav-attribute',
                      flex: 1,
                      viewConfig: {
                          preserveScrollOnRefresh: true
                      },
                      bind: {
                          data: {
                              bindTo: '{metaRecord}',
                              deep: true
                          }
                      },
                      listeners: {
                          beforedeselect: 'onAttributeTreeBeforeDeselect',
                          selectionchange: 'onAttributeTreeSelectionChange',
                          select: 'onAttributeTreeNodeSelect',
                          beforerender: 'onAttributeTreeBeforeRender',
                          datacomplete: 'onAttributeTreeDataComplete'
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
              items: [
                  {
                      xtype: 'grid',
                      reference: 'dqList',
                      cls: 'un-dq-nav-list',
                      padding: 10,
                      flex: 1,
                      bind: {
                          store: '{filteredDqRules}'
                      },
                      selModel: {
                          selType: 'checkboxmodel',
                          checkOnly: false,
                          pruneRemoved: false,
                          injectCheckbox: 'last'
                      },
                      viewConfig: {
                          markDirty: false,
                          preserveScrollOnRefresh: true,
                          plugins: {
                              ptype: 'gridviewdragdrop',
                              pluginId: 'ddplugin',
                              dragText: Unidata.i18n.t('admin.metamodel>selectNewPosition'),
                              containerScroll: true
                          },
                          listeners: {
                              drop: 'onDataQualityDrop'
                          },
                          getRowClass: function (record) {
                              var cls = [];

                              if (record.get('special')) {
                                  cls.push('un-dq-rule-special');
                              }

                              if (record.phantom) {
                                  cls.push('un-dq-rule-phantom');
                              } else if (record.checkDirty()) {
                                  cls.push('un-dq-rule-dirty');
                              }

                              return cls.join(' ');
                          }
                      },
                      columns: [
                          {
                              xtype: 'un.actioncolumn',
                              hideable: false,
                              width: 25,
                              hidden: true,
                              bind: {
                                  hidden: '{readOnly}'
                              },
                              items: [
                                  {
                                      iconCls: 'un-dq-acolumn-move icon-move'
                                  }
                              ]
                          },
                          {
                              dataIndex: 'order',
                              sortable: false,
                              align: 'right',
                              menuDisabled: true,
                              width: 40,
                              renderer: function (value, metaData, record) {
                                  return record.get('order') + 1;
                              }
                          },
                          {
                              text: Unidata.i18n.t('glossary:name'),
                              plugins: [
                                  {
                                      ptype: 'grid.column.headeritemswitcher',
                                      pluginId: 'headerSwitcherPlugin',
                                      mode: 'TEXT'
                                  }
                              ],
                              dataIndex: 'name',
                              sortable: false,
                              resizable: true,
                              menuDisabled: true,
                              flex: 3,
                              renderer: function (value, metaData, record) {
                                  var html;

                                  html = '<div class="un-dq-namecol-wrap"><span class="un-dq-namecol-text">' +
                                      Ext.htmlEncode(record.get('name')) + '</span></div>';

                                  return html;
                              },
                              items: {
                                  padding: 0,
                                  xtype: 'textfield',
                                  hideLabel: true,
                                  listeners: {
                                      change: 'onDqListFilterChange'
                                  },
                                  triggers: {
                                      reset: {
                                          cls: 'x-form-clear-trigger',
                                          handler: function () {
                                              this.reset();
                                          }
                                      }
                                  }
                              }
                          },
                          {
                              text: Unidata.i18n.t('glossary:description'),
                              plugins: [
                                  {
                                      ptype: 'grid.column.headeritemswitcher',
                                      pluginId: 'headerSwitcherPlugin',
                                      mode: 'TEXT'
                                  }
                              ],
                              dataIndex: 'description',
                              sortable: false,
                              resizable: true,
                              menuDisabled: true,
                              flex: 3,
                              items: {
                                  padding: 0,
                                  xtype: 'textfield',
                                  hideLabel: true,
                                  listeners: {
                                      change: 'onDqListFilterChange'
                                  },
                                  triggers: {
                                      reset: {
                                          cls: 'x-form-clear-trigger',
                                          handler: function () {
                                              this.reset();
                                          }
                                      }
                                  }
                              }
                          },
                          {
                              text: Unidata.i18n.t('admin.dqtest>cleanseFunctionNameColumn'),
                              dataIndex: 'cleanseFunctionName',
                              sortable: false,
                              resizable: true,
                              menuDisabled: true,
                              flex: 3
                          },
                          runTypeColumnCfg,
                          {
                              xtype: 'un.actioncolumn',
                              hideable: false,
                              width: 25,
                              hidden: true,
                              bind: {
                                  hidden: '{isDeleteDqRuleHidden}'
                              },
                              items: [
                                  {
                                      iconCls: 'un-dq-acolumn-remove',
                                      faIcon: 'trash-o',
                                      handler: 'onDeleteDqButtonClick'
                                  }
                              ]
                          }
                      ],
                      listeners: {
                          selectionchange: 'onDqListSelectionChange'
                      }
                  },
                  {
                      xtype: 'container',
                      reference: 'dqListFooterContainer',
                      layout: {
                          type: 'hbox',
                          align: 'stretch',
                          pack: 'end'
                      },
                      hidden: true,
                      bind: {
                          hidden: '{!dqTestMode}'
                      },
                      padding: 10,
                      items: [
                          {
                              xtype: 'button',
                              reference: 'showDqTestWizardButton',
                              disabled: true,
                              bind: {
                                  disabled: '{!isShowDqTestWizardButtonEnabled}'
                              },
                              listeners: {
                                  'click': 'onShowDqTestWizardButtonClick'
                              },
                              text: Unidata.i18n.t('admin.dqtest>showDqTestWizardButtonText')
                          }
                      ]
                  }
              ]
          }
        ];

        this.add(items);
    },

    buildRunTypeColumnCfg: function (customCfg) {
        var cfg,
            comboboxCfg;

        comboboxCfg = this.buildRunTypeFilterComboboxCfg();

        cfg =  {
            text: Unidata.i18n.t('admin.dq>runCondition'),
            plugins: [
                {
                    ptype: 'grid.column.headeritemswitcher',
                    pluginId: 'headerSwitcherPlugin',
                    mode: 'TEXT'
                }
            ],
            dataIndex: 'runType',
            sortable: false,
            resizable: false,
            menuDisabled: true,
            width: 200,
            align: 'center',
            renderer: this.runTypeRenderer.bind(this),
            items: [comboboxCfg]
        };

        cfg = Ext.apply(cfg, customCfg);

        return cfg;
    },

    buildRunTypeFilterComboboxCfg: function (customCfg) {
        var cfg,
            data;

        data = this.buildRunTypeFilterData();

        cfg = {
            xtype: 'combobox',
            width: '100%',
            padding: 0,
            displayField: 'displayName',
            valueField: 'name',
            queryMode: 'local',
            editable: false,
            store: {
                fields: [
                    'name',
                    'displayName'
                ],
                data: data
            },
            listeners: {
                change: 'onDqListFilterChange'
            },
            triggers: {
                reset: {
                    cls: 'x-form-clear-trigger',
                    handler: function () {
                        this.reset();
                        this.collapse();
                    }
                }
            }
        };

        cfg = Ext.apply(cfg, customCfg);

        return cfg;
    },

    buildRunTypeFilterData: function () {
        var DQ_RULE_RUN_TYPES_DISPLAY_SHORT = Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigation.DQ_RULE_RUN_TYPES_DISPLAY_SHORT,
            data = [];

        Ext.Object.each(DQ_RULE_RUN_TYPES_DISPLAY_SHORT, function (name, displayName) {
            data.push({
                name: name,
                displayName: displayName
            });
        });

        return data;
    },

    runTypeRenderer: function (value, metaData, record) {
        var DQ_RULE_RUN_TYPES_DISPLAY_SHORT = Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigation.DQ_RULE_RUN_TYPES_DISPLAY_SHORT,
            displayValue = '',
            runType      = record.get('runType');

        if (runType) {
            return DQ_RULE_RUN_TYPES_DISPLAY_SHORT[runType];
        }

        return displayValue;
    },

    listeners: {
        render: 'onDqNavigationRender'
    },

    initComponent: function () {
        if (!this.specialFilterType) {
            this.specialFilterType = Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigation.filterType.NONSPECIAL;
        }

        this.callParent(arguments);

        this.initReferences();
    },

    initReferences: function () {
        this.dqList = this.lookupReference('dqList');
        this.dqListFooterContainer = this.lookupReference('dqListFooterContainer');
        this.attributeTree = this.lookupReference('attributeTree');
        this.attributeComboBox = this.lookupReference('attributeComboBox');
        this.showDqTestWizardButton = this.lookupReference('showDqTestWizardButton');
    },

    onDestroy: function () {
        this.dqList = null;
        this.dqListFooterContainer = null;
        this.attributeTree = null;
        this.attributeComboBox = null;
        this.showDqTestWizardButton = null;

        this.callParent(arguments);
    }
});
