/**
 * @author Aleksandr Bavin
 * @date 2017-05-03
 */
Ext.define('Unidata.view.main.menu.MainMenu', {

    extend: 'Unidata.view.component.AbstractComponent',

    requires: [
        'Unidata.view.main.menu.MainMenuStructure',
        'Unidata.view.main.menu.MainMenuItem',
        'Unidata.view.main.menu.elements.*'
    ],

    alias: 'widget.mainmenu',

    layout: 'absolute',

    referenceHolder: true,

    baseCls: 'un-mainmenu',

    childEls: [
        {
            itemId: 'body',
            name: 'elBody'
        }
    ],

    config: {
        collapsed: true,
        lists: []
    },

    collapsedWidth: 54,
    expandedWidth: 250,
    animationDuration: 100,

    autoCollapseTimer: null,

    renderTpl: [
        '<div class="{baseCls}-body" id="{id}-body" data-ref="body">',
        '</div>'
    ],

    statics: {
        instance: null,

        tooltip: null,

        getTooltip: function () {
            if (this.tooltip) {
                return this.tooltip;
            }

            this.tooltip = Ext.create('Unidata.view.component.tooltip.Tooltip', {
                ui: 'un-dark',
                anchorTo: 'right',
                arrow: true,
                maxWidth: 450
            });

            return this.tooltip;
        },

        showTooltip: function (text, targetEl) {
            var tooltip = this.getTooltip();

            tooltip.setText(text);
            tooltip.showOver(targetEl);
        },

        hideTooltip: function () {
            this.getTooltip().hide();
        },

        isCollapsed: function () {
            return this.instance.getCollapsed();
        }
    },

    constructor: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        this.callParent(arguments);
        this.self.instance = this;

        DraftModeNotifier.subscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);
        DraftModeNotifier.subscribe(DraftModeNotifier.types.APPLYDRAFT, this.onGlobalDraftModeApply, this);
        DraftModeNotifier.subscribe(DraftModeNotifier.types.REMOVEDRAFT, this.onGlobalDraftModeRemove, this);
    },

    onDestroy: function () {
        var DraftModeNotifier = Unidata.module.notifier.DraftModeNotifier;

        Ext.Array.each(this.getLists(), function (item) {
            item.destroy();
        });

        if (this.self.tooltip) {
            this.self.tooltip.destroy();
            this.self.tooltip = null;
        }

        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.DRAFTMODECHANGE, this.onGlobalDraftModeChange, this);
        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.APPLYDRAFT, this.onGlobalDraftModeApply, this);
        DraftModeNotifier.unsubscribe(DraftModeNotifier.types.REMOVEDRAFT, this.onGlobalDraftModeRemove, this);

        this.callParent(arguments);
    },

    initComponent: function () {
        var MainMenuStructure = Unidata.view.main.menu.MainMenuStructure,
            lists = this.getLists();

        // инициализация дефолтного меню
        if (lists && lists.length === 0) {
            MainMenuStructure.setMenuComponent(this);
            this.setLists(MainMenuStructure.getMenuListsConfig());
        }

        this.callParent(arguments);
    },

    /**
     * Обработчик глобального изменения режима работы с черновиком
     *
     * @param draftMode
     */
    onGlobalDraftModeChange: function (draftMode) {
        this.disableMenuItemsOnDraftModeChangeEvent(draftMode);
        this.markMenuItemsOnDraftModeChangeEvent(draftMode);
        this.destroyMenuItemsViewOnDraftModeEvents();
    },

    /**
     * Обработчик глобального события - опубликование черновика
     *
     * @param draftMode
     */
    onGlobalDraftModeApply: function () {
        this.destroyMenuItemsViewOnDraftModeEvents();
    },

    /**
     * Обработчик глобального события - удаление черновика
     *
     * @param draftMode
     */
    onGlobalDraftModeRemove: function () {
        this.destroyMenuItemsViewOnDraftModeEvents();
    },

    /**
     * Удаляет созданные вьюшки из кеша с уничтожением для пунктов меню для которых разрешен режим работы в черновике
     * Текущее представление не уничтожается
     */
    destroyMenuItemsViewOnDraftModeEvents: function () {
        var selectedMenuItem = Unidata.view.main.menu.elements.InnerItem.getSelectedMenuItem(),
            centerListItems = this.getCenterListItems();

        Ext.Array.each(centerListItems, function (item) {
            if (item.getReference() === 'datamanagementlist') {
                Ext.Array.each(item.getSublist().getItems(), function (subItem) {
                    var subItemReference = subItem.getReference();

                    if (subItem.allowedDraftMode && subItem !== selectedMenuItem) {
                        Unidata.module.MainViewManager.removeComponentFromCache(subItemReference);
                    }
                });
            }
        });
    },

    /**
     * Деактивирует пункты меню недоступные в режиме реботы с черновиком
     *
     * @param draftMode
     */
    disableMenuItemsOnDraftModeChangeEvent: function (draftMode) {
        var centerListItems = this.getCenterListItems();

        Ext.Array.each(centerListItems, function (item) {
            if (item.getReference() !== 'datamanagementlist') {
                draftMode ? item.setDisabled(true) : item.setDisabled(false);
            } else {
                Ext.Array.each(item.getSublist().getItems(), function (subItem) {
                    var subItemReference = subItem.getReference();

                    if (!subItem.allowedDraftMode) {
                        draftMode ? subItem.setDisabled(true) : subItem.setDisabled(false);
                    }
                });
            }
        });
    },

    /**
     * Помечает пункты меню в режиме ченовика
     *
     * @param draftMode
     */
    markMenuItemsOnDraftModeChangeEvent: function (draftMode) {
        var centerListItems = this.getCenterListItems();

        Ext.Array.each(centerListItems, function (item) {
            if (item.getReference() === 'datamanagementlist') {
                Ext.Array.each(item.getSublist().getItems(), function (subItem) {
                    if (subItem.allowedDraftMode) {
                        draftMode ? subItem.setCounter('!') : subItem.setCounter(null);
                    }
                });
            }
        });
    },

    getCenterListItems: function () {
        var items = [],
            centerList = this.getList('center');

        if (centerList) {
            items = centerList.getItems();
        }

        return items;
    },

    onComponentRender: function () {

        this.initCollapse();

        Ext.Array.each(this.getLists(), this.renderList, this);

        this.initScroll();
    },

    applyLists: function (lists) {
        Ext.Array.each(lists, this.initList, this);

        return lists;
    },

    onItemSelected: function (component, item) {
        this.fireEvent('itemselected', this, item);
    },

    fireItemClick: function (component, item) {
        this.fireEvent('itemclick', this, item);

        if (item instanceof Unidata.view.main.menu.elements.InnerItem) {
            this.setCollapsed(true);
        }
    },

    /**
     * Инициализация списка меню
     */
    initList: function (list, index, lists) {
        if (!list) {
            return null;
        }

        if (!(list instanceof Ext.Component)) {
            if (!list.type) {
                list.type = 'mainmenu.default';
            }

            if (this.self.hasComponentRights(list.componentRights)) {
                list = Unidata.view.component.list.List.create(list);
            } else {
                // если нет прав
                list = null;
            }
        } else {
            if (!list.hasComponentRights()) {
                // если нет прав
                list = null;
            }
        }

        if (list) {
            list.ownerCt = this;
            list.on('itemselected', this.onItemSelected, this);
            list.on('itemclick', this.fireItemClick, this);
            this.renderList(list);
        }

        lists[index] = list;
    },

    renderList: function (list) {
        if (list && this.rendered) {
            list.render(this.elBody);
        }
    },

    /**
     * Находит и возвращает элемент списка по reference
     *
     * @param referenceName
     * @returns {null|Object}
     */
    getList: function (referenceName) {
        return Ext.Array.findBy(this.getLists(), function (list) {
            var listReference = list.getReference();

            return listReference === referenceName;
        });
    },

    /**
     * Возвращает все элементы всех списков
     *
     * @returns {Unidata.view.main.menu.MainMenuItem[]}
     */
    getMenuItems: function () {
        var allItems = [];

        Ext.Array.each(this.getLists(), function (list) {
            allItems = allItems.concat(list.getItems(true));
        }, this);

        return allItems;
    },

    /**
     * Возвращает элемент списка по reference
     *
     * @param referenceName
     * @returns {null|Unidata.view.main.menu.MainMenuItem}
     */
    getMenuItem: function (referenceName) {
        return Ext.Array.findBy(this.getMenuItems(), function (listItem) {
            return listItem.getReference() === referenceName;
        }, this);
    },

    initScroll: function () {
        var centerList = this.getList('center'),
            scrollable;

        if (!centerList) {
            return;
        }

        scrollable = centerList.getEl().dom;

        Ps.initialize(scrollable, {
            wheelSpeed: 1,
            wheelPropagation: true,
            minScrollbarLength: 20
        });

        this.on('resize', this.updateListsHeight, this);
    },

    updateScroll: function () {
        var scrollable = this.getList('center').getEl().dom;

        if (scrollable) {
            Ps.update(scrollable);
        }
    },

    updateListsHeight: function () {
        var listsHeight = 0,
            centerList;

        Ext.Array.each(this.getLists(), function (list) {
            var listReference = list.getReference();

            if (listReference !== 'center') {
                listsHeight += list.getHeight();
            } else {
                centerList = list;
            }
        });

        if (centerList) {
            centerList.setHeight(this.getHeight() - listsHeight);
            this.updateScroll();
        }
    },

    initCollapse: function () {
        var el = this.getEl();

        el.dom.addEventListener('mouseenter', Ext.bind(this.onMouseEnter, this));
        el.dom.addEventListener('mouseleave', Ext.bind(this.onMouseLeave, this));

        if (this.getCollapsed()) {
            this.setWidth(this.collapsedWidth);
        } else {
            this.setWidth(this.expandedWidth);
        }
    },

    onMouseEnter: function () {
        if (this.isCollapsed()) {
            return;
        }

        clearTimeout(this.autoCollapseTimer);
    },

    onMouseLeave: function (e) {
        if (this.isCollapsed()) {
            return;
        }

        this.autoCollapseTimer = Ext.defer(function () {
            this.setCollapsed(true);
        }, 750, this);
    },

    isCollapsed: function () {
        return this.getCollapsed();
    },

    toggleCollapsed: function () {
        this.setCollapsed(!this.getCollapsed());
    },

    updateCollapsed: function (collapsed) {
        var collapsedCls = this.baseCls + '-collapsed',
            animatingCls = this.baseCls + '-animating';

        this.addCls(animatingCls);

        Unidata.view.main.menu.elements.InnerItem.unpinTemp();

        if (collapsed) {
            this.addCls(collapsedCls);
        } else {
            this.removeCls(collapsedCls);
        }

        this.runCollapseAnimation(collapsed);
    },

    runCollapseAnimation: function (collapsed) {
        var animatingCls = this.baseCls + '-animating',
            scrollToExpanded = this.lastScrollExpanded || 0,
            scrollToCollapsed = this.lastScrollCollapsed || 0,
            el = this.getEl(),
            centerListEl;

        if (!el) {
            return;
        }

        centerListEl = this.getList('center').getEl();

        if (collapsed) {
            this.lastScrollExpanded = centerListEl.getScrollTop();
            centerListEl.scrollTo('top', scrollToCollapsed, {duration: this.animationDuration + 100});
            centerListEl.setHeight(centerListEl.getHeight() - 40);

            el.animate({
                duration: this.animationDuration,
                to: {
                    width: this.collapsedWidth
                },
                listeners: {
                    afteranimate: function () {
                        this.removeCls(animatingCls);
                        this.updateScroll();
                    },
                    scope: this
                }
            });
        } else {
            this.lastScrollCollapsed = centerListEl.getScrollTop();
            centerListEl.scrollTo('top', scrollToExpanded, {duration: this.animationDuration + 100});
            centerListEl.setHeight(centerListEl.getHeight() + 40);

            el.animate({
                duration: this.animationDuration,
                to: {
                    width: this.expandedWidth
                },
                listeners: {
                    afteranimate: function () {
                        this.removeCls(animatingCls);
                        this.updateScroll();
                    },
                    scope: this
                }
            });
        }
    }

});
