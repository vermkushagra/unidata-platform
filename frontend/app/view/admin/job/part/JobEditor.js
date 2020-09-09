/**
 * Компонент для редактирования операции
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-11
 */

Ext.define('Unidata.view.admin.job.part.JobEditor', {

    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.job.part.JobEditorController',
        'Unidata.view.admin.job.part.JobEditorModel',
        'Unidata.view.admin.job.part.JobParametersEditor',

        'Unidata.view.admin.job.part.JobTrigger'
    ],

    alias: 'widget.admin.job.editor',

    controller: 'admin.job.editor',

    viewModel: {
        type: 'admin.job.editor'
    },

    referenceHolder: true,

    jobTriggerPanel: null,

    rbar: [
        {
            xtype: 'button',
            ui: 'un-toolbar-admin',
            scale: 'small',
            iconCls: 'icon-floppy-disk',
            tooltip: Unidata.i18n.t('common:save'),
            reference: 'saveButton',
            handler: 'onSaveClick',
            bind: {
                hidden: '{readOnly}'
            }
        },
        '->',
        {
            xtype: 'button',
            ui: 'un-toolbar-admin',
            scale: 'small',
            iconCls: 'icon-trash2',
            tooltip: Unidata.i18n.t('common:delete'),
            margin: '0 0 80 0',
            handler: 'onDeleteClick',
            bind: {
                hidden: '{!deleteAllowed}'
            }
        }
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'form',
            reference: 'jobForm',
            title: Unidata.i18n.t('admin.job>commonSettings'),
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            bodyPadding: 10,
            items: [
                {
                    xtype: 'checkbox',
                    name: 'enabled',
                    fieldLabel: Unidata.i18n.t('admin.job>active'),
                    reference: 'activeCheckbox',
                    bind: {
                        value: '{job.enabled}',
                        // hidden: '{nameEditable}',
                        readOnly: '{!allowChangeActiveFlag}'
                    },
                    msgTarget: 'under'
                },
                {
                    xtype: 'combo',
                    name: 'jobNameReference',
                    fieldLabel: Unidata.i18n.t('admin.job>type'),
                    modelValidation: true,
                    bind: {
                        value: '{job.jobNameReference}',
                        store: '{jobMetaStore}',
                        readOnly: '{!nameEditable}'
                    },
                    displayField: 'name',
                    valueField: 'id',
                    editable: false,
                    msgTarget: 'under'
                },
                {
                    xtype: 'textfield',
                    name: 'name',
                    fieldLabel: Unidata.i18n.t('glossary:designation'),
                    modelValidation: true,
                    bind: {
                        value: '{job.name}',
                        editable: '{!readOnly}'
                    },
                    msgTarget: 'under'
                },
                {
                    xtype: 'textarea',
                    name: 'description',
                    fieldLabel: Unidata.i18n.t('glossary:description'),
                    modelValidation: true,
                    bind: {
                        value: '{job.description}',
                        editable: '{!readOnly}'
                    },
                    msgTarget: 'under'
                },
                {
                    xtype: 'textfield',
                    name: 'cronExpression',
                    modelValidation: true,
                    fieldLabel: Unidata.i18n.t('admin.job>cronExpression'),
                    bind: {
                        value: '{job.cronExpression}',
                        editable: '{!readOnly}'
                    },
                    msgTarget: 'under'
                }
            ]
        },
        {
            xtype: 'admin.job.editor.parameters',
            reference: 'parametersGrid',
            title: Unidata.i18n.t('admin.job>parameters'),
            bodyPadding: 10
        },
        {
            xtype: 'admin.job.editor.trigger',
            reference: 'jobTriggerPanel',
            title: Unidata.i18n.t('admin.job>nextOperationLaunch'),
            bind: {
                editable: '{!readOnly}'
            }
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
    },

    initReferences: function () {
        this.jobTriggerPanel = this.lookupReference('jobTriggerPanel');
    },

    /**
     * @param {Unidata.model.job.Job} job
     */
    setJob: function (job) {

        this.getController().setJob(job);

        return this;
    },

    /**
     * @returns {Unidata.model.job.Job}
     */
    getJob: function () {
        return this.getController().getJob();
    }

});
