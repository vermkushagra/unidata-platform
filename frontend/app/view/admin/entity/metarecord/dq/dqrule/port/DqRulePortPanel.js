/**
 * Секция настройки функции редактора правил качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortPanel', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.port.dqruleportpanel',

    requires: ['Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePort'],

    config: {
        metaRecord: null,
        dqRule: null,
        cleanseFunction: null
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    referenceHolder: true,

    inputPortFieldset: null,
    outputPortFieldset: null,

    cls: 'un-dq-rule-port-panel',

    relayPortEvents: function () {
        this.inputPortFieldset.items.each(function (port) {
            this.relayEvents(port, ['portupathchanged']);
        }, this);

        this.outputPortFieldset.items.each(function (port) {
            this.relayEvents(port, ['portupathchanged']);
        }, this);
    },

    initItems: function () {
        var portComponents = {input: [], output: []},
            items,
            cleanseFunction,
            dqRule;

        this.callParent(arguments);

        cleanseFunction = this.getCleanseFunction();
        dqRule          = this.getDqRule();

        if (cleanseFunction) {
            portComponents = this.buildAllPortComponentCfgs(dqRule, cleanseFunction);
        }

        items = [
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
                items: portComponents.input
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
                items: portComponents.output
            }
        ];

        this.add(items);
        this.initReferences();
        this.relayPortEvents();
    },

    initReferences: function () {
        this.inputPortFieldset = this.lookupReference('inputPortFieldset');
        this.outputPortFieldset = this.lookupReference('outputPortFieldset');
    },

    /**
     *
     * @param ports {Unidata.model.cleansefunction.InputPort[]|Unidata.model.cleansefunction.OutputPort[]}
     * @param portDatas {Unidata.model.dataquality.Input[]|Unidata.model.dataquality.Output[]}
     */
    buildPortComponentCfgs: function (ports, portDatas) {
        var portComponents,
            dqRule = this.getDqRule();

        portComponents = Ext.Array.map(ports, function (port) {
            var portData;

            portData = this.findPortData(portDatas, port.get('name'));

            return this.buildPortComponentCfg(port, dqRule, portData);
        }, this);

        return portComponents;
    },

    /**
     *
     * @param portDatas {Unidata.model.dataquality.Input[]|Unidata.model.dataquality.Output[]}
     * @param name {string}
     * @returns  {Unidata.model.dataquality.Input|Unidata.model.dataquality.Output}
     */
    findPortData: function (portDatas, name) {
        return Ext.Array.findBy(portDatas, function (portData) {
            return portData.get('functionPort') === name;
        });
    },

    /**
     * @param port {Unidata.model.cleansefunction.InputPort|Unidata.model.cleansefunction.OutputPort}
     * @param portData {Unidata.model.dataquality.Input|Unidata.model.dataquality.Output}
     */
    buildPortComponentCfg: function (port, dqRule, portData) {
        var reference,
            metaRecord,
            cfg;

        reference = this.buildPortReference(port);
        metaRecord = this.getMetaRecord();

        cfg = {
            xtype: 'admin.entity.metarecord.dq.port.dqruleport',
            dqRule: dqRule,
            port: port,
            portData: portData,
            reference: reference,
            metaRecord: metaRecord,
            bind: {
                readOnly: '{dqRuleEditorReadOnly}'
            }
        };

        return cfg;
    },

    /**
     * Построить свойство reference для порта
     *
     * @param port {Unidata.model.cleansefunction.InputPort|Unidata.model.cleansefunction.OutputPort}
     * @returns {string}
     */
    buildPortReference: function (port) {
        var portName = port.get('name'),
            direction = port.getPortType();

        return 'port' + Ext.String.capitalize(direction) + Ext.String.capitalize(portName);
    },

    /**
     * Найти поле ввода для порта по port
     * @param port {Unidata.model.cleansefunction.InputPort|Unidata.model.cleansefunction.OutputPort}
     * @returns {*}
     */
    findPortComponent: function (port) {
        var fieldset,
            portComponents,
            found;

        if (port.getPortType() === Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT) {
            fieldset = this.inputPortFieldset;
        } else {
            fieldset = this.outputPortFieldset;
        }

        portComponents = fieldset.items;
        found = portComponents.findBy(function (portComponent) {
            return portComponent.port.get('name') === port.get('name') &&
                    portComponent.port.$className === port.$className;
        });

        return found;
    },

    cleanPortDatas: function () {
        var dqRule = this.getDqRule();

        dqRule.inputs().removeAll();
        dqRule.outputs().removeAll();
    },

    updateCleanseFunction: function () {
        this.rebuildPorts();
    },

    buildAllPortComponentCfgs: function (dqRule, cleanseFunction) {
        var inputPorts      = cleanseFunction.inputPorts().getRange(),
            outputPorts     = cleanseFunction.outputPorts().getRange(),
            inputPortDatas  = dqRule.inputs().getRange(),
            outputPortDatas = dqRule.outputs().getRange(),
            portComponents = {};

        portComponents['input'] = this.buildPortComponentCfgs(inputPorts, inputPortDatas);
        portComponents['output'] = this.buildPortComponentCfgs(outputPorts, outputPortDatas);

        return portComponents;
    },

    rebuildPorts: function () {
        var dqRule = this.getDqRule(),
            cleanseFunction = this.getCleanseFunction(),
            portComponents,
            inputPortFieldset = this.inputPortFieldset,
            outputPortFieldset = this.outputPortFieldset;

        if (!inputPortFieldset || !outputPortFieldset) {
            return;
        }

        this.cleanPortDatas();
        portComponents = this.buildAllPortComponentCfgs(dqRule, cleanseFunction);
        inputPortFieldset.removeAll();
        outputPortFieldset.removeAll();
        inputPortFieldset.add(portComponents.input);
        outputPortFieldset.add(portComponents.output);
    }
});
