/**
 * Миксин для добавления панелям возможности сохранения состояния
 * @author Sergey Shishigin
 * @date 2016-09-02
 */
Ext.define('Unidata.mixin.PanelStateable', {
    extend: 'Ext.Mixin',

    config: {
        /**
         * Ключ хранения состояния
         * {String[]}
         */
        stateComponentKey: null
    },

    /**
     * Признак сохранения состояния панели
     * @private
     */
    isEnable: false,

    /**
     * Включить функциональность сохранения состояния панели
     */
    enableStateable: function () {
        if (!this.isEnable) {
            this.on('collapse', this.onGroupPanelCollapse);
            this.on('expand', this.onGroupPanelExpand);
            this.isEnable = true;
        }
    },

    /**
     * Выключить функциональность сохранения состояния панели
     */
    disableStateable: function () {
        if (this.isEnable) {
            this.removeListener('collapse', this.onGroupPanelCollapse);
            this.removeListener('expand', this.onGroupPanelExpand);
            this.isEnable = false;
        }
    },

    onGroupPanelCollapse: function (panel) {
        this.onGroupPanelCollapseToggle('collapsed', panel);
    },

    onGroupPanelExpand: function (panel) {
        this.onGroupPanelCollapseToggle('expanded', panel);
    },

    onGroupPanelCollapseToggle: function (collapseState, panel) {
        var ComponentState = Unidata.module.ComponentState,
            stateComponentKey = panel.getStateComponentKey(),
            collapsed,
            nestedState;

        if ((collapseState !== 'collapsed' && collapseState !== 'expanded') || !stateComponentKey) {
            return null;
        }

        collapsed = collapseState === 'collapsed';

        nestedState = {
            collapsed: collapsed
        };

        // при изменения состояния панели проставляем соответствующее состояние по ключу
        ComponentState.setNestedState(stateComponentKey, nestedState);
    },

    statics: {
        /**
         * Сформировать дополнительный конфиг панели, которая сохраняет состояние
         * @param stateComponentKey
         * @returns {{}}
         */
        getStateableCfg: function (stateComponentKey) {
            var ComponentState = Unidata.module.ComponentState,
                panelState,
                cfg = {};

            // получаем состояние панели
            panelState = ComponentState.getNestedState(stateComponentKey);
            // применяем состояние панели к конфигурации панели
            Ext.apply(cfg, panelState);
            Ext.apply(cfg, {stateComponentKey: stateComponentKey});

            return cfg;
        }
    }
});
