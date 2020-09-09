/**
 * Поддержка toWayBinding для значений из config любого класса
 *
 * @author Aleksandr Bavin
 * @date 2018-02-14
 */
Ext.define('Ext.overrides.app.bind.Stub', {

    override: 'Ext.app.bind.Stub',

    set: function (value) {
        if (!this.callConfigSetter(value)) {
            this.callParent(arguments);
        }
    },

    getChildValue: function (parentData) {
        var me = this,
            name = me.name,
            ret,
            configurator,
            config;

        if (!parentData && !Ext.isString(parentData)) {
            // since these forms of falsey values (0, false, etc.) are not things we
            // can index into, this child stub must be null.
            ret = me.hadValue ? null : undefined;
        } else {
            ret = me.inspectValue(parentData);

            if (!ret) {
                if (parentData.isEntity) {
                    // If we get here, we know it's not an association
                    ret = parentData.data[name];
                } else {
                    if (parentData.isConfigBindMixin) {
                        configurator = parentData.getConfigurator();
                        config = configurator.configs[this.name];

                        if (config) {
                            return parentData[config.names.get]();
                            // return parentData[config.names.internal];
                        }
                    }

                    ret = parentData[name];
                }
            }
        }

        return ret;
    },

    getDataObject: function () {
        var me = this,
            parentData = me.parent.getDataObject(), // RootStub does not get here
            name = me.name,
            ret = parentData ? me.getChildValue(parentData) : null; // <= изменения тут, корректное получение значения

        if (!ret || !(ret.$className || Ext.isObject(ret))) {
            if (ret) {
                //TODO - we probably need to schedule ourselves here
            }
            parentData[name] = ret = {};
            // We're implicitly setting a value on the object here
            me.hadValue = me.owner.hadValue[me.path] = true;
            // If we're creating the parent data object, invalidate the dirty
            // flag on our children.
            me.invalidate(true, true);
        }

        return ret;
    },

    /**
     * Устанавливает значение, если оно есть в конфиге класса
     *
     * @param value
     */
    callConfigSetter: function (value) {
        var parent = this.parent,
            parentData = parent ? parent.getDataObject() : undefined,
            /** @type {Ext.Configurator} **/
            configurator,
            /** @type {Ext.Config} **/
            config;

        if (parentData && parentData.isConfigBindMixin) {
            configurator = parentData.getConfigurator();
            config = configurator.configs[this.name];

            if (config) {
                parentData[config.names.set](value);
                // parentData[config.names.internal] = value;

                // this.inspectValue(parentData);
                // this.invalidate(true);

                return true;
            }
        }

        return false;
    },

    updateComponentStub: function (parentData) {
        var name = this.name,
            current = this.boundComponentValue,
            boundComponentValue = null,
            isConfigBindMixin;

        if (parentData) {
            boundComponentValue = parentData[name];
        }

        isConfigBindMixin = (boundComponentValue && boundComponentValue.isConfigBindMixin);

        if (current !== boundComponentValue && isConfigBindMixin) {
            if (current) {
                current.removeStub(this);
            }

            if (boundComponentValue) {
                boundComponentValue.addStub(this);
            }

            this.boundComponentValue = boundComponentValue;
        }

        return isConfigBindMixin;
    },

    updateComponentConfigStub: function (parentData) {
        var current = this.boundComponentValue,
            boundComponentValue = null,
            isConfigBindMixin;

        if (parentData) {
            boundComponentValue = parentData;
        }

        isConfigBindMixin = (boundComponentValue && boundComponentValue.isConfigBindMixin);

        if (current !== boundComponentValue && isConfigBindMixin) {
            if (current) {
                current.removeConfigStub(this);
            }

            if (boundComponentValue) {
                boundComponentValue.addConfigStub(this);
            }

            this.boundComponentValue = boundComponentValue;
        }

        return isConfigBindMixin;
    },

    privates: {
        inspectValue: function (parentData) {
            if (this.updateComponentStub(parentData) || this.updateComponentConfigStub(parentData)) {
                return;
            }

            return this.callParent(arguments);
        }
    }

});
