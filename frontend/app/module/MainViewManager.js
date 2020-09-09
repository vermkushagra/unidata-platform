/**
 * Синглтон, который управляет отображением компонентов в рабочей области приложения
 *
 * @author Aleksandr Bavin
 * @date 2017-05-18
 */
Ext.define('Unidata.module.MainViewManager', {

    mixins: {
        observable: 'Ext.util.Observable'
    },

    singleton: true,

    mainView: null,
    mainViewContainer: null,

    cache: true, // кэширует компоненты, если true
    componentsCache: null,

    currentXtype: null,
    currentCacheKey: null,

    constructor: function () {
        this.mixins.observable.constructor.call(this);
        this.componentsCache = {};
        this.callParent(arguments);
    },

    setMainView: function (mainView) {
        var oldMainView = this.mainView;

        this.mainView = mainView;

        this.updateMainView(mainView, oldMainView);
    },

    getMainView: function () {
        return this.mainView;
    },

    showComponent: function (xtype, cacheKey) {
        var mainViewContainer = this.getMainViewContainer(),
            currentComponent = this.getComponentsCache(this.currentCacheKey),
            componentToShow;

        this.currentXtype    = xtype;
        this.currentCacheKey = cacheKey;

        if (!mainViewContainer || !cacheKey) {
            return;
        }

        // прячем текущий компонент
        if (currentComponent) {
            currentComponent.hide();
        }

        componentToShow = this.getComponentsCache(cacheKey);

        mainViewContainer.suspendLayouts();
        // не дестроим компоненты, если включено кэширование
        mainViewContainer.removeAll(!this.cache);

        if (componentToShow) {
            mainViewContainer.add(componentToShow);
            componentToShow.show();
        } else {
            componentToShow = mainViewContainer.add({xtype: xtype || ''});
        }

        this.setComponentsCache(cacheKey, componentToShow);

        mainViewContainer.resumeLayouts(true);

        this.fireEvent('change', componentToShow, cacheKey);
    },

    clearComponentsCache: function () {
        Ext.Object.each(this.componentsCache, function (key, component, object) {
            if (!component.isDestroyed) {
                component.destroy();
            }

            object[key] = null;
        });

        this.componentsCache = {};

        this.setMainView(null);
        this.currentXtype = null;
        this.currentCacheKey = null;
    },

    /**
     * Удаляет из кеша вьюшку.
     *
     * @param cacheKey - ключ или инстанс компонента
     */
    removeComponentFromCache: function (cacheKey) {
        if (!this.componentsCache) {
            return;
        }

        Ext.Object.each(this.componentsCache, function (key, component, object) {
            var cmp;

            if (key === cacheKey || component === cacheKey) {
                cmp = object[key];
                object[key] = null;

                if (cmp && !cmp.isDestroyed && !cmp.destroying) {
                    cmp.destroy();
                }
            }
        });
    },

    privates: {
        updateMainView: function (newView) {
            this.mainViewContainer = null;

            if (newView) {
                this.mainViewContainer = newView.lookupReference('mainContainer');

                if (this.currentXtype && this.currentCacheKey) {
                    this.showComponent(this.currentXtype, this.currentCacheKey);
                }
            }
        },

        getMainViewContainer: function () {
            return this.mainViewContainer;
        },

        getComponentsCache: function (cacheKey) {
            var component;

            if (!this.cache) {
                return null;
            }

            if (this.componentsCache[cacheKey]) {
                component = this.componentsCache[cacheKey];

                if (!component.isDestroyed) {
                    return component;
                }
            }

            return null;
        },

        setComponentsCache: function (cacheKey, component) {
            if (!this.cache) {
                return;
            }

            this.componentsCache[cacheKey] = component;
        }
    },

    /**
     * Добавить обработчик события изменения view на определенный view (заданный cacheKey)
     * @param cacheKey
     * @param fn
     * @param scope
     */
    addViewChangeListener: function (cacheKey, fn, scope) {
        this.on('change', function (component, componentCacheKey) {
            if (componentCacheKey === cacheKey) {
                fn.apply(scope, [component]);
            }
        });
    }
});
