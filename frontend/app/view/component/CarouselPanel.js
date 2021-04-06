/**
 * Панель построения карусели
 *
 * @author Sergey Shishigin
 */
Ext.define('Unidata.view.component.CarouselPanel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'Ext.util.MixedCollection'
    ],

    alias: 'widget.carouselpanel',

    mixins: [
        'Unidata.mixin.NoDataDisplayable'
    ],

    itemsDisplayed: 2,  // кол-во элементов, отображаемых на экране
    itemsRotated: 1,    // кол-во элементов, на которое сдвигается карусель за один шаг
    firstItemIndex: 0,
    buttonPrev: null,
    buttonNext: null,

    carouselItemsCollection: null,

    referenceHolder: true,
    cls: 'un-carousel-panel',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    noDataText: Unidata.i18n.t('dataentity>noRecords'),

    items: [
        {
            xtype: 'container',
            layout: 'fit',
            maxWidth: 60,
            items: [
                {
                    xtype: 'button',
                    reference: 'buttonPrev',
                    glyph: 'xf053@FontAwesome',
                    disabled: true,
                    cls: 'un-carousel-button-prev'
                }
            ]
        },
        {
            xtype: 'container',
            reference: 'carouselContainer',
            layout: {
                type: 'hbox',
                align: 'begin'
            },
            flex: 1,
            scrollable: 'vertical',
            cls: 'un-carousel-items-container'
        },
        {
            xtype: 'container',
            layout: 'fit',
            maxWidth: 60,
            items: [
                {
                    xtype: 'button',
                    reference: 'buttonNext',
                    glyph: 'xf054@FontAwesome',
                    cls: 'un-carousel-button-next'
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent();

        this.carouselItemsCollection = this.createCarouselItemCollection();

        this.buttonPrev = this.lookupReference('buttonPrev');
        this.buttonNext = this.lookupReference('buttonNext');
        this.carouselContainer = this.lookupReference('carouselContainer');
        this.buttonPrev.on('click', this.onButtonPrevClick, this);
        this.buttonNext.on('click', this.onButtonNextClick, this);
        this.carouselContainer.on('resize', this.onAfterLayout, this);
        this.updateCarouselItems();
    },

    onAfterLayout: function () {
        var carouselContainer = this.carouselContainer,
            itemsDisplayed = this.itemsDisplayed,
            items = carouselContainer.items,
            maxWidth = null;

        if (items.length === 0) {
            return;
        }

        if (items.length < itemsDisplayed) {
            maxWidth = carouselContainer.getWidth() / itemsDisplayed;
        }

        items.each(function (item) {
            item.setMaxWidth(maxWidth);
        });
    },

    onButtonPrevClick: function () {
        this.movePrev();
    },

    onButtonNextClick: function () {
        this.moveNext();
    },

    /**
     * Прокрутить карусель на itemsRotated элементов
     *
     * @param itemsRotated Кол-во элементов для прокрутки (направление задается матем.знаком)
     */
    rotate: function (itemsRotated) {
        var carouselItemsCollection = this.getCarouselItemsCollection(),
            carouselItemsCount = carouselItemsCollection.length;

        // check if we can rotate carousel
        if ((itemsRotated > 0 && this.firstItemIndex + this.itemsDisplayed + itemsRotated > carouselItemsCount) ||
            (itemsRotated < 0 && this.firstItemIndex < Math.abs(itemsRotated))) {
            return;
        }

        this.firstItemIndex += itemsRotated;
        this.firstItemIndex = this.firstItemIndex < 0 ? 0 : this.firstItemIndex;

        this.updateCarouselItems();

        this.fireEvent('rotate', this, this.getCarouselPosition());
    },

    getCarouselPosition: function () {
        var carouselItemsCollection = this.getCarouselItemsCollection(),
            carouselItemsCount = carouselItemsCollection.length,
            data;

        data = {
            count: carouselItemsCount,
            displayed: this.itemsDisplayed,
            rotated: this.itemsRotated,
            first: this.firstItemIndex
        };

        return data;
    },

    isButtonPrevDisabled: function () {
        return this.firstItemIndex  === 0;
    },

    isButtonNextDisabled: function () {
        return this.firstItemIndex  + this.itemsDisplayed === this.getCarouselItems().length;
    },

    isButtonPrevHidden: function () {
        return this.getCarouselItems().length <= this.itemsDisplayed;
    },

    isButtonNextHidden: function () {
        return this.getCarouselItems().length <= this.itemsDisplayed;
    },

    movePrev: function () {
        this.rotate(this.itemsRotated * (-1));
    },

    moveNext: function () {
        this.rotate(this.itemsRotated);
    },

    moveFirst: function () {
        this.firstItemIndex = 0;
        this.rotate(0);
    },

    moveLast: function () {
        this.firstItemIndex = this.getCarouselItems().length - this.itemsDisplayed;
        this.rotate(0);
    },

    /**
     * Отобразить информацию об отсутствии записей для отображения
     */
    hideContainersAndShowNoData: function (noDataText) {
        this.buttonPrev.setHidden(true);
        this.buttonNext.setHidden(true);
        this.carouselContainer.setHidden(true);
        this.showNoData(noDataText);
    },

    /**
     * Отобразить информацию об отсутствии записей для отображения
     */
    showContainersAndHideNoData: function () {
        this.carouselContainer.setHidden(false);
        this.hideNoData();
    },

    /**
     * Обновить отображение элементов карусели
     */
    updateCarouselItems: function () {
        var lastItemIndex,
            lastItem,
            firstItem,
            ITEM_CLS = 'un-carousel-panel-item',
            LAST_VISIBLE_ITEM_CLS = ITEM_CLS + '__lastvisible',
            FIRST_VISIBLE_ITEM_CLS = ITEM_CLS + '__firstvisible',
            carouselContainer = this.carouselContainer,
            carouselItems = this.getCarouselItems();

        if (carouselItems.length === 0) {
            this.hideContainersAndShowNoData(this.noDataText);

            return;
        }

        this.showContainersAndHideNoData();

        lastItemIndex = this.firstItemIndex + this.itemsDisplayed;

        if (lastItemIndex > carouselItems.length) {
            lastItemIndex = carouselItems.length;
            this.firstItemIndex = lastItemIndex - this.itemsDisplayed;
            this.firstItemIndex = this.firstItemIndex < 0 ? 0 : this.firstItemIndex;
        }

        carouselItems.getRange(0, this.firstItemIndex - 1).forEach(function (item) {
            item.removeCls(LAST_VISIBLE_ITEM_CLS);
            item.removeCls(FIRST_VISIBLE_ITEM_CLS);
            item.addCls(ITEM_CLS);
            item.setHidden(true);
        });

        carouselItems.getRange(lastItemIndex, carouselItems.length - 1).forEach(function (item) {
            item.removeCls(LAST_VISIBLE_ITEM_CLS);
            item.removeCls(FIRST_VISIBLE_ITEM_CLS);
            item.addCls(ITEM_CLS);
            item.setHidden(true);
        });

        carouselItems.getRange(this.firstItemIndex, lastItemIndex - 1).forEach(function (item) {
            item.removeCls(LAST_VISIBLE_ITEM_CLS);
            item.removeCls(FIRST_VISIBLE_ITEM_CLS);
            item.addCls(ITEM_CLS);
            item.setHidden(false);

            if (!item.rendered) {
                carouselContainer.add(item);
            }
        });

        lastItem = carouselItems.getAt(lastItemIndex - 1);
        lastItem.removeCls(ITEM_CLS);
        lastItem.addCls(LAST_VISIBLE_ITEM_CLS);

        firstItem = carouselItems.getAt(this.firstItemIndex);
        firstItem.removeCls(ITEM_CLS);
        firstItem.addCls(FIRST_VISIBLE_ITEM_CLS);

        this.buttonPrev.setDisabled(this.isButtonPrevDisabled());
        this.buttonNext.setDisabled(this.isButtonNextDisabled());
        this.buttonPrev.setHidden(this.isButtonPrevHidden());
        this.buttonNext.setHidden(this.isButtonNextHidden());
    },

    addCarouselItems: function (items) {
        var count,
            oldCount;

        oldCount = this.carouselItemsCollection.length;
        this.getCarouselItemsCollection().addAll(items);
        count = this.carouselItemsCollection.length;

        this.fireEvent('itemcountchanged', this, count, oldCount);

        this.updateCarouselItems();
    },

    doConfigCarouselItem: function (item) {
        item.setHidden(true);
        item.setFlex(1); // не убирать: растягивает элементы по ширине
        item.setCollapsible(true);
        item.setCollapsed(false);

        return item;
    },

    addCarouselItem: function (item) {
        this.getCarouselItemsCollection().add(item);
        this.updateCarouselItems();
    },

    removeCarouselItem: function (item) {
        this.getCarouselItemsCollection().remove(item);
        this.updateCarouselItems();
    },

    removeAllCarouselItems: function () {
        this.getCarouselItemsCollection().removeAll();
        this.updateCarouselItems();
    },

    getCarouselItemsCollection: function () {
        return this.carouselItemsCollection;
    },

    getCarouselItems: function () {
        return this.getCarouselItemsCollection();
    },

    createCarouselItemCollection: function () {
        var carouselItemsCollection;

        carouselItemsCollection = new Ext.util.MixedCollection();
        carouselItemsCollection.on('add', this.onItemAdd, this);
        carouselItemsCollection.on('remove', this.onItemRemove, this);

        return carouselItemsCollection;
    },

    onItemAdd: function () {
        var count = this.carouselItemsCollection.length;

        this.fireEvent('itemcountchanged', this, count, count - 1);
    },

    onItemRemove: function (item) {
        var count = this.carouselItemsCollection.length;

        this.carouselContainer.remove(item);

        this.fireEvent('itemcountchanged', this, count, count + 1);
    }
});
