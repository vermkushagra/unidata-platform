/**
 * Переопределяем базовый класс для полей модели
 * При использовании bind в поля модели не записываются ошибочные значения (не проходящие валидацию).
 * Поэтому возникает ситуация, когда в модели по факту хранится одно значение, а в элементах интерфейса другое.
 *
 * UPD: Добавлено поле publishWithErrors, которое позволяет задавать значение модели даже если есть ошибки
 * @author Ivan Marshalkin
 * @date 2015-09-01
 */
Ext.define('Unidata.overrides.form.field.Base', {
    override: 'Ext.form.field.Base',

    publishWithErrors: true,

    publishValue: function () {
        var me = this;

        if (me.rendered && (this.publishWithErrors || !me.getErrors().length)) {
            me.publishState('value', me.getValue());
        }
    }
});
