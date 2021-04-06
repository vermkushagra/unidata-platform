Ext.define('Ext.overrides.dom.Element', {

    override: 'Ext.dom.Element',

    /**
     * В ExtJS 5 эта функция кривая, вообще никогда не выполняется.
     * Исправленный вариант
     *
     * @param text
     */
    updateText: function (text) {

        var me = this,
            dom = me.dom,
            textNode;

        if (dom) {

            textNode = dom.firstChild;

            if (!textNode || (textNode.nodeType !== 3 || textNode.nextSibling)) {
                textNode = document.createTextNode(text);
                me.empty();
                dom.appendChild(textNode);
            }

            if (text) {
                textNode.data = text;
            }
        }
    }

});
