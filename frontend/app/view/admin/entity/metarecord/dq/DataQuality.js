/**
 * Вкладка правил качества для экрана метамодели
 *
 * @author Ivan Marshalkin
 * @date 2018-01-23
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.DataQuality', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.DataQualityController',
        'Unidata.view.admin.entity.metarecord.dq.DataQualityModel'
    ],

    alias: 'widget.admin.entity.metarecord.dq',

    controller: 'admin.entity.metarecord.dq',

    viewModel: {
        type: 'admin.entity.metarecord.dq'
    },

    cls: 'un-dq-rule',

    referenceHolder: true,

    config: {
        cleanseFunctions: null,
        sourceSystems: null,
        metaRecord: null,
        readOnly: null
    },

    methodMapper: [
        {
            method: 'updateReadOnly'
        },
        {
            method: 'commitChanges'
        }
    ],

    layout: {
        type: 'border'
    },

    draftMode: null,                                  // режим работы с черновиком

    scrollable: false,

    defaults: {
        split: true,
        collapsible: true
    },

    dqRuleEditorContainer: null,
    dqRuleEditor: null,
    dqNavigation: null,
    createDqButton: null,

    items: [
        {
            xtype: 'admin.entity.metarecord.dq.dqnavigation',
            reference: 'dqNavigation',
            title: Unidata.i18n.t('admin.dq>dqRuleTitle'),
            flex: 1,
            margin: '10 10 10 10',
            region: 'center',
            collapsible: false,
            listeners: {
                dqrulegridselectionchange: 'onDqRuleGridSelectionChange'
            }
        },
        {
            xtype: 'panel',
            reference: 'dqRuleEditorContainer',
            cls: 'un-dq-rule-editor-container',
            height: 100,
            margin: '10 10 10 10',
            header: false,
            region: 'south',
            scrollable: 'vertical',
            layout: {
                type: 'hbox',
                layout: 'stretch'
            },
            items: []
        }
    ],

    listeners: {
        'activate': 'onDataQualityActivate'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
        this.initComponentEvent();
    },

    initReferences: function () {
        this.dqRuleEditorContainer = this.lookupReference('dqRuleEditorContainer');
        this.dqNavigation = this.lookupReference('dqNavigation');

        this.createDqButton = this.lookupReference('createDqButton');
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
        this.dqRuleEditorContainer = null;
        this.dqNavigation = null;

        this.createDqButton = null;

        if (this.dqRuleEditor) {
            this.dqRuleEditor.destroy();
            this.dqRuleEditor = null;
        }

        this.callParent(arguments);
    },

    updateMetaRecord: function (metaRecord) {
        this.dqNavigation.setMetaRecord(metaRecord);
    }
});
