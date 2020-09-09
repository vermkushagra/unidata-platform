/**
 * Панель настройки портов и выполнения функции
 *
 * @author Denis Makarov
 * @date 2018-05-08
 */
Ext.define('Unidata.view.admin.cleanseFunction.CleanseFunctionExecutionPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.cleanseFunction.CleanseFunctionController',
        'Unidata.view.admin.cleanseFunction.CleanseFunctionModel'
    ],

    alias: 'widget.admin.cleanseFunction.cleanseFunctionExecutionPanel',

    config: {
        cleanseFunction: null,
        cleanseFunctionInputPorts: null,
        cleanseFunctionOutputPorts: null,
        cleanseFunctionPath: null
    },

    referenceHolder: true,

    executeButton: null,

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.executeButton = this.lookupReference('executeButton');
    },

    initComponentEvent: function () {
        this.executeButton.on('click', this.execute, this);
    },

    onDestroy: function () {
        this.executeButton = null;
        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'fieldset',
            reference: 'inputPortFieldset',
            cls: 'un-dq-rule-port-panel-ports',
            title: Unidata.i18n.t('admin.dq>inputPorts'),
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            margin: 10,
            padding: 10,
            items: []
        },
        {
            xtype: 'fieldset',
            reference: 'outputPortFieldset',
            cls: 'un-dq-rule-port-panel-ports',
            title: Unidata.i18n.t('admin.dq>outputPorts'),
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            margin: '10 10 10 0',
            padding: 10,
            items: []
        }
    ],

    bbar: [
        {
            xtype: 'button',
            margin: '0 0 0 0',
            cls: 'execute-button',
            reference: 'executeButton',
            text: Unidata.i18n.t('common:execute')
        }
    ],

    updateCleanseFunction: function () {
        this.buildInputPorts();
        this.buildOutputPorts();
    },

    buildInputPorts: function () {
        var inputPortFieldset = this.lookupReference('inputPortFieldset');

        inputPortFieldset.removeAll();

        this.cleanseFunctionInputPorts = [];

        this.cleanseFunction.inputPorts().each(function (port) {
            var portField = this.buildPort(port);

            inputPortFieldset.add(portField);
            this.cleanseFunctionInputPorts.push(portField);
        }, this);

    },

    buildOutputPorts: function () {
        var outputPortFieldset = this.lookupReference('outputPortFieldset');

        outputPortFieldset.removeAll();

        this.cleanseFunctionOutputPorts = [];

        this.cleanseFunction.outputPorts().each(function (port) {
            var portField = this.buildPort(port, true);

            outputPortFieldset.add(portField);
            this.cleanseFunctionOutputPorts.push(portField);
        }, this);
    },

    fillOutputPorts: function (portDatas) {
        var cleanseFunctionOutputPorts = this.cleanseFunctionOutputPorts;

        cleanseFunctionOutputPorts.forEach(function (outputPort) {
            outputPort.getInputField().setValue('');
        });

        portDatas.forEach(this.fillOutputPort, this);
    },

    fillOutputPort: function (portData) {
        var cleanseFunctionPort;

        cleanseFunctionPort = this.cleanseFunctionOutputPorts.find(function (item) {
                return item.getPort().get('name') === portData.name;
            }
        );

        if (!cleanseFunctionPort) {
            Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>outputPortNotFound', {portName: portData.name}));
            throw new Error(Unidata.i18n.t('admin.cleanseFunction>outputPortNotFound', {portName: portData.name}));
        }

        cleanseFunctionPort.getInputField().setValue(portData.value);
    },

    buildPort: function (port, readOnly) {
        var portField;

        portField = Ext.create('Unidata.view.admin.cleanseFunction.CleanseFunctionPort', {
            port: port,
            readOnly: readOnly,
            margin: '0 0 10 0'
        });

        return portField;

    },

    execute: function () {
        var me = this,
            simpleAttributes,
            dataType,
            jsonData;

        simpleAttributes = Ext.Array.map(this.cleanseFunctionInputPorts, function (cleanseFunctionPort) {
            var simpleAttribute,
                port = cleanseFunctionPort.getPort(),
                inputField = cleanseFunctionPort.getInputField();

            dataType = Unidata.util.DataQuality.buildSubmitDataType(port);

            simpleAttribute = {
                name: port.get('name'),
                value: inputField.getSubmitValue(),
                type: dataType
            };

            return simpleAttribute;
        }, this);

        jsonData = {
            functionName: this.cleanseFunctionPath,
            simpleAttributes: simpleAttributes
        };

        Unidata.util.api.CleanseFunction.executeCleanseFunction(jsonData)
            .then(function (jsonResp) {
                me.fillOutputPorts(jsonResp.simpleAttributes);
            }, function (jsonResp) {
                Unidata.showError(jsonResp.errorMessage);
            }).done();
    }
});
