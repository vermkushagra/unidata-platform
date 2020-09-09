/**
 * Модель узла классификатора
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.model.classifier.ClassifierNode', {

    extend: 'Ext.data.TreeModel',

    idProperty: 'id',

    fields: [
        //{name: 'tempId', type: 'auto', persist: false},
        {
            name: 'name',
            type: 'string',
            allowBlank: false
        },
        {
            name: 'description',
            type: 'string'
        },
        {
            name: 'text',
            mapping: 'name',
            persist: false
        },
        {
            name: 'parentId',
            serialize: function (value) {
                if (value === 'root') {
                    value = null;
                }

                return value;
            },
            allowNull: false
        },
        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'code',
            type: 'string',
            allowNull: false
        },
        {
            name: 'childCount',
            type: 'int',
            persist: false
        },
        {
            name: 'leaf',
            calculate: function (data) {
                return data.childCount === 0;
            },
            depends: ['childCount']
        },
        {
            name: 'classifierName',
            type: 'string',
            persist: false
        },
        {
            // При клике на специальный узел загружаются остальные children
            name: 'isSpecialNode',
            type: 'boolean',
            persist: false
        },
        {
            name: 'ownNodeAttrs',
            type: 'boolean',
            persist: false
        },
        // см. Unidata.util.DataRecord.bindManyToOneAssociationListeners
        {
            name: 'localVersion',
            type: 'integer'
            //persist: false
        },
        // см. Unidata.util.DataRecord.bindManyToOneAssociationListeners
        {
            name: 'isLocalVersionBinded',
            type: 'boolean',
            defaultValue: false,
            persist: false
        },
        {
            name: 'phantom',
            type: 'string',
            persist: false
        }
    ],

    hasMany: [
        {
            name: 'nodeAttrs',
            model: 'attribute.ClassifierNodeAttribute'
        },
        {
            name: 'inheritedNodeAttrs',
            model: 'attribute.ClassifierNodeAttribute'
        },
        {
            name: 'nodeArrayAttrs',
            model: 'attribute.ClassifierNodeArrayAttribute'
        },
        {
            name: 'inheritedNodeArrayAttrs',
            model: 'attribute.ClassifierNodeArrayAttribute'
        },
        {
            name: 'customProperties',
            model: 'KeyValuePair',
            storeConfig: {
                /*
                 * Блокируем загрузку store
                 * В случае если backend пришлет null вместо пустого массива ExtJS запрашивает данные
                 * по url с именем модели
                 *
                 * https://unidata.atlassian.net/browse/UN-1062
                 * https://www.sencha.com/forum/showthread.php?302601-Nested-Model-Data-Bind-resulting-in-server-request-for-data
                 */
                load: function () {
                    return;
                }
            }
        }
    ],

    proxy: {
        type: 'un.classifiernode',
        isExactNodeLoad: true
    },

    // !!! Без этого валидатора не будет правильно работать store.sync
    validators: {
        name: [
            {
                type: 'presence',
                message: Unidata.i18n.t('validation:field.required')
            },
            {
                type: 'denydotsymbol'
            }
        ]
    },

    /**
     * Добавление специального узла
     * @param moreChildCount Кол-во children, которые можно подгрузить
     */
    appendSpecialNode: function (moreChildCount) {
        var name,
            specialNodeCfg;

        name = Unidata.i18n.t('other>more', {count: moreChildCount});

        specialNodeCfg = {
            childCount: 0,
            name: name,
            isSpecialNode: true,
            phantom: false,
            cls: 'un-classifier-node-special'
        };

        this.appendChild(specialNodeCfg);
    },

    /**
     * Возвращает истину если все имена атрибутов уникальны
     */
    isAttributeNamesUnique: function () {
        var me = this,
            uniques = [],
            stores,
            unique;

        stores = [
            this.inheritedNodeAttrs(),
            this.nodeAttrs(),
            this.inheritedNodeArrayAttrs(),
            this.nodeArrayAttrs()
        ];

        Ext.Array.each(stores, function (store) {
            store.each(function (attribute) {
                uniques.push(me.isAttributeNameUnique(attribute.get('name')));
            });
        });

        // все имена уникальны
        unique = Ext.Array.every(uniques, function (item) {
            return item === true;
        });

        return unique;
    },

    /**
     * Возвращает истину если указанное имя атрибута уникально
     */
    isAttributeNameUnique: function (attributeName) {
        var names = [],
            storesOwn,
            storesInherited,
            originalLength;

        storesInherited = [
            this.inheritedNodeAttrs(),
            this.inheritedNodeArrayAttrs()
        ];

        storesOwn = [
            this.nodeAttrs(),
            this.nodeArrayAttrs()
        ];

        // добавляем все наследованые атрибуты
        Ext.Array.each(storesInherited, function (store) {
            store.each(function (inheritedAttribute) {
                names.push(inheritedAttribute.get('name'));
            });
        });

        Ext.Array.each(storesOwn, function (store) {
            store.each(function (nodeAttribute) {
                if (!nodeAttribute.get('userAdded')) {
                    Ext.Array.include(names, nodeAttribute.get('name'));
                }

                // добавляем все атрибуты которые созданы ручками пользователем
                if (nodeAttribute.get('userAdded')) {
                    names.push(nodeAttribute.get('name'));
                }
            });
        });

        names = Ext.Array.filter(names, function (item) {
            return item === attributeName;
        });

        originalLength = names.length;

        names = Ext.Array.unique(names);

        // если количество уникальных имен совпадает с
        return originalLength === names.length;
    },

    /**
     * Проверка валидности узла классификатора
     *
     * @returns {*|Object}
     */
    isValid: function () {
        var stores,
            valid;

        valid = this.callParent(arguments);

        stores = [
            this.inheritedNodeAttrs(),
            this.nodeAttrs(),
            this.inheritedNodeArrayAttrs(),
            this.nodeArrayAttrs()
        ];

        Ext.Array.each(stores, function (store) {
            store.each(function (attribute) {
                if (!attribute.isValid() || !attribute.isValidSimpleDataTypeField()) {
                    valid = false;
                }
            });
        });

        if (!this.isAttributeNamesUnique()) {
            valid = false;
        }

        return valid;
    }
});
