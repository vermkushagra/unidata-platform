Ext.define('Unidata.view.steward.dataentity.complex.abstraction.AbstractAttributeTabletModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataentity.complex.abstractattributetablet',

    data: {
        readOnly: false,
        hiddenAttribute: null,
        minComplexAttributeCount: null, // мин кол-во комплексных атрибутов
        maxComplexAttributeCount: null, // макс кол-во комплексных атрибутов
        hiddenComplexAttribute: null,   // флаг hidden из мета информации по атрибуту
        complexAttributeCount: null     // текущее кол-во комплексных атрибутов
    },

    stores: {},

    formulas: {
        // комплексный атрибут видим
        isComplexAttributeVisible: {
            bind: {
                hiddenComplexAttribute: '{hiddenComplexAttribute}',
                hiddenAttribute: '{hiddenAttribute}',
                deep: true
            },
            get: function (getter) {
                var visible = true;

                if (getter.hiddenComplexAttribute && !getter.hiddenAttribute) {
                    visible = false;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        // возвращает true если кнопка добавления инстанса комплексного атрибута активна, иначе False
        addComplexAttributeEnabled: {
            bind: {
                readOnly: '{readOnly}',
                max: '{maxComplexAttributeCount}',
                count: '{complexAttributeCount}',
                deep: true
            },
            get: function (getter) {
                var enabled = true;

                // если режиме readOnly операция не доступна в любом случае
                if (getter.readOnly) {
                    return false;
                }

                // max === null, когда сверху кол-во не ограничено
                if (getter.max !== null) {
                    enabled = getter.max > getter.count;
                }

                enabled = Ext.coalesceDefined(enabled, false);

                return enabled;
            }
        },

        // возвращает true если кнопка добавления инстанса комплексного атрибута видима, иначе False
        addComplexAttributeVisible: {
            bind: {
                readOnly: '{readOnly}',
                max: '{maxComplexAttributeCount}',
                count: '{complexAttributeCount}',
                deep: true
            },
            get: function (getter) {
                var visible = true;

                // если режиме readOnly операция не доступна в любом случае
                if (getter.readOnly) {
                    return false;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        // возвращает true если кнопки удаления инстанса комплексного атрибута активна, иначе False
        deleteComplexAttributeEnabled: {
            bind: {
                readOnly: '{readOnly}',
                min: '{minComplexAttributeCount}',
                count: '{complexAttributeCount}',
                deep: true
            },
            get: function (getter) {
                var enabled = true;

                // если режиме readOnly операция не доступна в любом случае
                if (getter.readOnly) {
                    return false;
                }

                if (getter.min) {
                    enabled = getter.min < getter.count;
                }

                enabled = Ext.coalesceDefined(enabled, false);

                return enabled;
            }
        },

        // возвращает true если кнопки удаления инстанса комплексного атрибута видимы, иначе False
        deleteComplexAttributeVisible: {
            bind: {
                readOnly: '{readOnly}',
                min: '{minComplexAttributeCount}',
                count: '{complexAttributeCount}',
                deep: true
            },
            get: function (getter) {
                var visible = true;

                // если режиме readOnly операция не доступна в любом случае
                if (getter.readOnly) {
                    return false;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        }
    }
});
