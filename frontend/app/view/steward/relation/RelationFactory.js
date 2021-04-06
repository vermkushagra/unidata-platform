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
     * @param cfg
     * @returns {*}
     */
    buildRelationPanels: function (relType, cfg) {
        var metaRecord = cfg.metaRecord,
            dataRecord = cfg.dataRecord,
            operationId = cfg.operationId,
            drafts = cfg.drafts,
            readOnly = cfg.readOnly,
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
            var panel = Ext.create(className, {
                title: metaRelation.get('displayName'),
                drafts: drafts,
                operationId: operationId,
                metaRecord: metaRecord,
                dataRecord: dataRecord,
                metaRelation: metaRelation,
                relationName: metaRelation.get('name'), // QA использует имя связи для поиска
                readOnly: readOnly
            });

            panels.push(panel);
        });

        return panels;
    }
});
