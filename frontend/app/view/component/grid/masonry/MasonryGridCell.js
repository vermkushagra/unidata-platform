/**
 * @author Aleksandr Bavin
 * @date 2017-08-24
 *
 * @property elPlaceholder
 * @property elDraggable
 * @property elOverlay
 * @property elFullWidth
 * @property elDragHandler
 * @property elMenu
 * @property elResizer
 * @property elContent
 * @property elDropGroup
 */
Ext.define('Unidata.view.component.grid.masonry.MasonryGridCell', {

    extend: 'Unidata.view.component.grid.masonry.MasonryGridComponent',

    alias: 'widget.component.grid.masonry.cell',

    requires: [
        'Unidata.view.component.grid.masonry.MasonryGridCellDropTarget',
        'Unidata.view.component.grid.masonry.MasonryGridCellResizer'
    ],

    baseCls: 'un-masonry-grid-cell',

    childEls: [
        {
            itemId: 'placeholder',
            name: 'elPlaceholder'
        },
        {
            itemId: 'draggable',
            name: 'elDraggable'
        },
        {
            itemId: 'overlay',
            name: 'elOverlay'
        },
        {
            itemId: 'full-width',
            name: 'elFullWidth'
        },
        {
            itemId: 'drag-handler',
            name: 'elDragHandler'
        },
        {
            itemId: 'menu',
            name: 'elMenu'
        },
        {
            itemId: 'resizer',
            name: 'elResizer'
        },
        {
            itemId: 'content',
            name: 'elContent'
        },
        {
            itemId: 'drop-group',
            name: 'elDropGroup'
        }
    ],

    targetEl: 'elContent',

    renderTpl: [
        '<div class="{baseCls}-placeholder" id="{id}-placeholder" data-ref="placeholder" style="width: {cellPercentWidth}"></div>',
        '<div class="{baseCls}-draggable" id="{id}-draggable" data-ref="draggable">',
            '<div class="{baseCls}-overlay" id="{id}-overlay" data-ref="overlay" style="width: {cellPercentWidth}"></div>',
            '<div class="{baseCls}-full-width" id="{id}-full-width" data-ref="full-width" style="width: {cellPercentWidth}">',
                '<div class="{baseCls}-content" id="{id}-content" data-ref="content"></div>',
                '<div class="{baseCls}-controls">',
                    '<div class="{baseCls}-drag-handler" id="{id}-drag-handler" data-ref="drag-handler"></div>',
                    '<div class="{baseCls}-menu" id="{id}-menu" data-ref="menu"></div>',
                '</div>',
                '<div class="{baseCls}-resizer" id="{id}-resizer" data-ref="resizer"></div>',
                '<div class="{baseCls}-drop-group" id="{id}-drop-group" data-ref="drop-group"><span>' + Unidata.i18n.t('masonryGrid>group') + '</span></div>',
            '</div>',
        '</div>'
    ],

    config: {
        widget: null, // текущий виджет
        columnIndex: 0, // колонка, в которой находится ячейка
        columnsCount: 1 // количество колонок, которое занимает ячейка
    },

    resizer: null,
    dragger: null,
    cellAvatar: null,

    constructor: function () {
        this.callParent(arguments);
    },

    onDestroy: function () {
        this.callParent(arguments);

        Ext.destroyMembers(
            this,
            'cellAvatar',
            'resizer',
            'dragger'
        );
    },

    onComponentRender: function () {
        this.callParent(arguments);
        this.initMouseEvents();
        this.initResizer();
        this.initMenu();
        this.initDragAndDrop();
    },

    initMouseEvents: function () {
        var el = this.getEl();

        el.on('mouseenter', this.onMouseEnter, this);
        el.on('mouseleave', this.onMouseLeave, this);
    },

    onMouseEnter: function () {
        this.getEl().addCls('un-over');
    },

    onMouseLeave: function () {
        this.getEl().removeCls('un-over');
    },

    getAvatar: function () {
        if (this.cellAvatar) {
            return this.cellAvatar;
        }

        this.cellAvatar = Ext.create('Unidata.view.component.grid.masonry.MasonryGridCellAvatar', {
            cell: this
        });

        return this.cellAvatar;
    },

    getRow: function () {
        return this.ownerCt;
    },

    onRemoved: function () {
        this.callParent(arguments);

        Ext.destroyMembers(this, 'cellAvatar');
    },

    onItemAdd: function (item) {
        this.callParent(arguments);

        if (item instanceof Unidata.view.component.grid.masonry.MasonryGrid) {
            this.addCls(this.baseCls + '-group');
        } else {
            this.removeCls(this.baseCls + '-group');
        }

        // обновляем виджет
        this.setWidget(item);
    },

    onItemRemove: function (item) {
        this.callParent(arguments);

        if (item instanceof Unidata.view.component.grid.masonry.MasonryGrid) {
            this.removeCls(this.baseCls + '-group');
        }
    },

    /**
     * Обновляет размеры ячейки
     */
    updateCellStyle: function () {
        var columnIndex = this.getColumnIndex(),
            columnsCount = this.getColumnsCount(),
            percentLeft = columnIndex * 100,
            percentWidth = columnsCount * 100;

        percentLeft = percentLeft + '%';
        percentWidth = percentWidth + '%';

        this.setStyle({
            left: percentLeft
        });

        if (this.rendered) {
            this.elPlaceholder.setStyle({
                width: percentWidth
            });

            this.elOverlay.setStyle({
                width: percentWidth
            });

            this.elFullWidth.setStyle({
                width: percentWidth
            });
        } else {
            this.setTplValue('cellPercentWidth', percentWidth);
        }
    },

    updateColumnIndex: function (columnIndex) {
        this.updateCellStyle();
        this.fireEvent('columnindexchange', this, columnIndex);
    },

    updateColumnsCount: function (columnsCount) {
        this.updateCellStyle();
        this.fireEvent('columnscountchange', this, columnsCount);
    },

    initMenu: function () {
        this.elMenu.on('click', function (e, el) {
            var settingsMenu = this.getSettingsMenu();

            settingsMenu.showBy(el, 'tl-bl?');
            settingsMenu.focus();
        }, this);
    },

    initDragAndDrop: function () {
        this.dragger = Ext.create(
            'Unidata.view.component.grid.masonry.MasonryGridCellDragger',
            this.elDraggable,
            'masonryGrid',
            {
                cell: this
            }
        );

        this.dragger.setOuterHandleElId(this.elDragHandler);

        this.dragger.on('startdrag', this.onDraggerStartDrag, this);
        this.dragger.on('enddrag', this.onDraggerEndDrag, this);
        this.dragger.on('invaliddrop', this.updateCellStyle, this);
        this.dragger.on('drop', this.onDraggerDrop, this);

        Ext.create(
            'Unidata.view.component.grid.masonry.MasonryGridCellDropTarget',
            this.elDropGroup.dom,
            {
                dropArea: Unidata.view.component.grid.masonry.MasonryGridCellDropTarget.dropArea.GROUP,
                cell: this
            }
        );
    },

    onDraggerStartDrag: function () {
        this.setDragging(true);
    },

    onDraggerEndDrag: function () {
        this.setDragging(false);
    },

    onDraggerDrop: function () {
        this.updateCellStyle();
    },

    getSettingsMenu: function () {
        if (this.settingsMenu) {
            return this.settingsMenu;
        }

        this.settingsMenu = Ext.create('Ext.menu.Menu', {
            defaults: {
                scope: this
            },
            items: [
                {
                    text: Unidata.i18n.t('masonryGrid>settingsMenu.widget'),
                    iconCls: 'icon-picture',
                    menu: {
                        plain: true,
                        defaults: {
                            scope: this,
                            handler: function (menuItem) {
                                var widget = this.setWidget(menuItem.widget);
                            }
                        },
                        items: this.getWidgetsList()
                    }
                },
                {
                    text: Unidata.i18n.t('masonryGrid>settingsMenu.delete'),
                    iconCls: 'icon-trash2',
                    handler: function () {
                        this.destroy();
                    }
                }
            ]
        });

        return this.settingsMenu;
    },

    getWidgetsList: function () {
        var widgetsList;

        widgetsList = [
            {
                text: Unidata.i18n.t('masonryGrid>widget.entityStats'),
                widget: {
                    xtype: 'component.dashboard.entity'
                }
            },
            {
                text: Unidata.i18n.t('masonryGrid>widget.tasksAndExport'),
                widget: {
                    xtype: 'component.dashboard.taskandexport'
                }
            }
        ];

        Unidata.uiuserexit.overridable.dashboard.DashboardWidgets.editWidgetsList(widgetsList);

        return widgetsList;
    },

    applyWidget: function (widget) {
        var widgetClass;

        if (widget instanceof Ext.Component) {
            return widget;
        }

        widgetClass = this.getWidgetClass(widget);

        // если это виджет-заглушка, получаем оригиналный
        if (widgetClass === Unidata.view.component.grid.masonry.MasonryGridNotFoundWidget) {
            widgetClass = this.getWidgetClass(widget.notFoundWidgetConfig);
            widget = widget.notFoundWidgetConfig;
        }

        if (widgetClass) {
            widget = Ext.create(widget);
        } else {
            widget = Ext.create('Unidata.view.component.grid.masonry.MasonryGridNotFoundWidget', {
                notFoundWidgetConfig: widget
            });
        }

        return widget;
    },

    /**
     * Возвращает класс виджета
     *
     * @param widgetConfig
     * @returns {Ext.Component.constructor|null}
     */
    getWidgetClass: function (widgetConfig) {
        if (widgetConfig.xclass) {
            return Ext.ClassManager.get(widgetConfig.xclass);
        }

        if (widgetConfig.xtype) {
            return Ext.ClassManager.getByAlias('widget.' + widgetConfig.xtype);
        }

        return null;
    },

    /**
     * Устанавливает виджет для отображения
     * @param {Object|Ext.Component} widget
     */
    updateWidget: function (widget) {
        var item =  this.getItemsCollection().getAt(0);

        // если виджет уже установлен - выходим
        if (!Ext.isEmpty(item) && item === widget) {
            this.notifyWidgetSet(this, widget);

            return;
        }

        this.removeAllItems(true);
        widget = this.addItem(widget);

        this.notifyWidgetSet(this, widget);

        this.updateLayout();
    },

    getColumnWidth: function () {
        return this.getRow().getWidth();
    },

    initResizer: function () {
        this.resizer = Ext.create('Unidata.view.component.grid.masonry.MasonryGridCellResizer', this.elResizer);

        this.resizer.on('startdrag', this.onResizerStartDrag, this);
        this.resizer.on('dragdeltachange', this.onResizerDragDeltaChange, this);
        this.resizer.on('enddrag', this.onResizerEndDrag, this);
    },

    onResizerStartDrag: function (resizer) {
        this.setResizing(true);
        this.fireEvent('startresize', this, resizer);
    },

    onResizerDragDeltaChange: function (resizer, deltaX, deltaY) {
        var columnsDelta = Math.round(deltaX / this.getColumnWidth()),
            resizePreviewWidth = (this.getColumnsCount() + columnsDelta) * 100;

        this.elOverlay.setWidth(resizePreviewWidth + '%');
    },

    onResizerEndDrag: function (resizer, deltaX, deltaY) {
        var columnsDelta = Math.round(deltaX / this.getColumnWidth());

        this.setResizing(false);

        if (columnsDelta === 0) {
            return;
        }

        this.setColumnsCount(this.getColumnsCount() + columnsDelta);

        this.fireEvent('endresize', this, resizer, deltaX, deltaY);

        this.updateLayout();
    },

    editSaveData: function (saveData) {
        var widget = this.getWidget(),
            widgetSaveData;

        // сохраеняем размеры ячейки
        saveData.columnIndex = this.getColumnIndex();
        saveData.columnsCount = this.getColumnsCount();

        // items не нужны, сохраняем, как виджет
        delete saveData.items;

        if (!widget) {
            return;
        }

        // если виджет-плейсхолдер, сохраняем значения оригинального виджета
        if (widget instanceof Unidata.view.component.grid.masonry.MasonryGridNotFoundWidget) {
            saveData.widget = widget.getNotFoundWidgetConfig();

            return;
        }

        if (widget.hasSavableMixin) {
            widgetSaveData = widget.getSaveData();
        } else {
            widgetSaveData = {xclass: widget.$className};
        }

        saveData.widget = widgetSaveData;
    }

});
