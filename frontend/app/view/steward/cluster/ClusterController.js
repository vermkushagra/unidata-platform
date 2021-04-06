/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.ClusterController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.cluster',

    init: function () {
        var view = this.getView(),
            tokenValues;

        this.callParent(arguments);

        // подписываемся на события fireEvenComponent
        view.addComponentListener('clustersearch', this.onClusterSearch, this);
        view.addComponentListener('clusterselect', this.onClusterSelect, this);
        view.addComponentListener('clusterchanged', this.onClusterChanged, this);

        this.initRouter();
    },

    initRouter: function () {
        var view = this.getView(),
            tokenValues = Unidata.util.Router.getTokenValues('cluster');

        this.routeToCluster(tokenValues);
        Unidata.util.Router.on('cluster', this.routeToCluster, this);
        Unidata.util.Router.on('main', this.onMainTokenChange, this);

        view.on('destroy', this.removeClusterToken, this);
    },

    removeClusterToken: function () {
        Unidata.util.Router.removeToken('cluster');
    },

    onMainTokenChange: function (tokenValues, oldTokenValues) {
        if (tokenValues.section === oldTokenValues.section) {
            return;
        }

        if (oldTokenValues.section && oldTokenValues.section === 'cluster') {
            this.removeClusterToken();
        }
    },

    routeToCluster: function (tokenValues) {
        var view = this.getView(),
            clusterFilterPanel = view.clusterFilterPanel,
            entityName,
            entityType,
            etalonId;

        entityName = tokenValues.entityName;
        entityType = tokenValues.entityType;
        etalonId   = tokenValues.etalonId;

        if (entityName || entityType || etalonId) {
            clusterFilterPanel.setRouteToClusterData(entityName, entityType, etalonId);

            if (!clusterFilterPanel.getCollapsed()) {
                clusterFilterPanel.collapse();
            }
        }
    },

    onClusterSearch: function (cfg) {
        var entityName = cfg['entityName'],
            entityType = cfg['entityType'],
            bindCfg = {},
            getMetaCfg,
            promise;

        bindCfg = Ext.apply(bindCfg, cfg);
        bindCfg = Ext.apply(bindCfg, {
            entityName: entityName,
            entityType: entityType
        });

        getMetaCfg = {
            entityName: entityName,
            entityType: entityType
        };

        promise = Unidata.util.api.MetaRecord.getMetaRecord(getMetaCfg);
        promise.then(
            this.onSuccessLoadMeta.bind(this, bindCfg),
            this.onFailureLoadMeta.bind(this, bindCfg)
        ).done();

    },

    onSuccessLoadMeta: function (cfg, metaRecord) {
        var view = this.getView();

        cfg['metaRecord'] = metaRecord;

        view.clusterListPanel.displayClusterList(cfg);
    },

    onFailureLoadMeta: function () {
    },

    onClusterSelect: function (clusterRecord, matchingGroup, matchingRule) {
        var view               = this.getView(),
            clusterTabPanel    = view.clusterTabPanel,
            clusterFilterPanel = view.clusterFilterPanel,
            clusterListPanel   = view.clusterListPanel,
            entityName,
            entityType;

        entityName = clusterFilterPanel.getCurrentEntityName();
        entityType = clusterFilterPanel.getCurrentEntityType();

        clusterTabPanel.openClusterTab(entityName, entityType, clusterRecord, matchingGroup, matchingRule);

        if (!clusterFilterPanel.getCollapsed()) {
            clusterFilterPanel.collapse();
        }

        if (!clusterListPanel.getCollapsed()) {
            clusterListPanel.collapse();
        }
    },

    onClusterChanged: function () {
        var view = this.getView();

        view.clusterListPanel.reloadClusterList();
    },

    onDataRecordSaveSuccess: function () {
        var view = this.getView();

        view.clusterFilterPanel.doSearch();
    },

    onDataRecordDeleteSuccess: function () {
        var view = this.getView();

        view.clusterFilterPanel.doSearch();
    }
});
