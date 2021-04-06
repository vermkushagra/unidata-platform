/**
 * Компонент для редактирования параметра типа "дата"
 *
 * @author Cyril Sevastyanov
 * @date 2016-04-06
 */
Ext.define('Unidata.view.admin.job.part.parameter.DateParameter', {

    extend: 'Unidata.view.admin.job.part.parameter.AbstractParameter',

    alias: 'widget.admin.job.param.date',

    requires: [
        'Unidata.view.component.DateTimeField'
    ],

    TYPE: 'DATE',

    initInput: function (name, value) {

        var format = Unidata.model.job.parameter.meta.DateMetaParameter.FORMAT;

        return Ext.widget({
            xtype: 'datetimefield',
            name: name,
            value: value,
            readFormat: format,
            writeFormat: format,
            msgTarget: 'under'
        });
    }

});
