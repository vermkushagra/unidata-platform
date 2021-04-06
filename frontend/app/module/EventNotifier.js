/**
 * Уведомитель о системных событиях
 *
 * Асинхронно получает информацию о события (напр, от poller) и уведомляет пользователя (напр, всплыв. сообщ.)
 */
Ext.define('Unidata.module.EventNotifier', {
    singleton: true,

    newTasksText: null,

    requires: [
        'Unidata.module.poller.TaskCountPoller'
    ],

    constructor: function () {
        this.callParent(arguments);
        this.initListeners();
    },

    initListeners: function () {
        var poller = Unidata.module.poller.TaskCountPoller.getInstance(),
            eventBus;

        eventBus = poller.pollerEventBus;
        eventBus.on('changenewcount', this.onNewTaskCountChange, this);
    },

    /**
     * Уведомление о событии появления новых задач
     *
     * @param cfg
     * new_count_from_date
     */
    onNewTaskCountChange: function (cfg) {
        var newCount = cfg.new_count_from_date,
            tpl = Unidata.i18n.t('application>notifier.newTasks'),
            message,
            handlerDetails;

        message = Ext.String.format(tpl, newCount);

        handlerDetails = {
            title: Unidata.i18n.t('application>notifier.goToTasks'),
            handlerParams: {
                handler: this.onNewTasksShow,
                args: [],
                scope: this
            }
        };
        Unidata.showMessage(message, null, {autoClose: true}, handlerDetails);
    },

    onNewTasksShow: function () {
        Unidata.util.Router.setTokenValue('main', 'section', 'tasks');
    }
});
