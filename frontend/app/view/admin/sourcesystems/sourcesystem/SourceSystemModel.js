Ext.define('Unidata.view.admin.sourcesystems.sourcesystem.SourceSystemModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.admin.sourcesystems.sourcesystem',

    data: {
        currentRecord: null,
        adminSystemName: null,
        readOnly: false
    },

    formulas: {
        isPhantom: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (record) {
                return record ? record.phantom : null;
            }
        },
        isAdminSourceSystem: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (record) {
                return record ? record.get('name') === this.get('adminSystemName') : null;
            }
        },
        tabName: {
            bind: {
                bindTo: '{currentRecord}',
                deep: true
            },
            get: function (record) {
                var tabName = Unidata.i18n.t('admin.sourcesystems>newSourceSystem');

                if (!record) {
                    return tabName;
                }

                if (!record.phantom) {
                    tabName = record.isModified('name') ? record.getModified('name') : record.get('name');
                }

                return tabName;
            }
        },
        saveButtonVisible: {
            bind: {
                isAdminSourceSystem: '{isAdminSourceSystem}',
                currentRecord: '{currentRecord}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var canCreate = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'create'),
                    canWrite  = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'update'),
                    isAdmin = this.get('isAdmin');

                if (getter.isAdminSourceSystem) {
                    return false;
                }

                if (getter.readOnly || !getter.currentRecord) {
                    return false;
                }

                if (isAdmin) {
                    canWrite = false;
                } else {
                    if (canCreate && getter.currentRecord.phantom) {
                        canWrite = true;
                    }
                }

                return canWrite;
            }
        },
        canEdit: {
            bind: {
                currentRecord: '{currentRecord}',
                draftMode: '{draftMode}',
                deep: true
            },
            get: function (getter) {
                var canCreate = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'create'),
                    canWrite  = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'update'),
                    canEdit = false,
                    currentRecord = getter.currentRecord,
                    draftMode = getter.draftMode;

                if (!draftMode) {
                    return false;
                }

                if (canWrite || (canCreate && currentRecord.phantom)) {
                    canEdit = true;
                }

                return canEdit;
            }
        },
        canEditId: {
            bind: {
                currentRecord: '{currentRecord}',
                deep: true,
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var canCreate = Unidata.Config.userHasRight('ADMIN_DATA_MANAGEMENT', 'create'),
                    currentRecord = getter.currentRecord,
                    draftMode = getter.draftMode,
                    isAdminSystemName;

                if (!currentRecord) {
                    return false;
                }

                if (!draftMode) {
                    return false;
                }

                isAdminSystemName = currentRecord.get('name') === this.get('adminSystemName');

                if (isAdminSystemName) {
                    return false;
                }

                if (canCreate && currentRecord.phantom) {
                    return true;
                }

                return false;
            }
        }
    }
});
