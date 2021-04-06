/**
 * Компонет-таблетка для добавления новой группы атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-05-27
 */

Ext.define('Unidata.view.admin.entity.metarecord.presentation.attributegroup.AddTablet', {
    extend: 'Ext.container.Container',

    alias: 'widget.admin.entity.metarecord.presentation.attributegroup.addtablet',

    referenceHolder: true,

    addButton: null,  // ссылка на кнопку добавления

    config: {
        readOnly: false
    },

    cls: 'un-entity-grouptabletadd',
    layout: 'fit',
    margin: 5,

    items: [
        {
            xtype: 'button',
            reference: 'addButton',
            ui: 'default-toolbar',
            iconCls: 'icon-plus-circle',
            tooltip: Unidata.i18n.t('admin.metamodel>createGroup'),
            height: 40
        }
    ],

    initComponent: function () {
        this.callParent(arguments);

        this.initComponentReference();
        this.initComponentEvent();
    },

    initComponentReference: function () {
        this.addButton = this.lookupReference('addButton');
    },

    initComponentEvent: function () {
        this.addButton.on('click', this.onAddButtonClick, this);
    },

    onDestroy: function () {
        this.addButton = null;

        this.callParent(arguments);
    },

    onAddButtonClick: function () {
        this.fireEvent('createattributegroup');
    },

    updateReadOnly: function () {
        if (this.isConfiguring) {
            return;
        }

        this.syncReadOnly();
    },

    syncReadOnly: function () {
        var readOnly = this.getReadOnly();

        this.addButton.setDisabled(readOnly);
    }
});
