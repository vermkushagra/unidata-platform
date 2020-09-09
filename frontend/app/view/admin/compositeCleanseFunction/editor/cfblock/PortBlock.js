/**
 * Модуль построения блока входных/выходных портов
 *
 * @author Sergey Shishigin
 * @date 2018-04-13
 */
Ext.define('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.PortBlock', {

    view: null,
    jsPlumb: null,
    inputEndpoint: null,
    outputEndpoint: null,
    inputPortIds: [],
    outputPortIds: [],
    record: null,

    constructor: function (data) {
        this.view = data.view;
        this.jsPlumb = data.jsPlumb;
        this.inputEndpoint = data.inputEndpoint;
        this.outputEndpoint = data.outputEndpoint;
        this.record = data.record;
    },

    drawPorts: function (type, nodeId) {
        var CleanseFunctionTemplate = Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionTemplate,
            me = this,
            id,
            template,
            cleanseFunction = me.record,
            tpl,
            ports,
            portIds,
            portType,
            anchor,
            endpoint,
            data = {},
            connectionUuids = {},
            portConnections,
            selector,
            container;

        switch (type) {
            case 'in':
                ports = cleanseFunction.inputPorts();
                portIds = me.inputPortIds;
                anchor = 'RightMiddle';
                tpl = CleanseFunctionTemplate.inputPortsTpl;
                endpoint = me.outputEndpoint;
                break;
            case 'out':
                ports = cleanseFunction.outputPorts();
                portIds = me.outputPortIds;
                anchor = 'LeftMiddle';
                tpl = CleanseFunctionTemplate.outputPortsTpl;
                endpoint = me.inputEndpoint;
                break;
            default:
            //TODO: error
        }

        id = Ext.String.format('cfblock-{0}putports-block', type);
        template = Ext.create('Ext.XTemplate', tpl);
        container = Ext.get(id);

        portIds.forEach(function (portId) {
            switch (type) {
                case 'in':
                    selector = {source: portId};
                    break;
                case 'out':
                    selector = {target: portId};
                    break;
            }
            portConnections = me.jsPlumb.getConnections(selector);
            // сохраняем информацию о соединениях для последующего восстановления
            connectionUuids[portId] = Ext.Array.map(portConnections, function (portConnection) {
                return portConnection.getUuids();
            });
            me.jsPlumb.remove(portId);
        });

        if (container) {
            container.remove();
        }

        Ext.DomHelper.append(me.view.getEl().dom, {
            tag: 'div', id: id, 'class': Ext.String.format('unidata-{0}putports-container', type)
        });

        switch (type) {
            case 'in':
                me.inputPortIds = [];
                portIds   = me.inputPortIds;
                portType        = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT;
                break;
            case 'out':
                me.outputPortIds = [];
                portIds    = me.outputPortIds;
                portType         = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT;
                break;
        }

        ports.each(function (port) {
            port.set('endpointId', Ext.id(null, Ext.String.format('unidata-{0}putport-id-', type)));
        });

        data[type + 'putPorts'] = Ext.pluck(ports.data.items, 'data');
        template.overwrite(id, data);

        ports.each(function (port) {
            var portId,
                uuid,
                dataType = port.get('dataType'),
                portName = port.get('name');

            portId = me.buildPortId(type + 'putports', portType, portName);
            uuid = me.buildUuid(anchor, portId);
            portIds.push(portId);
            me.jsPlumb.addEndpoint(portId, endpoint, {
                anchor: anchor,
                uuid: uuid,
                parameters: {
                    portName: portName,
                    portType: portType,
                    nodeId: nodeId,
                    dataType: dataType
                }
            });

            // восстанавливаем соединения
            if (connectionUuids[portId]) {
                Ext.Array.every(connectionUuids[portId], function (uuids) {
                    me.jsPlumb.connect({
                        uuids: uuids
                    });
                });
            }
        });
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

    /**
     * blblb
     *
     * @param anchor
     * @param elementId
     * @return {*}
     */
    buildUuid: function (anchor, elementId) {
        var uuid;

        uuid = Ext.String.format('{0}{1}', anchor, elementId);

        return uuid;
    }
});
