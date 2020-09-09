Ext.define('Unidata.overrides.grid.property.Reader', {
    override: 'Ext.grid.property.Reader',

    // @private
    // перопределяем метод для возможности использования field, который хранит значения типа Array, в PropertyGrid
    isEditableValue: function (val) {
        return Ext.isPrimitive(val) || Ext.isDate(val) || val === null || Ext.isArray(val);
    }
});
