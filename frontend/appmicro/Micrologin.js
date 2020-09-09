window['Unidata'] = window['Unidata'] || {};

Unidata.Micrologin = (function () {
    var Micrologin;

    Micrologin = {
        dom: null,

        loginTab: null,
        warningTab: null,

        nameInput: null,
        passwordInput: null,
        nameInputWrap: null,
        passwordInputWrap: null,

        loginButton: null,
        localeSwitch: null,
        localeMenu: null,

        logoContainer: null,

        wrongAuthorization: null,

        licenseeWarningTitle: null,
        licenseeWarningText: null,
        licenseeBackButton: null,

        focusCls: 'focused',
        bodyCls: 'un-login-body',

        authenticateData: null, // данные по авторизации
        locale: 'ru',

        licenseInvalidErrorCode: null,
        licenseeWarningTextTranslate: null,
        licenseeHardwareSecurityTextTranslate: null,

        /**
         * Свойство для переопределения кастомером
         *
         * @returns {string|*}
         */
        platformFaviconUrl: 'resources/favicon.ico?v=6',

        /**
         * Свойство для переопределения кастомером
         *
         * @returns {string|*}
         */
        platformTitle: 'Unidata',

        translates: {
            ru: {
                signInButton: 'Войти',
                namePlaceholder: 'Имя пользователя',
                passwordPlaceholder: 'Пароль',
                licenseeWarningTitle: 'ВНИМАНИЕ!',
                licenseeWarningText: 'Срок действия лицензии истек',
                licenseeHardwareSecurityText: 'UUID установленной лицензии не совпадает с UUID сервера Юнидата',
                licenseeBackButton: 'Назад',
                wrongAuthorizationText: 'Неправильный логин или пароль.<br>Повторите еще раз.',
                unknownError: 'Произошла неизвестная ошибка'
            },
            en: {
                signInButton: 'Login',
                namePlaceholder: 'Username',
                passwordPlaceholder: 'Password',
                licenseeWarningTitle: 'ATTENTION!',
                licenseeWarningText: 'The license has expired',
                licenseeHardwareSecurityText: 'UUID of the installed license does not match the UUID of the server',
                licenseeBackButton: 'Back',
                wrongAuthorizationText: 'Incorrect login or password. <br> Please try again.',
                unknownError: 'Unknown error occured'
            }
        },

        initComponent: function () {
            var template = document.querySelector('#logintpl'),
                fragment;

            if (!template) {
                return;
            }

            fragment = $(template.innerHTML);
            fragment.appendTo($('body'));

            $('body').addClass(this.bodyCls);

            fragment.addClass(this.getPlatformBackgroundCls());
            fragment.addClass(this.getBackgroundCls());

            this.dom = fragment.get(0);

            this.initComponentReference();
            this.initComponentEvent();

            this.initCustomization();

            $(this.loginTab).show();
            $(this.warningTab).hide();
            $(this.wrongAuthorizationText).hide();

            $(this.logoContainer).append(Unidata.Micrologin.getLogoTpl());

            this.disableLoginButton();

            if (window.customerConfig && window.customerConfig.LOCALE) {
                this.setLocale(window.customerConfig.LOCALE);
            }

            this.updatePlatformFavicon();
            this.updatePlatformTitle();
        },

        destroyComponent: function () {
            $('body').removeClass(this.bodyCls);
            $(this.dom).remove();
        },

        initComponentReference: function () {
            var fragment = $(this.dom);

            if (!fragment) {
                return;
            }

            this.loginTab = fragment.find('.un-login-tab').get(0);
            this.warningTab = fragment.find('.un-licenseeexpired-tab').get(0);

            this.nameInput = fragment.find('input[type=text]').get(0);
            this.passwordInput = fragment.find('input[type=password]').get(0);

            this.nameInputWrap = fragment.find('.form-group:has(input[type=text])').get(0);
            this.passwordInputWrap = fragment.find('.form-group:has(input[type=password])').get(0);

            this.localeSwitch = fragment.find('.un-local-switch').get(0);
            this.localeMenu = $(this.localeSwitch).find('.menu').get(0);

            this.logoContainer = fragment.find('.logo-container').get(0);

            this.wrongAuthorizationText = $(this.loginTab).find('[role=wrong-authorization-text]').get(0);
            this.loginButton = $(this.loginTab).find('.btn').get(0);

            this.licenseeWarningTitle = $(this.warningTab).find('[role=warning-title]').get(0);
            this.licenseeWarningText = $(this.warningTab).find('[role=warning-text]').get(0);
            this.licenseeBackButton = $(this.warningTab).find('[role=licencee-back-btn]').get(0);
        },

        initComponentEvent: function () {
            $('body').bind('click', this.onBodyClick.bind(this));

            $(this.nameInput).bind('focus', this.onNameInputFocus.bind(this));
            $(this.nameInput).bind('blur', this.onNameInputBlur.bind(this));

            $(this.passwordInput).bind('focus', this.onPasswordInputFocus.bind(this));
            $(this.passwordInput).bind('blur', this.onPasswordInputBlur.bind(this));

            $(this.nameInput).bind('keydown', this.onNameInputKeyDown.bind(this));
            $(this.passwordInput).bind('keydown', this.onPasswordInputKeyDown.bind(this));

            $(this.nameInput).bind('change paste keydown', this.onNameInputChange.bind(this));
            $(this.passwordInput).bind('change paste keydown', this.onPasswordInputChange.bind(this));

            $(this.loginButton).bind('click', this.onLoginButtonClick.bind(this));
            $(this.licenseeBackButton).bind('click', this.onLicenseeBackButtonClick.bind(this));

            $(this.localeSwitch).find('.menu-wrap a').bind('click', this.onLocaleSwitchItemClick.bind(this));
            $(this.localeSwitch).bind('click', this.onLocaleSwitchClick.bind(this));
        },

        /**
         * Точка входа
         */
        runMicrologin: function () {
            var sso = Unidata.Micrologin.getQueryParam('sso');

            // параметр sso == true, означает интеграцию кастомной авторизацией
            // и отправлять запрос авторизации отправлять нужно в любом случае
            if (sso === 'true' || sso === 'on') {
                Unidata.Micrologin.doSsoAuthorization();
                // возможна авторизация по токену
            } else if (Unidata.Micrologin.getToken()) {
                Unidata.Micrologin.doTokenAuthorization();
            } else {
                Unidata.Micrologin.initComponent();
            }
        },

        /**
         * Стартуер загрузку приложения
         */
        runMicroloader: function () {
            Unidata.Microloader.runMicroloader();
        },

        /**
         * Метод для переопределения кастомером
         *
         * @returns {string|*}
         */
        initCustomization: function () {
        },

        /**
         * Метод для переопределения кастомером
         *
         * @returns {string|*}
         */
        getLogoTpl: function () {
            var locale = this.locale,
                html;

            html = '<object class="un-login-logo" data="resources/logoplatform-' + locale + '.svg" type="image/svg+xml" style="width: 200px;">' +
                '<img src="resources/logoplatform-' + locale + '.png" alt="Platform logo" />' +
                '</object>';

            return html;
        },

        /**
         * Метод для переопределения кастомером
         *
         * @returns {string|*}
         */
        getPlatformBackgroundCls: function () {
            var cls = 'un-application-usermode';

            switch (window.customerConfig.APP_MODE) {
                case 'user':
                    cls = 'un-application-usermode';
                    break;
                case 'admin':
                    cls = 'un-application-adminmode';
                    break;
                case 'dev':
                    cls = 'un-application-devmode';
                    break;
            }

            return cls;
        },

        /**
         * Метод для переопределения кастомером
         *
         * @returns {string|*}
         */
        getBackgroundCls: function () {
            return '';
        },

        /**
         * Обновляет favicon сайта
         */
        updatePlatformFavicon: function () {
            var head = document.head || document.getElementsByTagName('head')[0],
                element = document.querySelectorAll('link[rel="icon"]')[0],
                url = this.platformFaviconUrl;

            if (element) {
                head.removeChild(element);
            }

            element = document.createElement('link');
            element.href = url;
            element.rel = 'icon';
            element.type = 'image/x-icon';

            head.appendChild(element);
        },

        /**
         * Метод для переопределения кастомером
         *
         * @returns {*}
         */
        transformResourceBundle: function (language, namespace, data) {
            return data;
        },

        /**
         * Обновляет заголовок вкладки сайта
         */
        updatePlatformTitle: function () {
            var element = document.querySelectorAll('title')[0];

            if (element) {
                element.innerHTML = this.platformTitle;
            }
        },

        onBodyClick: function () {
            $(this.localeMenu).hide();
        },

        onLocaleSwitchClick: function (e) {
            $(this.localeMenu).toggle();
            e.stopPropagation();
        },

        onNameInputFocus: function () {
            $(this.nameInputWrap).addClass(this.focusCls);
        },

        onNameInputBlur: function () {
            $(this.nameInputWrap).removeClass(this.focusCls);
        },

        onPasswordInputFocus: function () {
            $(this.passwordInputWrap).addClass(this.focusCls);
        },

        onPasswordInputBlur: function () {
            $(this.passwordInputWrap).removeClass(this.focusCls);
        },

        onLoginButtonClick: function () {
            this.doLogin();
        },

        onLicenseeBackButtonClick: function () {
            $(this.loginTab).show();
            $(this.warningTab).hide();
        },

        onNameInputChange: function (e) {
            this.updateLoginButtonDisable();

            if (e.keyCode !== 13) {
                this.animateWrongAuthorizationText();
            }
        },

        onPasswordInputChange: function (e) {
            this.updateLoginButtonDisable();

            if (e.keyCode !== 13) {
                this.animateWrongAuthorizationText();
            }
        },

        onNameInputKeyDown: function (e) {
            if (e.keyCode === 9 || e.keyCode === 13) {
                e.stopPropagation();
                e.preventDefault();
            }

            switch (e.keyCode) {
                case 9:
                    $(this.passwordInput).focus();
                    $(this.passwordInput).select();
                    break;
                case 13:
                    this.doLogin();
                    break;
            }
        },

        onPasswordInputKeyDown: function (e) {
            if (e.keyCode === 9 || e.keyCode === 13) {
                e.stopPropagation();
                e.preventDefault();
            }

            switch (e.keyCode) {
                case 9:
                    $(this.nameInput).focus();
                    $(this.nameInput).select();
                    break;
                case 13:
                    this.doLogin();
                    break;
            }
        },

        onLocaleSwitchItemClick: function (e) {
            var locale = e.target.getAttribute('locale');

            if (!locale) {
                return;
            }

            this.setLocale(locale);
        },

        /**
         * Анимирует текст неуспешной авторизации
         */
        animateWrongAuthorizationText: function () {
            if ($(this.wrongAuthorizationText).is(':visible')) {
                $(this.wrongAuthorizationText).stop().animate({opacity: '0.3'}, 500);
            }
        },

        /**
         * Устанавливает локаль
         *
         * @param locale
         */
        setLocale: function (locale) {
            var translate = this.getTranslate(locale);

            if (!translate) {
                return;
            }

            $(this.loginButton).html(translate.signInButton);
            $(this.nameInput).attr('placeholder', translate.namePlaceholder);
            $(this.passwordInput).attr('placeholder', translate.passwordPlaceholder);

            $(this.licenseeWarningTitle).text(translate.licenseeWarningTitle);
            $(this.wrongAuthorizationText).html(translate.wrongAuthorizationText);
            $(this.licenseeBackButton).html(translate.licenseeBackButton);

            this.locale = locale;
            this.licenseeWarningTextTranslate = translate.licenseeWarningText;
            this.licenseeHardwareSecurityTextTranslate = translate.licenseeHardwareSecurityText;
            $(this.licenseeWarningText).html(this.buildLicenseeWarningText());

            $(this.localeMenu).find('a').removeClass('selected');
            $(this.localeMenu).find('a[locale=' + locale + ']').addClass('selected');

            $(this.logoContainer).empty();
            $(this.logoContainer).append(Unidata.Micrologin.getLogoTpl());
        },

        formatDate: function (date, locale) {
            var year = date.getFullYear(),
                month = String(date.getMonth() + 1).padStart(2, '0') ,
                day = String(date.getDate()).padStart(2, '0'),
                hours = String(date.getHours()).padStart(2, '0'),
                minutes = String(date.getMinutes()).padStart(2, '0'),
                dateText;

            if (locale === 'ru') {
                dateText = year + '-' + month + '-' + day + ' ' + hours + ':' + minutes;
            } else if (locale === 'en') {
                dateText = month + '/' + day + '/' + year + ' ' + hours + ':' + minutes;
            }

            return dateText;
        },

        /**
         * Возвращает токен авторизации
         *
         * @returns {string}
         */
        getToken: function () {
            var token = localStorage.getItem('ud-token');

            if (token === 'null') {
                token = null;
            }

            return token;
        },

        /**
         * Запоминает токен авторизации
         *
         * @param token
         */
        setToken: function (token) {
            if (token) {
                localStorage.setItem('ud-token', token);
            } else {
                localStorage.removeItem('ud-token');
            }
        },

        /**
         * Возвращает объект с текстами переводов для переданной локали / либо возвращается перевод для текущей локали
         *
         * @param locale
         * @returns {*}
         */
        getTranslate: function (locale) {
            if (!locale) {
                locale = this.locale;
            }

            return this.translates[locale];
        },

        /**
         * Возвращает значение get параметра
         *
         * @param param
         * @returns {*}
         */
        getQueryParam: function (param) {
            var found = null;

            window.location.search.substr(1).split('&').forEach(function (item) {
                if (param ==  item.split('=')[0]) {
                    found = item.split('=')[1];
                }
            });

            return found;
        },

        getAuthorizationInputData: function () {
            var data;

            data = {
                login: $(this.nameInput).val(),
                password: $(this.passwordInput).val()
            };

            return data;
        },

        updateLoginButtonDisable: function () {
            var data = this.getAuthorizationInputData();

            if (data.login.length && data.password.length) {
                this.enableLoginButton();
            } else {
                this.disableLoginButton();
            }
        },

        disableLoginButton: function () {
            $(this.loginButton).attr('disabled', true);
        },

        enableLoginButton: function () {
            $(this.loginButton).attr('disabled', false);
        },

        /**
         * Выполняет авторизацию с данными введенными пользователем
         */
        doLogin: function () {
            var me = this,
                authData = this.getAuthorizationInputData(),
                promise;

            if (!String(authData.login).length || !String(authData.password).length) {
                return;
            }

            promise = this.login(authData.login, authData.password, this.locale);
            promise
                .done(function (data) {
                    this.authenticateData = data;
                    this.setToken(data.token);

                    window.customerConfig.LOCALE = data.userInfo.locale;

                    $(this.wrongAuthorizationText).hide();

                    this.destroyComponent();

                    this.runMicroloader();
                }.bind(this))
                .fail(function (data) {
                    var handled = false;

                    this.authenticateData = null;
                    this.setToken(null);

                    if (data && data.errors) {
                        data.errors.forEach(function (item) {
                            if (item.errorCode === 'EX_SECURITY_LICENSE_INVALID' || item.errorCode === 'EX_SECURITY_HW_FOR_LICENSE_INVALID') {
                                me.licenseInvalidErrorCode = item.errorCode;

                                if (item.errorCode === 'EX_SECURITY_LICENSE_INVALID') {
                                    // при таком коде ошибки бекенд присылает дату истечения лицензии в секции userMessageDetails
                                    this.licenseeExpireDate = Date.parse(item.userMessageDetails, this.dateTimeFormat);

                                    if (this.licenseeExpireDate) {
                                        this.licenseeExpireDate = new Date(this.licenseeExpireDate);
                                    }
                                }

                                $(this.licenseeWarningText).html(this.buildLicenseeWarningText());

                                $(this.loginTab).hide();
                                $(this.warningTab).show();

                                handled = true;
                            } else if (item.errorCode === 'EX_SECURITY_CANNOT_LOGIN') {
                                $(this.wrongAuthorizationText).show();
                                $(this.wrongAuthorizationText).stop().css({opacity: '1'});

                                handled = true;
                            }
                        }.bind(this));
                    }

                    if (!handled) {
                        this.handleXhrErrors(data);
                    }
                }.bind(this));
        },

        buildLicenseeWarningText: function () {
            var licenseeHardwareSecurityTextTranslate = this.licenseeHardwareSecurityTextTranslate,
                licenseeWarningTextTranslate = this.licenseeWarningTextTranslate,
                licenseeExpireDate = this.licenseeExpireDate,
                txt;

            switch (this.licenseInvalidErrorCode) {
                case 'EX_SECURITY_LICENSE_INVALID':
                    txt = licenseeWarningTextTranslate;

                    if (licenseeExpireDate) {
                        txt = licenseeWarningTextTranslate + '<br/>' + this.formatDate(licenseeExpireDate, this.locale);
                    }
                    break;
                case 'EX_SECURITY_HW_FOR_LICENSE_INVALID':
                    txt = licenseeHardwareSecurityTextTranslate;
                    break;
            }

            return txt;
        },

        /**
         * Выполняет авторизацию пользователя по токену
         */
        doTokenAuthorization: function () {
            var token = this.getToken(),
                promise;

            promise = this.authenticate(token);
            promise
                .done(function (data) {
                    this.authenticateData = data;
                    this.setToken(data.token);
                    this.setLocale(data.userInfo.locale);

                    window.customerConfig.LOCALE = data.userInfo.locale;

                    this.runMicroloader();
                }.bind(this))
                .fail(function (xhr) {
                    this.authenticateData = null;
                    this.setToken(null);

                    this.initComponent();

                    if (xhr.status !== 401) {
                        this.handleXhrErrors(xhr.responseJSON);
                    }
                }.bind(this));
        },

        /**
         * Выполняет авторизацию пользователя с учетом авторизации интегратора
         */
        doSsoAuthorization: function () {
            var login = '',
                password = '',
                promise;

            promise = this.login(login, password, this.locale);
            promise
                .done(function (data) {
                    this.authenticateData = data;
                    this.setToken(data.token);

                    this.runMicroloader();
                }.bind(this))
                .fail(function () {
                    this.authenticateData = null;
                    this.setToken(null);

                    this.initComponent();
                }.bind(this));
        },

        login: function (login, password, locale) {
            var deferred = $.Deferred(),
                data;

            data = {
                password: password || '',
                userName: login || '',
                locale: locale || ''
            };

            $.ajax({
                type: 'POST',
                url: Unidata.Microloader.buildServerUrl('internal/authentication/login'),
                data: JSON.stringify(data),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function (data, textStatus, xhr) {
                    if (xhr && xhr.status === 200 && data && data.success) {
                        deferred.resolve(data.content);
                    } else {
                        deferred.reject(xhr.responseJSON);
                    }
                },
                error: function (xhr) {
                    deferred.reject(xhr.responseJSON);
                }
            });

            return deferred.promise();
        },

        authenticate: function (token) {
            var deferred = $.Deferred();

            $.ajax({
                type: 'GET',
                url: Unidata.Microloader.buildServerUrl('internal/authentication/get-current-user'),
                data: {
                    '_dc': Number(new Date())
                },
                headers: {
                    'Authorization': token
                },
                success: function (data, textStatus, xhr) {
                    if (xhr && xhr.status === 200 && data) {
                        deferred.resolve(data);
                    } else {
                        deferred.reject(xhr.responseJSON);
                    }
                },
                error: function (xhr) {
                    deferred.reject(xhr);
                }
            });

            return deferred.promise();
        },

        /**
         * Загружает кастомизацию для логин формы
         *
         * @param data
         * @param nextCallback
         */
        loadMicrologinCustomization: function (data, nextCallback) {
            var url = Unidata.Microloader.buildCustomerUrl('CUX/Micrologin.js');

            Unidata.Microloader.loadScript(url,
                function () {
                    if (Unidata.Microloader.isFunction(nextCallback)) {
                        nextCallback();
                    }
                },
                function () {
                    // для данного метода нам безразлично что этот файл не загружен т.к. его технически может не быть и значит
                    // поведние не переопределено
                    if (Unidata.Microloader.isFunction(nextCallback)) {
                        nextCallback();
                    }
                }
            );
        },

        /**
         * Обрабатывает ошибочные запросы с BE
         *
         * @param data
         */
        handleXhrErrors: function (data) {
            var msgs = [],
                translate = this.getTranslate(),
                msg = translate.unknownError;

            if (data && data.errors) {
                data.errors.forEach(function (item) {
                    if (item.userMessage) {
                        msgs.push(item.userMessage);
                    }
                }.bind(this));
            }

            if (msgs.length) {
                msg = msgs.join('<br>');
            }

            iziToast.show({
                message: msg,
                timeout: null,
                progressBar: false,
                icon: 'icon-notification-circle',
                class: 'un-error',
                transitionIn: null,
                transitionOut: null,
                transitionInMobile: null,
                transitionOutMobile: null
            });
        }
    };

    return Micrologin;
})();
