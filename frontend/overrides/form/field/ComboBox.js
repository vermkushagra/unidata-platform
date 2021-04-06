/**
 * @author Ivan Marshalkin
 * @date 2015-10-09
 */
Ext.define('Ext.overrides.form.field.ComboBox', {
    override: 'Ext.form.field.ComboBox',

    //TODO: Необходимо убрать переопределение после перехода на новую версию ExtJS.
    //В IE 11 комбобоксы всегда раскрыты.
    //В версии ExtJS 5.1.1 данное поведение исправлено
    //see https://www.sencha.com/forum/showthread.php?298257-Combobox-always-expanded-in-IE11
    //see fiddle https://fiddle.sencha.com/#fiddle/if2
    checkChangeEvents: Ext.isIE ? ['change', 'propertychange', 'keyup'] : ['change', 'input', 'textInput', 'keyup', 'dragdrop'],

    initComponent: function () {
        this.callParent(arguments);

        // комбобоксам сенчи свойственно раскрываться если они readOnly
        // просто скрываем его если он ro
        // например баг UN-4846
        this.on('expand', function () {
            if (this.readOnly) {
                this.getPicker().hide();
            }
        }, this);
    }

    // onFocusLeave: function (e) {
    //     var me = this;
    //
    //     // do not collapse a picker if 'focusleave' event target is one of four pagingtoolbar buttons
    //     if (!Ext.Array.contains(['first', 'prev', 'next', 'last', 'refresh'], e.fromComponent.itemId)) {
    //         me.collapse();
    //         me.callParent([
    //             e
    //         ]);
    //     }
    // },
});
