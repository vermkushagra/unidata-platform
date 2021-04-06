Ext.define('Unidata.model.data.CodeAttribute', {
    extend: 'Unidata.model.data.AbstractAttribute',

    fields: [
        {
            name: 'value',
            type: 'auto'
        },
        {
            name: 'type',
            type: 'string',
            defaultValue: 'String'
        },
        {
            name: 'supplementary',
            type: 'auto'
        }
    ],

    /**
     * Проверяем есть дополнительные значения (supplementary)
     *
     * @returns {boolean}
     */
    isSupplementary: function () {
        var isSupplementary,
            supplementary;

        supplementary = this.get('supplementary');
        isSupplementary = Ext.isArray(supplementary) && supplementary.length > 0;

        return isSupplementary;
    }
});
