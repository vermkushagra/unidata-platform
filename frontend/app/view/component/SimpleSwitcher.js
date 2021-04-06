/**
 * Простейшая переключалка двух состояний.
 * Переключение состояние реализуется по нажатию на ссылку с соответствующим заголовком.
 *
 * Состояния задаются конфигом:
 *   title {String}             Заголовок
 *   handlerParams {Object}     Конфиг обработчика (optional)
 *     handler {Function}       Функция
 *     scope {Object}           Скоуп (optional)
 *     args {Array}             Массив аргументов (optional)
 *
 */
Ext.define('Unidata.view.component.SimpleSwitcher', {
    extend: 'Ext.container.Container',

    xtype: 'un.simpleswitcher',

    cls: 'un-simple-switcher',

    config: {
        /**
         * Массив конфигов
         * Object[]
         *
         *   title {String}
         *   handlerParams {Object}
         *     handler {Function}
         *     scope {Object}
         *     args {Array}
         */
        stateCfgs: [],
        currentState: 0
    },

    statics: {
        states: {
            STATE_ONE: 0,
            STATE_TWO: 1
        }
    },

    initComponent: function () {
        this.callParent(arguments);
        this.wrapHandlerParamsInStateCfgs();
        this.refreshCurrentState(this.getCurrentState());
    },

    /**
     * Построение компонента "Ссылка"
     *
     * @param hrefLabelCfg
     * @returns {Unidata.view.component.HrefLabel}
     */
    buildHrefLabel: function (hrefLabelCfg) {
        var hrefLabel,
            title = hrefLabelCfg.title,
            handlerParams = hrefLabelCfg.handlerParams;

        hrefLabel = Ext.create('Unidata.view.component.HrefLabel', {
            width: 'auto',
            title: title,
            handlerParams: handlerParams
        });

        return hrefLabel;
    },

    /**
     * Переключение состояния
     */
    toggleState: function () {
        var SimpleSwitcher = Unidata.view.component.SimpleSwitcher,
            currentState = this.getCurrentState();

        // toggle currentState
        currentState = (currentState === SimpleSwitcher.states.STATE_ONE) ? SimpleSwitcher.states.STATE_TWO : SimpleSwitcher.states.STATE_ONE;

        this.setCurrentState(currentState);
    },

    /**
     * Обернуть обработчик функциональностью toggleState
     *
     * @param handlerParams
     * @returns {*}
     */
    wrapHandlerParamsWithToggleState: function (handlerParams) {
        var me = this,
            handler,
            args,
            scope;

        if (!handlerParams) {
            handlerParams = {
                handler: null,
                args: [],
                scope: null
            };
        }

        handler = Ext.clone(handlerParams.handler);
        args = handlerParams.args;
        scope = handlerParams.scope;

        scope = scope ? scope : this;

        handlerParams.handler = function () {
            var result = null;

            if (handler) {
                result = handler.apply(scope, args);
            }

            me.toggleState();

            return result;
        };

        return handlerParams;
    },

    /**
     * Обернуть все обработчики для конфигураций стейтов
     */
    wrapHandlerParamsInStateCfgs: function () {
        var stateCfgs = this.getStateCfgs();

        Ext.Array.each(stateCfgs, function (stateCfg) {
            stateCfg.handlerParams = this.wrapHandlerParamsWithToggleState(stateCfg.handlerParams);
        }, this);
    },

    getStateCfg: function (index) {
        var stateCfgs = this.getStateCfgs();

        if (!Ext.isArray(stateCfgs) || stateCfgs.length < index + 1) {
            throw new Error(Unidata.i18n.t('other>invalidStateCfgs'));
        }

        return stateCfgs[index];
    },

    updateCurrentState: function (currentState) {
        if (this.rendered) {
            this.refreshCurrentState(currentState);
        }
    },

    refreshCurrentState: function (currentState) {
        var stateCfg;

        stateCfg = this.getStateCfg(currentState);
        this.removeAll();
        this.add(this.buildHrefLabel(stateCfg));
    }
});
