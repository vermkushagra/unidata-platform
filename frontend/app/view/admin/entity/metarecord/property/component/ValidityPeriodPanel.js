/**
 * Компонент представления панели задания границ временного периода
 *
 * author: Sergey Shishigin
 */
// TODO: Использовать custom configs only (?)
Ext.define('Unidata.view.admin.entity.metarecord.property.component.ValidityPeriodPanel', {
    extend: 'Ext.container.Container',

    xtype: 'validityperiodpanel',

    referenceHolder: true,
    cls: 'un-validity-period-panel',

    dateFieldStart: null,
    dateFieldEnd: null,

    // настройки для date controls в виде объектов {start: ..., end: ...}
    config: {
        fieldLabels: null,
        labelWidths: null,
        fieldNames: null,
        binds: null,
        emptyTexts: null,
        customConfigs: null,
        globalDateLimits: {
            MIN: null,
            MAX: null
        }
    },

    /**
     * Создать date fields: start, end
     */
    createDateFields: function () {
        var dateFieldCfg,
            fieldLabels,
            labelWidths,
            fieldNames,
            binds,
            startCfg,
            endCfg,
            dateFields,
            emptyTexts;

        // init defaults
        fieldLabels = {
            start: Unidata.i18n.t('admin.metamodel>periodStart'),
            end: Unidata.i18n.t('admin.metamodel>periodEnd')
        };

        labelWidths = {
            start: null,
            end: null
        };

        fieldNames = {
            start: 'validityPeriodStart',
            end: 'validityPeriodEnd'
        };

        binds = {
            start: null,
            end: null
        };

        dateFields = {
            start: null,
            end: null
        };

        emptyTexts = {
            start: '- \u221E',
            end: '+ \u221E'
        };

        // rewrite defaults by a config if it is possible
        if (this.config.fieldLabels) {
            fieldLabels.start = this.config.fieldLabels.start || fieldLabels.start;
            fieldLabels.end   = this.config.fieldLabels.end || fieldLabels.end;
        }

        // rewrite defaults by a config if it is possible
        if (this.config.labelWidths) {
            labelWidths.start = this.config.labelWidths.start || labelWidths.start;
            labelWidths.end   = this.config.labelWidths.end || labelWidths.end;
        }

        if (this.config.fieldNames) {
            fieldNames.start = this.config.fieldNames.start || fieldNames.end;
            fieldNames.end   = this.config.fieldNames.end || fieldNames.end;
        }

        if (this.config.binds) {
            binds.start = this.config.binds.start || binds.start;
            binds.end   = this.config.binds.end || binds.end;
        }

        if (this.config.emptyTexts) {
            emptyTexts.start = this.config.emptyTexts.start || emptyTexts.start;
            emptyTexts.end   = this.config.emptyTexts.end || emptyTexts.end;
        }

        dateFieldCfg = {
            xtype: 'datefield',
            showToday: false,
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function (component) {
                        var picker = component.getPicker();

                        component.setValue(null);

                        function beforeHideHandler () {
                            return false;
                        }

                        if (component.isExpanded) {
                            picker.setLoading(component.loadingText);
                            picker.on('beforehide', beforeHideHandler, this, {single: true});
                            component.collapse();
                            component.expand();
                            picker.setLoading(false);
                        }
                    }
                }
            }
        };

        startCfg = {
            name: fieldNames.start,
            fieldLabel: fieldLabels.start,
            labelWidth: labelWidths.start,
            bind: binds.start,
            emptyText: emptyTexts.start
        };

        endCfg = {
            name: fieldNames.end,
            fieldLabel: fieldLabels.end,
            labelWidth: labelWidths.start,
            bind: binds.end,
            emptyText: emptyTexts.end
        };

        Ext.apply(startCfg, dateFieldCfg);
        Ext.apply(endCfg, dateFieldCfg);

        if (this.config.customConfigs) {
            Ext.apply(startCfg, this.config.customConfigs.start);
            Ext.apply(endCfg, this.config.customConfigs.end);
        }

        dateFields.start = Ext.create(startCfg);
        dateFields.end   = Ext.create(endCfg);

        dateFields.start.on('change', this.onStartDateChange, this);
        dateFields.end.on('change', this.onEndDateChange, this);

        return dateFields;
    },

    initComponent: function () {
        var dateFields;

        dateFields = this.createDateFields();
        this.dateFieldStart = dateFields.start;
        this.dateFieldEnd = dateFields.end;
        this.updateStartDateMaxValue(dateFields.end.getValue());
        this.updateEndDateMinValue(dateFields.start.getValue());

        this.callParent(arguments);
        this.add(this.dateFieldStart);
        this.add(this.dateFieldEnd);
    },

    onStartDateChange: function (component, value) {
        this.updateEndDateMinValue(value);
    },

    onEndDateChange: function (component, value) {
        this.updateStartDateMaxValue(value);
    },

    updateStartDateMaxValue: function (toValue) {
        var globalDateLimits = this.getGlobalDateLimits(),
            maxValue;

        if (globalDateLimits.MAX && toValue) {
            maxValue = globalDateLimits.MAX < toValue ? globalDateLimits.MAX : toValue;
        } else {
            // если хотя бы один не определен, то выбираем определенный или null
            maxValue = globalDateLimits.MAX || toValue;
        }

        this.dateFieldStart.setMaxValue(maxValue);
    },

    updateEndDateMinValue: function (fromValue) {
        var globalDateLimits = this.getGlobalDateLimits(),
            minValue;

        if (globalDateLimits.MIN && fromValue) {
            minValue = globalDateLimits.MIN > fromValue ? globalDateLimits.MIN : fromValue;
        } else {
            // если хотя бы один не определен, то выбираем определенный или null
            minValue = globalDateLimits.MIN || fromValue;
        }

        this.dateFieldEnd.setMinValue(minValue);
    }
});
