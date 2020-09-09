/**
 * @author Aleksandr Bavin
 * @date 2016-07-08
 */
Ext.define('Unidata.view.admin.security.AdditionalPropertySettingsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.additionalpropertysettings',

    data: {},

    formulas: {
        canCreate: {
            bind: {
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var result = false,
                    canCreate = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'create');

                if (getter.readOnly) {
                    return false;
                }

                if (canCreate) {
                    result = true;
                }

                return Ext.coalesceDefined(result, false);
            }
        },

        canRead: function () {
            return Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'read');
        },

        canUpdate: {
            bind: {
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var result = false,
                    canUpdate = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'update');

                if (getter.readOnly) {
                    return false;
                }

                if (canUpdate) {
                    result = true;
                }

                return Ext.coalesceDefined(result, false);
            }
        },

        canDelete: {
            bind: {
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var result = false,
                    canDelete = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'delete');

                if (getter.readOnly) {
                    return false;
                }

                if (canDelete) {
                    result = true;
                }

                return Ext.coalesceDefined(result, false);
            }
        },

        readOnlyViewer:  {
            bind: {
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var result = true,
                    canRead = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'read'),
                    canEdit = Unidata.Config.userHasAnyRights('ADMIN_SYSTEM_MANAGEMENT', ['create', 'delete', 'update']);

                if (getter.readOnly) {
                    return true;
                }

                if (canRead && canEdit) {
                    result = false;
                }

                return Ext.coalesceDefined(result, true);
            }
        },

        showDeleteColumn: {
            bind: {
                readOnly: '{readOnly}',
                canDelete: '{canDelete}',
                canCreate: '{canCreate}',
                deep: true
            },
            get: function (getter) {
                var visible = false;

                if (getter.readOnly) {
                    return false;
                }

                if (getter.canDelete || getter.canCreate) {
                    visible = true;
                }

                return Ext.coalesceDefined(visible, false);
            }
        }
    }

});
