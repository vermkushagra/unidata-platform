Ext.define('Unidata.view.admin.sourcesystems.recordshow.RecordshowController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin.sourcesystems.recordshow',

    onTabAdd: function (self, component) {
        self.setActiveTab(component);
    }
});
