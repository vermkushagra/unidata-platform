/**
 * Copyright 2011 Giovanni Candido da Silva <giovanni@giovannicandido.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * This Version works on ExtJS 4.x
 * Based on http://www.extjs.com.br/forum/index.php?PHPSESSID=3a88765ec1b311af7dc68c51e627a6a5&topic=4808.0
 * and http://www.sencha.com/forum/showthread.php?21040-InputTextMask-plugin-for-Textfield
 * The original author is unknown
 */

/**
 * InputTextMask script used for mask/regexp operations.
 * Mask Individual Character Usage:
 * 9 - designates only numeric values
 * L - designates only uppercase letter values
 * l - designates only lowercase letter values
 * A - designates only alphanumeric values
 * X - denotes that a custom client script regular expression is specified</li>
 * All other characters are assumed to be "special" characters used to mask the input component.
 * Example 1:
 * (999)999-9999 only numeric values can be entered where the the character
 * position value is 9. Parenthesis and dash are non-editable/mask characters.
 * Example 2:
 * 99L-ll-X[^A-C]X only numeric values for the first two characters,
 * uppercase values for the third character, lowercase letters for the
 * fifth/sixth characters, and the last character X[^A-C]X together counts
 * as the eighth character regular expression that would allow all characters
 * but "A", "B", and "C". Dashes outside the regular expression are non-editable/mask characters.
 * @constructor
 * @param (String) mask The InputTextMask
 * @param (boolean) clearWhenInvalid True to clear the mask when the field blurs and the text is invalid. Optional, default is true.
 */
