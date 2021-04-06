/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-05-14
 */

Ext.define('Unidata.view.steward.relation.m2m.M2mModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.relation.m2mrelation',

    data: {
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
