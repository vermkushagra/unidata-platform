Ext.define('Unidata.view.component.CleanseFunction', {
    extend: 'Ext.Component',

    config: {
        draftMode: null
    },

    name: '',

    cleanseFunctionList: [],
    cleanseFunctionIterator: 2,

    inputPortEndpoints: [],

    outputPortEndpoints: [],

    record: null,

    view: null,

    instance: null,

    sourceEndpoint: {
        endpoint: 'Dot',
        paintStyle: {
            strokeStyle: '#37799D',
            radius: 7
        },
        maxConnections: -1,
        isSource: true,
        connectorStyle: {
            lineWidth: 2,
            strokeStyle: '#37799D',
            joinstyle: 'round',
            outlineWidth: 2
        },
        connectorHoverStyle: {
            lineWidth: 2,
            strokeStyle: '#37799D',
            outlineWidth: 2
        },
        dragOptions: {},
        deleteEndpointsOnDetach: false
    },

    targetEndpoint: {
        endpoint: 'Dot',
        paintStyle: {fillStyle: '#37799D', radius: 7},
        maxConnections: 1,
        dropOptions: {hoverClass: 'hover', activeClass: 'active'},
        isTarget: true,
        deleteEndpointsOnDetach: false
    },

    setRecord: function (record) {
        this.record = record;
        this.drawOutputPorts();
        this.drawInputPorts();
        this.initCleanseFunction();
    },

    getRecord: function () {
        return this.prepareRecord();
    },

    setView: function (view) {
        this.view = view;
    },

    refresh: function () {
        jsPlumb.repaintEverything();
        this.instance.repaintEverything();
    },

    initCleanseFunction: function () {
        var me = this,
            el = this.view.getEl(),
            width = el.getWidth(),
            height = el.getHeight(),
            size = 0,
            loaded = 0;

        this.cleanseFunctionList = [];
        this.record.getLogic().nodes().each(function (node) {
            if (node.get('nodeType') !== 'FUNCTION') {
                return;
            }
            size++;
            Unidata.model.cleansefunction.CleanseFunction.load(node.get('functionName'), {
                params: {
                    draft: me.getDraftMode()
                },
                success: function (record) {
                    var position = node.get('uiRelativePosition').split(',');

                    record.nodeInternalId = node.internalId;
                    me.addCleanseFunction(record, {
                        x: width * position[1] / 100,
                        y: height * position[0] / 100
                    }, function () {
                        loaded++;

                        if (loaded === size) {
                            me.setLinksConnects();
                        }
                    });
                }
            });
        });
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

    //@TODO: Refact. part
    setLinksConnects: function () {
        var me = this,
            nodes = this.record.getLogic().nodes(),
            getUuid = function (nodeId, port, type) {
                var node = nodes.findRecord('nodeId', nodeId),
                    uuid;

                //@TODO must fix
                switch (node.get('nodeType')) {
                    case 'INPUT_PORTS':
                        uuid = 'RightMiddle' + 'unidata-inputport-' + port;
                        break;
                    case 'OUTPUT_PORTS':
                        uuid = 'LeftMiddle' + 'unidata-outputport-' + port;
                        break;
                    case 'FUNCTION':
                        var func = Ext.Array.findBy(me.cleanseFunctionList, function (cf) {
                            return node.internalId === cf.nodeInternalId;
                        });

                        uuid = (type === 'from' ?
                                'RightMiddleunidata-cfblock-outputport' : 'LeftMiddleunidata-cfblock-inputport') + '-' +
                            'cfblock-' + func.internalId + port;
                        break;
                }

                return uuid;
            };

        this.record.getLogic().links().each(function (link) {
            var from = getUuid(link.get('fromNodeId'), link.get('fromPort'), 'from'),
                to = getUuid(link.get('toNodeId'), link.get('toPort'), 'to');

            me.instance.connect({
                uuids: [from, to]
            });
        });
    },

    prepareRecord: function () {
        var me = this,
            nodes = me.record.getLogic().nodes(),
            links = me.record.getLogic().links();

        nodes.clearData();
        links.clearData();

        nodes.add(
            {
                nodeId: 0,
                nodeType: 'INPUT_PORTS'
            },
            {
                nodeId: 1,
                nodeType: 'OUTPUT_PORTS'
            });

        this.cleanseFunctionList.forEach(function (cleanseFunction) {
            nodes.add({
                nodeId: cleanseFunction.nodeId,
                nodeType: 'FUNCTION',
                functionName: cleanseFunction.get('tempId'),
                uiRelativePosition: me.getPosition(cleanseFunction.id)
            });
        });

        this.instance.getConnections().forEach(function (connect) {
            var from = connect.source.dataset,
                to = connect.target.dataset;

            links.add({
                fromNodeId: +from.nodeId,
                fromPort: from.portName,
                toNodeId: +to.nodeId,
                toPort: to.portName
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

        this.instance.getConnections().forEach(function (connect) {
            var from = connect.source.dataset,
                to = connect.target.dataset;

            links.push({
                fromNodeId: +from.nodeId,
                fromPort: from.portName,
                fromRequired: (from.portRequired === 'true'),
                toPort: to.portName,
                toNodeId: +to.nodeId,
                toRequired: (to.portRequired === 'true')
            });

            if (+from.nodeId === +to.nodeId) {
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

        this.cleanseFunctionList.forEach(function (cleanseFunction) {
            var input, output;

            cleanseFunction.inputPorts.forEach(function (port) {
                if (port.required) {
                    input = Ext.Array.findBy(links, function (link) {
                        return link.toNodeId === cleanseFunction.nodeId &&
                            link.toPort === port.name &&
                            link.toRequired === link.fromRequired;
                    });

                    return input;
                }
            });

            cleanseFunction.outputPorts.forEach(function (port) {
                if (port.required) {
                    output = Ext.Array.findBy(links, function (link) {
                        return link.fromNodeId === cleanseFunction.nodeId &&
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

    setInstance: function () {
        var me = this;

        this.instance = jsPlumb.getInstance({
            DragOptions: {
                cursor: 'pointer',
                zIndex: 2000
            },
            Container: this.view.id
        });

        //@TODO Error event connection
        this.instance.bind('connection', function (info) {
            if (!me.validationPorts(info.source.dataset.portDatatype, info.target.dataset.portDatatype)) {
                alert(Unidata.i18n.t('admin.cleanseFunction>notEqualDataTypes'));
            }
        });
    },

    /**
     * @param port1
     * @param port2
     * @returns {boolean}
     */
    validationPorts: function (port1, port2) {
        return port1 === port2 ? true : false;
    },

    drawOutputPorts: function () {
        var me = this,
            id = 'cfblock-outputports-block';

        this.getTemplate('component/CleanseFunction/outputPorts.tpl', function (template) {
            if (!Ext.get(id)) {
                Ext.DomHelper.append(me.view.getEl().dom, {
                    tag: 'div', id: id, 'class': 'unidata-outputports-container'
                });
            }

            me.outputPortEndpoints.forEach(function (endpoint) {
                me.instance.deleteEndpoint(endpoint);
            });
            me.outputPortEndpoints = [];

            me.record.outputPorts().each(function (port) {
                port.set('endpointId', Ext.id(null, 'unidata-outputport-id-'));
            });

            template.overwrite(id, {outputPorts: Ext.pluck(me.record.outputPorts().data.items, 'data')});

            me.record.outputPorts().each(function (port) {
                var uuid = 'LeftMiddle' + 'unidata-outputport-' + port.get('name');

                me.outputPortEndpoints.push(uuid);
                me.instance.addEndpoint(port.get('endpointId'), me.targetEndpoint, {
                    anchor: 'LeftMiddle', uuid: uuid
                });
            });
        });
    },

    drawInputPorts: function () {
        var me = this,
            id = 'cfblock-inputports-block';

        this.getTemplate('component/CleanseFunction/inputPorts.tpl', function (template) {
            if (!Ext.get(id)) {
                Ext.DomHelper.append(me.view.getEl().dom, {
                    tag: 'div', id: id, 'class': 'unidata-inputports-container'
                });
            }

            me.inputPortEndpoints.forEach(function (endpoint) {
                me.instance.deleteEndpoint(endpoint);
            });
            me.inputPortEndpoints = [];

            me.record.inputPorts().each(function (port) {
                port.set('endpointId', Ext.id(null, 'unidata-inputport-id-'));
            });

            template.overwrite(id, {inputPorts: Ext.pluck(me.record.inputPorts().data.items, 'data')});

            me.record.inputPorts().each(function (port) {
                var uuid = 'RightMiddle' + 'unidata-inputport-' + port.get('name');

                me.inputPortEndpoints.push(uuid);
                me.instance.addEndpoint(port.get('endpointId'), me.sourceEndpoint, {
                    anchor: 'RightMiddle', uuid: uuid
                });
            });
        });
    },

    /**
     *
     * @param cleanseFunction
     * @param position
     * @param callback
     */
    addCleanseFunction: function (cleanseFunction, position, callback) {
        var me = this,
            id = 'cfblock-' + cleanseFunction.internalId,
            nestedData = cleanseFunction.getAssociatedData();

        this.getTemplate('component/CleanseFunction/cleanseFunction.tpl', function (template) {

            for (var i in nestedData) {
                cleanseFunction[i] = nestedData[i];
            }

            var container = Ext.DomHelper.append(me.view.getEl().dom, {
                tag: 'div',
                id: id,
                'class': 'unidata-cfblock-container'
            });

            container.style.top = position.y + 'px';
            container.style.left = position.x + 'px';

            cleanseFunction.id = id;
            cleanseFunction.nodeId = me.cleanseFunctionIterator++;
            template.overwrite(id, cleanseFunction);

            container.querySelector('.unidata-cfblock-remove').onclick = function () {
                container.remove();
                cleanseFunction._inputPorts.each(function (ports) {
                    me.instance.deleteEndpoint('LeftMiddleunidata-cfblock-inputport-' + id + ports.get('name'));
                });
                cleanseFunction._outputPorts.each(function (ports) {
                    me.instance.deleteEndpoint('RightMiddleunidata-cfblock-outputport-' + id + ports.get('name'));
                });
                Ext.Array.remove(me.cleanseFunctionList, cleanseFunction);
            };

            cleanseFunction._inputPorts.each(function (ports) {
                var pointId = 'unidata-cfblock-inputport-' + id + ports.get('name');

                me.instance.addEndpoint(pointId, me.targetEndpoint, {
                    anchor: 'LeftMiddle', uuid: 'LeftMiddle' + pointId
                });
            });

            cleanseFunction._outputPorts.each(function (ports) {
                var pointId = 'unidata-cfblock-outputport-' + id + ports.get('name');

                me.instance.addEndpoint(pointId, me.sourceEndpoint, {
                    anchor: 'RightMiddle', uuid: 'RightMiddle' + pointId
                });
            });

            me.cleanseFunctionList.push(cleanseFunction);
            me.repaintPoints();

            typeof callback === 'function' && callback();
        });
    },

    repaintPoints: function () {
        jsPlumb.repaintEverything();
        this.instance.repaintEverything();

        //@TODO
        this.instance.draggable(jsPlumb.getSelector('.unidata-cfblock-container'));
    }

});
