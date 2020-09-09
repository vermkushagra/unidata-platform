/**
 * Окно, содержащее редактор порта
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortEditorWindow', {
    extend: 'Ext.window.Window',

    alias: 'widget.admin.entity.metarecord.dq.port.dqruleporteditorwindow',

    requires: ['Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortEditor'],

    config: {
        upathValue: null,
        metaRecord: null,
        dataType: null,
        portApplicationMode: null,
        useFilter: null,
        executionContext: null,
        executionContextMode: null,
        supportedExecutionContexts: null,
        portType: null
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    referenceHolder: true,

    dqRulePortEditor: null,
    buttonToolbar: null,
    saveButton: null,
    cancelButton: null,

    buildDockedItems: function () {
        var dockedItems;

        dockedItems = {
            xtype: 'toolbar',
            reference: 'buttonToolbar',
            ui: 'footer',
            dock: 'bottom',
            layout: {
                pack: 'center'
            },
            items: [
                {
                    xtype: 'button',
                    reference: 'saveButton',
                    text: Unidata.i18n.t('common:save'),
                    listeners: {
                        click: this.onSaveButtonClick.bind(this)
                    }
                },
                {
                    xtype: 'button',
                    reference: 'cancelButton',
                    color: 'transparent',
                    text: Unidata.i18n.t('common:cancel'),
                    listeners: {
                        click: this.onCancelButtonClick.bind(this)
                    }
                }
            ]
        };

        return dockedItems;
    },

    initComponent: function () {
        this.dockedItems = this.buildDockedItems();
        this.callParent(arguments);
        this.initReferences();
        this.computeAndApplyTitle();
    },

    computeAndApplyTitle: function () {
        var executionContextMode = this.getExecutionContextMode(),
            title;

        if (executionContextMode) {
            title = Unidata.i18n.t('admin.dq>executionContextModeTitle');
        } else {
            title = Unidata.i18n.t('admin.dq>portConfig');
        }

        this.setTitle(title);
    },

    initItems: function () {
        var items,
            upathValue,
            metaRecord,
            portApplicationMode,
            useFilter,
            executionContext,
            executionContextMode,
            portType,
            supportedExecutionContexts,
            executionContextMode,
            dataType;

        this.callParent(arguments);

        upathValue = this.getUpathValue();
        metaRecord = this.getMetaRecord();
        dataType = this.getDataType();
        portApplicationMode = this.getPortApplicationMode();
        useFilter = this.getUseFilter();
        executionContext = this.getExecutionContext();
        executionContextMode = this.getExecutionContextMode();
        supportedExecutionContexts = this.getSupportedExecutionContexts();
        portType = this.getPortType();

        items = [
            {
                xtype: 'admin.entity.metarecord.dq.port.dqruleporteditor',
                reference: 'dqRulePortEditor',
                upathValue: upathValue,
                metaRecord: metaRecord,
                dataType: dataType,
                portType: portType,
                portApplicationMode: portApplicationMode,
                executionContextMode: executionContextMode,
                useFilter: useFilter,
                executionContext: executionContext,
                supportedExecutionContexts: supportedExecutionContexts,
                flex: 1,
                listeners: {
                    attributetreeempty: this.onAttributeTreeEmpty.bind(this),
                    upathvalidchange: this.onUPathValidChange.bind(this)
                }
            }
        ];

        this.add(items);
        this.initReferences();
    },

    /**
     * Сконфигурировать окно для отображения сообщения об отсутствии атрибутов
     */
    doConfigNoAttributeWarning: function () {
        var saveButton,
            cancelButton,
            buttonToolbar;

        saveButton    = this.lookupReference('saveButton');
        cancelButton  = this.lookupReference('cancelButton');
        buttonToolbar = this.lookupReference('buttonToolbar');

        buttonToolbar.setLayout({
            pack: 'center'
        });

        this.setWidth(400);
        this.setHeight(200);
        saveButton.setHidden(true);
        cancelButton.setText(Unidata.i18n.t('common:close'));
    },

    onAttributeTreeEmpty: function () {
        this.doConfigNoAttributeWarning();
    },

    onUPathValidChange: function (self, uPathValid) {
        this.saveButton.setDisabled(!uPathValid);
    },

    initReferences: function () {
        this.dqRulePortEditor = this.lookupReference('dqRulePortEditor');
        this.buttonToolbar = this.lookupReference('buttonToolbar');
        this.saveButton = this.lookupReference('saveButton');
        this.cancelButton = this.lookupReference('cancelButton');
    },

    onSaveButtonClick: function () {
        var dqRulePortEditor = this.dqRulePortEditor,
            executionContext,
            upathValue,
            upath;

        upathValue = dqRulePortEditor.getUpathValue();
        upath = dqRulePortEditor.getUPath();
        executionContext = dqRulePortEditor.getExecutionContext();

        this.fireEvent('okbtnclick', this, upathValue, upath, executionContext);
        this.close();
    },

    onCancelButtonClick: function () {
        this.fireEvent('cancelbtnclick', this);
        this.close();
    }
});
