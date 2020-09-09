/**
 * Точка расширения для конфигурирования формы авторизации
 *
 * @author Ivan Marshalkin
 * @date 2017-10-18
 */

Ext.define('Unidata.uiuserexit.overridable.UnidataPlatform', {
    singleton: true,

    platformFaviconUrl: 'resources/favicon.ico?v=6',
    platformTitle: 'Unidata',

    /**
     * Возвращает html для контейнера с логотипами
     *
     * @returns {*}
     */
    getLogoPlatformHtml: function () {
        var locale = Unidata.Config.getLocale(),
            tpl,
            data;

        tpl = new Ext.XTemplate(
            '<div style="text-align: center">',
            '<object class="un-login-logo" data="{urlSvg}" type="image/svg+xml">',
            '<img src="{urlPng}" alt="Platform logo" />',
            '</object>',
            '</div>'
        );

        data = {
            urlSvg: 'resources/logoplatform-' + locale + '.svg',
            urlPng: 'resources/logoplatform-' + locale + '.png'
        };

        return tpl.apply(data);
    },

    /**
     * Возвращает html для контейнера в меню с текстом о платформе
     *
     * @returns {Array|[string,string,string]}
     */
    getMainMenuPlatformTextTpl: function () {
        var locale = Unidata.Config.getLocale(),
            tpl,
            data;

        tpl = new Ext.XTemplate(
            '<object data="resources/main-menu-logo-text-{locale}.svg" type="image/svg+xml">',
            '</object>'
        );

        data = {
            locale: locale
        };

        return tpl.apply(data);
    },

    /**
     * Возвращает html для контейнера в меню с иконкой платформы
     *
     * @returns {Array|[string,string,string]}
     */
    getMainMenuPlatformIconTpl: function () {
        var tpl;

        tpl = [
            '<span class="{iconCls}">',
            '<object data="resources/main-menu-logo-sign.svg" type="image/svg+xml">',
            '</object>',
            '</span>'
        ];

        return tpl;
    },

    /**
     * Обновляет favicon сайта
     */
    updatePlatformFavicon: function () {
        var head = document.head || document.getElementsByTagName('head')[0],
            element = document.querySelectorAll('link[rel="icon"]')[0],
            url = this.platformFaviconUrl;

        // <link rel="icon" type="image/x-icon" href="resources/favicon.ico?v=6"/>
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
     * Обновляет заголовок вкладки сайта
     */
    updatePlatformTitle: function () {
        var element = document.querySelectorAll('title')[0];

        if (element) {
            element.innerHTML = this.platformTitle;
        }
    }
});
