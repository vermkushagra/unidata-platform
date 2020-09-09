/**
 * Тулбар с кнопками
 *
 * @author Aleksandr Bavin
 * @date 2017-06-14
 */
Ext.define('Unidata.view.component.toolbar.Toolbar', {

    extend: 'Ext.container.Container',

    alias: 'widget.un.toolbar',

    layout: {
        type: 'hbox',
        align: 'middle'
    },

    defaults: {
        scale: 'large'
    },

    baseCls: 'un-toolbar',

    autoHide: false, // автоматически прятать тулбар, если все кнопки скрыты

    onAdd: function (component) {
        this.callParent(arguments);

        if (component instanceof Ext.button.Button) {
            component.on('show', this.updateToolbarVisibility, this);
            component.on('hide', this.updateToolbarVisibility, this);
        }

        this.updateToolbarVisibility();
    },

    onRemove: function (component) {
        this.callParent(arguments);

        if (component instanceof Ext.button.Button) {
            component.un('show', this.updateToolbarVisibility, this);
            component.un('hide', this.updateToolbarVisibility, this);
        }

        this.updateToolbarVisibility();
    },

    /**
     * Автоматически прячет тулбар, если все кнопки скрыты
     */
    updateToolbarVisibility: function () {
        var hasVisibleItems = false;

        if (!this.autoHide) {
            return;
        }

        this.items.each(function (item) {
            if (item instanceof Ext.button.Button && !item.isHidden()) {
                hasVisibleItems = true;

                return false;
            }
        });

        this.setHidden(!hasVisibleItems);
    }

});
