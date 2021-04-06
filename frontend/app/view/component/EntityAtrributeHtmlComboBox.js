/**
 * Копонент отображающий полный путь выбранного атрибута по моделе реестра / справочника
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

    cls: 'un-entityattributehtmlcombo',

    triggerWrapCls: 'input-port',
    triggers: {
        clear: {
            hideOnReadOnly: true,
            cls: 'x-form-clear-trigger',
            handler: function () {
                this.setValue(null);
            }
        }
    },

    displayField: 'displayName',
    valueField: 'path',

    store: {
        fields: [
            'displayName',
            'path'
        ],
        data: []
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
            data = this.buildLevel(metaRecord, '', '');

            this.getStore().setData(data);
        }
    },

    formatDisplayName: function (metaAttribute) {
        var str;

        str = '<span class="un-data-quality-htmlcombo-treeItem">' +
                Ext.String.htmlEncode(metaAttribute.get('displayName')) +
            '</span>';

        return str;
    },

    buildLevel: function (nestedEntity, level, viewLevel) {
        var me     = this,
            result = [],
            attr;

        function buildAttr (attr) {
            result.push({
                path: level + attr.get('name'),
                displayName: viewLevel + me.formatDisplayName(attr)
            });
        }

        if (Ext.isFunction(nestedEntity.getCodeAttribute)) {
            attr = nestedEntity.getCodeAttribute();

            if (attr) {
                // при создании справочника кодового аттрибута может и не быть
                buildAttr(attr);
            }
        }

        if (Ext.isFunction(nestedEntity.aliasCodeAttributes)) {
            nestedEntity.aliasCodeAttributes().each(buildAttr);
        }

        nestedEntity.simpleAttributes().each(buildAttr);

        if (Ext.isFunction(nestedEntity.complexAttributes)) {

            nestedEntity.complexAttributes().each(function (attr) {

                var path = level + attr.get('name'),
                    displayName = viewLevel + me.formatDisplayName(attr),
                    nestedResult;

                result.push({
                    path: path,
                    displayName: displayName
                });

                nestedResult = me.buildLevel(attr.getNestedEntity(), path + '.', displayName + ' &gt; ');

                result = Ext.Array.merge(result, nestedResult);
            });

        }

        return result;
    },

    expand: function () {
        return false;
    }
});
