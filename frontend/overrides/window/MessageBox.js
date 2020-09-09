/**
 * Автоматический вызов destroy для всех MessageBox
 *
 * @author Aleksandr Bavin
 * @date 2017-12-19
 */
Ext.define('Ext.overrides.window.MessageBox', {

    override: 'Ext.window.MessageBox',

    closeAction: 'destroy',
    destroyOnHide: true,

    destroy: function () {
        if (this === Ext.Msg) {
            return false;
        }

        this.callParent(arguments);
    },

    onHide: function () {
        this.callParent(arguments);

        if (this.destroyOnHide) {
            this.destroy();
        }
    }

});
