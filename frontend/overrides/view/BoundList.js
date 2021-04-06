/**
 * Оверрайд вьюшки комбобокса
 *
 * Пример как выключит кодирование html если это будет необходимо
 *
 * Ext.create('Ext.form.field.ComboBox', {
 *   listConfig: {
 *      enableHtmlEncode: false
 *   }
 * });
 *
 * @author Ivan Marshalkin
 * @date 2017-05-23
 */

Ext.define('Ext.overrides.view.BoundList', {
    override: 'Ext.view.BoundList',

    enableHtmlEncode: true, // флаг указывающий нужно кодировать html или нет

    // созвращает темплейт для списка в комбике
    getInnerTpl: function (displayField) {
        if (this.enableHtmlEncode) {
            return '{' + displayField + ':htmlEncode}';
        }

        // шаблон в том виде как он определен в библиотеке ExtJS
        return '{' + displayField + '}';
    }
});
