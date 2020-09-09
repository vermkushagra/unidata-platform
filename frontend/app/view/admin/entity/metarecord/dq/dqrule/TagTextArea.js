/**
 * Компонент с отображением тегов
 *
 * @date 2018-02-15
 * @author Ivan Marshalkin
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.TagTextArea', {
    extend: 'Ext.form.field.Base',

    xtype: 'un.tagtextarea',

    mode: {
        EDIT: 'EDIT',
        PREVIEW: 'PREVIEW'
    },
    fieldMode: null,

    referenceHolder: true,

    childEls: [
        'containerHolder'
    ],

    fieldSubTpl: [
        '<div id="{cmpId}-containerHolder" data-ref="containerHolder" role="containerHolder" style="height: 100%;"',
        '></div>'
    ],

    combineErrors: true,

    msgTarget: 'under',

    readOnly: false,
    allowBlank: true,

    componentContainer: null,

    editorField: null,
    previewField: null,

    minHeight: 60,

    focusCls: 'un-tagtextarea-focus',

    initComponent: function () {
        var me = this,
            cls = 'un-tagtextarea';

        if (!me.cls) {
            me.cls = [];
        } else if (Ext.isString(me.cls)) {
            me.cls = [me.cls];
        }

        me.cls.push(cls);

        me.buildField();
        me.setFieldMode(me.mode.PREVIEW);

        me.callParent(arguments);
    },

    /**
     * Устанавливает режим работы поля
     *
     * @param mode
     */
    setFieldMode: function (mode) {
        var me = this,
            caretPos = 0,
            scrollTop = 0;

        if (me.fieldMode === mode) {
            return;
        }

        me.fieldMode = mode;

        if (mode === me.mode.EDIT) {
            if (me.previewField.rendered) {
                caretPos = me.getCaretCharacterOffsetWithin(me.previewField.el.dom);
                scrollTop = me.previewField.el.getScrollTop();
            }

            me.previewField.hide();
            me.editorField.show();

            if (me.editorField.rendered) {
                me.editorField.focus();
                me.editorField.selectText(caretPos, caretPos);

                me.editorField.inputEl.setScrollTop(scrollTop);
            }

        } else if (mode === me.mode.PREVIEW) {
            if (me.editorField.rendered) {
                scrollTop = me.editorField.inputEl.getScrollTop();
            }

            me.previewField.show();
            me.editorField.hide();

            if (me.previewField.rendered) {
                me.previewField.el.setScrollTop(scrollTop);
            }
        }
    },

    buildField: function () {
        var me = this;

        me.editorField = Ext.create({
            xtype: 'textarea',
            cls: 'un-editor-field',
            value: 'editorField',
            margin: 0,
            padding: 0,
            flex: 1,
            listeners: {
                scope: this,
                focus: this.onEditorFocus,
                blur: function () {
                    me.setFieldMode(me.mode.PREVIEW);
                    me.onEditorBlur();
                },
                change: function (cmp, value) {
                    me.editing = true;

                    me.setValue(value);
                    me.editing = false;
                }
            }
        });

        me.previewField = Ext.create({
            xtype: 'container',
            cls: 'un-preview-field',
            scrollable: true,
            flex: 1,
            listeners: {
                el: {
                    click: function () {
                        if (me.isDisabled() || me.readOnly) {
                            return;
                        }

                        me.setFieldMode(me.mode.EDIT);
                    }
                }
            }
        });

        me.componentContainer = Ext.create('Ext.container.Container', {
            referenceHolder: true,
            cls: 'un-container-layout',
            height: '100%',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            minHeight: me.minHeight,
            scrollable: true,
            items: [
                me.editorField,
                me.previewField
            ]
        });
    },

    onEditorFocus: function () {
        this.addCls(this.focusCls);
    },

    onEditorBlur: function () {
        this.removeCls(this.focusCls);
    },

    getCaretCharacterOffsetWithin: function (element) {
        var caretOffset = 0,
            doc = element.ownerDocument || element.document,
            win = doc.defaultView || doc.parentWindow,
            sel,
            range,
            preCaretRange,
            textRange,
            preCaretTextRange;

        if (typeof win.getSelection != 'undefined') {
            sel = win.getSelection();

            if (sel.rangeCount > 0) {
                range = win.getSelection().getRangeAt(0);
                preCaretRange = range.cloneRange();

                preCaretRange.selectNodeContents(element);
                preCaretRange.setEnd(range.endContainer, range.endOffset);
                caretOffset = preCaretRange.toString().length;
            }
        } else if ((sel = doc.selection) && sel.type != 'Control') {
            textRange = sel.createRange();
            preCaretTextRange = doc.body.createTextRange();

            preCaretTextRange.moveToElementText(element);
            preCaretTextRange.setEndPoint('EndToEnd', textRange);
            caretOffset = preCaretTextRange.text.length;
        }

        return caretOffset;
    },

    getCaretPosition: function () {
        var range,
            selectedObj,
            rangeCount,
            childNodes,
            i;

        if (window.getSelection && window.getSelection().getRangeAt) {
            range = window.getSelection().getRangeAt(0);
            selectedObj = window.getSelection();
            rangeCount = 0;
            childNodes = selectedObj.anchorNode.parentNode.childNodes;

            for (i = 0; i < childNodes.length; i++) {
                if (childNodes[i] == selectedObj.anchorNode) {
                    break;
                }

                if (childNodes[i].outerHTML) {
                    rangeCount += childNodes[i].outerHTML.length;
                } else if (childNodes[i].nodeType == 3) {
                    rangeCount += childNodes[i].textContent.length;
                }
            }

            return range.startOffset + rangeCount;
        }

        return -1;
    },

    setReadOnly: function (readOnly) {
        this.callParent(arguments);

        if (readOnly && this.fieldMode === this.mode.EDIT) {
            this.setFieldMode(this.mode.PREVIEW);
        }

        this.editorField.setReadOnly(readOnly);
    },

    setValue: function (value) {
        var me = this;

        me.callParent(arguments);

        if (!me.editing) {
            me.editorField.setValue(value);
        }

        me.previewField.setHtml(me.transformValueToDisplayValue(value));
    },

    transformValueToDisplayValue: function (value) {
        var reqExp,
            result;

        result = Ext.String.htmlEncode(String(value));

        reqExp = /(#[^\s#$]+)(?=[\s#$]|$)/gi;
        result = result.replace(reqExp, '<span style="color: #507fba;">$1</span>');

        // заменяем переносы строк
        reqExp = /\n|\r\n|\r/gi;
        result = result.replace(reqExp, '<br>');

        return result;
    },

    setSize: function (width, height) {
        this.componentContainer.setWidth(width);
        this.componentContainer.setHeight(height);

        this.updateLayoutComponentContainer();
    },

    getWriteFormat: function () {
        return this.writeFormat;
    },

    setDisabled: function () {
        this.callParent(arguments);
    },

    onRender: function () {
        this.callParent(arguments);

        this.componentContainer.render(this.containerHolder);
    },

    onResize: function () {
        this.callParent(arguments);

        this.updateLayoutComponentContainer();
    },

    updateLayoutComponentContainer: function () {
        if (this.componentContainer.rendered) {
            this.componentContainer.updateLayout();
        }
    },

    updateLayout: function () {
        this.callParent(arguments);

        this.updateLayoutComponentContainer();
    }
});
