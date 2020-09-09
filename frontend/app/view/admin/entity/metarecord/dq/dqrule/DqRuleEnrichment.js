/**
 * Секция настройки обогащающих правил редактора правил качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 *
 **/
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEnrichment', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.dqruleenrichment',

    layout: {
        type: 'hbox',
        align: 'top'
    },

    bodyPadding: 10,

    config: {
        dqRule: null,
        dqEnrich: null,
        sourceSystems: null
    },

    referenceHolder: true,

    title: Unidata.i18n.t('admin.dq>enrichConfigTitle'),

    viewModelAccessors: ['dqEnrich'],

    viewModel: {
        data: {
            dqEnrich: null
        }
    },

    initItems: function () {
        var items,
            sourceSystems,
            store;

        this.callParent(arguments);

        sourceSystems = this.getSourceSystems();
        store = this.createSourceSystemStore(sourceSystems);

        items = [
            {
                xtype: 'combo',
                reference: 'sourceSystemCombo',
                fieldLabel: Unidata.i18n.t('admin.dq>enrichBySourceSystem'),
                displayField: 'name',
                valueField: 'name',
                labelWidth: 280,
                width: 650,
                emptyText: Unidata.i18n.t('admin.dq>selectSourceSystem'),
                queryMode: 'local',
                store: store,
                bind: {
                    value: '{dqEnrich.sourceSystem}',
                    readOnly: '{dqRuleEditorReadOnly}'
                }
            }
        ];

        this.add(items);
        this.initPanelEvents();
    },

    initPanelEvents: function () {
        this.on('collapse', this.onPanelCollapse.bind(this));
        this.on('expand', this.onPanelExpand.bind(this));
    },

    createSourceSystemStore: function (sourceSystems) {
        var store;

        store = Ext.create('Ext.data.Store', {
            model: 'Unidata.model.sourcesystem.SourceSystem',
            autoLoad: true,
            data: sourceSystems,
            proxy: {
                type: 'memory'
            }
        });

        return store;
    },

    buildTitle: function () {
        var collapsed    = this.getCollapsed(),
            dqRule       = this.getDqRule(),
            dqEnrich      = dqRule.getEnrich(),
            sourceSystem = dqEnrich.get('sourceSystem'),
            title        = Unidata.i18n.t('admin.dq>enrichConfigTitle'),
            isEnrichment = dqRule.get('isEnrichment'),
            parts        = [],
            titleInfo;

        if (collapsed && isEnrichment && dqEnrich) {
            sourceSystem = dqEnrich.get('sourceSystem');

            if (sourceSystem) {
                parts.push(sourceSystem);
            }

            if (parts.length > 0) {
                titleInfo = parts.join(' ');
                title = Ext.String.format('{0}: <span class="un-dq-rule-title-info">{1}</span>', title, titleInfo);
            }
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
