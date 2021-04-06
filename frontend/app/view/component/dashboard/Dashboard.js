/**
 * @author Aleksandr Bavin
 * @date 2017-06-21
 */
Ext.define('Unidata.view.component.dashboard.Dashboard', {

    extend: 'Ext.container.Container',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard',

    controller: 'component.dashboard',
    viewModel: 'component.dashboard',

    requires: [
        'Unidata.view.component.dashboard.DashboardController',
        'Unidata.view.component.dashboard.DashboardModel',
        'Unidata.view.component.dashboard.entity.EntityPanel',
        'Unidata.view.component.dashboard.task.Task'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    cls: 'un-dashboard',

    referenceHolder: true,

    scrollable: true,

    config: {
        grid: null
    },

    items: [
        {
            xtype: 'container',
            layout: 'hbox',
            defaults: {
                margin: '0 5 0 0'
            },
            margin: '0 0 10 0',
            items: [
                {
                    flex: 1
                },
                {
                    xtype: 'button',
                    text: Unidata.i18n.t('dashboard>save'),
                    scale: 'medium',
                    hidden: true,
                    bind: {
                        hidden: '{!gridEditMode}'
                    },
                    listeners: {
                        click: 'save'
                    }
                },
                {
                    xtype: 'button',
                    tooltip: Unidata.i18n.t('dashboard>switchModeTooltip'),
                    scale: 'medium',
                    cls: 'un-button-mode-switcher',
                    iconCls: 'icon-chart-settings',
                    bind: {
                        iconCls: '{editModeButtonIconCls}'
                    },
                    listeners: {
                        click: 'toggleGridView'
                    }
                }
            ]
        },
        {
            xtype: 'container',
            reference: 'gridContainer'
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        Ext.on('resize', this.onWindowResize, this);
    },

    onWindowResize: function () {
        if (this.isVisible(true)) {
            this.gridUpdateLayoutDelayed();
        }
    },

    componentAddedToHierarchy: function () {
        this.callParent(arguments);

        this.on('afterlayout', this.gridUpdateLayout, this, {single: true});
    },

    gridUpdateLayout: function () {
        this.getGrid().updateLayout();
    },

    gridUpdateLayoutDelayed: function () {
        this.getGrid().updateLayoutDelayed();
    },

    onRender: function () {
        this.callParent(arguments);

        this.initScrollManager();
    },

    /**
     * Инициализация скролла, при перетаскивании элементов
     */
    initScrollManager: function () {
        var el = this.getEl();

        el.ddScrollConfig = {
            animate: false,
            vthresh: 50,
            hthresh: -1,
            frequency: 5,
            increment: 5
        };

        Ext.dd.ScrollManager.register(el);
    },

    initItems: function () {
        var grid,
            viewModel;

        this.callParent(arguments);

        grid = this.getGrid();

        window.qwe = grid;

        if (grid) {
            grid.setEditMode(false);
            this.lookupReference('gridContainer').add(grid);
        } else {
            this.setGrid({
                xtype: 'component.grid.masonry'
            });
        }

        viewModel = this.getViewModel();
        viewModel.set('gridEditMode', grid.getEditMode());
    },

    applyGrid: function (grid) {
        if (grid instanceof Unidata.view.component.grid.masonry.MasonryGrid) {
            return grid;
        }

        grid = Unidata.view.component.grid.masonry.MasonryGrid.create(grid);

        return grid;
    },

    updateGrid: function (grid) {
        var viewModel = this.getViewModel(),
            gridContainer;

        viewModel.set('gridEditMode', grid.getEditMode());

        if (this.isConfiguring) {
            return;
        }

        gridContainer = this.lookupReference('gridContainer');

        gridContainer.removeAll(true);
        gridContainer.add(grid);
    },

    editSaveData: function (saveData) {
        var grid = this.getGrid();

        saveData.grid = grid.getSaveData();
    }

});
