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
        readOnly: false,
        showRemovedTimeIntervals: false,
        dataRecord: null,
        originalValidFrom: null,
        originalValidTo: null
    },
    showRemovedPanel: true,
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
                var dataRecord = self.getDataRecord();

                e.preventDefault();
                timeIntervalDataView.undoTimeIntervalCreate();
                self.fireEvent('undotimeintervalcreate', self, component);

                dataRecord.set('validFrom', self.getOriginalValidFrom(), {commit: true});
                dataRecord.set('validTo', self.getOriginalValidTo(), {commit: true});
            });
            this.updateHeaderTitle();
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        // привязываем контекст
        this.fireTimeIntervalDelete = this.fireTimeIntervalDelete.bind(this);
        this.fireTimeIntervalRestore = this.fireTimeIntervalRestore.bind(this);
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

    initReferences: function () {
        this.timeIntervalPickerPanel = this.lookupReference('timeIntervalPickerPanel');
        this.timeIntervalDataView = this.lookupReference('timeIntervalDataView');
        this.showRemovedTimeIntervalsPanel = this.lookupReference('showRemovedTimeIntervalsPanel');
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

    initItems: function () {
        var items, timeIntervalDataViewConfig;

        this.callParent(arguments);

        timeIntervalDataViewConfig = this.buildTimeIntervalDataViewConfig();

        items = [
            {
                xtype: 'panel',
                layout: 'vbox',
                reference: 'timeIntervalDataViewContainer',
                items: [
                    timeIntervalDataViewConfig
                ]
            },
            {
                xtype: 'panel',
                layout: 'vbox',
                cls: 'time-interval-show-removed',
                reference: 'showRemovedTimeIntervalsPanel',
                items: [
                    {
                        xtype: 'checkboxfield',
                        name: 'required',
                        labelAlign: 'right',
                        boxLabelAlign: 'after',
                        boxLabel: Unidata.i18n.t('dataviewer>showRemovedTimeInterval'),
                        reference: 'showRemovedTimeIntervals',
                        listeners: {
                            change: function (el, checked) {
                                var store = this.lookupReference('timeIntervalDataView').getStore();

                                this.setShowRemovedTimeIntervals(checked);

                                store.clearFilter();

                                if (!checked) {
                                    store.getFilters().add(Unidata.util.api.TimeInterval.getFilter());
                                }
                            },
                            afterRender: function () {
                                if (!this.showRemovedPanel) {
                                    this.showRemovedTimeIntervalsPanel.hide();
                                }
                            },
                            scope: this
                        }
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
                bind: {
                    //validFrom: '{currentRecord.validFrom}',
                    //validTo: '{currentRecord.validTo}'
                },
                minDate: Unidata.Config.getMinDate(),
                maxDate: Unidata.Config.getMaxDate()
            }
        ];

        this.add(items);

        this.initReferences();
        this.initComponentEvent();
    },

    initComponentEvent: function () {
        this.timeIntervalPickerPanel.on('validfromchange', this.onTimeIntervalValidFromChange, this);
        this.timeIntervalPickerPanel.on('validtochange', this.onTimeIntervalValidToChange, this);
    },

    onTimeIntervalValidFromChange: function (value) {
        var dataRecord = this.getDataRecord();

        dataRecord.set('validFrom', value);
    },

    onTimeIntervalValidToChange: function (value) {
        var dataRecord = this.getDataRecord();

        dataRecord.set('validTo', value);
    },

    buildTimeIntervalDataViewConfig: function () {
        var pickerPanelConfig = this.lookupReference('timeIntervalPickerPanel'),
            me = this,
            dataViewConfig,
            validityPeriodCfg = {
                minDate: this.getMinDate(),
                maxDate: this.getMaxDate()
            };

        dataViewConfig = {
            xtype: 'timeintervaldataview',
            // не отображаем loadMask, чтобы не было глюков при открытии неск.вкладок
            loadMask: false,
            reference: 'timeIntervalDataView',
            minDate: Unidata.Config.getMinDate(),
            maxDate: Unidata.Config.getMaxDate()
        };

        if (this.config.dataViewConfig) {
            Ext.apply(pickerPanelConfig, validityPeriodCfg);
            Ext.apply(dataViewConfig, this.config.dataViewConfig);
        }

        if (!dataViewConfig.listeners) {
            dataViewConfig.listeners = {};
        }

        dataViewConfig.listeners.refresh = {
            fn: function () {
                me.bindHrefListeners();
            }
        };
        dataViewConfig.listeners.selectionchange = {
            fn: function () {
                me.updateHeaderTitle();
                me.bindHrefListeners();
            },
            scope: this
        };

        dataViewConfig.listeners.storeload = {
            fn: function (store) {
                me.onStoreLoad(store);
            },
            scope: this
        };

        return dataViewConfig;

    },

    handleToggleCollapse: function (collapsed) {
        this.updateHeaderTitle(collapsed);

        return true;
    },
    bindHrefListeners: function () {
        var timeIntervalDataView = this.lookupReference('timeIntervalDataView'),
            intervalDeleteHrefs,
            intervalRestoreHrefs;

        intervalDeleteHrefs = timeIntervalDataView.getEl().query('.timeinterval-operation-delete');
        intervalRestoreHrefs = timeIntervalDataView.getEl().query('.timeinterval-operation-restore');

        intervalDeleteHrefs.forEach(function (href) {
            this.clearHrefListeners(href);
            href.addEventListener('click', this.fireTimeIntervalDelete);
        }.bind(this));

        intervalRestoreHrefs.forEach(function (href) {
            this.clearHrefListeners(href);
            href.addEventListener('click', this.fireTimeIntervalRestore);
        }.bind(this));
    },

    resetDataRecordValidData: function () {
        var dataRecord = this.getDataRecord();

        dataRecord.set('validFrom', null, {commit: true});
        dataRecord.set('validTo', null, {commit: true});
    },

    /**
     * Очистка всех обработчиков для href
     *
     * @param href
     */
    clearHrefListeners: function (href) {
        href.removeEventListener('click', this.fireTimeIntervalRestore);
        href.removeEventListener('click', this.fireTimeIntervalDelete);
    },

    fireTimeIntervalEvent: function (name, e) {
        this.fireEvent(name, this, e.srcElement);
    },

    fireTimeIntervalDelete: function (e) {
        this.fireTimeIntervalEvent('timeintervaldelete', e);
    },

    fireTimeIntervalRestore: function (e) {
        this.fireTimeIntervalEvent('timeintervalrestore', e);
    },

    updateDataRecord: function (dataRecord) {
        this.setOriginalValidFrom(dataRecord.get('validFrom'));
        this.setOriginalValidTo(dataRecord.get('validTo'));
    },
    setIsCopyMode: function (isCopyMode, undoHidden) {
        var timeIntervalDataView = this.lookupReference('timeIntervalDataView'),
            undoCreateTimeInterval = this.lookupReference('undoCreateTimeInterval'),
            timeIntervalPickerPanel = this.lookupReference('timeIntervalPickerPanel'),
            showRemovedTimeIntervalsPanel = this.lookupReference('showRemovedTimeIntervalsPanel');

        if (undoHidden !== undefined) {
            this.setUndoHidden(undoHidden);
        }

        if (!this.undoHidden) {
            undoCreateTimeInterval.setHidden(!isCopyMode);
        }

        if (isCopyMode) {
            this.collapsedSave = this.collapsed;
            this.expand();

            if (this.showRemovedPanel) {
                showRemovedTimeIntervalsPanel.hide();
            }

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

            if (this.showRemovedPanel) {
                showRemovedTimeIntervalsPanel.show();
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
