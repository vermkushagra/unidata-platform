/**
 * Панель реализующая группировку атрибутов
 *
 * @author Ivan Marshalkin
 * @date 2016-05-27
 */

Ext.define('Unidata.view.admin.entity.metarecord.presentation.attributegroup.Group', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupController',
        'Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupModel',
        'Unidata.view.admin.entity.metarecord.presentation.attributegroup.GroupColumn'
    ],

    alias: 'widget.admin.entity.metarecord.presentation.attributegroup',

    controller: 'admin.entity.metarecord.presentation.attributegroup',
    viewModel: {
        type: 'admin.entity.metarecord.presentation.attributegroup'
    },

    referenceHolder: true,

    config: {
        attributeGrid: null,
        readOnly: false
    },

    columnsContainer: null,

    cls: 'un-presentation-attribute-group',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    listeners: {
        render: 'onRender'
    },

    /**
     * Пробрасываем методы из view в controller или model
     */
    methodMapper: [
        {
            method: 'onDropNode'
        },
        {
            method: 'updateReadOnly'
        }
    ],

    initComponent: function () {
        var attributeGrid,
            attributeGridView;

        this.callParent(arguments);

        attributeGrid = this.getAttributeGrid();
        attributeGridView = attributeGrid.getView();
        attributeGridView.on('drop', this.onDropNode, this);
    },

    items: [
        {
            xtype: 'container',
            reference: 'columnsContainer',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
                {
                    xtype: 'admin.entity.metarecord.presentation.attributegroup.column',
                    maxWidth: 350,
                    flex: 1,
                    listeners: {
                        beforeremovegroup: 'onBeforeRemoveGroup',
                        removegroup: 'onRemoveGroup',
                        changegroup: 'onChangeGroup',
                        movegroup: 'onMoveGroup'
                    }
                }
            ]
        },
        {
            xtype: 'container',
            cls: 'un-entity-columnadd',
            layout: 'vbox',
            defaults: {
                xtype: 'button',
                ui: 'un-toolbar-admin',
                margin: '0 0 5 0',
                scale: 'medium'
            },
            items: [
                {
                    tooltip: Unidata.i18n.t('admin.metamodel>addCol'),
                    handler: 'onAddColumnClick',
                    iconCls: 'icon-plus',
                    bind: {
                        disabled: '{readOnly}'
                    }
                },
                {
                    tooltip: Unidata.i18n.t('admin.metamodel>removeEmptyCols'),
                    handler: 'onRemoveColumnClick',
                    iconCls: 'icon-minus',
                    bind: {
                        disabled: '{readOnly}'
                    }
                }
            ]
        }
    ]
});
