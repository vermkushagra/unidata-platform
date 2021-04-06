/**
 * Расширяем функции Ext.Array
 *
 * Так как класс Ext.Array определяется без использования Ext.define => сделать переопределение через override
 * невозможно.
 *
 * @author Sergey Shishigin
 * @date 2016-10-11
 */

Ext.define('Ext.overrides.Array', {
    requires: [
        'Ext.Array'
    ]
}, function () {
    // удаляем override т.к. это фейковый класс
    delete Ext.overrides.Array;

    /**
     * This method applies the `reduceFn` function against an accumulator and each
     * value of the `array` (from left-to-right) to reduce it to a single value.
     *
     * If no `initialValue` is specified, the first element of the array is used as
     * the initial value. For example:
     *
     *      function reducer (previous, value, index) {
         *          console.log('[' + index + ']: (' + previous + ',' + value + '}');
     *          return previous * 10 + value;
     *      }
     *
     *      v = Ext.Array.reduce([2, 3, 4], reducer);
     *      console.log('v = ' + v);
     *
     *      > [1]: (2, 3)
     *      > [2]: (23, 4)
     *      > v = 234
     *
     *      v = Ext.Array.reduce([2, 3, 4], reducer, 1);
     *      console.log('v = ' + v);
     *
     *      > [0]: (1, 2)
     *      > [1]: (12, 3)
     *      > [2]: (123, 4)
     *      > v = 1234
     *
     * @param {Array} array The array to process.
     * @param {Function} reduceFn The reducing callback function.
     * @param {Mixed} reduceFn.previous The previous value.
     * @param {Mixed} reduceFn.value The current value.
     * @param {Number} reduceFn.index The index in the array of the current `value`.
     * @param {Array} reduceFn.array The array to being processed.
     * @param {Mixed} [initialValue] The starting value.
     * @return {Mixed} The reduced value.
     * @method reduce
     * @since 6.0.0
     */
    Ext.Array.reduce =  Array.prototype.reduce ?
        function (array, reduceFn, initialValue) {
            if (arguments.length === 3) {
                return Array.prototype.reduce.call(array, reduceFn, initialValue);
            }

            return Array.prototype.reduce.call(array, reduceFn);
        } :
        function (array, reduceFn, initialValue) {
            array = Object(array);
            //<debug>
            if (!Ext.isFunction(reduceFn)) {
                Ext.raise('Invalid parameter: expected a function.');
            }
            //</debug>

            var index = 0,
                length = array.length >>> 0,
                reduced = initialValue;

            if (arguments.length < 3) {
                while (true) {
                    if (index in array) {
                        reduced = array[index++];
                        break;
                    }

                    if (++index >= length) {
                        throw new TypeError('Reduce of empty array with no initial value');
                    }
                }
            }

            for (; index < length; ++index) {
                if (index in array) {
                    reduced = reduceFn(reduced, array[index], index, array);
                }
            }

            return reduced;
        };

    /**
     * Возвращает новый массив, каждый элемент html безопасен
     *
     * @param array
     * @returns {*|Array|Ext.promise.Promise}
     */
    Ext.Array.htmlEncode = function (array) {
        var encodedArray;

        encodedArray = Ext.Array.map(array, function (value) {
            return Ext.String.htmlEncode(value);
        });

        return encodedArray;
    };

    /**
     * Передвинуть элемент с позиции pos1 на позицию pos2
     * @param array Массив
     * @param pos1 Позиция 1
     * @param pos2 Позиция 2
     */
    Ext.Array.move = function (array, pos1, pos2) {
        // local variables
        var i, tmp;
        // cast input parameters to integers
        pos1 = parseInt(pos1, 10);
        pos2 = parseInt(pos2, 10);
        // if positions are different and inside array
        if (pos1 !== pos2 && 0 <= pos1 && pos1 <= array.length && 0 <= pos2 && pos2 <= array.length) {
            // save element from position 1
            tmp = array[pos1];
            // move element down and shift other elements up
            if (pos1 < pos2) {
                for (i = pos1; i < pos2; i++) {
                    array[i] = array[i + 1];
                }
            }
            // move element up and shift other elements down
            else {
                for (i = pos1; i > pos2; i--) {
                    array[i] = array[i - 1];
                }
            }
            // put element from position 1 to destination
            array[pos2] = tmp;
        }

        return array;
    };
});
