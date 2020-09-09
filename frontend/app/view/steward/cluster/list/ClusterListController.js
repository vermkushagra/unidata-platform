/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.list.ClusterListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.cluster.listview',

    searchParams: null,
    clusterCount: null,

    init: function () {
        var viewModel        = this.getViewModel(),
            clusterListStore = viewModel.getStore('clusterListStore');

        viewModel.set('pageSize', clusterListStore.getPageSize());

        this.callParent(arguments);
    },

    displayClusterList: function (cfg) {
        var me               = this,
            view             = this.getView(),
            viewModel        = this.getViewModel(),
            grid             = view.gridClusterList,
            promiseCount,
            openIfOnlyOne    = cfg.openIfOnlyOne,
            forceResultExpand = cfg.forceResultExpand,
            record,
            store;

        delete cfg.openIfOnlyOne;
        this.searchParams = cfg;

        promiseCount = this.loadClusterCount();
        promiseCount.then(
            function (count) {
                me.setClusterCount(count);

                me.loadClusterList().then(function () {
                    if (count === 1 && openIfOnlyOne) {
                        // collapse list panel
                        if (!view.getCollapsed()) {
                            view.collapse();
                        }

                        // open first record
                        store = grid.getStore();
                        record = store.first();
                        me.onClusterItemClick(grid, record);
                    } else {
                        // collapse list panel
                        if (forceResultExpand && view.getCollapsed()) {
                            view.expand();
                        }
                    }
                }, function () {
                    Unidata.showError(view.loadClusterListErrorText);
                }).done();
            }
        ).then(
            function () {
                view.pagingClusterList.updateInfo();
            }
        ).done();
    },

    setClusterCount: function (count) {
        var viewModel = this.getViewModel();

        this.clusterCount = count;
        viewModel.set('totalCount', count);
    },

    loadClusterList: function () {
        var me               = this,
            viewModel        = this.getViewModel(),
            deferred         = Ext.create('Ext.Deferred'),
            clusterListStore = viewModel.getStore('clusterListStore');

        clusterListStore.getProxy().totalCount = me.clusterCount;

        clusterListStore.load({
            callback: function (records, operation, success) {
                if (success) {
                    deferred.resolve();
                } else {
                    deferred.reject();
                }
            }
        });

        return deferred.promise;
    },

    loadClusterCount: function () {
        var searchParams = this.searchParams,
            entityName   = searchParams['entityName'],
            ruleId       = searchParams['ruleId'],
            groupId      = searchParams['groupId'],
            etalonId     = searchParams['etalonId'],
            preprocessing = Boolean(searchParams['preprocessing']),
            viewModel = this.getViewModel(),
            promise;

        viewModel.set('preprocessing', preprocessing);

        promise = Unidata.util.api.Cluster.getClusterCount(entityName, groupId, ruleId, etalonId, preprocessing);

        return promise;
    },

    buildLoadParams: function (searchParams) {
        var ruleId           = searchParams['ruleId'],
            groupId          = searchParams['groupId'],
            etalonId         = searchParams['etalonId'],
            metaRecord       = searchParams['metaRecord'],
            preprocessing    = Boolean(searchParams['preprocessing']),
            pathsMainDisplayable,
            pathsDisplayable,
            params;

        pathsMainDisplayable = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [{
            property: 'mainDisplayable',
            value: true
        }]);

        pathsDisplayable = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [{
            property: 'displayable',
            value: true
        }]);

        pathsDisplayable = Ext.Array.merge(pathsDisplayable, pathsMainDisplayable);

        params = {
            preprocessing: preprocessing,
            entityName: searchParams['entityName'],
            fields: pathsDisplayable
        };

        if (ruleId) {
            params['ruleId'] = ruleId;
        }

        if (groupId) {
            params['groupId'] = groupId;
        }

        if (etalonId) {
            params['etalonId'] = etalonId;
        }

        return params;
    },

    onClusterListStoreBeforeLoad: function () {
        var searchParams     = this.searchParams,
            viewModel        = this.getViewModel(),
            clusterListStore = viewModel.getStore('clusterListStore'),
            loadParams       = this.buildLoadParams(searchParams),
            proxy            = clusterListStore.getProxy();

        proxy.setExtraParams(loadParams);
    },

    onClusterItemClick: function (grid, record) {
        var view          = this.getView(),
            matchingGroup = this.getMatchingGroupById(record.get('groupId')),
            matchingRule  = this.getMatchingRuleById(record.get('ruleId'));

        view.fireComponentEvent('clusterselect', record, matchingGroup, matchingRule);
    },

    ruleNameRenderer: function (value, metaData, record) {
        var result = '',
            matchingGroup,
            matchingRule;

        matchingGroup = this.getMatchingGroupById(record.get('groupId'));
        matchingRule  = this.getMatchingRuleById(record.get('ruleId'));

        result += this.buildDisplayableAttrCellInfo(record);
        result += this.buildEtalonCountCellInfo(record);
        result += this.buildMatchingCellInfo(matchingGroup, matchingRule);

        return '<ul>' + result + '</ul>';
    },

    buildEtalonCountCellInfo: function (record) {
        var etalonCount = record.get('recordsCount'),
            tpl         = new Ext.XTemplate('<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('common:recordCount') + ': </span><span class="un-result-grid-item-data">{value}</span></li>'),
            result;

        result = tpl.apply({
            value: etalonCount
        });

        return result;
    },

    buildMatchingCellInfo: function (matchingGroup, matchingRule) {
        var result = '',
            tpl;

        if (matchingGroup) {
            tpl = new Ext.XTemplate('<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('glossary:ruleGroup') + ': </span><span class="un-result-grid-item-data">{value}</span></li>');

            result += tpl.apply({
                value: matchingGroup.get('name')
            });
        }

        if (matchingRule) {
            tpl = new Ext.XTemplate('<li><span class="un-result-grid-item-label">' + Unidata.i18n.t('glossary:rule') + ': </span><span class="un-result-grid-item-data">{value}</span></li>');

            result += tpl.apply({
                value: matchingRule.get('name')
            });
        }

        return result;
    },

    buildDisplayableAttrCellInfo: function (record) {
        var result          = '',
            searchParams    = this.searchParams,
            metaRecord      = searchParams['metaRecord'],
            attributeValues = record.preview().getRange(),
            parseFormats,
            pathsMainDisplayable,
            pathsDisplayable,
            mainItems,
            simpleItems;

        parseFormats = {
            Date: 'Y-m-d',
            Timestamp: 'Y-m-d\\TH:i:s',
            Time: '\\TH:i:s'
        };

        pathsMainDisplayable = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [{
            property: 'mainDisplayable',
            value: true
        }]);

        pathsDisplayable     = Unidata.util.UPathMeta.buildSimpleAttributePaths(metaRecord, [{
            property: 'displayable',
            value: true
        }]);

        pathsDisplayable = Ext.Array.difference(pathsDisplayable, pathsMainDisplayable);

        mainItems = Ext.Array.filter(attributeValues, function (item) {
            return Ext.Array.contains(pathsMainDisplayable, item.get('field'));
        }, this);

        simpleItems = Ext.Array.filter(attributeValues, function (item) {
            return Ext.Array.contains(pathsDisplayable, item.get('field'));
        }, this);

        result += this.buildMainDisplayableAttrInfo(metaRecord, parseFormats, mainItems);
        //result += this.buildDisplayableAttrInfo(metaRecord, parseFormats, simpleItems);

        return result;
    },

    buildMainDisplayableAttrInfo: function (metaRecord, parseFormats, mainItems) {
        var result = [];

        mainItems.forEach(function (item) {
            var tpl = new Ext.XTemplate('{value}'),
                DataAttributeFormatterUtil = Unidata.util.DataAttributeFormatter,
                metaAttribute,
                data,
                value;

            metaAttribute = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.get('field'));
            value = DataAttributeFormatterUtil.formatValueByAttribute(metaAttribute, item.get('value'), parseFormats);

            data = {
                value: value
            };

            result.push(tpl.apply(data));
        });

        return '<span class="un-result-grid-item-header">' + result.join(' | ') + '</span>';
    },

    buildDisplayableAttrInfo: function (metaRecord, parseFormats, simpleItems) {
        var result = '';

        simpleItems.forEach(function (item) {
            var tpl = new Ext.XTemplate('<p>{value}</p>'),
                DataAttributeFormatterUtil = Unidata.util.DataAttributeFormatter,
                metaAttribute,
                data,
                value;

            metaAttribute              = Unidata.util.UPathMeta.findAttributeByPath(metaRecord, item.get('field'));
            value = DataAttributeFormatterUtil.formatValueByAttribute(metaAttribute, item.get('value'), parseFormats);

            data = {
                value: value
            };

            result += tpl.apply(data);
        });

        return result;
    },

    getMatchingGroupById: function (groupId) {
        var searchParams = this.searchParams,
            group = null;

        if (groupId) {
            Ext.Array.each(searchParams.matchingGroups, function (matchingGroup) {
                if (matchingGroup.get('id') === groupId) {
                    group = matchingGroup;

                    return false; // остановка итерации Ext.Array.each
                }
            });
        }

        return group;
    },

    getMatchingRuleById: function (ruleId) {
        var searchParams = this.searchParams,
            rule = null;

        if (ruleId) {
            Ext.Array.each(searchParams.matchingRules, function (matchingRule) {
                if (matchingRule.get('id') === ruleId) {
                    rule = matchingRule;

                    return false; // остановка итерации Ext.Array.each
                }
            });
        }

        return rule;
    },

    reloadClusterList: function () {
        var viewModel        = this.getViewModel(),
            clusterListStore = viewModel.getStore('clusterListStore');

        clusterListStore.reload();
    }
});
