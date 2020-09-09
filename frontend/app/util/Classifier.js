/**
 * Утилитный класс для работы с классификаторами и узлами
 *
 * @author Sergey Shishigin
 * @date 2016-10-14
 */

Ext.define('Unidata.util.Classifier', {
    singleton: true,

    /**
     * Построение заголовка узла записи
     * @param classifier
     * @param classifierNode
     * @return {string}
     */
    buildClassifierNodeTitle: function (classifier, classifierNode) {
        var path,
            title = '',
            classifierDisplayName = classifier.get('displayName');

        if (classifierNode) {
            path = classifierNode.getPath('name', ' / ');
            title = path;
        } else if (classifier) {
            title = classifierDisplayName;
        }
        title = title.slice(3);

        return title;
    },

    /**
     * Построение короткого заголовка узла записи
     * @param classifier
     * @param metaClassifierNode
     * @return {*}
     */
    buildClassifierNodeShortTitle: function (classifier, metaClassifierNode) {
        var title,
            classifierName,
            rootId;

        if (!metaClassifierNode || !classifier) {
            return '';
        }

        classifierName = classifier.get('name');
        rootId = classifierName + '.root';

        title = metaClassifierNode.get('text');

        if (metaClassifierNode.get('parentId') !== rootId) {
            title = '.. / ' + title;
        }

        return title;
    }
});
