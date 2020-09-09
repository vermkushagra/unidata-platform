/**
 * Поиск по данным
 *
 * @author Aleksandr Bavin
 * @date 2018-02-09
 */
Ext.define('Unidata.module.search.DataSearchQuery', {

    extend: 'Unidata.module.search.SearchQuery',

    config: {
        entityTerm: null,
        textTerm: null,
        flagsTerm: null,

        dateAsOfTerm: null,
        dateCreatedTerm: null,
        dateUpdatedTerm: null,

        //dq
        dqRuleNameTerm: null,
        dqSeverityTerm: null,
        dqCategoryTerm: null
    },

    entityTermDeferred: null,
    attributeTermsCollection: null,
    supplementaryRequestsCount: 0, // количество Unidata.module.search.term.SupplementaryRequest
    formFieldsCount: 0, // количество Unidata.module.search.term.FormField

    constructor: function () {
        this.entityTermDeferred = new Ext.Deferred();
        this.attributeTermsCollection = new Ext.util.Collection();

        this.callParent(arguments);
    },

    initTerms: function () {
        this.callParent(arguments);

        this.addTerm(new Unidata.module.search.term.Entity());
        this.addTerm(new Unidata.module.search.term.Text());
        this.addTerm(new Unidata.module.search.term.Flags());

        this.addTerm(new Unidata.module.search.term.DateAsOf());
        this.addTerm(new Unidata.module.search.term.DateCreated());
        this.addTerm(new Unidata.module.search.term.DateUpdated());

        this.initFacets();
        this.initDataQuality();
    },

    onTermsReady: function () {
        this.callParent(arguments);

        this.setEntityTerm(this.findTerm('entity'));
        this.setTextTerm(this.findTerm('text'));
        this.setFlagsTerm(this.findTerm('flags'));

        this.setDateAsOfTerm(this.findTerm('dateAsOf'));
        this.setDateCreatedTerm(this.findTerm('dateCreated'));
        this.setDateUpdatedTerm(this.findTerm('dateUpdated'));

        //dq
        this.setDqRuleNameTerm(this.findTerm('dq.ruleName'));
        this.setDqSeverityTerm(this.findTerm('dq.severity'));
        this.setDqCategoryTerm(this.findTerm('dq.category'));

        this.initFlagsCalculations();
        this.initDqCalculations();

        this.getTermsCollection().each(function (term) {
            this.updateTermJoins(term);
        }, this);
    },

    initDataQuality: function () {
        this.addTerm(new Unidata.module.search.term.dq.RuleNameFormField());
        this.addTerm(new Unidata.module.search.term.dq.SeverityFormField());
        this.addTerm(new Unidata.module.search.term.dq.CategoryFormField());
    },

    initDqCalculations: function () {
        var facetErrorsTerm = this.findTerm('facet.errors_only');

        facetErrorsTerm.bind('termIsActive', this.calculateFlags, this);

        this.getDqRuleNameTerm().setFacetErrorsTerm(facetErrorsTerm);
        this.getDqSeverityTerm().setFacetErrorsTerm(facetErrorsTerm);
        this.getDqCategoryTerm().setFacetErrorsTerm(facetErrorsTerm);
    },

    /**
     * Привязка флагов к другим термов
     */
    initFlagsCalculations: function () {
        var textTerm = this.getTextTerm();

        textTerm.bind('value', this.calculateFlags, this);

        this.calculateFlags();
    },

    /**
     * Обновляет значения флагов, на основе других термов
     */
    calculateFlags: function () {
        var flagsTerm = this.getFlagsTerm(),
            fetchAll = false,
            dateCreated = this.getDateCreatedTerm(),
            dateUpdated = this.getDateUpdatedTerm(),
            hasActiveFormFields = false,
            textTerm;

        if (!flagsTerm) {
            return;
        }

        // если поле текста пустое
        if (textTerm = this.getTextTerm()) {
            if (Ext.isEmpty(textTerm.getValue())) {
                fetchAll = true;
            }
        }

        // если есть supplementaryRequests
        if (this.supplementaryRequestsCount) {
            fetchAll = true;

            if (textTerm && !Ext.isEmpty(textTerm.getValue())) {
                fetchAll = false;
            }
        }

        this.getTermsCollection().each(function (term) {
            if ((term instanceof Unidata.module.search.term.FormField) && term.getTermIsActive()) {
                hasActiveFormFields = true;

                return false;
            }
        }, this);

        // если есть formFields
        if (hasActiveFormFields) {
            fetchAll = false;
        }

        // создана / обновлена
        if (dateCreated && dateCreated.getTermIsActive()) {
            fetchAll = false;
        } else if (dateUpdated && dateUpdated.getTermIsActive()) {
            fetchAll = false;
        }

        flagsTerm.setFetchAll(fetchAll);
    },

    getMetaRecord: function () {
        return this.getEntityTerm().getMetaRecord();
    },

    updateEntityTerm: function (entityTerm) {
        if (entityTerm) {
            this.entityTermDeferred.resolve(entityTerm);

            this.getTermsCollection().each(function (term) {
                this.joinSupplementaryRequestWithEntityTerm(term);
            }, this);
        }
    },

    /**
     * Объединяет {@see Unidata.module.search.term.Entity} с подзапросом
     *
     * @param term
     */
    joinRelationSupplementaryRequestWithDateAsOfTerm: function (term) {
        var termToJoin,
            dateAsOfTerm;

        if (term instanceof Unidata.module.search.term.relation.SupplementaryRequest) {
            dateAsOfTerm = this.getDateAsOfTerm();

            if (!dateAsOfTerm) {
                return;
            }

            termToJoin = new Unidata.module.search.term.DateAsOf({
                value: dateAsOfTerm.getValue(),
                termIsSearchable: false,
                termIsSavable: false
            });

            dateAsOfTerm.bind('value', termToJoin.setValue, termToJoin);

            term.setDateAsOfTerm(termToJoin);
        }
    },

    /**
     * Объединяет {@see Unidata.module.search.term.Entity} с подзапросом
     *
     * @param term
     */
    joinSupplementaryRequestWithEntityTerm: function (term) {
        var termToJoin;

        if (term instanceof Unidata.module.search.term.SupplementaryRequest) {
            termToJoin = this.getSupplementaryEntityTerm();

            if (termToJoin) {
                term.setEntityTerm(termToJoin);
            }
        }
    },

    /**
     * Объединяет {@see Unidata.module.search.term.facet.UnRanged} с поиском по связям
     *
     * @param term
     */
    joinRelationSupplementaryRequestWithUnRangedTerm: function (term) {
        var termToJoin;

        if (term instanceof Unidata.module.search.term.relation.SupplementaryRequest) {
            termToJoin = this.getSupplementaryUnRangedTerm();

            if (termToJoin) {
                term.setUnRangedTerm(termToJoin);
            }
        }
    },

    /**
     * facet для подзапроса, который связан с текущим {@see Unidata.module.search.term.facet.UnRanged}
     *
     * @returns {Unidata.module.search.term.Facet}
     */
    getSupplementaryUnRangedTerm: function () {
        var unRangedFacetTerm = this.findTerm('facet.un_ranged'),
            term;

        if (!unRangedFacetTerm) {
            return null;
        }

        term = new Unidata.module.search.term.Facet({
            name: Unidata.module.search.term.Facet.facetType.UN_RANGED,
            termIsActive: unRangedFacetTerm.getTermIsActive(),
            termIsSavable: false
        });

        unRangedFacetTerm.bind('termIsActive', term.setTermIsActive, term);

        return term;
    },

    /**
     * Создаёт простой key-value терм, значение которого, связано с {@see Unidata.module.search.term.Entity}
     *
     * @returns {Unidata.module.search.term.KetValue | null}
     */
    getSupplementaryEntityTerm: function () {
        var entityTerm = this.getEntityTerm(),
            term;

        if (!entityTerm) {
            return null;
        }

        term = new Unidata.module.search.term.KetValue({
            key: 'entity',
            value: entityTerm.getName(),
            termIsSavable: false
        });

        entityTerm.bind('name', term.setValue, term);

        return term;
    },

    /**
     * Инициализация фасетов
     */
    initFacets: function () {
        var facetTypes = Unidata.module.search.term.Facet.facetType;

        Ext.Object.each(facetTypes, function (key, value) {
            var facet;

            facet = Unidata.module.search.term.Facet.create({
                name: value,
                termIsActive: false
            });

            this.addTerm(facet);
        }, this);
    },

    /**
     * @param {Unidata.module.search.SearchQueryTerm} term
     */
    onTermAdd: function (term) {
        this.updateTermJoins(term);

        if ((term instanceof Unidata.module.search.term.DateCreated) ||
            (term instanceof Unidata.module.search.term.DateUpdated)
        ) {
            term.bind('value', this.calculateFlags, this);
            this.calculateFlags();
        }

        if (term instanceof Unidata.module.search.term.SupplementaryRequest) {
            this.supplementaryRequestsCount++;
            this.calculateFlags();
        }

        if (term instanceof Unidata.module.search.term.FormField) {
            this.formFieldsCount++;

            term.bind('termIsActive', this.calculateFlags, this);

            this.calculateFlags();
        }

        if (term instanceof Unidata.module.search.term.attribute.FormField) {
            this.attributeTermsCollection.add(term);
        }

        if (term instanceof Unidata.module.search.term.Entity) {
            this.setEntityTerm(term);
        }
    },

    updateTermJoins: function (term) {
        this.joinSupplementaryRequestWithEntityTerm(term);
        this.joinRelationSupplementaryRequestWithUnRangedTerm(term);
        this.joinRelationSupplementaryRequestWithDateAsOfTerm(term);
    },

    /**
     * @param {Unidata.module.search.SearchQueryTerm} term
     */
    onTermRemove: function (term) {
        if (term instanceof Unidata.module.search.term.SupplementaryRequest) {
            this.supplementaryRequestsCount--;
            this.calculateFlags();
        }

        if (term instanceof Unidata.module.search.term.FormField) {
            this.formFieldsCount--;

            term.unbind('termIsActive', this.calculateFlags, this);

            this.calculateFlags();
        }

        if (term instanceof Unidata.module.search.term.attribute.FormField) {
            this.attributeTermsCollection.remove(term);
        }
    }

});
