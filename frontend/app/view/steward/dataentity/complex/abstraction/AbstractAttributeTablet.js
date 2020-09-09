
/**
 * Базовый класс для представление комплексного атрибута (группа инстансов комплексного атрибута)
 * Данный класс используется расширенными классами представления комплексного атрибута: Карусель и Плоский вид
 *
 * @author Ivan Marshalkin
 * @date 2016-03-01
 */

Ext.define('Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTablet', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTabletController',
        'Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTabletModel',

        'Unidata.view.steward.dataentity.attribute.complex.ComplexAttribute',
        'Unidata.view.steward.dataentity.util.ComplexAttribute'
    ],

    alias: 'widget.steward.dataentity.complex.abstractattributetablet',

    controller: 'steward.dataentity.complex.abstractattributetablet',
    viewModel: {
        type: 'steward.dataentity.complex.abstractattributetablet'
    },

    mixins: {
        highlight: 'Unidata.mixin.DataHighlightable',
        stateable: 'Unidata.mixin.PanelStateable'
    },

    referenceHolder: true,

    ui: 'un-card',
    // cls: 'un-abstract-attribute-tablet',
    baseTipCls: 'un-abstract-attribute-tablet',

    collapsible: true,
    collapsed: false,
    titleCollapse: true,
    // hideCollapseTool: true,

    config: {
        metaRecord: null,
        dataRecord: null,
        metaNested: null,
        dataNested: null,
        attributePath: '',
        metaAttribute: null,
        dataAttribute: null,
        depth: 0,
        readOnly: null,
        hiddenAttribute: null,
        preventMarkField: null,
        attributeDiff: null
    },

    tools: [
        {
            xtype: 'un.fontbutton.additem',
            reference: 'addComplexAttributeButton',
            handler: 'onAddComplexAttributeClick',
            buttonSize: 'extrasmall',
            shadow: false,
            hidden: true,
            tooltip: Unidata.i18n.t('common:addSomething', {name: Unidata.i18n.t('glossary:complexAttribute')})
        }
    ],

    bind: {
        hidden: '{!isComplexAttributeVisible}'
    },

    getDataAttributePath: function () {
        var dataAttribute = this.getDataAttribute();

        if (!dataAttribute) {
            return null;
        }

        return dataAttribute.getPath();
    },

    /**
     * Рендеринг компонента
     */
    onRender: function () {
        var attributeDiff;

        this.callParent(arguments);
        this.highlightWinner();
        attributeDiff = this.highlightDiff();
        this.setAttributeDiff(attributeDiff);

        if (attributeDiff) {
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
            dismissDelay: 8000
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
        var me               = this,
            displayName      = me.getMetaAttributeField('displayName'),
            description      = me.getMetaAttributeField('description'),
            attributeDiff,
            diffAction,
            diffInfo,
            tooltip;

        tooltip = Ext.String.format('{0}: ', Ext.String.htmlEncodeMulti(displayName, 1));
        tooltip += Ext.String.format('<i>{0}</i>', Unidata.i18n.t('glossary:complexAttribute').toLowerCase());

        tooltip = Ext.String.format(
            '<div class="{0}">{1}</div>',
            this.baseTipCls + '-tip-title',
            tooltip
        );

        tooltip += '{0}'; // плейсхолдер для value

        if (!Ext.isEmpty(description)) {
            tooltip += Ext.String.format(
                '<div class="{0}">{1}</div>',
                this.baseTipCls + '-tip-description',
                Ext.String.htmlEncodeMulti(description, 1)
            );
        }

        attributeDiff = this.getAttributeDiff();

        if (attributeDiff) {
            diffAction = attributeDiff.get('action');

            if (diffAction === 'CHANGED') {
                diffInfo = Unidata.i18n.t('dataentity>diff.valuechanged');
            } else if (diffAction === 'ADDED') {
                diffInfo = Unidata.i18n.t('dataentity>diff.newvalue');
            } else if (diffAction === 'DELETED') {
                diffInfo = Unidata.i18n.t('dataentity>diff.valuedeleted');
            }

            tooltip += Ext.String.format(
                '<div class="{0}">{1}</div>',
                this.baseTipCls + '-tip-diff',
                diffInfo
            );
        }

        return tooltip;
    },

    /**
     * Возвращает значение поля fieldName из модели атрибута metaAttribute
     *
     * @param fieldName
     * @returns {*}
     */
    getMetaAttributeField: function (fieldName) {
        var result        = null,
            metaAttribute = this.getMetaAttribute();

        if (metaAttribute) {
            result = metaAttribute.get(fieldName);
        }

        return result;
    },

    /**
     * Обработчик наведения на поле ввода
     * Производит настройку содержимого тултипа
     */
    onInputMouseEnterTip: function () {
        var me    = this,
            header = me.getHeader(),
            valueHtml = '',
            html;

        html = Ext.String.format(me.baseTooltip, valueHtml);

        header.tip.setHtml(html);
    },

    highlightWinner: function () {
        var dataAttribute = this.getDataAttribute(),
            winner = dataAttribute.get('winner'),
            HighlightTypes = Unidata.view.steward.dataentity.DataEntity.highlightTypes;

        if (winner) {
            this.setHighlight(HighlightTypes.HIGHLIGHT_WINNER);
        }
    },

    // TODO: Move to mixin DiffHighlighable
    highlightDiff: function () {
        var HighlightTypes = Unidata.view.steward.dataentity.DataEntity.highlightTypes,
            AttributeDiff = Unidata.util.AttributeDiff,
            dataRecord = this.getDataRecord(),
            path = this.getAttributePath(),
            diffToDraft,
            attributeDiff;

        if (!Ext.isFunction(dataRecord.diffToDraft)) {
            return;
        }

        diffToDraft = dataRecord.diffToDraft();
        attributeDiff = AttributeDiff.findAttributeDiffByPath(diffToDraft, path);
        // если атрибут присутствует в массиве diffToDrafts, то подсвечиваем
        if (attributeDiff) {
            this.setHighlight(HighlightTypes.HIGHLIGHT_DIFF);
        }

        return attributeDiff;
    },

    /**
     * @param type
     * @private
     */
    highlightTablet: function (type) {
        var baseHighlightCls = this.baseHighlightCls,
            highlightCls = baseHighlightCls + '-' + type;

        this.addCls(highlightCls);
    },

    updateMetaAttribute: function (metaAttribute) {
        var metaRecord = this.getMetaRecord(),
            attributePath;

        if (metaAttribute && metaAttribute.get('readOnly')) {
            this.setReadOnly(true);
        }

        if (metaAttribute && metaRecord) {
            attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);
            this.setAttributePath(attributePath);
        }
    },

    updateMetaRecord: function (metaRecord) {
        var metaAttribute = this.getMetaAttribute(),
            attributePath;

        if (metaAttribute && metaRecord) {
            attributePath = Unidata.util.UPathMeta.buildAttributePath(metaRecord, metaAttribute);
            this.setAttributePath(attributePath);
        }
    }
});
