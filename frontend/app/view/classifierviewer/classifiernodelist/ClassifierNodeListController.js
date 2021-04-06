/**
 * Список узлов классификатора с выбором классификатора (controller)
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.classifiernodelist.ClassifierNodeListController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifierviewer.classifiernodelist',

    classifierNodePanel: null,
    /**
     * @const classifierTokenName
     */
    classifierTokenName: 'classifier',

    init: function () {
        this.initRouter();
    },

    initRouter: function () {
        this.routeToClassifier();

        // Добавить destroy токенов
        Unidata.util.Router.on(this.classifierTokenName, this.routeToClassifier, this);
    },

    routeToClassifier: function (tokenValues) {
        var view = this.getView();

        if (!tokenValues) {
            tokenValues = Unidata.util.Router.getTokenValues(this.classifierTokenName);
        }

        if (tokenValues.classifierName) {
            view.setClassifierName(tokenValues.classifierName);
        }
    },

    /**
     * @private
     * @param classifierName
     */
    updateClassifierName: function (classifierName) {
        var classifierNodePanel = this.lookupReference('classifierNodePanel');

        if (classifierNodePanel) {
            classifierNodePanel.setClassifierName(classifierName);
        }
    }
});
