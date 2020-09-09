/**
 * Корректная вставка элементов в списки, которые начинаются не с нуля
 *
 * @author Aleksandr Bavin
 * @date 2016-10-24
 */
Ext.define('Unidata.overrides.view.NodeCache', {
    override: 'Ext.view.NodeCache',

    insert: function (insertPoint, nodes) {
        var me = this,
            elements = me.elements,
            i,
            nodeCount = nodes.length;

        // If not inserting into empty cache, validate, and possibly shuffle.
        if (me.count) {
            //<debug>
            if (insertPoint > me.endIndex + 1 || insertPoint + nodes.length - 1 < me.startIndex) {
                Ext.Error.raise('Discontiguous range would result from inserting ' + nodes.length + ' nodes at ' + insertPoint);
            }
            //</debug>

            // Move following nodes forwards by <nodeCount> positions
            // Иногда, индексация элементов может начинаться не с нуля, поэтому ориентируемся на endIndex
            //if (insertPoint < me.count) {
            if (insertPoint < (me.endIndex + 1)) {
                for (i = me.endIndex + nodeCount; i >= insertPoint + nodeCount; i--) {
                    elements[i] = elements[i - nodeCount];
                    elements[i].setAttribute('data-recordIndex', i);
                }
            }
            me.endIndex = me.endIndex + nodeCount;
        }
        // Empty cache. set up counters
        else {
            me.startIndex = insertPoint;
            me.endIndex = insertPoint + nodeCount - 1;
        }

        // Insert new nodes into place
        for (i = 0; i < nodeCount; i++, insertPoint++) {
            elements[insertPoint] = nodes[i];
            elements[insertPoint].setAttribute('data-recordIndex', insertPoint);
        }
        me.count += nodeCount;
    }

});
