/**
 * Панель редактирования набора записей
 *
 * @author Sergey Shishigin
 * @date 2016-02-02
 */

Ext.define('Unidata.view.component.search.query.etaloncluster.EtalonClusterPanel', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.component.search.query.etaloncluster.etalonclusterpanel',

    requires: [
        'Unidata.view.component.search.query.etaloncluster.recordlist.EtalonClusterRecordList',
        'Unidata.view.component.search.query.etaloncluster.list.EtalonClusterList'
    ],

    referenceHolder: true,

    etalonClusterList: null,
    etalonClusterRecordList: null,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'component.search.query.etaloncluster.list.etalonclusterlist',
            reference: 'etalonClusterList',
            flex: 1
        },
        {
            xtype: 'component.search.query.etaloncluster.recordlist.etalonclusterrecordlist',
            reference: 'etalonClusterRecordList',
            flex: 1
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initReferences: function () {
        this.etalonClusterList = this.lookupReference('etalonClusterList');
        this.etalonClusterRecordList = this.lookupReference('etalonClusterRecordList');
    },

    initListeners: function () {
        this.etalonClusterList.on('etalonclustergridselectionchange', this.onEtalonClusterGridSelectionChange, this);
    },

    /**
     *
     * @param etalonCluster {Unidata.model.etaloncluster.EtalonCluster}
     */
    onEtalonClusterGridSelectionChange: function (etalonCluster) {
        this.etalonClusterRecordList.setEtalonCluster(etalonCluster);
    }
});
