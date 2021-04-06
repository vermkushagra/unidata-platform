Ext.define('Unidata.util.AttributeDiff', {
    singleton: true,

    findAttributeDiffByPath: function (diffToDraftCollection, path) {
        return diffToDraftCollection.findRecord('path', path, 0, false, false, true);
    },

    getOldAttribute: function (attributeDiff, path, dataAttribute) {
        var prefix,
            getterName,
            oldAttribute;

        if (!attributeDiff) {
            return null;
        }

        prefix = this.getTypePrefixByDataAttribute(dataAttribute);
        getterName = this.buildOldAttributeGetterName(prefix);
        oldAttribute = attributeDiff[getterName].call(attributeDiff);

        return oldAttribute;
    },

    getTypePrefixByDataAttribute: function (dataAttribute) {
        var prefix = '';

        if (dataAttribute instanceof Unidata.model.data.SimpleAttribute) {
            prefix = 'simple';
        } else if (dataAttribute instanceof  Unidata.model.data.CodeAttribute) {
            prefix = 'code';
        } else if (dataAttribute instanceof  Unidata.model.data.ComplexAttribute) {
            prefix = 'complex';
        } else if (dataAttribute instanceof  Unidata.model.data.ArrayAttribute) {
            prefix = 'array';
        }

        return prefix;
    },

    buildOldAttributeGetterName: function (attributePrefix) {
        var tpl = 'getOld{0}Value',
            getterName;

        getterName = Ext.String.format(tpl, Ext.String.capitalize(attributePrefix));

        return getterName;
    }
});
