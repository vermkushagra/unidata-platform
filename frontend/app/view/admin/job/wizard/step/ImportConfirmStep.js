/**
 * Шаг загрузки файла для импорта. Визард импорта / экспорта списка операций (jobs)
 *
 * @author Ivan Marshalkin
 * @date 2018-03-20
 */

Ext.define('Unidata.view.admin.admin.job.wizard.step.ImportConfirmStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
    ],

    alias: 'widget.jobimportexport.wizard.importconfirmstep',

    referenceHolder: true,

    config: {},

    layout: {
        type: 'vbox',
        align: 'middle',
        pack: 'center'
    },

    bodyPadding: '0 16 8 16',

    title: Unidata.i18n.t('admin.job>wizard>confirmStepTitle'),

    items: [
        {
            xtype: 'component',
            html: Unidata.i18n.t('admin.job>wizard>importDescription'),
            style: {
                'text-align': 'center'
            },
            width: 380
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.fileUploadDownload = Ext.create('Unidata.util.FileUploadDownload', {
            inputName: 'file',
            listeners: {
                scope: this,
                uploadsuccess: this.onUploadSuccess,
                uploaderror: this.onUploadError,
                uploadfinish: this.onUploadFinish
            }
        });
    },

    onDestroy: function () {
        this.fileUploadDownload.destroy();
        this.fileUploadDownload = null;

        this.callParent(arguments);
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
                text: Unidata.i18n.t('common:confirm'),
                reference: 'confirmButton',
                listeners: {
                    click: this.onConfirmButtonClick,
                    scope: this
                }
            }
        ];
    },

    onConfirmButtonClick: function () {
        var prevStep = this.getPrevStep(),
            url = Unidata.Config.getMainUrl() + 'internal/jobs/import-jobs';

        this.fireEvent('blockwizard');

        this.setLoading(true);

        this.fileUploadDownload.uploadFiles(prevStep.fileField, url);
    },

    onUploadSuccess: function (success) {
        if (success) {
            this.finish();

            Unidata.showMessage(Unidata.i18n.t('admin.job>wizard>importSuccessMessage'));
        }
    },

    onUploadError: function () {
    },

    onUploadFinish: function () {
        this.fireEvent('unblockwizard');

        this.setLoading(false);
    }
});
