/**
 * Миксин, добавляющий базовое управление статуса для экранов, относящимся к классификаторам
 * @author Sergey Shishigin
 * @date 2016-08-017
 */
Ext.define('Unidata.mixin.StatusManageable', {
    extend: 'Ext.Mixin',

    loadingText: Unidata.i18n.t('common:loading'),

    config: {
        status: Unidata.StatusConstant.NONE
    },

    updateStatus: function (status, oldStatus) {
        var StatusConstantClass = Unidata.StatusConstant;

        if (status === oldStatus) {
            return;
        }

        switch (status) {
            case StatusConstantClass.NONE:
                this.setLoading(false);
                break;
            case StatusConstantClass.LOADING:
                this.setLoading(this.loadingText);
                break;
            case StatusConstantClass.READY:
                this.setLoading(false);
                break;
            default:
                throw new Error(Unidata.i18n.t('glossary:badStatus'));
                //TODO: implement me better
                break;
        }
    }
});
