/**
 * @author Aleksandr Bavin
 * @date 2017-03-28
 */
Ext.define('Unidata.view.component.search.query.dataquality.DataQualitySearchModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.component.search.query.dataquality.dataqualitysearch',

    data: {
        disabledAll: true
    },

    stores: {
        dqNames: {
            fields: ['value'],
            proxy: 'dataquality.info.names'
        },
        dqCategories: {
            fields: ['value'],
            proxy: 'dataquality.info.categories'
        }
    },

    formulas: {
        ruleNameDisabled: {
            bind: {
                disabledAll: '{disabledAll}',
                errorsOnlyCheckboxValue: '{errorsOnlyCheckbox.value}',
                severityValue: '{severity.value}',
                categoryValue: '{category.value}'
            },
            get: function (data) {
                if (!data.errorsOnlyCheckboxValue) {
                    return true;
                }

                return (data.disabledAll || data.severityValue || data.categoryValue);
            }
        },
        otherFieldsDisabled: {
            bind: {
                disabledAll: '{disabledAll}',
                errorsOnlyCheckboxValue: '{errorsOnlyCheckbox.value}',
                ruleNameValue: '{ruleName.value}'
            },
            get: function (data) {
                if (!data.errorsOnlyCheckboxValue) {
                    return true;
                }

                return (data.disabledAll || data.ruleNameValue);
            }
        }
    }

});
