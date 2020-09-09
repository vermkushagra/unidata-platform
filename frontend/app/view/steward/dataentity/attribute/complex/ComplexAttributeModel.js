Ext.define('Unidata.view.steward.dataentity.attribute.complex.ComplexAttributeModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataentity.attribute.complex.complexattribute',

    data: {
        deletable: true,
        deletableHidden: true,
        readOnly: true
    },

    stores: {},

    formulas: {
        /**
         * Определяет видимость кнопки удаления инстанса комплексного атрибута
         */
        deleteButtonEnabled: {
            bind: {
                deletable: '{deletable}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var enabled = true;

                if (getter.readOnly || !getter.deletable) {
                    enabled = false;
                }

                enabled = Ext.coalesceDefined(enabled, false);

                return enabled;
            }
        },

        /**
         * Определяет видимость кнопки удаления инстанса комплексного атрибута
         */
        deleteButtonVisible: {
            bind: {
                deletableHidden: '{deletableHidden}',
                deep: true
            },
            get: function (getter) {
                var visible = false;

                if (!getter.deletableHidden) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        }
    }
});
