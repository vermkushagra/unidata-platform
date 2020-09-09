/**
 * Панель группы backend properties
 *
 * @author Ivan Marshalkin
 * @date 2017-09-21
 */

Ext.define('Unidata.view.admin.beproperties.propertygroup.PropertyGroup', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.beproperties.group',

    viewModel: {
        type: 'admin.beproperties.group'
    },
    controller: 'admin.beproperties.group',

    requires: [
        'Unidata.view.admin.beproperties.propertygroup.PropertyGroupController',
        'Unidata.view.admin.beproperties.propertygroup.PropertyGroupModel',
        'Unidata.view.admin.beproperties.propertyeditor.PropertyEditor'
    ],

    config: {
        readOnly: false
    },

    methodMapper: [
        {
            method: 'updateReadOnly'
        }
    ],

    properties: null,

    referenceHolder: true,

    ui: 'un-card',
    cls: 'un-beprop-propgroup',

    collapsible: true,
    titleCollapse: true,
    animCollapse: false,

    width: 800,
    maxWidth: 800,
    minWidth: 800,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
    ]
});
