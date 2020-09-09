/**
 * Редактор композитных функций
 *
 * @author Igor Redkin
 * @date 2015
 *
 * @author Sergey Shishigin
 * @date 02.04.2018
 */
Ext.define('Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionEditor', {
    extend: 'Ext.Component',

    requires: [
        'Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionTemplate',
        'Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionConstantWindow',
        'Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionIfThenElseWindow',

        'Unidata.view.admin.compositeCleanseFunction.editor.cfblock.Constant',
        'Unidata.view.admin.compositeCleanseFunction.editor.cfblock.IfThenElse',
        'Unidata.view.admin.compositeCleanseFunction.editor.cfblock.Function',
        'Unidata.view.admin.compositeCleanseFunction.editor.cfblock.PortBlock'
    ],

    config: {
        draftMode: null
    },

    name: '',

    cleanseFunctionUtil: null,
    constantUtil: null,
    ifThenElseUtil: null,

    nodeSeqIdentifier: null,

    record: null,

    view: null,

    jsPlumb: null,

    initComponent: function () {
        this.callParent(arguments);
        this.nodeSeqIdentifier = Ext.create('Ext.data.identifier.Sequential');
    },

    getConstantUtil: function () {
        if (!this.constantUtil) {
            this.constantUtil = Ext.create('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.Constant', {
                jsPlumb: this.jsPlumb,
                view: this.view,
                sourceEndpoint: this.buildConstantSourceEndpoint(),
                nodeSeqIdentifier: this.nodeSeqIdentifier
            });
        }

        return this.constantUtil;
    },

    getIfThenElseUtil: function () {
        if (!this.ifThenElseUtil) {
            this.ifThenElseUtil = Ext.create('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.IfThenElse', {
                jsPlumb: this.jsPlumb,
                view: this.view,
                conditionEndpoint: this.buildIfThenElseConditionEndpointCfg(),
                inputEndpoint: this.buildTargetEndpointCfg(),
                outputEndpoint: this.buildSourceEndpointCfg(),
                nodeSeqIdentifier: this.nodeSeqIdentifier
            });
        }

        return this.ifThenElseUtil;
    },

    getCleanseFunctionUtil: function () {
        if (!this.cleanseFunctionUtil) {
            this.cleanseFunctionUtil = Ext.create('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.Function', {
                jsPlumb: this.jsPlumb,
                view: this.view,
                inputEndpoint: this.buildTargetEndpointCfg(),
                outputEndpoint: this.buildSourceEndpointCfg(),
                nodeSeqIdentifier: this.nodeSeqIdentifier
            });
        }

        return this.cleanseFunctionUtil;
    },

    getPortBlockUtil: function () {
        if (!this.portBlockUtil) {
            this.portBlockUtil = Ext.create('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.PortBlock', {
                view: this.view,
                jsPlumb: this.jsPlumb,
                inputEndpoint: this.buildTargetEndpointCfg(),
                outputEndpoint: this.buildSourceEndpointCfg(),
                record: this.record
            });
        }

        return this.portBlockUtil;
    },

    buildSourceEndpointCfg: function () {
        var endpoint;

        endpoint = {
            endpoint: 'Dot',
            paintStyle: {
                fillStyle: '#fff',
                strokeStyle: '#37799D',
                radius: 6
            },
            maxConnections: -1,
            isSource: true,
            isTarget: false,
            connectorStyle: {
                lineWidth: 1,
                strokeStyle: '#37799D',
                joinstyle: 'round',
                outlineWidth: 1
            },
            connectorHoverStyle: {
                lineWidth: 1,
                strokeStyle: '#37799D',
                outlineWidth: 1
            },
            dragOptions: {},
            deleteEndpointsOnDetach: false
        };

        return endpoint;
    },

    buildTargetEndpointCfg: function () {
        var endpoint;

        endpoint = {
            endpoint: 'Dot',
            paintStyle: {fillStyle: '#37799D', radius: 6},
            maxConnections: -1,
            dropOptions: {hoverClass: 'hover', activeClass: 'active'},
            isTarget: true,
            isSource: false,
            deleteEndpointsOnDetach: false
        };

        return endpoint;
    },

    buildConstantSourceEndpoint: function () {
        var endpoint,
            constantEndpoint;

        endpoint = this.buildSourceEndpointCfg();
        constantEndpoint = Ext.Object.merge(endpoint, {
            paintStyle: {
                strokeStyle: '#f3730b'
            },
            connectorStyle: {
                strokeStyle: '#f3730b'
            },
            connectorHoverStyle: {
                strokeStyle: '#f3730b'
            }
        });

        return constantEndpoint;
    },

    buildIfThenElseConditionEndpointCfg: function () {
        var endpoint;

        endpoint = {
            endpoint: 'Rectangle',
            paintStyle: {
                fillStyle: '#f3730b',
                strokeStyle: '#fff',
                strokeWidth: 2,
                width: 12,
                height: 12
            },
            maxConnections: 1,
            dropOptions: {hoverClass: 'hover', activeClass: 'active'},
            isTarget: true,
            isSource: false,
            deleteEndpointsOnDetach: false
        };

        return endpoint;
    },

    loadAllCleanseFunctions: function () {
        var CleanseFunctionApi = Unidata.util.api.CleanseFunction,
            nodes = this.getNodes(),
            promises;

        promises = Ext.Array.map(nodes.getRange(), function (node) {
            return CleanseFunctionApi.loadCleanseFunction(node.get('functionName'), this.getDraftMode());
        }, this);

        return Ext.Deferred.all(promises);
    },

    setRecord: function (record) {
        var me = this,
            maxNodeId,
            cleanseFunctionUtil = this.getCleanseFunctionUtil(),
            constantUtil = this.getConstantUtil(),
            ifThenElseUtil = this.getIfThenElseUtil();

        cleanseFunctionUtil.clearCleanseFunctionList();
        constantUtil.clearConstantList();
        ifThenElseUtil.clearIfThenElseList();
        this.record = record;
        this.loadAllCleanseFunctions()
            .then(function (cleanseFunctions) {
                me.drawInputPorts();
                me.drawOutputPorts();
                me.initNodes(cleanseFunctions);
                maxNodeId = me.getMaxNodeId();
                me.nodeSeqIdentifier.setSeed(maxNodeId + 1);
                me.setLinksConnects();
            }, function () {
                Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>functionsLoadError'));
            })
            .done();
    },

    /**
     * Получить узлы композитной функции
     * @return {*}
     */
    getNodes: function () {
        var record = this.record,
            logic = record.getLogic(),
            nodes = logic.nodes();

        return nodes;
    },

    /**
     * Получить максимальный номер id
     * @return {number}
     */
    getMaxNodeId: function () {
        var nodes = this.getNodes().getRange(),
            nodeIds,
            maxNodeId = 1;

        nodeIds = Ext.Array.map(nodes, function (node) {
            return node.get('nodeId');
        });

        if (nodeIds.length > 0) {
            maxNodeId = Ext.Array.max(nodeIds);
        }

        return maxNodeId;
    },

    getRecord: function () {
        return this.prepareRecord();
    },

    setView: function (view) {
        this.view = view;
    },

    refresh: function () {
        jsPlumb.repaintEverything();
        this.jsPlumb.repaintEverything();
    },

    initNodes: function (cleanseFunctions) {
        var CompositeCleanseFunction = Unidata.view.admin.compositeCleanseFunction.CompositeCleanseFunction,
            me = this,
            el = this.view.getEl(),
            width = el.getWidth(),
            height = el.getHeight(),
            nodes = this.getNodes(),
            ifThenElsePorts;

        nodes.each(function (node) {
            var nodeType = node.get('nodeType'),
                nodeId = node.get('nodeId'),
                functionName = node.get('functionName'),
                uiRelativePosition = node.get('uiRelativePosition'),
                availableNodeTypes = [CompositeCleanseFunction.CCF_BLOCK_TYPE.FUNCTION,
                                      CompositeCleanseFunction.CCF_BLOCK_TYPE.CONSTANT,
                                      CompositeCleanseFunction.CCF_BLOCK_TYPE.IFTHENELSE],
                constant,
                position,
                cleanseFunction;

            if (!Ext.Array.contains(availableNodeTypes, nodeType)) {
                return;
            }

            position = uiRelativePosition.split(',');
            position = {
                x: width * position[1] / 100,
                y: height * position[0] / 100
            };

            if (nodeType === CompositeCleanseFunction.CCF_BLOCK_TYPE.FUNCTION) {
                cleanseFunction = Ext.Array.findBy(cleanseFunctions, function (cleanseFunction) {
                    return cleanseFunction && cleanseFunction.get('fullName') === functionName;
                });

                if (!cleanseFunction) {
                    Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>functionFindError', {name: functionName}));

                    return;
                }
                me.addCleanseFunction(cleanseFunction, position, nodeId);
            } else if (nodeType === CompositeCleanseFunction.CCF_BLOCK_TYPE.CONSTANT) {
                constant = node.getValue();
                me.addConstant(constant, position, nodeId);
            } else if (nodeType === CompositeCleanseFunction.CCF_BLOCK_TYPE.IFTHENELSE) {
                ifThenElsePorts = me.buildIfThenElsePorts(node);

                if (ifThenElsePorts) {
                    me.addIfThenElse(ifThenElsePorts, position, nodeId);
                }

            }
        });
    },

    buildIfThenElsePorts: function (node) {
        var ifThenElseStore,
            portsDataAttr = node.getValue(),
            portsData,
            portsDataRaw,
            portsNumber,
            i;

        ifThenElseStore = Ext.create('Ext.data.Store', {
            autoDestroy: true,
            model: 'Unidata.model.cleansefunction.IfThenElsePort',
            proxy: {
                type: 'memory'
            },
            data: []
        });

        //кейс, когда у нас сохранено только количество портов для старых Cleanse-функций
        if (portsDataAttr.get('type') === 'Integer') {
            portsNumber = portsDataAttr.get('value');

            for (i = 0; i < portsNumber; i++) {
                ifThenElseStore.add({
                    dataType: 'Any'
                });
            }
        } else {
            portsDataRaw = portsDataAttr.get('value');
            portsData = JSON.parse(portsDataRaw).ports;

            Ext.Array.forEach(portsData, function (dataType) {
                ifThenElseStore.add({
                    dataType: dataType
                });
            });
        }

        return ifThenElseStore.getData();
    },
    /**
     * @param elementId
     * @returns {string}
     */
    getPosition: function (elementId) {
        var el = this.view.getEl(),
            elW = el.getWidth(),
            elH = el.getHeight(),
            block = Ext.get(elementId),
            blockT = block.getLocalY(),
            blockL = block.getLocalX();

        return (blockT / elH * 100) + ',' + (blockL / elW * 100);
    },

    findUuid: function (nodeId, portName, portType) {
        var me = this,
            nodes = this.getNodes(),
            node,
            uuid,
            portId,
            id,
            anchor,
            cleanseFunctionUtil = this.getCleanseFunctionUtil(),
            constantUtil = this.getConstantUtil(),
            ifThenElseUtil = this.getIfThenElseUtil();

        switch (portType) {
            case Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT:
                anchor = 'LeftMiddle';
                break;
            case Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT:
                anchor = 'RightMiddle';
                break;
        }

        node = nodes.findRecord('nodeId', nodeId);
        //@TODO must fix
        switch (node.get('nodeType')) {
            case 'INPUT_PORTS':
                uuid = me.buildUuid('RightMiddle', me.buildPortId('inputports', portType, portName));
                break;
            case 'OUTPUT_PORTS':
                uuid = me.buildUuid('LeftMiddle', me.buildPortId('outputports', portType, portName));
                break;
            case 'FUNCTION':
                id = cleanseFunctionUtil.buildCleanseFunctionId(nodeId);
                uuid = me.buildUuid(anchor, me.buildPortId(id, portType, portName));
                break;
            case 'CONSTANT':
                id = constantUtil.buildConstantId(nodeId);
                uuid = me.buildUuid(anchor, me.buildPortId(id, portType, constantUtil.buildConstantPortName()));
                break;
            case 'IFTHENELSE':
                id = ifThenElseUtil.buildIfThenElseId(nodeId);
                portId = ifThenElseUtil.buildIfThenElsePortId(id, portName);
                uuid = ifThenElseUtil.buildIfThenElseUuid(portType, portId);
                break;
        }

        return uuid;
    },

    /**
     * Создать связи между блоками
     */
    setLinksConnects: function () {
        var links = this.record.getLogic().links();

        links.each(this.createLink.bind(this));
    },

    /**
     * Создат связь
     * @param link
     */
    createLink: function (link) {
        var fromNodeId = link.get('fromNodeId'),
            fromPort   = link.get('fromPort'),
            fromPortType   = link.get('fromPortType'),
            toNodeId   = link.get('toNodeId'),
            toPort     = link.get('toPort'),
            toPortType     = link.get('toPortType'),
            fromUuid,
            toUuid;

        fromUuid = this.findUuid(fromNodeId, fromPort, fromPortType);
        toUuid = this.findUuid(toNodeId, toPort, toPortType);

        this.jsPlumb.connect({
            uuids: [fromUuid, toUuid]
        });
    },

    prepareRecord: function () {
        var me = this,
            nodes = me.getNodes(),
            links = me.record.getLogic().links(),
            cleanseFunctionUtil = this.cleanseFunctionUtil,
            cleanseFunctionList = cleanseFunctionUtil.getCleanseFunctionList(),
            constantUtil = this.constantUtil,
            constantList = constantUtil.getConstantList(),
            ifThenElseUtil = this.getIfThenElseUtil(),
            ifThenElseList = ifThenElseUtil.ifThenElseList;

        nodes.clearData();
        links.clearData();

        // TODO: nodeType to enum
        nodes.add(
            {
                nodeId: 0,
                nodeType: 'INPUT_PORTS'
            },
            {
                nodeId: 1,
                nodeType: 'OUTPUT_PORTS'
            });

        // добавляем блоки cleanse функций
        Ext.Object.each(cleanseFunctionList, function (nodeId, cleanseFunctionInfo) {
            nodes.add({
                nodeId: nodeId,
                nodeType: 'FUNCTION',
                functionName: cleanseFunctionInfo.cleanseFunction.get('tempId'),
                uiRelativePosition: me.getPosition(cleanseFunctionInfo.id)
            });
        });

        // добавляем блоки констант
        Ext.Object.each(constantList, function (nodeId, constant) {
            var id = constantUtil.buildConstantId(nodeId);

            nodes.add({
                nodeId: nodeId,
                nodeType: 'CONSTANT',
                value: constant.getData(),
                uiRelativePosition: me.getPosition(id)
            });
        });

        // добавляем блоки if-then-else
        Ext.Object.each(ifThenElseList, function (nodeId, ifThenElseInfo) {
            var id,
                portsData = ifThenElseInfo.portsData,
                portsDataAttr;

            id = ifThenElseUtil.buildIfThenElseId(nodeId);
            portsDataAttr = ifThenElseUtil.buildPortDataAttributeCfg(portsData);

            nodes.add({
                nodeId: nodeId,
                nodeType: 'IFTHENELSE',
                value: portsDataAttr,
                uiRelativePosition: me.getPosition(id)
            });
        });

        this.jsPlumb.getConnections().forEach(function (conn) {
            var sourceEndpoint = conn.endpoints[0],
                targetEndpoint = conn.endpoints[1],
                sourcePortName = sourceEndpoint.getParameter('portName'),
                targetPortName = targetEndpoint.getParameter('portName'),
                sourcePortType = sourceEndpoint.getParameter('portType'),
                targetPortType = targetEndpoint.getParameter('portType'),
                sourceNodeId = sourceEndpoint.getParameter('nodeId'),
                targetNodeId = targetEndpoint.getParameter('nodeId');

            links.add({
                fromNodeId: Number(sourceNodeId),
                fromPort: sourcePortName,
                fromPortType: sourcePortType,
                toNodeId: Number(targetNodeId),
                toPort: targetPortName,
                toPortType: targetPortType
            });
        });

        return this.record;
    },

    checkValid: function () {
        var links = [],
            cycle = false,
            errorFunction = '',
            inputPortsRequired = false,
            outputPortsRequired = false;

        this.record.inputPorts().each(function (port) {
            return (inputPortsRequired = port.get('required')) ? false : true;
        });

        this.record.outputPorts().each(function (port) {
            return (outputPortsRequired = port.get('required')) ? false : true;
        });

        if (!inputPortsRequired || !outputPortsRequired) {
            return {
                valid: false,
                errorText: Unidata.i18n.t('admin.cleanseFunction>checkRequirePortError')
            };
        }

        this.jsPlumb.getConnections().forEach(function (connect) {
            var from = connect.source.dataset,
                to = connect.target.dataset;

            links.push({
                fromNodeId: Number(from.nodeId),
                fromPort: from.portName,
                fromRequired: (from.portRequired === 'true'),
                toPort: to.portName,
                toNodeId: Number(to.nodeId),
                toRequired: (to.portRequired === 'true')
            });

            if (Number(from.nodeId) === Number(to.nodeId)) {
                cycle = true;

                return false;
            }
        });

        if (cycle) {
            return {
                valid: false,
                errorText: Unidata.i18n.t('admin.cleanseFunction>cantSaveCircularFunction')
            };
        }

        Ext.Object.eachValue(this.cleanseFunctionList, function (cleanseFunctionInfo) {
            var input,
                output,
                cleanseFunction = cleanseFunctionInfo.cleanseFunction;

            cleanseFunction.inputPorts.forEach(function (port) {
                if (port.required) {
                    input = Ext.Array.findBy(links, function (link) {
                        return link.toNodeId === cleanseFunctionInfo.nodeId &&
                            link.toPort === port.name &&
                            link.toRequired === link.fromRequired;
                    });

                    return input;
                }
            });

            cleanseFunction.outputPorts.forEach(function (port) {
                if (port.required) {
                    output = Ext.Array.findBy(links, function (link) {
                        return link.fromNodeId === cleanseFunctionInfo.nodeId &&
                            link.fromPort === port.name &&
                            link.toRequired === link.fromRequired;
                    });

                    return output;
                }
            });

            if (!input || !output) {
                errorFunction = cleanseFunction.get('name');

                return false;
            }
        });

        return errorFunction ? {
            valid: false,
            errorText: Unidata.i18n.t('admin.cleanseFunction>invalidFunction', {name: errorFunction})
        } : {
            valid: true
        };

    },

    initJsPlumb: function () {
        this.jsPlumb = jsPlumb.getInstance({
            DragOptions: {
                cursor: 'pointer',
                zIndex: 2000
            },
            Connector: ['Bezier', {
                curviness: 30
            }],
            Container: this.view.id
        });

        // проверяем можно ли создать связь между коннекторами на основании их типов
        this.jsPlumb.bind('beforeDrop', this.onBeforeDrop.bind(this));
    },

    onBeforeDrop: function (info) {
        var DQ_RULE_PORT_DATA_TYPES = Unidata.model.dataquality.DqRule.DQ_RULE_PORT_DATA_TYPES,
            conn       = info.connection,
            sourceDataType = conn.endpoints[0].getParameter('dataType'),
            targetDataType = info.dropEndpoint.getParameter('dataType');

        if (Ext.isEmpty(sourceDataType)) {
            Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>sourceDataTypesIsEmpty'));

            return false;
        }

        if (Ext.isEmpty(targetDataType)) {
            Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>targetDataTypesIsEmpty'));

            return false;
        }

        if (sourceDataType !== DQ_RULE_PORT_DATA_TYPES.ANY &&
            targetDataType !== DQ_RULE_PORT_DATA_TYPES.ANY &&
            sourceDataType !== targetDataType) {

            Unidata.showError(Unidata.i18n.t('admin.cleanseFunction>notEqualDataTypes', {sourceType: sourceDataType, targetType: targetDataType}));

            return false;
        }

        return true;
    },

    addConstant: function (constant, position, nodeId) {
        var constantUtil = this.getConstantUtil(),
            el;

        el = constantUtil.addConstant(constant, position, nodeId);
        this.applyDraggable(el);
    },

    addIfThenElse: function (portsData, position, nodeId) {
        var ifThenElseUtil = this.getIfThenElseUtil(),
            el;

        el = ifThenElseUtil.addIfThenElse(portsData, position, nodeId);
        this.applyDraggable(el);
    },

    addCleanseFunction: function (cleanseFunction, position, nodeId) {
        var CleanseFunctionUtil = this.getCleanseFunctionUtil(),
            el;

        el = CleanseFunctionUtil.addCleanseFunction(cleanseFunction, position, nodeId);
        this.applyDraggable(el);
    },

    /**
     * Построить id (html) элемента
     * @param portType
     * @param id {String}
     * @param portName {String}
     * @return {String}
     */
    buildPortId: function (id, portType, portName) {
        var elementId,
            type;

        switch (portType) {
            case Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT:
                type = 'in';
                break;
            case Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT:
                type = 'out';
                break;
        }

        elementId = Ext.String.format('un-ccf-{0}-{1}putport-{2}', id, type, portName);

        return elementId;
    },

    buildUuid: function (anchor, elementId) {
        var uuid;

        uuid = Ext.String.format('{0}{1}', anchor, elementId);

        return uuid;
    },

    /**
     * Делает элемент el перетаскиваем
     *
     * @param el
     */
    applyDraggable: function (el) {
        this.jsPlumb.draggable(el);
    },

    statics: {
        IF_THEN_ELSE_ANCHOR_TYPE: {
            CONDITION: 'if_then_else_anchor_condition',
            INPUT_PORT: 'if_then_else_anchor_input_port',
            OUTPUT_TRUE_PORT: 'if_then_else_anchor_output_true_port',
            OUTPUT_FALSE_PORT: 'if_then_else_anchor_output_false_port'
        },
        IF_THEN_ELSE_SIZE: {
            SMALL: 'small',
            MEDIUM: 'medium',
            LARGE: 'large',
            EXTRALARGE: 'extralarge'
        }
    },

    editIfThenElsePorts: function (position) {
        var okCallback;

        okCallback = this.onApplyPortCountButtonClick.bind(this);
        this.getIfThenElseUtil().editIfThenElsePorts(position, okCallback);
    },

    onApplyPortCountButtonClick: function (self, portsData) {
        var el      = self.getEl(),
            elX     = el.getX(),
            elY     = el.getY(),
            x,
            y;

        x = elX;
        y = elY;
        this.addIfThenElse(portsData, {x: x, y: y});
    },

    drawOutputPorts: function () {
        var PortBlockUtil = this.getPortBlockUtil();

        PortBlockUtil.drawPorts('out', 1);
    },

    drawInputPorts: function () {
        var PortBlockUtil = this.getPortBlockUtil();

        PortBlockUtil.drawPorts('in', 0);
    },

    recalcBlockPositions: function (newWidth, oldWidth) {
        var me = this,
            domElements,
            oldX,
            percent,
            isEnlargement = newWidth > oldWidth ,
            newX;

        domElements = Ext.query('.unidata-cfblock-container', false);
        this.jsPlumb.batch(function () {
            Ext.Array.each(domElements, function (domElement) {
                oldX    = domElement.getX();
                percent = me.convertWidthToPercent(oldX, oldWidth);
                newX    = me.convertPercentToWidth(percent, newWidth);

                if (isEnlargement || (oldX + domElement.getWidth()) > newWidth) {
                    domElement.move('r', newX - oldX);
                }
            }, this);
        });
    },

    convertWidthToPercent: function (width, fullWidth) {
        return width * 100 / fullWidth;
    },

    convertPercentToWidth: function (percent, fullWidth) {
        return fullWidth * percent / 100;
    }
});
