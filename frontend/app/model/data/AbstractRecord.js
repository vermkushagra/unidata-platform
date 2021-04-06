Ext.define('Unidata.model.data.AbstractRecord', {
    extend: 'Unidata.model.Base',

    fields: [
        {
            name: 'entityName',
            type: 'string',
            allowNull: true
        },
        {
            name: 'version',
            type: 'string',
            allowNull: true
        },
        {
            name: 'modified',
            type: 'boolean',
            defaultValue: true
        },
        {
            name: 'duplicateIds',
            persist: false
        },
        {
            name: 'validFrom',
            type: 'datetimeintervalfrom',
            allowNull: true
        },
        {
            name: 'validTo',
            type: 'datetimeintervalto',
            allowNull: true
        },
        {
            name: 'createDate',
            type: 'date',
            dateFormat: 'c',
            persist: false,
            allowNull: true
        },
        {
            name: 'updateDate',
            type: 'date',
            dateFormat: 'c',
            persist: false,
            allowNull: true
        },
        {
            name: 'createdBy',
            type: 'string',
            persist: false
        },
        {
            name: 'updatedBy',
            type: 'string',
            persist: false
        },
        {
            name: 'status',
            type: 'string',
            allowNull: true
        },
        {
            name: 'approval',
            type: 'string',
            allowNull: true
        }
    ],

    constructor: function () {
        // Временно исключили передачу record в validator
        // т.к. валидация границ сейчас осуществляется в компоненте TimeInterval
        //this.validators.validFrom[0]['record'] = this;
        //this.validators.validTo[0]['record'] = this;
        this.callParent(arguments);
    }

    // Временно исключили validators проверки границ периода,
    // т.к. валидация границ сейчас осуществляется в компоненте TimeInterval
    //validators: {
    //    validFrom: [
    //        {
    //            type: 'timeinterval',
    //            message: 'Некорректные границы периода'
    //        }
    //    ],
    //    validTo: [
    //        {
    //            type: 'timeinterval',
    //            message: 'Некорректные границы периода'
    //        }
    //    ]
    //}
});
//TODO: SS rename record to etalon ?
