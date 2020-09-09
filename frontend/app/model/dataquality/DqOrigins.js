Ext.define('Unidata.model.dataquality.DqOrigins', {
    extend: 'Unidata.model.Base',

    idProperty: 'tempId',

    fields: [
        {name: 'tempId', type: 'auto', persist: false},
        {
            name: 'all',
            type: 'boolean',
            defaultValue: false
        },
        {
            name: 'sourceSystems',
            type: 'auto'
        }
    ]

    // Пока оставляю закомментированным. Не понятно почему реализовано было такое решение
    // Простой массив в поле sourceSystems видится более удобным (см. выше)
    // Возможно сломается старый экран dq, но он нам в целом и не нужен.
    // После реализации UN-7033 данный коммент надо удалить.
    // manyToMany: {
    //     sourceSystems: {
    //         type: 'sourcesystem.SourceSystem',
    //         role: 'sourceSystems',
    //         field: 'name',
    //         right: true
    //     }
    // }

});
