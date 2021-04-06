/**
 * Абстрактный компонент с набором полезных методов
 *
 * @author Aleksandr Bavin
 * @date 2017-05-22
 */
Ext.define('Unidata.view.component.AbstractComponent', {

    extend: 'Ext.Component',

    targetEl: null, // куда рендерить tpl (string из childEls.name)

    /**
     * Права пользователя на компонент
     * key - resourceName
     * value - crud
     * @type {Object.<String, String[]>}
     */
    componentRights: null,

    inheritableStatics: {
        /**
         * Проверяет права пользователя
         * @param [componentRights] - если ничего не передать, то считается, что права есть
         * @returns {boolean}
         */
        hasComponentRights: function (componentRights) {
            var hasRights = false;

            if (Unidata.Config.isUserAdmin()) {
                return true;
            }

            if (componentRights) {
                Ext.Object.each(componentRights, function (resourceName, crud) {

                    if (resourceName === 'USER_DEFINED' && Unidata.Config.userHasUserDefinedRights()) {
                        hasRights = true;

                        return false;
                    }

                    if (Unidata.Config.userHasRights(resourceName, crud)) {
                        hasRights = true;

                        return false;
                    }
                });
            } else {
                return true;
            }

            return hasRights;
        }
    },

    privates: {
        // куда рендерится tpl
        getTargetEl: function () {
            return this[this.targetEl] || this.frameBody || this.el;
        }
    },

    constructor: function () {
        this.renderData = {};
        this.callParent(arguments);
    },

    initComponent: function () {
        this.callParent(arguments);
        this.on('render', this.onComponentRender, this);
    },

    /**
     * Проверяет права пользователя на компонент
     * @returns {boolean}
     */
    hasComponentRights: function () {
        return this.self.hasComponentRights(this.componentRights);
    },

    onComponentRender: function () {
    },

    setTplValue: function (key, value) {
        var data = {};

        data[key] = value;
        data = Ext.Object.merge(this.getData() || {}, data);

        if (!this.rendered) {
            this.renderData[key] = value;
        }

        this.data = data;

        if (this.rendered) {
            this.updateDataDelayed();
        }
    },

    updateDataDelayed: function () {
        clearTimeout(this.updateTimer);
        this.updateTimer = Ext.defer(function () {
            this.update(this.getData());
        }, 1, this);
    }

});
