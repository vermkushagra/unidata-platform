/**
 * @author Ivan Marshalkin
 * @date 2016-10-06
 */

Ext.define('Unidata.view.admin.duplicates.item.RuleEditModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.duplicates.ruleedit',

    data: {
        rule: null,
        readOnly: null
    },

    stores: {
        matchAlgorithmsStore: {
            model: 'Unidata.model.matching.MatchingAlgorithm',
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: Unidata.Config.getMainUrl() + 'internal/matching/matchingAlgorithms',
                reader: {
                    type: 'json',
                    rootProperty: 'content'
                }
            }
        }
    },

    formulas: {
        /**
         * Значение флага phantom текущего правила
         */
        rulePhantom: {
            bind: {
                bindTo: '{rule}',
                deep: true
            },
            get: function (rule) {
                var phantom = true;

                if (rule) {
                    phantom = rule.phantom;
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },

        /**
         * Определяет видимость кнопки сохранения
         */
        saveButtonVisible: {
            bind: {
                rule: '{rule}',
                rulePhantom: '{rulePhantom}',
                readOnly: '{readOnly}',
                deep: true
            },
            get: function (getter) {
                var visible = false;

                if (getter.readOnly) {
                    return false;
                }

                if (Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'update')) {
                    visible = true;
                }

                if (getter.rulePhantom && Unidata.Config.userHasRight('ADMIN_MATCHING_MANAGEMENT', 'create')) {
                    visible = true;
                }

                visible = Ext.coalesceDefined(visible, false);

                return visible;
            }
        },

        /**
         * Правило доступно к редактированию
         */
        ruleEditable: {
            bind: {
                bindTo: '{saveButtonVisible}',
                deep: true
            },
            get: function (saveButtonVisible) {
                var editable = false;

                if (saveButtonVisible) {
                    editable = true;
                }

                editable = Ext.coalesceDefined(editable, false);

                return editable;
            }
        }
    }
});
