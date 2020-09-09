/**
 * Редактор правил качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEditor', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.dqruleeditor',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEditorController',
        'Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEditorModel',

        'Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleProperties',
        'Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEnrichment',
        'Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleValidation'
    ],

    controller: 'admin.entity.metarecord.dq.dqruleeditor',

    viewModel: {
        type: 'admin.entity.metarecord.dq.dqruleeditor'
    },

    cls: 'un-dq-rule-editor',

    config: {
        dqRule: null,
        cleanseFunction: null,
        cleanseFunctionPath: null,
        sourceSystems: null,
        cleanseFunctions: null,
        metaRecord: null,
        readOnly: null
    },

    viewModelAccessors: ['dqRule'],

    methodMapper: [
        {
            method: 'updateReadOnly'
        },
        {
            method: 'isValidationAvailable'
        },
        {
            method: 'isEnrichAvailable'
        }

    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    referenceHolder: true,

    draftMode: null,

    dqRuleFunction: null,
    dqRuleValidation: null,
    dqRuleEnrichment: null,
    dqRuleProps: null,

    initItems: function () {
        var items,
            sourceSystems,
            cleanseFunctions,
            dqRule,
            cleanseFunction,
            cleanseFunctionPath,
            metaRecord;

        this.callParent(arguments);
        sourceSystems = this.getSourceSystems();
        cleanseFunctions = this.getCleanseFunctions();
        dqRule = this.getDqRule();
        cleanseFunction = this.getCleanseFunction();
        metaRecord = this.getMetaRecord();

        if (cleanseFunction) {
            cleanseFunctionPath = cleanseFunction.get('name');
        }

        items = [
            {
                xtype: 'admin.entity.metarecord.dq.dqruleprops',
                reference: 'dqRuleProps',
                flex: 1,
                collapsible: true,
                dqRule: dqRule,
                sourceSystems: sourceSystems,
                height: 240
            },
            {
                xtype: 'admin.entity.metarecord.dq.dqrulefunction',
                reference: 'dqRuleFunction',
                flex: 1,
                collapsible: true,
                dqRule: dqRule,
                cleanseFunctions: cleanseFunctions,
                cleanseFunction: cleanseFunction,
                cleanseFunctionPath: cleanseFunctionPath,
                metaRecord: metaRecord,
                listeners: {
                    cleansefunctionitemclick: 'onCleanseFunctionItemClick',
                    executioncontextpathchanged: 'onExecutionContextPathChanged',
                    isvalidationchange: 'onIsValidationChange',
                    isenrichchange: 'onIsEnrichChange'
                }
            },
            {
                xtype: 'admin.entity.metarecord.dq.dqrulevalidation',
                reference: 'dqRuleValidation',
                collapsible: true,
                dqRule: dqRule,
                dqRaise: dqRule.getRaise(),
                metaRecord: metaRecord,
                cleanseFunction: cleanseFunction,
                hidden: true
            },
            {
                xtype: 'admin.entity.metarecord.dq.dqruleenrichment',
                reference: 'dqRuleEnrichment',
                collapsible: true,
                dqRule: dqRule,
                dqEnrich: dqRule.getEnrich(),
                sourceSystems: sourceSystems,
                height: 156,
                hidden: true
            }
        ];
        this.add(items);
        this.initReferences();
        this.relayPortEvents();
    },

    relayPortEvents: function () {
        this.relayEvents(this.dqRuleFunction, ['portupathchanged']);
        this.relayEvents(this.dqRuleProps, ['dqrulenamechange']);
    },

    initReferences: function () {
        this.dqRuleFunction = this.lookupReference('dqRuleFunction');
        this.dqRuleValidation = this.lookupReference('dqRuleValidation');
        this.dqRuleEnrichment = this.lookupReference('dqRuleEnrichment');
        this.dqRuleProps = this.lookupReference('dqRuleProps');
    },

    updateCleanseFunction: function (cleanseFunction) {
        var dqRuleFunction = this.dqRuleFunction,
            dqRuleValidation = this.dqRuleValidation,
            dqRuleEnrichment = this.dqRuleEnrichment,
            dqRule = this.getDqRule(),
            dqRuleSelectWarning,
            isValidatationChecked = false,
            isEnrichmentChecked = false,
            hasErrors = false;

        if (!dqRuleFunction) {
            return;
        }

        if (dqRule) {
            isValidatationChecked = dqRule.data.isValidation;
            isEnrichmentChecked = dqRule.data.isEnrichment;
        }

        dqRuleSelectWarning = dqRuleFunction.dqRuleSelectWarning;
        dqRuleFunction.setCleanseFunction(cleanseFunction);
        dqRuleValidation.setCleanseFunction(cleanseFunction);

        if (!dqRuleValidation) {
            return;
        }

        dqRuleValidation.setCleanseFunction(cleanseFunction);

        if (isValidatationChecked) {
            if (this.isValidationAvailable(cleanseFunction)) {
                dqRuleValidation.setHidden(false);
            } else {
                //если нажат checkbox, но есть ошибки - скрываем блок валидации и показываем warning.
                dqRuleSelectWarning.setText(dqRuleFunction.getValidationWarningTemplate());
                dqRuleSelectWarning.setHidden(false);
                dqRuleValidation.setHidden(true);
                hasErrors = true;
            }
        }

        if (isEnrichmentChecked) {
            if (this.isEnrichAvailable(cleanseFunction)) {
                dqRuleEnrichment.setHidden(false);
            } else {
                dqRuleSelectWarning.setText(dqRuleFunction.getEnrichWarningTemplate());
                dqRuleSelectWarning.setHidden(false);
                dqRuleValidation.setHidden(true);
                hasErrors = true;
            }
        }

        if (!hasErrors) {
            dqRuleSelectWarning.setHidden(true);
        }
    },

    updateCleanseFunctionPath: function (cleanseFunctionPath) {
        if (!this.dqRuleFunction) {
            return;
        }
        this.dqRuleFunction.setCleanseFunctionPath(cleanseFunctionPath);
    }
});
