Ext.define('Unidata.view.steward.dataviewer.card.history.HistoryCardController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.steward.dataviewer.historycard',

    recordLoadDelayed: null,

    init: function () {
        this.recordLoadDelayed = Ext.create('Ext.util.DelayedTask');
    },

    /**
     * Запуск отложенной задачи на загрузку данных
     *
     * @param delay - задержка перед запуском в миллисекундах
     */
    runDelayedTask: function (delay) {
        var delayedTask = this.recordLoadDelayed;

        this.clearHistoryCard();

        delayedTask.cancel();
        delayedTask.delay(delay, this.loadHistoryRecord, this);
    },

    /**
     * Обработчик события изменения параметров пользователем
     */
    onChangeDateRange: function () {
        this.runDelayedTask(1000);
    },

    /**
     * Клик по кнопке загрузки истории записи
     */
    onLoadHistoryRecordClick: function () {
        this.runDelayedTask(0);
    },

    /**
     * Отображает экран записи
     */
    displayHistoryCard: function () {
        var me              = this,
            view            = me.getView(),
            metaRecord      = view.getMetaRecord(),
            dataRecord      = view.getDataRecord(),
            classifierNodes = view.getClassifierNodes();

        // выводим запись
        view.dataEntity.setEntityData(metaRecord, dataRecord, classifierNodes);
        view.dataEntity.displayDataEntity();
    },

    /**
     * Очищает экран
     */
    clearHistoryCard: function () {
        var view = this.getView();

        view.dataEntity.clearDataEntity();
    },

    /**
     * Подгружаем данные по заданным параметрам пользователем
     */
    loadHistoryRecord: function () {
        var me             = this,
            view           = this.getView(),
            date           = view.dateField.getValue(),
            lastUpdateDate = view.lastUpdateDateField.getValue(),
            etalonId       = view.getEtalonId(),
            record,
            proxy;

        record = Ext.create('Unidata.model.data.Record', {});

        record.setId(etalonId);

        proxy = record.getProxy();

        proxy.setDate(date);

        // lastUpdateDate - включительно
        lastUpdateDate.setDate(lastUpdateDate.getDate() + 1);
        proxy.setLastUpdateDate(lastUpdateDate);

        me.clearHistoryCard();

        me.setLoading();

        record.load({
            scope: this,
            failure: function () {
                me.unsetLoading();
            },
            success: function (record) {
                view.setDataRecord(record);

                me.displayHistoryCard();

                me.unsetLoading();
            }
        });

        proxy.setDate(null);
        proxy.setLastUpdateDate(null);
    },

    /**
     * Отображает маску "идет загрузка"
     */
    setLoading: function () {
        var view = this.getView();

        view.setLoading(Unidata.i18n.t('dataviewer>loadingData'));
    },

    /**
     * Скрывает маску "идет загрузка"
     */
    unsetLoading: function () {
        var view = this.getView();

        view.setLoading(false);
    }
});
