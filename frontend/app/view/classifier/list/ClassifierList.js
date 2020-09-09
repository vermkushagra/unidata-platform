/**
 * Список классификаторов
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.list.ClassifierList', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.classifier.list.ClassifierListController',
        'Unidata.view.classifier.list.ClassifierListModel'
    ],

    alias: 'widget.classifier.list',

    viewModel: {
        type: 'classifier.list'
    },

    controller: 'classifier.list',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    title: Unidata.i18n.t('glossary:classifiers'),

    collapsible: true,
    collapseDirection: 'left',
    collapseMode: 'header',
    animCollapse: false,
    titleCollapse: true,
    width: 300,
    //cls: 'un-resultset un-search-panel un-classifier-list',
    ui: 'un-result',

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            padding: 0,
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
                    xclass: 'Unidata.view.component.button.RoundButton',
                    iconCls: 'icon-hdd-down',
                    tooltip: Unidata.i18n.t('glossary:importClassifier'),
                    listeners: {
                        click: 'onImportClassifierClick'
                    },
                    securedResource: 'ADMIN_CLASSIFIER_MANAGEMENT',
                    securedEvent: 'create',
                    color: 'gray'
                },
                {
                    xclass: 'Unidata.view.component.button.RoundButton',
                    iconCls: 'icon-hdd-up',
                    tooltip: Unidata.i18n.t('classifier>exportClassifier'),
                    disabled: true,
                    bind: {
                        disabled: '{classifierExportDisabled}'
                    },
                    arrowVisible: false,
                    menuAlign: 'l-r',
                    menu: {
                        cls: 'un-classifier-list-button-menu',
                        shadow: false,
                        plain: true,
                        minWidth: 70,
                        defaults: {
                            listeners: {
                                click: 'onExportClassifierClick'
                            }
                        },
                        items: [
                            {
                                text: 'xml'
                            },
                            {
                                text: 'xlsx'
                            }
                        ]
                    },
                    color: 'gray'
                }
            ]
        }
    ],

    tools: [
        {
            type: 'plus',
            tooltip: Unidata.i18n.t('classifier>createNewClassifier'),
            handler: 'onCreateClassifierButtonClick',
            securedResource: 'ADMIN_CLASSIFIER_MANAGEMENT',
            securedEvent: 'create'
        },
        {
            type: 'refresh',
            handler: 'onRefreshButtonClick',
            tooltip: Unidata.i18n.t('classifier>refreshClassifiersList'),
            securedResource: 'ADMIN_CLASSIFIER_MANAGEMENT',
            securedEvent: 'read'
        }
    ],

    items: [{
        xtype: 'grid',
        reference: 'classifierGrid',
        hideHeaders: true,
        flex: 1,
        emptyText: Unidata.i18n.t('classifier>noData'),
        bind: {
            store: '{classifierStore}'
        },
        cls: 'un-result-grid',
        columns: [
            {
                text: Unidata.i18n.t('glossary:designation'),
                dataIndex: 'displayName',
                sortable: false,
                hideable: false,
                flex: 1
            }
        ],
        listeners: {
            select: 'onSelectClassifier',
            deselect: 'onDeselectClassifier'
        }
    }]
});
