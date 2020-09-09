/**
 * Форма в модальном окне,
 * отправлеят события submitstart и submitend
 * @author Aleksandr Bavin
 * @date 2016-08-31
 */
Ext.define('Unidata.view.component.form.WindowForm', {
    extend: 'Ext.window.Window',

    alias: 'widget.form.window',

    formParams: null, // параметры формы

    padding: 10,
    draggable: false,
    resizable: false,
    modal: true,
    width: 500,
    referenceHolder: true,

    formView: null, // форма

    config: {
        closeOnSubmitSuccess: true,
        closeOnSubmitFailed: true
    },

    dockedItems: {
        xtype: 'toolbar',
        reference: 'toolbar',
        ui: 'footer',
        dock: 'bottom',
        layout: {
            pack: 'center'
        },
        defaults: {
            minWidth: 75
        },
        items: []
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initForm();
        this.initToolbar();
    },

    onDestroy: function () {
        this.callParent(arguments);

        delete this.formView;
    },

    initToolbar: function () {
        var form = this.lookupReference('form'),
            toolbar = this.lookupReference('toolbar');

        toolbar.add(
            {
                xtype: 'button',
                text: 'OK',
                qaId: 'okButton',
                scope: this,
                handler: function () {
                    form.submit();
                }
            },
            {
                xtype: 'button',
                text: Unidata.i18n.t('common:cancel', {context: 'noun'}),
                qaId: 'cancelButton',
                scope: this,
                handler: function () {
                    this.close();
                }
            }
        );
    },

    initForm: function () {
        if (!this.formParams) {
            return;
        }

        this.formView = this.add(Ext.Object.merge({
            xtype: 'form',
            reference: 'form',
            defaults: {
                labelAlign: 'top',
                width: '100%'
            }
        }, this.formParams));

        this.formView.on('beforeaction', this.onBeforeaction, this);
        this.formView.on('actioncomplete', this.onActioncomplete , this);
        this.formView.on('actionfailed', this.onActionfailed , this);
    },

    /**
     * Если форма с файлом, то отправляем её при помощи утильного класса,
     * т.к. при стандартной отправке, возникают трудности с кроссдоменностью
     * @param {Ext.form.Panel} form
     */
    onBeforeaction: function (form) {
        var view = this,
            params = form.baseParams || {},
            formFieldValues,
            fileUploadDownload,
            fileField;

        if (!form.isValid()) {
            return false;
        }

        if (!view.fireEvent('submitstart', form)) {
            return false;
        }

        view.setLoading(Unidata.i18n.t('common:submitForm'));

        formFieldValues = form.getFieldValues();

        form.getFields().each(function (field) {
            var fieldName = field.getName();

            if (fieldName && field.isFileUpload()) {
                fileField = field;
                delete formFieldValues[fieldName];
            }
        });

        params = Ext.Object.merge(params, formFieldValues);

        if (fileField === undefined) {
            return true;
        }

        fileUploadDownload = Ext.create('Unidata.util.FileUploadDownload');

        fileUploadDownload.on('uploadsuccess', this.onUploadsuccess, this);
        fileUploadDownload.on('uploaderror', this.onUploaderror, this);

        fileUploadDownload.uploadFiles(
            fileField,
            form.url,
            params
        );

        return false;
    },

    onActioncomplete: function () {
        this.submitEnd(true);
    },

    onActionfailed: function () {
        this.submitEnd(false);
    },

    onUploadsuccess: function (success, errors) {
        this.submitEnd(success);

        if (errors && errors.length) {
            Ext.Array.each(errors, function (error) {
                Unidata.showError(error);
            });
        }
    },

    onUploaderror: function () {
        this.submitEnd(false);
    },

    submitEnd: function (success) {
        var formView = this.formView,
            view = this;

        view.fireEvent('submitend', formView, success);
        view.setLoading(false);

        if (this.getCloseOnSubmitSuccess() && success) {
            view.close();
        }

        if (this.getCloseOnSubmitFailed() && !success) {
            view.close();
        }
    }

});
