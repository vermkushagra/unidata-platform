/**
 * Модель компонента для управления каталогом реестров/справочников
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.view.admin.entity.catalog.CatalogModel', {

    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.catalog',

    requires: [
        'Unidata.proxy.entity.CatalogProxy'
    ],

    data: {
        editAllowed: false,
        deleteAllowed: false,
        createAllowed: false,
        selection: false,
        dirtyMetaModelName: false,
        dirtyCatalog: false,
        hasErrors: false,
        draftMode: false
    },

    formulas: {
        creationAllowedInSelection: {
            bind: {
                createAllowed: '{createAllowed}',
                selection: '{selection}',
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                // создавать можно только в режиме черновика
                if (!getter.draftMode) {
                    return false;
                }

                return getter.selection && (getter.selection.getDepth() < 3) && getter.createAllowed;
            }
        },

        savingAllowed: {
            bind: {
                editAllowed:   '{editAllowed}',
                deleteAllowed: '{deleteAllowed}',
                createAllowed: '{createAllowed}'
            },
            get: function (getter) {
                return getter.editAllowed || getter.deleteAllowed || getter.createAllowed;
            }
        },

        savingBlocked: {
            bind: {
                dirtyCatalog: '{dirtyCatalog}',
                dirtyMetaModelName: '{dirtyMetaModelName}',
                hasErrors: '{hasErrors}',
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                // сохранять можно только в режиме черновика
                if (!getter.draftMode) {
                    return true;
                }

                if (!getter.dirtyCatalog && !getter.dirtyMetaModelName) {
                    return true;
                }

                return getter.hasErrors;
            }
        },

        metaModelEditAllowed: {
            bind: {
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                if (getter.draftMode) {
                    return true;
                }

                return false;
            }
        }

    },

    stores: {
        /**
         * Хранилище списка задач
         */
        catalogStore: {
            type: 'tree',
            model: 'Unidata.model.entity.Catalog',
            proxy: {
                type: 'un.entity.catalog',
                onlyCatalog: true,
                filterEmptyGroups: false,
                draftMode: false
            },
            root: {
                id: null
            }
        }
    }

});
