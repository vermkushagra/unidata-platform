/**
 * Утилитный класс для работы с классификаторами и узлами
 *
 * @author Sergey Shishigin
 * @date 2016-10-14
 */

Ext.define('Unidata.util.Classifier', {
    singleton: true,

    buildClassifierNodeTitle: function (classifier, classifierNode) {
        var path,
            tooltip = '',
            classifierDisplayName = classifier.get('displayName');

        if (classifierNode) {
            path = classifierNode.getPath('name', ' / ');
            tooltip = path;
        } else if (classifier) {
            tooltip = classifierDisplayName;
        }
        tooltip = tooltip.slice(3);

        return tooltip;
    }
});
