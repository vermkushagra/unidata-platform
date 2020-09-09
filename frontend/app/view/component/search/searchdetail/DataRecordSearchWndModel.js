/**
 *
 * @author Ivan Marshalkin
 * @date 2018-06-27
 */

Ext.define('Unidata.view.component.search.searchdetail.DataRecordSearchWndModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.search.datarecordsearch',

    data: {
        selectedSearchHit: null,
        status: 'READY',
        allowSelectedSearchHitToSelect: false
    },

    stores: {
    },

    formulas: {
        selectButtonEnabled: {
            bind: {
                selectedSearchHit: '{selectedSearchHit}',
                status: '{status}',
                allowSelectedSearchHitToSelect: '{allowSelectedSearchHitToSelect}',
                deep: true
            },
            get: function (getter) {
                var enabled = false;

                // выбор возможен только при статусе READY (нет загрузки данныех и т.д.)
                if (getter.status !== 'READY') {
                    return false;
                }

                // запись запрещено выбирать для подставновки т.к. она не соответствует критериям
                if (!getter.allowSelectedSearchHitToSelect) {
                    return false;
                }

                if (getter.selectedSearchHit) {
                    enabled = true;
                }

                enabled = Ext.coalesceDefined(enabled, false);

                return enabled;
            }
        },
        dataRecordDetailVisible: {
            bind: {
                selectedSearchHit: '{selectedSearchHit}',
                deep: true
            },
            get: function (getter) {
                var visible = false;

                if (getter.selectedSearchHit) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        }
    }
});
