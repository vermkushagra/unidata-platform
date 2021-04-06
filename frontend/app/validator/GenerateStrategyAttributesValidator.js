/**
 * Validate strategy CONCAT separator. Must be not empty
 *
 * В валидатор не передается запись BUG: EXTJS-18997
 * https://www.sencha.com/forum/showthread.php?304946-Custom-data-field-validator-not-passing-in-record-just-the-value
 * Ошибка исправлена в Ext JS 5.1.2 release October 9, 2015
 * По всей видимости исправлено и в 6.0.2
 *
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.validator.GenerateStrategyAttributesValidator', {
    extend: 'Ext.data.validator.Validator',
    alias: 'data.validator.generatestrategyattributes',

    config: {
        message: 'Attributes is required for a CONCAT type'
    },

    type: 'generatestrategyattributes',

    /**
     * record должен быть проставлен снаружи. см. напр. Ext.overrides.data.field.Field.addPropertiesToValidators
     */
    record: null,

    validate: function () {
        var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
            record = this.record,
            result = true,
            type;

        if (!record) {
            return this.getMessage();
        }

        type = record.get('@type');
        attributes = record.get('attributes');

        if (type === GenerationStrategy.generationStrategyType.CONCAT.value && !(Ext.isArray(attributes) && attributes.length)) {
            result = false;
        }

        return result ? true : this.getMessage();
    }
});
