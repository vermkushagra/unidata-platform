/**
 * Класс реализует скачивание файла через форму
 *
 * При необходимости скачивания файла через ajax POST запрос можно использовать следующие библиотеки:
 *
 * https://github.com/eligrey/FileSaver.js
 * https://github.com/eligrey/Blob.js
 *
 * @author Ivan Marshalkin
 * @date 2016-01-22
 */

/*
 * Пример использования
 *
 *   downloadConfig = {
 *       // параметры                      // значения по умолчанию
 *       url: url,                         // всегда берется из конфига
 *       method: method,                   // 'POST'
 *       target: target,                   // '_self'
 *       headers: headers,                 // {}
 *       params: params,                   // {}
 *       standartForm: standartForm,       // true
 *       jsonSubmit: jsonSubmit            // true
 *   };
 *
 *   Unidata.util.DownloadFile.downloadFile(downloadConfig);
 */

Ext.define('Unidata.util.DownloadFile', {
    requires: [
        'Ext.form.Panel',                     // используем этот класс
        'Ext.form.action.StandardSubmit',     // не явная зависимость => без нее ошибка
        'Ext.form.action.Submit'              // не явная зависимость => без нее ошибка
    ],

    singleton: true,

    /**
     * Скачивает файл c настройками по переданному конфигу
     *
     * Свойства конфига по умолчанию
     *
     * config = {
     *     method: 'POST',
     *     target: '_self',
     *     headers: {},
     *     params: {},
     *     standartForm: true,
     *     jsonSubmit: true
     * };
     *
     * Допустимые значения конфига:
     *
     * method: 'GET' | 'POST'
     * target: '_blank' | '_self' | '_parent' | '_top' | framename
     * standartForm: true | false
     * jsonSubmit: true | false
     *
     * @param config
     */
    downloadFile: function (config) {
        var url,
            method,
            params,
            target,
            headers,
            form,
            standartForm,
            jsonSubmit;

        config = config || {};

        url          = config.url;
        method       = config.method || 'POST';
        params       = config.params || {};
        target       = config.target || '_self';
        headers      = config.headers || {};
        standartForm = config.hasOwnProperty('standartForm') ? Boolean(config.standartForm) : true;
        jsonSubmit   = config.hasOwnProperty('jsonSubmit')   ? Boolean(config.jsonSubmit)   : true;

        form = Ext.create('Ext.form.Panel', {
            // свойство jsonSubmit работает только если отправка производится standardSubmit = false
            jsonSubmit: jsonSubmit,
            standardSubmit: standartForm,
            url: url,
            method: method
        });

        // делаем submit для начала скачивания файла
        form.submit({
            target: target,
            // headers отправляется только, если отправка производится AJAX запросом (standardSubmit = false)
            // в случае отправки элементом формы form (standardSubmit = true) отправить headers не возможно
            headers: headers,
            params: params
        });

        // очищаем форму т.к. ее уже больше не возможно будет использовать
        Ext.defer(function () {
            form.close();
        }, 100);
    },

    /**
     * Скачивает файл по готовой ссылке.
     * Открытие новой вкладки не производится.
     * Сам запрос отображается на вкладке network в консоле разработчика для текущей страницы
     *
     * @param url - ссылка для скачивания файла
     */
    downloadFileByUrl: function (url) {
        window.location.href = url;
    }
});
