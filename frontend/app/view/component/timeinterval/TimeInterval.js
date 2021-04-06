/**
 * Компонент для выбора/отображения периодов актуальности
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.timeinterval.TimeInterval', {
    extend: 'Ext.panel.Panel',

    xtype: 'timeinterval',

    layout: 'vbox',
    title: Unidata.i18n.t('glossary:timeinterval', {count: 2}),
    cls: 'timeintervals-container',
    referenceHolder: true,
    collapsible: true,
    collapsed: true,
    hideCollapseTool: true,
    collapsedSave: true,
    config: {
        undoHidden: false,
        minDate: null,
        maxDate: null,
        readOnly: false
    },
    timeIntervalPickerPanel: null,
    timeIntervalDataView: null,
    updateMinDate: function (value) {
        if (this.timeIntervalPickerPanel && this.timeIntervalDataView) {
            this.timeIntervalPickerPanel.setMinDate(value);
            this.timeIntervalDataView.setMinDate(value);
        }
    },
    updateMaxDate: function (value) {
        if (this.timeIntervalPickerPanel && this.timeIntervalDataView) {
            this.timeIntervalPickerPanel.setMaxDate(value);
            this.timeIntervalDataView.setMaxDate(value);
        }
    },
    header: {
        items: [{
            xtype: 'label',
            reference: 'collapseLabel',
            cls: 'un-timeintervals-collapse-label',
            width: 'auto',
            html: '<a href="javascript:void(0)">' + Unidata.i18n.t('dataviewer>allPeriods') + '</a>',
            listeners: {
                render: function (self) {
                    self.getEl().down('a').on('click', function (e) {
                        var panel = self.findParentByType('panel');

                        e.preventDefault();

                        panel.toggleCollapse();
                    });
                }
            }
        }]
    },

    listeners: {
        beforecollapse: function (self) {
            return self.handleToggleCollapse(true);
        },
        beforeexpand: function (self) {
            return self.handleToggleCollapse(false);
        },
        afterrender: function (self) {
            var undoCreateTimeInterval = self.lookupReference('undoCreateTimeInterval'),
                timeIntervalDataView = this.lookupReference('timeIntervalDataView');

            undoCreateTimeInterval.getEl().down('a').on('click', function (e, component) {
                e.preventDefault();
                timeIntervalDataView.undoTimeIntervalCreate();
                self.fireEvent('undotimeintervalcreate', self, component);
            });
            this.updateHeaderTitle();
        }
    },
    onStoreLoad: function (store) {
        if (store.count()) {
            if (store.count() <= 1) {
                this.collapse();
            }

            this.updateHeaderTitle();
        }
    },
    findItemByProp: function (arr, property, value) {
        return Ext.Array.findBy(arr, function (item) {
            return item[property] === value;
        });
    },
    initComponent: function () {
        var pickerPanelConfig = this.findItemByProp(this.items, 'reference', 'timeIntervalPickerPanel'),
            containerConfig = this.findItemByProp(this.items, 'reference', 'timeIntervalDataViewContainer'),
            dataViewConfig = this.findItemByProp(containerConfig.items, 'reference', 'timeIntervalDataView'),
            me = this,
            validityPeriodCfg = {
                minDate: this.getMinDate(),
                maxDate: this.getMaxDate()
            };

        if (this.config.datePickerConfig) {
            Ext.apply(pickerPanelConfig, validityPeriodCfg);
            Ext.apply(pickerPanelConfig, this.config.datePickerConfig);
        }

        if (this.config.dataViewConfig) {
            Ext.apply(pickerPanelConfig, validityPeriodCfg);
            Ext.apply(dataViewConfig, this.config.dataViewConfig);
        }

        if (!dataViewConfig.listeners) {
            dataViewConfig.listeners = {};
        }

        if (dataViewConfig.listeners) {
            dataViewConfig.listeners.refresh = {
                fn: function () {
                    me.bindHrefListeners();
                }
            };
        }

        dataViewConfig.listeners.selectionchange = {
            fn: function () {
                me.updateHeaderTitle();
            },
            scope: this
            //single: true
        };

        dataViewConfig.listeners.storeload = {
            fn: function (store) {
                me.onStoreLoad(store);
            },
            scope: this
        };
        this.callParent(arguments);
        this.initReferences();
    },
    initReferences: function () {
        this.timeIntervalPickerPanel = this.lookupReference('timeIntervalPickerPanel');
        this.timeIntervalDataView = this.lookupReference('timeIntervalDataView');
    },
    updateHeaderTitle: function (collapsed) {
        var collapseLabel = this.lookupReference('collapseLabel'),
            timeIntervalDataView = this.lookupReference('timeIntervalDataView'),
            selection = timeIntervalDataView.getSelection(),
            html,
            title = '';

        collapsed = collapsed !== undefined ? collapsed : this.collapsed;

        // calc title and html
        if (collapsed) {
            if (selection && selection.length) {
                title = this.buildTitle(selection[0]);
            }
            html = '<a href="javascript:void(0)">' + Unidata.i18n.t('dataviewer>allPeriods') + '</a>';
        } else {
            title = Unidata.i18n.t('glossary:timeinterval', {count: 2});
            html = '<a href="javascript:void(0)">' + Unidata.i18n.t('dataviewer>rollUp') + '</a>';
        }

        if (collapseLabel) {
            if (this.getIsCopyMode()) {
                collapseLabel.hide();
            } else {
                //if (timeIntervalDataView.getStore().count() > 1) {
                collapseLabel.setHtml(html);
                collapseLabel.show();

                if (collapseLabel.getEl()) {
                    collapseLabel.getEl().down('a').on('click', function (e) {
                        var panel = collapseLabel.findParentByType('panel');

                        e.preventDefault();

                        panel.toggleCollapse();
                    });
                }
            }
        }

        this.setTitle(title);
    },
    buildTitle: function (timeInterval) {
        var dateFrom = timeInterval.get('dateFrom'),
            dateTo = timeInterval.get('dateTo'),
            timeIntervalStr,
            minDateSymbolHtml = Unidata.Config.getMinDateSymbolHtml(),
            maxDateSymbolHtml = Unidata.Config.getMaxDateSymbolHtml();

        dateFrom = dateFrom ? Ext.Date.format(dateFrom, Unidata.Config.getDateFormat()) : minDateSymbolHtml;
        dateTo = dateTo ? Ext.Date.format(dateTo, Unidata.Config.getDateFormat()) : maxDateSymbolHtml;
        timeIntervalStr = Ext.String.format('{0} &mdash; {1}', dateFrom, dateTo);

        return timeIntervalStr;
    },
    items: [
        {
            xtype: 'panel',
            layout: 'vbox',
            reference: 'timeIntervalDataViewContainer',
            items: [
                {
                    xtype: 'timeintervaldataview',
                    // не отображаем loadMask, чтобы не было глюков при открытии неск.вкладок
                    loadMask: false,
                    reference: 'timeIntervalDataView',
                    minDate: Unidata.Config.getMinDate(),
                    maxDate: Unidata.Config.getMaxDate()
                }
            ]
        },
        {
            xtype: 'container',
            cls: 'undocreatetimeinterval-container',
            reference: 'undoCreateTimeInterval',
            hidden: true,
            items: [
                {
                    xtype: 'label',
                    html: '<a href="#"><i class="fa fa-undo"></i>' + Unidata.i18n.t('dataviewer>cancelCreateTimeInterval') + '</a>'
                }
            ]
        },
        {
            xtype: 'timeintervalpickerpanel',
            reference: 'timeIntervalPickerPanel',
            hidden: true,
            minDate: Unidata.Config.getMinDate(),
            maxDate: Unidata.Config.getMaxDate()
        }
    ],
    handleToggleCollapse: function (collapsed) {
        this.updateHeaderTitle(collapsed);

        return true;
    },
    bindHrefListeners: function () {
        var timeIntervalDataView = this.lookupReference('timeIntervalDataView'),
            hrefs,
            me = this;

        function onClickHref (e) {
            me.fireEvent('timeintervaldelete', me, e.srcElement);
        }

        hrefs = timeIntervalDataView.getEl().query('i');
        hrefs.forEach(function (href) {
            href.addEventListener('click', onClickHref);
        });
    },
    setIsCopyMode: function (isCopyMode, undoHidden) {
        var timeIntervalDataView = this.lookupReference('timeIntervalDataView'),
            undoCreateTimeInterval = this.lookupReference('undoCreateTimeInterval'),
            timeIntervalPickerPanel = this.lookupReference('timeIntervalPickerPanel');

        if (undoHidden !== undefined) {
            this.setUndoHidden(undoHidden);
        }

        if (!this.undoHidden) {
            undoCreateTimeInterval.setHidden(!isCopyMode);
        }

        if (isCopyMode) {
            this.collapsedSave = this.collapsed;
            this.expand();
        } else {
            if (this.getIsCopyMode() !== isCopyMode) {
                this.getDataView().lastCreatedTimeIntervalDate = this.getCreatedTimeIntervalDate();
            }
            // restore collapsedSave
            if (this.collapsedSave) {
                this.collapse();
            } else {
                this.expand();
            }
        }

        timeIntervalDataView.setIsCopyMode(isCopyMode);
        timeIntervalPickerPanel.setHidden(!isCopyMode);

        this.updateHeaderTitle();
    },
    getIsCopyMode: function () {
        return this.lookupReference('timeIntervalDataView').getIsCopyMode();
    },
    getDataView: function () {
        return this.lookupReference('timeIntervalDataView');
    },
    getDatePickerPanel: function () {
        return this.lookupReference('timeIntervalPickerPanel');
    },
    getValidFrom: function () {
        return this.getDatePickerPanel().getValidFrom();
    },
    getValidTo: function () {
        return this.getDatePickerPanel().getValidTo();
    },
    setValidFrom: function (value) {
        return this.getDatePickerPanel().setValidFrom(value);
    },
    setValidTo: function (value) {
        return this.getDatePickerPanel().setValidTo(value);
    },
    getCreatedTimeInterval: function () {
        return {
            validFrom: this.getValidFrom(),
            validTo: this.getValidTo()
        };
    },
    getCreatedTimeIntervalDate: function () {
        return this.getValidFrom() || this.getValidTo();
    },

    updateReadOnly: function (readOnly) {

        if (this.timeIntervalDataView) {
            this.timeIntervalDataView.setReadOnly(readOnly);
        }

        if (this.timeIntervalPickerPanel) {
            this.timeIntervalPickerPanel.setReadOnly(readOnly);
        }
    }
});
