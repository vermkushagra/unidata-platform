/**
 * Комбобокс с единицами измерения
 *
 * @author Sergey Shishigin
 * @date 2016-11-14
 */
Ext.define('Unidata.view.component.MeasurementUnitComboBox', {
    extend: 'Ext.form.field.ComboBox',

    xtype: 'un.measurementunitcombobox',

    mixins: [
        'Unidata.mixin.Tooltipable'
    ],

    config: {
        measurementValue: null,
        showTooltip: false
    },

    cls: 'un-measurementunit-combobox',
    displayField: 'shortName',
    valueField: 'id',
    autoSelect: false,
    editable: false,
    emptyText: Unidata.i18n.t('classifier>selectValue'),
    queryMode: 'local',

    initComponent: function () {
        this.callParent(arguments);
        this.initListeners();
    },

    initListeners: function () {
        this.on('change', this.onChange, this);
        this.on('render', this.onRenderInit, this);
    },

    onChange: function () {
        if (this.showTooltip) {
            this.refreshTooltipText();
        }
    },

    onRenderInit: function () {
        if (this.showTooltip) {
            this.refreshTooltipText();
        }
    },

    refreshTooltipText: function () {
        var tooltipText;

        tooltipText = this.buildTooltipText();

        this.setTooltipText(tooltipText);
    },

    buildTooltipText: function () {
        var tooltipText,
            measurementValue = this.getMeasurementValue(),
            measurementUnit  = null,
            value            = this.getValue(),
            measurementUnitName,
            measurementUnitDisplayName;

        if (!this.showTooltip) {
            return '';
        }

        if (!measurementValue) {
            return '';
        }

        if (value) {
            measurementUnit = this.findRecordByValue(value);
        }

        tooltipText = measurementValue.get('name');

        if (measurementUnit) {
            measurementUnitName        = measurementUnit.get('name');
            measurementUnitDisplayName = measurementUnit.get('shortName');
            tooltipText += Ext.String.format(': {0} ({1})', measurementUnitName, measurementUnitDisplayName);
        }

        return tooltipText;
    }
});
