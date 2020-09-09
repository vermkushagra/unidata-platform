/**
 *
 * Панель с уведомлениями для правил качества данных
 *
 * @author Ivan Marshalkin
 * @date 2016-03-14
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.notice.bar.DqBar', {
    extend: 'Ext.container.Container',

    requires: [
        'Unidata.view.steward.dataviewer.card.data.header.notice.panel.DqPanel'
    ],

    alias: 'widget.steward.datacard.header.dqbar',

    cls: 'un-dqbar',

    referenceHolder: true,

    errorButton: null,   // ссылка на кнопунец

    config: {
        dqErrorCount: null
    },

    items: [
        {
            xtype: 'button',
            reference: 'errorButton',
            scale: 'small',
            color: 'red',
            text: Unidata.i18n.t('common:errors')
        }
    ],

    /**
     * Инициализация компонента
     */
    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    /**
     * Инициализируем ссылка на компоненты
     */
    initComponentReference: function () {
        this.errorButton = this.lookupReference('errorButton');
    },

    onRender: function () {
        var el;

        this.callParent(arguments);

        el = this.getEl();
        el.on('click', this.onComponentClick, this);
    },

    /**
     * Обрабатываем уничтожение объекта
     */
    onDestroy: function () {
        this.errorButton = null;

        this.callParent(arguments);
    },

    /**
     * Пробрасываем событие клика вверх
     */
    onComponentClick: function () {
        this.fireEventArgs('click', arguments);
    },

    updateDqErrorCount: function (count) {
        var errorButton = this.errorButton;

        if (errorButton.rendered) {
            errorButton.setText(Unidata.i18n.t('dataviewer>errorsCount', {count: count}));
        } else {
            errorButton.on('render', function () {
                errorButton.setText(Unidata.i18n.t('dataviewer>errorsCount', {count: count}));
            }, this, {single: true});
        }
    }
});
