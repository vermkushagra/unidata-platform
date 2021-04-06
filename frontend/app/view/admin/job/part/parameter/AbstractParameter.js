/**
 * Абстрактный компонент для редактирования параметра
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.AbstractParameter', {

    extend: 'Ext.container.Container',

    requires: [
        'Ext.form.Labelable',
        'Ext.Element'
    ],

    config: {
        readOnly: false,
        value: '',
        /**
         * Модель параметра
         *
         * @type {Unidata.model.job.parameter.DefaultParameter}
         */
        parameter: null
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    TYPE: '',

    /**
     * Поле ввода.
     *
     * @type {Ext.form.field.Base}
     */
    input: null,

    /**
     * Название инпута. Сделано отдельным label, т.к. названия параметров могут иметь разную длину.
     * Длина заранее неизвестна и надо выстроить все инпуты ровно + чтобы названия параметров были видны
     *
     * @type {Ext.form.Label}
     */
    label: null,

    /**
     * Инициализация компонентов
     */
    initItems: function () {

        var me = this,
            input;

        me.callParent(arguments);

        me.input = input = me.initInput(me.getName(), me.getValue());

        input.setReadOnly(me.getReadOnly());
        input.on('change', me.onInputChange, me);

        me.label = me.initLabel();

        me.add([
            me.label,
            input
        ]);

    },

    /**
     * Инициализация label
     *
     * @returns {Ext.form.Label}
     */
    initLabel: function () {

        var input = this.input,
            labelCls = Ext.form.Labelable.prototype.labelCls,
            unselectableCls = Ext.Element.unselectableCls,
            ui = input.ui,
            cls = [];

        // классы лэйбла инпута
        cls.push(
            labelCls,
            labelCls + '-' + ui,
            unselectableCls
        );

        return Ext.widget({
            xtype:  'label',
            text:   this.getTitle() + ':',
            margin: '0 10 0 0',
            width:  'auto',
            minWidth: 90,
            cls: cls.join(' '),
            forId: input.getId()
        });

    },

    /**
     * Инициализация инпута
     *
     * @param {String} name - имя
     * @param {String} value - значение
     */
    initInput: function () {
        throw new Error(Unidata.i18n.t('admin.job>notImplementedMethodInitInput'));
    },

    /**
     * Получение ширины подписи к инпуту
     *
     * @returns {Number}
     */
    getLabelWidth: function () {
        return this.label.getWidth();
    },

    /**
     * Задание ширины подписи к инпуту
     *
     * @param {Number} width
     */
    setLabelWidth: function (width) {
        this.label.setWidth(width);
    },

    /**
     * Загружка-алиас для получения названия параметра (вдруг появится). Сейчас просто возвращает name
     *
     * @returns {String}
     */
    getTitle: function () {
        return this.getName();
    },

    /**
     * @returns {Unidata.model.job.parameter.meta.DefaultMetaParameter}
     */
    getMeta: function () {
        return this.getParameter().getMeta();
    },

    /**
     * Обновляет параметр. См. config
     *
     * @param {Object} parameter
     */
    updateParameter: function (parameter) {
        this.setName(parameter.getName());
    },

    /**
     * Обновляет состояние readOnly. См. config
     *
     * @param {Boolean} readOnly
     */
    updateReadOnly: function (readOnly) {
        if (this.rendered) {
            this.input.setReadOnly(readOnly);
        }
    },

    /**
     * Задаёт имя параметра
     *
     * @param {String} name
     */
    setName: function (name) {

        this.name = name;

        if (this.rendered) {
            this.input.setName(name);
        }
    },

    /**
     * Возвращает имя параметра
     *
     * @returns {String}
     */
    getName: function () {
        return this.name;
    },

    /**
     * Обновляет значение параметра. См. config
     *
     * @param value
     */
    updateValue: function (value) {
        if (this.rendered) {
            this.input.setValue(value);
        }
    },

    /**
     * Получение типа параметра (STRING, etc...)
     *
     * @returns {string}
     */
    getType: function () {
        return this.TYPE;
    },

    /**
     * Обработчик события изменения значения инпута
     */
    onInputChange: function () {
        this.value = this.input.getValue();
        this.parameter.setValue(this.value);
    }

});
