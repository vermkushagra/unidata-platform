/**
 * Окно редактирования набора записей
 *
 * @author Sergey Shishigin
 * @date 2016-02-02
 */

Ext.define('Unidata.view.component.search.SearchWindow', {
    extend: 'Ext.window.Window',

    alias: 'widget.component.search.searchwindow',

    requires: [
        'Unidata.view.component.search.searchpanel.SearchPanel'
    ],

    referenceHolder: true,

    config: {
        toEntityDefaultDisplayAttributes: null,
        selectedEntityName: null,
        entityReadOnly: null
    },

    layout: {
        type: 'fit'
    },

    cls: 'un-search-window',

    modal: true,
    resizable: false,
    width: 600,
    height: 600,
    padding: 0,

    searchPanel: null,

    dockedItems: {
        xtype: 'toolbar',
        reference: 'toolbar',
        ui: 'footer',
        dock: 'bottom',
        layout: {
            pack: 'center'
        },
        defaults: {
            minWidth: 75
        },
        items: []
    },

    items: [
        {
            xtype: 'component.search.searchpanel',
            reference: 'searchPanel'
        }
    ],

    initComponent: function () {
        var searchPanel,
            selectedEntityName,
            entityReadOnly;

        this.callParent(arguments);

        this.initReferences();
        this.initToolbar();

        selectedEntityName = this.getSelectedEntityName();
        entityReadOnly = this.getEntityReadOnly();

        searchPanel = this.searchPanel;

        if (selectedEntityName) {
            searchPanel.setSelectedEntityName(selectedEntityName);
            searchPanel.setToEntityDefaultDisplayAttributes(this.getToEntityDefaultDisplayAttributes());
        }

        searchPanel.setEntityReadOnly(entityReadOnly);

        // растягиваем панель результатов поиска
        searchPanel.flexResultSetPanel();
    },

    initReferences: function () {
        this.searchPanel = this.lookupReference('searchPanel');
    },

    initToolbar: function () {
        var toolbar = this.lookupReference('toolbar');

        toolbar.add(
            {
                xtype: 'button',
                reference: 'okButton',
                text: 'OK',
                scope: this,
                handler: function () {
                    var searchPanel = this.searchPanel,
                        searchHits;

                    searchHits = searchPanel.getSelectedSearchHits();

                    this.fireEvent('okbtnclick', this, searchHits);
                    this.close();
                }
            },
            {
                xtype: 'button',
                reference: 'cancelButton',
                text: Unidata.i18n.t('common:cancel', {context: 'noun'}),
                scope: this,
                handler: function () {
                    this.fireEvent('cancelbtnclick', this);
                    this.close();
                }
            }
        );
    }
});
