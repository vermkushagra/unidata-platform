Ext.define('Unidata.view.admin.security.user.UserController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.security.user',

    // TODO: Необходимо рефакторинг - вынесение общего функционала для экрана пользователей/ролей (напр: custom props)

    statics: {
        // фаза вызова обработчика
        tabChangePhase: {
            BEFORE_ADD_OR_REMOVE_TAB: 'BEFORE_ADD_OR_REMOVE_TAB',   // до удаления/добавления таба
            AFTER_ADD_OR_REMOVE_TAB: 'AFTER_ADD_OR_REMOVE_TAB'      // после удаления/добавления таба
        }
    },

    onUserPropertiesSettingsClick: function () {
        var tabPanel = this.lookupReference('userTabPanel'),
            settingsOpened = false,
            countTab,
            propertyEditorReadOnly,
            propertySettings;

        countTab = tabPanel.items.getCount();

        // редактирование допустимо только если нет открытых карточек ролей
        propertyEditorReadOnly = (countTab > 0) ? true : false;

        tabPanel.items.each(function (item) {
            if (item instanceof Unidata.view.admin.security.user.UserAdditionalPropertySettings) {
                settingsOpened = true;
                propertySettings = item;
            }
        });

        if (!settingsOpened) {
            propertySettings = Ext.create('Unidata.view.admin.security.user.UserAdditionalPropertySettings', {
                readOnly: propertyEditorReadOnly
            });

            tabPanel.insert(0, propertySettings);
        } else {
            tabPanel.setActiveTab(propertySettings);
        }
    },

    displayUserTab: function (record) {
        var TabChangePhase = Unidata.view.admin.security.user.UserController.tabChangePhase,
            tabPanel = this.lookupReference('userTabPanel'),
            usersStore = this.getViewModel().getStore('users'),
            userGrid = this.lookupReference('userGrid'),
            theUser = record || new Unidata.model.user.User(),
            tabComponent,
            insertRecord,
            viewModel = this.getViewModel(),
            storeUserProperties = viewModel.getStore('additionalProperties');

        tabComponent = tabPanel.items.findBy(function (item) {
            var viewModel = item.getViewModel(),
                user = viewModel ? viewModel.get('currentUser') : null;

            return user !== null && user.get('login') === theUser.get('login');
        });

        if (theUser.phantom) {
            theUser.properties().removeAll();

            storeUserProperties.each(function (record) {
                theUser.properties().add(record.getData());
            });
        }

        if (!tabComponent) {
            tabComponent = Ext.create('Unidata.view.admin.security.user.UserEdit',
                {
                    userGrid: userGrid,
                    viewModel: {
                        data: {
                            title: record ? Unidata.i18n.t('admin.metamodel>edit') : Unidata.i18n.t('admin.security>newUser'),
                            loginReadOnly: record ? true : false,
                            activeReadOnly: Unidata.Config.getUser().get('login') === theUser.get('login'),
                            currentUser: theUser
                        },
                        formulas: {
                        },
                        stores: {
                            authSources: viewModel.get('authSources'),
                            endpoints: {
                                model: 'Unidata.model.user.UserEndpoint',
                                autoLoad: true
                            },
                            roles: {
                                model: 'Unidata.model.user.Role',
                                autoLoad: true
                            }
                        }
                    }
                }
            );
            tabComponent.on('close', this.onCloseTab, this, {single: true});
            tabPanel.add(tabComponent);
        }

        if (theUser.phantom) {
            insertRecord = usersStore.insert(0, theUser);

            userGrid.suspendEvents(false);
            userGrid.getSelectionModel().select(insertRecord);
            userGrid.resumeEvents();
        }

        this.updatePropertySettingsTabReadOnly(TabChangePhase.AFTER_ADD_OR_REMOVE_TAB);

        tabPanel.setActiveTab(tabComponent);
    },

    onCloseTab: function (tab) {
        var TabChangePhase = Unidata.view.admin.security.role.RoleController.tabChangePhase,
            viewModel = tab.getViewModel(),
            currentUser = viewModel.get('currentUser'),
            userGrid = this.lookupReference('userGrid'),
            selectionModel = userGrid.getSelectionModel();

        if (selectionModel.getCount()) {
            if (currentUser.phantom) {
                userGrid.getStore().remove(selectionModel.getSelection());
            } else {
                selectionModel.deselectAll();
            }
        }

        this.updatePropertySettingsTabReadOnly(TabChangePhase.BEFORE_ADD_OR_REMOVE_TAB);
    },

    onAddUserButtonClick: function () {
        this.displayUserTab();
    },

    onSelectUser: function (grid, currentUser) {
        var userNew = new Unidata.model.user.User();

        function onCurrentUserLoad () {
            this.displayUserTab(userNew);
        }

        if (currentUser.phantom) {
            this.displayUserTab(currentUser);

            return;
        }

        userNew.setId(currentUser.get('login'));

        if (currentUser.isModified('login')) {
            userNew.setId(currentUser.getModified('login'));
        }

        userNew.load({
            success: onCurrentUserLoad,
            scope: this
        });
    },

    onActiveFilterSelect: function () {
        var combo,
            filterType,
            filter,
            filterValue,
            grid,
            store;

        combo = this.lookupReference('comboActiveFilter');
        filterType = combo.getValue();
        grid = this.lookupReference('userGrid');
        store = grid.getStore();

        store.clearFilter();

        if (filterType === 'all') {
            return;
        } else {
            filterValue = (filterType === 'active' ? true : false);
        }

        filter = new Ext.util.Filter({
            filterFn: function (item) {
                return item.get('active') === filterValue;
            }
        });

        store.getFilters().add(filter);
    },

    /**
     * Обновить свойство "Только для чтения" для табки настройки custom properties
     *
     * @param tabChangePhase Фаза вызова метода
     */
    updatePropertySettingsTabReadOnly: function (tabChangePhase) {
        var tabPanel = this.lookupReference('userTabPanel'),
            openedPropertySettingsTab = this.getOpenedPropertySettingsTab(),
            TabChangePhase = Unidata.view.admin.security.role.RoleController.tabChangePhase,
            countTab,
            readOnly;

        countTab = tabPanel.items.getCount();

        if (!openedPropertySettingsTab) {
            return;
        }

        switch (tabChangePhase) {
            case TabChangePhase.AFTER_ADD_OR_REMOVE_TAB:
                readOnly = countTab > 1;
                break;
            case TabChangePhase.BEFORE_ADD_OR_REMOVE_TAB:
                readOnly = countTab > 2;
                break;
        }

        openedPropertySettingsTab.setReadOnly(readOnly);
    },

    getOpenedPropertySettingsTab: function () {
        var tabs = this.getOpenedTabs(),
            propertySettingsTab = null;

        Ext.Array.each(tabs, function (tab) {
            if (tab instanceof Unidata.view.admin.security.user.UserAdditionalPropertySettings) {
                propertySettingsTab = tab;

                return false; // завершение итерации Ext.Array.each
            }
        });

        return propertySettingsTab;
    },

    getOpenedTabs: function () {
        var tabPanel = this.lookupReference('userTabPanel');

        return tabPanel.items.getRange();
    }
});
