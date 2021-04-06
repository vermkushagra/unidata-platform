/**
 * Утилитный класс построения системных эталона
 *
 * @author Sergey Shishigin
 * @date 2016-03-29
 */

Ext.define('Unidata.util.EtalonSystemAttributeEntity', {

    extend: 'Unidata.view.util.AbstractSystemAttributeEntity',

    singleton: true,

    buildMetaAttributesCfg: function () {
        var metaAttrCfg;

        metaAttrCfg = {
            simpleAttributes: [
                {
                    name: 'etalonId',
                    order: 0
                },
                {
                    name: 'timeInterval',
                    order: 1
                }
            ],
            nestedSimpleAttributes: [
                {
                    name: 'created',
                    order: 3,
                    displayable: true,
                    mainDisplayable: true
                },
                {
                    name: 'updated',
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

    buildDataAttributesCfg: function (dataRecord) {
        var dataAttrCfg,
            data;

        data = this.prepareData(dataRecord);

        dataAttrCfg = {
            simpleAttributes: [
                {
                    name: 'etalonId',
                    value: data.etalonId
                },
                {
                    name: 'timeInterval',
                    value: data.timeInterval
                }
            ],
            nestedSimpleAttributes: [
                {
                    name: 'created',
                    value: data.created,
                    displayable: true,
                    mainDisplayable: true
                },
                {
                    name: 'updated',
                    value: data.updated
                },
                {
                    name: 'status',
                    value: data.status
                }
            ]
        };

        return dataAttrCfg;
    },

    prepareData: function (dataRecord) {
        var data;

        data = {
            etalonId: dataRecord.get('etalonId'),
            timeInterval: this.buildTimeIntervalValue(dataRecord),
            created: this.buildDateValue(dataRecord.get('createDate'), dataRecord.get('createdBy')),
            updated: this.buildDateValue(dataRecord.get('updateDate'), dataRecord.get('updatedBy')),
            status: this.buildStatusValue(dataRecord.get('status'))
        };

        Ext.Object.each(data, function (key, value) {
            if (!value) {
                data[key] = '—';
            }
        });

        return data;
    }
});
