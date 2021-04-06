/**
 * Лейаут отображения в одну колонку
 *
 * @author Ivan Marshalkin
 * @date 2016-05-25
 */

Ext.define('Unidata.view.steward.dataentity.layout.FlatLayout', {
    extend: 'Unidata.view.steward.dataentity.layout.AbstractLayout',

    tablets: null,

    referenceHolder: true,

    config: {
        enableComponentState: true
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function () {
        var me = this,
            enableComponentState;

        this.callParent(arguments);

        enableComponentState = this.getEnableComponentState();

        // размещаем по контейнерам таблетки
        Ext.Array.each(this.tablets, function (tablet) {
            var GroupPanel = Unidata.view.steward.dataentity.group.GroupPanel,
                group = tablet.attributeGroup,
                title = group.title,
                headerTooltip = group.headerTooltip,
                panel,
                stateComponentKey,
                stateComponentType,
                stateableCfg,
                cfg;

            cfg = {
                title: Ext.String.htmlEncode(title), // не забываем предотвращение XSS
                headerTooltip: headerTooltip,
                collapsed: title ? true : false,
                items: tablet
            };

            if (enableComponentState) {
                // определяем типа компонента с точки зрения хранения состояний
                stateComponentType = GroupPanel.resolveStateComponentType(tablet);
                // формируем ключ получения состояния панели
                stateComponentKey = GroupPanel.buildStateComponentKey(tablet, stateComponentType);
                // формируем конфиг для stateable панели (name + состояние)
                stateableCfg = Unidata.mixin.PanelStateable.getStateableCfg(stateComponentKey);
                // применяем
                Ext.apply(cfg, stateableCfg);
            }

            panel = Ext.create('Unidata.view.steward.dataentity.group.GroupPanel', cfg);

            if (enableComponentState) {
                panel.enableStateable();
            }

            me.add(panel);
        });
    }
});
