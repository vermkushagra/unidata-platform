/**
 * Миксин для компонентов, реализующий методы для ожидания выполенения асинхронных операций
 *
 * @author Aleksandr Bavin
 * @date 2018-02-12
 */
Ext.define('Unidata.mixin.PromisedComponent', {

    extend: 'Ext.Mixin',

    mixinConfig: {
        extended: function (baseClass, derivedClass, classBody) {
            // оборачиваем конструкторы дочерних классов
            Unidata.mixin.PromisedComponent.wrapConstructor(classBody);
        }
    },

    hasPromisedComponentMixin: true,

    statics: {
        /**
         * Оборачивает конструктор, дополняя его добавлением промисов,
         * которые будут зарезолвены, после того, как вызван оригинальный метод конструктора.
         * Необходимо, для того, что бы знать, что все конструкторы полностью выполнены.
         *
         * @param targetClass
         */
        wrapConstructor: function (targetClass) {
            var targetClassConstructor = targetClass['constructor'],
                innerConstructorWrap;

            if (targetClassConstructor) {
                innerConstructorWrap = function () {
                    var constructorDeferred = new Ext.Deferred();

                    this.initPromisedComponent();

                    this.addComponentPromise(constructorDeferred.promise);

                    targetClassConstructor.apply(this, arguments);

                    constructorDeferred.resolve();
                };

                targetClass['constructor'] = innerConstructorWrap;
            }
        }
    },

    /**
     * Добавляем обёртку конструктора в прототип класса, к которому применили миксин
     *
     * @param targetClass
     * @private
     */
    onClassMixedIn: function (targetClass) {
        Unidata.mixin.PromisedComponent.wrapConstructor(targetClass.prototype);
    },

    /**
     * Инициализация начальных данных
     *
     * @private
     */
    initPromisedComponent: function () {
        var component = this;

        this.componentPromises = [];
        this.componentReadyDeferred = new Ext.Deferred();

        // запускаем итерацию проверки промисов на готовность
        this.runComponentReadyIteration(component.componentPromises.length)
            .then(
                function () {
                    component.componentReadyDeferred.resolve(component);
                    component.onComponentReady(component);
                },
                component.componentReadyReject
            )
            .done();

        // инициализация необходима только один раз
        this.initPromisedComponent = Ext.emptyFn;
    },

    /**
     * Возвращает промис с компонентом, который полностью готов для использования
     *
     * @returns {Ext.Promise}
     */
    componentReady: function () {
        return this.componentReadyDeferred.promise;
    },

    /**
     * Метод для переопледеления, вызывается, когда компонент готов к использованию
     * @protected
     */
    onComponentReady: Ext.emptyFn,

    /**
     * Добавляет промис, который необходимо зарезолвить, для готовности компонента
     *
     * @param {Ext.Promise} promise
     * @protected
     */
    addComponentPromise: function (promise) {
        this.componentPromises.push(promise);
    },

    privates: {
        componentReadyDeferred: null,

        /**
         * Массив промисов, которые необходимо зарезолвить, для готовности компонента
         * @type {Array}
         */
        componentPromises: null,

        /**
         * Рекурсивно запускает итерацию проверки промисов компонента,
         * до тех пор, пока появляются новые
         *
         * @param componentPromisesLength
         * @returns {Ext.Promise}
         */
        runComponentReadyIteration: function (componentPromisesLength) {
            var component = this,
                promise;

            promise = Ext.Deferred.all(Ext.clone(component.componentPromises))
                .then(
                    function () {
                        // если во время резолва промисов добавились новые, запускаем новую итерацию ожидания
                        if (componentPromisesLength !== component.componentPromises.length) {
                            return component.runComponentReadyIteration(component.componentPromises.length);
                        } else {
                            return component;
                        }
                    }
                );

            return promise;
        },

        /**
         * Метод для реджекта
         *
         * @param error
         * @returns {Ext.Promise}
         */
        componentReadyReject: function (error) {
            return Ext.Deferred.rejected(error);
        }
    }

});
