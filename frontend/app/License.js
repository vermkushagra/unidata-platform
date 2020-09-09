Ext.define('Unidata.License', {

    singleton: true,

    config: {
        license: null
    },

    MODE: {
        DEVELOP: 'develop',
        PRODUCTION: 'production'
    },

    setLicense: function () {
        var result = this.callParent(arguments);

        this.updateWatermark();

        return result;
    },

    updateWatermark: function () {
        var demoOverlay = Ext.get('demo-overlay');

        if (!Ext.isEmpty(this.getLicense()) && this.getLicenseMode() !== this.MODE.PRODUCTION) {
            if (!demoOverlay) {
                demoOverlay = document.createElement('div');
                demoOverlay.setAttribute('id', 'demo-overlay');

                Ext.getBody().appendChild(demoOverlay);
            }
        } else {
            if (demoOverlay) {
                demoOverlay.destroy();
            }
        }
    },

    /**
     * Общий метод для получения значения
     *
     * @param name
     * @param defaultValue
     * @returns {*}
     */
    getLicenseValue: function (name, defaultValue) {
        var license = this.getLicense(),
            value = license ? license[name] : null;

        if (Ext.isEmpty(value)) {
            return defaultValue;
        }

        return value;
    },

    getLicenseExpirationDate: function () {
        return this.getLicenseValue('expirationDate', null);
    },

    getLicenseModeDisplayName: function () {
        return this.getLicenseValue('licenseModeDisplayName', null);
    },

    getLicenseVersion: function () {
        return this.getLicenseValue('version', null);
    },

    getLicenseModules: function () {
        return this.getLicenseValue('modules', []);
    },

    getLicenseOwner: function () {
        return this.getLicenseValue('owner', null);
    },

    /**
     * @see {Unidata.License.MODE}
     */
    getLicenseMode: function () {
        var license = this.getLicense(),
            mode = license ? license['licenseMode'] : null;

        if (mode !== this.MODE.PRODUCTION) {
            return this.MODE.DEVELOP;
        }

        return mode;
    }

});
