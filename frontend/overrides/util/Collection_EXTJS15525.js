/**
 *
 * EXTJS-15525 - Syncing of newly client-side created records with a server results in the new record being permanently selected
 *
 * https://www.sencha.com/forum/showthread.php?295892-Ext-JS-5.1-Post-GA-Patches&langid=14
 *
 * BUG: EXTJS-15525
 *
 * Данный патч применим только к версии 5.1.0.107
 *
 * Исправлено в 5.1.1.451
 *
 * !!! ПРИ ИСПОЛЬЗОВАНИИ БИБЛИОТЕКИ ВЕРСИЕЙ ВЫШЕ - УДАЛИТЬ !!!
 *
 * @author Ivan Marshalkin
 * @date 2016-05-11
 */

Ext.define('Ext.overrides.util.Collection_EXTJS15525', {
    override: 'Ext.util.Collection',

    compatibility: '5.1.0.107',

    updateKey: function (item, oldKey) {
        var me = this,
            map = me.map,
            indices = me.indices,
            source = me.getSource(),
            newKey;

        if (source && !source.updating) {
            // If we are being told of the key change and the source has the same idea
            // on keying the item, push the change down instead.
            source.updateKey(item, oldKey);
        }
        // If there *is* an existing item by the oldKey and the key yielded by the new item is different from the oldKey...
        else if (map[oldKey] && (newKey = me.getKey(item)) !== oldKey) {
            if (oldKey in map || map[newKey] !== item) {
                if (oldKey in map) {
                    //<debug>
                    if (map[oldKey] !== item) {
                        Ext.Error.raise('Incorrect oldKey "' + oldKey +
                            '" for item with newKey "' + newKey + '"');
                    }
                    //</debug>

                    delete map[oldKey];
                }

                // We need to mark ourselves as updating so that observing collections
                // don't reflect the updateKey back to us (see above check) but this is
                // not really a normal update cycle so we don't call begin/endUpdate.
                me.updating++;

                me.generation++;
                map[newKey] = item;

                if (indices) {
                    indices[newKey] = indices[oldKey];
                    delete indices[oldKey];
                }

                me.notify('updatekey', [{
                    item: item,
                    newKey: newKey,
                    oldKey: oldKey
                }]);

                me.updating--;
            }
        }
    }
});
