/**
 * Поле для выбора порта dq правила
 *
 * @author Sergey Shishigin
 * @date 2018-02-12
 */
Ext.define('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortUPathField', {
    extend: 'Unidata.view.component.EntityAtrributeHtmlComboBox',

    alias: 'widget.admin.entity.metarecord.dq.port.dqruleportupathfield',

    cls: 'un-entityattributehtmlcombo un-htmlcombo un-dq-rule-port-upath',

    labelAlign: 'top',
    editable: false,

    requires: ['Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortEditorWindow'],

    config: {
        metaRecord: null,
        dataType: null,
        upathValue: null,
        uPath: null,
        portApplicationMode: null,
        portType: null,
        useFilter: null,
        executionContext: null,
        executionContextMode: null,
        executionContextPath: null,
        supportedExecutionContexts: null
    },

    triggers: {
        edit: {
            cls: 'un-form-edit2-trigger'
            // handler: this.onEditPort.bind(this)
        }
    },

    clearValue: function (silent) {
        this.callParent(arguments);

        silent = Ext.isBoolean(silent) ? silent : false;

        if (silent) {
            this.suspendEvent('upathvaluechange');
        }
        this.clearPortUPathInput();

        if (silent) {
            this.resumeEvent('upathvaluechange');
        }
    },

    initComponent: function () {
        this.callParent(arguments);

        if (this.triggers.edit) {
            this.triggers.edit.handler = this.onEditPort.bind(this);
        }
    },

    onEditPort: function (self) {
        var upathValue = self.getUpathValue(),
            metaRecord = this.getMetaRecord(),
            dataType = this.getDataType(),
            portApplicationMode = this.getPortApplicationMode(),
            useFilter = this.getUseFilter(),
            executionContextMode = this.getExecutionContextMode(),
            executionContext = this.getExecutionContext(),
            supportedExecutionContexts = this.getSupportedExecutionContexts(),
            portType = this.getPortType();

        this.showPortEditorWindow(metaRecord, upathValue, dataType, portApplicationMode, useFilter, executionContext, supportedExecutionContexts, executionContextMode, portType);
    },

    clearPortUPathInput: function () {
        this.setUpathValue(null);
    },

    setReadOnly: function (readOnly) {
        this.callParent(arguments);

        this.setHideTrigger(Boolean(readOnly));
    },

    /**
     * Отобразить окно редактирования порта
     * @param metaRecord {Unidata.model.entity.Entity|Unidata.model.entity.LookupEntity}
     * @param upathValue {String}
     * @param dataType {String}
     */
    showPortEditorWindow: function (metaRecord, upathValue, dataType, portApplicationMode, useFilter, executionContext, supportedExecutionContexts, executionContextMode, portType) {
        var window,
            cfg;

        cfg = {
            width: 1000,
            height: 620,
            modal: true,
            metaRecord: metaRecord,
            upathValue: upathValue,
            portApplicationMode: portApplicationMode,
            useFilter: useFilter,
            executionContext: executionContext,
            executionContextMode: executionContextMode,
            supportedExecutionContexts: supportedExecutionContexts,
            dataType: dataType,
            portType: portType,
            listeners: {
                okbtnclick: 'onShowPortEditorWindowOkButtonClick',
                scope: this
            }
        };

        window = Ext.create('Unidata.view.admin.entity.metarecord.dq.dqrule.port.DqRulePortEditorWindow', cfg);

        window.show();
    },

    /**
     *
     * @param self
     * @param upathValue {String}
     * @param uPath
     * @param canonicalPath
     */
    onShowPortEditorWindowOkButtonClick: function (self, upathValue, uPath, executionContext) {
        var canonicalPath = uPath.toCanonicalPath();

        this.setUpathValue(upathValue);
        this.setValue(canonicalPath);
        this.setExecutionContext(executionContext);
        this.updateLayout();
    },

    getUPath: function () {
        var UPath,
            metaRecord;

        UPath = this.callParent(arguments);

        if (!UPath) {
            metaRecord = this.getMetaRecord();
            UPath    = Ext.create('Unidata.util.upath.UPath', {
                entity: metaRecord
            });
            this.setUPath(UPath);
        }

        return UPath;
    },

    updateUpathValue: function (uPathValue) {
        var pathTokens,
            canonicalPath,
            uPath = this.getUPath(),
            oldUPathValue;

        oldUPathValue = uPath.toUPath();
        pathTokens = uPath.fromUPath(uPathValue);

        if (pathTokens) {
            canonicalPath = uPath.toCanonicalPath();
        }

        if (uPathValue) {
            uPath.fromUPath(uPathValue);
        } else {
            this.setUPath(null);
        }

        this.fireEvent('upathvaluechange', this, uPathValue, oldUPathValue);

        if (this.rendered) {
            this.setValue(canonicalPath);
        } else {
            this.on('render', function () {
                this.setValue(canonicalPath);
            }, this);
        }
    },

    updateExecutionContext: function (executionContext, oldExecutionContext) {
        this.fireEvent('executioncontextchange', this, executionContext, oldExecutionContext);
    },

    /**
     * Переопределение метода  Ext.form.field.ComboBox.getDisplayValue
     * Добавляем информацию о фильтрации в токен
     *
     * @private
     * Generates the string value to be displayed in the text field for the currently stored value
     * @return {*}
     */
    getDisplayValue: function () {
        var pathTokens,
            displayUPath;

        if (this.displayTplData.length === 1) {
            pathTokens = this.displayTplData[0].pathTokens;
            displayUPath = this.buildDisplayUPath(pathTokens);
        }

        if (displayUPath) {
            this.displayTplData[0].displayName = displayUPath;
        }

        return this.callParent(arguments);
    },

    /**
     * Построить человекочитаемый upath
     *
     * @param pathTokens
     * @return {*}
     */
    buildDisplayUPath: function (pathTokens) {
        var me = this,
            uPath,
            upathElements,
            displayUPath = null,
            delimiter;

        uPath = this.getUPath();
        delimiter = this.getDelimiter();

        if (uPath) {
            upathElements = uPath.getElements();
        }

        if (Ext.isArray(pathTokens)) {
            if (uPath && upathElements.length === pathTokens.length) {
                pathTokens = Ext.Array.map(pathTokens, this.mapPathTokenToFilteredPathToken.bind(this, upathElements) , this);
            }

            if (upathElements.length === 1 && upathElements[0].path === Unidata.util.upath.UPath.fullRecordPath) {
                pathTokens[0] = Ext.String.format('<span class="un-htmlcombo-treeItem-icon icon-folder"></span>{0}', pathTokens[0]);
            }
            pathTokens = Ext.Array.map(pathTokens, me.wrapHtmlComboItem, this);
            displayUPath = pathTokens.join(delimiter);
        }

        return displayUPath;
    },

    wrapHtmlComboItem: function (itemHtml) {
        return Ext.String.format('<span class="un-htmlcombo-treeItem">{0}</span>', itemHtml);
    },

    /**
     * Преобразовать простой pathToken в pathToken с фильтрацией
     *
     * @param upathElements {Unidata.util.UPathElement[]}
     * @param pathToken {String}
     * @param @optional index {Number}
     * @return {String}
     */
    mapPathTokenToFilteredPathToken: function (upathElements, pathToken, index) {
        var UPathElementTypeUtil = Unidata.util.upath.UPathElement.UPathElementType,
            upathElement,
            expressionPredicateDisplayName,
            expressionPredicateValue,
            keyPath,
            foundIndex,
            store = this.getStore(),
            filterCls = 'un-htmlcombo-treeItem-filter',
            filterValueCls = filterCls + '-value';

        if (Ext.isArray(upathElements)) {
            upathElement = upathElements[index];
        } else {
            upathElement = upathElements;
        }

        switch (upathElement.type) {
            case UPathElementTypeUtil.SUBSCRIPT:
                pathToken = Ext.String.htmlEncode(pathToken);
                pathToken = Ext.String.format('{0} [<span class="{1}">{2}</span>]', pathToken, filterValueCls, upathElement.predicate);
                break;
            case UPathElementTypeUtil.EXPRESSION:
                expressionPredicateDisplayName = upathElement.predicate.property;

                if (upathElement.path === Unidata.util.upath.UPath.fullRecordPath) {
                    keyPath = upathElement.predicate.property;
                } else {
                    keyPath = Ext.String.format('{0}.{1}', upathElement.path, upathElement.predicate.property);
                }
                foundIndex = store.findExact('path', keyPath);

                if (foundIndex > -1) {
                    expressionPredicateDisplayName = store.getAt(foundIndex).get('displayNameSimple');
                }
                expressionPredicateValue = upathElement.predicate.value;

                pathToken = Ext.String.htmlEncode(pathToken);
                expressionPredicateDisplayName = Ext.String.htmlEncode(expressionPredicateDisplayName);
                expressionPredicateValue = Ext.String.htmlEncode(expressionPredicateValue);

                pathToken = Ext.String.format('{0} \{<span class="{1}">{2}:<span class="{3}">{4}</span></span>\}', pathToken, filterCls, expressionPredicateDisplayName, filterValueCls, expressionPredicateValue);
                break;
        }

        return pathToken;
    }
});
