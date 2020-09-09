/**
 * Копонент отображающий детальную информацию по текущему датарекорду
 *
 * @author Denis Makarov
 * @date 2018-06-27
 */

Ext.define('Unidata.view.component.dropdown.Detail', {
    extend: 'Ext.Container',

    requires: [
        'Unidata.view.component.dropdown.DetailController',
        'Unidata.view.component.dropdown.DetailModel',
        'Unidata.Config'
    ],

    alias: 'widget.dropdownpickerfield.detail',

    viewModel: {
        type: 'dropdownpickerfield.detail'
    },

    controller: 'dropdownpickerfield.detail',

    cls: 'un-lookup-attribute-details',

    gridHeight: 140,

    referenceHolder: true,

    methodMapper: [
        {
            method: 'displayDataRecordDetail'
        },
        {
            method: 'displayEmptyDataRecordDetail'
        }
    ],

    filterActiveOnly: {
        filterFn: function (rec) {
            var inactive = rec.mapToObject()['$inactive'];

            return !inactive || inactive == 'false';
        }
    },

    config: {
        validFrom: null,
        validTo: null,
        metaRecord: null,
        recordStatus: null,
        etalonId: null,
        showReferencedAttributes: true,
        referencedDisplayAttributes: null
    },

    isShowRemovedIntervals: false,

    displayAttributes: [],

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function () {
        var viewModel = this.getViewModel();

        this.callParent(arguments);

        this.detailsStore = viewModel.getStore('details');
        this.lookupReference('detailsGrid').bindStore(this.detailsStore);

        if (this.asModal) {
            this.buildDetailData();
        }
    },

    buildDetailData: function () {
        var referencedDisplayAttrs = this.getReferencedDisplayAttributes(),
        grid = this.lookupReference('detailsGrid');

        grid.getSelectionModel().setLocked(false);

        this.displayAttributes = Unidata.util.UPathMeta.buildAttributePaths(this.getMetaRecord(), [{
            property: 'displayable',
            value: true
        }]);
        this.loadDetailsStore(this.getEtalonId());

        if (!referencedDisplayAttrs || referencedDisplayAttrs.length == 0) {
            this.setShowReferencedAttributes(false);
            this.lookupReference('show-referenced-attributes-toggle').hide();
        }
    },

    onDestroy: function () {
        this.callParent(arguments);
    },

    initItems: function () {
        var me = this,
            items;

        this.callParent(arguments);
        items = [
            {
                xtype: 'container',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'grid',
                        flex: 1,
                        overflowY: 'auto',
                        height: me.gridHeight,
                        reference: 'detailsGrid',
                        columns: [
                            {
                                flex: 2,
                                hideable: false,
                                sortable: false,
                                text: Unidata.i18n.t('ddpickerfield>detail>column.periodActual'),
                                dataIndex: 'keyValue',
                                renderer: this.renderTimePeriodsColumn.bind(this),
                                tdCls: 'un-validity-period-column'
                            },
                            {
                                flex: 5,
                                text: Unidata.i18n.t('ddpickerfield>detail>column.calculatedDisplayValue'),
                                hideable: false,
                                sortable: false,
                                dataIndex: 'keyValue',
                                renderer: this.renderTitle.bind(this),
                                tdCls: 'un-validity-attribute-display'
                            }
                        ]
                    },
                    {
                        xtype: 'toolbar',
                        ui: 'footer',
                        flex: 1,
                        dock: 'bottom',
                        reference: 'showRemoved',
                        padding: '0 5 0 10',
                        cls: 'un-show-removed-periods',
                        items: [
                            {
                                xtype: 'checkboxfield',
                                name: 'required',
                                labelAlign: 'right',
                                boxLabelAlign: 'after',
                                boxLabel: Unidata.i18n.t('ddpickerfield>detail>showDeletedPeriodActual'),
                                listeners: {
                                    change: function (el, checked) {
                                        var store = this.detailsStore,
                                            grid = this.lookupReference('detailsGrid');

                                        store.clearFilter();

                                        if (!checked) {
                                            store.filter([me.filterActiveOnly]);
                                        }

                                        grid.getView().focusRow(grid.getSelection()[0]);

                                        this.isShowRemovedIntervals = checked;
                                    },
                                    scope: me
                                }
                            },
                            {
                                xtype: 'container',
                                flex: 1
                            },
                            {
                                xtype: 'container',
                                layout: {
                                    type: 'hbox'
                                },
                                reference: 'show-referenced-attributes-toggle',
                                items: [
                                    {
                                        xtype: 'component',
                                        cls: 'un-toggle-label',
                                        html: '<span>' + Unidata.i18n.t('ddpickerfield>detail>displayType') + ': </span>'
                                    },
                                    {
                                        xtype: 'container',
                                        paddingLeft: 4,
                                        defaults: {
                                            xtype: 'button',
                                            ui: 'un-text-toggle-button',
                                            color: 'toggle-text-light-gray',
                                            allowDepress: false,
                                            toggleGroup: 'attributeTypes'
                                        },
                                        cls: 'un-show-referenced-attrubutes-toggle',
                                        items: [
                                            {
                                                text: Unidata.i18n.t('ddpickerfield>detail>displayAttributes.overriden'),
                                                toggleHandler: 'toggleToReferencedDisplayAttributes',
                                                pressed: me.showReferencedAttributes
                                            },
                                            {
                                                text: Unidata.i18n.t('ddpickerfield>detail>displayAttributes.default')
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];

        this.add(items);
    },

    renderTimePeriodsColumn: function (value, metaData, searchHit) {
        var val = searchHit.mapToObject(),
            str,
            template,
            toDate,
            fromDate,
            cls;

        cls = val['$inactive'] === 'true' ? 'un-timeinterval-removed' : '';
        fromDate = Ext.Date.parse(val['$from'], Unidata.Config.getDateTimeFormatServer());
        fromDate = Ext.Date.format(fromDate, Unidata.Config.getDateFormat());

        if (!fromDate) {
            fromDate = Unidata.Config.getMinDateSymbolHtml();
        }

        toDate = Ext.Date.parse(val['$to'], Unidata.Config.getDateTimeFormatServer());
        toDate = Ext.Date.format(toDate, Unidata.Config.getDateFormat());

        if (!toDate) {
            toDate = Unidata.Config.getMaxDateSymbolHtml();
        }

        template = '<span class="un-result-grid-item-data">{0}&nbsp;-&nbsp;{1}</span>';
        str = Ext.String.format(template, fromDate, toDate);

        return Ext.String.format('<div class="{0}">{1}</div>', cls, str);
    },

    renderTitle: function (value, metaData, searchHit) {
        var val = searchHit.mapToObject(),
            str = '',
            attributesList,
            cls;

        cls = val['$inactive'] === 'true' ? 'un-relation-attribute-title un-timeinterval-removed' : 'un-relation-attribute-title';
        attributesList = (this.getShowReferencedAttributes() === true) ? this.referencedDisplayAttributes : this.displayAttributes;

        attributesList.forEach(function (attr) {
            if (attr && val[attr]) {
                str += val[attr] + ' ';
            }
        });

        if (Ext.isEmpty(str)) {
            str = Unidata.i18n.t('ddpickerfield>displayValueIsEmpty');
        }

        return Ext.String.format('<div class="{0}">{1}</div>', cls, Ext.String.htmlEncode(str));
    },

    loadDetailsStore: function (etalonId) {
        var controller = this.getController();

        controller.initExtraParams(etalonId);
        controller.loadStore(etalonId, this.setSelection.bind(this));
    },

    isDateInsideTimeInterval: function (fromDate, toDate, currentDate) {
        if (fromDate >= toDate || !currentDate) {
            return false;
        }

        if (fromDate <= currentDate && (!toDate || toDate == 'Invalid Date')) {
            return true;
        }

        if (toDate >= currentDate && (!fromDate || fromDate == 'Invalid Date')) {
            return true;
        }

        return (fromDate <= currentDate) && (currentDate <= toDate);
    },

    setSelection: function (detailRecords) {
        var selectedRecord = null,
            grid = this.lookupReference('detailsGrid'),
            val,
            fromDate,
            toDate,
            validFrom;

        Ext.Array.each(detailRecords, function (detail) {
            val = detail.mapToObject();
            fromDate = Ext.Date.parse(val['$from'], Unidata.Config.getDateTimeFormatProxy());
            fromDate = Ext.Date.format(fromDate, Unidata.Config.getDateFormat());

            toDate = Ext.Date.parse(val['$to'], Unidata.Config.getDateTimeFormatProxy());
            toDate = Ext.Date.format(toDate, Unidata.Config.getDateFormat());

            if (this.isDateInsideTimeInterval(new Date(val['$from']), new Date(val['$to']), new Date())) {
                selectedRecord = detail;

                return false;
            } else {
                validFrom = Ext.Date.format(this.getValidFrom(), Unidata.Config.getDateTimeFormatProxy());

                if (fromDate === validFrom) {
                    selectedRecord = detail;

                    return false;
                }
            }
        }, this);

        if (selectedRecord) {
            grid.selModel.doSelect(selectedRecord);
            grid.getView().focusRow(selectedRecord);
        }

        grid.getSelectionModel().setLocked(true);
    }
});
