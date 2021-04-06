/**
 * Контейнер классификатора (контроллер)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.itemcontainer.ClassifierItemContainerController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifier.itemcontainer',

    classifierItem: null,

    init: function () {
        this.initComponentListeners();
    },

    initComponentListeners: function () {
        var view = this.getView();

        view.addComponentListener('selectclassifier', this.onSelectClassifier, this);
        view.addComponentListener('classifierstatuschanged', this.onClassifierStatusChanged, this);
        view.addComponentListener('classifierdelete', this.onClassifierDelete, this);
    },

    createClassifierItem: function (classifier, customCfg) {
        var classifierItem,
            cfg;

        cfg = {
            classifier: classifier
        };

        //customCfg = customCfg || {};

        Ext.apply(cfg, customCfg);

        classifierItem = Ext.create('Unidata.view.classifier.item.ClassifierItem', cfg);

        return classifierItem;
    },

    onSelectClassifier: function (classifier) {
        var view = this.getView();

        view.removeAll();
        this.classifierItem = null;
        this.classifierItem = this.createClassifierItem();
        view.add(this.classifierItem);

        if (classifier.phantom) {
            this.classifierItem.setClassifier(classifier);
        } else {
            view.setStatus(Unidata.StatusConstant.LOADING);
            classifier.load({
                scope: this,
                success: this.onClassifierLoadSuccess,
                failure: this.onClassifierLoadFailure
            });
            //classifier = this.loadClassifier(classifier);
            //TODO: promise ?
        }

    },

    onClassifierStatusChanged: function (status) {
        var view = this.getView();

        view.setStatus(status);
    },

    onClassifierLoadSuccess: function (classifier) {
        var view = this.getView();

        view.setStatus(Unidata.StatusConstant.READY);

        if (this.classifierItem) {
            this.classifierItem.setClassifier(classifier);
        }
    },

    onClassifierLoadFailure: function () {
        var view = this.getView();

        view.setStatus(Unidata.StatusConstant.NONE);
        Unidata.showError(view.loadClassifierFailureText);
    },

    onClassifierDelete: function () {
        var view = this.getView();

        view.removeAll();
    },

    updateStatus: function (status, oldStatus) {
        var StatusConstantClass = Unidata.StatusConstant,
            view = this.getView(),
            classifierItem = this.classifierItem,
            buttons;

        if (classifierItem) {
            buttons = classifierItem.buttons;
        }

        if (status === oldStatus) {
            return;
        }

        switch (status) {
            case StatusConstantClass.NONE:
                if (buttons) {
                    buttons.setDisabled(false);
                }
                view.setLoading(false);
                break;
            case StatusConstantClass.LOADING:
                if (buttons) {
                    buttons.setDisabled(true);
                }
                view.setLoading(this.loadingText);
                break;
            case StatusConstantClass.READY:
                if (buttons) {
                    buttons.setDisabled(false);
                }
                view.setLoading(false);
                break;
            default:
                throw new Error(Unidata.i18n.t('glossary:badStatus'));
                //TODO: implement me better
                break;
        }
    }
});
