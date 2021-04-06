/**
 * Список кодовых обозначений точек расширения для реализации callback из ui user exit
 *
 * @author Ivan Marshalkin
 * @date 2017-07-24
 */

Ext.define('Unidata.uiuserexit.callback.CallbackTypes', {
    statics: {
        UNKNOWN: null,

        // точки расширения стадии initComponent
        LOGIN_FORM_INITCOMPONENT: 'LOGIN_FORM_INITCOMPONENT',      // форма входа в систему

        // точки расширения для dataviewer
        DATAVIEWER_SHOW: 'DATAVIEWER_SHOW'                         // показ карточки записи
    }
});
