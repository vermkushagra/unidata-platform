/**
 * Группа по атрибуту
 *
 * @author Aleksandr Bavin
 * @date 2017-06-07
 */
Ext.define('Unidata.view.component.search.attribute.tablet.TabletGroup', {
    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.search.SearchFilter'
    ],

    requires: [
        'Unidata.view.component.search.attribute.tablet.*'
    ],

    alias: 'widget.component.search.attribute.tablet.tabletgroup',

    viewModelAccessors: [
        'attribute',
        'searchFilterItemsCount',
        'searchQuery'
    ],

    viewModel: {
        searchQuery: null,
        sortTerm: null,
        searchFilterItemsCount: 0,
        /** @type {Unidata.model.attribute.AbstractAttribute} */
        attribute: null
    },

    attributeName: null,

    ui: 'un-search-attribute-group',

    config: {
        sortTerm: null,
        searchQuery: null,
        attribute: null,
        sort: false, // использовать или нет в сортировке
        sortType: 'ASC' // ASC | DESC
    },

    sortButton: null, // кнопка сортировки

    hideCollapseTool: true,
    frameHeader: false,
    referenceHolder: true,
    collapsible: true,
    titleCollapse: true,
    collapsed: false,
    animCollapse: false,

    bind: {
        title: '{attribute.displayName}&nbsp;({searchFilterItemsCount})',
        sort: '{sortTerm.termIsActive}',
        sortType: '{sortTerm.order}'
    },

    /**
     * Установка заголовка панели
     */
    setTitle: function () {
        this.callParent(arguments);

        // текст может быть большим и он должен растягивать заголовок
        if (this.header && this.header.rendered) {
            this.header.updateLayout();
        }
    },

    twoWayBindable: [
        'sort',
        'sortType'
    ],

    header: {
        titlePosition: 1
    },

    dockedItems: [],

    tools: [],

    constructor: function () {
        this.callParent(arguments);
        this.onComponentReady();
    },

    initComponent: function () {
        var searchFilterItemsCollection;

        this.callParent(arguments);

        searchFilterItemsCollection = this.getSearchFilterItemsCollection();
        searchFilterItemsCollection.on('remove', this.fireChangeEvent, this);
        searchFilterItemsCollection.on('add', this.fireChangeEvent, this);
        searchFilterItemsCollection.on('remove', this.onFilterItemsCollectionRemove, this);
    },

    getAttributePath: function () {
        var attribute = this.getAttribute(),
            attributePath;

        // у атрибутов классификатора нет path
        if (attribute instanceof Unidata.model.attribute.ClassifierNodeAttribute) {
            attributePath = attribute.get('name');
        } else {
            attributePath = attribute.get('path');
        }

        return attributePath;
    },

    onComponentReady: function () {
        var viewModel = this.getViewModel(),
            searchQuery = this.getSearchQuery(),
            attribute = this.getAttribute(),
            attributePath = this.getAttributePath(),
            type,
            sortTerm;

        sortTerm = searchQuery.findSortFieldTerm(attributePath);

        if (attribute instanceof Unidata.model.attribute.ClassifierNodeAttribute) {
            type = attribute.get('lookupEntityCodeAttributeType');

            if (Ext.isEmpty(type)) {
                type = attribute.get('simpleDataType');
            }
        } else {
            type = attribute.get('typeValue');
        }

        if (!sortTerm) {
            sortTerm = new Unidata.module.search.term.SortField({
                field: attributePath,
                order: this.getSortType(),
                type: type,
                termIsActive: this.getSort()
            });
        }

        this.setSortTerm(sortTerm);
        viewModel.set('sortTerm', sortTerm);

        searchQuery.addTerm(sortTerm);
    },

    updateAttribute: function (attribute) {
        if (attribute) {
            this.attributeName = attribute.get('name');
        }
    },

    fireChangeEvent: function () {
        this.fireEvent('change');
    },

    onRender: function () {
        var attribute = this.getAttribute(),
            unsortable = ['Blob', 'Clob'];

        this.callParent(arguments);
        this.sortButton = this.lookupReference('sortButton');

        // прячем кнопку сортировки в некоторых случаях
        if (attribute.get('typeCategory') === 'arrayDataType' ||
            unsortable.indexOf(attribute.get('typeValue')) !== -1) {

            this.sortButton.setHidden(true);
        }

        this.updateSortButton();
    },

    onDestroy: function () {
        this.getSortTerm().destroy();
        this.callParent(arguments);
        this.sortButton = null;
    },

    /**
     * Если удалили все элементы для фильрации - удаляем группу
     *
     * @param collection
     */
    onFilterItemsCollectionRemove: function (collection) {
        if (collection.getCount() === 0) {
            this.destroy();
        }
    },

    initTools: function () {
        this.callParent(arguments);

        this.addTool([
            {
                xtype: 'tool',
                cls: 'x-tool-collapse-el',
                handler: 'toggleCollapse',
                scope: this
            },
            {
                xtype: 'un.fontbutton',
                iconCls: 'icon-sort-amount-asc',
                reference: 'sortButton',
                enableToggle: true,
                arrowVisible: false,
                color: 'lightgray',
                tooltip: Unidata.i18n.t('search>query.addToSort'),
                menuAlign: 'tl-bl',
                menu: {
                    plain: true,
                    defaults: {
                        scope: this
                    },
                    items: [
                        {
                            text: Unidata.i18n.t('search>query.priorityUp'),
                            handler: 'moveUp'
                        },
                        {
                            text: Unidata.i18n.t('search>query.priorityDown'),
                            handler: 'moveDown'
                        },
                        {
                            xtype: 'menuseparator'
                        },
                        {
                            text: Unidata.i18n.t('search>query.sortAsc'),
                            handler: 'sortAsc'
                        },
                        {
                            text: Unidata.i18n.t('search>query.sortDesc'),
                            handler: 'sortDesc'
                        },
                        {
                            xtype: 'menuseparator'
                        },
                        {
                            text: Unidata.i18n.t('search>query.noSort'),
                            handler: 'noSort'
                        }
                    ]
                }
            },
            {
                xtype: 'un.fontbutton',
                scale: 'small',
                color: 'lightgray',
                iconCls: 'icon-cross2',
                handler: 'closeTabletGroup',
                scope: this
            }
        ]);

        this.addDocked([
            {
                xtype: 'toolbar',
                dock: 'bottom',
                layout: {
                    type: 'hbox',
                    pack: 'center'
                },
                items: {
                    xtype: 'button',
                    color: 'transparent-light',
                    text: Unidata.i18n.t('search>query.or'),
                    handler: 'fireAddFilterItem',
                    scope: this
                }
            }
        ]);
    },

    closeTabletGroup: function () {
        var viewModel = this.getViewModel();

        if (viewModel.get('searchFilterItemsCount') <= 1) {
            this.destroy();

            return;
        }

        Ext.Msg.confirm(
            this.getAttribute().get('displayName'),
            Unidata.i18n.t('search>query.removeAttribute'),
            function (button) {
                if (button === 'yes') {
                    this.destroy();
                }
            },
            this
        );
    },

    /**
     * Отправляет сообщение о том, что необходимо добавить элемент фильтра
     */
    fireAddFilterItem: function () {
        this.fireEvent('addfilteritem', this);
    },

    updateSort: function () {
        this.updateSortButton();
    },

    updateSortType: function () {
        this.updateSortButton();
    },

    /**
     * Обновляет состояние кнопки
     */
    updateSortButton: function () {
        if (!this.sortButton) {
            return;
        }

        if (this.getSort()) {
            this.sortButton.setTooltip(null);
            this.sortButton.setColor('orange');
        } else {
            this.sortButton.setTooltip(Unidata.i18n.t('search>query.addToSort'));
            this.sortButton.setColor('lightgray');
        }

        this.sortButton.setIconCls('icon-sort-amount-' + this.getSortType().toLowerCase());
    },

    updateSortButtonDelayed: function () {
        clearTimeout(this.updateSortButtonTimer);
        this.updateSortButtonTimer = Ext.defer(this.updateSortButton, 1);
    },

    moveUp: function () {
        this.fireEvent('moveup', this);
    },

    moveDown: function () {
        this.fireEvent('movedown', this);
    },

    sortAsc: function () {
        this.setSortType('ASC');
        this.setSort(true);
    },

    sortDesc: function () {
        this.setSortType('DESC');
        this.setSort(true);
    },

    noSort: function () {
        this.setSort(false);
    },

    /**
     * Возвращает данные для сортировки
     *
     * @returns {boolean|Object}
     */
    getSortData: function () {
        var attribute = this.getAttribute(),
            sortData;

        if (!this.getSort()) {
            return false;
        }

        sortData = {
            field: attribute.get('path'),
            type: attribute.get('typeValue'),
            order: this.getSortType()
        };

        return sortData;
    }

});
