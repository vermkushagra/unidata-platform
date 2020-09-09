/**
 * Фабрика порождения панелей для связей
 *
 * @author Sergey Shishigin
 * @date 2016-04-10
 */

Ext.define('Unidata.view.steward.relation.RelationFactory', {
    singleton: true,

    /**
     *
     * @param relType Тип связи
     * @param metaRecord
     * @param customCfg
     * @returns {*}
     */
    buildRelationPanels: function (relType, metaRecord, customCfg) {
        var panel,
            classNamePrefix = 'Unidata.view.steward.relation',
            relationGroup,
            relationNamesOrdered = [],
            className,
            ApiClass,
            panels = [],
            metaRelations,
            sorters;

        if (relType === 'ManyToMany') {
            className = classNamePrefix + '.' + 'm2m.M2m';
            ApiClass = Unidata.util.api.RelationM2m;
        } else if (relType === 'Contains') {
            className = classNamePrefix + '.' + 'contains.Contains';
            ApiClass = Unidata.util.api.RelationContains;
        } else {
            return panels;
        }

        metaRelations = ApiClass.getMetaRelations(metaRecord);
        relationGroup = metaRecord.getRelationGroupByType(relType);

        if (relationGroup) {
            // если определен порядок групп связей, то сортируем связи в соответствии с этим порядком
            relationNamesOrdered = relationGroup.get('relations');
            sorters = {
                sorterFn: Unidata.util.Sorter.byListSorterFn.bind(this, relationNamesOrdered, 'name', 'displayName'),
                direction: 'ASC'
            };
        } else {
            // по умолчанию сортируем связи по алфавиту
            sorters = {
                property: 'displayName',
                direction: 'ASC'
            };
        }
        metaRelations.setSorters(sorters);

        metaRelations.each(function (metaRelation) {
            var cfg  = {
                metaRecord: metaRecord,
                title: metaRelation.get('displayName'),
                metaRelation: metaRelation,
                relationName: metaRelation.get('name') // QA использует имя связи для поиска
            };

            cfg = Ext.apply(cfg, customCfg);
            panel = Ext.create(className, cfg);

            panels.push(panel);
        });

        return panels;
    }
});
