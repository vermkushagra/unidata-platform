/**
 *
 * Добавлен фикс для бага EXTJS-21965 - TreeStore#findNode contains typo in optimized path
 * Баг в методе findNode:
 * д.б. fieldName === this.model.idProperty вместо value === this.model.idProperty
 *
 * Использована версия метода из версии Ext JS 6.5.3
 * Подробности:
 * https://www.sencha.com/forum/showthread.php?313545-Probably-typo-in-TreeStore-findNode
 * http://docs.sencha.com/extjs/6.2.0/guides/whats_new/release_notes.html
 *
 * @author Sergey Shishigin
 * @date 2018-04-02
 */

Ext.define('Ext.overrides.data.TreeStore', {
    override: 'Ext.data.TreeStore',

    /**
     * Finds the first matching node in the tree by a specific field value regardless of visibility
     * due to collapsed states; all nodes present in the tree structure are searched.
     *
     * @param {String} fieldName The name of the Record field to test.
     * @param {String/RegExp} value Either a string that the field value
     * should begin with, or a RegExp to test against the field.
     * @param {Boolean} [startsWith=true] Pass `false` to allow a match to start
     * anywhere in the string. By default the `value` will match only at the start
     * of the string.
     * @param {Boolean} [endsWith=true] Pass `false` to allow the match to end before
     * the end of the string. By default the `value` will match only at the end of the
     * string.
     * @param {Boolean} [ignoreCase=true] Pass `false` to make the `RegExp` case
     * sensitive (removes the 'i' flag).
     * @return {Ext.data.NodeInterface} The matched node or null
     */
    findNode: function (fieldName, value, startsWith, endsWith, ignoreCase) {
        if (Ext.isEmpty(value, false)) {
            return null;
        }

        // If they are looking up by the idProperty, do it the fast way.
        if (fieldName === this.model.idProperty && arguments.length < 3) {
            return this.byIdMap[value];
        }
        var regex = Ext.String.createRegex(value, startsWith, endsWith, ignoreCase),
            result = null;

        Ext.Object.eachValue(this.byIdMap, function (node) {
            if (node && regex.test(node.get(fieldName))) {
                result = node;

                return false;
            }
        });

        return result;
    }
});
