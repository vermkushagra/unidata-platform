/**
 * Список панелей поиска по связям
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.list.RelationSearchList', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Unidata.view.component.search.query.relation.list.RelationSearchListController',
        'Unidata.view.component.search.query.relation.list.RelationSearchListModel',

        'Unidata.view.component.search.query.relation.item.RelationSearchItem',
        'Unidata.view.component.search.query.etaloncluster.EtalonClusterWindow'
    ],

    alias: 'widget.component.search.query.relation.list.relationsearchlist',

    controller: 'component.search.query.relation.list.relationsearchlist',
    viewModel: 'component.search.query.relation.list.relationsearchlist',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-search-filter',

    cls: 'un-relation-search-list',

    title: Unidata.i18n.t('glossary:relations'),

    collapsible: true,
    titleCollapse: true,
    collapsed: true,
    disabled: true,

    referenceHolder: true,

    config: {
        metaRecord: null
    },

    methodMapper: [
        {
            method: 'hidePicker'
        },
        {
            method: 'onRelationSelect'
        },
        {
            method: 'onPickerBlur'
        },
        {
            method: 'updateMetaRecord'
        },
        {
            method: 'getRelationSearchItems'
        }
    ],

    viewModelAccessors: ['metaRecord'],

    tools: [
        {
            xtype: 'un.fontbutton.additem',
            reference: 'addRelationSearchItemButton',
            handler: 'onAddRelationSearchItemButtonClick',
            color: 'lightgray',
            tooltip: Unidata.i18n.t('search>query.addRelationSearchPanel'),
            bind: {
                disabled: '{!addRelationSearchItemButtonEnabled}'
            }
        }
    ],

    items: [],

    initComponent: function () {
        this.callParent(arguments);

        this.on('add', this.updateSearchTitle, this);
        this.on('remove', this.updateSearchTitle, this);
    },

    updateSearchTitle: function () {
        var title = Unidata.i18n.t('glossary:relations'),
            count;

        if (this.items && (count = this.items.getCount())) {
            title += ' (' + count + ')';
        }

        this.setTitle(title);
    },

    initItems: function () {
        this.callParent(arguments);

        this.relationPicker = Ext.widget({
            xtype: 'component.search.query.relation.component.relationpicker',
            ui: 'un-field-default',
            floating: true,
            autoDestroy: false,
            // bind: {
            //     disabled: '{!relationPickerEnabled}'
            // },
            triggers: {
                picker: {
                    cls: 'x-form-clear-trigger',
                    handler: this.hidePicker,
                    scope: this
                }
            },
            listeners: {
                select: 'onRelationSelect',
                blur: 'onPickerBlur',
                show: function (field) {
                    setTimeout(function () {
                        field.expand();
                    }, 0);
                },
                scope: this
            }
        });
    },

    onDestroy: function () {
        this.relationPicker.destroy();
        this.relationPicker = null;
        this.callParent(arguments);
    },

    isEmptyFilter: function () {
        if (this.items.getCount()) {
            return false;
        } else {
            return true;
        }
    }

});
