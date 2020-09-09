/**
 * Компонент для построения таблицы временных интервалов
 * с возможностью активации режима копирования (isCopyMode)
 * Для использования необходимо указать store и обработчик события select.
 *
 * Пример использования:
 * {
 *      xtype: 'timeintervaldataview',
 *      reference: 'timeIntervalDataView',
 *      bind: {
 *          store: '{timeIntervalStore}'
 *      },
 *      listeners: {
 *          select: 'onTimeIntervalDataViewSelect'
 *      }
 * }
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.timeinterval.TimeIntervalDataView', {
    extend: 'Ext.view.View',

    xtype: 'timeintervaldataview',

    config: {
        isCopyMode: false,
        autoSelectTimeInterval: true,
        minDate: null,
        maxDate: null
    },
    publishes: ['isCopyMode'],
    overItemCls: 'timeinterval-over',
    selectionModel: {
        allowDeselect: false,
        mode: 'SINGLE'
    },
    cls: 'time-interval-dataview',
    listeners: {
        selectionchange: function (component) {
            this.onSelectionChange(component);
        },
        beforecontainerclick: function () {
            // запрещаем событие, т.к. из-за него сбрасывалось выделение строки
            return false;
        },
        render: function () {
            if (this.getAutoSelectTimeInterval() && !this.getSelection().length) {
                this.findAndSelectTimeInterval();
            }
        }
    },
    itemSelector: 'div.item-wrap',
    scrollable: 'vertical',
    maxHeight: 190,
    readOnly: false,
    lastSelectedDate: null,
    lastCreatedTimeIntervalDate: null,
    timeIntervalSelectedDate: null,
    templates: {
        'timeIntervalListTpl':
        '<tpl for=".">' +
            '<div class="item-wrap {[xindex % 2 === 0 ? "even" : "odd"]} {[values.active ? "timeinterval-exist": "timeinterval-deleted"]}' +
            '       {[this.getClsDeletedInterval(values.active, values.contributors)]}">' +

                '<tpl if="this.isInfInterval(dateFrom, dateTo)">' +
                    '<span class="timeinterval-from">' + Unidata.Config.getMinDateSymbolHtml() + '</span>' +
                    '<span class="timeinterval-separator" >&mdash;</span>' +
                    '<span class="timeinterval-to">' + Unidata.Config.getMaxDateSymbolHtml() + '</span>' +
                '<tpl elseif="this.isNegInfInterval(dateFrom, dateTo)">' +
                    '<span class="timeinterval-from">' + Unidata.Config.getMinDateSymbolHtml() + '</span>' +
                    '<span class="timeinterval-separator" >&mdash;</span>' +
                    '<span class="timeinterval-to">{dateTo:date(this.dateFormat)}</span>' +
                '<tpl elseif="this.isPosInfInterval(dateFrom, dateTo)">' +
                    '<span class="timeinterval-from">{dateFrom:date(this.dateFormat)}</span>' +
                    '<span class="timeinterval-separator">&mdash;</span>' +
                    '<span class="timeinterval-to">' + Unidata.Config.getMaxDateSymbolHtml() + '</span>' +
                '<tpl else>' +
                    '<span class="timeinterval-from">{dateFrom:date(this.dateFormat)}</span>' +
                    '<span class="timeinterval-separator">&mdash;</span>' +
                    '<span class="timeinterval-to">{dateTo:date(this.dateFormat)}</span>' +
                '</tpl>' +
                '<tpl if="!this.readOnly">' +
                    '<tpl if="!values.active">' +
                        '<div data-qtip="' + Unidata.i18n.t('dataviewer>restoreTimeInterval') + '" class="timeinterval-operation">' +
                            '<i class="fa fa-undo timeinterval-operation-restore"></i>' +
                        '</div>' +
                    '<tpl else>' +
                        '<div data-qtip="' + Unidata.i18n.t('dataviewer>removeTimeInterval') + '" class="timeinterval-operation">' +
                            '<i class="fa fa-times timeinterval-operation-delete"></i>' +
                        '</div>' +
                    '</tpl>' +
                '</tpl>' +
                '<div class="timeinterval-accept-indicator-wrap">' +
                    '<tpl if="this.isVisibleAcceptIndicator(contributors)">' +
                        '<span class="timeinterval-accept-indicator" title="' + Unidata.i18n.t('dataviewer>approveNeeded') + '"></span>' +
                    '</tpl>' +
                '</div>' +
            '</div>' +
        '</tpl>'
    },

    setReadOnly: function (value) {
        this.readOnly = value;
        this.tpl.readOnly = this.readOnly;
        this.refresh();
    },

    /**
     * Generate template
     * @returns {Ext.XTemplate}
     */
    getTimeIntervalListTpl: function (readOnly) {
        var template;

        readOnly = readOnly || false;

        template =  new Ext.XTemplate(
            this.templates['timeIntervalListTpl'],
            {
                dateFormat: Unidata.Config.getDateFormat(),
                readOnly: readOnly,
                isVisibleAcceptIndicator: function (contributors) {
                    var visible;

                    visible = Ext.Array.some(contributors, function (contributor) {
                        return contributor.approval === 'PENDING';
                    });

                    return visible;
                },
                getClsDeletedInterval: function (active, contributors) {
                    var interval4Approve = this.isInterval4Approve(active, contributors);

                    return interval4Approve ? 'timeinterval-inactive' : 'timeinterval-active';
                },
                isInterval4Approve: function (active, contributors) {
                    var pendingExist;

                    pendingExist = Ext.Array.some(contributors, function (contributor) {
                        return contributor.status === 'INACTIVE' && contributor.approval === 'PENDING';
                    });

                    return (!active && pendingExist) ? true : false;
                },
                isInfInterval: function (dateFrom, dateTo) {
                    return dateFrom === null && dateTo === null;
                },
                isNegInfInterval: function (dateFrom, dateTo) {
                    return dateFrom === null && dateTo !== null;
                },
                isPosInfInterval: function (dateFrom, dateTo) {
                    return dateFrom !== null && dateTo === null;
                },
                convertDate: function (dt) {
                    dt = Ext.Date.parse(dt, 'c');

                    return Ext.Date.format(dt, this.dateFormat);
                }
            }
        );

        return template;
    },
    initComponent: function () {
        this.tpl = this.getTimeIntervalListTpl();
        this.tpl.readOnly = this.readOnly;
        this.callParent();
        this.on('select', function () {
                this.lastSelectedDate = this.getSelectedTimeIntervalDate();
                this.lastCreatedTimeIntervalDate = null;
            },
            this);
    },
    setIsCopyMode: function (isCopyMode) {
        var store = this.getStore();

        //this.config.isCopyMode = isCopyMode;
        if (isCopyMode) {
            this.suspendEvents();

            if (store && store.isStore && store.count()) {
                this.disable();
            } else {
                this.setHidden(true);
            }
            this.setSelection(null);
            this.updateLayout();
        } else {
            this.resumeEvents();

            if (store && store.isStore && store.count()) {
                this.setHidden(false);
                this.enable();
            } else {
                this.setHidden(false);
            }
            this.updateLayout();
        }
        this.callParent(arguments);
    },
    undoTimeIntervalCreate: function () {
        this.findAndSelectTimeInterval();
    },
    getIsCopyMode: function () {
        return this.isCopyMode;
    },
    scrollToSelected: function () {
        var domElement = new Ext.dom.Element({
            dom: this.getSelectedNodes()[0]
        });

        domElement.scrollIntoView(this.getEl(), null, true);
    },
    onSelectionChange: function () {
        this.scrollToSelected();
    },

    setStore: function () {
        var store;

        this.callParent(arguments);
        store = this.getStore();

        if (store && store !== 'ext-empty-store') {
            store.on('load', this.onStoreLoad, this);
        }
    },

    onStoreLoad: function (store) {
        if (this.getAutoSelectTimeInterval() &&
            store.count() &&
            !this.getSelection().length) {
            this.findAndSelectTimeInterval();
        }

        this.fireEvent('storeload', store);
    },

    /**
     * Select time interval for a current datetime (now)
     *
     * silent - если переменная равна true, то не вызывать событие onSelect
     * @returns {*}
     */
    findAndSelectTimeInterval: function (timeIntervalDate, silent) {
        var currentTimeInterval,
            store = this.getStore(),
            silent = silent || false;

        if (!timeIntervalDate) {
            if (this.timeIntervalSelectedDate) {
                timeIntervalDate = this.timeIntervalSelectedDate;
                this.timeIntervalSelectedDate = null;
            } else if (this.lastCreatedTimeIntervalDate) {
                timeIntervalDate = this.lastCreatedTimeIntervalDate;
            } else if (this.lastSelectedDate) {
                timeIntervalDate = this.lastSelectedDate;
            } else {
                timeIntervalDate = new Date();
            }
        }

        //TODO: implement find nearest time interval (this._findNearestTimeInterval(store))
        if (store.count() > 0) {
            currentTimeInterval = Unidata.view.component.timeinterval.TimeIntervalDataView.findTimeInterval(store, timeIntervalDate);

            if (currentTimeInterval) {
                if (silent) {
                    this.suspendEvent('select');
                    this.suspendEvent('beforeselect');
                }

                this.setSelection(currentTimeInterval);

                if (silent) {
                    this.resumeEvent('select');
                    this.resumeEvent('beforeselect');
                }
            }
        }

        return currentTimeInterval;
    },

    /**
     * Get selected time interval date
     * @returns {*}
     * @private
     */
    getSelectedTimeIntervalDate: function () {
        var timeIntervals, timeIntervalDate;

        timeIntervals = this.getSelection();

        if (timeIntervals !== null && timeIntervals.length === 1) {
            timeIntervalDate = Unidata.view.component.timeinterval.TimeIntervalDataView.getTimeIntervalDate(timeIntervals[0]);
        } else {
            timeIntervalDate = null;
        }

        return timeIntervalDate;
    },

    isMaxDate: function (date) {
        return Ext.Date.isEqual(date, this.getMaxDate());
    },

    /**
     * Calc a convenient values
     * @returns {{validFrom: *, validTo: null}}
     */
    calcDatePickersDefaults: function (currentTI) {
        var selectedTI = this.getSelection()[0],
            dateFrom = null,
            dateTo = null;

        if (currentTI === selectedTI) {
            // if selected time interval is current, then date from for new interval is tomorrow
            dateFrom = new Date();
            dateFrom = !this.isMaxDate(dateFrom) ? Ext.Date.add(dateFrom, Ext.Date.DAY, 1) : dateFrom;
        } else {
            // else, then date from for new interval is next day after dateTo
            dateFrom = selectedTI.get('dateTo');
            dateFrom = !this.isMaxDate(dateFrom) ? dateFrom : null;
            dateFrom = dateFrom ? Ext.Date.add(dateFrom, Ext.Date.DAY, 1) : dateFrom;
        }

        return {validFrom: dateFrom, validTo: dateTo};
    },

    statics: {
        /**
         * Find time interval for a current datetime (now)
         * @returns {*}
         */
        findTimeInterval: function (store, dt) {
            var index;

            dt = dt || new Date();

            index = store.findBy(function (r) {
                var result;

                if (r.get('dateFrom') === null && r.get('dateTo') === null) {
                    result = true;
                } else if (r.get('dateFrom') === null) {
                    result = dt < r.get('dateTo');
                } else if (r.get('dateTo') === null) {
                    result = dt >= r.get('dateFrom');
                } else {
                    result = dt >= r.get('dateFrom') && dt < r.get('dateTo');
                }

                return result;
            });

            return index > -1 ? store.getAt(index) : store.first();
        },

        /**
         * Получить опорную дату для временного интервала
         * В качестве опорной выбирается дата на левой границе интервала, если она не null.
         * Иначе, выбирается дата на правой границе интервала, если она не null.
         * Если обе даты null, то возвращается null.
         * @param timeInterval
         * @returns {*}
         */
        getTimeIntervalDate: function (timeInterval) {
            var timeIntervalDate;

            timeIntervalDate = timeInterval.get('dateFrom');

            if (timeIntervalDate === null) {
                timeIntervalDate = timeInterval.get('dateTo');
            }

            return timeIntervalDate;
        }
    }
});
