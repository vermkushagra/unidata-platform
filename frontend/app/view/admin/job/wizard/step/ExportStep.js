/**
 * Шаг запуска операции экспорта. Визард импорта / экспорта списка операций (jobs)
 *
 * @author Ivan Marshalkin
 * @date 2018-03-19
 */

Ext.define('Unidata.view.admin.admin.job.wizard.step.ExportStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
    ],

    alias: 'widget.jobimportexport.wizard.exportstep',

    referenceHolder: true,

    config: {},

    title: Unidata.i18n.t('admin.job>wizard>confirmStepTitle'),

    layout: {
        type: 'vbox',
        align: 'middle',
        pack: 'center'
    },

    bodyPadding: '0 16 8 16',

    items: [
        {
            xtype: 'component',
            html: Unidata.i18n.t('admin.job>wizard>exportDescription'),
            style: {
                'text-align': 'center'
            },
            width: 380
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
    },

    initComponentEvent: function () {
    },

    onDestroy: function () {
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
        this.runExport();
    },

    runExport: function () {
        var ids = null,
            jsonData = 'null';

        Unidata.showMessage(Unidata.i18n.t('admin.metamodel>startExportProcess'));

        if (ids) {
            jsonData = Ext.util.JSON.encode(ids);
        }

        Ext.Ajax.unidataRequest({
            url: Unidata.Config.getMainUrl() + 'internal/jobs/export-jobs',
            method: 'POST',
            headers: {
                'Accept':       'application/json',
                'Content-Type': 'application/json'
            },
            jsonData: jsonData
        });

        this.finish();
    }
});
