/**
 * Модель для контейнера, реализующего отображение связи типа reference
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-10
 */

Ext.define('Unidata.view.steward.relation.contains.ContainsModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.relation.contains',

    data: {
        anyDqErrors: false, // наличие dq ошибок хотя бы в одном из включений
        readOnly: null
    },

    formulas: {
        /**
         * Определяет видимость кнопки создания связи
         */
        createButtonVisible: {
            bind: {
                readOnly: '{readOnly}'
            },
            get: function (getter) {
                var visible = true;

                if (getter.readOnly) {
                    visible = false;
                }

                return visible;
            }
        }
    }

});
