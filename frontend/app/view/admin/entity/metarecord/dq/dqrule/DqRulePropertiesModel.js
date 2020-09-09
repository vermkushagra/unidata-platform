/**
 * Секция настройки основных свойств экрана настройки правил качества данных
 *
 * @author Sergey Shishigin
 * @date 2018-02-01
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.DqRulePropertiesModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.dq.dqruleprops',

    data: {
        dqRule: false,
        dqRuleActive: null
    },

    stores: {},
    formulas: {
        dqRaise: {
            bind: {
                bindTo: '{dqRule}',
                deep: true
            },
            get: function (dqRule) {
                return dqRule ? dqRule.getRaise() : null;
            }
        },
        dqRuleRunTypeDisabled: {
            bind: {
                dqRuleActive: '{dqRuleActive}',
                dqRuleEditorReadOnly: '{dqRuleEditorReadOnly}'
            },
            get: function (getter) {
                var dqRuleActive = getter.dqRuleActive,
                    dqRuleEditorReadOnly = getter.dqRuleEditorReadOnly;

                return !dqRuleActive || dqRuleEditorReadOnly;
            }
        }
    }

});
