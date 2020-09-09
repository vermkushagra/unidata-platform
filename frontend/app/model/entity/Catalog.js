/**
 * Модель элемента каталога
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-15
 */
Ext.define('Unidata.model.entity.Catalog', {

    extend: 'Ext.data.TreeModel',

    requires: [
        'Unidata.validator.LatinAlphaNumber',
        'Unidata.model.entity.catalog.Entity',
        'Unidata.model.entity.catalog.LookupEntity'
    ],

    schema: '',

    identifier: 'sequential',

    fields: [
        {
            name: 'id',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string',
            persist: false
        },
        {
            name: 'groupName',
            type: 'string',
            unique: true
        },
        {
            name: 'displayName',
            type: 'string'
        },
        {
            name: 'entityName',
            type: 'string',
            persist: false,
            convert: function (value, model) {

                if (model.isLeaf()) {
                    return model.get('name');
                } else {
                    return '';
                }

            },
            depends: ['name', 'groupName']
        }
    ],

    validators: [
        {
            type: 'latinalphanumber',
            field: 'name'
        },
        {
            type: 'presence',
            field: 'name',
            message: Unidata.i18n.t('model>codeNameRequired')
        },
        {
            type: 'presence',
            field: 'displayName',
            message: Unidata.i18n.t('model>displayNameRequired')
        }
    ],

    /**
     * Возвращает оригинальное имя группы
     *
     * @returns {String}
     */
    getGroupName: function () {
        return this.get('groupName') || '';
    },

    constructor: function () {
        this.callParent(arguments);
        this.updateName();
    },

    set: function () {

        var result = this.callParent(arguments);

        if (!result) {
            return result;
        }

        if (result.indexOf('groupName') !== -1) {
            this.updateName();
        } else if (result.indexOf('name') !== -1) {
            this.updateGroupName();

            // каскадно обновляем имена групп вложенных нод
            this.cascadeBy(function (record) {
                record.updateGroupName();
            });
        }

        return result;

    },

    updateName: function () {

        if (this.isLeaf()) {
            return;
        }

        this.set('name', this.getGroupName().split('.').pop());
    },

    updateGroupName: function () {
        var name = this.get('name'),
            parent = this.parentNode,
            groupName;

        if (this.isLeaf()) {
            return;
        }

        if (!parent) {
            return;
        }

        if (parent.isRoot()) {
            groupName = name;
        } else {
            groupName = parent.getGroupName() + '.' + name;
        }

        this.set('groupName', groupName);
    }

});
