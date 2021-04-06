Ext.define('Unidata.view.admin.cleanseFunction.CleanseFunctionModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.cleanseFunction',

    data: {
        currentRecord: null,
        currentRecordPath: null,
        currentRecordIcon: null,
        currentRecordDeletable: false,
        draftMode: null
    },
    stores: {
        cleanseGroupsStore: {
            model: 'cleansefunction.Group',
            autoLoad: false,
            // при изменениях не забывать что это дублируется в двух местах
            // Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunctionModel
            // и
            // Unidata.view.admin.cleanseFunction.CleanseFunctionModel
            proxy: {
                type: 'rest',
                url: Unidata.Config.getMainUrl() + 'internal/meta/cleanse-functions',
                reader: {
                    type: 'json',
                    transform: function (data) {
                        // сортируем функции по алфавиту

                        Ext.Array.each(data.groups, function (group) {
                            group.functions = Ext.Array.sort(group.functions, function (f1, f2) {
                                var name1 = f1.name.toLowerCase(),
                                    name2 = f2.name.toLowerCase();

                                if (name1 === name2) {
                                    return 0;
                                }

                                return (name1 > name2) ? 1 : -1;
                            });

                            Ext.Array.each(group.functions, function (fn, index) {
                                fn.order = index + 1;
                            });
                        });

                        return data;
                    }
                }
            }
        }
    },
    formulas: {
        cleanseGroups: {
            bind: {
                bindTo: '{cleanseGroupsStore}',
                deep: true
            },
            get: function (record) {
                return record && record.first();
            }
        },
        canEdit: {
            bind: {
                currentRecord: '{currentRecord}',
                draftMode: '{draftMode}',
                deep: true
            },
            get: function (getter) {
                var canEdit = false,
                    canWrite = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'update'),
                    record = getter.currentRecord,
                    draftMode = getter.draftMode;

                if (record && record.get('type') === 'COMPOSITE_FUNCTION' && canWrite && draftMode) {
                    canEdit = true;
                }

                return Ext.coalesceDefined(canEdit, false);
            }
        },
        canDelete: {
            bind: {
                currentRecord: '{currentRecord}',
                draftMode: '{draftMode}',
                currentRecordDeletable: '{currentRecordDeletable}',
                deep: true
            },
            get: function (getter) {
                var canDeleteCF = false,
                    canDelete = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'delete'),
                    currentRecordDeletable = getter.currentRecordDeletable,
                    record = getter.currentRecord,
                    draftMode = getter.draftMode;

                if (record && currentRecordDeletable && canDelete && draftMode) {
                    canDeleteCF = true;
                }

                return Ext.coalesceDefined(canDeleteCF, false);
            }
        },
        createCleanseFunctionVisible: {
            bind: {
                draftMode: '{draftMode}',
                deep: true
            },
            get: function (getter) {
                var result = false,
                    canCreate = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'create'),
                    draftMode = getter.draftMode;

                if (canCreate && draftMode) {
                    result = true;
                }

                return Ext.coalesceDefined(result, false);
            }
        }
    }
});
