Ext.define('Unidata.view.admin.entity.recordshow.RecordshowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.admin.entity.recordshow',

    onTabAdd: function (self, component) {
        self.setActiveTab(component);
    }
});
