/**
 * @author Aleksandr Bavin
 * @date 2017-01-31
 */
Ext.define('Unidata.view.admin.entity.wizard.step.modelexport.ConfirmStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    alias: 'widget.admin.entity.wizard.step.modelexport.confirm',

    title: Unidata.i18n.t('common:confirmation'),

    items: {
        html: Unidata.i18n.t('admin.metamodel>exportDescription')
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
        this.runExport();
    },

    runExport: function () {
        Unidata.showMessage(Unidata.i18n.t('admin.metamodel>startExportProcess'));

        Ext.Ajax.unidataRequest({
            url: Unidata.Config.getMainUrl() + 'internal/meta/model-ie/export',
            method: 'POST',
            headers: {
                'Accept':       'application/json',
                'Content-Type': 'application/json'
            },
            // jsonData: Ext.util.JSON.encode({
            //     storageId: 1
            // }),
            success: function () {
            }
        });

        this.finish();
    }

});
