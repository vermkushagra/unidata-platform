/**
 * @author Sergey Shishigin
 * @date 2017-01-20
 */
Ext.define('Unidata.view.steward.dataentity.attribute.CodeArrayAttribute', {
    extend: 'Unidata.view.steward.dataentity.attribute.ArrayAttribute',

    statics: {
        TYPE: 'Array'
    },

    /**
     * @override
     */
    getSubmitValue: function () {
        var value = this.getValue(),
            submitValue = value;

        if (Ext.isArray(value) && value.length > 0) {
            submitValue = value[0];
        }

        return submitValue;
    },

    /**
     * @override
     * @param dataAttribute
     */
    getInnerValue: function (dataAttribute) {
        var innerValue = [];

        innerValue.push(dataAttribute.get('value'));
        innerValue = Ext.Array.push(innerValue, dataAttribute.get('supplementary'));
        innerValue = Ext.Array.map(innerValue, function (value) {
            return {
                value: value
            };
        });

        return innerValue;
    }
});
