Ext.define('Unidata.view.component.NumberfieldExtended', {
    extend: 'Ext.form.field.Number',

    xtype: 'numberfieldextended',
    editable: false,
    cls: 'unidata-textfield-extended',

    triggers: {
        edit: {
            cls: 'unidata-edit-field-trigger',
            handler: function () {
                this.setEditable(true);
                this.removeCls('readonly-textfield');
            }
        }
    },
    listeners: {
        blur: function () {
            this.setEditable(false);
            this.addCls('readonly-textfield');
        },
        beforerender: function () {
            if (this.editable) {
                this.removeCls('readonly-textfield');
            } else {
                this.addCls('readonly-textfield');
            }
        },
        afterrender: function () {
            this.setTriggers(
                {
                    edit: {
                        cls: 'unidata-edit-field-trigger',
                        handler: function () {
                            this.setEditable(true);
                            this.removeCls('readonly-textfield');
                        }
                    }
                }
            );
        }
    }
});
