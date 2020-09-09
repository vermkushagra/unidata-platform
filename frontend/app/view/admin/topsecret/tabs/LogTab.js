/**
 * Вкладка логов для сверх секретной области
 *
 * @author Ivan Marshalkin
 * @date 2017-02-10
 */

Ext.define('Unidata.view.admin.topsecret.tabs.LogTab', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.admin.systemtoolspanel.logtab',

    bodyPadding: 10,

    items: [
        {
            xtype: 'button',
            text: Unidata.i18n.t('admin.topsecrets>loadLogs'),
            handler: function () {
                var url,
                    downloadCfg;

                url = Unidata.Api.getSystemLogUrl();

                downloadCfg = {
                    method: 'GET',
                    url: url,
                    params: {
                        token: Unidata.Config.getToken()
                    }
                };

                Unidata.util.DownloadFile.downloadFile(downloadCfg);
            }
        }
    ]
});
