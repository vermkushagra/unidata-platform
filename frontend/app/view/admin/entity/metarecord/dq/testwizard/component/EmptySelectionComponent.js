/**
 * Компонент с текстом "выберите запись для предпросмотра"
 *
 * @author Ivan Marshalkin
 * @date 2018-03-07
 */

Ext.define('Unidata.view.admin.entity.metarecord.dq.testwizard.component.EmptySelectionComponent', {
    extend: 'Ext.Component',

    xtype: 'un.testwizard.emptyselection',

    cls: 'un-testwizard-emptyselection',

    html:   '<div class="wrapper"><div class="inner-container">' +
                '<div role="border">' +
                    Unidata.util.Icon.getLinearIcon('file-search') +
                        '<span>' +
                            Unidata.i18n.t('admin.dqtest>plzSelectRecord') +
                        '</span>' +
                    '</div>' +
                '</div>' +
            '</div>'
});
