/**
 * Модуль который управляет хранением состояний компонентов (карточки записи) в зависимости от реестра
 *
 * Состояние хранится в структуре state следующего вида
 *
 * state: {
 *   entityName: {
 *     componentType: {
 *       componentName: {
 *          //..componentState
 *       }
 *     }
 *   }
 * }
 *
 * где:
 * entityName - имя реестра/справочника
 * componentType - тип компонента (@see Unidata.module.ComponentState.componentTypes)
 * componentName - имя компонента
 *
 * @author Sergey Shishigin
 * @date 2017-07-05
 */
Ext.define('Unidata.module.ComponentState', {

    singleton: true,

    mixins: {
        observable: 'Ext.util.Observable'
    },

    state: null,

    // Типы компонентов (вторая составляющая ключа)
    componentTypes: {
        GROUP_PANEL: 'group_panels',
        COMPLEX_ATTRIBUTE_TABLET: 'complex_attribute_tablets',
        CLASSIFIER_PANEL: 'classifier_panels'
    },

    constructor: function () {
        this.mixins.observable.constructor.call(this);
        this.callParent(arguments);
    },

    /**
     * Получить полное состояние компонентов
     * @returns {Unidata.module.ComponentState.state|{}}
     */
    getState: function () {
        if (!this.state) {
            this.state = {};
        }

        return this.state;
    },

    /**
     * Установить полное состояние компонентов
     * @returns {Unidata.module.ComponentState.state|{}}
     */
    setState: function (state, silent) {
        silent = Ext.isBoolean(silent) ? silent : false;

        this.state = state;

        if (!silent) {
            this.fireEvent('change', this.getState());
        }
    },

    /**
     * Получить вложенное состояние по ключу
     * @param key {String[]|String} Составной ключ. Является массивом.
     * @param forceCreate {Boolean} Принудительно создать недостающие секции
     * @returns {Object|null}
     */
    getNestedState: function (key, forceCreate) {
        var state,
            nestedState = null,
            result;

        forceCreate = forceCreate !== undefined ? forceCreate : false;

        state = this.getState();

        if (!Ext.isString(key) && !Ext.isArray(key) && key !== null) {
            return null;
        }

        if (key === null || (Ext.isArray(key) && key.length === 0)) {
            return state;
        }

        if (Ext.isString(key)) {
            nestedState = state[key];

            return nestedState;
        }

        nestedState = state;
        result = Ext.Array.every(key, function (keyPart) {
            if (!nestedState[keyPart]) {
                if (forceCreate) {
                    nestedState[keyPart] = {};
                } else {
                    return false;
                }
            }

            nestedState = nestedState[keyPart];

            return true;
        });

        if (!result) {
            return null;
        }

        return nestedState;
    },

    /**
     * Установить вложенное состояние по ключу
     * @param key {String[]|String} Составной ключ. Является массивом
     * @param newNestedState {Object} Вложенное состояние
     * @returns {*}
     */
    setNestedState: function (key, newNestedState) {
        var parentNestedState,
            lastKey,
            oldNestedState;

        if (!Ext.isString(key) && !(Ext.isArray(key) && key.length > 0)) {
            return null;
        }

        if (Ext.isString()) {
            lastKey = key;
            key = [];
        } else {
            lastKey = key.pop();
        }

        parentNestedState = this.getNestedState(key, true);

        if (!parentNestedState) {
            return null;
        }

        // состояние не пустое, то
        if (newNestedState) {
            oldNestedState = parentNestedState[lastKey];

            if (oldNestedState !== newNestedState) {
                parentNestedState[lastKey] = newNestedState;
                this.fireEvent('change', this.getState());
            }
        } else {
            if (parentNestedState.hasOwnProperty(lastKey)) {
                delete parentNestedState[lastKey];
                this.fireEvent('change', this.getState());
            }
        }

        return parentNestedState[lastKey];
    },

    /**
     *
     * @param key {String[]} Составной ключ
     */
    removeNestedState: function (key) {
        return this.setNestedState(key, null);
    },

    /**
     *
     * @param keys {String[][]} Массив составных ключей
     */
    removeNestedStates: function (keys) {
        if (!Ext.isArray(keys) || keys.length === 0) {
            return null;
        }

        Ext.Array.each(keys, function (key) {
            this.setNestedState(key, null);
        });
    },

    pluckFirstLevelKeys: function () {
        return Ext.isObject(this.state) ? Ext.Object.getKeys(this.state) : [];
    }
});
