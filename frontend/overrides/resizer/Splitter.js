/**
 * @author Ivan Marshalkin
 * @date 2015-11-19
 */
Ext.define('Ext.overrides.resizer.Splitter', {
    override: 'Ext.resizer.Splitter',

    // Устанавливаем размер области по умолчанию. В исходниках он по умолчанию 5, но по факту оказывается 8
    // Переопределил для того чтоб выкинуть однотипный код по установлению ширины / высоты разделителя
    size: 5
});
