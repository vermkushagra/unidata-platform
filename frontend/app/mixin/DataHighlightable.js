/**
 * Миксин для отображения информации об отсутствии данных
 * @author Sergey Shishigin
 * @date 2016-09-02
 */
Ext.define('Unidata.mixin.DataHighlightable', {
    extend: 'Ext.Mixin',

    baseHighlightCls: 'un-dataentity-highlight',

    config: {
        highlight: null
    },

    updateHighlight: function (type) {
        if (this.rendered) {
            this.highlightTablet(type);
        } else {
            this.on('afterrender', function () {
                this.highlightTablet(type);
            }, this);
        }
    },

    clearHighlight: function (type) {
        var baseHighlightCls = this.baseHighlightCls,
            highlightCls = baseHighlightCls + '-' + type;

        this.removeCls(highlightCls);
    },

    /**
     * @param type Тип подсветки из Unidata.view.steward.dataentity.DataEntity.highlightTypes
     * @private
     */
    highlightTablet: function (type) {
        var baseHighlightCls = this.baseHighlightCls,
            highlightCls = baseHighlightCls + '-' + type;

        this.addCls(highlightCls);
    }
});
