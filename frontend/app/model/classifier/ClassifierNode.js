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
     * Этот метод реализован в связи с багами ExtJS: не обновляются записи associations после load ранее загруженной
     * записи или после save update. Для загрузки/сохранения новой записи все в порядке. Т.е. записи associations
     * проставляются только один раз. см. Ext.data.schema.Role.getAssociatedStore
     *
     * Данный метод вручную записывает записи associations из переданного объекта
     */
    // TODO: Реализовать единообразно. Напр, override of Ext.data.schema.Role.getAssociatedStore
    applyAttributes: function (obj) {
        var nodeAttrs = this.nodeAttrs(),
            inheritedNodeAttrs = this.inheritedNodeAttrs();

        if (!obj) {
            return;
        }

        nodeAttrs.suspendEvent('datachanged');
        inheritedNodeAttrs.suspendEvent('datachanged');
        nodeAttrs.removeAll();
        inheritedNodeAttrs.removeAll();
        nodeAttrs.add(obj.nodeAttrs);
        inheritedNodeAttrs.add(obj.inheritedNodeAttrs);
        nodeAttrs.resumeEvent('datachanged');
        inheritedNodeAttrs.resumeEvent('datachanged');
    },

    /**
     * Возвращает истину если все имена атрибутов уникальны
     */
    isAttributeNamesUnique: function () {
        var me      = this,
            uniques = [],
            unique;

        // добавляем все наследованые атрибуты
        this.inheritedNodeAttrs().each(function (inheritedAttribute) {
            uniques.push(me.isAttributeNameUnique(inheritedAttribute.get('name')));
        });

        // добавляем все атрибуты которые созданы ручками пользователем
        this.nodeAttrs().each(function (nodeAttribute) {
            uniques.push(me.isAttributeNameUnique(nodeAttribute.get('name')));
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
            originalLength;

        // добавляем все наследованые атрибуты
        this.inheritedNodeAttrs().each(function (inheritedAttribute) {
            names.push(inheritedAttribute.get('name'));
        });

        // добавляем все атрибуты которые созданы ручками пользователем
        this.nodeAttrs().each(function (nodeAttribute) {
            if (!nodeAttribute.get('userAdded')) {
                Ext.Array.include(names, nodeAttribute.get('name'));
            }
        });

        // добавляем все атрибуты которые созданы ручками пользователем
        this.nodeAttrs().each(function (nodeAttribute) {
            if (nodeAttribute.get('userAdded')) {
                names.push(nodeAttribute.get('name'));
            }
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
        var valid = this.callParent(arguments);

        this.inheritedNodeAttrs().each(function (inheritedAttribute) {
            if (!inheritedAttribute.isValid() || !inheritedAttribute.isValidSimpleDataTypeField()) {
                valid = false;
            }
        });

        this.nodeAttrs().each(function (nodeAttribute) {
            if (!nodeAttribute.isValid() || !nodeAttribute.isValidSimpleDataTypeField()) {
                valid = false;
            }
        });

        if (!this.isAttributeNamesUnique()) {
            valid = false;
        }

        return valid;
    }
});