Ext.define('Unidata.mixin.form.InputTextMask', {

    config: {
        mask: '',
        clearWhenInvalid: true
    },

    constructor: function (cfg) {
        this.initConfig(cfg);
    },

    updateMask: function (mask) {

        var me = this,
            mai = 0,
            regexp = '',
            i;

        me.rawMask = mask;
        me.viewMask = '';
        me.maskArray = [];

        for (i = 0; i < mask.length; i++) {

            if (regexp) {

                if (regexp == 'X') {
                    regexp = '';
                }

                if (mask.charAt(i) == 'X') {
                    me.maskArray[mai] = regexp;
                    mai++;
                    regexp = '';
                } else {
                    regexp += mask.charAt(i);
                }

            } else if (mask.charAt(i) == 'X') {
                regexp += 'X';
                me.viewMask += '_';
            } else if (mask.charAt(i) == '9' ||
                mask.charAt(i) == 'L' ||
                mask.charAt(i) == 'l' ||
                mask.charAt(i) == 'A'
            ) {
                me.viewMask += '_';
                me.maskArray[mai] = mask.charAt(i);
                mai++;
            } else {
                me.viewMask += mask.charAt(i);
                me.maskArray[mai] = RegExp.escape(mask.charAt(i));
                mai++;
            }

        }

        me.specialChars = me.viewMask.replace(/(L|l|9|A|_|X)/g, '');

    },

    init: function (field) {
        var originalIsValid = field.isValid;

        this.field = field;

        if (field.rendered) {
            this.assignEl();
        } else {
            field.on('render', this.assignEl, this);
        }

        field.on('blur',  this.removeValueWhenInvalid, this);
        field.on('focus', this.processMaskFocus,       this);

        // переопределяем метод валидации
        field.isValid = Ext.bind(function () {

            // валидация пустого значения
            if (!field.allowBlank && this.isEmpty()) {
                field.setActiveError(field.blankText);

                return false;
            }

            // валидация по маске
            if (!this.validateFieldMask()) {
                return false;
            }

            // родная валидация
            return originalIsValid.call(field);
        }, this);
    },

    assignEl: function () {

        var inputEl = this.field.inputEl,
            isGecko = Ext.isGecko,
            isOpera = Ext.isOpera;

        this.inputTextElement = inputEl.dom;

        inputEl.on('keypress', this.processKeyPress, this);
        inputEl.on('keydown',  this.processKeyDown,  this);

        inputEl.on('contextmenu',  function (e) {
            e.stopEvent();
        },  this);

        if (Ext.isSafari || Ext.isIE) {
            inputEl.on('paste', this.startTask, this);
            inputEl.on('cut', this.startTask, this);
        }

        if (isGecko || isOpera) {
            inputEl.on('mousedown', this.setPreviousValue, this);
        }

        if (isGecko) {
            inputEl.on('input', this.onInput, this);
        }

        if (isOpera) {
            inputEl.on('input', this.onInputOpera, this);
        }

    },

    onInput: function () {
        this.startTask(false);
    },

    onInputOpera: function () {
        if (!this.prevValueOpera) {
            this.startTask(false);
        } else {
            this.manageBackspaceAndDeleteOpera();
        }
    },

    manageBackspaceAndDeleteOpera: function () {
        this.inputTextElement.value = this.prevValueOpera.cursorPos.previousValue;
        this.manageTheText(this.prevValueOpera.keycode, this.prevValueOpera.cursorPos);
        this.prevValueOpera = null;
    },

    setPreviousValue: function () {
        this.oldCursorPos = this.getCursorPosition();
    },

    validateFieldMask: function () {
        var field = this.field,
            value = field.getValue(),
            i;

        // если нет введённых данных, то не проверяем соответствие маске
        if (this.isEmpty()) {
            return true;
        }

        for (i = 0; i < value.length; i++) {

            if (!this.getValidatedKey(value[i], i)) {
                field.setActiveError(Ext.String.format(Unidata.i18n.t('dataentity>inputTextMaskError'), this.rawMask));

                return false;
            }
        }

        field.unsetActiveError();

        return true;
    },

    isEmpty: function () {
        var value = this.field.getValue(),
            isEmpty = (value === this.viewMask);

        return isEmpty;
    },

    validateInputValue: function () {

        var maskArray = this.maskArray,
            maskItem,
            val = this.inputTextElement.value,
            maskChars = ['9', 'L', 'l', 'A'],
            i;

        for (i = 0; i < val.length; i++) {

            maskItem = maskArray[i];

            // _ - допустимый символ, если там место для символа маски
            if (val[i] === '_' && (maskChars.indexOf(maskItem) !== -1)) {
                continue;
            }

            if (!this.getValidatedKey(val[i], i)) {
                return false;
            }
        }

        return true;
    },

    getValidatedKey: function (chr, index) {
        var maskKey = this.maskArray[index];

        if (maskKey == '9') {
            return chr.match(/[0-9]/);
        } else if (maskKey == 'L') {
            return (chr.match(/[A-ZА-ЯЁa-zа-яё]/)) ? chr.toUpperCase() : null;
        } else if (maskKey == 'l') {
            return (chr.match(/[A-ZА-ЯЁa-zа-яё]/)) ? chr.toLowerCase() : null;
        } else if (maskKey == 'A') {
            return chr.match(/[A-ZА-ЯЁa-zа-яё0-9]/);
        } else if (maskKey) {
            return (chr.match(new RegExp(maskKey)));
        }

        return (null);
    },

    removeValueWhenInvalid: function () {
        if (this.clearWhenInvalid && this.inputTextElement.value.indexOf('_') > -1) {
            this.inputTextElement.value = '';
        }
        /**
         *  Corrige Bug https://projetos.atende.info/browse/EXTUX-1
         *  Reinicia as validacoes do campo, fazendo com que se o campo puder ser nulo, não vai ficar com marcação
         *  de inválido devido a mascara
         */

        this.field.isValid();
    },

    managePaste: function () {

        var valuePasted,
            inputTextElement = this.inputTextElement,
            charOk,
            i,
            oldCursorPos = this.oldCursorPos,
            keycode;

        if (this.field.readOnly) {
            return;
        }

        if (oldCursorPos == null) {
            return;
        }

        valuePasted = inputTextElement.value.substring(
            oldCursorPos.start,
            inputTextElement.value.length - (oldCursorPos.previousValue.length - oldCursorPos.end)
        );

        if (oldCursorPos.start < oldCursorPos.end) {

            oldCursorPos.previousValue =
                oldCursorPos.previousValue.substring(0, oldCursorPos.start) +
                this.viewMask.substring(oldCursorPos.start, oldCursorPos.end) +
                oldCursorPos.previousValue.substring(oldCursorPos.end, oldCursorPos.previousValue.length);

            valuePasted = valuePasted.substr(0, oldCursorPos.end - oldCursorPos.start);

        }

        inputTextElement.value = oldCursorPos.previousValue;

        keycode = {
            unicode: '',
            isShiftPressed: false,
            isTab: false,
            isBackspace: false,
            isLeftOrRightArrow: false,
            isDelete: false,
            pressedKey: ''
        };

        charOk = false;

        for (i = 0; i < valuePasted.length; i++) {
            keycode.pressedKey = valuePasted.substr(i, 1);
            keycode.unicode = valuePasted.charCodeAt(i);
            this.oldCursorPos = this.skipMaskCharacters(keycode, this.oldCursorPos);

            if (this.oldCursorPos === false) {
                break;
            }

            if (this.injectValue(keycode, this.oldCursorPos)) {
                charOk = true;
                this.moveCursorToPosition(keycode, this.oldCursorPos);
                this.oldCursorPos.previousValue = this.inputTextElement.value;
                this.oldCursorPos.start = this.oldCursorPos.start + 1;
            }
        }

        if (!charOk && this.oldCursorPos !== false) {
            this.moveCursorToPosition(null, this.oldCursorPos);
        }
        this.oldCursorPos = null;
    },

    processKeyDown: function (e) {
        this.processMaskFormatting(e, 'keydown');
    },

    processKeyPress: function (e) {
        this.processMaskFormatting(e, 'keypress');
    },

    startTask: function (setOldCursor) {
        if (this.field.readOnly) {
            return;
        }

        if (this.task == undefined) {
            this.task = new Ext.util.DelayedTask(this.managePaste, this);
        }

        if (setOldCursor !== false) {
            this.oldCursorPos = this.getCursorPosition();
        }
        this.task.delay(0);
    },

    /**
     * Пропускает спецсимволы из маски
     *
     * @param keycode
     * @param cursorPos
     * @returns {*}
     */
    skipMaskCharacters: function (keycode, cursorPos) {

        var specialChars = this.specialChars,
            viewMask = this.viewMask,
            nextPos;

        if (cursorPos.start != cursorPos.end && (keycode.isDelete || keycode.isBackspace)) {
            return (cursorPos);
        }

        while (specialChars.match(
            RegExp.escape(viewMask.charAt((nextPos = (keycode.isBackspace) ? cursorPos.start - 1 : cursorPos.start)))
        )) {

            // при проходе по символам не из маски заменяем их
            cursorPos.previousValue = cursorPos.previousValue.substring(0, nextPos) +
                viewMask.charAt(nextPos) +
                cursorPos.previousValue.substring(
                    nextPos + 1,
                    cursorPos.previousValue.length
                );

            if (keycode.isBackspace) {
                cursorPos.dec();
            } else {
                cursorPos.inc();
            }

            if (cursorPos.start >= cursorPos.previousValue.length || cursorPos.start < 0) {
                return false;
            }
        }

        return (cursorPos);
    },

    isManagedByKeyDown: function (keycode) {
        if (keycode.isDelete || keycode.isBackspace) {
            return (true);
        }

        return (false);
    },

    processMaskFormatting: function (e, type) {

        var cursorPos = this.getCursorPosition(),
            keycode = this.getKeyCode(e, type);

        if (this.field.readOnly) {
            return;
        }

        if (e.shiftKey && e.keyCode == Ext.event.Event.INSERT || /* shift+ins windows paste	*/
            e.shiftKey && e.keyCode == Ext.event.Event.DELETE || /* shift+del windows cut	*/
            e.ctrlKey && e.keyCode == Ext.event.Event.INSERT /* ctrl+ins windows copy*/) {
            // проблемы при вставке через insert поэтому запрещено через ctrl+c - ctrl+v все ок
            e.preventDefault();

            return;
        }

        this.oldCursorPos = null;

        if (keycode.unicode == 0) {//?? sometimes on Safari
            return;
        }

        if ((keycode.unicode == 67 || keycode.unicode == 99) && e.ctrlKey) {//Ctrl+c, let's the browser manage it!
            return;
        }

        if ((keycode.unicode == 88 || keycode.unicode == 120) && e.ctrlKey) {//Ctrl+x, manage paste
            this.startTask();

            return;
        }

        if ((keycode.unicode == 86 || keycode.unicode == 118) && e.ctrlKey) {//Ctrl+v, manage paste....
            this.startTask();

            return;
        }

        if ((keycode.isBackspace || keycode.isDelete) && Ext.isOpera) {

            this.prevValueOpera = {
                cursorPos: cursorPos,
                keycode: keycode
            };

            return;
        }

        if (type == 'keydown' && !this.isManagedByKeyDown(keycode)) {
            return true;
        }

        if (type == 'keypress' && this.isManagedByKeyDown(keycode)) {
            return true;
        }

        if (this.handleEventBubble(e, keycode, type)) {
            return true;
        }

        return (this.manageTheText(keycode, cursorPos));
    },

    manageTheText: function (keycode, cursorPos) {

        var input = this.inputTextElement,
            ln = input.value.length,
            isDelete = (keycode.isBackspace || keycode.isDelete),
            allRange = (cursorPos.start === 0 && cursorPos.end === ln && ln > 0);

        if (ln === 0 || allRange && isDelete || !this.validateInputValue()) {
            this.inputTextElement.value = this.viewMask;

            if (allRange) {
                cursorPos.end = 0;
                cursorPos.previousValue = this.viewMask;
            }
        }

        cursorPos = this.skipMaskCharacters(keycode, cursorPos);

        if (cursorPos === false) {
            return false;
        }

        if (this.injectValue(keycode, cursorPos)) {
            this.moveCursorToPosition(keycode, cursorPos);
        }

        return (false);
    },

    processMaskFocus: function () {

        var cursorPos;

        if (this.field.readOnly) {
            this.field.blur();

            return;
        }

        if (this.inputTextElement.value.length == 0) {

            cursorPos = this.getCursorPosition();

            this.inputTextElement.value = this.viewMask;
            this.moveCursorToPosition(null, cursorPos);

        }
    },

    isManagedByBrowser: function (keyEvent, keycode, type) {

        var EventObj = Ext.EventObject,
            unicode = keycode.unicode;

        if (((type == 'keypress' && keyEvent.charCode === 0) || type == 'keydown') && (
            unicode == EventObj.TAB ||
            unicode == EventObj.RETURN ||
            unicode == EventObj.ENTER ||
            unicode == EventObj.SHIFT ||
            unicode == EventObj.CONTROL ||
            unicode == EventObj.ESC ||
            unicode == EventObj.PAGEUP ||
            unicode == EventObj.PAGEDOWN ||
            unicode == EventObj.END ||
            unicode == EventObj.HOME ||
            unicode == EventObj.LEFT ||
            unicode == EventObj.UP ||
            unicode == EventObj.RIGHT ||
            unicode == EventObj.DOWN
            )) {
            return true;
        }

        return false;
    },

    handleEventBubble: function (keyEvent, keycode, type) {
        try {
            if (keycode && this.isManagedByBrowser(keyEvent, keycode, type)) {
                return true;
            }
            keyEvent.stopEvent();

            return false;
        } catch (e) {
            alert(e.message);
        }
    },

    getCursorPosition: function () {
        var s, e, r;

        // this.inputTextElement.createTextRange
        // см http://stackoverflow.com/a/19756936/1204043
        if (this.inputTextElement.createTextRange && document.selection) {
            r = document.selection.createRange().duplicate();
            r.moveEnd('character', this.inputTextElement.value.length);

            if (r.text === '') {
                s = this.inputTextElement.value.length;
            } else {
                s = this.inputTextElement.value.lastIndexOf(r.text);
            }
            r = document.selection.createRange().duplicate();
            r.moveStart('character', -this.inputTextElement.value.length);
            e = r.text.length;
        } else {
            s = this.inputTextElement.selectionStart;
            e = this.inputTextElement.selectionEnd;
        }

        return this.CursorPosition(s, e, r, this.inputTextElement.value);
    },

    moveCursorToPosition: function (keycode, cursorPosition) {
        var p = (!keycode || (keycode && keycode.isBackspace)) ? cursorPosition.start : cursorPosition.start + 1;

        // this.inputTextElement.createTextRange
        // см http://stackoverflow.com/a/19756936/1204043
        if (this.inputTextElement.createTextRange && document.selection) {
            cursorPosition.range.move('character', p);
            cursorPosition.range.select();
        } else {
            this.inputTextElement.selectionStart = p;
            this.inputTextElement.selectionEnd = p;
        }
    },

    injectValue: function (keycode, cursorPosition) {

        var key;

        if (!keycode.isDelete && keycode.unicode == cursorPosition.previousValue.charCodeAt(cursorPosition.start)) {
            return true;
        }

        if (!keycode.isDelete && !keycode.isBackspace) {
            key = this.getValidatedKey(keycode.pressedKey, cursorPosition.start);
        } else {
            if (cursorPosition.start == cursorPosition.end) {
                key = '_';

                if (keycode.isBackspace) {
                    cursorPosition.dec();
                }
            } else {
                key = this.viewMask.substring(cursorPosition.start, cursorPosition.end);
            }
        }

        if (key) {
            this.inputTextElement.value = cursorPosition.previousValue.substring(0, cursorPosition.start) +
                key +
                cursorPosition.previousValue.substring(
                    cursorPosition.start + key.length,
                    cursorPosition.previousValue.length
                );

            return true;
        }

        return false;
    },

    getKeyCode: function (onKeyDownEvent, type) {
        var keycode = {};

        keycode.unicode = onKeyDownEvent.getKey();
        keycode.isShiftPressed = onKeyDownEvent.shiftKey;

        keycode.isDelete = ((onKeyDownEvent.getKey() == Ext.EventObject.DELETE && type == 'keydown') || (type == 'keypress' && onKeyDownEvent.charCode === 0 && onKeyDownEvent.keyCode == Ext.EventObject.DELETE)) ? true : false;
        keycode.isTab = (onKeyDownEvent.getKey() == Ext.EventObject.TAB) ? true : false;
        keycode.isBackspace = (onKeyDownEvent.getKey() == Ext.EventObject.BACKSPACE) ? true : false;
        keycode.isLeftOrRightArrow = (onKeyDownEvent.getKey() == Ext.EventObject.LEFT || onKeyDownEvent.getKey() == Ext.EventObject.RIGHT) ? true : false;
        keycode.pressedKey = String.fromCharCode(keycode.unicode);

        return (keycode);
    },

    CursorPosition: function (start, end, range, previousValue) {
        var cursorPosition = {};

        cursorPosition.start = isNaN(start) ? 0 : start;
        cursorPosition.end = isNaN(end) ? 0 : end;
        cursorPosition.range = range;
        cursorPosition.previousValue = previousValue;
        cursorPosition.inc = function () {cursorPosition.start++;cursorPosition.end++;};
        cursorPosition.dec = function () {cursorPosition.start--;cursorPosition.end--;};

        return (cursorPosition);
    }
});

Ext.applyIf(RegExp, {
    escape: function (str) {
        return String(str).replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');
    }
});
