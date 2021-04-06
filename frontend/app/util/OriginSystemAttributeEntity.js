/**
 * Утилитный класс построения системных атрибутов origina
 *
 * @author Sergey Shishigin
 * @date 2016-03-29
 */

Ext.define('Unidata.util.OriginSystemAttributeEntity', {

    extend: 'Unidata.view.util.AbstractSystemAttributeEntity',

    singleton: true,

    buildMetaAttributesCfg: function () {
        var metaAttrCfg;

        metaAttrCfg = {
            simpleAttributes: [
                {
                    name: 'sourceSystem',
                    order: 0
                },
                {
                    name: 'timeInterval',
                    order: 1
                }
            ],
            nestedSimpleAttributes: [
                {
                    name: 'originId',
                    order: 0,
                    displayable: true,
                    mainDisplayable: true
                },
                {
                    name: 'externalId',
                    order: 1
                },
                {
                    name: 'revision',
                    order: 2
                },
                {
                    name: 'updated',
                    order: 3
                },
                {
                    name: 'created',
                    order: 4
                },
                {
                    name: 'status',
                    order: 5
                }
            ]
        };

        return metaAttrCfg;
    },

    buildDataAttributesCfg: function (originRecord) {
        var dataAttrCfg,
            data;

        data = this.prepareData(originRecord);

        dataAttrCfg = {
            simpleAttributes: [
                {
                    name: 'sourceSystem',
                    value: data.sourceSystem
                },
                {
                    name: 'timeInterval',
                    value: data.timeInterval
                }
            ],
            nestedSimpleAttributes: [
                {
                    name: 'originId',
                    value: data.originId
                },
                {
                    name: 'externalId',
                    value: data.externalId
                },
                {
                    name: 'revision',
                    value: data.revision
                },
                {
                    name: 'updated',
                    value: data.updated
                },
                {
                    name: 'created',
                    value: data.created
                },
                {
                    name: 'status',
                    value: data.status
                }
            ]
        };

        return dataAttrCfg;
    },

    prepareData: function (originRecord) {
        var data;

        data = {
            sourceSystem: originRecord.get('sourceSystem'),
            timeInterval: this.buildTimeIntervalValue(originRecord),
            revision: originRecord.get('revision'),
            created: this.buildDateValue(originRecord.get('createDate'), originRecord.get('createdBy')),
            updated: this.buildDateValue(originRecord.get('updateDate'), originRecord.get('updatedBy')),
            status: this.buildStatusValue(originRecord.get('status')),
            originId: originRecord.get('originId'),
            externalId: originRecord.get('externalId')
        };

        return data;
    }
});
