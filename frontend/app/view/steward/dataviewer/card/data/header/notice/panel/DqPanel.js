/**
 *
 * Панель с уведомлениями для правил качества данных
 *
 * @author Ivan Marshalkin
 * @date 2016-03-14
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataviewer.card.data.header.notice.tablet.DqTablet'
    ],

    alias: 'widget.steward.datacard.header.dqpanel',

    referenceHolder: true,

    showByComponent: null, // компонет, относительно которого показываем панельку

    MAX_VISIBLE_TABLET: 7,  // максимальное количество отображаемых таблеток в свернутом виде

    config: {
        dataRecord: null,
        dqName: null
    },

    style: {
        'background-color': 'white',
        'border-top': 'solid 3px #D84315'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    scrollable: true,

    items: [],

    initComponent: function () {
        this.callParent(arguments);

        this.iniComponentEvent();
    },

    iniComponentEvent: function () {
        var showByComponent = this.showByComponent;

        // отслеживаем изменения лейаута для корректного позиционирования
        if (showByComponent) {
            showByComponent.on('afterlayout', this.onAfterlayout, this);
        }
        this.on('afterrender', this.onAfterrender, this);
        this.on('show', this.onPanelShow, this);
        this.on('hide', this.onPanelHide, this);
    },

    /**
     * обновляем позиционирование при скролле
     */
    onAfterrender: function () {
        var showByComponent = this.showByComponent,
            firstScrollable;

        firstScrollable = showByComponent.findParentBy(function (component) {
            if (component.getScrollable()) {
                return true;
            }
        });

        if (firstScrollable) {
            firstScrollable.getEl().on('scroll', this.updatePosition, this);
        }
    },

    onDestroy: function () {
        this.unsubscribeDocumentClickDq();

        this.callParent(arguments);
    },

    subscribeDocumentClickDq: function () {
        Ext.getDoc().on('click', this.onDocumentClickDqPanel, this);
    },

    unsubscribeDocumentClickDq: function () {
        Ext.getDoc().un('click', this.onDocumentClickDqPanel, this);
    },

    onDocumentClickDqPanel: function (event) {
        if (event.within(this.getEl())) {
            return;
        }

        if (this.showByComponent) {
            if (event.within(this.showByComponent.getEl())) {
                return;
            }
        }

        this.hide();
    },

    onPanelShow: function () {
        // обновляем список ошибок DQ
        this.refreshDQErrrorList();

        // reposition
        this.updatePosition();
        this.updatePanelHeight();
        this.subscribeDocumentClickDq();
    },

    onPanelHide: function () {
        this.unsubscribeDocumentClickDq();
    },

    onViewerResize: function () {
        this.updatePosition();
    },

    onAfterlayout: function () {
        this.updatePosition();
    },

    updatePosition: function () {
        if (!this.showByComponent) {
            return;
        }

        if (this.isVisible()) {
            this.showBy(this.showByComponent, 'tr-br', [0, 5]);
        }
    },

    togglePanel: function () {
        var panel = this;

        if (panel.isVisible()) {
            panel.hide();
        } else {
            panel.show();
        }
    },

    updateDataRecord: function (dataRecord) {
        this.buildDQErrorList(dataRecord);
    },

    /**
     * Обновляет список ошибок по текущему dataRecord
     */
    refreshDQErrrorList: function () {
        var dataRecord = this.getDataRecord();

        this.buildDQErrorList(dataRecord);
    },

    /**
     * Обновляет список ошибок по переданному dataRecord
     *
     * @param dataRecord - запись, которая содержит ошибки
     */
    buildDQErrorList: function (dataRecord) {
        var me       = this,
            dqErrors = dataRecord.dqErrors(),
            errors   = dqErrors.getRange();

        this.removeAll();

        if (!dqErrors.getCount()) {
            return;
        }

        // строим сортированный список по критичности
        errors = Ext.Array.merge(
            Unidata.util.DataRecord.getAllDqErrorsBySeverity(dataRecord, 'CRITICAL'),
            Unidata.util.DataRecord.getAllDqErrorsBySeverity(dataRecord, 'HIGH'),
            Unidata.util.DataRecord.getAllDqErrorsBySeverity(dataRecord, 'NORMAL'),
            Unidata.util.DataRecord.getAllDqErrorsBySeverity(dataRecord, 'LOW')
        );

        // создаем плитки
        Ext.Array.each(errors, function (error) {
            var tablet,
                tabletCfg;

            tabletCfg = {
                dqError: error
            };
            tablet = Ext.create('Unidata.view.steward.dataviewer.card.data.header.notice.tablet.DqTablet', tabletCfg);

            // восстанавливаем прокрутку после переключения таблетки
            tablet.on('expand', me.onExpandTablet, me);
            tablet.on('collapse', me.onCollapseTablet, me);

            me.add(tablet);
        });
    },

    onExpandTablet: function (tablet, expanded, dqName) {
        // сворачиваем все другие таблетки
        this.collapseTablets(null, [tablet]);

        // при разворачивании считаем что выбрано правило качества для отображения ошибок
        this.setDqName(dqName);

        this.onToggleTablet.apply(this, arguments);
    },

    onCollapseTablet: function (tablet, expanded, dqName) {
        var currentDqName = this.getDqName();

        // при сворачивании если свернули выбраное правило - сбрасываем выделение с полей
        if (currentDqName === dqName) {
            this.setDqName(null);
        }

        this.onToggleTablet.apply(this, arguments);
    },

    onToggleTablet: function () {
        var me = this,
            el = me.getEl(),
            top;

        if (this.isVisible()) {
            top = el.getScrollTop();
        }

        me.updateLayout();

        if (this.isVisible()) {
            el.setScrollTop(top);
        }
    },

    updateDqName: function (newDqName, oldDqName) {
        if (newDqName !== oldDqName) {
            this.fireEvent('changedq', newDqName);
        }
    },

    updatePanelHeight: function () {
        var me      = this,
            tablets = this.items.getRange(),
            height  = 0;

        tablets = tablets.slice(0, me.MAX_VISIBLE_TABLET);

        Ext.Array.each(tablets, function (tablet) {
            height += tablet.getHeight();
        });

        this.setMaxHeight(height * me.MAX_VISIBLE_TABLET / tablets.length);
    },

    /**
     * Возвращает список всех элементов таблеток
     *
     * @returns {Array}
     */
    getTablets: function () {
        var containers = [],
            items      = this.items;

        if (items && items.isMixedCollection) {
            containers = items.getRange();
        }

        return containers;
    },

    /**
     * Сворачивает таблетки
     * Если не указан tablets, тогда все таблетки
     *
     * @param tablets    - список таблеток для сворачивания
     * @param skipTablet - список таблеток, которые не нужно сворачивать
     */
    collapseTablets: function (tablets, skipTablet) {
        skipTablet = skipTablet || [];
        tablets    = tablets    || this.getTablets();

        if (!Ext.isArray(skipTablet)) {
            skipTablet = [skipTablet];
        }

        if (!Ext.isArray(tablets)) {
            tablets = [tablets];
        }

        Ext.Array.each(tablets, function (tablet) {
            var silent = true;

            if (!Ext.Array.contains(skipTablet, tablet)) {
                tablet.collapse(silent);
            }
        });
    }
});
