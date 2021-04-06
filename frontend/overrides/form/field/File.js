/**
 * Добавлена влидация размера файла
 *
 * @author Aleksandr Bavin
 * @date 2017-04-03
 */
Ext.define('Unidata.overrides.form.field.File', {
    override: 'Ext.form.field.File',

    config: {
        minSize: null,
        maxSize: null,
        sizeUnit: 1024 * 1024 // MB
    },

    inheritableStatics: {
        sizeUnits: {
            KB: 1024,
            MB: 1024 * 1024,
            GB: 1024 * 1024 * 1024,
            TB: 1024 * 1024 * 1024 * 1024
        }
    },

    /**
     * Размер файла в байтах
     */
    getFileSize: function () {
        var fileInputEl = this.fileInputEl;

        if (!fileInputEl) {
            return 0;
        }

        if (!fileInputEl.dom.files) {
            //TODO: error
            return 0;
        }

        if (!fileInputEl.dom.files[0]) {
            return 0;
        }

        return fileInputEl.dom.files[0].size;
    },

    getFileSizeInUnits: function () {
        return this.getFileSize() / this.getSizeUnit();
    },

    getSizeUnitText: function () {
        var sizeUnits = this.self.sizeUnits;

        switch (this.getSizeUnit()) {
            case sizeUnits.KB:
                return Unidata.i18n.t('common:kb');
                break;
            case sizeUnits.MB:
                return Unidata.i18n.t('common:mb');
                break;
            case sizeUnits.GB:
                return Unidata.i18n.t('common:gb');
                break;
            case sizeUnits.TB:
                return Unidata.i18n.t('common:tb');
                break;
        }

        return 'Error';
    },

    validateValue: function () {
        var valid = this.callParent(arguments),
            msg;

        if (!valid) {
            return valid;
        }

        if (msg = this.checkSizeMsg()) {
            this.setActiveError(msg);

            return false;
        }

        return valid;
    },

    checkSizeMsg: function () {
        var minSize = this.getMinSize(),
            maxSize = this.getMaxSize(),
            fileSize = this.getFileSizeInUnits(),
            msg;

        if (maxSize !== null && fileSize > maxSize) {
            msg = Unidata.i18n.t('other>maxFileSize', {max: maxSize, current: this.getSizeUnitText()});
        }

        if (minSize !== null && fileSize < minSize) {
            msg = Unidata.i18n.t('other>minFileSize', {min: minSize, current: this.getSizeUnitText()});
        }

        return msg;
    },

    getValue: function () {
        return 'sadfsadf';
    },

    /**
     * Убираем c:\fakepath и подобные оставляем для отображения только имя файла
     *
     * @param value
     */
    setRawValue: function (value) {
        var newValue,
            lastForwardSlash = value.lastIndexOf('\/');

        /* if this is a Windows-based path or just a file name
         * (Windows-based paths and file names in general don't contain forward slashes)
         */
        if (lastForwardSlash < 0) {
            /* remove the characters before the last backslash (included)
             * but only for Windows users -- as UNIX-like file names may contain backslashes
             */
            if (Ext.isWindows === true) {
                newValue = value.substring(value.lastIndexOf('\\') + 1);
            } else {
                newValue = value;
            }
        }
        // there is a forward slash: this is a UNIX-like (Linux, MacOS) path
        else {
            // remove the characters before the last forward slash (included)
            newValue = value.substring(lastForwardSlash + 1);
        }

        this.superclass.setRawValue.call(this, newValue);
    }

});
