Ext.define('Unidata.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    requires: [
        'Unidata.view.admin.entity.layout.Layout'
    ],

    alias: 'controller.main',

    menuPanelDelayedTask: null,

    pressedButton: null,

    leaveDirtyText: Unidata.i18n.t('application>confirmLeaveUnsavedSection'),

    init: function () {
        var view = this.getView();

        // наблюдатель за масштабом отображения
        Unidata.ZoomWatcher.startWatch();

        Ext.defer(this.initRouter, 100, this);

        view.on('afterrender', this.initCache, this);
    },

    /**
     * Инициализация всех данных, которые необходимы для работы
     */
    initCache: function () {
        var CacheApi = Unidata.util.api.Cache,
            me = this,
            view = this.getView();

        view.setLoading(true);

        CacheApi.loadCache().then(
            function (results) {
                var MetaRecordUtil = Unidata.util.MetaRecord,
                    entities = results[2],
                    lookupEntities = results[3];

                MetaRecordUtil.cleanComponentState(entities, lookupEntities);
                view.setLoading(false);
            },
            function () {
                if (view.isDestroyed) {
                    return;
                }

                Ext.Msg.alert(
                    Unidata.i18n.t('application>initialize'),
                    Unidata.i18n.t('application>initializeError'),
                    me.initCache,
                    me
                );
            }
        ).done();
    },

    initRouter: function () {
        this.routeToTokenValue();
        Unidata.util.Router.on('main', this.routeToTokenValue, this);
    },

    routeToTokenValue: function () {
        var routerTokenValues = Unidata.util.Router.getTokenValues('main'),
            referenceName = 'home';

        if (routerTokenValues && routerTokenValues.section) {
            referenceName = routerTokenValues.section;
        }

        this.routeTo(referenceName);
    },

    routeTo: function (referenceName) {
        var mainMenu = this.lookupReference('mainMenu'),
            menuItem;

        if (mainMenu) {
            menuItem = mainMenu.getMenuItem(referenceName);
        }

        if (menuItem) {
            menuItem.setSelected(true);
        }
    },

    /**
     * Выбран элемент меню
     *
     * @param component - контекст(на кого подписались)
     * @param {Unidata.view.main.menu.MainMenuItem} menuItem
     */
    onMenuItemSelected: function (component, menuItem) {
        var reference = menuItem.getReference();

        if (reference) {
            Unidata.util.Router.setTokenValue('main', 'section', reference);
        }
    }
});
