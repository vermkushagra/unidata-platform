/**
 * Отдельная модель для treestore,
 * т.к. при использовании модели в treestore, её наполняет лишним мусором
 * @author Aleksandr Bavin
 * @date 2016-09-13
 */
Ext.define('Unidata.model.user.SecuredResourceTree', {
    extend: 'Unidata.model.user.SecuredResource'
});
