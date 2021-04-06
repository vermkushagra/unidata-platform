/**
 * @author Aleksandr Bavin
 * @date 2017-01-31
 */
Ext.define('Unidata.view.admin.entity.wizard.step.modelimport.UploadFileStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.entity.wizard.step.modelimport.SettingsStep'
    ],

    alias: 'widget.admin.entity.wizard.step.modelimport.uploadfile',

    title: Unidata.i18n.t('common:fileLoading'),

    items: [],

    initComponent: function () {
        this.callParent(arguments);

        this.setNextStep({
            xtype: 'admin.entity.wizard.step.modelimport.settings'
        });

        this.fileUploadDownload = Ext.create('Unidata.util.FileUploadDownload', {
            inputName: 'modelFile',
            listeners: {
                scope: this,
                uploadsuccess: this.onUploadsuccess,
                uploaderror: this.onUploaderror,
                uploadfinish: this.onUploadfinish
            }
        });
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.fileUploadDownload.destroy();
        this.fileUploadDownload = null;
        this.fileField = null;
        this.checkbox = null;
    },

    initItems: function () {
        var file,
            checkbox;

        this.callParent(arguments);

        this.fileField = file = Ext.widget({
            xtype: 'filefield',
            flex: 1,
            name: 'modelFile',
            allowBlank: false,
            regex: /^.*\.(zip|ZIP)$/,
            regexText: Unidata.i18n.t('admin.metamodel>selectFileOfCorrectType', {correctTypes: 'zip'}),
            msgTarget: 'under',
            buttonText: Unidata.i18n.t('admin.metamodel>selectFile', {extension: 'zip'}),
            grow: true,
            growMin: 200,
            listeners: {
                change: this.onFileChange,
                validitychange: this.onFileValiditychange,
                scope: this
            }
        });

        this.checkbox = checkbox = Ext.widget({
            xtype: 'checkbox',
            boxLabel: Unidata.i18n.t('admin.metamodel>overrideModelByImported'),
            name: 'override',
            listeners: {
                change: this.onCheckboxChange,
                scope: this
            }
        });

        this.add([
            {
                xtype: 'container',
                layout: 'hbox',
                items: [
                    file,
                    {
                        xtype: 'button',
                        text: Unidata.i18n.t('common:load'),
                        reference: 'confirmButton',
                        margin: '0 0 0 10',
                        disabled: true,
                        listeners: {
                            click: this.onConfirmButtonClick,
                            scope: this
                        }
                    }
                ]
            },
            checkbox
        ]);
    },

    onCheckboxChange: function () {
        this.fieldValuesChangeCheck();
    },

    onFileValiditychange: function (field, isValid) {
        var confirmButton = this.lookupReference('confirmButton');

        confirmButton.setDisabled(!isValid);
    },

    onFileChange: function (field) {
        this.fieldValuesChangeCheck();
        field.validate();
    },

    onConfirmButtonClick: function () {
        var url = Unidata.Config.getMainUrl() + 'internal/meta/model-ie/upload',
            override = this.checkbox.getValue();

        if (!this.fileField.isValid()) {
            return;
        }

        this.getNextStep().setStepAllowed(false);

        this.fireEvent('blockwizard');

        this.setLoading(true);

        this.fileUploadDownload.uploadFiles(this.fileField, url, {override: override});
    },

    /**
     * Проверяет, изменились ли поля формы, после последнй успешной загрузки
     */
    fieldValuesChangeCheck: function () {
        var nextStep = this.getNextStep();

        if (this.lastCheckboxValue === this.checkbox.getValue() &&
            this.lastFileFieldValue === this.fileField.getValue()) {
            nextStep.setStepAllowed(true);
        } else {
            nextStep.setStepAllowed(false);
        }
    },

    onUploadsuccess: function (success, errors, result) {
        var nextStep = this.getNextStep();

        if (!success) {
            nextStep.setStepAllowed(false);
        } else {
            this.lastCheckboxValue = this.checkbox.getValue();
            this.lastFileFieldValue = this.fileField.getValue();

            nextStep.setSettingsData(result);
            nextStep.setStepAllowed(true);
        }
    },

    onUploaderror: function () {
        var nextStep = this.getNextStep();

        nextStep.setStepAllowed(false);
    },

    onUploadfinish: function () {
        this.fireEvent('unblockwizard');

        this.setLoading(false);
    }

});
