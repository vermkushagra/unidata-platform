/**
 *
 * Панель отображает количество кластеров
 *
 * @author Sergey Shishigin
 * @date 2017-06-14
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.notice.bar.ClusterBar', {
    extend: 'Ext.button.Button',

    requires: [],

    alias: 'widget.steward.datacard.header.clusterbar',

    cls: 'un-clusterbar',

    scale: 'small',

    config: {
        clusterCount: 0
    },

    /**
     * Инициализация компонента
     */
    initComponent: function () {
        this.callParent(arguments);
    },

    updateClusterCount: function (clusterCount) {
        var visible = Ext.isNumber(clusterCount) && clusterCount > 0,
            text;

        text = Unidata.i18n.t('dataviewer>mergeCount', {count: clusterCount});
        this.setHidden(!visible);

        if (!this.rendered) {
            this.on('afterrender', function () {
                this.setText(text);
            }, this, {single: true});
        } else {
            this.setText(text);
        }
    }
});
