Ext.define('Unidata.util.MetaDependencyGraph', {
    singleton: true,

    /**
     * Найти по имени все vertexes типа searchType в metaDependencyGraph
     *
     * @param metaDependencyGraph {Unidata.model.entity.metadependency.MetaDependencyGraph} Граф мета-зависимостей
     * @param searchName
     * @param searchType
     *
     * @returns {Unidata.model.entity.metadependency.Vertex[]|[]}
     */
    findVertexes: function (metaDependencyGraph, searchName, searchType) {
        var vertexes = metaDependencyGraph.vertexes().getRange(),
            filteredVertexes;

        filteredVertexes = Ext.Array.filter(vertexes, function (vertex) {
            var id = vertex.get('id'),
                type = vertex.get('type');

            return id  === searchName && (type === searchType);
        });

        return filteredVertexes;
    },

    /**
     * Найти в графе зависимостей вершины, отражающие справочника, находящиеся в отношении "Ссылка "для заданного реестра/справочника
     * @param metaDependencyGraph {Unidata.model.entity.metadependency.MetaDependencyGraph} Граф мета-зависимостей
     * @param id {String} ID вертекса
     * @param direction {String} Направление связи INBOUND|OUTBOUND
     * @param fromVertexTypes {String[]} Массив типов вершин
     * @param toVertexTypes {String[]} Массив типов вершин
     * @return {Unidata.model.entity.metadependency.Vertex[]}
     */
    findVertexesRelatedToId: function (metaDependencyGraph, id, direction, fromVertexTypes, toVertexTypes) {
        var EdgeDirection = Unidata.view.admin.security.role.RoleEdit.EDGE_DIRECTION,
            edges = metaDependencyGraph.edges().getRange(),
            edgesByLookup,
            vertexesRelated;

        if (direction !== EdgeDirection.INBOUND && direction !== EdgeDirection.OUTBOUND) {
            throw new Error('Edge direction is not defined');
        }

        edgesByLookup = Ext.Array.filter(edges, function (edge) {
            var fromVertex     = edge.getFrom(),
                toVertex       = edge.getTo(),
                fromVertexType = fromVertex.get('type'),
                toVertexType   = toVertex.get('type'),
                toVertexId     = toVertex.get('id'),
                fromVertexId   = fromVertex.get('id'),
                typesCondition,
                idCondition;

            typesCondition = Ext.Array.contains(fromVertexTypes, fromVertexType) && Ext.Array.contains(toVertexTypes, toVertexType);

            if (direction === EdgeDirection.INBOUND) {
                idCondition = toVertexId === id;
            } else {
                idCondition = fromVertexId === id;
            }

            return idCondition && typesCondition;
        }, this);

        vertexesRelated = Ext.Array.map(edgesByLookup, function (edge) {
            return direction === EdgeDirection.INBOUND ? edge.getFrom() : edge.getTo();
        });

        return vertexesRelated;
    }
});
