/**
 * Абстрактный класс, реализующий простые аттрибуты
 *
 * @property elSwitcher
 * @property elError
 * @property elBottom
 * @property elTitle
 * @property elInputCont
 * @property elViewValue
 * @property elApproveIndicator
 * @property elErrorIndicator
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.AbstractAttribute', {

    extend: 'Ext.Component',

    requires: [
        'Unidata.AttributeViewMode',
        'Unidata.util.GenerationStrategy'
    ],

    mixins: {
        search: 'Unidata.view.steward.dataentity.mixin.SearchableComponent',
        highlight: 'Unidata.mixin.DataHighlightable'
    },

    config: {
        metaRecord: null,
        dataRecord: null,
        metaAttribute: null,
        dataAttribute: null,
        oldDataAttribute: null,
        attributePath: '',
        viewMode: Unidata.AttributeViewMode.EDIT,
        inputVisible: false, // если true - инпут всегда виден
        inputValid: true, // !!! для внутреннего использования !!!
        readOnly: false,
        disabled: false,
        autoRender: true,
        indicator: null, // смотри функцию setIndicator
        preventMarkField: null,
        hideAttributeTitle: false,
        noWrapTitle: false,
        attributeDiff: null,
        alwaysShowInput: false, // всегда показывать input (не показывать плюсик)
        showTooltip: true
    },

    inputFocused: false, // фокус на инпуте

    restoreValueOnEsc: true, // восстанавливает значение, которое было при фокусе, когда кликаем на ESC
    focusInputValueCache: null,   // кэш значения инпута, при установке фокуса

    input: null,             // ссылка на поле вода
    baseTooltip: '',         // базовый текст тултипа, отображаемого при наведении на поле ввода

    inputWidth: '100%',
    //maxInputWidth: 'auto',

    baseCls: 'un-dataentity-attribute',

    invalidClass: 'un-dataentity-attribute-invalid',

    childEls: [
        {
            itemId: 'title',
            name: 'elTitle'
        },
        {
            itemId: 'switcher',
            name: 'elSwitcher'
        },
        {
            itemId: 'error',
            name: 'elError'
        },
        {
            itemId: 'input-cont-wrapper',
            name: 'elInputContWrapper'
        },
        {
            itemId: 'input-cont',
            name: 'elInputCont'
        },
        {
            itemId: 'notification',
            name: 'elNotification'
        },
        {
            itemId: 'i-error',
            name: 'elErrorIndicator'
        }
    ],

    renderTpl: [
        '<div class="{baseCls}-top">',
            '<tpl if="!hideIndicator">',
                '<div class="{baseCls}-indicator {baseCls}-indicator__error" ',
                    'id="{id}-i-error" data-ref="i-error"></div>',
            '</tpl>',
                '<div class="{baseCls}-title {baseCls}-title-text" id="{id}-title" data-ref="title">',
                    '{title:htmlEncode}',
                    '<tpl if="required">',
                        '<div class="{baseCls}-required {baseCls}-required" ',
                        'id="{id}-i-required" data-ref="i-required">*</div>',
                    '</tpl>',
                '</div>',
                '<div class="{baseCls}-switcher fa x-tool-img x-tool-plus" ',
                    'id="{id}-switcher" data-ref="switcher" tabindex="0" data-qtip="' + Unidata.i18n.t('dataentity>addValue') + '">',
                '</div>',
            '<div class="{baseCls}-input-cont-wrapper" id="{id}-input-cont-wrapper" data-ref="input-cont-wrapper">',
                '<div class="{baseCls}-notification" id="{id}-notification" data-ref="notification">',
                    '<object id="{id}-notification-icon" data-ref="notification-icon" class="{baseCls}-notification-icon" type="image/svg+xml" data="resources/icons/icon-notification-circle.svg"></object>',
                '</div>',
                '<div class="{baseCls}-input-cont" id="{id}-input-cont" data-ref="input-cont"></div>',
            '</div>',
        '</div>',
        '<div class="{baseCls}-error ',
            'x-form-error-msg x-form-invalid-under x-form-invalid-under-default" ',
            'id="{id}-error" data-ref="error" style="display: none;">',
        '</div>'
    ],

    statics: {
        TYPE: ''
    },

    /**
     * Конструктор
     */
    constructor: function (config) {
        var me = this,
            baseCls = me.baseCls,
            noWrapTitle = this.getNoWrapTitle(),
            type;

        noWrapTitle = config.noWrapTitle !== undefined ? config.noWrapTitle : noWrapTitle;

        me.renderData = {
            hideIndicator: false
        };

        if (config.renderData) {
            me.renderData = Ext.Object.merge(me.renderData, config.renderData);
        }

        me.indicator = {};

        me.CLASS_READONLY = baseCls + '-readOnly';
        me.CLASS_APPROVE  = baseCls + '-approve';
        me.CLASS_ERROR    = baseCls + '-error';

        me.callParent(arguments);

        if (noWrapTitle) {
            this.addCls(baseCls + '-nowraptitle');
        } else {
            this.removeCls(baseCls + '-nowraptitle');
        }

        type = this.getType().toLowerCase();

        this.addCls(baseCls + '-type-' + type);
    },

    /**
     * Путь к атрибуту с индексом
     *
     * @returns {*|void|string}
     */
    getDataAttributePath: function () {
        var dataAttribute = this.getDataAttribute();

        if (!dataAttribute) {
            return null;
        }

        return dataAttribute.getPath();
    },

    updateHideAttributeTitle: function () {
        if (!this.rendered) {
            return;
        }

        this.useHideAttributeTitle();
    },

    useHideAttributeTitle: function () {
        var baseCls = this.baseCls,
            hideAttributeTitle = this.getHideAttributeTitle();

        if (hideAttributeTitle) {
            this.addCls(baseCls + '-notitle');
        } else {
            this.removeCls(baseCls + '-notitle');
        }
    },

    /**
     * Деструктор
     */
    destroy: function () {

        if (this.rendered) {

            Ext.destroyMembers(
                this,
                'errorIndicator',
                // 'approveIndicator',
                'input'
            );
        }

        this.callParent(arguments);
    },

    /**
     * Рендеринг компонента
     */
    onRender: function () {
        var cfg,
            attributeDiff,
            alwaysShowInput;

        this.callParent(arguments);

        this.initElements();

        this.createIndicators();

        // readOnly и disabled также дополнительно проставляются позже (после создания инпута) в методе setInput
        cfg = {
            readOnly: true,
            disabled: false
        };

        cfg = this.buildAllowBlankConfig(cfg);

        attributeDiff = this.highlightDiff();
        this.setAttributeDiff(attributeDiff);

        alwaysShowInput = this.getAlwaysShowInput();

        // создание и инициализация состояния инпута
        this.setInput(this.initInput(cfg));

        if (alwaysShowInput) {
            this.showInput();
        }

        this.useHideAttributeTitle();
        this.initTitle();
        this.initSwitcher();
        this.updateInputVisibility();
        this.highlightWinner();
    },

    /**
     * Вычислить конфиг allowBlank для инпута атрибута
     * @param cfg
     * @returns {*}
     */
    buildAllowBlankConfig: function (cfg) {
        var GenerationStrategyUtil = Unidata.util.GenerationStrategy,
            dataRecord,
            attributePath,
            metaRecord,
            metaAttribute,
            isCodeAttribute,
            isAttrHasGenerationStrategy,
            isAttrUsedInCodeAttrGenerationStrategy,
            isAttrUsedInExternalIdGenerationStrategy;

        dataRecord = this.getDataRecord();
        attributePath = this.getAttributePath();
        metaRecord = this.getMetaRecord();
        metaAttribute = this.getMetaAttribute();

        // только для создаваемых записей
        if (dataRecord.phantom) {
            isCodeAttribute = Unidata.util.MetaAttribute.isCodeAttribute(metaAttribute);
            isAttrHasGenerationStrategy = GenerationStrategyUtil.isAttributeHasGenerationStrategy(metaAttribute);
            isAttrUsedInCodeAttrGenerationStrategy = GenerationStrategyUtil.isAttributeUsedInCodeAttributeGenerationStrategy(attributePath, metaRecord);
            isAttrUsedInExternalIdGenerationStrategy = GenerationStrategyUtil.isAttributeUsedInExternalIdGenerationStrategy(attributePath, metaRecord);

            // если у атрибута есть стратегия генерации то он не обязательный, т.к. в любом случае будет заполнен за счет генерации
            // если атрибут, не являющийся кодовым, учавствует в стратегии генерации "Конкатенации" для кодового атрибута или внешнего ключа, то он обязательный
            if (isAttrHasGenerationStrategy) {
                cfg = Ext.apply(cfg, {allowBlank: true});
            } else if (!isCodeAttribute && (isAttrUsedInCodeAttrGenerationStrategy || isAttrUsedInExternalIdGenerationStrategy)) {
                cfg = Ext.apply(cfg, {allowBlank: false});
            }
        }

        return cfg;
    },

    /**
     * Подсветить атрибут-победитель (выставить класс un-dataentity-highlight-winner)
     */
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

    initElements: function () {
        if (this.elSwitcher) {
            this.elSwitcher.setVisibilityMode(Ext.dom.Element.DISPLAY);
        }

        this.elError.setVisibilityMode(Ext.dom.Element.DISPLAY);
        this.elInputContWrapper.setVisibilityMode(Ext.dom.Element.DISPLAY);
    },

    initTitle: function () {
        if (this.elTitle) {
            this.elTitle.on('click', this.onTitleClick, this);
        }
    },

    onTitleClick: function () {
    },

    initSwitcher: function () {
        if (this.elSwitcher) {
            this.elSwitcher.on('focus', this.onSwitcherFocus, this);
        }
    },

    onSwitcherFocus: function () {
        var alwaysShowInput = this.getAlwaysShowInput();

        if (!alwaysShowInput) {
            this.showInput();
        }
        this.focusInput();
    },

    onInputFocus: function (input, event, eOpts) {
        var baseCls = this.baseCls;

        if (this.restoreValueOnEsc) {
            this.focusInputValueCache = input.getValue();
        }

        if (this.blurTimout) {
            clearTimeout(this.blurTimout);
            this.blurTimout = null;
        }

        this.inputFocused = true;
        this.addCls(baseCls + '-focus');

        this.fireEvent('focus', this, event, eOpts);
    },

    onInputBlur: function () {
        var baseCls = this.baseCls;

        if (this.blurTimout) {
            clearTimeout(this.blurTimout);
            this.blurTimout = null;
        }

        this.removeCls(baseCls + '-focus');

        this.blurTimout = Ext.defer(this.onInputDelayedBlur, 50, this);
    },

    onInputDelayedBlur: function (input, event, eOpts) {
        this.blurTimout = null;
        this.inputFocused = false;
        this.updateInputVisibility();
        this.fireEvent('blur', this, event, eOpts);
    },

    updateInputVisible: function () {
        this.updateInputVisibility();
    },

    updateInputValid: function (valid) {
        this.updateInputVisibility();
        this.updateErrorVisibility(valid);
    },

    createIndicators: function () {
        // создание индикаторов статуса
        //this.approveIndicator = Ext.create('Unidata.view.component.dataentity.IndicatorRound', {
        //    renderTo: this.elApproveIndicator,
        //    color: 'yellow',
        //    radius: 3
        //});

        this.errorIndicator = Ext.create('Unidata.view.component.dataentity.IndicatorRound', {
            renderTo: this.elErrorIndicator,
            color: 'red',
            radius: 2.5
        });
    },

    setInput: function (input) {
        var baseCls = this.baseCls,
            showTooltip = this.getShowTooltip(),
            type;

        if (input === this.input) {
            return;
        }

        this.suspendLayouts();

        if (this.input) {
            this.input.hide();
        }

        Ext.destroy(this.listenerRemovers);

        this.input = input;
        this.listenerRemovers = this.setupInputEventsListening();

        if (!input.rendered) {
            input.render(this.elInputCont);
        }

        type = this.getType().toLowerCase();
        input.addCls(baseCls + '-input');
        input.addCls(baseCls + '-input-' + type);

        this.fireEvent('inputset', this, input);

        input.show();

        this.initState();

        if (showTooltip) {
            this.initToolTip();
        }

        this.resumeLayouts(true);
    },

    getInput: function () {
        return this.input;
    },

    /**
     * Инициализация показа подсказки по наведению на поле ввода
     */
    initToolTip: function () {
        var me          = this,
            input       = me.input,
            baseTooltip,
            toolTip;

        if (!input) {
            return;
        }

        if (input.tip !== undefined) {
            return;
        }

        baseTooltip = me.buildBaseToolTip();

        if (!baseTooltip) {
            return;
        }

        toolTip = Ext.create('Ext.tip.ToolTip', {
            target: input.getEl(),
            html: '',
            dismissDelay: 8000,
            cls: this.baseCls + '-tip'
        });

        me.baseTooltip = baseTooltip;
        input.tip = toolTip;

        input.on('destroy', function () {
            Ext.destroyMembers(toolTip, 'tip');
        }, this);

        input.getEl().on('mouseenter', this.onInputMouseEnterTip, this);
    },

    /**
     * Обработчик наведения на поле ввода
     * Производит настройку содержимого тултипа
     */
    onInputMouseEnterTip: function () {
        var DataAttributeFormatterUtil = Unidata.util.DataAttributeFormatter,
            me    = this,
            input = me.input,
            metaAttribute = this.getMetaAttribute(),
            dataAttribute = this.getDataAttribute(),
            valueHtml = '',
            value,
            parseFormats,
            typeValue,
            html,
            codeValue;

        parseFormats = {
            Date: 'Y-m-dTH:i:s.u',
            Timestamp: 'Y-m-dTH:i:s.u',
            Time: 'Y-m-dTH:i:s.u'
        };

        typeValue = me.getMetaAttributeField('typeValue');

        value = dataAttribute.get('value');

        if (metaAttribute.isLookupEntityType() ||
            metaAttribute.isEnumDataType()) {
            codeValue = value;
            value = input.getRawValue();
        } else if (metaAttribute.isBlobOrClobAttribute()) {
            value = input.getValue();

            if (value) {
                value = value.fileName;
            }
        }

        value = DataAttributeFormatterUtil.formatValueByAttribute(metaAttribute, value, parseFormats);

        // measurement
        value = this.addMeasurementUnitText(value, metaAttribute, dataAttribute);

        if (!Ext.isEmpty(value)) {
            value = Ext.String.htmlEncodeMulti(value, 1);

            if (metaAttribute.isLookupEntityType()) {
                codeValue = Ext.String.htmlEncodeMulti(codeValue, 1);
                valueHtml = Ext.String.format('<div class="{0}">{1}<div class="{2}">{3}</div></div>',
                    this.baseCls + '-tip-value',
                    value,
                    this.baseCls + '-tip-code-value',
                    codeValue
                );
            } else {
                valueHtml = Ext.String.format('<div class="{0}">{1}</div>',
                    this.baseCls + '-tip-value',
                    value
                );
            }
        }

        html = Ext.String.format(me.baseTooltip, valueHtml);

        input.tip.setHtml(html);
    },

    /**
     * Иницилазирует базовое содержание тултипа
     *
     * @returns {*}
     */
    buildBaseToolTip: function () {
        var MetaAttributeFormatter = Unidata.util.MetaAttributeFormatter,
            AttributeDiff    = Unidata.util.AttributeDiff,
            me               = this,
            displayName      = me.getMetaAttributeField('displayName'),
            description      = me.getMetaAttributeField('description'),
            dataAttribute    = me.getDataAttribute(),
            metaAttribute    = me.getMetaAttribute(),
            attributeDiff    = me.getAttributeDiff(),
            diffAction,
            path             = this.getAttributePath(),
            diffInfo,
            oldDataAttribute,
            tooltip,
            typeValueDisplay,
            oldValue,
            oldDisplayValue;

        tooltip = Ext.String.format('{0}:', Ext.String.htmlEncodeMulti(displayName, 1));

        typeValueDisplay = MetaAttributeFormatter.buildDataTypeDisplayValue(metaAttribute);

        tooltip += Ext.String.format('<i>{0}</i>',
            Ext.String.htmlEncodeMulti(typeValueDisplay, 1));

        tooltip = Ext.String.format(
            '<div class="{0}">{1}</div>',
            this.baseCls + '-tip-title',
            tooltip
        );

        tooltip += '{0}'; // плейсхолдер для value

        if (!Ext.isEmpty(description)) {
            tooltip += Ext.String.format(
                '<div class="{0}">{1}</div>',
                this.baseCls + '-tip-description',
                Ext.String.htmlEncodeMulti(description, 1)
            );
        }

        if (attributeDiff) {
            oldDataAttribute = AttributeDiff.getOldAttribute(attributeDiff, path, dataAttribute);

            diffAction = attributeDiff.get('action');

            if (diffAction === 'CHANGED'  || diffAction === 'DELETED') {
                diffInfo = Unidata.i18n.t('dataentity>diff.valuechanged');

                if (oldDataAttribute && !(metaAttribute instanceof Unidata.model.attribute.ArrayAttribute)) {
                    oldValue = oldDataAttribute.get('value');
                    oldDisplayValue = oldDataAttribute.get('displayValue');

                    // если у атрибута указано displayValue, то его нужно выводить вместо value
                    if (metaAttribute && !Ext.isEmpty(oldDisplayValue)) {
                        oldValue = oldDisplayValue;
                    }

                    oldValue = Unidata.util.DataAttributeFormatter.formatValueByAttribute(metaAttribute, oldValue);

                    // measurement
                    oldValue = this.addMeasurementUnitText(oldValue, metaAttribute, oldDataAttribute);

                    diffInfo = Ext.String.format(
                        '<div class="{0}">{1}:</div><div class="{2}">{3}</div>',
                        this.baseCls + '-tip-title',
                        Unidata.i18n.t('dataentity>diff.previousvalue'),
                        this.baseCls + '-tip-value',
                        Ext.String.htmlEncodeMulti(oldValue, 1)
                    );
                }
            } else if (diffAction === 'ADDED') {
                diffInfo = Unidata.i18n.t('dataentity>diff.newvalue');
            }

            tooltip += Ext.String.format(
                '<div class="{0}">{1}</div>',
                this.baseCls + '-tip-diff',
                diffInfo
            );

        }

        return tooltip;
    },

    /**
     * Добавляет measurementUnit к значению
     *
     * @param value
     * @param metaAttribute
     * @param dataAttribute
     * @returns {string|*}
     */
    addMeasurementUnitText: function (value, metaAttribute, dataAttribute) {
        var valueId = metaAttribute.get('valueId'),
            measurementUnit;

        if (!Ext.isEmpty(valueId) && !Ext.isEmpty(value)) {
            measurementUnit = Unidata.util.api.MeasurementValues.getMeasurementUnit(
                valueId,
                dataAttribute.get('unitId')
            );

            if (measurementUnit) {
                value += ' ' + measurementUnit.get('shortName');
            }
        }

        return value;
    },

    /**
     * Инициализация состояния инпута
     */
    initState: function () {

        var me = this;

        me.suspendEvent('change');

        me.setInputValue(me.value);

        me.updateReadOnly(me.readOnly);
        me.updateDisabled(me.disabled);
        me.updateIndicator(me.indicator);

        me.resumeEvent('change');

    },

    updateReadOnly: function (value) {
        this.readOnly = value;

        if (this.input) {
            this.input.setReadOnly(value);
        }

        if (value) {
            this.addCls(this.CLASS_READONLY);
        } else {
            this.removeCls(this.CLASS_READONLY);
        }

        this.updateInputVisibility();
    },

    /**
     * Обновляет видимость инпута
     */
    updateInputVisibility: function () {
        var alwaysShowInput = this.getAlwaysShowInput();

        if (!alwaysShowInput && !this.getReadOnly() &&
            this.value === null &&
            !this.inputFocused &&
            !this.getInputVisible() &&
            this.getInputValid()
        ) {
            this.hideInput();
        } else {
            this.showInput();
        }
    },

    updateErrorVisibility: function (valid) {
        if (!this.elError) {
            return;
        }

        if (valid) {
            this.elError.hide();
        } else {
            this.elError.show();
        }
    },

    focusInput: function () {
        var me = this;

        if (this.input && this.input.focus) {
            // нужен таймаут, еще один... иначе может не срабатывать фокус в некторых случаях
            setTimeout(function () {
                me.input.focus(false, 1);
            }, 0);
        }
    },

    showInput: function () {
        if (this.elSwitcher && this.elInputContWrapper) {
            this.elSwitcher.hide();
            this.elInputContWrapper.show();
        }

        this.updateLayoutDelayed();
    },

    hideInput: function () {
        if (this.elInputContWrapper) {
            this.elInputContWrapper.hide();
        }

        if (this.elSwitcher && !this.getReadOnly()) {
            this.elSwitcher.show();
        }

        this.updateLayoutDelayed();
    },

    /**
     * Обновляет лэйаут с задержкой, что бы не вызывался лишний раз
     */
    updateLayoutDelayed: function () {
        if (this.delayedUpdate) {
            clearTimeout(this.delayedUpdate);
            this.delayedUpdate = null;
        }

        this.delayedUpdate = Ext.defer(this.updateLayout, 10, this);
    },

    updateLayout: function () {
        var input = this.getInput();

        if (input) {
            input.updateLayout();
        }

        this.callParent(arguments);
    },

    /**
     * Инициализация и создание поля ввода
     * @param customCfg Кастомный конфиг
     */
    initInput: function () {
        throw new Error(Unidata.i18n.t('dataentity>notImplementedInitInputInClass', {className: Ext.getClassName(this)}));
    },

    /**
     * Возвращает массив обработчиков
     *
     * @returns {Array}
     */
    setupInputEventsListening: function () {
        var input = this.getInput(),
            listenerRemovers = [];

        listenerRemovers.push(
            input.on({
                change: this.onChange,
                validitychange: this.onValiditychange,
                updatelayout: this.updateLayoutDelayed,
                setviewmode: this.setViewMode,
                focus: this.onInputFocus,
                blur: this.onInputBlur,
                specialkey: this.blurOnSpecialKey,
                destroyable: true,
                scope: this
            })
        );

        return listenerRemovers;
    },

    blurOnSpecialKey: function (field, e) {
        if (e.isStopped) {
            return;
        }

        if (e.getKey() === e.ESC) {
            this.onEscClick(field, e);
        }

        if (e.getKey() === e.ENTER) {
            field.blur();
        }
    },

    onEscClick: function (field) {
        if (this.restoreValueOnEsc) {
            this.getInput().setValue(this.focusInputValueCache);
        }

        field.blur();
    },

    setTitle: function (value) {
        var me = this;

        if (!me.rendered) {
            me.renderData.title = value;
        } else {
            me.elTitle.updateText(value);
        }

        me.title = value;

    },

    setRequired: function (nullable) {
        var required = !nullable,
            me = this;

        if (!me.rendered) {
            me.renderData.required = required;
        }

        me.required = required;

    },

    formatTitle: function (title) {
        var tpl = '{0}:';

        title = Ext.String.format(tpl, title);

        return title;
    },

    setValue: function (value, needAttributeUpdate) {
        var dataAttribute = this.getDataAttribute();

        this.value = Ext.clone(value); // копируем значение, на случай, если это массив или объект

        if (needAttributeUpdate) {
            dataAttribute.set('value', value);
        }

        this.setInputValue(value);

        this.updateInputVisibility();

        return this;
    },

    /**
     * Нужно в некоторых случаях переопределить эту функцию для аттрибутов
     *
     * @see Unidata.view.steward.dataentity.mixin.SearchableComponent#getDataForSearch
     *
     * @returns {String[]}
     */
    getDataForSearch: function () {
        return [
            String(this.value || '')
        ];
    },

    getValue: function () {
        return this.value;
    },

    setInputValue: function (value) {
        if (this.input) {
            this.input.setValue(value);
        }
    },

    setDisplayValue: function () {
    },

    getInputValue: function () {

        if (this.input) {
            return this.input.getValue();
        }

        return false;
    },

    getInputDisplayValue: function () {

        if (this.input && this.input.getDisplayValue) {
            return this.input.getDisplayValue();
        }

        return false;
    },

    /**
     * Формирует значение для хранения и отправки на сервер
     *
     * @returns {*}
     */
    getSubmitValue: function () {
        return this.getInputValue();
    },

    /**
     * Формириуем value для использования внутри компонента
     *
     * @param dataAttribute
     * @returns {*}
     */
    getInnerValue: function (dataAttribute) {
        return dataAttribute.get('value');
    },

    setDataAttribute: function (dataAttribute) {
        var innerValue;

        this.dataAttribute = dataAttribute;

        innerValue = this.getInnerValue(dataAttribute);

        this.setValue(innerValue);

        return this;
    },

    setMetaAttribute: function (metaAttribute) {

        this.setTitle(metaAttribute.get('displayName'));
        this.setRequired(metaAttribute.get('nullable'));

        this.metaAttribute = metaAttribute;

        return this;
    },

    /**
     * Возвращает тип атрибута
     *
     * @returns {string}
     */
    getType: function () {
        return this.self.TYPE;
    },

    applyReadOnly: function (value) {
        var metaAttribute = this.getMetaAttribute(),
            dataRecord = this.getDataRecord(),
            MetaAttributeUtil = Unidata.util.MetaAttribute;

        /**
         * Если в модели указано что атрибут read only то запрещаем устанавливать какие либо другие значения
         * см UN-2186
         */
        if (this.getMetaAttributeField('readOnly')) {
            value = true;
        } else {
            if (MetaAttributeUtil.isCodeAttributeExact(metaAttribute)) {
                /**
                 * UN-2212
                 * Если атрибут кодовый, и запись не новая - запрещаем редактировать
                 *
                 * UN-2594
                 * Необходимо так же учитывать что алтернативные кодовые атрибуты могут быть редактируемыми
                 *
                 * UN-4063
                 * FE: Массивы - Данные
                 * Альтернативные атрибуты также стали нередактируемыми в связи с тем что к ним добавились
                 * supplementary (которые не редактируемыми по своей сути).
                 */
                if (!dataRecord.phantom) {
                    value = true;
                }
            }
        }

        return value;
    },

    updatePreventMarkField: function (value) {
        if (this.input) {
            this.input.preventMark = value;

            // если хотим игнорировать ошибки тогда сбрасываем ошибки отображенные сейчас
            // иначе они будут отображаться всегда
            if (value) {
                this.input.clearInvalid();
            }
        }
    },

    setViewMode: function (value) {
        var viewModes = Unidata.AttributeViewMode;

        if (viewModes[value] === undefined) {
            throw new Error('Unknown attribute viewMode: ' + value);
        }

        return this.callParent(arguments);
    },

    /**
     * @param viewMode
     */
    updateViewMode: function (viewMode) {
        if (!this.isConfiguring) {
            this.onViewModeChange(viewMode);
        }
    },

    /**
     * @param viewMode
     */
    onViewModeChange: function () {
    },

    updateDisabled: function (value) {

        var me = this;

        me.disabled = value;

        if (me.input) {
            me.input.setDisabled(value);
        }

        return me;
    },

    /**
     * Изменение состояния индикатора
     *
     * @param {String} name - название индикатора ('error' или 'matching')
     * @param {String} state - состояние ('led' - яркий, 'on' - обычный, 'off' - выключен)
     */
    changeIndicator: function (name, state) {
        // ВНИМАНИЕ: this.indicator это _ОБЪЕКТ_ а this.changeIndicator модифицирует его а не подменяет
        // поэтому updateIndicator не вызывается после вызова метода this.changeIndicator

        this.indicator[name] = state;

        this.drawIndicator(this.indicator);
    },

    /**
     * Выключение индикатора
     *
     * @param {String} name - название отключаемого индикатора ('error' или 'matching')
     */
    disableIndicator: function (name) {
        this.changeIndicator(name, 'off');
    },

    /**
     * Задаёт индикатор в виде точки (двух точек) для аттрибута
     *
     * @example
     *
     * item.setIndicator({error: 'led', approve: 'led'}); // оба индикатора будут гореть ярко
     * item.setIndicator({error: 'led', approve: 'on'});  // красный индикатор - ярко, желтый - тускло
     * item.setIndicator({error: 'on', approve: 'on'});   // оба тускло
     * item.setIndicator({error: 'off', approve: 'on'});  // красный исчезнет, желтый - тускло
     * item.setIndicator({approve: 'on'});                // красный исчезнет, желтый - тускло
     *
     * @param {Object} indicator
     */
    updateIndicator: function (indicator) {
        // ВНИМАНИЕ: this.indicator это _ОБЪЕКТ_ а this.changeIndicator модифицирует его а не подменяет
        // поэтому updateIndicator не вызывается после вызова метода this.changeIndicator

        this.drawIndicator(indicator);
    },

    /**
     * Перерисовывает индикаторы в зависимости от переданного состояния индикаторов
     *
     * @param indicator - объект описывающий состояние индикаторов
     *
     * @returns {Unidata.view.steward.dataentity.attribute.AbstractAttribute}
     */
    drawIndicator: function (indicator) {
        var indicatorCode,
            indicatorStatus,
            me               = this,
            el               = me.el,
            CLASS_APPROVE    = me.CLASS_APPROVE,
            CLASS_ERROR      = me.CLASS_ERROR,
            //approveIndicator = me.approveIndicator,
            errorIndicator   = me.errorIndicator;

        indicator = indicator || {};

        me.indicator = indicator;

        if (!me.rendered) {
            return me;
        }

        el.removeCls([
            CLASS_APPROVE,
            CLASS_ERROR
        ]);

        errorIndicator
            .setActive(false)
            .removeTooltip()
            .hide();

        function setup (indicator, indicatorStatus, activeClass) {

            switch (indicatorStatus) {
                case 'off':
                    indicator.hide();
                    break;
                case 'led':
                    indicator.show();
                    indicator.setActive(true);
                    el.addCls(activeClass);
                    break;
                case 'on':
                    indicator.show();
                    indicator.setActive(false);
                    el.addCls(activeClass);
                    break;
                default:
                    throw new Error(Unidata.i18n.t('dataentity>unknownIndicatorStatus', {status: indicatorStatus}));
            }

        }

        for (indicatorCode in indicator) {

            if (!indicator.hasOwnProperty(indicatorCode)) {
                continue;
            }

            indicatorStatus = indicator[indicatorCode];

            switch (indicatorCode) {
                case 'error':

                    setup(errorIndicator, indicatorStatus, CLASS_ERROR);

                    errorIndicator.setTooltipText(Unidata.i18n.t('dataentity>badValue'));

                    this.setInputVisible(true);

                    break;
            }
        }

        return me;

    },

    clearIndicator: function () {
        this.setIndicator({});
    },

    onValiditychange: function (input, valid) {
        var baseCls = this.baseCls;

        this.setInputValid(valid);

        if (valid) {
            this.removeCls(baseCls + '-invalid');
        } else {
            this.addCls(baseCls + '-invalid');
        }

        this.fireEventArgs('validitychange', arguments);

        this.updateLayoutDelayed();
    },

    /**
     * Обработчик изменения значения инпута.
     * Внимание! В зависимости от класса, список событий, которые обрабатывает
     * этот коллбэк, может меняться
     */
    onChange: function () {

        var me = this,
            value,
            submitValue,
            dataAttributeRecord;

        // инициализация инпута, ничего не делаем
        if (me.events.change && me.events.change.suspended) {
            return;
        }

        // значение не изменилось, ничего не делаем
        if (!this.hasChanges()) {
            return;
        }

        submitValue = this.getSubmitValue();
        value = this.getInputValue();

        dataAttributeRecord = me.dataAttribute;

        // проверяем, можно ли нам менять значение
        if (me.fireEvent('beforechange', me, dataAttributeRecord, submitValue) !== false) {

            me.value = Ext.clone(value); // копируем занчение, на случай, если это массив или объект
            dataAttributeRecord.set('value', submitValue);

            this.updateDataAttributeOnChange(this.dataAttribute);

            me.fireEvent('change', me, dataAttributeRecord, submitValue);
        } else {
            // значение запретили менять, откатываемся назад
            me.setInputValue(me.value);
        }

    },

    updateDataAttributeOnChange: function (dataAttribute) {
        var inputDisplayValue = this.getInputDisplayValue();

        if (inputDisplayValue) {
            dataAttribute.set('displayValue', inputDisplayValue);
        }
    },

    hasChanges: function () {
        return this.value !== this.getInputValue();
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
     * Возвращает значение поля name из модели атрибута metaAttribute
     *
     * @returns {*}
     */
    getMetaAttributeName: function () {
        return this.getMetaAttributeField('name');
    },

    /**
     * Возвращает attributePath с присоединенным entityName
     *
     * Пример:
     * attributePath = 'complex.someAttributeName'
     * entityName = 'ADMZHDOR'
     *
     * Полный путь ADMZHDOR.complex.someAttributeName
     */
    getFullAttributePath: function () {
        var metaRecord    = this.getMetaRecord(),
            entityName    = metaRecord.get('name'),
            attributePath = this.getAttributePath();

        return [entityName, attributePath].join('.');
    },

    /**
     * Возвращает значение из dataAttribute
     * @returns {*}
     */
    getDataAttributeValue: function () {
        var dataAttribute = this.getDataAttribute();

        return dataAttribute.get('value');
    },

    /**
     * Возвращает валидность отображаемого поля ввода
     *
     * @returns {boolean}
     */
    isValid: function () {
        var me    = this,
            valid = true;

        if (me.input && Ext.isFunction(me.input.isValid)) {
            valid = me.input.isValid();
        }

        return valid;
    },

    validate: function () {
        if (this.input && Ext.isFunction(this.input.validate)) {
            return this.input.validate();
        }

        return true;
    },

    hideErrorMsg: function () {
        this.removeCls(this.invalidClass);

        if (this.rendered) {
            this.elError.hide();
        }
        this.updateLayoutDelayed();
    },

    showErrorMsg: function (msg) {
        this.addCls(this.invalidClass);

        if (this.rendered) {
            this.elError.setHtml(msg);
            this.elError.show();
        }

        this.updateLayoutDelayed();
    }

});
