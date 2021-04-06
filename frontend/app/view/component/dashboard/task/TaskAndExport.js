/**
 * @author Aleksandr Bavin
 * @date 2017-10-20
 */
Ext.define('Unidata.view.component.dashboard.task.TaskAndExport', {
    extend: 'Ext.container.Container',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.taskandexport',

    requires: [
        'Unidata.view.component.dashboard.task.Task'
    ],

    layout: 'hbox',

    items: [
        {
            flex: 1
        },
        {
            xtype: 'component.dashboard.task',
            padding: '0 10 5 33'
        },
        {
            cls: 'un-v-split',
            width: 1,
            height: 30
        },
        {
            xtype: 'button',
            margin: '0 0 0 10',
            cls: 'un-export-stats-button',
            color: 'transparent',
            scale: 'medium',
            iconCls: 'icon-database-download',
            tooltip: Unidata.i18n.t('dashboard>exportStats.tooltip'),
            handler: function () {
                Ext.Ajax.unidataRequest({
                    url: Unidata.Config.getMainUrl() + 'internal/data/stat/export-stats',
                    method: 'GET'
                });

                Unidata.showMessage(Unidata.i18n.t('dashboard>exportStats.message'));
            }
        }
    ]
});
