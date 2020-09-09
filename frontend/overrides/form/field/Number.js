/**
 *
 * @author Ivan Marshalkin
 * @date 2018-04-16
 */

Ext.define('Unidata.overrides.form.field.Number', {
    override: 'Ext.form.field.Number',

    // запрещаем вставлять в поля вода всякую ерунду из буфера обмена
    stripCharsRe: /[^0-9.,\-]/g
});
