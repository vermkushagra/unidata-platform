/**
 *
 * @author Ivan Marshalkin
 * @date 2016-03-14
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.HeaderBarController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.datacard.header',

    initPanels: function () {
        this.initDqPanel();
        this.initApprovePanel();
        this.initOperationIdPanel();

        this.initComponentEvent();
    },

    initComponentEvent: function () {
        var view = this.getView(),
            me   = this;

        view.dqBar.on('click', function () {
            me.onDqBarClick.apply(me, arguments);
        }, view);

        // view.approveBar.on('click', function () {
        //     me.onApproveBarClick.apply(me, arguments);
        // }, view);
    },

    initOperationIdPanel: function () {
        var view     = this.getView(),
            operationIdPanel = this.lookupReference('operationIdPanel'),
            dataCard = view.dataCard,
            operationId = dataCard.getOperationId();

        if (!operationId) {
            operationIdPanel.hide();

            return;
        }

        operationIdPanel.setHtml(Unidata.i18n.t('dataviewer>recordState') + operationId);
        operationIdPanel.show();
    },

    initDqPanel: function () {
        var view     = this.getView(),
            dataCard = view.dataCard,
            dqBar    = view.dqBar,
            panel,
            panelCfg;

        panelCfg = {
            floating: true,
            width: 300,
            hidden: true,
            showByComponent: dqBar
        };

        panel = Ext.create('Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel', panelCfg);

        dataCard.on('render', function () {
            panel.render(dataCard.getEl());
        }, this, {single: true});

        view.dqPanel = panel;
    },

    initApprovePanel: function () {
        var view     = this.getView(),
            dataCard = view.dataCard,
            panel,
            panelCfg;

        panelCfg = {
            floating: true,
            width: 300,
            hidden: true,
            dataCard: dataCard,
            approvehidden: false,
            declinehidden: false
        };

        panel = Ext.create('Unidata.view.steward.dataviewer.card.data.header.notice.panel.ApprovePanel', panelCfg);

        panel.on('approve', function () { view.fireEvent('approve'); }, this);
        panel.on('decline', function () { view.fireEvent('decline'); }, this);

        dataCard.on('render', function () {
            panel.render(dataCard.getEl());
        }, this, {single: true});

        view.approvePanel = panel;
    },

    onDqBarClick: function () {
        var view    = this.getView(),
            dqPanel = view.dqPanel;

        this.hideApprovePanel();

        if (dqPanel.isVisible()) {
            this.hideDqPanel();
        } else {
            this.showDqPanel();
        }
    },

    onApproveBarClick: function () {
        var view         = this.getView(),
            approvePanel = view.approvePanel;

        this.hideDqPanel();

        if (approvePanel.isVisible()) {
            this.hideApprovePanel();
        } else {
            this.showApprovePanel();
        }
    },

    onEtalonInfoMenuItemClick: function () {
        this.showEtalonInfoWindow();
    },

    onRefreshMenuItemClick: function () {
        var view = this.getView();

        view.fireEvent('recordrefresh');
    },

    onRefreshButtonClick: function () {
        var view = this.getView();

        view.fireEvent('recordrefresh');
    },
    showApprovePanel: function () {
        var view  = this.getView(),
            panel = view.approvePanel;

        this.hideAllPanel();

        if (panel && !panel.isDestroyed) {
            panel.show();

            this.subscribeDocumentClickApprove();
        }
    },

    hideApprovePanel: function () {
        var view  = this.getView(),
            panel = view.approvePanel;

        if (panel && !panel.isDestroyed) {
            panel.hide();

            this.unsubscribeDocumentClickApprove();
        }
    },

    showDqPanel: function () {
        var view  = this.getView(),
            panel = view.dqPanel;

        this.hideAllPanel();

        if (panel && !panel.isDestroyed) {
            panel.show();
        }
    },

    hideDqPanel: function () {
        var view  = this.getView(),
            panel = view.dqPanel;

        if (panel && !panel.isDestroyed) {
            panel.hide();
        }
    },

    hideAllPanel: function () {
        this.hideDqPanel();
        this.hideApprovePanel();
    },

    subscribeDocumentClickApprove: function () {
        Ext.getDoc().on('click', this.onDocumentClickApprovePanel, this);
    },

    unsubscribeDocumentClickApprove: function () {
        Ext.getDoc().un('click', this.onDocumentClickApprovePanel, this);
    },

    onDocumentClickApprovePanel: function (event) {
        var me           = this,
            view         = me.getView(),
            approvePanel = view.approvePanel,
            approveBar   = view.approveBar,
            elApproveBar,
            elApprovePanel;

        if (approvePanel) {
            elApprovePanel = approvePanel.getEl();
        }

        if (approveBar) {
            elApproveBar = approveBar.getEl();
        }

        if (event.within(elApproveBar) || event.within(elApprovePanel)) {
            return;
        }

        me.hideApprovePanel();
    },

    showApproveBar: function () {
        var view = me.getView();

        view.approveBar.show();
    },

    hideApproveBar: function () {
        var view = me.getView();

        view.approveBar.hide();
        this.hideApprovePanel();
    },

    updateDottedmenubuttonhidden: function (hidden) {
        var view       = this.getView(),
            dottedMenuButton = view.dottedMenuButton;

        // TODO: Ivan Marshalkin необходимо разобраться почему иногда вызыаем метод у несуществующего уже элемента
        if (dottedMenuButton && !dottedMenuButton.isDestroyed) {
            dottedMenuButton.setHidden(hidden);
        }
    },

    updateApprovebarhidden: function (hidden) {
        var view       = this.getView(),
            approveBar = view.approveBar;

        view.hideApprovePanel();

        // TODO: Ivan Marshalkin необходимо разобраться почему иногда вызыаем метод у несуществующего уже элемента
        if (approveBar && !approveBar.isDestroyed) {
            approveBar.setHidden(hidden);
        }
    },

    updateDqbarhidden: function (hidden) {
        var view  = this.getView(),
            dqBar = view.dqBar;

        view.hideDqPanel();

        // TODO: Ivan Marshalkin необходимо разобраться почему иногда вызыаем метод у несуществующего уже элемента
        if (dqBar && !dqBar.isDestroyed) {
            dqBar.setHidden(hidden);
        }
    },

    updateClusterCount: function (count) {
        var view  = this.getView(),
            clusterBar = view.clusterBar;

        // TODO: Ivan Marshalkin необходимо разобраться почему иногда вызыаем метод у несуществующего уже элемента
        if (clusterBar && !clusterBar.isDestroyed) {
            clusterBar.setClusterCount(count);
        }
    },

    updateDqErrorCount: function (count) {
        var view = this.getView();

        view.dqBar.setDqErrorCount(count);
    },

    setApproveInfo: function (info) {
        var view = this.getView();

        view.approvePanel.setApproveInfo(info);
    },

    onToggleCheckboxChange: function (self, showHidden) {
        var view = this.getView();

        view.fireEvent('togglehiddenattribute', showHidden);
    },

    updateToggleHiddenButtonState: function (hiddenAttribute, hiddenAttributeCount) {
        var view         = this.getView(),
            toggleButton = view.toggleHiddenAttributeButton,
            isAdmin      = Unidata.Config.isUserAdmin();

        toggleButton.hide();
        toggleButton.setValue(hiddenAttribute);

        if (hiddenAttributeCount > 0 && isAdmin) {
            toggleButton.show();
        }
    },

    updateDataCardStateInfo: function (dataCardState) {
        var view      = this.getView(),
            dataCard = view.dataCard,
            dataRecord = dataCard.getDataRecord(),
            workflowState = dataRecord.workflowState(),
            container = view.dataCardStatusContainer,
            taskId,
            hash;

        if (workflowState.getCount()) {
            taskId = workflowState.getAt(0).get('taskId');
            hash = Unidata.util.Router.buildHash([
                {
                    name: 'main',
                    values: {
                        section: 'tasks'
                    }
                },
                {
                    name: 'task',
                    values: {
                        taskId: taskId
                    }
                }
            ]);

            container.setState(dataCardState, hash);
        } else {
            container.setState(dataCardState);
        }
    }
});
