/**
 * Лейаут для ориджина :) Особенность в том что там карточки групп есть только для классификации
 *
 * @author Ivan Marshalkin
 * @date 2016-05-25
 */

Ext.define('Unidata.view.steward.dataentity.layout.OriginLayout', {
    extend: 'Unidata.view.steward.dataentity.layout.AbstractLayout',

    tablets: null,

    referenceHolder: true,

    config: {
        enableComponentState: false
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function () {
        var me = this,
            enableComponentState;

        this.callParent(arguments);

        // classifier tablet в конец
        this.tablets = Ext.Array.sort(this.tablets, function (a, b) {
            var compare = 0;

            if (a instanceof Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet) {
                compare = 1;
            } else if (b instanceof Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet) {
                compare = -1;
            }

            return compare;
        });

        enableComponentState = this.getEnableComponentState();

        // размещаем по контейнерам таблетки
        Ext.Array.each(this.tablets, function (tablet) {
            var GroupPanel = Unidata.view.steward.dataentity.group.GroupPanel,
                group = tablet.attributeGroup,
                classifier = tablet.classifier,
                attributePath = null,
                panel,
                cfg,
                stateComponentKey,
                stateableCfg,
                stateComponentType;

            if (group.groupType === 'CLASSIFIER') {
                if (classifier) {
                    attributePath = classifier.get('name');
                }

                cfg = {
                    title: Ext.String.htmlEncode(group.title), // не забываем предотвращение XSS
                    headerTooltip: group.headerTooltip,
                    items: tablet,
                    collapsed: true,
                    // сочетание collapseFirst:true и header.titlePosition:1 позволяет отобразить иконку сворачивания в начале header панели
                    collapseFirst: true,
                    header: {
                        titlePosition: 1
                    },
                    attributePath: attributePath
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

                me.add(panel);

                if (enableComponentState) {
                    panel.enableStateable();
                }
            } else {
                me.add(tablet);
            }
        });
    }
});
