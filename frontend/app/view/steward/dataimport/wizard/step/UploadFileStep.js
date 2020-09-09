/**
 * @author Aleksandr Bavin
 * @date 2017-07-04
 */
Ext.define('Unidata.view.steward.dataimport.wizard.step.UploadFileStep', {

    extend: 'Unidata.view.steward.dataimport.wizard.DataImportStep',

    requires: [
    ],

    alias: 'widget.dataimport.wizard.uploadfile',

    title: Unidata.i18n.t('common:import'),

    createDockedButtons: function () {
        return [
            {
                xtype: 'button',
                text: Unidata.i18n.t('common:back'),
                reference: 'prevButton',
                color: 'transparent',
                listeners: {
                    click: 'onPrevClick'
                }
            },
            {
                xtype: 'container',
                flex: 1
            },
            {
                xtype: 'button',
                text: Unidata.i18n.t('common:import', {context: 'verb'}),
                reference: 'confirmButton',
                disabled: true,
                bind: {
                    disabled: '{!confirmCheckbox.value}'
                },
                listeners: {
                    click: this.onConfirmButtonClick,
                    scope: this
                }
            }
        ];
    },

    initComponent: function () {
        this.callParent(arguments);

        this.fileUploadDownload = Ext.create('Unidata.util.FileUploadDownload', {
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
    },

    initItems: function () {
        this.callParent(arguments);

        this.fileField = Ext.widget({
            xtype: 'filefield',
            width: 384,
            name: 'file',
            allowBlank: false,
            regex: /^.*\.(xlsx|XLSX)$/,
            regexText: Unidata.i18n.t('admin.cleanseFunction>selectFileOfCorrectType', {correctTypes: 'xlsx'}),
            msgTarget: 'under',
            buttonText: Unidata.i18n.t('search>wizard.selectFile'),
            listeners: {
                change: this.onFileChange,
                validitychange: this.onFileValiditychange,
                scope: this
            }
        });

        this.add({
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [
                {
                    xtype: 'container',
                    margin: '0 0 10 0',
                    html: Unidata.i18n.t('search>wizard.selectPrevStepTemplate') + '<br/>' +
                    Unidata.i18n.t('glossary:resultsWillAvailableInNotification')
                },
                this.fileField
            ]
        });
    },

    onFileValiditychange: function (field, isValid) {
        var confirmButton = this.lookupReference('confirmButton');

        confirmButton.setDisabled(!isValid);
    },

    onFileChange: function (field) {
        field.validate();
    },

    onConfirmButtonClick: function () {
        var url = Unidata.Config.getMainUrl() + 'internal/import/data/xlsx';

        if (!this.fileField.isValid()) {
            return;
        }

        this.fireEvent('blockwizard');

        this.fileUploadDownload.uploadFiles(
            this.fileField,
            url,
            {
                importParams: Ext.JSON.encode({
                    sourceSystem: this.getSourceSystem(),
                    entityName: this.getEntityName(),
                    mergeWithPreviousVersion: this.getMergeWithPreviousVersion()
                })
            },
            true
        );
    },

    onUploadsuccess: function () {
        Unidata.showMessage(Unidata.i18n.t('search>wizard.dataHandlingProcess'));

        this.finish();
    },

    onUploaderror: function () {
    },

    onUploadfinish: function () {
        this.fireEvent('unblockwizard');
    }

});
