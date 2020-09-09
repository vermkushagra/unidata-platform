/**
 * @author Aleksandr Bavin
 * @date 2017-05-15
 */
Ext.define('Unidata.view.component.list.AbstractListElement', {

    extend: 'Unidata.view.component.AbstractComponent',

    config: {
        reference: null
    },

    /**
     * Добавляет класс на основе имени референса
     *
     * @param {String} newReference
     * @param {String} oldReference
     */
    updateReference: function (newReference, oldReference) {
        if (oldReference) {
            this.removeCls(this.baseCls + '-' + oldReference);
        }

        if (newReference) {
            this.addCls(this.baseCls + '-' + newReference);
        }
    }

});
