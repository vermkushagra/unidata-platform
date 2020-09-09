/**
 * Поддержка диалога с пользователем
 *
 * @author Sergey Shishigin
 * @date 2016-01-14
 */

Ext.define('Unidata.util.UserDialog', {
    singleton: true,

    errorDetailText: Unidata.i18n.t('util>supportMessage'),

    /**
     * Показать ошибку
     *
     * Если errorDetails = null, то отображаем toast с кнопкой "Закрыть", который необходимо закрывать вручную
     * @param autoClose Признак автоматического закрытия
     * @param message Текстовое сообщение или массив сообщений
     * @param errorDetails Детали ошибки
     * @param severity
     */
    showError: function (message, autoClose, errorDetails, severity) {
        var opts,
            handlerDetails;

        autoClose = autoClose === undefined ? true : autoClose;
        message = message || Unidata.i18n.t('util>applicationError');

        if (errorDetails) {
            autoClose = false;
            handlerDetails = {
                title: Unidata.i18n.t('util>errorDetails'),
                handlerParams: {
                    handler: this.showErrorDetail,
                    args: [errorDetails],
                    scope: this
                }
            };
        }

        opts = {
            autoClose: autoClose
        };

        Unidata.util.UserDialog.showMessage(message, 'e', opts, handlerDetails, severity);
    },

    /**
     * Показать предупреждение
     *
     * @param message Текстовое сообщение или массив сообщений
     */
    showWarning: function (message, autoClose) {
        var opts;

        autoClose = autoClose === undefined ? true : autoClose;
        message = message || Unidata.i18n.t('util>applicationError');

        opts = {
            autoClose: autoClose
        };

        Unidata.util.UserDialog.showMessage(message, 'w', opts);
    },

    /**
     * Показать сообщение
     *
     * @param message Текстовое сообщение или массив сообщений
     * @param type Error ('e'), Warning ('w'), Default (info)
     * @param opts {autoClose}
     * @param handlerDetails
     *        title текст ссылки
     *        handlerParams {Object}
     *          handler {Function}
     *          args {Array}
     *          scope
     */
    showMessage: function (message, type, opts, handlerDetails, severity) {
        var autoClose = true,
            mainCls = 'un-notification-window',
            toastCfg,
            toast,
            typeStyle,
            messageHtml,
            html;

        toastCfg = {
            closeDelay: 10000
        }

        if (Ext.isArray(message)) {
            message = Ext.Array.htmlEncode(message);
        } else {
            message = Ext.String.htmlEncode(message);
        }

        // TODO: use enum
        severity = severity || 'NORMAL';

        if (Ext.isObject(opts)) {
            autoClose = opts.autoClose === 'undefined' ? false : opts.autoClose;
        }

        /**
         * Создать html-лист из сообщения
         *
         * @param message
         * @returns {string|*}
         */
        function createList (message) {
            // wrap messages array to an html list
            message = Ext.Array.map(message, function (msg) {
                return '<li>' + msg + '</li>';
            });
            messageHtml = '<ul>' + message.join('') + '</ul>';

            return messageHtml;
        }

        /**
         * Создать html для вывода в нотификационное окно
         *
         * @returns {*}
         */
        function createMessageHtml (message) {
            if (Ext.isArray(message)) {
                if (message.length > 1) {
                    messageHtml = createList(message);
                } else {
                    messageHtml = message[0];
                }
            } else {
                messageHtml = message;
            }

            return messageHtml;
        }

        /**
         * Сформировать стили на основании типа сообщения
         *
         * @param type
         * @returns {{icon: string, bodyCls: string}}
         */
        function getTypeStyle (type, mainCls, severity) {
            var typeStyle;

            typeStyle = {
                icon: '',
                cls: '',
                headerCls: '',
                mainCls: mainCls
            };

            switch (type) {
                case 'e':
                    typeStyle.icon = 'fa-exclamation-circle';
                    typeStyle.cls = mainCls + '-error';

                    if (severity) {
                        typeStyle.cls = typeStyle.cls + '-' + severity.toLowerCase();
                    }
                    break;
                case 'w':
                    typeStyle.icon = 'fa-exclamation-circle';
                    typeStyle.cls = mainCls + '-warning';
                    break;
                default :
                    typeStyle.icon = 'fa-check';
                    typeStyle.cls = mainCls + '-info';
            }

            return typeStyle;
        }

        /**
         * Создать html для вывода в нотификационное окно
         *
         * @param messageHtml
         * @param typeStyle
         * @returns {*}
         */
        function createHtml (messageHtml, typeStyle) {
            var iconHtml,
                template;

            iconHtml = '<i class="fa ' + typeStyle.icon + ' zoomIn animated"></i>';
            template = '<div class="un-toast-icon">{0}</div>' +
                '<div class="un-toast-msg">{1}</div>' +
                '<div class="un-toast-clear"></div>';
            html = Ext.String.format(template, iconHtml, messageHtml);

            return html;
        }

        messageHtml = createMessageHtml(message);
        typeStyle = getTypeStyle(type, mainCls, severity);

        // простое окно с информацией можно закрывать и время отображения меньше
        if (!type) {
            toastCfg.closable = true;
            toastCfg.closeDelay = 5000;
        }

        html = createHtml(messageHtml, typeStyle);
        toast = this.createToast(html, typeStyle, autoClose, handlerDetails, toastCfg);

        toast.show();
    },

    /**
     * Создать toast
     *
     * typeStyle:
     *  icon - иконка типа сообщения
     *  cls  - css-класс
     *  headerCls - css-класс заголовка
     *  mainCls - базовый css-класс
     *
     * @param html
     * @param typeStyle - настройка стилей для типа сообщения
     * @param autoClose - признак автоматического закрытия окна
     * @param handlerDetails - детали ошибки
     *      title текст ссылки
     *      handlerParams {Object}
     *        handler {Function}
     *        args {Array}
     *        scope
     * @returns {Ext.window.Toast|*}
     */
    createToast: function (html, typeStyle, autoClose, handlerDetails, toastCfg) {
        var toast,
            me = this,
            bottomToolbar,
            closeDelay = toastCfg.closeDelay || 10000,
            stickOnClick = false;

        function onToastShow (self) {
            var closeHref = bottomToolbar.lookupReference('closeHref');

            closeHref.setHandlerParams({
                handler: me.closeErrorWindow,
                scope: self,
                args: [self]
            });
            closeHref.configHrefHandler();
        }

        if (toastCfg.closable) {
            stickOnClick = true;
        }

        toast = Ext.create('Ext.window.Toast', {
            items: [
                {
                    xtype: 'container',
                    scrollable: 'vertical',
                    html: html
                }
            ],
            layout: 'fit',
            title: '',
            headerCls: typeStyle.headerCls,
            cls: [typeStyle.mainCls, typeStyle.cls],
            autoClose: autoClose,
            autoCloseDelay: closeDelay,
            stickOnClick: stickOnClick,
            width: 400,
            maxHeight: 500,
            closable: false,
            align: 'br',
            paddingX: 10,
            paddingY: 80
        });

        bottomToolbar = Ext.create('Ext.toolbar.Toolbar', {
            xtype: 'toolbar',
            dock: 'bottom',
            referenceHolder: true,
            items: []
        });

        if (handlerDetails) {
            bottomToolbar.add({
                xtype: 'un.hreflabel',
                reference: 'detailHref',
                width: 'auto',
                title: handlerDetails.title,
                handlerParams: handlerDetails.handlerParams
            });
        }

        if (!autoClose || toastCfg.closable) {
            bottomToolbar.add('->');
            bottomToolbar.add({
                xtype: 'un.hreflabel',
                reference: 'closeHref',
                width: 'auto',
                title: Unidata.i18n.t('common:close')
            });

            toast.on('show', onToastShow);
        }

        if (bottomToolbar.items.getCount() > 0) {
            toast.addDocked(bottomToolbar);
        }

        Ext.WindowManager.bringToFront(toast);

        return toast;
    },

    /**
     * Создать окно отображения детализированных ошибок
     *
     * @param errorDetailText
     * @returns {Ext.window.Window|*}
     */
    createErrorDetailWindow: function (errorDetailText, showHint) {
        var wnd;

        showHint = showHint === undefined ? true : showHint;

        function onToastShow (self) {
            var textarea = self.lookupReference('textarea');

            textarea.selectText();
            textarea.focus();
        }

        wnd = Ext.create('Ext.window.Window', {
            title: Unidata.i18n.t('util>errorDetails'),
            cls: 'un-error-detail-window',
            height: 500,
            width: 400,
            resizable: false,
            modal: true,
            referenceHolder: true,
            closeOnOutsideClick: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            listeners: {
                show: onToastShow.bind(wnd)
            },
            items: [
                {
                    xtype: 'textarea',
                    cls: 'un-error-detail-textarea',
                    value: errorDetailText,
                    reference: 'textarea',
                    resizable: false,
                    editable: false,
                    width: '100%',
                    flex: 1
                }
            ]
        });

        if (showHint) {
            wnd.insert(0, {
                xtype: 'label',
                cls: 'un-error-detail-info',
                width: 'auto',
                html: this.errorDetailText,
                height: 50
            });
        }

        return wnd;
    },

    /**
     * Отобразить детализированные ошибки
     *
     * errorDetails:
     *  errorUrl
     *  errorCode
     *  stackTrace
     *  userMessage
     *
     * @param errorDetails
     */
    showErrorDetail: function (errorDetails) {
        var wnd,
            errorDetailText,
            showHint;

        errorDetails = errorDetails || {};
        errorDetailText = createErrorDetailText(errorDetails);

        function createErrorDetailText (errorDetails) {
            var errorUrl   = errorDetails.errorUrl || '',
                errorCode  = errorDetails.errorCode || '',
                stackTrace = errorDetails.stackTrace || '',
                userMessage = errorDetails.userMessage || '',
                userMessageDetails = errorDetails.userMessageDetails || null,
                text;

            if (userMessageDetails) {
                // если необходимо вывести расширенное сообщение об ошибке бизнес-логики (http code 200)
                text = userMessageDetails;
            } else {
                // если необходимо вывести расширенное сообщение о внутренней ошибке сервера (http code 500)
                text = Unidata.i18n.t('util>errorCode') + errorCode;

                if (userMessage) {
                    text += '\n\n' + Unidata.i18n.t('util>message') + ': ' + userMessage;
                }

                if (errorUrl) {
                    text += '\n\nAPI URL: ' + errorUrl.replace(Unidata.Config.getMainUrl(), '');
                }

                if (stackTrace) {
                    text += '\n\nStack trace: ' + stackTrace;
                }
            }

            return text;
        }

        showHint = !errorDetails.userMessageDetails;

        wnd = this.createErrorDetailWindow(errorDetailText, showHint);

        wnd.show();
    },

    /**
     * Закрыть окно ошибки
     *
     * @param self
     */
    closeErrorWindow: function (self) {
        self.close();
    },

    /**
     * Показать prompt
     * @param title
     * @param msg
     * @param yesHandler            - обработчик положительного ответа
     * @param scope
     * @param btn                   - кнопка из которой вылетает prompt
     * @param yesHandlerArguments   - аргументы для обработчика положительного ответа
     * @param noHandler             - обработчик отрицательного ответа
     * @param noHandlerArguments    - аргументы для обработчика отрицательного ответа
     * @param viewConfig            - данные для отображения
     */
    showPrompt: function (title, msg, yesHandler, scope, btn, yesHandlerArguments, noHandler, noHandlerArguments, viewConfig) {
        var viewConfig = viewConfig || {},
            yesText = viewConfig.yesText || Unidata.i18n.t('common:yes'),
            noText = viewConfig.noText || Unidata.i18n.t('common:no'),
            message = viewConfig.html || msg,
            config,
            wnd;

        scope = scope || this;

        config = {
            title: title,
            message: message,
            buttons: Ext.MessageBox.YESNO,
            buttonText: {
                yes: yesText,
                no: noText
            },
            scope: this,
            defaultFocus: 3,
            fn: function (btn) {
                if (btn === 'yes') {
                    if (yesHandler) {
                        yesHandler.apply(scope, yesHandlerArguments);
                    }
                } else {
                    if (noHandler) {
                        noHandler.apply(scope, noHandlerArguments);
                    }
                }
            }
        };

        if (Boolean(btn)) {
            config.animateTarget = btn;
        }

        wnd = Ext.create('Ext.window.MessageBox', {
            // переопределяем стандартную функцию, т.к. нам необходимо управлять цветовой палитрой кнопок
            makeButton: function (btnIdx) {
                var btnId = this.buttonIds[btnIdx],
                    button,
                    buttonCfg;

                buttonCfg = {
                    handler: this.btnCallback,
                    itemId: btnId,
                    scope: this,
                    text: this.buttonText[btnId],
                    minWidth: 75
                };

                if (btnId === 'yes' && !Ext.isEmpty(viewConfig.yesBtnColor)) {
                    buttonCfg = Ext.apply(buttonCfg, {
                        color: viewConfig.yesBtnColor
                    });
                }

                if (btnId === 'no' && !Ext.isEmpty(viewConfig.noBtnColor)) {
                    buttonCfg = Ext.apply(buttonCfg, {
                        color: viewConfig.noBtnColor
                    });
                }

                button = new Ext.button.Button(buttonCfg);

                return button;
            }
        });

        wnd.show(config);
    },

    /**
     * Возвращает html верстку для отображения в диалоговом окне
     *
     * config = {
     *      iconHtml: Unidata.util.Icon.getLinearIcon(iconName) | Unidata.util.Icon.getAwesomeIcon(iconName) | 'html верстка'
            textHtml: 'html верстка текста сообщения'
     * }
     *
     * config = {
     *      iconHtml: iconHtml,               // html верстка блока с иконкой
            textHtml: textHtml                // html верстка блока с текстом
     * }
     *
     * @param config - конфиг для построения html диалогового окна
     * @returns {*}
     */
    buildDialogBodyHtml: function (config) {
        var iconTpl = new Ext.XTemplate('<div class="un-dialog-body-icon">{html}</div>'),
            textTpl = new Ext.XTemplate('<div class="un-dialog-body-text">{html}</div>'),
            resultTpl = new Ext.XTemplate('<div class="un-dialog-body">{icon} {text}</div>'),
            iconHtml = config.iconHtml,
            textHtml = config.textHtml,
            iconWrapper = '',
            textWrapper = '',
            result;

        if (!Ext.isEmpty(iconHtml)) {
            iconWrapper = iconTpl.apply({html: iconHtml});
        }

        if (!Ext.isEmpty(textHtml)) {
            textWrapper = textTpl.apply({html: textHtml});
        }

        result = resultTpl.apply({
            icon: iconWrapper,
            text: textWrapper
        });

        return result;
    },

    /**
     * Вспомогательная функция. Возвращает span с html
     *
     * @param html
     * @returns {*}
     */
    buildInlineBoldHtml: function (html) {
        var tpl = new Ext.XTemplate('<span style="font-weight: 600">{html}</span>');

        return tpl.apply({html: html});
    },

    /**
     * Вспомогательная функция. Возвращает div с html
     *
     * @param html
     * @returns {*}
     */
    buildBlockHtml: function (html) {
        var tpl = new Ext.XTemplate('<div style="line-height:  2.4">{html}</div>');

        return tpl.apply({html: html});
    }

});
