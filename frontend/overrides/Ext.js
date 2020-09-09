/**
 * Расширяем функции Ext
 *
 * Так как класс Ext определяется без использования Ext.define => сделать переопределение через override
 * невозможно.
 *
 * @author Ivan Marshalkin
 * @date 2016-05-19
 */

Ext.define('Ext.overrides.Ext', {
}, function () {
    // удаляем override т.к. это фейковы класс
    delete Ext.overrides.Ext;

    /**
     * Возвращает первый аргумент, который не undefined
     *
     * Примеры:
     *
     * Ext.coalesceDefined(undefined, undefined, 123, null) вернет 123
     * Ext.coalesceDefined(undefined, undefined, null, 123) вернет null
     * Ext.coalesceDefined('aaa', undefined, null, 123) вернет 'aaa'
     *
     * @returns {*}
     */
    Ext.coalesceDefined = function coalesceDefined () {
        var len = arguments.length,
            i;

        for (i = 0; i < len; i++) {
            if (Ext.isDefined(arguments[i])) {
                return arguments[i];
            }
        }

        return undefined;
    };

    /**
     * Выводит в консоль имена классов в иерархии компонентов (вверх, включая себя)
     *
     * @param {string|Ext.Component} id - id компонента или компонент
     */
    Ext.printOwnerClasses = function printOwnerClasses (id, data) {
        var component = id;

        data = data || [];

        if (Ext.isString(id)) {
            component = Ext.getCmp(id);
        }

        if (!component) {
            console.log('printOwnerClasses can`t find component by id = ' + String(id));

            return;
        }

        data.push({
            className: component.$className,
            id: component.id
        });

        if (component.ownerCt) {
            Ext.printOwnerClasses(component.ownerCt, data);
        } else {
            console.table(data);
        }
    };

    /**
     * Возвращает текущий timestamp в миллисекундах
     *
     * @returns {number}
     */
    Ext.timestamp = function () {
        return Number(new Date());
    };

    /**
     * Имеет ли значение тип Integer
     * Полифилл перенесен из https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Number/isInteger
     *
     * @param value
     * @return {boolean}
     */
    Ext.isInteger = function (value) {
        return typeof value === 'number' &&
            isFinite(value) &&
            Math.floor(value) === value;
    };
});
