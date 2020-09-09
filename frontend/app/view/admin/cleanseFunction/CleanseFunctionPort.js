/**
 * Компонент для отображения порта функции
 *
 * @author Denis Makarov
 * @date 2018-05-08
 */

Ext.define('Unidata.view.admin.cleanseFunction.CleanseFunctionPort', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.cleanseFunction.cleansefunctionport',

    config: {
        port: null,
        readOnly: null,
        inputField: null
    },

    cls: 'un-dq-rule-port',

    requires: [
        'Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DqRulePortConstantFieldFactory'
    ],

    referenceHolder: true,

    layout: {
        type: 'vbox',
        align: 'left'
    },

    initItems: function () {
        var dataType,
            portCfg, description, dqRulePortConstantFieldFactory, title, readOnly, required, port;

        this.callParent(arguments);

        port = this.getPort();

        description = port.get('description');
        required = port.get('required');
        dataType = port.get('dataType');

        title = this.buildPortTitle(description, required, dataType);

        readOnly = this.readOnly || false;

        dqRulePortConstantFieldFactory = Ext.create('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DqRulePortConstantFieldFactory');
        portCfg = dqRulePortConstantFieldFactory.buildFieldCfg(dataType, {
            readOnly: readOnly,
            name: port.get('name')
        });

        this.inputField = Ext.widget(portCfg);
        this.add(this.inputField);
        this.setTitle(title);
    },

    /**
     * Построить заголовок порта
     *
     * @param description {string}
     * @param required {boolean}
     * @param dataType {string}
     * @returns {string}
     */
    buildPortTitle: function (description, required, dataType) {
        var DqRuleModel = Unidata.model.dataquality.DqRule,
            title,
            requiredAsterisk,
            dataTypeDisplayName,
            dataTypeCls;

        dataTypeCls = this.cls + '-data-type';
        requiredAsterisk = required ? '*' : '';

        dataTypeDisplayName = DqRuleModel.getDataTypeDisplayName(dataType).toLowerCase();
        title = Ext.String.format('{0}{1}<span class="{2}">{3}</span>', description, requiredAsterisk, dataTypeCls, dataTypeDisplayName);

        return title;
    }

});
