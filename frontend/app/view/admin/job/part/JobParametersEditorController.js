/**
 * Контроллер компонента для редактирования парметров операции
 *
 * Есть особенность работы модели - при сохранении операции список моделей параметров полностью обновляется
 * и все модели заменяются на новые.
 *
 * @author Cyril Sevastyanov
 * @date 2016-03-14
 */

Ext.define('Unidata.view.admin.job.part.JobParametersEditorController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.job.editor.parameters',

    init: function () {

        var me = this,
            view,
            viewModel;

        me.callParent(arguments);

        me.paramsForm = me.lookupReference('paramsForm');

        view = me.getView();
        viewModel = me.getViewModel();

        viewModel.bind('{job}', me.onChangeJob, me);
        viewModel.bind('{jobMetaRecord}', me.onJobMetaChanged, me);
    },

    refreshViewDelayed: function () {
        this.getView().setLoading(true);
        clearTimeout(this.refreshViewTimer);
        this.refreshViewTimer = Ext.defer(this.refreshView, 50, this);
    },

    /**
     * Рендерит заново список параметров
     */
    refreshView: function () {
        var me = this,
            paramsForm = me.paramsForm,
            view = me.getView(),
            job = me.getJob(),
            jobMeta = me.getJobMeta(),
            fields = [];

        paramsForm.removeAll();

        if (!job || !jobMeta) {
            me.fields = paramsForm.add(fields);
            view.setLoading(false);

            return;
        }

        job.parameters().each(function (parameter) {

            fields.push({
                xtype: 'admin.job.param',
                parameter: parameter,
                value: parameter.getValue(),
                width: '100%',
                bind: {
                    readOnly: '{readOnly}'
                },
                margin: '0 0 5 0'
            });

        });

        me.fields = paramsForm.add(fields);

        me.updateLabelsWidth();

        view.setLoading(false);
    },

    updateLabelsWidth: function () {
        var fields = this.fields,
            count = fields.length,
            maxWidth = 0;

        if (count && !fields[count - 1].rendered) {
            fields[count - 1].on({
                afterrender: {
                    fn: this.updateLabelsWidth,
                    scope: this,
                    single: true
                }
            });

            return;
        }

        fields.forEach(function (field) {
            var width = field.getLabelWidth();

            if (width > maxWidth) {
                maxWidth = width;
            }
        });

        fields.forEach(function (field) {
            field.setLabelWidth(maxWidth);
        });
    },

    showValidationError: function (fieldName, errorMessage) {

        var field = this.paramsForm.getForm().findField(fieldName);

        if (!field) {
            return;
        }

        field.setActiveError(errorMessage);
    },

    /**
     * @returns {Unidata.model.job.Job}
     */
    getJob: function () {
        return this.job;
    },

    /**
     * @returns {Unidata.model.job.JobMeta}
     */
    getJobMeta: function () {
        return this.getViewModel().get('jobMetaRecord');
    },

    /**
     * Срабатывает, если начинаем редактировать/просматривать другую операцию
     */
    onChangeJob: function () {

        if (this.job) {
            this.job.parameters().un('datachanged', this.onParametersLoad, this);
        }

        this.job = this.getViewModel().get('job');

        if (this.job) {
            this.job.parameters().on('datachanged', this.onParametersLoad, this);
        }

        this.refreshViewDelayed();
    },

    /**
     * Срабатывает, если тип операции операция
     *
     * @param meta
     */
    onJobMetaChanged: function () {
        this.refreshViewDelayed();
    },

    /**
     * Срабатывает при изменении списка параметров
     */
    onParametersLoad: function () {
        this.refreshViewDelayed();
    }

});
