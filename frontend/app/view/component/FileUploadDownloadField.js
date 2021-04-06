/**
 * @author Aleksandr Bavin
 * @date 2016-07-04
 */
Ext.define('Unidata.view.component.FileUploadDownloadField', {
    extend: 'Ext.form.field.File',

    xtype: 'fileuploaddownloadfield',

    referenceHolder: true,

    cls: 'file-uploaddownload-field',
    msgTarget: 'under',

    config: {
        inputName: 'file',
        uploadParams: null,
        hideFileName: false // не отображать имя файла
    },

    maxSize: 60,

    /**
     * Утилита для загрузки файлов
     * @type Unidata.util.FileUploadDownload
     */
    fileUploadDownload: null,

    /**
     * @private
     */
    fileIsLoading: false,

    extensions: [],

    baseUrl: '',

    fileNameOnClick: null,  // обработчик клик по имени файла

    /**
     * @private
     */
    templates: null, // закэшированные темплейты urlTpl

    urlTpl: {
        upload: null,
        download: null,
        delete: null
    },

    buttonConfig: {
        text: '',
        ui: 'un-toolbar-admin',
        scale: 'small',
        iconCls: 'icon-file-search',   // TODO: change icon depends on type
        tooltip: Unidata.i18n.t('common:loadSomething', {name: Unidata.i18n.t('glossary:file')})
    },

    fieldSubTpl: [
        '<div id="{id}"></div>'
    ],

    fileNameTpl: [
        '<tpl if="!hideFileName">',
            '<tpl if="isLoading"><span class="isLoading">' + Unidata.i18n.t('common:fileLoadingEllipsis') + '</span></tpl>',
            '<tpl if="!isLoading">',
                '<tpl if="fileName">',
                    '<tpl if="url">',
                        '<a href="{url}">{fileName}</a>',
                    '<tpl else>',
                        '{fileName}',
                    '</tpl>',
                '</tpl>',
                '<tpl if="!fileName"><span class="un-no-data">' + Unidata.i18n.t('common:fileNotSelected') + '</span>&nbsp;</tpl>',
            '</tpl>',
        '</tpl>'
    ],
    // &nbsp; вставляется для случая сокрытия un-no-data, чтобы не менялась высота контейнера

    triggers: {
        filebutton: {
            type: 'component',
            hideOnReadOnly: true,
            preventMouseDown: false
        }
    },

    initComponent: function () {
        this.templates = {};

        this.fileUploadDownload = Ext.create('Unidata.util.FileUploadDownload', {
            inputName: this.inputName,
            listeners: {
                scope: this,
                uploadsuccess: this.onUploadsuccess,
                uploaderror: this.onUploaderror,
                uploadfinish: this.onUploadfinish
            }
        });

        this.callParent(arguments);
    },

    onRender: function () {
        this.callParent(arguments);

        this.fileInputEl.set({
            title: ' '
        });

        this.drawFileName();
    },

    /**
     * Отображает текущий файл
     */
    drawFileName: function () {
        var value = this.getValue(),
            fileNameTpl = this.getTpl('fileNameTpl'),
            elem,
            fileName;

        if (!this.rendered) {
            return;
        }

        fileName = fileNameTpl.apply({
            isLoading: this.isLoading(),
            fileName: value ? value.fileName : null,
            url: this.getDownloadUrl(),
            hideFileName: this.getHideFileName()
        });

        this.inputEl.setHtml(fileName);

        if (Ext.isFunction(this.fileNameOnClick)) {
            elem = this.inputEl.down('a');

            if (elem) {
                elem.on('click', this.fileNameOnClick);
            }
        }
    },

    showUploadError: function () {
        this.reset();
        // Unidata.showError('При загрузке файла произошла ошибка');
    },

    /**
     * Возвращает готовый для использования шаблон
     * @param urlTpl
     * @returns {Ext.XTemplate}
     */
    getUrlTemplate: function (urlTpl) {
        if (this.templates[urlTpl] === undefined) {
            this.templates[urlTpl] = new Ext.XTemplate(urlTpl);
        }

        return this.templates[urlTpl];
    },

    /**
     * Возвращает url, собранный по шаблону
     * @param urlTpl
     * @returns {string}
     */
    getUrl: function (urlTpl) {
        return this.baseUrl + this.getUrlTemplate(urlTpl).apply(this);
    },

    getUploadUrl: function () {
        return this.getUrl(this.urlTpl.upload);
    },

    getDownloadUrl: function () {
        return this.urlTpl.download ? this.getUrl(this.urlTpl.download) : false;
    },

    isLoading: function () {
        return this.fileIsLoading;
    },

    /**
     * @private
     */
    setIsLoading: function (flag) {
        this.fileIsLoading = flag;
        this.setTriggersDisabled(flag);
        this.drawFileName();
    },

    setTriggersDisabled: function (flag) {
        Ext.Object.each(this.triggers, function (key, trigger) {
            if (trigger.component) {
                trigger.component.setDisabled(flag);
            }
        }, this);
    },

    /**
     * Проверяет расширение для файла, который мы выбрали
     * @param selectedFile
     * @returns {boolean}
     */
    checkExtension: function (selectedFile) {
        var fileExtension;

        // если нет ограничений
        if (this.extensions.length == 0) {
            return true;
        }

        fileExtension = selectedFile.substring(selectedFile.lastIndexOf('.') + 1);

        if (Ext.Array.indexOf(this.extensions, fileExtension) === -1) {
            return false;
        }

        return true;
    },

    //=========== переопределённые методы ===========

    applyTriggers: function (triggers) {
        triggers.remove = {
            type: 'component',
            component: {
                xtype: 'button',
                ui: 'un-toolbar-admin',
                scale: 'small',
                iconCls: 'icon-trash2',
                tooltip: Unidata.i18n.t('common:deleteSomething', {name: Unidata.i18n.t('glossary:file')}),
                text: '',
                listeners: {
                    scope: this,
                    click: 'onRemoveClick'
                }
            }
        };

        return this.callParent(arguments);
    },

    /**
     * Используется при валидации
     */
    getRawValue: function () {
        var value = this.getValue();

        if (!value) {
            return null;
        }

        if (!value.id) {
            return null;
        }

        return value.id;
    },

    didValueChange: function (newVal, oldVal) {
        return !this.isEqual(newVal, oldVal);
    },

    /**
     * Сравниваем объекты
     */
    isEqual: function (value1, value2) {
        return Ext.Object.equals(value1, value2);
    },

    setValue: function (value) {
        var result = this.mixins.field.setValue.call(this, value);

        this.baseTooltip = value ? value.fileName : '';

        // перерисовываем шаблон
        this.drawFileName();

        return result;
    },

    getValue: function () {
        return this.mixins.field.getValue.call(this);
    },

    //=========== обработчики ===========

    onFileChange: function (button, e, value) {
        var msg;

        if (this.checkExtension(value)) {

            if (msg = this.checkSizeMsg()) {
                Unidata.showError(msg);

                return;
            }

            this.setIsLoading(true);
            this.fireEvent('uploadstart', this);
            this.fileUploadDownload.uploadFiles(
                this,
                this.getUploadUrl(),
                this.getUploadParams()
            );
        } else {
            if (value === '') {
                return;
            }

            this.setActiveError(
                Unidata.i18n.t('admin.cleanseFunction>selectFileOfCorrectType', {correctTypes: this.extensions.join(', ')})
            );
            this.fireEvent('extensionerror', this, true);
        }
    },

    onRemoveClick: function () {
        this.setValue(null);
    },

    onUploadsuccess: function (success, errors, content) {
        if (success) {
            this.setValue(content);
        } else {
            this.showUploadError();
        }
    },

    onUploaderror: function () {
        this.showUploadError();
    },

    onUploadfinish: function () {
        this.setIsLoading(false);
    },

    getFileName: function () {
        var value = this.getValue(),
            fileName;

        fileName = value ? value.fileName : '';

        return fileName;
    }

});
