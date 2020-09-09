/**
 * Статистика по ошибкам на каждый день
 *
 * @author Aleksandr Bavin
 * @date 2018-08-23
 */
Ext.define('Unidata.view.component.dashboard.entity.items.DqerrorsDaily', {

    extend: 'Ext.panel.Panel',

    mixins: [
        'Unidata.mixin.Savable'
    ],

    alias: 'widget.component.dashboard.entity.dqerrors.daily',

    viewModel: 'component.dashboard.entity.dqerrors.daily',
    controller: 'component.dashboard.entity.dqerrors.daily',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-card',

    cls: 'un-dashboard-dqerrors-daily',

    referenceHolder: true,

    title: Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily'),

    minWidth: 770,

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            defaults: {
                margin: '0 0 0 10'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'hbox',
                    width: 220,
                    margin: 0,
                    defaults: {
                        xtype: 'datefield',
                        ui: 'un-field-default',
                        editable: false,
                        flex: 1
                    },
                    items: [
                        {
                            reference: 'startDate',
                            emptyText: Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.input.from'),
                            listeners: {
                                change: 'onStartDateChange'
                            }
                        },
                        {
                            reference: 'endDate',
                            emptyText: Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.input.to'),
                            listeners: {
                                change: 'onEndDateChange'
                            }
                        }
                    ]
                },
                {
                    xtype: 'un.entitytagfield',
                    reference: 'entities',
                    ui: 'un-field-default',
                    placeholder: Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.input.entities'),
                    flex: 1,
                    listeners: {
                        change: 'onEntitiesChange'
                    }
                },
                {
                    xtype: 'tagfield',
                    reference: 'severity',
                    ui: 'un-field-default',
                    queryMode: 'local',
                    allQuery: [],
                    displayField: 'text',
                    valueField: 'value',
                    store: {
                        fields: ['text', 'value'],
                        autoLoad: true,
                        data: Unidata.model.dataquality.DqRaise.getSeverityList()
                    },
                    placeholder: Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.input.severity'),
                    flex: 1,
                    listeners: {
                        change: 'onSeverityChange'
                    }
                },
                {
                    xtype: 'button',
                    scale: 'medium',
                    text: Unidata.i18n.t('common:search'),
                    listeners: {
                        click: 'onSearchButtonClick'
                    }
                }
            ]
        },
        {
            xtype: 'container',
            reference: 'tableContainer',
            tpl: [
                '<div class="un-table-wrap">',
                '<table>',
                '<tr>',
                    '<th class="un-col-fixed">',
                        Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.table.entityName'),
                    '</th>',
                    '<th class="un-col-fixed">',
                        Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.table.category'),
                    '</th>',
                    '<th class="un-col-fixed">',
                        Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.table.severity'),
                    '</th>',
                    '<tpl for="dateColumns">',
                        '<th>{.}</th>',
                    '</tpl>',
                    '<th class="un-col-fixed">',
                        Unidata.i18n.t('masonryGrid>widget.dqerrorsDaily.table.total'),
                    '</th>',
                '</tr>',
                '<tpl for="plainData">',
                        '<tpl if="parent.prevEntity !== values.entityName">',
                            '{% parent.prevCategory = null %}',
                    '<tr class="un-dqerrors-entity">',
                            '<td class="un-col-fixed">{entityDisplayName}</td>',
                        '<tpl else>',
                    '<tr>',
                            '<td class="un-col-fixed"></td>',
                        '</tpl>',
                        '<tpl if="parent.prevCategory !== values.category">',
                            '<td class="un-col-fixed">{category}</td>',
                        '<tpl else>',
                            '<td class="un-col-fixed"></td>',
                        '</tpl>',
                        '<td class="un-col-fixed">{severityDisplayName}</td>',
                        '<tpl for="parent.dateColumns">',
                            '<td class="un-dq-count">',
                                '<tpl if="parent.today === values && parent[values] !== 0">',
                                '<a href="#main?section=data|dataSearch?entityName={parent.entityName}&dq.category={parent.category}&dq.severity={parent.severity}">',
                                    '{[parent[values]]}',
                                '</a>',
                                '<tpl else>',
                                '{[parent[values]]}',
                                '</tpl>',
                            '</td>',
                        '</tpl>',
                        '<td class="un-col-fixed un-dq-count">{totalCount}</td>',
                        '{% parent.prevEntity = values.entityName %}',
                        '{% parent.prevCategory = values.category %}',
                    '</tr>',
                '</tpl>',
                '</table>',
                '</div>'
            ]
        }
    ]

});
