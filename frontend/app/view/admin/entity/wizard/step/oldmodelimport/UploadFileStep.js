/**
 * @author Aleksandr Bavin
 * @date 2017-03-17
 */
Ext.define('Unidata.view.admin.entity.wizard.step.oldmodelimport.UploadFileStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    alias: 'widget.admin.entity.wizard.step.oldmodelimport.uploadfile',

    title: Unidata.i18n.t('common:fileLoading'),

    items: [],

    /**
     * Утилита для загрузки файлов
     * @type Unidata.util.FileUploadDownload
     */
    fileUploadDownload: null,

    initComponent: function () {

        this.fileUploadDownload = Ext.create('Unidata.util.FileUploadDownload', {
            inputName: 'file',
            listeners: {
                scope: this,
                uploadsuccess: this.onUploadsuccess,
                uploaderror: this.onUploaderror,
                uploadfinish: this.onUploadfinish
            }
        });

        this.callParent(arguments);
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.fileUploadDownload.destroy();
        this.fileUploadDownload = null;
        this.fileField = null;
        this.checkbox = null;
        this.loadingMask = null;
    },

    initItems: function () {
        var file,
            checkbox;

        this.callParent(arguments);

        this.lookupReference('confirmButton').setDisabled(true);

        this.fileField = file = Ext.widget({
            xtype: 'filefield',
            width: '100%',
            name: 'file',
            regex: /^.*\.(xml|XML)$/,
            regexText: Unidata.i18n.t('admin.metamodel>selectFileOfCorrectType', {correctTypes: 'xml'}),
            msgTarget: 'under',
            buttonText: Unidata.i18n.t('admin.metamodel>selectFile', {extension: 'xml'}),
            grow: true,
            growMin: 200,
            listeners: {
                change: this.onFileChange,
                scope: this
            }
        });

        this.checkbox = checkbox = Ext.widget({
            xtype: 'checkbox',
            boxLabel: Unidata.i18n.t('admin.metamodel>overrideModelByImported'),
            name: 'recreate'
        });

        this.add([
            file,
            checkbox
        ]);
    },

    onFileChange: function (file) {
        var confirmButton = this.lookupReference('confirmButton');

        if (file.isValid()) {
            confirmButton.setDisabled(false);
        } else {
            confirmButton.setDisabled(true);
        }
    },

    onUploadsuccess: function (success, errors) {
        this.loadingMask.destroy();

        if (success) {
            this.showReloadWindow();
            this.finish();
        }
    },

    showReloadWindow: function () {
        var msg = Unidata.i18n.t(
            'admin.metamodel>metaModelSuccessfully',
            {action: this.checkbox.getValue() ? Unidata.i18n.t('admin.metamodel>recreated') : Unidata.i18n.t('admin.metamodel>updated')}
        );

        Ext.create('Ext.window.Window', {
            title: Unidata.i18n.t('admin.metamodel>importModel'),
            html: msg,
            modal: true,
            closable: false,
            bodyPadding: 10,
            minWidth: 250,
            minHeight: 120,
            dockedItems: {
                xtype: 'toolbar',
                reference: 'toolbar',
                ui: 'footer',
                dock: 'bottom',
                layout: {
                    pack: 'center'
                },
                items: {
                    xtype: 'button',
                    text: 'OK',
                    scope: this,
                    handler: function () {
                        window.location.reload();
                    }
                }
            }
        }).show();
    },

    onUploaderror: function () {
        this.loadingMask.destroy();
    },

    onUploadfinish: function () {
    },

    onConfirmButtonClick: function () {
        var url = Unidata.Config.getMainUrl() + 'internal/meta/model',
            recreate = this.checkbox.getValue();

        if (!this.fileField.isValid()) {
            return;
        }

        this.loadingMask = new Ext.LoadMask({
            msg: Unidata.i18n.t('admin.metamodel>importMetadata'),
            target: Unidata.getApplication().getActiveView(),
            style: {
                zIndex: 99999
            }
        });

        this.loadingMask.show();

        this.fileUploadDownload.uploadFiles(this.fileField, url, {recreate: recreate});
    },

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
                listeners: {
                    click: this.onConfirmButtonClick,
                    scope: this
                }
            }
        ];
    }

});
