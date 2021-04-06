/**
 * @author Aleksandr Bavin
 * @date 2017-02-08
 */
Ext.define('Unidata.view.admin.entity.wizard.step.modelimport.ConfirmStep', {
    extend: 'Unidata.view.component.wizard.step.Step',

    requires: [
        'Unidata.view.admin.entity.wizard.step.modelimport.ConfirmStepController',
        'Unidata.view.admin.entity.wizard.step.modelimport.ConfirmStepModel'
    ],

    alias: 'widget.admin.entity.wizard.step.modelimport.confirm',

    viewModel: {
        type: 'admin.entity.wizard.step.modelimport.confirm'
    },

    controller: 'admin.entity.wizard.step.modelimport.confirm',

    referenceHolder: true,
    confirmCheckbox: null,                             // чекбокс подтверждения

    title: Unidata.i18n.t('common:confirmation'),

    disabled: true,

    items: [
        {
            html: Unidata.i18n.t('admin.metamodel>importPorcessDescription')
        },
        {
            xtype: 'checkbox',
            reference: 'confirmCheckbox',
            publishes: ['value'],
            msgTarget: 'under',
            validateOnBlur: false,
            validateOnChange: false,
            boxLabel: Unidata.i18n.t('admin.metamodel>importCounter', {checkedCount: 0, importCount: 0}),
            bind: {
                boxLabel: Unidata.i18n.t('admin.metamodel>importCounter', {checkedCount: '{checkedCount}', importCount: '{importCount}'})
            }
        }
    ],

    listeners: {
        'afterrender': 'onComponentAfterRender'
    },

    config: {
        settingsData: null,
        vertexesData: null
    },

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
    },

    initComponentReference: function () {
        var me = this;

        me.confirmCheckbox = this.lookupReference('confirmCheckbox');
    },

    onDestroy: function () {
        var me = this;

        me.confirmCheckbox = null;

        me.callParent(arguments);
    },

    createDockedButtons: function () {
        return [
            {
                xtype: 'button',
                text: Unidata.i18n.t('common:back'),
                reference: 'prevButton',
                color: 'transparent',
                listeners: {
                    click: 'onPrevClick'
                }
            },
            {
                xtype: 'container',
                flex: 1
            },
            {
                xtype: 'button',
                text: Unidata.i18n.t('common:confirm'),
                reference: 'confirmButton',
                disabled: true,
                bind: {
                    disabled: '{!confirmCheckbox.value}'
                },
                listeners: {
                    click: this.onConfirmButtonClick,
                    scope: this
                }
            }
        ];
    },

    onConfirmButtonClick: function () {
        this.runImport();
    },

    runImport: function () {
        var me = this,
            vertexesData = this.getVertexesData(),
            settingsData = this.getSettingsData(),
            notificationCountPoller = Unidata.module.poller.NotificationCountPoller.getInstance(),
            taskCountPoller = Unidata.module.poller.TaskCountPoller.getInstance(),
            jsonData,
            loadingMask;

        notificationCountPoller.stop();
        taskCountPoller.stop();

        settingsData.vertexes = [];

        vertexesData.each(function (dataItem) {
            settingsData.vertexes.push(dataItem.getData());
        });

        jsonData = Ext.util.JSON.encode(settingsData);

        Unidata.showMessage(Unidata.i18n.t('admin.metamodel>startImportProcess'));

        loadingMask = new Ext.LoadMask({
            msg: Unidata.i18n.t('admin.metamodel>importMetadata'),
            target: Unidata.getApplication().getActiveView(),
            style: {
                zIndex: 99999
            }
        });

        loadingMask.show();

        // обновляем сессию, пока идёт импорт
        Unidata.util.Session.autoUpdateSessionStart();

        Ext.Ajax.unidataRequest({
            url: Unidata.Config.getMainUrl() + 'internal/meta/model-ie/apply',
            method: 'POST',
            headers: {
                'Accept':       'application/json',
                'Content-Type': 'application/json'
            },
            timeout: 1000 * 60 * 60 * 5, //таймаут на импорт 5 часов
            jsonData: jsonData,
            success: function () {
                me.finish();
                me.showReloadWindow();
            },
            failure: function (response) {
                var responseJson,
                    prevStep;

                if (response) {
                    responseJson = Ext.JSON.decode(response.responseText, true);
                }

                if (responseJson && !responseJson.success) {
                    prevStep = me.getPrevStep();

                    if (responseJson.content) {
                        prevStep.setSettingsData(responseJson.content);
                    }

                    prevStep.activate();
                }

                Unidata.showError(Unidata.i18n.t('admin.metamodel>importError'));
            },
            callback: function () {
                loadingMask.destroy();

                Unidata.util.Session.autoUpdateSessionEnd();

                notificationCountPoller.start();
                taskCountPoller.start();
            }
        });
    },

    showReloadWindow: function () {
        Ext.create('Ext.window.Window', {
            title: Unidata.i18n.t('common:import'),
            html: Unidata.i18n.t('admin.metamodel>importSuccess'),
            modal: true,
            closable: false,
            minWidth: 250,
            minHeight: 120,
            bodyPadding: 10,
            dockedItems: {
                xtype: 'toolbar',
                reference: 'toolbar',
                ui: 'footer',
                dock: 'bottom',
                layout: {
                    pack: 'center'
                },
                items: {
                    xtype: 'button',
                    text: 'OK',
                    scope: this,
                    handler: function () {
                        window.location.reload();
                    }
                }
            }
        }).show();
    },

    /**
     * Реинициализация всех данных, которые необходимы для работы
     */
    reinitCache: function () {
        var CacheApi = Unidata.util.api.Cache,
            me = this,
            loadingMask;

        loadingMask = new Ext.LoadMask({
            msg: Unidata.i18n.t('admin.metamodel>refreshCache'),
            target: Unidata.getApplication().getActiveView(),
            style: {
                zIndex: 99999
            }
        });

        CacheApi.loadCache().then(
            function (results) {
                var MetaRecordUtil = Unidata.util.MetaRecord,
                    entities = results[2],
                    lookupEntities = results[3];

                MetaRecordUtil.cleanComponentState(entities, lookupEntities);
                loadingMask.destroy();
            },
            function () {
                Ext.Msg.alert(
                    Unidata.i18n.t('admin.metamodel>initialize'),
                    Unidata.i18n.t('admin.metamodel>initializeError'),
                    me.reinitCache,
                    me
                );
            }
        ).done();
    }
});
