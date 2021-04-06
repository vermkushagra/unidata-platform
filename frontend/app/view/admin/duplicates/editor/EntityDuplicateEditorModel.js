/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.editor.EntityDuplicateEditorModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.duplicates.entityduplicateeditor',

    data: {
        currentRule: null
    },

    stores: {},

    formulas: {
        /**
         * Определяет доступность текущего эдитора к редактированию
         */
        ruleEditorReadOnly: {
            bind: {
                rule: '{currentRule}',
                deep: true
            },
            get: function (getter) {
                var rule     = getter.rule,
                    readOnly = true;

                if (!rule) {
                    return true;
                }

                // текущее правило новое и пользователь имеет права создавать
                if (rule.phantom && Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'create')) {
                    readOnly = false;
                }

                // текущее правило не новое и пользователь имеет право редактировать
                if (!rule.phantom && Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'update')) {
                    readOnly = false;
                }

                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        }
    }
});
