/**
 * Утилитный класс для работы с enumerations
 *
 * @author Sergey Shishigin
 * @date 2016-10-21
 */
// TODO: Можно объединить Unidata.util.MeasurementUnit и Unidata.util.Enumeration
Ext.define('Unidata.util.Enumeration', {
    singleton: true,

    /**
     * Фабричный метод по созданию combobox
     *
     * @param enumerationStore Store (model: Unidata.model.entity.Enumeration)
     * @param enumerationName Имя enumeration для значений
     * @param customCfg
     * @returns {Unidata.view.component.EnumerationComboBox|*}
     */
    createEnumerationComboBox: function (enumerationStore, enumerationName, customCfg) {
        var combobox,
            store,
            enumeration,
            enumerationValues,
            cfg;

        cfg = {};

        enumeration = this.findEnumeration(enumerationStore, enumerationName);

        if (enumeration) {
            enumerationValues = enumeration.values();

            store = Ext.create('Ext.data.ChainedStore', {
                source: enumerationValues,
                remoteSort: false,
                remoteFilter: false
            });

            cfg.store = store;
        }

        Ext.apply(cfg, customCfg);

        combobox = Ext.create('Unidata.view.component.EnumerationComboBox', cfg);

        return combobox;
    },

    findEnumeration: function (enumerationStore, enumerationName) {
        var enumeration;

        enumeration = enumerationStore.findRecord('name', enumerationName, 0, false, true, true);

        return enumeration;
    }
});
