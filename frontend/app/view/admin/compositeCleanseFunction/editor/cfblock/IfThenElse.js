/**
 * Модуль построения блока условий
 *
 * @author Sergey Shishigin
 * @date 2018-04-13
 */
Ext.define('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.IfThenElse', {
    ifThenElseList: {},

    jsPlumb: null,
    view: null,
    conditionEndpoint: null,
    inputEndpoint: null,
    outputEndpoint: null,
    nodeSeqIdentifier: null,
    template: null,
    tooltip: null,

    constructor: function (data) {
        this.jsPlumb = data.jsPlumb;
        this.view = data.view;
        this.conditionEndpoint = data.conditionEndpoint;
        this.inputEndpoint = data.inputEndpoint;
        this.outputEndpoint = data.outputEndpoint;
        this.nodeSeqIdentifier = data.nodeSeqIdentifier;
        this.tooltip = Ext.create('Unidata.view.component.tooltip.Tooltip', {
            anchorTo: 'bottom',
            arrow: true,
            maxWidth: 450
        });
    },

    /**
     * Получить объект кэша функций
     * @return {Object|null}
     */
    getIfThenElseList: function () {
        if (!Ext.isObject(this.ifThenElseList)) {
            this.ifThenElseList = {};
        }

        return this.ifThenElseList;
    },

    /**
     * Получить cleanse function из кэша по полному имени
     * @param {String} key
     * @return {Unidata.model.data.SimpleAttribute}
     */
    getFromIfThenElseList: function (key) {
        var ifThenElseList = this.getIfThenElseList();

        return ifThenElseList[key];
    },

    /**
     * Поместить в список if-then-else блоков
     * @param {Integer} nodeId
     * @param {String[]} endpointUuids
     * @param {Boolean} override
     */
    putInIfThenElseList: function (nodeId, portsData, endpointUuids, override) {
        var ifThenElseList = this.getIfThenElseList();

        override = Ext.isBoolean(override) ? override : true;

        if (override) {
            ifThenElseList[nodeId] = {
                nodeId: nodeId,
                portsData: this.buildIfThenElseJson(portsData),
                endpointUuids: endpointUuids
            };
        }

    },

    buildIfThenElseJson: function (portsData) {
        var ifThenElseJson = {
            ports: []
        };

        Ext.Array.forEach(portsData.getRange(), function (port) {
            ifThenElseJson.ports.push(port.get('dataType'));
        });

        return ifThenElseJson;
    },

    deleteFromIfThenElseList: function (nodeId) {
        var ifThenElseList = this.getIfThenElseList();

        delete ifThenElseList[nodeId];
    },

    clearIfThenElseList: function () {
        this.ifThenElseList = {};
    },

    editIfThenElsePorts: function (position, okCallback) {
        var view = this.view,
            viewEl = view.getEl(),
            parentContainerTop = viewEl.getTop(),
            parentContainerLeft = viewEl.getLeft(),
            windowWidth = 500,
            windowHeight = 300,
            x;

        x = (parentContainerLeft + position.x) - windowWidth / 2;

        if (x + (windowWidth / 2) > viewEl.getWidth()) {
            x = viewEl.getWidth() - (windowWidth / 2);
        } else if (x - windowWidth / 2 < 0) {
            x = parentContainerLeft;
        }

        this.showIfThenElseEditorWindow({
            x: x,
            y: parentContainerTop + position.y,
            width: windowWidth,
            height: windowHeight
        }, okCallback);
    },

    showIfThenElseEditorWindow: function (customCfg, okCallback) {
        var window,
            cfg;

        cfg = {
            modal: true,
            draggable: false,
            resizable: false,
            listeners: {
                okbtnclick: okCallback
            }
        };

        cfg = Ext.apply(cfg, customCfg);

        window = Ext.create('Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionIfThenElseWindow', cfg);

        window.show();
    },

    addIfThenElse: function (portsData, position, nodeId) {
        var id,
            el,
            size,
            endpointUuids;

        if (!nodeId) {
            nodeId = this.nodeSeqIdentifier.generate();
        }

        id = this.buildIfThenElseId(nodeId);
        el = this.createIfThenElseContainer(this.view.getEl(), nodeId, position);
        size = this.getSizeByPortCount(portsData.length);
        this.buildIfThenElseTemplate(nodeId, {
            id: id,
            size: size
        });
        endpointUuids = this.createIfThenElsePortConnectors(nodeId, portsData);
        this.initIfThenElseListeners(el, nodeId);
        this.putInIfThenElseList(nodeId, portsData, endpointUuids);

        return el;
    },

    getSizeByPortCount: function (portCount) {
        var CompositeCleanseFunctionEditor = Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor,
            size;

        if (portCount === 1) {
            size = CompositeCleanseFunctionEditor.IF_THEN_ELSE_SIZE.SMALL;
        } else if (portCount === 2 || portCount === 3) {
            size = CompositeCleanseFunctionEditor.IF_THEN_ELSE_SIZE.MEDIUM;
        } else if (portCount === 4) {
            size = CompositeCleanseFunctionEditor.IF_THEN_ELSE_SIZE.LARGE;
        } else if (portCount > 4 && portCount < 8) {
            size = CompositeCleanseFunctionEditor.IF_THEN_ELSE_SIZE.EXTRALARGE;
        } else {
            //TODO: implement me
            throw new Error('Method is not implemented');
        }

        return size;
    },

    buildIfThenElseId: function (nodeId) {
        var id;

        id = Ext.String.format('ifthenelse-{0}', nodeId);

        return id;
    },

    createIfThenElseContainer: function (parentEl, nodeId, position) {
        var container,
            id;

        id = this.buildIfThenElseId(nodeId);
        position = Ext.isObject(position) ? position : {x: 100, y: 100};

        container = Ext.DomHelper.append(parentEl.dom, {
            tag: 'div',
            id: id,
            'class': 'unidata-cfblock-container'
        });

        container.style.top = position.y + 'px';
        container.style.left = position.x + 'px';

        return container;
    },

    buildIfThenElseTemplate: function (nodeId, data) {
        var template,
            id;

        id = this.buildIfThenElseId(nodeId);
        template = Ext.create('Ext.XTemplate', Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionTemplate.ifThenElseTpl);

        if (data.size !== 'small') {
            data.fullText = true;
        }

        template.overwrite(id, data);
        this.template = template;

        return template;
    },

    createIfThenElsePortConnectors: function (nodeId, portsData) {
        var uuids,
            endpoints = [];

        endpoints.push(this.createIfThenElseConditionPort(nodeId));
        endpoints = endpoints.concat(this.createIfThenElseInputPorts(nodeId, portsData));
        endpoints = endpoints.concat(this.createIfThenElseOutputPorts(nodeId, portsData, true));
        endpoints = endpoints.concat(this.createIfThenElseOutputPorts(nodeId, portsData, false));

        uuids = Ext.Array.map(endpoints, function (endpoint) {
            return endpoint.getUuid();
        });

        return uuids;
    },

    buildIfThenElseUuid: function (portType, portId) {
        var uuid;

        uuid = this.buildUuid(portType, portId);

        return uuid;
    },

    createIfThenElsePort: function (nodeId, anchor, endpointCfg, dataType, portType, portIndex) {
        var me = this,
            uuid,
            endpoint,
            id,
            portId,
            portName,
            label,
            cssClass,
            tooltipHtml;

        portIndex = Ext.isNumeric(portIndex) ? portIndex : 0;

        id = this.buildIfThenElseId(nodeId);
        portName = this.buildIfThenElsePortName(portIndex);
        portId = this.buildIfThenElsePortId(id, portName);
        uuid = this.buildIfThenElseUuid(portType, portId);

        if (portType !== 'CONDITION') {
            label = (portIndex + 1).toString();
            cssClass = portType == 'INPUT' ? 'unidata-cfblock-port-endpoint-dark' : 'unidata-cfblock-port-endpoint';
            endpointCfg.overlays = [
                ['Label', {
                    label: label, id: 'label', cssClass: cssClass, location: [0.5, 0.5]
                }]
            ];
        }

        tooltipHtml = this.buildTooltipHtml(portType, dataType, portIndex);

        endpoint = this.jsPlumb.addEndpoint(id, endpointCfg, {
            zIndex: 2001,
            anchor: anchor,
            cssClass: 'un-if-then-else-port',
            uuid: uuid,
            parameters: {
                portName: portName,
                portType: portType,
                nodeId: nodeId,
                dataType: dataType,
                portIndex: portIndex,
                tooltipHtml: tooltipHtml
            }
        });

        endpoint.bind('mouseover', function (endpoint) {
            if (endpoint.component) { //при наведении на label, берем у него родительский компонент
                endpoint = endpoint.component;
            }

            if (endpoint.getOverlay('label')) {
                endpoint.getOverlay('label').hide();
            }
            clearTimeout(endpoint.tipShowTimer);

            endpoint.tipShowTimer = Ext.defer(me.tooltip.showTooltip, 500, me.tooltip, [endpoint.canvas, endpoint.getParameter('tooltipHtml')]);
        });

        endpoint.bind('mouseout', function (endpoint) {
            if (endpoint.component) {
                endpoint = endpoint.component;
            }

            if (endpoint.getOverlay('label')) {
                endpoint.getOverlay('label').show();
            }
            clearTimeout(endpoint.tipShowTimer);
            me.tooltip.hide();
        });

        return endpoint;
    },

    buildTooltipHtml: function (portType, dataType, portIndex) {
        var html,
            portTypeName,
            portTypeText,
            dataTypeDisplayName;

        if (portType === 'CONDITION') {
            portTypeName = Unidata.i18n.t('admin.cleanseFunction>condition');
            portTypeText = portTypeName + ': ';
        } else if (portType === 'INPUT') {
            portTypeName = Unidata.i18n.t('admin.cleanseFunction>inputPort');
            portTypeText = Ext.String.format('{0} {1}: ', portTypeName, (portIndex + 1));
        } else if (portType === 'OUTPUT_TRUE' || portType === 'OUTPUT_FALSE') {
            portTypeName = Unidata.i18n.t('admin.cleanseFunction>outputPort');
            portTypeText = Ext.String.format('{0} {1}: ', portTypeName, (portIndex + 1));
        }

        dataTypeDisplayName = Unidata.util.MetaAttributeFormatter.getSimpleDataDisplayName(dataType);
        html = portTypeText + dataTypeDisplayName;

        return html;
    },

    createIfThenElseConditionPort: function (nodeId) {
        var DQ_RULE_PORT_DATA_TYPES = Unidata.model.dataquality.DqRule.DQ_RULE_PORT_DATA_TYPES,
            CompositeCleanseFunctionEditor = Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor,
            anchor,
            endpointCfg,
            endpoint,
            dataType = DQ_RULE_PORT_DATA_TYPES.BOOLEAN,
            portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.CONDITION;

        anchor = this.buildIfThenElseAnchor(CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.CONDITION);
        endpointCfg = this.conditionEndpoint;

        endpoint = this.createIfThenElsePort(nodeId, anchor, endpointCfg, dataType, portType);

        return endpoint;
    },

    createIfThenElseInputPorts: function (nodeId, portsData) {
        var portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT,
            CompositeCleanseFunctionEditor = Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor,
            anchor,
            endpointCfg,
            portIndex,
            portCount = portsData.length,
            endpoints = [];

        for (portIndex = 0; portIndex < portCount; portIndex++) {
            anchor = this.buildIfThenElseAnchor(CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.INPUT_PORT, portsData.length, portIndex);
            endpointCfg = this.inputEndpoint;
            endpoints.push(this.createIfThenElsePort(nodeId, anchor, endpointCfg, portsData.getAt([portIndex]).getData().dataType, portType, portIndex));
        }

        return endpoints;
    },

    createIfThenElseOutputPorts: function (nodeId, portsData, isTrue) {
        var CompositeCleanseFunctionEditor = Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor,
            endpointCfg,
            anchor,
            anchorType,
            portIndex,
            portType,
            endpoints = [];

        endpointCfg = this.outputEndpoint;

        if (isTrue) {
            anchorType = CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.OUTPUT_TRUE_PORT;
            portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT_TRUE;
        } else {
            anchorType = CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.OUTPUT_FALSE_PORT;
            portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT_FALSE;
        }

        for (portIndex = 0; portIndex < portsData.length; portIndex++) {
            anchor = this.buildIfThenElseAnchor(anchorType, portsData.length, portIndex);
            endpoints.push(this.createIfThenElsePort(nodeId, anchor, endpointCfg, portsData.getAt([portIndex]).getData().dataType, portType, portIndex));
        }

        return endpoints;
    },

    buildIfThenElseAnchor: function (type, portCount, portIndex) {
        var anchor,
            CompositeCleanseFunctionEditor = Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor,
            yOffset,
            xOffset,
            x,
            y,
            dx = 0,
            dy = -1,
            sideLength = 0.5,
            step,
            precision = 2;

        if (Ext.isEmpty(portCount)) {
            portCount = 1;
            portIndex = 0;
        }

        step = Ext.util.Format.round((sideLength / (portCount + 1)) * (portIndex + 1), precision);

        switch (type) {
            case CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.CONDITION:
                dx = 0;
                dy = -1;
                xOffset = 0;
                yOffset = 0.5;
                x = xOffset + step;
                y = yOffset - step;
                break;
            case CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.INPUT_PORT:
                dx = 0;
                dy = -1;
                xOffset = 0;
                yOffset = 0.5;
                x = xOffset + step;
                y = yOffset + step;
                break;
            case CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.OUTPUT_TRUE_PORT:
                dx = 0;
                dy = -1;
                xOffset = 0.5;
                yOffset = 0;
                x = xOffset + step;
                y = yOffset + step;
                break;
            case CompositeCleanseFunctionEditor.IF_THEN_ELSE_ANCHOR_TYPE.OUTPUT_FALSE_PORT:
                dx = 0;
                dy = -1;
                xOffset = 0.5;
                yOffset = 1;
                x = xOffset + step;
                y = yOffset - step;
                break;
        }

        anchor = [x, y, dx, dy];

        return anchor;
    },

    initIfThenElseListeners: function (container, nodeId) {
        container.querySelector('.unidata-cfblock-remove-ifthenelse').addEventListener('click', this.onRemoveIfThenElse.bind(this, nodeId, container));
    },

    onRemoveIfThenElse: function (nodeId, container) {
        var title = Unidata.i18n.t('admin.cleanseFunction>deleteConstantBlock'),
            msg = Unidata.i18n.t('admin.cleanseFunction>deleteConstantBlockPrompt');

        Unidata.showPrompt(title, msg, this.removeIfThenElse, this, null, [nodeId, container]);
    },

    removeIfThenElse: function (nodeId, container) {
        var ifThenElseListCfg = this.getFromIfThenElseList(nodeId),
            endpointUuids = ifThenElseListCfg.endpointUuids;

        container.remove();
        this.removeIfThenElseEndpoints(endpointUuids);
        this.deleteFromIfThenElseList(nodeId);
    },

    removeIfThenElseEndpoints: function (uuids) {
        // TODO: сделать удаление через elId как для констант и функций
        Ext.Array.each(uuids, function (uuid) {
            this.jsPlumb.deleteEndpoint(uuid);
        }, this);
    },

    buildIfThenElsePortId: function (id, portName) {
        var elementId;

        elementId = Ext.String.format('un-ccf-{0}-{1}', id, portName);

        return elementId;
    },

    buildIfThenElsePortName: function (portIndex) {
        return 'port' + portIndex;
    },

    /**
     *
     * @param portsData
     * @return {Unidata.model.data.SimpleAttribute}
     */
    buildPortDataAttributeCfg: function (portsData) {
        var attrCfg,
            name = 'portsData',
            type = 'String';

        attrCfg = {
            name: name,
            value: JSON.stringify(portsData),
            type: type
        };

        return attrCfg;
    },

    buildUuid: function (anchor, elementId) {
        var uuid;

        uuid = Ext.String.format('{0}{1}', anchor, elementId);

        return uuid;
    }
});
