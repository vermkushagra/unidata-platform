/**
 * Переопределяем базовый класс для полей модели
 * При использовании bind в поля модели не записываются ошибочные значения (не проходящие валидацию).
 * Поэтому возникает ситуация, когда в модели по факту хранится одно значение, а в элементах интерфейса другое.
 *
 * @author Ivan Marshalkin
 * @date 2015-09-01
 */
Ext.define('Unidata.overrides.form.field.Base', {
    override: 'Ext.form.field.Base',

    publishValue: function () {
        var me = this;

        //закомментирована часть текста оригинальной версии
        if (me.rendered /*&& !me.getErrors().length*/) {
            me.publishState('value', me.getValue());
        }
    }
});
