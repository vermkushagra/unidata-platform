/**
 * Tab panel (controller)
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.tabpanel.TabPanelController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifierviewer.tabpanel',

    classifiedEntityStatPanel: null,

    init: function () {
        this.callParent(arguments);
        this.initListeners();
    },

    initListeners: function () {
        var view = this.getView();

        view.addComponentListener('nodeselect', this.onNodeSelect, this);
        view.addComponentListener('datarecordopen', this.onDataRecordOpen, this);
    },

    onNodeSelect: function (classifierNode, classifier) {

        this.clearClassifedEntityStatPanel();

        if (!classifierNode) {
            return;
        }

        this.showClassifiedRecordListTab(classifierNode, classifier);
    },

    clearClassifedEntityStatPanel: function () {
        var view = this.getView();

        if (this.classifiedEntityStatPanel) {
            view.removeAll();
        }
    },

    onDataRecordOpen: function (cfg) {
        this.createRecordTab(cfg);
    },

    //TODO: rename to createDataRecordTab
    /**
     * Создать record tab (если это возможно)
     *
     * @param etalonId
     * @param metaRecord
     * @param title
     * @param sourceRecord
     * @param saveCallback
     */
    createRecordTab: function (cfg) {
        var view = this.getView(),
            dataViewerPanel,
            buildPanelCfg,
            panelCfg,
            searchHit = cfg.searchHit,
            metaRecord = cfg.metaRecord,
            dataRecordBundle = cfg.dataRecordBundle,
            etalonId = null,
            title = cfg.title;

        if (searchHit) {
            etalonId = searchHit.get('etalonId');
        } else if (dataRecordBundle) {
            etalonId = Unidata.util.DataRecordBundle.retrieveEtalonId(dataRecordBundle);
        }

        if (!view.onBeforeCreateDataRecordTab(etalonId)) {
            return;
        }

        title = title || view.tabLoadingTitleText;

        buildPanelCfg = {
            metaRecord: metaRecord,
            etalonId: etalonId
        };

        panelCfg = {
            closable: true,
            readOnly: true
        };

        dataViewerPanel = Unidata.util.DataViewerPanelFactory.buildDataViewerPanel(buildPanelCfg, panelCfg);
        this.configDataViewerPanelListeners(dataViewerPanel);
        view.add(dataViewerPanel);
        dataViewerPanel.loadAndShowDataViewer(buildPanelCfg);
    },

    configDataViewerPanelListeners: function (panel) {
        // Для полноценного редактирования записи необходимо дописать все обработчики

        //panel.on('savesuccess', this.onDataViewerSaveSuccess, this);
        //panel.on('deletesuccess', this.onDataViewerDeleteSuccess, this);
        //panel.on('datarecordstatuschanged', this.onDataViewerDataRecordStatusChanged, this);
        //panel.on('datacardloadfail', this.onDataViewerDataRecordDatacardloadfail, this);
        //panel.on('datarecordmanualmerge', this.onDataRecordManualMerge, this);
        panel.on('datarecordopen', this.onDataRecordOpen, this);
        //panel.on('dataviewerstatuschanged', this.onDataViewerStatusChanged, this);
    },

    showClassifiedRecordListTab: function (classifierNode, classifier) {
        var view = this.getView();

        this.classifiedEntityStatPanel = this.createClassifiedEntityStatPanel(classifierNode, classifier);
        view.add(this.classifiedEntityStatPanel);
        view.setActiveTab(this.classifiedEntityStatPanel);
        this.classifiedEntityStatPanel.loadAndShowClassifierEntities();
    },

    createClassifiedEntityStatPanel: function (classifierNode, classifier) {
        var panel,
            title;

        title = Unidata.util.Classifier.buildClassifierNodeTitle(classifier, classifierNode);

        panel = Ext.create('Unidata.view.classifierviewer.entitystat.ClassifierEntityStat', {
            classifier: classifier,
            classifierNode: classifierNode,
            title: Ext.String.htmlEncode(title),
            closable: false,
            cls: 'un-content-inner'
        });

        return panel;
    },

    onTabAdd: function (self, component) {
        if (!self.rendered) {
            return;
        }
        self.setActiveTab(component);
    }
});
