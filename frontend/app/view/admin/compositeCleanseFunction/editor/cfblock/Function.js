/**
 * Модуль построения блока функций
 *
 * @author Sergey Shishigin
 * @date 2018-04-13
 */
Ext.define('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.Function', {
    cleanseFunctionList: {},

    jsPlumb: null,
    view: null,
    inputEndpoint: null,
    outputEndpoint: null,
    nodeSeqIdentifier: null,

    constructor: function (data) {
        this.jsPlumb = data.jsPlumb;
        this.view = data.view;
        this.inputEndpoint = data.inputEndpoint;
        this.outputEndpoint = data.outputEndpoint;
        this.nodeSeqIdentifier = data.nodeSeqIdentifier;
    },

    /**
     * Получить ассоциативный массив cleanse функций
     * @return {Object|null}
     */
    getCleanseFunctionList: function () {
        if (!Ext.isObject(this.cleanseFunctionList)) {
            this.cleanseFunctionList = {};
        }

        return this.cleanseFunctionList;
    },

    /**
     * Поместить cleanse функцию в список
     *
     * @param {String} nodeId
     * @param {Object} cleanseFunctionInfo
     * @params {Boolean} override
     */
    putInCleanseFunctionList: function (nodeId, cleanseFunctionInfo, override) {
        var cleanseFunctionList = this.getCleanseFunctionList();

        override = Ext.isBoolean(override) ? override : true;

        if (override) {
            cleanseFunctionList[nodeId] = cleanseFunctionInfo;
        }
    },

    /**
     * Удалить из списка cleanse функций
     *
     * @param {Integer} nodeId
     */
    deleteFromCleanseFunctionList: function (nodeId) {
        var cleanseFunctionList = this.getCleanseFunctionList();

        delete cleanseFunctionList[nodeId];
    },

    clearCleanseFunctionList: function () {
        this.cleanseFunctionList = {};
    },

    buildCleanseFunctionId: function (nodeId) {
        var id;

        id = Ext.String.format('function-{0}', nodeId);

        return id;
    },

    /**
     * Добавляет блок функций в рабочую область
     *
     * @param cleanseFunction
     * @param position
     * @param callback
     *
     * @return el {HTMLElement}
     */
    addCleanseFunction: function (cleanseFunction, position, nodeId) {
        var me = this,
            id,
            nestedData = cleanseFunction.getAssociatedData(),
            template,
            el,
            cleanseFunctionInfo,
            prop;

        if (!nodeId) {
            nodeId = this.nodeSeqIdentifier.generate();
        }

        id = this.buildCleanseFunctionId(nodeId);

        template = Ext.create('Ext.XTemplate', Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionTemplate.cleanseFunctionTpl);

        cleanseFunction['nestedData'] = [];

        for (prop in nestedData) {
            if (nestedData.hasOwnProperty(prop)) {
                cleanseFunction['nestedData'][prop] = nestedData[prop];
            }
        }

        el = Ext.DomHelper.append(me.view.getEl().dom, {
            tag: 'div',
            id: id,
            'class': 'unidata-cfblock-container'
        });

        el.style.top = position.y + 'px';
        el.style.left = position.x + 'px';

        cleanseFunctionInfo = {
            id: id,
            nodeId: nodeId,
            cleanseFunction: cleanseFunction
        };

        // cleanseFunction.id = id;
        cleanseFunction.nodeId = nodeId;
        template.overwrite(id, cleanseFunctionInfo);

        el.querySelector('.unidata-cfblock-remove').addEventListener('click', me.onRemoveCleanseFunction.bind(this, nodeId, el, cleanseFunction));

        cleanseFunction.inputPorts().each(function (port) {
            var portId,
                uuid,
                dataType = port.get('dataType'),
                portName = port.get('name'),
                portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.INPUT;

            portId = me.buildPortId(id, portType, portName);
            uuid = me.buildUuid('LeftMiddle', portId);
            me.jsPlumb.addEndpoint(portId, me.inputEndpoint, {
                anchor: 'LeftMiddle',
                uuid: uuid,
                parameters: {
                    portName: portName,
                    portType: portType,
                    nodeId: nodeId,
                    dataType: dataType
                }
            });
        });

        cleanseFunction.outputPorts().each(function (port) {
            var portId,
                uuid,
                dataType = port.get('dataType'),
                portName = port.get('name'),
                portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT;

            portId = me.buildPortId(id, portType, portName);
            uuid     = me.buildUuid('RightMiddle', portId);
            me.jsPlumb.addEndpoint(portId, me.outputEndpoint, {
                anchor: 'RightMiddle',
                uuid: uuid,
                parameters: {
                    portName: portName,
                    portType: portType,
                    nodeId: nodeId,
                    dataType: dataType
                }
            });
        });

        me.putInCleanseFunctionList(nodeId, cleanseFunctionInfo);

        return el;
    },

    onRemoveCleanseFunction: function (nodeId, container, cleanseFunction) {
        var title = Unidata.i18n.t('admin.cleanseFunction>deleteCfBlock'),
            msg   = Unidata.i18n.t('admin.cleanseFunction>deleteCfBlockPrompt');

        Unidata.showPrompt(title, msg, this.removeCleanseFunction, this, null, [nodeId, container, cleanseFunction]);
    },

    removeCleanseFunction: function (nodeId, container, cleanseFunction) {
        var me = this,
            PORT_TYPE = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE,
            portId,
            id;

        id = this.buildCleanseFunctionId(nodeId);

        container.remove();
        cleanseFunction.inputPorts().each(function (port) {
            portId = me.buildPortId(id, PORT_TYPE.INPUT, port.get('name'));
            me.jsPlumb.remove(portId);
        });
        cleanseFunction.outputPorts().each(function (port) {
            portId = me.buildPortId(id, PORT_TYPE.OUTPUT, port.get('name'));
            me.jsPlumb.remove(portId);
        });

        this.deleteFromCleanseFunctionList(nodeId);
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
    }
});
