/**
 * Поддержка tipTpl из 5.1.1
 * Поддержка placeholder
 *
 * @author Aleksandr Bavin
 * @date 2017-03-03
 */
Ext.define('Unidata.overrides.form.field.Tag', {
    override: 'Ext.form.field.Tag',

    placeholder: null,

    tagItemCls: Ext.baseCSSPrefix + 'tagfield-item',
    tagItemTextCls: Ext.baseCSSPrefix + 'tagfield-item-text',
    tagItemCloseCls: Ext.baseCSSPrefix + 'tagfield-item-close',

    /**
     * @cfg {String/Ext.XTemplate} tipTpl
     * The {@link Ext.XTemplate XTemplate} to use for the tip of the labeled items.
     *
     * @since  5.1.1
     */
    tipTpl: undefined,

    /**
     * Build the markup for the labeled items. Template must be built on demand due to ComboBox initComponent
     * life cycle for the creation of on-demand stores (to account for automatic valueField/displayField setting)
     * @private
     */
    getMultiSelectItemMarkup: function () {
        var me = this,
            cssPrefix = Ext.baseCSSPrefix,
            valueField = me.valueField;

        if (!me.multiSelectItemTpl) {
            if (!me.labelTpl) {
                me.labelTpl = '{' + me.displayField + '}';
            }
            me.labelTpl = me.getTpl('labelTpl');

            if (me.tipTpl) {
                me.tipTpl = me.getTpl('tipTpl');
            }

            me.multiSelectItemTpl = new Ext.XTemplate([
                '<tpl for=".">',
                '<li data-selectionIndex="{[xindex - 1]}" data-recordId="{internalId}" class="{data.tagCls} ',
                me.tagItemCls,
                '<tpl if="this.isSelected(values)">',
                ' ' + me.tagSelectedCls,
                '</tpl>',
                '{%',
                'values = values.data;',
                '%}',
                me.tipTpl ? '" data-qtip="{[this.getTip(values)]}">' : '">',
                '<div class="' + me.tagItemTextCls + '">{[this.getItemLabel(values)]}</div>',
                '<div class="' + me.tagItemCloseCls + '"></div>' ,
                '</li>' ,
                '</tpl>',
                {
                    isSelected: function (rec) {
                        return me.selectionModel.isSelected(rec);
                    },
                    getItemLabel: function (values) {
                        return Ext.String.htmlEncode(me.labelTpl.apply(values));
                    },
                    getTip: function (values) {
                        return Ext.String.htmlEncode(me.tipTpl.apply(values));
                    },
                    strict: true
                }
            ]);
        }

        if (!me.multiSelectItemTpl.isTemplate) {
            me.multiSelectItemTpl = this.getTpl('multiSelectItemTpl');
        }

        return me.multiSelectItemTpl.apply(me.valueCollection.getRange());
    },

    initComponent: function () {
        this.callParent(arguments);
        this.initPlaceholder();
    },

    initPlaceholder: function () {
        if (!this.placeholder) {
            return;
        }

        this.on('afterrender', this.updatePlaceholder, this);
        this.on('change', this.updatePlaceholder, this);
    },

    updatePlaceholder: function () {
        var values = this.getValueRecords();

        this.inputEl.set({'placeholder': values.length ? '' : this.placeholder});
    }

});
