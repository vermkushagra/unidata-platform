/**
 * @author Aleksandr Bavin
 * @date 2017-07-04
 */
Ext.define('Unidata.view.steward.dataimport.wizard.step.TemplateStep', {

    extend: 'Unidata.view.steward.dataimport.wizard.DataImportStep',

    requires: [
        'Unidata.view.steward.dataimport.wizard.step.UploadFileStep'
    ],

    alias: 'widget.dataimport.wizard.template',

    // controller: 'dataimport.wizard.settings',
    // viewModel: 'dataimport.wizard.settings',

    title: Unidata.i18n.t('search>wizard.prepareData'),

    nextStep: {
        xtype: 'dataimport.wizard.uploadfile',
        stepAllowed: true
    },

    initItems: function () {
        this.callParent(arguments);

        this.add({
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'center'
            },
            items: [
                {
                    xtype: 'container',
                    html: Unidata.i18n.t('search>wizard.needDownloadTemplate'),
                    margin: '0 0 10 0'
                },
                {
                    xtype: 'button',
                    scale: 'small',
                    text: Unidata.i18n.t('search>wizard.downloadTemplate'),
                    listeners: {
                        click: 'downloadTemplate',
                        scope: this
                    }
                }
            ]
        });
    },

    downloadTemplate: function () {
        var url  = Unidata.Config.getMainUrl() + 'internal/export/data/template/' + this.getEntityName(),
            downloadConfig;

        downloadConfig = {
            method: 'GET',
            url: url,
            params: {
                token: Unidata.Config.getToken()
            }
        };

        Unidata.util.DownloadFile.downloadFile(downloadConfig);
    }

});
