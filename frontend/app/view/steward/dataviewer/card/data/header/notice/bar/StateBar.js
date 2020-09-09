/**
 *
 * Панель отображает статус записи
 *
 * @author Ivan Marshalkin
 * @date 2016-07-19
 */

Ext.define('Unidata.view.steward.dataviewer.card.data.header.notice.bar.StateBar', {
    extend: 'Ext.button.Button',

    requires: [],

    alias: 'widget.steward.datacard.header.statebar',

    scale: 'small',

    config: {
        dataCardState: null,
        redirectHash: null
    },

    /**
     * Инициализация компонента
     */
    initComponent: function () {
        this.callParent(arguments);

        this.on('afterrender', this.onAfterRender, this, {single: true});
    },

    /**
     * Устанавливает стейт
     * @param {String} state
     * @param {String} [redirectHash] - хэш для редиректа
     */
    setState: function (state, redirectHash) {
        this.setDataCardState(state);
        this.setRedirectHash(redirectHash);

        this.refreshDisplayableInfo();
    },

    /**
     * Обработчик события afterrender
     */
    onAfterRender: function () {
        this.refreshDisplayableInfo();
    },

    onClick: function () {
        var redirectHash = this.getRedirectHash();

        if (redirectHash) {
            Unidata.util.Router.redirectTo(redirectHash);
        }
    },

    /**
     * Обновляет отображаемую информацию в компоненте
     */
    refreshDisplayableInfo: function () {
        var cardState = this.getDataCardState(),
            cardConst = Unidata.view.steward.dataviewer.card.data.DataCardConst,
            hidden    = true,
            text,
            color,
            inactive;

        if (cardState === cardConst.DATACARD_STATE_READONLY) {
            hidden = false;
            text = Unidata.i18n.t('dataviewer>readOnly');
            color = 'gray';
            inactive = true;
        } else if (cardState === cardConst.DATACARD_STATE_PENDING) {
            hidden = false;

            // на экране согласования прячем
            if (Unidata.util.Router.getTokenValue('main', 'section') === 'tasks') {
                hidden = true;
            }

            text = Unidata.i18n.t('dataviewer>onApproval');
            color = 'pink';
            inactive = false;
        }

        // если кнопка еще не отрендерена то свойства надо установить отложено
        if (this.rendered) {
            this.setText(text);
            this.setColor(color);
            this.setInactive(inactive);
        } else {
            this.on('afterrender', function () {
                this.setText(text);
                this.setColor(color);
                this.setInactive(inactive);
            }, this, {single: true});
        }

        this.setHidden(hidden);
    }
});
