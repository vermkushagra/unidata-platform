/**
 * Field типа Enum на базе String
 *
 * @author: Sergey Shishigin
 * @date: 2018-04-18
 */
Ext.define('Unidata.field.Enum', {
    extend: 'Ext.data.field.String',

    alias: 'data.field.enum',
    enumList: null,

    constructor: function () {
        this.callParent(arguments);
        this.validators = {
            type: 'inclusion',
            list: Ext.Object.getKeys(this.enumList)
        };
    }
});
