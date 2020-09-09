/**
 * Переопределяем валидацию поля
 *
 * @author Ivan Marshalkin
 * @date 2018-08-24
 */

Ext.define('Unidata.overrides.form.field.Date', {
    override: 'Ext.form.field.Date',

    // отключаем валидацию на изменение т.к. в стандартном поведении поле
    // то "синее" то "красное" во время воода даты в ручну
    validateOnChange: false,

    enableKeyEvents: true,

    initComponent: function () {
        this.callParent(arguments);

        this.on('specialkey', this.onEnterKeyPress, this);
    },

    /**
     * По нажатию на ENTER производим валидацию (код аналогичен)
     *
     * @param field
     * @param event
     */
    onEnterKeyPress: function (field, event) {
        var me = this;

        if (event.getKey() === Ext.event.Event.ENTER) {
            me.validate();
        }
    },

    /**
     * @private
     * Called when the field's value changes. Performs validation if the {@link #validateOnChange}
     * config is enabled, and invokes the dirty check.
     */
    onChange: function (newVal) {
        var me = this;

        // запускаем принудительно валидацию если значение пусто
        if (me.validateOnChange || Ext.isEmpty(newVal)) {
            me.validate();
        }

        me.checkDirty();
    }
});
