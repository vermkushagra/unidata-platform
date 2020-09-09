/**
 * Список классификаторов (контроллер)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.list.ClassifierListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifier.list',

    classifierGrid: null,

    init: function () {
        var view = this.getView();

        this.initReferences();

        view.header = {
            listeners: {
                click: {
                    element: 'el',
                    fn: this.onHeaderClick.bind(this)
                }
            }
        };
    },

    initReferences: function () {
        var view = this.getView();

        this.classifierGrid = view.lookupReference('classifierGrid');
    },

    onCreateClassifierButtonClick: function () {
        var classifierStore,
            classifier;

        classifierStore = this.classifierGrid.getStore();
        classifier = this.createClassifier();
        classifierStore.add(classifier);
        this.classifierGrid.selModel.doSelect(classifier);
    },

    createClassifier: function (cfg) {
        var classifier,
            defaultCfg;

        cfg = cfg || {};

        defaultCfg = {};

        Ext.apply(cfg, defaultCfg);

        classifier = Ext.create('Unidata.model.classifier.Classifier', cfg);
        classifier.setId(null);
        Unidata.util.DataRecord.bindManyToOneAssociationListeners(classifier);

        return classifier;
    },

    onSelectClassifier: function (self, classifier) {
        var view = this.getView();

        view.fireComponentEvent('selectclassifier', classifier);
    },

    onDeselectClassifier: function () {
        // TODO: implement me
    },

    //TODO: Ivan Marshalkin: move to util class
    onHeaderClick: function (event) {
        var view   = this.getView(),
            header = view.getHeader(),
            headerEl,
            titleEl,
            targetEl;

        if (header && header.rendered) {
            headerEl = header.getEl();
            titleEl  = header.titleCmp.getEl();
            targetEl = Ext.get(event.target);

            // отменяем событие если кликнули не по дом элементу, например по задизабленому tools
            if (Ext.Array.contains([headerEl.component, titleEl.component], targetEl.component) &&
                view.collapsible &&
                view.titleCollapse) {
                view.toggleCollapse();
            }
        }
    },

    onImportClassifierClick: function (button) {
        Ext.widget({
            xtype: 'form.window',
            title: Unidata.i18n.t('glossary:importClassifier'),
            animateTarget: button,
            qaId: 'importClassifierWindow',
            closeOnSubmitFailed: false,
            formParams: {
                method: 'POST',
                url: Unidata.Config.getMainUrl() + 'internal/data/classifier/import',
                items: [
                    {
                        xtype: 'radiogroup',
                        fieldLabel: Unidata.i18n.t('classifier>importBy'),
                        labelAlign: 'left',
                        labelWidth: 120,
                        columns: [60, 60],
                        cls: 'x-check-group-alt',
                        items: [
                            {
                                boxLabel: Unidata.i18n.t('classifier>byCode'),
                                name: 'resolvingStrategy',
                                inputValue: 'CODE',
                                checked: true
                            },
                            {
                                boxLabel: 'Id',
                                name: 'resolvingStrategy',
                                inputValue: 'ID'
                            }
                        ]
                    },
                    {
                        xtype: 'fileuploadfield',
                        msgTarget: 'under',
                        allowBlank: false,
                        name: 'file'
                    }
                ]
            },
            listeners: {
                submitend: function (form, success) {
                    if (success) {
                        Unidata.showMessage(
                            Unidata.i18n.t('classifier>importProcess') +
                            Unidata.i18n.t('glossary:resultsWillAvailableInNotification')
                        );
                    }
                }
            }
        }).show();
    },

    onExportClassifierClick: function (menuItem) {
        var type = menuItem.text,
            classifierGrid = this.lookupReference('classifierGrid'),
            selection = classifierGrid.getSelection(),
            classifier = selection[0],
            classifierName = classifier.get('name'),
            url;

        url = Ext.String.format(
            '{0}internal/data/classifier/{1}/export/{2}',
            Unidata.Config.getMainUrl(),
            classifierName,
            type
        );

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            success: function (response) {
                var jsonResp = Ext.util.JSON.decode(response.responseText);

                if (jsonResp.success) {
                    Unidata.showMessage(
                        Unidata.i18n.t('classifier>exportProcess') +
                        Unidata.i18n.t('glossary:resultsWillAvailableInNotification')
                    );
                } else {
                    Unidata.showError(Unidata.i18n.t('classifier>exportProcessError'));
                }
            },
            failure: function () {
            }
        });
    },

    onRefreshButtonClick: function () {
        var viewModel = this.getViewModel(),
            store     = viewModel.get('classifierStore');

        store.load();
    }
});
