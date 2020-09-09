/**
 * Радиогруппа выбора контекста выполнения правила качества
 *
 * @author Sergey Shishigin
 * @date 2018-05-17
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.ExecutionContextRadioGroup', {
    extend: 'Ext.form.RadioGroup',

    alias: 'widget.admin.entity.metarecord.dq.executioncontextradiogroup',

    columns: 2,

    config: {
        executionContext: null,
        supportedExecutionContexts: null
    },

    radioName: 'execution_context',

    referenceHolder: true,
    radioButtonGlobal: null,
    radioButtonLocal: null,

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
    },

    initReferences: function () {
        this.radionButtonGlobal = this.items.getAt(0);
        this.radionButtonLocal = this.items.getAt(1);
    },

    /**
     * Вычислить disabled свойства для radiobuttons
     *
     * @param supportedExecutionContexts
     * @return {{GLOBAL: boolean, LOCAL: boolean}}
     */
    computeRadioButtonsDisabled: function (supportedExecutionContexts) {
        var executionContextEnumList = Unidata.util.DataQuality.executionContextEnumList;

        if (!Ext.isArray(supportedExecutionContexts)) {
            return {GLOBAL: true, LOCAL: true};
        }

        return {GLOBAL: !Ext.Array.contains(supportedExecutionContexts, executionContextEnumList.GLOBAL),
                LOCAL: !Ext.Array.contains(supportedExecutionContexts, executionContextEnumList.LOCAL)};
    },

    initItems: function () {
        var items,
            radioName = this.radioName,
            executionContext,
            DataQualityUtil = Unidata.util.DataQuality,
            supportedExecutionContexts,
            radioButtonsDisabled;

        this.callParent(arguments);

        executionContext = this.getExecutionContext();
        supportedExecutionContexts = this.getSupportedExecutionContexts();
        radioButtonsDisabled = this.computeRadioButtonsDisabled(supportedExecutionContexts);

        items = [
            {
                reference: 'radioButtonGlobal',
                boxLabel: DataQualityUtil.executionContextLabels.GLOBAL,
                name: radioName,
                checked: executionContext === DataQualityUtil.executionContextEnumList.GLOBAL,
                inputValue: DataQualityUtil.executionContextEnumList.GLOBAL,
                disabled: radioButtonsDisabled.GLOBAL,
                margin: '0 10 0 0'
            },
            {
                reference: 'radioButtonLocal',
                boxLabel: DataQualityUtil.executionContextLabels.LOCAL,
                name: radioName,
                checked: executionContext === DataQualityUtil.executionContextEnumList.LOCAL,
                disabled: radioButtonsDisabled.LOCAL,
                inputValue: DataQualityUtil.executionContextEnumList.LOCAL
            }
        ];

        this.add(items);
    },

    updateSupportedExecutionContexts: function (supportedExecutionContexts) {
        var radioButtonsDisabled;

        if (this.rendered) {
            radioButtonsDisabled = this.computeRadioButtonsDisabled(supportedExecutionContexts);
            this.radionButtonGlobal.setDisabled(radioButtonsDisabled.GLOBAL);
            this.radionButtonLocal.setDisabled(radioButtonsDisabled.LOCAL);
        } else {
            this.on('render', function () {
                var radioButtonsDisabled;

                radioButtonsDisabled = this.computeRadioButtonsDisabled(supportedExecutionContexts);
                this.radionButtonGlobal.setDisabled(radioButtonsDisabled.GLOBAL);
                this.radionButtonLocal.setDisabled(radioButtonsDisabled.LOCAL);
            }, this, {single: true});
        }
    }
});
