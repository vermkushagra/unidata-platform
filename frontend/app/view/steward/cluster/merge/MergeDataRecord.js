/**
 * Компонент "Просмотр записи на консолидацию"
 *
 * @author Sergey Shishigin
 * @date 2016-10-26
 */
Ext.define('Unidata.view.steward.cluster.merge.MergeDataRecord', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.steward.cluster.merge.mergedatarecord',

    requires: [
        'Unidata.AttributeViewMode'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    mixins: [
        'Unidata.mixin.NoDataDisplayable'
    ],

    noDataText: Unidata.i18n.t('cluster>recordNotUpload'),

    cls: 'un-merge-data-record un-compare',
    header: false,

    config: {
        dataRecord: null,
        metaRecord: null,
        classifierNodes: null,
        hideAttributeTitle: null,
        noWrapTitle: false
    },

    etalonId: null, // хринит etalonId записи для нужд QA отдела

    referenceHolder: true,

    dataEntity: null,
    HIGHLIGHT_COLOR: '#d9f2d9',

    items: [
        {
            xtype: 'dataentity',
            reference: 'systemAttributeEntity',
            useCarousel: false,
            readOnly: true,
            flex: 1,
            depth: 1,
            noWrapTitle: true
        }
    ],

    initComponent: function () {
        var dataEntity,
            hideAttributeTitle,
            noWrapTitle,
            cfg;

        this.callParent(arguments);
        hideAttributeTitle = this.getHideAttributeTitle();
        noWrapTitle = this.getNoWrapTitle();

        cfg = {
            noWrapTitle: noWrapTitle
        };

        if (hideAttributeTitle !== null) {
            cfg = Ext.apply(cfg, {
                hideAttributeTitle: hideAttributeTitle
            });
        }

        dataEntity = this.buildDataEntity(cfg);
        this.insert(0, dataEntity);
        this.initReferences();

        this.on('render', this.onRenderInit, this, {single: true});
        this.relayEvents(this.dataEntity, ['datarecordopen']);
    },

    buildDataEntity: function (customCfg) {
        var cfg,
            cmp;

        customCfg = customCfg || {};

        cfg = {
            xtype: 'dataentity',
            reference: 'dataEntity',
            attributeViewMode: Unidata.AttributeViewMode.COMPARE,
            useCarousel: false,
            readOnly: true,
            preventMarkField: true,
            showEmptyClassifierAttributeTablet: true,
            showClassifierAttributeGroup: true,
            flex: 1
        };

        Ext.apply(cfg, customCfg);

        cmp = Ext.create(cfg);

        return cmp;
    },

    initReferences: function () {
        this.dataEntity            = this.lookupReference('dataEntity');
        this.systemAttributeEntity = this.lookupReference('systemAttributeEntity');
    },

    displaySystemAttributeEntity: function () {
        var dataRecord = this.getDataRecord(),
            systemAttributeEntity           = this.systemAttributeEntity,
            systemAttrEntityParams,
            EtalonSystemAttributeEntityUtil = Unidata.util.EtalonSystemAttributeEntity;

        if (dataRecord) {
            systemAttrEntityParams = EtalonSystemAttributeEntityUtil.buildSystemEntityParams(dataRecord);
        } else {
            systemAttrEntityParams = {
                metaRecord: null,
                dataRecord: null
            };
        }
        systemAttributeEntity.setEntityData(systemAttrEntityParams.metaRecord, systemAttrEntityParams.dataRecord);
        systemAttributeEntity.displayDataEntity();
    },

    onRenderInit: function () {
        var dataRecord                      = this.getDataRecord(),
            metaRecord                      = this.getMetaRecord(),
            systemAttributeEntity           = this.systemAttributeEntity,
            dataEntity                      = this.dataEntity;

        if (systemAttributeEntity && dataRecord && metaRecord && dataEntity) {
            this.displayDataEntity();
        } else {
            this.showNoData(this.noDataText);
        }
    },

    displayDataEntity: function () {
        var dataEntity,
            metaRecord      = this.getMetaRecord(),
            dataRecord      = this.getDataRecord(),
            classifierNodes = this.getClassifierNodes();

        this.hideNoData();

        dataEntity = this.dataEntity;

        this.displaySystemAttributeEntity();

        dataEntity.setEntityData(metaRecord, dataRecord, classifierNodes);
        dataEntity.displayDataEntity();
    },

    /**
     * highlight BVT Attributes
     *
     * @param attributePaths {Array[string]}
     */
    highlightBVTAttributes: function (attributeWinnersMap) {
        var dataRecord = this.getDataRecord(),
            etalonId,
            attributePaths;

        if (!dataRecord || !attributeWinnersMap) {
            return;
        }

        etalonId       = dataRecord.get('etalonId');
        attributePaths = this.pluckBVTAttributePaths(etalonId, attributeWinnersMap);

        this.dataEntity.highlightAttributeWinners(attributePaths);
    },

    pluckBVTAttributePaths: function (etalonId, attributeWinnersMap) {
        var paths = [];

        Ext.Object.each(attributeWinnersMap, function (key, value) {
            if (value === etalonId) {
                paths.push(key);
            }
        });

        return paths;
    },

    updateDataRecord: function () {
        this.etalonId = this.getEtalonId();
    },

    /**
     * Возвращает etalonId записи. Методы добавлены для QA отдела. Используются в автотестах
     *
     * добавлены по задаче UN-3928
     * @returns {*}
     */
    getEtalonId: function () {
        var dataRecord = this.getDataRecord(),
            etalonId = null;

        if (dataRecord) {
            etalonId = dataRecord.get('etalonId');
        }

        return etalonId;
    }
});
