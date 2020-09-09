Ext.define('Unidata.view.component.DisableFieldSet', {
    extend: 'Ext.form.FieldSet',

    xtype: 'disablefieldset',
    disableItems: true,

    privates: {
        onCheckChange: function (checkbox) {
            var checked = checkbox.checked,
                beforeChangeResult;

            beforeChangeResult = this.fireEvent('beforechange', this, checked, this.items);

            if (beforeChangeResult !== false) {
                this.fireEvent('change', this, checked, this.items);

                if (this.disableItems) {
                    this.items.each(function (item) {
                        item.setDisabled(!checked);
                    });
                }
            } else {
                checkbox.setValue(!checked);
            }
        }
    },

    setCheckBoxValue: function (checked) {
        this.checkboxCmp.setValue(checked);
    }
});
