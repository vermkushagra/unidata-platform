/**
 * Пункт меню "Задачи"
 *
 * @author Sergey Shishigin
 * @date 2017-05-18
 */
Ext.define('Unidata.view.main.menu.elements.TaskInnerItem', {

    extend: 'Unidata.view.main.menu.elements.InnerItem',

    alias: 'widget.un.list.item.mainmenu.item.inner.task',

    text:      Unidata.i18n.t('menu>tasks'),
    reference: 'tasks',
    iconCls:   'un-icon-4',
    colorIndex: 4,
    pinned: true,

    initListeners: function () {
        var eventBus,
            poller;

        poller = Unidata.module.poller.TaskCountPoller.getInstance();

        eventBus = poller.pollerEventBus;
        eventBus.on('changeavailablecount', this.onTaskCountChange, this);
        eventBus.on('changemycount', this.onTaskCountChange, this);
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initCounter();
        this.initListeners();
    },

    initCounter: function () {
        var poller;

        poller = Unidata.module.poller.TaskCountPoller.getInstance();

        if (Ext.isNumber(poller.myTaskCount) && Ext.isNumber(poller.availableTaskCount)) {
            this.setCounter(poller.myTaskCount + poller.availableTaskCount);
        }
    },

    onTaskCountChange: function (cfg) {
        this.setCounter(cfg.available_count + cfg.total_user_count);
    }
});
