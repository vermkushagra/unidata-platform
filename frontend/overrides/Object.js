/**
 * Расширяем функции Ext.Object
 *
 * Так как класс Ext.Object определяется без использования Ext.define => сделать переопределение через override
 * невозможно.
 *
 * @author Aleksandr Bavin
 * @date 2018-08-28
 */

Ext.define('Ext.overrides.Object', {
    requires: [
        'Ext.Object'
    ]
}, function () {
    // удаляем override т.к. это фейковый класс
    delete Ext.overrides.Object;

    /**
     * Гарантированно устанавливает значение для объекта по пути path
     *
     * @param {Object} obj
     * @param {string|Array<string>} path - строка вида X.Y.Z или массив
     * @param {*} value
     * @param {boolean} [init] - только инициализация (если true, то не перезаписывает уже существующее значение)
     * @returns {*}
     */
    Ext.Object.setValueByPath = function (obj, path, value, init) {
        var pathParts = Ext.isString(path) ? path.split('.') : path,
            currentObj = obj;

        Ext.Array.each(pathParts, function (pathPart, index, array) {
            var isLast = (index === (array.length - 1));

            if (isLast) {
                if (init && currentObj.hasOwnProperty(pathPart)) {
                    value = currentObj[pathPart];

                    return;
                }

                currentObj[pathPart] = value;

            } else {
                if (currentObj[pathPart]) {
                    currentObj = currentObj[pathPart];
                } else {
                    currentObj = currentObj[pathPart] = {};
                }
            }
        });

        return value;
    };

});
