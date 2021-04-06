/**
 * @author unidata team
 * @date 2015
 */
Ext.define('Unidata.view.admin.security.role.RoleController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.security.role',

    specialNode: null,
    groupedNode: null,
    unGroupedNode: null,

    statics: {
        // фаза вызова обработчика
        tabChangePhase: {
            BEFORE_ADD_OR_REMOVE_TAB: 'BEFORE_ADD_OR_REMOVE_TAB',   // до удаления/добавления таба
            AFTER_ADD_OR_REMOVE_TAB: 'AFTER_ADD_OR_REMOVE_TAB'      // после удаления/добавления таба
        }
    },

    onRolesGridItemClick: function (sm, role) {
        var tabPanel = this.lookupReference('roleTabPanel'),
            tab = this.getRoleTab(role);

        if (!tab) {
            this.loadRoleAndShowEditor(role);
        } else {
            tabPanel.setActiveTab(tab);
        }
    },

    loadRoleAndShowEditor: function (role) {
        var view = this.getView(),
            id;

        function onSuccessRoleLoad () {
            this.displayRoleTab(role);
        }

        function onFailureRoleLoad () {
            Unidata.showError(view.roleLoadingErrorText);
        }

        if (role.isModified('name')) {
            id = role.getModified('name');
        } else {
            id = role.get('name');
        }

        role = Ext.create('Unidata.model.user.Role');
        role.setId(id);

        role.load({
            success: onSuccessRoleLoad,
            failure: onFailureRoleLoad,
            scope: this
        });
    },

    displayRoleTab: function (role) {
        var tabPanel = this.lookupReference('roleTabPanel'),
            TabChangePhase = Unidata.view.admin.security.role.RoleController.tabChangePhase,
            tab;

        tab = Ext.create('Unidata.view.admin.security.role.RoleEdit', {
            role: role
        });

        tabPanel.add(tab);
        tabPanel.setActiveTab(tab);

        this.updatePropertySettingsTabReadOnly(TabChangePhase.AFTER_ADD_OR_REMOVE_TAB);

        tab.on('close', this.onRoleTabClose, this);
        tab.on('delete', this.onDeleteRole, this);
        tab.on('save', this.onSaveRole, this);

        tab.displayRoleEditor();
    },

    onDeleteRole: function (tab) {
        var tabPanel = this.lookupReference('roleTabPanel');

        tabPanel.remove(tab);

        this.reloadRoleList();
    },

    onSaveRole: function () {
        this.reloadRoleList();
    },

    reloadRoleList: function () {
        var roleList = this.lookupReference('rolesGrid');

        roleList.getStore().reload();
    },

    onRoleTabClose: function () {
        var TabChangePhase = Unidata.view.admin.security.role.RoleController.tabChangePhase;

        this.updatePropertySettingsTabReadOnly(TabChangePhase.BEFORE_ADD_OR_REMOVE_TAB);
    },

    /**
     * Обновить свойство "Только для чтения" для табки настройки custom properties
     *
     * @param tabChangePhase Фаза вызова метода
     */
    updatePropertySettingsTabReadOnly: function (tabChangePhase) {
        var tabPanel = this.lookupReference('roleTabPanel'),
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

    getRoleTab: function (role) {
        var tabPanel = this.lookupReference('roleTabPanel'),
            tab = null;

        if (!role) {
            return tab;
        }

        tabPanel.items.each(function (item) {
            var isPropertyTab,
                itemRole;

            isPropertyTab = item instanceof Unidata.view.admin.security.role.RoleAdditionalPropertySettings;

            if (!isPropertyTab) {
                itemRole = item.getRole();

                if (!itemRole.phantom && itemRole.get('name') === role.get('name')) {
                    tab = item;

                    return false; // заврешение итерации цикла each
                }
            }
        });

        return tab;
    },

    /**
     * Обработчик клика по кнопке редактирования пропертей
     */
    onRolePropertiesSettingsClick: function () {
        var tabPanel = this.lookupReference('roleTabPanel'),
            countTab,
            propertyEditorReadOnly,
            propertySettingsTab;

        countTab = tabPanel.items.getCount();

        // редактирование допустимо только если нет открытых карточек ролей
        propertyEditorReadOnly = (countTab > 0) ? true : false;

        propertySettingsTab = this.getOpenedPropertySettingsTab();

        if (!propertySettingsTab) {
            propertySettingsTab = Ext.create('Unidata.view.admin.security.role.RoleAdditionalPropertySettings', {
                readOnly: propertyEditorReadOnly
            });

            tabPanel.insert(0, propertySettingsTab);
        } else {
            tabPanel.setActiveTab(propertySettingsTab);
        }
    },

    getOpenedTabs: function () {
        var tabPanel = this.lookupReference('roleTabPanel');

        return tabPanel.items.getRange();
    },

    getOpenedPropertySettingsTab: function () {
        var tabs = this.getOpenedTabs(),
            propertySettingsTab = null;

        Ext.Array.each(tabs, function (tab) {
            if (tab instanceof Unidata.view.admin.security.role.RoleAdditionalPropertySettings) {
                propertySettingsTab = tab;

                return false; // завершение итерации Ext.Array.each
            }
        });

        return propertySettingsTab;
    },

    getOpenedRoleTabs: function () {
        var tabs = this.getOpenedTabs(),
            roleTabs = [];

        Ext.Array.each(tabs, function (tab) {
            if (tab instanceof Unidata.view.admin.security.role.RoleEdit) {
                roleTabs.push(tab);
            }
        });

        return roleTabs;
    },

    onAddRoleButtonClick: function () {
        this.createAndDisplayRole();
    },

    createAndDisplayRole: function () {
        var role = new Unidata.model.user.Role();

        role.properties().load({
            callback: function (records, operation, success) {
                if (success) {
                    this.displayRoleTab(role);
                }
            },
            scope: this
        });

        return role;
    }
});
