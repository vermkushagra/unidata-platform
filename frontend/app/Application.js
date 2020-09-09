Ext.define('Unidata.Application', {
    extend: 'Ext.app.Application',

    requires: [
        'Unidata.constant.Http',
        'Unidata.constant.Delay',
        'Unidata.Constants',

        'Unidata.Config',
        'Unidata.Security',
        'Unidata.Api',
        'Unidata.StatusConstant',

        'Unidata.module.poller.NotificationCountPoller',
        'Unidata.module.poller.TaskCountPoller',
        'Unidata.module.MainViewManager',
        'Unidata.module.hotkey.GlobalHotKeyManager',
        'Unidata.module.ComponentStateStorage',
        'Unidata.module.ComponentState',
        'Unidata.module.EventNotifier',
        'Unidata.module.search.*',

        'Unidata.util.Router',
        'Unidata.util.DataAttributeFormatter',
        'Unidata.util.FileUploadDownload',
        'Unidata.util.Icon',
        'Unidata.util.MetaAttribute',
        'Unidata.util.MetaAttributeFormatter',
        'Unidata.util.SecurityLabel',
        'Unidata.util.Session',
        'Unidata.util.UPathData',
        'Unidata.util.UPathMeta',
        'Unidata.util.UserDialog',
        'Unidata.util.ZoomDetector',
        'Unidata.util.DownloadFile',
        'Unidata.util.ErrorMessageFactory',
        'Unidata.util.UserDialog',
        'Unidata.util.ValidityPeriod',
        'Unidata.util.api.AbstractApi',
        'Unidata.util.api.RelationContains',
        'Unidata.util.api.RelationReference',
        'Unidata.util.api.RelationM2m',
        'Unidata.util.api.TimeInterval',
        'Unidata.util.api.DataRecord',
        'Unidata.util.api.OriginRecord',
        'Unidata.util.api.MetaRecord',
        'Unidata.util.api.RelationViewMetaRecord',
        'Unidata.util.api.RelationsDigest',
        'Unidata.util.api.Enumeration',
        'Unidata.util.api.Authenticate',
        'Unidata.util.api.Task',
        'Unidata.util.EntityDependency',

        'Unidata.ZoomWatcher',

        'Unidata.view.component.grid.masonry.MasonryGridComponent',

        'Unidata.view.component.*',
        'Unidata.view.component.DisableFieldSet',
        'Unidata.view.component.HrefLabel',
        'Unidata.view.component.search.searchpanel.SearchPanel',

        'Ext.window.Toast',
        'Ext.chart.series.Line',
        'Ext.toolbar.Spacer', // sencha cmd не всегда находит зависимость
        'Unidata.module.storage.BackendStorageManager',
        'Unidata.module.storage.LocalStorageManager',
        'Unidata.module.storage.EtalonClusterStorage',
        'Unidata.module.storage.QueryPresetStorage',
        'Unidata.module.notifier.DraftModeNotifier',

        'Unidata.proxy.*',

        'Unidata.store.*',

        'Unidata.validator.*',

        'Unidata.view.component.timeinterval.*',

        'Unidata.field.*',

        'Unidata.event.*',

        'Unidata.plugin.component.ManagedStoreLoader',
        'Unidata.plugin.grid.column.HeaderItemSwitcher',
        'Unidata.plugin.grid.HideColumnIfEmpty',
        'Unidata.plugin.form.field.FieldRouter',

        'Ext.Deferred',

        'Unidata.view.steward.cluster.merge.Merge',

        'Unidata.view.component.search.SearchWindow',
        'Unidata.view.component.WarningMessage',
        'Unidata.uiuserexit.viewmodel.FormulaProvider',
        'Unidata.uiuserexit.dataviewer.MenuButtonProvider',
        'Unidata.uiuserexit.callback.CallbackProvider',

        'Unidata.uiuserexit.overridable.*'

    ],

    name: 'Unidata',

    models: [
        'KeyValuePair',

        'attribute.Enumeration',
        'attribute.AliasCodeAttribute',
        'attribute.CodeAttribute',
        'attribute.SimpleAttribute',
        'attribute.ComplexAttribute',

        'entity.GenerationStrategy',
        'entity.Enumeration',
        'entity.LookupEntity',
        'entity.NestedEntity',
        'entity.Entity',
        'entity.Relation',
        'entity.Catalog',

        'cleansefunction.thirdparty.CleanseFunctionLoadStatus',
        'cleansefunction.thirdparty.CleanseFunctionLoadAction',

        'cleansefunction.OutputPort',
        'cleansefunction.InputPort',
        'cleansefunction.CleanseFunction',
        'cleansefunction.Group',

        'cleansefunction.Node',
        'cleansefunction.Link',
        'cleansefunction.Logic',
        'cleansefunction.CompositeCleanseFunction',

        'search.SearchPreview',
        'search.SearchHit',
        'search.SearchResult',
        'search.QueryPreset',

        'workflow.Task',

        'data.AbstractAttribute',
        'data.ComplexAttribute',
        'data.NestedRecord',

        'data.RelationReference',
        'data.RelationContains',
        'data.RelationTimeline',
        'data.RelationReferenceDiff',
        'data.RelationContainsDiff',
        'data.RelationReferenceDelete',

        'data.Record',
        'data.OriginRecord',
        'data.AtomicRecord',
        'data.SimpleAttribute',
        'data.CodeAttribute',
        'data.ArrayAttribute',
        'data.DqError',
        'data.ClassifierNode',
        'data.TimeInterval',
        'data.Contributor',
        'data.DataRecordKey',
        'data.AttributeDiff',

        'dataquality.Input',
        'dataquality.Output',
        'dataquality.DqRule',
        'dataquality.DqOrigins',
        'dataquality.DqRaise',
        'dataquality.DqEnrich',

        'sourcesystem.SourceSystemsInfo',
        'sourcesystem.SourceSystem',

        'dashboard.StatsResponse',
        'dashboard.Stats',
        'dashboard.Series',
        'dashboard.ValidationError',

        'user.SecuredResource',
        'user.User',
        'user.Right',
        'user.Role',
        'user.SecurityLabelUser',
        'user.SecurityLabelAttributeUser',
        'user.SecurityLabelRole',
        'user.SecurityLabelAttributeRole',
        'user.UserEndpoint',
        'user.UserProperty',
        'user.RoleProperty',

        'mergesettings.SourceSystemsConfig',
        'mergesettings.MergeSettings',
        'mergesettings.Bvt',
        'mergesettings.Bvr',
        'mergesettings.Attribute',

        'matching.Group',
        'matching.PostAction',

        'notification.Notification',

        'attribute.ClassifierNodeAttribute',
        'classifier.Classifier',
        'classifier.ClassifierNode',

        'cluster.ClusterSearchHit',

        'measurement.MeasurementValue',
        'entity.EntityDependency',

        'etaloncluster.EtalonClusterRecord',
        'etaloncluster.EtalonCluster',

        'entity.metadependency.Vertex',
        'entity.metadependency.Edge',
        'entity.metadependency.MetaDependencyGraph',

        'presentation.AttributeGroup',
        'presentation.RelationGroup'
    ],

    views: [
        'Unidata.view.main.Main',
        'Unidata.view.login.lock.Lock',
        'Unidata.view.login.password.reset.ResetPassword',
        'Unidata.view.login.password.change.ChangePassword',
        'Unidata.view.steward.search.searchpanel.SearchPanel',
        'Unidata.view.workflow.tasksearch.layout.Layout',
        'Unidata.view.workflow.process.assignment.WorkflowProcessAssignment',
        'Unidata.view.steward.dashboard.layout.Layout',
        'Unidata.view.admin.entity.layout.Layout',
        'Unidata.view.admin.cleanseFunction.CleanseFunction',
        'Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction',
        'Unidata.view.admin.schema.Schema',
        'Unidata.view.admin.sourcesystems.layout.Layout',
        'Unidata.view.admin.security.user.User',
        'Unidata.view.admin.security.role.Role',
        'Unidata.view.admin.security.label.Label',
        'Unidata.view.admin.job.Job',
        'Unidata.view.admin.audit.log.AuditLog',
        'Unidata.view.steward.notification.NotificationArea',
        'Unidata.view.admin.duplicates.Duplicates',
        'Unidata.view.steward.cluster.Cluster',
        'Unidata.view.classifier.ClassifierLayout',
        'Unidata.view.classifierviewer.ClassifierViewerLayout',
        'Unidata.view.admin.measurement.Measurement',
        'Unidata.view.admin.enumeration.Enumeration',
        'Unidata.view.admin.beproperties.BackendProperties'
    ],

    config: {
        activeView: null
    },

    lsStateProvider: null,

    notificationCountPoller: null,
    taskCountPoller: null,

    launch: function () {
        this.initLocalization();

        this.initApplicationEventListeners();

        this.requireCustomerExternalClass();

        // инициализируем обновление количества нотификаций пользователя
        this.initNotificationCountPoller();
        // инициализируем обновление количества задач пользователя
        this.initTaskCountPoller();
    },

    /**
     * Перепоределяет некоторые параметры локализации т.к. в ExtJS некоторые места не удачно реализованы
     * например смотри в ext\packages\ext-locale\overrides\ru\ext-locale-ru.js переопределине для Ext.util.Format
     */
    initLocalization: function () {
        Ext.util.Format.thousandSeparator = Unidata.Config.getThousandSeparator();
    },

    /**
     * Настройка обработчиков глобальных событий
     */
    initApplicationEventListeners: function () {
        var application = Unidata.getApplication();

        // пользователь аутентифицирован
        application.on('authenticate', this.onAuthenticate, this);
        // пользователь деаутентифицирован
        application.on('deauthenticate', this.onDeauthenticate, this);
        // сессия пользователя истекла
        application.on('sessionexpire', this.onSessionExpire, this);
    },

    /**
     * Инициализирует "светофор" - переодическое опрашивание BE на получение задач пользователя
     */
    initNotificationCountPoller: function () {
        this.notificationCountPoller = Ext.create('Unidata.module.poller.NotificationCountPoller');
    },

    /**
     * Инициализирует "светофор" - переодическое опрашивание BE на получение количества нотификаций пользователя
     */
    initTaskCountPoller: function () {
        this.taskCountPoller = Unidata.module.poller.TaskCountPoller.getInstance();
    },

    /**
     * Обработка глобального события - аутентификация пользователя
     *
     * @param authenticateData
     */
    onAuthenticate: function (authenticateData) {
        var me = this,
            application = Unidata.getApplication();

        this.assignInitialPage(authenticateData);

        Unidata.util.Session.setTokenTTL(authenticateData.tokenTTL);

        Unidata.Config.setAuthenticateData(authenticateData);
        Unidata.Config.setUser(authenticateData.userInfo);
        Unidata.Config.setRole(authenticateData.rights);
        Unidata.Config.setToken(authenticateData.token);

        Unidata.License.setLicense(authenticateData.license);

        // если в админском режиме и без админских прав - отправляем на экран логина
        if (Unidata.Config.getAppMode() === Unidata.Config.APP_MODE.ADMIN && !Unidata.Config.userHasAdminRights()) {

            Unidata.util.Session.stop();

            Unidata.util.Session.clearSessionData();

            Unidata.Config.setAuthenticateData(null);
            Unidata.Config.setUser(null);
            Unidata.Config.setRole(null);
            Unidata.Config.setToken(null);

            Unidata.License.setLicense(null);

            this.gotoLogin();

            Unidata.showError(Unidata.i18n.t('menu>accessDeniedToAdminPanel'));

            return;
        }

        // загружаем пользовательские данные
        Unidata.BackendStorage.loadByCurrentUser().then(
            function () {
                // если надо менять пароль то не авторизуем, а перенаправляем на страницу смены пароля
                if (authenticateData.forcePasswordChange) {
                    application.showViewPort('resetpassword');
                } else {
                    application.showViewPort('main');
                }

                // активируем глобальные хоткеи зависимые от авторизации пользователя
                Unidata.module.hotkey.GlobalHotKeyManager.enableAutoManagedHotKeys();

                // запускаем обновление количества нотификаций
                me.notificationCountPoller.start();
                me.taskCountPoller.start();
            }
        ).done();
    },

    /**
     * Назначение стартовой страницы пользователя
     *
     * @param authenticateData
     */
    assignInitialPage: function (authenticateData) {
        var currentHash =  window.location.hash,
            uriParamsString = currentHash.substr(currentHash.indexOf('?') + 1),
            homePage = 'home',
            initalPageHash;

        if (!uriParamsString || this.getSectionPath(uriParamsString) === homePage) {
            initalPageHash = Unidata.uiuserexit.overridable.authorization.InitialPage.buildInitialPageHash(authenticateData);

            if (initalPageHash) {
                window.location.hash = initalPageHash;
            }
        }
    },

    getSectionPath: function (uriParamsString) {
        var uriParamsObject = {};

        uriParamsString.split('&').forEach(function (part) {
            var item = part.split('=');

            uriParamsObject[item[0]] = decodeURIComponent(item[1]);

        });

        return uriParamsObject['section'];
    },

    /**
     * Обработка глобального события - деаутентификация пользователя
     */
    onDeauthenticate: function () {
        Unidata.util.Session.stop();

        Unidata.util.Session.clearSessionData();

        Unidata.Config.setAuthenticateData(null);
        Unidata.Config.setUser(null);
        Unidata.Config.setRole(null);
        Unidata.Config.setToken(null);

        Unidata.License.setLicense(null);

        // деактивируем глобальные хоткеи зависимые от авторизации пользователя
        Unidata.module.hotkey.GlobalHotKeyManager.disableAutoManagedHotKeys();

        // очищаем кэш компонентов
        Unidata.module.MainViewManager.clearComponentsCache();

        // останавливаем обновление количества нотификаций
        this.notificationCountPoller.stop();
        this.taskCountPoller.stop();

        this.gotoLogin();
    },

    /**
     * Обработка глобального события - сессия пользователя истекла
     */
    onSessionExpire: function () {
        // деактивируем глобальные хоткеи зависимые от авторизации пользователя
        Unidata.module.hotkey.GlobalHotKeyManager.disableAutoManagedHotKeys();

        // останавливаем обновление количества нотификаций
        this.notificationCountPoller.stop();
        this.taskCountPoller.stop();
    },

    /**
     * Загружает сторонние классы
     */
    requireCustomerExternalClass: function () {
        var me = this,
            externalUrls = Unidata.Config.getPlatformExternalVendorLibUrls(),
            externalClassNames = Unidata.Config.getPlatformExternalClasses(),
            customerUrl = Ext.String.trim(String(window.customerUrl));

        // нормализуем url ExtJS ожидает что url не будет заканчиваться слешем, в противном случае будет не верно сформирован url файла
        customerUrl = customerUrl.replace(/\/+$/, '').replace(/\\+$/, '');

        // классы CUX.* закачиваются из раположения указанного кастомером
        if (!Ext.isEmpty(customerUrl)) {
            // добавляем CUX для того чтобы можно было кастомеру просто отдать папку с файлами расширения
            Ext.Loader.setPath('CUX', customerUrl + '/CUX');
            Ext.Loader.setPath('CUX.unidata', 'CUX/unidata');
        }

        // подгружаем зависимости
        // сначала загружаем библиотеки внешних поставщиков т.к. теоретически они могут быть использованы во
        // внешних классах кастомера. Простой пример: внешнеие представление атрибута с использованием яднекс карты =>
        // требуется загрузка либы yamaps
        Ext.Loader.loadScript({
            url: externalUrls,
            onLoad: function () {
                try {
                    Ext.Loader.syncRequire(externalClassNames, function () {
                        me.launchUnidataApplication();
                    });
                } catch (e) {
                    alert(Unidata.i18n.t('application>error.loadExternalDeps'));
                }
            },
            onError: function () {
                alert(Unidata.i18n.t('application>error.loadExternalDeps'));
            }
        });
    },

    /**
     * Стартует приложение
     */
    launchUnidataApplication: function () {
        var UiUeUnidataPlatform = Unidata.uiuserexit.overridable.UnidataPlatform,
            checkResults;

        this.createShorthandFunction();

        // проверка параметров customer.json
        checkResults = this.checkCustomerConfig();

        if (Ext.isArray(checkResults) && checkResults.length > 0) {
            this.handleCustomerConfigErrors(checkResults);

            return;
        }

        this.lsStateProvider = Ext.create('Ext.state.LocalStorageProvider');

        Ext.Ajax.on('beforerequest', this.onAjaxBeforeRequest, this);
        Ext.Ajax.on('requestexception', this.onAjaxRequestException, this);

        // после загрузки классов кастомера необходимо обновить настраиваемые данные
        UiUeUnidataPlatform.updatePlatformFavicon();
        UiUeUnidataPlatform.updatePlatformTitle();

        Unidata.util.Session.init();

        this.doAuthorization();
    },

    /**
     * Проверка параметров customer.json
     *
     * @returns {Array} Массив сообщение об ошибках
     */
    checkCustomerConfig: function () {
        var MIN_MERGE_RECORD_DISPLAY = 1,
            MAX_MERGE_RECORD_DISPLAY = 4,
            MERGE_RECORD_DISPLAY_COUNT = Unidata.Config.getMergeRecordDisplayCount(),
            APP_MODE = Unidata.Config.getAppMode(),
            checkResults = [];

        // проверка параметра MERGE_RECORD_DISPLAY_COUNT
        if (!Ext.isNumber(MERGE_RECORD_DISPLAY_COUNT)) {
            checkResults.push(Unidata.i18n.t('application>error.mergeRecordDisplayCountShouldInt'));
        } else if (MERGE_RECORD_DISPLAY_COUNT < MIN_MERGE_RECORD_DISPLAY || MERGE_RECORD_DISPLAY_COUNT > MAX_MERGE_RECORD_DISPLAY) {
            checkResults.push(
                Unidata.i18n.t('application>error.mergeRecordDisplayCountShouldInRange', {
                    min: MIN_MERGE_RECORD_DISPLAY,
                    max: MAX_MERGE_RECORD_DISPLAY
                })
            );
        }

        // проверка параметра APP_MODE
        if (APP_MODE === undefined) {
            checkResults.push(Unidata.i18n.t('application>error.notSetAppModeParameter'));
        } else if (Ext.Object.getValues(Unidata.Config.APP_MODE).indexOf(APP_MODE) === -1) {
            checkResults.push(Unidata.i18n.t('application>error.invalidAppModeParameter'));
        }

        return checkResults;
    },

    /**
     * Обработка ошибок проверки параметров customer.json
     *
     * @param checkResults Сообщения об ошибках
     */
    handleCustomerConfigErrors: function (checkResults) {
        var MAX_ERROR_DISPLAY = 5,
            checkResultsLength = checkResults.length,
            checkResult,
            count,
            i;

        count = checkResultsLength > MAX_ERROR_DISPLAY ? MAX_ERROR_DISPLAY : checkResultsLength;

        for (i = 0; i < count; i++) {
            checkResult = checkResults[i];
            Unidata.showError(checkResult, false);
        }
    },

    /**
     * Создает по alias viewport.
     * Если уже были созданы viewport - они удаляются. В случае если создан один viewport и его alias совпадает
     * с создаваемым, то ничего не происходит
     *
     * @param widgetAlias - alias создаваемого viewport
     */
    showViewPort: function (widgetAlias) {
        var viewPorts = [],
            widget;

        viewPorts = Ext.ComponentQuery.query('viewport');

        // если существует единственный viewport и мы пытаемся создать его же то выходим
        if (viewPorts.length === 1 && viewPorts[0].getXType() === widgetAlias) {
            return;
        }

        // закрываем все всплывающие подсказки
        Ext.window.Toast.closeAllToasts(true);
        // закрываем все окна
        Ext.window.Window.closeAllWindows(true);

        // очищаем кэш компонентов
        Unidata.module.MainViewManager.clearComponentsCache();

        // удаляем все viewport
        Ext.Array.each(viewPorts, function (viewPort) {
            viewPort.destroy();
        });

        widget = Ext.widget(widgetAlias);

        this.setActiveView(widget);

        return widget;
    },

    /**
     * Переходим на форму логина
     */
    gotoLogin: function () {
        // на всякий случай затираем токен
        Unidata.Config.setToken(null);

        window.location.reload();
    },

    /**
     * Выполняет авторизацию пользователя
     */
    doAuthorization: function () {
        var application = Unidata.getApplication();

        // в настоящий момент авторизация производится в отдельном внешнем модуле
        application.fireEvent('authenticate', Unidata.Micrologin.authenticateData);
    },

    onAjaxBeforeRequest: function (conn, options) {
        var headers,
            url;

        url = options.url;

        if (options.headers === undefined) {
            options.headers = {};
        }

        headers = options.headers;

        // TODO: обновление сессии должно происходить после успешного получения ответа от сервера?
        // т.к. сессия может теоретически быть прервана на сервере
        if (!headers.hasOwnProperty('PROLONG_TTL') || headers['PROLONG_TTL'] === 'true') {
            Unidata.util.Session.updateSession();
        }

        if (Ext.isString(url) && url.indexOf(Unidata.Config.getMainUrl()) === 0) {
            // добавляем специальный хидер если запрос к нашему BE
            options.headers.Authorization = Unidata.Config.getToken();
        }
    },

    onAjaxRequestException: function (conn, response, options) {
        var result = Ext.decode(response.responseText, true),
            msg = Unidata.i18n.t('application>error.unknownServerError'),
            error,
            title;

        if (response.aborted) {
            return;
        }

        function isErrorsExists (result) {
            return result && result.errors && result.errors.length;
        }

        if (response.status === 401) {
            // Unauthorized
            msg = Unidata.i18n.t('application>error.occurredAuth');
            title = Unidata.i18n.t('application>error.auth');

            Unidata.Config.setToken(null);

            Unidata.util.Session.hideScreen();

            this.gotoLogin();
        } else if (response.status === 403) {
            // TODO: временный обработчик. впоследствии может будет более интеллектуальный.
            // Пока решили просто глушить ошибки доступа к неавторизованным ресурсам
            // Теперь http code 403 используется только для таких ошибок
            // А не для аутентификации как было раньше
            console.log(Unidata.i18n.t('application>error.access'));
        } else if (response.status === 402) {
            title = Unidata.i18n.t('application>error.license');
            msg = Unidata.i18n.t('application>error.invalidLicense.license');

            if (isErrorsExists(result)) {
                error = result.errors[0];

                msg = error.userMessage || msg;
            }

            Unidata.showError(msg, false);
        } else if (response.status === 0) {
            msg = Unidata.i18n.t('application>error.serverConnection');

            Unidata.showError(msg);
        } else {
            // Server errors
            if (response.status === 500) {
                msg = Unidata.i18n.t('application>error.internalServerError');
            } else if (response.status === 405) {
                msg = Unidata.i18n.t('application>error.unsupportedRequest');
            } else if (response.status === 404) {
                msg = Unidata.i18n.t('application>error.urlNotFound');
            }

            if (isErrorsExists(result)) {
                error = result.errors[0];
                error.errorUrl = options.url;
                error.stackTrace = result.stackTrace;
                error.severity = 'CRITICAL';
                msg = error.userMessage || msg;
            }

            Unidata.showError(msg, false, error);
        }
    },

    createShorthandFunction: function () {
        var UserDialog = Unidata.util.UserDialog;

        Unidata.showError = UserDialog.showError.bind(UserDialog);
        Unidata.showWarning = UserDialog.showWarning.bind(UserDialog);
        Unidata.showMessage = UserDialog.showMessage.bind(UserDialog);
        Unidata.showPrompt = UserDialog.showPrompt.bind(UserDialog);

        Unidata.formatDataValueByAttribute = Unidata.util.DataAttributeFormatter.formatValueByAttribute;
        Unidata.buildMetaTypeDisplayText = Unidata.util.MetaAttributeFormatter.buildTypeDisplayText;
        Unidata.buildAttributePaths = Unidata.util.UPathMeta.buildAttributePaths;
    }
});
