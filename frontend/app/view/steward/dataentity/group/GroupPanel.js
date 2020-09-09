/**
 * Класс реализует панель для группы атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-05-25
 */

Ext.define('Unidata.view.steward.dataentity.group.GroupPanel', {
    extend: 'Ext.panel.Panel',

    cls: 'un-dataentity-attrgroup',
    ui: 'un-card',

    mixins: {
        highlight: 'Unidata.mixin.DataHighlightable',
        stateable: 'Unidata.mixin.PanelStateable'
    },

    collapsible: true,
    collapsed: false,
    titleCollapse: true,
    // hideCollapseTool: true,
    animCollapse: false,

    layout: 'fit',

    title: '',

    tools: [],

    config: {
        // Айтемы для рендера при разворачивании панельки
        itemsLazyRender: null,
        attributePath: '',
        stateComponentKey: null,
        headerTooltip: null
    },

    initComponent: function () {
        this.callParent(arguments);

        Ext.defer(this.lazyRender, 1, this, [this.getItemsLazyRender()]);
    },

    initTools: function () {
        this.callParent(arguments);

        // контейнер с размерами 0x0, для разворачивания панельки по фокусу
        if (this.getTitle()) {
            this.addTool({
                xtype: 'container',
                width: 0,
                height: 0,
                focusable: true,
                tabIndex: 0,
                listeners: {
                    focus: this.onHeaderFocus,
                    scope: this
                }
            });
        }
    },

    onHeaderFocus: function () {
        this.expand();
    },

    /**
     * Рендерит айтемы только при открытии панельки
     * @param items
     */
    lazyRender: function (items) {
        if (!this.collapsed) {
            this.add(items);
        } else {
            this.on('beforeexpand', function () {
                this.add(items);
            }, this, {single: true});
        }
    },

    /**
     * Рендеринг компонента
     */
    onRender: function () {
        var headerTooltip;

        this.callParent(arguments);

        headerTooltip = this.getHeaderTooltip();

        if (headerTooltip) {
            this.getHeader().on('render', function () {
                this.initTitleTooltip();
            }, this);
        }
    },

    // TODO: Использовать миксин Unidata.mixin.HeaderTooltipable
    initTitleTooltip: function () {
        var me          = this,
            header       = me.getHeader(),
            baseTooltip,
            toolTip;

        if (!header) {
            return;
        }

        if (header.tip !== undefined) {
            return;
        }

        baseTooltip = me.buildBaseToolTip();

        if (!baseTooltip) {
            return;
        }

        toolTip = Ext.create('Ext.tip.ToolTip', {
            target: header.getEl(),
            html: '',
            dismissDelay: 5000
        });

        me.baseTooltip = baseTooltip;
        header.tip = toolTip;

        header.getEl().on('mouseenter', this.onInputMouseEnterTip, this);
    },

    /**
     * Иницилазирует базовое содержание тултипа
     *
     * @returns {*}
     */
    buildBaseToolTip: function () {
        var tooltip;

        tooltip = '{0}'; // плейсхолдер для value

        return tooltip;
    },

    /**
     * Обработчик наведения на поле ввода
     * Производит настройку содержимого тултипа
     */
    onInputMouseEnterTip: function () {
        var me    = this,
            header = me.getHeader(),
            valueHtml,
            html;

        valueHtml = this.getHeaderTooltip();
        html = Ext.String.format(me.baseTooltip, valueHtml);

        header.tip.setHtml(html);
    },

    statics: {
        keyPrefix: 'group-panel-',
        /**
         * Сформировать имя компонента GroupPanel в хранилище состояний
         * @param tabletKeyPostfix Постфикс имени из tablet
         * @returns {string}
         */
        buildStateComponentName: function (tabletKeyPostfix) {
            var name,
                prefix = Unidata.view.steward.dataentity.group.GroupPanel.keyPrefix;

            name = prefix + tabletKeyPostfix;

            return name;
        },
        /**
         * Сформировать композитный ключ к хранилищу
         * @param tablet {Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet|Unidata.view.steward.dataentity.simple.AttributeTablet} Вложенный контейнер с данными
         * @param componentType {Unidata.module.ComponentState.componentTypes}
         * @returns {Array}
         */
        buildStateComponentKey: function (tablet, componentType) {
            var GroupPanel = Unidata.view.steward.dataentity.group.GroupPanel,
                tabletKey,
                stateComponentName,
                stateComponentKey,
                metaRecord,
                entityName;

            metaRecord = tablet.getMetaRecord();
            entityName = metaRecord.get('name');

            if (!tablet.buildTabletKey) {
                throw Error(Unidata.i18n.t('dataentity>notImplementedBuildTabletKeyInClass', {className: tablet.$className}));
            }

            tabletKey = tablet.buildTabletKey();
            stateComponentName = GroupPanel.buildStateComponentName(tabletKey);
            stateComponentKey = [entityName, componentType, stateComponentName];

            return stateComponentKey;
        },
        /**
         * Сформировать тип компонента исходя из типа контейнера
         * @param tablet {Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet|Unidata.view.steward.dataentity.simple.AttributeTablet} Вложенный контейнер с данными
         * @returns {Unidata.module.ComponentState.componentTypes|null}
         */
        resolveStateComponentType: function (tablet) {
            var ComponentTypes = Unidata.module.ComponentState.componentTypes,
                componentStateType = null;

            if (tablet instanceof Unidata.view.steward.dataentity.simple.ClassifierAttributeTablet) {
                componentStateType = ComponentTypes.CLASSIFIER_PANEL;
            } else if (tablet instanceof Unidata.view.steward.dataentity.simple.AttributeTablet) {
                componentStateType = ComponentTypes.GROUP_PANEL;
            }

            return componentStateType;
        }
    }
});
