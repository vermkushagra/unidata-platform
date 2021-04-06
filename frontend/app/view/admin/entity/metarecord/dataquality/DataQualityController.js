/**
 * @fileOverview: DataQualityController
 */
Ext.define('Unidata.view.admin.entity.metarecord.dataquality.DataQualityController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.entity.metarecord.dataquality',

    requires: [
        'Ext.String',
        'Unidata.view.component.HtmlComboBox',
        'Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionPortEnum'
    ],

    dqRuleCodeAttributeFailText: Unidata.i18n.t('admin.metamodel>codeAttributeCantUseForOutputPort'),

    init: function () {
        var me = this,
            view = this.getView(),
            viewModel = this.getViewModel(),
            grid = view.lookupReference('dataQualityGrid'),
            draftMode = view.draftMode,
            cleanseGroupsStore = viewModel.getStore('cleanseGroupsStore'),
            sourceSystemsStore = viewModel.getStore('sourceSystems'),
            lookupEntitiesStore = viewModel.getStore('lookupEntities'),
            entitiesStore = viewModel.getStore('entities'),
            stores;

        grid.getView().getRowClass = this.getDqGridRowClass;

        stores = [
            cleanseGroupsStore,
            sourceSystemsStore,
            lookupEntitiesStore,
            entitiesStore
        ];

        Ext.Array.each(stores, function (store) {
            store.getProxy().on('endprocessresponse', me.onEndProcessResponse, me);

            store.load({
                params: {
                    draft: draftMode
                }
            });
        });

        this.lookupReference('messagePortsCombo').on('change', this.onMessagePortsComboChange, this);
    },

    onMessagePortsComboChange: function (combo, newValue) {
    },

    onEndProcessResponse: function (proxy, options, operation) {
        if (options.request.options.action === 'read' && operation.getError()) {
            this.fireViewEvent('serverexception');
        }
    },

    onAttributeTreeBeforeRender: function () {
        var attributeTreePanel = this.lookupReference('attributeTreePanel'),
            attributeComboBox  = this.lookupReference('attributeComboBox');

        attributeComboBox.setAttributeTree(attributeTreePanel);
    },

    getDqGridRowClass: function (record) {
        var cls = '';

        if (!record.isValid()) {
            cls = 'errorrow';
        }

        if (!record.get('isEnrichment') && !record.get('isValidation')) {
            cls = 'errorrow';
        }

        if (record.get('isEnrichment') && (!record.getEnrich() || !record.getEnrich().isValid())) {
            cls = 'errorrow';
        }

        if (record.get('isValidation') && (!record.getRaise() || !record.getRaise().isValid())) {
            cls = 'errorrow';
        }

        return cls;
    },

    onDataQualityViewActivate: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            attributeTreePanel = view.lookupReference('attributeTreePanel'),
            currentRecord = viewModel.get('currentRecord');

        attributeTreePanel.setData(currentRecord);
    },

    /**
     * Config input and output ports
     *
     * @param path
     * @param portType
     * @param portName
     * @param attributeConstantValue
     */
    setPorts: function (path, portType, portName, attributeConstantValue) {
        var viewModel = this.getViewModel(),
            currentDqRule = viewModel.get('currentDqRule'),
            currentIndex,
            isset = false,
            ports,
            port;

        attributeConstantValue = attributeConstantValue || null;

        if (portType === 'inputPort') {
            ports = currentDqRule.inputs();
        } else {
            ports = currentDqRule.outputs();
        }

        ports.each(function (port, index) {
            if (port.get('functionPort') === portName) {
                isset = true;
                currentIndex = index;
            }
        });

        if (isset) {
            port = ports.getAt(currentIndex);

            port.set('attributeName', path);
            port.set('attributeConstantValue', attributeConstantValue);

            if (port.get('attributeName') === null && port.get('attributeConstantValue') === null) {
                ports.remove(port);
            }

        } else {
            ports.add({
                attributeName: path,
                attributeConstantValue: attributeConstantValue,
                functionPort: portName
            });
        }
    },

    /**
     *
     * @param component
     * @param {Ext.form.field.Field} field
     * @param port
     * @param type
     */
    buildPortDropTarget: function (component, field, port, type) {
        var dropTarget,
            me = this;

        function IsCodeAttribute (record) {
            return record instanceof Unidata.model.attribute.CodeAttribute;
        }

        dropTarget = new Ext.dd.DropTarget(component.bodyEl.dom, {
            ddGroup: 'dqDragDrop',
            notifyDrop: function (dd, e, node) {
                var record = node.records[0],
                    path = record.getPath('name', '.').substring(2),
                    simpleDataType = record.data.record.get('simpleDataType'),
                    typeCategory = record.data.record.get('typeCategory'),
                    recordType,
                    portDataType = port.get('dataType'),
                    portName = port.get('name');

                if (IsCodeAttribute(record.data.record) && type === 'outputPort') {
                    Unidata.showError(me.dqRuleCodeAttributeFailText);

                    return false;
                }

                recordType = simpleDataType || Ext.String.capitalize(typeCategory);

                if (portDataType !== Unidata.Constants.getAnyDataTypeName() && recordType !== portDataType) {
                    Unidata.showError(Unidata.i18n.t('admin.metamodel>incompatibilityPortType', {portDataType: portDataType, recordType: recordType}));

                    return false;
                }

                me.setPorts(path, type, portName);
                field.setValue(path);

                return true;
            }
        });
    },

    buildPortFieldConfigBase: function (value, prefix) {
        var cfg = {
            value: value,
            anchor: '100%',
            triggers: {
                clear: {
                    hideOnReadOnly: false,
                    cls: 'x-form-clear-trigger',
                    handler: 'clear' + Ext.String.capitalize(prefix) + 'Port'
                }
            }
        };

        return cfg;
    },

    /**
     * Построить конфиг для порта
     *
     * @param cleanseFunction
     * @param port
     * @param value
     * @returns {Object}
     */
    buildPortFieldCfg: function (cleanseFunction, port, value) {
        var portType     = port.getPortType(),
            portName         = port.get('name'),
            description  = port.get('description') + (portType === 'input' && port.get('required') ? '*' : ''),
            portDataType = port.get('dataType'),
            me           = this,
            configBase   = this.buildPortFieldConfigBase(value, portType),
            dqConstantPortInput,
            config,
            portField;

        config = {
            fieldLabel: description,
            reference: portName,
            name: portName,
            emptyText: Unidata.i18n.t('admin.metamodel>port') + portDataType.toLowerCase(),
            xtype: 'un.htmlcombo',
            displayField: 'displayName',
            valueField: 'path',
            queryMode: 'local',
            msgTarget: 'under',
            bind: {
                store: '{attributesStore}',
                readOnly: '{metaRecordViewReadOnly}'
            },
            listeners: {
                afterrender: function (field) {
                    me.buildPortDropTarget(this, field, port, portType + 'Port');
                }
            },
            expand: function () {
                return false;
            },
            triggerWrapCls: portType + '-port'
        };

        if (portType === 'input') {
            config.listeners.change = function (component) {
                port.set('attributeConstantValue', null);
                dqConstantPortInput = me.getConstantPortComponentByName(portName, 'input');
                dqConstantPortInput.clearInvalid();

                portField = component.ownerCt.lookupReference(port.get('name') + 'ConstantValue');

                if (portField) {
                    portField.suspendEvent('change');
                    portField.setValue('');
                    portField.resumeEvent('change');
                }

            };
        }

        return Ext.Object.merge(config, configBase);
    },

    /**
     * Строим входные и выходные порты
     *
     * @param cleanseFunction {Unidata.model.cleansefunction.CleanseFunction}
     * @param values
     * @param prefix {'input'|'output'}
     */
    buildPorts: function (cleanseFunction, values, prefix) {
        var portContainer = this.lookupReference(prefix + 'Ports'),
            ports;

        switch (prefix) {
            case 'input':
                ports = cleanseFunction.inputPorts();
                break;
            case 'output':
                ports = cleanseFunction.outputPorts();
                break;
        }

        portContainer.removeAll();
        ports.each(this.buildPort.bind(this, values, cleanseFunction));
    },

    /**
     * Строим порт
     *
     * @param cleanseFunction
     * @param port
     */
    buildPort: function (values, cleanseFunction, port) {
        var me = this,
            portType = port.getPortType(),
            portContainer,
            portValues = values && values.query('functionPort', port.get('name')) || [],
            portValue = '',
            constantValue = '',
            portFieldCfg,
            portValuesFirst,
            isConstantInputPort;

        portContainer = this.lookupReference(portType + 'Ports');

        if (portValues.length) {
            portValuesFirst = portValues.first();

            portValue = portValuesFirst.get('attributeName');
            constantValue = portValuesFirst.get('attributeConstantValue');
            constantValue = constantValue ? constantValue.value : '';
        }

        isConstantInputPort = me.isConstantInputPort(cleanseFunction, port);

        // строим поле биндинга атрибутов
        if (!isConstantInputPort) {
            portFieldCfg = me.buildPortFieldCfg(cleanseFunction, port, portValue);
            portContainer.add(portFieldCfg);
        }

        // строим константные поля
        if (portType === 'input') {
            this.buildInputConstantPort(cleanseFunction, port, portContainer, constantValue);
        }
    },

    /**
     * Строим входной константный порт
     *
     * @param cleanseFunction
     * @param port
     * @param portContainer
     * @param constantValue
     */
    buildInputConstantPort: function (cleanseFunction, port, portContainer, constantValue) {
        var ConstantPortComponentFactory = Unidata.view.admin.entity.metarecord.dataquality.ConstantPortComponentFactory,  // jscs:ignore maximumLineLength
            CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst,
            portValueChangeFn,
            onConstantPortEntityLoadFn,
            portComponent,
            portFieldConfig,
            isConstantInputPort,
            customCfg,
            portEntity,
            entityCatalog;

        isConstantInputPort = this.isConstantInputPort(cleanseFunction, port);

        customCfg = {
            hideLabel: !isConstantInputPort,
            bind: {
                readOnly: '{metaRecordViewReadOnly}'
            }
        };

        // используем особый callback для portEntity
        if (ConstantPortComponentFactory.isCFInputFetchPortEntity(cleanseFunction, port)) {
            portValueChangeFn = this.onConstantPortEntityValueChange.bind(this, cleanseFunction, port);
        } else {
            portValueChangeFn = this.onConstantPortValueChange.bind(this, cleanseFunction, port);
        }

        // формируем особый customCfg для portAttribute
        if (ConstantPortComponentFactory.isCFFetchPortAttribute(cleanseFunction, port)) {
            portEntity = this.getConstantPortComponentByName(CleanseFunctionConst.CFInnerFetch.PORT_ENTITY, 'input');

            if (portEntity) {
                entityCatalog = portEntity.getRecord();

                if (entityCatalog) {
                    customCfg = Ext.apply(customCfg, {
                        metaRecordKey: entityCatalog.getMetaRecordKey()
                    });
                }
            }
        }

        portFieldConfig = ConstantPortComponentFactory.buildConstantPortCfg(
            cleanseFunction,
            port,
            constantValue,
            portValueChangeFn,
            customCfg
        );

        portComponent = portContainer.add(portFieldConfig);

        if (ConstantPortComponentFactory.isCFInputFetchPortEntity(cleanseFunction, port)) {
            onConstantPortEntityLoadFn = this.onConstantPortEntityLoad.bind(this, cleanseFunction, port, portComponent);
            portComponent.on('load', onConstantPortEntityLoadFn, this, {single: true});
        }
    },

    /**
     * Обработчик загрузки реестров/справочников для соотв. порта
     *
     * @param cleanseFunction
     * @param port
     * @param portComponent
     */
    onConstantPortEntityLoad: function (cleanseFunction, port, portComponent) {
        var CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst,
            attributePorts;

        attributePorts = [CleanseFunctionConst.CFInnerFetch.PORT_RETURN_ATTRIBUTE,
                          CleanseFunctionConst.CFInnerFetch.PORT_ORDER_ATTRIBUTE,
                          CleanseFunctionConst.CFInnerFetch.PORT_SEARCH_ATTRIBUTE];

        attributePorts.forEach(function (attributePort) {
            this.populateMetaRecordKeyToAttributePort(cleanseFunction, port, portComponent, attributePort);
        }, this);
    },

    /**
     * Обработчик изменения значения для соотв. порта
     *
     * @param cleanseFunction
     * @param port
     * @param portComponent
     */
    onConstantPortEntityValueChange: function (cleanseFunction, port, portComponent) {
        var CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst,
            attributePorts;

        attributePorts = [CleanseFunctionConst.CFInnerFetch.PORT_RETURN_ATTRIBUTE,
            CleanseFunctionConst.CFInnerFetch.PORT_ORDER_ATTRIBUTE,
            CleanseFunctionConst.CFInnerFetch.PORT_SEARCH_ATTRIBUTE];

        this.clearConstantPort(CleanseFunctionConst.CFInnerFetch.PORT_RETURN_ATTRIBUTE, 'input');
        this.clearConstantPort(CleanseFunctionConst.CFInnerFetch.PORT_ORDER_ATTRIBUTE, 'input');
        this.clearConstantPort(CleanseFunctionConst.CFInnerFetch.PORT_SEARCH_ATTRIBUTE, 'input');

        attributePorts.forEach(function (attributePort) {
            this.populateMetaRecordKeyToAttributePort(cleanseFunction, port, portComponent, attributePort);
        }, this);

        this.onConstantPortValueChange(cleanseFunction, port, portComponent);
    },

    /**
     *
     *
     * @param cleanseFunction
     * @param port
     * @param portComponent
     * @param attributePortName
     */
    populateMetaRecordKeyToAttributePort: function (cleanseFunction, port, portComponent, attributePortName) {
        var ConstantPortComponentFactory = Unidata.view.admin.entity.metarecord.dataquality.ConstantPortComponentFactory, // jscs:ignore maximumLineLength
            isCFFetchPortEntity,
            attributePortComponent,
            metaRecord,
            metaRecordKey = null;

        isCFFetchPortEntity = ConstantPortComponentFactory.isCFInputFetchPortEntity(cleanseFunction, port);

        if (isCFFetchPortEntity) {
            attributePortComponent = this.getConstantPortComponentByName(attributePortName, 'input');

            if (attributePortComponent) {
                metaRecord = portComponent.getRecord();

                if (metaRecord) {
                    metaRecordKey = metaRecord.getMetaRecordKey();
                }
                attributePortComponent.setMetaRecordKey(metaRecordKey);
            }
        }
    },

    onConstantPortValueChange: function (cleanseFunction, port, component) {
        var value = component.getValue(),
            portType = port.getPortType(),
            dataType = port.get('dataType'),
            dqPortInput,
            attributeConstantValue,
            portField,
            portName = port.get('name');

        switch (dataType) {
            case 'Number':
                value = parseFloat(value);
                break;
            case 'Integer':
                value = parseInt(value);
                break;
            case 'Any':
                // UN-4144 константа должна быть с типом String
                dataType = 'String';
                break;
        }

        if (!Ext.isEmpty(value)) {
            attributeConstantValue = {
                name: portName,
                type: dataType,
                value: value
            };
        } else {
            attributeConstantValue = null;
        }

        portField = component.ownerCt.lookupReference(portName);

        if (portField) {
            portField.suspendEvent('change');
            this.clearPort(portField, portType);
            dqPortInput = this.getPortComponentByName(portName, 'input');
            dqPortInput.clearInvalid();
            portField.resumeEvent('change');
        }

        this.setPorts(null, portType + 'Port', portName, attributeConstantValue);
    },

    /**
     * Получить константный порт по имени
     *
     * @param portName
     * @param prefix
     * @returns {*}
     */
    getConstantPortComponentByName: function (portName, prefix) {
        var portContainer = this.lookupReference(prefix + 'Ports'),
            ConstantPortComponentFactory = Unidata.view.admin.entity.metarecord.dataquality.ConstantPortComponentFactory,  // jscs:ignore maximumLineLength
            port = null;

        if (portContainer) {
            port = portContainer.lookupReference(ConstantPortComponentFactory.buildPortReferenceByName(portName));
        }

        return port;
    },

    /**
     * Получить константный порт по имени
     *
     * @param portName
     * @param prefix
     * @returns {*}
     */
    getPortComponentByName: function (portName, prefix) {
        var portContainer = this.lookupReference(prefix + 'Ports'),
            port = null;

        if (portContainer) {
            port = portContainer.lookupReference(portName);
        }

        return port;
    },

    clearPort: function (component, prefix) {
        component.setValue('');
        this.setPorts(null, prefix + 'Port', component.name);
    },

    clearConstantPort: function (portName, portType) {
        var constantPort;

        constantPort = this.getConstantPortComponentByName(portName, portType);
        constantPort.setValue(null);
    },

    /**
     * Является ли пункт константным портом ввода (нет возможности привязать атрибут)
     *
     * @param cleanseFunction
     * @param port
     * @returns {boolean}
     */
    isConstantInputPort: function (cleanseFunction, port) {
        var cfJavaClass = cleanseFunction.get('javaClass'),
            portName = port.get('name'),
            CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst,
            found,
            isConstant,
            isInput;

        found = Ext.Array.findBy(CleanseFunctionConst.CONSTANT_PORTS, function (constantInputPort) {
            return constantInputPort.cfJavaClass === cfJavaClass;
        });

        isConstant = found && Ext.Array.contains(found.ports, portName);

        isInput = port instanceof Unidata.model.cleansefunction.InputPort;
        isConstant = isInput && isConstant;

        return isConstant;
    },

    /**
     * Fill input ports with values
     *
     * @param cleanseFunction {Unidata.model.cleansefunction.CleanseFunction}
     * @param values
     */
    buildInputPorts: function (cleanseFunction, values) {
        this.buildPorts(cleanseFunction, values, 'input');
    },

    clearInputPort: function (component) {
        this.clearPort(component, 'input');
    },

    /**
     * Fill output ports with values
     *
     * @param cleanseFunction {Unidata.model.cleansefunction.CleanseFunction}
     * @param values
     */
    buildOutputPorts: function (cleanseFunction, values) {
        this.buildPorts(cleanseFunction, values, 'output');
    },

    clearOutputPort: function (component) {
        this.clearPort(component, 'output');
    },

    onCleanseFunctionBeforeSelect: function (combo, record) {
        return record.get('type') === 'cleansefunction';
    },

    //TODO: refactoring for origins ui ctrl + create component

    setDefaultEnrichValue: function (enrich) {
        var viewModel = this.getViewModel();

        enrich.set('action', 'UPDATE_CURRENT');
        enrich.set('phase', 'BEFORE_UPSERT');
    },

    setDefaultRaiseValue: function (raise) {
        raise.set('phase', 'BEFORE_UPSERT');
    },

    onDataQualityGridSelectionChange: function (component, selected) {
        var me                 = this,
            cbgroup            = this.lookupReference('originsCBGroup'),
            dqTypeCBGroup      = this.lookupReference('dqTypeCBGroup'),
            actionRBGroup      = this.getView().lookupReference('actionRBGroup'),
            originsRBGroup     = this.getView().lookupReference('originsRBGroup'),
            masterDataCheckbox = this.lookupReference('masterDataCheckbox'),
            dqrule,
            applicable         = [];

        actionRBGroup.suspendEvent('change');
        originsRBGroup.suspendEvent('change');
        masterDataCheckbox.suspendEvent('change');

        originsRBGroup.reset();
        dqTypeCBGroup.clearInvalid();

        dqrule = selected[0];

        this.getViewModel().set('currentDqRule', dqrule);

        if (dqrule !== null && dqrule !== undefined) {
            applicable = dqrule.get('applicable');

            // create empty dqrule.raise if it is not exists
            if (!dqrule.getRaise()) {
                dqrule.setRaise(Ext.create('Unidata.model.dataquality.DqRaise', {}));
            }

            this.setDefaultRaiseValue(dqrule.getRaise());

            // create empty dqrule.enrich if it is not exists
            if (!dqrule.getEnrich()) {
                dqrule.setEnrich(Ext.create('Unidata.model.dataquality.DqEnrich', {}));

                this.setDefaultEnrichValue(dqrule.getEnrich());

                actionRBGroup.setValue({
                    dqAction: dqrule.getEnrich().get('action')
                });

                originsRBGroup.setValue({
                    origins: dqrule.getEnrich().get('sourceSystem')
                });
            } else {
                //set some values manually

                this.setDefaultEnrichValue(dqrule.getEnrich());

                actionRBGroup.setValue({
                    dqAction: dqrule.getEnrich().get('action')
                });

                originsRBGroup.setValue({
                    origins: dqrule.getEnrich().get('sourceSystem')
                });
            }

            masterDataCheckbox.setValue(Ext.Array.contains(applicable, 'ETALON'));

            // handle origins
            var names = [];
            var isAll = dqrule.getOrigins().get('all');

            if (isAll) {
                names.push('all');
            } else {
                dqrule.getOrigins().sourceSystems().each(function (sourceSystem) {
                    names.push(sourceSystem.data);
                });
            }
            cbgroup.suspendEvent('change');
            cbgroup.setValue({origins: names});
            cbgroup.resumeEvent('change');

            if (dqrule.get('cleanseFunctionName')) {
                Unidata.model.cleansefunction.CleanseFunction.load(dqrule.get('cleanseFunctionName'), {
                    success: function (record) {
                        dqrule.setCleanseFunction(record);
                        this.updateCleanseFunction(record);
                        // валидируем ради подсветки
                        this.checkDqRuleValid(dqrule);
                    },
                    scope: this
                });
            } else {
                this.lookupReference('inputPorts').removeAll();
                this.lookupReference('outputPorts').removeAll();
            }
        }

        this.displayEnrichSourceSystem();

        actionRBGroup.resumeEvent('change');
        originsRBGroup.resumeEvent('change');
        masterDataCheckbox.resumeEvent('change');
    },

    cleanDqRule: function (dqrule) {
        if (!dqrule.get('isValidation') && dqrule.getRaise() !== null && dqrule.getRaise() !== undefined) {
            dqrule.getRaise().drop();
        }

        if (!dqrule.get('isEnrichment') && dqrule.getEnrich() !== null && dqrule.getEnrich() !== undefined) {
            dqrule.getEnrich().drop();
        }
    },

    updateApplicable: function (dqrule) {
        var all,
            count,
            applicable;

        if (!dqrule.get('isValidation') && !dqrule.get('isEnrichment')) {
            return;
        }

        all        = dqrule.getOrigins().get('all');
        count      = dqrule.getOrigins().sourceSystems().getCount();
        applicable = dqrule.get('applicable');

        if (all || count) {
            applicable = Ext.Array.merge(applicable, ['ORIGIN']);
            dqrule.set('applicable', applicable);
        }
    },

    onDataQualityGridBeforeDeselect: function (self, dqrule) {
        // set raise/enrich to null if isValidation/isEnrichment are false
        this.cleanDqRule(dqrule);
    },

    onSourceSystemsCBGroupChange: function (component, newValue, oldValue) {
        var viewModel     = this.getViewModel(),
            currentDqRule = viewModel.get('currentDqRule'),
            applicable    = currentDqRule.get('applicable'),
            isAllExistsNew,
            isAllExistsOld,
            isAll,
            origins;

        component.suspendEvent('change');

        if (newValue.origins && newValue.origins.length) {
            applicable = Ext.Array.merge(applicable, ['ORIGIN']);
        } else {
            applicable = Ext.Array.remove(applicable, ['ORIGIN']);
        }

        currentDqRule.set('applicable', applicable);

        isAllExistsNew = newValue.hasOwnProperty('origins') ? newValue.origins.indexOf('all') !== -1 : false;
        isAllExistsOld = oldValue.hasOwnProperty('origins') ? oldValue.origins.indexOf('all') !== -1 : false;
        isAll = !isAllExistsOld && isAllExistsNew;

        if (isAll) {
            component.setValue({origins: ['all']});
        } else if (isAllExistsNew && Array.isArray(newValue.origins) && newValue.origins.length > 1) {
            newValue.origins.splice(newValue.origins.indexOf('all'), 1);
            component.setValue(newValue);
        }

        currentDqRule.getOrigins().sourceSystems().removeAll();

        if (isAll) {
            currentDqRule.getOrigins().set('all', true);
        } else {
            origins = component.getValue().origins;

            currentDqRule.getOrigins().set('all', false);
            currentDqRule.getOrigins().sourceSystems().insert(0, origins);
        }
        component.resumeEvent('change');
    },

    onAddDqRuleButtonClick: function (component) {
        var grid    = component.up('grid'),
            store   = grid.getStore(),
            record  = Ext.create('Unidata.model.dataquality.DqRule', {special: false}),
            origins = Ext.create('Unidata.model.dataquality.DqOrigins', {all: true}),
            order;

        record.setOrigins(origins);

        // calc and set order
        order = store.last() !== null ? store.last().get('order') + 1 : 0;
        record.set('order', order);

        store.add(record);
        grid.setSelection(record);
        //this.getViewModel().set('currentDqRule', record[0]);
        this.checkRecordDirty(record, this.getViewModel().getParent());
    },

    onDeleteDqRuleButtonClick: function (btn) {
        this.showPrompt(
          Unidata.i18n.t('admin.duplicates>removingRule'),
          Unidata.i18n.t('admin.duplicates>confirmRemoveRule'),
          this.deleteDqRule, this, btn
        );
    },

    deleteDqRule: function () {
        var grid            = this.getView().lookupReference('dataQualityGrid'),
            records         = grid.getSelection(),
            store           = grid.getStore(),
            index           = store.indexOf(records[0]),
            recordsToUpdate = store.getRange(index + 1),
            selIndex        = 0,
            count;

        recordsToUpdate.forEach(function (r) {
            r.set('order', r.get('order') - 1);
        });

        store.remove(records[0]);
        count = store.count();

        if (count > 0) {
            if (count === 0) {
                selIndex = 0;
            } else if (index < count) {
                selIndex = index;
            } else {
                selIndex = index - 1;
            }
            grid.selModel.select(store.getAt(selIndex));
        } else {
            grid.selModel.deselectAll();
            // TODO: почему-то в первый раз при вызове deselectAll selection не сбрасывается.
            // В проблеме не смог разобраться, потому добавлена эта строчка:
            grid.setSelection(false);
            this.getViewModel().set('currentDqRule', null);
        }

        this.checkRecordDirty(records[0], this.getViewModel().getParent());
    },

    onAttributePickerFieldClick: function (tree, node) {
        //@TODO create check for only simple attribute
        var ref = this.lookupReference('attributePickerField'),
            record = node.get('record'),
            displayName;

        displayName = record instanceof Unidata.model.entity.Entity ? Unidata.i18n.t('admin.metamodel>baseEntity') : record.get('displayName');

        ref.setValue(displayName);
        ref.picker.setHidden(true);
        this.checkRecordDirty(record, this.getViewModel().getParent());
    },

    updateCleanseFunction: function (cleanseFunction) {
        var view,
            viewModel,
            dqrule,
            emptyRecordMessagePort,
            emptyRecordCategoryPort,
            records,
            loadingPromises = [],
            allRecordsMessagePort,
            allRecordsCategoryPort;

        viewModel = this.getViewModel();
        viewModel.set('dqLoading', true);
        viewModel.notify();

        view = this.getView();
        dqrule = viewModel.get('currentDqRule');

        this.buildInputPorts(cleanseFunction, dqrule.inputs());
        this.buildOutputPorts(cleanseFunction, dqrule.outputs());

        emptyRecordMessagePort = [{name: Unidata.i18n.t('admin.metamodel>userMessageText'), value: '', dataType: 'String'}];
        emptyRecordCategoryPort = [{name: Unidata.i18n.t('admin.metamodel>userCategory'), value: '', dataType: 'String'}];

        records = Ext.Array.clone(cleanseFunction.outputPorts().data.items);
        records.forEach(function (record) {
            record.set('value', record.get('name'));
        });

        if (dqrule.getRaise() !== null) {
            allRecordsMessagePort = emptyRecordMessagePort.concat(records);
            allRecordsCategoryPort = emptyRecordCategoryPort.concat(records);

            viewModel.getStore('functionRaiseErrorPorts').getProxy().setData(records);
            viewModel.getStore('messagePorts').getProxy().setData(allRecordsMessagePort);
            //viewModel.getStore('severityPorts').getProxy().setData(allRecords);
            viewModel.getStore('categoryPorts').getProxy().setData(allRecordsCategoryPort);

            var functionRaiseErrorPort = dqrule.getRaise().get('functionRaiseErrorPort');
            var messagePort = dqrule.getRaise().get('messagePort');
            //var severityPort = dqrule.getRaise().get('severityPort');
            var categoryPort = dqrule.getRaise().get('categoryPort');

            loadingPromises.push(
                this.loadStorePromise(viewModel.getStore('functionRaiseErrorPorts'))
            );
            loadingPromises.push(
                this.loadStorePromise(viewModel.getStore('messagePorts'))
            );
            // временно скрыто т.к. severity port не используется
            // loadingPromises.push(
            //     this.loadStorePromise(viewModel.getStore('severityPorts'))
            // );
            loadingPromises.push(
                this.loadStorePromise(viewModel.getStore('categoryPorts'))
            );

            view.lookupReference('functionRaiseErrorPortsCombo').setValue(functionRaiseErrorPort);
            view.lookupReference('messagePortsCombo').setValue(messagePort);
            // временно скрыто т.к. severity port не используется
            //view.lookupReference('severityPortsCombo').setValue(severityPort);
            view.lookupReference('categoryPortsCombo').setValue(categoryPort);
        }

        // после загрузки всех данных
        Ext.Deferred.all(loadingPromises)
            .then(function () {
                viewModel.set('dqLoading', false);
            });
    },

    loadStorePromise: function (store) {
        var deferred = Ext.create('Ext.Deferred');

        store.load(function (records, operation, success) {
            if (success) {
                deferred.resolve(records);
            } else {
                deferred.reject();
            }
        });

        return deferred.promise;
    },

    /**
     * @param {Ext.form.field.ComboBox} tree
     * @param {Ext.data.Model/Ext.data.Model[]} node
     */
    onSelectCleanseFunction: function (tree, node) {
        var ref = this.lookupReference('cleanseFunctionPickerField'),
            viewModel = this.getViewModel(),
            dqRule = viewModel.get('currentDqRule');

        if (node.get('record') instanceof  Unidata.model.cleansefunction.Group) {
            return;
        }

        ref.setValue(node.getPath('name', '.').substring(2));
        Unidata.model.cleansefunction.CleanseFunction.load(ref.getValue(), {
            success: function (record) {
                dqRule.inputs().removeAll();
                dqRule.outputs().removeAll();
                dqRule.setCleanseFunction(record);
                this.updateCleanseFunction(record);
            },
            scope: this
        });
        ref.picker.setHidden(true);
    },

    onDataQualityGridDrop: function () {
        var grid  = this.lookupReference('dataQualityGrid'),
            store = grid.getStore();

        // обновляем нумерацию правил
        store.each(function (record) {
            var index = store.indexOf(record);

            record.set('order', index);
        });
    },

    fillOriginsFieldContainer: function (fieldContainer, fieldName, records, isInsertAllField) {
        isInsertAllField = isInsertAllField || false;
        fieldContainer.removeAll();

        if (isInsertAllField) {
            fieldContainer.add(
                {
                    boxLabel: Unidata.i18n.t('admin.metamodel>allSystems'),
                    name: fieldName,
                    inputValue: 'all',
                    bind: {
                        readOnly: '{metaRecordViewReadOnly}'
                    }
                }
            );
        }
        records.forEach(function (record) {
            fieldContainer.add(
                {
                    boxLabel: record.get('name'),
                    name: fieldName,
                    inputValue: record.get('name'),
                    bind: {
                        readOnly: '{metaRecordViewReadOnly}'
                    }
                }
            );
        });
    },

    onSourceSystemsLoad: function (store, records, success, eOpts) {
        // add items to checkbox group
        var fieldName = 'origins',
            viewModel = this.getViewModel(),
            response,
            responseText,
            adminSystemName;

        if (!success) {
            return;
        }

        response = eOpts.getResponse();

        if (!response) {
            return;
        }

        responseText = Ext.decode(response.responseText, true);

        if (responseText) {
            adminSystemName = responseText.adminSystemName;

            viewModel.set('adminSystemName', adminSystemName);
        }

        this.fillOriginsFieldContainer(this.lookupReference('originsCBGroup'), fieldName, records, true);
        this.fillOriginsFieldContainer(this.lookupReference('originsRBGroup'), fieldName, records);
    },

    selectFirstComboItem: function (reference, records) {
        var combo = this.getView().lookupReference(reference);

        if (records.length > 0) {
            combo.select(records[0]);
        }
    },

    onMessagePortsStoreLoad: function (store, records) {
        this.selectFirstComboItem('messagePortsCombo', records);
    },

    onSeverityPortsStoreLoad: function (store, records) {
        this.selectFirstComboItem('severityPortsCombo', records);
    },

    onCategoryPortsStoreLoad: function (store, records) {
        this.selectFirstComboItem('categoryPortsCombo', records);
    },

    setDqEnrichField: function (field, value) {
        var dqEnrich = this.getViewModel().get('currentDqEnrich');

        if (dqEnrich !== null && dqEnrich !== undefined) {
            dqEnrich.set(field, value);
        }
    },

    onDqActionRadioGroupChange: function (component, newValue) {
        this.setDqEnrichField('action', newValue.dqAction);
    },

    onOriginsRBGroupChange: function (component, newValue) {
        this.setDqEnrichField('sourceSystem', newValue.origins);

        if (!Ext.Object.isEmpty(component.getValue())) {
            component.clearInvalid();
        }
    },

    onDqTypeCBGroupChange: function (component, newValue) {
        var viewModel = this.getViewModel(),
            currentDqRule = viewModel.get('currentDqRule');

        component.clearInvalid();

        if (currentDqRule && !currentDqRule.getRaise()) {
            currentDqRule.setRaise(Ext.create('Unidata.model.dataquality.DqRaise', {}));

            this.setDefaultRaiseValue(currentDqRule.getRaise());
        }

        if (currentDqRule && !currentDqRule.getEnrich()) {
            currentDqRule.setEnrich(Ext.create('Unidata.model.dataquality.DqEnrich', {}));

            this.setDefaultEnrichValue(currentDqRule.getEnrich());
        }

        if (Ext.Object.isEmpty(newValue) && currentDqRule) {
            component.markInvalid(Unidata.i18n.t('admin.metamodel>selectLeastOneRuleType'));
        }

        this.displayEnrichSourceSystem();
    },

    displayEnrichSourceSystem: function () {
        var checkbox        = this.lookupReference('masterDataCheckbox'),
            viewModel       = this.getViewModel(),
            currentDqRule   = viewModel.get('currentDqRule'),
            list            = this.lookupReference('originsRBGroup'),
            masterDataPanel = this.lookupReference('masterDataPanel');

        list.clearInvalid();

        if (checkbox.getValue() && currentDqRule && currentDqRule.get('isEnrichment')) {
            masterDataPanel.setHidden(false);

            if (Ext.Object.isEmpty(list.getValue())) {
                list.markInvalid(Unidata.i18n.t('admin.metamodel>selectDataSource'));
            }
        } else {
            masterDataPanel.setHidden(true);
            list.reset();
        }
    },

    onChangeMaterDataCheckbox: function (field, checked) {
        var viewModel     = this.getViewModel(),
            currentDqRule = viewModel.get('currentDqRule'),
            applicable    = currentDqRule.get('applicable');

        if (checked) {
            applicable = Ext.Array.merge(applicable, ['ETALON']);
        } else {
            applicable = Ext.Array.remove(applicable, 'ETALON');
        }

        currentDqRule.set('applicable', applicable);

        this.displayEnrichSourceSystem();
    },

    validateRequiredFields: function () {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            currentDqRule = viewModel.get('currentDqRule'),
            validateReferences = ['dqFieldName', 'cleanseFunctionPickerField'],
            field,
            dqTypeCBGroup = view.lookupReference('dqTypeCBGroup'),
            cbGroupEnrich = view.lookupReference('cbGroupEnrich'),
            cbGroupValidation = view.lookupReference('cbGroupValidation'),
            messagePortsCombo = view.lookupReference('messagePortsCombo'),
            messageText = view.lookupReference('messageText');

        if (cbGroupValidation.getValue()) {
            validateReferences = Ext.Array.merge(validateReferences, ['severityValue', /*'phaseRaise',*/ 'functionRaiseErrorPortsCombo']);
        }

        if (cbGroupEnrich.getValue()) {
            validateReferences = Ext.Array.merge(validateReferences, ['phaseEnrich']);

            field = view.lookupReference('actionRBGroup');
            field.clearInvalid();

            if (Ext.Object.isEmpty(field.getValue())) {
                field.markInvalid(Unidata.i18n.t('admin.metamodel>fixChangesMethodNotSpecified'));
            }

            field = view.lookupReference('originsRBGroup');
            field.clearInvalid();

            if (Ext.Object.isEmpty(field.getValue())) {
                field.markInvalid(Unidata.i18n.t('admin.metamodel>sourceNotSpecified'));
            }
        }

        Ext.Array.each(validateReferences, function (item) {
            field = view.lookupReference(item);

            if (field) {
                currentDqRule ? field.validate() : field.clearInvalid();
            }
        });

        if (!cbGroupEnrich.getValue() && !cbGroupValidation.getValue()) {
            if (currentDqRule) {
                dqTypeCBGroup.markInvalid(Unidata.i18n.t('admin.metamodel>selectLeastOneRuleType'));
            } else {
                dqTypeCBGroup.clearInvalid();
            }
        }

        if (Ext.isEmpty(messageText.getValue())) {
            // выбран пункт "Пользовательское сообщение"
            messageText.markInvalid(Unidata.i18n.t('admin.metamodel>messageTextRequired'));
        } else {
            messageText.clearInvalid();
        }
    },

    onDataQualityBeforeRender: function () {
        var managedStoreLoader = this.getView().getPlugin('managedstoreloader');

        return !managedStoreLoader || managedStoreLoader.isEveryStoreIsLoaded();
    },

    onNecessaryStoresLoad: function () {
        var view               = this.getView(),
            viewModel          = this.getViewModel(),
            attributeTreePanel = this.lookupReference('attributeTreePanel');

        attributeTreePanel.setLookupEntities(viewModel.getStore('lookupEntities'));

        view.fireEvent('loadallstore');
    },

    getCurrentDqRule: function () {
        var viewModel = this.getViewModel(),
            currentDqRule = viewModel.get('currentDqRule');

        return currentDqRule;
    },

    isCurrentDqRule: function (dqRule) {
        var currentDqRule = this.getCurrentDqRule();

        if (dqRule === null || currentDqRule === null) {
            return false;
        }

        return dqRule.get('id') === currentDqRule.get('id');
    },

    /**
     * Проверка валидности настроек dq
     * @param metaRecord {Unidata.model.entity.Entity} Метамодель реестра
     * @returns {String[]|Boolean}
     */
    checkDataQualityValid: function (metaRecord) {
        var me = this,
            errorMsgs;

        errorMsgs = [];

        metaRecord.dataQualityRules().each(function (dqRule) {
            errorMsgs = errorMsgs.concat(me.checkDqRuleValid(dqRule));
        });

        return errorMsgs.length > 0 ? errorMsgs : true;
    },

    /**
     * Проверка валидности конкретного dqRule
     *
     * @param dqRule {Unidata.model.dataquality.DqRule} Правило качества
     * @returns {String[]} Массив ошибок
     */
    checkDqRuleValid: function (dqRule) {
        var dqRaiseValid,
            dqEnrichValid,
            errorMsgs = [],
            dqRuleName,
            dqRuleRaise,
            me = this;

        dqRaiseValid = true;
        dqEnrichValid = true;
        dqRuleName = dqRule.get('name');
        dqRuleRaise = dqRule.getRaise();

        function addPrefix (el) {
            return dqRuleName ? Ext.String.format('{0}: {1}', dqRuleName, el) : el;
        }

        function isRaise () {
            return dqRule.getRaise() !== null && dqRule.getRaise() !== undefined;
        }

        function isEnrich () {
            return dqRule.getEnrich() !== null && dqRule.getEnrich() !== undefined;
        }

        if (!dqRule.get('special')) {
            if (!dqRule.isValid()) {
                errorMsgs = Ext.Array.merge(errorMsgs, dqRule.getErrorMessages().map(addPrefix));
            }

            if (!isEnrich() && !isRaise()) {
                errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>selectTypeRule'));
            }

            if (isRaise()) {
                dqRaiseValid = dqRule.getRaise().isValid();

                if (dqRuleRaise.get('messagePort') == '' && Ext.String.trim(dqRuleRaise.get('messageText')) == '') {
                    errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>notSetUserMessage'));
                }
            }

            if (isEnrich()) {
                dqEnrichValid = dqRule.getEnrich().isValid();
            }

            if (!dqRaiseValid) {
                errorMsgs = Ext.Array.merge(errorMsgs, dqRule.getRaise().getErrorMessages().map(addPrefix));
            }

            if (!dqEnrichValid) {
                errorMsgs = Ext.Array.merge(errorMsgs, dqRule.getEnrich().getErrorMessages().map(addPrefix));
            }

            if (dqRule.getCleanseFunction() && me.getInvalidInputPortNames(dqRule).length > 0) {
                errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>invalidPorts'));
            }

            if (isEnrich() && Ext.Array.contains(dqRule.get('applicable'), 'ETALON') && !dqRule.getEnrich().get('sourceSystem')) {
                errorMsgs = Ext.Array.merge(errorMsgs, Unidata.i18n.t('admin.metamodel>notSetDataSource'));
            }
        }

        return errorMsgs;
    },

    /**
     * Получить имена некорректных входных портов
     *
     * Имеет побочный эффект - подсветку некорректных портов выбранного правила качества
     * @param dqRule {Unidata.model.entity.DqRule} Правило качества
     * @returns {String[]}
     */
    getInvalidInputPortNames: function (dqRule) {
        var me = this,
            cleanseFunction = dqRule.getCleanseFunction(),
            metaPorts = cleanseFunction.inputPorts(),
            dataPorts = dqRule.inputs(),
            invalidPortNames = [];

        metaPorts.setRemoteFilter(false);
        metaPorts.filter('required', true);
        metaPorts.each(function (metaPort) {
            var portName = metaPort.get('name'),
                portData = dataPorts.findRecord('functionPort', portName),
                dqConstantPortInput = me.getConstantPortComponentByName(portName, 'input'),
                dqPortInput = me.getPortComponentByName(portName, 'input'),
                isValid;

            isValid = portData && (portData.get('attributeName') || portData.get('attributeConstantValue'));

            if (!isValid) {
                invalidPortNames.push(portName);
            }

            // highlight
            if(me.isCurrentDqRule(dqRule)) {
                if (!isValid) {
                    dqConstantPortInput.markInvalid(Unidata.i18n.t('admin.metamodel>inputPortRequired'));
                    dqPortInput.markInvalid(Unidata.i18n.t('admin.metamodel>inputPortRequired'));
                } else {
                    dqConstantPortInput.clearInvalid();
                    dqPortInput.clearInvalid();
                }
            }
        }, this);

        metaPorts.clearFilter();

        return invalidPortNames;
    }
});
