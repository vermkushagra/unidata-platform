/**
 * @author Aleksandr Bavin
 * @date 2017-03-10
 */
Ext.define('Unidata.view.steward.dataentity.attribute.array.ArrayTagField', {

    extend: 'Ext.form.field.Tag',

    alias: 'widget.arrayattribute.tagfield',

    cls: 'un-array-attribute-read-tagfield',

    tagSelectedCls: Ext.baseCSSPrefix + 'tagfield-item-selected-un',

    hideTrigger: true,
    expand: Ext.emptyFn,

    /**
     * Не выбираем фейковый тэг
     *
     * @param e
     * @returns {*|Object}
     */
    onItemListClick: function (e) {
        var me = this,
            itemEl = e.getTarget(me.tagItemSelector),
            fakeTag = itemEl ? e.getTarget('.un-array-attribute-fake-tag') : false;

        if (fakeTag) {
            this.fireEvent('faketagclick');

            return;
        }

        return this.callParent(arguments);
    }

});
