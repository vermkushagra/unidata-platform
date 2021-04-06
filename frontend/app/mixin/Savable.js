/**
 * Миксин, который позволяет рекурсивно собирать конфиг для сохранения информации о компоненте,
 * и всех его дочерних savable компонентах, для последующего воссоздания
 *
 * @author Aleksandr Bavin
 * @date 2017-10-19
 */
Ext.define('Unidata.mixin.Savable', {

    extend: 'Ext.Mixin',

    hasSavableMixin: true,

    /**
     * Рекурсивно собирает данные для сохранения
     *
     * @returns {Object}
     */
    getSaveData: function () {
        var saveData = {xclass: this.$className};

        this.getIterator().each(function (item) {
            if (item && Ext.isObject(item) && item.hasSavableMixin) {
                if (!saveData.items) {
                    saveData.items = [];
                }

                saveData.items.push(item.getSaveData());
            }
        });

        this.editSaveData(saveData);

        return saveData;
    },

    /**
     *
     * @param saveData
     * @returns {Object}
     */
    editSaveData: Ext.emptyFn,

    /**
     * @typedef {Object} Iterator
     * @property {Function} each
     */

    /**
     * @returns {Iterator}
     */
    getIterator: function () {
        if (this instanceof Ext.container.Container) {
            return this.items;
        }

        if (this instanceof Unidata.view.component.AbstractComponentItems) {
            return this.getItemsCollection();
        }
    }

});
