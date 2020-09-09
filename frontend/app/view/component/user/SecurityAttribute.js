Ext.define('Unidata.view.component.user.SecurityAttribute', {
    extend: 'Ext.panel.Panel',

    config: {
        readOnly: false
    },

    viewModel: {
        data: {
            readOnly: false
        }
    },

    securityLabelsGroup: null,
    securityLabels: null,
    labelName: null,

    securityLabelSample: null, // метка - образец, на основе которой создаются копии

    attrLabelWidth: 130,

    title: '',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    ui: 'un-card',

    count: 0,

    items: [],

    initComponent: function () {
        var me = this,
            addButton;

        this.callParent(arguments);

        if (this.securityLabelsGroup.length) {
            this.setTitle(this.securityLabelsGroup[0].get('displayName'));
            this.labelName = this.securityLabelsGroup[0].get('name');
            this.initEmptySample(this.securityLabelsGroup[0]);
        } else {
            this.setTitle(this.securityLabelSample.get('displayName'));
            this.labelName = this.securityLabelSample.get('name');
        }

        Ext.Array.each(this.securityLabelsGroup,  function (securityLabel) {
            var container;

            container = me.createSecurityLabelContainer(securityLabel);

            me.add(container);
            me.count++;
        });

        addButton = Ext.widget({
            xtype: 'component',
            html: '<i class="fa fa-plus-circle"></i> ' +
                  Unidata.i18n.t('common:add') +
                  ' <span style="text-decoration: underline dashed;">' +
                      Unidata.i18n.t('admin.security>value').toLowerCase() +
                  '<span>',
            padding: '5 0 0 130',
            style: {
                'font-family': 'Open Sans',
                'font-size': '13px',
                'color': 'rgb(80, 127, 186)'
            },
            hidden: true,
            bind: {
                hidden: '{readOnly}'
            },
            listeners: {
                el: {
                    click: me.onAddButtonClick,
                    scope: me
                },
                render: function (cmp) {
                    cmp.toolTip = Ext.create('Ext.tip.ToolTip', {
                        target: cmp.el,
                        delegate: 'span',
                        trackMouse: true,
                        html: Unidata.i18n.t('admin.security>addNewValueToSecurityLabel'),
                        renderTo: Ext.getBody()
                    });
                },
                destroy: function (cmp) {
                    if (cmp.toolTip) {
                        cmp.toolTip.destroy();
                    }
                }
            }
        });

        me.add(addButton);

        this.securityLabelsGroup = [];
    },

    updateReadOnly: function (readOnly) {
        this.getViewModel().set('readOnly', readOnly);
    },

    initEmptySample: function (sl) {
        var data = sl.getData(),
            newSecurityLabel = Ext.create('Unidata.model.user.SecurityLabelUser', data);

        delete data[sl.getIdProperty()];

        sl.attributes().each(function (attribute) {
            var newAttribute;

            data = attribute.getData();
            delete data[attribute.getIdProperty()];

            newAttribute = Ext.create('Unidata.model.user.SecurityLabelAttributeUser', data);
            newAttribute.set('value', '');

            newSecurityLabel.attributes().add(newAttribute);
        });

        this.securityLabelSample = newSecurityLabel;
    },

    createSecurityLabelContainer: function (securityLabel) {
        var me = this,
            container,
            fieldContainer;

        fieldContainer = Ext.create({
            xtype: 'container',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            items: [
            ]
        });

        container = Ext.create({
            xtype: 'container',
            cls: 'un-securitylabel-container',
            referenceHolder: true,
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                fieldContainer,
                {
                    xtype: 'container',
                    flex: 0,
                    layout: {
                        type: 'vbox'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{readOnly}'
                    },
                    items: [
                        {
                            xtype: 'button',
                            reference: 'deleteButton',
                            text: Unidata.i18n.t('common:delete'),
                            margin: '0 0 0 10',
                            listeners: {
                                click: function () {
                                    me.onDeleteButtonClick(securityLabel, container);
                                }
                            }
                        }
                    ]
                }
            ]
        });

        container.deleteButton = container.lookupReference('deleteButton');
        container.isSecurityLabelContainer = true;

        securityLabel.attributes().each(function (attribute) {
            var item;

            item = Ext.create({
                xtype: 'textfield',
                labelWidth: me.attrLabelWidth,
                emptyText: Unidata.i18n.t('other>allowAll'),
                viewModel: {
                    data: {
                        securityLabel: securityLabel,
                        attribute: attribute
                    }
                },
                bind: {
                    readOnly: '{readOnly}',
                    fieldLabel: '{attribute.name}',
                    value: '{attribute.value}'
                },
                flex: 1
            });

            fieldContainer.add(item);
        });

        return container;
    },

    /**
     *
     * @returns {boolean}
     */
    checkDeleteLimitCount: function () {
        return false;
    },

    onDeleteButtonClick: function (securityLabel, container) {
        if (this.checkDeleteLimitCount()) {
            return;
        }

        this.securityLabels.remove(securityLabel);
        container.destroy();
        this.count--;

        this.fireEvent('removelabel');
    },

    onAddButtonClick: function () {
        var securityLabel,
            newSecurityLabel,
            data;

        securityLabel = this.securityLabelSample;

        data = securityLabel.getData();
        delete data[securityLabel.getIdProperty()];

        newSecurityLabel = Ext.create('Unidata.model.user.SecurityLabelUser', data);

        securityLabel.attributes().each(function (attribute) {
            var newAttribute;

            data = attribute.getData();
            delete data[attribute.getIdProperty()];

            newAttribute = Ext.create('Unidata.model.user.SecurityLabelAttributeUser', data);
            newAttribute.set('value', '');

            newSecurityLabel.attributes().add(newAttribute);
        });

        this.securityLabels.add(newSecurityLabel);
        this.insert(this.items.length - 1, this.createSecurityLabelContainer(newSecurityLabel));
        this.count++;

        this.fireEvent('addlabel');
    },

    getCount: function () {
        return this.count;
    },

    getLabelName: function () {
        return this.labelName;
    },

    getSecurityLabelsByLabelName: function (labelName) {
        var result = [];

        this.securityLabels.each(function (securityLabel) {
            if (securityLabel.get('name') === labelName) {
                result.push(securityLabel);
            }
        });

        return result;
    },

    removeSecurityLabels: function () {
        this.securityLabels.remove(this.getSecurityLabelsByLabelName(this.labelName));
        this.destroy();
    },

    getSecurityLabelByLabelName: function (labelName) {
        var result = null;

        this.securityLabels.each(function (securityLabel) {
            if (securityLabel.get('name') === labelName) {
                result = securityLabel;

                return false; //прекращение итерации
            }
        });

        return result;
    }
});
