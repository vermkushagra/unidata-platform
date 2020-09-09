/**
 * Layout экрана "Классификаторы. Просмотр" (controller)
 *
 * @author Sergey Shishigin
 * @date 2016-10-07
 */
Ext.define('Unidata.view.classifierviewer.ClassifierViewerLayoutController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.classifierviewer',

    /**
     * @private
     * @param classifierName
     */
    updateClassifierName: function (classifierName) {
        var classifierNodeList = this.lookupReference('classifierNodeList');

        if (classifierNodeList) {
            classifierNodeList.setClassifierName(classifierName);
        }
    }
});
