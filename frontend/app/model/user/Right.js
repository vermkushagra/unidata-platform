Ext.define('Unidata.model.user.Right', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'read',
            type: 'boolean'
        },
        {
            name: 'create',
            type: 'boolean'
        },
        {
            name: 'update',
            type: 'boolean'
        },
        {
            name: 'delete',
            type: 'boolean'
        },
        {
            name: 'merge',
            type: 'boolean'
        },
        {
            name: 'restore',
            type: 'boolean'
        },
        {
            name: 'full',
            type: 'boolean',
            calculate: function (value) {
                var result = false;

                if (value['create'] && value['read'] && value['update'] && value['delete']) {
                    result = true;
                }

                return result;
            },
            persist: false,
            depends: ['create', 'read', 'update', 'delete']
        },
        {
            name: 'anyCrud',
            type: 'boolean',
            calculate: function (value) {
                var result = false;

                if (value['create'] || value['read'] || value['update'] || value['delete']) {
                    result = true;
                }

                return result;
            },
            persist: false,
            depends: ['create', 'read', 'update', 'delete']
        }
    ],

    hasOne: [{
        name: 'securedResource',
        model: 'user.SecuredResource'
    }],

    proxy: {
        writer: {
            type: 'json',
            writeAllFields: true,
            writeRecordId: false
        }
    },

    /**
     * Получить список прав
     * @returns {String[]}
     */
    getRightList: function () {
        var rights = ['create', 'read', 'update', 'delete'],
            rightList = [];

        Ext.Array.forEach(rights, function (right) {
            if (this.get(right)) {
                rightList.push(right);
            }
        }, this);

        return rightList;
    },

    statics: {
        rightsEqual: function (rightModel1, rightModel2) {
            if (!rightModel1 || !rightModel2) {
                return false;
            }

            return rightModel1.get('create') === rightModel2.get('create') &&
                rightModel1.get('read') === rightModel2.get('read') &&
                rightModel1.get('update') === rightModel2.get('update') &&
                rightModel1.get('delete') === rightModel2.get('delete');
        }
    }
});
