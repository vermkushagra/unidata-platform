/**
 * Компонент "Порт правила качества"
 *
 * @author Sergey Shishigin
 * @date 2018-02-15
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePort', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.port.dqruleport',

    config: {
        dqRule: null,
        port: null,
        portData: null,
        metaRecord: null,
        readOnly: null
    },

    cls: 'un-dq-rule-port',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortUPathField',
        'Unidata.view.component.EntityAtrributeHtmlComboBox',

        'Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DqRulePortConstantFieldFactory'
    ],

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'left'
    },

    portConstantInputContainer: null,
    portUPathInput: null,

    portSelectionTypeRadio: null,
    portManualTypeRadio: null,

    radioGroupNamePrefix: 'un-dq-rule-port-input-type-',    // префикс для генерации name для radioGroup
    radioGroupName: null,                                   // сгенерированное имя radioGroup

    portApplicationModeLabel: null,

    initItems: function () {
        var items,
            port,
            portData,
            me = this,
            required,
            description,
            dataType,
            portType,
            upathValue,
            constantValueObject,
            constantValue,
            title,
            fieldName,
            isPortTypeInput,
            metaRecord,
            fieldCfg,
            dqRulePortConstantFieldFactory,
            portApplicationMode,
            portApplicationModeHtml,
            readOnly;

        this.callParent(arguments);

        port          = this.getPort();
        portData      = this.getPortData();
        required      = port.get('required');
        description   = port.get('description');
        dataType      = port.get('dataType');    // TODO: куда засунуть тип порта?
        portType          = port.getPortType();
        upathValue    = null;
        constantValueObject = null;
        constantValue = null;
        title = this.buildPortTitle(description, required, dataType);
        fieldName = this.getRadioGroupName();
        metaRecord = this.getMetaRecord();
        readOnly = this.getReadOnly();

        isPortTypeInput = portType === Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT;

        if (portData) {
            upathValue = portData.get('attributeName');

            if (isPortTypeInput) {
                constantValueObject = portData.getAttributeConstantValue();

                if (constantValueObject) {
                    constantValue = constantValueObject.get('value');
                }
            }
        }

        portApplicationMode = port.get('portApplicationMode');

        dqRulePortConstantFieldFactory = Ext.create('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DqRulePortConstantFieldFactory');

        fieldCfg = dqRulePortConstantFieldFactory.buildFieldCfg(dataType, {
            value: constantValue,
            readOnly: readOnly,
            changeFn: this.onPortConstantInputChange.bind(this),
            reference: 'portConstantInputContainer',
            hidden: !this.isPortDataConstant(portData)
        });

        portApplicationModeHtml = this.buildPortApplicationModeHtml(portApplicationMode);

        items = [
            {
                xtype: 'radiogroup',
                cls: 'un-dq-rule-port-radiogroup',
                columns: 2,
                hidden: !isPortTypeInput && !this.isPortDataConstant(portData),
                listeners: {
                    change: 'onPortInputTypeRadioChange',
                    scope: this
                },
                width: 300,
                items: [
                    {
                        reference: 'portSelectionTypeRadio',
                        boxLabel: Unidata.i18n.t('admin.dq>portInput'),
                        name: fieldName,
                        inputValue: 'upath',
                        checked: !this.isPortDataConstant(portData),
                        readOnly: me.getReadOnly()
                    },
                    {
                        reference: 'portManualTypeRadio',
                        boxLabel: Unidata.i18n.t('admin.dq>constantInput'),
                        name: fieldName,
                        inputValue: 'constant',
                        checked: this.isPortDataConstant(portData),
                        readOnly: me.getReadOnly()
                    }
                ]
            },
            {
                xtype: 'admin.entity.metarecord.dq.port.dqruleportupathfield',
                width: '100%',
                reference: 'portUPathInput',
                msgTarget: 'under',
                metaRecord: metaRecord,
                dataType: dataType,
                portApplicationMode: portApplicationMode,
                portType: portType,
                upathValue: upathValue,
                hidden: this.isPortDataConstant(portData),
                readOnly: me.getReadOnly(),
                listeners: {
                    upathvaluechange: 'onPortUPathValueChange',
                    scope: this
                }
            }
        ];

        this.add(items);
        this.add(fieldCfg);
        this.add({
            xtype: 'component',
            reference: 'portApplicationModeLabel',
            baseCls: 'un-dq-rule-port-warning',
            html: portApplicationModeHtml
        });
        this.initReferences();
        this.setTitle(title);
    },

    buildPortApplicationModeHtml: function (portApplicationMode) {
        return Unidata.util.DataQuality.portApplicationModeLabels[portApplicationMode];
    },

    /**
     * Получить сгенерированное имя группы радиобаттонов
     * @returns {String}
     */
    getRadioGroupName: function () {
        var prefix = this.radioGroupNamePrefix;

        if (this.radioGroupName) {
            return this.radioGroupName;
        }

        this.radioGroupName = prefix + this.getId();

        return this.radioGroupName;
    },

    initReferences: function () {
        this.portConstantExtendedInput = this.lookupReference('portConstantExtendedInput');
        this.portConstantInputContainer = this.lookupReference('portConstantInputContainer');
        this.portUPathInput = this.lookupReference('portUPathInput');

        this.portSelectionTypeRadio = this.lookupReference('portSelectionTypeRadio');
        this.portManualTypeRadio = this.lookupReference('portManualTypeRadio');

        this.portApplicationModeLabel = this.lookupReference('portApplicationModeLabel');
    },

    /**
     * Является ли значение порта константой
     *
     * @param portData {Unidata.model.dataquality.Input|Unidata.model.dataquality.Output}
     * @returns {boolean}
     */
    isPortDataConstant: function (portData) {
        var constantValue,
            port = this.getPort(),
            portType = port.getPortType();

        if (!portData) {
            return false;
        }

        if (portType === Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT) {
            constantValue = portData.getAttributeConstantValue();
        }

        return Boolean(constantValue);
    },

    /**
     * Построить заголовок порта
     *
     * @param description {string}
     * @param required {boolean}
     * @param dataType {string}
     * @returns {string}
     */
    buildPortTitle: function (description, required, dataType) {
        var DqRuleModel = Unidata.model.dataquality.DqRule,
            title,
            requiredAsterisk,
            dataTypeDisplayName,
            dataTypeCls;

        dataTypeCls = this.cls + '-data-type';
        requiredAsterisk = required ? '*' : '';

        dataTypeDisplayName = DqRuleModel.getDataTypeDisplayName(dataType).toLowerCase();
        title      = Ext.String.format('{0}{1}<span class="{2}">{3}</span>', description, requiredAsterisk, dataTypeCls, dataTypeDisplayName);

        return title;
    },

    onPortInputTypeRadioChange: function (self, value) {
        var isConstant,
            fieldName = this.getRadioGroupName(),
            portData;

        if (!Ext.isObject(value) || !value.hasOwnProperty(fieldName)) {
            return;
        }

        isConstant = value[fieldName] === 'constant';

        portData = this.getPortData();

        if (portData) {
            if (isConstant) {
                this.portUPathInput.clearValue();
            } else {
                this.portConstantInputContainer.clearValue();
            }
        }
        this.portConstantInputContainer.setHidden(!isConstant);
        this.portUPathInput.setHidden(isConstant);
    },

    /**
     * Создать порт для dqRule
     *
     * @return portData {Unidata.model.dataquality.Input|Unidata.model.dataquality.Output}
     */
    createPortData: function () {
        var portData = this.getPortData(),
            port = this.getPort(),
            portName = port.get('name'),
            portType = port.getPortType(),
            portDataClsName,
            collection;

        if (portData) {
            return portData;
        }

        portDataClsName = Ext.String.format('Unidata.model.dataquality.{0}', Ext.String.capitalize(portType.toLowerCase()));

        portData = Ext.create(portDataClsName, {
            functionPort: portName
        });

        collection = this.getPortDataCollection();

        collection.add(portData);
        this.setPortData(portData);

        return portData;
    },

    /**
     * Удалить dq порт
     */
    deletePortData: function () {
        var portData = this.getPortData(),
            collection,
            index,
            found;

        collection = this.getPortDataCollection();
        index = collection.findExact('functionPort', portData.get('functionPort'));

        if (index > -1) {
            found = collection.getAt(index);
            collection.remove(found);
            this.setPortData(null);
        }
    },

    /**
     * Обработчик события измемнения uPathValue
     *
     * @param self
     * @param uPathValue
     * @param oldUPathValue
     */
    onPortUPathValueChange: function (self, uPathValue, oldUPathValue) {
        var metaRecord = this.getMetaRecord(),
            dqRule = this.getDqRule(),
            portData = this.getPortData(),
            canonicalPath,
            oldCanonicalPath,
            UPath;

        UPath    = Ext.create('Unidata.util.upath.UPath', {
            entity: metaRecord
        });

        if (!portData) {
            portData = this.createPortData();
        }

        oldUPathValue = portData.get('attributeName');
        UPath.fromUPath(oldUPathValue);
        oldCanonicalPath = UPath.toCanonicalPath();
        UPath.fromUPath(uPathValue);

        canonicalPath = UPath.toCanonicalPath();
        portData.set('attributeName', uPathValue);

        if (canonicalPath !== oldCanonicalPath) {
            this.fireEvent('portupathchanged', this, dqRule, portData, canonicalPath, oldCanonicalPath);
        }

        if (!this.isPortDataHasData()) {
            this.deletePortData();
        }
    },

    createAttributeConstantValue: function () {
        var port = this.getPort(),
            portData = this.getPortData(),
            attributeConstantValue,
            dataType;

        if (!portData) {
            return null;
        }

        attributeConstantValue = portData.getAttributeConstantValue();

        if (attributeConstantValue) {
            return attributeConstantValue;
        }

        dataType = Unidata.util.DataQuality.buildSubmitDataType(port);
        attributeConstantValue = Ext.create('Unidata.model.data.SimpleAttribute', {
            name: port.get('name'),
            type: dataType
        });
        portData.setAttributeConstantValue(attributeConstantValue);

        return attributeConstantValue;
    },

    onPortConstantInputChange: function (self) {
        var portData = this.getPortData(),
            attributeConstantValue,
            submitConstantValue;

        submitConstantValue = self.getSubmitValue();

        if (Ext.isEmpty(submitConstantValue)) {
            // если значение пустое, то удаляем секцию attributeConstantValue в случае ее наличия
            if (portData) {
                attributeConstantValue = portData.getAttributeConstantValue();

                if (attributeConstantValue) {
                    attributeConstantValue.drop();
                }
            }
        } else {
            // если значение не пустое, то заполяем им секцию attributeConstantValue
            if (!portData) {
                portData = this.createPortData();
            }

            attributeConstantValue = portData.getAttributeConstantValue();

            if (!attributeConstantValue) {
                attributeConstantValue = this.createAttributeConstantValue();
            }
            attributeConstantValue.set('value', submitConstantValue);
            portData.setAttributeConstantValue(attributeConstantValue);
        }

        if (!this.isPortDataHasData()) {
            this.deletePortData();
        }
    },

    isPortDataHasData: function () {
        var portData = this.getPortData(),
            port = this.getPort(),
            portType = port.getPortType(),
            attributeConstantValue = null;

        if (!portData) {
            return false;
        }

        if (portType === Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT) {
            attributeConstantValue = portData.getAttributeConstantValue();
        }

        return portData.get('attributeName') || (attributeConstantValue && !Ext.isEmpty(attributeConstantValue.get('value')));
    },

    getPortDataCollection: function () {
        var dqRule   = this.getDqRule(),
            port     = this.getPort(),
            portType = port.getPortType(),
            collection;

        if (portType === Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT) {
            collection = dqRule.inputs();
        } else {
            collection = dqRule.outputs();
        }

        return collection;
    },

    updateReadOnly: function (readOnly) {
        this.portSelectionTypeRadio.setReadOnly(readOnly);
        this.portManualTypeRadio.setReadOnly(readOnly);

        this.portUPathInput.setReadOnly(readOnly);
        this.portConstantInputContainer.setReadOnly(readOnly);
    }
});
