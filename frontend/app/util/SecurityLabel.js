/**
 * Проверки валидности меток безопасности для работы с EntityDraw
 * @author Ivan Marshalkin
 * @date 2015-10-21
 */
Ext.define('Unidata.util.SecurityLabel', {
    singleton: true,

    checkSecurityLabels: function (attrPath, attrValues, securityLabels) {
        var slFiltered = [],
            checkMap = {},
            result;

        //находим security label в которых участвует проверяемый аттрибут
        Ext.Array.each(securityLabels, function (sl) {
            Ext.Array.each(sl.attributes, function (attribute) {
                if (attribute.path === attrPath) {
                    slFiltered.push(sl);

                    return false; //прекращение итерации
                }
            });
        });

        //проверка
        Ext.Array.each(slFiltered, function (sl) {
            var valid = true;

            if (!checkMap.hasOwnProperty(sl.name)) {
                checkMap[sl.name] = false;
            }

            Ext.Array.each(sl.attributes, function (attribute) {
                var s1,
                    s2;

                if (!attrValues.hasOwnProperty(attribute.path)) {
                    valid = false;
                } else {
                    s1 = attrValues[attribute.path];
                    s2 = attribute.value;

                    s1 = Ext.isString(s1) ? s1.toUpperCase() : s1;
                    s2 = Ext.isString(s2) ? s2.toUpperCase() : s2;

                    /*пустая строка в attribute.value означает что допустимо любое значение*/
                    if (s2 !== '') {
                        if (Ext.isNumeric(s1)) {
                            s1 = String(s1);
                        }

                        if (s1 !== s2) {
                            valid = false;
                        }
                    }
                }
            });

            if (!checkMap[sl.name]) {
                checkMap[sl.name] = valid;
            }
        });

        result = true;

        //все проверки должны быть успешны
        Ext.Object.each(checkMap, function (key, value) {
            if (!value) {
                result = false;
            }
        });

        return result;
    },

    /**
     * TODO: после выпиливания старого entityDraw необходимо выпилить эту рудиментарную функцию
     */
    getAttrValues: function (entityName, sAttrContainer) {
        var attrValues = {};

        sAttrContainer.items.each(function (item) {
            var field = item.items.getAt(0),
                record = field.record,
                sAttrPath;

            if (item.containerType !== 'simpleAttribute') {
                return;
            }

            sAttrPath = [entityName, record.get('name')].join('.');

            attrValues[sAttrPath] = record.get('value');
        });

        return attrValues;
    },

    /**
     * TODO: после выпиливания старого entityDraw необходимо выпилить эту рудиментарную функцию
     */
    isFormValid: function (entityName, attrValues, sAttrContainer) {
        var isFormValid = true,
            user = Unidata.Config.getUser();

        sAttrContainer.items.each(function (item) {
            var field = item.items.getAt(0),
                record = field.record,
                sAttrPath;

            if (item.containerType !== 'simpleAttribute') {
                return;
            }

            sAttrPath = [entityName, record.get('name')].join('.');

            if (!Unidata.util.SecurityLabel.checkSecurityLabels(sAttrPath, attrValues, user.get('securityLabels'))) {
                field.markInvalid(Unidata.i18n.t('util>invalidFieldValue'));
                isFormValid = false;
            }
        });

        return isFormValid;
    },

    /**
     * Возвращает истину, если у пользователю есть метка безопасности для реестра / справочника с именем entityName
     *
     * @param entityName - имя реестра / справочника
     * @returns {boolean}
     */
    hasEntitySecurityLabel: function (entityName) {
        var user           = Unidata.Config.getUser(),
            securityLabels = user.get('securityLabels'),
            result         = false;

        Ext.Array.each(securityLabels, function (sl) {
            Ext.Array.each(sl.attributes, function (attribute) {
                var slEntityName = attribute.path;

                if (Ext.isString(slEntityName)) {
                    slEntityName = slEntityName.split('.');
                    slEntityName = slEntityName[0];

                    if (slEntityName === entityName) {
                        result = true;
                    }
                }
            });
        });

        return result;
    }
});
