/**
 * Validate attributes CONCAT separator. Must be not empty
 *
 *
 * В валидатор не передается запись BUG: EXTJS-18997
 * https://www.sencha.com/forum/showthread.php?304946-Custom-data-field-validator-not-passing-in-record-just-the-value
 * Ошибка исправлена в Ext JS 5.1.2 release October 9, 2015
 * По всей видимости исправлено и в 6.0.2
 *
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.validator.GenerateStrategySeparatorValidator', {
    extend: 'Ext.data.validator.Validator',
    alias: 'data.validator.generatestrategyseparator',

    config: {
        message: 'Separator is required for a CONCAT type'
    },

    type: 'generatestrategyseparator',

    /**
     * record должен быть проставлен снаружи. см. напр. Ext.overrides.data.field.Field.addPropertiesToValidators
     */
    record: null,

    validate: function () {
        var GenerationStrategy = Unidata.model.entity.GenerationStrategy,
            record = this.record,
            result = true,
            type,
            separator;

        if (!record) {
            return this.getMessage();
        }

        type = record.get('@type');
        separator = record.get('separator');

        if (type === GenerationStrategy.generationStrategyType.CONCAT.value && !separator) {
            result = false;
        }

        return result ? true : this.getMessage();
    }
});

