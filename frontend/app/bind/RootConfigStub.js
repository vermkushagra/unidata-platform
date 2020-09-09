/**
 * Для биндинга конфига
 * @see {Unidata.mixin.ConfigBind}
 *
 * @author Aleksandr Bavin
 * @date 2018-02-15
 */
Ext.define('Unidata.bind.RootConfigStub', {

    extend: 'Ext.app.bind.RootStub',

    constructor: function () {
        // рутовый owner, обычно, это viewModel
        var owner = {
            data: {},
            hadValue: {},
            scheduler: new Ext.util.Scheduler(),
            getParent: function () {
                return null;
            },
            getScheduler: function () {
                return this.scheduler;
            },
            getData: function () {
                return this.data;
            },
            onBindDestroy: function () {
                // TODO: implement
            }
        };

        this.callParent([owner, 'root']);
    }

});
