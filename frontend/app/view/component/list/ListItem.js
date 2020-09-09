/**
 * @author Aleksandr Bavin
 * @date 2017-05-02
 */
Ext.define('Unidata.view.component.list.ListItem', {

    extend: 'Unidata.view.component.list.AbstractListElement',

    mixins: [
        'Ext.mixin.Factoryable'
    ],

    alias: 'widget.un.list.item.default',

    factoryConfig: {
        defaultType: 'default'
    },

    // autoEl: 'li',

    baseCls: 'un-list-item',

    tpl: [
        '{text}'
    ],

    config: {
        text: null,
        selected: false,
        collapsed: false,
        // sublistDefaults: null,
        sublist: null // вложенный список
    },

    onDestroy: function () {
        var sublist = this.getSublist();

        if (sublist) {
            sublist.destroy();
        }

        this.callParent(arguments);
    },

    onComponentRender: function () {
        var sublist = this.getSublist();

        if (sublist) {
            sublist.render(this.getEl());
        }

        this.initClickEvent();
    },

    initClickEvent: function () {
        this.getEl().on('click', function () {
            this.fireItemClick(this, this);
        }, this);
    },

    updateText: function (text) {
        this.setTplValue('text', text);
    },

    applySublist: function (list) {
        if (!list.items) {
            return null;
        }

        if (!(list instanceof Ext.Component)) {

            // list.defaults = Ext.Object.merge({}, this.getSublistDefaults(), list.defaults);

            // если нет прав
            if (!this.self.hasComponentRights(list.componentRights)) {
                return null;
            }

            list = Unidata.view.component.list.List.create(list);
        } else {
            // если нет прав
            if (!list.hasComponentRights()) {
                return null;
            }
        }

        list.ownerCt = this;

        list.on('itemselected', this.fireItemSelected, this);
        list.on('itemclick', this.fireItemClick, this);

        return list;
    },

    updateSublist: function (list) {
        var el = this.getEl();

        if (el) {
            list.render(el);
        }
    },

    isSelected: function () {
        return this.getSelected();
    },

    updateCollapsed: function () {
    },

    updateSelected: function (selected) {
        var cls = this.baseCls + '-selected';

        if (selected) {
            this.addCls(cls);

            if (!this.isConfiguring) {
                this.fireItemSelected(this, this);
            }
        } else {
            this.removeCls(cls);
        }
    },

    fireItemClick: function (component, item) {
        this.fireEvent('itemclick', this, item);
    },

    fireItemSelected: function (component, item) {
        this.fireEvent('itemselected', this, item);
    }

});
