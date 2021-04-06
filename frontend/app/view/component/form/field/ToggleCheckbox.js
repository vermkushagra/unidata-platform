/**
 * Компонент "переключатель"
 */
Ext.define('Unidata.view.component.form.field.ToggleCheckbox', {
    extend: 'Ext.form.field.Checkbox',
    alias: ['widget.togglecheckboxfield', 'widget.togglecheckbox'],

    cls: 'un-toggle-checkbox',
    labelAlign: 'right',
    labelSeparator: '',

    // Переопределяем шаблон fieldSubTpl из Ext.form.field.Checkbox - вносим 2 изменения (см. коммент)
    // note: {id} here is really {inputId}, but {cmpId} is available
    fieldSubTpl: [
        '<div class="{wrapInnerCls} {noBoxLabelCls}" role="presentation">',
            '<tpl if="labelAlignedBefore">',
                '{beforeBoxLabelTpl}',
                '<label id="{cmpId}-boxLabelEl" data-ref="boxLabelEl" {boxLabelAttrTpl} class="{boxLabelCls} ',
                        '{boxLabelCls}-{ui} {boxLabelCls}-{boxLabelAlign} {childElCls}" for="{id}">',
                    '{beforeBoxLabelTextTpl}',
                    '{boxLabel}',
                    '{afterBoxLabelTextTpl}',
                '</label>',
                '{afterBoxLabelTpl}',
            '</tpl>',
            // Первое изменение относительно Ext.form.field.Checkbox: type="checkbox" вместо type="button"
            '<input type="checkbox" id="{id}" data-ref="inputEl" role="{role}" {inputAttrTpl}',
                '<tpl if="tabIdx != null"> tabindex="{tabIdx}"</tpl>',
                '<tpl if="disabled"> disabled="disabled"</tpl>',
                '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
                ' class="{fieldCls} {typeCls} {typeCls}-{ui} {inputCls} {inputCls}-{ui} {childElCls} {afterLabelCls}" autocomplete="off" hidefocus="true" />',
        // Второе изменение относительно Ext.form.field.Checkbox: убрали проверку на существование boxLabel
        '<tpl if="!labelAlignedBefore">',
                '{beforeBoxLabelTpl}',
                '<label id="{cmpId}-boxLabelEl" data-ref="boxLabelEl" {boxLabelAttrTpl} class="{boxLabelCls} ',
                        '{boxLabelCls}-{ui} {boxLabelCls}-{boxLabelAlign} {childElCls}" for="{id}">',
                    '{beforeBoxLabelTextTpl}',
                    '{boxLabel}',
                    '{afterBoxLabelTextTpl}',
                '</label>',
                '{afterBoxLabelTpl}',
            '</tpl>',
        '</div>',
        {
            disableFormats: true,
            compiled: true
        }
    ],

    // Переопределяем шаблон labelableRenderTpl из Ext.form.Labelable - добавлена возможность отображать лейбл после чекбокса
    /**
     * @cfg {String/String[]/Ext.XTemplate} labelableRenderTpl
     * The rendering template for the field decorations. Component classes using this mixin
     * should include logic to use this as their {@link Ext.Component#renderTpl renderTpl},
     * and implement the {@link #getSubTplMarkup} method to generate the field body content.
     * @private
     */
    labelableRenderTpl: [
        '<tpl if="mainLabelAlignedBefore">',
            '{beforeLabelTpl}',
            '<label id="{id}-labelEl" data-ref="labelEl" class="{labelCls} {labelCls}-{ui} {labelClsExtra} ',
                    '{unselectableCls}" style="{labelStyle}"<tpl if="inputId">',
                    ' for="{inputId}"</tpl> {labelAttrTpl}>',
                '<span class="{labelInnerCls} {labelInnerCls}-{ui}" style="{labelInnerStyle}">',
                '{beforeLabelTextTpl}',
                '<tpl if="fieldLabel">{fieldLabel}',
                    '<tpl if="labelSeparator">{labelSeparator}</tpl>',
                '</tpl>',
                '{afterLabelTextTpl}',
                '</span>',
            '</label>',
            '{afterLabelTpl}',
        '</tpl>',
        '<div id="{id}-bodyEl" data-ref="bodyEl" class="{baseBodyCls} {baseBodyCls}-{ui}<tpl if="fieldBodyCls">',
            ' {fieldBodyCls} {fieldBodyCls}-{ui}</tpl> {growCls} {extraFieldBodyCls}"',
            '<tpl if="bodyStyle"> style="{bodyStyle}"</tpl>>',
            '{beforeBodyEl}',
            '{beforeSubTpl}',
            '{[values.$comp.getSubTplMarkup(values)]}',
            '{afterSubTpl}',
            '{afterBodyEl}',
        '</div>',
        '<tpl if="!mainLabelAlignedBefore">',
            '{beforeLabelTpl}',
            '<label id="{id}-labelEl" data-ref="labelEl" class="{labelCls} {labelCls}-{ui} {labelClsExtra} ',
                    '{unselectableCls}" style="{labelStyle}"<tpl if="inputId">',
                    ' for="{inputId}"</tpl> {labelAttrTpl}>',
                '<span class="{labelInnerCls} {labelInnerCls}-{ui}" style="{labelInnerStyle}">',
                '{beforeLabelTextTpl}',
                '<tpl if="fieldLabel">{fieldLabel}',
                    '<tpl if="labelSeparator">{labelSeparator}</tpl>',
                '</tpl>',
                '{afterLabelTextTpl}',
                '</span>',
            '</label>',
            '{afterLabelTpl}',
        '</tpl>',
        '<tpl if="renderError">',
            '<div id="{id}-errorWrapEl" data-ref="errorWrapEl" class="{errorWrapCls} {errorWrapCls}-{ui}',
                    ' {errorWrapExtraCls}" style="{errorWrapStyle}">',
                '<div role="alert" aria-live="polite" id="{id}-errorEl" data-ref="errorEl" ',
                    'class="{errorMsgCls} {invalidMsgCls} {invalidMsgCls}-{ui}" ',
                    'data-anchorTarget="{id}-inputEl">',
                '</div>',
            '</div>',
        '</tpl>',
        {
            disableFormats: true
        }
    ],

    getLabelableRenderData: function () {
        var me = this,
            labelAlign = me.labelAlign,
            mainLabelAlignedBefore = labelAlign === 'left';

        // Формируем свойство mainLabelAlignedBefore, определяющее положение лейбла
        // Дефолтный компонент умеет только выравнивать текст внутри лейбла, но не меняет его положение
        return Ext.apply(me.callParent(arguments), {
            mainLabelAlignedBefore: mainLabelAlignedBefore
        });
    }
});
