/**
 * @author Ivan Marshalkin
 * @date 2018-04-18
 */

Ext.define('Ext.overrides.form.field.Picker', {
    override: 'Ext.form.field.Picker',

    expandOnFocus: false,

    initComponent: function () {
        this.callParent(arguments);

        this.on('focus', this.expandOnFocusHandler, this);
    },

    /**
     * Обработка расширения для комбобокса. Автораскрытие на фокус
     */
    expandOnFocusHandler: function () {
        if (this.expandOnFocus) {
            this.expand();
        }
    }
});
