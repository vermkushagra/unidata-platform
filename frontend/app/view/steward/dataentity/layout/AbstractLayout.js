/**
 * Абстрактный лейаут для карточек простых атрибутов с учетом групп
 *
 * @author Ivan Marshalkin
 * @date 2016-05-26
 */

Ext.define('Unidata.view.steward.dataentity.layout.AbstractLayout', {
    extend: 'Ext.container.Container',

    cls: 'un-ded-layout',

    tablets: null,

    constructor: function () {
        this.callParent(arguments);

        this.tablets = this.tablets || [];
    },

    onDestroy: function () {
        this.tablets = null;

        this.callParent(arguments);
    },

    /**
     * Прокидываем readOnly до таблеток с простыми атрибутами
     *
     * @param readOnly
     */
    setReadOnly: function (readOnly) {
        var tablets = this.tablets;

        if (!Ext.isArray(tablets)) {
            return;
        }

        Ext.Array.each(tablets, function (tablet) {
            tablet.setReadOnly(readOnly);
        });
    },

    /**
     * Прокидываем preventMarkField до таблеток с простыми атрибутами
     *
     * @param readOnly
     */
    setPreventMarkField: function (value) {
        var tablets = this.tablets;

        if (!Ext.isArray(tablets)) {
            return;
        }

        Ext.Array.each(tablets, function (tablet) {
            tablet.setPreventMarkField(value);
        });
    },

    /**
     * Прокидываем hiddenAttribute до таблеток с простыми атрибутами
     *
     * @param readOnly
     */
    setHiddenAttribute: function (hiddenAttribute) {
        var tablets = this.tablets;

        if (!Ext.isArray(tablets)) {
            return;
        }

        Ext.Array.each(tablets, function (tablet) {
            tablet.setHiddenAttribute(hiddenAttribute);
        });
    }
});
