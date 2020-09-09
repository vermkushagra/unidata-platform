/**
 *
 *
 * @author Ivan Marshalkin
 * @date 2016-10-26
 */

Ext.define('Unidata.view.steward.cluster.filter.ClusterFilterModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.cluster.filterview',

    data: {
        dataRecordLoading: false // для роутинга по etalonId
    },

    formulas: {
        /**
         * Индикация того, что нужные данные еще грузятся
         */
        dataLoading: {
            bind: {
                loading1: '{duplicateRuleCombo.dataLoading}',
                // loading2: '{duplicateGroupCombo.dataLoading}',
                loading3: '{dataRecordLoading}'
            },

            get: function (data) {
                var isLoading = false;

                Ext.Object.eachValue(data, function (value) {
                    if (value == true) {
                        isLoading = true;

                        return false;
                    }
                });

                return isLoading;
            }
        },

        /**
         * Доступен ли поиск
         */
        canSearch: {
            bind: {
                entityComboValue: '{entityCombo.value}',
                dataLoading: '{dataLoading}'
            },

            get: function (data) {
                if (data.dataLoading) {
                    return false;
                }

                if (data.entityComboValue) {
                    return true;
                }

                return false;
            }
        }
    }

});
