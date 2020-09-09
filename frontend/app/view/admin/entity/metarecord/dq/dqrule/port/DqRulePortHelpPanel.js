/**
 * Панель со справкой в окне редактирования порта
 *
 * @author Denis Makarov
 * @date 2018-04-24
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortHelpPanel', {
    extend: 'Ext.TabPanel',

    alias: 'widget.admin.entity.metarecord.dq.port.dqruleporthelppanel',

    cls: 'un-dq-rule-port-help-panel',

    buildHtml: function () {
        var html, htmlTpl, lines;

        lines = [
            '<div class="un-dq-rule-port-example-syntax-el">' + Unidata.i18n.t('admin.dq>dqRulePortHelp.examples.syntax.line1') + '</div>',
            '<div class="un-dq-rule-port-example-syntax-el">' + Unidata.i18n.t('admin.dq>dqRulePortHelp.examples.syntax.line2') + '</div>',
            '<div class="un-dq-rule-port-example-syntax-el">' + Unidata.i18n.t('admin.dq>dqRulePortHelp.examples.syntax.line3') + '</div>',
            '<div class="un-dq-rule-port-example-syntax-el">' + Unidata.i18n.t('admin.dq>dqRulePortHelp.examples.syntax.line4') + '</div>'
        ];

        htmlTpl = '<div class="un-dq-rule-port-example-syntax">{0}{1}{2}{3}</div>';
        html = Ext.String.format(htmlTpl, lines[0], lines[1], lines[2], lines[3]);

        return html;
    },

    initItems: function () {
        var html, items;

        this.callParent(arguments);
        html = this.buildHtml();
        items = [
            {
                xtype: 'container',
                title: Unidata.i18n.t('admin.dq>dqRulePortHelp.syntax'),
                html: html

            }, {
                title: Unidata.i18n.t('admin.dq>dqRulePortHelp.examples'),
                xtype: 'admin.entity.metarecord.dq.port.dqruleporthelppanelexamples'
            }
        ];
        this.add(items);
    }

});
