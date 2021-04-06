/**
 * Фабрика параметров. В зависимости от типа meta возвращает разные
 * компоненты, унаследованные от Unidata.view.admin.job.part.parameter.AbstractParameter
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.Parameter', {

    requires: [
        'Unidata.view.admin.job.part.parameter.StringParameter',
        'Unidata.view.admin.job.part.parameter.EnumParameter',
        'Unidata.view.admin.job.part.parameter.BooleanParameter',
        'Unidata.view.admin.job.part.parameter.LongParameter',
        'Unidata.view.admin.job.part.parameter.DoubleParameter',
        'Unidata.view.admin.job.part.parameter.DateParameter',
        'Unidata.view.admin.job.part.parameter.UserSelectorParameter'
    ],

    alias: 'widget.admin.job.param',

    config: {
        /**
         * Модель параметра, по типу которого работает фабрика.
         * Остальные опции - смотри Unidata.view.admin.job.part.parameter.AbstractParameter
         *
         * @type {Unidata.model.job.parameter.DefaultParameter}
         */
        parameter: null
    },

    constructor: function (cfg) {

        cfg.xtype = 'admin.job.param.' + cfg.parameter.getType().toLowerCase();

        return Ext.widget(cfg);
    }

});
