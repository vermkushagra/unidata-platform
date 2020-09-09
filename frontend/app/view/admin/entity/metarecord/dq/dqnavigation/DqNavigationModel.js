/**
 *
 * @author Ivan Marshalkin
 * @date 2018-01-29
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.dqnavigation.DqNavigationModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.admin.entity.metarecord.dq.dqnavigation',

    constructor: function (options) {
        // отменяем наследование значений из родительской viewModel
        options = options || {};
        options.parent = null;

        this.callParent([options]);

        // устанавливаем текущий режим работы с черновиком
        this.set('draftMode', Unidata.module.notifier.DraftModeNotifier.getDraftMode());
    },

    data: {
        metaRecord: null,
        readOnly: null,
        filterData: null,
        dqTestMode: false,
        selectedCount: 0,
        attributeTreeNode: null,
        draftMode: false
    },

    stores: {
        filteredDqRules: {
            source: '{metaRecord.dataQualityRules}',
            filters: [
                {
                    // фильтруем все записи
                    filterFn: function () {
                        return false;
                    }
                }
            ]
        }
    },

    formulas: {
        /**
         * Признак доступности кнопок удаления правил качества
         */
        isDeleteDqRuleHidden: {
            bind: {
                readOnly: '{readOnly}',
                dqTestMode: '{dqTestMode}'
            },
            get:  function (getter) {
                var hidden = false;

                if (getter.readOnly || getter.dqTestMode) {
                    hidden = true;
                }

                return hidden;
            }
        },

        /**
         * Признак доступности кнопки показа визарда тестирования
         */
        isShowDqTestWizardButtonEnabled: {
            bind: {
                selectedCount: '{selectedCount}'
            },
            get:  function (getter) {
                var enabled = false;

                if (getter.selectedCount > 0) {
                    enabled = true;
                }

                return enabled;
            }
        },

        /**
         * Признак доступности кнопки тестирования правил качества
         */
        isTestDqModeButtonEnabled: {
            bind: {
                attributeTreeNode: '{attributeTreeNode}',
                metaRecord: '{metaRecord}',
                metaRecordDeepDirty: '{metaRecord.deepDirty}',
                metaRecordModelDirty: '{metaRecord.modelDirty}',
                draftMode: '{draftMode}'
            },
            get: function (getter) {
                var node = getter.attributeTreeNode,
                    metaRecord = getter.metaRecord,
                    deepDirty = getter.metaRecordDeepDirty,
                    dirty = getter.metaRecordModelDirty,
                    enabled = false;

                if (!node || !metaRecord) {
                    return false;
                }

                // кнопка доступна только в режиме работы с черновиком
                if (!getter.draftMode) {
                    return false;
                }

                if (node.isRoot() && !deepDirty && !dirty) {
                    enabled = true;
                }

                return enabled;
            }
        },

        /**
         * Признак доступности кнопки создания правила качества
         */
        isCreateDqRuleEnabled: {
            bind: {
                readOnly: '{readOnly}',
                dqTestMode: '{dqTestMode}'
            },
            get:  function (getter) {
                var enabled = true;

                if (getter.readOnly || getter.dqTestMode) {
                    enabled = false;
                }

                return enabled;
            }
        }
    }
});
