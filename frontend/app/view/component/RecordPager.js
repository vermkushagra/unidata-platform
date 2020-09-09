/**
 * Простоей Pager
 *
 * @author Sergey Shishigin
 * 2015-09-08
 */

Ext.define('Unidata.view.component.RecordPager', {
    extend: 'Ext.container.Container',

    xtype: 'un.recordpager',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    config: {
        start: 0,
        count: 5,
        total: 0,
        showTotal: true
    },

    referenceHolder: true,

    buttonPrev: null,
    buttonNext: null,
    totalLabel: null,

    cls: 'un-recordpager',

    totalTextTpl: Unidata.i18n.t('common:displayCounter'),

    initComponent: function () {
        this.callParent(arguments);
        this.initReferences();
        this.updateLabels();
    },

    initReferences: function () {
        this.buttonPrev = this.lookupReference('buttonPrev');
        this.buttonNext = this.lookupReference('buttonNext');
        this.totalLabel = this.lookupReference('totalLabel');
    },

    initItems: function () {
        var items,
            showTotal,
            totalLabelCfg,
            total,
            totalText;

        this.callParent(arguments);

        showTotal = this.getShowTotal();
        total = this.getTotal();

        items = [
            {
                xtype: 'un.hreflabel',
                reference: 'buttonPrev',
                cls: 'un-recordpager-prev',
                handlerParams: {
                    handler: this.movePrev,
                    scope: this
                }
            },
            {
                xtype: 'un.hreflabel',
                reference: 'buttonNext',
                cls: 'un-recordpager-next',
                handlerParams: {
                    handler: this.moveNext,
                    scope: this
                }
            }
        ];

        if (showTotal) {
            totalText = Ext.String.format(this.totalTextTpl, total);
            totalLabelCfg = {
                xtype: 'label',
                reference: 'totalLabel',
                cls: 'un-recordpager-total',
                text: totalText
            };

            Ext.Array.insert(items, 1, [totalLabelCfg]);
        }

        this.add(items);
    },

    movePrev: function () {
        this.rotate(this.getCount() * (-1));
    },

    moveNext: function () {
        this.rotate(this.getCount());
    },

    /**
     * Прокрутить карусель на step элементов
     *
     * @param step Кол-во элементов для прокрутки (направление задается матем.знаком)
     */
    rotate: function (step) {
        var start = this.getStart(),
            total = this.getTotal();

        start += step;

        if (start < total) {
            this.setStart(start);
            this.fireEvent('rotate', this);
        }
    },

    updateLabels: function () {
        var titleTpl,
            title = {},
            recordsCount = {},
            start = this.getStart(),
            count = this.getCount(),
            total = this.getTotal(),
            from,
            to,
            totalText;

        titleTpl = {
            prev: Unidata.i18n.t('common:prevCounter'),
            next: Unidata.i18n.t('common:nextCounter')
        };

        recordsCount.prev = start + 1 > count ? count : 0;

        if (start + count >= total) {
            recordsCount.next = 0;
        } else if (start + 2 * count > total) {
            recordsCount.next  = total - (start + count);
        } else {
            recordsCount.next = count;
        }

        title.prev = Ext.String.format(titleTpl.prev, recordsCount.prev);
        title.next = Ext.String.format(titleTpl.next, recordsCount.next);

        this.buttonPrev.setTitle(title.prev);
        this.buttonNext.setTitle(title.next);
        this.buttonPrev.setDisabled(recordsCount.prev === 0);
        this.buttonNext.setDisabled(recordsCount.next === 0);

        from = start + 1;
        to = start + count;
        to = to > total ? total : to;
        totalText = Ext.String.format(this.totalTextTpl, from, to, total);
        this.totalLabel.setText(totalText);
    },

    items: [],

    setStart: function (start) {
        var total = this.getTotal();

        if (start < 0) {
            start = 0;
        }

        if (start >= total) {
            start = total - 1;
        }

        this.callParent([start]);
    },

    updateStart: function () {
        if (this.rendered) {
            this.updateLabels();
        }
    },

    updateCount: function () {
        if (this.rendered) {
            this.updateLabels();
        }
    },

    updateTotal: function () {
        if (this.rendered) {
            this.updateLabels();
        }
    }
});
