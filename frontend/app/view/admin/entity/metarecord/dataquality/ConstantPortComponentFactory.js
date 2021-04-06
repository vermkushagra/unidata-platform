/**
 * Фабрика компонентов для константных портов.
 * Компонент (элемент ввода) генерится на основании типа порта или кастомно для определенных портов определенных функций
 *
 * @author Sergey Shishigin
 * @date 2017-01-11
 */

Ext.define('Unidata.view.admin.entity.metarecord.dataquality.ConstantPortComponentFactory', {
    singleton: true,

    requires: [
        'Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst',
        'Unidata.view.component.AttributePicker'
    ],

    /**
     * Построение компонента для представления константного порта
     *
     * @param cleanseFunction {Unidata.model.cleansefunction.CleanseFunction}
     * @param port {Unidata.model.cleansefunction.InputPort|Unidata.model.cleansefunction.OutputPort}
     * @param value
     * @param onPortValueChange
     * @param customCfg
     * @returns {*}
     */
    buildConstantPortCfg: function (cleanseFunction, port, value, onPortValueChange, customCfg) {
        var portName     = port.get('name'),
            portDataType = port.get('dataType'),
            portRequired = port.get('required'),
            portDescription = port.get('description'),
            portType,
            componentCfg,
            cfg;

        if (port instanceof Unidata.model.cleansefunction.InputPort) {
            portType = 'input';
        } else {
            portType = 'output';
        }

        portDescription = portDescription + (portType === 'input' && portRequired ? '*' : '');

        if (onPortValueChange && !Ext.isFunction(onPortValueChange)) {
            throw new Error('onConstantPortValueChange is not a function');
        }

        cfg = {
            fieldLabel: portDescription,
            name: portName,
            reference: this.buildPortReferenceByName(portName),
            emptyText: Unidata.i18n.t('admin.metamodel>const'),
            triggerWrapCls: portType + '-port-constant',
            value: value,
            anchor: '100%',
            msgTarget: 'under',
            triggers: {
                clear: {
                    hideOnReadOnly: false,
                    cls: 'x-form-clear-trigger',
                    handler: 'clear' + Ext.String.capitalize(portType) + 'Port'
                }
            }
        };

        if (onPortValueChange) {
            cfg.listeners = {
                change: onPortValueChange
            };
        }

        // сначала проверяем не являются ли порты особыми и строим для них особые компоненты
        componentCfg = this.tryBuildSpecificPortFieldConstantCfg(cleanseFunction, port);

        if (!componentCfg) {
            componentCfg = {};
            // строим контрол обычным путем
            switch (portDataType) {
                case 'Number':
                case 'Integer':
                    componentCfg.xtype = 'numberfield';
                    break;
                case 'Boolean':
                    componentCfg = Ext.apply(cfg, {
                        xtype: 'combo',
                        displayField: 'name',
                        valueField: 'value',
                        store: {
                        fields: ['name', 'value'],
                        data: [
                            {name: 'TRUE', value: true},
                            {name: 'FALSE', value: false},
                            {name: Unidata.i18n.t('glossary:notSet'), value: ''}
                        ]}
                    });
                    break;
                default:
                    componentCfg.xtype = 'textfield';
            }
        }

        //portFieldConstantValueConfig.allowBlank = !port.get('required');

        cfg = Ext.apply(cfg, customCfg);
        cfg = Ext.apply(cfg, componentCfg);

        return cfg;
    },

    /**
     * Пробуем создать спефицичный вид порта в зависимости от имени функции и порта
     *
     * @param cleanseFunction
     * @param port
     * @returns {*}
     */
    tryBuildSpecificPortFieldConstantCfg: function (cleanseFunction, port) {
        var CleanseFunctionPortEnum = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionPortEnum,
            cfg = null;

        if (this.isCFFetchPortFetchMode(cleanseFunction, port)) {
            cfg = this.buildPortFieldConstantEnumCfg(CleanseFunctionPortEnum.CF_INNER_FETCH_FETCH_MODE);
        } else if (this.isCFFetchPortDataType(cleanseFunction, port)) {
            cfg = this.buildPortFieldConstantEnumCfg(CleanseFunctionPortEnum.CF_INNER_FETCH_DATA_TYPE);
        } else if (this.isCFInputFetchPortEntity(cleanseFunction, port)) {
            cfg = this.buildPortFieldEntityTreeCfg();
        } else if (this.isCFFetchPortAttribute(cleanseFunction, port)) {
            cfg = this.buildPortFieldAttributeCfg();
        }

        return cfg;
    },

    /**
     * Порт предназначен для выбора вида запроса в функциях подтягивания данных
     * @param cleanseFunction
     * @param port
     * @returns {boolean}
     */
    isCFFetchPortFetchMode: function (cleanseFunction, port) {
        var cfJavaClass = cleanseFunction.get('javaClass'),
            portName = port.get('name'),
            prefix = 'com.unidata.mdm.cleanse.misc.',
            CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst;

        return cfJavaClass === prefix + 'CFInnerFetch' &&
               portName === CleanseFunctionConst.CFInnerFetch.PORT_FETCH_MODE ||
               cfJavaClass === prefix + 'CFOuterFetch' &&
               portName === CleanseFunctionConst.CFOuterFetch.PORT_FETCH_MODE;
    },

    /**
     * Порт предназначен для выбора типа выходного значения в функциях подтягивания данных
     * @param cleanseFunction
     * @param port
     * @returns {boolean}
     */
    isCFFetchPortDataType: function (cleanseFunction, port) {
        var cfJavaClass = cleanseFunction.get('javaClass'),
            portName = port.get('name'),
            prefix = 'com.unidata.mdm.cleanse.misc.',
            CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst;

        return cfJavaClass === prefix + 'CFInnerFetch' &&
            portName === CleanseFunctionConst.CFInnerFetch.PORT_DATA_TYPE ||
            cfJavaClass === prefix + 'CFOuterFetch' &&
            portName === CleanseFunctionConst.CFOuterFetch.PORT_DATA_TYPE;
    },

    /**
     * Проверяем предназначен ли порт для выбора реестра в функции внутреннего подтягивания данных
     *
     * @param cleanseFunction
     * @param port
     * @returns {boolean}
     */
    isCFInputFetchPortEntity: function (cleanseFunction, port) {
        var cfJavaClass = cleanseFunction.get('javaClass'),
            portName = port.get('name'),
            prefix = 'com.unidata.mdm.cleanse.misc.',
            CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst;

        return cfJavaClass === prefix + 'CFInnerFetch' && portName === CleanseFunctionConst.CFInnerFetch.PORT_ENTITY;
    },

    /**
     * Проверяем предназначен ли порт для выбора атрибута в функции внутреннего подтягивания данных
     *
     * @param cleanseFunction
     * @param port
     * @returns {boolean}
     */
    isCFFetchPortAttribute: function (cleanseFunction, port) {
        var cfJavaClass = cleanseFunction.get('javaClass'),
            portName = port.get('name'),
            prefix = 'com.unidata.mdm.cleanse.misc.',
            CleanseFunctionConst = Unidata.view.admin.entity.metarecord.dataquality.CleanseFunctionConst,
            isCFFetchPortAttribute = false;

        if (cfJavaClass === prefix + 'CFInnerFetch') {
            if (portName === CleanseFunctionConst.CFInnerFetch.PORT_RETURN_ATTRIBUTE ||
                portName === CleanseFunctionConst.CFInnerFetch.PORT_ORDER_ATTRIBUTE ||
                portName === CleanseFunctionConst.CFInnerFetch.PORT_SEARCH_ATTRIBUTE) {

                isCFFetchPortAttribute = true;
            }
        }

        return isCFFetchPortAttribute;
    },

    /**
     * Создать константый порт с выпадающим списком
     *
     * @param enumData
     * @param customCfg
     * @returns {Object}
     */
    buildPortFieldConstantEnumCfg: function (enumData, customCfg) {
        var cfg;

        cfg = {
            xtype: 'combo',
            displayField: 'name',
            valueField: 'value',
            editable: false,
            store: {
                fields: ['name', 'value'],
                data: enumData
            }
        };

        Ext.apply(cfg, customCfg);

        return cfg;
    },

    /**
     * Построить константный порт с выпадающим деревом реестров/справочников
     *
     * @returns {Object}
     */
    buildPortFieldEntityTreeCfg: function () {
        var cfg;

        cfg = {
            xtype: 'un.entitycombo',
            emptyText: Unidata.i18n.t('admin.metamodel>selectEntityOrLookupEntity')
        };

        return cfg;
    },

    /**
     * Построить константный порт с выпадающим деревом атрибутов
     *
     * @returns {Object}
     */
    buildPortFieldAttributeCfg: function () {
        var cfg;

        cfg = {
            xtype: 'un.attributepicker',
            isArrayAttributesHidden: true
        };

        return cfg;
    },

    /**
     * Построить reference by name для порта
     *
     * @param portName
     * @returns {string}
     */
    buildPortReferenceByName: function (portName) {
        return portName + 'ConstantValue';
    }
});
