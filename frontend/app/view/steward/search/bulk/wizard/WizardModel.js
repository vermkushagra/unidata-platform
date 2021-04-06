/**
 * @author Aleksandr Bavin
 * @date 16.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.WizardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.wizard',

    data: {
        selectedCount: null,     // количество выбранных элементов
        nextStepAllowed: false   // доступен ли следующий шаг
    },

    formulas: {
        selectionLimit: function () {
            return Unidata.Config.getUserBulkSelectionLimit();
        },
        // можно ли запускать операцию
        canConfirm: {
            bind: {
                selectedCount: '{selectedCount}',
                selectionLimit: '{selectionLimit}'
            },
            get: function (data) {
                if (data.selectionLimit == 0) {
                    // если нет лимита, то можно запускать операцию
                    return true;
                } else {
                    // проверяем превышение лимита
                    return (data.selectedCount <= data.selectionLimit);
                }
            }
        },
        // заголовок для визарда
        selectionTitle: {
            bind: {
                selectedCount: '{selectedCount}',
                selectionLimit: '{selectionLimit}'
            },
            get: function (data) {
                var str = Unidata.i18n.t('search>wizard.selectionTitle', {count: data.selectedCount});

                if (data.selectionLimit != 0) {
                    str += ' (' + Unidata.i18n.t('search>wizard.maximum') + ' ' + data.selectionLimit + ')';
                }

                return str;
            }
        }
    }

});
