/**
 *
 * Компонент для DataEntity
 *
 * @author Ivan Marshalkin
 * @date 2016-02-01
 */

/**
 * Пример использования
 *
 * var button = Ext.create('Unidata.view.component.dataEntity.button.RoundButton', {
 *      buttonSize: 'big',
 *      shadowed: false,
 *      floating: true
 * });
 *
 * button.showAt(100, 100);
 *
 * button.setShadowed(!button.getShadowed());
 * button.setButtonMedium();
 *
 * button.setRadius(50);
 * button.setDiameter(40);
 *
 * button.on('beforeclick', function () {}, this);
 * Если хотя бы один обработчик события beforeclick возвращает false тогда событие click не генерируется
 * button.on('click', function () {}, this);
 *
 * Возможные параметры конфига
 *
 * buttonSize: big | medium
 * shadowed: true | false
 */

Ext.define('Unidata.view.component.button.RoundButton', {
    extend: 'Ext.button.Button',

    componentCls: 'un-roundbutton',

    xtype: 'un.roundbtn',

    glyph: '',

    radius: null,
    buttonSize: 'medium',
    shadowed: true,

    initComponent: function () {
        this.callParent(arguments);

        this.on('render', this.onComponentRender, this, {single: true});
    },

    onComponentRender: function () {
        this.initButtonSize();
        this.initShadowed();
    },

    initButtonSize: function () {
        if (!Ext.Array.contains(['big', 'medium', 'small', 'extrasmall'], this.buttonSize)) {
            this.buttonSize = 'medium';
        }

        switch (this.buttonSize) {
            case 'big':
                this.setButtonBig();
                break;
            case 'medium':
                this.setButtonMedium();
                break;
            case 'small':
                this.setButtonSmall();
                break;
            case 'extrasmall':
                this.setButtonExtraSmall();
                break;
            default:
                console.warn('RoundButton: unknown buttonSize value');
        }

        if (this.radius) {
            this.setRadius(this.radius);
        }
    },

    initShadowed: function () {
        var shadowed = Boolean(this.shadowed);

        this.setShadowed(shadowed);
    },

    setRadius: function (radius) {
        var diameter = 2 * radius;

        this.setHeight(diameter);
        this.setWidth(diameter);
    },

    setDiameter: function (diameter) {
        var radius = parseInt(diameter / 2, 10);

        this.setRadius(radius);
    },

    removeButtonSizeCls: function () {
        this.removeCls('un-roundbutton__big');
        this.removeCls('un-roundbutton__medium');
        this.removeCls('un-roundbutton__small');
        this.removeCls('un-roundbutton__extrasmall');
    },

    setButtonBig: function () {
        this.buttonsize = 'big';

        this.setRadius(30);

        this.removeButtonSizeCls();
        this.addCls('un-roundbutton__big');
    },

    setButtonMedium: function () {
        this.buttonsize = 'medium';

        this.setRadius(22);

        this.removeButtonSizeCls();
        this.addCls('un-roundbutton__medium');
    },

    setButtonSmall: function () {
        this.buttonsize = 'small';

        this.setRadius(16);

        this.removeButtonSizeCls();
        this.addCls('un-roundbutton__small');
    },

    setButtonExtraSmall: function () {
        this.buttonsize = 'extrasmall';

        this.setRadius(12);

        this.removeButtonSizeCls();
        this.addCls('un-roundbutton__extrasmall');
    },

    setShadowed: function (shadowed) {
        var cls = 'un-roundbutton-shadowed';

        this.shadowed = shadowed;

        shadowed ? this.addCls(cls) : this.removeCls(cls);
    },

    getShadowed: function () {
        return this.shadowed;
    }
});
