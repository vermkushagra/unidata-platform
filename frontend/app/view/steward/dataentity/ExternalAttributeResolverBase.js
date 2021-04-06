/**
 * Базовый класс для нахождения внешнего класса предатавления атрибута
 *
 * @author Ivan Marshalkin
 * @date 2017-01-27
 */

Ext.define('Unidata.view.steward.dataentity.ExternalAttributeResolverBase', {
    statics: {
        active: false, // резолвер включен / выключен (выключеные не учитываются)

        resolveExternalAttributeClassName: function (metaRecord, dataRecord, metaAttribute, dataAttribute, opts) {
            var allClasses = Ext.ClassManager.classes,
                ExternalAttributeClass = null,
                classes;

            classes = Ext.Object.getValues(allClasses);

            Ext.Array.each(classes, function (cls) {
                var ClassConstructor = null,
                    baseResolverClass = Unidata.view.steward.dataentity.ExternalAttributeResolverBase;

                // обходим все резолверы
                if (cls && cls.prototype instanceof baseResolverClass && cls !== baseResolverClass) {
                    // только активные резолверы
                    if (Ext.isFunction(cls.resolveClassConstructor) && cls.active) {
                        ClassConstructor = cls.resolveClassConstructor(metaRecord, dataRecord,
                            metaAttribute, dataAttribute, opts);

                        // резолвер должен вернуть класс-конструктор
                        if (ClassConstructor && Ext.Array.contains(classes , ClassConstructor)) {
                            ExternalAttributeClass = ExternalAttributeClass ? ExternalAttributeClass : ClassConstructor;

                            return false; // завершение итерации
                        }
                    }
                }
            });

            return ExternalAttributeClass;
        },

        /**
         * Функция возвращающая конструктор класса
         *
         * @returns {null}
         */
        resolveClassConstructor: function (metaRecord, dataRecord, metaAttribute, dataAttribute, opts) {
            var unused = opts; // неиспользуемая переменная, чтоб jscs не почистил параметры

            return null;
        }
    }
});
