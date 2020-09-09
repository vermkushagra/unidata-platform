/**
 * Фабрика для создания полей ввода константных значений dq порта
 *
 * @author Sergey Shishigin
 * @date 2018-03-12
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DqRulePortConstantFieldFactory', {

    requires: ['Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.StringField',
                'Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DateField',
                'Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.DateTimeField',
                'Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.TimeField',
                'Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.IntegerField',
                'Unidata.view.admin.entity.metarecord.dq.dqrule.port.constant.BooleanField'
    ],

    buildFieldCfg: function (type, fieldBaseCfg) {
        var DQ_RULE_PORT_DATA_TYPES = Unidata.model.dataquality.DqRule.DQ_RULE_PORT_DATA_TYPES,
            fieldCfg,

            cls = 'un-dq-rule-port-constant';

        fieldBaseCfg.value = !Ext.isEmpty(fieldBaseCfg.value) ? fieldBaseCfg.value : null;
        fieldBaseCfg.readOnly = Ext.isBoolean(fieldBaseCfg.readOnly) ? fieldBaseCfg.readOnly : null;
        fieldBaseCfg.changeFn = Ext.isFunction(fieldBaseCfg.changeFn) ? fieldBaseCfg.changeFn : null;

        fieldBaseCfg = Ext.apply(fieldBaseCfg, {
            width: '100%',
            cls: cls,
            msgTarget: 'under',
            validateOnChange: false,
            validateOnBlur: true
        });

        if (type === DQ_RULE_PORT_DATA_TYPES.STRING) {
            fieldCfg = {
                xtype: 'admin.entity.metarecord.dq.dqrule.port.constant.stringfield'
            };
            fieldCfg = Ext.apply(fieldCfg, fieldBaseCfg);
        } else if (type === DQ_RULE_PORT_DATA_TYPES.DATE) {
            fieldCfg = {
                xtype: 'admin.entity.metarecord.dq.dqrule.port.constant.datefield'
            };
            fieldCfg = Ext.apply(fieldCfg, fieldBaseCfg);
        } else if (type === DQ_RULE_PORT_DATA_TYPES.TIMESTAMP) {
            fieldCfg = {
                xtype: 'admin.entity.metarecord.dq.dqrule.port.constant.datetimefield'
            };
            fieldCfg = Ext.apply(fieldCfg, fieldBaseCfg);
        } else if (type === DQ_RULE_PORT_DATA_TYPES.TIME) {
            fieldCfg = {
                xtype: 'admin.entity.metarecord.dq.dqrule.port.constant.timefield'
            };
            fieldCfg = Ext.apply(fieldCfg, fieldBaseCfg);
        } else if (type === DQ_RULE_PORT_DATA_TYPES.INTEGER || type === DQ_RULE_PORT_DATA_TYPES.NUMBER) {
            fieldCfg = {
                xtype: 'admin.entity.metarecord.dq.dqrule.port.constant.integerfield',
                allowDecimals: type === DQ_RULE_PORT_DATA_TYPES.NUMBER,
                decimalPrecision: 6
            };
            fieldCfg = Ext.apply(fieldCfg, fieldBaseCfg);
        } else if (type === DQ_RULE_PORT_DATA_TYPES.BOOLEAN) {
            fieldCfg = {
                xtype: 'admin.entity.metarecord.dq.dqrule.port.constant.booleanfield'
            };
            fieldCfg = Ext.apply(fieldCfg, fieldBaseCfg);
        } else {
            // string field by default
            fieldCfg = {
                xtype: 'admin.entity.metarecord.dq.dqrule.port.constant.stringfield'
            };
            fieldCfg = Ext.apply(fieldCfg, fieldBaseCfg);
        }

        return fieldCfg;
    }
});
