Ext.define('Unidata.view.admin.security.user.UserEditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.security.useredit',

    init: function () {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            currentUser = viewModel.get('currentUser'),
            arrLabels = [],
            userPropertyPanel = view.lookupReference('userPropertyPanel');

        currentUser.securityLabels().each(function (securityLabel) {
            var name = securityLabel.get('name');

            if (!arrLabels[name]) {
                arrLabels[name] = [];
            }

            arrLabels[name].push(securityLabel);
        });

        currentUser.properties().each(function (property) {
            userPropertyPanel.addPropertyItem(
                property.get('name'),
                property.get('displayName'),
                property.get('value'),
                property.get('id')
            );
        });

        viewModel.set('propertiesIsVisible', currentUser.properties().count());

        this.checkNewSecurityLabel();
    },

    onActivate: function () {
        var currentUser = this.getViewModel().get('currentUser');

        this.updateByUserRight(currentUser);
    },

    onSaveClick: function () {
        var me = this,
            view = this.getView(),
            form = this.lookupReference('userForm'),
            refRoles = form.lookupReference('userRoles'),
            userPropertyPanel = this.lookupReference('userPropertyPanel'),
            refPass = form.lookupReference('refPass'),
            confirmPass = form.lookupReference('confirmPass'),
            record,
            associatedData;

        record = this.getViewModel().get('currentUser');

        if (record && record.phantom && refPass.getValue() === '' && !record.get('external')) {
            refPass.markInvalid(Unidata.i18n.t('validation:passwordCantBeEmpty', {name: Unidata.i18n.t('common:password')}));
            confirmPass.markInvalid(Unidata.i18n.t('admin.security>confirmPaswordCantBeEmpty'));

            return;
        }

        if (form.isValid()) {
            record.properties().removeAll();
            record.properties().add(userPropertyPanel.getValues());

            record.setId(record.get('login'));

            if (record.isModified('login')) {
                record.setId(record.getModified('login'));
            }

            associatedData = record.getAssociatedData();

            record.set('password', refPass.getValue() || null);
            record.set('roles', refRoles.getValue());
            record.set('endpoints', associatedData['endpoints']);
            record.set('securityLabels', associatedData['securityLabels']);
            record.set('properties', associatedData['properties']);

            record.save({
                /**
                 * @param {Unidata.model.user.User} record
                 * @param {Ext.data.operation.Operation} operation
                 */
                success: function (record) {

                    var userStore = view.userGrid.getStore(),
                        userGrid = view.userGrid,
                        userGridTable = userGrid.getView(),
                        selectedRecord,
                        userStoreSource = userStore.data.getSource(),
                        currentUser = Unidata.Config.getUser();

                    selectedRecord = userStore.data.find('login', record.get('login'));

                    if (currentUser.get('login') === record.get('login')) {
                        // разлогиниваем пользователя если он редактировал себя
                        Ext.Function.defer(me.logoutUser, 0, this);

                        return;
                    }

                    if (!selectedRecord && userStoreSource) {
                        // ищем в source, т.к. store отфильтрован гридом
                        selectedRecord = userStore.data.getSource().find('login', record.get('login'));
                    }

                    if (selectedRecord) {
                        // обновляем record в списке пользователей
                        selectedRecord.set(
                            {
                                active:    record.get('active'),
                                login:     record.get('login'),
                                firstName: record.get('firstName'),
                                lastName:  record.get('lastName')
                            },
                            {
                                commit: true
                            }
                        );

                        // обновляем прозрачность строки (активна/не активна)
                        userGridTable.removeRowCls(selectedRecord, 'opacity_5');

                        if (!selectedRecord.get('active')) {
                            userGridTable.addRowCls(selectedRecord, 'opacity_5');
                        }
                    }

                    me.showMessage(Unidata.i18n.t('admin.security>userSaveSuccess'));
                }
            });
            this.onCancelClick();
        }
    },

    logoutUser: function () {
        var promise;

        Unidata.util.Router
            .suspendTokenEvents()
            .removeTokens()
            .resumeTokenEvents();

        promise = Unidata.util.api.Authenticate.logout();

        promise
            .then(function () {
                var application = Unidata.getApplication();

                application.fireEvent('deauthenticate');

                application.showViewPort('login');
            })
            .otherwise(function () {
            })
            .done();
    },

    onCancelClick: function () {
        this.dialog = Ext.destroy(this.dialog);
    },

    updateByUserRight: function (currentUser) {
        var canCreate = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'create'),
            canWrite  = Unidata.Config.userHasRight('ADMIN_SYSTEM_MANAGEMENT', 'update'),
            view = this.getView(),
            saveButton = view.lookupReference('saveButton');

        if (canCreate && currentUser.phantom) {
            canWrite = true;
        }

        view.setReadOnly(!canWrite);
        saveButton.setDisabled(!canWrite);
        saveButton.setHidden(!canWrite);
    },

    createSecurityAttribute: function (container, securityLabelsGroup, securityLabels, securityLabelSample, readOnly) {
        var component;

        component = Ext.create('Unidata.view.component.user.SecurityAttribute', {
            securityLabelSample: securityLabelSample,
            securityLabelsGroup: securityLabelsGroup,
            securityLabels: securityLabels,
            readOnly: Boolean(readOnly)
        });
        container.add(component);

        return component;
    },

    checkNewSecurityLabel: function () {
        var me = this,
            viewModel = this.getViewModel(),
            currentUser = viewModel.get('currentUser'),
            roleIds = currentUser.get('roles'),
            roles = [],
            recordCount = 0,
            loadedCount = 0;

        if (roleIds) {
            recordCount = roleIds.length;
        }

        function onRoleLoad () {
            loadedCount++;

            if (loadedCount === recordCount) {
                this.onRolesLoadCheckNewSecurityLabel(roles);
            }
        }

        Ext.Array.each(roleIds, function (roleId) {
            var role = Ext.create('Unidata.model.user.Role');

            role.setId(roleId);
            role.load({
                success: onRoleLoad,
                scope: me
            });

            roles.push(role);
        });
    },

    /**
     * Возвращает стор со со всеми метками, когда он загрузится
     *
     * @returns {Ext.promise.Promise}
     */
    getAllSecurityLabelsStore: function () {
        var view = this.getView(),
            deferred = new Ext.Deferred(),
            allSlStore = this.getStore('securityLabels');

        if (allSlStore.isLoaded()) {
            deferred.resolve(allSlStore);
        } else {
            allSlStore.on('load', function (store, records, successful) {
                if (view.isDestroyed) {
                    return;
                }

                if (successful) {
                    deferred.resolve(store);
                } else {
                    throw new Error('Ошибка при загрузке всех меток безопасности');
                }
            });
        }

        return deferred.promise;
    },

    onRolesLoadCheckNewSecurityLabel: function (roles) {
        this.updateSecurityLabelsPanel(roles);
    },

    onEndpointsChange: function (combo) {
        var viewModel = this.getViewModel(),
            currentUser = viewModel.get('currentUser'),
            endpointsList = this.getStore('endpoints'),
            endpoints = currentUser.endpoints(),
            endpointNames = combo.getValue();

        endpoints.removeAll();

        endpointsList.addFilter({
            property: 'name',
            operator: 'in',
            value: endpointNames
        });

        endpoints.add(endpointsList.getRange());

        endpointsList.clearFilter();
    },

    onRolesChange: function (combo) {
        var me = this,
            records = combo.getValueRecords(),
            recordCount = records.length,
            loadedCount = 0;

        function onRoleLoad () {
            loadedCount++;

            if (loadedCount === recordCount) {
                this.updateSecurityLabelsPanel(records);
            }
        }

        if (recordCount) {
            Ext.Array.each(records, function (record) {
                record.setId(record.get('name'));
                record.load({
                    success: onRoleLoad,
                    scope: me
                });
            });
        } else {
            this.updateSecurityLabelsPanel(records);
        }
    },

    /**
     * Обновляет все панели меток безопасности
     *
     * @param [roles] - опционально
     */
    updateSecurityLabelsPanel: function (roles) {
        var me = this,
            userRoles = roles || [],
            view = this.getView(),
            viewModel = this.getViewModel(),
            currentUser = viewModel.get('currentUser'),
            currentUserSecurityLabels = currentUser.securityLabels(),
            securityLabelPanelUserContainer = view.lookupReference('securityLabelPanelUser'),
            securityLabelPanelRoleContainer = view.lookupReference('securityLabelPanelRole'),
            roleLabelsGroupByName = {},
            userLabelsGroupByName = {};

        securityLabelPanelRoleContainer.removeAll();
        securityLabelPanelUserContainer.removeAll();

        // собираем метки для ролей по имени
        Ext.Array.each(userRoles, function (role) {

            // группируем метки по имени
            role.securityLabels().each(function (securityLabel) {
                var slName = securityLabel.get('name');

                if (!roleLabelsGroupByName[slName]) {
                    roleLabelsGroupByName[slName] = [];
                    userLabelsGroupByName[slName] = [];
                }

                // нас интересуют только метки с атрибутами,
                // т.к. метки без атрибутов служат для индикации того, что метка отмечена на роли
                if (securityLabel.attributes().getCount()) {
                    roleLabelsGroupByName[slName].push(securityLabel);
                }
            });
        });

        // после загрузки всех меток, которые нужны для образцов - отрисовываем панели
        me.getAllSecurityLabelsStore().then(function (allSlStore) {

            // отображаем метки, которые пришли от ролей
            Ext.Object.each(roleLabelsGroupByName, function (securityLabelName, securityLabels) {
                var securityLabelSample = allSlStore.findRecord(
                    'name', securityLabelName, 0, false, false, true
                );

                if (securityLabels.length) {
                    me.createSecurityAttribute(
                        securityLabelPanelRoleContainer, securityLabels, currentUserSecurityLabels, securityLabelSample, true
                    );
                }
            });

            // собираем метки пользователя по имени
            currentUserSecurityLabels.each(function (securityLabel) {
                var slName = securityLabel.get('name');

                if (!userLabelsGroupByName[slName]) {
                    userLabelsGroupByName[slName] = [];
                }

                userLabelsGroupByName[slName].push(securityLabel);
            });

            // отображаем метки пользователя
            Ext.Object.each(userLabelsGroupByName, function (securityLabelName, securityLabels) {
                var securityLabelSample = allSlStore.findRecord(
                    'name', securityLabelName, 0, false, false, true
                );

                if (securityLabelSample) {
                    me.createSecurityAttribute(
                        securityLabelPanelUserContainer, securityLabels, currentUserSecurityLabels, securityLabelSample
                    );
                } else {
                    me.createSecurityAttribute(
                        securityLabelPanelUserContainer, securityLabels, currentUserSecurityLabels
                    );
                }
            });

        }).done();
    },

    setReadOnly: function (readOnly) {
        var viewModel;

        viewModel = this.getViewModel();

        viewModel.set('readOnly', readOnly);

        if (readOnly) {
            viewModel.set('activeReadOnly', readOnly);
        }
    }
});
