/**
 * Хранилище состояний компонентов ComponentState (панелей)
 * Компонент подписывается на событие change Unidata.module.ComponentState
 * По изменению состояния в этом компоненте происходит запись в localStorage
 *
 * @author Sergey Shishigin
 * @date 2017-07-05
 */
Ext.define('Unidata.module.ComponentStateStorage', {

    singleton: true,

    localStorage: null,
    localStorageKey: 'component-state',

    requires: ['Unidata.module.ComponentState'],

    constructor: function () {
        this.localStorage = Ext.util.LocalStorage.get('ud');

        if (!this.localStorage) {
            // build local storage with id: 'ud'
            this.localStorage = new Ext.util.LocalStorage({
                id: 'ud'
            });
        }

        this.initListeners();
        this.initFromLocalStorage();
    },

    initListeners: function () {
        Unidata.module.ComponentState.on('change', this.persistToLocalStorage, this);
    },

    /**
     * Начальная загрузка состояния из localStorage
     */
    initFromLocalStorage: function () {
        var ComponentState = Unidata.module.ComponentState,
            localStorage = this.localStorage,
            jsonStr,
            state;

        jsonStr = localStorage.getItem(this.localStorageKey);

        if (jsonStr) {
            state = Ext.util.JSON.decode(jsonStr);
            ComponentState.setState(state, true);
        }

        return state;
    },

    /**
     * Сохранить текущее состояние etalonClusters в локальное хранилище
     */
    persistToLocalStorage: function (obj) {
        this.localStorage.setItem(this.localStorageKey, JSON.stringify(obj));
    }
});
