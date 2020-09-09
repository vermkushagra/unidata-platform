/**
 * Компонент отображающий полный путь выбранного атрибута по моделе реестра / справочника
 *
 * @author Ivan Marshalkin
 * @date 2016-10-17
 */

Ext.define('Unidata.view.component.EntityAtrributeHtmlComboBox', {
    extend: 'Unidata.view.component.HtmlComboBox',

    alias: 'widget.un.entityattributehtmlcombo',

    config: {
        metaRecord: null
    },

    // simpleAttributes: false,             // отображать простые атрибуты
    // complexAttributes: false,            // отображать комплексные атрибуты
    // codeAttributes: false,               // отображать кодовые атрибуты
    // aliasCodeAttributes: false,          // отображать альтернативные кодовые атрибуты

    queryMode: 'local',

    cls: 'un-entityattributehtmlcombo un-htmlcombo',

    triggerWrapCls: 'input-port',
    triggers: {
        clear: {
            hideOnReadOnly: true,
            cls: 'x-form-clear-trigger',
            handler: function () {
                this.clearValue();
            }
        }
    },

    displayField: 'displayName',
    valueField: 'path',

    /**
     * В store хранится map: namePath -> displayNamePath
     */
    store: {
        fields: [
            'displayName',
            'path',
            'pathTokens'
        ],
        data: []
    },

    getDelimiter: function () {
        var delimiter;

        delimiter = '<span class="un-htmlcombo-treeItem-delimiter"></span>';

        return delimiter;
    },

    updateMetaRecord: function () {
        this.updateDataStore();
    },

    initComponent: function () {
        var value;

        this.callParent(arguments);

        value = this.getValue();

        this.updateDataStore();

        if (value) {
            this.setValue(value);
        }

        this.on('select', this.onRecordSelect, this);
    },

    onRecordSelect: function () {
        this.updateLayout();
    },

    updateDataStore: function () {
        var metaRecord = this.getMetaRecord(),
            store      = this.getStore(),
            data;

        if (store && store.isStore && metaRecord) {
            data = this.buildLevel(metaRecord);

            this.getStore().setData(data);
        }
    },

    formatDisplayName: function (displayName) {
        return Ext.String.format('<span class="un-htmlcombo-treeItem">{0}</span>', Ext.String.htmlEncode(displayName));
    },

    buildLevel: function (nestedEntity, level, pathTokens) {
        var me     = this,
            result = [],
            attr,
            delimiter = this.getDelimiter();

        pathTokens = pathTokens || [];
        level = level || '';

        function buildAttr (attr, path) {
            var displayName = attr.get('displayName'),
                innerPathTokens = Ext.Array.merge(pathTokens, displayName);

            if (!path) {
                path = level + attr.get('name');
            }

            result.push({
                path: path,
                displayName: Ext.Array.map(innerPathTokens, me.formatDisplayName, this).join(delimiter),
                displayNameSimple: displayName,
                pathTokens: innerPathTokens
            });
        }

        if (Unidata.util.MetaRecord.isEntity(nestedEntity)) {
            buildAttr(nestedEntity, Unidata.util.upath.UPath.fullRecordPath);
        }

        if (Ext.isFunction(nestedEntity.getCodeAttribute)) {
            attr = nestedEntity.getCodeAttribute();

            if (attr) {
                // при создании справочника кодового аттрибута может и не быть
                buildAttr(attr);
            }
        }

        if (Ext.isFunction(nestedEntity.aliasCodeAttributes)) {
            nestedEntity.aliasCodeAttributes().each(function (attr) {
                buildAttr(attr);
            });
        }

        nestedEntity.simpleAttributes().each(function (attr) {
            buildAttr(attr);
        });

        nestedEntity.arrayAttributes().each(function (attr) {
            buildAttr(attr);
        });

        if (Ext.isFunction(nestedEntity.complexAttributes)) {

            nestedEntity.complexAttributes().each(function (attr) {
                var path = level + attr.get('name'),
                    displayName = attr.get('displayName'),
                    nestedResult,
                    nestedPathTokens;

                nestedPathTokens = Ext.Array.merge(pathTokens, displayName);

                result.push({
                    path: path,
                    displayName: Ext.Array.map(nestedPathTokens, me.formatDisplayName, this).join(delimiter),
                    displayNameSimple: displayName,
                    pathTokens: nestedPathTokens
                });

                nestedResult = me.buildLevel(attr.getNestedEntity(), path + '.', nestedPathTokens);

                result = Ext.Array.merge(result, nestedResult);
            });

        }

        return result;
    },

    expand: function () {
        return false;
    }
});
