/**
 * Автоматический лейаут для карточек простых атрибутов с учетом групп
 *
 * @author Ivan Marshalkin
 * @date 2016-05-25
 */

Ext.define('Unidata.view.steward.dataentity.layout.AutoLayout', {

    requires: [
        'Unidata.view.steward.dataentity.layout.NoneLayout',
        'Unidata.view.steward.dataentity.layout.FlatLayout',
        'Unidata.view.steward.dataentity.layout.MultiColumnLayout',
        'Unidata.view.steward.dataentity.layout.OriginLayout'
    ],

    layoutType: null, // NONE || FLAT || MULTICOLUMN || ORIGIN

    tablets: null,
    layout: null,

    constructor: function (cfg) {
        var noneLayoutClassName   = 'Unidata.view.steward.dataentity.layout.NoneLayout',
            flatLayoutClassName   = 'Unidata.view.steward.dataentity.layout.FlatLayout',
            multiLayoutClassName  = 'Unidata.view.steward.dataentity.layout.MultiColumnLayout',
            originLayoutClassName = 'Unidata.view.steward.dataentity.layout.OriginLayout',
            layoutClassName       = flatLayoutClassName;

        this.callParent(arguments);

        Ext.apply(this, cfg);

        // какой лейату создаем?
        // лейаут указали явно
        if (this.layoutType) {
            switch (this.layoutType) {
                case 'NONE':
                    layoutClassName = noneLayoutClassName;
                    break;
                case 'FLAT':
                    layoutClassName = flatLayoutClassName;
                    break;
                case 'MULTICOLUMN':
                    layoutClassName = multiLayoutClassName;
                    break;
                case 'ORIGIN':
                    layoutClassName = originLayoutClassName;
                    break;
            }
        // если не указали подбираем сами
        } else {
            if (!this.isSingleColumn()) {
                layoutClassName = multiLayoutClassName;
            }
        }

        this.layout = Ext.create(layoutClassName, {
            tablets: this.tablets
        });
    },

    destroy: function () {
        this.tablets = null;
        this.layout  = null;

        this.callParent(arguments);
    },

    getContainers: function () {
        return this.layout;
    },

    /**
     * Простой вариант отображения - одноколоночный
     */
    isSingleColumn: function () {
        var singleColumn = true,
            columns      = [];

        // если это не вырожденный случай
        if (!this.isDegenerate()) {
            Ext.Array.each(this.tablets, function (tablet) {
                var group = tablet.attributeGroup;

                if (group) {
                    columns.push(group.column);
                }
            });

            columns = Ext.Array.unique(columns);

            if (columns.length > 1) {
                singleColumn = false;
            }
        }

        return singleColumn;
    },

    /**
     * Вырожденный случай - один элемент
     */
    isDegenerate: function () {
        return this.tablets.length <= 1;
    }
});
