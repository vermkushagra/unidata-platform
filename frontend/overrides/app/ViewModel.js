/**
 * Оверайд базового класса ViewModel
 *
 * @author Ivan Marshalkin
 * @date 2017-11-01
 */

Ext.define('Ext.overrides.app.ViewModel', {
    override: 'Ext.app.ViewModel',

    enableExperimentalOverride: false,

    constructor: function () {
        var me = this;

        this.callParent(arguments);

        // переодически формулы забинденые на свойство из секции data имеют значения из родительской ViewModel а не из текущей
        // исправляем данное поведение
        // реализовано на примере Unidata.view.admin.entity.metarecord.attribute.AttributeModel часть формул получает не правлиьное значение
        // readOnly (например isCurrentAttributeCanDeleted)
        if (this.enableExperimentalOverride) {
            if (this.defaultConfig && this.defaultConfig.data) {
                Ext.Object.each(this.defaultConfig.data, function (key, value) {
                    me.hadValue[key] = true;
                    me.data[key] = value;
                    // me._data[key] = value;
                });
            }
        }
    }
});
