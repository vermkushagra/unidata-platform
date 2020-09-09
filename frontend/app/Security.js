/**
 * Класс отвечающий за безопасность (проверка прав и т.д. и т.п.)
 *
 * @author Ivan Marshalkin
 * @date 2017-07-20
 */

Ext.define('Unidata.Security', {
    singleton: true,

    /**
     * Тип ресурсов безопасности (security resources)
     */
    category: {
        SYSTEM: 'SYSTEM',
        CLASSIFIER: 'CLASSIFIER',
        META_MODEL: 'META_MODEL'
    },

    operation: {
        'create': 'create',
        'read': 'read',
        'update': 'update',
        'delete': 'delete'
    },

    /**
     * Возвращает право пользователя по имени ресурса
     *
     * @param resource имя ресурска
     *
     * @returns {boolean}
     */
    getRightByResourceName: function (resource) {
        var result = null,
            rights = Unidata.Config.getRole();

        if (Ext.isArray(rights)) {
            Ext.Array.each(rights, function (right) {
                if (right && right.securedResource && right.securedResource.name === resource) {
                    result = right;
                }
            });
        }

        return result;
    },

    /**
     * Возвращает все права пользователя по категории
     *
     * @param category - имя категории
     *
     * @returns {Array}
     */
    getRightsByCategoty: function (category) {
        var result = [],
            rights = Unidata.Config.getRole();

        if (Ext.isArray(rights)) {
            Ext.Array.each(rights, function (right) {
                if (right && right.securedResource && right.securedResource.category === category) {
                    result.push(right);
                }
            });
        }

        return result;
    },

    /**
     * Возвращает истину если операция разрешается правом
     *
     * @param right - право из роли
     * @param operation - (create/read/update/delete)
     *
     * @returns {boolean}
     */
    rightAllowOperation: function (right, operation) {
        var allowed = false;

        if (Unidata.Config.isUserAdmin()) {
            return true;
        }

        if (right && right[operation] === true) {
            allowed = true;
        }

        return allowed;
    },

    /**
     * Возвращает истину если операции разрешены правом
     *
     * @param right - право из роли
     * @param operations - массив операций (create/read/update/delete)
     *
     * @returns {boolean}
     */
    rightAllowOperations: function (right, operations) {
        var allowed = false,
            allowMap = [];

        allowMap = Ext.Array.map(operations, function (operation) {
            return Unidata.Security.rightAllowOperation(right, operation);
        });

        allowed = Ext.Array.every(allowMap, function (item) {
            return item === true;
        });

        return allowed;
    },

    /**
     * Возвращает истину если хотя бы одна операция разрешена правом
     *
     * @param right - право
     * @param operations - массив операций (create/read/update/delete)
     *
     * @returns {boolean}
     */
    rightAllowAnyOperations: function (right, operations) {
        var allowed = false,
            allowMap = [];

        allowMap = Ext.Array.map(operations, function (operation) {
            return Unidata.Security.rightAllowOperation(right, operation);
        });

        allowed = Ext.Array.some(allowMap, function (item) {
            return item === true;
        });

        return allowed;
    },

    /**
     * Возвращает истину если пользователь имеет хотя бы одно право с указаной категорией
     *
     * @param category - имя категории
     *
     * @returns {boolean}
     */
    userHasAnyCategoryRights: function (category) {
        var rights = Unidata.Security.getRightsByCategoty(category),
            allowMap = [],
            allowed = false;

        if (Unidata.Config.isUserAdmin()) {
            return true;
        }

        allowMap = Ext.Array.map(rights, function (right) {
            var result;

            result = Unidata.Security.rightAllowAnyOperations(right, [
                Unidata.Security.operation.create,
                Unidata.Security.operation.read,
                Unidata.Security.operation.update,
                Unidata.Security.operation.delete
            ]);

            return result;
        });

        allowed = Ext.Array.some(allowMap, function (item) {
            return item === true;
        });

        return allowed;
    },

    /**
     * Возвращает истину если пользователь имеет хотя бы одно системное право
     *
     * @returns {boolean}
     */
    userHasAnySystemRights: function () {
        return Unidata.Security.userHasAnyCategoryRights(Unidata.Security.category.SYSTEM);
    },

    /**
     * Возвращает истину если пользователь имеет хотя бы одно право на реестр / справочник
     *
     * @returns {boolean}
     */
    userHasAnyMetaModelRights: function () {
        return Unidata.Security.userHasAnyCategoryRights(Unidata.Security.category.META_MODEL);
    }

    /**
     * Возвращает истину если пользователь имеет хотя бы одно право на классификатор
     *
     * @returns {boolean}
     */
    // на данный момент интерпритация прав для админа и пользователя для прав на классификаторы разная
    // поэтому пользователи видят полный список классификаторов независимо от наличия прав
    // поэтому данный метод пока не нужен
    // userHasAnyClassifierRights: function () {
    //     return Unidata.Security.userHasAnyCategoryRights(Unidata.Security.category.CLASSIFIER);
    // }
});
