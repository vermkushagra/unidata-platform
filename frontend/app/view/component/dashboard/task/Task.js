/**
 * Панель сводной информации о задачах
 *
 * @author Sergey Shishigin
 * @date 2017-08-04
 */
Ext.define('Unidata.view.component.dashboard.task.Task', {
    extend: 'Ext.container.Container',

    alias: 'widget.component.dashboard.task',

    controller: 'component.dashboard.task',

    requires: [
        'Unidata.view.component.dashboard.task.TaskController',
        'Unidata.view.component.Counter'
    ],

    cls: 'un-dashboard-task',

    layout: {
        type: 'hbox',
        pack: 'end',
        align: 'middle'
    },

    availableTaskCounter: null,
    myTaskCounter: null,

    methodMapper: [
        {
            method: 'onMyTaskCountChange'
        },
        {
            method: 'onAvailableTaskCountChange'
        },
        {
            method: 'onMyTaskCountClick'
        },
        {
            method: 'onAvailableTaskCountClick'
        }
    ],

    initItems: function () {
        this.callParent(arguments);

        this.add([
            {
                xtype: 'component.icon',
                iconCls: 'icon-inbox2'
            },
            {
                xtype: 'component.counter',
                reference: 'availableTaskCounter',
                cls: 'un-counter-available-task',
                fieldLabel: Unidata.i18n.t('workflow>tasksearch.commonTasks'),
                labelWidth: 95,
                value: 0,
                handler: {
                    fn: this.onMyTaskCountClick,
                    scope: this
                }
            },
            {
                xtype: 'component.counter',
                fieldLabel: Unidata.i18n.t('workflow>tasksearch.inProcess'),
                reference: 'myTaskCounter',
                cls: 'un-counter-my-task',
                labelWidth: 70,
                value: 0,
                handler: {
                    fn: this.onAvailableTaskCountClick,
                    scope: this
                }
            }
        ]);
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initListeners: function () {
        var eventBus,
            poller;

        poller = Unidata.module.poller.TaskCountPoller.getInstance();

        // init values
        if (Ext.isNumber(poller.myTaskCount)) {
            this.myTaskCounter.setValue(poller.myTaskCount);
        }

        if (Ext.isNumber(poller.availableTaskCount)) {
            this.availableTaskCounter.setValue(poller.availableTaskCount);
        }

        eventBus = poller.pollerEventBus;
        eventBus.on('changeavailablecount', this.onAvailableTaskCountChange, this);
        eventBus.on('changemycount', this.onMyTaskCountChange, this);
    },

    initReferences: function () {
        this.availableTaskCounter = this.lookupReference('availableTaskCounter');
        this.myTaskCounter = this.lookupReference('myTaskCounter');
    }
});
