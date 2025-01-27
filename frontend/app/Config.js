Ext.define('Unidata.Config', {
    singleton: true,

    config: {
        mainUrl: null,
        customerCfg: null,
        //TODO: write comments for format
        dateTimeFormat: 'd.m.Y H:i:s',
        timeFormat: 'H:i:s',
        dateTimeFormatProxy: 'Y-m-d\\TH:i:s.u',    // add 'P' postfix for timezone offset
        dateTimeFormatServer: 'Y-m-dTH:i:s.u',      // add 'Z' postfix for timezone offset
        dateTimeFormatConfig: 'Y-m-d\\TH:i:s.u',
        decimalDelimiter: ',', // use . or , ONLY
        serverDecimalDelimiter: '.', // use . or , ONLY
        decimalRegExpTpl: '^[-+]?\\d*{0}?\\d+$',
        fileNameLength: 40
    },

    user: null,
    role: null,

    dateFormats: {
        'ru': 'd.m.Y',
        'en': 'm/d/Y'
    },

    getDateFormat: function () {
        var locale = this.getLocale(),
            dateFormat = this.dateFormats[locale];

        if (!dateFormat) {
            throw new Error('No date formats in config');
        }

        return this.dateFormats[locale];
    },

    // список режимов приложения
    APP_MODE: {
        DEV: 'dev',
        ADMIN: 'admin',
        USER: 'user'
    },

    // список всех прав
    RIGHT: {
        // Администратор классификаторов
        ADMIN_CLASSIFIER_MANAGEMENT: 'ADMIN_CLASSIFIER_MANAGEMENT',
        // Администратор данных
        ADMIN_DATA_MANAGEMENT: 'ADMIN_DATA_MANAGEMENT',
        // Администратор правил сопоставления записей
        ADMIN_MATCHING_MANAGEMENT: 'ADMIN_MATCHING_MANAGEMENT',
        // Администратор системы
        ADMIN_SYSTEM_MANAGEMENT: 'ADMIN_SYSTEM_MANAGEMENT',
        // Групповые операции
        BULK_OPERATIONS_OPERATOR: 'BULK_OPERATIONS_OPERATOR'
    },

    constructor: function (config) {
        this.initConfig(config);

        if (window.customerConfig) {
            this.initCustomerConfig(window.customerConfig);
        }
    },

    initCustomerConfig: function (customerCfg) {
        if (customerCfg.serverUrl) {
            this.setMainUrl(customerCfg.serverUrl);
        }

        this.setCustomerCfg(customerCfg);
    },

    getAppMode: function () {
        return this.getCustomerCfg()['APP_MODE'];
    },

    getAppModeUrl: function (appMode) {
        return this.getCustomerCfg()['APP_MODE_URL'][appMode];
    },

    /**
     * Возвращает список url внешних библиотек которные необходимо подгрузить до загрузки платформы
     * @returns {*}
     */
    getPlatformExternalVendorLibUrls: function () {
        var customerCfg = this.getCustomerCfg();

        if (!Ext.isArray(customerCfg.CDN_VENDOR_LIBS)) {
            return [];
        }

        return Ext.clone(customerCfg.CDN_VENDOR_LIBS);
    },

    /**
     * Возвращает список внешних классов, подключаемых к платформе
     *
     * @returns {*}
     */
    getPlatformExternalClasses: function () {
        var customerCfg = this.getCustomerCfg();

        if (!Ext.isArray(customerCfg.PLATFORM_EXTERNAL_CLASSES)) {
            return [];
        }

        return Ext.clone(customerCfg.PLATFORM_EXTERNAL_CLASSES);
    },

    /**
     * Парсит дату из строки по формату dateTimeFormatConfig
     *
     * @param dateString - строковое представление даты
     * @returns {*|number}
     */
    parseDateTimeFormatConfig: function (dateString) {
        var format = this.getDateTimeFormatConfig();

        return Ext.Date.parse(dateString, format);
    },

    /**
     * Преобразует дату в строку по формату dateFormat
     *
     * @param date - дата
     * @returns {*}
     */
    formatDateFormatConfig: function (date) {
        var format = this.getDateFormat();

        return Ext.Date.format(date, format);
    },

    /**
     * Парсит дату из строки по формату dateTimeFormatConfig с преобразованием в строку по формату dateFormat
     *
     * @param dateString
     * @returns {*}
     */
    parseDateTimeAndFormatDate: function (dateString) {
        var parsedDate = this.parseDateTimeFormatConfig(dateString);

        return this.formatDateFormatConfig(parsedDate);
    },

    getMinDate: function () {
        var result      = null,
            customerCfg = this.getCustomerCfg();

        if (customerCfg.GLOBAL_DATE_LIMITS) {
            result = this.parseDateTimeFormatConfig(customerCfg.GLOBAL_DATE_LIMITS.MIN);
        }

        return result;
    },

    getMaxDate: function () {
        var result      = null,
            customerCfg = this.getCustomerCfg();

        if (customerCfg.GLOBAL_DATE_LIMITS) {
            result = this.parseDateTimeFormatConfig(customerCfg.GLOBAL_DATE_LIMITS.MAX);
        }

        return result;
    },

    getMinDateSymbolHtml: function () {
        var result      = '-&infin;',
            customerCfg = this.getCustomerCfg();

        if (customerCfg.GLOBAL_DATE_LIMITS && customerCfg.GLOBAL_DATE_LIMITS.MIN) {
            result = this.parseDateTimeAndFormatDate(customerCfg.GLOBAL_DATE_LIMITS.MIN);
        }

        return result;
    },

    getMaxDateSymbolHtml: function () {
        var result      = '+&infin;',
            customerCfg = this.getCustomerCfg();

        if (customerCfg.GLOBAL_DATE_LIMITS && customerCfg.GLOBAL_DATE_LIMITS.MAX) {
            result = this.parseDateTimeAndFormatDate(customerCfg.GLOBAL_DATE_LIMITS.MAX);
        }

        return result;
    },

    getMinDateSymbol: function () {
        var result      = '- \u221E',
            customerCfg = this.getCustomerCfg();

        if (customerCfg.GLOBAL_DATE_LIMITS && customerCfg.GLOBAL_DATE_LIMITS.MIN) {
            result = this.parseDateTimeAndFormatDate(customerCfg.GLOBAL_DATE_LIMITS.MIN);
        }

        return result;
    },

    getMaxDateSymbol: function () {
        var result      = '+ \u221E',
            customerCfg = this.getCustomerCfg();

        if (customerCfg.GLOBAL_DATE_LIMITS && customerCfg.GLOBAL_DATE_LIMITS.MAX) {
            result = this.parseDateTimeAndFormatDate(customerCfg.GLOBAL_DATE_LIMITS.MAX);
        }

        return result;
    },

    getCheckSourceSystemWeightUnique: function () {
        var result      = true,
            customerCfg = this.getCustomerCfg();

        if (customerCfg.CHECK_SOURCESYSTEM_WEIGHT_UNIQUE !== undefined) {
            result = customerCfg.CHECK_SOURCESYSTEM_WEIGHT_UNIQUE;
        }

        return result;
    },

    getMergeRecordDisplayCount: function () {
        var mergeRecordDisplayCount = null,
            customerCfg = this.getCustomerCfg();

        if (customerCfg && customerCfg.hasOwnProperty('MERGE_RECORD_DISPLAY_COUNT')) {
            mergeRecordDisplayCount = customerCfg.MERGE_RECORD_DISPLAY_COUNT;
        }

        return mergeRecordDisplayCount;
    },

    getRole: function () {
        return Unidata.Config.role;
    },

    getMaxTabs: function () {
        var maxTabs = 1,
            customerCfg = this.getCustomerCfg();

        if (customerCfg && customerCfg.MAX_TABS) {
            maxTabs = customerCfg.MAX_TABS;
        }

        return maxTabs;
    },

    getTimeintervalEnabled: function () {
        var timeintervalEnabled = true,
            customerCfg = this.getCustomerCfg();

        if (customerCfg && Ext.isBoolean(customerCfg.TIMEINTERVAL_ENABLED)) {
            timeintervalEnabled = customerCfg.TIMEINTERVAL_ENABLED;
        }

        return timeintervalEnabled;
    },

    getQueryPresetPanelVisible: function () {
        var queryPresetPanelVisible = false,
            customerCfg = this.getCustomerCfg();

        if (customerCfg && Ext.isBoolean(customerCfg.QUERY_PRESET_PANEL_VISIBLE)) {
            queryPresetPanelVisible = customerCfg.QUERY_PRESET_PANEL_VISIBLE;
        }

        return queryPresetPanelVisible;
    },

    /**
     * Возвращает true если пользователь обладает правами на ресурс
     *
     * @param resource имя ресурска
     * @param rightName имя права (create/read/update/delete)
     *
     * @returns {boolean}
     */
    userHasRight: function (resource, rightName) {
        var hasRight = false,
            rights = this.getRole();

        //администратору можно все
        if (this.isUserAdmin()) {
            hasRight = true;

            return hasRight;
        }

        if (Ext.isArray(rights) && Ext.isString(rightName)) {
            Ext.Array.each(rights, function (right) {
                if (right &&
                    right.securedResource &&
                    right.securedResource.name === resource &&
                    right[rightName] === true) {

                    hasRight = true;

                    return false; // остановка итерации Ext.Array.each
                }
            });
        }

        return hasRight;
    },

    /**
     * Возвращает true если у пользователя есть хотя бы одно из прав на ресурс
     *
     * @param resource - имя ресурска
     * @param rightNames - массив имен прав (create/read/update/delete)
     *
     * @returns {boolean}
     */
    userHasAnyRights: function (resource, rightNames) {
        var me = this,
            hasRight = false;

        if (Ext.isArray(rightNames)) {
            Ext.Array.each(rightNames, function (rightName) {
                var result;

                result = me.userHasRight(resource, rightName);

                if (result) {
                    hasRight = true;

                    return false; // остановка итерации Ext.Array.each
                }
            });
        }

        return hasRight;
    },

    /**
     * Возвращает true если у пользователя есть все перечисленные права на ресурс
     *
     * @param resource имя ресурска
     * @param rightNames - массив имен прав (create/read/update/delete)
     *
     * @returns {boolean}
     */
    userHasRights: function (resource, rightNames) {
        var me = this,
            hasRights = true;

        if (Ext.isArray(rightNames) && rightNames.length) {
            Ext.Array.each(rightNames, function (rightName) {
                var result;

                result = me.userHasRight(resource, rightName);

                if (!result) {
                    hasRights = false;

                    return false; // остановка итерации Ext.Array.each
                }
            });
        } else {
            hasRights = false;
        }

        return hasRights;
    },

    /**
     * Возвращает true если у пользователя есть полный набор прав на ресурс
     *
     * @param resource
     *
     * @returns {boolean}
     */
    userHasFullRights: function (resource) {
        return this.userHasRights(resource, ['create', 'read', 'update', 'delete']);
    },

    setRole: function (data) {
        localStorage.setItem('ud-role', JSON.stringify(data));
        Unidata.Config.role = data;
    },

    getToken: function () {
        return localStorage.getItem('ud-token');
    },

    setToken: function (token) {
        return localStorage.setItem('ud-token', token);
    },

    setLicense: function (license) {
        var expirationDate = null,
            version = null;

        if (license) {
            expirationDate = license.expirationDate || null;
            version = license.version || null;
        }

        localStorage.setItem('ud-license-expirationDate', expirationDate);
        localStorage.setItem('ud-license-version', version);

        return license;
    },

    getLicenseExpirationDate: function () {
        var expirationDate = localStorage.getItem('ud-license-expirationDate');

        if (expirationDate === 'null') {
            return null;
        }

        return expirationDate;
    },

    getLicenseVersion: function () {
        var version = localStorage.getItem('ud-license-version');

        if (version === 'null') {
            return null;
        }

        return version;
    },

    getForcePasswordChange: function () {
        return localStorage.getItem('forcePasswordChange');
    },

    setForcePasswordChange: function (change) {
        return localStorage.setItem('forcePasswordChange', change);
    },

    getUser: function () {
        return Unidata.Config.user;
    },

    setUser: function (data) {
        localStorage.setItem('ud-user', JSON.stringify(data));
        Unidata.Config.user = Ext.create('Unidata.model.user.User', data);
    },

    isUserAdmin: function () {
        var user,
            result = false;

        user = this.getUser();

        if (user && user.get('admin')) {
            result = true;
        }

        return result;
    },

    /**
     * Проверяет у пользователя наличие хотя бы одного из админских прав
     * @returns {boolean}
     */
    userHasAdminRights: function () {
        var me = this,
            hasAdminRights = false,
            adminRights = [
                this.RIGHT.ADMIN_CLASSIFIER_MANAGEMENT,
                this.RIGHT.ADMIN_DATA_MANAGEMENT,
                this.RIGHT.ADMIN_MATCHING_MANAGEMENT,
                this.RIGHT.ADMIN_SYSTEM_MANAGEMENT
            ];

        Ext.Array.each(adminRights, function (right) {
            if (me.userHasAnyRights(right, ['read', 'create', 'update', 'delete'])) {
                hasAdminRights = true;

                return false; // остановка итерации Ext.Array.each
            }
        });

        return hasAdminRights;
    },

    getUserBulkSelectionLimit: function () {
        if (this.userHasRights('BULK_OPERATIONS_OPERATOR', ['read'])) {
            return 0;
        } else {
            return this.getCustomerCfg()['BULK_SELECTION_LIMIT'];
        }
    },

    userHasUserDefinedRights: function () {
        return this.getUserDefinedRights().length > 0;
    },

    getUserDefinedRights: function () {
        var result = [],
            rights = this.getRole();

        if (!Ext.isArray(rights)) {
            return result;
        }

        Ext.Array.each(rights, function (right) {
            if (right &&
                right.securedResource &&
                right.securedResource.type === 'USER_DEFINED') {

                result.push(right);
            }
        });

        return result;

    },

    /**
     * Возвращает количество значимых знаков после запятой
     *
     * @returns {number}
     */
    getDecimalPrecision: function () {
        var precision = 2,
            customerCfg = this.getCustomerCfg();

        if (customerCfg.DECIMAL_PRECISION) {
            precision = customerCfg.DECIMAL_PRECISION;
        }

        return precision;
    },

    getTasksPollInterval: function () {
        var tasksPollInterval = 300 * Unidata.constant.Delay.SECOND,    // default value
            customerCfg = this.getCustomerCfg();

        if (customerCfg && !Ext.isEmpty(customerCfg.TASKS_POLL_INTERVAL)) {
            tasksPollInterval = customerCfg.TASKS_POLL_INTERVAL * Unidata.constant.Delay.SECOND;
        }

        return tasksPollInterval;
    },

    /**
     * Возвращает истину если у пользователя есть роль
     *
     * @param roleName - имя роли
     */
    userHasRole: function (roleName) {
        var result = false,
            roles,
            user;

        user = this.getUser();

        if (user) {
            roles = user.get('roles');

            if (Ext.Array.contains(roles, roleName)) {
                result = true;
            }
        }

        return result;
    },

    initLocale: function () {
        var thousandSeparator = ' ';

        Ext.util.Format.thousandSeparator = thousandSeparator;
    },

    getLocale: function () {
        var customerCfg = this.getCustomerCfg(),
            locale;

        locale = customerCfg.LOCALE ? customerCfg.LOCALE : 'ru';

        return locale;
    },

    /**
     * Максимальное количество отображаемое в карусельке на экране ориджинов
     *
     * @returns {number}
     */
    getOriginCarouselMaxCount: function () {
        var customerCfg = this.getCustomerCfg(),
            count = 100;

        if (customerCfg.ORIGIN_CAROUSEL_MAXCOUNT && Ext.isNumeric(customerCfg.ORIGIN_CAROUSEL_MAXCOUNT)) {
            count = Number(customerCfg.ORIGIN_CAROUSEL_MAXCOUNT);
        }

        return count;
    },

    /**
     * Количество записей видимых одномоментно на экране ориджинов
     *
     * @returns {number}
     */
    getOriginCarouselDisplayCount: function () {
        var customerCfg = this.getCustomerCfg(),
            count = 2;

        if (customerCfg.ORIGIN_CAROUSEL_DISPLAYCOUNT && Ext.isNumeric(customerCfg.ORIGIN_CAROUSEL_DISPLAYCOUNT)) {
            count = Number(customerCfg.ORIGIN_CAROUSEL_DISPLAYCOUNT);
        }

        return count;
    }
});
