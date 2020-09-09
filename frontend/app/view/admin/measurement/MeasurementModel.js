/**
 * @author Ivan Marshalkin
 * @date 2016-11-08
 */

Ext.define('Unidata.view.admin.measurement.MeasurementModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.measurement',

    data: {
        checkedCount: 0,
        rootNodesCount: 0,
        draftMode: null
    },

    stores: {
        measurementTreeStore: {
            type: 'tree',
            fields: [
                {
                    name: 'text',
                    type: 'string'
                },
                {
                    name: 'record',
                    type: 'auto'
                }
            ],
            root: {
                id: null
            }
        },
        measurementValuesStore: {
            type: 'un.measurementvalues'
        }
    },

    formulas: {
        /**
         * Определяет доступность кнопки импорта
         */
        importButtonEnabled: {
            bind: {
                rootNodesCount: '{rootNodesCount}',
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                return getter && getter.draftMode;
            }
        },
        /**
         * Определяет видимость кнопки импорта
         */
        importButtonVisible: {
            bind: {
                deep: true
            },
            get: function () {
                var visible = false,
                    allowedByRights = false;

                if (Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'create') ||
                    Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'update')) {
                    allowedByRights = true;
                }

                if (allowedByRights) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },
        /**
         * Определяет доступность кнопки экспорта
         */
        exportButtonEnabled: {
            bind: {
                checkedCount: '{checkedCount}'
            },
            get: function (getter) {
                return getter && (getter.checkedCount > 0);
            }
        },
        /**
         * Определяет видимость кнопки экспорта
         */
        exportButtonVisible: {
            bind: {
                deep: true
            },
            get: function () {
                var visible = false,
                    allowedByRights = false;

                if (Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'read')) {
                    allowedByRights = true;
                }

                if (allowedByRights) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Определяет доступность кнопки удаления
         */
        deleteButtonEnabled: {
            bind: {
                checkedCount: '{checkedCount}',
                deep: true,
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var enabled = false;

                if (getter && getter.draftMode && getter.checkedCount > 0) {
                    enabled = true;
                }

                enabled = Ext.coalesceDefined(enabled, false);

                return enabled;
            }
        },/**
         * Определяет видимость кнопки удаления
         */
        deleteButtonVisible: {
            bind: {
                deep: true
            },
            get: function () {
                var visible = false,
                    allowedByRights = false;

                if (Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'delete')) {
                    allowedByRights = true;
                }

                if (allowedByRights) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },
        /**
         * Определяет доступность переключалки выбрать / снять все
         */
        toggleAllCheckBoxEnabled: {
            bind: {
                rootNodesCount: '{rootNodesCount}',
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var rootNodesCount = getter.rootNodesCount,
                    enabled;

                enabled = (rootNodesCount > 0);

                enabled = Ext.coalesceDefined(enabled, false);

                return enabled;
            }
        }
    }
});
