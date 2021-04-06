Ext.define('Ext.overrides.layout.container.Card', {

    override: 'Ext.layout.container.Card',

    /**
     * Перед тихой установкой me.activeItem = result
     * добавлена проверка result.rendered
     * если элемент не отрендерен, могут не отправляться необходимые события
     * @returns {*}
     */
    getActiveItem: function () {
        var me = this,
            // It's necessary to check that me.activeItem is not undefined as it could be 0 (falsey). We're more interested in
            // checking the layout's activeItem property, since that is the source of truth for an activeItem.  If it's
            // determined to be empty, check the owner. Note that a default item is returned if activeItem is `undefined` but
            // not `null`. Also, note that `null` is legitimate value and completely different from `undefined`.
            item = me.activeItem === undefined ? (me.owner && me.owner.activeItem) : me.activeItem,
            result = me.parseActiveItem(item);

        // && result.rendered
        // Sanitize the result in case the active item is no longer there.
        if (result && me.owner.items.indexOf(result) !== -1) {
            if (result.rendered) {
                me.activeItem = result;

                if (result.getEl()) {
                    // в некоторых случаях, добавляемый элемент скрыт, нужно показать
                    result.getEl().show();
                }
            } else {
                result.on('render', this.activateOnRender, this, {single: true});
            }

            return result;
        }

        // Note that in every use case me.activeItem will have a truthy value except for when a container or tabpanel is explicity
        // configured with activeItem/Tab === null or when an out-of-range index is given for an active tab (as it will be undefined).
        // In those cases, it is meaningful to return the null value, so do so.
        return result == null ? null : (me.activeItem || me.owner.activeItem);
    },

    /**
     * Активируем таб после рендера
     * @param item
     */
    activateOnRender: function (item) {
        this.setActiveItem(item);
    }

});
