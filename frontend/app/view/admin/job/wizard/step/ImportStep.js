/**
 * Шаг выбора файла для импорта. Визард импорта / экспорта списка операций (jobs)
 *
 * @author Ivan Marshalkin
 * @date 2018-03-19
 */

Ext.define('Unidata.view.admin.admin.job.wizard.step.ImportStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.admin.job.wizard.step.ImportConfirmStep'
    ],

    alias: 'widget.jobimportexport.wizard.importstep',

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    scrollable: 'vertical',

    config: {},

    title: Unidata.i18n.t('admin.job>wizard>importStepTitle'),

    bodyPadding: '8 50 8 50',

    items: [],

    nextStep: {
        xtype: 'jobimportexport.wizard.importconfirmstep'
    },

    initComponent: function () {
        this.callParent(arguments);
    },

    onDestroy: function () {
        this.callParent(arguments);

        this.fileField = null;
        this.checkbox = null;
    },

    initItems: function () {
        var file,
            warning;

        this.callParent(arguments);

        this.fileField = file = Ext.widget({
            xtype: 'filefield',
            flex: 1,
            name: 'file',
            emptyText: Unidata.i18n.t('admin.metamodel>fileFormat', {type: 'JSON'}),
            allowBlank: true,
            regex: /^.*\.(json|JSON)$/,
            regexText: Unidata.i18n.t('admin.metamodel>selectFileOfCorrectType', {correctTypes: 'json'}),
            msgTarget: 'under',
            buttonText: Unidata.i18n.t('search>wizard.selectFile'),
            grow: true,
            growMin: 200,
            listeners: {
                change: this.onFileChange,
                validitychange: this.onFileValiditychange,
                scope: this
            }
        });

        warning = Ext.widget({
            xtype: 'warning-message',
            iconHtml: Unidata.util.Icon.getLinearIcon('warning'),
            text: Unidata.i18n.t('admin.job>wizard>overrideSameNameOperations'),
            padding: 0 // default padding в компоненте Step переопределяет paddings, объявленные внутри child-компонента
        });

        this.add([
            {
                xtype: 'container',
                layout: 'hbox',
                items: [
                    file
                ]
            },
            warning
        ]);
    },

    onFileValiditychange: function () {
    },

    onFileChange: function (field) {
        field.validate();
        this.refreshNextStepButtonState();
    },

    /**
     * Обновляет доступность кнопки для перехода на следующий шаг
     */
    refreshNextStepButtonState: function () {
        var nextStep = this.getNextStep();

        if (this.fileField.isValid() && !Ext.isEmpty(this.fileField.getValue())) {
            nextStep.setStepAllowed(true);
        } else {
            nextStep.setStepAllowed(false);
        }
    }
});
