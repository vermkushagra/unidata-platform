/**
 *
 * @author Ivan Marshalkin
 * @date 2017-11-02
 */

Ext.define('Unidata.overrides.form.field.Text', {
    override: 'Ext.form.field.Text',

    preventLabelClick: false,

    onRender: function () {
        this.callParent(arguments);

        // по флагу отменяем дефолтное поведение на клик по лейблу
        if (this.preventLabelClick && this.labelEl) {
            this.labelEl.on('click', function (e) {
                e.stopEvent();
            }, this);
        }
    },

    // перенесено с версии extjs 5.1.1 устранение бага для полей numberfield не работали кнопка delete (см UN-6477)
    // EXTJS-16414 Delete button does not work in a numberfield when allowDecimals and allowExponential are both false
    filterKeys: function (e) {
        /*
         * Current only FF will fire keypress events for special keys.
         *
         * On European keyboards, the right alt key, Alt Gr, is used to type certain special characters.
         * JS detects a keypress of this as ctrlKey & altKey. As such, we check that alt isn't pressed
         * so we can still process these special characters.
         */
        if ((e.ctrlKey && !e.altKey) || e.isSpecialKey()) {
            return;
        }

        var charCode = String.fromCharCode(e.getCharCode());

        if (!this.maskRe.test(charCode)) {
            e.stopEvent();
        }
    }
});
