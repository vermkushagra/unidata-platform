/**
 * Экран "Классификатор" (модель)
 *
 * @author Sergey Shishigin
 * @date 2016-08-03
 */
Ext.define('Unidata.view.classifier.item.ClassifierItemModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.classifier.item',

    data: {
        classifier: null,
        classifierTreeEdit: false,
        classifierTreeSaving: false
    },

    calculateReadOnly: function (classifier, usePhantom) {
        var readOnly   = true,
            hasClassifierRights,
            phantom;

        usePhantom = Ext.coalesceDefined(usePhantom, false);

        if (classifier) {
            phantom = classifier.phantom;
        }

        hasClassifierRights = this.userHasClassifierCreateOrUpdateRights(classifier);

        readOnly = !hasClassifierRights;

        if (usePhantom) {
            readOnly = readOnly || !phantom;
        }

        return readOnly;
    },

    formulas: {
        /**
         * Значение флага phantom классификатора
         */
        classifierPhantom: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var phantom = true;

                if (classifier) {
                    phantom = classifier.phantom;
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },

        classifierName: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var name = true;

                if (classifier) {
                    name = classifier.get('name');
                }

                name = Ext.coalesceDefined(name, '');

                return name;
            }
        },

        selectedClassifierNode: {
            bind: {
                bindTo: '{classifierTree.selection}',
                deep: true
            },
            get: function (value) {
                return value;
            }
        },

        selectedClassifierNodeValidAndNotDirty: {
            bind: {
                bindTo: '{classifierTree.selection}',
                deep: true
            },
            get: function (classifierNode) {
                var validAndNotDirty;

                validAndNotDirty = this.isClassifierNodeValidAndNotDirty(classifierNode, validAndNotDirty);

                validAndNotDirty = Ext.coalesceDefined(validAndNotDirty, true);

                return validAndNotDirty;
            }
        },

        selectedClassifierNodePhantom: {
            bind: {
                bindTo: '{classifierTree.selection}',
                deep: true
            },
            get: function (classifierNode) {
                var phantom = true;

                if (classifierNode) {
                    phantom = classifierNode.phantom;
                }

                phantom = Ext.coalesceDefined(phantom, true);

                return phantom;
            }
        },

        addChildClassifierNodeButtonDisabled: {
            bind: {
                classifier: '{classifier}',
                selectedClassifierNodeValidAndNotDirty: '{selectedClassifierNodeValidAndNotDirty}',
                classifierTreeEdit: '{classifierTreeEdit}',
                classifierTreeSaving: '{classifierTreeSaving}'
            },
            get: function (getter) {
                var classifier = getter.classifier,
                    validAndNotDirty = getter.selectedClassifierNodeValidAndNotDirty,
                    //classifierTreeEdit = getter.classifierTreeEdit,
                    //classifierTreeSaving = getter.classifierTreeSaving,
                    disabled;

                disabled = this.isAddActionDisabled(classifier, validAndNotDirty);

                disabled = Ext.coalesceDefined(disabled, true);

                return disabled;
            }
        },

        deleteChildClassifierNodeButtonDisabled: {
            bind: {
                classifier: '{classifier}',
                classifierNode: '{selectedClassifierNode}'
            },
            get: function (getter) {
                var classifier = getter.classifier,
                    classifierNode = getter.classifierNode,
                    disabled = true;

                disabled = this.isDeleteActionDisabled(classifier, classifierNode);

                disabled = Ext.coalesceDefined(disabled, true);

                return disabled;
            }
        },

        classifierItemPanelTitle: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var title = '',
                    displayName;

                // TODO: extract to buildTitle method (?)
                if (classifier) {
                    displayName = classifier.get('displayName');

                    if (classifier.phantom && displayName === '') {
                        title = Unidata.i18n.t('classifier>newClassifier');
                    } else {
                        title = displayName;
                    }
                }

                title = Ext.coalesceDefined(title, '');

                return title;
            }
        },

        // TODO: рефакторинг (?), extract method
        nameFieldReadOnly: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var readOnly;

                readOnly = this.calculateReadOnly(classifier, true);
                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        displayNameFieldReadOnly: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var readOnly;

                readOnly = this.calculateReadOnly(classifier);
                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        descriptionFieldReadOnly: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var readOnly;

                readOnly = this.calculateReadOnly(classifier);
                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        patternCodeFieldReadOnly: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var readOnly;

                readOnly = this.calculateReadOnly(classifier, true);
                readOnly = Ext.coalesceDefined(readOnly, true);

                return readOnly;
            }
        },

        validateCodeByLevelDisabled: {
            bind: {
                patternCodeFieldReadOnly: '{patternCodeFieldReadOnly}',
                classifierCodePattern: '{classifier.codePattern}'
            },
            get: function (getter) {
                var patternCodeFieldReadOnly = getter.patternCodeFieldReadOnly,
                    classifierCodePattern = getter.classifierCodePattern,
                    disabled;

                patternCodeFieldReadOnly = patternCodeFieldReadOnly === undefined ? true : patternCodeFieldReadOnly;
                classifierCodePattern = classifierCodePattern === undefined ? false : classifierCodePattern;

                disabled = patternCodeFieldReadOnly || !classifierCodePattern;

                return disabled;
            }
        },

        saveButtonHidden: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var hidden,
                    hasClassifierRights = false,
                    hasClassifierNodeRights = false,
                    classifierName,
                    phantom;

                if (classifier) {
                    phantom = classifier.phantom;
                }

                if (classifier) {
                    classifierName = classifier.get('name');
                    hasClassifierRights = this.userHasClassifierCreateOrUpdateRights(classifier);
                    hasClassifierNodeRights = Unidata.Config.userHasAnyRights(classifierName, ['create', 'update']);
                }

                hidden = !hasClassifierRights && !hasClassifierNodeRights;

                hidden = Ext.coalesceDefined(hidden, true);

                return hidden;
            }
        },

        deleteButtonHidden: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var hidden,
                    userHasDeleteRights,
                    phantom;

                if (classifier) {
                    phantom = classifier.phantom;
                }

                userHasDeleteRights = this.userHasClassifiersRight('delete');

                hidden = !userHasDeleteRights && !phantom;
                hidden = Ext.coalesceDefined(hidden, true);

                return hidden;
            }
        },

        classifierTreeReadOnly: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var readOnly,
                    hasCreateOrUpdateRight = false,
                    classifierName;

                if (classifier) {
                    classifierName = classifier.get('name');
                    hasCreateOrUpdateRight = Unidata.Config.userHasAnyRights(classifierName, ['update', 'create']);
                }

                readOnly = !hasCreateOrUpdateRight;
                readOnly = Ext.coalesceDefined(readOnly, false);

                return readOnly;
            }
        },

        classifierTreeHidden: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var hidden,
                    phantom = true;

                if (classifier) {
                    phantom = classifier.phantom;
                }

                // права на чтение теперь не играют роли для классификаторов (см. баг UN-5604)
                hidden = phantom;

                hidden = Ext.coalesceDefined(hidden, true);

                return hidden;
            }
        },

        classifierPatternCodeEmpty: {
            bind: {
                bindTo: '{classifier}',
                deep: true
            },
            get: function (classifier) {
                var empty = true,
                    codePattern;

                if (classifier) {
                    codePattern = classifier.get('codePattern');
                    empty = !codePattern;
                }

                empty = Ext.coalesceDefined(empty, true);

                return empty;
            }
        },

        classifierNotValidateByLevel: {
            bind: {
                bindTo: '{classifier.validateCodeByLevel}'
            },
            get: function (validateCodeByLevel) {
                return !validateCodeByLevel;
            },
            set: function (nonValidateCodeByLevel) {
                var classifier;

                classifier = this.get('classifier');
                classifier.set('validateCodeByLevel', !nonValidateCodeByLevel);
            }
        }
    },

    userHasClassifiersRight: function (rightName) {
        return Unidata.Config.userHasRight('ADMIN_CLASSIFIER_MANAGEMENT', rightName);
    },

    userHasClassifierCreateOrUpdateRights: function (classifier) {
        var hasClassifierCreateRight,
            hasClassifierUpdateRight,
            hasClassifierRights,
            phantom;

        if (classifier) {
            phantom = classifier.phantom;
        }

        hasClassifierUpdateRight = this.userHasClassifiersRight('update');
        hasClassifierCreateRight = this.userHasClassifiersRight('create');
        hasClassifierRights       = phantom ? hasClassifierCreateRight : hasClassifierUpdateRight;

        return hasClassifierRights;
    },

    isNodeCodeFieldValid: function (node) {
        var classifier = this.get('classifier'),
            codePattern,
            code,
            isValid = false;

        if (classifier && node) {
            code = node.get('code');
            codePattern = classifier.get('codePattern');
            isValid = node.isRoot() || !codePattern || (codePattern && code);
        }

        return isValid;
    },

    isClassifierNodeValid: function (node) {
        var isValid = false,
            isNodeCodeFieldValid,
            isModelValid;

        isModelValid = node.isValid();
        isNodeCodeFieldValid = this.isNodeCodeFieldValid(node);
        isValid = isModelValid && isNodeCodeFieldValid;

        return isValid;
    },

    isClassifierNodeValidAndNotDirty: function (classifierNode) {
        var valid,
            dirty,
            validAndNotDirty;

        if (classifierNode) {
            valid            = this.isClassifierNodeValid(classifierNode);
            dirty            = classifierNode.dirty;
            validAndNotDirty = valid && !dirty;
        }

        return validAndNotDirty;
    },

    isAddActionDisabled: function (classifier, validAndNotDirty) {
        var classifierName,
            hasClassifierNodeCreateRights,
            disabled;

        if (classifier) {
            classifierName                = classifier.get('name');
            hasClassifierNodeCreateRights = Unidata.Config.userHasRight(classifierName, 'create');
        }

        disabled = !validAndNotDirty || !hasClassifierNodeCreateRights;

        return disabled;
    },

    isDeleteActionDisabled: function (classifier, node) {
        var classifierName,
            hasClassifierNodeDeleteRights,
            nodePhantom,
            disabled = true;

        if (!node) {
            return disabled;
        }

        if (node.isRoot()) {
            disabled = true;
        } else {
            if (classifier) {
                classifierName                = classifier.get('name');
                hasClassifierNodeDeleteRights = Unidata.Config.userHasRight(classifierName, 'delete');
            }

            disabled = !nodePhantom && !hasClassifierNodeDeleteRights;
        }

        return disabled;
    }
});
