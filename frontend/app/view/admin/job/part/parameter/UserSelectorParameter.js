/**
 * Компонент для редактирования параметра типа "UserSelectorParameter"
 *
 * @author Alexander Bavin
 * @date 2017-12-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.UserSelectorParameter', {

    extend: 'Unidata.view.admin.job.part.parameter.AbstractParameter',

    alias: 'widget.admin.job.param.user_selector',

    TYPE: 'USER_SELECTOR',

    statics: {
        usersStore: null
    },

    initInput: function (name, value) {

        /** @type {Unidata.model.job.parameter.meta.UserSelectorMetaParameter} */
        var meta = this.getParameter().getMeta(),
            usersStore = this.self.usersStore,
            input;

        if (!usersStore) {
            usersStore = Ext.create('Ext.data.Store', {
                model: 'Unidata.model.user.User',
                autoLoad: true
            });

            this.self.usersStore = usersStore;
        }

        input = Ext.widget({
            xtype: 'tagfield',
            name: name,
            value: value.split('|'),
            msgTarget: 'under',
            queryMode: 'local',
            displayField: 'fullName',
            valueField: 'login',
            store: usersStore
        });

        return input;
    },

    onInputChange: function () {
        this.value = this.input.getValue().join('|');
        this.parameter.setValue(this.value);
    }

});
