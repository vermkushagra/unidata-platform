Ext.define('Ext.overrides.data.field.Field', {
    override: 'Ext.data.field.Field',

    /**
     * Добаляет свойства в валидаторы для использования внутри метода validate
     * @param properties
     */
    addPropertiesToValidators: function (properties) {
        var validators,
            field = this;

        if (!field._validators) {
            field.compileValidators();
        }

        validators = field._validators;

        Ext.Array.each(validators, function (validator, index) {
            Ext.Object.each(properties, function (key, value) {
                field._validators[index][key] = value;
            });
        });
    }
});
