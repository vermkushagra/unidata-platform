/**
 * Представление одного backend property
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertyeditor.PropertyEditor', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.beproperties.propertyeditor',

    viewModel: {
        type: 'admin.beproperties.propertyeditor'
    },
    controller: 'admin.beproperties.propertyeditor',

    requires: [
        'Unidata.view.admin.beproperties.propertyeditor.PropertyEditorController',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyEditorModel',

        'Unidata.view.admin.beproperties.propertyeditor.PropertyInteger',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyNumber',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyString',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyUnknown',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyBoolean',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyEnumeration'
    ],

    referenceHolder: true,

    config: {
        readOnly: false
    },

    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    propertyLabel: null,
    propertyContainer: null,
    propertyToolTip: null,

    property: null,

    layout: {
        type: 'hbox',
        align: 'middle'
    },

    margin: 10,

    initComponent: function () {
        this.callParent(arguments);

        this.initReferences();
    },

    initReferences: function () {
        this.propertyLabel = this.lookupReference('propertyLabel');
        this.propertyContainer = this.lookupReference('propertyContainer');
    },

    onDestroy: function () {
        this.propertyLabel = null;
        this.propertyContainer = null;

        if (this.propertyToolTip) {
            this.propertyToolTip.destroy();

            this.propertyToolTip = null;
        }

        this.callParent(arguments);
    },

    items: [
        {
            xtype: 'label',
            style: 'overflow: hidden; text-overflow: ellipsis;',
            reference: 'propertyLabel',
            maxWidth: 300,
            width: 300
        },
        {
            xtype: 'container',
            reference: 'propertyContainer',
            layout: 'fit',
            flex: 1
        }
    ],

    listeners: {
        afterrender: 'onPropertyEditorAfterRender'
    }
});
