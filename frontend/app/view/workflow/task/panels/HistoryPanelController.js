/**
 * @author Aleksandr Bavin
 * @date 2016-08-25
 */
Ext.define('Unidata.view.workflow.task.panels.HistoryPanelController', {
    extend: 'Unidata.view.workflow.task.panels.AbstractPanelController',
    alias: 'controller.history',

    showProcessMap: function (button) {
        var viewModel = this.getViewModel(),
            task = viewModel.get('task'),
            src;

        src = Ext.String.format(
            '{0}internal/data/workflow/diagram/{1}?token={2}&finished={3}',
            Unidata.Config.getMainUrl(),
            task.get('processId'),
            Unidata.Config.getToken(),
            task.get('processFinished')
        );

        Ext.create('Ext.window.Window', {
            title: viewModel.get('addItemText'),
            qaId: 'process-map-window', // для QA отдела. Используются в автотестах
            draggable: false,
            resizable: false,
            modal: true,
            minWidth: 300,
            minHeight: 200,
            maxHeight: window.innerHeight - 50,
            maxWidth: window.innerWidth - 50,
            loading: true,
            layout: 'fit',
            animateTarget: button,
            alwaysCentered: true,
            listeners: {
                show: function () {
                    var wnd = this,
                        imageContainer;

                    imageContainer = wnd.add({
                        xtype: 'container',
                        flex: 1,
                        scrollable: true,
                        layout: {
                            type: 'hbox',
                            align: 'stretchmax'
                        },
                        items: {
                            xtype: 'image',
                            src: src,
                            listeners: {
                                load: {
                                    element: 'el',
                                    fn: function () {
                                        wnd.updateLayout();

                                        wnd.center();
                                        imageContainer.setLoading(false);
                                    }
                                }
                            }
                        }
                    });

                    imageContainer.setLoading(true);
                }
            }
        }).show();

    }

});
