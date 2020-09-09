/**
 * Секция настройки основных свойств экрана настройки правил качества данных
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRulePropertiesController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.entity.metarecord.dq.dqruleprops',

    handleSourceSystemCheckboxes: function (newValue, oldValue) {
        var view = this.getView(),
            sourceSystemCheckBoxGroup = view.sourceSystemCheckBoxGroup,
            allOriginsCheckbox = view.allOriginsCheckbox,
            newValueOrigins = Ext.clone(newValue['origins']),
            originsChanges,
            value,
            all;

        sourceSystemCheckBoxGroup.suspendEvent('change');
        allOriginsCheckbox.suspendEvent('change');

        newValueOrigins = newValueOrigins || [];
        originsChanges = this.getOriginsChanges(newValue, oldValue);
        all = Ext.Array.contains(newValueOrigins, 'all');

        if (Ext.Array.contains(originsChanges.on, 'all')) {
            this.sourceSystemCheckBoxGroupCheckAll();
            sourceSystemCheckBoxGroup.addCls('un-checkboxgroup-selected-all');
        } else if (all) {
            value = {
                origins: originsChanges.off
            };

            allOriginsCheckbox.setValue(!all);
            sourceSystemCheckBoxGroup.setValue(value);
            sourceSystemCheckBoxGroup.removeCls('un-checkboxgroup-selected-all');
        } else {
            sourceSystemCheckBoxGroup.removeCls('un-checkboxgroup-selected-all');
        }

        allOriginsCheckbox.resumeEvent('change');
        sourceSystemCheckBoxGroup.resumeEvent('change');
    },

    sourceSystemCheckBoxGroupCheckAll: function () {
        var view = this.getView(),
            value,
            sourceSystemNames,
            sourceSystemCheckBoxGroup = view.sourceSystemCheckBoxGroup;

        sourceSystemNames = this.getSourceSystemNames();
        value = {
            origins: sourceSystemNames
        };

        sourceSystemCheckBoxGroup.setValue(value);
    },

    getOriginsChanges: function (newValue, oldValue) {
        var oldOrigins,
            newOrigins,
            originsOff,
            originsOn;

        oldOrigins = this.buildOriginsArray(oldValue);
        newOrigins = this.buildOriginsArray(newValue);

        originsOn = Ext.Array.difference(newOrigins, oldOrigins);
        originsOff = Ext.Array.difference(oldOrigins, newOrigins);

        return {
            on: originsOn,
            off: originsOff
        };
    },

    getSourceSystemNames: function () {
        var view = this.getView(),
            sourceSystems = view.getSourceSystems(),
            names;

        names = Ext.Array.map(sourceSystems, function (sourceSystem) {
            return sourceSystem.get('name');
        });

        return names;
    },

    updateRecordOrigins: function () {
        var view = this.getView(),
            dqRule = view.getDqRule(),
            allOriginsCheckbox = view.allOriginsCheckbox,
            sourceSystemCheckBoxGroup = view.sourceSystemCheckBoxGroup,
            all,
            sourceSystems = [],
            dqOrigins;

        all = allOriginsCheckbox.getValue();

        if (!all) {
            sourceSystems = this.buildOriginsArray(sourceSystemCheckBoxGroup.getValue());
        }

        dqOrigins = dqRule.getOrigins();
        dqOrigins.set('all', all);
        dqOrigins.set('sourceSystems', sourceSystems);
        dqRule.setOrigins(dqOrigins);
    },

    buildOriginsArray: function (checkboxGroupValue) {
        var sourceSystems;

        if (!checkboxGroupValue) {
            return [];
        }

        sourceSystems = Ext.clone(checkboxGroupValue.origins);
        sourceSystems = sourceSystems || [];

        if (Ext.isString(sourceSystems)) {
            sourceSystems = new Array(sourceSystems);
        }

        return sourceSystems;
    },

    onSourceSystemCheckBoxGroupChange: function (sourceSystemCheckBoxGroup, newValue, oldValue) {
        var view = this.getView(),
            allOriginsCheckbox = view.allOriginsCheckbox,
            all = allOriginsCheckbox.getValue(),
            oldOrigins,
            newOrigins;

        if (all) {
            oldOrigins = this.buildOriginsArray(oldValue);
            newOrigins = this.buildOriginsArray(newValue);

            oldOrigins.push('all');
            newOrigins.push('all');

            oldValue = {
                origins: oldOrigins
            };

            newValue = {
                origins: newOrigins
            };
        }

        this.handleSourceSystemCheckboxes(newValue, oldValue);
        this.calcAndUpdateApplicable();
        this.updateRecordOrigins();
    },

    onMasterDataCheckboxChange: function () {
        this.calcAndUpdateApplicable();
    },

    onAllOriginsCheckboxChange: function (allOriginsCheckbox, all) {
        var view = this.getView(),
            sourceSystemCheckBoxGroup = view.sourceSystemCheckBoxGroup,
            oldValue = sourceSystemCheckBoxGroup.getValue(),
            newValue = oldValue,
            value,
            origins;

        origins = this.buildOriginsArray(oldValue);
        origins.push('all');

        value = {
            origins: origins
        };

        if (all) {
            newValue = value;
        } else {
            oldValue = value;
        }

        this.handleSourceSystemCheckboxes(newValue, oldValue);
        this.calcAndUpdateApplicable();
        this.updateRecordOrigins();
    },

    calcAndUpdateApplicable: function () {
        var view = this.getView(),
            dqRule = view.getDqRule(),
            masterDataCheckBox =  view.masterDataCheckbox,
            sourceSystemCheckBoxGroup = view.sourceSystemCheckBoxGroup,
            masterDataCheckBoxChecked = masterDataCheckBox.getValue(),
            sourceSystemCheckBoxGroupValue = sourceSystemCheckBoxGroup.getValue(),
            applicable = [];

        if (masterDataCheckBoxChecked) {
            applicable.push('ETALON');
        }

        if (sourceSystemCheckBoxGroupValue.hasOwnProperty('origins')) {
            applicable.push('ORIGIN');
        }

        dqRule.set('applicable', applicable);
    },

    buildSourceSystemCheckboxes: function () {
        var view = this.getView(),
            sourceSystems = view.getSourceSystems(),
            sourceSystemCheckBoxGroup = view.sourceSystemCheckBoxGroup,
            fieldName = 'origins';

        Ext.Array.each(sourceSystems, function (sourceSystem) {
            var cfg,
                name = sourceSystem.get('name');

            cfg = {
                boxLabel: name,
                name: fieldName,
                inputValue: name,
                bind: {
                    readOnly: '{dqRuleEditorReadOnly}'    // TODO: ???
                }
            };

            sourceSystemCheckBoxGroup.add(cfg);
        }, this);
    },

    isSourceSystemChecked: function (sourceSystem) {
        var view = this.getView(),
            dqRule = view.getDqRule(),
            origins = dqRule.getOrigins(),
            selectedSourceSystems = origins.get('sourceSystems'),
            sourceSystemName = sourceSystem.get('name'),
            found;

        found = Ext.Array.findBy(selectedSourceSystems, function (selectedSourceSystem) {
            return selectedSourceSystem === sourceSystemName;
        });

        return Boolean(found);
    }
});
