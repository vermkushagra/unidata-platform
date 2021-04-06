Ext.define('Unidata.view.steward.dataviewer.card.data.DataCardModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.steward.dataviewer.datacard',

    constructor: function (options) {
        // отменяем наследование значений из родительской viewModel
        options        = options || {};
        options.parent = null;

        this.callParent([options]);
    },

    data: {
        metaRecord: null,
        dataRecord: null,
        etalonId: null,
        referenceRelations: null,
        timeInterval: null,
        timeIntervalDate: null,
        timeIntervalStore: null
    },

    stores: {
        /**
         * стор для поиска workflow
         * @see Unidata.view.steward.dataviewer.card.data.DataCardController.checkWorkflow
         */
        taskSearchHitStore: {
            model: 'Unidata.model.workflow.Task',
            proxy: 'workflow.task.search',
            pageSize: 1
        }
    },

    formulas: {
        /**
         * Значение флага phantom текущей записи
         */
        etalonPhantom: {
            bind: {
                bindTo: '{dataRecord}',
                deep: true
            },
            get: function (dataRecord) {
                var phantom = true;

                if (dataRecord) {
                    phantom = dataRecord.phantom || !dataRecord.get('etalonId');
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },

        /**
         * Определяет видимость панели со связями типа ссылка
         */
        referencePanelVisible: {
            bind: {
                bindTo: '{metaRecord}',
                deep: true
            },
            get: function (metaRecord) {
                var has;

                has = Unidata.util.MetaRecord.hasReferenceRelation(metaRecord);

                // панель видна если в модели заданы связи типа reference
                return has;
            }
        },

        /**
         * Определяет видимость панели со связями типа включение
         */
        containsPanelVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                deep: true
            },
            get: function (getter) {
                var metaRecord = this.get('metaRecord'),
                    has        = Unidata.util.MetaRecord.hasContainsRelation(metaRecord),
                    visible    = false;

                // панель видна если в модели заданы связи типа contains и запись сохранялась на сервере
                if (!getter.etalonPhantom && has) {
                    visible = true;
                }

                return visible;
            }
        },

        /**
         * Определяет видимость панели со связями типа многие-ко-многим
         */
        m2mPanelVisible: {
            bind: {
                etalonPhantom: '{etalonPhantom}',
                deep: true
            },
            get: function (getter) {
                var metaRecord = this.get('metaRecord'),
                    has        = Unidata.util.MetaRecord.hasM2mRelation(metaRecord),
                    visible    = false;

                // панель видна если в модели заданы связи типа m2m и запись сохранялась на сервере
                if (!getter.etalonPhantom && has) {
                    visible = true;
                }

                return visible;
            }
        }
    }
});
