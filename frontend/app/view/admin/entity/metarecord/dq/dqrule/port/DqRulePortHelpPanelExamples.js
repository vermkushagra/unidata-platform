/**
 * Вкладка с примерами в панели справки
 *
 * @author Denis Makarov
 * @date 2018-04-24
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortHelpPanelExamples', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.entity.metarecord.dq.port.dqruleporthelppanelexamples',

    xtype: 'layout-accordion',

    layout: 'accordion',

    defaults: {
        // applied to each contained panel
        bodyPadding: 15
    },

    initItems: function () {
        var items;

        this.callParent(arguments);

        items = [{
            title: Unidata.i18n.t('admin.dq>dqRulePortHelp.example1.title'),
            html: Unidata.i18n.t('admin.dq>dqRulePortHelp.example1')
        }, {
            title: Unidata.i18n.t('admin.dq>dqRulePortHelp.example2.title'),
            html: Unidata.i18n.t('admin.dq>dqRulePortHelp.example2')
        }, {
            title: Unidata.i18n.t('admin.dq>dqRulePortHelp.example3.title'),
            html: Unidata.i18n.t('admin.dq>dqRulePortHelp.example3')
        }, {
            title: Unidata.i18n.t('admin.dq>dqRulePortHelp.example4.title'),
            html: Unidata.i18n.t('admin.dq>dqRulePortHelp.example4')
        }, {
            title: Unidata.i18n.t('admin.dq>dqRulePortHelp.example5.title'),
            html: Unidata.i18n.t('admin.dq>dqRulePortHelp.example5')
        }];

        this.add(items);
    }
});
