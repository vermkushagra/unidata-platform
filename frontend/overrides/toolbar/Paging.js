/**
 * Оверрайдля пагинатора.
 *
 * Добавлена возможность скрывать управляющие элементы пейджера.
 *
 *
 * Пример: пейджер который ничего не отображает
 *
 *       {
 *           xtype: 'pagingtoolbar',
 *           reference: 'm2mlistPaging',
 *           displayInfo: false,
 *           hideRefreshButton: true,
 *           hideFirstButton: true,
 *           hidePrevButton: true,
 *           hideBeforePageText: true,
 *           hidePageNumberField: true,
 *           hideAfterPageText: true,
 *           hideNextButton: true,
 *           hideLastButton: true,
 *           hideRefreshButton: true,
 *           hideSeparator1: true,
 *           hideSeparator2: true,
 *           hideSeparator3: true,
 *       }
 *
 * @author Ivan Marshalkin
 * @date 2017-05-15
 */

Ext.define('Ext.overrides.toolbar.Paging', {
    override: 'Ext.toolbar.Paging',

    cls: 'un-toolbar-paging',
    // по умолчанию не отображаем текст перед полем ввода страницы
    beforePageText: '',
    // поле ввода страницы по умолчанию (нормально влезает 4 разряда)
    inputItemWidth: 50,

    // лейаут по умолчанию для отображения элементов паджинатора выровненных по центру
    layout: {
        type: 'hbox',
        pack: 'center'
    },

    // true - скрыть кнопку используемую для перехода на первую страницу данных в гриде
    hideFirstButton: false,

    // true - скрыть кнопку используемую для перехода на предыдущую страницу данных в гриде
    hidePrevButton: false,

    // true - скрыть разделитель после кнопкок 'на первую' и 'предыдущая' (по умолчанию скрыт)
    hideSeparator1: true,

    //true - скрыть 'beforePageText' текст (по умолчанию скрыт)
    hideBeforePageText: true,

    //true - скрыть поле ввода номера страницы
    hidePageNumberField: false,

    //true - скрыть 'afterPageText' текст
    hideAfterPageText: false,

    // true - скрыть разделитель после информационной секции по текущей странице (по умолчанию скрыт)
    hideSeparator2: true,

    // true - скрыть кнопку используемую для перехода на следующую страницу данных в гриде
    hideNextButton: false,

    // true - скрыть кнопку используемую для перехода на последнюю страницу данных в гриде
    hideLastButton: false,

    // true - скрыть разделитель после кнопкок 'на последнюю' и 'следующую' (по умолчанию скрыт)
    hideSeparator3: true,

    //true - скрыть кнопку используемую для обновления данных в гриде
    hideRefreshButton: true,

    inputItemMargin: '-1 2 3 15',
    beforePageTextMargin: '-1 2 3 12',

    //есть так же из коробки флаг displayInfo - отображать / скрывать информацию по количеству всего записей

    initComponent: function () {
        var inputItem;

        this.callParent(arguments);

        // скрываем элементы которые не нужны
        if (this.hideFirstButton) {
            this.down('#first').hide();
        }

        if (this.hidePrevButton) {
            this.down('#prev').hide();
        }

        if (this.hideSeparator1) {
            this.hideSeparatorByIndex(1);
        }

        if (this.hideBeforePageText) {
            this.hideTbTextByIndex(1);

            if (inputItem = this.down('#inputItem')) {
                inputItem.setMargin(this.inputItemMargin);
            }
        } else {
            this.getTbTextByIndex(1).setMargin(this.beforePageTextMargin);
        }

        if (this.hidePageNumberField) {
            this.down('#inputItem').hide();
        }

        if (this.hideAfterPageText) {
            this.down('#afterTextItem').hide();
        }

        if (this.hideSeparator2) {
            this.hideSeparatorByIndex(2);
        }

        if (this.hideNextButton) {
            this.down('#next').hide();
        }

        if (this.hideLastButton) {
            this.down('#last').hide();
        }

        if (this.hideSeparator3) {
            this.hideSeparatorByIndex(3);
        }

        if (this.hideRefreshButton) {
            this.down('#refresh').hide();
        }
    },

    /**
     * Скрывает сепараторы по индексу
     *
     * @param index - индекс скрываемого сепаратора
     */
    hideSeparatorByIndex: function (index) {
        var components = this.query('tbseparator'),
            separator = components[index - 1];

        if (separator) {
            separator.hide();
        }
    },

    /**
     * Скрывает элементы отображающие текст по индексу
     *
     * @param index
     */
    hideTbTextByIndex: function (index) {
        var tbtext = this.getTbTextByIndex(index);

        if (tbtext) {
            tbtext.hide();
        }
    },

    /**
     * Получить элемент отоображающий текст по индексу
     *
     * @param index
     * @returns {*}
     */
    getTbTextByIndex: function (index) {
        var components = this.query('tbtext'),
            tbtext = components[index - 1];

        return tbtext;
    }
});
