/**
 * @author Aleksandr Bavin
 * @date 2017-10-12
 */
Ext.define('Ext.overrides.dd.DragDrop', {

    override: 'Ext.dd.DragDrop',

    /**
     * isTarget определяется из конфига, только если значение явно указано
     */
    applyConfig: function () {

        // configurable properties:
        //    padding, isTarget, maintainOffset, primaryButtonOnly
        this.padding           = this.config.padding || [0, 0, 0, 0];

        if (this.config.isTarget !== undefined) {
            this.isTarget = this.config.isTarget;
        }
        this.maintainOffset    = (this.config.maintainOffset);
        this.primaryButtonOnly = (this.config.primaryButtonOnly !== false);
    }

});
