Ext.define('Unidata.view.steward.search.recordshow.Recordshow', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.steward.search.recordshow',

    viewModel: {
        type: 'steward.search.recordshow'
    },
    controller: 'steward.search.recordshow',

    mixins: [
        'Unidata.mixin.ExtendedTabPanel',
        'Unidata.mixin.StatusManageable'
    ],

    createDataRecordButton: null,

    tabBar: {
        childEls: [
            'body',
            'strip',
            'prebody'
        ],

        renderTpl:
        '<div id="{id}-prebody" data-ref="prebody" style = "position: absolute; left: 0px; height: 100%; width: 40px;"></div>' +
        '<div id="{id}-body" data-ref="body" role="presentation" class="{baseBodyCls} {baseBodyCls}-{ui}' +
        '{bodyCls} {bodyTargetCls}{childElCls}"<tpl if="bodyStyle"> style="{bodyStyle}; left: 100px;"</tpl>>' +
        '{%this.renderContainer(out,values)%}' +
        '</div>' +
        '<div id="{id}-strip" data-ref="strip" role="presentation" class="{stripCls} {stripCls}-{ui}{childElCls}"></div>',

        padding: '0 0 0 40',

        reference: 'recordshowTabBar',

        listeners: {
            afterrender: 'onCreateDataRecordButtonAfterRender'
        }
    },

    ui: 'un-content',
    cls: 'un-datarecord-tabpanel',

    requires: [
        'Unidata.view.steward.search.recordshow.RecordshowController',
        'Unidata.view.steward.search.recordshow.RecordshowModel',

        'Unidata.view.steward.search.recordshow.CreateDataRecordButton',

        'Unidata.plugin.tab.DirtyTabChangePrompt',

        'Unidata.view.steward.relation.ReferencePanel',
        'Unidata.util.DataRecord',

        'Unidata.view.steward.dataviewerpanel.DataViewerPanel',

        'Unidata.view.steward.cluster.merge.MergeStatusConstant'
    ],

    plugins: [
        {
            ptype: 'dirtytabchangeprompt',
            pluginId: 'dirtytabchangeprompt',
            leaveUnsavedTabText: Unidata.i18n.t('search>recordshow.leaveUnsavedRecord')
        }
    ],

    maxTabWidth: 250,
    reference: 'recordshowTabPanel',
    referenceHolder: true,
    defaults: {
        bodyPadding: 0,
        closable: true
    },

    invalidMergeMetaRecordText: Unidata.i18n.t('search>recordshow.metamodelNotMatchConsolidation'),

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'createRecordTab'
        },
        {
            method: 'createRecordTabFromRecord'
        }
    ],

    onDestroy: function () {
        this.callParent(arguments);

        if (this.createDataRecordButton) {
            this.createDataRecordButton.destroy();
            this.createDataRecordButton = null;
        }
    }
});
