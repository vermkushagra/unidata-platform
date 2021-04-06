/**
 * Класс, реализующий редактирование аттрибута типа blob
 *
 * @author Cyril Sevastyanov
 * @since 2016-02-15
 */

Ext.define('Unidata.view.steward.dataentity.attribute.BlobAttribute', {

    extend: 'Unidata.view.steward.dataentity.attribute.AbstractAttribute',

    statics: {
        TYPE: 'Blob'
    },

    extensions: [],

    config: {
        blobAttributeType: null,    // тип blob-атрибута
        imageWidth: null,           // ширина изображения
        imageHeight: null,          // высота изображения
        popupFullsize: false,       // раскрывать изображение fullsize?
        imageDisplayMode: null,     // режим представления изображения (inline|full)
        isRenderImgArea: false,     // рендерить img area?
        alwaysShowInput: true
    },

    contentContainer: null,

    /**
     * Обработать customProperties для метаатрибута
     */
    processCustomProperties: function () {
        var BlobAttribute = Unidata.constant.BlobAttribute,
            KeyValuePair = Unidata.util.KeyValuePair,
            metaAttribute = this.getMetaAttribute(),
            customProperties = metaAttribute.customProperties(),
            blobAttributeType,
            imageDisplayMode,
            imagePopupFullsize,
            imageWidth,
            imageHeight,
            isRenderImgArea,
            obj;

        obj = KeyValuePair.mapToObject(customProperties);

        imageDisplayMode   = obj[BlobAttribute.CUSTOM_PROPERTY_IMAGE_DISPLAY_MODE] || 'full';
        imagePopupFullsize = obj[BlobAttribute.CUSTOM_PROPERTY_IMAGE_POPUP_FULLSIZE] ? obj[BlobAttribute.CUSTOM_PROPERTY_IMAGE_POPUP_FULLSIZE] === 'true' : true;
        imageWidth         = obj[BlobAttribute.CUSTOM_PROPERTY_IMAGE_WIDTH] ? obj[BlobAttribute.CUSTOM_PROPERTY_IMAGE_WIDTH] : '100%';
        imageHeight        = obj[BlobAttribute.CUSTOM_PROPERTY_IMAGE_HEIGHT] ? obj[BlobAttribute.CUSTOM_PROPERTY_IMAGE_HEIGHT] : '';
        blobAttributeType  = obj[BlobAttribute.CUSTOM_PROPERTY_BLOB_TYPE];
        isRenderImgArea = blobAttributeType === BlobAttribute.CUSTOM_PROPERTY_BLOB_TYPE_IMAGE &&
                          imageDisplayMode === BlobAttribute.CUSTOM_PROPERTY_IMAGE_DISPLAY_MODE_FULL;

        this.setBlobAttributeType(blobAttributeType);
        this.setImageHeight(imageHeight);
        this.setImageWidth(imageWidth);
        this.setPopupFullsize(imagePopupFullsize);
        this.setImageDisplayMode(imageDisplayMode);
        this.setIsRenderImgArea(isRenderImgArea);
    },

    initInput: function (customCfg) {
        var BlobAttribute = Unidata.constant.BlobAttribute,
            blobAttributeType,
            isRenderImgArea,
            imageDisplayMode,
            fileNameOnClick = null,
            input,
            hideFileName = false,
            el = this.getEl(),
            customCfg,
            cfg;

        customCfg = customCfg || {};

        this.processCustomProperties();

        blobAttributeType = this.getBlobAttributeType();
        imageDisplayMode = this.getImageDisplayMode();
        isRenderImgArea = this.getIsRenderImgArea();

        // модифицируем атрибут, если blob используется для хранения изображений
        if (blobAttributeType === BlobAttribute.CUSTOM_PROPERTY_BLOB_TYPE_IMAGE) {
            if (imageDisplayMode === BlobAttribute.CUSTOM_PROPERTY_IMAGE_DISPLAY_MODE_FULL) {
                this.setHideAttributeTitle(true);
                this.setShowTooltip(false);
                hideFileName = true;
            } else if (imageDisplayMode === BlobAttribute.CUSTOM_PROPERTY_IMAGE_DISPLAY_MODE_INLINE) {
                fileNameOnClick = this.onImageClick.bind(this);
            }

            customCfg = Ext.apply(customCfg, {
                buttonConfig: {
                    text: '',
                    ui: 'un-toolbar-admin',
                    scale: 'small',
                    iconCls: 'icon-picture2',   // TODO: change icon depends on type
                    tooltip: Unidata.i18n.t('common:loadSomething', {name: Unidata.i18n.t('glossary:file')})
                }
            });
        }

        cfg = {
            xtype: 'fileuploaddownloadfield',
            name: this.getAttributePath(),
            baseUrl: Unidata.Config.getMainUrl(),
            dataType: this.metaAttribute.get('simpleDataType').toLowerCase(),
            token: Unidata.Config.getToken(),
            extensions: this.extensions,
            preventMark: this.getPreventMarkField(),
            msgTarget: this.elError.getId(),
            urlTpl: {
                // upload: 'internal/data/entities/{dataType}/{name}<tpl for="value"><tpl if="id">/{id}</tpl></tpl>',
                upload: 'internal/data/entities/{dataType}/{name}',
                download: 'internal/data/entities/{dataType}/<tpl for="value">{id}</tpl>?token={token}',
                delete: null
            },
            value: this.value,
            allowBlank: this.getMetaAttributeField('nullable'),
            fileNameOnClick: fileNameOnClick,
            hideFileName: hideFileName
        };

        cfg = Ext.apply(cfg, customCfg);

        input = Ext.widget(cfg);

        if (isRenderImgArea) {
            this.addCls(this.baseCls + '-type-blob-image-full');
            this.contentContainer = el.insertHtml('afterBegin', '<div class="un-dataentity-attribute-type-blob-content"></div>', true);
            this.buildImgHtmlElement(this.contentContainer, input);
            input.on('change', this.onFileUploadDownloadChange, this);
        }

        // показываем ошибку неправильного расширения
        input.on('extensionerror', function (input, hasError) {
            if (hasError) {
                this.elError.show();
                this.updateLayout();
            }
        }, this);

        return input;
    },

    /**
     * Построить img элемент для представления изображения
     * @param where Где разместить элемент
     * @param inputFileField Поле с файлом
     */
    buildImgHtmlElement: function (where, inputFileField) {
        var html,
            tpl,
            imageWidth = this.getImageWidth(),
            imageHeight = this.getImageHeight(),
            popupFullsize = this.getPopupFullsize(),
            fileName = inputFileField.getFileName(),
            placeholderUrl = 'resources/placeholder-image.png',
            url;

        tpl = '<img src="{0}"';
        imageWidth = imageWidth || '100%';

        if (imageWidth) {
            tpl += Ext.String.format(' width={0} ', imageWidth);
        }

        if (popupFullsize && fileName) {
            tpl += ' class="un-image-active" ';
        }

        if (imageHeight) {
            tpl += Ext.String.format(' height={0} ', imageHeight);
        }

        if (fileName) {
            tpl += Ext.String.format(' data-qtip={0} ', fileName);
        }

        tpl += '></img>';
        url = fileName ? inputFileField.getDownloadUrl() : placeholderUrl;
        html = Ext.String.format(tpl, url);
        where.setHtml(html);

        if (popupFullsize && fileName) {
            where.on('click', this.onImageClick, this);
        }
        this.fireEvent('layoutchanged', this);
    },

    onImageClick: function (e) {
        var cfg,
            fileName;

        fileName = this.input.getFileName();

        e.stopEvent();

        if (fileName) {
            cfg = {
                src: this.input.getDownloadUrl()
            };

            this.showPopupImageWindow(cfg);
        }
    },

    onFileUploadDownloadChange: function (field) {
        this.buildImgHtmlElement(this.contentContainer, field);
    },

    showPopupImageWindow: function (cfg) {
        var wnd;

        wnd = Ext.create('Unidata.view.component.image.ImageWindow', cfg);

        wnd.show();
    },

    getDataForSearch: function () {

        var value = this.input.getValue();

        if (!value) {
            return [];
        }

        return [
            value.fileName
        ];
    }

});
