/**
 * Фабрика для создания табов dataRecord
 *
 * @author Sergey Shishigin
 * @date 2016-10-11
 */

Ext.define('Unidata.util.DataViewerPanelFactory', {
    singleton: true,

    requires: [
        'Unidata.view.steward.dataviewerpanel.DataViewerPanel'
    ],

    buildDataViewerPanel: function (buildCfg, panelCfg) {
        var panel,
            etalonId = buildCfg.etalonId,
            operationId = buildCfg.operationId,
            dataRecord = buildCfg.dataRecord,
            searchHit = buildCfg.searchHit,
            metaRecord = buildCfg.metaRecord,
            saveCallback = buildCfg.saveCallback,
            drafts = buildCfg.drafts,
            title;

        panelCfg = panelCfg || {};

        // определяем etalonId на основании имеющихся данных
        if (!etalonId) {
            if (dataRecord) {
                etalonId = dataRecord.get('id');
            } else if (searchHit) {
                etalonId = searchHit.get('etalonId');
            }
        }

        // формируем title для панели, если возможно
        if (!etalonId) {
            title = Unidata.i18n.t('util>newRecord');
        } else if (etalonId && metaRecord && searchHit) {
            title = Unidata.util.DataAttributeFormatter.buildEntityTitleFromSearchHit(metaRecord, searchHit);
        }

        Ext.apply(panelCfg, {
            saveCallback: saveCallback,
            etalonId: etalonId,
            operationId: operationId,
            title: title,
            drafts: drafts
        });

        panel = Ext.create('Unidata.view.steward.dataviewerpanel.DataViewerPanel', panelCfg);

        return panel;
    }
});
