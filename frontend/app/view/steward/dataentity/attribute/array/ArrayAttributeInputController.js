Ext.define('Unidata.view.steward.dataentity.attribute.array.ArrayAttributeInputController', {

    extend: 'Ext.app.ViewController',

    alias: 'controller.arrayattributeinput',

    init: function () {
        var valuesStore = this.getStore('values'),
            inputControls = this.lookupReference('inputControls'),
            inputControlsSwitch = this.lookupReference('inputControlsSwitch');

        valuesStore.on('load', this.onValuesStoreLoad, this);
    },

    toggleInputControls: function () {
        var view = this.getView(),
            pageSize = view.getPageSize();

        if (pageSize === 1) {
            this.expand();
        } else {
            this.collapse();
        }
    },

    expand: function () {
        this.getView().setPageSize(10);
    },

    collapse: function () {
        this.getView().setPageSize(1);
    },

    onValuesStoreLoad: function (store, records) {
        var view = this.getView(),
            arrayContainer = this.lookupReference('arrayContainer'),
            arrayContainerCount;

        view.suspendLayouts();

        // отрисовываем элементы массива
        Ext.Array.each(records, function (record, index) {
            this.setArrayContainerItemValue(index, record);
        }, this);

        arrayContainerCount = arrayContainer.items.getCount();

        // прячем лишние
        if (records && records.length < arrayContainerCount) {
            Ext.Array.each(arrayContainer.items.getRange(records.length), function (item) {
                item.hide();
            });
        }

        view.resumeLayouts(true);
    },

    getSimpleAttributeAt: function (index) {
        var view = this.getView(),
            arrayContainer = this.lookupReference('arrayContainer'),
            arrayContainerItem = arrayContainer.items.getAt(index),
            simpleAttribute = arrayContainerItem.items.getAt(0);

        return simpleAttribute;
    },

    setArrayContainerItemValue: function (index, record) {
        var view = this.getView(),
            arrayContainer = this.lookupReference('arrayContainer'),
            arrayContainerItem = arrayContainer.items.getAt(index),
            simpleAttribute;

        if (arrayContainerItem === undefined) {
            do {
                simpleAttribute = this.createSimpleAttribute(null);
                simpleAttribute.on('change', this.onSimpleAttributeChange, this);
                view.relayEvents(simpleAttribute, ['change', 'validitychange']);
                arrayContainerItem = arrayContainer.add(this.wrapSimpleAttribute(simpleAttribute));
            } while (arrayContainer.items.getAt(index) === undefined);
        }

        simpleAttribute = arrayContainerItem.items.getAt(0);
        simpleAttribute.relatedRecord = record;
        simpleAttribute.suspendEvent('change');
        simpleAttribute.setDisplayValue(record.get('displayValue'));
        simpleAttribute.setValue(record.get('value'));
        simpleAttribute.resumeEvent('change');
        arrayContainerItem.show();
    },

    onSimpleAttributeChange: function (simpleAttribute) {
        simpleAttribute.relatedRecord.set('value', simpleAttribute.getValue());
        simpleAttribute.relatedRecord.set('displayValue', simpleAttribute.getInputDisplayValue());
    },

    createSimpleAttribute: function (value) {
        var view = this.getView(),
            metaAttribute = view.getMetaAttribute(),
            metaAttributeData = metaAttribute.getData(),
            dataAttribute = view.getDataAttribute(),
            dataAttributeData = dataAttribute.getData(),
            metaAttributeClone,
            dataAttributeClone,
            simpleAttribute;

        dataAttributeData.value = value;

        dataAttributeClone = dataAttribute.self.create(dataAttributeData);

        // избавляемся от лишних проверок, всё проверяется ну уровне ArrayAttribute
        metaAttributeData.nullable = true;
        delete metaAttributeData.id;

        metaAttributeClone = metaAttribute.self.create(metaAttributeData);

        simpleAttribute = Ext.create('Unidata.view.steward.dataentity.attribute.SimpleAttribute', {
            flex: 1,
            renderData: {
                hideIndicator: true
            },
            hideAttributeTitle: true,
            inputVisible: true,
            metaRecord: view.getMetaRecord(),
            dataRecord: view.getDataRecord(),
            metaAttribute: metaAttributeClone,
            dataAttribute: dataAttributeClone,
            attributePath: null, // предположительно нам он не потребуется
            readOnly: view.getReadOnly(),
            disabled: view.getDisabled(),
            bind: {
                disabled: '{disabled}',
                readOnly: '{readOnly}'
            },
            preventMarkField: view.getPreventMarkField()
        });

        view.relayEvents(simpleAttribute, ['focus', 'blur']);

        simpleAttribute.on('blur', this.onSimpleAttributeBlur, this);

        return simpleAttribute;
    },

    /**
     * Оборачивает simpleAttribute в контейнер с кнопками
     */
    wrapSimpleAttribute: function (simpleAttribute) {
        var deleteButton,
            container;

        if (simpleAttribute instanceof Unidata.view.steward.dataentity.attribute.LookupAttribute) {
            simpleAttribute.on('inputset', function (simpleAttribute, input) {
                // удаляем триггер очистки, т.к. есть deleteButton
                input.triggers.clear.destroy();
            });
        }

        deleteButton = {
            xtype: 'un.fontbutton.delete',
            tabIndex: -1,
            margin: '5 9 5 9',
            //shadow: false,
            //buttonSize: 'extrasmall',
            tooltip: Unidata.i18n.t('dataentity>removeFromList'),
            hidden: true,
            bind: {
                hidden: '{readOnly}'
            },
            listeners: {
                click: Ext.bind(this.onDeleteButtonClick, this, [simpleAttribute])
            }
        };

        container = Ext.widget({
            xtype: 'container',
            cls: 'un-array-attribute-wrap',
            margin: 0,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                simpleAttribute,
                deleteButton
            ]
        });

        return container;
    },

    onAddButtonClick: function () {
        this.addArrayItem();
    },

    addArrayItem: function () {
        var store = this.getStore('values'),
            viewModel = this.getViewModel(),
            proxy = store.getProxy(),
            pagingToolbar = this.lookupReference('pagingToolbar'),
            simpleAttribute,
            proxyData;

        if (!proxy.data) {
            proxy.data = [];
        }

        proxyData = proxy.data;

        proxyData.unshift({value: null, displayValue: null});
        store.reload();
        pagingToolbar.moveFirst();

        viewModel.set('count', proxyData.length);

        this.fireChangeEvent();

        this.expand();

        simpleAttribute = this.getSimpleAttributeAt(0);

        this.addNewSimpleAttributeListeners(simpleAttribute);

        simpleAttribute.focusInput();
    },

    addNewSimpleAttributeListeners: function (simpleAttribute) {
        var simpleAttributeInput = simpleAttribute.getInput();

        simpleAttributeInput.on(
            'specialkey',
            this.onSpecialKey,
            this,
            {
                args: [simpleAttribute],
                /**
                 * Повышаем приоритет, что бы можно было отменить событие blur
                 * @see Unidata.view.steward.dataentity.attribute.AbstractAttribute.blurOnSpecialKey
                 */
                priority: 1
            }
        );

        simpleAttribute.on('blur', this.removeSpecialKeyListener, this, {single: true, args: [simpleAttributeInput]});
    },

    removeSpecialKeyListener: function (simpleAttributeInput) {
        simpleAttributeInput.un('specialkey', this.onSpecialKey, this);
    },

    onSimpleAttributeBlur: function (simpleAttribute) {
        if (Ext.isEmpty(simpleAttribute.getInputValue()) || !simpleAttribute.isValid()) {
            this.deleteSimpleAttribute(simpleAttribute);
        }
    },

    /**
     * Обработчик событий для только что созданного инпута,
     * на ENTER - создаётся новый
     */
    onSpecialKey: function (simpleAttribute, simpleAttributeInput, e) {
        if (e.getKey() === e.ENTER) {
            if (!simpleAttribute.isValid()) {
                e.stopEvent();

                return;
            }

            if (Ext.isEmpty(simpleAttribute.getValue())) {
                e.stopEvent(); // не даём сработать blur
            } else {
                simpleAttributeInput.un('specialkey', this.onSpecialKey, this);
                this.addArrayItem();
            }
        }
    },

    deleteSimpleAttribute: function (simpleAttribute) {
        var view = this.getView(),
            viewModel = this.getViewModel(),
            store = this.getStore('values'),
            proxy = store.getProxy(),
            proxyData = proxy.data;

        Ext.Array.each(proxyData, function (item, index, array) {
            if (item.id === simpleAttribute.relatedRecord.id) {
                array.splice(index, 1);
                store.totalCount = array.length;
                view.fixPage();

                return false;
            }
        }, this);

        viewModel.set('count', proxyData.length);

        this.fireChangeEvent();
    },

    onDeleteButtonClick: function (simpleAttribute) {
        this.deleteSimpleAttribute(simpleAttribute);
    },

    fireChangeEvent: function () {
        var view = this.getView();

        view.fireEvent('change');
    }

});
