/**
 * Константы видов отображения связей
 *
 * @author Ivan Marshalkin
 * @date 2017-05-11
 */

Ext.define('Unidata.view.steward.relation.RelationViewType', {

    singleton: true,

    m2m: {
        TABLE: 'table',                                     // режим отображения табличным списком
        CAROUSEL: 'carousel'                                // режим отображения в карусельке
    },

    contains: {
        TABLE: 'table',                                     // режим отображения табличным списком
        CAROUSEL: 'carousel'                                // режим отображения в карусельке
    }
});
