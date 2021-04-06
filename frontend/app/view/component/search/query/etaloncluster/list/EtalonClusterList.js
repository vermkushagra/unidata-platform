/**
 * Список записей (набор панелей)
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.etaloncluster.list.EtalonClusterList', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.etaloncluster.list.EtalonClusterListController',

        //'Unidata.view.component.search.query.etaloncluster.list.EtalonClusterListModel'
        'Unidata.view.component.search.query.etaloncluster.component.EtalonClusterGrid'
    ],

    alias: 'widget.component.search.query.etaloncluster.list.etalonclusterlist',

    controller: 'component.search.query.etaloncluster.list.etalonclusterlist',
    viewModel: 'component.search.query.etaloncluster.list.etalonclusterlist',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    referenceHolder: true,

    entityComboBox: null,
    etalonClusterGrid: null,

    methodMapper: [
        {
            method: 'onEtalonClusterGridSelectionChange'
        },
        {
            method: 'deleteEtalonCluster'
        }
    ],

    config: {
        etalonCluster: null
    },

    viewModelAccessors: ['etalonCluster'],

    items: [
        {
            xtype: 'un.entitycombo',
            fieldLabel: Unidata.i18n.t('glossary:selectEntityOrLookupEntity'),
            reference: 'entityComboBox',
            autoSelect: true,
            allowBlank: false,
            validateBlank: true,
            emptyText: '- ' + Unidata.i18n.t('glossary:selectEntityOrLookupEntity').toLowerCase() + ' -',
            bind: {
                editable: '{!readOnly}'
            },
            modelValidation: true,
            height: 50,
            listeners: {
                select: 'onEntitySelect'
            }
        },
        {
            xtype: 'component.search.query.etaloncluster.component.etalonclustergrid',
            reference: 'etalonClusterGrid',
            flex: 1
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            padding: 0,
            //hidden: true,
            layout: {
                type: 'hbox',
                pack: 'center',
                align: 'middle'
            },
            defaults: {
                margin: 10,
                buttonSize: 'medium'
            },
            items: [
                {
                    xtype: 'un.roundbtn.delete',
                    reference: 'deleteClusterButton',
                    buttonSize: 'extrasmall',
                    shadow: false,
                    tooltip: Unidata.i18n.t('common:delete'),
                    listeners: {
                        click: 'deleteEtalonCluster'
                    },
                    bind: {
                        disabled: '{!deleteClusterButtonEnabled}'
                    }
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.initListeners();
    },

    initReferences: function () {
        this.entityComboBox = this.lookupReference('entityComboBox');
        this.etalonClusterGrid = this.lookupReference('etalonClusterGrid');
    },

    initListeners: function () {
        this.etalonClusterGrid.on('selectionchange', this.onEtalonClusterGridSelectionChange, this);
    }
});
