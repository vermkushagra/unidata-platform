/**
 * Редактор правил качества
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRuleEditorModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.dq.dqruleeditor',

    constructor: function (options) {
        // отменяем наследование значений из родительской viewModel
        options = options || {};
        options.parent = null;

        this.callParent([options]);
    },

    data: {
        dqRule: null,
        dqRuleEditorReadOnly: null
    },

    stores: {},

    formulas: {
    }
});
