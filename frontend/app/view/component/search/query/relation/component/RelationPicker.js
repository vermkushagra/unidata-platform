/**
 * Выпадающий список со связями
 *
 * @author Sergey Shishigin
 * @date 2017-02-01
 */
Ext.define('Unidata.view.component.search.query.relation.component.RelationPicker', {
    extend: 'Ext.form.field.Picker',

    alias: 'widget.component.search.query.relation.component.relationpicker',

    requires: [
        'Unidata.view.component.search.query.relation.component.RelationGrid'
    ],

    config: {
        metaRecord: null
    },

    pickerPanel: null,

    matchFieldWidth: false,
    //disabled: true,
    editable: true,

    cls: 'un-relation-picker',

    initComponent: function () {
        this.callParent(arguments);

        this.getPicker();

        this.initListeners();
    },

    onCollapseIf: function (e) {
        var picker = this.getPicker();

        if (picker && picker.rendered && e.within(picker.el)) {
            if (e.type !== 'mousewheel') {
                e.preventDefault();
                e.stopPropagation();
            }
        } else {
            this.collapse();
        }
    },

    initListeners: function () {
        this.on('collapseIf', this.onCollapseIf, this);
        this.pickerPanel.on('select', this.onRelationSelect, this);
        this.on('change', this.onChangeValue);
        this.on('hide', this.onPickerHide, this);
    },

    onPickerHide: function () {
        this.setValue(null);
    },

    onChangeValue: function (picker, value) {
        var store = this.pickerPanel.getStore(),
            pickerValue = value;

        if (Ext.isEmpty(value)) {
            store.clearFilter();
        } else {
            store.clearFilter(true);
            store.filterBy(function (storeItem) {
                return storeItem.get('displayName').toLowerCase().indexOf(pickerValue.toLowerCase()) !== -1;
            });
        }
    },

    onRelationSelect: function (tree, relation) {
        this.fireEvent('select', relation);
    },

    /**
     * Обработчик потери фокуса компонентом
     *
     * @param e
     */
    onFocusLeave: function (e) {
        var fromComponent = e.fromComponent,
            isCollapseEnabled = true;

        if (fromComponent instanceof Ext.tree.View) {
            if (fromComponent.grid instanceof Unidata.view.component.AttributeTree) {
                isCollapseEnabled = false;
            }
        } else if (fromComponent instanceof Ext.toolbar.Toolbar) {
            isCollapseEnabled = false;
        }

        if (isCollapseEnabled) {
            this.collapse();
            this.callParent(arguments);
        }
    },

    /**
     * Построить грид отображения связей, сгруппированных по типам
     * @param customCfg
     * @returns Unidata.view.component.search.query.relation.component.RelationGrid
     */
    buildRelationGrid: function (customCfg) {
        var cfg,
            cmp;

        customCfg = customCfg || {};

        cfg = {
            autoRender: true,
            pickerField: this,
            floating: true,
            //hidden: true,
            maxHeight: 400,
            width: 400,
            anchor: '100% 100',
            overflowY: 'auto'
        };

        cfg = Ext.apply(cfg, customCfg);

        cmp = Ext.create('Unidata.view.component.search.query.relation.component.RelationGrid', cfg);

        return cmp;
    },

    createPicker: function () {
        var metaRecord = this.getMetaRecord(),
            cfg;

        cfg = {
            metaRecord: metaRecord
        };

        this.pickerPanel = this.buildRelationGrid(cfg);

        return this.pickerPanel;
    },

    updateMetaRecord: function (metaRecord) {
        var selection;

        this.pickerPanel.setMetaRecord(metaRecord);
        selection = this.pickerPanel.getSelection();

        if (!selection.length) {
            this.setValue(null);
        }
    },

    getSelectedRelation: function () {
        var selection = this.pickerPanel.getSelection(),
            relation = null;

        if (selection.length > 0) {
            relation = selection[0];
        }

        return relation;
    }
});
