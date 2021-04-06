/**
 * Структура главного меню
 *
 * @author Aleksandr Bavin
 * @date 2017-05-24
 */
Ext.define('Unidata.view.main.menu.MainMenuStructure', {

    singleton: true,

    userExit: Unidata.uiuserexit.overridable.mainmenu.MainMenuStructure,

    /**
     * @type {Unidata.view.main.menu.MainMenu}
     */
    menuComponent: null,

    /**
     * @param {Unidata.view.main.menu.MainMenu} menuComponent
     */
    setMenuComponent: function (menuComponent) {
        this.menuComponent = menuComponent;
    },

    /**
     * Возвращает собранный конфиг для элементов меню
     * @returns {Object[]}
     * @public
     */
    getMenuListsConfig: function () {
        var config;

        config = [
            this.getTopList(),
            this.getCenterList(),
            this.getBottomList()
        ];

        return config;
    },

    /**
     * Верхний список - логотип, переключатель
     * @returns {Object}
     * @protected
     */
    getTopList: function () {
        var topList;

        topList = {
            type: 'mainmenu.top',
            items: [
                {
                    type: 'mainmenu.item.logo'
                },
                {
                    type: 'mainmenu.item.default',
                    text: Unidata.i18n.t('menu>menu'),
                    iconCls: 'icon-menu',
                    listeners: {
                        itemclick: this.menuComponent.toggleCollapsed,
                        scope: this.menuComponent
                    }
                }
            ]
        };

        this.userExit.editTopListItems(topList.items);

        return topList;
    },

    /**
     * Центральный список - основные разделы системы
     * @returns {Object}
     * @protected
     */
    getCenterList: function () {
        var centerList;

        centerList = {
            type: 'mainmenu.center',
            defaults: {
                type: 'mainmenu.item.group',
                sublist: {
                    type: 'mainmenu.inner',
                    defaults: {
                        type: 'mainmenu.item.inner'
                    }
                },
                listeners: {
                    transitionend: this.menuComponent.updateScroll,
                    scope: this.menuComponent
                }
            },
            items: Ext.Array.clean([
                this.getDataProcessingList(),    // Обработка данных
                this.getDataManagementList(),    // Управление данными
                this.getAdminList()              // Администрирование
            ])
        };

        this.userExit.editCenterListItems(centerList.items);

        return centerList;
    },

    /**
     * Обработка данных
     * @returns {null|Object}
     * @protected
     */
    getDataProcessingList: function () {
        var dataProcessingList;

        if (Unidata.Config.getAppMode() === Unidata.Config.APP_MODE.ADMIN) {
            return null;
        }

        dataProcessingList = {
            text: Unidata.i18n.t('menu>dataProcessing'),
            reference: 'dataprocessinglist',
            componentRights: {
                'USER_DEFINED': ['read']
            },
            sublist: {
                items: [
                    {
                        text:      Unidata.i18n.t('menu>home'),
                        reference: 'home',
                        view:      'steward.dashboard.layout',
                        iconCls:   'un-icon-1',
                        colorIndex: 1,
                        pinned: true
                    },
                    {
                        text:      Unidata.i18n.t('menu>data'),
                        reference: 'data',
                        view:      'steward.search.searchpanel',
                        iconCls:   'un-icon-2',
                        hidden: !Unidata.Security.userHasAnyMetaModelRights(),
                        colorIndex: 2,
                        pinned: true
                    },
                    {
                        text:      Unidata.i18n.t('glossary:duplicates'),
                        reference: 'cluster',
                        view:      'steward.cluster',
                        iconCls:   'un-icon-3',
                        hidden: !Unidata.Security.userHasAnyMetaModelRights(),
                        colorIndex: 3
                    },
                    {
                        type: 'mainmenu.item.inner.task',
                        view: 'workflow.tasksearch.layout'
                    },
                    {
                        text:      Unidata.i18n.t('menu>classifier'),
                        reference: 'classifierviewer',
                        view:      'classifierviewer',
                        iconCls:   'un-icon-5',
                        colorIndex: 5
                    }
                ]
            }
        };

        this.userExit.editDataProcessingListItems(dataProcessingList.sublist.items);

        return dataProcessingList;
    },

    /**
     * Управление данными
     * @returns {null|Object}
     * @protected
     */
    getDataManagementList: function () {
        var dataManagementList;

        if (Unidata.Config.getAppMode() === Unidata.Config.APP_MODE.USER) {
            return null;
        }

        dataManagementList = {
            type: 'mainmenu.item.togglegroup',
            reference: 'datamanagementlist',
            text: Unidata.i18n.t('menu>dataManagement'),
            componentRights: {
                'ADMIN_DATA_MANAGEMENT': ['read']
            },
            sublist: {
                items: [
                    {
                        text:      Unidata.i18n.t('menu>dataModel'),
                        reference: 'model',
                        view:      'admin.entity.layout',
                        iconCls:   'icon-puzzle',
                        pinned: true,
                        allowedDraftMode: true
                    },
                    {
                        text:      Unidata.i18n.t('menu>duplicates'),
                        reference: 'duplicates',
                        view:      'admin.duplicates',
                        componentRights: {
                            'ADMIN_MATCHING_MANAGEMENT': ['read']
                        },
                        iconCls:   'icon-compare'
                    },
                    {
                        text:      Unidata.i18n.t('glossary:classifiers'),
                        reference: 'classifiers',
                        view:      'classifier',
                        componentRights: {
                            'ADMIN_CLASSIFIER_MANAGEMENT': ['read']
                        },
                        iconCls:   'icon-indent-increase'
                    },
                    {
                        text:      Unidata.i18n.t('menu>schema'),
                        reference: 'schema',
                        view:      'admin.schema',
                        iconCls:   'icon-site-map'
                    },
                    {
                        text:      Unidata.i18n.t('glossary:dataSources'),
                        reference: 'sources',
                        view:      'admin.sourcesystems.layout',
                        iconCls:   'icon-cable2',
                        allowedDraftMode: true
                    },
                    {
                        text:      Unidata.i18n.t('glossary:functions'),
                        reference: 'functions',
                        view:      'admin.cleanseFunction',
                        iconCls:   'icon-cog',
                        allowedDraftMode: true
                    },
                    {
                        text:      Unidata.i18n.t('glossary:units'),
                        reference: 'measurement',
                        view:      'admin.measurement',
                        componentRights: {
                            'ADMIN_SYSTEM_MANAGEMENT': ['read']
                        },
                        iconCls:   'icon-ruler',
                        allowedDraftMode: true
                    },
                    {
                        text:      Unidata.i18n.t('glossary:enums'),
                        reference: 'enumeration',
                        view:      'admin.enumeration',
                        componentRights: {
                            'ADMIN_SYSTEM_MANAGEMENT': ['read']
                        },
                        iconCls:   'icon-list3',
                        allowedDraftMode: true
                    }
                ]
            }
        };

        this.userExit.editDataManagementListItems(dataManagementList.sublist.items);

        return dataManagementList;
    },

    /**
     * Администрирование
     * @returns {null|Object}
     * @protected
     */
    getAdminList: function () {
        var adminList;

        if (Unidata.Config.getAppMode() === Unidata.Config.APP_MODE.USER) {
            return null;
        }

        adminList = {
            text: Unidata.i18n.t('menu>admin'),
            reference: 'adminlist',
            componentRights: {
                'ADMIN_SYSTEM_MANAGEMENT': ['read']
            },
            sublist: {
                items: [
                    {
                        text:      Unidata.i18n.t('glossary:users'),
                        reference: 'users',
                        view:      'admin.security.user',
                        iconCls:   'icon-user',
                        pinned: true
                    },
                    {
                        text:      Unidata.i18n.t('glossary:roles'),
                        reference: 'roles',
                        view:      'admin.security.role',
                        iconCls:   'icon-users',
                        pinned: true
                    },
                    {
                        text:      Unidata.i18n.t('glossary:securityLabel'),
                        reference: 'security',
                        view:      'admin.security.label',
                        iconCls:   'icon-flag'
                    },
                    {
                        text:      Unidata.i18n.t('menu>operations'),
                        reference: 'operations',
                        view:      'admin.job',
                        iconCls:   'icon-server'
                    },
                    {
                        text:      Unidata.i18n.t('menu>process'),
                        reference: 'process',
                        view:      'workflow.process.assignment',
                        iconCls:   'icon-ligature'
                    },
                    {
                        text:      Unidata.i18n.t('menu>log'),
                        reference: 'log',
                        view:      'admin.audit.log',
                        iconCls:   'icon-binoculars2'
                    },
                    {
                        text:      Unidata.i18n.t('menu>backendProperties'),
                        reference: 'beproperties',
                        componentRights: {
                            'ADMIN_SYSTEM_MANAGEMENT': ['update']
                        },
                        view:      'admin.beproperties',
                        iconCls:   'icon-wrench'
                    }
                ]
            }
        };

        this.userExit.editAdminListItems(adminList.sublist.items);

        return adminList;
    },

    /**
     * Нижний список - пользователь, уведомления, выход
     * @returns {Object}
     * @protected
     */
    getBottomList: function () {
        var bottomList;

        bottomList = {
            type: 'mainmenu.bottom',
            defaults: {
                type: 'mainmenu.item.default',
                collapsed: true,
                sublist: {
                    type: 'mainmenu.inner',
                    defaults: {
                        type: 'mainmenu.item.default'
                    }
                }
            },
            items: [
                {
                    type: 'mainmenu.item.user',
                    sublist: {
                        items: [
                            {
                                type: 'mainmenu.item.password'
                            },
                            {
                                type: 'mainmenu.item.about'
                            }
                        ]
                    }
                },
                {
                    type: 'mainmenu.item.notification'
                },
                {
                    type: 'mainmenu.item.exit'
                }
            ]
        };

        this.userExit.editBottomListItems(bottomList.items);

        return bottomList;
    }

});
