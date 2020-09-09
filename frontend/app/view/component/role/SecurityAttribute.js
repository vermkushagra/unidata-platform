/**
 * Компонент редактирования метки безопасности для роли
 *
 * @author Aleksandr Bavin
 * @date 2018-02-06
 */
Ext.define('Unidata.view.component.role.SecurityAttribute', {

    extend: 'Unidata.view.component.user.SecurityAttribute',

    initComponent: function () {
        this.callParent(arguments);

        this.securityLabelSample.set('checked', true);
    },

    checkDeleteLimitCount: function () {
        return false;
    },

    initTools: function () {
        this.callParent(arguments);

        this.addTool(this.initCheckbox());
    },

    initCheckbox: function () {
        var checked = false,
            checkbox;

        checkbox = Ext.widget({
            xtype: 'checkbox',
            labelWidth: 80,
            fieldLabel: Unidata.i18n.t('admin.security>applied'),
            value: true,
            listeners: {
                change: 'onCheckboxChange',
                scope: this
            }
        });

        // если хотя бы одна метка отмечена
        this.securityLabels.each(function (securityLabel) {
            if (securityLabel.get('name') === this.labelName && securityLabel.get('checked')) {
                checked = true;

                return false;
            }
        }, this);

        this.onCheckboxChange(checkbox, checked);
        checkbox.setValue(checked);

        return checkbox;
    },

    onCheckboxChange: function (checkbox, checked) {
        this.securityLabels.each(function (securityLabel) {
            if (securityLabel.get('name') === this.labelName) {
                securityLabel.set('checked', checked);
            }
        }, this);

        this.items.each(function (item) {
            item.setHidden(!checked);
        });
    }

});
