/**
 *
 * Панель с уведомлениями для согласования изменений
 *
 * @author Ivan Marshalkin
 * @date 2016-03-14
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.notice.bar.ApproveBar', {
    extend: 'Ext.container.Container',

    requires: [],

    alias: 'widget.steward.datacard.header.approvebar',

    cls: 'un-approvebar',

    referenceHolder: true,

    approveButton: null,   // ссылка на кнопунец

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'button',
            scale: 'small',
            color: 'orange',
            reference: 'approveButton',
            text: Unidata.i18n.t('dataviewer>approval')
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
        this.approveButton = this.lookupReference('approveButton');
    },

    /**
     * Обработчик рендеринга компонента
     */
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
        this.approveButton = null;

        this.callParent(arguments);
    },

    /**
     * Пробрасываем событие вверх
     */
    onComponentClick: function () {
        this.fireEvent('click');
    }
});
