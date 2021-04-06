/**
 * Миксин для создания табпанели данных
 *
 * @author Sergey Shishigin
 * @date 2016-10-12
 */
Ext.define('Unidata.mixin.ExtendedTabPanel', {
    extend: 'Ext.Mixin',

    tabLoadingTitleText: Unidata.i18n.t('dataviewer>loading'),
    maxTabsErrorText: Unidata.i18n.t('other>maxOpenTabs'),

    findMergeTabByClusterId: function (clusterId) {
        var tabBar = this.getTabBar(),
            tabs = tabBar.items,
            tab = null;

        tab = tabs.findBy(function (tab) {
            var mergePanel = tab.card,
                cluster;

            if (!(mergePanel instanceof Unidata.view.steward.cluster.merge.Merge)) {
                return false;
            }

            cluster = mergePanel.getClusterRecord();

            if (!cluster) {
                return clusterId === null;
            }

            return cluster.get('id') === clusterId;
        });

        return tab;
    },

    findDataRecordTabByEtalonId: function (etalonId) {
        var tabBar = this.getTabBar(),
            tabs = tabBar.items,
            tab = null;

        tab = tabs.findBy(function (tab) {
            var itemEtalonId = null,
                item = tab.card;

            // проверяем наличие метода getEtalon
            if (Ext.isFunction(item.getEtalonId)) {
                itemEtalonId = item.getEtalonId();
            } else {
                return false;
            }

            return itemEtalonId === etalonId;
        });

        return tab;
    },

    findActiveTab: function () {
        var tabBar = this.getTabBar(),
            tabs = tabBar.items,
            tab;

        tab = tabs.findBy(function (item) {
            return item.active;
        });

        return tab;
    },

    isCanOpenTab: function () {
        var canOpenTab = false,
            maxTabs,
            openedTabs;

        maxTabs = Unidata.Config.getMaxTabs();
        openedTabs = this.getOpenedTabsCount();
        canOpenTab = openedTabs < maxTabs;

        return canOpenTab;
    },

    getOpenedTabsCount: function () {
        var tabBar = this.getTabBar(),
            tabs = tabBar.items,
            count = tabs.length;

        return count;
    },

    /**
     * Закрыть табы в которых открыты соответствующие dataRecords
     *
     * @param dataRecords
     */
    closeDataRecordTabsByDataRecords: function (dataRecords) {
        var me = this;

        dataRecords.each(function (dataRecord) {
            me.closeDataRecordTabByDataRecord(dataRecord);
        });
    },

    /**
     * * Закрыть табы в котором открыт соответствующий dataRecord
     *
     * @param dataRecord
     */
    closeDataRecordTabByDataRecord: function (dataRecord) {
        var etalonId,
            tab;

        etalonId = dataRecord.get('etalonId');
        tab = this.findDataRecordTabByEtalonId(etalonId);

        if (tab) {
            this.remove(tab.card, true);
        }
    },

    /**
     * Проверяем можно ли создать recordTab
     *
     * @param etalonId
     * @returns {boolean}
     */
    onBeforeCreateDataRecordTab: function (etalonId) {
        var maxTabs = Unidata.Config.getMaxTabs(),
            tab,
            error;

        if (!this.isCanOpenTab()) {
            error = Ext.String.format(this.maxTabsErrorText, maxTabs);
            Unidata.showError(error);

            return false;
        }

        if (etalonId) {
            tab = this.findDataRecordTabByEtalonId(etalonId);

            if (tab) {
                this.setActiveTab(tab.card);

                return false;
            }
        }

        return true;
    },

    setTabStyle: function (etalonId, status) {
        var tab;

        if (etalonId) {
            tab = this.findDataRecordTabByEtalonId(etalonId);
        }

        if (!tab) {
            tab = this.findActiveTab();
        }

        function changeCls (component, clsPrefix, status) {
            var el = component.getEl(),
                classList,
                clsToRemove;

            if (!el) {
                return;
            }

            classList   = Ext.Array.toArray(el.dom.classList);
            clsToRemove = classList.filter(function (item) {
                return Ext.String.startsWith(item, clsPrefix);
            });

            clsToRemove.forEach(function (cls) {
                component.removeCls(cls);
            });

            component.addCls(clsPrefix + status.toLowerCase());
        }

        if (tab) {
            changeCls(tab, 'datarecord-tab-', status);

            if (tab.card) {
                changeCls(tab.card, 'datarecord-', status);
            }
        }
    }
});
