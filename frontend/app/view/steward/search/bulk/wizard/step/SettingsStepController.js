/**
 * @author Aleksandr Bavin
 * @date 21.06.2016
 */
Ext.define('Unidata.view.steward.search.bulk.wizard.step.SettingsStepController', {
    extend: 'Unidata.view.steward.search.bulk.wizard.WizardStepController',
    alias: 'controller.settings',

    init: function () {
        var view = this.getView();

        view.addComponentListener('resetsettings', this.resetSettings, this);

        // кэшируем виджеты по типам, что бы не загружать лишний раз
        this.widgetByType = {};
        this.callParent(arguments);
    },

    resetSettings: function () {
        this.removeCurrentSettingsWidget();
        this.removeWidgetsCache();
        this.widgetByType = {};
    },

    destroy: function () {
        this.removeWidgetsCache();
        delete this.widgetByType;
        this.callParent(arguments);
    },

    removeWidgetsCache: function () {
        Ext.Object.each(this.widgetByType, function (type, widget) {
            widget.destroy();
        }, this);
    },

    /**
     * Возвращает закэшированный виджет
     * @param {String} type
     * @returns {undefined|Unidata.view.steward.search.bulk.wizard.settings.Default}
     */
    getWidgetByType: function (type) {
        return this.widgetByType[type];
    },

    removeCurrentSettingsWidget: function () {
        var view = this.getView();

        view.remove(view.currentSettingsWidget, false);
        view.currentSettingsWidget = null;
    },

    /**
     * При активации подгружаем виджет с настройками и показываем его
     */
    onActivate: function () {
        var view = this.getView(),
            me = this;

        this.callParent(arguments);

        this.getSettingsWidget().then(
            function (widget) {
                me.removeCurrentSettingsWidget();
                view.currentSettingsWidget = widget;
                view.insert(0, widget);
            },
            function () {
                me.removeCurrentSettingsWidget();
            }
        ).done();
    },

    /**
     * Когда уходим из настроек, прописываем их в визард, что бы потом взять при конфирме
     */
    onBeforedeactivate: function () {
        var view = this.getView(),
            wizard = this.getWizard();

        wizard.setOperationSettings(
            view.currentSettingsWidget.getOperationSettings()
        );
    },

    /**
     * Возвращает промис, а по итогам промиса виджет с настройками
     * @returns {Ext.promise}
     */
    getSettingsWidget: function () {
        var view = this.getView(),
            wizard = this.getWizard(),
            deferred = Ext.create('Ext.Deferred'),
            type = wizard.getOperationType(),
            widget = this.getWidgetByType(type);

        // если виджет уже есть, сразу выходим
        if (widget !== undefined) {
            deferred.resolve(widget);

            return deferred.promise;
        }

        view.setLoading(true);

        Ext.Ajax.request({
            url: Unidata.Config.getMainUrl() + 'internal/data/bulk/configure/' + type,
            method: 'GET',
            scope: this,
            success: function (response) {
                var jsonResp = Ext.util.JSON.decode(response.responseText);

                widget = this.settingsFactory(jsonResp);
                deferred.resolve(widget);
            },
            failure: function () {
                Unidata.showError(Unidata.i18n.t('search>wizard.loadSettingsError'));
                // возвращаем на предыдущий шаг, что бы юзер мог попытаться загрузить настройки еще раз
                wizard.activatePrevStep();
            },
            callback: function () {
                view.setLoading(false);
            }
        });

        return deferred.promise;
    },

    /**
     * Фабрика виджетов с настройками
     * @param config
     * @returns {Unidata.view.steward.search.bulk.wizard.settings.Default}
     */
    settingsFactory: function (config) {
        var widget,
            xtype;

        switch (config.type) {
            //TODO: разные виджеты с настройками
            case 'MODIFY_RECORDS':
                xtype = 'steward.search.bulk.wizard.settings.modifyrecords';
                break;
            case 'REMOVE_RECORDS':
                xtype = 'steward.search.bulk.wizard.settings.removerecords';
                break;
            case 'REMOVE_RELATIONS_TO':
                xtype = 'steward.search.bulk.wizard.settings.removerelationto';
                break;
            case 'REMOVE_RELATIONS_FROM':
                xtype = 'steward.search.bulk.wizard.settings.removerelationfrom';
                break;
            default:
                xtype = 'steward.search.bulk.wizard.settings.default';
        }

        config.xtype = xtype;
        config.wizardStep = this.getView();

        widget = Ext.create(config);

        // кэшируем настройки
        this.widgetByType[config.type] = widget;

        return widget;
    }

});
