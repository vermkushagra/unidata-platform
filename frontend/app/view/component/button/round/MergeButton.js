/**
 * Кнопка консолидации
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

Ext.define('Unidata.view.component.button.round.MergeButton', {
    extend: 'Unidata.view.component.button.RoundButton',

    xtype: 'un.roundbtn.merge',

    cls: 'un-roundbutton__merge',
    iconCls: 'icon-arrows-merge',
    tooltip: Unidata.i18n.t('cluster>merge.action'),
    config: {
        clusterCount: 0
    },

    updateClusterCount: function (count, countOld) {
        var iconCls,
            text,
            tooltip;

        if (count !== countOld) {
            if (count > 0) {
                iconCls = '';
                text = count;
                tooltip = Unidata.i18n.t('other>mergeRecordsClusterCounter', {count: count});
            } else {
                iconCls = 'icon-arrows-merge';
                text = '';
                tooltip = Unidata.i18n.t('cluster>merge.action');
            }

            this.setIconCls(iconCls);
            this.setText(text);
            this.setTooltip(tooltip);
        }
    }
});
