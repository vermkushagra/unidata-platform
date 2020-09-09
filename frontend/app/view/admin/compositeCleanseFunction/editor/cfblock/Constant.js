/**
 * Модуль построения блока задания констант
 *
 * @author Sergey Shishigin
 * @date 2018-04-13
 */
Ext.define('Unidata.view.admin.compositeCleanseFunction.editor.cfblock.Constant', {
    constantList: {},

    view: null,
    jsPlumb: null,
    sourceEndpoint: null,
    nodeSeqIdentifier: null,

    constructor: function (data) {
        this.view = data.view;
        this.jsPlumb = data.jsPlumb;
        this.sourceEndpoint = data.sourceEndpoint;
        this.nodeSeqIdentifier = data.nodeSeqIdentifier;
    },

    /**
     * Получить объект кэша функций
     * @return {Object|null}
     */
    getConstantList: function () {
        if (!Ext.isObject(this.constantList)) {
            this.constantList = {};
        }

        return this.constantList;
    },

    /**
     * Поместить константу в список констант отображаемых на экране
     *
     * @param {Integer} nodeId
     * @param {Unidata.model.data.SimpleAttribute} constant
     */
    putInConstantList: function (nodeId, constant, override) {
        var constantList = this.getConstantList();

        override = Ext.isBoolean(override) ? override : true;

        if (override) {
            constantList[nodeId] = constant;
        }
    },

    /**
     * Удалить из списка констант
     *
     * @param {Integer} nodeId
     */
    deleteFromConstantList: function (nodeId) {
        var constantList = this.getConstantList();

        delete constantList[nodeId];
    },

    clearConstantList: function () {
        this.constantList = {};
    },

    /**
     * Добавляет блок константы в рабочую область
     *
     * @param constant
     * @param position
     * @param nodeId
     * @return el {HTMLElement}
     */
    addConstant: function (constant, position, nodeId) {
        var el;

        if (!nodeId) {
            nodeId = this.nodeSeqIdentifier.generate();
        }

        constant.setId(nodeId);
        el = this.createConstantContainer(this.view.getEl(), nodeId, position);
        this.updateConstantTemplate(nodeId, constant);
        this.putInConstantList(nodeId, constant);
        this.initConstantListeners(nodeId, constant, el);
        this.createConstantPortConnectors(nodeId, constant);

        return el;
    },

    createConstantContainer: function (parentEl, nodeId, position) {
        var container,
            id;

        id = this.buildConstantId(nodeId);
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

    /**
     * Подставить новые значения в html
     * @param nodeId
     * @param constant
     */
    applyConstantData: function (nodeId, constant) {
        var id,
            displayValue,
            cfBlockPortId,
            titleDataTypeId,
            cfBlockPort,
            titleDataType,
            title,
            dataType;

        id  = this.buildConstantId(nodeId);
        dataType = constant.get('type');
        displayValue = this.formatDisplayValue(constant.get('value'), dataType);
        constant.set('displayValue', displayValue);

        if (displayValue === undefined || displayValue === null || displayValue === '') {
            displayValue = Unidata.i18n.t('admin.cleanseFunction>selectConstantEmptyText');
        }
        cfBlockPortId = Ext.String.format('#{0} {1}', id, '.unidata-cfblock-port');
        titleDataTypeId = Ext.String.format('#{0} {1}', id, ' .unidata-cfblock-title-datatype');
        title = Ext.String.format('{0} ({1})', displayValue, dataType);

        cfBlockPort = Ext.query(cfBlockPortId)[0];
        titleDataType = Ext.query(titleDataTypeId)[0];

        cfBlockPort.innerHTML = displayValue;
        cfBlockPort.setAttribute('title', title);
        cfBlockPort.setAttribute('data-port-datatype', dataType);
        titleDataType.innerHTML = dataType;
    },

    updateConstantTemplate: function (nodeId, constant) {
        var template,
            displayValue,
            el,
            id;

        id = this.buildConstantId(nodeId);
        displayValue = this.formatDisplayValue(constant.get('value'), constant.get('type'));
        constant.set('displayValue', displayValue);

        template = Ext.create('Ext.XTemplate', Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionTemplate.portConstantTpl);

        el = template.overwrite(id, constant);

        return el;
    },

    initConstantListeners: function (nodeId, constant, container) {
        container.querySelector('.unidata-cfblock-remove').addEventListener('click', this.onRemoveConstant.bind(this, nodeId, container));
        container.querySelector('.unidata-cfblock-port').addEventListener('click', this.editConstant.bind(this, nodeId, container, constant));
    },

    createConstantPortConnectors: function (nodeId, constant) {
        var me = this,
            portId,
            uuid,
            dataType,
            portName = this.buildConstantPortName(),
            portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT,
            id;

        id = this.buildConstantId(nodeId);

        portId = this.buildPortId(id, portType, portName);
        uuid          = this.buildUuid('RightMiddle', portId);
        dataType      = constant.get('type');

        me.jsPlumb.addEndpoint(portId, this.sourceEndpoint, {
            anchor: 'RightMiddle',
            uuid: uuid,
            parameters: {
                portName: portName,
                portType: portType,
                nodeId: nodeId,
                dataType: dataType
            }
        });
    },

    buildConstantId: function (nodeId) {
        var id;

        id = Ext.String.format('constant-{0}', nodeId);

        return id;
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

    onRemoveConstant: function (nodeId, container) {
        var title = Unidata.i18n.t('admin.cleanseFunction>deleteConstantBlock'),
            msg   = Unidata.i18n.t('admin.cleanseFunction>deleteConstantBlockPrompt');

        Unidata.showPrompt(title, msg, this.removeConstant, this, null, [nodeId, container]);
    },

    /**
     * Удалить блок "Константа"
     * @param id {String}
     * @param container {HTMLElement}
     */
    removeConstant: function (nodeId, container) {
        var portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT,
            id,
            portId;

        id = this.buildConstantId(nodeId);
        portId = this.buildPortId(id, portType, this.buildConstantPortName());
        this.jsPlumb.remove(portId);
        container.remove();
        this.deleteFromConstantList(nodeId);
    },

    buildConstantPortName: function () {
        return 'port0';
    },

    /**
     * Отформатировать значение для вывода в блоке констант
     * @param value {String}
     * @param type {String}
     * @return {String}
     */
    formatDisplayValue: function (value, type) {
        var DQ_RULE_PORT_DATA_TYPES = Unidata.model.dataquality.DqRule.DQ_RULE_PORT_DATA_TYPES;

        switch (type) {
            case DQ_RULE_PORT_DATA_TYPES.DATE:
                if (!Ext.isDate(value)) {
                    value = Ext.Date.parse(value, Unidata.Config.getDateTimeFormatServer());
                }
                value = Ext.Date.format(value, 'Y-m-d');
                break;
            case DQ_RULE_PORT_DATA_TYPES.TIMESTAMP:
                value = Ext.Date.parse(value, Unidata.Config.getDateTimeFormatServer());
                value = Ext.Date.format(value, 'Y-m-d H:i:s');
                break;
            case DQ_RULE_PORT_DATA_TYPES.TIME:
                value = Ext.Date.parse(value, Unidata.Config.getDateTimeFormatServer());
                value = Ext.Date.format(value, 'H:i:s');
                break;
            case DQ_RULE_PORT_DATA_TYPES.BOOLEAN:
                switch (value) {
                    case true:
                        value = Unidata.i18n.t('common:yes');
                        break;
                    case false:
                        value = Unidata.i18n.t('common:no');
                        break;
                }
                break;
        }

        return value;
    },

    /**
     * Редактирование константы
     *
     * @param id
     * @param container
     * @param constant
     */
    editConstant: function (nodeId, container, constant) {
        var view = this.view,
            viewEl = view.getEl(),
            parentContainerTop = viewEl.getTop(),
            parentContainerLeft = viewEl.getLeft(),
            headerHeight = 0;

        this.showConstantEditorWindow({
            x: parentContainerLeft + container.offsetLeft,
            y: parentContainerTop + container.offsetTop - headerHeight,
            width: 400,
            constant: constant,
            portConstantBlock: container
        }, nodeId);
    },

    showConstantEditorWindow: function (customCfg, nodeId) {
        var window,
            cfg;

        cfg = {
            modal: true,
            draggable: false,
            resizable: false,
            listeners: {
                okbtnclick: this.onApplyConstantButtonClick.bind(this, nodeId)
            }
        };

        cfg = Ext.apply(cfg, customCfg);

        window = Ext.create('Unidata.view.admin.compositeCleanseFunction.editor.CompositeCleanseFunctionConstantWindow', cfg);

        window.show();
    },

    /**
     * Применяем настройки константы
     * @param self
     * @param portConstantBlock {HTMLElement}
     * @param constant {Unidata.model.data.SimpleAttribute}
     * @param oldConstant {Unidata.model.data.SimpleAttribute}
     */
    onApplyConstantButtonClick: function (nodeId, self, portConstantBlock, constant, oldConstant) {
        var elementId,
            portType = Unidata.model.cleansefunction.CleanseFunction.PORT_TYPE.OUTPUT,
            endpoint,
            id,
            dataType  = constant.get('type'),
            oldDataType = oldConstant.get('type');

        id = this.buildConstantId(nodeId);

        elementId = this.buildPortId(id, portType, this.buildConstantPortName());

        if (!constant || !oldConstant || dataType !== oldDataType) {
            this.jsPlumb.detachAllConnections(elementId);
        }

        this.applyConstantData(nodeId, constant);
        endpoint = this.jsPlumb.getEndpoint(this.buildUuid('RightMiddle', elementId));
        endpoint.setParameter('dataType', dataType);
    }
});
