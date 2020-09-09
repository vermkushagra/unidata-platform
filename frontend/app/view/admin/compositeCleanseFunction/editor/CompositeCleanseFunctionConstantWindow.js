/**
 * Окно настройки параметров константного блока
 *
 * @author Sergey Shishigin
 * @date 2018-03-22
 */
Ext.define('Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionConstantWindow', {
    extend: 'Ext.window.Window',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    config: {
        constant: null,
        oldConstant: null,
        portConstantBlock: null
    },

    referenceHolder: true,

    cls: 'un-cleanse-function-constant-window',

    initComponent: function () {
        var constant;

        this.dockedItems = this.buildDockedItems();
        this.callParent(arguments);

        constant = this.getConstant();

        if (constant) {
            this.setOldConstant(constant.clone());
        }
        this.buildAndUpdateTitle();
    },

    initItems: function () {
        var valueInputCfg,
            dataTypeComboCfg;

        this.callParent(arguments);

        dataTypeComboCfg = this.buildDataTypeComboCfg();
        valueInputCfg = this.buildValueInputCfg();

        this.add(dataTypeComboCfg);
        this.add(valueInputCfg);
        this.initReferences();
    },

    buildValueInputCfg: function () {
        var dqRulePortConstantFieldFactory,
            dataType,
            value,
            fieldCfg;

        dqRulePortConstantFieldFactory = Ext.create('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DqRulePortConstantFieldFactory');
        dataType = this.getDataType();
        value = this.getValue();
        fieldCfg = dqRulePortConstantFieldFactory.buildFieldCfg(dataType, {
            value: value,
            reference: 'valueInput',
            changeFn: this.onValueInputChange.bind(this),
            ui: 'un-field-default',
            isDisableExtended: true
        });

        return fieldCfg;
    },

    //!
    buildDockedItems: function () {
        var dockedItems;

        dockedItems = {
            xtype: 'toolbar',
            reference: 'buttonToolbar',
            ui: 'footer',
            dock: 'bottom',
            layout: {
                pack: 'center'
            },
            items: [
                {
                    xtype: 'button',
                    reference: 'saveButton',
                    text: Unidata.i18n.t('common:save'),
                    listeners: {
                        click: this.onSaveButtonClick.bind(this)
                    }
                },
                {
                    xtype: 'button',
                    color: 'transparent',
                    reference: 'cancelButton',
                    text: Unidata.i18n.t('common:cancel'),
                    listeners: {
                        click: this.onCancelButtonClick.bind(this)
                    }
                }
            ]
        };

        return dockedItems;
    },

    buildDataTypeComboCfg: function () {
        var DqRuleModel = Unidata.model.dataquality.DqRule,
            fieldCfg,
            simpleDataTypeObjectList,
            dataType;

        simpleDataTypeObjectList = DqRuleModel.buildSimpleDataTypeObjectList();
        dataType = this.getDataType();

        fieldCfg = {
            xtype: 'combo',
            valueField: 'name',
            displayField: 'displayName',
            reference: 'dataTypeCombo',
            editable: false,
            ui: 'un-field-default',
            store: {
                data: simpleDataTypeObjectList,
                fields: ['name', 'displayName']
            },
            value: dataType,
            listeners: {
                change: this.onDataTypeComboChange.bind(this)
            }
        };

        return fieldCfg;
    },

    initReferences: function () {
        this.dataTypeCombo = this.lookupReference('dataTypeCombo');
        this.valueInput = this.lookupReference('valueInput');
    },

    onDataTypeComboChange: function (self, dataType) {
        var fieldCfg;

        this.setDataType(dataType);
        this.remove(this.valueInput);
        this.setValue(null);

        fieldCfg = this.buildValueInputCfg();
        this.add(fieldCfg);
        this.valueInput = this.lookupReference('valueInput');
    },

    onValueInputChange: function (self, value) {
        this.setValue(value);
    },

    onSaveButtonClick: function () {
        var constant = this.getConstant(),
            portConstantBlock = this.getPortConstantBlock(),
            oldConstant = this.getOldConstant();

        if (!this.valueInput.validate()) {
            return;
        }

        this.fireEvent('okbtnclick', this, portConstantBlock, constant, oldConstant);
        this.close();
    },

    onCancelButtonClick: function () {
        this.fireEvent('cancelbtnclick', this);
        this.close();
    },

    getDataType: function () {
        var constant = this.getConstant(),
            dataType = null;

        if (constant) {
            dataType = constant.get('type');
        }

        return dataType;
    },

    getValue: function () {
        var constant = this.getConstant(),
            value = null;

        if (constant) {
            value = constant.get('value');
        }

        return value;
    },

    setDataType: function (dataType) {
        var constant = this.getConstant(),
            oldDataType;

        if (!constant) {
            return false;
        }

        oldDataType = constant.get('type');
        constant.set('type', dataType);
        this.updateDataType(dataType, oldDataType);

        return true;
    },

    updateDataType: function () {
        this.buildAndUpdateTitle();
    },

    buildAndUpdateTitle: function () {
        this.setTitle(this.buildTitle());
    },

    setValue: function (value) {
        var constant = this.getConstant();

        if (!constant) {
            return false;
        }

        constant.set('value', value);

        return true;
    },

    buildTitle: function () {
        var title,
            dataType = this.getDataType();

        title = Ext.String.format('<div class="un-cleanse-function-constant-window-title-icon"></div><span class="un-cleanse-function-constant-window-title-text">Константа <span class="un-cleanse-function-constant-window-title-type">{0}</span></span>', dataType);

        return title;
    }
});
