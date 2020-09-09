/**
 * Взято из 5.1.1
 * Фикс срабатываения эвента deselect.
 */
Ext.define('Unidata.overrides.selection.CheckboxModel', {

    override: 'Ext.selection.CheckboxModel',

    constructor: function () {
        var me = this;

        me.callParent(arguments);

        // If mode is single and showHeaderCheck isn't explicity set to
        // true, hide it.
        if (me.mode === 'SINGLE') {
            //<debug>
            if (me.showHeaderCheckbox) {
                Ext.Error.raise('The header checkbox is not supported for SINGLE mode selection models.');
            }
            //</debug>
            me.showHeaderCheckbox = false;
        }
    },

    onHeaderClick: function (headerCt, header, e) {
        var me = this,
            isChecked;

        if (header === me.column && me.mode !== 'SINGLE') {
            e.stopEvent();
            isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');

            if (isChecked) {
                me.deselectAll();
            } else {
                me.selectAll();
            }
        }
    },

    getHeaderConfig: function () {
        var me = this,
            showCheck = me.showHeaderCheckbox !== false;

        return {
            xtype: 'gridcolumn',
            isCheckerHd: showCheck,
            text: '&#160;',
            clickTargetName: 'el',
            width: me.headerWidth,
            sortable: false,
            draggable: false,
            resizable: false,
            hideable: false,
            menuDisabled: true,
            dataIndex: '',
            tdCls: me.tdCls,
            cls: showCheck ? Ext.baseCSSPrefix + 'column-header-checkbox ' : '',
            defaultRenderer: me.renderer.bind(me),
            editRenderer: me.editRenderer || me.renderEmpty,
            locked: me.hasLockedHeader()
        };
    },

    /**
     * Корректный метод из 5.1.1 для запрета выделения при клике на строку, когда checkOnly == true
     */
    selectByPosition: function (position, keepExisting) {
        if (!position.isCellContext) {
            position = new Ext.grid.CellContext(this.view).setPosition(position.row, position.column);
        }

        // Do not select if checkOnly, and the requested position is not the check column
        if (!this.checkOnly || position.column === this.column) {
            this.callParent([position, keepExisting]);
        }
    },

    privates: {
        onBeforeNavigate: function (metaEvent) {
            var e = metaEvent.keyEvent;

            if (this.selectionMode !== 'SINGLE') {
                metaEvent.ctrlKey = metaEvent.ctrlKey || e.ctrlKey || (e.type === 'click' && !e.shiftKey) || e.getKey() === e.SPACE;
            }
        },

        selectWithEventMulti: function (record, e, isSelected) {
            var me = this;

            if (!e.shiftKey && !e.ctrlKey && e.getTarget(me.checkSelector)) {
                if (isSelected) {
                    me.doDeselect(record);
                } else {
                    me.doSelect(record, true);
                }
            } else {
                me.callParent([record, e, isSelected]);
            }
        }
    }

});
