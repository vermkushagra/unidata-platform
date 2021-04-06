/**
 * Миксин для выполнения валидации поисковых таблеток
 *
 * Применяется к поисковой таблетке
 * Для использования миксина в составе таблетки должны быть определены свойства
 *
 * eqValue
 * leftRange
 * rightRange
 * errorField
 *
 * @author Sergey Shishigin
 * @date 2017-11-30
 */
Ext.define('Unidata.mixin.search.SearchTabletValidatable', {

    extend: 'Ext.Mixin',

    eqValue: null,
    leftRange: null,
    rightRange: null,
    errorField: null,

    /**
     * Валидация инпутов поля данных
     *
     * Имеет побочный эффект - выводит сообщение об ошибках в
     * @returns {Boolean}
     */
    validate: function () {
        var componentMap,
            errorMsgs = [],
            errorHtml = '',
            errorField  = this.errorField,
            result,
            components;

        if (!this.eqValue || !this.leftRange || !this.rightRange) {
            throw new Error('Unidata.mixin.search.SearchTabletValidatable: не указаны компоненты');
        }

        componentMap = {
            eqValue: this.eqValue,
            leftRange: this.leftRange,
            rightRange: this.rightRange
        };

        components = Ext.Object.getValues(componentMap);
        result = Ext.Array.reduce(components, function (previous, item) {
            var errors,
                isValid;

            item.validate();    // для подсветки красным
            errors = item.getErrors();
            isValid = errors.length === 0;

            if (!isValid) {
                errorMsgs.push(errors.join(', '));
            }

            return previous && isValid;
        }, true);

        if (errorMsgs.length > 0) {
            errorHtml = errorMsgs[0];
        }

        errorField.setHtml(errorHtml);

        return result;
    }
});
