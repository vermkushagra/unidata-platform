/*! JointJS v0.9.2 - JavaScript diagramming library  2014-09-17 


 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0. If a copy of the MPL was not distributed with this
 file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*!
 * jQuery JavaScript Library v2.0.3
 * http://jquery.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2005, 2013 jQuery Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: 2013-07-03T13:30Z
 */
(function (window, undefined) {

    // Can't do this because several apps including ASP.NET trace
    // the stack via arguments.caller.callee and Firefox dies if
    // you try to trace through "use strict" call chains. (#13335)
    // Support: Firefox 18+
    //"use strict";
    var
    // A central reference to the root jQuery(document)
        rootjQuery,

    // The deferred used on DOM ready
        readyList,

    // Support: IE9
    // For `typeof xmlNode.method` instead of `xmlNode.method !== undefined`
        core_strundefined = typeof undefined,

    // Use the correct document accordingly with window argument (sandbox)
        location = window.location,
        document = window.document,
        docElem = document.documentElement,

    // Map over jQuery in case of overwrite
        _jQuery = window.jQuery,

    // Map over the $ in case of overwrite
        _$ = window.$,

    // [[Class]] -> type pairs
        class2type = {},

    // List of deleted data cache ids, so we can reuse them
        core_deletedIds = [],

        core_version = "2.0.3",

    // Save a reference to some core methods
        core_concat = core_deletedIds.concat,
        core_push = core_deletedIds.push,
        core_slice = core_deletedIds.slice,
        core_indexOf = core_deletedIds.indexOf,
        core_toString = class2type.toString,
        core_hasOwn = class2type.hasOwnProperty,
        core_trim = core_version.trim,

    // Define a local copy of jQuery
        jQuery = function (selector, context) {
            // The jQuery object is actually just the init constructor 'enhanced'
            return new jQuery.fn.init(selector, context, rootjQuery);
        },

    // Used for matching numbers
        core_pnum = /[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/.source,

    // Used for splitting on whitespace
        core_rnotwhite = /\S+/g,

    // A simple way to check for HTML strings
    // Prioritize #id over <tag> to avoid XSS via location.hash (#9521)
    // Strict HTML recognition (#11290: must start with <)
        rquickExpr = /^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/,

    // Match a standalone tag
        rsingleTag = /^<(\w+)\s*\/?>(?:<\/\1>|)$/,

    // Matches dashed string for camelizing
        rmsPrefix = /^-ms-/,
        rdashAlpha = /-([\da-z])/gi,

    // Used by jQuery.camelCase as callback to replace()
        fcamelCase = function (all, letter) {
            return letter.toUpperCase();
        },

    // The ready event handler and self cleanup method
        completed = function () {
            document.removeEventListener("DOMContentLoaded", completed, false);
            window.removeEventListener("load", completed, false);
            jQuery.ready();
        };

    jQuery.fn = jQuery.prototype = {
        // The current version of jQuery being used
        jquery: core_version,

        constructor: jQuery,
        init: function (selector, context, rootjQuery) {
            var match, elem;

            // HANDLE: $(""), $(null), $(undefined), $(false)
            if (!selector) {
                return this;
            }

            // Handle HTML strings
            if (typeof selector === "string") {
                if (selector.charAt(0) === "<" && selector.charAt(selector.length - 1) === ">" && selector.length >= 3) {
                    // Assume that strings that start and end with <> are HTML and skip the regex check
                    match = [null, selector, null];

                } else {
                    match = rquickExpr.exec(selector);
                }

                // Match html or make sure no context is specified for #id
                if (match && (match[1] || !context)) {

                    // HANDLE: $(html) -> $(array)
                    if (match[1]) {
                        context = context instanceof jQuery ? context[0] : context;

                        // scripts is true for back-compat
                        jQuery.merge(this, jQuery.parseHTML(
                            match[1],
                            context && context.nodeType ? context.ownerDocument || context : document,
                            true
                        ));

                        // HANDLE: $(html, props)
                        if (rsingleTag.test(match[1]) && jQuery.isPlainObject(context)) {
                            for (match in context) {
                                // Properties of context are called as methods if possible
                                if (jQuery.isFunction(this[match])) {
                                    this[match](context[match]);

                                    // ...and otherwise set as attributes
                                } else {
                                    this.attr(match, context[match]);
                                }
                            }
                        }

                        return this;

                        // HANDLE: $(#id)
                    } else {
                        elem = document.getElementById(match[2]);

                        // Check parentNode to catch when Blackberry 4.6 returns
                        // nodes that are no longer in the document #6963
                        if (elem && elem.parentNode) {
                            // Inject the element directly into the jQuery object
                            this.length = 1;
                            this[0] = elem;
                        }

                        this.context = document;
                        this.selector = selector;
                        return this;
                    }

                    // HANDLE: $(expr, $(...))
                } else if (!context || context.jquery) {
                    return ( context || rootjQuery ).find(selector);

                    // HANDLE: $(expr, context)
                    // (which is just equivalent to: $(context).find(expr)
                } else {
                    return this.constructor(context).find(selector);
                }

                // HANDLE: $(DOMElement)
            } else if (selector.nodeType) {
                this.context = this[0] = selector;
                this.length = 1;
                return this;

                // HANDLE: $(function)
                // Shortcut for document ready
            } else if (jQuery.isFunction(selector)) {
                return rootjQuery.ready(selector);
            }

            if (selector.selector !== undefined) {
                this.selector = selector.selector;
                this.context = selector.context;
            }

            return jQuery.makeArray(selector, this);
        },

        // Start with an empty selector
        selector: "",

        // The default length of a jQuery object is 0
        length: 0,

        toArray: function () {
            return core_slice.call(this);
        },

        // Get the Nth element in the matched element set OR
        // Get the whole matched element set as a clean array
        get: function (num) {
            return num == null ?

                // Return a 'clean' array
                this.toArray() :

                // Return just the object
                ( num < 0 ? this[this.length + num] : this[num] );
        },

        // Take an array of elements and push it onto the stack
        // (returning the new matched element set)
        pushStack: function (elems) {

            // Build a new jQuery matched element set
            var ret = jQuery.merge(this.constructor(), elems);

            // Add the old object onto the stack (as a reference)
            ret.prevObject = this;
            ret.context = this.context;

            // Return the newly-formed element set
            return ret;
        },

        // Execute a callback for every element in the matched set.
        // (You can seed the arguments with an array of args, but this is
        // only used internally.)
        each: function (callback, args) {
            return jQuery.each(this, callback, args);
        },

        ready: function (fn) {
            // Add the callback
            jQuery.ready.promise().done(fn);

            return this;
        },

        slice: function () {
            return this.pushStack(core_slice.apply(this, arguments));
        },

        first: function () {
            return this.eq(0);
        },

        last: function () {
            return this.eq(-1);
        },

        eq: function (i) {
            var len = this.length,
                j = +i + ( i < 0 ? len : 0 );
            return this.pushStack(j >= 0 && j < len ? [this[j]] : []);
        },

        map: function (callback) {
            return this.pushStack(jQuery.map(this, function (elem, i) {
                return callback.call(elem, i, elem);
            }));
        },

        end: function () {
            return this.prevObject || this.constructor(null);
        },

        // For internal use only.
        // Behaves like an Array's method, not like a jQuery method.
        push: core_push,
        sort: [].sort,
        splice: [].splice
    };

    // Give the init function the jQuery prototype for later instantiation
    jQuery.fn.init.prototype = jQuery.fn;

    jQuery.extend = jQuery.fn.extend = function () {
        var options, name, src, copy, copyIsArray, clone,
            target = arguments[0] || {},
            i = 1,
            length = arguments.length,
            deep = false;

        // Handle a deep copy situation
        if (typeof target === "boolean") {
            deep = target;
            target = arguments[1] || {};
            // skip the boolean and the target
            i = 2;
        }

        // Handle case when target is a string or something (possible in deep copy)
        if (typeof target !== "object" && !jQuery.isFunction(target)) {
            target = {};
        }

        // extend jQuery itself if only one argument is passed
        if (length === i) {
            target = this;
            --i;
        }

        for (; i < length; i++) {
            // Only deal with non-null/undefined values
            if ((options = arguments[i]) != null) {
                // Extend the base object
                for (name in options) {
                    src = target[name];
                    copy = options[name];

                    // Prevent never-ending loop
                    if (target === copy) {
                        continue;
                    }

                    // Recurse if we're merging plain objects or arrays
                    if (deep && copy && ( jQuery.isPlainObject(copy) || (copyIsArray = jQuery.isArray(copy)) )) {
                        if (copyIsArray) {
                            copyIsArray = false;
                            clone = src && jQuery.isArray(src) ? src : [];

                        } else {
                            clone = src && jQuery.isPlainObject(src) ? src : {};
                        }

                        // Never move original objects, clone them
                        target[name] = jQuery.extend(deep, clone, copy);

                        // Don't bring in undefined values
                    } else if (copy !== undefined) {
                        target[name] = copy;
                    }
                }
            }
        }

        // Return the modified object
        return target;
    };

    jQuery.extend({
        // Unique for each copy of jQuery on the page
        expando: "jQuery" + ( core_version + Math.random() ).replace(/\D/g, ""),

        noConflict: function (deep) {
            if (window.$ === jQuery) {
                window.$ = _$;
            }

            if (deep && window.jQuery === jQuery) {
                window.jQuery = _jQuery;
            }

            return jQuery;
        },

        // Is the DOM ready to be used? Set to true once it occurs.
        isReady: false,

        // A counter to track how many items to wait for before
        // the ready event fires. See #6781
        readyWait: 1,

        // Hold (or release) the ready event
        holdReady: function (hold) {
            if (hold) {
                jQuery.readyWait++;
            } else {
                jQuery.ready(true);
            }
        },

        // Handle when the DOM is ready
        ready: function (wait) {

            // Abort if there are pending holds or we're already ready
            if (wait === true ? --jQuery.readyWait : jQuery.isReady) {
                return;
            }

            // Remember that the DOM is ready
            jQuery.isReady = true;

            // If a normal DOM Ready event fired, decrement, and wait if need be
            if (wait !== true && --jQuery.readyWait > 0) {
                return;
            }

            // If there are functions bound, to execute
            readyList.resolveWith(document, [jQuery]);

            // Trigger any bound ready events
            if (jQuery.fn.trigger) {
                jQuery(document).trigger("ready").off("ready");
            }
        },

        // See test/unit/core.js for details concerning isFunction.
        // Since version 1.3, DOM methods and functions like alert
        // aren't supported. They return false on IE (#2968).
        isFunction: function (obj) {
            return jQuery.type(obj) === "function";
        },

        isArray: Array.isArray,

        isWindow: function (obj) {
            return obj != null && obj === obj.window;
        },

        isNumeric: function (obj) {
            return !isNaN(parseFloat(obj)) && isFinite(obj);
        },

        type: function (obj) {
            if (obj == null) {
                return String(obj);
            }
            // Support: Safari <= 5.1 (functionish RegExp)
            return typeof obj === "object" || typeof obj === "function" ?
            class2type[core_toString.call(obj)] || "object" :
                typeof obj;
        },

        isPlainObject: function (obj) {
            // Not plain objects:
            // - Any object or value whose internal [[Class]] property is not "[object Object]"
            // - DOM nodes
            // - window
            if (jQuery.type(obj) !== "object" || obj.nodeType || jQuery.isWindow(obj)) {
                return false;
            }

            // Support: Firefox <20
            // The try/catch suppresses exceptions thrown when attempting to access
            // the "constructor" property of certain host objects, ie. |window.location|
            // https://bugzilla.mozilla.org/show_bug.cgi?id=814622
            try {
                if (obj.constructor && !core_hasOwn.call(obj.constructor.prototype, "isPrototypeOf")) {
                    return false;
                }
            } catch (e) {
                return false;
            }

            // If the function hasn't returned already, we're confident that
            // |obj| is a plain object, created by {} or constructed with new Object
            return true;
        },

        isEmptyObject: function (obj) {
            var name;
            for (name in obj) {
                return false;
            }
            return true;
        },

        error: function (msg) {
            throw new Error(msg);
        },

        // data: string of html
        // context (optional): If specified, the fragment will be created in this context, defaults to document
        // keepScripts (optional): If true, will include scripts passed in the html string
        parseHTML: function (data, context, keepScripts) {
            if (!data || typeof data !== "string") {
                return null;
            }
            if (typeof context === "boolean") {
                keepScripts = context;
                context = false;
            }
            context = context || document;

            var parsed = rsingleTag.exec(data),
                scripts = !keepScripts && [];

            // Single tag
            if (parsed) {
                return [context.createElement(parsed[1])];
            }

            parsed = jQuery.buildFragment([data], context, scripts);

            if (scripts) {
                jQuery(scripts).remove();
            }

            return jQuery.merge([], parsed.childNodes);
        },

        parseJSON: JSON.parse,

        // Cross-browser xml parsing
        parseXML: function (data) {
            var xml, tmp;
            if (!data || typeof data !== "string") {
                return null;
            }

            // Support: IE9
            try {
                tmp = new DOMParser();
                xml = tmp.parseFromString(data, "text/xml");
            } catch (e) {
                xml = undefined;
            }

            if (!xml || xml.getElementsByTagName("parsererror").length) {
                jQuery.error("Invalid XML: " + data);
            }
            return xml;
        },

        noop: function () {},

        // Evaluates a script in a global context
        globalEval: function (code) {
            var script,
                indirect = eval;

            code = jQuery.trim(code);

            if (code) {
                // If the code includes a valid, prologue position
                // strict mode pragma, execute code by injecting a
                // script tag into the document.
                if (code.indexOf("use strict") === 1) {
                    script = document.createElement("script");
                    script.text = code;
                    document.head.appendChild(script).parentNode.removeChild(script);
                } else {
                    // Otherwise, avoid the DOM node creation, insertion
                    // and removal by using an indirect global eval
                    indirect(code);
                }
            }
        },

        // Convert dashed to camelCase; used by the css and data modules
        // Microsoft forgot to hump their vendor prefix (#9572)
        camelCase: function (string) {
            return string.replace(rmsPrefix, "ms-").replace(rdashAlpha, fcamelCase);
        },

        nodeName: function (elem, name) {
            return elem.nodeName && elem.nodeName.toLowerCase() === name.toLowerCase();
        },

        // args is for internal usage only
        each: function (obj, callback, args) {
            var value,
                i = 0,
                length = obj.length,
                isArray = isArraylike(obj);

            if (args) {
                if (isArray) {
                    for (; i < length; i++) {
                        value = callback.apply(obj[i], args);

                        if (value === false) {
                            break;
                        }
                    }
                } else {
                    for (i in obj) {
                        value = callback.apply(obj[i], args);

                        if (value === false) {
                            break;
                        }
                    }
                }

                // A special, fast, case for the most common use of each
            } else {
                if (isArray) {
                    for (; i < length; i++) {
                        value = callback.call(obj[i], i, obj[i]);

                        if (value === false) {
                            break;
                        }
                    }
                } else {
                    for (i in obj) {
                        value = callback.call(obj[i], i, obj[i]);

                        if (value === false) {
                            break;
                        }
                    }
                }
            }

            return obj;
        },

        trim: function (text) {
            return text == null ? "" : core_trim.call(text);
        },

        // results is for internal usage only
        makeArray: function (arr, results) {
            var ret = results || [];

            if (arr != null) {
                if (isArraylike(Object(arr))) {
                    jQuery.merge(ret,
                        typeof arr === "string" ?
                            [arr] : arr
                    );
                } else {
                    core_push.call(ret, arr);
                }
            }

            return ret;
        },

        inArray: function (elem, arr, i) {
            return arr == null ? -1 : core_indexOf.call(arr, elem, i);
        },

        merge: function (first, second) {
            var l = second.length,
                i = first.length,
                j = 0;

            if (typeof l === "number") {
                for (; j < l; j++) {
                    first[i++] = second[j];
                }
            } else {
                while (second[j] !== undefined) {
                    first[i++] = second[j++];
                }
            }

            first.length = i;

            return first;
        },

        grep: function (elems, callback, inv) {
            var retVal,
                ret = [],
                i = 0,
                length = elems.length;
            inv = !!inv;

            // Go through the array, only saving the items
            // that pass the validator function
            for (; i < length; i++) {
                retVal = !!callback(elems[i], i);
                if (inv !== retVal) {
                    ret.push(elems[i]);
                }
            }

            return ret;
        },

        // arg is for internal usage only
        map: function (elems, callback, arg) {
            var value,
                i = 0,
                length = elems.length,
                isArray = isArraylike(elems),
                ret = [];

            // Go through the array, translating each of the items to their
            if (isArray) {
                for (; i < length; i++) {
                    value = callback(elems[i], i, arg);

                    if (value != null) {
                        ret[ret.length] = value;
                    }
                }

                // Go through every key on the object,
            } else {
                for (i in elems) {
                    value = callback(elems[i], i, arg);

                    if (value != null) {
                        ret[ret.length] = value;
                    }
                }
            }

            // Flatten any nested arrays
            return core_concat.apply([], ret);
        },

        // A global GUID counter for objects
        guid: 1,

        // Bind a function to a context, optionally partially applying any
        // arguments.
        proxy: function (fn, context) {
            var tmp, args, proxy;

            if (typeof context === "string") {
                tmp = fn[context];
                context = fn;
                fn = tmp;
            }

            // Quick check to determine if target is callable, in the spec
            // this throws a TypeError, but we will just return undefined.
            if (!jQuery.isFunction(fn)) {
                return undefined;
            }

            // Simulated bind
            args = core_slice.call(arguments, 2);
            proxy = function () {
                return fn.apply(context || this, args.concat(core_slice.call(arguments)));
            };

            // Set the guid of unique handler to the same of original handler, so it can be removed
            proxy.guid = fn.guid = fn.guid || jQuery.guid++;

            return proxy;
        },

        // Multifunctional method to get and set values of a collection
        // The value/s can optionally be executed if it's a function
        access: function (elems, fn, key, value, chainable, emptyGet, raw) {
            var i = 0,
                length = elems.length,
                bulk = key == null;

            // Sets many values
            if (jQuery.type(key) === "object") {
                chainable = true;
                for (i in key) {
                    jQuery.access(elems, fn, i, key[i], true, emptyGet, raw);
                }

                // Sets one value
            } else if (value !== undefined) {
                chainable = true;

                if (!jQuery.isFunction(value)) {
                    raw = true;
                }

                if (bulk) {
                    // Bulk operations run against the entire set
                    if (raw) {
                        fn.call(elems, value);
                        fn = null;

                        // ...except when executing function values
                    } else {
                        bulk = fn;
                        fn = function (elem, key, value) {
                            return bulk.call(jQuery(elem), value);
                        };
                    }
                }

                if (fn) {
                    for (; i < length; i++) {
                        fn(elems[i], key, raw ? value : value.call(elems[i], i, fn(elems[i], key)));
                    }
                }
            }

            return chainable ?
                elems :

                // Gets
                bulk ?
                    fn.call(elems) :
                    length ? fn(elems[0], key) : emptyGet;
        },

        now: Date.now,

        // A method for quickly swapping in/out CSS properties to get correct calculations.
        // Note: this method belongs to the css module but it's needed here for the support module.
        // If support gets modularized, this method should be moved back to the css module.
        swap: function (elem, options, callback, args) {
            var ret, name,
                old = {};

            // Remember the old values, and insert the new ones
            for (name in options) {
                old[name] = elem.style[name];
                elem.style[name] = options[name];
            }

            ret = callback.apply(elem, args || []);

            // Revert the old values
            for (name in options) {
                elem.style[name] = old[name];
            }

            return ret;
        }
    });

    jQuery.ready.promise = function (obj) {
        if (!readyList) {

            readyList = jQuery.Deferred();

            // Catch cases where $(document).ready() is called after the browser event has already occurred.
            // we once tried to use readyState "interactive" here, but it caused issues like the one
            // discovered by ChrisS here: http://bugs.jquery.com/ticket/12282#comment:15
            if (document.readyState === "complete") {
                // Handle it asynchronously to allow scripts the opportunity to delay ready
                setTimeout(jQuery.ready);

            } else {

                // Use the handy event callback
                document.addEventListener("DOMContentLoaded", completed, false);

                // A fallback to window.onload, that will always work
                window.addEventListener("load", completed, false);
            }
        }
        return readyList.promise(obj);
    };

    // Populate the class2type map
    jQuery.each("Boolean Number String Function Array Date RegExp Object Error".split(" "), function (i, name) {
        class2type["[object " + name + "]"] = name.toLowerCase();
    });

    function isArraylike(obj) {
        var length = obj.length,
            type = jQuery.type(obj);

        if (jQuery.isWindow(obj)) {
            return false;
        }

        if (obj.nodeType === 1 && length) {
            return true;
        }

        return type === "array" || type !== "function" &&
            ( length === 0 ||
            typeof length === "number" && length > 0 && ( length - 1 ) in obj );
    }

    // All jQuery objects should point back to these
    rootjQuery = jQuery(document);
    /*!
     * Sizzle CSS Selector Engine v1.9.4-pre
     * http://sizzlejs.com/
     *
     * Copyright 2013 jQuery Foundation, Inc. and other contributors
     * Released under the MIT license
     * http://jquery.org/license
     *
     * Date: 2013-06-03
     */
    (function (window, undefined) {

        var i,
            support,
            cachedruns,
            Expr,
            getText,
            isXML,
            compile,
            outermostContext,
            sortInput,

        // Local document vars
            setDocument,
            document,
            docElem,
            documentIsHTML,
            rbuggyQSA,
            rbuggyMatches,
            matches,
            contains,

        // Instance-specific data
            expando = "sizzle" + -(new Date()),
            preferredDoc = window.document,
            dirruns = 0,
            done = 0,
            classCache = createCache(),
            tokenCache = createCache(),
            compilerCache = createCache(),
            hasDuplicate = false,
            sortOrder = function (a, b) {
                if (a === b) {
                    hasDuplicate = true;
                    return 0;
                }
                return 0;
            },

        // General-purpose constants
            strundefined = typeof undefined,
            MAX_NEGATIVE = 1 << 31,

        // Instance methods
            hasOwn = ({}).hasOwnProperty,
            arr = [],
            pop = arr.pop,
            push_native = arr.push,
            push = arr.push,
            slice = arr.slice,
        // Use a stripped-down indexOf if we can't use a native one
            indexOf = arr.indexOf || function (elem) {
                    var i = 0,
                        len = this.length;
                    for (; i < len; i++) {
                        if (this[i] === elem) {
                            return i;
                        }
                    }
                    return -1;
                },

            booleans = "checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",

        // Regular expressions

        // Whitespace characters http://www.w3.org/TR/css3-selectors/#whitespace
            whitespace = "[\\x20\\t\\r\\n\\f]",
        // http://www.w3.org/TR/css3-syntax/#characters
            characterEncoding = "(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+",

        // Loosely modeled on CSS identifier characters
        // An unquoted value should be a CSS identifier http://www.w3.org/TR/css3-selectors/#attribute-selectors
        // Proper syntax: http://www.w3.org/TR/CSS21/syndata.html#value-def-identifier
            identifier = characterEncoding.replace("w", "w#"),

        // Acceptable operators http://www.w3.org/TR/selectors/#attribute-selectors
            attributes = "\\[" + whitespace + "*(" + characterEncoding + ")" + whitespace +
                "*(?:([*^$|!~]?=)" + whitespace + "*(?:(['\"])((?:\\\\.|[^\\\\])*?)\\3|(" + identifier + ")|)|)" + whitespace + "*\\]",

        // Prefer arguments quoted,
        //   then not containing pseudos/brackets,
        //   then attribute selectors/non-parenthetical expressions,
        //   then anything else
        // These preferences are here to reduce the number of selectors
        //   needing tokenize in the PSEUDO preFilter
            pseudos = ":(" + characterEncoding + ")(?:\\(((['\"])((?:\\\\.|[^\\\\])*?)\\3|((?:\\\\.|[^\\\\()[\\]]|" + attributes.replace(3, 8) + ")*)|.*)\\)|)",

        // Leading and non-escaped trailing whitespace, capturing some non-whitespace characters preceding the latter
            rtrim = new RegExp("^" + whitespace + "+|((?:^|[^\\\\])(?:\\\\.)*)" + whitespace + "+$", "g"),

            rcomma = new RegExp("^" + whitespace + "*," + whitespace + "*"),
            rcombinators = new RegExp("^" + whitespace + "*([>+~]|" + whitespace + ")" + whitespace + "*"),

            rsibling = new RegExp(whitespace + "*[+~]"),
            rattributeQuotes = new RegExp("=" + whitespace + "*([^\\]'\"]*)" + whitespace + "*\\]", "g"),

            rpseudo = new RegExp(pseudos),
            ridentifier = new RegExp("^" + identifier + "$"),

            matchExpr = {
                "ID": new RegExp("^#(" + characterEncoding + ")"),
                "CLASS": new RegExp("^\\.(" + characterEncoding + ")"),
                "TAG": new RegExp("^(" + characterEncoding.replace("w", "w*") + ")"),
                "ATTR": new RegExp("^" + attributes),
                "PSEUDO": new RegExp("^" + pseudos),
                "CHILD": new RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\(" + whitespace +
                    "*(even|odd|(([+-]|)(\\d*)n|)" + whitespace + "*(?:([+-]|)" + whitespace +
                    "*(\\d+)|))" + whitespace + "*\\)|)", "i"),
                "bool": new RegExp("^(?:" + booleans + ")$", "i"),
                // For use in libraries implementing .is()
                // We use this for POS matching in `select`
                "needsContext": new RegExp("^" + whitespace + "*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\(" +
                    whitespace + "*((?:-\\d)?\\d*)" + whitespace + "*\\)|)(?=[^-]|$)", "i")
            },

            rnative = /^[^{]+\{\s*\[native \w/,

        // Easily-parseable/retrievable ID or TAG or CLASS selectors
            rquickExpr = /^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,

            rinputs = /^(?:input|select|textarea|button)$/i,
            rheader = /^h\d$/i,

            rescape = /'|\\/g,

        // CSS escapes http://www.w3.org/TR/CSS21/syndata.html#escaped-characters
            runescape = new RegExp("\\\\([\\da-f]{1,6}" + whitespace + "?|(" + whitespace + ")|.)", "ig"),
            funescape = function (_, escaped, escapedWhitespace) {
                var high = "0x" + escaped - 0x10000;
                // NaN means non-codepoint
                // Support: Firefox
                // Workaround erroneous numeric interpretation of +"0x"
                return high !== high || escapedWhitespace ?
                    escaped :
                    // BMP codepoint
                    high < 0 ?
                        String.fromCharCode(high + 0x10000) :
                        // Supplemental Plane codepoint (surrogate pair)
                        String.fromCharCode(high >> 10 | 0xD800, high & 0x3FF | 0xDC00);
            };

        // Optimize for push.apply( _, NodeList )
        try {
            push.apply(
                (arr = slice.call(preferredDoc.childNodes)),
                preferredDoc.childNodes
            );
            // Support: Android<4.0
            // Detect silently failing push.apply
            arr[preferredDoc.childNodes.length].nodeType;
        } catch (e) {
            push = {
                apply: arr.length ?

                    // Leverage slice if possible
                    function (target, els) {
                        push_native.apply(target, slice.call(els));
                    } :

                    // Support: IE<9
                    // Otherwise append directly
                    function (target, els) {
                        var j = target.length,
                            i = 0;
                        // Can't trust NodeList.length
                        while ((target[j++] = els[i++])) {
                        }
                        target.length = j - 1;
                    }
            };
        }

        function Sizzle(selector, context, results, seed) {
            var match, elem, m, nodeType,
            // QSA vars
                i, groups, old, nid, newContext, newSelector;

            if (( context ? context.ownerDocument || context : preferredDoc ) !== document) {
                setDocument(context);
            }

            context = context || document;
            results = results || [];

            if (!selector || typeof selector !== "string") {
                return results;
            }

            if ((nodeType = context.nodeType) !== 1 && nodeType !== 9) {
                return [];
            }

            if (documentIsHTML && !seed) {

                // Shortcuts
                if ((match = rquickExpr.exec(selector))) {
                    // Speed-up: Sizzle("#ID")
                    if ((m = match[1])) {
                        if (nodeType === 9) {
                            elem = context.getElementById(m);
                            // Check parentNode to catch when Blackberry 4.6 returns
                            // nodes that are no longer in the document #6963
                            if (elem && elem.parentNode) {
                                // Handle the case where IE, Opera, and Webkit return items
                                // by name instead of ID
                                if (elem.id === m) {
                                    results.push(elem);
                                    return results;
                                }
                            } else {
                                return results;
                            }
                        } else {
                            // Context is not a document
                            if (context.ownerDocument && (elem = context.ownerDocument.getElementById(m)) &&
                                contains(context, elem) && elem.id === m) {
                                results.push(elem);
                                return results;
                            }
                        }

                        // Speed-up: Sizzle("TAG")
                    } else if (match[2]) {
                        push.apply(results, context.getElementsByTagName(selector));
                        return results;

                        // Speed-up: Sizzle(".CLASS")
                    } else if ((m = match[3]) && support.getElementsByClassName && context.getElementsByClassName) {
                        push.apply(results, context.getElementsByClassName(m));
                        return results;
                    }
                }

                // QSA path
                if (support.qsa && (!rbuggyQSA || !rbuggyQSA.test(selector))) {
                    nid = old = expando;
                    newContext = context;
                    newSelector = nodeType === 9 && selector;

                    // qSA works strangely on Element-rooted queries
                    // We can work around this by specifying an extra ID on the root
                    // and working up from there (Thanks to Andrew Dupont for the technique)
                    // IE 8 doesn't work on object elements
                    if (nodeType === 1 && context.nodeName.toLowerCase() !== "object") {
                        groups = tokenize(selector);

                        if ((old = context.getAttribute("id"))) {
                            nid = old.replace(rescape, "\\$&");
                        } else {
                            context.setAttribute("id", nid);
                        }
                        nid = "[id='" + nid + "'] ";

                        i = groups.length;
                        while (i--) {
                            groups[i] = nid + toSelector(groups[i]);
                        }
                        newContext = rsibling.test(selector) && context.parentNode || context;
                        newSelector = groups.join(",");
                    }

                    if (newSelector) {
                        try {
                            push.apply(results,
                                newContext.querySelectorAll(newSelector)
                            );
                            return results;
                        } catch (qsaError) {
                        } finally {
                            if (!old) {
                                context.removeAttribute("id");
                            }
                        }
                    }
                }
            }

            // All others
            return select(selector.replace(rtrim, "$1"), context, results, seed);
        }

        /**
         * Create key-value caches of limited size
         * @returns {Function(string, Object)} Returns the Object data after storing it on itself with
         *    property name the (space-suffixed) string and (if the cache is larger than Expr.cacheLength)
         *    deleting the oldest entry
         */
        function createCache() {
            var keys = [];

            function cache(key, value) {
                // Use (key + " ") to avoid collision with native prototype properties (see Issue #157)
                if (keys.push(key += " ") > Expr.cacheLength) {
                    // Only keep the most recent entries
                    delete cache[keys.shift()];
                }
                return (cache[key] = value);
            }

            return cache;
        }

        /**
         * Mark a function for special use by Sizzle
         * @param {Function} fn The function to mark
         */
        function markFunction(fn) {
            fn[expando] = true;
            return fn;
        }

        /**
         * Support testing using an element
         * @param {Function} fn Passed the created div and expects a boolean result
         */
        function assert(fn) {
            var div = document.createElement("div");

            try {
                return !!fn(div);
            } catch (e) {
                return false;
            } finally {
                // Remove from its parent by default
                if (div.parentNode) {
                    div.parentNode.removeChild(div);
                }
                // release memory in IE
                div = null;
            }
        }

        /**
         * Adds the same handler for all of the specified attrs
         * @param {String} attrs Pipe-separated list of attributes
         * @param {Function} handler The method that will be applied
         */
        function addHandle(attrs, handler) {
            var arr = attrs.split("|"),
                i = attrs.length;

            while (i--) {
                Expr.attrHandle[arr[i]] = handler;
            }
        }

        /**
         * Checks document order of two siblings
         * @param {Element} a
         * @param {Element} b
         * @returns {Number} Returns less than 0 if a precedes b, greater than 0 if a follows b
         */
        function siblingCheck(a, b) {
            var cur = b && a,
                diff = cur && a.nodeType === 1 && b.nodeType === 1 &&
                    ( ~b.sourceIndex || MAX_NEGATIVE ) -
                    ( ~a.sourceIndex || MAX_NEGATIVE );

            // Use IE sourceIndex if available on both nodes
            if (diff) {
                return diff;
            }

            // Check if b follows a
            if (cur) {
                while ((cur = cur.nextSibling)) {
                    if (cur === b) {
                        return -1;
                    }
                }
            }

            return a ? 1 : -1;
        }

        /**
         * Returns a function to use in pseudos for input types
         * @param {String} type
         */
        function createInputPseudo(type) {
            return function (elem) {
                var name = elem.nodeName.toLowerCase();
                return name === "input" && elem.type === type;
            };
        }

        /**
         * Returns a function to use in pseudos for buttons
         * @param {String} type
         */
        function createButtonPseudo(type) {
            return function (elem) {
                var name = elem.nodeName.toLowerCase();
                return (name === "input" || name === "button") && elem.type === type;
            };
        }

        /**
         * Returns a function to use in pseudos for positionals
         * @param {Function} fn
         */
        function createPositionalPseudo(fn) {
            return markFunction(function (argument) {
                argument = +argument;
                return markFunction(function (seed, matches) {
                    var j,
                        matchIndexes = fn([], seed.length, argument),
                        i = matchIndexes.length;

                    // Match elements found at the specified indexes
                    while (i--) {
                        if (seed[(j = matchIndexes[i])]) {
                            seed[j] = !(matches[j] = seed[j]);
                        }
                    }
                });
            });
        }

        /**
         * Detect xml
         * @param {Element|Object} elem An element or a document
         */
        isXML = Sizzle.isXML = function (elem) {
            // documentElement is verified for cases where it doesn't yet exist
            // (such as loading iframes in IE - #4833)
            var documentElement = elem && (elem.ownerDocument || elem).documentElement;
            return documentElement ? documentElement.nodeName !== "HTML" : false;
        };

        // Expose support vars for convenience
        support = Sizzle.support = {};

        /**
         * Sets document-related variables once based on the current document
         * @param {Element|Object} [doc] An element or document object to use to set the document
         * @returns {Object} Returns the current document
         */
        setDocument = Sizzle.setDocument = function (node) {
            var doc = node ? node.ownerDocument || node : preferredDoc,
                parent = doc.defaultView;

            // If no document and documentElement is available, return
            if (doc === document || doc.nodeType !== 9 || !doc.documentElement) {
                return document;
            }

            // Set our document
            document = doc;
            docElem = doc.documentElement;

            // Support tests
            documentIsHTML = !isXML(doc);

            // Support: IE>8
            // If iframe document is assigned to "document" variable and if iframe has been reloaded,
            // IE will throw "permission denied" error when accessing "document" variable, see jQuery #13936
            // IE6-8 do not support the defaultView property so parent will be undefined
            if (parent && parent.attachEvent && parent !== parent.top) {
                parent.attachEvent("onbeforeunload", function () {
                    setDocument();
                });
            }

            /* Attributes
             ---------------------------------------------------------------------- */

            // Support: IE<8
            // Verify that getAttribute really returns attributes and not properties (excepting IE8 booleans)
            support.attributes = assert(function (div) {
                div.className = "i";
                return !div.getAttribute("className");
            });

            /* getElement(s)By*
             ---------------------------------------------------------------------- */

            // Check if getElementsByTagName("*") returns only elements
            support.getElementsByTagName = assert(function (div) {
                div.appendChild(doc.createComment(""));
                return !div.getElementsByTagName("*").length;
            });

            // Check if getElementsByClassName can be trusted
            support.getElementsByClassName = assert(function (div) {
                div.innerHTML = "<div class='a'></div><div class='a i'></div>";

                // Support: Safari<4
                // Catch class over-caching
                div.firstChild.className = "i";
                // Support: Opera<10
                // Catch gEBCN failure to find non-leading classes
                return div.getElementsByClassName("i").length === 2;
            });

            // Support: IE<10
            // Check if getElementById returns elements by name
            // The broken getElementById methods don't pick up programatically-set names,
            // so use a roundabout getElementsByName test
            support.getById = assert(function (div) {
                docElem.appendChild(div).id = expando;
                return !doc.getElementsByName || !doc.getElementsByName(expando).length;
            });

            // ID find and filter
            if (support.getById) {
                Expr.find["ID"] = function (id, context) {
                    if (typeof context.getElementById !== strundefined && documentIsHTML) {
                        var m = context.getElementById(id);
                        // Check parentNode to catch when Blackberry 4.6 returns
                        // nodes that are no longer in the document #6963
                        return m && m.parentNode ? [m] : [];
                    }
                };
                Expr.filter["ID"] = function (id) {
                    var attrId = id.replace(runescape, funescape);
                    return function (elem) {
                        return elem.getAttribute("id") === attrId;
                    };
                };
            } else {
                // Support: IE6/7
                // getElementById is not reliable as a find shortcut
                delete Expr.find["ID"];

                Expr.filter["ID"] = function (id) {
                    var attrId = id.replace(runescape, funescape);
                    return function (elem) {
                        var node = typeof elem.getAttributeNode !== strundefined && elem.getAttributeNode("id");
                        return node && node.value === attrId;
                    };
                };
            }

            // Tag
            Expr.find["TAG"] = support.getElementsByTagName ?
                function (tag, context) {
                    if (typeof context.getElementsByTagName !== strundefined) {
                        return context.getElementsByTagName(tag);
                    }
                } :
                function (tag, context) {
                    var elem,
                        tmp = [],
                        i = 0,
                        results = context.getElementsByTagName(tag);

                    // Filter out possible comments
                    if (tag === "*") {
                        while ((elem = results[i++])) {
                            if (elem.nodeType === 1) {
                                tmp.push(elem);
                            }
                        }

                        return tmp;
                    }
                    return results;
                };

            // Class
            Expr.find["CLASS"] = support.getElementsByClassName && function (className, context) {
                    if (typeof context.getElementsByClassName !== strundefined && documentIsHTML) {
                        return context.getElementsByClassName(className);
                    }
                };

            /* QSA/matchesSelector
             ---------------------------------------------------------------------- */

            // QSA and matchesSelector support

            // matchesSelector(:active) reports false when true (IE9/Opera 11.5)
            rbuggyMatches = [];

            // qSa(:focus) reports false when true (Chrome 21)
            // We allow this because of a bug in IE8/9 that throws an error
            // whenever `document.activeElement` is accessed on an iframe
            // So, we allow :focus to pass through QSA all the time to avoid the IE error
            // See http://bugs.jquery.com/ticket/13378
            rbuggyQSA = [];

            if ((support.qsa = rnative.test(doc.querySelectorAll))) {
                // Build QSA regex
                // Regex strategy adopted from Diego Perini
                assert(function (div) {
                    // Select is set to empty string on purpose
                    // This is to test IE's treatment of not explicitly
                    // setting a boolean content attribute,
                    // since its presence should be enough
                    // http://bugs.jquery.com/ticket/12359
                    div.innerHTML = "<select><option selected=''></option></select>";

                    // Support: IE8
                    // Boolean attributes and "value" are not treated correctly
                    if (!div.querySelectorAll("[selected]").length) {
                        rbuggyQSA.push("\\[" + whitespace + "*(?:value|" + booleans + ")");
                    }

                    // Webkit/Opera - :checked should return selected option elements
                    // http://www.w3.org/TR/2011/REC-css3-selectors-20110929/#checked
                    // IE8 throws error here and will not see later tests
                    if (!div.querySelectorAll(":checked").length) {
                        rbuggyQSA.push(":checked");
                    }
                });

                assert(function (div) {

                    // Support: Opera 10-12/IE8
                    // ^= $= *= and empty values
                    // Should not select anything
                    // Support: Windows 8 Native Apps
                    // The type attribute is restricted during .innerHTML assignment
                    var input = doc.createElement("input");
                    input.setAttribute("type", "hidden");
                    div.appendChild(input).setAttribute("t", "");

                    if (div.querySelectorAll("[t^='']").length) {
                        rbuggyQSA.push("[*^$]=" + whitespace + "*(?:''|\"\")");
                    }

                    // FF 3.5 - :enabled/:disabled and hidden elements (hidden elements are still enabled)
                    // IE8 throws error here and will not see later tests
                    if (!div.querySelectorAll(":enabled").length) {
                        rbuggyQSA.push(":enabled", ":disabled");
                    }

                    // Opera 10-11 does not throw on post-comma invalid pseudos
                    div.querySelectorAll("*,:x");
                    rbuggyQSA.push(",.*:");
                });
            }

            if ((support.matchesSelector = rnative.test((matches = docElem.webkitMatchesSelector ||
                    docElem.mozMatchesSelector ||
                    docElem.oMatchesSelector ||
                    docElem.msMatchesSelector)))) {

                assert(function (div) {
                    // Check to see if it's possible to do matchesSelector
                    // on a disconnected node (IE 9)
                    support.disconnectedMatch = matches.call(div, "div");

                    // This should fail with an exception
                    // Gecko does not error, returns false instead
                    matches.call(div, "[s!='']:x");
                    rbuggyMatches.push("!=", pseudos);
                });
            }

            rbuggyQSA = rbuggyQSA.length && new RegExp(rbuggyQSA.join("|"));
            rbuggyMatches = rbuggyMatches.length && new RegExp(rbuggyMatches.join("|"));

            /* Contains
             ---------------------------------------------------------------------- */

            // Element contains another
            // Purposefully does not implement inclusive descendent
            // As in, an element does not contain itself
            contains = rnative.test(docElem.contains) || docElem.compareDocumentPosition ?
                function (a, b) {
                    var adown = a.nodeType === 9 ? a.documentElement : a,
                        bup = b && b.parentNode;
                    return a === bup || !!( bup && bup.nodeType === 1 && (
                            adown.contains ?
                                adown.contains(bup) :
                            a.compareDocumentPosition && a.compareDocumentPosition(bup) & 16
                        ));
                } :
                function (a, b) {
                    if (b) {
                        while ((b = b.parentNode)) {
                            if (b === a) {
                                return true;
                            }
                        }
                    }
                    return false;
                };

            /* Sorting
             ---------------------------------------------------------------------- */

            // Document order sorting
            sortOrder = docElem.compareDocumentPosition ?
                function (a, b) {

                    // Flag for duplicate removal
                    if (a === b) {
                        hasDuplicate = true;
                        return 0;
                    }

                    var compare = b.compareDocumentPosition && a.compareDocumentPosition && a.compareDocumentPosition(b);

                    if (compare) {
                        // Disconnected nodes
                        if (compare & 1 ||
                            (!support.sortDetached && b.compareDocumentPosition(a) === compare)) {

                            // Choose the first element that is related to our preferred document
                            if (a === doc || contains(preferredDoc, a)) {
                                return -1;
                            }
                            if (b === doc || contains(preferredDoc, b)) {
                                return 1;
                            }

                            // Maintain original order
                            return sortInput ?
                                ( indexOf.call(sortInput, a) - indexOf.call(sortInput, b) ) :
                                0;
                        }

                        return compare & 4 ? -1 : 1;
                    }

                    // Not directly comparable, sort on existence of method
                    return a.compareDocumentPosition ? -1 : 1;
                } :
                function (a, b) {
                    var cur,
                        i = 0,
                        aup = a.parentNode,
                        bup = b.parentNode,
                        ap = [a],
                        bp = [b];

                    // Exit early if the nodes are identical
                    if (a === b) {
                        hasDuplicate = true;
                        return 0;

                        // Parentless nodes are either documents or disconnected
                    } else if (!aup || !bup) {
                        return a === doc ? -1 :
                            b === doc ? 1 :
                                aup ? -1 :
                                    bup ? 1 :
                                        sortInput ?
                                            ( indexOf.call(sortInput, a) - indexOf.call(sortInput, b) ) :
                                            0;

                        // If the nodes are siblings, we can do a quick check
                    } else if (aup === bup) {
                        return siblingCheck(a, b);
                    }

                    // Otherwise we need full lists of their ancestors for comparison
                    cur = a;
                    while ((cur = cur.parentNode)) {
                        ap.unshift(cur);
                    }
                    cur = b;
                    while ((cur = cur.parentNode)) {
                        bp.unshift(cur);
                    }

                    // Walk down the tree looking for a discrepancy
                    while (ap[i] === bp[i]) {
                        i++;
                    }

                    return i ?
                        // Do a sibling check if the nodes have a common ancestor
                        siblingCheck(ap[i], bp[i]) :

                        // Otherwise nodes in our document sort first
                        ap[i] === preferredDoc ? -1 :
                            bp[i] === preferredDoc ? 1 :
                                0;
                };

            return doc;
        };

        Sizzle.matches = function (expr, elements) {
            return Sizzle(expr, null, null, elements);
        };

        Sizzle.matchesSelector = function (elem, expr) {
            // Set document vars if needed
            if (( elem.ownerDocument || elem ) !== document) {
                setDocument(elem);
            }

            // Make sure that attribute selectors are quoted
            expr = expr.replace(rattributeQuotes, "='$1']");

            if (support.matchesSelector && documentIsHTML &&
                ( !rbuggyMatches || !rbuggyMatches.test(expr) ) &&
                ( !rbuggyQSA || !rbuggyQSA.test(expr) )) {

                try {
                    var ret = matches.call(elem, expr);

                    // IE 9's matchesSelector returns false on disconnected nodes
                    if (ret || support.disconnectedMatch ||
                            // As well, disconnected nodes are said to be in a document
                            // fragment in IE 9
                        elem.document && elem.document.nodeType !== 11) {
                        return ret;
                    }
                } catch (e) {
                }
            }

            return Sizzle(expr, document, null, [elem]).length > 0;
        };

        Sizzle.contains = function (context, elem) {
            // Set document vars if needed
            if (( context.ownerDocument || context ) !== document) {
                setDocument(context);
            }
            return contains(context, elem);
        };

        Sizzle.attr = function (elem, name) {
            // Set document vars if needed
            if (( elem.ownerDocument || elem ) !== document) {
                setDocument(elem);
            }

            var fn = Expr.attrHandle[name.toLowerCase()],
            // Don't get fooled by Object.prototype properties (jQuery #13807)
                val = fn && hasOwn.call(Expr.attrHandle, name.toLowerCase()) ?
                    fn(elem, name, !documentIsHTML) :
                    undefined;

            return val === undefined ?
                support.attributes || !documentIsHTML ?
                    elem.getAttribute(name) :
                    (val = elem.getAttributeNode(name)) && val.specified ?
                        val.value :
                        null :
                val;
        };

        Sizzle.error = function (msg) {
            throw new Error("Syntax error, unrecognized expression: " + msg);
        };

        /**
         * Document sorting and removing duplicates
         * @param {ArrayLike} results
         */
        Sizzle.uniqueSort = function (results) {
            var elem,
                duplicates = [],
                j = 0,
                i = 0;

            // Unless we *know* we can detect duplicates, assume their presence
            hasDuplicate = !support.detectDuplicates;
            sortInput = !support.sortStable && results.slice(0);
            results.sort(sortOrder);

            if (hasDuplicate) {
                while ((elem = results[i++])) {
                    if (elem === results[i]) {
                        j = duplicates.push(i);
                    }
                }
                while (j--) {
                    results.splice(duplicates[j], 1);
                }
            }

            return results;
        };

        /**
         * Utility function for retrieving the text value of an array of DOM nodes
         * @param {Array|Element} elem
         */
        getText = Sizzle.getText = function (elem) {
            var node,
                ret = "",
                i = 0,
                nodeType = elem.nodeType;

            if (!nodeType) {
                // If no nodeType, this is expected to be an array
                for (; (node = elem[i]); i++) {
                    // Do not traverse comment nodes
                    ret += getText(node);
                }
            } else if (nodeType === 1 || nodeType === 9 || nodeType === 11) {
                // Use textContent for elements
                // innerText usage removed for consistency of new lines (see #11153)
                if (typeof elem.textContent === "string") {
                    return elem.textContent;
                } else {
                    // Traverse its children
                    for (elem = elem.firstChild; elem; elem = elem.nextSibling) {
                        ret += getText(elem);
                    }
                }
            } else if (nodeType === 3 || nodeType === 4) {
                return elem.nodeValue;
            }
            // Do not include comment or processing instruction nodes

            return ret;
        };

        Expr = Sizzle.selectors = {

            // Can be adjusted by the user
            cacheLength: 50,

            createPseudo: markFunction,

            match: matchExpr,

            attrHandle: {},

            find: {},

            relative: {
                ">": {dir: "parentNode", first: true},
                " ": {dir: "parentNode"},
                "+": {dir: "previousSibling", first: true},
                "~": {dir: "previousSibling"}
            },

            preFilter: {
                "ATTR": function (match) {
                    match[1] = match[1].replace(runescape, funescape);

                    // Move the given value to match[3] whether quoted or unquoted
                    match[3] = ( match[4] || match[5] || "" ).replace(runescape, funescape);

                    if (match[2] === "~=") {
                        match[3] = " " + match[3] + " ";
                    }

                    return match.slice(0, 4);
                },

                "CHILD": function (match) {
                    /* matches from matchExpr["CHILD"]
                     1 type (only|nth|...)
                     2 what (child|of-type)
                     3 argument (even|odd|\d*|\d*n([+-]\d+)?|...)
                     4 xn-component of xn+y argument ([+-]?\d*n|)
                     5 sign of xn-component
                     6 x of xn-component
                     7 sign of y-component
                     8 y of y-component
                     */
                    match[1] = match[1].toLowerCase();

                    if (match[1].slice(0, 3) === "nth") {
                        // nth-* requires argument
                        if (!match[3]) {
                            Sizzle.error(match[0]);
                        }

                        // numeric x and y parameters for Expr.filter.CHILD
                        // remember that false/true cast respectively to 0/1
                        match[4] = +( match[4] ? match[5] + (match[6] || 1) : 2 * ( match[3] === "even" || match[3] === "odd" ) );
                        match[5] = +( ( match[7] + match[8] ) || match[3] === "odd" );

                        // other types prohibit arguments
                    } else if (match[3]) {
                        Sizzle.error(match[0]);
                    }

                    return match;
                },

                "PSEUDO": function (match) {
                    var excess,
                        unquoted = !match[5] && match[2];

                    if (matchExpr["CHILD"].test(match[0])) {
                        return null;
                    }

                    // Accept quoted arguments as-is
                    if (match[3] && match[4] !== undefined) {
                        match[2] = match[4];

                        // Strip excess characters from unquoted arguments
                    } else if (unquoted && rpseudo.test(unquoted) &&
                            // Get excess from tokenize (recursively)
                        (excess = tokenize(unquoted, true)) &&
                            // advance to the next closing parenthesis
                        (excess = unquoted.indexOf(")", unquoted.length - excess) - unquoted.length)) {

                        // excess is a negative index
                        match[0] = match[0].slice(0, excess);
                        match[2] = unquoted.slice(0, excess);
                    }

                    // Return only captures needed by the pseudo filter method (type and argument)
                    return match.slice(0, 3);
                }
            },

            filter: {

                "TAG": function (nodeNameSelector) {
                    var nodeName = nodeNameSelector.replace(runescape, funescape).toLowerCase();
                    return nodeNameSelector === "*" ?
                        function () { return true; } :
                        function (elem) {
                            return elem.nodeName && elem.nodeName.toLowerCase() === nodeName;
                        };
                },

                "CLASS": function (className) {
                    var pattern = classCache[className + " "];

                    return pattern ||
                        (pattern = new RegExp("(^|" + whitespace + ")" + className + "(" + whitespace + "|$)")) &&
                        classCache(className, function (elem) {
                            return pattern.test(typeof elem.className === "string" && elem.className || typeof elem.getAttribute !== strundefined && elem.getAttribute("class") || "");
                        });
                },

                "ATTR": function (name, operator, check) {
                    return function (elem) {
                        var result = Sizzle.attr(elem, name);

                        if (result == null) {
                            return operator === "!=";
                        }
                        if (!operator) {
                            return true;
                        }

                        result += "";

                        return operator === "=" ? result === check :
                            operator === "!=" ? result !== check :
                                operator === "^=" ? check && result.indexOf(check) === 0 :
                                    operator === "*=" ? check && result.indexOf(check) > -1 :
                                        operator === "$=" ? check && result.slice(-check.length) === check :
                                            operator === "~=" ? ( " " + result + " " ).indexOf(check) > -1 :
                                                operator === "|=" ? result === check || result.slice(0, check.length + 1) === check + "-" :
                                                    false;
                    };
                },

                "CHILD": function (type, what, argument, first, last) {
                    var simple = type.slice(0, 3) !== "nth",
                        forward = type.slice(-4) !== "last",
                        ofType = what === "of-type";

                    return first === 1 && last === 0 ?

                        // Shortcut for :nth-*(n)
                        function (elem) {
                            return !!elem.parentNode;
                        } :

                        function (elem, context, xml) {
                            var cache, outerCache, node, diff, nodeIndex, start,
                                dir = simple !== forward ? "nextSibling" : "previousSibling",
                                parent = elem.parentNode,
                                name = ofType && elem.nodeName.toLowerCase(),
                                useCache = !xml && !ofType;

                            if (parent) {

                                // :(first|last|only)-(child|of-type)
                                if (simple) {
                                    while (dir) {
                                        node = elem;
                                        while ((node = node[dir])) {
                                            if (ofType ? node.nodeName.toLowerCase() === name : node.nodeType === 1) {
                                                return false;
                                            }
                                        }
                                        // Reverse direction for :only-* (if we haven't yet done so)
                                        start = dir = type === "only" && !start && "nextSibling";
                                    }
                                    return true;
                                }

                                start = [forward ? parent.firstChild : parent.lastChild];

                                // non-xml :nth-child(...) stores cache data on `parent`
                                if (forward && useCache) {
                                    // Seek `elem` from a previously-cached index
                                    outerCache = parent[expando] || (parent[expando] = {});
                                    cache = outerCache[type] || [];
                                    nodeIndex = cache[0] === dirruns && cache[1];
                                    diff = cache[0] === dirruns && cache[2];
                                    node = nodeIndex && parent.childNodes[nodeIndex];

                                    while ((node = ++nodeIndex && node && node[dir] ||

                                            // Fallback to seeking `elem` from the start
                                        (diff = nodeIndex = 0) || start.pop())) {

                                        // When found, cache indexes on `parent` and break
                                        if (node.nodeType === 1 && ++diff && node === elem) {
                                            outerCache[type] = [dirruns, nodeIndex, diff];
                                            break;
                                        }
                                    }

                                    // Use previously-cached element index if available
                                } else if (useCache && (cache = (elem[expando] || (elem[expando] = {}))[type]) && cache[0] === dirruns) {
                                    diff = cache[1];

                                    // xml :nth-child(...) or :nth-last-child(...) or :nth(-last)?-of-type(...)
                                } else {
                                    // Use the same loop as above to seek `elem` from the start
                                    while ((node = ++nodeIndex && node && node[dir] ||
                                        (diff = nodeIndex = 0) || start.pop())) {

                                        if (( ofType ? node.nodeName.toLowerCase() === name : node.nodeType === 1 ) && ++diff) {
                                            // Cache the index of each encountered element
                                            if (useCache) {
                                                (node[expando] || (node[expando] = {}))[type] = [dirruns, diff];
                                            }

                                            if (node === elem) {
                                                break;
                                            }
                                        }
                                    }
                                }

                                // Incorporate the offset, then check against cycle size
                                diff -= last;
                                return diff === first || ( diff % first === 0 && diff / first >= 0 );
                            }
                        };
                },

                "PSEUDO": function (pseudo, argument) {
                    // pseudo-class names are case-insensitive
                    // http://www.w3.org/TR/selectors/#pseudo-classes
                    // Prioritize by case sensitivity in case custom pseudos are added with uppercase letters
                    // Remember that setFilters inherits from pseudos
                    var args,
                        fn = Expr.pseudos[pseudo] || Expr.setFilters[pseudo.toLowerCase()] ||
                            Sizzle.error("unsupported pseudo: " + pseudo);

                    // The user may use createPseudo to indicate that
                    // arguments are needed to create the filter function
                    // just as Sizzle does
                    if (fn[expando]) {
                        return fn(argument);
                    }

                    // But maintain support for old signatures
                    if (fn.length > 1) {
                        args = [pseudo, pseudo, "", argument];
                        return Expr.setFilters.hasOwnProperty(pseudo.toLowerCase()) ?
                            markFunction(function (seed, matches) {
                                var idx,
                                    matched = fn(seed, argument),
                                    i = matched.length;
                                while (i--) {
                                    idx = indexOf.call(seed, matched[i]);
                                    seed[idx] = !( matches[idx] = matched[i] );
                                }
                            }) :
                            function (elem) {
                                return fn(elem, 0, args);
                            };
                    }

                    return fn;
                }
            },

            pseudos: {
                // Potentially complex pseudos
                "not": markFunction(function (selector) {
                    // Trim the selector passed to compile
                    // to avoid treating leading and trailing
                    // spaces as combinators
                    var input = [],
                        results = [],
                        matcher = compile(selector.replace(rtrim, "$1"));

                    return matcher[expando] ?
                        markFunction(function (seed, matches, context, xml) {
                            var elem,
                                unmatched = matcher(seed, null, xml, []),
                                i = seed.length;

                            // Match elements unmatched by `matcher`
                            while (i--) {
                                if ((elem = unmatched[i])) {
                                    seed[i] = !(matches[i] = elem);
                                }
                            }
                        }) :
                        function (elem, context, xml) {
                            input[0] = elem;
                            matcher(input, null, xml, results);
                            return !results.pop();
                        };
                }),

                "has": markFunction(function (selector) {
                    return function (elem) {
                        return Sizzle(selector, elem).length > 0;
                    };
                }),

                "contains": markFunction(function (text) {
                    return function (elem) {
                        return ( elem.textContent || elem.innerText || getText(elem) ).indexOf(text) > -1;
                    };
                }),

                // "Whether an element is represented by a :lang() selector
                // is based solely on the element's language value
                // being equal to the identifier C,
                // or beginning with the identifier C immediately followed by "-".
                // The matching of C against the element's language value is performed case-insensitively.
                // The identifier C does not have to be a valid language name."
                // http://www.w3.org/TR/selectors/#lang-pseudo
                "lang": markFunction(function (lang) {
                    // lang value must be a valid identifier
                    if (!ridentifier.test(lang || "")) {
                        Sizzle.error("unsupported lang: " + lang);
                    }
                    lang = lang.replace(runescape, funescape).toLowerCase();
                    return function (elem) {
                        var elemLang;
                        do {
                            if ((elemLang = documentIsHTML ?
                                    elem.lang :
                                elem.getAttribute("xml:lang") || elem.getAttribute("lang"))) {

                                elemLang = elemLang.toLowerCase();
                                return elemLang === lang || elemLang.indexOf(lang + "-") === 0;
                            }
                        } while ((elem = elem.parentNode) && elem.nodeType === 1);
                        return false;
                    };
                }),

                // Miscellaneous
                "target": function (elem) {
                    var hash = window.location && window.location.hash;
                    return hash && hash.slice(1) === elem.id;
                },

                "root": function (elem) {
                    return elem === docElem;
                },

                "focus": function (elem) {
                    return elem === document.activeElement && (!document.hasFocus || document.hasFocus()) && !!(elem.type || elem.href || ~elem.tabIndex);
                },

                // Boolean properties
                "enabled": function (elem) {
                    return elem.disabled === false;
                },

                "disabled": function (elem) {
                    return elem.disabled === true;
                },

                "checked": function (elem) {
                    // In CSS3, :checked should return both checked and selected elements
                    // http://www.w3.org/TR/2011/REC-css3-selectors-20110929/#checked
                    var nodeName = elem.nodeName.toLowerCase();
                    return (nodeName === "input" && !!elem.checked) || (nodeName === "option" && !!elem.selected);
                },

                "selected": function (elem) {
                    // Accessing this property makes selected-by-default
                    // options in Safari work properly
                    if (elem.parentNode) {
                        elem.parentNode.selectedIndex;
                    }

                    return elem.selected === true;
                },

                // Contents
                "empty": function (elem) {
                    // http://www.w3.org/TR/selectors/#empty-pseudo
                    // :empty is only affected by element nodes and content nodes(including text(3), cdata(4)),
                    //   not comment, processing instructions, or others
                    // Thanks to Diego Perini for the nodeName shortcut
                    //   Greater than "@" means alpha characters (specifically not starting with "#" or "?")
                    for (elem = elem.firstChild; elem; elem = elem.nextSibling) {
                        if (elem.nodeName > "@" || elem.nodeType === 3 || elem.nodeType === 4) {
                            return false;
                        }
                    }
                    return true;
                },

                "parent": function (elem) {
                    return !Expr.pseudos["empty"](elem);
                },

                // Element/input types
                "header": function (elem) {
                    return rheader.test(elem.nodeName);
                },

                "input": function (elem) {
                    return rinputs.test(elem.nodeName);
                },

                "button": function (elem) {
                    var name = elem.nodeName.toLowerCase();
                    return name === "input" && elem.type === "button" || name === "button";
                },

                "text": function (elem) {
                    var attr;
                    // IE6 and 7 will map elem.type to 'text' for new HTML5 types (search, etc)
                    // use getAttribute instead to test this case
                    return elem.nodeName.toLowerCase() === "input" &&
                        elem.type === "text" &&
                        ( (attr = elem.getAttribute("type")) == null || attr.toLowerCase() === elem.type );
                },

                // Position-in-collection
                "first": createPositionalPseudo(function () {
                    return [0];
                }),

                "last": createPositionalPseudo(function (matchIndexes, length) {
                    return [length - 1];
                }),

                "eq": createPositionalPseudo(function (matchIndexes, length, argument) {
                    return [argument < 0 ? argument + length : argument];
                }),

                "even": createPositionalPseudo(function (matchIndexes, length) {
                    var i = 0;
                    for (; i < length; i += 2) {
                        matchIndexes.push(i);
                    }
                    return matchIndexes;
                }),

                "odd": createPositionalPseudo(function (matchIndexes, length) {
                    var i = 1;
                    for (; i < length; i += 2) {
                        matchIndexes.push(i);
                    }
                    return matchIndexes;
                }),

                "lt": createPositionalPseudo(function (matchIndexes, length, argument) {
                    var i = argument < 0 ? argument + length : argument;
                    for (; --i >= 0;) {
                        matchIndexes.push(i);
                    }
                    return matchIndexes;
                }),

                "gt": createPositionalPseudo(function (matchIndexes, length, argument) {
                    var i = argument < 0 ? argument + length : argument;
                    for (; ++i < length;) {
                        matchIndexes.push(i);
                    }
                    return matchIndexes;
                })
            }
        };

        Expr.pseudos["nth"] = Expr.pseudos["eq"];

        // Add button/input type pseudos
        for (i in {radio: true, checkbox: true, file: true, password: true, image: true}) {
            Expr.pseudos[i] = createInputPseudo(i);
        }
        for (i in {submit: true, reset: true}) {
            Expr.pseudos[i] = createButtonPseudo(i);
        }

        // Easy API for creating new setFilters
        function setFilters() {}

        setFilters.prototype = Expr.filters = Expr.pseudos;
        Expr.setFilters = new setFilters();

        function tokenize(selector, parseOnly) {
            var matched, match, tokens, type,
                soFar, groups, preFilters,
                cached = tokenCache[selector + " "];

            if (cached) {
                return parseOnly ? 0 : cached.slice(0);
            }

            soFar = selector;
            groups = [];
            preFilters = Expr.preFilter;

            while (soFar) {

                // Comma and first run
                if (!matched || (match = rcomma.exec(soFar))) {
                    if (match) {
                        // Don't consume trailing commas as valid
                        soFar = soFar.slice(match[0].length) || soFar;
                    }
                    groups.push(tokens = []);
                }

                matched = false;

                // Combinators
                if ((match = rcombinators.exec(soFar))) {
                    matched = match.shift();
                    tokens.push({
                        value: matched,
                        // Cast descendant combinators to space
                        type: match[0].replace(rtrim, " ")
                    });
                    soFar = soFar.slice(matched.length);
                }

                // Filters
                for (type in Expr.filter) {
                    if ((match = matchExpr[type].exec(soFar)) && (!preFilters[type] ||
                        (match = preFilters[type](match)))) {
                        matched = match.shift();
                        tokens.push({
                            value: matched,
                            type: type,
                            matches: match
                        });
                        soFar = soFar.slice(matched.length);
                    }
                }

                if (!matched) {
                    break;
                }
            }

            // Return the length of the invalid excess
            // if we're just parsing
            // Otherwise, throw an error or return tokens
            return parseOnly ?
                soFar.length :
                soFar ?
                    Sizzle.error(selector) :
                    // Cache the tokens
                    tokenCache(selector, groups).slice(0);
        }

        function toSelector(tokens) {
            var i = 0,
                len = tokens.length,
                selector = "";
            for (; i < len; i++) {
                selector += tokens[i].value;
            }
            return selector;
        }

        function addCombinator(matcher, combinator, base) {
            var dir = combinator.dir,
                checkNonElements = base && dir === "parentNode",
                doneName = done++;

            return combinator.first ?
                // Check against closest ancestor/preceding element
                function (elem, context, xml) {
                    while ((elem = elem[dir])) {
                        if (elem.nodeType === 1 || checkNonElements) {
                            return matcher(elem, context, xml);
                        }
                    }
                } :

                // Check against all ancestor/preceding elements
                function (elem, context, xml) {
                    var data, cache, outerCache,
                        dirkey = dirruns + " " + doneName;

                    // We can't set arbitrary data on XML nodes, so they don't benefit from dir caching
                    if (xml) {
                        while ((elem = elem[dir])) {
                            if (elem.nodeType === 1 || checkNonElements) {
                                if (matcher(elem, context, xml)) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        while ((elem = elem[dir])) {
                            if (elem.nodeType === 1 || checkNonElements) {
                                outerCache = elem[expando] || (elem[expando] = {});
                                if ((cache = outerCache[dir]) && cache[0] === dirkey) {
                                    if ((data = cache[1]) === true || data === cachedruns) {
                                        return data === true;
                                    }
                                } else {
                                    cache = outerCache[dir] = [dirkey];
                                    cache[1] = matcher(elem, context, xml) || cachedruns;
                                    if (cache[1] === true) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                };
        }

        function elementMatcher(matchers) {
            return matchers.length > 1 ?
                function (elem, context, xml) {
                    var i = matchers.length;
                    while (i--) {
                        if (!matchers[i](elem, context, xml)) {
                            return false;
                        }
                    }
                    return true;
                } :
                matchers[0];
        }

        function condense(unmatched, map, filter, context, xml) {
            var elem,
                newUnmatched = [],
                i = 0,
                len = unmatched.length,
                mapped = map != null;

            for (; i < len; i++) {
                if ((elem = unmatched[i])) {
                    if (!filter || filter(elem, context, xml)) {
                        newUnmatched.push(elem);
                        if (mapped) {
                            map.push(i);
                        }
                    }
                }
            }

            return newUnmatched;
        }

        function setMatcher(preFilter, selector, matcher, postFilter, postFinder, postSelector) {
            if (postFilter && !postFilter[expando]) {
                postFilter = setMatcher(postFilter);
            }
            if (postFinder && !postFinder[expando]) {
                postFinder = setMatcher(postFinder, postSelector);
            }
            return markFunction(function (seed, results, context, xml) {
                var temp, i, elem,
                    preMap = [],
                    postMap = [],
                    preexisting = results.length,

                // Get initial elements from seed or context
                    elems = seed || multipleContexts(selector || "*", context.nodeType ? [context] : context, []),

                // Prefilter to get matcher input, preserving a map for seed-results synchronization
                    matcherIn = preFilter && ( seed || !selector ) ?
                        condense(elems, preMap, preFilter, context, xml) :
                        elems,

                    matcherOut = matcher ?
                        // If we have a postFinder, or filtered seed, or non-seed postFilter or preexisting results,
                        postFinder || ( seed ? preFilter : preexisting || postFilter ) ?

                            // ...intermediate processing is necessary
                            [] :

                            // ...otherwise use results directly
                            results :
                        matcherIn;

                // Find primary matches
                if (matcher) {
                    matcher(matcherIn, matcherOut, context, xml);
                }

                // Apply postFilter
                if (postFilter) {
                    temp = condense(matcherOut, postMap);
                    postFilter(temp, [], context, xml);

                    // Un-match failing elements by moving them back to matcherIn
                    i = temp.length;
                    while (i--) {
                        if ((elem = temp[i])) {
                            matcherOut[postMap[i]] = !(matcherIn[postMap[i]] = elem);
                        }
                    }
                }

                if (seed) {
                    if (postFinder || preFilter) {
                        if (postFinder) {
                            // Get the final matcherOut by condensing this intermediate into postFinder contexts
                            temp = [];
                            i = matcherOut.length;
                            while (i--) {
                                if ((elem = matcherOut[i])) {
                                    // Restore matcherIn since elem is not yet a final match
                                    temp.push((matcherIn[i] = elem));
                                }
                            }
                            postFinder(null, (matcherOut = []), temp, xml);
                        }

                        // Move matched elements from seed to results to keep them synchronized
                        i = matcherOut.length;
                        while (i--) {
                            if ((elem = matcherOut[i]) &&
                                (temp = postFinder ? indexOf.call(seed, elem) : preMap[i]) > -1) {

                                seed[temp] = !(results[temp] = elem);
                            }
                        }
                    }

                    // Add elements to results, through postFinder if defined
                } else {
                    matcherOut = condense(
                        matcherOut === results ?
                            matcherOut.splice(preexisting, matcherOut.length) :
                            matcherOut
                    );
                    if (postFinder) {
                        postFinder(null, results, matcherOut, xml);
                    } else {
                        push.apply(results, matcherOut);
                    }
                }
            });
        }

        function matcherFromTokens(tokens) {
            var checkContext, matcher, j,
                len = tokens.length,
                leadingRelative = Expr.relative[tokens[0].type],
                implicitRelative = leadingRelative || Expr.relative[" "],
                i = leadingRelative ? 1 : 0,

            // The foundational matcher ensures that elements are reachable from top-level context(s)
                matchContext = addCombinator(function (elem) {
                    return elem === checkContext;
                }, implicitRelative, true),
                matchAnyContext = addCombinator(function (elem) {
                    return indexOf.call(checkContext, elem) > -1;
                }, implicitRelative, true),
                matchers = [function (elem, context, xml) {
                    return ( !leadingRelative && ( xml || context !== outermostContext ) ) || (
                            (checkContext = context).nodeType ?
                                matchContext(elem, context, xml) :
                                matchAnyContext(elem, context, xml) );
                }];

            for (; i < len; i++) {
                if ((matcher = Expr.relative[tokens[i].type])) {
                    matchers = [addCombinator(elementMatcher(matchers), matcher)];
                } else {
                    matcher = Expr.filter[tokens[i].type].apply(null, tokens[i].matches);

                    // Return special upon seeing a positional matcher
                    if (matcher[expando]) {
                        // Find the next relative operator (if any) for proper handling
                        j = ++i;
                        for (; j < len; j++) {
                            if (Expr.relative[tokens[j].type]) {
                                break;
                            }
                        }
                        return setMatcher(
                            i > 1 && elementMatcher(matchers),
                            i > 1 && toSelector(
                                // If the preceding token was a descendant combinator, insert an implicit any-element
                                // `*`
                                tokens.slice(0, i - 1).concat({value: tokens[i - 2].type === " " ? "*" : ""})
                            ).replace(rtrim, "$1"),
                            matcher,
                            i < j && matcherFromTokens(tokens.slice(i, j)),
                            j < len && matcherFromTokens((tokens = tokens.slice(j))),
                            j < len && toSelector(tokens)
                        );
                    }
                    matchers.push(matcher);
                }
            }

            return elementMatcher(matchers);
        }

        function matcherFromGroupMatchers(elementMatchers, setMatchers) {
            // A counter to specify which element is currently being matched
            var matcherCachedRuns = 0,
                bySet = setMatchers.length > 0,
                byElement = elementMatchers.length > 0,
                superMatcher = function (seed, context, xml, results, expandContext) {
                    var elem, j, matcher,
                        setMatched = [],
                        matchedCount = 0,
                        i = "0",
                        unmatched = seed && [],
                        outermost = expandContext != null,
                        contextBackup = outermostContext,
                    // We must always have either seed elements or context
                        elems = seed || byElement && Expr.find["TAG"]("*", expandContext && context.parentNode || context),
                    // Use integer dirruns iff this is the outermost matcher
                        dirrunsUnique = (dirruns += contextBackup == null ? 1 : Math.random() || 0.1);

                    if (outermost) {
                        outermostContext = context !== document && context;
                        cachedruns = matcherCachedRuns;
                    }

                    // Add elements passing elementMatchers directly to results
                    // Keep `i` a string if there are no elements so `matchedCount` will be "00" below
                    for (; (elem = elems[i]) != null; i++) {
                        if (byElement && elem) {
                            j = 0;
                            while ((matcher = elementMatchers[j++])) {
                                if (matcher(elem, context, xml)) {
                                    results.push(elem);
                                    break;
                                }
                            }
                            if (outermost) {
                                dirruns = dirrunsUnique;
                                cachedruns = ++matcherCachedRuns;
                            }
                        }

                        // Track unmatched elements for set filters
                        if (bySet) {
                            // They will have gone through all possible matchers
                            if ((elem = !matcher && elem)) {
                                matchedCount--;
                            }

                            // Lengthen the array for every element, matched or not
                            if (seed) {
                                unmatched.push(elem);
                            }
                        }
                    }

                    // Apply set filters to unmatched elements
                    matchedCount += i;
                    if (bySet && i !== matchedCount) {
                        j = 0;
                        while ((matcher = setMatchers[j++])) {
                            matcher(unmatched, setMatched, context, xml);
                        }

                        if (seed) {
                            // Reintegrate element matches to eliminate the need for sorting
                            if (matchedCount > 0) {
                                while (i--) {
                                    if (!(unmatched[i] || setMatched[i])) {
                                        setMatched[i] = pop.call(results);
                                    }
                                }
                            }

                            // Discard index placeholder values to get only actual matches
                            setMatched = condense(setMatched);
                        }

                        // Add matches to results
                        push.apply(results, setMatched);

                        // Seedless set matches succeeding multiple successful matchers stipulate sorting
                        if (outermost && !seed && setMatched.length > 0 &&
                            ( matchedCount + setMatchers.length ) > 1) {

                            Sizzle.uniqueSort(results);
                        }
                    }

                    // Override manipulation of globals by nested matchers
                    if (outermost) {
                        dirruns = dirrunsUnique;
                        outermostContext = contextBackup;
                    }

                    return unmatched;
                };

            return bySet ?
                markFunction(superMatcher) :
                superMatcher;
        }

        compile = Sizzle.compile = function (selector, group /* Internal Use Only */) {
            var i,
                setMatchers = [],
                elementMatchers = [],
                cached = compilerCache[selector + " "];

            if (!cached) {
                // Generate a function of recursive functions that can be used to check each element
                if (!group) {
                    group = tokenize(selector);
                }
                i = group.length;
                while (i--) {
                    cached = matcherFromTokens(group[i]);
                    if (cached[expando]) {
                        setMatchers.push(cached);
                    } else {
                        elementMatchers.push(cached);
                    }
                }

                // Cache the compiled function
                cached = compilerCache(selector, matcherFromGroupMatchers(elementMatchers, setMatchers));
            }
            return cached;
        };

        function multipleContexts(selector, contexts, results) {
            var i = 0,
                len = contexts.length;
            for (; i < len; i++) {
                Sizzle(selector, contexts[i], results);
            }
            return results;
        }

        function select(selector, context, results, seed) {
            var i, tokens, token, type, find,
                match = tokenize(selector);

            if (!seed) {
                // Try to minimize operations if there is only one group
                if (match.length === 1) {

                    // Take a shortcut and set the context if the root selector is an ID
                    tokens = match[0] = match[0].slice(0);
                    if (tokens.length > 2 && (token = tokens[0]).type === "ID" &&
                        support.getById && context.nodeType === 9 && documentIsHTML &&
                        Expr.relative[tokens[1].type]) {

                        context = ( Expr.find["ID"](token.matches[0].replace(runescape, funescape), context) || [] )[0];
                        if (!context) {
                            return results;
                        }
                        selector = selector.slice(tokens.shift().value.length);
                    }

                    // Fetch a seed set for right-to-left matching
                    i = matchExpr["needsContext"].test(selector) ? 0 : tokens.length;
                    while (i--) {
                        token = tokens[i];

                        // Abort if we hit a combinator
                        if (Expr.relative[(type = token.type)]) {
                            break;
                        }
                        if ((find = Expr.find[type])) {
                            // Search, expanding context for leading sibling combinators
                            if ((seed = find(
                                    token.matches[0].replace(runescape, funescape),
                                    rsibling.test(tokens[0].type) && context.parentNode || context
                                ))) {

                                // If seed is empty or no tokens remain, we can return early
                                tokens.splice(i, 1);
                                selector = seed.length && toSelector(tokens);
                                if (!selector) {
                                    push.apply(results, seed);
                                    return results;
                                }

                                break;
                            }
                        }
                    }
                }
            }

            // Compile and execute a filtering function
            // Provide `match` to avoid retokenization if we modified the selector above
            compile(selector, match)(
                seed,
                context,
                !documentIsHTML,
                results,
                rsibling.test(selector)
            );
            return results;
        }

        // One-time assignments

        // Sort stability
        support.sortStable = expando.split("").sort(sortOrder).join("") === expando;

        // Support: Chrome<14
        // Always assume duplicates if they aren't passed to the comparison function
        support.detectDuplicates = hasDuplicate;

        // Initialize against the default document
        setDocument();

        // Support: Webkit<537.32 - Safari 6.0.3/Chrome 25 (fixed in Chrome 27)
        // Detached nodes confoundingly follow *each other*
        support.sortDetached = assert(function (div1) {
            // Should return 1, but returns 4 (following)
            return div1.compareDocumentPosition(document.createElement("div")) & 1;
        });

        // Support: IE<8
        // Prevent attribute/property "interpolation"
        // http://msdn.microsoft.com/en-us/library/ms536429%28VS.85%29.aspx
        if (!assert(function (div) {
                div.innerHTML = "<a href='#'></a>";
                return div.firstChild.getAttribute("href") === "#";
            })) {
            addHandle("type|href|height|width", function (elem, name, isXML) {
                if (!isXML) {
                    return elem.getAttribute(name, name.toLowerCase() === "type" ? 1 : 2);
                }
            });
        }

        // Support: IE<9
        // Use defaultValue in place of getAttribute("value")
        if (!support.attributes || !assert(function (div) {
                div.innerHTML = "<input/>";
                div.firstChild.setAttribute("value", "");
                return div.firstChild.getAttribute("value") === "";
            })) {
            addHandle("value", function (elem, name, isXML) {
                if (!isXML && elem.nodeName.toLowerCase() === "input") {
                    return elem.defaultValue;
                }
            });
        }

        // Support: IE<9
        // Use getAttributeNode to fetch booleans when getAttribute lies
        if (!assert(function (div) {
                return div.getAttribute("disabled") == null;
            })) {
            addHandle(booleans, function (elem, name, isXML) {
                var val;
                if (!isXML) {
                    return (val = elem.getAttributeNode(name)) && val.specified ?
                        val.value :
                        elem[name] === true ? name.toLowerCase() : null;
                }
            });
        }

        jQuery.find = Sizzle;
        jQuery.expr = Sizzle.selectors;
        jQuery.expr[":"] = jQuery.expr.pseudos;
        jQuery.unique = Sizzle.uniqueSort;
        jQuery.text = Sizzle.getText;
        jQuery.isXMLDoc = Sizzle.isXML;
        jQuery.contains = Sizzle.contains;

    })(window);
    // String to Object options format cache
    var optionsCache = {};

    // Convert String-formatted options into Object-formatted ones and store in cache
    function createOptions(options) {
        var object = optionsCache[options] = {};
        jQuery.each(options.match(core_rnotwhite) || [], function (_, flag) {
            object[flag] = true;
        });
        return object;
    }

    /*
     * Create a callback list using the following parameters:
     *
     *	options: an optional list of space-separated options that will change how
     *			the callback list behaves or a more traditional option object
     *
     * By default a callback list will act like an event callback list and can be
     * "fired" multiple times.
     *
     * Possible options:
     *
     *	once:			will ensure the callback list can only be fired once (like a Deferred)
     *
     *	memory:			will keep track of previous values and will call any callback added
     *					after the list has been fired right away with the latest "memorized"
     *					values (like a Deferred)
     *
     *	unique:			will ensure a callback can only be added once (no duplicate in the list)
     *
     *	stopOnFalse:	interrupt callings when a callback returns false
     *
     */
    jQuery.Callbacks = function (options) {

        // Convert options from String-formatted to Object-formatted if needed
        // (we check in cache first)
        options = typeof options === "string" ?
            ( optionsCache[options] || createOptions(options) ) :
            jQuery.extend({}, options);

        var // Last fire value (for non-forgettable lists)
            memory,
        // Flag to know if list was already fired
            fired,
        // Flag to know if list is currently firing
            firing,
        // First callback to fire (used internally by add and fireWith)
            firingStart,
        // End of the loop when firing
            firingLength,
        // Index of currently firing callback (modified by remove if needed)
            firingIndex,
        // Actual callback list
            list = [],
        // Stack of fire calls for repeatable lists
            stack = !options.once && [],
        // Fire callbacks
            fire = function (data) {
                memory = options.memory && data;
                fired = true;
                firingIndex = firingStart || 0;
                firingStart = 0;
                firingLength = list.length;
                firing = true;
                for (; list && firingIndex < firingLength; firingIndex++) {
                    if (list[firingIndex].apply(data[0], data[1]) === false && options.stopOnFalse) {
                        memory = false; // To prevent further calls using add
                        break;
                    }
                }
                firing = false;
                if (list) {
                    if (stack) {
                        if (stack.length) {
                            fire(stack.shift());
                        }
                    } else if (memory) {
                        list = [];
                    } else {
                        self.disable();
                    }
                }
            },
        // Actual Callbacks object
            self = {
                // Add a callback or a collection of callbacks to the list
                add: function () {
                    if (list) {
                        // First, we save the current length
                        var start = list.length;
                        (function add(args) {
                            jQuery.each(args, function (_, arg) {
                                var type = jQuery.type(arg);
                                if (type === "function") {
                                    if (!options.unique || !self.has(arg)) {
                                        list.push(arg);
                                    }
                                } else if (arg && arg.length && type !== "string") {
                                    // Inspect recursively
                                    add(arg);
                                }
                            });
                        })(arguments);
                        // Do we need to add the callbacks to the
                        // current firing batch?
                        if (firing) {
                            firingLength = list.length;
                            // With memory, if we're not firing then
                            // we should call right away
                        } else if (memory) {
                            firingStart = start;
                            fire(memory);
                        }
                    }
                    return this;
                },
                // Remove a callback from the list
                remove: function () {
                    if (list) {
                        jQuery.each(arguments, function (_, arg) {
                            var index;
                            while (( index = jQuery.inArray(arg, list, index) ) > -1) {
                                list.splice(index, 1);
                                // Handle firing indexes
                                if (firing) {
                                    if (index <= firingLength) {
                                        firingLength--;
                                    }
                                    if (index <= firingIndex) {
                                        firingIndex--;
                                    }
                                }
                            }
                        });
                    }
                    return this;
                },
                // Check if a given callback is in the list.
                // If no argument is given, return whether or not list has callbacks attached.
                has: function (fn) {
                    return fn ? jQuery.inArray(fn, list) > -1 : !!( list && list.length );
                },
                // Remove all callbacks from the list
                empty: function () {
                    list = [];
                    firingLength = 0;
                    return this;
                },
                // Have the list do nothing anymore
                disable: function () {
                    list = stack = memory = undefined;
                    return this;
                },
                // Is it disabled?
                disabled: function () {
                    return !list;
                },
                // Lock the list in its current state
                lock: function () {
                    stack = undefined;
                    if (!memory) {
                        self.disable();
                    }
                    return this;
                },
                // Is it locked?
                locked: function () {
                    return !stack;
                },
                // Call all callbacks with the given context and arguments
                fireWith: function (context, args) {
                    if (list && ( !fired || stack )) {
                        args = args || [];
                        args = [context, args.slice ? args.slice() : args];
                        if (firing) {
                            stack.push(args);
                        } else {
                            fire(args);
                        }
                    }
                    return this;
                },
                // Call all the callbacks with the given arguments
                fire: function () {
                    self.fireWith(this, arguments);
                    return this;
                },
                // To know if the callbacks have already been called at least once
                fired: function () {
                    return !!fired;
                }
            };

        return self;
    };
    jQuery.extend({

        Deferred: function (func) {
            var tuples = [
                    // action, add listener, listener list, final state
                    ["resolve", "done", jQuery.Callbacks("once memory"), "resolved"],
                    ["reject", "fail", jQuery.Callbacks("once memory"), "rejected"],
                    ["notify", "progress", jQuery.Callbacks("memory")]
                ],
                state = "pending",
                promise = {
                    state: function () {
                        return state;
                    },
                    always: function () {
                        deferred.done(arguments).fail(arguments);
                        return this;
                    },
                    then: function (/* fnDone, fnFail, fnProgress */) {
                        var fns = arguments;
                        return jQuery.Deferred(function (newDefer) {
                            jQuery.each(tuples, function (i, tuple) {
                                var action = tuple[0],
                                    fn = jQuery.isFunction(fns[i]) && fns[i];
                                // deferred[ done | fail | progress ] for forwarding actions to newDefer
                                deferred[tuple[1]](function () {
                                    var returned = fn && fn.apply(this, arguments);
                                    if (returned && jQuery.isFunction(returned.promise)) {
                                        returned.promise()
                                            .done(newDefer.resolve)
                                            .fail(newDefer.reject)
                                            .progress(newDefer.notify);
                                    } else {
                                        newDefer[action + "With"](this === promise ? newDefer.promise() : this, fn ? [returned] : arguments);
                                    }
                                });
                            });
                            fns = null;
                        }).promise();
                    },
                    // Get a promise for this deferred
                    // If obj is provided, the promise aspect is added to the object
                    promise: function (obj) {
                        return obj != null ? jQuery.extend(obj, promise) : promise;
                    }
                },
                deferred = {};

            // Keep pipe for back-compat
            promise.pipe = promise.then;

            // Add list-specific methods
            jQuery.each(tuples, function (i, tuple) {
                var list = tuple[2],
                    stateString = tuple[3];

                // promise[ done | fail | progress ] = list.add
                promise[tuple[1]] = list.add;

                // Handle state
                if (stateString) {
                    list.add(function () {
                        // state = [ resolved | rejected ]
                        state = stateString;

                        // [ reject_list | resolve_list ].disable; progress_list.lock
                    }, tuples[i ^ 1][2].disable, tuples[2][2].lock);
                }

                // deferred[ resolve | reject | notify ]
                deferred[tuple[0]] = function () {
                    deferred[tuple[0] + "With"](this === deferred ? promise : this, arguments);
                    return this;
                };
                deferred[tuple[0] + "With"] = list.fireWith;
            });

            // Make the deferred a promise
            promise.promise(deferred);

            // Call given func if any
            if (func) {
                func.call(deferred, deferred);
            }

            // All done!
            return deferred;
        },

        // Deferred helper
        when: function (subordinate /* , ..., subordinateN */) {
            var i = 0,
                resolveValues = core_slice.call(arguments),
                length = resolveValues.length,

            // the count of uncompleted subordinates
                remaining = length !== 1 || ( subordinate && jQuery.isFunction(subordinate.promise) ) ? length : 0,

            // the master Deferred. If resolveValues consist of only a single Deferred, just use that.
                deferred = remaining === 1 ? subordinate : jQuery.Deferred(),

            // Update function for both resolve and progress values
                updateFunc = function (i, contexts, values) {
                    return function (value) {
                        contexts[i] = this;
                        values[i] = arguments.length > 1 ? core_slice.call(arguments) : value;
                        if (values === progressValues) {
                            deferred.notifyWith(contexts, values);
                        } else if (!( --remaining )) {
                            deferred.resolveWith(contexts, values);
                        }
                    };
                },

                progressValues, progressContexts, resolveContexts;

            // add listeners to Deferred subordinates; treat others as resolved
            if (length > 1) {
                progressValues = new Array(length);
                progressContexts = new Array(length);
                resolveContexts = new Array(length);
                for (; i < length; i++) {
                    if (resolveValues[i] && jQuery.isFunction(resolveValues[i].promise)) {
                        resolveValues[i].promise()
                            .done(updateFunc(i, resolveContexts, resolveValues))
                            .fail(deferred.reject)
                            .progress(updateFunc(i, progressContexts, progressValues));
                    } else {
                        --remaining;
                    }
                }
            }

            // if we're not waiting on anything, resolve the master
            if (!remaining) {
                deferred.resolveWith(resolveContexts, resolveValues);
            }

            return deferred.promise();
        }
    });
    jQuery.support = (function (support) {
        var input = document.createElement("input"),
            fragment = document.createDocumentFragment(),
            div = document.createElement("div"),
            select = document.createElement("select"),
            opt = select.appendChild(document.createElement("option"));

        // Finish early in limited environments
        if (!input.type) {
            return support;
        }

        input.type = "checkbox";

        // Support: Safari 5.1, iOS 5.1, Android 4.x, Android 2.3
        // Check the default checkbox/radio value ("" on old WebKit; "on" elsewhere)
        support.checkOn = input.value !== "";

        // Must access the parent to make an option select properly
        // Support: IE9, IE10
        support.optSelected = opt.selected;

        // Will be defined later
        support.reliableMarginRight = true;
        support.boxSizingReliable = true;
        support.pixelPosition = false;

        // Make sure checked status is properly cloned
        // Support: IE9, IE10
        input.checked = true;
        support.noCloneChecked = input.cloneNode(true).checked;

        // Make sure that the options inside disabled selects aren't marked as disabled
        // (WebKit marks them as disabled)
        select.disabled = true;
        support.optDisabled = !opt.disabled;

        // Check if an input maintains its value after becoming a radio
        // Support: IE9, IE10
        input = document.createElement("input");
        input.value = "t";
        input.type = "radio";
        support.radioValue = input.value === "t";

        // #11217 - WebKit loses check when the name is after the checked attribute
        input.setAttribute("checked", "t");
        input.setAttribute("name", "t");

        fragment.appendChild(input);

        // Support: Safari 5.1, Android 4.x, Android 2.3
        // old WebKit doesn't clone checked state correctly in fragments
        support.checkClone = fragment.cloneNode(true).cloneNode(true).lastChild.checked;

        // Support: Firefox, Chrome, Safari
        // Beware of CSP restrictions (https://developer.mozilla.org/en/Security/CSP)
        support.focusinBubbles = "onfocusin" in window;

        div.style.backgroundClip = "content-box";
        div.cloneNode(true).style.backgroundClip = "";
        support.clearCloneStyle = div.style.backgroundClip === "content-box";

        // Run tests that need a body at doc ready
        jQuery(function () {
            var container, marginDiv,
            // Support: Firefox, Android 2.3 (Prefixed box-sizing versions).
                divReset = "padding:0;margin:0;border:0;display:block;-webkit-box-sizing:content-box;-moz-box-sizing:content-box;box-sizing:content-box",
                body = document.getElementsByTagName("body")[0];

            if (!body) {
                // Return for frameset docs that don't have a body
                return;
            }

            container = document.createElement("div");
            container.style.cssText = "border:0;width:0;height:0;position:absolute;top:0;left:-9999px;margin-top:1px";

            // Check box-sizing and margin behavior.
            body.appendChild(container).appendChild(div);
            div.innerHTML = "";
            // Support: Firefox, Android 2.3 (Prefixed box-sizing versions).
            div.style.cssText = "-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:1px;border:1px;display:block;width:4px;margin-top:1%;position:absolute;top:1%";

            // Workaround failing boxSizing test due to offsetWidth returning wrong value
            // with some non-1 values of body zoom, ticket #13543
            jQuery.swap(body, body.style.zoom != null ? {zoom: 1} : {}, function () {
                support.boxSizing = div.offsetWidth === 4;
            });

            // Use window.getComputedStyle because jsdom on node.js will break without it.
            if (window.getComputedStyle) {
                support.pixelPosition = ( window.getComputedStyle(div, null) || {} ).top !== "1%";
                support.boxSizingReliable = ( window.getComputedStyle(div, null) || {width: "4px"} ).width === "4px";

                // Support: Android 2.3
                // Check if div with explicit width and no margin-right incorrectly
                // gets computed margin-right based on width of container. (#3333)
                // WebKit Bug 13343 - getComputedStyle returns wrong value for margin-right
                marginDiv = div.appendChild(document.createElement("div"));
                marginDiv.style.cssText = div.style.cssText = divReset;
                marginDiv.style.marginRight = marginDiv.style.width = "0";
                div.style.width = "1px";

                support.reliableMarginRight = !parseFloat(( window.getComputedStyle(marginDiv, null) || {} ).marginRight);
            }

            body.removeChild(container);
        });

        return support;
    })({});

    /*
     Implementation Summary

     1. Enforce API surface and semantic compatibility with 1.9.x branch
     2. Improve the module's maintainability by reducing the storage
     paths to a single mechanism.
     3. Use the same single mechanism to support "private" and "user" data.
     4. _Never_ expose "private" data to user code (TODO: Drop _data, _removeData)
     5. Avoid exposing implementation details on user objects (eg. expando properties)
     6. Provide a clear path for implementation upgrade to WeakMap in 2014
     */
    var data_user, data_priv,
        rbrace = /(?:\{[\s\S]*\}|\[[\s\S]*\])$/,
        rmultiDash = /([A-Z])/g;

    function Data() {
        // Support: Android < 4,
        // Old WebKit does not have Object.preventExtensions/freeze method,
        // return new empty object instead with no [[set]] accessor
        Object.defineProperty(this.cache = {}, 0, {
            get: function () {
                return {};
            }
        });

        this.expando = jQuery.expando + Math.random();
    }

    Data.uid = 1;

    Data.accepts = function (owner) {
        // Accepts only:
        //  - Node
        //    - Node.ELEMENT_NODE
        //    - Node.DOCUMENT_NODE
        //  - Object
        //    - Any
        return owner.nodeType ?
        owner.nodeType === 1 || owner.nodeType === 9 : true;
    };

    Data.prototype = {
        key: function (owner) {
            // We can accept data for non-element nodes in modern browsers,
            // but we should not, see #8335.
            // Always return the key for a frozen object.
            if (!Data.accepts(owner)) {
                return 0;
            }

            var descriptor = {},
            // Check if the owner object already has a cache key
                unlock = owner[this.expando];

            // If not, create one
            if (!unlock) {
                unlock = Data.uid++;

                // Secure it in a non-enumerable, non-writable property
                try {
                    descriptor[this.expando] = {value: unlock};
                    Object.defineProperties(owner, descriptor);

                    // Support: Android < 4
                    // Fallback to a less secure definition
                } catch (e) {
                    descriptor[this.expando] = unlock;
                    jQuery.extend(owner, descriptor);
                }
            }

            // Ensure the cache object
            if (!this.cache[unlock]) {
                this.cache[unlock] = {};
            }

            return unlock;
        },
        set: function (owner, data, value) {
            var prop,
            // There may be an unlock assigned to this node,
            // if there is no entry for this "owner", create one inline
            // and set the unlock as though an owner entry had always existed
                unlock = this.key(owner),
                cache = this.cache[unlock];

            // Handle: [ owner, key, value ] args
            if (typeof data === "string") {
                cache[data] = value;

                // Handle: [ owner, { properties } ] args
            } else {
                // Fresh assignments by object are shallow copied
                if (jQuery.isEmptyObject(cache)) {
                    jQuery.extend(this.cache[unlock], data);
                    // Otherwise, copy the properties one-by-one to the cache object
                } else {
                    for (prop in data) {
                        cache[prop] = data[prop];
                    }
                }
            }
            return cache;
        },
        get: function (owner, key) {
            // Either a valid cache is found, or will be created.
            // New caches will be created and the unlock returned,
            // allowing direct access to the newly created
            // empty data object. A valid owner object must be provided.
            var cache = this.cache[this.key(owner)];

            return key === undefined ?
                cache : cache[key];
        },
        access: function (owner, key, value) {
            var stored;
            // In cases where either:
            //
            //   1. No key was specified
            //   2. A string key was specified, but no value provided
            //
            // Take the "read" path and allow the get method to determine
            // which value to return, respectively either:
            //
            //   1. The entire cache object
            //   2. The data stored at the key
            //
            if (key === undefined ||
                ((key && typeof key === "string") && value === undefined)) {

                stored = this.get(owner, key);

                return stored !== undefined ?
                    stored : this.get(owner, jQuery.camelCase(key));
            }

            // [*]When the key is not a string, or both a key and value
            // are specified, set or extend (existing objects) with either:
            //
            //   1. An object of properties
            //   2. A key and value
            //
            this.set(owner, key, value);

            // Since the "set" path can have two possible entry points
            // return the expected data based on which path was taken[*]
            return value !== undefined ? value : key;
        },
        remove: function (owner, key) {
            var i, name, camel,
                unlock = this.key(owner),
                cache = this.cache[unlock];

            if (key === undefined) {
                this.cache[unlock] = {};

            } else {
                // Support array or space separated string of keys
                if (jQuery.isArray(key)) {
                    // If "name" is an array of keys...
                    // When data is initially created, via ("key", "val") signature,
                    // keys will be converted to camelCase.
                    // Since there is no way to tell _how_ a key was added, remove
                    // both plain key and camelCase key. #12786
                    // This will only penalize the array argument path.
                    name = key.concat(key.map(jQuery.camelCase));
                } else {
                    camel = jQuery.camelCase(key);
                    // Try the string as a key before any manipulation
                    if (key in cache) {
                        name = [key, camel];
                    } else {
                        // If a key with the spaces exists, use it.
                        // Otherwise, create an array by matching non-whitespace
                        name = camel;
                        name = name in cache ?
                            [name] : ( name.match(core_rnotwhite) || [] );
                    }
                }

                i = name.length;
                while (i--) {
                    delete cache[name[i]];
                }
            }
        },
        hasData: function (owner) {
            return !jQuery.isEmptyObject(
                this.cache[owner[this.expando]] || {}
            );
        },
        discard: function (owner) {
            if (owner[this.expando]) {
                delete this.cache[owner[this.expando]];
            }
        }
    };

    // These may be used throughout the jQuery core codebase
    data_user = new Data();
    data_priv = new Data();

    jQuery.extend({
        acceptData: Data.accepts,

        hasData: function (elem) {
            return data_user.hasData(elem) || data_priv.hasData(elem);
        },

        data: function (elem, name, data) {
            return data_user.access(elem, name, data);
        },

        removeData: function (elem, name) {
            data_user.remove(elem, name);
        },

        // TODO: Now that all calls to _data and _removeData have been replaced
        // with direct calls to data_priv methods, these can be deprecated.
        _data: function (elem, name, data) {
            return data_priv.access(elem, name, data);
        },

        _removeData: function (elem, name) {
            data_priv.remove(elem, name);
        }
    });

    jQuery.fn.extend({
        data: function (key, value) {
            var attrs, name,
                elem = this[0],
                i = 0,
                data = null;

            // Gets all values
            if (key === undefined) {
                if (this.length) {
                    data = data_user.get(elem);

                    if (elem.nodeType === 1 && !data_priv.get(elem, "hasDataAttrs")) {
                        attrs = elem.attributes;
                        for (; i < attrs.length; i++) {
                            name = attrs[i].name;

                            if (name.indexOf("data-") === 0) {
                                name = jQuery.camelCase(name.slice(5));
                                dataAttr(elem, name, data[name]);
                            }
                        }
                        data_priv.set(elem, "hasDataAttrs", true);
                    }
                }

                return data;
            }

            // Sets multiple values
            if (typeof key === "object") {
                return this.each(function () {
                    data_user.set(this, key);
                });
            }

            return jQuery.access(this, function (value) {
                var data,
                    camelKey = jQuery.camelCase(key);

                // The calling jQuery object (element matches) is not empty
                // (and therefore has an element appears at this[ 0 ]) and the
                // `value` parameter was not undefined. An empty jQuery object
                // will result in `undefined` for elem = this[ 0 ] which will
                // throw an exception if an attempt to read a data cache is made.
                if (elem && value === undefined) {
                    // Attempt to get data from the cache
                    // with the key as-is
                    data = data_user.get(elem, key);
                    if (data !== undefined) {
                        return data;
                    }

                    // Attempt to get data from the cache
                    // with the key camelized
                    data = data_user.get(elem, camelKey);
                    if (data !== undefined) {
                        return data;
                    }

                    // Attempt to "discover" the data in
                    // HTML5 custom data-* attrs
                    data = dataAttr(elem, camelKey, undefined);
                    if (data !== undefined) {
                        return data;
                    }

                    // We tried really hard, but the data doesn't exist.
                    return;
                }

                // Set the data...
                this.each(function () {
                    // First, attempt to store a copy or reference of any
                    // data that might've been store with a camelCased key.
                    var data = data_user.get(this, camelKey);

                    // For HTML5 data-* attribute interop, we have to
                    // store property names with dashes in a camelCase form.
                    // This might not apply to all properties...*
                    data_user.set(this, camelKey, value);

                    // *... In the case of properties that might _actually_
                    // have dashes, we need to also store a copy of that
                    // unchanged property.
                    if (key.indexOf("-") !== -1 && data !== undefined) {
                        data_user.set(this, key, value);
                    }
                });
            }, null, value, arguments.length > 1, null, true);
        },

        removeData: function (key) {
            return this.each(function () {
                data_user.remove(this, key);
            });
        }
    });

    function dataAttr(elem, key, data) {
        var name;

        // If nothing was found internally, try to fetch any
        // data from the HTML5 data-* attribute
        if (data === undefined && elem.nodeType === 1) {
            name = "data-" + key.replace(rmultiDash, "-$1").toLowerCase();
            data = elem.getAttribute(name);

            if (typeof data === "string") {
                try {
                    data = data === "true" ? true :
                        data === "false" ? false :
                            data === "null" ? null :
                                // Only convert to a number if it doesn't change the string
                                +data + "" === data ? +data :
                                    rbrace.test(data) ? JSON.parse(data) :
                                        data;
                } catch (e) {
                }

                // Make sure we set the data so it isn't changed later
                data_user.set(elem, key, data);
            } else {
                data = undefined;
            }
        }
        return data;
    }

    jQuery.extend({
        queue: function (elem, type, data) {
            var queue;

            if (elem) {
                type = ( type || "fx" ) + "queue";
                queue = data_priv.get(elem, type);

                // Speed up dequeue by getting out quickly if this is just a lookup
                if (data) {
                    if (!queue || jQuery.isArray(data)) {
                        queue = data_priv.access(elem, type, jQuery.makeArray(data));
                    } else {
                        queue.push(data);
                    }
                }
                return queue || [];
            }
        },

        dequeue: function (elem, type) {
            type = type || "fx";

            var queue = jQuery.queue(elem, type),
                startLength = queue.length,
                fn = queue.shift(),
                hooks = jQuery._queueHooks(elem, type),
                next = function () {
                    jQuery.dequeue(elem, type);
                };

            // If the fx queue is dequeued, always remove the progress sentinel
            if (fn === "inprogress") {
                fn = queue.shift();
                startLength--;
            }

            if (fn) {

                // Add a progress sentinel to prevent the fx queue from being
                // automatically dequeued
                if (type === "fx") {
                    queue.unshift("inprogress");
                }

                // clear up the last queue stop function
                delete hooks.stop;
                fn.call(elem, next, hooks);
            }

            if (!startLength && hooks) {
                hooks.empty.fire();
            }
        },

        // not intended for public consumption - generates a queueHooks object, or returns the current one
        _queueHooks: function (elem, type) {
            var key = type + "queueHooks";
            return data_priv.get(elem, key) || data_priv.access(elem, key, {
                    empty: jQuery.Callbacks("once memory").add(function () {
                        data_priv.remove(elem, [type + "queue", key]);
                    })
                });
        }
    });

    jQuery.fn.extend({
        queue: function (type, data) {
            var setter = 2;

            if (typeof type !== "string") {
                data = type;
                type = "fx";
                setter--;
            }

            if (arguments.length < setter) {
                return jQuery.queue(this[0], type);
            }

            return data === undefined ?
                this :
                this.each(function () {
                    var queue = jQuery.queue(this, type, data);

                    // ensure a hooks for this queue
                    jQuery._queueHooks(this, type);

                    if (type === "fx" && queue[0] !== "inprogress") {
                        jQuery.dequeue(this, type);
                    }
                });
        },
        dequeue: function (type) {
            return this.each(function () {
                jQuery.dequeue(this, type);
            });
        },
        // Based off of the plugin by Clint Helfers, with permission.
        // http://blindsignals.com/index.php/2009/07/jquery-delay/
        delay: function (time, type) {
            time = jQuery.fx ? jQuery.fx.speeds[time] || time : time;
            type = type || "fx";

            return this.queue(type, function (next, hooks) {
                var timeout = setTimeout(next, time);
                hooks.stop = function () {
                    clearTimeout(timeout);
                };
            });
        },
        clearQueue: function (type) {
            return this.queue(type || "fx", []);
        },
        // Get a promise resolved when queues of a certain type
        // are emptied (fx is the type by default)
        promise: function (type, obj) {
            var tmp,
                count = 1,
                defer = jQuery.Deferred(),
                elements = this,
                i = this.length,
                resolve = function () {
                    if (!( --count )) {
                        defer.resolveWith(elements, [elements]);
                    }
                };

            if (typeof type !== "string") {
                obj = type;
                type = undefined;
            }
            type = type || "fx";

            while (i--) {
                tmp = data_priv.get(elements[i], type + "queueHooks");
                if (tmp && tmp.empty) {
                    count++;
                    tmp.empty.add(resolve);
                }
            }
            resolve();
            return defer.promise(obj);
        }
    });
    var nodeHook, boolHook,
        rclass = /[\t\r\n\f]/g,
        rreturn = /\r/g,
        rfocusable = /^(?:input|select|textarea|button)$/i;

    jQuery.fn.extend({
        attr: function (name, value) {
            return jQuery.access(this, jQuery.attr, name, value, arguments.length > 1);
        },

        removeAttr: function (name) {
            return this.each(function () {
                jQuery.removeAttr(this, name);
            });
        },

        prop: function (name, value) {
            return jQuery.access(this, jQuery.prop, name, value, arguments.length > 1);
        },

        removeProp: function (name) {
            return this.each(function () {
                delete this[jQuery.propFix[name] || name];
            });
        },

        addClass: function (value) {
            var classes, elem, cur, clazz, j,
                i = 0,
                len = this.length,
                proceed = typeof value === "string" && value;

            if (jQuery.isFunction(value)) {
                return this.each(function (j) {
                    jQuery(this).addClass(value.call(this, j, this.className));
                });
            }

            if (proceed) {
                // The disjunction here is for better compressibility (see removeClass)
                classes = ( value || "" ).match(core_rnotwhite) || [];

                for (; i < len; i++) {
                    elem = this[i];
                    cur = elem.nodeType === 1 && ( elem.className ?
                            ( " " + elem.className + " " ).replace(rclass, " ") :
                            " "
                        );

                    if (cur) {
                        j = 0;
                        while ((clazz = classes[j++])) {
                            if (cur.indexOf(" " + clazz + " ") < 0) {
                                cur += clazz + " ";
                            }
                        }
                        elem.className = jQuery.trim(cur);

                    }
                }
            }

            return this;
        },

        removeClass: function (value) {
            var classes, elem, cur, clazz, j,
                i = 0,
                len = this.length,
                proceed = arguments.length === 0 || typeof value === "string" && value;

            if (jQuery.isFunction(value)) {
                return this.each(function (j) {
                    jQuery(this).removeClass(value.call(this, j, this.className));
                });
            }
            if (proceed) {
                classes = ( value || "" ).match(core_rnotwhite) || [];

                for (; i < len; i++) {
                    elem = this[i];
                    // This expression is here for better compressibility (see addClass)
                    cur = elem.nodeType === 1 && ( elem.className ?
                            ( " " + elem.className + " " ).replace(rclass, " ") :
                            ""
                        );

                    if (cur) {
                        j = 0;
                        while ((clazz = classes[j++])) {
                            // Remove *all* instances
                            while (cur.indexOf(" " + clazz + " ") >= 0) {
                                cur = cur.replace(" " + clazz + " ", " ");
                            }
                        }
                        elem.className = value ? jQuery.trim(cur) : "";
                    }
                }
            }

            return this;
        },

        toggleClass: function (value, stateVal) {
            var type = typeof value;

            if (typeof stateVal === "boolean" && type === "string") {
                return stateVal ? this.addClass(value) : this.removeClass(value);
            }

            if (jQuery.isFunction(value)) {
                return this.each(function (i) {
                    jQuery(this).toggleClass(value.call(this, i, this.className, stateVal), stateVal);
                });
            }

            return this.each(function () {
                if (type === "string") {
                    // toggle individual class names
                    var className,
                        i = 0,
                        self = jQuery(this),
                        classNames = value.match(core_rnotwhite) || [];

                    while ((className = classNames[i++])) {
                        // check each className given, space separated list
                        if (self.hasClass(className)) {
                            self.removeClass(className);
                        } else {
                            self.addClass(className);
                        }
                    }

                    // Toggle whole class name
                } else if (type === core_strundefined || type === "boolean") {
                    if (this.className) {
                        // store className if set
                        data_priv.set(this, "__className__", this.className);
                    }

                    // If the element has a class name or if we're passed "false",
                    // then remove the whole classname (if there was one, the above saved it).
                    // Otherwise bring back whatever was previously saved (if anything),
                    // falling back to the empty string if nothing was stored.
                    this.className = this.className || value === false ? "" : data_priv.get(this, "__className__") || "";
                }
            });
        },

        hasClass: function (selector) {
            var className = " " + selector + " ",
                i = 0,
                l = this.length;
            for (; i < l; i++) {
                if (this[i].nodeType === 1 && (" " + this[i].className + " ").replace(rclass, " ").indexOf(className) >= 0) {
                    return true;
                }
            }

            return false;
        },

        val: function (value) {
            var hooks, ret, isFunction,
                elem = this[0];

            if (!arguments.length) {
                if (elem) {
                    hooks = jQuery.valHooks[elem.type] || jQuery.valHooks[elem.nodeName.toLowerCase()];

                    if (hooks && "get" in hooks && (ret = hooks.get(elem, "value")) !== undefined) {
                        return ret;
                    }

                    ret = elem.value;

                    return typeof ret === "string" ?
                        // handle most common string cases
                        ret.replace(rreturn, "") :
                        // handle cases where value is null/undef or number
                        ret == null ? "" : ret;
                }

                return;
            }

            isFunction = jQuery.isFunction(value);

            return this.each(function (i) {
                var val;

                if (this.nodeType !== 1) {
                    return;
                }

                if (isFunction) {
                    val = value.call(this, i, jQuery(this).val());
                } else {
                    val = value;
                }

                // Treat null/undefined as ""; convert numbers to string
                if (val == null) {
                    val = "";
                } else if (typeof val === "number") {
                    val += "";
                } else if (jQuery.isArray(val)) {
                    val = jQuery.map(val, function (value) {
                        return value == null ? "" : value + "";
                    });
                }

                hooks = jQuery.valHooks[this.type] || jQuery.valHooks[this.nodeName.toLowerCase()];

                // If set returns undefined, fall back to normal setting
                if (!hooks || !("set" in hooks) || hooks.set(this, val, "value") === undefined) {
                    this.value = val;
                }
            });
        }
    });

    jQuery.extend({
        valHooks: {
            option: {
                get: function (elem) {
                    // attributes.value is undefined in Blackberry 4.7 but
                    // uses .value. See #6932
                    var val = elem.attributes.value;
                    return !val || val.specified ? elem.value : elem.text;
                }
            },
            select: {
                get: function (elem) {
                    var value, option,
                        options = elem.options,
                        index = elem.selectedIndex,
                        one = elem.type === "select-one" || index < 0,
                        values = one ? null : [],
                        max = one ? index + 1 : options.length,
                        i = index < 0 ?
                            max :
                            one ? index : 0;

                    // Loop through all the selected options
                    for (; i < max; i++) {
                        option = options[i];

                        // IE6-9 doesn't update selected after form reset (#2551)
                        if (( option.selected || i === index ) &&
                                // Don't return options that are disabled or in a disabled optgroup
                            ( jQuery.support.optDisabled ? !option.disabled : option.getAttribute("disabled") === null ) &&
                            ( !option.parentNode.disabled || !jQuery.nodeName(option.parentNode, "optgroup") )) {

                            // Get the specific value for the option
                            value = jQuery(option).val();

                            // We don't need an array for one selects
                            if (one) {
                                return value;
                            }

                            // Multi-Selects return an array
                            values.push(value);
                        }
                    }

                    return values;
                },

                set: function (elem, value) {
                    var optionSet, option,
                        options = elem.options,
                        values = jQuery.makeArray(value),
                        i = options.length;

                    while (i--) {
                        option = options[i];
                        if ((option.selected = jQuery.inArray(jQuery(option).val(), values) >= 0)) {
                            optionSet = true;
                        }
                    }

                    // force browsers to behave consistently when non-matching value is set
                    if (!optionSet) {
                        elem.selectedIndex = -1;
                    }
                    return values;
                }
            }
        },

        attr: function (elem, name, value) {
            var hooks, ret,
                nType = elem.nodeType;

            // don't get/set attributes on text, comment and attribute nodes
            if (!elem || nType === 3 || nType === 8 || nType === 2) {
                return;
            }

            // Fallback to prop when attributes are not supported
            if (typeof elem.getAttribute === core_strundefined) {
                return jQuery.prop(elem, name, value);
            }

            // All attributes are lowercase
            // Grab necessary hook if one is defined
            if (nType !== 1 || !jQuery.isXMLDoc(elem)) {
                name = name.toLowerCase();
                hooks = jQuery.attrHooks[name] ||
                    ( jQuery.expr.match.bool.test(name) ? boolHook : nodeHook );
            }

            if (value !== undefined) {

                if (value === null) {
                    jQuery.removeAttr(elem, name);

                } else if (hooks && "set" in hooks && (ret = hooks.set(elem, value, name)) !== undefined) {
                    return ret;

                } else {
                    elem.setAttribute(name, value + "");
                    return value;
                }

            } else if (hooks && "get" in hooks && (ret = hooks.get(elem, name)) !== null) {
                return ret;

            } else {
                ret = jQuery.find.attr(elem, name);

                // Non-existent attributes return null, we normalize to undefined
                return ret == null ?
                    undefined :
                    ret;
            }
        },

        removeAttr: function (elem, value) {
            var name, propName,
                i = 0,
                attrNames = value && value.match(core_rnotwhite);

            if (attrNames && elem.nodeType === 1) {
                while ((name = attrNames[i++])) {
                    propName = jQuery.propFix[name] || name;

                    // Boolean attributes get special treatment (#10870)
                    if (jQuery.expr.match.bool.test(name)) {
                        // Set corresponding property to false
                        elem[propName] = false;
                    }

                    elem.removeAttribute(name);
                }
            }
        },

        attrHooks: {
            type: {
                set: function (elem, value) {
                    if (!jQuery.support.radioValue && value === "radio" && jQuery.nodeName(elem, "input")) {
                        // Setting the type on a radio button after the value resets the value in IE6-9
                        // Reset value to default in case type is set after value during creation
                        var val = elem.value;
                        elem.setAttribute("type", value);
                        if (val) {
                            elem.value = val;
                        }
                        return value;
                    }
                }
            }
        },

        propFix: {
            "for": "htmlFor",
            "class": "className"
        },

        prop: function (elem, name, value) {
            var ret, hooks, notxml,
                nType = elem.nodeType;

            // don't get/set properties on text, comment and attribute nodes
            if (!elem || nType === 3 || nType === 8 || nType === 2) {
                return;
            }

            notxml = nType !== 1 || !jQuery.isXMLDoc(elem);

            if (notxml) {
                // Fix name and attach hooks
                name = jQuery.propFix[name] || name;
                hooks = jQuery.propHooks[name];
            }

            if (value !== undefined) {
                return hooks && "set" in hooks && (ret = hooks.set(elem, value, name)) !== undefined ?
                    ret :
                    ( elem[name] = value );

            } else {
                return hooks && "get" in hooks && (ret = hooks.get(elem, name)) !== null ?
                    ret :
                    elem[name];
            }
        },

        propHooks: {
            tabIndex: {
                get: function (elem) {
                    return elem.hasAttribute("tabindex") || rfocusable.test(elem.nodeName) || elem.href ?
                        elem.tabIndex :
                        -1;
                }
            }
        }
    });

    // Hooks for boolean attributes
    boolHook = {
        set: function (elem, value, name) {
            if (value === false) {
                // Remove boolean attributes when set to false
                jQuery.removeAttr(elem, name);
            } else {
                elem.setAttribute(name, name);
            }
            return name;
        }
    };
    jQuery.each(jQuery.expr.match.bool.source.match(/\w+/g), function (i, name) {
        var getter = jQuery.expr.attrHandle[name] || jQuery.find.attr;

        jQuery.expr.attrHandle[name] = function (elem, name, isXML) {
            var fn = jQuery.expr.attrHandle[name],
                ret = isXML ?
                    undefined :
                    /* jshint eqeqeq: false */
                    // Temporarily disable this handler to check existence
                    (jQuery.expr.attrHandle[name] = undefined) !=
                    getter(elem, name, isXML) ?

                        name.toLowerCase() :
                        null;

            // Restore handler
            jQuery.expr.attrHandle[name] = fn;

            return ret;
        };
    });

    // Support: IE9+
    // Selectedness for an option in an optgroup can be inaccurate
    if (!jQuery.support.optSelected) {
        jQuery.propHooks.selected = {
            get: function (elem) {
                var parent = elem.parentNode;
                if (parent && parent.parentNode) {
                    parent.parentNode.selectedIndex;
                }
                return null;
            }
        };
    }

    jQuery.each([
        "tabIndex",
        "readOnly",
        "maxLength",
        "cellSpacing",
        "cellPadding",
        "rowSpan",
        "colSpan",
        "useMap",
        "frameBorder",
        "contentEditable"
    ], function () {
        jQuery.propFix[this.toLowerCase()] = this;
    });

    // Radios and checkboxes getter/setter
    jQuery.each(["radio", "checkbox"], function () {
        jQuery.valHooks[this] = {
            set: function (elem, value) {
                if (jQuery.isArray(value)) {
                    return ( elem.checked = jQuery.inArray(jQuery(elem).val(), value) >= 0 );
                }
            }
        };
        if (!jQuery.support.checkOn) {
            jQuery.valHooks[this].get = function (elem) {
                // Support: Webkit
                // "" is returned instead of "on" if a value isn't specified
                return elem.getAttribute("value") === null ? "on" : elem.value;
            };
        }
    });
    var rkeyEvent = /^key/,
        rmouseEvent = /^(?:mouse|contextmenu)|click/,
        rfocusMorph = /^(?:focusinfocus|focusoutblur)$/,
        rtypenamespace = /^([^.]*)(?:\.(.+)|)$/;

    function returnTrue() {
        return true;
    }

    function returnFalse() {
        return false;
    }

    function safeActiveElement() {
        try {
            return document.activeElement;
        } catch (err) {
        }
    }

    /*
     * Helper functions for managing events -- not part of the public interface.
     * Props to Dean Edwards' addEvent library for many of the ideas.
     */
    jQuery.event = {

        global: {},

        add: function (elem, types, handler, data, selector) {

            var handleObjIn, eventHandle, tmp,
                events, t, handleObj,
                special, handlers, type, namespaces, origType,
                elemData = data_priv.get(elem);

            // Don't attach events to noData or text/comment nodes (but allow plain objects)
            if (!elemData) {
                return;
            }

            // Caller can pass in an object of custom data in lieu of the handler
            if (handler.handler) {
                handleObjIn = handler;
                handler = handleObjIn.handler;
                selector = handleObjIn.selector;
            }

            // Make sure that the handler has a unique ID, used to find/remove it later
            if (!handler.guid) {
                handler.guid = jQuery.guid++;
            }

            // Init the element's event structure and main handler, if this is the first
            if (!(events = elemData.events)) {
                events = elemData.events = {};
            }
            if (!(eventHandle = elemData.handle)) {
                eventHandle = elemData.handle = function (e) {
                    // Discard the second event of a jQuery.event.trigger() and
                    // when an event is called after a page has unloaded
                    return typeof jQuery !== core_strundefined && (!e || jQuery.event.triggered !== e.type) ?
                        jQuery.event.dispatch.apply(eventHandle.elem, arguments) :
                        undefined;
                };
                // Add elem as a property of the handle fn to prevent a memory leak with IE non-native events
                eventHandle.elem = elem;
            }

            // Handle multiple events separated by a space
            types = ( types || "" ).match(core_rnotwhite) || [""];
            t = types.length;
            while (t--) {
                tmp = rtypenamespace.exec(types[t]) || [];
                type = origType = tmp[1];
                namespaces = ( tmp[2] || "" ).split(".").sort();

                // There *must* be a type, no attaching namespace-only handlers
                if (!type) {
                    continue;
                }

                // If event changes its type, use the special event handlers for the changed type
                special = jQuery.event.special[type] || {};

                // If selector defined, determine special event api type, otherwise given type
                type = ( selector ? special.delegateType : special.bindType ) || type;

                // Update special based on newly reset type
                special = jQuery.event.special[type] || {};

                // handleObj is passed to all event handlers
                handleObj = jQuery.extend({
                    type: type,
                    origType: origType,
                    data: data,
                    handler: handler,
                    guid: handler.guid,
                    selector: selector,
                    needsContext: selector && jQuery.expr.match.needsContext.test(selector),
                    namespace: namespaces.join(".")
                }, handleObjIn);

                // Init the event handler queue if we're the first
                if (!(handlers = events[type])) {
                    handlers = events[type] = [];
                    handlers.delegateCount = 0;

                    // Only use addEventListener if the special events handler returns false
                    if (!special.setup || special.setup.call(elem, data, namespaces, eventHandle) === false) {
                        if (elem.addEventListener) {
                            elem.addEventListener(type, eventHandle, false);
                        }
                    }
                }

                if (special.add) {
                    special.add.call(elem, handleObj);

                    if (!handleObj.handler.guid) {
                        handleObj.handler.guid = handler.guid;
                    }
                }

                // Add to the element's handler list, delegates in front
                if (selector) {
                    handlers.splice(handlers.delegateCount++, 0, handleObj);
                } else {
                    handlers.push(handleObj);
                }

                // Keep track of which events have ever been used, for event optimization
                jQuery.event.global[type] = true;
            }

            // Nullify elem to prevent memory leaks in IE
            elem = null;
        },

        // Detach an event or set of events from an element
        remove: function (elem, types, handler, selector, mappedTypes) {

            var j, origCount, tmp,
                events, t, handleObj,
                special, handlers, type, namespaces, origType,
                elemData = data_priv.hasData(elem) && data_priv.get(elem);

            if (!elemData || !(events = elemData.events)) {
                return;
            }

            // Once for each type.namespace in types; type may be omitted
            types = ( types || "" ).match(core_rnotwhite) || [""];
            t = types.length;
            while (t--) {
                tmp = rtypenamespace.exec(types[t]) || [];
                type = origType = tmp[1];
                namespaces = ( tmp[2] || "" ).split(".").sort();

                // Unbind all events (on this namespace, if provided) for the element
                if (!type) {
                    for (type in events) {
                        jQuery.event.remove(elem, type + types[t], handler, selector, true);
                    }
                    continue;
                }

                special = jQuery.event.special[type] || {};
                type = ( selector ? special.delegateType : special.bindType ) || type;
                handlers = events[type] || [];
                tmp = tmp[2] && new RegExp("(^|\\.)" + namespaces.join("\\.(?:.*\\.|)") + "(\\.|$)");

                // Remove matching events
                origCount = j = handlers.length;
                while (j--) {
                    handleObj = handlers[j];

                    if (( mappedTypes || origType === handleObj.origType ) &&
                        ( !handler || handler.guid === handleObj.guid ) &&
                        ( !tmp || tmp.test(handleObj.namespace) ) &&
                        ( !selector || selector === handleObj.selector || selector === "**" && handleObj.selector )) {
                        handlers.splice(j, 1);

                        if (handleObj.selector) {
                            handlers.delegateCount--;
                        }
                        if (special.remove) {
                            special.remove.call(elem, handleObj);
                        }
                    }
                }

                // Remove generic event handler if we removed something and no more handlers exist
                // (avoids potential for endless recursion during removal of special event handlers)
                if (origCount && !handlers.length) {
                    if (!special.teardown || special.teardown.call(elem, namespaces, elemData.handle) === false) {
                        jQuery.removeEvent(elem, type, elemData.handle);
                    }

                    delete events[type];
                }
            }

            // Remove the expando if it's no longer used
            if (jQuery.isEmptyObject(events)) {
                delete elemData.handle;
                data_priv.remove(elem, "events");
            }
        },

        trigger: function (event, data, elem, onlyHandlers) {

            var i, cur, tmp, bubbleType, ontype, handle, special,
                eventPath = [elem || document],
                type = core_hasOwn.call(event, "type") ? event.type : event,
                namespaces = core_hasOwn.call(event, "namespace") ? event.namespace.split(".") : [];

            cur = tmp = elem = elem || document;

            // Don't do events on text and comment nodes
            if (elem.nodeType === 3 || elem.nodeType === 8) {
                return;
            }

            // focus/blur morphs to focusin/out; ensure we're not firing them right now
            if (rfocusMorph.test(type + jQuery.event.triggered)) {
                return;
            }

            if (type.indexOf(".") >= 0) {
                // Namespaced trigger; create a regexp to match event type in handle()
                namespaces = type.split(".");
                type = namespaces.shift();
                namespaces.sort();
            }
            ontype = type.indexOf(":") < 0 && "on" + type;

            // Caller can pass in a jQuery.Event object, Object, or just an event type string
            event = event[jQuery.expando] ?
                event :
                new jQuery.Event(type, typeof event === "object" && event);

            // Trigger bitmask: & 1 for native handlers; & 2 for jQuery (always true)
            event.isTrigger = onlyHandlers ? 2 : 3;
            event.namespace = namespaces.join(".");
            event.namespace_re = event.namespace ?
                new RegExp("(^|\\.)" + namespaces.join("\\.(?:.*\\.|)") + "(\\.|$)") :
                null;

            // Clean up the event in case it is being reused
            event.result = undefined;
            if (!event.target) {
                event.target = elem;
            }

            // Clone any incoming data and prepend the event, creating the handler arg list
            data = data == null ?
                [event] :
                jQuery.makeArray(data, [event]);

            // Allow special events to draw outside the lines
            special = jQuery.event.special[type] || {};
            if (!onlyHandlers && special.trigger && special.trigger.apply(elem, data) === false) {
                return;
            }

            // Determine event propagation path in advance, per W3C events spec (#9951)
            // Bubble up to document, then to window; watch for a global ownerDocument var (#9724)
            if (!onlyHandlers && !special.noBubble && !jQuery.isWindow(elem)) {

                bubbleType = special.delegateType || type;
                if (!rfocusMorph.test(bubbleType + type)) {
                    cur = cur.parentNode;
                }
                for (; cur; cur = cur.parentNode) {
                    eventPath.push(cur);
                    tmp = cur;
                }

                // Only add window if we got to document (e.g., not plain obj or detached DOM)
                if (tmp === (elem.ownerDocument || document)) {
                    eventPath.push(tmp.defaultView || tmp.parentWindow || window);
                }
            }

            // Fire handlers on the event path
            i = 0;
            while ((cur = eventPath[i++]) && !event.isPropagationStopped()) {

                event.type = i > 1 ?
                    bubbleType :
                special.bindType || type;

                // jQuery handler
                handle = ( data_priv.get(cur, "events") || {} )[event.type] && data_priv.get(cur, "handle");
                if (handle) {
                    handle.apply(cur, data);
                }

                // Native handler
                handle = ontype && cur[ontype];
                if (handle && jQuery.acceptData(cur) && handle.apply && handle.apply(cur, data) === false) {
                    event.preventDefault();
                }
            }
            event.type = type;

            // If nobody prevented the default action, do it now
            if (!onlyHandlers && !event.isDefaultPrevented()) {

                if ((!special._default || special._default.apply(eventPath.pop(), data) === false) &&
                    jQuery.acceptData(elem)) {

                    // Call a native DOM method on the target with the same name name as the event.
                    // Don't do default actions on window, that's where global variables be (#6170)
                    if (ontype && jQuery.isFunction(elem[type]) && !jQuery.isWindow(elem)) {

                        // Don't re-trigger an onFOO event when we call its FOO() method
                        tmp = elem[ontype];

                        if (tmp) {
                            elem[ontype] = null;
                        }

                        // Prevent re-triggering of the same event, since we already bubbled it above
                        jQuery.event.triggered = type;
                        elem[type]();
                        jQuery.event.triggered = undefined;

                        if (tmp) {
                            elem[ontype] = tmp;
                        }
                    }
                }
            }

            return event.result;
        },

        dispatch: function (event) {

            // Make a writable jQuery.Event from the native event object
            event = jQuery.event.fix(event);

            var i, j, ret, matched, handleObj,
                handlerQueue = [],
                args = core_slice.call(arguments),
                handlers = ( data_priv.get(this, "events") || {} )[event.type] || [],
                special = jQuery.event.special[event.type] || {};

            // Use the fix-ed jQuery.Event rather than the (read-only) native event
            args[0] = event;
            event.delegateTarget = this;

            // Call the preDispatch hook for the mapped type, and let it bail if desired
            if (special.preDispatch && special.preDispatch.call(this, event) === false) {
                return;
            }

            // Determine handlers
            handlerQueue = jQuery.event.handlers.call(this, event, handlers);

            // Run delegates first; they may want to stop propagation beneath us
            i = 0;
            while ((matched = handlerQueue[i++]) && !event.isPropagationStopped()) {
                event.currentTarget = matched.elem;

                j = 0;
                while ((handleObj = matched.handlers[j++]) && !event.isImmediatePropagationStopped()) {

                    // Triggered event must either 1) have no namespace, or
                    // 2) have namespace(s) a subset or equal to those in the bound event (both can have no namespace).
                    if (!event.namespace_re || event.namespace_re.test(handleObj.namespace)) {

                        event.handleObj = handleObj;
                        event.data = handleObj.data;

                        ret = ( (jQuery.event.special[handleObj.origType] || {}).handle || handleObj.handler )
                            .apply(matched.elem, args);

                        if (ret !== undefined) {
                            if ((event.result = ret) === false) {
                                event.preventDefault();
                                event.stopPropagation();
                            }
                        }
                    }
                }
            }

            // Call the postDispatch hook for the mapped type
            if (special.postDispatch) {
                special.postDispatch.call(this, event);
            }

            return event.result;
        },

        handlers: function (event, handlers) {
            var i, matches, sel, handleObj,
                handlerQueue = [],
                delegateCount = handlers.delegateCount,
                cur = event.target;

            // Find delegate handlers
            // Black-hole SVG <use> instance trees (#13180)
            // Avoid non-left-click bubbling in Firefox (#3861)
            if (delegateCount && cur.nodeType && (!event.button || event.type !== "click")) {

                for (; cur !== this; cur = cur.parentNode || this) {

                    // Don't process clicks on disabled elements (#6911, #8165, #11382, #11764)
                    if (cur.disabled !== true || event.type !== "click") {
                        matches = [];
                        for (i = 0; i < delegateCount; i++) {
                            handleObj = handlers[i];

                            // Don't conflict with Object.prototype properties (#13203)
                            sel = handleObj.selector + " ";

                            if (matches[sel] === undefined) {
                                matches[sel] = handleObj.needsContext ?
                                jQuery(sel, this).index(cur) >= 0 :
                                    jQuery.find(sel, this, null, [cur]).length;
                            }
                            if (matches[sel]) {
                                matches.push(handleObj);
                            }
                        }
                        if (matches.length) {
                            handlerQueue.push({elem: cur, handlers: matches});
                        }
                    }
                }
            }

            // Add the remaining (directly-bound) handlers
            if (delegateCount < handlers.length) {
                handlerQueue.push({elem: this, handlers: handlers.slice(delegateCount)});
            }

            return handlerQueue;
        },

        // Includes some event props shared by KeyEvent and MouseEvent
        props: "altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),

        fixHooks: {},

        keyHooks: {
            props: "char charCode key keyCode".split(" "),
            filter: function (event, original) {

                // Add which for key events
                if (event.which == null) {
                    event.which = original.charCode != null ? original.charCode : original.keyCode;
                }

                return event;
            }
        },

        mouseHooks: {
            props: "button buttons clientX clientY offsetX offsetY pageX pageY screenX screenY toElement".split(" "),
            filter: function (event, original) {
                var eventDoc, doc, body,
                    button = original.button;

                // Calculate pageX/Y if missing and clientX/Y available
                if (event.pageX == null && original.clientX != null) {
                    eventDoc = event.target.ownerDocument || document;
                    doc = eventDoc.documentElement;
                    body = eventDoc.body;

                    event.pageX = original.clientX + ( doc && doc.scrollLeft || body && body.scrollLeft || 0 ) - ( doc && doc.clientLeft || body && body.clientLeft || 0 );
                    event.pageY = original.clientY + ( doc && doc.scrollTop || body && body.scrollTop || 0 ) - ( doc && doc.clientTop || body && body.clientTop || 0 );
                }

                // Add which for click: 1 === left; 2 === middle; 3 === right
                // Note: button is not normalized, so don't use it
                if (!event.which && button !== undefined) {
                    event.which = ( button & 1 ? 1 : ( button & 2 ? 3 : ( button & 4 ? 2 : 0 ) ) );
                }

                return event;
            }
        },

        fix: function (event) {
            if (event[jQuery.expando]) {
                return event;
            }

            // Create a writable copy of the event object and normalize some properties
            var i, prop, copy,
                type = event.type,
                originalEvent = event,
                fixHook = this.fixHooks[type];

            if (!fixHook) {
                this.fixHooks[type] = fixHook =
                    rmouseEvent.test(type) ? this.mouseHooks :
                        rkeyEvent.test(type) ? this.keyHooks :
                        {};
            }
            copy = fixHook.props ? this.props.concat(fixHook.props) : this.props;

            event = new jQuery.Event(originalEvent);

            i = copy.length;
            while (i--) {
                prop = copy[i];
                event[prop] = originalEvent[prop];
            }

            // Support: Cordova 2.5 (WebKit) (#13255)
            // All events should have a target; Cordova deviceready doesn't
            if (!event.target) {
                event.target = document;
            }

            // Support: Safari 6.0+, Chrome < 28
            // Target should not be a text node (#504, #13143)
            if (event.target.nodeType === 3) {
                event.target = event.target.parentNode;
            }

            return fixHook.filter ? fixHook.filter(event, originalEvent) : event;
        },

        special: {
            load: {
                // Prevent triggered image.load events from bubbling to window.load
                noBubble: true
            },
            focus: {
                // Fire native event if possible so blur/focus sequence is correct
                trigger: function () {
                    if (this !== safeActiveElement() && this.focus) {
                        this.focus();
                        return false;
                    }
                },
                delegateType: "focusin"
            },
            blur: {
                trigger: function () {
                    if (this === safeActiveElement() && this.blur) {
                        this.blur();
                        return false;
                    }
                },
                delegateType: "focusout"
            },
            click: {
                // For checkbox, fire native event so checked state will be right
                trigger: function () {
                    if (this.type === "checkbox" && this.click && jQuery.nodeName(this, "input")) {
                        this.click();
                        return false;
                    }
                },

                // For cross-browser consistency, don't fire native .click() on links
                _default: function (event) {
                    return jQuery.nodeName(event.target, "a");
                }
            },

            beforeunload: {
                postDispatch: function (event) {

                    // Support: Firefox 20+
                    // Firefox doesn't alert if the returnValue field is not set.
                    if (event.result !== undefined) {
                        event.originalEvent.returnValue = event.result;
                    }
                }
            }
        },

        simulate: function (type, elem, event, bubble) {
            // Piggyback on a donor event to simulate a different one.
            // Fake originalEvent to avoid donor's stopPropagation, but if the
            // simulated event prevents default then we do the same on the donor.
            var e = jQuery.extend(
                new jQuery.Event(),
                event,
                {
                    type: type,
                    isSimulated: true,
                    originalEvent: {}
                }
            );
            if (bubble) {
                jQuery.event.trigger(e, null, elem);
            } else {
                jQuery.event.dispatch.call(elem, e);
            }
            if (e.isDefaultPrevented()) {
                event.preventDefault();
            }
        }
    };

    jQuery.removeEvent = function (elem, type, handle) {
        if (elem.removeEventListener) {
            elem.removeEventListener(type, handle, false);
        }
    };

    jQuery.Event = function (src, props) {
        // Allow instantiation without the 'new' keyword
        if (!(this instanceof jQuery.Event)) {
            return new jQuery.Event(src, props);
        }

        // Event object
        if (src && src.type) {
            this.originalEvent = src;
            this.type = src.type;

            // Events bubbling up the document may have been marked as prevented
            // by a handler lower down the tree; reflect the correct value.
            this.isDefaultPrevented = ( src.defaultPrevented ||
            src.getPreventDefault && src.getPreventDefault() ) ? returnTrue : returnFalse;

            // Event type
        } else {
            this.type = src;
        }

        // Put explicitly provided properties onto the event object
        if (props) {
            jQuery.extend(this, props);
        }

        // Create a timestamp if incoming event doesn't have one
        this.timeStamp = src && src.timeStamp || jQuery.now();

        // Mark it as fixed
        this[jQuery.expando] = true;
    };

    // jQuery.Event is based on DOM3 Events as specified by the ECMAScript Language Binding
    // http://www.w3.org/TR/2003/WD-DOM-Level-3-Events-20030331/ecma-script-binding.html
    jQuery.Event.prototype = {
        isDefaultPrevented: returnFalse,
        isPropagationStopped: returnFalse,
        isImmediatePropagationStopped: returnFalse,

        preventDefault: function () {
            var e = this.originalEvent;

            this.isDefaultPrevented = returnTrue;

            if (e && e.preventDefault) {
                e.preventDefault();
            }
        },
        stopPropagation: function () {
            var e = this.originalEvent;

            this.isPropagationStopped = returnTrue;

            if (e && e.stopPropagation) {
                e.stopPropagation();
            }
        },
        stopImmediatePropagation: function () {
            this.isImmediatePropagationStopped = returnTrue;
            this.stopPropagation();
        }
    };

    // Create mouseenter/leave events using mouseover/out and event-time checks
    // Support: Chrome 15+
    jQuery.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout"
    }, function (orig, fix) {
        jQuery.event.special[orig] = {
            delegateType: fix,
            bindType: fix,

            handle: function (event) {
                var ret,
                    target = this,
                    related = event.relatedTarget,
                    handleObj = event.handleObj;

                // For mousenter/leave call the handler if related is outside the target.
                // NB: No relatedTarget if the mouse left/entered the browser window
                if (!related || (related !== target && !jQuery.contains(target, related))) {
                    event.type = handleObj.origType;
                    ret = handleObj.handler.apply(this, arguments);
                    event.type = fix;
                }
                return ret;
            }
        };
    });

    // Create "bubbling" focus and blur events
    // Support: Firefox, Chrome, Safari
    if (!jQuery.support.focusinBubbles) {
        jQuery.each({focus: "focusin", blur: "focusout"}, function (orig, fix) {

            // Attach a single capturing handler while someone wants focusin/focusout
            var attaches = 0,
                handler = function (event) {
                    jQuery.event.simulate(fix, event.target, jQuery.event.fix(event), true);
                };

            jQuery.event.special[fix] = {
                setup: function () {
                    if (attaches++ === 0) {
                        document.addEventListener(orig, handler, true);
                    }
                },
                teardown: function () {
                    if (--attaches === 0) {
                        document.removeEventListener(orig, handler, true);
                    }
                }
            };
        });
    }

    jQuery.fn.extend({

        on: function (types, selector, data, fn, /*INTERNAL*/ one) {
            var origFn, type;

            // Types can be a map of types/handlers
            if (typeof types === "object") {
                // ( types-Object, selector, data )
                if (typeof selector !== "string") {
                    // ( types-Object, data )
                    data = data || selector;
                    selector = undefined;
                }
                for (type in types) {
                    this.on(type, selector, data, types[type], one);
                }
                return this;
            }

            if (data == null && fn == null) {
                // ( types, fn )
                fn = selector;
                data = selector = undefined;
            } else if (fn == null) {
                if (typeof selector === "string") {
                    // ( types, selector, fn )
                    fn = data;
                    data = undefined;
                } else {
                    // ( types, data, fn )
                    fn = data;
                    data = selector;
                    selector = undefined;
                }
            }
            if (fn === false) {
                fn = returnFalse;
            } else if (!fn) {
                return this;
            }

            if (one === 1) {
                origFn = fn;
                fn = function (event) {
                    // Can use an empty set, since event contains the info
                    jQuery().off(event);
                    return origFn.apply(this, arguments);
                };
                // Use same guid so caller can remove using origFn
                fn.guid = origFn.guid || ( origFn.guid = jQuery.guid++ );
            }
            return this.each(function () {
                jQuery.event.add(this, types, fn, data, selector);
            });
        },
        one: function (types, selector, data, fn) {
            return this.on(types, selector, data, fn, 1);
        },
        off: function (types, selector, fn) {
            var handleObj, type;
            if (types && types.preventDefault && types.handleObj) {
                // ( event )  dispatched jQuery.Event
                handleObj = types.handleObj;
                jQuery(types.delegateTarget).off(
                    handleObj.namespace ? handleObj.origType + "." + handleObj.namespace : handleObj.origType,
                    handleObj.selector,
                    handleObj.handler
                );
                return this;
            }
            if (typeof types === "object") {
                // ( types-object [, selector] )
                for (type in types) {
                    this.off(type, selector, types[type]);
                }
                return this;
            }
            if (selector === false || typeof selector === "function") {
                // ( types [, fn] )
                fn = selector;
                selector = undefined;
            }
            if (fn === false) {
                fn = returnFalse;
            }
            return this.each(function () {
                jQuery.event.remove(this, types, fn, selector);
            });
        },

        trigger: function (type, data) {
            return this.each(function () {
                jQuery.event.trigger(type, data, this);
            });
        },
        triggerHandler: function (type, data) {
            var elem = this[0];
            if (elem) {
                return jQuery.event.trigger(type, data, elem, true);
            }
        }
    });
    var isSimple = /^.[^:#\[\.,]*$/,
        rparentsprev = /^(?:parents|prev(?:Until|All))/,
        rneedsContext = jQuery.expr.match.needsContext,
    // methods guaranteed to produce a unique set when starting from a unique set
        guaranteedUnique = {
            children: true,
            contents: true,
            next: true,
            prev: true
        };

    jQuery.fn.extend({
        find: function (selector) {
            var i,
                ret = [],
                self = this,
                len = self.length;

            if (typeof selector !== "string") {
                return this.pushStack(jQuery(selector).filter(function () {
                    for (i = 0; i < len; i++) {
                        if (jQuery.contains(self[i], this)) {
                            return true;
                        }
                    }
                }));
            }

            for (i = 0; i < len; i++) {
                jQuery.find(selector, self[i], ret);
            }

            // Needed because $( selector, context ) becomes $( context ).find( selector )
            ret = this.pushStack(len > 1 ? jQuery.unique(ret) : ret);
            ret.selector = this.selector ? this.selector + " " + selector : selector;
            return ret;
        },

        has: function (target) {
            var targets = jQuery(target, this),
                l = targets.length;

            return this.filter(function () {
                var i = 0;
                for (; i < l; i++) {
                    if (jQuery.contains(this, targets[i])) {
                        return true;
                    }
                }
            });
        },

        not: function (selector) {
            return this.pushStack(winnow(this, selector || [], true));
        },

        filter: function (selector) {
            return this.pushStack(winnow(this, selector || [], false));
        },

        is: function (selector) {
            return !!winnow(
                this,

                // If this is a positional/relative selector, check membership in the returned set
                // so $("p:first").is("p:last") won't return true for a doc with two "p".
                typeof selector === "string" && rneedsContext.test(selector) ?
                    jQuery(selector) :
                selector || [],
                false
            ).length;
        },

        closest: function (selectors, context) {
            var cur,
                i = 0,
                l = this.length,
                matched = [],
                pos = ( rneedsContext.test(selectors) || typeof selectors !== "string" ) ?
                    jQuery(selectors, context || this.context) :
                    0;

            for (; i < l; i++) {
                for (cur = this[i]; cur && cur !== context; cur = cur.parentNode) {
                    // Always skip document fragments
                    if (cur.nodeType < 11 && (pos ?
                        pos.index(cur) > -1 :

                            // Don't pass non-elements to Sizzle
                        cur.nodeType === 1 &&
                        jQuery.find.matchesSelector(cur, selectors))) {

                        cur = matched.push(cur);
                        break;
                    }
                }
            }

            return this.pushStack(matched.length > 1 ? jQuery.unique(matched) : matched);
        },

        // Determine the position of an element within
        // the matched set of elements
        index: function (elem) {

            // No argument, return index in parent
            if (!elem) {
                return ( this[0] && this[0].parentNode ) ? this.first().prevAll().length : -1;
            }

            // index in selector
            if (typeof elem === "string") {
                return core_indexOf.call(jQuery(elem), this[0]);
            }

            // Locate the position of the desired element
            return core_indexOf.call(this,

                // If it receives a jQuery object, the first element is used
                elem.jquery ? elem[0] : elem
            );
        },

        add: function (selector, context) {
            var set = typeof selector === "string" ?
                    jQuery(selector, context) :
                    jQuery.makeArray(selector && selector.nodeType ? [selector] : selector),
                all = jQuery.merge(this.get(), set);

            return this.pushStack(jQuery.unique(all));
        },

        addBack: function (selector) {
            return this.add(selector == null ?
                    this.prevObject : this.prevObject.filter(selector)
            );
        }
    });

    function sibling(cur, dir) {
        while ((cur = cur[dir]) && cur.nodeType !== 1) {
        }

        return cur;
    }

    jQuery.each({
        parent: function (elem) {
            var parent = elem.parentNode;
            return parent && parent.nodeType !== 11 ? parent : null;
        },
        parents: function (elem) {
            return jQuery.dir(elem, "parentNode");
        },
        parentsUntil: function (elem, i, until) {
            return jQuery.dir(elem, "parentNode", until);
        },
        next: function (elem) {
            return sibling(elem, "nextSibling");
        },
        prev: function (elem) {
            return sibling(elem, "previousSibling");
        },
        nextAll: function (elem) {
            return jQuery.dir(elem, "nextSibling");
        },
        prevAll: function (elem) {
            return jQuery.dir(elem, "previousSibling");
        },
        nextUntil: function (elem, i, until) {
            return jQuery.dir(elem, "nextSibling", until);
        },
        prevUntil: function (elem, i, until) {
            return jQuery.dir(elem, "previousSibling", until);
        },
        siblings: function (elem) {
            return jQuery.sibling(( elem.parentNode || {} ).firstChild, elem);
        },
        children: function (elem) {
            return jQuery.sibling(elem.firstChild);
        },
        contents: function (elem) {
            return elem.contentDocument || jQuery.merge([], elem.childNodes);
        }
    }, function (name, fn) {
        jQuery.fn[name] = function (until, selector) {
            var matched = jQuery.map(this, fn, until);

            if (name.slice(-5) !== "Until") {
                selector = until;
            }

            if (selector && typeof selector === "string") {
                matched = jQuery.filter(selector, matched);
            }

            if (this.length > 1) {
                // Remove duplicates
                if (!guaranteedUnique[name]) {
                    jQuery.unique(matched);
                }

                // Reverse order for parents* and prev-derivatives
                if (rparentsprev.test(name)) {
                    matched.reverse();
                }
            }

            return this.pushStack(matched);
        };
    });

    jQuery.extend({
        filter: function (expr, elems, not) {
            var elem = elems[0];

            if (not) {
                expr = ":not(" + expr + ")";
            }

            return elems.length === 1 && elem.nodeType === 1 ?
                jQuery.find.matchesSelector(elem, expr) ? [elem] : [] :
                jQuery.find.matches(expr, jQuery.grep(elems, function (elem) {
                    return elem.nodeType === 1;
                }));
        },

        dir: function (elem, dir, until) {
            var matched = [],
                truncate = until !== undefined;

            while ((elem = elem[dir]) && elem.nodeType !== 9) {
                if (elem.nodeType === 1) {
                    if (truncate && jQuery(elem).is(until)) {
                        break;
                    }
                    matched.push(elem);
                }
            }
            return matched;
        },

        sibling: function (n, elem) {
            var matched = [];

            for (; n; n = n.nextSibling) {
                if (n.nodeType === 1 && n !== elem) {
                    matched.push(n);
                }
            }

            return matched;
        }
    });

    // Implement the identical functionality for filter and not
    function winnow(elements, qualifier, not) {
        if (jQuery.isFunction(qualifier)) {
            return jQuery.grep(elements, function (elem, i) {
                /* jshint -W018 */
                return !!qualifier.call(elem, i, elem) !== not;
            });

        }

        if (qualifier.nodeType) {
            return jQuery.grep(elements, function (elem) {
                return ( elem === qualifier ) !== not;
            });

        }

        if (typeof qualifier === "string") {
            if (isSimple.test(qualifier)) {
                return jQuery.filter(qualifier, elements, not);
            }

            qualifier = jQuery.filter(qualifier, elements);
        }

        return jQuery.grep(elements, function (elem) {
            return ( core_indexOf.call(qualifier, elem) >= 0 ) !== not;
        });
    }

    var rxhtmlTag = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi,
        rtagName = /<([\w:]+)/,
        rhtml = /<|&#?\w+;/,
        rnoInnerhtml = /<(?:script|style|link)/i,
        manipulation_rcheckableType = /^(?:checkbox|radio)$/i,
    // checked="checked" or checked
        rchecked = /checked\s*(?:[^=]|=\s*.checked.)/i,
        rscriptType = /^$|\/(?:java|ecma)script/i,
        rscriptTypeMasked = /^true\/(.*)/,
        rcleanScript = /^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g,

    // We have to close these tags to support XHTML (#13200)
        wrapMap = {

            // Support: IE 9
            option: [1, "<select multiple='multiple'>", "</select>"],

            thead: [1, "<table>", "</table>"],
            col: [2, "<table><colgroup>", "</colgroup></table>"],
            tr: [2, "<table><tbody>", "</tbody></table>"],
            td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],

            _default: [0, "", ""]
        };

    // Support: IE 9
    wrapMap.optgroup = wrapMap.option;

    wrapMap.tbody = wrapMap.tfoot = wrapMap.colgroup = wrapMap.caption = wrapMap.thead;
    wrapMap.th = wrapMap.td;

    jQuery.fn.extend({
        text: function (value) {
            return jQuery.access(this, function (value) {
                return value === undefined ?
                    jQuery.text(this) :
                    this.empty().append(( this[0] && this[0].ownerDocument || document ).createTextNode(value));
            }, null, value, arguments.length);
        },

        append: function () {
            return this.domManip(arguments, function (elem) {
                if (this.nodeType === 1 || this.nodeType === 11 || this.nodeType === 9) {
                    var target = manipulationTarget(this, elem);
                    target.appendChild(elem);
                }
            });
        },

        prepend: function () {
            return this.domManip(arguments, function (elem) {
                if (this.nodeType === 1 || this.nodeType === 11 || this.nodeType === 9) {
                    var target = manipulationTarget(this, elem);
                    target.insertBefore(elem, target.firstChild);
                }
            });
        },

        before: function () {
            return this.domManip(arguments, function (elem) {
                if (this.parentNode) {
                    this.parentNode.insertBefore(elem, this);
                }
            });
        },

        after: function () {
            return this.domManip(arguments, function (elem) {
                if (this.parentNode) {
                    this.parentNode.insertBefore(elem, this.nextSibling);
                }
            });
        },

        // keepData is for internal use only--do not document
        remove: function (selector, keepData) {
            var elem,
                elems = selector ? jQuery.filter(selector, this) : this,
                i = 0;

            for (; (elem = elems[i]) != null; i++) {
                if (!keepData && elem.nodeType === 1) {
                    jQuery.cleanData(getAll(elem));
                }

                if (elem.parentNode) {
                    if (keepData && jQuery.contains(elem.ownerDocument, elem)) {
                        setGlobalEval(getAll(elem, "script"));
                    }
                    elem.parentNode.removeChild(elem);
                }
            }

            return this;
        },

        empty: function () {
            var elem,
                i = 0;

            for (; (elem = this[i]) != null; i++) {
                if (elem.nodeType === 1) {

                    // Prevent memory leaks
                    jQuery.cleanData(getAll(elem, false));

                    // Remove any remaining nodes
                    elem.textContent = "";
                }
            }

            return this;
        },

        clone: function (dataAndEvents, deepDataAndEvents) {
            dataAndEvents = dataAndEvents == null ? false : dataAndEvents;
            deepDataAndEvents = deepDataAndEvents == null ? dataAndEvents : deepDataAndEvents;

            return this.map(function () {
                return jQuery.clone(this, dataAndEvents, deepDataAndEvents);
            });
        },

        html: function (value) {
            return jQuery.access(this, function (value) {
                var elem = this[0] || {},
                    i = 0,
                    l = this.length;

                if (value === undefined && elem.nodeType === 1) {
                    return elem.innerHTML;
                }

                // See if we can take a shortcut and just use innerHTML
                if (typeof value === "string" && !rnoInnerhtml.test(value) && !wrapMap[( rtagName.exec(value) || ["", ""] )[1].toLowerCase()]) {

                    value = value.replace(rxhtmlTag, "<$1></$2>");

                    try {
                        for (; i < l; i++) {
                            elem = this[i] || {};

                            // Remove element nodes and prevent memory leaks
                            if (elem.nodeType === 1) {
                                jQuery.cleanData(getAll(elem, false));
                                elem.innerHTML = value;
                            }
                        }

                        elem = 0;

                        // If using innerHTML throws an exception, use the fallback method
                    } catch (e) {
                    }
                }

                if (elem) {
                    this.empty().append(value);
                }
            }, null, value, arguments.length);
        },

        replaceWith: function () {
            var
            // Snapshot the DOM in case .domManip sweeps something relevant into its fragment
                args = jQuery.map(this, function (elem) {
                    return [elem.nextSibling, elem.parentNode];
                }),
                i = 0;

            // Make the changes, replacing each context element with the new content
            this.domManip(arguments, function (elem) {
                var next = args[i++],
                    parent = args[i++];

                if (parent) {
                    // Don't use the snapshot next if it has moved (#13810)
                    if (next && next.parentNode !== parent) {
                        next = this.nextSibling;
                    }
                    jQuery(this).remove();
                    parent.insertBefore(elem, next);
                }
                // Allow new content to include elements from the context set
            }, true);

            // Force removal if there was no new content (e.g., from empty arguments)
            return i ? this : this.remove();
        },

        detach: function (selector) {
            return this.remove(selector, true);
        },

        domManip: function (args, callback, allowIntersection) {

            // Flatten any nested arrays
            args = core_concat.apply([], args);

            var fragment, first, scripts, hasScripts, node, doc,
                i = 0,
                l = this.length,
                set = this,
                iNoClone = l - 1,
                value = args[0],
                isFunction = jQuery.isFunction(value);

            // We can't cloneNode fragments that contain checked, in WebKit
            if (isFunction || !( l <= 1 || typeof value !== "string" || jQuery.support.checkClone || !rchecked.test(value) )) {
                return this.each(function (index) {
                    var self = set.eq(index);
                    if (isFunction) {
                        args[0] = value.call(this, index, self.html());
                    }
                    self.domManip(args, callback, allowIntersection);
                });
            }

            if (l) {
                fragment = jQuery.buildFragment(args, this[0].ownerDocument, false, !allowIntersection && this);
                first = fragment.firstChild;

                if (fragment.childNodes.length === 1) {
                    fragment = first;
                }

                if (first) {
                    scripts = jQuery.map(getAll(fragment, "script"), disableScript);
                    hasScripts = scripts.length;

                    // Use the original fragment for the last item instead of the first because it can end up
                    // being emptied incorrectly in certain situations (#8070).
                    for (; i < l; i++) {
                        node = fragment;

                        if (i !== iNoClone) {
                            node = jQuery.clone(node, true, true);

                            // Keep references to cloned scripts for later restoration
                            if (hasScripts) {
                                // Support: QtWebKit
                                // jQuery.merge because core_push.apply(_, arraylike) throws
                                jQuery.merge(scripts, getAll(node, "script"));
                            }
                        }

                        callback.call(this[i], node, i);
                    }

                    if (hasScripts) {
                        doc = scripts[scripts.length - 1].ownerDocument;

                        // Reenable scripts
                        jQuery.map(scripts, restoreScript);

                        // Evaluate executable scripts on first document insertion
                        for (i = 0; i < hasScripts; i++) {
                            node = scripts[i];
                            if (rscriptType.test(node.type || "") && !data_priv.access(node, "globalEval") && jQuery.contains(doc, node)) {

                                if (node.src) {
                                    // Hope ajax is available...
                                    jQuery._evalUrl(node.src);
                                } else {
                                    jQuery.globalEval(node.textContent.replace(rcleanScript, ""));
                                }
                            }
                        }
                    }
                }
            }

            return this;
        }
    });

    jQuery.each({
        appendTo: "append",
        prependTo: "prepend",
        insertBefore: "before",
        insertAfter: "after",
        replaceAll: "replaceWith"
    }, function (name, original) {
        jQuery.fn[name] = function (selector) {
            var elems,
                ret = [],
                insert = jQuery(selector),
                last = insert.length - 1,
                i = 0;

            for (; i <= last; i++) {
                elems = i === last ? this : this.clone(true);
                jQuery(insert[i])[original](elems);

                // Support: QtWebKit
                // .get() because core_push.apply(_, arraylike) throws
                core_push.apply(ret, elems.get());
            }

            return this.pushStack(ret);
        };
    });

    jQuery.extend({
        clone: function (elem, dataAndEvents, deepDataAndEvents) {
            var i, l, srcElements, destElements,
                clone = elem.cloneNode(true),
                inPage = jQuery.contains(elem.ownerDocument, elem);

            // Support: IE >= 9
            // Fix Cloning issues
            if (!jQuery.support.noCloneChecked && ( elem.nodeType === 1 || elem.nodeType === 11 ) && !jQuery.isXMLDoc(elem)) {

                // We eschew Sizzle here for performance reasons: http://jsperf.com/getall-vs-sizzle/2
                destElements = getAll(clone);
                srcElements = getAll(elem);

                for (i = 0, l = srcElements.length; i < l; i++) {
                    fixInput(srcElements[i], destElements[i]);
                }
            }

            // Copy the events from the original to the clone
            if (dataAndEvents) {
                if (deepDataAndEvents) {
                    srcElements = srcElements || getAll(elem);
                    destElements = destElements || getAll(clone);

                    for (i = 0, l = srcElements.length; i < l; i++) {
                        cloneCopyEvent(srcElements[i], destElements[i]);
                    }
                } else {
                    cloneCopyEvent(elem, clone);
                }
            }

            // Preserve script evaluation history
            destElements = getAll(clone, "script");
            if (destElements.length > 0) {
                setGlobalEval(destElements, !inPage && getAll(elem, "script"));
            }

            // Return the cloned set
            return clone;
        },

        buildFragment: function (elems, context, scripts, selection) {
            var elem, tmp, tag, wrap, contains, j,
                i = 0,
                l = elems.length,
                fragment = context.createDocumentFragment(),
                nodes = [];

            for (; i < l; i++) {
                elem = elems[i];

                if (elem || elem === 0) {

                    // Add nodes directly
                    if (jQuery.type(elem) === "object") {
                        // Support: QtWebKit
                        // jQuery.merge because core_push.apply(_, arraylike) throws
                        jQuery.merge(nodes, elem.nodeType ? [elem] : elem);

                        // Convert non-html into a text node
                    } else if (!rhtml.test(elem)) {
                        nodes.push(context.createTextNode(elem));

                        // Convert html into DOM nodes
                    } else {
                        tmp = tmp || fragment.appendChild(context.createElement("div"));

                        // Deserialize a standard representation
                        tag = ( rtagName.exec(elem) || ["", ""] )[1].toLowerCase();
                        wrap = wrapMap[tag] || wrapMap._default;
                        tmp.innerHTML = wrap[1] + elem.replace(rxhtmlTag, "<$1></$2>") + wrap[2];

                        // Descend through wrappers to the right content
                        j = wrap[0];
                        while (j--) {
                            tmp = tmp.lastChild;
                        }

                        // Support: QtWebKit
                        // jQuery.merge because core_push.apply(_, arraylike) throws
                        jQuery.merge(nodes, tmp.childNodes);

                        // Remember the top-level container
                        tmp = fragment.firstChild;

                        // Fixes #12346
                        // Support: Webkit, IE
                        tmp.textContent = "";
                    }
                }
            }

            // Remove wrapper from fragment
            fragment.textContent = "";

            i = 0;
            while ((elem = nodes[i++])) {

                // #4087 - If origin and destination elements are the same, and this is
                // that element, do not do anything
                if (selection && jQuery.inArray(elem, selection) !== -1) {
                    continue;
                }

                contains = jQuery.contains(elem.ownerDocument, elem);

                // Append to fragment
                tmp = getAll(fragment.appendChild(elem), "script");

                // Preserve script evaluation history
                if (contains) {
                    setGlobalEval(tmp);
                }

                // Capture executables
                if (scripts) {
                    j = 0;
                    while ((elem = tmp[j++])) {
                        if (rscriptType.test(elem.type || "")) {
                            scripts.push(elem);
                        }
                    }
                }
            }

            return fragment;
        },

        cleanData: function (elems) {
            var data, elem, events, type, key, j,
                special = jQuery.event.special,
                i = 0;

            for (; (elem = elems[i]) !== undefined; i++) {
                if (Data.accepts(elem)) {
                    key = elem[data_priv.expando];

                    if (key && (data = data_priv.cache[key])) {
                        events = Object.keys(data.events || {});
                        if (events.length) {
                            for (j = 0; (type = events[j]) !== undefined; j++) {
                                if (special[type]) {
                                    jQuery.event.remove(elem, type);

                                    // This is a shortcut to avoid jQuery.event.remove's overhead
                                } else {
                                    jQuery.removeEvent(elem, type, data.handle);
                                }
                            }
                        }
                        if (data_priv.cache[key]) {
                            // Discard any remaining `private` data
                            delete data_priv.cache[key];
                        }
                    }
                }
                // Discard any remaining `user` data
                delete data_user.cache[elem[data_user.expando]];
            }
        },

        _evalUrl: function (url) {
            return jQuery.ajax({
                url: url,
                type: "GET",
                dataType: "script",
                async: false,
                global: false,
                "throws": true
            });
        }
    });

    // Support: 1.x compatibility
    // Manipulating tables requires a tbody
    function manipulationTarget(elem, content) {
        return jQuery.nodeName(elem, "table") &&
        jQuery.nodeName(content.nodeType === 1 ? content : content.firstChild, "tr") ?

        elem.getElementsByTagName("tbody")[0] ||
        elem.appendChild(elem.ownerDocument.createElement("tbody")) :
            elem;
    }

    // Replace/restore the type attribute of script elements for safe DOM manipulation
    function disableScript(elem) {
        elem.type = (elem.getAttribute("type") !== null) + "/" + elem.type;
        return elem;
    }

    function restoreScript(elem) {
        var match = rscriptTypeMasked.exec(elem.type);

        if (match) {
            elem.type = match[1];
        } else {
            elem.removeAttribute("type");
        }

        return elem;
    }

    // Mark scripts as having already been evaluated
    function setGlobalEval(elems, refElements) {
        var l = elems.length,
            i = 0;

        for (; i < l; i++) {
            data_priv.set(
                elems[i], "globalEval", !refElements || data_priv.get(refElements[i], "globalEval")
            );
        }
    }

    function cloneCopyEvent(src, dest) {
        var i, l, type, pdataOld, pdataCur, udataOld, udataCur, events;

        if (dest.nodeType !== 1) {
            return;
        }

        // 1. Copy private data: events, handlers, etc.
        if (data_priv.hasData(src)) {
            pdataOld = data_priv.access(src);
            pdataCur = data_priv.set(dest, pdataOld);
            events = pdataOld.events;

            if (events) {
                delete pdataCur.handle;
                pdataCur.events = {};

                for (type in events) {
                    for (i = 0, l = events[type].length; i < l; i++) {
                        jQuery.event.add(dest, type, events[type][i]);
                    }
                }
            }
        }

        // 2. Copy user data
        if (data_user.hasData(src)) {
            udataOld = data_user.access(src);
            udataCur = jQuery.extend({}, udataOld);

            data_user.set(dest, udataCur);
        }
    }

    function getAll(context, tag) {
        var ret = context.getElementsByTagName ? context.getElementsByTagName(tag || "*") :
            context.querySelectorAll ? context.querySelectorAll(tag || "*") :
                [];

        return tag === undefined || tag && jQuery.nodeName(context, tag) ?
            jQuery.merge([context], ret) :
            ret;
    }

    // Support: IE >= 9
    function fixInput(src, dest) {
        var nodeName = dest.nodeName.toLowerCase();

        // Fails to persist the checked state of a cloned checkbox or radio button.
        if (nodeName === "input" && manipulation_rcheckableType.test(src.type)) {
            dest.checked = src.checked;

            // Fails to return the selected option to the default selected state when cloning options
        } else if (nodeName === "input" || nodeName === "textarea") {
            dest.defaultValue = src.defaultValue;
        }
    }

    jQuery.fn.extend({
        wrapAll: function (html) {
            var wrap;

            if (jQuery.isFunction(html)) {
                return this.each(function (i) {
                    jQuery(this).wrapAll(html.call(this, i));
                });
            }

            if (this[0]) {

                // The elements to wrap the target around
                wrap = jQuery(html, this[0].ownerDocument).eq(0).clone(true);

                if (this[0].parentNode) {
                    wrap.insertBefore(this[0]);
                }

                wrap.map(function () {
                    var elem = this;

                    while (elem.firstElementChild) {
                        elem = elem.firstElementChild;
                    }

                    return elem;
                }).append(this);
            }

            return this;
        },

        wrapInner: function (html) {
            if (jQuery.isFunction(html)) {
                return this.each(function (i) {
                    jQuery(this).wrapInner(html.call(this, i));
                });
            }

            return this.each(function () {
                var self = jQuery(this),
                    contents = self.contents();

                if (contents.length) {
                    contents.wrapAll(html);

                } else {
                    self.append(html);
                }
            });
        },

        wrap: function (html) {
            var isFunction = jQuery.isFunction(html);

            return this.each(function (i) {
                jQuery(this).wrapAll(isFunction ? html.call(this, i) : html);
            });
        },

        unwrap: function () {
            return this.parent().each(function () {
                if (!jQuery.nodeName(this, "body")) {
                    jQuery(this).replaceWith(this.childNodes);
                }
            }).end();
        }
    });
    var curCSS, iframe,
    // swappable if display is none or starts with table except "table", "table-cell", or "table-caption"
    // see here for display values: https://developer.mozilla.org/en-US/docs/CSS/display
        rdisplayswap = /^(none|table(?!-c[ea]).+)/,
        rmargin = /^margin/,
        rnumsplit = new RegExp("^(" + core_pnum + ")(.*)$", "i"),
        rnumnonpx = new RegExp("^(" + core_pnum + ")(?!px)[a-z%]+$", "i"),
        rrelNum = new RegExp("^([+-])=(" + core_pnum + ")", "i"),
        elemdisplay = {BODY: "block"},

        cssShow = {position: "absolute", visibility: "hidden", display: "block"},
        cssNormalTransform = {
            letterSpacing: 0,
            fontWeight: 400
        },

        cssExpand = ["Top", "Right", "Bottom", "Left"],
        cssPrefixes = ["Webkit", "O", "Moz", "ms"];

    // return a css property mapped to a potentially vendor prefixed property
    function vendorPropName(style, name) {

        // shortcut for names that are not vendor prefixed
        if (name in style) {
            return name;
        }

        // check for vendor prefixed names
        var capName = name.charAt(0).toUpperCase() + name.slice(1),
            origName = name,
            i = cssPrefixes.length;

        while (i--) {
            name = cssPrefixes[i] + capName;
            if (name in style) {
                return name;
            }
        }

        return origName;
    }

    function isHidden(elem, el) {
        // isHidden might be called from jQuery#filter function;
        // in that case, element will be second argument
        elem = el || elem;
        return jQuery.css(elem, "display") === "none" || !jQuery.contains(elem.ownerDocument, elem);
    }

    // NOTE: we've included the "window" in window.getComputedStyle
    // because jsdom on node.js will break without it.
    function getStyles(elem) {
        return window.getComputedStyle(elem, null);
    }

    function showHide(elements, show) {
        var display, elem, hidden,
            values = [],
            index = 0,
            length = elements.length;

        for (; index < length; index++) {
            elem = elements[index];
            if (!elem.style) {
                continue;
            }

            values[index] = data_priv.get(elem, "olddisplay");
            display = elem.style.display;
            if (show) {
                // Reset the inline display of this element to learn if it is
                // being hidden by cascaded rules or not
                if (!values[index] && display === "none") {
                    elem.style.display = "";
                }

                // Set elements which have been overridden with display: none
                // in a stylesheet to whatever the default browser style is
                // for such an element
                if (elem.style.display === "" && isHidden(elem)) {
                    values[index] = data_priv.access(elem, "olddisplay", css_defaultDisplay(elem.nodeName));
                }
            } else {

                if (!values[index]) {
                    hidden = isHidden(elem);

                    if (display && display !== "none" || !hidden) {
                        data_priv.set(elem, "olddisplay", hidden ? display : jQuery.css(elem, "display"));
                    }
                }
            }
        }

        // Set the display of most of the elements in a second loop
        // to avoid the constant reflow
        for (index = 0; index < length; index++) {
            elem = elements[index];
            if (!elem.style) {
                continue;
            }
            if (!show || elem.style.display === "none" || elem.style.display === "") {
                elem.style.display = show ? values[index] || "" : "none";
            }
        }

        return elements;
    }

    jQuery.fn.extend({
        css: function (name, value) {
            return jQuery.access(this, function (elem, name, value) {
                var styles, len,
                    map = {},
                    i = 0;

                if (jQuery.isArray(name)) {
                    styles = getStyles(elem);
                    len = name.length;

                    for (; i < len; i++) {
                        map[name[i]] = jQuery.css(elem, name[i], false, styles);
                    }

                    return map;
                }

                return value !== undefined ?
                    jQuery.style(elem, name, value) :
                    jQuery.css(elem, name);
            }, name, value, arguments.length > 1);
        },
        show: function () {
            return showHide(this, true);
        },
        hide: function () {
            return showHide(this);
        },
        toggle: function (state) {
            if (typeof state === "boolean") {
                return state ? this.show() : this.hide();
            }

            return this.each(function () {
                if (isHidden(this)) {
                    jQuery(this).show();
                } else {
                    jQuery(this).hide();
                }
            });
        }
    });

    jQuery.extend({
        // Add in style property hooks for overriding the default
        // behavior of getting and setting a style property
        cssHooks: {
            opacity: {
                get: function (elem, computed) {
                    if (computed) {
                        // We should always get a number back from opacity
                        var ret = curCSS(elem, "opacity");
                        return ret === "" ? "1" : ret;
                    }
                }
            }
        },

        // Don't automatically add "px" to these possibly-unitless properties
        cssNumber: {
            "columnCount": true,
            "fillOpacity": true,
            "fontWeight": true,
            "lineHeight": true,
            "opacity": true,
            "order": true,
            "orphans": true,
            "widows": true,
            "zIndex": true,
            "zoom": true
        },

        // Add in properties whose names you wish to fix before
        // setting or getting the value
        cssProps: {
            // normalize float css property
            "float": "cssFloat"
        },

        // Get and set the style property on a DOM Node
        style: function (elem, name, value, extra) {
            // Don't set styles on text and comment nodes
            if (!elem || elem.nodeType === 3 || elem.nodeType === 8 || !elem.style) {
                return;
            }

            // Make sure that we're working with the right name
            var ret, type, hooks,
                origName = jQuery.camelCase(name),
                style = elem.style;

            name = jQuery.cssProps[origName] || ( jQuery.cssProps[origName] = vendorPropName(style, origName) );

            // gets hook for the prefixed version
            // followed by the unprefixed version
            hooks = jQuery.cssHooks[name] || jQuery.cssHooks[origName];

            // Check if we're setting a value
            if (value !== undefined) {
                type = typeof value;

                // convert relative number strings (+= or -=) to relative numbers. #7345
                if (type === "string" && (ret = rrelNum.exec(value))) {
                    value = ( ret[1] + 1 ) * ret[2] + parseFloat(jQuery.css(elem, name));
                    // Fixes bug #9237
                    type = "number";
                }

                // Make sure that NaN and null values aren't set. See: #7116
                if (value == null || type === "number" && isNaN(value)) {
                    return;
                }

                // If a number was passed in, add 'px' to the (except for certain CSS properties)
                if (type === "number" && !jQuery.cssNumber[origName]) {
                    value += "px";
                }

                // Fixes #8908, it can be done more correctly by specifying setters in cssHooks,
                // but it would mean to define eight (for every problematic property) identical functions
                if (!jQuery.support.clearCloneStyle && value === "" && name.indexOf("background") === 0) {
                    style[name] = "inherit";
                }

                // If a hook was provided, use that value, otherwise just set the specified value
                if (!hooks || !("set" in hooks) || (value = hooks.set(elem, value, extra)) !== undefined) {
                    style[name] = value;
                }

            } else {
                // If a hook was provided get the non-computed value from there
                if (hooks && "get" in hooks && (ret = hooks.get(elem, false, extra)) !== undefined) {
                    return ret;
                }

                // Otherwise just get the value from the style object
                return style[name];
            }
        },

        css: function (elem, name, extra, styles) {
            var val, num, hooks,
                origName = jQuery.camelCase(name);

            // Make sure that we're working with the right name
            name = jQuery.cssProps[origName] || ( jQuery.cssProps[origName] = vendorPropName(elem.style, origName) );

            // gets hook for the prefixed version
            // followed by the unprefixed version
            hooks = jQuery.cssHooks[name] || jQuery.cssHooks[origName];

            // If a hook was provided get the computed value from there
            if (hooks && "get" in hooks) {
                val = hooks.get(elem, true, extra);
            }

            // Otherwise, if a way to get the computed value exists, use that
            if (val === undefined) {
                val = curCSS(elem, name, styles);
            }

            //convert "normal" to computed value
            if (val === "normal" && name in cssNormalTransform) {
                val = cssNormalTransform[name];
            }

            // Return, converting to number if forced or a qualifier was provided and val looks numeric
            if (extra === "" || extra) {
                num = parseFloat(val);
                return extra === true || jQuery.isNumeric(num) ? num || 0 : val;
            }
            return val;
        }
    });

    curCSS = function (elem, name, _computed) {
        var width, minWidth, maxWidth,
            computed = _computed || getStyles(elem),

        // Support: IE9
        // getPropertyValue is only needed for .css('filter') in IE9, see #12537
            ret = computed ? computed.getPropertyValue(name) || computed[name] : undefined,
            style = elem.style;

        if (computed) {

            if (ret === "" && !jQuery.contains(elem.ownerDocument, elem)) {
                ret = jQuery.style(elem, name);
            }

            // Support: Safari 5.1
            // A tribute to the "awesome hack by Dean Edwards"
            // Safari 5.1.7 (at least) returns percentage for a larger set of values, but width seems to be reliably
            // pixels this is against the CSSOM draft spec: http://dev.w3.org/csswg/cssom/#resolved-values
            if (rnumnonpx.test(ret) && rmargin.test(name)) {

                // Remember the original values
                width = style.width;
                minWidth = style.minWidth;
                maxWidth = style.maxWidth;

                // Put in the new values to get a computed value out
                style.minWidth = style.maxWidth = style.width = ret;
                ret = computed.width;

                // Revert the changed values
                style.width = width;
                style.minWidth = minWidth;
                style.maxWidth = maxWidth;
            }
        }

        return ret;
    };

    function setPositiveNumber(elem, value, subtract) {
        var matches = rnumsplit.exec(value);
        return matches ?
            // Guard against undefined "subtract", e.g., when used as in cssHooks
        Math.max(0, matches[1] - ( subtract || 0 )) + ( matches[2] || "px" ) :
            value;
    }

    function augmentWidthOrHeight(elem, name, extra, isBorderBox, styles) {
        var i = extra === ( isBorderBox ? "border" : "content" ) ?
                // If we already have the right measurement, avoid augmentation
                4 :
                // Otherwise initialize for horizontal or vertical properties
                name === "width" ? 1 : 0,

            val = 0;

        for (; i < 4; i += 2) {
            // both box models exclude margin, so add it if we want it
            if (extra === "margin") {
                val += jQuery.css(elem, extra + cssExpand[i], true, styles);
            }

            if (isBorderBox) {
                // border-box includes padding, so remove it if we want content
                if (extra === "content") {
                    val -= jQuery.css(elem, "padding" + cssExpand[i], true, styles);
                }

                // at this point, extra isn't border nor margin, so remove border
                if (extra !== "margin") {
                    val -= jQuery.css(elem, "border" + cssExpand[i] + "Width", true, styles);
                }
            } else {
                // at this point, extra isn't content, so add padding
                val += jQuery.css(elem, "padding" + cssExpand[i], true, styles);

                // at this point, extra isn't content nor padding, so add border
                if (extra !== "padding") {
                    val += jQuery.css(elem, "border" + cssExpand[i] + "Width", true, styles);
                }
            }
        }

        return val;
    }

    function getWidthOrHeight(elem, name, extra) {

        // Start with offset property, which is equivalent to the border-box value
        var valueIsBorderBox = true,
            val = name === "width" ? elem.offsetWidth : elem.offsetHeight,
            styles = getStyles(elem),
            isBorderBox = jQuery.support.boxSizing && jQuery.css(elem, "boxSizing", false, styles) === "border-box";

        // some non-html elements return undefined for offsetWidth, so check for null/undefined
        // svg - https://bugzilla.mozilla.org/show_bug.cgi?id=649285
        // MathML - https://bugzilla.mozilla.org/show_bug.cgi?id=491668
        if (val <= 0 || val == null) {
            // Fall back to computed then uncomputed css if necessary
            val = curCSS(elem, name, styles);
            if (val < 0 || val == null) {
                val = elem.style[name];
            }

            // Computed unit is not pixels. Stop here and return.
            if (rnumnonpx.test(val)) {
                return val;
            }

            // we need the check for style in case a browser which returns unreliable values
            // for getComputedStyle silently falls back to the reliable elem.style
            valueIsBorderBox = isBorderBox && ( jQuery.support.boxSizingReliable || val === elem.style[name] );

            // Normalize "", auto, and prepare for extra
            val = parseFloat(val) || 0;
        }

        // use the active box-sizing model to add/subtract irrelevant styles
        return ( val +
            augmentWidthOrHeight(
                elem,
                name,
                extra || ( isBorderBox ? "border" : "content" ),
                valueIsBorderBox,
                styles
            )
            ) + "px";
    }

    // Try to determine the default display value of an element
    function css_defaultDisplay(nodeName) {
        var doc = document,
            display = elemdisplay[nodeName];

        if (!display) {
            display = actualDisplay(nodeName, doc);

            // If the simple way fails, read from inside an iframe
            if (display === "none" || !display) {
                // Use the already-created iframe if possible
                iframe = ( iframe ||
                jQuery("<iframe frameborder='0' width='0' height='0'/>")
                    .css("cssText", "display:block !important")
                ).appendTo(doc.documentElement);

                // Always write a new HTML skeleton so Webkit and Firefox don't choke on reuse
                doc = ( iframe[0].contentWindow || iframe[0].contentDocument ).document;
                doc.write("<!doctype html><html><body>");
                doc.close();

                display = actualDisplay(nodeName, doc);
                iframe.detach();
            }

            // Store the correct default display
            elemdisplay[nodeName] = display;
        }

        return display;
    }

    // Called ONLY from within css_defaultDisplay
    function actualDisplay(name, doc) {
        var elem = jQuery(doc.createElement(name)).appendTo(doc.body),
            display = jQuery.css(elem[0], "display");
        elem.remove();
        return display;
    }

    jQuery.each(["height", "width"], function (i, name) {
        jQuery.cssHooks[name] = {
            get: function (elem, computed, extra) {
                if (computed) {
                    // certain elements can have dimension info if we invisibly show them
                    // however, it must have a current display style that would benefit from this
                    return elem.offsetWidth === 0 && rdisplayswap.test(jQuery.css(elem, "display")) ?
                        jQuery.swap(elem, cssShow, function () {
                            return getWidthOrHeight(elem, name, extra);
                        }) :
                        getWidthOrHeight(elem, name, extra);
                }
            },

            set: function (elem, value, extra) {
                var styles = extra && getStyles(elem);
                return setPositiveNumber(elem, value, extra ?
                        augmentWidthOrHeight(
                            elem,
                            name,
                            extra,
                            jQuery.support.boxSizing && jQuery.css(elem, "boxSizing", false, styles) === "border-box",
                            styles
                        ) : 0
                );
            }
        };
    });

    // These hooks cannot be added until DOM ready because the support test
    // for it is not run until after DOM ready
    jQuery(function () {
        // Support: Android 2.3
        if (!jQuery.support.reliableMarginRight) {
            jQuery.cssHooks.marginRight = {
                get: function (elem, computed) {
                    if (computed) {
                        // Support: Android 2.3
                        // WebKit Bug 13343 - getComputedStyle returns wrong value for margin-right
                        // Work around by temporarily setting element display to inline-block
                        return jQuery.swap(elem, {"display": "inline-block"},
                            curCSS, [elem, "marginRight"]);
                    }
                }
            };
        }

        // Webkit bug: https://bugs.webkit.org/show_bug.cgi?id=29084
        // getComputedStyle returns percent when specified for top/left/bottom/right
        // rather than make the css module depend on the offset module, we just check for it here
        if (!jQuery.support.pixelPosition && jQuery.fn.position) {
            jQuery.each(["top", "left"], function (i, prop) {
                jQuery.cssHooks[prop] = {
                    get: function (elem, computed) {
                        if (computed) {
                            computed = curCSS(elem, prop);
                            // if curCSS returns percentage, fallback to offset
                            return rnumnonpx.test(computed) ?
                            jQuery(elem).position()[prop] + "px" :
                                computed;
                        }
                    }
                };
            });
        }

    });

    if (jQuery.expr && jQuery.expr.filters) {
        jQuery.expr.filters.hidden = function (elem) {
            // Support: Opera <= 12.12
            // Opera reports offsetWidths and offsetHeights less than zero on some elements
            return elem.offsetWidth <= 0 && elem.offsetHeight <= 0;
        };

        jQuery.expr.filters.visible = function (elem) {
            return !jQuery.expr.filters.hidden(elem);
        };
    }

    // These hooks are used by animate to expand properties
    jQuery.each({
        margin: "",
        padding: "",
        border: "Width"
    }, function (prefix, suffix) {
        jQuery.cssHooks[prefix + suffix] = {
            expand: function (value) {
                var i = 0,
                    expanded = {},

                // assumes a single number if not a string
                    parts = typeof value === "string" ? value.split(" ") : [value];

                for (; i < 4; i++) {
                    expanded[prefix + cssExpand[i] + suffix] =
                        parts[i] || parts[i - 2] || parts[0];
                }

                return expanded;
            }
        };

        if (!rmargin.test(prefix)) {
            jQuery.cssHooks[prefix + suffix].set = setPositiveNumber;
        }
    });
    var r20 = /%20/g,
        rbracket = /\[\]$/,
        rCRLF = /\r?\n/g,
        rsubmitterTypes = /^(?:submit|button|image|reset|file)$/i,
        rsubmittable = /^(?:input|select|textarea|keygen)/i;

    jQuery.fn.extend({
        serialize: function () {
            return jQuery.param(this.serializeArray());
        },
        serializeArray: function () {
            return this.map(function () {
                // Can add propHook for "elements" to filter or add form elements
                var elements = jQuery.prop(this, "elements");
                return elements ? jQuery.makeArray(elements) : this;
            })
                .filter(function () {
                    var type = this.type;
                    // Use .is(":disabled") so that fieldset[disabled] works
                    return this.name && !jQuery(this).is(":disabled") &&
                        rsubmittable.test(this.nodeName) && !rsubmitterTypes.test(type) &&
                        ( this.checked || !manipulation_rcheckableType.test(type) );
                })
                .map(function (i, elem) {
                    var val = jQuery(this).val();

                    return val == null ?
                        null :
                        jQuery.isArray(val) ?
                            jQuery.map(val, function (val) {
                                return {name: elem.name, value: val.replace(rCRLF, "\r\n")};
                            }) :
                        {name: elem.name, value: val.replace(rCRLF, "\r\n")};
                }).get();
        }
    });

    //Serialize an array of form elements or a set of
    //key/values into a query string
    jQuery.param = function (a, traditional) {
        var prefix,
            s = [],
            add = function (key, value) {
                // If value is a function, invoke it and return its value
                value = jQuery.isFunction(value) ? value() : ( value == null ? "" : value );
                s[s.length] = encodeURIComponent(key) + "=" + encodeURIComponent(value);
            };

        // Set traditional to true for jQuery <= 1.3.2 behavior.
        if (traditional === undefined) {
            traditional = jQuery.ajaxSettings && jQuery.ajaxSettings.traditional;
        }

        // If an array was passed in, assume that it is an array of form elements.
        if (jQuery.isArray(a) || ( a.jquery && !jQuery.isPlainObject(a) )) {
            // Serialize the form elements
            jQuery.each(a, function () {
                add(this.name, this.value);
            });

        } else {
            // If traditional, encode the "old" way (the way 1.3.2 or older
            // did it), otherwise encode params recursively.
            for (prefix in a) {
                buildParams(prefix, a[prefix], traditional, add);
            }
        }

        // Return the resulting serialization
        return s.join("&").replace(r20, "+");
    };

    function buildParams(prefix, obj, traditional, add) {
        var name;

        if (jQuery.isArray(obj)) {
            // Serialize array item.
            jQuery.each(obj, function (i, v) {
                if (traditional || rbracket.test(prefix)) {
                    // Treat each array item as a scalar.
                    add(prefix, v);

                } else {
                    // Item is non-scalar (array or object), encode its numeric index.
                    buildParams(prefix + "[" + ( typeof v === "object" ? i : "" ) + "]", v, traditional, add);
                }
            });

        } else if (!traditional && jQuery.type(obj) === "object") {
            // Serialize object item.
            for (name in obj) {
                buildParams(prefix + "[" + name + "]", obj[name], traditional, add);
            }

        } else {
            // Serialize scalar item.
            add(prefix, obj);
        }
    }

    jQuery.each(("blur focus focusin focusout load resize scroll unload click dblclick " +
    "mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave " +
    "change select submit keydown keypress keyup error contextmenu").split(" "), function (i, name) {

        // Handle event binding
        jQuery.fn[name] = function (data, fn) {
            return arguments.length > 0 ?
                this.on(name, null, data, fn) :
                this.trigger(name);
        };
    });

    jQuery.fn.extend({
        hover: function (fnOver, fnOut) {
            return this.mouseenter(fnOver).mouseleave(fnOut || fnOver);
        },

        bind: function (types, data, fn) {
            return this.on(types, null, data, fn);
        },
        unbind: function (types, fn) {
            return this.off(types, null, fn);
        },

        delegate: function (selector, types, data, fn) {
            return this.on(types, selector, data, fn);
        },
        undelegate: function (selector, types, fn) {
            // ( namespace ) or ( selector, types [, fn] )
            return arguments.length === 1 ? this.off(selector, "**") : this.off(types, selector || "**", fn);
        }
    });
    var
    // Document location
        ajaxLocParts,
        ajaxLocation,

        ajax_nonce = jQuery.now(),

        ajax_rquery = /\?/,
        rhash = /#.*$/,
        rts = /([?&])_=[^&]*/,
        rheaders = /^(.*?):[ \t]*([^\r\n]*)$/mg,
    // #7653, #8125, #8152: local protocol detection
        rlocalProtocol = /^(?:about|app|app-storage|.+-extension|file|res|widget):$/,
        rnoContent = /^(?:GET|HEAD)$/,
        rprotocol = /^\/\//,
        rurl = /^([\w.+-]+:)(?:\/\/([^\/?#:]*)(?::(\d+)|)|)/,

    // Keep a copy of the old load method
        _load = jQuery.fn.load,

    /* Prefilters
     * 1) They are useful to introduce custom dataTypes (see ajax/jsonp.js for an example)
     * 2) These are called:
     *    - BEFORE asking for a transport
     *    - AFTER param serialization (s.data is a string if s.processData is true)
     * 3) key is the dataType
     * 4) the catchall symbol "*" can be used
     * 5) execution will start with transport dataType and THEN continue down to "*" if needed
     */
        prefilters = {},

    /* Transports bindings
     * 1) key is the dataType
     * 2) the catchall symbol "*" can be used
     * 3) selection will start with transport dataType and THEN go to "*" if needed
     */
        transports = {},

    // Avoid comment-prolog char sequence (#10098); must appease lint and evade compression
        allTypes = "*/".concat("*");

    // #8138, IE may throw an exception when accessing
    // a field from window.location if document.domain has been set
    try {
        ajaxLocation = location.href;
    } catch (e) {
        // Use the href attribute of an A element
        // since IE will modify it given document.location
        ajaxLocation = document.createElement("a");
        ajaxLocation.href = "";
        ajaxLocation = ajaxLocation.href;
    }

    // Segment location into parts
    ajaxLocParts = rurl.exec(ajaxLocation.toLowerCase()) || [];

    // Base "constructor" for jQuery.ajaxPrefilter and jQuery.ajaxTransport
    function addToPrefiltersOrTransports(structure) {

        // dataTypeExpression is optional and defaults to "*"
        return function (dataTypeExpression, func) {

            if (typeof dataTypeExpression !== "string") {
                func = dataTypeExpression;
                dataTypeExpression = "*";
            }

            var dataType,
                i = 0,
                dataTypes = dataTypeExpression.toLowerCase().match(core_rnotwhite) || [];

            if (jQuery.isFunction(func)) {
                // For each dataType in the dataTypeExpression
                while ((dataType = dataTypes[i++])) {
                    // Prepend if requested
                    if (dataType[0] === "+") {
                        dataType = dataType.slice(1) || "*";
                        (structure[dataType] = structure[dataType] || []).unshift(func);

                        // Otherwise append
                    } else {
                        (structure[dataType] = structure[dataType] || []).push(func);
                    }
                }
            }
        };
    }

    // Base inspection function for prefilters and transports
    function inspectPrefiltersOrTransports(structure, options, originalOptions, jqXHR) {

        var inspected = {},
            seekingTransport = ( structure === transports );

        function inspect(dataType) {
            var selected;
            inspected[dataType] = true;
            jQuery.each(structure[dataType] || [], function (_, prefilterOrFactory) {
                var dataTypeOrTransport = prefilterOrFactory(options, originalOptions, jqXHR);
                if (typeof dataTypeOrTransport === "string" && !seekingTransport && !inspected[dataTypeOrTransport]) {
                    options.dataTypes.unshift(dataTypeOrTransport);
                    inspect(dataTypeOrTransport);
                    return false;
                } else if (seekingTransport) {
                    return !( selected = dataTypeOrTransport );
                }
            });
            return selected;
        }

        return inspect(options.dataTypes[0]) || !inspected["*"] && inspect("*");
    }

    // A special extend for ajax options
    // that takes "flat" options (not to be deep extended)
    // Fixes #9887
    function ajaxExtend(target, src) {
        var key, deep,
            flatOptions = jQuery.ajaxSettings.flatOptions || {};

        for (key in src) {
            if (src[key] !== undefined) {
                ( flatOptions[key] ? target : ( deep || (deep = {}) ) )[key] = src[key];
            }
        }
        if (deep) {
            jQuery.extend(true, target, deep);
        }

        return target;
    }

    jQuery.fn.load = function (url, params, callback) {
        if (typeof url !== "string" && _load) {
            return _load.apply(this, arguments);
        }

        var selector, type, response,
            self = this,
            off = url.indexOf(" ");

        if (off >= 0) {
            selector = url.slice(off);
            url = url.slice(0, off);
        }

        // If it's a function
        if (jQuery.isFunction(params)) {

            // We assume that it's the callback
            callback = params;
            params = undefined;

            // Otherwise, build a param string
        } else if (params && typeof params === "object") {
            type = "POST";
        }

        // If we have elements to modify, make the request
        if (self.length > 0) {
            jQuery.ajax({
                url: url,

                // if "type" variable is undefined, then "GET" method will be used
                type: type,
                dataType: "html",
                data: params
            }).done(function (responseText) {

                // Save response for use in complete callback
                response = arguments;

                self.html(selector ?

                    // If a selector was specified, locate the right elements in a dummy div
                    // Exclude scripts to avoid IE 'Permission Denied' errors
                    jQuery("<div>").append(jQuery.parseHTML(responseText)).find(selector) :

                    // Otherwise use the full result
                    responseText);

            }).complete(callback && function (jqXHR, status) {
                    self.each(callback, response || [jqXHR.responseText, status, jqXHR]);
                });
        }

        return this;
    };

    // Attach a bunch of functions for handling common AJAX events
    jQuery.each(["ajaxStart", "ajaxStop", "ajaxComplete", "ajaxError", "ajaxSuccess", "ajaxSend"], function (i, type) {
        jQuery.fn[type] = function (fn) {
            return this.on(type, fn);
        };
    });

    jQuery.extend({

        // Counter for holding the number of active queries
        active: 0,

        // Last-Modified header cache for next request
        lastModified: {},
        etag: {},

        ajaxSettings: {
            url: ajaxLocation,
            type: "GET",
            isLocal: rlocalProtocol.test(ajaxLocParts[1]),
            global: true,
            processData: true,
            async: true,
            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
            /*
             timeout: 0,
             data: null,
             dataType: null,
             username: null,
             password: null,
             cache: null,
             throws: false,
             traditional: false,
             headers: {},
             */

            accepts: {
                "*": allTypes,
                text: "text/plain",
                html: "text/html",
                xml: "application/xml, text/xml",
                json: "application/json, text/javascript"
            },

            contents: {
                xml: /xml/,
                html: /html/,
                json: /json/
            },

            responseFields: {
                xml: "responseXML",
                text: "responseText",
                json: "responseJSON"
            },

            // Data converters
            // Keys separate source (or catchall "*") and destination types with a single space
            converters: {

                // Convert anything to text
                "* text": String,

                // Text to html (true = no transformation)
                "text html": true,

                // Evaluate text as a json expression
                "text json": jQuery.parseJSON,

                // Parse text as xml
                "text xml": jQuery.parseXML
            },

            // For options that shouldn't be deep extended:
            // you can add your own custom options here if
            // and when you create one that shouldn't be
            // deep extended (see ajaxExtend)
            flatOptions: {
                url: true,
                context: true
            }
        },

        // Creates a full fledged settings object into target
        // with both ajaxSettings and settings fields.
        // If target is omitted, writes into ajaxSettings.
        ajaxSetup: function (target, settings) {
            return settings ?

                // Building a settings object
                ajaxExtend(ajaxExtend(target, jQuery.ajaxSettings), settings) :

                // Extending ajaxSettings
                ajaxExtend(jQuery.ajaxSettings, target);
        },

        ajaxPrefilter: addToPrefiltersOrTransports(prefilters),
        ajaxTransport: addToPrefiltersOrTransports(transports),

        // Main method
        ajax: function (url, options) {

            // If url is an object, simulate pre-1.5 signature
            if (typeof url === "object") {
                options = url;
                url = undefined;
            }

            // Force options to be an object
            options = options || {};

            var transport,
            // URL without anti-cache param
                cacheURL,
            // Response headers
                responseHeadersString,
                responseHeaders,
            // timeout handle
                timeoutTimer,
            // Cross-domain detection vars
                parts,
            // To know if global events are to be dispatched
                fireGlobals,
            // Loop variable
                i,
            // Create the final options object
                s = jQuery.ajaxSetup({}, options),
            // Callbacks context
                callbackContext = s.context || s,
            // Context for global events is callbackContext if it is a DOM node or jQuery collection
                globalEventContext = s.context && ( callbackContext.nodeType || callbackContext.jquery ) ?
                    jQuery(callbackContext) :
                    jQuery.event,
            // Deferreds
                deferred = jQuery.Deferred(),
                completeDeferred = jQuery.Callbacks("once memory"),
            // Status-dependent callbacks
                statusCode = s.statusCode || {},
            // Headers (they are sent all at once)
                requestHeaders = {},
                requestHeadersNames = {},
            // The jqXHR state
                state = 0,
            // Default abort message
                strAbort = "canceled",
            // Fake xhr
                jqXHR = {
                    readyState: 0,

                    // Builds headers hashtable if needed
                    getResponseHeader: function (key) {
                        var match;
                        if (state === 2) {
                            if (!responseHeaders) {
                                responseHeaders = {};
                                while ((match = rheaders.exec(responseHeadersString))) {
                                    responseHeaders[match[1].toLowerCase()] = match[2];
                                }
                            }
                            match = responseHeaders[key.toLowerCase()];
                        }
                        return match == null ? null : match;
                    },

                    // Raw string
                    getAllResponseHeaders: function () {
                        return state === 2 ? responseHeadersString : null;
                    },

                    // Caches the header
                    setRequestHeader: function (name, value) {
                        var lname = name.toLowerCase();
                        if (!state) {
                            name = requestHeadersNames[lname] = requestHeadersNames[lname] || name;
                            requestHeaders[name] = value;
                        }
                        return this;
                    },

                    // Overrides response content-type header
                    overrideMimeType: function (type) {
                        if (!state) {
                            s.mimeType = type;
                        }
                        return this;
                    },

                    // Status-dependent callbacks
                    statusCode: function (map) {
                        var code;
                        if (map) {
                            if (state < 2) {
                                for (code in map) {
                                    // Lazy-add the new callback in a way that preserves old ones
                                    statusCode[code] = [statusCode[code], map[code]];
                                }
                            } else {
                                // Execute the appropriate callbacks
                                jqXHR.always(map[jqXHR.status]);
                            }
                        }
                        return this;
                    },

                    // Cancel the request
                    abort: function (statusText) {
                        var finalText = statusText || strAbort;
                        if (transport) {
                            transport.abort(finalText);
                        }
                        done(0, finalText);
                        return this;
                    }
                };

            // Attach deferreds
            deferred.promise(jqXHR).complete = completeDeferred.add;
            jqXHR.success = jqXHR.done;
            jqXHR.error = jqXHR.fail;

            // Remove hash character (#7531: and string promotion)
            // Add protocol if not provided (prefilters might expect it)
            // Handle falsy url in the settings object (#10093: consistency with old signature)
            // We also use the url parameter if available
            s.url = ( ( url || s.url || ajaxLocation ) + "" ).replace(rhash, "")
                .replace(rprotocol, ajaxLocParts[1] + "//");

            // Alias method option to type as per ticket #12004
            s.type = options.method || options.type || s.method || s.type;

            // Extract dataTypes list
            s.dataTypes = jQuery.trim(s.dataType || "*").toLowerCase().match(core_rnotwhite) || [""];

            // A cross-domain request is in order when we have a protocol:host:port mismatch
            if (s.crossDomain == null) {
                parts = rurl.exec(s.url.toLowerCase());
                s.crossDomain = !!( parts &&
                ( parts[1] !== ajaxLocParts[1] || parts[2] !== ajaxLocParts[2] ||
                ( parts[3] || ( parts[1] === "http:" ? "80" : "443" ) ) !==
                ( ajaxLocParts[3] || ( ajaxLocParts[1] === "http:" ? "80" : "443" ) ) )
                );
            }

            // Convert data if not already a string
            if (s.data && s.processData && typeof s.data !== "string") {
                s.data = jQuery.param(s.data, s.traditional);
            }

            // Apply prefilters
            inspectPrefiltersOrTransports(prefilters, s, options, jqXHR);

            // If request was aborted inside a prefilter, stop there
            if (state === 2) {
                return jqXHR;
            }

            // We can fire global events as of now if asked to
            fireGlobals = s.global;

            // Watch for a new set of requests
            if (fireGlobals && jQuery.active++ === 0) {
                jQuery.event.trigger("ajaxStart");
            }

            // Uppercase the type
            s.type = s.type.toUpperCase();

            // Determine if request has content
            s.hasContent = !rnoContent.test(s.type);

            // Save the URL in case we're toying with the If-Modified-Since
            // and/or If-None-Match header later on
            cacheURL = s.url;

            // More options handling for requests with no content
            if (!s.hasContent) {

                // If data is available, append data to url
                if (s.data) {
                    cacheURL = ( s.url += ( ajax_rquery.test(cacheURL) ? "&" : "?" ) + s.data );
                    // #9682: remove data so that it's not used in an eventual retry
                    delete s.data;
                }

                // Add anti-cache in url if needed
                if (s.cache === false) {
                    s.url = rts.test(cacheURL) ?

                        // If there is already a '_' parameter, set its value
                        cacheURL.replace(rts, "$1_=" + ajax_nonce++) :

                        // Otherwise add one to the end
                    cacheURL + ( ajax_rquery.test(cacheURL) ? "&" : "?" ) + "_=" + ajax_nonce++;
                }
            }

            // Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
            if (s.ifModified) {
                if (jQuery.lastModified[cacheURL]) {
                    jqXHR.setRequestHeader("If-Modified-Since", jQuery.lastModified[cacheURL]);
                }
                if (jQuery.etag[cacheURL]) {
                    jqXHR.setRequestHeader("If-None-Match", jQuery.etag[cacheURL]);
                }
            }

            // Set the correct header, if data is being sent
            if (s.data && s.hasContent && s.contentType !== false || options.contentType) {
                jqXHR.setRequestHeader("Content-Type", s.contentType);
            }

            // Set the Accepts header for the server, depending on the dataType
            jqXHR.setRequestHeader(
                "Accept",
                s.dataTypes[0] && s.accepts[s.dataTypes[0]] ?
                s.accepts[s.dataTypes[0]] + ( s.dataTypes[0] !== "*" ? ", " + allTypes + "; q=0.01" : "" ) :
                    s.accepts["*"]
            );

            // Check for headers option
            for (i in s.headers) {
                jqXHR.setRequestHeader(i, s.headers[i]);
            }

            // Allow custom headers/mimetypes and early abort
            if (s.beforeSend && ( s.beforeSend.call(callbackContext, jqXHR, s) === false || state === 2 )) {
                // Abort if not done already and return
                return jqXHR.abort();
            }

            // aborting is no longer a cancellation
            strAbort = "abort";

            // Install callbacks on deferreds
            for (i in {success: 1, error: 1, complete: 1}) {
                jqXHR[i](s[i]);
            }

            // Get transport
            transport = inspectPrefiltersOrTransports(transports, s, options, jqXHR);

            // If no transport, we auto-abort
            if (!transport) {
                done(-1, "No Transport");
            } else {
                jqXHR.readyState = 1;

                // Send global event
                if (fireGlobals) {
                    globalEventContext.trigger("ajaxSend", [jqXHR, s]);
                }
                // Timeout
                if (s.async && s.timeout > 0) {
                    timeoutTimer = setTimeout(function () {
                        jqXHR.abort("timeout");
                    }, s.timeout);
                }

                try {
                    state = 1;
                    transport.send(requestHeaders, done);
                } catch (e) {
                    // Propagate exception as error if not done
                    if (state < 2) {
                        done(-1, e);
                        // Simply rethrow otherwise
                    } else {
                        throw e;
                    }
                }
            }

            // Callback for when everything is done
            function done(status, nativeStatusText, responses, headers) {
                var isSuccess, success, error, response, modified,
                    statusText = nativeStatusText;

                // Called once
                if (state === 2) {
                    return;
                }

                // State is "done" now
                state = 2;

                // Clear timeout if it exists
                if (timeoutTimer) {
                    clearTimeout(timeoutTimer);
                }

                // Dereference transport for early garbage collection
                // (no matter how long the jqXHR object will be used)
                transport = undefined;

                // Cache response headers
                responseHeadersString = headers || "";

                // Set readyState
                jqXHR.readyState = status > 0 ? 4 : 0;

                // Determine if successful
                isSuccess = status >= 200 && status < 300 || status === 304;

                // Get response data
                if (responses) {
                    response = ajaxHandleResponses(s, jqXHR, responses);
                }

                // Convert no matter what (that way responseXXX fields are always set)
                response = ajaxConvert(s, response, jqXHR, isSuccess);

                // If successful, handle type chaining
                if (isSuccess) {

                    // Set the If-Modified-Since and/or If-None-Match header, if in ifModified mode.
                    if (s.ifModified) {
                        modified = jqXHR.getResponseHeader("Last-Modified");
                        if (modified) {
                            jQuery.lastModified[cacheURL] = modified;
                        }
                        modified = jqXHR.getResponseHeader("etag");
                        if (modified) {
                            jQuery.etag[cacheURL] = modified;
                        }
                    }

                    // if no content
                    if (status === 204 || s.type === "HEAD") {
                        statusText = "nocontent";

                        // if not modified
                    } else if (status === 304) {
                        statusText = "notmodified";

                        // If we have data, let's convert it
                    } else {
                        statusText = response.state;
                        success = response.data;
                        error = response.error;
                        isSuccess = !error;
                    }
                } else {
                    // We extract error from statusText
                    // then normalize statusText and status for non-aborts
                    error = statusText;
                    if (status || !statusText) {
                        statusText = "error";
                        if (status < 0) {
                            status = 0;
                        }
                    }
                }

                // Set data for the fake xhr object
                jqXHR.status = status;
                jqXHR.statusText = ( nativeStatusText || statusText ) + "";

                // Success/Error
                if (isSuccess) {
                    deferred.resolveWith(callbackContext, [success, statusText, jqXHR]);
                } else {
                    deferred.rejectWith(callbackContext, [jqXHR, statusText, error]);
                }

                // Status-dependent callbacks
                jqXHR.statusCode(statusCode);
                statusCode = undefined;

                if (fireGlobals) {
                    globalEventContext.trigger(isSuccess ? "ajaxSuccess" : "ajaxError",
                        [jqXHR, s, isSuccess ? success : error]);
                }

                // Complete
                completeDeferred.fireWith(callbackContext, [jqXHR, statusText]);

                if (fireGlobals) {
                    globalEventContext.trigger("ajaxComplete", [jqXHR, s]);
                    // Handle the global AJAX counter
                    if (!( --jQuery.active )) {
                        jQuery.event.trigger("ajaxStop");
                    }
                }
            }

            return jqXHR;
        },

        getJSON: function (url, data, callback) {
            return jQuery.get(url, data, callback, "json");
        },

        getScript: function (url, callback) {
            return jQuery.get(url, undefined, callback, "script");
        }
    });

    jQuery.each(["get", "post"], function (i, method) {
        jQuery[method] = function (url, data, callback, type) {
            // shift arguments if data argument was omitted
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }

            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });

    /* Handles responses to an ajax request:
     * - finds the right dataType (mediates between content-type and expected dataType)
     * - returns the corresponding response
     */
    function ajaxHandleResponses(s, jqXHR, responses) {

        var ct, type, finalDataType, firstDataType,
            contents = s.contents,
            dataTypes = s.dataTypes;

        // Remove auto dataType and get content-type in the process
        while (dataTypes[0] === "*") {
            dataTypes.shift();
            if (ct === undefined) {
                ct = s.mimeType || jqXHR.getResponseHeader("Content-Type");
            }
        }

        // Check if we're dealing with a known content-type
        if (ct) {
            for (type in contents) {
                if (contents[type] && contents[type].test(ct)) {
                    dataTypes.unshift(type);
                    break;
                }
            }
        }

        // Check to see if we have a response for the expected dataType
        if (dataTypes[0] in responses) {
            finalDataType = dataTypes[0];
        } else {
            // Try convertible dataTypes
            for (type in responses) {
                if (!dataTypes[0] || s.converters[type + " " + dataTypes[0]]) {
                    finalDataType = type;
                    break;
                }
                if (!firstDataType) {
                    firstDataType = type;
                }
            }
            // Or just use first one
            finalDataType = finalDataType || firstDataType;
        }

        // If we found a dataType
        // We add the dataType to the list if needed
        // and return the corresponding response
        if (finalDataType) {
            if (finalDataType !== dataTypes[0]) {
                dataTypes.unshift(finalDataType);
            }
            return responses[finalDataType];
        }
    }

    /* Chain conversions given the request and the original response
     * Also sets the responseXXX fields on the jqXHR instance
     */
    function ajaxConvert(s, response, jqXHR, isSuccess) {
        var conv2, current, conv, tmp, prev,
            converters = {},
        // Work with a copy of dataTypes in case we need to modify it for conversion
            dataTypes = s.dataTypes.slice();

        // Create converters map with lowercased keys
        if (dataTypes[1]) {
            for (conv in s.converters) {
                converters[conv.toLowerCase()] = s.converters[conv];
            }
        }

        current = dataTypes.shift();

        // Convert to each sequential dataType
        while (current) {

            if (s.responseFields[current]) {
                jqXHR[s.responseFields[current]] = response;
            }

            // Apply the dataFilter if provided
            if (!prev && isSuccess && s.dataFilter) {
                response = s.dataFilter(response, s.dataType);
            }

            prev = current;
            current = dataTypes.shift();

            if (current) {

                // There's only work to do if current dataType is non-auto
                if (current === "*") {

                    current = prev;

                    // Convert response if prev dataType is non-auto and differs from current
                } else if (prev !== "*" && prev !== current) {

                    // Seek a direct converter
                    conv = converters[prev + " " + current] || converters["* " + current];

                    // If none found, seek a pair
                    if (!conv) {
                        for (conv2 in converters) {

                            // If conv2 outputs current
                            tmp = conv2.split(" ");
                            if (tmp[1] === current) {

                                // If prev can be converted to accepted input
                                conv = converters[prev + " " + tmp[0]] ||
                                    converters["* " + tmp[0]];
                                if (conv) {
                                    // Condense equivalence converters
                                    if (conv === true) {
                                        conv = converters[conv2];

                                        // Otherwise, insert the intermediate dataType
                                    } else if (converters[conv2] !== true) {
                                        current = tmp[0];
                                        dataTypes.unshift(tmp[1]);
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    // Apply converter (if not an equivalence)
                    if (conv !== true) {

                        // Unless errors are allowed to bubble, catch and return them
                        if (conv && s["throws"]) {
                            response = conv(response);
                        } else {
                            try {
                                response = conv(response);
                            } catch (e) {
                                return {
                                    state: "parsererror",
                                    error: conv ? e : "No conversion from " + prev + " to " + current
                                };
                            }
                        }
                    }
                }
            }
        }

        return {state: "success", data: response};
    }

    // Install script dataType
    jQuery.ajaxSetup({
        accepts: {
            script: "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"
        },
        contents: {
            script: /(?:java|ecma)script/
        },
        converters: {
            "text script": function (text) {
                jQuery.globalEval(text);
                return text;
            }
        }
    });

    // Handle cache's special case and crossDomain
    jQuery.ajaxPrefilter("script", function (s) {
        if (s.cache === undefined) {
            s.cache = false;
        }
        if (s.crossDomain) {
            s.type = "GET";
        }
    });

    // Bind script tag hack transport
    jQuery.ajaxTransport("script", function (s) {
        // This transport only deals with cross domain requests
        if (s.crossDomain) {
            var script, callback;
            return {
                send: function (_, complete) {
                    script = jQuery("<script>").prop({
                        async: true,
                        charset: s.scriptCharset,
                        src: s.url
                    }).on(
                        "load error",
                        callback = function (evt) {
                            script.remove();
                            callback = null;
                            if (evt) {
                                complete(evt.type === "error" ? 404 : 200, evt.type);
                            }
                        }
                    );
                    document.head.appendChild(script[0]);
                },
                abort: function () {
                    if (callback) {
                        callback();
                    }
                }
            };
        }
    });
    var oldCallbacks = [],
        rjsonp = /(=)\?(?=&|$)|\?\?/;

    // Default jsonp settings
    jQuery.ajaxSetup({
        jsonp: "callback",
        jsonpCallback: function () {
            var callback = oldCallbacks.pop() || ( jQuery.expando + "_" + ( ajax_nonce++ ) );
            this[callback] = true;
            return callback;
        }
    });

    // Detect, normalize options and install callbacks for jsonp requests
    jQuery.ajaxPrefilter("json jsonp", function (s, originalSettings, jqXHR) {

        var callbackName, overwritten, responseContainer,
            jsonProp = s.jsonp !== false && ( rjsonp.test(s.url) ?
                    "url" :
                typeof s.data === "string" && !( s.contentType || "" ).indexOf("application/x-www-form-urlencoded") && rjsonp.test(s.data) && "data"
                );

        // Handle iff the expected data type is "jsonp" or we have a parameter to set
        if (jsonProp || s.dataTypes[0] === "jsonp") {

            // Get callback name, remembering preexisting value associated with it
            callbackName = s.jsonpCallback = jQuery.isFunction(s.jsonpCallback) ?
                s.jsonpCallback() :
                s.jsonpCallback;

            // Insert callback into url or form data
            if (jsonProp) {
                s[jsonProp] = s[jsonProp].replace(rjsonp, "$1" + callbackName);
            } else if (s.jsonp !== false) {
                s.url += ( ajax_rquery.test(s.url) ? "&" : "?" ) + s.jsonp + "=" + callbackName;
            }

            // Use data converter to retrieve json after script execution
            s.converters["script json"] = function () {
                if (!responseContainer) {
                    jQuery.error(callbackName + " was not called");
                }
                return responseContainer[0];
            };

            // force json dataType
            s.dataTypes[0] = "json";

            // Install callback
            overwritten = window[callbackName];
            window[callbackName] = function () {
                responseContainer = arguments;
            };

            // Clean-up function (fires after converters)
            jqXHR.always(function () {
                // Restore preexisting value
                window[callbackName] = overwritten;

                // Save back as free
                if (s[callbackName]) {
                    // make sure that re-using the options doesn't screw things around
                    s.jsonpCallback = originalSettings.jsonpCallback;

                    // save the callback name for future use
                    oldCallbacks.push(callbackName);
                }

                // Call if it was a function and we have a response
                if (responseContainer && jQuery.isFunction(overwritten)) {
                    overwritten(responseContainer[0]);
                }

                responseContainer = overwritten = undefined;
            });

            // Delegate to script
            return "script";
        }
    });
    jQuery.ajaxSettings.xhr = function () {
        try {
            return new XMLHttpRequest();
        } catch (e) {
        }
    };

    var xhrSupported = jQuery.ajaxSettings.xhr(),
        xhrSuccessStatus = {
            // file protocol always yields status code 0, assume 200
            0: 200,
            // Support: IE9
            // #1450: sometimes IE returns 1223 when it should be 204
            1223: 204
        },
    // Support: IE9
    // We need to keep track of outbound xhr and abort them manually
    // because IE is not smart enough to do it all by itself
        xhrId = 0,
        xhrCallbacks = {};

    if (window.ActiveXObject) {
        jQuery(window).on("unload", function () {
            for (var key in xhrCallbacks) {
                xhrCallbacks[key]();
            }
            xhrCallbacks = undefined;
        });
    }

    jQuery.support.cors = !!xhrSupported && ( "withCredentials" in xhrSupported );
    jQuery.support.ajax = xhrSupported = !!xhrSupported;

    jQuery.ajaxTransport(function (options) {
        var callback;
        // Cross domain only allowed if supported through XMLHttpRequest
        if (jQuery.support.cors || xhrSupported && !options.crossDomain) {
            return {
                send: function (headers, complete) {
                    var i, id,
                        xhr = options.xhr();
                    xhr.open(options.type, options.url, options.async, options.username, options.password);
                    // Apply custom fields if provided
                    if (options.xhrFields) {
                        for (i in options.xhrFields) {
                            xhr[i] = options.xhrFields[i];
                        }
                    }
                    // Override mime type if needed
                    if (options.mimeType && xhr.overrideMimeType) {
                        xhr.overrideMimeType(options.mimeType);
                    }
                    // X-Requested-With header
                    // For cross-domain requests, seeing as conditions for a preflight are
                    // akin to a jigsaw puzzle, we simply never set it to be sure.
                    // (it can always be set on a per-request basis or even using ajaxSetup)
                    // For same-domain requests, won't change header if already provided.
                    if (!options.crossDomain && !headers["X-Requested-With"]) {
                        headers["X-Requested-With"] = "XMLHttpRequest";
                    }
                    // Set headers
                    for (i in headers) {
                        xhr.setRequestHeader(i, headers[i]);
                    }
                    // Callback
                    callback = function (type) {
                        return function () {
                            if (callback) {
                                delete xhrCallbacks[id];
                                callback = xhr.onload = xhr.onerror = null;
                                if (type === "abort") {
                                    xhr.abort();
                                } else if (type === "error") {
                                    complete(
                                        // file protocol always yields status 0, assume 404
                                        xhr.status || 404,
                                        xhr.statusText
                                    );
                                } else {
                                    complete(
                                        xhrSuccessStatus[xhr.status] || xhr.status,
                                        xhr.statusText,
                                        // Support: IE9
                                        // #11426: When requesting binary data, IE9 will throw an exception
                                        // on any attempt to access responseText
                                        typeof xhr.responseText === "string" ? {
                                            text: xhr.responseText
                                        } : undefined,
                                        xhr.getAllResponseHeaders()
                                    );
                                }
                            }
                        };
                    };
                    // Listen to events
                    xhr.onload = callback();
                    xhr.onerror = callback("error");
                    // Create the abort callback
                    callback = xhrCallbacks[( id = xhrId++ )] = callback("abort");
                    // Do send the request
                    // This may raise an exception which is actually
                    // handled in jQuery.ajax (so no try/catch here)
                    xhr.send(options.hasContent && options.data || null);
                },
                abort: function () {
                    if (callback) {
                        callback();
                    }
                }
            };
        }
    });
    var fxNow, timerId,
        rfxtypes = /^(?:toggle|show|hide)$/,
        rfxnum = new RegExp("^(?:([+-])=|)(" + core_pnum + ")([a-z%]*)$", "i"),
        rrun = /queueHooks$/,
        animationPrefilters = [defaultPrefilter],
        tweeners = {
            "*": [function (prop, value) {
                var tween = this.createTween(prop, value),
                    target = tween.cur(),
                    parts = rfxnum.exec(value),
                    unit = parts && parts[3] || ( jQuery.cssNumber[prop] ? "" : "px" ),

                // Starting value computation is required for potential unit mismatches
                    start = ( jQuery.cssNumber[prop] || unit !== "px" && +target ) &&
                        rfxnum.exec(jQuery.css(tween.elem, prop)),
                    scale = 1,
                    maxIterations = 20;

                if (start && start[3] !== unit) {
                    // Trust units reported by jQuery.css
                    unit = unit || start[3];

                    // Make sure we update the tween properties later on
                    parts = parts || [];

                    // Iteratively approximate from a nonzero starting point
                    start = +target || 1;

                    do {
                        // If previous iteration zeroed out, double until we get *something*
                        // Use a string for doubling factor so we don't accidentally see scale as unchanged below
                        scale = scale || ".5";

                        // Adjust and apply
                        start = start / scale;
                        jQuery.style(tween.elem, prop, start + unit);

                        // Update scale, tolerating zero or NaN from tween.cur()
                        // And breaking the loop if scale is unchanged or perfect, or if we've just had enough
                    } while (scale !== (scale = tween.cur() / target) && scale !== 1 && --maxIterations);
                }

                // Update tween properties
                if (parts) {
                    start = tween.start = +start || +target || 0;
                    tween.unit = unit;
                    // If a +=/-= token was provided, we're doing a relative animation
                    tween.end = parts[1] ?
                    start + ( parts[1] + 1 ) * parts[2] :
                        +parts[2];
                }

                return tween;
            }]
        };

    // Animations created synchronously will run synchronously
    function createFxNow() {
        setTimeout(function () {
            fxNow = undefined;
        });
        return ( fxNow = jQuery.now() );
    }

    function createTween(value, prop, animation) {
        var tween,
            collection = ( tweeners[prop] || [] ).concat(tweeners["*"]),
            index = 0,
            length = collection.length;
        for (; index < length; index++) {
            if ((tween = collection[index].call(animation, prop, value))) {

                // we're done with this property
                return tween;
            }
        }
    }

    function Animation(elem, properties, options) {
        var result,
            stopped,
            index = 0,
            length = animationPrefilters.length,
            deferred = jQuery.Deferred().always(function () {
                // don't match elem in the :animated selector
                delete tick.elem;
            }),
            tick = function () {
                if (stopped) {
                    return false;
                }
                var currentTime = fxNow || createFxNow(),
                    remaining = Math.max(0, animation.startTime + animation.duration - currentTime),
                // archaic crash bug won't allow us to use 1 - ( 0.5 || 0 ) (#12497)
                    temp = remaining / animation.duration || 0,
                    percent = 1 - temp,
                    index = 0,
                    length = animation.tweens.length;

                for (; index < length; index++) {
                    animation.tweens[index].run(percent);
                }

                deferred.notifyWith(elem, [animation, percent, remaining]);

                if (percent < 1 && length) {
                    return remaining;
                } else {
                    deferred.resolveWith(elem, [animation]);
                    return false;
                }
            },
            animation = deferred.promise({
                elem: elem,
                props: jQuery.extend({}, properties),
                opts: jQuery.extend(true, {specialEasing: {}}, options),
                originalProperties: properties,
                originalOptions: options,
                startTime: fxNow || createFxNow(),
                duration: options.duration,
                tweens: [],
                createTween: function (prop, end) {
                    var tween = jQuery.Tween(elem, animation.opts, prop, end,
                        animation.opts.specialEasing[prop] || animation.opts.easing);
                    animation.tweens.push(tween);
                    return tween;
                },
                stop: function (gotoEnd) {
                    var index = 0,
                    // if we are going to the end, we want to run all the tweens
                    // otherwise we skip this part
                        length = gotoEnd ? animation.tweens.length : 0;
                    if (stopped) {
                        return this;
                    }
                    stopped = true;
                    for (; index < length; index++) {
                        animation.tweens[index].run(1);
                    }

                    // resolve when we played the last frame
                    // otherwise, reject
                    if (gotoEnd) {
                        deferred.resolveWith(elem, [animation, gotoEnd]);
                    } else {
                        deferred.rejectWith(elem, [animation, gotoEnd]);
                    }
                    return this;
                }
            }),
            props = animation.props;

        propFilter(props, animation.opts.specialEasing);

        for (; index < length; index++) {
            result = animationPrefilters[index].call(animation, elem, props, animation.opts);
            if (result) {
                return result;
            }
        }

        jQuery.map(props, createTween, animation);

        if (jQuery.isFunction(animation.opts.start)) {
            animation.opts.start.call(elem, animation);
        }

        jQuery.fx.timer(
            jQuery.extend(tick, {
                elem: elem,
                anim: animation,
                queue: animation.opts.queue
            })
        );

        // attach callbacks from options
        return animation.progress(animation.opts.progress)
            .done(animation.opts.done, animation.opts.complete)
            .fail(animation.opts.fail)
            .always(animation.opts.always);
    }

    function propFilter(props, specialEasing) {
        var index, name, easing, value, hooks;

        // camelCase, specialEasing and expand cssHook pass
        for (index in props) {
            name = jQuery.camelCase(index);
            easing = specialEasing[name];
            value = props[index];
            if (jQuery.isArray(value)) {
                easing = value[1];
                value = props[index] = value[0];
            }

            if (index !== name) {
                props[name] = value;
                delete props[index];
            }

            hooks = jQuery.cssHooks[name];
            if (hooks && "expand" in hooks) {
                value = hooks.expand(value);
                delete props[name];

                // not quite $.extend, this wont overwrite keys already present.
                // also - reusing 'index' from above because we have the correct "name"
                for (index in value) {
                    if (!( index in props )) {
                        props[index] = value[index];
                        specialEasing[index] = easing;
                    }
                }
            } else {
                specialEasing[name] = easing;
            }
        }
    }

    jQuery.Animation = jQuery.extend(Animation, {

        tweener: function (props, callback) {
            if (jQuery.isFunction(props)) {
                callback = props;
                props = ["*"];
            } else {
                props = props.split(" ");
            }

            var prop,
                index = 0,
                length = props.length;

            for (; index < length; index++) {
                prop = props[index];
                tweeners[prop] = tweeners[prop] || [];
                tweeners[prop].unshift(callback);
            }
        },

        prefilter: function (callback, prepend) {
            if (prepend) {
                animationPrefilters.unshift(callback);
            } else {
                animationPrefilters.push(callback);
            }
        }
    });

    function defaultPrefilter(elem, props, opts) {
        /* jshint validthis: true */
        var prop, value, toggle, tween, hooks, oldfire,
            anim = this,
            orig = {},
            style = elem.style,
            hidden = elem.nodeType && isHidden(elem),
            dataShow = data_priv.get(elem, "fxshow");

        // handle queue: false promises
        if (!opts.queue) {
            hooks = jQuery._queueHooks(elem, "fx");
            if (hooks.unqueued == null) {
                hooks.unqueued = 0;
                oldfire = hooks.empty.fire;
                hooks.empty.fire = function () {
                    if (!hooks.unqueued) {
                        oldfire();
                    }
                };
            }
            hooks.unqueued++;

            anim.always(function () {
                // doing this makes sure that the complete handler will be called
                // before this completes
                anim.always(function () {
                    hooks.unqueued--;
                    if (!jQuery.queue(elem, "fx").length) {
                        hooks.empty.fire();
                    }
                });
            });
        }

        // height/width overflow pass
        if (elem.nodeType === 1 && ( "height" in props || "width" in props )) {
            // Make sure that nothing sneaks out
            // Record all 3 overflow attributes because IE9-10 do not
            // change the overflow attribute when overflowX and
            // overflowY are set to the same value
            opts.overflow = [style.overflow, style.overflowX, style.overflowY];

            // Set display property to inline-block for height/width
            // animations on inline elements that are having width/height animated
            if (jQuery.css(elem, "display") === "inline" &&
                jQuery.css(elem, "float") === "none") {

                style.display = "inline-block";
            }
        }

        if (opts.overflow) {
            style.overflow = "hidden";
            anim.always(function () {
                style.overflow = opts.overflow[0];
                style.overflowX = opts.overflow[1];
                style.overflowY = opts.overflow[2];
            });
        }

        // show/hide pass
        for (prop in props) {
            value = props[prop];
            if (rfxtypes.exec(value)) {
                delete props[prop];
                toggle = toggle || value === "toggle";
                if (value === ( hidden ? "hide" : "show" )) {

                    // If there is dataShow left over from a stopped hide or show and we are going to proceed with
                    // show, we should pretend to be hidden
                    if (value === "show" && dataShow && dataShow[prop] !== undefined) {
                        hidden = true;
                    } else {
                        continue;
                    }
                }
                orig[prop] = dataShow && dataShow[prop] || jQuery.style(elem, prop);
            }
        }

        if (!jQuery.isEmptyObject(orig)) {
            if (dataShow) {
                if ("hidden" in dataShow) {
                    hidden = dataShow.hidden;
                }
            } else {
                dataShow = data_priv.access(elem, "fxshow", {});
            }

            // store state if its toggle - enables .stop().toggle() to "reverse"
            if (toggle) {
                dataShow.hidden = !hidden;
            }
            if (hidden) {
                jQuery(elem).show();
            } else {
                anim.done(function () {
                    jQuery(elem).hide();
                });
            }
            anim.done(function () {
                var prop;

                data_priv.remove(elem, "fxshow");
                for (prop in orig) {
                    jQuery.style(elem, prop, orig[prop]);
                }
            });
            for (prop in orig) {
                tween = createTween(hidden ? dataShow[prop] : 0, prop, anim);

                if (!( prop in dataShow )) {
                    dataShow[prop] = tween.start;
                    if (hidden) {
                        tween.end = tween.start;
                        tween.start = prop === "width" || prop === "height" ? 1 : 0;
                    }
                }
            }
        }
    }

    function Tween(elem, options, prop, end, easing) {
        return new Tween.prototype.init(elem, options, prop, end, easing);
    }

    jQuery.Tween = Tween;

    Tween.prototype = {
        constructor: Tween,
        init: function (elem, options, prop, end, easing, unit) {
            this.elem = elem;
            this.prop = prop;
            this.easing = easing || "swing";
            this.options = options;
            this.start = this.now = this.cur();
            this.end = end;
            this.unit = unit || ( jQuery.cssNumber[prop] ? "" : "px" );
        },
        cur: function () {
            var hooks = Tween.propHooks[this.prop];

            return hooks && hooks.get ?
                hooks.get(this) :
                Tween.propHooks._default.get(this);
        },
        run: function (percent) {
            var eased,
                hooks = Tween.propHooks[this.prop];

            if (this.options.duration) {
                this.pos = eased = jQuery.easing[this.easing](
                    percent, this.options.duration * percent, 0, 1, this.options.duration
                );
            } else {
                this.pos = eased = percent;
            }
            this.now = ( this.end - this.start ) * eased + this.start;

            if (this.options.step) {
                this.options.step.call(this.elem, this.now, this);
            }

            if (hooks && hooks.set) {
                hooks.set(this);
            } else {
                Tween.propHooks._default.set(this);
            }
            return this;
        }
    };

    Tween.prototype.init.prototype = Tween.prototype;

    Tween.propHooks = {
        _default: {
            get: function (tween) {
                var result;

                if (tween.elem[tween.prop] != null &&
                    (!tween.elem.style || tween.elem.style[tween.prop] == null)) {
                    return tween.elem[tween.prop];
                }

                // passing an empty string as a 3rd parameter to .css will automatically
                // attempt a parseFloat and fallback to a string if the parse fails
                // so, simple values such as "10px" are parsed to Float.
                // complex values such as "rotate(1rad)" are returned as is.
                result = jQuery.css(tween.elem, tween.prop, "");
                // Empty strings, null, undefined and "auto" are converted to 0.
                return !result || result === "auto" ? 0 : result;
            },
            set: function (tween) {
                // use step hook for back compat - use cssHook if its there - use .style if its
                // available and use plain properties where available
                if (jQuery.fx.step[tween.prop]) {
                    jQuery.fx.step[tween.prop](tween);
                } else if (tween.elem.style && ( tween.elem.style[jQuery.cssProps[tween.prop]] != null || jQuery.cssHooks[tween.prop] )) {
                    jQuery.style(tween.elem, tween.prop, tween.now + tween.unit);
                } else {
                    tween.elem[tween.prop] = tween.now;
                }
            }
        }
    };

    // Support: IE9
    // Panic based approach to setting things on disconnected nodes

    Tween.propHooks.scrollTop = Tween.propHooks.scrollLeft = {
        set: function (tween) {
            if (tween.elem.nodeType && tween.elem.parentNode) {
                tween.elem[tween.prop] = tween.now;
            }
        }
    };

    jQuery.each(["toggle", "show", "hide"], function (i, name) {
        var cssFn = jQuery.fn[name];
        jQuery.fn[name] = function (speed, easing, callback) {
            return speed == null || typeof speed === "boolean" ?
                cssFn.apply(this, arguments) :
                this.animate(genFx(name, true), speed, easing, callback);
        };
    });

    jQuery.fn.extend({
        fadeTo: function (speed, to, easing, callback) {

            // show any hidden elements after setting opacity to 0
            return this.filter(isHidden).css("opacity", 0).show()

                // animate to the value specified
                .end().animate({opacity: to}, speed, easing, callback);
        },
        animate: function (prop, speed, easing, callback) {
            var empty = jQuery.isEmptyObject(prop),
                optall = jQuery.speed(speed, easing, callback),
                doAnimation = function () {
                    // Operate on a copy of prop so per-property easing won't be lost
                    var anim = Animation(this, jQuery.extend({}, prop), optall);

                    // Empty animations, or finishing resolves immediately
                    if (empty || data_priv.get(this, "finish")) {
                        anim.stop(true);
                    }
                };
            doAnimation.finish = doAnimation;

            return empty || optall.queue === false ?
                this.each(doAnimation) :
                this.queue(optall.queue, doAnimation);
        },
        stop: function (type, clearQueue, gotoEnd) {
            var stopQueue = function (hooks) {
                var stop = hooks.stop;
                delete hooks.stop;
                stop(gotoEnd);
            };

            if (typeof type !== "string") {
                gotoEnd = clearQueue;
                clearQueue = type;
                type = undefined;
            }
            if (clearQueue && type !== false) {
                this.queue(type || "fx", []);
            }

            return this.each(function () {
                var dequeue = true,
                    index = type != null && type + "queueHooks",
                    timers = jQuery.timers,
                    data = data_priv.get(this);

                if (index) {
                    if (data[index] && data[index].stop) {
                        stopQueue(data[index]);
                    }
                } else {
                    for (index in data) {
                        if (data[index] && data[index].stop && rrun.test(index)) {
                            stopQueue(data[index]);
                        }
                    }
                }

                for (index = timers.length; index--;) {
                    if (timers[index].elem === this && (type == null || timers[index].queue === type)) {
                        timers[index].anim.stop(gotoEnd);
                        dequeue = false;
                        timers.splice(index, 1);
                    }
                }

                // start the next in the queue if the last step wasn't forced
                // timers currently will call their complete callbacks, which will dequeue
                // but only if they were gotoEnd
                if (dequeue || !gotoEnd) {
                    jQuery.dequeue(this, type);
                }
            });
        },
        finish: function (type) {
            if (type !== false) {
                type = type || "fx";
            }
            return this.each(function () {
                var index,
                    data = data_priv.get(this),
                    queue = data[type + "queue"],
                    hooks = data[type + "queueHooks"],
                    timers = jQuery.timers,
                    length = queue ? queue.length : 0;

                // enable finishing flag on private data
                data.finish = true;

                // empty the queue first
                jQuery.queue(this, type, []);

                if (hooks && hooks.stop) {
                    hooks.stop.call(this, true);
                }

                // look for any active animations, and finish them
                for (index = timers.length; index--;) {
                    if (timers[index].elem === this && timers[index].queue === type) {
                        timers[index].anim.stop(true);
                        timers.splice(index, 1);
                    }
                }

                // look for any animations in the old queue and finish them
                for (index = 0; index < length; index++) {
                    if (queue[index] && queue[index].finish) {
                        queue[index].finish.call(this);
                    }
                }

                // turn off finishing flag
                delete data.finish;
            });
        }
    });

    // Generate parameters to create a standard animation
    function genFx(type, includeWidth) {
        var which,
            attrs = {height: type},
            i = 0;

        // if we include width, step value is 1 to do all cssExpand values,
        // if we don't include width, step value is 2 to skip over Left and Right
        includeWidth = includeWidth ? 1 : 0;
        for (; i < 4; i += 2 - includeWidth) {
            which = cssExpand[i];
            attrs["margin" + which] = attrs["padding" + which] = type;
        }

        if (includeWidth) {
            attrs.opacity = attrs.width = type;
        }

        return attrs;
    }

    // Generate shortcuts for custom animations
    jQuery.each({
        slideDown: genFx("show"),
        slideUp: genFx("hide"),
        slideToggle: genFx("toggle"),
        fadeIn: {opacity: "show"},
        fadeOut: {opacity: "hide"},
        fadeToggle: {opacity: "toggle"}
    }, function (name, props) {
        jQuery.fn[name] = function (speed, easing, callback) {
            return this.animate(props, speed, easing, callback);
        };
    });

    jQuery.speed = function (speed, easing, fn) {
        var opt = speed && typeof speed === "object" ? jQuery.extend({}, speed) : {
            complete: fn || !fn && easing ||
            jQuery.isFunction(speed) && speed,
            duration: speed,
            easing: fn && easing || easing && !jQuery.isFunction(easing) && easing
        };

        opt.duration = jQuery.fx.off ? 0 : typeof opt.duration === "number" ? opt.duration :
            opt.duration in jQuery.fx.speeds ? jQuery.fx.speeds[opt.duration] : jQuery.fx.speeds._default;

        // normalize opt.queue - true/undefined/null -> "fx"
        if (opt.queue == null || opt.queue === true) {
            opt.queue = "fx";
        }

        // Queueing
        opt.old = opt.complete;

        opt.complete = function () {
            if (jQuery.isFunction(opt.old)) {
                opt.old.call(this);
            }

            if (opt.queue) {
                jQuery.dequeue(this, opt.queue);
            }
        };

        return opt;
    };

    jQuery.easing = {
        linear: function (p) {
            return p;
        },
        swing: function (p) {
            return 0.5 - Math.cos(p * Math.PI) / 2;
        }
    };

    jQuery.timers = [];
    jQuery.fx = Tween.prototype.init;
    jQuery.fx.tick = function () {
        var timer,
            timers = jQuery.timers,
            i = 0;

        fxNow = jQuery.now();

        for (; i < timers.length; i++) {
            timer = timers[i];
            // Checks the timer has not already been removed
            if (!timer() && timers[i] === timer) {
                timers.splice(i--, 1);
            }
        }

        if (!timers.length) {
            jQuery.fx.stop();
        }
        fxNow = undefined;
    };

    jQuery.fx.timer = function (timer) {
        if (timer() && jQuery.timers.push(timer)) {
            jQuery.fx.start();
        }
    };

    jQuery.fx.interval = 13;

    jQuery.fx.start = function () {
        if (!timerId) {
            timerId = setInterval(jQuery.fx.tick, jQuery.fx.interval);
        }
    };

    jQuery.fx.stop = function () {
        clearInterval(timerId);
        timerId = null;
    };

    jQuery.fx.speeds = {
        slow: 600,
        fast: 200,
        // Default speed
        _default: 400
    };

    // Back Compat <1.8 extension point
    jQuery.fx.step = {};

    if (jQuery.expr && jQuery.expr.filters) {
        jQuery.expr.filters.animated = function (elem) {
            return jQuery.grep(jQuery.timers, function (fn) {
                return elem === fn.elem;
            }).length;
        };
    }
    jQuery.fn.offset = function (options) {
        if (arguments.length) {
            return options === undefined ?
                this :
                this.each(function (i) {
                    jQuery.offset.setOffset(this, options, i);
                });
        }

        var docElem, win,
            elem = this[0],
            box = {top: 0, left: 0},
            doc = elem && elem.ownerDocument;

        if (!doc) {
            return;
        }

        docElem = doc.documentElement;

        // Make sure it's not a disconnected DOM node
        if (!jQuery.contains(docElem, elem)) {
            return box;
        }

        // If we don't have gBCR, just use 0,0 rather than error
        // BlackBerry 5, iOS 3 (original iPhone)
        if (typeof elem.getBoundingClientRect !== core_strundefined) {
            box = elem.getBoundingClientRect();
        }
        win = getWindow(doc);
        return {
            top: box.top + win.pageYOffset - docElem.clientTop,
            left: box.left + win.pageXOffset - docElem.clientLeft
        };
    };

    jQuery.offset = {

        setOffset: function (elem, options, i) {
            var curPosition, curLeft, curCSSTop, curTop, curOffset, curCSSLeft, calculatePosition,
                position = jQuery.css(elem, "position"),
                curElem = jQuery(elem),
                props = {};

            // Set position first, in-case top/left are set even on static elem
            if (position === "static") {
                elem.style.position = "relative";
            }

            curOffset = curElem.offset();
            curCSSTop = jQuery.css(elem, "top");
            curCSSLeft = jQuery.css(elem, "left");
            calculatePosition = ( position === "absolute" || position === "fixed" ) && ( curCSSTop + curCSSLeft ).indexOf("auto") > -1;

            // Need to be able to calculate position if either top or left is auto and position is either absolute or
            // fixed
            if (calculatePosition) {
                curPosition = curElem.position();
                curTop = curPosition.top;
                curLeft = curPosition.left;

            } else {
                curTop = parseFloat(curCSSTop) || 0;
                curLeft = parseFloat(curCSSLeft) || 0;
            }

            if (jQuery.isFunction(options)) {
                options = options.call(elem, i, curOffset);
            }

            if (options.top != null) {
                props.top = ( options.top - curOffset.top ) + curTop;
            }
            if (options.left != null) {
                props.left = ( options.left - curOffset.left ) + curLeft;
            }

            if ("using" in options) {
                options.using.call(elem, props);

            } else {
                curElem.css(props);
            }
        }
    };

    jQuery.fn.extend({

        position: function () {
            if (!this[0]) {
                return;
            }

            var offsetParent, offset,
                elem = this[0],
                parentOffset = {top: 0, left: 0};

            // Fixed elements are offset from window (parentOffset = {top:0, left: 0}, because it is it's only offset
            // parent
            if (jQuery.css(elem, "position") === "fixed") {
                // We assume that getBoundingClientRect is available when computed position is fixed
                offset = elem.getBoundingClientRect();

            } else {
                // Get *real* offsetParent
                offsetParent = this.offsetParent();

                // Get correct offsets
                offset = this.offset();
                if (!jQuery.nodeName(offsetParent[0], "html")) {
                    parentOffset = offsetParent.offset();
                }

                // Add offsetParent borders
                parentOffset.top += jQuery.css(offsetParent[0], "borderTopWidth", true);
                parentOffset.left += jQuery.css(offsetParent[0], "borderLeftWidth", true);
            }

            // Subtract parent offsets and element margins
            return {
                top: offset.top - parentOffset.top - jQuery.css(elem, "marginTop", true),
                left: offset.left - parentOffset.left - jQuery.css(elem, "marginLeft", true)
            };
        },

        offsetParent: function () {
            return this.map(function () {
                var offsetParent = this.offsetParent || docElem;

                while (offsetParent && ( !jQuery.nodeName(offsetParent, "html") && jQuery.css(offsetParent, "position") === "static" )) {
                    offsetParent = offsetParent.offsetParent;
                }

                return offsetParent || docElem;
            });
        }
    });

    // Create scrollLeft and scrollTop methods
    jQuery.each({scrollLeft: "pageXOffset", scrollTop: "pageYOffset"}, function (method, prop) {
        var top = "pageYOffset" === prop;

        jQuery.fn[method] = function (val) {
            return jQuery.access(this, function (elem, method, val) {
                var win = getWindow(elem);

                if (val === undefined) {
                    return win ? win[prop] : elem[method];
                }

                if (win) {
                    win.scrollTo(
                        !top ? val : window.pageXOffset,
                        top ? val : window.pageYOffset
                    );

                } else {
                    elem[method] = val;
                }
            }, method, val, arguments.length, null);
        };
    });

    function getWindow(elem) {
        return jQuery.isWindow(elem) ? elem : elem.nodeType === 9 && elem.defaultView;
    }

    // Create innerHeight, innerWidth, height, width, outerHeight and outerWidth methods
    jQuery.each({Height: "height", Width: "width"}, function (name, type) {
        jQuery.each({padding: "inner" + name, content: type, "": "outer" + name}, function (defaultExtra, funcName) {
            // margin is only for outerHeight, outerWidth
            jQuery.fn[funcName] = function (margin, value) {
                var chainable = arguments.length && ( defaultExtra || typeof margin !== "boolean" ),
                    extra = defaultExtra || ( margin === true || value === true ? "margin" : "border" );

                return jQuery.access(this, function (elem, type, value) {
                    var doc;

                    if (jQuery.isWindow(elem)) {
                        // As of 5/8/2012 this will yield incorrect results for Mobile Safari, but there
                        // isn't a whole lot we can do. See pull request at this URL for discussion:
                        // https://github.com/jquery/jquery/pull/764
                        return elem.document.documentElement["client" + name];
                    }

                    // Get document width or height
                    if (elem.nodeType === 9) {
                        doc = elem.documentElement;

                        // Either scroll[Width/Height] or offset[Width/Height] or client[Width/Height],
                        // whichever is greatest
                        return Math.max(
                            elem.body["scroll" + name], doc["scroll" + name],
                            elem.body["offset" + name], doc["offset" + name],
                            doc["client" + name]
                        );
                    }

                    return value === undefined ?
                        // Get width or height on the element, requesting but not forcing parseFloat
                        jQuery.css(elem, type, extra) :

                        // Set width or height on the element
                        jQuery.style(elem, type, value, extra);
                }, type, chainable ? margin : undefined, chainable, null);
            };
        });
    });
    // Limit scope pollution from any deprecated API
    // (function() {

    // The number of elements contained in the matched element set
    jQuery.fn.size = function () {
        return this.length;
    };

    jQuery.fn.andSelf = jQuery.fn.addBack;

    // })();
    if (typeof module === "object" && module && typeof module.exports === "object") {
        // Expose jQuery as module.exports in loaders that implement the Node
        // module pattern (including browserify). Do not create the global, since
        // the user will be storing it themselves locally, and globals are frowned
        // upon in the Node module world.
        module.exports = jQuery;
    } else {
        // Register as a named AMD module, since jQuery can be concatenated with other
        // files that may use define, but not via a proper concatenation script that
        // understands anonymous AMD modules. A named AMD is safest and most robust
        // way to register. Lowercase jquery is used because AMD module names are
        // derived from file names, and jQuery is normally delivered in a lowercase
        // file name. Do this after creating the global so that if an AMD module wants
        // to call noConflict to hide this version of jQuery, it will work.
        if (typeof define === "function" && define.amd) {
            define("jquery", [], function () { return jQuery; });
        }
    }

    // If there is a window object, that at least has a document property,
    // define jQuery and $ identifiers
    if (typeof window === "object" && typeof window.document === "object") {
        window.jQuery = window.$ = jQuery;
    }

})(window);

/**
 * @license
 * Lo-Dash 2.2.1 (Custom Build) <http://lodash.com/>
 * Build: `lodash modern -o ./dist/lodash.js`
 * Copyright 2012-2013 The Dojo Foundation <http://dojofoundation.org/>
 * Based on Underscore.js 1.5.2 <http://underscorejs.org/LICENSE>
 * Copyright 2009-2013 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors
 * Available under MIT license <http://lodash.com/license>
 */
;
(function () {

    /** Used as a safe reference for `undefined` in pre ES5 environments */
    var undefined;

    /** Used to pool arrays and objects used internally */
    var arrayPool = [],
        objectPool = [];

    /** Used to generate unique IDs */
    var idCounter = 0;

    /** Used to prefix keys to avoid issues with `__proto__` and properties on `Object.prototype` */
    var keyPrefix = +new Date + '';

    /** Used as the size when optimizations are enabled for large arrays */
    var largeArraySize = 75;

    /** Used as the max size of the `arrayPool` and `objectPool` */
    var maxPoolSize = 40;

    /** Used to detect and test whitespace */
    var whitespace = (
        // whitespace
    ' \t\x0B\f\xA0\ufeff' +

        // line terminators
    '\n\r\u2028\u2029' +

        // unicode category "Zs" space separators
    '\u1680\u180e\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200a\u202f\u205f\u3000'
    );

    /** Used to match empty string literals in compiled template source */
    var reEmptyStringLeading = /\b__p \+= '';/g,
        reEmptyStringMiddle = /\b(__p \+=) '' \+/g,
        reEmptyStringTrailing = /(__e\(.*?\)|\b__t\)) \+\n'';/g;

    /**
     * Used to match ES6 template delimiters
     * http://people.mozilla.org/~jorendorff/es6-draft.html#sec-7.8.6
     */
    var reEsTemplate = /\$\{([^\\}]*(?:\\.[^\\}]*)*)\}/g;

    /** Used to match regexp flags from their coerced string values */
    var reFlags = /\w*$/;

    /** Used to detected named functions */
    var reFuncName = /^function[ \n\r\t]+\w/;

    /** Used to match "interpolate" template delimiters */
    var reInterpolate = /<%=([\s\S]+?)%>/g;

    /** Used to match leading whitespace and zeros to be removed */
    var reLeadingSpacesAndZeros = RegExp('^[' + whitespace + ']*0+(?=.$)');

    /** Used to ensure capturing order of template delimiters */
    var reNoMatch = /($^)/;

    /** Used to detect functions containing a `this` reference */
    var reThis = /\bthis\b/;

    /** Used to match unescaped characters in compiled string literals */
    var reUnescapedString = /['\n\r\t\u2028\u2029\\]/g;

    /** Used to assign default `context` object properties */
    var contextProps = [
        'Array', 'Boolean', 'Date', 'Function', 'Math', 'Number', 'Object',
        'RegExp', 'String', '_', 'attachEvent', 'clearTimeout', 'isFinite', 'isNaN',
        'parseInt', 'setImmediate', 'setTimeout'
    ];

    /** Used to make template sourceURLs easier to identify */
    var templateCounter = 0;

    /** `Object#toString` result shortcuts */
    var argsClass = '[object Arguments]',
        arrayClass = '[object Array]',
        boolClass = '[object Boolean]',
        dateClass = '[object Date]',
        funcClass = '[object Function]',
        numberClass = '[object Number]',
        objectClass = '[object Object]',
        regexpClass = '[object RegExp]',
        stringClass = '[object String]';

    /** Used to identify object classifications that `_.clone` supports */
    var cloneableClasses = {};
    cloneableClasses[funcClass] = false;
    cloneableClasses[argsClass] = cloneableClasses[arrayClass] =
        cloneableClasses[boolClass] = cloneableClasses[dateClass] =
            cloneableClasses[numberClass] = cloneableClasses[objectClass] =
                cloneableClasses[regexpClass] = cloneableClasses[stringClass] = true;

    /** Used as an internal `_.debounce` options object */
    var debounceOptions = {
        'leading': false,
        'maxWait': 0,
        'trailing': false
    };

    /** Used as the property descriptor for `__bindData__` */
    var descriptor = {
        'configurable': false,
        'enumerable': false,
        'value': null,
        'writable': false
    };

    /** Used to determine if values are of the language type Object */
    var objectTypes = {
        'boolean': false,
        'function': true,
        'object': true,
        'number': false,
        'string': false,
        'undefined': false
    };

    /** Used to escape characters for inclusion in compiled string literals */
    var stringEscapes = {
        '\\': '\\',
        "'": "'",
        '\n': 'n',
        '\r': 'r',
        '\t': 't',
        '\u2028': 'u2028',
        '\u2029': 'u2029'
    };

    /** Used as a reference to the global object */
    var root = (objectTypes[typeof window] && window) || this;

    /** Detect free variable `exports` */
    var freeExports = objectTypes[typeof exports] && exports && !exports.nodeType && exports;

    /** Detect free variable `module` */
    var freeModule = objectTypes[typeof module] && module && !module.nodeType && module;

    /** Detect the popular CommonJS extension `module.exports` */
    var moduleExports = freeModule && freeModule.exports === freeExports && freeExports;

    /** Detect free variable `global` from Node.js or Browserified code and use it as `root` */
    var freeGlobal = objectTypes[typeof global] && global;
    if (freeGlobal && (freeGlobal.global === freeGlobal || freeGlobal.window === freeGlobal)) {
        root = freeGlobal;
    }

    /*--------------------------------------------------------------------------*/

    /**
     * The base implementation of `_.indexOf` without support for binary searches
     * or `fromIndex` constraints.
     *
     * @private
     * @param {Array} array The array to search.
     * @param {*} value The value to search for.
     * @param {number} [fromIndex=0] The index to search from.
     * @returns {number} Returns the index of the matched value or `-1`.
     */
    function baseIndexOf(array, value, fromIndex) {
        var index = (fromIndex || 0) - 1,
            length = array ? array.length : 0;

        while (++index < length) {
            if (array[index] === value) {
                return index;
            }
        }
        return -1;
    }

    /**
     * An implementation of `_.contains` for cache objects that mimics the return
     * signature of `_.indexOf` by returning `0` if the value is found, else `-1`.
     *
     * @private
     * @param {Object} cache The cache object to inspect.
     * @param {*} value The value to search for.
     * @returns {number} Returns `0` if `value` is found, else `-1`.
     */
    function cacheIndexOf(cache, value) {
        var type = typeof value;
        cache = cache.cache;

        if (type == 'boolean' || value == null) {
            return cache[value] ? 0 : -1;
        }
        if (type != 'number' && type != 'string') {
            type = 'object';
        }
        var key = type == 'number' ? value : keyPrefix + value;
        cache = (cache = cache[type]) && cache[key];

        return type == 'object'
            ? (cache && baseIndexOf(cache, value) > -1 ? 0 : -1)
            : (cache ? 0 : -1);
    }

    /**
     * Adds a given value to the corresponding cache object.
     *
     * @private
     * @param {*} value The value to add to the cache.
     */
    function cachePush(value) {
        var cache = this.cache,
            type = typeof value;

        if (type == 'boolean' || value == null) {
            cache[value] = true;
        } else {
            if (type != 'number' && type != 'string') {
                type = 'object';
            }
            var key = type == 'number' ? value : keyPrefix + value,
                typeCache = cache[type] || (cache[type] = {});

            if (type == 'object') {
                (typeCache[key] || (typeCache[key] = [])).push(value);
            } else {
                typeCache[key] = true;
            }
        }
    }

    /**
     * Used by `_.max` and `_.min` as the default callback when a given
     * collection is a string value.
     *
     * @private
     * @param {string} value The character to inspect.
     * @returns {number} Returns the code unit of given character.
     */
    function charAtCallback(value) {
        return value.charCodeAt(0);
    }

    /**
     * Used by `sortBy` to compare transformed `collection` elements, stable sorting
     * them in ascending order.
     *
     * @private
     * @param {Object} a The object to compare to `b`.
     * @param {Object} b The object to compare to `a`.
     * @returns {number} Returns the sort order indicator of `1` or `-1`.
     */
    function compareAscending(a, b) {
        var ac = a.criteria,
            bc = b.criteria;

        // ensure a stable sort in V8 and other engines
        // http://code.google.com/p/v8/issues/detail?id=90
        if (ac !== bc) {
            if (ac > bc || typeof ac == 'undefined') {
                return 1;
            }
            if (ac < bc || typeof bc == 'undefined') {
                return -1;
            }
        }
        // The JS engine embedded in Adobe applications like InDesign has a buggy
        // `Array#sort` implementation that causes it, under certain circumstances,
        // to return the same value for `a` and `b`.
        // See https://github.com/jashkenas/underscore/pull/1247
        return a.index - b.index;
    }

    /**
     * Creates a cache object to optimize linear searches of large arrays.
     *
     * @private
     * @param {Array} [array=[]] The array to search.
     * @returns {null|Object} Returns the cache object or `null` if caching should not be used.
     */
    function createCache(array) {
        var index = -1,
            length = array.length,
            first = array[0],
            mid = array[(length / 2) | 0],
            last = array[length - 1];

        if (first && typeof first == 'object' &&
            mid && typeof mid == 'object' && last && typeof last == 'object') {
            return false;
        }
        var cache = getObject();
        cache['false'] = cache['null'] = cache['true'] = cache['undefined'] = false;

        var result = getObject();
        result.array = array;
        result.cache = cache;
        result.push = cachePush;

        while (++index < length) {
            result.push(array[index]);
        }
        return result;
    }

    /**
     * Used by `template` to escape characters for inclusion in compiled
     * string literals.
     *
     * @private
     * @param {string} match The matched character to escape.
     * @returns {string} Returns the escaped character.
     */
    function escapeStringChar(match) {
        return '\\' + stringEscapes[match];
    }

    /**
     * Gets an array from the array pool or creates a new one if the pool is empty.
     *
     * @private
     * @returns {Array} The array from the pool.
     */
    function getArray() {
        return arrayPool.pop() || [];
    }

    /**
     * Gets an object from the object pool or creates a new one if the pool is empty.
     *
     * @private
     * @returns {Object} The object from the pool.
     */
    function getObject() {
        return objectPool.pop() || {
                'array': null,
                'cache': null,
                'criteria': null,
                'false': false,
                'index': 0,
                'null': false,
                'number': null,
                'object': null,
                'push': null,
                'string': null,
                'true': false,
                'undefined': false,
                'value': null
            };
    }

    /**
     * A no-operation function.
     *
     * @private
     */
    function noop() {
        // no operation performed
    }

    /**
     * Releases the given array back to the array pool.
     *
     * @private
     * @param {Array} [array] The array to release.
     */
    function releaseArray(array) {
        array.length = 0;
        if (arrayPool.length < maxPoolSize) {
            arrayPool.push(array);
        }
    }

    /**
     * Releases the given object back to the object pool.
     *
     * @private
     * @param {Object} [object] The object to release.
     */
    function releaseObject(object) {
        var cache = object.cache;
        if (cache) {
            releaseObject(cache);
        }
        object.array = object.cache = object.criteria = object.object = object.number = object.string = object.value = null;
        if (objectPool.length < maxPoolSize) {
            objectPool.push(object);
        }
    }

    /**
     * Slices the `collection` from the `start` index up to, but not including,
     * the `end` index.
     *
     * Note: This function is used instead of `Array#slice` to support node lists
     * in IE < 9 and to ensure dense arrays are returned.
     *
     * @private
     * @param {Array|Object|string} collection The collection to slice.
     * @param {number} start The start index.
     * @param {number} end The end index.
     * @returns {Array} Returns the new array.
     */
    function slice(array, start, end) {
        start || (start = 0);
        if (typeof end == 'undefined') {
            end = array ? array.length : 0;
        }
        var index = -1,
            length = end - start || 0,
            result = Array(length < 0 ? 0 : length);

        while (++index < length) {
            result[index] = array[start + index];
        }
        return result;
    }

    /*--------------------------------------------------------------------------*/

    /**
     * Create a new `lodash` function using the given context object.
     *
     * @static
     * @memberOf _
     * @category Utilities
     * @param {Object} [context=root] The context object.
     * @returns {Function} Returns the `lodash` function.
     */
    function runInContext(context) {
        // Avoid issues with some ES3 environments that attempt to use values, named
        // after built-in constructors like `Object`, for the creation of literals.
        // ES5 clears this up by stating that literals must use built-in constructors.
        // See http://es5.github.io/#x11.1.5.
        context = context ? _.defaults(root.Object(), context, _.pick(root, contextProps)) : root;

        /** Native constructor references */
        var Array = context.Array,
            Boolean = context.Boolean,
            Date = context.Date,
            Function = context.Function,
            Math = context.Math,
            Number = context.Number,
            Object = context.Object,
            RegExp = context.RegExp,
            String = context.String,
            TypeError = context.TypeError;

        /**
         * Used for `Array` method references.
         *
         * Normally `Array.prototype` would suffice, however, using an array literal
         * avoids issues in Narwhal.
         */
        var arrayRef = [];

        /** Used for native method references */
        var objectProto = Object.prototype;

        /** Used to restore the original `_` reference in `noConflict` */
        var oldDash = context._;

        /** Used to detect if a method is native */
        var reNative = RegExp('^' +
            String(objectProto.valueOf)
                .replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
                .replace(/valueOf|for [^\]]+/g, '.+?') + '$'
        );

        /** Native method shortcuts */
        var ceil = Math.ceil,
            clearTimeout = context.clearTimeout,
            floor = Math.floor,
            fnToString = Function.prototype.toString,
            getPrototypeOf = reNative.test(getPrototypeOf = Object.getPrototypeOf) && getPrototypeOf,
            hasOwnProperty = objectProto.hasOwnProperty,
            now = reNative.test(now = Date.now) && now || function () { return +new Date; },
            push = arrayRef.push,
            setImmediate = context.setImmediate,
            setTimeout = context.setTimeout,
            splice = arrayRef.splice,
            toString = objectProto.toString,
            unshift = arrayRef.unshift;

        var defineProperty = (function () {
            try {
                var o = {},
                    func = reNative.test(func = Object.defineProperty) && func,
                    result = func(o, o, o) && func;
            } catch (e) {
            }
            return result;
        }());

        /* Native method shortcuts for methods with the same name as other `lodash` methods */
        var nativeBind = reNative.test(nativeBind = toString.bind) && nativeBind,
            nativeCreate = reNative.test(nativeCreate = Object.create) && nativeCreate,
            nativeIsArray = reNative.test(nativeIsArray = Array.isArray) && nativeIsArray,
            nativeIsFinite = context.isFinite,
            nativeIsNaN = context.isNaN,
            nativeKeys = reNative.test(nativeKeys = Object.keys) && nativeKeys,
            nativeMax = Math.max,
            nativeMin = Math.min,
            nativeParseInt = context.parseInt,
            nativeRandom = Math.random,
            nativeSlice = arrayRef.slice;

        /** Detect various environments */
        var isIeOpera = reNative.test(context.attachEvent),
            isV8 = nativeBind && !/\n|true/.test(nativeBind + isIeOpera);

        /** Used to lookup a built-in constructor by [[Class]] */
        var ctorByClass = {};
        ctorByClass[arrayClass] = Array;
        ctorByClass[boolClass] = Boolean;
        ctorByClass[dateClass] = Date;
        ctorByClass[funcClass] = Function;
        ctorByClass[objectClass] = Object;
        ctorByClass[numberClass] = Number;
        ctorByClass[regexpClass] = RegExp;
        ctorByClass[stringClass] = String;

        /*--------------------------------------------------------------------------*/

        /**
         * Creates a `lodash` object which wraps the given value to enable intuitive
         * method chaining.
         *
         * In addition to Lo-Dash methods, wrappers also have the following `Array` methods:
         * `concat`, `join`, `pop`, `push`, `reverse`, `shift`, `slice`, `sort`, `splice`,
         * and `unshift`
         *
         * Chaining is supported in custom builds as long as the `value` method is
         * implicitly or explicitly included in the build.
         *
         * The chainable wrapper functions are:
         * `after`, `assign`, `bind`, `bindAll`, `bindKey`, `chain`, `compact`,
         * `compose`, `concat`, `countBy`, `createCallback`, `curry`, `debounce`,
         * `defaults`, `defer`, `delay`, `difference`, `filter`, `flatten`, `forEach`,
         * `forEachRight`, `forIn`, `forInRight`, `forOwn`, `forOwnRight`, `functions`,
         * `groupBy`, `indexBy`, `initial`, `intersection`, `invert`, `invoke`, `keys`,
         * `map`, `max`, `memoize`, `merge`, `min`, `object`, `omit`, `once`, `pairs`,
         * `partial`, `partialRight`, `pick`, `pluck`, `pull`, `push`, `range`, `reject`,
         * `remove`, `rest`, `reverse`, `shuffle`, `slice`, `sort`, `sortBy`, `splice`,
         * `tap`, `throttle`, `times`, `toArray`, `transform`, `union`, `uniq`, `unshift`,
         * `unzip`, `values`, `where`, `without`, `wrap`, and `zip`
         *
         * The non-chainable wrapper functions are:
         * `clone`, `cloneDeep`, `contains`, `escape`, `every`, `find`, `findIndex`,
         * `findKey`, `findLast`, `findLastIndex`, `findLastKey`, `has`, `identity`,
         * `indexOf`, `isArguments`, `isArray`, `isBoolean`, `isDate`, `isElement`,
         * `isEmpty`, `isEqual`, `isFinite`, `isFunction`, `isNaN`, `isNull`, `isNumber`,
         * `isObject`, `isPlainObject`, `isRegExp`, `isString`, `isUndefined`, `join`,
         * `lastIndexOf`, `mixin`, `noConflict`, `parseInt`, `pop`, `random`, `reduce`,
         * `reduceRight`, `result`, `shift`, `size`, `some`, `sortedIndex`, `runInContext`,
         * `template`, `unescape`, `uniqueId`, and `value`
         *
         * The wrapper functions `first` and `last` return wrapped values when `n` is
         * provided, otherwise they return unwrapped values.
         *
         * Explicit chaining can be enabled by using the `_.chain` method.
         *
         * @name _
         * @constructor
         * @category Chaining
         * @param {*} value The value to wrap in a `lodash` instance.
         * @returns {Object} Returns a `lodash` instance.
         * @example
         *
         * var wrapped = _([1, 2, 3]);
         *
         * // returns an unwrapped value
         * wrapped.reduce(function(sum, num) {
     *   return sum + num;
     * });
         * // => 6
         *
         * // returns a wrapped value
         * var squares = wrapped.map(function(num) {
     *   return num * num;
     * });
         *
         * _.isArray(squares);
         * // => false
         *
         * _.isArray(squares.value());
         * // => true
         */
        function lodash(value) {
            // don't wrap if already wrapped, even if wrapped by a different `lodash` constructor
            return (value && typeof value == 'object' && !isArray(value) && hasOwnProperty.call(value, '__wrapped__'))
                ? value
                : new lodashWrapper(value);
        }

        /**
         * A fast path for creating `lodash` wrapper objects.
         *
         * @private
         * @param {*} value The value to wrap in a `lodash` instance.
         * @param {boolean} chainAll A flag to enable chaining for all methods
         * @returns {Object} Returns a `lodash` instance.
         */
        function lodashWrapper(value, chainAll) {
            this.__chain__ = !!chainAll;
            this.__wrapped__ = value;
        }

        // ensure `new lodashWrapper` is an instance of `lodash`
        lodashWrapper.prototype = lodash.prototype;

        /**
         * An object used to flag environments features.
         *
         * @static
         * @memberOf _
         * @type Object
         */
        var support = lodash.support = {};

        /**
         * Detect if `Function#bind` exists and is inferred to be fast (all but V8).
         *
         * @memberOf _.support
         * @type boolean
         */
        support.fastBind = nativeBind && !isV8;

        /**
         * Detect if functions can be decompiled by `Function#toString`
         * (all but PS3 and older Opera mobile browsers & avoided in Windows 8 apps).
         *
         * @memberOf _.support
         * @type boolean
         */
        support.funcDecomp = !reNative.test(context.WinRTError) && reThis.test(runInContext);

        /**
         * Detect if `Function#name` is supported (all but IE).
         *
         * @memberOf _.support
         * @type boolean
         */
        support.funcNames = typeof Function.name == 'string';

        /**
         * By default, the template delimiters used by Lo-Dash are similar to those in
         * embedded Ruby (ERB). Change the following template settings to use alternative
         * delimiters.
         *
         * @static
         * @memberOf _
         * @type Object
         */
        lodash.templateSettings = {

            /**
             * Used to detect `data` property values to be HTML-escaped.
             *
             * @memberOf _.templateSettings
             * @type RegExp
             */
            'escape': /<%-([\s\S]+?)%>/g,

            /**
             * Used to detect code to be evaluated.
             *
             * @memberOf _.templateSettings
             * @type RegExp
             */
            'evaluate': /<%([\s\S]+?)%>/g,

            /**
             * Used to detect `data` property values to inject.
             *
             * @memberOf _.templateSettings
             * @type RegExp
             */
            'interpolate': reInterpolate,

            /**
             * Used to reference the data object in the template text.
             *
             * @memberOf _.templateSettings
             * @type string
             */
            'variable': '',

            /**
             * Used to import variables into the compiled template.
             *
             * @memberOf _.templateSettings
             * @type Object
             */
            'imports': {

                /**
                 * A reference to the `lodash` function.
                 *
                 * @memberOf _.templateSettings.imports
                 * @type Function
                 */
                '_': lodash
            }
        };

        /*--------------------------------------------------------------------------*/

        /**
         * The base implementation of `_.clone` without argument juggling or support
         * for `thisArg` binding.
         *
         * @private
         * @param {*} value The value to clone.
         * @param {boolean} [deep=false] Specify a deep clone.
         * @param {Function} [callback] The function to customize cloning values.
         * @param {Array} [stackA=[]] Tracks traversed source objects.
         * @param {Array} [stackB=[]] Associates clones with source counterparts.
         * @returns {*} Returns the cloned value.
         */
        function baseClone(value, deep, callback, stackA, stackB) {
            if (callback) {
                var result = callback(value);
                if (typeof result != 'undefined') {
                    return result;
                }
            }
            // inspect [[Class]]
            var isObj = isObject(value);
            if (isObj) {
                var className = toString.call(value);
                if (!cloneableClasses[className]) {
                    return value;
                }
                var ctor = ctorByClass[className];
                switch (className) {
                    case boolClass:
                    case dateClass:
                        return new ctor(+value);

                    case numberClass:
                    case stringClass:
                        return new ctor(value);

                    case regexpClass:
                        result = ctor(value.source, reFlags.exec(value));
                        result.lastIndex = value.lastIndex;
                        return result;
                }
            } else {
                return value;
            }
            var isArr = isArray(value);
            if (deep) {
                // check for circular references and return corresponding clone
                var initedStack = !stackA;
                stackA || (stackA = getArray());
                stackB || (stackB = getArray());

                var length = stackA.length;
                while (length--) {
                    if (stackA[length] == value) {
                        return stackB[length];
                    }
                }
                result = isArr ? ctor(value.length) : {};
            }
            else {
                result = isArr ? slice(value) : assign({}, value);
            }
            // add array properties assigned by `RegExp#exec`
            if (isArr) {
                if (hasOwnProperty.call(value, 'index')) {
                    result.index = value.index;
                }
                if (hasOwnProperty.call(value, 'input')) {
                    result.input = value.input;
                }
            }
            // exit for shallow clone
            if (!deep) {
                return result;
            }
            // add the source value to the stack of traversed objects
            // and associate it with its clone
            stackA.push(value);
            stackB.push(result);

            // recursively populate clone (susceptible to call stack limits)
            (isArr ? forEach : forOwn)(value, function (objValue, key) {
                result[key] = baseClone(objValue, deep, callback, stackA, stackB);
            });

            if (initedStack) {
                releaseArray(stackA);
                releaseArray(stackB);
            }
            return result;
        }

        /**
         * The base implementation of `_.createCallback` without support for creating
         * "_.pluck" or "_.where" style callbacks.
         *
         * @private
         * @param {*} [func=identity] The value to convert to a callback.
         * @param {*} [thisArg] The `this` binding of the created callback.
         * @param {number} [argCount] The number of arguments the callback accepts.
         * @returns {Function} Returns a callback function.
         */
        function baseCreateCallback(func, thisArg, argCount) {
            if (typeof func != 'function') {
                return identity;
            }
            // exit early if there is no `thisArg`
            if (typeof thisArg == 'undefined') {
                return func;
            }
            var bindData = func.__bindData__ || (support.funcNames && !func.name);
            if (typeof bindData == 'undefined') {
                var source = reThis && fnToString.call(func);
                if (!support.funcNames && source && !reFuncName.test(source)) {
                    bindData = true;
                }
                if (support.funcNames || !bindData) {
                    // checks if `func` references the `this` keyword and stores the result
                    bindData = !support.funcDecomp || reThis.test(source);
                    setBindData(func, bindData);
                }
            }
            // exit early if there are no `this` references or `func` is bound
            if (bindData !== true && (bindData && bindData[1] & 1)) {
                return func;
            }
            switch (argCount) {
                case 1:
                    return function (value) {
                        return func.call(thisArg, value);
                    };
                case 2:
                    return function (a, b) {
                        return func.call(thisArg, a, b);
                    };
                case 3:
                    return function (value, index, collection) {
                        return func.call(thisArg, value, index, collection);
                    };
                case 4:
                    return function (accumulator, value, index, collection) {
                        return func.call(thisArg, accumulator, value, index, collection);
                    };
            }
            return bind(func, thisArg);
        }

        /**
         * The base implementation of `_.flatten` without support for callback
         * shorthands or `thisArg` binding.
         *
         * @private
         * @param {Array} array The array to flatten.
         * @param {boolean} [isShallow=false] A flag to restrict flattening to a single level.
         * @param {boolean} [isArgArrays=false] A flag to restrict flattening to arrays and `arguments` objects.
         * @param {number} [fromIndex=0] The index to start from.
         * @returns {Array} Returns a new flattened array.
         */
        function baseFlatten(array, isShallow, isArgArrays, fromIndex) {
            var index = (fromIndex || 0) - 1,
                length = array ? array.length : 0,
                result = [];

            while (++index < length) {
                var value = array[index];

                if (value && typeof value == 'object' && typeof value.length == 'number'
                    && (isArray(value) || isArguments(value))) {
                    // recursively flatten arrays (susceptible to call stack limits)
                    if (!isShallow) {
                        value = baseFlatten(value, isShallow, isArgArrays);
                    }
                    var valIndex = -1,
                        valLength = value.length,
                        resIndex = result.length;

                    result.length += valLength;
                    while (++valIndex < valLength) {
                        result[resIndex++] = value[valIndex];
                    }
                } else if (!isArgArrays) {
                    result.push(value);
                }
            }
            return result;
        }

        /**
         * The base implementation of `_.isEqual`, without support for `thisArg` binding,
         * that allows partial "_.where" style comparisons.
         *
         * @private
         * @param {*} a The value to compare.
         * @param {*} b The other value to compare.
         * @param {Function} [callback] The function to customize comparing values.
         * @param {Function} [isWhere=false] A flag to indicate performing partial comparisons.
         * @param {Array} [stackA=[]] Tracks traversed `a` objects.
         * @param {Array} [stackB=[]] Tracks traversed `b` objects.
         * @returns {boolean} Returns `true` if the values are equivalent, else `false`.
         */
        function baseIsEqual(a, b, callback, isWhere, stackA, stackB) {
            // used to indicate that when comparing objects, `a` has at least the properties of `b`
            if (callback) {
                var result = callback(a, b);
                if (typeof result != 'undefined') {
                    return !!result;
                }
            }
            // exit early for identical values
            if (a === b) {
                // treat `+0` vs. `-0` as not equal
                return a !== 0 || (1 / a == 1 / b);
            }
            var type = typeof a,
                otherType = typeof b;

            // exit early for unlike primitive values
            if (a === a && !(a && objectTypes[type]) && !(b && objectTypes[otherType])) {
                return false;
            }
            // exit early for `null` and `undefined` avoiding ES3's Function#call behavior
            // http://es5.github.io/#x15.3.4.4
            if (a == null || b == null) {
                return a === b;
            }
            // compare [[Class]] names
            var className = toString.call(a),
                otherClass = toString.call(b);

            if (className == argsClass) {
                className = objectClass;
            }
            if (otherClass == argsClass) {
                otherClass = objectClass;
            }
            if (className != otherClass) {
                return false;
            }
            switch (className) {
                case boolClass:
                case dateClass:
                    // coerce dates and booleans to numbers, dates to milliseconds and booleans
                    // to `1` or `0` treating invalid dates coerced to `NaN` as not equal
                    return +a == +b;

                case numberClass:
                    // treat `NaN` vs. `NaN` as equal
                    return (a != +a)
                        ? b != +b
                        // but treat `+0` vs. `-0` as not equal
                        : (a == 0 ? (1 / a == 1 / b) : a == +b);

                case regexpClass:
                case stringClass:
                    // coerce regexes to strings (http://es5.github.io/#x15.10.6.4)
                    // treat string primitives and their corresponding object instances as equal
                    return a == String(b);
            }
            var isArr = className == arrayClass;
            if (!isArr) {
                // unwrap any `lodash` wrapped values
                if (hasOwnProperty.call(a, '__wrapped__ ') || hasOwnProperty.call(b, '__wrapped__')) {
                    return baseIsEqual(a.__wrapped__ || a, b.__wrapped__ || b, callback, isWhere, stackA, stackB);
                }
                // exit for functions and DOM nodes
                if (className != objectClass) {
                    return false;
                }
                // in older versions of Opera, `arguments` objects have `Array` constructors
                var ctorA = a.constructor,
                    ctorB = b.constructor;

                // non `Object` object instances with different constructors are not equal
                if (ctorA != ctorB && !(
                    isFunction(ctorA) && ctorA instanceof ctorA &&
                    isFunction(ctorB) && ctorB instanceof ctorB
                    )) {
                    return false;
                }
            }
            // assume cyclic structures are equal
            // the algorithm for detecting cyclic structures is adapted from ES 5.1
            // section 15.12.3, abstract operation `JO` (http://es5.github.io/#x15.12.3)
            var initedStack = !stackA;
            stackA || (stackA = getArray());
            stackB || (stackB = getArray());

            var length = stackA.length;
            while (length--) {
                if (stackA[length] == a) {
                    return stackB[length] == b;
                }
            }
            var size = 0;
            result = true;

            // add `a` and `b` to the stack of traversed objects
            stackA.push(a);
            stackB.push(b);

            // recursively compare objects and arrays (susceptible to call stack limits)
            if (isArr) {
                length = a.length;
                size = b.length;

                // compare lengths to determine if a deep comparison is necessary
                result = size == a.length;
                if (!result && !isWhere) {
                    return result;
                }
                // deep compare the contents, ignoring non-numeric properties
                while (size--) {
                    var index = length,
                        value = b[size];

                    if (isWhere) {
                        while (index--) {
                            if ((result = baseIsEqual(a[index], value, callback, isWhere, stackA, stackB))) {
                                break;
                            }
                        }
                    } else if (!(result = baseIsEqual(a[size], value, callback, isWhere, stackA, stackB))) {
                        break;
                    }
                }
                return result;
            }
            // deep compare objects using `forIn`, instead of `forOwn`, to avoid `Object.keys`
            // which, in this case, is more costly
            forIn(b, function (value, key, b) {
                if (hasOwnProperty.call(b, key)) {
                    // count the number of properties.
                    size++;
                    // deep compare each property value.
                    return (result = hasOwnProperty.call(a, key) && baseIsEqual(a[key], value, callback, isWhere, stackA, stackB));
                }
            });

            if (result && !isWhere) {
                // ensure both objects have the same number of properties
                forIn(a, function (value, key, a) {
                    if (hasOwnProperty.call(a, key)) {
                        // `size` will be `-1` if `a` has more properties than `b`
                        return (result = --size > -1);
                    }
                });
            }
            if (initedStack) {
                releaseArray(stackA);
                releaseArray(stackB);
            }
            return result;
        }

        /**
         * The base implementation of `_.merge` without argument juggling or support
         * for `thisArg` binding.
         *
         * @private
         * @param {Object} object The destination object.
         * @param {Object} source The source object.
         * @param {Function} [callback] The function to customize merging properties.
         * @param {Array} [stackA=[]] Tracks traversed source objects.
         * @param {Array} [stackB=[]] Associates values with source counterparts.
         */
        function baseMerge(object, source, callback, stackA, stackB) {
            (isArray(source) ? forEach : forOwn)(source, function (source, key) {
                var found,
                    isArr,
                    result = source,
                    value = object[key];

                if (source && ((isArr = isArray(source)) || isPlainObject(source))) {
                    // avoid merging previously merged cyclic sources
                    var stackLength = stackA.length;
                    while (stackLength--) {
                        if ((found = stackA[stackLength] == source)) {
                            value = stackB[stackLength];
                            break;
                        }
                    }
                    if (!found) {
                        var isShallow;
                        if (callback) {
                            result = callback(value, source);
                            if ((isShallow = typeof result != 'undefined')) {
                                value = result;
                            }
                        }
                        if (!isShallow) {
                            value = isArr
                                ? (isArray(value) ? value : [])
                                : (isPlainObject(value) ? value : {});
                        }
                        // add `source` and associated `value` to the stack of traversed objects
                        stackA.push(source);
                        stackB.push(value);

                        // recursively merge objects and arrays (susceptible to call stack limits)
                        if (!isShallow) {
                            baseMerge(value, source, callback, stackA, stackB);
                        }
                    }
                }
                else {
                    if (callback) {
                        result = callback(value, source);
                        if (typeof result == 'undefined') {
                            result = source;
                        }
                    }
                    if (typeof result != 'undefined') {
                        value = result;
                    }
                }
                object[key] = value;
            });
        }

        /**
         * The base implementation of `_.uniq` without support for callback shorthands
         * or `thisArg` binding.
         *
         * @private
         * @param {Array} array The array to process.
         * @param {boolean} [isSorted=false] A flag to indicate that `array` is sorted.
         * @param {Function} [callback] The function called per iteration.
         * @returns {Array} Returns a duplicate-value-free array.
         */
        function baseUniq(array, isSorted, callback) {
            var index = -1,
                indexOf = getIndexOf(),
                length = array ? array.length : 0,
                result = [];

            var isLarge = !isSorted && length >= largeArraySize && indexOf === baseIndexOf,
                seen = (callback || isLarge) ? getArray() : result;

            if (isLarge) {
                var cache = createCache(seen);
                if (cache) {
                    indexOf = cacheIndexOf;
                    seen = cache;
                } else {
                    isLarge = false;
                    seen = callback ? seen : (releaseArray(seen), result);
                }
            }
            while (++index < length) {
                var value = array[index],
                    computed = callback ? callback(value, index, array) : value;

                if (isSorted
                        ? !index || seen[seen.length - 1] !== computed
                        : indexOf(seen, computed) < 0
                ) {
                    if (callback || isLarge) {
                        seen.push(computed);
                    }
                    result.push(value);
                }
            }
            if (isLarge) {
                releaseArray(seen.array);
                releaseObject(seen);
            } else if (callback) {
                releaseArray(seen);
            }
            return result;
        }

        /**
         * Creates a function that aggregates a collection, creating an object composed
         * of keys generated from the results of running each element of the collection
         * through a callback. The given `setter` function sets the keys and values
         * of the composed object.
         *
         * @private
         * @param {Function} setter The setter function.
         * @returns {Function} Returns the new aggregator function.
         */
        function createAggregator(setter) {
            return function (collection, callback, thisArg) {
                var result = {};
                callback = lodash.createCallback(callback, thisArg, 3);

                var index = -1,
                    length = collection ? collection.length : 0;

                if (typeof length == 'number') {
                    while (++index < length) {
                        var value = collection[index];
                        setter(result, value, callback(value, index, collection), collection);
                    }
                } else {
                    forOwn(collection, function (value, key, collection) {
                        setter(result, value, callback(value, key, collection), collection);
                    });
                }
                return result;
            };
        }

        /**
         * Creates a function that, when called, either curries or invokes `func`
         * with an optional `this` binding and partially applied arguments.
         *
         * @private
         * @param {Function|string} func The function or method name to reference.
         * @param {number} bitmask The bitmask of method flags to compose.
         *  The bitmask may be composed of the following flags:
         *  1 - `_.bind`
         *  2 - `_.bindKey`
         *  4 - `_.curry`
         *  8 - `_.curry` (bound)
         *  16 - `_.partial`
         *  32 - `_.partialRight`
         * @param {Array} [partialArgs] An array of arguments to prepend to those
         *  provided to the new function.
         * @param {Array} [partialRightArgs] An array of arguments to append to those
         *  provided to the new function.
         * @param {*} [thisArg] The `this` binding of `func`.
         * @param {number} [arity] The arity of `func`.
         * @returns {Function} Returns the new bound function.
         */
        function createBound(func, bitmask, partialArgs, partialRightArgs, thisArg, arity) {
            var isBind = bitmask & 1,
                isBindKey = bitmask & 2,
                isCurry = bitmask & 4,
                isCurryBound = bitmask & 8,
                isPartial = bitmask & 16,
                isPartialRight = bitmask & 32,
                key = func;

            if (!isBindKey && !isFunction(func)) {
                throw new TypeError;
            }
            if (isPartial && !partialArgs.length) {
                bitmask &= ~16;
                isPartial = partialArgs = false;
            }
            if (isPartialRight && !partialRightArgs.length) {
                bitmask &= ~32;
                isPartialRight = partialRightArgs = false;
            }
            var bindData = func && func.__bindData__;
            if (bindData) {
                if (isBind && !(bindData[1] & 1)) {
                    bindData[4] = thisArg;
                }
                if (!isBind && bindData[1] & 1) {
                    bitmask |= 8;
                }
                if (isCurry && !(bindData[1] & 4)) {
                    bindData[5] = arity;
                }
                if (isPartial) {
                    push.apply(bindData[2] || (bindData[2] = []), partialArgs);
                }
                if (isPartialRight) {
                    push.apply(bindData[3] || (bindData[3] = []), partialRightArgs);
                }
                bindData[1] |= bitmask;
                return createBound.apply(null, bindData);
            }
            // use `Function#bind` if it exists and is fast
            // (in V8 `Function#bind` is slower except when partially applied)
            if (isBind && !(isBindKey || isCurry || isPartialRight) &&
                (support.fastBind || (nativeBind && isPartial))) {
                if (isPartial) {
                    var args = [thisArg];
                    push.apply(args, partialArgs);
                }
                var bound = isPartial
                    ? nativeBind.apply(func, args)
                    : nativeBind.call(func, thisArg);
            }
            else {
                bound = function () {
                    // `Function#bind` spec
                    // http://es5.github.io/#x15.3.4.5
                    var args = arguments,
                        thisBinding = isBind ? thisArg : this;

                    if (isCurry || isPartial || isPartialRight) {
                        args = nativeSlice.call(args);
                        if (isPartial) {
                            unshift.apply(args, partialArgs);
                        }
                        if (isPartialRight) {
                            push.apply(args, partialRightArgs);
                        }
                        if (isCurry && args.length < arity) {
                            bitmask |= 16 & ~32;
                            return createBound(func, (isCurryBound ? bitmask : bitmask & ~3), args, null, thisArg, arity);
                        }
                    }
                    if (isBindKey) {
                        func = thisBinding[key];
                    }
                    if (this instanceof bound) {
                        // ensure `new bound` is an instance of `func`
                        thisBinding = createObject(func.prototype);

                        // mimic the constructor's `return` behavior
                        // http://es5.github.io/#x13.2.2
                        var result = func.apply(thisBinding, args);
                        return isObject(result) ? result : thisBinding;
                    }
                    return func.apply(thisBinding, args);
                };
            }
            setBindData(bound, nativeSlice.call(arguments));
            return bound;
        }

        /**
         * Creates a new object with the specified `prototype`.
         *
         * @private
         * @param {Object} prototype The prototype object.
         * @returns {Object} Returns the new object.
         */
        function createObject(prototype) {
            return isObject(prototype) ? nativeCreate(prototype) : {};
        }

        // fallback for browsers without `Object.create`
        if (!nativeCreate) {
            createObject = function (prototype) {
                if (isObject(prototype)) {
                    noop.prototype = prototype;
                    var result = new noop;
                    noop.prototype = null;
                }
                return result || {};
            };
        }

        /**
         * Used by `escape` to convert characters to HTML entities.
         *
         * @private
         * @param {string} match The matched character to escape.
         * @returns {string} Returns the escaped character.
         */
        function escapeHtmlChar(match) {
            return htmlEscapes[match];
        }

        /**
         * Gets the appropriate "indexOf" function. If the `_.indexOf` method is
         * customized, this method returns the custom method, otherwise it returns
         * the `baseIndexOf` function.
         *
         * @private
         * @returns {Function} Returns the "indexOf" function.
         */
        function getIndexOf() {
            var result = (result = lodash.indexOf) === indexOf ? baseIndexOf : result;
            return result;
        }

        /**
         * Sets `this` binding data on a given function.
         *
         * @private
         * @param {Function} func The function to set data on.
         * @param {*} value The value to set.
         */
        var setBindData = !defineProperty ? noop : function (func, value) {
            descriptor.value = value;
            defineProperty(func, '__bindData__', descriptor);
        };

        /**
         * A fallback implementation of `isPlainObject` which checks if a given value
         * is an object created by the `Object` constructor, assuming objects created
         * by the `Object` constructor have no inherited enumerable properties and that
         * there are no `Object.prototype` extensions.
         *
         * @private
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if `value` is a plain object, else `false`.
         */
        function shimIsPlainObject(value) {
            var ctor,
                result;

            // avoid non Object objects, `arguments` objects, and DOM elements
            if (!(value && toString.call(value) == objectClass) ||
                (ctor = value.constructor, isFunction(ctor) && !(ctor instanceof ctor))) {
                return false;
            }
            // In most environments an object's own properties are iterated before
            // its inherited properties. If the last iterated property is an object's
            // own property then there are no inherited enumerable properties.
            forIn(value, function (value, key) {
                result = key;
            });
            return typeof result == 'undefined' || hasOwnProperty.call(value, result);
        }

        /**
         * Used by `unescape` to convert HTML entities to characters.
         *
         * @private
         * @param {string} match The matched character to unescape.
         * @returns {string} Returns the unescaped character.
         */
        function unescapeHtmlChar(match) {
            return htmlUnescapes[match];
        }

        /*--------------------------------------------------------------------------*/

        /**
         * Checks if `value` is an `arguments` object.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is an `arguments` object, else `false`.
         * @example
         *
         * (function() { return _.isArguments(arguments); })(1, 2, 3);
         * // => true
         *
         * _.isArguments([1, 2, 3]);
         * // => false
         */
        function isArguments(value) {
            return value && typeof value == 'object' && typeof value.length == 'number' &&
                toString.call(value) == argsClass || false;
        }

        /**
         * Checks if `value` is an array.
         *
         * @static
         * @memberOf _
         * @type Function
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is an array, else `false`.
         * @example
         *
         * (function() { return _.isArray(arguments); })();
         * // => false
         *
         * _.isArray([1, 2, 3]);
         * // => true
         */
        var isArray = nativeIsArray || function (value) {
                return value && typeof value == 'object' && typeof value.length == 'number' &&
                    toString.call(value) == arrayClass || false;
            };

        /**
         * A fallback implementation of `Object.keys` which produces an array of the
         * given object's own enumerable property names.
         *
         * @private
         * @type Function
         * @param {Object} object The object to inspect.
         * @returns {Array} Returns an array of property names.
         */
        var shimKeys = function (object) {
            var index, iterable = object, result = [];
            if (!iterable) return result;
            if (!(objectTypes[typeof object])) return result;
            for (index in iterable) {
                if (hasOwnProperty.call(iterable, index)) {
                    result.push(index);
                }
            }
            return result
        };

        /**
         * Creates an array composed of the own enumerable property names of an object.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to inspect.
         * @returns {Array} Returns an array of property names.
         * @example
         *
         * _.keys({ 'one': 1, 'two': 2, 'three': 3 });
         * // => ['one', 'two', 'three'] (property order is not guaranteed across environments)
         */
        var keys = !nativeKeys ? shimKeys : function (object) {
            if (!isObject(object)) {
                return [];
            }
            return nativeKeys(object);
        };

        /**
         * Used to convert characters to HTML entities:
         *
         * Though the `>` character is escaped for symmetry, characters like `>` and `/`
         * don't require escaping in HTML and have no special meaning unless they're part
         * of a tag or an unquoted attribute value.
         * http://mathiasbynens.be/notes/ambiguous-ampersands (under "semi-related fun fact")
         */
        var htmlEscapes = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        };

        /** Used to convert HTML entities to characters */
        var htmlUnescapes = invert(htmlEscapes);

        /** Used to match HTML entities and HTML characters */
        var reEscapedHtml = RegExp('(' + keys(htmlUnescapes).join('|') + ')', 'g'),
            reUnescapedHtml = RegExp('[' + keys(htmlEscapes).join('') + ']', 'g');

        /*--------------------------------------------------------------------------*/

        /**
         * Assigns own enumerable properties of source object(s) to the destination
         * object. Subsequent sources will overwrite property assignments of previous
         * sources. If a callback is provided it will be executed to produce the
         * assigned values. The callback is bound to `thisArg` and invoked with two
         * arguments; (objectValue, sourceValue).
         *
         * @static
         * @memberOf _
         * @type Function
         * @alias extend
         * @category Objects
         * @param {Object} object The destination object.
         * @param {...Object} [source] The source objects.
         * @param {Function} [callback] The function to customize assigning values.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns the destination object.
         * @example
         *
         * _.assign({ 'name': 'moe' }, { 'age': 40 });
         * // => { 'name': 'moe', 'age': 40 }
         *
         * var defaults = _.partialRight(_.assign, function(a, b) {
     *   return typeof a == 'undefined' ? b : a;
     * });
         *
         * var food = { 'name': 'apple' };
         * defaults(food, { 'name': 'banana', 'type': 'fruit' });
         * // => { 'name': 'apple', 'type': 'fruit' }
         */
        var assign = function (object, source, guard) {
            var index, iterable = object, result = iterable;
            if (!iterable) return result;
            var args = arguments,
                argsIndex = 0,
                argsLength = typeof guard == 'number' ? 2 : args.length;
            if (argsLength > 3 && typeof args[argsLength - 2] == 'function') {
                var callback = baseCreateCallback(args[--argsLength - 1], args[argsLength--], 2);
            } else if (argsLength > 2 && typeof args[argsLength - 1] == 'function') {
                callback = args[--argsLength];
            }
            while (++argsIndex < argsLength) {
                iterable = args[argsIndex];
                if (iterable && objectTypes[typeof iterable]) {
                    var ownIndex = -1,
                        ownProps = objectTypes[typeof iterable] && keys(iterable),
                        length = ownProps ? ownProps.length : 0;

                    while (++ownIndex < length) {
                        index = ownProps[ownIndex];
                        result[index] = callback ? callback(result[index], iterable[index]) : iterable[index];
                    }
                }
            }
            return result
        };

        /**
         * Creates a clone of `value`. If `deep` is `true` nested objects will also
         * be cloned, otherwise they will be assigned by reference. If a callback
         * is provided it will be executed to produce the cloned values. If the
         * callback returns `undefined` cloning will be handled by the method instead.
         * The callback is bound to `thisArg` and invoked with one argument; (value).
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to clone.
         * @param {boolean} [deep=false] Specify a deep clone.
         * @param {Function} [callback] The function to customize cloning values.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the cloned value.
         * @example
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * var shallow = _.clone(stooges);
         * shallow[0] === stooges[0];
         * // => true
         *
         * var deep = _.clone(stooges, true);
         * deep[0] === stooges[0];
         * // => false
         *
         * _.mixin({
     *   'clone': _.partialRight(_.clone, function(value) {
     *     return _.isElement(value) ? value.cloneNode(false) : undefined;
     *   })
     * });
         *
         * var clone = _.clone(document.body);
         * clone.childNodes.length;
         * // => 0
         */
        function clone(value, deep, callback, thisArg) {
            // allows working with "Collections" methods without using their `index`
            // and `collection` arguments for `deep` and `callback`
            if (typeof deep != 'boolean' && deep != null) {
                thisArg = callback;
                callback = deep;
                deep = false;
            }
            return baseClone(value, deep, typeof callback == 'function' && baseCreateCallback(callback, thisArg, 1));
        }

        /**
         * Creates a deep clone of `value`. If a callback is provided it will be
         * executed to produce the cloned values. If the callback returns `undefined`
         * cloning will be handled by the method instead. The callback is bound to
         * `thisArg` and invoked with one argument; (value).
         *
         * Note: This method is loosely based on the structured clone algorithm. Functions
         * and DOM nodes are **not** cloned. The enumerable properties of `arguments` objects and
         * objects created by constructors other than `Object` are cloned to plain `Object` objects.
         * See http://www.w3.org/TR/html5/infrastructure.html#internal-structured-cloning-algorithm.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to deep clone.
         * @param {Function} [callback] The function to customize cloning values.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the deep cloned value.
         * @example
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * var deep = _.cloneDeep(stooges);
         * deep[0] === stooges[0];
         * // => false
         *
         * var view = {
     *   'label': 'docs',
     *   'node': element
     * };
         *
         * var clone = _.cloneDeep(view, function(value) {
     *   return _.isElement(value) ? value.cloneNode(true) : undefined;
     * });
         *
         * clone.node == view.node;
         * // => false
         */
        function cloneDeep(value, callback, thisArg) {
            return baseClone(value, true, typeof callback == 'function' && baseCreateCallback(callback, thisArg, 1));
        }

        /**
         * Assigns own enumerable properties of source object(s) to the destination
         * object for all destination properties that resolve to `undefined`. Once a
         * property is set, additional defaults of the same property will be ignored.
         *
         * @static
         * @memberOf _
         * @type Function
         * @category Objects
         * @param {Object} object The destination object.
         * @param {...Object} [source] The source objects.
         * @param- {Object} [guard] Allows working with `_.reduce` without using its
         *  `key` and `object` arguments as sources.
         * @returns {Object} Returns the destination object.
         * @example
         *
         * var food = { 'name': 'apple' };
         * _.defaults(food, { 'name': 'banana', 'type': 'fruit' });
         * // => { 'name': 'apple', 'type': 'fruit' }
         */
        var defaults = function (object, source, guard) {
            var index, iterable = object, result = iterable;
            if (!iterable) return result;
            var args = arguments,
                argsIndex = 0,
                argsLength = typeof guard == 'number' ? 2 : args.length;
            while (++argsIndex < argsLength) {
                iterable = args[argsIndex];
                if (iterable && objectTypes[typeof iterable]) {
                    var ownIndex = -1,
                        ownProps = objectTypes[typeof iterable] && keys(iterable),
                        length = ownProps ? ownProps.length : 0;

                    while (++ownIndex < length) {
                        index = ownProps[ownIndex];
                        if (typeof result[index] == 'undefined') result[index] = iterable[index];
                    }
                }
            }
            return result
        };

        /**
         * This method is like `_.findIndex` except that it returns the key of the
         * first element that passes the callback check, instead of the element itself.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to search.
         * @param {Function|Object|string} [callback=identity] The function called per
         *  iteration. If a property name or object is provided it will be used to
         *  create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {string|undefined} Returns the key of the found element, else `undefined`.
         * @example
         *
         * _.findKey({ 'a': 1, 'b': 2, 'c': 3, 'd': 4 }, function(num) {
     *   return num % 2 == 0;
     * });
         * // => 'b' (property order is not guaranteed across environments)
         */
        function findKey(object, callback, thisArg) {
            var result;
            callback = lodash.createCallback(callback, thisArg, 3);
            forOwn(object, function (value, key, object) {
                if (callback(value, key, object)) {
                    result = key;
                    return false;
                }
            });
            return result;
        }

        /**
         * This method is like `_.findKey` except that it iterates over elements
         * of a `collection` in the opposite order.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to search.
         * @param {Function|Object|string} [callback=identity] The function called per
         *  iteration. If a property name or object is provided it will be used to
         *  create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {string|undefined} Returns the key of the found element, else `undefined`.
         * @example
         *
         * _.findLastKey({ 'a': 1, 'b': 2, 'c': 3, 'd': 4 }, function(num) {
     *   return num % 2 == 1;
     * });
         * // => returns `c`, assuming `_.findKey` returns `a`
         */
        function findLastKey(object, callback, thisArg) {
            var result;
            callback = lodash.createCallback(callback, thisArg, 3);
            forOwnRight(object, function (value, key, object) {
                if (callback(value, key, object)) {
                    result = key;
                    return false;
                }
            });
            return result;
        }

        /**
         * Iterates over own and inherited enumerable properties of an object,
         * executing the callback for each property. The callback is bound to `thisArg`
         * and invoked with three arguments; (value, key, object). Callbacks may exit
         * iteration early by explicitly returning `false`.
         *
         * @static
         * @memberOf _
         * @type Function
         * @category Objects
         * @param {Object} object The object to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns `object`.
         * @example
         *
         * function Dog(name) {
     *   this.name = name;
     * }
         *
         * Dog.prototype.bark = function() {
     *   console.log('Woof, woof!');
     * };
         *
         * _.forIn(new Dog('Dagny'), function(value, key) {
     *   console.log(key);
     * });
         * // => logs 'bark' and 'name' (property order is not guaranteed across environments)
         */
        var forIn = function (collection, callback, thisArg) {
            var index, iterable = collection, result = iterable;
            if (!iterable) return result;
            if (!objectTypes[typeof iterable]) return result;
            callback = callback && typeof thisArg == 'undefined' ? callback : baseCreateCallback(callback, thisArg, 3);
            for (index in iterable) {
                if (callback(iterable[index], index, collection) === false) return result;
            }
            return result
        };

        /**
         * This method is like `_.forIn` except that it iterates over elements
         * of a `collection` in the opposite order.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns `object`.
         * @example
         *
         * function Dog(name) {
     *   this.name = name;
     * }
         *
         * Dog.prototype.bark = function() {
     *   console.log('Woof, woof!');
     * };
         *
         * _.forInRight(new Dog('Dagny'), function(value, key) {
     *   console.log(key);
     * });
         * // => logs 'name' and 'bark' assuming `_.forIn ` logs 'bark' and 'name'
         */
        function forInRight(object, callback, thisArg) {
            var pairs = [];

            forIn(object, function (value, key) {
                pairs.push(key, value);
            });

            var length = pairs.length;
            callback = baseCreateCallback(callback, thisArg, 3);
            while (length--) {
                if (callback(pairs[length--], pairs[length], object) === false) {
                    break;
                }
            }
            return object;
        }

        /**
         * Iterates over own enumerable properties of an object, executing the callback
         * for each property. The callback is bound to `thisArg` and invoked with three
         * arguments; (value, key, object). Callbacks may exit iteration early by
         * explicitly returning `false`.
         *
         * @static
         * @memberOf _
         * @type Function
         * @category Objects
         * @param {Object} object The object to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns `object`.
         * @example
         *
         * _.forOwn({ '0': 'zero', '1': 'one', 'length': 2 }, function(num, key) {
     *   console.log(key);
     * });
         * // => logs '0', '1', and 'length' (property order is not guaranteed across environments)
         */
        var forOwn = function (collection, callback, thisArg) {
            var index, iterable = collection, result = iterable;
            if (!iterable) return result;
            if (!objectTypes[typeof iterable]) return result;
            callback = callback && typeof thisArg == 'undefined' ? callback : baseCreateCallback(callback, thisArg, 3);
            var ownIndex = -1,
                ownProps = objectTypes[typeof iterable] && keys(iterable),
                length = ownProps ? ownProps.length : 0;

            while (++ownIndex < length) {
                index = ownProps[ownIndex];
                if (callback(iterable[index], index, collection) === false) return result;
            }
            return result
        };

        /**
         * This method is like `_.forOwn` except that it iterates over elements
         * of a `collection` in the opposite order.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns `object`.
         * @example
         *
         * _.forOwnRight({ '0': 'zero', '1': 'one', 'length': 2 }, function(num, key) {
     *   console.log(key);
     * });
         * // => logs 'length', '1', and '0' assuming `_.forOwn` logs '0', '1', and 'length'
         */
        function forOwnRight(object, callback, thisArg) {
            var props = keys(object),
                length = props.length;

            callback = baseCreateCallback(callback, thisArg, 3);
            while (length--) {
                var key = props[length];
                if (callback(object[key], key, object) === false) {
                    break;
                }
            }
            return object;
        }

        /**
         * Creates a sorted array of property names of all enumerable properties,
         * own and inherited, of `object` that have function values.
         *
         * @static
         * @memberOf _
         * @alias methods
         * @category Objects
         * @param {Object} object The object to inspect.
         * @returns {Array} Returns an array of property names that have function values.
         * @example
         *
         * _.functions(_);
         * // => ['all', 'any', 'bind', 'bindAll', 'clone', 'compact', 'compose', ...]
         */
        function functions(object) {
            var result = [];
            forIn(object, function (value, key) {
                if (isFunction(value)) {
                    result.push(key);
                }
            });
            return result.sort();
        }

        /**
         * Checks if the specified object `property` exists and is a direct property,
         * instead of an inherited property.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to check.
         * @param {string} property The property to check for.
         * @returns {boolean} Returns `true` if key is a direct property, else `false`.
         * @example
         *
         * _.has({ 'a': 1, 'b': 2, 'c': 3 }, 'b');
         * // => true
         */
        function has(object, property) {
            return object ? hasOwnProperty.call(object, property) : false;
        }

        /**
         * Creates an object composed of the inverted keys and values of the given object.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to invert.
         * @returns {Object} Returns the created inverted object.
         * @example
         *
         *  _.invert({ 'first': 'moe', 'second': 'larry' });
         * // => { 'moe': 'first', 'larry': 'second' }
         */
        function invert(object) {
            var index = -1,
                props = keys(object),
                length = props.length,
                result = {};

            while (++index < length) {
                var key = props[index];
                result[object[key]] = key;
            }
            return result;
        }

        /**
         * Checks if `value` is a boolean value.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is a boolean value, else `false`.
         * @example
         *
         * _.isBoolean(null);
         * // => false
         */
        function isBoolean(value) {
            return value === true || value === false || toString.call(value) == boolClass;
        }

        /**
         * Checks if `value` is a date.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is a date, else `false`.
         * @example
         *
         * _.isDate(new Date);
         * // => true
         */
        function isDate(value) {
            return value ? (typeof value == 'object' && toString.call(value) == dateClass) : false;
        }

        /**
         * Checks if `value` is a DOM element.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is a DOM element, else `false`.
         * @example
         *
         * _.isElement(document.body);
         * // => true
         */
        function isElement(value) {
            return value ? value.nodeType === 1 : false;
        }

        /**
         * Checks if `value` is empty. Arrays, strings, or `arguments` objects with a
         * length of `0` and objects with no own enumerable properties are considered
         * "empty".
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Array|Object|string} value The value to inspect.
         * @returns {boolean} Returns `true` if the `value` is empty, else `false`.
         * @example
         *
         * _.isEmpty([1, 2, 3]);
         * // => false
         *
         * _.isEmpty({});
         * // => true
         *
         * _.isEmpty('');
         * // => true
         */
        function isEmpty(value) {
            var result = true;
            if (!value) {
                return result;
            }
            var className = toString.call(value),
                length = value.length;

            if ((className == arrayClass || className == stringClass || className == argsClass ) ||
                (className == objectClass && typeof length == 'number' && isFunction(value.splice))) {
                return !length;
            }
            forOwn(value, function () {
                return (result = false);
            });
            return result;
        }

        /**
         * Performs a deep comparison between two values to determine if they are
         * equivalent to each other. If a callback is provided it will be executed
         * to compare values. If the callback returns `undefined` comparisons will
         * be handled by the method instead. The callback is bound to `thisArg` and
         * invoked with two arguments; (a, b).
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} a The value to compare.
         * @param {*} b The other value to compare.
         * @param {Function} [callback] The function to customize comparing values.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {boolean} Returns `true` if the values are equivalent, else `false`.
         * @example
         *
         * var moe = { 'name': 'moe', 'age': 40 };
         * var copy = { 'name': 'moe', 'age': 40 };
         *
         * moe == copy;
         * // => false
         *
         * _.isEqual(moe, copy);
         * // => true
         *
         * var words = ['hello', 'goodbye'];
         * var otherWords = ['hi', 'goodbye'];
         *
         * _.isEqual(words, otherWords, function(a, b) {
     *   var reGreet = /^(?:hello|hi)$/i,
     *       aGreet = _.isString(a) && reGreet.test(a),
     *       bGreet = _.isString(b) && reGreet.test(b);
     *
     *   return (aGreet || bGreet) ? (aGreet == bGreet) : undefined;
     * });
         * // => true
         */
        function isEqual(a, b, callback, thisArg) {
            return baseIsEqual(a, b, typeof callback == 'function' && baseCreateCallback(callback, thisArg, 2));
        }

        /**
         * Checks if `value` is, or can be coerced to, a finite number.
         *
         * Note: This is not the same as native `isFinite` which will return true for
         * booleans and empty strings. See http://es5.github.io/#x15.1.2.5.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is finite, else `false`.
         * @example
         *
         * _.isFinite(-101);
         * // => true
         *
         * _.isFinite('10');
         * // => true
         *
         * _.isFinite(true);
         * // => false
         *
         * _.isFinite('');
         * // => false
         *
         * _.isFinite(Infinity);
         * // => false
         */
        function isFinite(value) {
            return nativeIsFinite(value) && !nativeIsNaN(parseFloat(value));
        }

        /**
         * Checks if `value` is a function.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is a function, else `false`.
         * @example
         *
         * _.isFunction(_);
         * // => true
         */
        function isFunction(value) {
            return typeof value == 'function';
        }

        /**
         * Checks if `value` is the language type of Object.
         * (e.g. arrays, functions, objects, regexes, `new Number(0)`, and `new String('')`)
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is an object, else `false`.
         * @example
         *
         * _.isObject({});
         * // => true
         *
         * _.isObject([1, 2, 3]);
         * // => true
         *
         * _.isObject(1);
         * // => false
         */
        function isObject(value) {
            // check if the value is the ECMAScript language type of Object
            // http://es5.github.io/#x8
            // and avoid a V8 bug
            // http://code.google.com/p/v8/issues/detail?id=2291
            return !!(value && objectTypes[typeof value]);
        }

        /**
         * Checks if `value` is `NaN`.
         *
         * Note: This is not the same as native `isNaN` which will return `true` for
         * `undefined` and other non-numeric values. See http://es5.github.io/#x15.1.2.4.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is `NaN`, else `false`.
         * @example
         *
         * _.isNaN(NaN);
         * // => true
         *
         * _.isNaN(new Number(NaN));
         * // => true
         *
         * isNaN(undefined);
         * // => true
         *
         * _.isNaN(undefined);
         * // => false
         */
        function isNaN(value) {
            // `NaN` as a primitive is the only value that is not equal to itself
            // (perform the [[Class]] check first to avoid errors with some host objects in IE)
            return isNumber(value) && value != +value;
        }

        /**
         * Checks if `value` is `null`.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is `null`, else `false`.
         * @example
         *
         * _.isNull(null);
         * // => true
         *
         * _.isNull(undefined);
         * // => false
         */
        function isNull(value) {
            return value === null;
        }

        /**
         * Checks if `value` is a number.
         *
         * Note: `NaN` is considered a number. See http://es5.github.io/#x8.5.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is a number, else `false`.
         * @example
         *
         * _.isNumber(8.4 * 5);
         * // => true
         */
        function isNumber(value) {
            return typeof value == 'number' || toString.call(value) == numberClass;
        }

        /**
         * Checks if `value` is an object created by the `Object` constructor.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if `value` is a plain object, else `false`.
         * @example
         *
         * function Stooge(name, age) {
     *   this.name = name;
     *   this.age = age;
     * }
         *
         * _.isPlainObject(new Stooge('moe', 40));
         * // => false
         *
         * _.isPlainObject([1, 2, 3]);
         * // => false
         *
         * _.isPlainObject({ 'name': 'moe', 'age': 40 });
         * // => true
         */
        var isPlainObject = function (value) {
            if (!(value && toString.call(value) == objectClass)) {
                return false;
            }
            var valueOf = value.valueOf,
                objProto = typeof valueOf == 'function' && (objProto = getPrototypeOf(valueOf)) && getPrototypeOf(objProto);

            return objProto
                ? (value == objProto || getPrototypeOf(value) == objProto)
                : shimIsPlainObject(value);
        };

        /**
         * Checks if `value` is a regular expression.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is a regular expression, else `false`.
         * @example
         *
         * _.isRegExp(/moe/);
         * // => true
         */
        function isRegExp(value) {
            return value ? (typeof value == 'object' && toString.call(value) == regexpClass) : false;
        }

        /**
         * Checks if `value` is a string.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is a string, else `false`.
         * @example
         *
         * _.isString('moe');
         * // => true
         */
        function isString(value) {
            return typeof value == 'string' || toString.call(value) == stringClass;
        }

        /**
         * Checks if `value` is `undefined`.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {*} value The value to check.
         * @returns {boolean} Returns `true` if the `value` is `undefined`, else `false`.
         * @example
         *
         * _.isUndefined(void 0);
         * // => true
         */
        function isUndefined(value) {
            return typeof value == 'undefined';
        }

        /**
         * Recursively merges own enumerable properties of the source object(s), that
         * don't resolve to `undefined` into the destination object. Subsequent sources
         * will overwrite property assignments of previous sources. If a callback is
         * provided it will be executed to produce the merged values of the destination
         * and source properties. If the callback returns `undefined` merging will
         * be handled by the method instead. The callback is bound to `thisArg` and
         * invoked with two arguments; (objectValue, sourceValue).
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The destination object.
         * @param {...Object} [source] The source objects.
         * @param {Function} [callback] The function to customize merging properties.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns the destination object.
         * @example
         *
         * var names = {
     *   'stooges': [
     *     { 'name': 'moe' },
     *     { 'name': 'larry' }
     *   ]
     * };
         *
         * var ages = {
     *   'stooges': [
     *     { 'age': 40 },
     *     { 'age': 50 }
     *   ]
     * };
         *
         * _.merge(names, ages);
         * // => { 'stooges': [{ 'name': 'moe', 'age': 40 }, { 'name': 'larry', 'age': 50 }] }
         *
         * var food = {
     *   'fruits': ['apple'],
     *   'vegetables': ['beet']
     * };
         *
         * var otherFood = {
     *   'fruits': ['banana'],
     *   'vegetables': ['carrot']
     * };
         *
         * _.merge(food, otherFood, function(a, b) {
     *   return _.isArray(a) ? a.concat(b) : undefined;
     * });
         * // => { 'fruits': ['apple', 'banana'], 'vegetables': ['beet', 'carrot] }
         */
        function merge(object) {
            var args = arguments,
                length = 2;

            if (!isObject(object)) {
                return object;
            }
            // allows working with `_.reduce` and `_.reduceRight` without using
            // their `index` and `collection` arguments
            if (typeof args[2] != 'number') {
                length = args.length;
            }
            if (length > 3 && typeof args[length - 2] == 'function') {
                var callback = baseCreateCallback(args[--length - 1], args[length--], 2);
            } else if (length > 2 && typeof args[length - 1] == 'function') {
                callback = args[--length];
            }
            var sources = nativeSlice.call(arguments, 1, length),
                index = -1,
                stackA = getArray(),
                stackB = getArray();

            while (++index < length) {
                baseMerge(object, sources[index], callback, stackA, stackB);
            }
            releaseArray(stackA);
            releaseArray(stackB);
            return object;
        }

        /**
         * Creates a shallow clone of `object` excluding the specified properties.
         * Property names may be specified as individual arguments or as arrays of
         * property names. If a callback is provided it will be executed for each
         * property of `object` omitting the properties the callback returns truey
         * for. The callback is bound to `thisArg` and invoked with three arguments;
         * (value, key, object).
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The source object.
         * @param {Function|...string|string[]} [callback] The properties to omit or the
         *  function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns an object without the omitted properties.
         * @example
         *
         * _.omit({ 'name': 'moe', 'age': 40 }, 'age');
         * // => { 'name': 'moe' }
         *
         * _.omit({ 'name': 'moe', 'age': 40 }, function(value) {
     *   return typeof value == 'number';
     * });
         * // => { 'name': 'moe' }
         */
        function omit(object, callback, thisArg) {
            var indexOf = getIndexOf(),
                isFunc = typeof callback == 'function',
                result = {};

            if (isFunc) {
                callback = lodash.createCallback(callback, thisArg, 3);
            } else {
                var props = baseFlatten(arguments, true, false, 1);
            }
            forIn(object, function (value, key, object) {
                if (isFunc
                        ? !callback(value, key, object)
                        : indexOf(props, key) < 0
                ) {
                    result[key] = value;
                }
            });
            return result;
        }

        /**
         * Creates a two dimensional array of an object's key-value pairs,
         * i.e. `[[key1, value1], [key2, value2]]`.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to inspect.
         * @returns {Array} Returns new array of key-value pairs.
         * @example
         *
         * _.pairs({ 'moe': 30, 'larry': 40 });
         * // => [['moe', 30], ['larry', 40]] (property order is not guaranteed across environments)
         */
        function pairs(object) {
            var index = -1,
                props = keys(object),
                length = props.length,
                result = Array(length);

            while (++index < length) {
                var key = props[index];
                result[index] = [key, object[key]];
            }
            return result;
        }

        /**
         * Creates a shallow clone of `object` composed of the specified properties.
         * Property names may be specified as individual arguments or as arrays of
         * property names. If a callback is provided it will be executed for each
         * property of `object` picking the properties the callback returns truey
         * for. The callback is bound to `thisArg` and invoked with three arguments;
         * (value, key, object).
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The source object.
         * @param {Function|...string|string[]} [callback] The function called per
         *  iteration or property names to pick, specified as individual property
         *  names or arrays of property names.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns an object composed of the picked properties.
         * @example
         *
         * _.pick({ 'name': 'moe', '_userid': 'moe1' }, 'name');
         * // => { 'name': 'moe' }
         *
         * _.pick({ 'name': 'moe', '_userid': 'moe1' }, function(value, key) {
     *   return key.charAt(0) != '_';
     * });
         * // => { 'name': 'moe' }
         */
        function pick(object, callback, thisArg) {
            var result = {};
            if (typeof callback != 'function') {
                var index = -1,
                    props = baseFlatten(arguments, true, false, 1),
                    length = isObject(object) ? props.length : 0;

                while (++index < length) {
                    var key = props[index];
                    if (key in object) {
                        result[key] = object[key];
                    }
                }
            } else {
                callback = lodash.createCallback(callback, thisArg, 3);
                forIn(object, function (value, key, object) {
                    if (callback(value, key, object)) {
                        result[key] = value;
                    }
                });
            }
            return result;
        }

        /**
         * An alternative to `_.reduce` this method transforms `object` to a new
         * `accumulator` object which is the result of running each of its elements
         * through a callback, with each callback execution potentially mutating
         * the `accumulator` object. The callback is bound to `thisArg` and invoked
         * with four arguments; (accumulator, value, key, object). Callbacks may exit
         * iteration early by explicitly returning `false`.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Array|Object} collection The collection to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [accumulator] The custom accumulator value.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the accumulated value.
         * @example
         *
         * var squares = _.transform([1, 2, 3, 4, 5, 6, 7, 8, 9, 10], function(result, num) {
     *   num *= num;
     *   if (num % 2) {
     *     return result.push(num) < 3;
     *   }
     * });
         * // => [1, 9, 25]
         *
         * var mapped = _.transform({ 'a': 1, 'b': 2, 'c': 3 }, function(result, num, key) {
     *   result[key] = num * 3;
     * });
         * // => { 'a': 3, 'b': 6, 'c': 9 }
         */
        function transform(object, callback, accumulator, thisArg) {
            var isArr = isArray(object);
            callback = baseCreateCallback(callback, thisArg, 4);

            if (accumulator == null) {
                if (isArr) {
                    accumulator = [];
                } else {
                    var ctor = object && object.constructor,
                        proto = ctor && ctor.prototype;

                    accumulator = createObject(proto);
                }
            }
            (isArr ? forEach : forOwn)(object, function (value, index, object) {
                return callback(accumulator, value, index, object);
            });
            return accumulator;
        }

        /**
         * Creates an array composed of the own enumerable property values of `object`.
         *
         * @static
         * @memberOf _
         * @category Objects
         * @param {Object} object The object to inspect.
         * @returns {Array} Returns an array of property values.
         * @example
         *
         * _.values({ 'one': 1, 'two': 2, 'three': 3 });
         * // => [1, 2, 3] (property order is not guaranteed across environments)
         */
        function values(object) {
            var index = -1,
                props = keys(object),
                length = props.length,
                result = Array(length);

            while (++index < length) {
                result[index] = object[props[index]];
            }
            return result;
        }

        /*--------------------------------------------------------------------------*/

        /**
         * Creates an array of elements from the specified indexes, or keys, of the
         * `collection`. Indexes may be specified as individual arguments or as arrays
         * of indexes.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {...(number|number[]|string|string[])} [index] The indexes of `collection`
         *   to retrieve, specified as individual indexes or arrays of indexes.
         * @returns {Array} Returns a new array of elements corresponding to the
         *  provided indexes.
         * @example
         *
         * _.at(['a', 'b', 'c', 'd', 'e'], [0, 2, 4]);
         * // => ['a', 'c', 'e']
         *
         * _.at(['moe', 'larry', 'curly'], 0, 2);
         * // => ['moe', 'curly']
         */
        function at(collection) {
            var args = arguments,
                index = -1,
                props = baseFlatten(args, true, false, 1),
                length = (args[2] && args[2][args[1]] === collection) ? 1 : props.length,
                result = Array(length);

            while (++index < length) {
                result[index] = collection[props[index]];
            }
            return result;
        }

        /**
         * Checks if a given value is present in a collection using strict equality
         * for comparisons, i.e. `===`. If `fromIndex` is negative, it is used as the
         * offset from the end of the collection.
         *
         * @static
         * @memberOf _
         * @alias include
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {*} target The value to check for.
         * @param {number} [fromIndex=0] The index to search from.
         * @returns {boolean} Returns `true` if the `target` element is found, else `false`.
         * @example
         *
         * _.contains([1, 2, 3], 1);
         * // => true
         *
         * _.contains([1, 2, 3], 1, 2);
         * // => false
         *
         * _.contains({ 'name': 'moe', 'age': 40 }, 'moe');
         * // => true
         *
         * _.contains('curly', 'ur');
         * // => true
         */
        function contains(collection, target, fromIndex) {
            var index = -1,
                indexOf = getIndexOf(),
                length = collection ? collection.length : 0,
                result = false;

            fromIndex = (fromIndex < 0 ? nativeMax(0, length + fromIndex) : fromIndex) || 0;
            if (isArray(collection)) {
                result = indexOf(collection, target, fromIndex) > -1;
            } else if (typeof length == 'number') {
                result = (isString(collection) ? collection.indexOf(target, fromIndex) : indexOf(collection, target, fromIndex)) > -1;
            } else {
                forOwn(collection, function (value) {
                    if (++index >= fromIndex) {
                        return !(result = value === target);
                    }
                });
            }
            return result;
        }

        /**
         * Creates an object composed of keys generated from the results of running
         * each element of `collection` through the callback. The corresponding value
         * of each key is the number of times the key was returned by the callback.
         * The callback is bound to `thisArg` and invoked with three arguments;
         * (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns the composed aggregate object.
         * @example
         *
         * _.countBy([4.3, 6.1, 6.4], function(num) { return Math.floor(num); });
         * // => { '4': 1, '6': 2 }
         *
         * _.countBy([4.3, 6.1, 6.4], function(num) { return this.floor(num); }, Math);
         * // => { '4': 1, '6': 2 }
         *
         * _.countBy(['one', 'two', 'three'], 'length');
         * // => { '3': 2, '5': 1 }
         */
        var countBy = createAggregator(function (result, value, key) {
            (hasOwnProperty.call(result, key) ? result[key]++ : result[key] = 1);
        });

        /**
         * Checks if the given callback returns truey value for **all** elements of
         * a collection. The callback is bound to `thisArg` and invoked with three
         * arguments; (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias all
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {boolean} Returns `true` if all elements passed the callback check,
         *  else `false`.
         * @example
         *
         * _.every([true, 1, null, 'yes'], Boolean);
         * // => false
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.every(stooges, 'age');
         * // => true
         *
         * // using "_.where" callback shorthand
         * _.every(stooges, { 'age': 50 });
         * // => false
         */
        function every(collection, callback, thisArg) {
            var result = true;
            callback = lodash.createCallback(callback, thisArg, 3);

            var index = -1,
                length = collection ? collection.length : 0;

            if (typeof length == 'number') {
                while (++index < length) {
                    if (!(result = !!callback(collection[index], index, collection))) {
                        break;
                    }
                }
            } else {
                forOwn(collection, function (value, index, collection) {
                    return (result = !!callback(value, index, collection));
                });
            }
            return result;
        }

        /**
         * Iterates over elements of a collection, returning an array of all elements
         * the callback returns truey for. The callback is bound to `thisArg` and
         * invoked with three arguments; (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias select
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a new array of elements that passed the callback check.
         * @example
         *
         * var evens = _.filter([1, 2, 3, 4, 5, 6], function(num) { return num % 2 == 0; });
         * // => [2, 4, 6]
         *
         * var food = [
         *   { 'name': 'apple',  'organic': false, 'type': 'fruit' },
         *   { 'name': 'carrot', 'organic': true,  'type': 'vegetable' }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.filter(food, 'organic');
         * // => [{ 'name': 'carrot', 'organic': true, 'type': 'vegetable' }]
         *
         * // using "_.where" callback shorthand
         * _.filter(food, { 'type': 'fruit' });
         * // => [{ 'name': 'apple', 'organic': false, 'type': 'fruit' }]
         */
        function filter(collection, callback, thisArg) {
            var result = [];
            callback = lodash.createCallback(callback, thisArg, 3);

            var index = -1,
                length = collection ? collection.length : 0;

            if (typeof length == 'number') {
                while (++index < length) {
                    var value = collection[index];
                    if (callback(value, index, collection)) {
                        result.push(value);
                    }
                }
            } else {
                forOwn(collection, function (value, index, collection) {
                    if (callback(value, index, collection)) {
                        result.push(value);
                    }
                });
            }
            return result;
        }

        /**
         * Iterates over elements of a collection, returning the first element that
         * the callback returns truey for. The callback is bound to `thisArg` and
         * invoked with three arguments; (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias detect, findWhere
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the found element, else `undefined`.
         * @example
         *
         * _.find([1, 2, 3, 4], function(num) {
     *   return num % 2 == 0;
     * });
         * // => 2
         *
         * var food = [
         *   { 'name': 'apple',  'organic': false, 'type': 'fruit' },
         *   { 'name': 'banana', 'organic': true,  'type': 'fruit' },
         *   { 'name': 'beet',   'organic': false, 'type': 'vegetable' }
         * ];
         *
         * // using "_.where" callback shorthand
         * _.find(food, { 'type': 'vegetable' });
         * // => { 'name': 'beet', 'organic': false, 'type': 'vegetable' }
         *
         * // using "_.pluck" callback shorthand
         * _.find(food, 'organic');
         * // => { 'name': 'banana', 'organic': true, 'type': 'fruit' }
         */
        function find(collection, callback, thisArg) {
            callback = lodash.createCallback(callback, thisArg, 3);

            var index = -1,
                length = collection ? collection.length : 0;

            if (typeof length == 'number') {
                while (++index < length) {
                    var value = collection[index];
                    if (callback(value, index, collection)) {
                        return value;
                    }
                }
            } else {
                var result;
                forOwn(collection, function (value, index, collection) {
                    if (callback(value, index, collection)) {
                        result = value;
                        return false;
                    }
                });
                return result;
            }
        }

        /**
         * This method is like `_.find` except that it iterates over elements
         * of a `collection` from right to left.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the found element, else `undefined`.
         * @example
         *
         * _.findLast([1, 2, 3, 4], function(num) {
     *   return num % 2 == 1;
     * });
         * // => 3
         */
        function findLast(collection, callback, thisArg) {
            var result;
            callback = lodash.createCallback(callback, thisArg, 3);
            forEachRight(collection, function (value, index, collection) {
                if (callback(value, index, collection)) {
                    result = value;
                    return false;
                }
            });
            return result;
        }

        /**
         * Iterates over elements of a collection, executing the callback for each
         * element. The callback is bound to `thisArg` and invoked with three arguments;
         * (value, index|key, collection). Callbacks may exit iteration early by
         * explicitly returning `false`.
         *
         * @static
         * @memberOf _
         * @alias each
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array|Object|string} Returns `collection`.
         * @example
         *
         * _([1, 2, 3]).forEach(function(num) { console.log(num); }).join(',');
         * // => logs each number and returns '1,2,3'
         *
         * _.forEach({ 'one': 1, 'two': 2, 'three': 3 }, function(num) { console.log(num); });
         * // => logs each number and returns the object (property order is not guaranteed across environments)
         */
        function forEach(collection, callback, thisArg) {
            var index = -1,
                length = collection ? collection.length : 0;

            callback = callback && typeof thisArg == 'undefined' ? callback : baseCreateCallback(callback, thisArg, 3);
            if (typeof length == 'number') {
                while (++index < length) {
                    if (callback(collection[index], index, collection) === false) {
                        break;
                    }
                }
            } else {
                forOwn(collection, callback);
            }
            return collection;
        }

        /**
         * This method is like `_.forEach` except that it iterates over elements
         * of a `collection` from right to left.
         *
         * @static
         * @memberOf _
         * @alias eachRight
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array|Object|string} Returns `collection`.
         * @example
         *
         * _([1, 2, 3]).forEachRight(function(num) { console.log(num); }).join(',');
         * // => logs each number from right to left and returns '3,2,1'
         */
        function forEachRight(collection, callback, thisArg) {
            var length = collection ? collection.length : 0;
            callback = callback && typeof thisArg == 'undefined' ? callback : baseCreateCallback(callback, thisArg, 3);
            if (typeof length == 'number') {
                while (length--) {
                    if (callback(collection[length], length, collection) === false) {
                        break;
                    }
                }
            } else {
                var props = keys(collection);
                length = props.length;
                forOwn(collection, function (value, key, collection) {
                    key = props ? props[--length] : --length;
                    return callback(collection[key], key, collection);
                });
            }
            return collection;
        }

        /**
         * Creates an object composed of keys generated from the results of running
         * each element of a collection through the callback. The corresponding value
         * of each key is an array of the elements responsible for generating the key.
         * The callback is bound to `thisArg` and invoked with three arguments;
         * (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns the composed aggregate object.
         * @example
         *
         * _.groupBy([4.2, 6.1, 6.4], function(num) { return Math.floor(num); });
         * // => { '4': [4.2], '6': [6.1, 6.4] }
         *
         * _.groupBy([4.2, 6.1, 6.4], function(num) { return this.floor(num); }, Math);
         * // => { '4': [4.2], '6': [6.1, 6.4] }
         *
         * // using "_.pluck" callback shorthand
         * _.groupBy(['one', 'two', 'three'], 'length');
         * // => { '3': ['one', 'two'], '5': ['three'] }
         */
        var groupBy = createAggregator(function (result, value, key) {
            (hasOwnProperty.call(result, key) ? result[key] : result[key] = []).push(value);
        });

        /**
         * Creates an object composed of keys generated from the results of running
         * each element of the collection through the given callback. The corresponding
         * value of each key is the last element responsible for generating the key.
         * The callback is bound to `thisArg` and invoked with three arguments;
         * (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Object} Returns the composed aggregate object.
         * @example
         *
         * var keys = [
         *   { 'dir': 'left', 'code': 97 },
         *   { 'dir': 'right', 'code': 100 }
         * ];
         *
         * _.indexBy(keys, 'dir');
         * // => { 'left': { 'dir': 'left', 'code': 97 }, 'right': { 'dir': 'right', 'code': 100 } }
         *
         * _.indexBy(keys, function(key) { return String.fromCharCode(key.code); });
         * // => { 'a': { 'dir': 'left', 'code': 97 }, 'd': { 'dir': 'right', 'code': 100 } }
         *
         * _.indexBy(stooges, function(key) { this.fromCharCode(key.code); }, String);
         * // => { 'a': { 'dir': 'left', 'code': 97 }, 'd': { 'dir': 'right', 'code': 100 } }
         */
        var indexBy = createAggregator(function (result, value, key) {
            result[key] = value;
        });

        /**
         * Invokes the method named by `methodName` on each element in the `collection`
         * returning an array of the results of each invoked method. Additional arguments
         * will be provided to each invoked method. If `methodName` is a function it
         * will be invoked for, and `this` bound to, each element in the `collection`.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|string} methodName The name of the method to invoke or
         *  the function invoked per iteration.
         * @param {...*} [arg] Arguments to invoke the method with.
         * @returns {Array} Returns a new array of the results of each invoked method.
         * @example
         *
         * _.invoke([[5, 1, 7], [3, 2, 1]], 'sort');
         * // => [[1, 5, 7], [1, 2, 3]]
         *
         * _.invoke([123, 456], String.prototype.split, '');
         * // => [['1', '2', '3'], ['4', '5', '6']]
         */
        function invoke(collection, methodName) {
            var args = nativeSlice.call(arguments, 2),
                index = -1,
                isFunc = typeof methodName == 'function',
                length = collection ? collection.length : 0,
                result = Array(typeof length == 'number' ? length : 0);

            forEach(collection, function (value) {
                result[++index] = (isFunc ? methodName : value[methodName]).apply(value, args);
            });
            return result;
        }

        /**
         * Creates an array of values by running each element in the collection
         * through the callback. The callback is bound to `thisArg` and invoked with
         * three arguments; (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias collect
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a new array of the results of each `callback` execution.
         * @example
         *
         * _.map([1, 2, 3], function(num) { return num * 3; });
         * // => [3, 6, 9]
         *
         * _.map({ 'one': 1, 'two': 2, 'three': 3 }, function(num) { return num * 3; });
         * // => [3, 6, 9] (property order is not guaranteed across environments)
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.map(stooges, 'name');
         * // => ['moe', 'larry']
         */
        function map(collection, callback, thisArg) {
            var index = -1,
                length = collection ? collection.length : 0;

            callback = lodash.createCallback(callback, thisArg, 3);
            if (typeof length == 'number') {
                var result = Array(length);
                while (++index < length) {
                    result[index] = callback(collection[index], index, collection);
                }
            } else {
                result = [];
                forOwn(collection, function (value, key, collection) {
                    result[++index] = callback(value, key, collection);
                });
            }
            return result;
        }

        /**
         * Retrieves the maximum value of a collection. If the collection is empty or
         * falsey `-Infinity` is returned. If a callback is provided it will be executed
         * for each value in the collection to generate the criterion by which the value
         * is ranked. The callback is bound to `thisArg` and invoked with three
         * arguments; (value, index, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the maximum value.
         * @example
         *
         * _.max([4, 2, 8, 6]);
         * // => 8
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * _.max(stooges, function(stooge) { return stooge.age; });
         * // => { 'name': 'larry', 'age': 50 };
         *
         * // using "_.pluck" callback shorthand
         * _.max(stooges, 'age');
         * // => { 'name': 'larry', 'age': 50 };
         */
        function max(collection, callback, thisArg) {
            var computed = -Infinity,
                result = computed;

            if (!callback && isArray(collection)) {
                var index = -1,
                    length = collection.length;

                while (++index < length) {
                    var value = collection[index];
                    if (value > result) {
                        result = value;
                    }
                }
            } else {
                callback = (!callback && isString(collection))
                    ? charAtCallback
                    : lodash.createCallback(callback, thisArg, 3);

                forEach(collection, function (value, index, collection) {
                    var current = callback(value, index, collection);
                    if (current > computed) {
                        computed = current;
                        result = value;
                    }
                });
            }
            return result;
        }

        /**
         * Retrieves the minimum value of a collection. If the collection is empty or
         * falsey `Infinity` is returned. If a callback is provided it will be executed
         * for each value in the collection to generate the criterion by which the value
         * is ranked. The callback is bound to `thisArg` and invoked with three
         * arguments; (value, index, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the minimum value.
         * @example
         *
         * _.min([4, 2, 8, 6]);
         * // => 2
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * _.min(stooges, function(stooge) { return stooge.age; });
         * // => { 'name': 'moe', 'age': 40 };
         *
         * // using "_.pluck" callback shorthand
         * _.min(stooges, 'age');
         * // => { 'name': 'moe', 'age': 40 };
         */
        function min(collection, callback, thisArg) {
            var computed = Infinity,
                result = computed;

            if (!callback && isArray(collection)) {
                var index = -1,
                    length = collection.length;

                while (++index < length) {
                    var value = collection[index];
                    if (value < result) {
                        result = value;
                    }
                }
            } else {
                callback = (!callback && isString(collection))
                    ? charAtCallback
                    : lodash.createCallback(callback, thisArg, 3);

                forEach(collection, function (value, index, collection) {
                    var current = callback(value, index, collection);
                    if (current < computed) {
                        computed = current;
                        result = value;
                    }
                });
            }
            return result;
        }

        /**
         * Retrieves the value of a specified property from all elements in the `collection`.
         *
         * @static
         * @memberOf _
         * @type Function
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {string} property The property to pluck.
         * @returns {Array} Returns a new array of property values.
         * @example
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * _.pluck(stooges, 'name');
         * // => ['moe', 'larry']
         */
        function pluck(collection, property) {
            var index = -1,
                length = collection ? collection.length : 0;

            if (typeof length == 'number') {
                var result = Array(length);
                while (++index < length) {
                    result[index] = collection[index][property];
                }
            }
            return result || map(collection, property);
        }

        /**
         * Reduces a collection to a value which is the accumulated result of running
         * each element in the collection through the callback, where each successive
         * callback execution consumes the return value of the previous execution. If
         * `accumulator` is not provided the first element of the collection will be
         * used as the initial `accumulator` value. The callback is bound to `thisArg`
         * and invoked with four arguments; (accumulator, value, index|key, collection).
         *
         * @static
         * @memberOf _
         * @alias foldl, inject
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [accumulator] Initial value of the accumulator.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the accumulated value.
         * @example
         *
         * var sum = _.reduce([1, 2, 3], function(sum, num) {
     *   return sum + num;
     * });
         * // => 6
         *
         * var mapped = _.reduce({ 'a': 1, 'b': 2, 'c': 3 }, function(result, num, key) {
     *   result[key] = num * 3;
     *   return result;
     * }, {});
         * // => { 'a': 3, 'b': 6, 'c': 9 }
         */
        function reduce(collection, callback, accumulator, thisArg) {
            if (!collection) return accumulator;
            var noaccum = arguments.length < 3;
            callback = baseCreateCallback(callback, thisArg, 4);

            var index = -1,
                length = collection.length;

            if (typeof length == 'number') {
                if (noaccum) {
                    accumulator = collection[++index];
                }
                while (++index < length) {
                    accumulator = callback(accumulator, collection[index], index, collection);
                }
            } else {
                forOwn(collection, function (value, index, collection) {
                    accumulator = noaccum
                        ? (noaccum = false, value)
                        : callback(accumulator, value, index, collection)
                });
            }
            return accumulator;
        }

        /**
         * This method is like `_.reduce` except that it iterates over elements
         * of a `collection` from right to left.
         *
         * @static
         * @memberOf _
         * @alias foldr
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function} [callback=identity] The function called per iteration.
         * @param {*} [accumulator] Initial value of the accumulator.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the accumulated value.
         * @example
         *
         * var list = [[0, 1], [2, 3], [4, 5]];
         * var flat = _.reduceRight(list, function(a, b) { return a.concat(b); }, []);
         * // => [4, 5, 2, 3, 0, 1]
         */
        function reduceRight(collection, callback, accumulator, thisArg) {
            var noaccum = arguments.length < 3;
            callback = baseCreateCallback(callback, thisArg, 4);
            forEachRight(collection, function (value, index, collection) {
                accumulator = noaccum
                    ? (noaccum = false, value)
                    : callback(accumulator, value, index, collection);
            });
            return accumulator;
        }

        /**
         * The opposite of `_.filter` this method returns the elements of a
         * collection that the callback does **not** return truey for.
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a new array of elements that failed the callback check.
         * @example
         *
         * var odds = _.reject([1, 2, 3, 4, 5, 6], function(num) { return num % 2 == 0; });
         * // => [1, 3, 5]
         *
         * var food = [
         *   { 'name': 'apple',  'organic': false, 'type': 'fruit' },
         *   { 'name': 'carrot', 'organic': true,  'type': 'vegetable' }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.reject(food, 'organic');
         * // => [{ 'name': 'apple', 'organic': false, 'type': 'fruit' }]
         *
         * // using "_.where" callback shorthand
         * _.reject(food, { 'type': 'fruit' });
         * // => [{ 'name': 'carrot', 'organic': true, 'type': 'vegetable' }]
         */
        function reject(collection, callback, thisArg) {
            callback = lodash.createCallback(callback, thisArg, 3);
            return filter(collection, function (value, index, collection) {
                return !callback(value, index, collection);
            });
        }

        /**
         * Retrieves a random element or `n` random elements from a collection.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to sample.
         * @param {number} [n] The number of elements to sample.
         * @param- {Object} [guard] Allows working with functions, like `_.map`,
         *  without using their `key` and `object` arguments as sources.
         * @returns {Array} Returns the random sample(s) of `collection`.
         * @example
         *
         * _.sample([1, 2, 3, 4]);
         * // => 2
         *
         * _.sample([1, 2, 3, 4], 2);
         * // => [3, 1]
         */
        function sample(collection, n, guard) {
            var length = collection ? collection.length : 0;
            if (typeof length != 'number') {
                collection = values(collection);
            }
            if (n == null || guard) {
                return collection ? collection[random(length - 1)] : undefined;
            }
            var result = shuffle(collection);
            result.length = nativeMin(nativeMax(0, n), result.length);
            return result;
        }

        /**
         * Creates an array of shuffled values, using a version of the Fisher-Yates
         * shuffle. See http://en.wikipedia.org/wiki/Fisher-Yates_shuffle.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to shuffle.
         * @returns {Array} Returns a new shuffled collection.
         * @example
         *
         * _.shuffle([1, 2, 3, 4, 5, 6]);
         * // => [4, 1, 6, 3, 5, 2]
         */
        function shuffle(collection) {
            var index = -1,
                length = collection ? collection.length : 0,
                result = Array(typeof length == 'number' ? length : 0);

            forEach(collection, function (value) {
                var rand = random(++index);
                result[index] = result[rand];
                result[rand] = value;
            });
            return result;
        }

        /**
         * Gets the size of the `collection` by returning `collection.length` for arrays
         * and array-like objects or the number of own enumerable properties for objects.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to inspect.
         * @returns {number} Returns `collection.length` or number of own enumerable properties.
         * @example
         *
         * _.size([1, 2]);
         * // => 2
         *
         * _.size({ 'one': 1, 'two': 2, 'three': 3 });
         * // => 3
         *
         * _.size('curly');
         * // => 5
         */
        function size(collection) {
            var length = collection ? collection.length : 0;
            return typeof length == 'number' ? length : keys(collection).length;
        }

        /**
         * Checks if the callback returns a truey value for **any** element of a
         * collection. The function returns as soon as it finds a passing value and
         * does not iterate over the entire collection. The callback is bound to
         * `thisArg` and invoked with three arguments; (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias any
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {boolean} Returns `true` if any element passed the callback check,
         *  else `false`.
         * @example
         *
         * _.some([null, 0, 'yes', false], Boolean);
         * // => true
         *
         * var food = [
         *   { 'name': 'apple',  'organic': false, 'type': 'fruit' },
         *   { 'name': 'carrot', 'organic': true,  'type': 'vegetable' }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.some(food, 'organic');
         * // => true
         *
         * // using "_.where" callback shorthand
         * _.some(food, { 'type': 'meat' });
         * // => false
         */
        function some(collection, callback, thisArg) {
            var result;
            callback = lodash.createCallback(callback, thisArg, 3);

            var index = -1,
                length = collection ? collection.length : 0;

            if (typeof length == 'number') {
                while (++index < length) {
                    if ((result = callback(collection[index], index, collection))) {
                        break;
                    }
                }
            } else {
                forOwn(collection, function (value, index, collection) {
                    return !(result = callback(value, index, collection));
                });
            }
            return !!result;
        }

        /**
         * Creates an array of elements, sorted in ascending order by the results of
         * running each element in a collection through the callback. This method
         * performs a stable sort, that is, it will preserve the original sort order
         * of equal elements. The callback is bound to `thisArg` and invoked with
         * three arguments; (value, index|key, collection).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a new array of sorted elements.
         * @example
         *
         * _.sortBy([1, 2, 3], function(num) { return Math.sin(num); });
         * // => [3, 1, 2]
         *
         * _.sortBy([1, 2, 3], function(num) { return this.sin(num); }, Math);
         * // => [3, 1, 2]
         *
         * // using "_.pluck" callback shorthand
         * _.sortBy(['banana', 'strawberry', 'apple'], 'length');
         * // => ['apple', 'banana', 'strawberry']
         */
        function sortBy(collection, callback, thisArg) {
            var index = -1,
                length = collection ? collection.length : 0,
                result = Array(typeof length == 'number' ? length : 0);

            callback = lodash.createCallback(callback, thisArg, 3);
            forEach(collection, function (value, key, collection) {
                var object = result[++index] = getObject();
                object.criteria = callback(value, key, collection);
                object.index = index;
                object.value = value;
            });

            length = result.length;
            result.sort(compareAscending);
            while (length--) {
                var object = result[length];
                result[length] = object.value;
                releaseObject(object);
            }
            return result;
        }

        /**
         * Converts the `collection` to an array.
         *
         * @static
         * @memberOf _
         * @category Collections
         * @param {Array|Object|string} collection The collection to convert.
         * @returns {Array} Returns the new converted array.
         * @example
         *
         * (function() { return _.toArray(arguments).slice(1); })(1, 2, 3, 4);
         * // => [2, 3, 4]
         */
        function toArray(collection) {
            if (collection && typeof collection.length == 'number') {
                return slice(collection);
            }
            return values(collection);
        }

        /**
         * Performs a deep comparison of each element in a `collection` to the given
         * `properties` object, returning an array of all elements that have equivalent
         * property values.
         *
         * @static
         * @memberOf _
         * @type Function
         * @category Collections
         * @param {Array|Object|string} collection The collection to iterate over.
         * @param {Object} properties The object of property values to filter by.
         * @returns {Array} Returns a new array of elements that have the given properties.
         * @example
         *
         * var stooges = [
         *   { 'name': 'curly', 'age': 30, 'quotes': ['Oh, a wise guy, eh?', 'Poifect!'] },
         *   { 'name': 'moe', 'age': 40, 'quotes': ['Spread out!', 'You knucklehead!'] }
         * ];
         *
         * _.where(stooges, { 'age': 40 });
         * // => [{ 'name': 'moe', 'age': 40, 'quotes': ['Spread out!', 'You knucklehead!'] }]
         *
         * _.where(stooges, { 'quotes': ['Poifect!'] });
         * // => [{ 'name': 'curly', 'age': 30, 'quotes': ['Oh, a wise guy, eh?', 'Poifect!'] }]
         */
        var where = filter;

        /*--------------------------------------------------------------------------*/

        /**
         * Creates an array with all falsey values removed. The values `false`, `null`,
         * `0`, `""`, `undefined`, and `NaN` are all falsey.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to compact.
         * @returns {Array} Returns a new array of filtered values.
         * @example
         *
         * _.compact([0, 1, false, 2, '', 3]);
         * // => [1, 2, 3]
         */
        function compact(array) {
            var index = -1,
                length = array ? array.length : 0,
                result = [];

            while (++index < length) {
                var value = array[index];
                if (value) {
                    result.push(value);
                }
            }
            return result;
        }

        /**
         * Creates an array excluding all values of the provided arrays using strict
         * equality for comparisons, i.e. `===`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to process.
         * @param {...Array} [array] The arrays of values to exclude.
         * @returns {Array} Returns a new array of filtered values.
         * @example
         *
         * _.difference([1, 2, 3, 4, 5], [5, 2, 10]);
         * // => [1, 3, 4]
         */
        function difference(array) {
            var index = -1,
                indexOf = getIndexOf(),
                length = array ? array.length : 0,
                seen = baseFlatten(arguments, true, true, 1),
                result = [];

            var isLarge = length >= largeArraySize && indexOf === baseIndexOf;

            if (isLarge) {
                var cache = createCache(seen);
                if (cache) {
                    indexOf = cacheIndexOf;
                    seen = cache;
                } else {
                    isLarge = false;
                }
            }
            while (++index < length) {
                var value = array[index];
                if (indexOf(seen, value) < 0) {
                    result.push(value);
                }
            }
            if (isLarge) {
                releaseObject(seen);
            }
            return result;
        }

        /**
         * This method is like `_.find` except that it returns the index of the first
         * element that passes the callback check, instead of the element itself.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to search.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {number} Returns the index of the found element, else `-1`.
         * @example
         *
         * _.findIndex(['apple', 'banana', 'beet'], function(food) {
     *   return /^b/.test(food);
     * });
         * // => 1
         */
        function findIndex(array, callback, thisArg) {
            var index = -1,
                length = array ? array.length : 0;

            callback = lodash.createCallback(callback, thisArg, 3);
            while (++index < length) {
                if (callback(array[index], index, array)) {
                    return index;
                }
            }
            return -1;
        }

        /**
         * This method is like `_.findIndex` except that it iterates over elements
         * of a `collection` from right to left.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to search.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {number} Returns the index of the found element, else `-1`.
         * @example
         *
         * _.findLastIndex(['apple', 'banana', 'beet'], function(food) {
     *   return /^b/.test(food);
     * });
         * // => 2
         */
        function findLastIndex(array, callback, thisArg) {
            var length = array ? array.length : 0;
            callback = lodash.createCallback(callback, thisArg, 3);
            while (length--) {
                if (callback(array[length], length, array)) {
                    return length;
                }
            }
            return -1;
        }

        /**
         * Gets the first element or first `n` elements of an array. If a callback
         * is provided elements at the beginning of the array are returned as long
         * as the callback returns truey. The callback is bound to `thisArg` and
         * invoked with three arguments; (value, index, array).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias head, take
         * @category Arrays
         * @param {Array} array The array to query.
         * @param {Function|Object|number|string} [callback] The function called
         *  per element or the number of elements to return. If a property name or
         *  object is provided it will be used to create a "_.pluck" or "_.where"
         *  style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the first element(s) of `array`.
         * @example
         *
         * _.first([1, 2, 3]);
         * // => 1
         *
         * _.first([1, 2, 3], 2);
         * // => [1, 2]
         *
         * _.first([1, 2, 3], function(num) {
     *   return num < 3;
     * });
         * // => [1, 2]
         *
         * var food = [
         *   { 'name': 'banana', 'organic': true },
         *   { 'name': 'beet',   'organic': false },
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.first(food, 'organic');
         * // => [{ 'name': 'banana', 'organic': true }]
         *
         * var food = [
         *   { 'name': 'apple',  'type': 'fruit' },
         *   { 'name': 'banana', 'type': 'fruit' },
         *   { 'name': 'beet',   'type': 'vegetable' }
         * ];
         *
         * // using "_.where" callback shorthand
         * _.first(food, { 'type': 'fruit' });
         * // => [{ 'name': 'apple', 'type': 'fruit' }, { 'name': 'banana', 'type': 'fruit' }]
         */
        function first(array, callback, thisArg) {
            var n = 0,
                length = array ? array.length : 0;

            if (typeof callback != 'number' && callback != null) {
                var index = -1;
                callback = lodash.createCallback(callback, thisArg, 3);
                while (++index < length && callback(array[index], index, array)) {
                    n++;
                }
            } else {
                n = callback;
                if (n == null || thisArg) {
                    return array ? array[0] : undefined;
                }
            }
            return slice(array, 0, nativeMin(nativeMax(0, n), length));
        }

        /**
         * Flattens a nested array (the nesting can be to any depth). If `isShallow`
         * is truey, the array will only be flattened a single level. If a callback
         * is provided each element of the array is passed through the callback before
         * flattening. The callback is bound to `thisArg` and invoked with three
         * arguments; (value, index, array).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to flatten.
         * @param {boolean} [isShallow=false] A flag to restrict flattening to a single level.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a new flattened array.
         * @example
         *
         * _.flatten([1, [2], [3, [[4]]]]);
         * // => [1, 2, 3, 4];
         *
         * _.flatten([1, [2], [3, [[4]]]], true);
         * // => [1, 2, 3, [[4]]];
         *
         * var stooges = [
         *   { 'name': 'curly', 'quotes': ['Oh, a wise guy, eh?', 'Poifect!'] },
         *   { 'name': 'moe', 'quotes': ['Spread out!', 'You knucklehead!'] }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.flatten(stooges, 'quotes');
         * // => ['Oh, a wise guy, eh?', 'Poifect!', 'Spread out!', 'You knucklehead!']
         */
        function flatten(array, isShallow, callback, thisArg) {
            // juggle arguments
            if (typeof isShallow != 'boolean' && isShallow != null) {
                thisArg = callback;
                callback = !(thisArg && thisArg[isShallow] === array) ? isShallow : null;
                isShallow = false;
            }
            if (callback != null) {
                array = map(array, callback, thisArg);
            }
            return baseFlatten(array, isShallow);
        }

        /**
         * Gets the index at which the first occurrence of `value` is found using
         * strict equality for comparisons, i.e. `===`. If the array is already sorted
         * providing `true` for `fromIndex` will run a faster binary search.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to search.
         * @param {*} value The value to search for.
         * @param {boolean|number} [fromIndex=0] The index to search from or `true`
         *  to perform a binary search on a sorted array.
         * @returns {number} Returns the index of the matched value or `-1`.
         * @example
         *
         * _.indexOf([1, 2, 3, 1, 2, 3], 2);
         * // => 1
         *
         * _.indexOf([1, 2, 3, 1, 2, 3], 2, 3);
         * // => 4
         *
         * _.indexOf([1, 1, 2, 2, 3, 3], 2, true);
         * // => 2
         */
        function indexOf(array, value, fromIndex) {
            if (typeof fromIndex == 'number') {
                var length = array ? array.length : 0;
                fromIndex = (fromIndex < 0 ? nativeMax(0, length + fromIndex) : fromIndex || 0);
            } else if (fromIndex) {
                var index = sortedIndex(array, value);
                return array[index] === value ? index : -1;
            }
            return baseIndexOf(array, value, fromIndex);
        }

        /**
         * Gets all but the last element or last `n` elements of an array. If a
         * callback is provided elements at the end of the array are excluded from
         * the result as long as the callback returns truey. The callback is bound
         * to `thisArg` and invoked with three arguments; (value, index, array).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to query.
         * @param {Function|Object|number|string} [callback=1] The function called
         *  per element or the number of elements to exclude. If a property name or
         *  object is provided it will be used to create a "_.pluck" or "_.where"
         *  style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a slice of `array`.
         * @example
         *
         * _.initial([1, 2, 3]);
         * // => [1, 2]
         *
         * _.initial([1, 2, 3], 2);
         * // => [1]
         *
         * _.initial([1, 2, 3], function(num) {
     *   return num > 1;
     * });
         * // => [1]
         *
         * var food = [
         *   { 'name': 'beet',   'organic': false },
         *   { 'name': 'carrot', 'organic': true }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.initial(food, 'organic');
         * // => [{ 'name': 'beet',   'organic': false }]
         *
         * var food = [
         *   { 'name': 'banana', 'type': 'fruit' },
         *   { 'name': 'beet',   'type': 'vegetable' },
         *   { 'name': 'carrot', 'type': 'vegetable' }
         * ];
         *
         * // using "_.where" callback shorthand
         * _.initial(food, { 'type': 'vegetable' });
         * // => [{ 'name': 'banana', 'type': 'fruit' }]
         */
        function initial(array, callback, thisArg) {
            var n = 0,
                length = array ? array.length : 0;

            if (typeof callback != 'number' && callback != null) {
                var index = length;
                callback = lodash.createCallback(callback, thisArg, 3);
                while (index-- && callback(array[index], index, array)) {
                    n++;
                }
            } else {
                n = (callback == null || thisArg) ? 1 : callback || n;
            }
            return slice(array, 0, nativeMin(nativeMax(0, length - n), length));
        }

        /**
         * Creates an array of unique values present in all provided arrays using
         * strict equality for comparisons, i.e. `===`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {...Array} [array] The arrays to inspect.
         * @returns {Array} Returns an array of composite values.
         * @example
         *
         * _.intersection([1, 2, 3], [101, 2, 1, 10], [2, 1]);
         * // => [1, 2]
         */
        function intersection(array) {
            var args = arguments,
                argsLength = args.length,
                argsIndex = -1,
                caches = getArray(),
                index = -1,
                indexOf = getIndexOf(),
                length = array ? array.length : 0,
                result = [],
                seen = getArray();

            while (++argsIndex < argsLength) {
                var value = args[argsIndex];
                caches[argsIndex] = indexOf === baseIndexOf &&
                    (value ? value.length : 0) >= largeArraySize &&
                    createCache(argsIndex ? args[argsIndex] : seen);
            }
            outer:
                while (++index < length) {
                    var cache = caches[0];
                    value = array[index];

                    if ((cache ? cacheIndexOf(cache, value) : indexOf(seen, value)) < 0) {
                        argsIndex = argsLength;
                        (cache || seen).push(value);
                        while (--argsIndex) {
                            cache = caches[argsIndex];
                            if ((cache ? cacheIndexOf(cache, value) : indexOf(args[argsIndex], value)) < 0) {
                                continue outer;
                            }
                        }
                        result.push(value);
                    }
                }
            while (argsLength--) {
                cache = caches[argsLength];
                if (cache) {
                    releaseObject(cache);
                }
            }
            releaseArray(caches);
            releaseArray(seen);
            return result;
        }

        /**
         * Gets the last element or last `n` elements of an array. If a callback is
         * provided elements at the end of the array are returned as long as the
         * callback returns truey. The callback is bound to `thisArg` and invoked
         * with three arguments; (value, index, array).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to query.
         * @param {Function|Object|number|string} [callback] The function called
         *  per element or the number of elements to return. If a property name or
         *  object is provided it will be used to create a "_.pluck" or "_.where"
         *  style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {*} Returns the last element(s) of `array`.
         * @example
         *
         * _.last([1, 2, 3]);
         * // => 3
         *
         * _.last([1, 2, 3], 2);
         * // => [2, 3]
         *
         * _.last([1, 2, 3], function(num) {
     *   return num > 1;
     * });
         * // => [2, 3]
         *
         * var food = [
         *   { 'name': 'beet',   'organic': false },
         *   { 'name': 'carrot', 'organic': true }
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.last(food, 'organic');
         * // => [{ 'name': 'carrot', 'organic': true }]
         *
         * var food = [
         *   { 'name': 'banana', 'type': 'fruit' },
         *   { 'name': 'beet',   'type': 'vegetable' },
         *   { 'name': 'carrot', 'type': 'vegetable' }
         * ];
         *
         * // using "_.where" callback shorthand
         * _.last(food, { 'type': 'vegetable' });
         * // => [{ 'name': 'beet', 'type': 'vegetable' }, { 'name': 'carrot', 'type': 'vegetable' }]
         */
        function last(array, callback, thisArg) {
            var n = 0,
                length = array ? array.length : 0;

            if (typeof callback != 'number' && callback != null) {
                var index = length;
                callback = lodash.createCallback(callback, thisArg, 3);
                while (index-- && callback(array[index], index, array)) {
                    n++;
                }
            } else {
                n = callback;
                if (n == null || thisArg) {
                    return array ? array[length - 1] : undefined;
                }
            }
            return slice(array, nativeMax(0, length - n));
        }

        /**
         * Gets the index at which the last occurrence of `value` is found using strict
         * equality for comparisons, i.e. `===`. If `fromIndex` is negative, it is used
         * as the offset from the end of the collection.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to search.
         * @param {*} value The value to search for.
         * @param {number} [fromIndex=array.length-1] The index to search from.
         * @returns {number} Returns the index of the matched value or `-1`.
         * @example
         *
         * _.lastIndexOf([1, 2, 3, 1, 2, 3], 2);
         * // => 4
         *
         * _.lastIndexOf([1, 2, 3, 1, 2, 3], 2, 3);
         * // => 1
         */
        function lastIndexOf(array, value, fromIndex) {
            var index = array ? array.length : 0;
            if (typeof fromIndex == 'number') {
                index = (fromIndex < 0 ? nativeMax(0, index + fromIndex) : nativeMin(fromIndex, index - 1)) + 1;
            }
            while (index--) {
                if (array[index] === value) {
                    return index;
                }
            }
            return -1;
        }

        /**
         * Removes all provided values from the given array using strict equality for
         * comparisons, i.e. `===`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to modify.
         * @param {...*} [value] The values to remove.
         * @returns {Array} Returns `array`.
         * @example
         *
         * var array = [1, 2, 3, 1, 2, 3];
         * _.pull(array, 2, 3);
         * console.log(array);
         * // => [1, 1]
         */
        function pull(array) {
            var args = arguments,
                argsIndex = 0,
                argsLength = args.length,
                length = array ? array.length : 0;

            while (++argsIndex < argsLength) {
                var index = -1,
                    value = args[argsIndex];
                while (++index < length) {
                    if (array[index] === value) {
                        splice.call(array, index--, 1);
                        length--;
                    }
                }
            }
            return array;
        }

        /**
         * Creates an array of numbers (positive and/or negative) progressing from
         * `start` up to but not including `end`. If `start` is less than `stop` a
         * zero-length range is created unless a negative `step` is specified.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {number} [start=0] The start of the range.
         * @param {number} end The end of the range.
         * @param {number} [step=1] The value to increment or decrement by.
         * @returns {Array} Returns a new range array.
         * @example
         *
         * _.range(10);
         * // => [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
         *
         * _.range(1, 11);
         * // => [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
         *
         * _.range(0, 30, 5);
         * // => [0, 5, 10, 15, 20, 25]
         *
         * _.range(0, -10, -1);
         * // => [0, -1, -2, -3, -4, -5, -6, -7, -8, -9]
         *
         * _.range(1, 4, 0);
         * // => [1, 1, 1]
         *
         * _.range(0);
         * // => []
         */
        function range(start, end, step) {
            start = +start || 0;
            step = typeof step == 'number' ? step : (+step || 1);

            if (end == null) {
                end = start;
                start = 0;
            }
            // use `Array(length)` so engines, like Chakra and V8, avoid slower modes
            // http://youtu.be/XAqIpGU8ZZk#t=17m25s
            var index = -1,
                length = nativeMax(0, ceil((end - start) / (step || 1))),
                result = Array(length);

            while (++index < length) {
                result[index] = start;
                start += step;
            }
            return result;
        }

        /**
         * Removes all elements from an array that the callback returns truey for
         * and returns an array of removed elements. The callback is bound to `thisArg`
         * and invoked with three arguments; (value, index, array).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to modify.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a new array of removed elements.
         * @example
         *
         * var array = [1, 2, 3, 4, 5, 6];
         * var evens = _.remove(array, function(num) { return num % 2 == 0; });
         *
         * console.log(array);
         * // => [1, 3, 5]
         *
         * console.log(evens);
         * // => [2, 4, 6]
         */
        function remove(array, callback, thisArg) {
            var index = -1,
                length = array ? array.length : 0,
                result = [];

            callback = lodash.createCallback(callback, thisArg, 3);
            while (++index < length) {
                var value = array[index];
                if (callback(value, index, array)) {
                    result.push(value);
                    splice.call(array, index--, 1);
                    length--;
                }
            }
            return result;
        }

        /**
         * The opposite of `_.initial` this method gets all but the first element or
         * first `n` elements of an array. If a callback function is provided elements
         * at the beginning of the array are excluded from the result as long as the
         * callback returns truey. The callback is bound to `thisArg` and invoked
         * with three arguments; (value, index, array).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias drop, tail
         * @category Arrays
         * @param {Array} array The array to query.
         * @param {Function|Object|number|string} [callback=1] The function called
         *  per element or the number of elements to exclude. If a property name or
         *  object is provided it will be used to create a "_.pluck" or "_.where"
         *  style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a slice of `array`.
         * @example
         *
         * _.rest([1, 2, 3]);
         * // => [2, 3]
         *
         * _.rest([1, 2, 3], 2);
         * // => [3]
         *
         * _.rest([1, 2, 3], function(num) {
     *   return num < 3;
     * });
         * // => [3]
         *
         * var food = [
         *   { 'name': 'banana', 'organic': true },
         *   { 'name': 'beet',   'organic': false },
         * ];
         *
         * // using "_.pluck" callback shorthand
         * _.rest(food, 'organic');
         * // => [{ 'name': 'beet', 'organic': false }]
         *
         * var food = [
         *   { 'name': 'apple',  'type': 'fruit' },
         *   { 'name': 'banana', 'type': 'fruit' },
         *   { 'name': 'beet',   'type': 'vegetable' }
         * ];
         *
         * // using "_.where" callback shorthand
         * _.rest(food, { 'type': 'fruit' });
         * // => [{ 'name': 'beet', 'type': 'vegetable' }]
         */
        function rest(array, callback, thisArg) {
            if (typeof callback != 'number' && callback != null) {
                var n = 0,
                    index = -1,
                    length = array ? array.length : 0;

                callback = lodash.createCallback(callback, thisArg, 3);
                while (++index < length && callback(array[index], index, array)) {
                    n++;
                }
            } else {
                n = (callback == null || thisArg) ? 1 : nativeMax(0, callback);
            }
            return slice(array, n);
        }

        /**
         * Uses a binary search to determine the smallest index at which a value
         * should be inserted into a given sorted array in order to maintain the sort
         * order of the array. If a callback is provided it will be executed for
         * `value` and each element of `array` to compute their sort ranking. The
         * callback is bound to `thisArg` and invoked with one argument; (value).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to inspect.
         * @param {*} value The value to evaluate.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {number} Returns the index at which `value` should be inserted
         *  into `array`.
         * @example
         *
         * _.sortedIndex([20, 30, 50], 40);
         * // => 2
         *
         * // using "_.pluck" callback shorthand
         * _.sortedIndex([{ 'x': 20 }, { 'x': 30 }, { 'x': 50 }], { 'x': 40 }, 'x');
         * // => 2
         *
         * var dict = {
     *   'wordToNumber': { 'twenty': 20, 'thirty': 30, 'fourty': 40, 'fifty': 50 }
     * };
         *
         * _.sortedIndex(['twenty', 'thirty', 'fifty'], 'fourty', function(word) {
     *   return dict.wordToNumber[word];
     * });
         * // => 2
         *
         * _.sortedIndex(['twenty', 'thirty', 'fifty'], 'fourty', function(word) {
     *   return this.wordToNumber[word];
     * }, dict);
         * // => 2
         */
        function sortedIndex(array, value, callback, thisArg) {
            var low = 0,
                high = array ? array.length : low;

            // explicitly reference `identity` for better inlining in Firefox
            callback = callback ? lodash.createCallback(callback, thisArg, 1) : identity;
            value = callback(value);

            while (low < high) {
                var mid = (low + high) >>> 1;
                (callback(array[mid]) < value)
                    ? low = mid + 1
                    : high = mid;
            }
            return low;
        }

        /**
         * Creates an array of unique values, in order, of the provided arrays using
         * strict equality for comparisons, i.e. `===`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {...Array} [array] The arrays to inspect.
         * @returns {Array} Returns an array of composite values.
         * @example
         *
         * _.union([1, 2, 3], [101, 2, 1, 10], [2, 1]);
         * // => [1, 2, 3, 101, 10]
         */
        function union(array) {
            return baseUniq(baseFlatten(arguments, true, true));
        }

        /**
         * Creates a duplicate-value-free version of an array using strict equality
         * for comparisons, i.e. `===`. If the array is sorted, providing
         * `true` for `isSorted` will use a faster algorithm. If a callback is provided
         * each element of `array` is passed through the callback before uniqueness
         * is computed. The callback is bound to `thisArg` and invoked with three
         * arguments; (value, index, array).
         *
         * If a property name is provided for `callback` the created "_.pluck" style
         * callback will return the property value of the given element.
         *
         * If an object is provided for `callback` the created "_.where" style callback
         * will return `true` for elements that have the properties of the given object,
         * else `false`.
         *
         * @static
         * @memberOf _
         * @alias unique
         * @category Arrays
         * @param {Array} array The array to process.
         * @param {boolean} [isSorted=false] A flag to indicate that `array` is sorted.
         * @param {Function|Object|string} [callback=identity] The function called
         *  per iteration. If a property name or object is provided it will be used
         *  to create a "_.pluck" or "_.where" style callback, respectively.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns a duplicate-value-free array.
         * @example
         *
         * _.uniq([1, 2, 1, 3, 1]);
         * // => [1, 2, 3]
         *
         * _.uniq([1, 1, 2, 2, 3], true);
         * // => [1, 2, 3]
         *
         * _.uniq(['A', 'b', 'C', 'a', 'B', 'c'], function(letter) { return letter.toLowerCase(); });
         * // => ['A', 'b', 'C']
         *
         * _.uniq([1, 2.5, 3, 1.5, 2, 3.5], function(num) { return this.floor(num); }, Math);
         * // => [1, 2.5, 3]
         *
         * // using "_.pluck" callback shorthand
         * _.uniq([{ 'x': 1 }, { 'x': 2 }, { 'x': 1 }], 'x');
         * // => [{ 'x': 1 }, { 'x': 2 }]
         */
        function uniq(array, isSorted, callback, thisArg) {
            // juggle arguments
            if (typeof isSorted != 'boolean' && isSorted != null) {
                thisArg = callback;
                callback = !(thisArg && thisArg[isSorted] === array) ? isSorted : null;
                isSorted = false;
            }
            if (callback != null) {
                callback = lodash.createCallback(callback, thisArg, 3);
            }
            return baseUniq(array, isSorted, callback);
        }

        /**
         * Creates an array excluding all provided values using strict equality for
         * comparisons, i.e. `===`.
         *
         * @static
         * @memberOf _
         * @category Arrays
         * @param {Array} array The array to filter.
         * @param {...*} [value] The values to exclude.
         * @returns {Array} Returns a new array of filtered values.
         * @example
         *
         * _.without([1, 2, 1, 0, 3, 1, 4], 0, 1);
         * // => [2, 3, 4]
         */
        function without(array) {
            return difference(array, nativeSlice.call(arguments, 1));
        }

        /**
         * Creates an array of grouped elements, the first of which contains the first
         * elements of the given arrays, the second of which contains the second
         * elements of the given arrays, and so on.
         *
         * @static
         * @memberOf _
         * @alias unzip
         * @category Arrays
         * @param {...Array} [array] Arrays to process.
         * @returns {Array} Returns a new array of grouped elements.
         * @example
         *
         * _.zip(['moe', 'larry'], [30, 40], [true, false]);
         * // => [['moe', 30, true], ['larry', 40, false]]
         */
        function zip() {
            var array = arguments.length > 1 ? arguments : arguments[0],
                index = -1,
                length = array ? max(pluck(array, 'length')) : 0,
                result = Array(length < 0 ? 0 : length);

            while (++index < length) {
                result[index] = pluck(array, index);
            }
            return result;
        }

        /**
         * Creates an object composed from arrays of `keys` and `values`. Provide
         * either a single two dimensional array, i.e. `[[key1, value1], [key2, value2]]`
         * or two arrays, one of `keys` and one of corresponding `values`.
         *
         * @static
         * @memberOf _
         * @alias object
         * @category Arrays
         * @param {Array} keys The array of keys.
         * @param {Array} [values=[]] The array of values.
         * @returns {Object} Returns an object composed of the given keys and
         *  corresponding values.
         * @example
         *
         * _.zipObject(['moe', 'larry'], [30, 40]);
         * // => { 'moe': 30, 'larry': 40 }
         */
        function zipObject(keys, values) {
            var index = -1,
                length = keys ? keys.length : 0,
                result = {};

            while (++index < length) {
                var key = keys[index];
                if (values) {
                    result[key] = values[index];
                } else if (key) {
                    result[key[0]] = key[1];
                }
            }
            return result;
        }

        /*--------------------------------------------------------------------------*/

        /**
         * Creates a function that executes `func`, with  the `this` binding and
         * arguments of the created function, only after being called `n` times.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {number} n The number of times the function must be called before
         *  `func` is executed.
         * @param {Function} func The function to restrict.
         * @returns {Function} Returns the new restricted function.
         * @example
         *
         * var saves = ['profile', 'settings'];
         *
         * var done = _.after(saves.length, function() {
     *   console.log('Done saving!');
     * });
         *
         * _.forEach(saves, function(type) {
     *   asyncSave({ 'type': type, 'complete': done });
     * });
         * // => logs 'Done saving!', after all saves have completed
         */
        function after(n, func) {
            if (!isFunction(func)) {
                throw new TypeError;
            }
            return function () {
                if (--n < 1) {
                    return func.apply(this, arguments);
                }
            };
        }

        /**
         * Creates a function that, when called, invokes `func` with the `this`
         * binding of `thisArg` and prepends any additional `bind` arguments to those
         * provided to the bound function.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to bind.
         * @param {*} [thisArg] The `this` binding of `func`.
         * @param {...*} [arg] Arguments to be partially applied.
         * @returns {Function} Returns the new bound function.
         * @example
         *
         * var func = function(greeting) {
     *   return greeting + ' ' + this.name;
     * };
         *
         * func = _.bind(func, { 'name': 'moe' }, 'hi');
         * func();
         * // => 'hi moe'
         */
        function bind(func, thisArg) {
            return arguments.length > 2
                ? createBound(func, 17, nativeSlice.call(arguments, 2), null, thisArg)
                : createBound(func, 1, null, null, thisArg);
        }

        /**
         * Binds methods of an object to the object itself, overwriting the existing
         * method. Method names may be specified as individual arguments or as arrays
         * of method names. If no method names are provided all the function properties
         * of `object` will be bound.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Object} object The object to bind and assign the bound methods to.
         * @param {...string} [methodName] The object method names to
         *  bind, specified as individual method names or arrays of method names.
         * @returns {Object} Returns `object`.
         * @example
         *
         * var view = {
     *  'label': 'docs',
     *  'onClick': function() { console.log('clicked ' + this.label); }
     * };
         *
         * _.bindAll(view);
         * jQuery('#docs').on('click', view.onClick);
         * // => logs 'clicked docs', when the button is clicked
         */
        function bindAll(object) {
            var funcs = arguments.length > 1 ? baseFlatten(arguments, true, false, 1) : functions(object),
                index = -1,
                length = funcs.length;

            while (++index < length) {
                var key = funcs[index];
                object[key] = createBound(object[key], 1, null, null, object);
            }
            return object;
        }

        /**
         * Creates a function that, when called, invokes the method at `object[key]`
         * and prepends any additional `bindKey` arguments to those provided to the bound
         * function. This method differs from `_.bind` by allowing bound functions to
         * reference methods that will be redefined or don't yet exist.
         * See http://michaux.ca/articles/lazy-function-definition-pattern.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Object} object The object the method belongs to.
         * @param {string} key The key of the method.
         * @param {...*} [arg] Arguments to be partially applied.
         * @returns {Function} Returns the new bound function.
         * @example
         *
         * var object = {
     *   'name': 'moe',
     *   'greet': function(greeting) {
     *     return greeting + ' ' + this.name;
     *   }
     * };
         *
         * var func = _.bindKey(object, 'greet', 'hi');
         * func();
         * // => 'hi moe'
         *
         * object.greet = function(greeting) {
     *   return greeting + ', ' + this.name + '!';
     * };
         *
         * func();
         * // => 'hi, moe!'
         */
        function bindKey(object, key) {
            return arguments.length > 2
                ? createBound(key, 19, nativeSlice.call(arguments, 2), null, object)
                : createBound(key, 3, null, null, object);
        }

        /**
         * Creates a function that is the composition of the provided functions,
         * where each function consumes the return value of the function that follows.
         * For example, composing the functions `f()`, `g()`, and `h()` produces `f(g(h()))`.
         * Each function is executed with the `this` binding of the composed function.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {...Function} [func] Functions to compose.
         * @returns {Function} Returns the new composed function.
         * @example
         *
         * var realNameMap = {
     *   'curly': 'jerome'
     * };
         *
         * var format = function(name) {
     *   name = realNameMap[name.toLowerCase()] || name;
     *   return name.charAt(0).toUpperCase() + name.slice(1).toLowerCase();
     * };
         *
         * var greet = function(formatted) {
     *   return 'Hiya ' + formatted + '!';
     * };
         *
         * var welcome = _.compose(greet, format);
         * welcome('curly');
         * // => 'Hiya Jerome!'
         */
        function compose() {
            var funcs = arguments,
                length = funcs.length;

            while (length--) {
                if (!isFunction(funcs[length])) {
                    throw new TypeError;
                }
            }
            return function () {
                var args = arguments,
                    length = funcs.length;

                while (length--) {
                    args = [funcs[length].apply(this, args)];
                }
                return args[0];
            };
        }

        /**
         * Produces a callback bound to an optional `thisArg`. If `func` is a property
         * name the created callback will return the property value for a given element.
         * If `func` is an object the created callback will return `true` for elements
         * that contain the equivalent object properties, otherwise it will return `false`.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {*} [func=identity] The value to convert to a callback.
         * @param {*} [thisArg] The `this` binding of the created callback.
         * @param {number} [argCount] The number of arguments the callback accepts.
         * @returns {Function} Returns a callback function.
         * @example
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * // wrap to create custom callback shorthands
         * _.createCallback = _.wrap(_.createCallback, function(func, callback, thisArg) {
     *   var match = /^(.+?)__([gl]t)(.+)$/.exec(callback);
     *   return !match ? func(callback, thisArg) : function(object) {
     *     return match[2] == 'gt' ? object[match[1]] > match[3] : object[match[1]] < match[3];
     *   };
     * });
         *
         * _.filter(stooges, 'age__gt45');
         * // => [{ 'name': 'larry', 'age': 50 }]
         */
        function createCallback(func, thisArg, argCount) {
            var type = typeof func;
            if (func == null || type == 'function') {
                return baseCreateCallback(func, thisArg, argCount);
            }
            // handle "_.pluck" style callback shorthands
            if (type != 'object') {
                return function (object) {
                    return object[func];
                };
            }
            var props = keys(func),
                key = props[0],
                a = func[key];

            // handle "_.where" style callback shorthands
            if (props.length == 1 && a === a && !isObject(a)) {
                // fast path the common case of providing an object with a single
                // property containing a primitive value
                return function (object) {
                    var b = object[key];
                    return a === b && (a !== 0 || (1 / a == 1 / b));
                };
            }
            return function (object) {
                var length = props.length,
                    result = false;

                while (length--) {
                    if (!(result = baseIsEqual(object[props[length]], func[props[length]], null, true))) {
                        break;
                    }
                }
                return result;
            };
        }

        /**
         * Creates a function which accepts one or more arguments of `func` that when
         * invoked either executes `func` returning its result, if all `func` arguments
         * have been provided, or returns a function that accepts one or more of the
         * remaining `func` arguments, and so on. The arity of `func` can be specified
         * if `func.length` is not sufficient.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to curry.
         * @param {number} [arity=func.length] The arity of `func`.
         * @returns {Function} Returns the new curried function.
         * @example
         *
         * var curried = _.curry(function(a, b, c) {
     *   console.log(a + b + c);
     * });
         *
         * curried(1)(2)(3);
         * // => 6
         *
         * curried(1, 2)(3);
         * // => 6
         *
         * curried(1, 2, 3);
         * // => 6
         */
        function curry(func, arity) {
            arity = typeof arity == 'number' ? arity : (+arity || func.length);
            return createBound(func, 4, null, null, null, arity);
        }

        /**
         * Creates a function that will delay the execution of `func` until after
         * `wait` milliseconds have elapsed since the last time it was invoked.
         * Provide an options object to indicate that `func` should be invoked on
         * the leading and/or trailing edge of the `wait` timeout. Subsequent calls
         * to the debounced function will return the result of the last `func` call.
         *
         * Note: If `leading` and `trailing` options are `true` `func` will be called
         * on the trailing edge of the timeout only if the the debounced function is
         * invoked more than once during the `wait` timeout.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to debounce.
         * @param {number} wait The number of milliseconds to delay.
         * @param {Object} [options] The options object.
         * @param {boolean} [options.leading=false] Specify execution on the leading edge of the timeout.
         * @param {number} [options.maxWait] The maximum time `func` is allowed to be delayed before it's called.
         * @param {boolean} [options.trailing=true] Specify execution on the trailing edge of the timeout.
         * @returns {Function} Returns the new debounced function.
         * @example
         *
         * // avoid costly calculations while the window size is in flux
         * var lazyLayout = _.debounce(calculateLayout, 150);
         * jQuery(window).on('resize', lazyLayout);
         *
         * // execute `sendMail` when the click event is fired, debouncing subsequent calls
         * jQuery('#postbox').on('click', _.debounce(sendMail, 300, {
     *   'leading': true,
     *   'trailing': false
     * });
         *
         * // ensure `batchLog` is executed once after 1 second of debounced calls
         * var source = new EventSource('/stream');
         * source.addEventListener('message', _.debounce(batchLog, 250, {
     *   'maxWait': 1000
     * }, false);
         */
        function debounce(func, wait, options) {
            var args,
                maxTimeoutId,
                result,
                stamp,
                thisArg,
                timeoutId,
                trailingCall,
                lastCalled = 0,
                maxWait = false,
                trailing = true;

            if (!isFunction(func)) {
                throw new TypeError;
            }
            wait = nativeMax(0, wait) || 0;
            if (options === true) {
                var leading = true;
                trailing = false;
            } else if (isObject(options)) {
                leading = options.leading;
                maxWait = 'maxWait' in options && (nativeMax(wait, options.maxWait) || 0);
                trailing = 'trailing' in options ? options.trailing : trailing;
            }
            var delayed = function () {
                var remaining = wait - (now() - stamp);
                if (remaining <= 0) {
                    if (maxTimeoutId) {
                        clearTimeout(maxTimeoutId);
                    }
                    var isCalled = trailingCall;
                    maxTimeoutId = timeoutId = trailingCall = undefined;
                    if (isCalled) {
                        lastCalled = now();
                        result = func.apply(thisArg, args);
                    }
                } else {
                    timeoutId = setTimeout(delayed, remaining);
                }
            };

            var maxDelayed = function () {
                if (timeoutId) {
                    clearTimeout(timeoutId);
                }
                maxTimeoutId = timeoutId = trailingCall = undefined;
                if (trailing || (maxWait !== wait)) {
                    lastCalled = now();
                    result = func.apply(thisArg, args);
                }
            };

            return function () {
                args = arguments;
                stamp = now();
                thisArg = this;
                trailingCall = trailing && (timeoutId || !leading);

                if (maxWait === false) {
                    var leadingCall = leading && !timeoutId;
                } else {
                    if (!maxTimeoutId && !leading) {
                        lastCalled = stamp;
                    }
                    var remaining = maxWait - (stamp - lastCalled);
                    if (remaining <= 0) {
                        if (maxTimeoutId) {
                            maxTimeoutId = clearTimeout(maxTimeoutId);
                        }
                        lastCalled = stamp;
                        result = func.apply(thisArg, args);
                    }
                    else if (!maxTimeoutId) {
                        maxTimeoutId = setTimeout(maxDelayed, remaining);
                    }
                }
                if (!timeoutId && wait !== maxWait) {
                    timeoutId = setTimeout(delayed, wait);
                }
                if (leadingCall) {
                    result = func.apply(thisArg, args);
                }
                return result;
            };
        }

        /**
         * Defers executing the `func` function until the current call stack has cleared.
         * Additional arguments will be provided to `func` when it is invoked.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to defer.
         * @param {...*} [arg] Arguments to invoke the function with.
         * @returns {number} Returns the timer id.
         * @example
         *
         * _.defer(function() { console.log('deferred'); });
         * // returns from the function before 'deferred' is logged
         */
        function defer(func) {
            if (!isFunction(func)) {
                throw new TypeError;
            }
            var args = nativeSlice.call(arguments, 1);
            return setTimeout(function () { func.apply(undefined, args); }, 1);
        }

        // use `setImmediate` if available in Node.js
        if (isV8 && moduleExports && typeof setImmediate == 'function') {
            defer = function (func) {
                if (!isFunction(func)) {
                    throw new TypeError;
                }
                return setImmediate.apply(context, arguments);
            };
        }

        /**
         * Executes the `func` function after `wait` milliseconds. Additional arguments
         * will be provided to `func` when it is invoked.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to delay.
         * @param {number} wait The number of milliseconds to delay execution.
         * @param {...*} [arg] Arguments to invoke the function with.
         * @returns {number} Returns the timer id.
         * @example
         *
         * var log = _.bind(console.log, console);
         * _.delay(log, 1000, 'logged later');
         * // => 'logged later' (Appears after one second.)
         */
        function delay(func, wait) {
            if (!isFunction(func)) {
                throw new TypeError;
            }
            var args = nativeSlice.call(arguments, 2);
            return setTimeout(function () { func.apply(undefined, args); }, wait);
        }

        /**
         * Creates a function that memoizes the result of `func`. If `resolver` is
         * provided it will be used to determine the cache key for storing the result
         * based on the arguments provided to the memoized function. By default, the
         * first argument provided to the memoized function is used as the cache key.
         * The `func` is executed with the `this` binding of the memoized function.
         * The result cache is exposed as the `cache` property on the memoized function.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to have its output memoized.
         * @param {Function} [resolver] A function used to resolve the cache key.
         * @returns {Function} Returns the new memoizing function.
         * @example
         *
         * var fibonacci = _.memoize(function(n) {
     *   return n < 2 ? n : fibonacci(n - 1) + fibonacci(n - 2);
     * });
         *
         * var data = {
     *   'moe': { 'name': 'moe', 'age': 40 },
     *   'curly': { 'name': 'curly', 'age': 60 }
     * };
         *
         * // modifying the result cache
         * var stooge = _.memoize(function(name) { return data[name]; }, _.identity);
         * stooge('curly');
         * // => { 'name': 'curly', 'age': 60 }
         *
         * stooge.cache.curly.name = 'jerome';
         * stooge('curly');
         * // => { 'name': 'jerome', 'age': 60 }
         */
        function memoize(func, resolver) {
            if (!isFunction(func)) {
                throw new TypeError;
            }
            var memoized = function () {
                var cache = memoized.cache,
                    key = resolver ? resolver.apply(this, arguments) : keyPrefix + arguments[0];

                return hasOwnProperty.call(cache, key)
                    ? cache[key]
                    : (cache[key] = func.apply(this, arguments));
            }
            memoized.cache = {};
            return memoized;
        }

        /**
         * Creates a function that is restricted to execute `func` once. Repeat calls to
         * the function will return the value of the first call. The `func` is executed
         * with the `this` binding of the created function.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to restrict.
         * @returns {Function} Returns the new restricted function.
         * @example
         *
         * var initialize = _.once(createApplication);
         * initialize();
         * initialize();
         * // `initialize` executes `createApplication` once
         */
        function once(func) {
            var ran,
                result;

            if (!isFunction(func)) {
                throw new TypeError;
            }
            return function () {
                if (ran) {
                    return result;
                }
                ran = true;
                result = func.apply(this, arguments);

                // clear the `func` variable so the function may be garbage collected
                func = null;
                return result;
            };
        }

        /**
         * Creates a function that, when called, invokes `func` with any additional
         * `partial` arguments prepended to those provided to the new function. This
         * method is similar to `_.bind` except it does **not** alter the `this` binding.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to partially apply arguments to.
         * @param {...*} [arg] Arguments to be partially applied.
         * @returns {Function} Returns the new partially applied function.
         * @example
         *
         * var greet = function(greeting, name) { return greeting + ' ' + name; };
         * var hi = _.partial(greet, 'hi');
         * hi('moe');
         * // => 'hi moe'
         */
        function partial(func) {
            return createBound(func, 16, nativeSlice.call(arguments, 1));
        }

        /**
         * This method is like `_.partial` except that `partial` arguments are
         * appended to those provided to the new function.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to partially apply arguments to.
         * @param {...*} [arg] Arguments to be partially applied.
         * @returns {Function} Returns the new partially applied function.
         * @example
         *
         * var defaultsDeep = _.partialRight(_.merge, _.defaults);
         *
         * var options = {
     *   'variable': 'data',
     *   'imports': { 'jq': $ }
     * };
         *
         * defaultsDeep(options, _.templateSettings);
         *
         * options.variable
         * // => 'data'
         *
         * options.imports
         * // => { '_': _, 'jq': $ }
         */
        function partialRight(func) {
            return createBound(func, 32, null, nativeSlice.call(arguments, 1));
        }

        /**
         * Creates a function that, when executed, will only call the `func` function
         * at most once per every `wait` milliseconds. Provide an options object to
         * indicate that `func` should be invoked on the leading and/or trailing edge
         * of the `wait` timeout. Subsequent calls to the throttled function will
         * return the result of the last `func` call.
         *
         * Note: If `leading` and `trailing` options are `true` `func` will be called
         * on the trailing edge of the timeout only if the the throttled function is
         * invoked more than once during the `wait` timeout.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {Function} func The function to throttle.
         * @param {number} wait The number of milliseconds to throttle executions to.
         * @param {Object} [options] The options object.
         * @param {boolean} [options.leading=true] Specify execution on the leading edge of the timeout.
         * @param {boolean} [options.trailing=true] Specify execution on the trailing edge of the timeout.
         * @returns {Function} Returns the new throttled function.
         * @example
         *
         * // avoid excessively updating the position while scrolling
         * var throttled = _.throttle(updatePosition, 100);
         * jQuery(window).on('scroll', throttled);
         *
         * // execute `renewToken` when the click event is fired, but not more than once every 5 minutes
         * jQuery('.interactive').on('click', _.throttle(renewToken, 300000, {
     *   'trailing': false
     * }));
         */
        function throttle(func, wait, options) {
            var leading = true,
                trailing = true;

            if (!isFunction(func)) {
                throw new TypeError;
            }
            if (options === false) {
                leading = false;
            } else if (isObject(options)) {
                leading = 'leading' in options ? options.leading : leading;
                trailing = 'trailing' in options ? options.trailing : trailing;
            }
            debounceOptions.leading = leading;
            debounceOptions.maxWait = wait;
            debounceOptions.trailing = trailing;

            var result = debounce(func, wait, debounceOptions);
            return result;
        }

        /**
         * Creates a function that provides `value` to the wrapper function as its
         * first argument. Additional arguments provided to the function are appended
         * to those provided to the wrapper function. The wrapper is executed with
         * the `this` binding of the created function.
         *
         * @static
         * @memberOf _
         * @category Functions
         * @param {*} value The value to wrap.
         * @param {Function} wrapper The wrapper function.
         * @returns {Function} Returns the new function.
         * @example
         *
         * var hello = function(name) { return 'hello ' + name; };
         * hello = _.wrap(hello, function(func) {
     *   return 'before, ' + func('moe') + ', after';
     * });
         * hello();
         * // => 'before, hello moe, after'
         */
        function wrap(value, wrapper) {
            if (!isFunction(wrapper)) {
                throw new TypeError;
            }
            return function () {
                var args = [value];
                push.apply(args, arguments);
                return wrapper.apply(this, args);
            };
        }

        /*--------------------------------------------------------------------------*/

        /**
         * Converts the characters `&`, `<`, `>`, `"`, and `'` in `string` to their
         * corresponding HTML entities.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {string} string The string to escape.
         * @returns {string} Returns the escaped string.
         * @example
         *
         * _.escape('Moe, Larry & Curly');
         * // => 'Moe, Larry &amp; Curly'
         */
        function escape(string) {
            return string == null ? '' : String(string).replace(reUnescapedHtml, escapeHtmlChar);
        }

        /**
         * This method returns the first argument provided to it.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {*} value Any value.
         * @returns {*} Returns `value`.
         * @example
         *
         * var moe = { 'name': 'moe' };
         * moe === _.identity(moe);
         * // => true
         */
        function identity(value) {
            return value;
        }

        /**
         * Adds function properties of a source object to the `lodash` function and
         * chainable wrapper.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {Object} object The object of function properties to add to `lodash`.
         * @param {Object} object The object of function properties to add to `lodash`.
         * @example
         *
         * _.mixin({
     *   'capitalize': function(string) {
     *     return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
     *   }
     * });
         *
         * _.capitalize('moe');
         * // => 'Moe'
         *
         * _('moe').capitalize();
         * // => 'Moe'
         */
        function mixin(object, source) {
            var ctor = object,
                isFunc = !source || isFunction(ctor);

            if (!source) {
                ctor = lodashWrapper;
                source = object;
                object = lodash;
            }
            forEach(functions(source), function (methodName) {
                var func = object[methodName] = source[methodName];
                if (isFunc) {
                    ctor.prototype[methodName] = function () {
                        var value = this.__wrapped__,
                            args = [value];

                        push.apply(args, arguments);
                        var result = func.apply(object, args);
                        if (value && typeof value == 'object' && value === result) {
                            return this;
                        }
                        result = new ctor(result);
                        result.__chain__ = this.__chain__;
                        return result;
                    };
                }
            });
        }

        /**
         * Reverts the '_' variable to its previous value and returns a reference to
         * the `lodash` function.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @returns {Function} Returns the `lodash` function.
         * @example
         *
         * var lodash = _.noConflict();
         */
        function noConflict() {
            context._ = oldDash;
            return this;
        }

        /**
         * Converts the given value into an integer of the specified radix.
         * If `radix` is `undefined` or `0` a `radix` of `10` is used unless the
         * `value` is a hexadecimal, in which case a `radix` of `16` is used.
         *
         * Note: This method avoids differences in native ES3 and ES5 `parseInt`
         * implementations. See http://es5.github.io/#E.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {string} value The value to parse.
         * @param {number} [radix] The radix used to interpret the value to parse.
         * @returns {number} Returns the new integer value.
         * @example
         *
         * _.parseInt('08');
         * // => 8
         */
        var parseInt = nativeParseInt(whitespace + '08') == 8 ? nativeParseInt : function (value, radix) {
            // Firefox and Opera still follow the ES3 specified implementation of `parseInt`
            return nativeParseInt(isString(value) ? value.replace(reLeadingSpacesAndZeros, '') : value, radix || 0);
        };

        /**
         * Produces a random number between `min` and `max` (inclusive). If only one
         * argument is provided a number between `0` and the given number will be
         * returned. If `floating` is truey or either `min` or `max` are floats a
         * floating-point number will be returned instead of an integer.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {number} [min=0] The minimum possible value.
         * @param {number} [max=1] The maximum possible value.
         * @param {boolean} [floating=false] Specify returning a floating-point number.
         * @returns {number} Returns a random number.
         * @example
         *
         * _.random(0, 5);
         * // => an integer between 0 and 5
         *
         * _.random(5);
         * // => also an integer between 0 and 5
         *
         * _.random(5, true);
         * // => a floating-point number between 0 and 5
         *
         * _.random(1.2, 5.2);
         * // => a floating-point number between 1.2 and 5.2
         */
        function random(min, max, floating) {
            var noMin = min == null,
                noMax = max == null;

            if (floating == null) {
                if (typeof min == 'boolean' && noMax) {
                    floating = min;
                    min = 1;
                }
                else if (!noMax && typeof max == 'boolean') {
                    floating = max;
                    noMax = true;
                }
            }
            if (noMin && noMax) {
                max = 1;
            }
            min = +min || 0;
            if (noMax) {
                max = min;
                min = 0;
            } else {
                max = +max || 0;
            }
            var rand = nativeRandom();
            return (floating || min % 1 || max % 1)
                ? nativeMin(min + (rand * (max - min + parseFloat('1e-' + ((rand + '').length - 1)))), max)
                : min + floor(rand * (max - min + 1));
        }

        /**
         * Resolves the value of `property` on `object`. If `property` is a function
         * it will be invoked with the `this` binding of `object` and its result returned,
         * else the property value is returned. If `object` is falsey then `undefined`
         * is returned.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {Object} object The object to inspect.
         * @param {string} property The property to get the value of.
         * @returns {*} Returns the resolved value.
         * @example
         *
         * var object = {
     *   'cheese': 'crumpets',
     *   'stuff': function() {
     *     return 'nonsense';
     *   }
     * };
         *
         * _.result(object, 'cheese');
         * // => 'crumpets'
         *
         * _.result(object, 'stuff');
         * // => 'nonsense'
         */
        function result(object, property) {
            if (object) {
                var value = object[property];
                return isFunction(value) ? object[property]() : value;
            }
        }

        /**
         * A micro-templating method that handles arbitrary delimiters, preserves
         * whitespace, and correctly escapes quotes within interpolated code.
         *
         * Note: In the development build, `_.template` utilizes sourceURLs for easier
         * debugging. See http://www.html5rocks.com/en/tutorials/developertools/sourcemaps/#toc-sourceurl
         *
         * For more information on precompiling templates see:
         * http://lodash.com/#custom-builds
         *
         * For more information on Chrome extension sandboxes see:
         * http://developer.chrome.com/stable/extensions/sandboxingEval.html
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {string} text The template text.
         * @param {Object} data The data object used to populate the text.
         * @param {Object} [options] The options object.
         * @param {RegExp} [options.escape] The "escape" delimiter.
         * @param {RegExp} [options.evaluate] The "evaluate" delimiter.
         * @param {Object} [options.imports] An object to import into the template as local variables.
         * @param {RegExp} [options.interpolate] The "interpolate" delimiter.
         * @param {string} [sourceURL] The sourceURL of the template's compiled source.
         * @param {string} [variable] The data object variable name.
         * @returns {Function|string} Returns a compiled function when no `data` object
         *  is given, else it returns the interpolated text.
         * @example
         *
         * // using the "interpolate" delimiter to create a compiled template
         * var compiled = _.template('hello <%= name %>');
         * compiled({ 'name': 'moe' });
         * // => 'hello moe'
         *
         * // using the "escape" delimiter to escape HTML in data property values
         * _.template('<b><%- value %></b>', { 'value': '<script>' });
         * // => '<b>&lt;script&gt;</b>'
         *
         * // using the "evaluate" delimiter to generate HTML
         * var list = '<% _.forEach(people, function(name) { %><li><%- name %></li><% }); %>';
         * _.template(list, { 'people': ['moe', 'larry'] });
         * // => '<li>moe</li><li>larry</li>'
         *
         * // using the ES6 delimiter as an alternative to the default "interpolate" delimiter
         * _.template('hello ${ name }', { 'name': 'curly' });
         * // => 'hello curly'
         *
         * // using the internal `print` function in "evaluate" delimiters
         * _.template('<% print("hello " + name); %>!', { 'name': 'larry' });
         * // => 'hello larry!'
         *
         * // using a custom template delimiters
         * _.templateSettings = {
     *   'interpolate': /{{([\s\S]+?)}}/g
     * };
         *
         * _.template('hello {{ name }}!', { 'name': 'mustache' });
         * // => 'hello mustache!'
         *
         * // using the `imports` option to import jQuery
         * var list = '<% $.each(people, function(name) { %><li><%- name %></li><% }); %>';
         * _.template(list, { 'people': ['moe', 'larry'] }, { 'imports': { '$': jQuery } });
         * // => '<li>moe</li><li>larry</li>'
         *
         * // using the `sourceURL` option to specify a custom sourceURL for the template
         * var compiled = _.template('hello <%= name %>', null, { 'sourceURL': '/basic/greeting.jst' });
         * compiled(data);
         * // => find the source of "greeting.jst" under the Sources tab or Resources panel of the web inspector
         *
         * // using the `variable` option to ensure a with-statement isn't used in the compiled template
         * var compiled = _.template('hi <%= data.name %>!', null, { 'variable': 'data' });
         * compiled.source;
         * // => function(data) {
     *   var __t, __p = '', __e = _.escape;
     *   __p += 'hi ' + ((__t = ( data.name )) == null ? '' : __t) + '!';
     *   return __p;
     * }
         *
         * // using the `source` property to inline compiled templates for meaningful
         * // line numbers in error messages and a stack trace
         * fs.writeFileSync(path.join(cwd, 'jst.js'), '\
         *   var JST = {\
     *     "main": ' + _.template(mainText).source + '\
     *   };\
         * ');
         */
        function template(text, data, options) {
            // based on John Resig's `tmpl` implementation
            // http://ejohn.org/blog/javascript-micro-templating/
            // and Laura Doktorova's doT.js
            // https://github.com/olado/doT
            var settings = lodash.templateSettings;
            text || (text = '');

            // avoid missing dependencies when `iteratorTemplate` is not defined
            options = defaults({}, options, settings);

            var imports = defaults({}, options.imports, settings.imports),
                importsKeys = keys(imports),
                importsValues = values(imports);

            var isEvaluating,
                index = 0,
                interpolate = options.interpolate || reNoMatch,
                source = "__p += '";

            // compile the regexp to match each delimiter
            var reDelimiters = RegExp(
                (options.escape || reNoMatch).source + '|' +
                interpolate.source + '|' +
                (interpolate === reInterpolate ? reEsTemplate : reNoMatch).source + '|' +
                (options.evaluate || reNoMatch).source + '|$'
                , 'g');

            text.replace(reDelimiters, function (match, escapeValue, interpolateValue, esTemplateValue, evaluateValue, offset) {
                interpolateValue || (interpolateValue = esTemplateValue);

                // escape characters that cannot be included in string literals
                source += text.slice(index, offset).replace(reUnescapedString, escapeStringChar);

                // replace delimiters with snippets
                if (escapeValue) {
                    source += "' +\n__e(" + escapeValue + ") +\n'";
                }
                if (evaluateValue) {
                    isEvaluating = true;
                    source += "';\n" + evaluateValue + ";\n__p += '";
                }
                if (interpolateValue) {
                    source += "' +\n((__t = (" + interpolateValue + ")) == null ? '' : __t) +\n'";
                }
                index = offset + match.length;

                // the JS engine embedded in Adobe products requires returning the `match`
                // string in order to produce the correct `offset` value
                return match;
            });

            source += "';\n";

            // if `variable` is not specified, wrap a with-statement around the generated
            // code to add the data object to the top of the scope chain
            var variable = options.variable,
                hasVariable = variable;

            if (!hasVariable) {
                variable = 'obj';
                source = 'with (' + variable + ') {\n' + source + '\n}\n';
            }
            // cleanup code by stripping empty strings
            source = (isEvaluating ? source.replace(reEmptyStringLeading, '') : source)
                .replace(reEmptyStringMiddle, '$1')
                .replace(reEmptyStringTrailing, '$1;');

            // frame code as the function body
            source = 'function(' + variable + ') {\n' +
                (hasVariable ? '' : variable + ' || (' + variable + ' = {});\n') +
                "var __t, __p = '', __e = _.escape" +
                (isEvaluating
                    ? ', __j = Array.prototype.join;\n' +
                "function print() { __p += __j.call(arguments, '') }\n"
                    : ';\n'
                ) +
                source +
                'return __p\n}';

            // Use a sourceURL for easier debugging.
            // http://www.html5rocks.com/en/tutorials/developertools/sourcemaps/#toc-sourceurl
            var sourceURL = '\n/*\n//# sourceURL=' + (options.sourceURL || '/lodash/template/source[' + (templateCounter++) + ']') + '\n*/';

            try {
                var result = Function(importsKeys, 'return ' + source + sourceURL).apply(undefined, importsValues);
            } catch (e) {
                e.source = source;
                throw e;
            }
            if (data) {
                return result(data);
            }
            // provide the compiled function's source by its `toString` method, in
            // supported environments, or the `source` property as a convenience for
            // inlining compiled templates during the build process
            result.source = source;
            return result;
        }

        /**
         * Executes the callback `n` times, returning an array of the results
         * of each callback execution. The callback is bound to `thisArg` and invoked
         * with one argument; (index).
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {number} n The number of times to execute the callback.
         * @param {Function} callback The function called per iteration.
         * @param {*} [thisArg] The `this` binding of `callback`.
         * @returns {Array} Returns an array of the results of each `callback` execution.
         * @example
         *
         * var diceRolls = _.times(3, _.partial(_.random, 1, 6));
         * // => [3, 6, 4]
         *
         * _.times(3, function(n) { mage.castSpell(n); });
         * // => calls `mage.castSpell(n)` three times, passing `n` of `0`, `1`, and `2` respectively
         *
         * _.times(3, function(n) { this.cast(n); }, mage);
         * // => also calls `mage.castSpell(n)` three times
         */
        function times(n, callback, thisArg) {
            n = (n = +n) > -1 ? n : 0;
            var index = -1,
                result = Array(n);

            callback = baseCreateCallback(callback, thisArg, 1);
            while (++index < n) {
                result[index] = callback(index);
            }
            return result;
        }

        /**
         * The inverse of `_.escape` this method converts the HTML entities
         * `&amp;`, `&lt;`, `&gt;`, `&quot;`, and `&#39;` in `string` to their
         * corresponding characters.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {string} string The string to unescape.
         * @returns {string} Returns the unescaped string.
         * @example
         *
         * _.unescape('Moe, Larry &amp; Curly');
         * // => 'Moe, Larry & Curly'
         */
        function unescape(string) {
            return string == null ? '' : String(string).replace(reEscapedHtml, unescapeHtmlChar);
        }

        /**
         * Generates a unique ID. If `prefix` is provided the ID will be appended to it.
         *
         * @static
         * @memberOf _
         * @category Utilities
         * @param {string} [prefix] The value to prefix the ID with.
         * @returns {string} Returns the unique ID.
         * @example
         *
         * _.uniqueId('contact_');
         * // => 'contact_104'
         *
         * _.uniqueId();
         * // => '105'
         */
        function uniqueId(prefix) {
            var id = ++idCounter;
            return String(prefix == null ? '' : prefix) + id;
        }

        /*--------------------------------------------------------------------------*/

        /**
         * Creates a `lodash` object that wraps the given value with explicit
         * method chaining enabled.
         *
         * @static
         * @memberOf _
         * @category Chaining
         * @param {*} value The value to wrap.
         * @returns {Object} Returns the wrapper object.
         * @example
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 },
         *   { 'name': 'curly', 'age': 60 }
         * ];
         *
         * var youngest = _.chain(stooges)
         *     .sortBy('age')
         *     .map(function(stooge) { return stooge.name + ' is ' + stooge.age; })
         *     .first()
         *     .value();
         * // => 'moe is 40'
         */
        function chain(value) {
            value = new lodashWrapper(value);
            value.__chain__ = true;
            return value;
        }

        /**
         * Invokes `interceptor` with the `value` as the first argument and then
         * returns `value`. The purpose of this method is to "tap into" a method
         * chain in order to perform operations on intermediate results within
         * the chain.
         *
         * @static
         * @memberOf _
         * @category Chaining
         * @param {*} value The value to provide to `interceptor`.
         * @param {Function} interceptor The function to invoke.
         * @returns {*} Returns `value`.
         * @example
         *
         * _([1, 2, 3, 4])
         *  .filter(function(num) { return num % 2 == 0; })
         *  .tap(function(array) { console.log(array); })
         *  .map(function(num) { return num * num; })
         *  .value();
         * // => // [2, 4] (logged)
         * // => [4, 16]
         */
        function tap(value, interceptor) {
            interceptor(value);
            return value;
        }

        /**
         * Enables explicit method chaining on the wrapper object.
         *
         * @name chain
         * @memberOf _
         * @category Chaining
         * @returns {*} Returns the wrapper object.
         * @example
         *
         * var stooges = [
         *   { 'name': 'moe', 'age': 40 },
         *   { 'name': 'larry', 'age': 50 }
         * ];
         *
         * // without explicit chaining
         * _(stooges).first();
         * // => { 'name': 'moe', 'age': 40 }
         *
         * // with explicit chaining
         * _(stooges).chain()
         *   .first()
         *   .pick('age')
         *   .value()
         * // => { 'age': 40 }
         */
        function wrapperChain() {
            this.__chain__ = true;
            return this;
        }

        /**
         * Produces the `toString` result of the wrapped value.
         *
         * @name toString
         * @memberOf _
         * @category Chaining
         * @returns {string} Returns the string result.
         * @example
         *
         * _([1, 2, 3]).toString();
         * // => '1,2,3'
         */
        function wrapperToString() {
            return String(this.__wrapped__);
        }

        /**
         * Extracts the wrapped value.
         *
         * @name valueOf
         * @memberOf _
         * @alias value
         * @category Chaining
         * @returns {*} Returns the wrapped value.
         * @example
         *
         * _([1, 2, 3]).valueOf();
         * // => [1, 2, 3]
         */
        function wrapperValueOf() {
            return this.__wrapped__;
        }

        /*--------------------------------------------------------------------------*/

        // add functions that return wrapped values when chaining
        lodash.after = after;
        lodash.assign = assign;
        lodash.at = at;
        lodash.bind = bind;
        lodash.bindAll = bindAll;
        lodash.bindKey = bindKey;
        lodash.chain = chain;
        lodash.compact = compact;
        lodash.compose = compose;
        lodash.countBy = countBy;
        lodash.createCallback = createCallback;
        lodash.curry = curry;
        lodash.debounce = debounce;
        lodash.defaults = defaults;
        lodash.defer = defer;
        lodash.delay = delay;
        lodash.difference = difference;
        lodash.filter = filter;
        lodash.flatten = flatten;
        lodash.forEach = forEach;
        lodash.forEachRight = forEachRight;
        lodash.forIn = forIn;
        lodash.forInRight = forInRight;
        lodash.forOwn = forOwn;
        lodash.forOwnRight = forOwnRight;
        lodash.functions = functions;
        lodash.groupBy = groupBy;
        lodash.indexBy = indexBy;
        lodash.initial = initial;
        lodash.intersection = intersection;
        lodash.invert = invert;
        lodash.invoke = invoke;
        lodash.keys = keys;
        lodash.map = map;
        lodash.max = max;
        lodash.memoize = memoize;
        lodash.merge = merge;
        lodash.min = min;
        lodash.omit = omit;
        lodash.once = once;
        lodash.pairs = pairs;
        lodash.partial = partial;
        lodash.partialRight = partialRight;
        lodash.pick = pick;
        lodash.pluck = pluck;
        lodash.pull = pull;
        lodash.range = range;
        lodash.reject = reject;
        lodash.remove = remove;
        lodash.rest = rest;
        lodash.shuffle = shuffle;
        lodash.sortBy = sortBy;
        lodash.tap = tap;
        lodash.throttle = throttle;
        lodash.times = times;
        lodash.toArray = toArray;
        lodash.transform = transform;
        lodash.union = union;
        lodash.uniq = uniq;
        lodash.values = values;
        lodash.where = where;
        lodash.without = without;
        lodash.wrap = wrap;
        lodash.zip = zip;
        lodash.zipObject = zipObject;

        // add aliases
        lodash.collect = map;
        lodash.drop = rest;
        lodash.each = forEach;
        lodash.eachRight = forEachRight;
        lodash.extend = assign;
        lodash.methods = functions;
        lodash.object = zipObject;
        lodash.select = filter;
        lodash.tail = rest;
        lodash.unique = uniq;
        lodash.unzip = zip;

        // add functions to `lodash.prototype`
        mixin(lodash);

        /*--------------------------------------------------------------------------*/

        // add functions that return unwrapped values when chaining
        lodash.clone = clone;
        lodash.cloneDeep = cloneDeep;
        lodash.contains = contains;
        lodash.escape = escape;
        lodash.every = every;
        lodash.find = find;
        lodash.findIndex = findIndex;
        lodash.findKey = findKey;
        lodash.findLast = findLast;
        lodash.findLastIndex = findLastIndex;
        lodash.findLastKey = findLastKey;
        lodash.has = has;
        lodash.identity = identity;
        lodash.indexOf = indexOf;
        lodash.isArguments = isArguments;
        lodash.isArray = isArray;
        lodash.isBoolean = isBoolean;
        lodash.isDate = isDate;
        lodash.isElement = isElement;
        lodash.isEmpty = isEmpty;
        lodash.isEqual = isEqual;
        lodash.isFinite = isFinite;
        lodash.isFunction = isFunction;
        lodash.isNaN = isNaN;
        lodash.isNull = isNull;
        lodash.isNumber = isNumber;
        lodash.isObject = isObject;
        lodash.isPlainObject = isPlainObject;
        lodash.isRegExp = isRegExp;
        lodash.isString = isString;
        lodash.isUndefined = isUndefined;
        lodash.lastIndexOf = lastIndexOf;
        lodash.mixin = mixin;
        lodash.noConflict = noConflict;
        lodash.parseInt = parseInt;
        lodash.random = random;
        lodash.reduce = reduce;
        lodash.reduceRight = reduceRight;
        lodash.result = result;
        lodash.runInContext = runInContext;
        lodash.size = size;
        lodash.some = some;
        lodash.sortedIndex = sortedIndex;
        lodash.template = template;
        lodash.unescape = unescape;
        lodash.uniqueId = uniqueId;

        // add aliases
        lodash.all = every;
        lodash.any = some;
        lodash.detect = find;
        lodash.findWhere = find;
        lodash.foldl = reduce;
        lodash.foldr = reduceRight;
        lodash.include = contains;
        lodash.inject = reduce;

        forOwn(lodash, function (func, methodName) {
            if (!lodash.prototype[methodName]) {
                lodash.prototype[methodName] = function () {
                    var args = [this.__wrapped__],
                        chainAll = this.__chain__;

                    push.apply(args, arguments);
                    var result = func.apply(lodash, args);
                    return chainAll
                        ? new lodashWrapper(result, chainAll)
                        : result;
                };
            }
        });

        /*--------------------------------------------------------------------------*/

        // add functions capable of returning wrapped and unwrapped values when chaining
        lodash.first = first;
        lodash.last = last;
        lodash.sample = sample;

        // add aliases
        lodash.take = first;
        lodash.head = first;

        forOwn(lodash, function (func, methodName) {
            var callbackable = methodName !== 'sample';
            if (!lodash.prototype[methodName]) {
                lodash.prototype[methodName] = function (n, guard) {
                    var chainAll = this.__chain__,
                        result = func(this.__wrapped__, n, guard);

                    return !chainAll && (n == null || (guard && !(callbackable && typeof n == 'function')))
                        ? result
                        : new lodashWrapper(result, chainAll);
                };
            }
        });

        /*--------------------------------------------------------------------------*/

        /**
         * The semantic version number.
         *
         * @static
         * @memberOf _
         * @type string
         */
        lodash.VERSION = '2.2.1';

        // add "Chaining" functions to the wrapper
        lodash.prototype.chain = wrapperChain;
        lodash.prototype.toString = wrapperToString;
        lodash.prototype.value = wrapperValueOf;
        lodash.prototype.valueOf = wrapperValueOf;

        // add `Array` functions that return unwrapped values
        forEach(['join', 'pop', 'shift'], function (methodName) {
            var func = arrayRef[methodName];
            lodash.prototype[methodName] = function () {
                var chainAll = this.__chain__,
                    result = func.apply(this.__wrapped__, arguments);

                return chainAll
                    ? new lodashWrapper(result, chainAll)
                    : result;
            };
        });

        // add `Array` functions that return the wrapped value
        forEach(['push', 'reverse', 'sort', 'unshift'], function (methodName) {
            var func = arrayRef[methodName];
            lodash.prototype[methodName] = function () {
                func.apply(this.__wrapped__, arguments);
                return this;
            };
        });

        // add `Array` functions that return new wrapped values
        forEach(['concat', 'slice', 'splice'], function (methodName) {
            var func = arrayRef[methodName];
            lodash.prototype[methodName] = function () {
                return new lodashWrapper(func.apply(this.__wrapped__, arguments), this.__chain__);
            };
        });

        return lodash;
    }

    /*--------------------------------------------------------------------------*/

    // expose Lo-Dash
    var _ = runInContext();

    // some AMD build optimizers, like r.js, check for condition patterns like the following:
    if (typeof define == 'function' && typeof define.amd == 'object' && define.amd) {
        // Expose Lo-Dash to the global object even when an AMD loader is present in
        // case Lo-Dash was injected by a third-party script and not intended to be
        // loaded as a module. The global assignment can be reverted in the Lo-Dash
        // module by its `noConflict()` method.
        root._ = _;

        // define as an anonymous module so, through path mapping, it can be
        // referenced as the "underscore" module
        define(function () {
            return _;
        });
    }
    // check for `exports` after `define` in case a build optimizer adds an `exports` object
    else if (freeExports && freeModule) {
        // in Node.js or RingoJS
        if (moduleExports) {
            (freeModule.exports = _)._ = _;
        }
        // in Narwhal or Rhino -require
        else {
            freeExports._ = _;
        }
    }
    else {
        // in a browser or Rhino
        root._ = _;
    }
}.call(this));

//     Backbone.js 1.1.2

//     (c) 2010-2014 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors
//     Backbone may be freely distributed under the MIT license.
//     For all details and documentation:
//     http://backbonejs.org

(function (root, factory) {

    // Set up Backbone appropriately for the environment. Start with AMD.
    if (typeof define === 'function' && define.amd) {
        define(['underscore', 'jquery', 'exports'], function (_, $, exports) {
            // Export global even in AMD case in case this script is loaded with
            // others that may still expect a global Backbone.
            root.Backbone = factory(root, exports, _, $);
        });

        // Next for Node.js or CommonJS. jQuery may not be needed as a module.
    } else if (typeof exports !== 'undefined') {
        var _ = require('underscore');
        factory(root, exports, _);

        // Finally, as a browser global.
    } else {
        root.Backbone = factory(root, {}, root._, (root.jQuery || root.Zepto || root.ender || root.$));
    }

}(this, function (root, Backbone, _, $) {

    // Initial Setup
    // -------------

    // Save the previous value of the `Backbone` variable, so that it can be
    // restored later on, if `noConflict` is used.
    var previousBackbone = root.Backbone;

    // Create local references to array methods we'll want to use later.
    var array = [];
    var push = array.push;
    var slice = array.slice;
    var splice = array.splice;

    // Current version of the library. Keep in sync with `package.json`.
    Backbone.VERSION = '1.1.2';

    // For Backbone's purposes, jQuery, Zepto, Ender, or My Library (kidding) owns
    // the `$` variable.
    Backbone.$ = $;

    // Runs Backbone.js in *noConflict* mode, returning the `Backbone` variable
    // to its previous owner. Returns a reference to this Backbone object.
    Backbone.noConflict = function () {
        root.Backbone = previousBackbone;
        return this;
    };

    // Turn on `emulateHTTP` to support legacy HTTP servers. Setting this option
    // will fake `"PATCH"`, `"PUT"` and `"DELETE"` requests via the `_method` parameter and
    // set a `X-Http-Method-Override` header.
    Backbone.emulateHTTP = false;

    // Turn on `emulateJSON` to support legacy servers that can't deal with direct
    // `application/json` requests ... will encode the body as
    // `application/x-www-form-urlencoded` instead and will send the model in a
    // form param named `model`.
    Backbone.emulateJSON = false;

    // Backbone.Events
    // ---------------

    // A module that can be mixed in to *any object* in order to provide it with
    // custom events. You may bind with `on` or remove with `off` callback
    // functions to an event; `trigger`-ing an event fires all callbacks in
    // succession.
    //
    //     var object = {};
    //     _.extend(object, Backbone.Events);
    //     object.on('expand', function(){ alert('expanded'); });
    //     object.trigger('expand');
    //
    var Events = Backbone.Events = {

        // Bind an event to a `callback` function. Passing `"all"` will bind
        // the callback to all events fired.
        on: function (name, callback, context) {
            if (!eventsApi(this, 'on', name, [callback, context]) || !callback) return this;
            this._events || (this._events = {});
            var events = this._events[name] || (this._events[name] = []);
            events.push({callback: callback, context: context, ctx: context || this});
            return this;
        },

        // Bind an event to only be triggered a single time. After the first time
        // the callback is invoked, it will be removed.
        once: function (name, callback, context) {
            if (!eventsApi(this, 'once', name, [callback, context]) || !callback) return this;
            var self = this;
            var once = _.once(function () {
                self.off(name, once);
                callback.apply(this, arguments);
            });
            once._callback = callback;
            return this.on(name, once, context);
        },

        // Remove one or many callbacks. If `context` is null, removes all
        // callbacks with that function. If `callback` is null, removes all
        // callbacks for the event. If `name` is null, removes all bound
        // callbacks for all events.
        off: function (name, callback, context) {
            var retain, ev, events, names, i, l, j, k;
            if (!this._events || !eventsApi(this, 'off', name, [callback, context])) return this;
            if (!name && !callback && !context) {
                this._events = void 0;
                return this;
            }
            names = name ? [name] : _.keys(this._events);
            for (i = 0, l = names.length; i < l; i++) {
                name = names[i];
                if (events = this._events[name]) {
                    this._events[name] = retain = [];
                    if (callback || context) {
                        for (j = 0, k = events.length; j < k; j++) {
                            ev = events[j];
                            if ((callback && callback !== ev.callback && callback !== ev.callback._callback) ||
                                (context && context !== ev.context)) {
                                retain.push(ev);
                            }
                        }
                    }
                    if (!retain.length) delete this._events[name];
                }
            }

            return this;
        },

        // Trigger one or many events, firing all bound callbacks. Callbacks are
        // passed the same arguments as `trigger` is, apart from the event name
        // (unless you're listening on `"all"`, which will cause your callback to
        // receive the true name of the event as the first argument).
        trigger: function (name) {
            if (!this._events) return this;
            var args = slice.call(arguments, 1);
            if (!eventsApi(this, 'trigger', name, args)) return this;
            var events = this._events[name];
            var allEvents = this._events.all;
            if (events) triggerEvents(events, args);
            if (allEvents) triggerEvents(allEvents, arguments);
            return this;
        },

        // Tell this object to stop listening to either specific events ... or
        // to every object it's currently listening to.
        stopListening: function (obj, name, callback) {
            var listeningTo = this._listeningTo;
            if (!listeningTo) return this;
            var remove = !name && !callback;
            if (!callback && typeof name === 'object') callback = this;
            if (obj) (listeningTo = {})[obj._listenId] = obj;
            for (var id in listeningTo) {
                obj = listeningTo[id];
                obj.off(name, callback, this);
                if (remove || _.isEmpty(obj._events)) delete this._listeningTo[id];
            }
            return this;
        }

    };

    // Regular expression used to split event strings.
    var eventSplitter = /\s+/;

    // Implement fancy features of the Events API such as multiple event
    // names `"change blur"` and jQuery-style event maps `{change: action}`
    // in terms of the existing API.
    var eventsApi = function (obj, action, name, rest) {
        if (!name) return true;

        // Handle event maps.
        if (typeof name === 'object') {
            for (var key in name) {
                obj[action].apply(obj, [key, name[key]].concat(rest));
            }
            return false;
        }

        // Handle space separated event names.
        if (eventSplitter.test(name)) {
            var names = name.split(eventSplitter);
            for (var i = 0, l = names.length; i < l; i++) {
                obj[action].apply(obj, [names[i]].concat(rest));
            }
            return false;
        }

        return true;
    };

    // A difficult-to-believe, but optimized internal dispatch function for
    // triggering events. Tries to keep the usual cases speedy (most internal
    // Backbone events have 3 arguments).
    var triggerEvents = function (events, args) {
        var ev, i = -1, l = events.length, a1 = args[0], a2 = args[1], a3 = args[2];
        switch (args.length) {
            case 0:
                while (++i < l) (ev = events[i]).callback.call(ev.ctx);
                return;
            case 1:
                while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1);
                return;
            case 2:
                while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1, a2);
                return;
            case 3:
                while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1, a2, a3);
                return;
            default:
                while (++i < l) (ev = events[i]).callback.apply(ev.ctx, args);
                return;
        }
    };

    var listenMethods = {listenTo: 'on', listenToOnce: 'once'};

    // Inversion-of-control versions of `on` and `once`. Tell *this* object to
    // listen to an event in another object ... keeping track of what it's
    // listening to.
    _.each(listenMethods, function (implementation, method) {
        Events[method] = function (obj, name, callback) {
            var listeningTo = this._listeningTo || (this._listeningTo = {});
            var id = obj._listenId || (obj._listenId = _.uniqueId('l'));
            listeningTo[id] = obj;
            if (!callback && typeof name === 'object') callback = this;
            obj[implementation](name, callback, this);
            return this;
        };
    });

    // Aliases for backwards compatibility.
    Events.bind = Events.on;
    Events.unbind = Events.off;

    // Allow the `Backbone` object to serve as a global event bus, for folks who
    // want global "pubsub" in a convenient place.
    _.extend(Backbone, Events);

    // Backbone.Model
    // --------------

    // Backbone **Models** are the basic data object in the framework --
    // frequently representing a row in a table in a database on your server.
    // A discrete chunk of data and a bunch of useful, related methods for
    // performing computations and transformations on that data.

    // Create a new model with the specified attributes. A client id (`cid`)
    // is automatically generated and assigned for you.
    var Model = Backbone.Model = function (attributes, options) {
        var attrs = attributes || {};
        options || (options = {});
        this.cid = _.uniqueId('c');
        this.attributes = {};
        if (options.collection) this.collection = options.collection;
        if (options.parse) attrs = this.parse(attrs, options) || {};
        attrs = _.defaults({}, attrs, _.result(this, 'defaults'));
        this.set(attrs, options);
        this.changed = {};
        this.initialize.apply(this, arguments);
    };

    // Attach all inheritable methods to the Model prototype.
    _.extend(Model.prototype, Events, {

        // A hash of attributes whose current and previous value differ.
        changed: null,

        // The value returned during the last failed validation.
        validationError: null,

        // The default name for the JSON `id` attribute is `"id"`. MongoDB and
        // CouchDB users may want to set this to `"_id"`.
        idAttribute: 'id',

        // Initialize is an empty function by default. Override it with your own
        // initialization logic.
        initialize: function () {},

        // Return a copy of the model's `attributes` object.
        toJSON: function (options) {
            return _.clone(this.attributes);
        },

        // Proxy `Backbone.sync` by default -- but override this if you need
        // custom syncing semantics for *this* particular model.
        sync: function () {
            return Backbone.sync.apply(this, arguments);
        },

        // Get the value of an attribute.
        get: function (attr) {
            return this.attributes[attr];
        },

        // Get the HTML-escaped value of an attribute.
        escape: function (attr) {
            return _.escape(this.get(attr));
        },

        // Returns `true` if the attribute contains a value that is not null
        // or undefined.
        has: function (attr) {
            return this.get(attr) != null;
        },

        // Set a hash of model attributes on the object, firing `"change"`. This is
        // the core primitive operation of a model, updating the data and notifying
        // anyone who needs to know about the change in state. The heart of the beast.
        set: function (key, val, options) {
            var attr, attrs, unset, changes, silent, changing, prev, current;
            if (key == null) return this;

            // Handle both `"key", value` and `{key: value}` -style arguments.
            if (typeof key === 'object') {
                attrs = key;
                options = val;
            } else {
                (attrs = {})[key] = val;
            }

            options || (options = {});

            // Run validation.
            if (!this._validate(attrs, options)) return false;

            // Extract attributes and options.
            unset = options.unset;
            silent = options.silent;
            changes = [];
            changing = this._changing;
            this._changing = true;

            if (!changing) {
                this._previousAttributes = _.clone(this.attributes);
                this.changed = {};
            }
            current = this.attributes, prev = this._previousAttributes;

            // Check for changes of `id`.
            if (this.idAttribute in attrs) this.id = attrs[this.idAttribute];

            // For each `set` attribute, update or delete the current value.
            for (attr in attrs) {
                val = attrs[attr];
                if (!_.isEqual(current[attr], val)) changes.push(attr);
                if (!_.isEqual(prev[attr], val)) {
                    this.changed[attr] = val;
                } else {
                    delete this.changed[attr];
                }
                unset ? delete current[attr] : current[attr] = val;
            }

            // Trigger all relevant attribute changes.
            if (!silent) {
                if (changes.length) this._pending = options;
                for (var i = 0, l = changes.length; i < l; i++) {
                    this.trigger('change:' + changes[i], this, current[changes[i]], options);
                }
            }

            // You might be wondering why there's a `while` loop here. Changes can
            // be recursively nested within `"change"` events.
            if (changing) return this;
            if (!silent) {
                while (this._pending) {
                    options = this._pending;
                    this._pending = false;
                    this.trigger('change', this, options);
                }
            }
            this._pending = false;
            this._changing = false;
            return this;
        },

        // Remove an attribute from the model, firing `"change"`. `unset` is a noop
        // if the attribute doesn't exist.
        unset: function (attr, options) {
            return this.set(attr, void 0, _.extend({}, options, {unset: true}));
        },

        // Clear all attributes on the model, firing `"change"`.
        clear: function (options) {
            var attrs = {};
            for (var key in this.attributes) attrs[key] = void 0;
            return this.set(attrs, _.extend({}, options, {unset: true}));
        },

        // Determine if the model has changed since the last `"change"` event.
        // If you specify an attribute name, determine if that attribute has changed.
        hasChanged: function (attr) {
            if (attr == null) return !_.isEmpty(this.changed);
            return _.has(this.changed, attr);
        },

        // Return an object containing all the attributes that have changed, or
        // false if there are no changed attributes. Useful for determining what
        // parts of a view need to be updated and/or what attributes need to be
        // persisted to the server. Unset attributes will be set to undefined.
        // You can also pass an attributes object to diff against the model,
        // determining if there *would be* a change.
        changedAttributes: function (diff) {
            if (!diff) return this.hasChanged() ? _.clone(this.changed) : false;
            var val, changed = false;
            var old = this._changing ? this._previousAttributes : this.attributes;
            for (var attr in diff) {
                if (_.isEqual(old[attr], (val = diff[attr]))) continue;
                (changed || (changed = {}))[attr] = val;
            }
            return changed;
        },

        // Get the previous value of an attribute, recorded at the time the last
        // `"change"` event was fired.
        previous: function (attr) {
            if (attr == null || !this._previousAttributes) return null;
            return this._previousAttributes[attr];
        },

        // Get all of the attributes of the model at the time of the previous
        // `"change"` event.
        previousAttributes: function () {
            return _.clone(this._previousAttributes);
        },

        // Fetch the model from the server. If the server's representation of the
        // model differs from its current attributes, they will be overridden,
        // triggering a `"change"` event.
        fetch: function (options) {
            options = options ? _.clone(options) : {};
            if (options.parse === void 0) options.parse = true;
            var model = this;
            var success = options.success;
            options.success = function (resp) {
                if (!model.set(model.parse(resp, options), options)) return false;
                if (success) success(model, resp, options);
                model.trigger('sync', model, resp, options);
            };
            wrapError(this, options);
            return this.sync('read', this, options);
        },

        // Set a hash of model attributes, and sync the model to the server.
        // If the server returns an attributes hash that differs, the model's
        // state will be `set` again.
        save: function (key, val, options) {
            var attrs, method, xhr, attributes = this.attributes;

            // Handle both `"key", value` and `{key: value}` -style arguments.
            if (key == null || typeof key === 'object') {
                attrs = key;
                options = val;
            } else {
                (attrs = {})[key] = val;
            }

            options = _.extend({validate: true}, options);

            // If we're not waiting and attributes exist, save acts as
            // `set(attr).save(null, opts)` with validation. Otherwise, check if
            // the model will be valid when the attributes, if any, are set.
            if (attrs && !options.wait) {
                if (!this.set(attrs, options)) return false;
            } else {
                if (!this._validate(attrs, options)) return false;
            }

            // Set temporary attributes if `{wait: true}`.
            if (attrs && options.wait) {
                this.attributes = _.extend({}, attributes, attrs);
            }

            // After a successful server-side save, the client is (optionally)
            // updated with the server-side state.
            if (options.parse === void 0) options.parse = true;
            var model = this;
            var success = options.success;
            options.success = function (resp) {
                // Ensure attributes are restored during synchronous saves.
                model.attributes = attributes;
                var serverAttrs = model.parse(resp, options);
                if (options.wait) serverAttrs = _.extend(attrs || {}, serverAttrs);
                if (_.isObject(serverAttrs) && !model.set(serverAttrs, options)) {
                    return false;
                }
                if (success) success(model, resp, options);
                model.trigger('sync', model, resp, options);
            };
            wrapError(this, options);

            method = this.isNew() ? 'create' : (options.patch ? 'patch' : 'update');
            if (method === 'patch') options.attrs = attrs;
            xhr = this.sync(method, this, options);

            // Restore attributes.
            if (attrs && options.wait) this.attributes = attributes;

            return xhr;
        },

        // Destroy this model on the server if it was already persisted.
        // Optimistically removes the model from its collection, if it has one.
        // If `wait: true` is passed, waits for the server to respond before removal.
        destroy: function (options) {
            options = options ? _.clone(options) : {};
            var model = this;
            var success = options.success;

            var destroy = function () {
                model.trigger('destroy', model, model.collection, options);
            };

            options.success = function (resp) {
                if (options.wait || model.isNew()) destroy();
                if (success) success(model, resp, options);
                if (!model.isNew()) model.trigger('sync', model, resp, options);
            };

            if (this.isNew()) {
                options.success();
                return false;
            }
            wrapError(this, options);

            var xhr = this.sync('delete', this, options);
            if (!options.wait) destroy();
            return xhr;
        },

        // Default URL for the model's representation on the server -- if you're
        // using Backbone's restful methods, override this to change the endpoint
        // that will be called.
        url: function () {
            var base =
                _.result(this, 'urlRoot') ||
                _.result(this.collection, 'url') ||
                urlError();
            if (this.isNew()) return base;
            return base.replace(/([^\/])$/, '$1/') + encodeURIComponent(this.id);
        },

        // **parse** converts a response into the hash of attributes to be `set` on
        // the model. The default implementation is just to pass the response along.
        parse: function (resp, options) {
            return resp;
        },

        // Create a new model with identical attributes to this one.
        clone: function () {
            return new this.constructor(this.attributes);
        },

        // A model is new if it has never been saved to the server, and lacks an id.
        isNew: function () {
            return !this.has(this.idAttribute);
        },

        // Check if the model is currently in a valid state.
        isValid: function (options) {
            return this._validate({}, _.extend(options || {}, {validate: true}));
        },

        // Run validation against the next complete set of model attributes,
        // returning `true` if all is well. Otherwise, fire an `"invalid"` event.
        _validate: function (attrs, options) {
            if (!options.validate || !this.validate) return true;
            attrs = _.extend({}, this.attributes, attrs);
            var error = this.validationError = this.validate(attrs, options) || null;
            if (!error) return true;
            this.trigger('invalid', this, error, _.extend(options, {validationError: error}));
            return false;
        }

    });

    // Underscore methods that we want to implement on the Model.
    var modelMethods = ['keys', 'values', 'pairs', 'invert', 'pick', 'omit'];

    // Mix in each Underscore method as a proxy to `Model#attributes`.
    _.each(modelMethods, function (method) {
        Model.prototype[method] = function () {
            var args = slice.call(arguments);
            args.unshift(this.attributes);
            return _[method].apply(_, args);
        };
    });

    // Backbone.Collection
    // -------------------

    // If models tend to represent a single row of data, a Backbone Collection is
    // more analagous to a table full of data ... or a small slice or page of that
    // table, or a collection of rows that belong together for a particular reason
    // -- all of the messages in this particular folder, all of the documents
    // belonging to this particular author, and so on. Collections maintain
    // indexes of their models, both in order, and for lookup by `id`.

    // Create a new **Collection**, perhaps to contain a specific type of `model`.
    // If a `comparator` is specified, the Collection will maintain
    // its models in sort order, as they're added and removed.
    var Collection = Backbone.Collection = function (models, options) {
        options || (options = {});
        if (options.model) this.model = options.model;
        if (options.comparator !== void 0) this.comparator = options.comparator;
        this._reset();
        this.initialize.apply(this, arguments);
        if (models) this.reset(models, _.extend({silent: true}, options));
    };

    // Default options for `Collection#set`.
    var setOptions = {add: true, remove: true, merge: true};
    var addOptions = {add: true, remove: false};

    // Define the Collection's inheritable methods.
    _.extend(Collection.prototype, Events, {

        // The default model for a collection is just a **Backbone.Model**.
        // This should be overridden in most cases.
        model: Model,

        // Initialize is an empty function by default. Override it with your own
        // initialization logic.
        initialize: function () {},

        // The JSON representation of a Collection is an array of the
        // models' attributes.
        toJSON: function (options) {
            return this.map(function (model) { return model.toJSON(options); });
        },

        // Proxy `Backbone.sync` by default.
        sync: function () {
            return Backbone.sync.apply(this, arguments);
        },

        // Add a model, or list of models to the set.
        add: function (models, options) {
            return this.set(models, _.extend({merge: false}, options, addOptions));
        },

        // Remove a model, or a list of models from the set.
        remove: function (models, options) {
            var singular = !_.isArray(models);
            models = singular ? [models] : _.clone(models);
            options || (options = {});
            var i, l, index, model;
            for (i = 0, l = models.length; i < l; i++) {
                model = models[i] = this.get(models[i]);
                if (!model) continue;
                delete this._byId[model.id];
                delete this._byId[model.cid];
                index = this.indexOf(model);
                this.models.splice(index, 1);
                this.length--;
                if (!options.silent) {
                    options.index = index;
                    model.trigger('remove', model, this, options);
                }
                this._removeReference(model, options);
            }
            return singular ? models[0] : models;
        },

        // Update a collection by `set`-ing a new list of models, adding new ones,
        // removing models that are no longer present, and merging models that
        // already exist in the collection, as necessary. Similar to **Model#set**,
        // the core operation for updating the data contained by the collection.
        set: function (models, options) {
            options = _.defaults({}, options, setOptions);
            if (options.parse) models = this.parse(models, options);
            var singular = !_.isArray(models);
            models = singular ? (models ? [models] : []) : _.clone(models);
            var i, l, id, model, attrs, existing, sort;
            var at = options.at;
            var targetModel = this.model;
            var sortable = this.comparator && (at == null) && options.sort !== false;
            var sortAttr = _.isString(this.comparator) ? this.comparator : null;
            var toAdd = [], toRemove = [], modelMap = {};
            var add = options.add, merge = options.merge, remove = options.remove;
            var order = !sortable && add && remove ? [] : false;

            // Turn bare objects into model references, and prevent invalid models
            // from being added.
            for (i = 0, l = models.length; i < l; i++) {
                attrs = models[i] || {};
                if (attrs instanceof Model) {
                    id = model = attrs;
                } else {
                    id = attrs[targetModel.prototype.idAttribute || 'id'];
                }

                // If a duplicate is found, prevent it from being added and
                // optionally merge it into the existing model.
                if (existing = this.get(id)) {
                    if (remove) modelMap[existing.cid] = true;
                    if (merge) {
                        attrs = attrs === model ? model.attributes : attrs;
                        if (options.parse) attrs = existing.parse(attrs, options);
                        existing.set(attrs, options);
                        if (sortable && !sort && existing.hasChanged(sortAttr)) sort = true;
                    }
                    models[i] = existing;

                    // If this is a new, valid model, push it to the `toAdd` list.
                } else if (add) {
                    model = models[i] = this._prepareModel(attrs, options);
                    if (!model) continue;
                    toAdd.push(model);
                    this._addReference(model, options);
                }

                // Do not add multiple models with the same `id`.
                model = existing || model;
                if (order && (model.isNew() || !modelMap[model.id])) order.push(model);
                modelMap[model.id] = true;
            }

            // Remove nonexistent models if appropriate.
            if (remove) {
                for (i = 0, l = this.length; i < l; ++i) {
                    if (!modelMap[(model = this.models[i]).cid]) toRemove.push(model);
                }
                if (toRemove.length) this.remove(toRemove, options);
            }

            // See if sorting is needed, update `length` and splice in new models.
            if (toAdd.length || (order && order.length)) {
                if (sortable) sort = true;
                this.length += toAdd.length;
                if (at != null) {
                    for (i = 0, l = toAdd.length; i < l; i++) {
                        this.models.splice(at + i, 0, toAdd[i]);
                    }
                } else {
                    if (order) this.models.length = 0;
                    var orderedModels = order || toAdd;
                    for (i = 0, l = orderedModels.length; i < l; i++) {
                        this.models.push(orderedModels[i]);
                    }
                }
            }

            // Silently sort the collection if appropriate.
            if (sort) this.sort({silent: true});

            // Unless silenced, it's time to fire all appropriate add/sort events.
            if (!options.silent) {
                for (i = 0, l = toAdd.length; i < l; i++) {
                    (model = toAdd[i]).trigger('add', model, this, options);
                }
                if (sort || (order && order.length)) this.trigger('sort', this, options);
            }

            // Return the added (or merged) model (or models).
            return singular ? models[0] : models;
        },

        // When you have more items than you want to add or remove individually,
        // you can reset the entire set with a new list of models, without firing
        // any granular `add` or `remove` events. Fires `reset` when finished.
        // Useful for bulk operations and optimizations.
        reset: function (models, options) {
            options || (options = {});
            for (var i = 0, l = this.models.length; i < l; i++) {
                this._removeReference(this.models[i], options);
            }
            options.previousModels = this.models;
            this._reset();
            models = this.add(models, _.extend({silent: true}, options));
            if (!options.silent) this.trigger('reset', this, options);
            return models;
        },

        // Add a model to the end of the collection.
        push: function (model, options) {
            return this.add(model, _.extend({at: this.length}, options));
        },

        // Remove a model from the end of the collection.
        pop: function (options) {
            var model = this.at(this.length - 1);
            this.remove(model, options);
            return model;
        },

        // Add a model to the beginning of the collection.
        unshift: function (model, options) {
            return this.add(model, _.extend({at: 0}, options));
        },

        // Remove a model from the beginning of the collection.
        shift: function (options) {
            var model = this.at(0);
            this.remove(model, options);
            return model;
        },

        // Slice out a sub-array of models from the collection.
        slice: function () {
            return slice.apply(this.models, arguments);
        },

        // Get a model from the set by id.
        get: function (obj) {
            if (obj == null) return void 0;
            return this._byId[obj] || this._byId[obj.id] || this._byId[obj.cid];
        },

        // Get the model at the given index.
        at: function (index) {
            return this.models[index];
        },

        // Return models with matching attributes. Useful for simple cases of
        // `filter`.
        where: function (attrs, first) {
            if (_.isEmpty(attrs)) return first ? void 0 : [];
            return this[first ? 'find' : 'filter'](function (model) {
                for (var key in attrs) {
                    if (attrs[key] !== model.get(key)) return false;
                }
                return true;
            });
        },

        // Return the first model with matching attributes. Useful for simple cases
        // of `find`.
        findWhere: function (attrs) {
            return this.where(attrs, true);
        },

        // Force the collection to re-sort itself. You don't need to call this under
        // normal circumstances, as the set will maintain sort order as each item
        // is added.
        sort: function (options) {
            if (!this.comparator) throw new Error('Cannot sort a set without a comparator');
            options || (options = {});

            // Run sort based on type of `comparator`.
            if (_.isString(this.comparator) || this.comparator.length === 1) {
                this.models = this.sortBy(this.comparator, this);
            } else {
                this.models.sort(_.bind(this.comparator, this));
            }

            if (!options.silent) this.trigger('sort', this, options);
            return this;
        },

        // Pluck an attribute from each model in the collection.
        pluck: function (attr) {
            return _.invoke(this.models, 'get', attr);
        },

        // Fetch the default set of models for this collection, resetting the
        // collection when they arrive. If `reset: true` is passed, the response
        // data will be passed through the `reset` method instead of `set`.
        fetch: function (options) {
            options = options ? _.clone(options) : {};
            if (options.parse === void 0) options.parse = true;
            var success = options.success;
            var collection = this;
            options.success = function (resp) {
                var method = options.reset ? 'reset' : 'set';
                collection[method](resp, options);
                if (success) success(collection, resp, options);
                collection.trigger('sync', collection, resp, options);
            };
            wrapError(this, options);
            return this.sync('read', this, options);
        },

        // Create a new instance of a model in this collection. Add the model to the
        // collection immediately, unless `wait: true` is passed, in which case we
        // wait for the server to agree.
        create: function (model, options) {
            options = options ? _.clone(options) : {};
            if (!(model = this._prepareModel(model, options))) return false;
            if (!options.wait) this.add(model, options);
            var collection = this;
            var success = options.success;
            options.success = function (model, resp) {
                if (options.wait) collection.add(model, options);
                if (success) success(model, resp, options);
            };
            model.save(null, options);
            return model;
        },

        // **parse** converts a response into a list of models to be added to the
        // collection. The default implementation is just to pass it through.
        parse: function (resp, options) {
            return resp;
        },

        // Create a new collection with an identical list of models as this one.
        clone: function () {
            return new this.constructor(this.models);
        },

        // Private method to reset all internal state. Called when the collection
        // is first initialized or reset.
        _reset: function () {
            this.length = 0;
            this.models = [];
            this._byId = {};
        },

        // Prepare a hash of attributes (or other model) to be added to this
        // collection.
        _prepareModel: function (attrs, options) {
            if (attrs instanceof Model) return attrs;
            options = options ? _.clone(options) : {};
            options.collection = this;
            var model = new this.model(attrs, options);
            if (!model.validationError) return model;
            this.trigger('invalid', this, model.validationError, options);
            return false;
        },

        // Internal method to create a model's ties to a collection.
        _addReference: function (model, options) {
            this._byId[model.cid] = model;
            if (model.id != null) this._byId[model.id] = model;
            if (!model.collection) model.collection = this;
            model.on('all', this._onModelEvent, this);
        },

        // Internal method to sever a model's ties to a collection.
        _removeReference: function (model, options) {
            if (this === model.collection) delete model.collection;
            model.off('all', this._onModelEvent, this);
        },

        // Internal method called every time a model in the set fires an event.
        // Sets need to update their indexes when models change ids. All other
        // events simply proxy through. "add" and "remove" events that originate
        // in other collections are ignored.
        _onModelEvent: function (event, model, collection, options) {
            if ((event === 'add' || event === 'remove') && collection !== this) return;
            if (event === 'destroy') this.remove(model, options);
            if (model && event === 'change:' + model.idAttribute) {
                delete this._byId[model.previous(model.idAttribute)];
                if (model.id != null) this._byId[model.id] = model;
            }
            this.trigger.apply(this, arguments);
        }

    });

    // Underscore methods that we want to implement on the Collection.
    // 90% of the core usefulness of Backbone Collections is actually implemented
    // right here:
    var methods = ['forEach', 'each', 'map', 'collect', 'reduce', 'foldl',
        'inject', 'reduceRight', 'foldr', 'find', 'detect', 'filter', 'select',
        'reject', 'every', 'all', 'some', 'any', 'include', 'contains', 'invoke',
        'max', 'min', 'toArray', 'size', 'first', 'head', 'take', 'initial', 'rest',
        'tail', 'drop', 'last', 'without', 'difference', 'indexOf', 'shuffle',
        'lastIndexOf', 'isEmpty', 'chain', 'sample'];

    // Mix in each Underscore method as a proxy to `Collection#models`.
    _.each(methods, function (method) {
        Collection.prototype[method] = function () {
            var args = slice.call(arguments);
            args.unshift(this.models);
            return _[method].apply(_, args);
        };
    });

    // Underscore methods that take a property name as an argument.
    var attributeMethods = ['groupBy', 'countBy', 'sortBy', 'indexBy'];

    // Use attributes instead of properties.
    _.each(attributeMethods, function (method) {
        Collection.prototype[method] = function (value, context) {
            var iterator = _.isFunction(value) ? value : function (model) {
                return model.get(value);
            };
            return _[method](this.models, iterator, context);
        };
    });

    // Backbone.View
    // -------------

    // Backbone Views are almost more convention than they are actual code. A View
    // is simply a JavaScript object that represents a logical chunk of UI in the
    // DOM. This might be a single item, an entire list, a sidebar or panel, or
    // even the surrounding frame which wraps your whole app. Defining a chunk of
    // UI as a **View** allows you to define your DOM events declaratively, without
    // having to worry about render order ... and makes it easy for the view to
    // react to specific changes in the state of your models.

    // Creating a Backbone.View creates its initial element outside of the DOM,
    // if an existing element is not provided...
    var View = Backbone.View = function (options) {
        this.cid = _.uniqueId('view');
        options || (options = {});
        _.extend(this, _.pick(options, viewOptions));
        this._ensureElement();
        this.initialize.apply(this, arguments);
        this.delegateEvents();
    };

    // Cached regex to split keys for `delegate`.
    var delegateEventSplitter = /^(\S+)\s*(.*)$/;

    // List of view options to be merged as properties.
    var viewOptions = ['model', 'collection', 'el', 'id', 'attributes', 'className', 'tagName', 'events'];

    // Set up all inheritable **Backbone.View** properties and methods.
    _.extend(View.prototype, Events, {

        // The default `tagName` of a View's element is `"div"`.
        tagName: 'div',

        // jQuery delegate for element lookup, scoped to DOM elements within the
        // current view. This should be preferred to global lookups where possible.
        $: function (selector) {
            return this.$el.find(selector);
        },

        // Initialize is an empty function by default. Override it with your own
        // initialization logic.
        initialize: function () {},

        // **render** is the core function that your view should override, in order
        // to populate its element (`this.el`), with the appropriate HTML. The
        // convention is for **render** to always return `this`.
        render: function () {
            return this;
        },

        // Remove this view by taking the element out of the DOM, and removing any
        // applicable Backbone.Events listeners.
        remove: function () {
            this.$el.remove();
            this.stopListening();
            return this;
        },

        // Change the view's element (`this.el` property), including event
        // re-delegation.
        setElement: function (element, delegate) {
            if (this.$el) this.undelegateEvents();
            this.$el = element instanceof Backbone.$ ? element : Backbone.$(element);
            this.el = this.$el[0];
            if (delegate !== false) this.delegateEvents();
            return this;
        },

        // Set callbacks, where `this.events` is a hash of
        //
        // *{"event selector": "callback"}*
        //
        //     {
        //       'mousedown .title':  'edit',
        //       'click .button':     'save',
        //       'click .open':       function(e) { ... }
        //     }
        //
        // pairs. Callbacks will be bound to the view, with `this` set properly.
        // Uses event delegation for efficiency.
        // Omitting the selector binds the event to `this.el`.
        // This only works for delegate-able events: not `focus`, `blur`, and
        // not `change`, `submit`, and `reset` in Internet Explorer.
        delegateEvents: function (events) {
            if (!(events || (events = _.result(this, 'events')))) return this;
            this.undelegateEvents();
            for (var key in events) {
                var method = events[key];
                if (!_.isFunction(method)) method = this[events[key]];
                if (!method) continue;

                var match = key.match(delegateEventSplitter);
                var eventName = match[1], selector = match[2];
                method = _.bind(method, this);
                eventName += '.delegateEvents' + this.cid;
                if (selector === '') {
                    this.$el.on(eventName, method);
                } else {
                    this.$el.on(eventName, selector, method);
                }
            }
            return this;
        },

        // Clears all callbacks previously bound to the view with `delegateEvents`.
        // You usually don't need to use this, but may wish to if you have multiple
        // Backbone views attached to the same DOM element.
        undelegateEvents: function () {
            this.$el.off('.delegateEvents' + this.cid);
            return this;
        },

        // Ensure that the View has a DOM element to render into.
        // If `this.el` is a string, pass it through `$()`, take the first
        // matching element, and re-assign it to `el`. Otherwise, create
        // an element from the `id`, `className` and `tagName` properties.
        _ensureElement: function () {
            if (!this.el) {
                var attrs = _.extend({}, _.result(this, 'attributes'));
                if (this.id) attrs.id = _.result(this, 'id');
                if (this.className) attrs['class'] = _.result(this, 'className');
                var $el = Backbone.$('<' + _.result(this, 'tagName') + '>').attr(attrs);
                this.setElement($el, false);
            } else {
                this.setElement(_.result(this, 'el'), false);
            }
        }

    });

    // Backbone.sync
    // -------------

    // Override this function to change the manner in which Backbone persists
    // models to the server. You will be passed the type of request, and the
    // model in question. By default, makes a RESTful Ajax request
    // to the model's `url()`. Some possible customizations could be:
    //
    // * Use `setTimeout` to batch rapid-fire updates into a single request.
    // * Send up the models as XML instead of JSON.
    // * Persist models via WebSockets instead of Ajax.
    //
    // Turn on `Backbone.emulateHTTP` in order to send `PUT` and `DELETE` requests
    // as `POST`, with a `_method` parameter containing the true HTTP method,
    // as well as all requests with the body as `application/x-www-form-urlencoded`
    // instead of `application/json` with the model in a param named `model`.
    // Useful when interfacing with server-side languages like **PHP** that make
    // it difficult to read the body of `PUT` requests.
    Backbone.sync = function (method, model, options) {
        var type = methodMap[method];

        // Default options, unless specified.
        _.defaults(options || (options = {}), {
            emulateHTTP: Backbone.emulateHTTP,
            emulateJSON: Backbone.emulateJSON
        });

        // Default JSON-request options.
        var params = {type: type, dataType: 'json'};

        // Ensure that we have a URL.
        if (!options.url) {
            params.url = _.result(model, 'url') || urlError();
        }

        // Ensure that we have the appropriate request data.
        if (options.data == null && model && (method === 'create' || method === 'update' || method === 'patch')) {
            params.contentType = 'application/json';
            params.data = JSON.stringify(options.attrs || model.toJSON(options));
        }

        // For older servers, emulate JSON by encoding the request into an HTML-form.
        if (options.emulateJSON) {
            params.contentType = 'application/x-www-form-urlencoded';
            params.data = params.data ? {model: params.data} : {};
        }

        // For older servers, emulate HTTP by mimicking the HTTP method with `_method`
        // And an `X-HTTP-Method-Override` header.
        if (options.emulateHTTP && (type === 'PUT' || type === 'DELETE' || type === 'PATCH')) {
            params.type = 'POST';
            if (options.emulateJSON) params.data._method = type;
            var beforeSend = options.beforeSend;
            options.beforeSend = function (xhr) {
                xhr.setRequestHeader('X-HTTP-Method-Override', type);
                if (beforeSend) return beforeSend.apply(this, arguments);
            };
        }

        // Don't process data on a non-GET request.
        if (params.type !== 'GET' && !options.emulateJSON) {
            params.processData = false;
        }

        // If we're sending a `PATCH` request, and we're in an old Internet Explorer
        // that still has ActiveX enabled by default, override jQuery to use that
        // for XHR instead. Remove this line when jQuery supports `PATCH` on IE8.
        if (params.type === 'PATCH' && noXhrPatch) {
            params.xhr = function () {
                return new ActiveXObject("Microsoft.XMLHTTP");
            };
        }

        // Make the request, allowing the user to override any Ajax options.
        var xhr = options.xhr = Backbone.ajax(_.extend(params, options));
        model.trigger('request', model, xhr, options);
        return xhr;
    };

    var noXhrPatch =
        typeof window !== 'undefined' && !!window.ActiveXObject && !(window.XMLHttpRequest && (new XMLHttpRequest).dispatchEvent);

    // Map from CRUD to HTTP for our default `Backbone.sync` implementation.
    var methodMap = {
        'create': 'POST',
        'update': 'PUT',
        'patch': 'PATCH',
        'delete': 'DELETE',
        'read': 'GET'
    };

    // Set the default implementation of `Backbone.ajax` to proxy through to `$`.
    // Override this if you'd like to use a different library.
    Backbone.ajax = function () {
        return Backbone.$.ajax.apply(Backbone.$, arguments);
    };

    // Backbone.Router
    // ---------------

    // Routers map faux-URLs to actions, and fire events when routes are
    // matched. Creating a new one sets its `routes` hash, if not set statically.
    var Router = Backbone.Router = function (options) {
        options || (options = {});
        if (options.routes) this.routes = options.routes;
        this._bindRoutes();
        this.initialize.apply(this, arguments);
    };

    // Cached regular expressions for matching named param parts and splatted
    // parts of route strings.
    var optionalParam = /\((.*?)\)/g;
    var namedParam = /(\(\?)?:\w+/g;
    var splatParam = /\*\w+/g;
    var escapeRegExp = /[\-{}\[\]+?.,\\\^$|#\s]/g;

    // Set up all inheritable **Backbone.Router** properties and methods.
    _.extend(Router.prototype, Events, {

        // Initialize is an empty function by default. Override it with your own
        // initialization logic.
        initialize: function () {},

        // Manually bind a single named route to a callback. For example:
        //
        //     this.route('search/:query/p:num', 'search', function(query, num) {
        //       ...
        //     });
        //
        route: function (route, name, callback) {
            if (!_.isRegExp(route)) route = this._routeToRegExp(route);
            if (_.isFunction(name)) {
                callback = name;
                name = '';
            }
            if (!callback) callback = this[name];
            var router = this;
            Backbone.history.route(route, function (fragment) {
                var args = router._extractParameters(route, fragment);
                router.execute(callback, args);
                router.trigger.apply(router, ['route:' + name].concat(args));
                router.trigger('route', name, args);
                Backbone.history.trigger('route', router, name, args);
            });
            return this;
        },

        // Execute a route handler with the provided parameters.  This is an
        // excellent place to do pre-route setup or post-route cleanup.
        execute: function (callback, args) {
            if (callback) callback.apply(this, args);
        },

        // Simple proxy to `Backbone.history` to save a fragment into the history.
        navigate: function (fragment, options) {
            Backbone.history.navigate(fragment, options);
            return this;
        },

        // Bind all defined routes to `Backbone.history`. We have to reverse the
        // order of the routes here to support behavior where the most general
        // routes can be defined at the bottom of the route map.
        _bindRoutes: function () {
            if (!this.routes) return;
            this.routes = _.result(this, 'routes');
            var route, routes = _.keys(this.routes);
            while ((route = routes.pop()) != null) {
                this.route(route, this.routes[route]);
            }
        },

        // Convert a route string into a regular expression, suitable for matching
        // against the current location hash.
        _routeToRegExp: function (route) {
            route = route.replace(escapeRegExp, '\\$&')
                .replace(optionalParam, '(?:$1)?')
                .replace(namedParam, function (match, optional) {
                    return optional ? match : '([^/?]+)';
                })
                .replace(splatParam, '([^?]*?)');
            return new RegExp('^' + route + '(?:\\?([\\s\\S]*))?$');
        },

        // Given a route, and a URL fragment that it matches, return the array of
        // extracted decoded parameters. Empty or unmatched parameters will be
        // treated as `null` to normalize cross-browser behavior.
        _extractParameters: function (route, fragment) {
            var params = route.exec(fragment).slice(1);
            return _.map(params, function (param, i) {
                // Don't decode the search params.
                if (i === params.length - 1) return param || null;
                return param ? decodeURIComponent(param) : null;
            });
        }

    });

    // Backbone.History
    // ----------------

    // Handles cross-browser history management, based on either
    // [pushState](http://diveintohtml5.info/history.html) and real URLs, or
    // [onhashchange](https://developer.mozilla.org/en-US/docs/DOM/window.onhashchange)
    // and URL fragments. If the browser supports neither (old IE, natch),
    // falls back to polling.
    var History = Backbone.History = function () {
        this.handlers = [];
        _.bindAll(this, 'checkUrl');

        // Ensure that `History` can be used outside of the browser.
        if (typeof window !== 'undefined') {
            this.location = window.location;
            this.history = window.history;
        }
    };

    // Cached regex for stripping a leading hash/slash and trailing space.
    var routeStripper = /^[#\/]|\s+$/g;

    // Cached regex for stripping leading and trailing slashes.
    var rootStripper = /^\/+|\/+$/g;

    // Cached regex for detecting MSIE.
    var isExplorer = /msie [\w.]+/;

    // Cached regex for removing a trailing slash.
    var trailingSlash = /\/$/;

    // Cached regex for stripping urls of hash.
    var pathStripper = /#.*$/;

    // Has the history handling already been started?
    History.started = false;

    // Set up all inheritable **Backbone.History** properties and methods.
    _.extend(History.prototype, Events, {

        // The default interval to poll for hash changes, if necessary, is
        // twenty times a second.
        interval: 50,

        // Are we at the app root?
        atRoot: function () {
            return this.location.pathname.replace(/[^\/]$/, '$&/') === this.root;
        },

        // Gets the true hash value. Cannot use location.hash directly due to bug
        // in Firefox where location.hash will always be decoded.
        getHash: function (window) {
            var match = (window || this).location.href.match(/#(.*)$/);
            return match ? match[1] : '';
        },

        // Get the cross-browser normalized URL fragment, either from the URL,
        // the hash, or the override.
        getFragment: function (fragment, forcePushState) {
            if (fragment == null) {
                if (this._hasPushState || !this._wantsHashChange || forcePushState) {
                    fragment = decodeURI(this.location.pathname + this.location.search);
                    var root = this.root.replace(trailingSlash, '');
                    if (!fragment.indexOf(root)) fragment = fragment.slice(root.length);
                } else {
                    fragment = this.getHash();
                }
            }
            return fragment.replace(routeStripper, '');
        },

        // Start the hash change handling, returning `true` if the current URL matches
        // an existing route, and `false` otherwise.
        start: function (options) {
            if (History.started) throw new Error("Backbone.history has already been started");
            History.started = true;

            // Figure out the initial configuration. Do we need an iframe?
            // Is pushState desired ... is it available?
            this.options = _.extend({root: '/'}, this.options, options);
            this.root = this.options.root;
            this._wantsHashChange = this.options.hashChange !== false;
            this._wantsPushState = !!this.options.pushState;
            this._hasPushState = !!(this.options.pushState && this.history && this.history.pushState);
            var fragment = this.getFragment();
            var docMode = document.documentMode;
            var oldIE = (isExplorer.exec(navigator.userAgent.toLowerCase()) && (!docMode || docMode <= 7));

            // Normalize root to always include a leading and trailing slash.
            this.root = ('/' + this.root + '/').replace(rootStripper, '/');

            if (oldIE && this._wantsHashChange) {
                var frame = Backbone.$('<iframe src="javascript:0" tabindex="-1">');
                this.iframe = frame.hide().appendTo('body')[0].contentWindow;
                this.navigate(fragment);
            }

            // Depending on whether we're using pushState or hashes, and whether
            // 'onhashchange' is supported, determine how we check the URL state.
            if (this._hasPushState) {
                Backbone.$(window).on('popstate', this.checkUrl);
            } else if (this._wantsHashChange && ('onhashchange' in window) && !oldIE) {
                Backbone.$(window).on('hashchange', this.checkUrl);
            } else if (this._wantsHashChange) {
                this._checkUrlInterval = setInterval(this.checkUrl, this.interval);
            }

            // Determine if we need to change the base url, for a pushState link
            // opened by a non-pushState browser.
            this.fragment = fragment;
            var loc = this.location;

            // Transition from hashChange to pushState or vice versa if both are
            // requested.
            if (this._wantsHashChange && this._wantsPushState) {

                // If we've started off with a route from a `pushState`-enabled
                // browser, but we're currently in a browser that doesn't support it...
                if (!this._hasPushState && !this.atRoot()) {
                    this.fragment = this.getFragment(null, true);
                    this.location.replace(this.root + '#' + this.fragment);
                    // Return immediately as browser will do redirect to new url
                    return true;

                    // Or if we've started out with a hash-based route, but we're currently
                    // in a browser where it could be `pushState`-based instead...
                } else if (this._hasPushState && this.atRoot() && loc.hash) {
                    this.fragment = this.getHash().replace(routeStripper, '');
                    this.history.replaceState({}, document.title, this.root + this.fragment);
                }

            }

            if (!this.options.silent) return this.loadUrl();
        },

        // Disable Backbone.history, perhaps temporarily. Not useful in a real app,
        // but possibly useful for unit testing Routers.
        stop: function () {
            Backbone.$(window).off('popstate', this.checkUrl).off('hashchange', this.checkUrl);
            if (this._checkUrlInterval) clearInterval(this._checkUrlInterval);
            History.started = false;
        },

        // Add a route to be tested when the fragment changes. Routes added later
        // may override previous routes.
        route: function (route, callback) {
            this.handlers.unshift({route: route, callback: callback});
        },

        // Checks the current URL to see if it has changed, and if it has,
        // calls `loadUrl`, normalizing across the hidden iframe.
        checkUrl: function (e) {
            var current = this.getFragment();
            if (current === this.fragment && this.iframe) {
                current = this.getFragment(this.getHash(this.iframe));
            }
            if (current === this.fragment) return false;
            if (this.iframe) this.navigate(current);
            this.loadUrl();
        },

        // Attempt to load the current URL fragment. If a route succeeds with a
        // match, returns `true`. If no defined routes matches the fragment,
        // returns `false`.
        loadUrl: function (fragment) {
            fragment = this.fragment = this.getFragment(fragment);
            return _.any(this.handlers, function (handler) {
                if (handler.route.test(fragment)) {
                    handler.callback(fragment);
                    return true;
                }
            });
        },

        // Save a fragment into the hash history, or replace the URL state if the
        // 'replace' option is passed. You are responsible for properly URL-encoding
        // the fragment in advance.
        //
        // The options object can contain `trigger: true` if you wish to have the
        // route callback be fired (not usually desirable), or `replace: true`, if
        // you wish to modify the current URL without adding an entry to the history.
        navigate: function (fragment, options) {
            if (!History.started) return false;
            if (!options || options === true) options = {trigger: !!options};

            var url = this.root + (fragment = this.getFragment(fragment || ''));

            // Strip the hash for matching.
            fragment = fragment.replace(pathStripper, '');

            if (this.fragment === fragment) return;
            this.fragment = fragment;

            // Don't include a trailing slash on the root.
            if (fragment === '' && url !== '/') url = url.slice(0, -1);

            // If pushState is available, we use it to set the fragment as a real URL.
            if (this._hasPushState) {
                this.history[options.replace ? 'replaceState' : 'pushState']({}, document.title, url);

                // If hash changes haven't been explicitly disabled, update the hash
                // fragment to store history.
            } else if (this._wantsHashChange) {
                this._updateHash(this.location, fragment, options.replace);
                if (this.iframe && (fragment !== this.getFragment(this.getHash(this.iframe)))) {
                    // Opening and closing the iframe tricks IE7 and earlier to push a
                    // history entry on hash-tag change.  When replace is true, we don't
                    // want this.
                    if (!options.replace) this.iframe.document.open().close();
                    this._updateHash(this.iframe.location, fragment, options.replace);
                }

                // If you've told us that you explicitly don't want fallback hashchange-
                // based history, then `navigate` becomes a page refresh.
            } else {
                return this.location.assign(url);
            }
            if (options.trigger) return this.loadUrl(fragment);
        },

        // Update the hash location, either replacing the current entry, or adding
        // a new one to the browser history.
        _updateHash: function (location, fragment, replace) {
            if (replace) {
                var href = location.href.replace(/(javascript:|#).*$/, '');
                location.replace(href + '#' + fragment);
            } else {
                // Some browsers require that `hash` contains a leading #.
                location.hash = '#' + fragment;
            }
        }

    });

    // Create the default Backbone.history.
    Backbone.history = new History;

    // Helpers
    // -------

    // Helper function to correctly set up the prototype chain, for subclasses.
    // Similar to `goog.inherits`, but uses a hash of prototype properties and
    // class properties to be extended.
    var extend = function (protoProps, staticProps) {
        var parent = this;
        var child;

        // The constructor function for the new subclass is either defined by you
        // (the "constructor" property in your `extend` definition), or defaulted
        // by us to simply call the parent's constructor.
        if (protoProps && _.has(protoProps, 'constructor')) {
            child = protoProps.constructor;
        } else {
            child = function () { return parent.apply(this, arguments); };
        }

        // Add static properties to the constructor function, if supplied.
        _.extend(child, parent, staticProps);

        // Set the prototype chain to inherit from `parent`, without calling
        // `parent`'s constructor function.
        var Surrogate = function () { this.constructor = child; };
        Surrogate.prototype = parent.prototype;
        child.prototype = new Surrogate;

        // Add prototype properties (instance properties) to the subclass,
        // if supplied.
        if (protoProps) _.extend(child.prototype, protoProps);

        // Set a convenience property in case the parent's prototype is needed
        // later.
        child.__super__ = parent.prototype;

        return child;
    };

    // Set up inheritance for the model, collection, router, view and history.
    Model.extend = Collection.extend = Router.extend = View.extend = History.extend = extend;

    // Throw an error when a URL is needed, and none is supplied.
    var urlError = function () {
        throw new Error('A "url" property or function must be specified');
    };

    // Wrap an optional error callback with a fallback error event.
    var wrapError = function (model, options) {
        var error = options.error;
        options.error = function (resp) {
            if (error) error(model, resp, options);
            model.trigger('error', model, resp, options);
        };
    };

    return Backbone;

}));

// Vectorizer.
// -----------

// A tiny library for making your live easier when dealing with SVG.

// Copyright © 2012 - 2014 client IO (http://client.io)

(function (root, factory) {

    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define([], factory);

    } else {
        // Browser globals.
        root.Vectorizer = root.V = factory();
    }

}(this, function () {

    // Well, if SVG is not supported, this library is useless.
    var SVGsupported = !!(window.SVGAngle || document.implementation.hasFeature('http://www.w3.org/TR/SVG11/feature#BasicStructure', '1.1'));

    // XML namespaces.
    var ns = {
        xmlns: 'http://www.w3.org/2000/svg',
        xlink: 'http://www.w3.org/1999/xlink'
    };
    // SVG version.
    var SVGversion = '1.1';

    // A function returning a unique identifier for this client session with every call.
    var idCounter = 0;

    function uniqueId() {
        var id = ++idCounter + '';
        return 'v-' + id;
    }

    // Create an SVG document element.
    // If `content` is passed, it will be used as the SVG content of the `<svg>` root element.
    function createSvgDocument(content) {

        var svg = '<svg xmlns="' + ns.xmlns + '" xmlns:xlink="' + ns.xlink + '" version="' + SVGversion + '">' + (content || '') + '</svg>';
        var parser = new DOMParser();
        parser.async = false;
        return parser.parseFromString(svg, 'text/xml').documentElement;
    }

    // Create SVG element.
    // -------------------

    function createElement(el, attrs, children) {

        if (!el) return undefined;

        // If `el` is an object, it is probably a native SVG element. Wrap it to VElement.
        if (typeof el === 'object') {
            return new VElement(el);
        }
        attrs = attrs || {};

        // If `el` is a `'svg'` or `'SVG'` string, create a new SVG canvas.
        if (el.toLowerCase() === 'svg') {

            return new VElement(createSvgDocument());

        } else if (el[0] === '<') {
            // Create element from an SVG string.
            // Allows constructs of type: `document.appendChild(Vectorizer('<rect></rect>').node)`.

            var svgDoc = createSvgDocument(el);

            // Note that `createElement()` might also return an array should the SVG string passed as
            // the first argument contain more then one root element.
            if (svgDoc.childNodes.length > 1) {

                // Map child nodes to `VElement`s.
                var ret = [];
                for (var i = 0, len = svgDoc.childNodes.length; i < len; i++) {

                    var childNode = svgDoc.childNodes[i];
                    ret.push(new VElement(document.importNode(childNode, true)));
                }
                return ret;
            }

            return new VElement(document.importNode(svgDoc.firstChild, true));
        }

        el = document.createElementNS(ns.xmlns, el);

        // Set attributes.
        for (var key in attrs) {

            setAttribute(el, key, attrs[key]);
        }

        // Normalize `children` array.
        if (Object.prototype.toString.call(children) != '[object Array]') children = [children];

        // Append children if they are specified.
        var i = 0, len = (children[0] && children.length) || 0, child;
        for (; i < len; i++) {
            child = children[i];
            el.appendChild(child instanceof VElement ? child.node : child);
        }

        return new VElement(el);
    }

    function setAttribute(el, name, value) {

        if (name.indexOf(':') > -1) {
            // Attribute names can be namespaced. E.g. `image` elements
            // have a `xlink:href` attribute to set the source of the image.
            var combinedKey = name.split(':');
            el.setAttributeNS(ns[combinedKey[0]], combinedKey[1], value);

        } else if (name === 'id') {
            el.id = value;
        } else {
            el.setAttribute(name, value);
        }
    }

    function parseTransformString(transform) {
        var translate,
            rotate,
            scale;

        if (transform) {

            var separator = /[ ,]+/;

            var translateMatch = transform.match(/translate\((.*)\)/);
            if (translateMatch) {
                translate = translateMatch[1].split(separator);
            }
            var rotateMatch = transform.match(/rotate\((.*)\)/);
            if (rotateMatch) {
                rotate = rotateMatch[1].split(separator);
            }
            var scaleMatch = transform.match(/scale\((.*)\)/);
            if (scaleMatch) {
                scale = scaleMatch[1].split(separator);
            }
        }

        var sx = (scale && scale[0]) ? parseFloat(scale[0]) : 1;

        return {
            translate: {
                tx: (translate && translate[0]) ? parseInt(translate[0], 10) : 0,
                ty: (translate && translate[1]) ? parseInt(translate[1], 10) : 0
            },
            rotate: {
                angle: (rotate && rotate[0]) ? parseInt(rotate[0], 10) : 0,
                cx: (rotate && rotate[1]) ? parseInt(rotate[1], 10) : undefined,
                cy: (rotate && rotate[2]) ? parseInt(rotate[2], 10) : undefined
            },
            scale: {
                sx: sx,
                sy: (scale && scale[1]) ? parseFloat(scale[1]) : sx
            }
        };
    }

    // Matrix decomposition.
    // ---------------------

    function deltaTransformPoint(matrix, point) {

        var dx = point.x * matrix.a + point.y * matrix.c + 0;
        var dy = point.x * matrix.b + point.y * matrix.d + 0;
        return {x: dx, y: dy};
    }

    function decomposeMatrix(matrix) {

        // @see https://gist.github.com/2052247

        // calculate delta transform point
        var px = deltaTransformPoint(matrix, {x: 0, y: 1});
        var py = deltaTransformPoint(matrix, {x: 1, y: 0});

        // calculate skew
        var skewX = ((180 / Math.PI) * Math.atan2(px.y, px.x) - 90);
        var skewY = ((180 / Math.PI) * Math.atan2(py.y, py.x));

        return {

            translateX: matrix.e,
            translateY: matrix.f,
            scaleX: Math.sqrt(matrix.a * matrix.a + matrix.b * matrix.b),
            scaleY: Math.sqrt(matrix.c * matrix.c + matrix.d * matrix.d),
            skewX: skewX,
            skewY: skewY,
            rotation: skewX // rotation is the same as skew x
        };
    }

    // VElement.
    // ---------

    function VElement(el) {
        this.node = el;
        if (!this.node.id) {
            this.node.id = uniqueId();
        }
    }

    // VElement public API.
    // --------------------

    VElement.prototype = {

        translate: function (tx, ty, opt) {

            opt = opt || {};
            ty = ty || 0;

            var transformAttr = this.attr('transform') || '',
                transform = parseTransformString(transformAttr);

            // Is it a getter?
            if (typeof tx === 'undefined') {
                return transform.translate;
            }

            transformAttr = transformAttr.replace(/translate\([^\)]*\)/g, '').trim();

            var newTx = opt.absolute ? tx : transform.translate.tx + tx,
                newTy = opt.absolute ? ty : transform.translate.ty + ty,
                newTranslate = 'translate(' + newTx + ',' + newTy + ')';

            // Note that `translate()` is always the first transformation. This is
            // usually the desired case.
            this.attr('transform', (newTranslate + ' ' + transformAttr).trim());
            return this;
        },

        rotate: function (angle, cx, cy, opt) {

            opt = opt || {};

            var transformAttr = this.attr('transform') || '',
                transform = parseTransformString(transformAttr);

            // Is it a getter?
            if (typeof angle === 'undefined') {
                return transform.rotate;
            }

            transformAttr = transformAttr.replace(/rotate\([^\)]*\)/g, '').trim();

            angle %= 360;

            var newAngle = opt.absolute ? angle : transform.rotate.angle + angle,
                newOrigin = (cx !== undefined && cy !== undefined) ? ',' + cx + ',' + cy : '',
                newRotate = 'rotate(' + newAngle + newOrigin + ')';

            this.attr('transform', (transformAttr + ' ' + newRotate).trim());
            return this;
        },

        // Note that `scale` as the only transformation does not combine with previous values.
        scale: function (sx, sy) {
            sy = (typeof sy === 'undefined') ? sx : sy;

            var transformAttr = this.attr('transform') || '',
                transform = parseTransformString(transformAttr);

            // Is it a getter?
            if (typeof sx === 'undefined') {
                return transform.scale;
            }

            transformAttr = transformAttr.replace(/scale\([^\)]*\)/g, '').trim();

            var newScale = 'scale(' + sx + ',' + sy + ')';

            this.attr('transform', (transformAttr + ' ' + newScale).trim());
            return this;
        },

        // Get SVGRect that contains coordinates and dimension of the real bounding box,
        // i.e. after transformations are applied.
        // If `target` is specified, bounding box will be computed relatively to `target` element.
        bbox: function (withoutTransformations, target) {

            // If the element is not in the live DOM, it does not have a bounding box defined and
            // so fall back to 'zero' dimension element.
            if (!this.node.ownerSVGElement) return {x: 0, y: 0, width: 0, height: 0};

            var box;
            try {

                box = this.node.getBBox();

                // Opera returns infinite values in some cases.
                // Note that Infinity | 0 produces 0 as opposed to Infinity || 0.
                // We also have to create new object as the standard says that you can't
                // modify the attributes of a bbox.
                box = {x: box.x | 0, y: box.y | 0, width: box.width | 0, height: box.height | 0};

            } catch (e) {

                // Fallback for IE.
                box = {
                    x: this.node.clientLeft,
                    y: this.node.clientTop,
                    width: this.node.clientWidth,
                    height: this.node.clientHeight
                };
            }

            if (withoutTransformations) {

                return box;
            }

            var matrix = this.node.getTransformToElement(target || this.node.ownerSVGElement);
            var corners = [];
            var point = this.node.ownerSVGElement.createSVGPoint();

            point.x = box.x;
            point.y = box.y;
            corners.push(point.matrixTransform(matrix));

            point.x = box.x + box.width;
            point.y = box.y;
            corners.push(point.matrixTransform(matrix));

            point.x = box.x + box.width;
            point.y = box.y + box.height;
            corners.push(point.matrixTransform(matrix));

            point.x = box.x;
            point.y = box.y + box.height;
            corners.push(point.matrixTransform(matrix));

            var minX = corners[0].x;
            var maxX = minX;
            var minY = corners[0].y;
            var maxY = minY;

            for (var i = 1, len = corners.length; i < len; i++) {

                var x = corners[i].x;
                var y = corners[i].y;

                if (x < minX) {
                    minX = x;
                } else if (x > maxX) {
                    maxX = x;
                }

                if (y < minY) {
                    minY = y;
                } else if (y > maxY) {
                    maxY = y;
                }
            }

            return {
                x: minX,
                y: minY,
                width: maxX - minX,
                height: maxY - minY
            };
        },

        text: function (content, opt) {

            opt = opt || {};
            var lines = content.split('\n');
            var i = 0;
            var tspan;

            // `alignment-baseline` does not work in Firefox.
            // Setting `dominant-baseline` on the `<text>` element doesn't work in IE9.
            // In order to have the 0,0 coordinate of the `<text>` element (or the first `<tspan>`)
            // in the top left corner we translate the `<text>` element by `0.8em`.
            // See `http://www.w3.org/Graphics/SVG/WG/wiki/How_to_determine_dominant_baseline`.
            // See also `http://apike.ca/prog_svg_text_style.html`.
            this.attr('y', '0.8em');

            // An empty text gets rendered into the DOM in webkit-based browsers.
            // In order to unify this behaviour across all browsers
            // we rather hide the text element when it's empty.
            this.attr('display', content ? null : 'none');

            // Preserve spaces. In other words, we do not want consecutive spaces to get collapsed to one.
            this.node.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");

            if (lines.length === 1) {
                this.node.textContent = content;
                return this;
            }
            // Easy way to erase all `<tspan>` children;
            this.node.textContent = '';

            for (; i < lines.length; i++) {

                // Shift all the <tspan> but first by one line (`1em`)
                tspan = V('tspan', {dy: (i == 0 ? '0em' : opt.lineHeight || '1em'), x: this.attr('x') || 0});
                // Make sure the textContent is never empty. If it is, add an additional 
                // space (an invisible character) so that following lines are correctly
                // relatively positioned. `dy=1em` won't work with empty lines otherwise.
                tspan.node.textContent = lines[i] || ' ';

                this.append(tspan);
            }
            return this;
        },

        attr: function (name, value) {

            if (typeof name === 'string' && typeof value === 'undefined') {
                return this.node.getAttribute(name);
            }

            if (typeof name === 'object') {

                for (var attrName in name) {
                    if (name.hasOwnProperty(attrName)) {
                        setAttribute(this.node, attrName, name[attrName]);
                    }
                }

            } else {

                setAttribute(this.node, name, value);
            }

            return this;
        },

        remove: function () {
            if (this.node.parentNode) {
                this.node.parentNode.removeChild(this.node);
            }
        },

        append: function (el) {

            var els = el;

            if (Object.prototype.toString.call(el) !== '[object Array]') {

                els = [el];
            }

            for (var i = 0, len = els.length; i < len; i++) {
                el = els[i];
                this.node.appendChild(el instanceof VElement ? el.node : el);
            }

            return this;
        },

        prepend: function (el) {
            this.node.insertBefore(el instanceof VElement ? el.node : el, this.node.firstChild);
        },

        svg: function () {

            return this.node instanceof window.SVGSVGElement ? this : V(this.node.ownerSVGElement);
        },

        defs: function () {

            var defs = this.svg().node.getElementsByTagName('defs');

            return (defs && defs.length) ? V(defs[0]) : undefined;
        },

        clone: function () {
            var clone = V(this.node.cloneNode(true));
            // Note that clone inherits also ID. Therefore, we need to change it here.
            clone.node.id = uniqueId();
            return clone;
        },

        findOne: function (selector) {

            var found = this.node.querySelector(selector);
            return found ? V(found) : undefined;
        },

        find: function (selector) {

            var nodes = this.node.querySelectorAll(selector);

            // Map DOM elements to `VElement`s.
            for (var i = 0, len = nodes.length; i < len; i++) {
                nodes[i] = V(nodes[i]);
            }
            return nodes;
        },

        // Convert global point into the coordinate space of this element.
        toLocalPoint: function (x, y) {

            var svg = this.svg().node;

            var p = svg.createSVGPoint();
            p.x = x;
            p.y = y;

            try {

                var globalPoint = p.matrixTransform(svg.getScreenCTM().inverse());
                var globalToLocalMatrix = this.node.getTransformToElement(svg).inverse();

            } catch (e) {
                // IE9 throws an exception in odd cases. (`Unexpected call to method or property access`)
                // We have to make do with the original coordianates.
                return p;
            }

            return globalPoint.matrixTransform(globalToLocalMatrix);
        },

        translateCenterToPoint: function (p) {

            var bbox = this.bbox();
            var center = g.rect(bbox).center();

            this.translate(p.x - center.x, p.y - center.y);
        },

        // Efficiently auto-orient an element. This basically implements the orient=auto attribute
        // of markers. The easiest way of understanding on what this does is to imagine the element is an
        // arrowhead. Calling this method on the arrowhead makes it point to the `position` point while
        // being auto-oriented (properly rotated) towards the `reference` point.
        // `target` is the element relative to which the transformations are applied. Usually a viewport.
        translateAndAutoOrient: function (position, reference, target) {

            // Clean-up previously set transformations except the scale. If we didn't clean up the
            // previous transformations then they'd add up with the old ones. Scale is an exception as
            // it doesn't add up, consider: `this.scale(2).scale(2).scale(2)`. The result is that the
            // element is scaled by the factor 2, not 8.

            var s = this.scale();
            this.attr('transform', '');
            this.scale(s.sx, s.sy);

            var svg = this.svg().node;
            var bbox = this.bbox(false, target);

            // 1. Translate to origin.
            var translateToOrigin = svg.createSVGTransform();
            translateToOrigin.setTranslate(-bbox.x - bbox.width / 2, -bbox.y - bbox.height / 2);

            // 2. Rotate around origin.
            var rotateAroundOrigin = svg.createSVGTransform();
            var angle = g.point(position).changeInAngle(position.x - reference.x, position.y - reference.y, reference);
            rotateAroundOrigin.setRotate(angle, 0, 0);

            // 3. Translate to the `position` + the offset (half my width) towards the `reference` point.
            var translateFinal = svg.createSVGTransform();
            var finalPosition = g.point(position).move(reference, bbox.width / 2);
            translateFinal.setTranslate(position.x + (position.x - finalPosition.x), position.y + (position.y - finalPosition.y));

            // 4. Apply transformations.
            var ctm = this.node.getTransformToElement(target);
            var transform = svg.createSVGTransform();
            transform.setMatrix(
                translateFinal.matrix.multiply(
                    rotateAroundOrigin.matrix.multiply(
                        translateToOrigin.matrix.multiply(
                            ctm)))
            );

            // Instead of directly setting the `matrix()` transform on the element, first, decompose
            // the matrix into separate transforms. This allows us to use normal Vectorizer methods
            // as they don't work on matrices. An example of this is to retrieve a scale of an element.
            // this.node.transform.baseVal.initialize(transform);

            var decomposition = decomposeMatrix(transform.matrix);

            this.translate(decomposition.translateX, decomposition.translateY);
            this.rotate(decomposition.rotation);
            // Note that scale has been already applied, hence the following line stays commented. (it's here just for
            // reference). this.scale(decomposition.scaleX, decomposition.scaleY);

            return this;
        },

        animateAlongPath: function (attrs, path) {

            var animateMotion = V('animateMotion', attrs);
            var mpath = V('mpath', {'xlink:href': '#' + V(path).node.id});

            animateMotion.append(mpath);

            this.append(animateMotion);
            try {
                animateMotion.node.beginElement();
            } catch (e) {
                // Fallback for IE 9.
                // Run the animation programatically if FakeSmile (`http://leunen.me/fakesmile/`) present 
                if (document.documentElement.getAttribute('smiling') === 'fake') {

                    // Register the animation. (See `https://answers.launchpad.net/smil/+question/203333`)
                    var animation = animateMotion.node;
                    animation.animators = [];

                    var animationID = animation.getAttribute('id');
                    if (animationID) id2anim[animationID] = animation;

                    var targets = getTargets(animation);
                    for (var i = 0, len = targets.length; i < len; i++) {
                        var target = targets[i];
                        var animator = new Animator(animation, target, i);
                        animators.push(animator);
                        animation.animators[i] = animator;
                        animator.register();
                    }
                }
            }
        },

        hasClass: function (className) {

            return new RegExp('(\\s|^)' + className + '(\\s|$)').test(this.node.getAttribute('class'));
        },

        addClass: function (className) {

            if (!this.hasClass(className)) {
                var prevClasses = this.node.getAttribute('class') || '';
                this.node.setAttribute('class', (prevClasses + ' ' + className).trim());
            }

            return this;
        },

        removeClass: function (className) {

            if (this.hasClass(className)) {
                var newClasses = this.node.getAttribute('class').replace(new RegExp('(\\s|^)' + className + '(\\s|$)', 'g'), '$2');
                this.node.setAttribute('class', newClasses);
            }

            return this;
        },

        toggleClass: function (className, toAdd) {

            var toRemove = typeof toAdd === 'undefined' ? this.hasClass(className) : !toAdd;

            if (toRemove) {
                this.removeClass(className);
            } else {
                this.addClass(className);
            }

            return this;
        }
    };

    // Convert a rectangle to SVG path commands. `r` is an object of the form:
    // `{ x: [number], y: [number], width: [number], height: [number], top-ry: [number], top-ry: [number], bottom-rx:
    // [number], bottom-ry: [number] }`, where `x, y, width, height` are the usual rectangle attributes and
    // [top-/bottom-]rx/ry allows for specifying radius of the rectangle for all its sides (as opposed to the built-in
    // SVG rectangle that has only `rx` and `ry` attributes).
    function rectToPath(r) {

        var topRx = r.rx || r['top-rx'] || 0;
        var bottomRx = r.rx || r['bottom-rx'] || 0;
        var topRy = r.ry || r['top-ry'] || 0;
        var bottomRy = r.ry || r['bottom-ry'] || 0;

        return [
            'M', r.x, r.y + topRy,
            'v', r.height - topRy - bottomRy,
            'a', bottomRx, bottomRy, 0, 0, 0, bottomRx, bottomRy,
            'h', r.width - 2 * bottomRx,
            'a', bottomRx, bottomRy, 0, 0, 0, bottomRx, -bottomRy,
            'v', -(r.height - bottomRy - topRy),
            'a', topRx, topRy, 0, 0, 0, -topRx, -topRy,
            'h', -(r.width - 2 * topRx),
            'a', topRx, topRy, 0, 0, 0, -topRx, topRy
        ].join(' ');
    }

    var V = createElement;

    V.decomposeMatrix = decomposeMatrix;
    V.rectToPath = rectToPath;

    var svgDocument = V('svg').node;

    V.createSVGMatrix = function (m) {

        var svgMatrix = svgDocument.createSVGMatrix();
        for (var component in m) {
            svgMatrix[component] = m[component];
        }

        return svgMatrix;
    };

    V.createSVGTransform = function () {

        return svgDocument.createSVGTransform();
    };

    V.createSVGPoint = function (x, y) {

        var p = svgDocument.createSVGPoint();
        p.x = x;
        p.y = y;
        return p;
    };

    return V;

}));

//      Geometry library.
//      (c) 2011-2013 client IO

(function (root, factory) {

    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define([], factory);

    } else if (typeof exports === 'object') {
        // Node. Does not work with strict CommonJS, but
        // only CommonJS-like environments that support module.exports,
        // like Node.
        module.exports = factory();

    } else {
        // Browser globals.
        root.g = factory();
    }

}(this, function () {


    // Declare shorthands to the most used math functions.
    var math = Math;
    var abs = math.abs;
    var cos = math.cos;
    var sin = math.sin;
    var sqrt = math.sqrt;
    var mmin = math.min;
    var mmax = math.max;
    var atan = math.atan;
    var atan2 = math.atan2;
    var acos = math.acos;
    var round = math.round;
    var floor = math.floor;
    var PI = math.PI;
    var random = math.random;
    var toDeg = function (rad) { return (180 * rad / PI) % 360; };
    var toRad = function (deg, over360) {
        over360 = over360 || false;
        deg = over360 ? deg : (deg % 360);
        return deg * PI / 180;
    };
    var snapToGrid = function (val, gridSize) { return gridSize * Math.round(val / gridSize); };
    var normalizeAngle = function (angle) { return (angle % 360) + (angle < 0 ? 360 : 0); };

    // Point
    // -----

    // Point is the most basic object consisting of x/y coordinate,.

    // Possible instantiations are:

    // * `point(10, 20)`
    // * `new point(10, 20)`
    // * `point('10 20')`
    // * `point(point(10, 20))`
    function point(x, y) {
        if (!(this instanceof point))
            return new point(x, y);
        var xy;
        if (y === undefined && Object(x) !== x) {
            xy = x.split(x.indexOf('@') === -1 ? ' ' : '@');
            this.x = parseInt(xy[0], 10);
            this.y = parseInt(xy[1], 10);
        } else if (Object(x) === x) {
            this.x = x.x;
            this.y = x.y;
        } else {
            this.x = x;
            this.y = y;
        }
    }

    point.prototype = {
        toString: function () {
            return this.x + "@" + this.y;
        },
        // If point lies outside rectangle `r`, return the nearest point on the boundary of rect `r`,
        // otherwise return point itself.
        // (see Squeak Smalltalk, Point>>adhereTo:)
        adhereToRect: function (r) {
            if (r.containsPoint(this)) {
                return this;
            }
            this.x = mmin(mmax(this.x, r.x), r.x + r.width);
            this.y = mmin(mmax(this.y, r.y), r.y + r.height);
            return this;
        },
        // Compute the angle between me and `p` and the x axis.
        // (cartesian-to-polar coordinates conversion)
        // Return theta angle in degrees.
        theta: function (p) {
            p = point(p);
            // Invert the y-axis.
            var y = -(p.y - this.y);
            var x = p.x - this.x;
            // Makes sure that the comparison with zero takes rounding errors into account.
            var PRECISION = 10;
            // Note that `atan2` is not defined for `x`, `y` both equal zero.
            var rad = (y.toFixed(PRECISION) == 0 && x.toFixed(PRECISION) == 0) ? 0 : atan2(y, x);

            // Correction for III. and IV. quadrant.
            if (rad < 0) {
                rad = 2 * PI + rad;
            }
            return 180 * rad / PI;
        },
        // Returns distance between me and point `p`.
        distance: function (p) {
            return line(this, p).length();
        },
        // Returns a manhattan (taxi-cab) distance between me and point `p`.
        manhattanDistance: function (p) {
            return abs(p.x - this.x) + abs(p.y - this.y);
        },
        // Offset me by the specified amount.
        offset: function (dx, dy) {
            this.x += dx || 0;
            this.y += dy || 0;
            return this;
        },
        magnitude: function () {
            return sqrt((this.x * this.x) + (this.y * this.y)) || 0.01;
        },
        update: function (x, y) {
            this.x = x || 0;
            this.y = y || 0;
            return this;
        },
        round: function (decimals) {
            this.x = decimals ? this.x.toFixed(decimals) : round(this.x);
            this.y = decimals ? this.y.toFixed(decimals) : round(this.y);
            return this;
        },
        // Scale the line segment between (0,0) and me to have a length of len.
        normalize: function (len) {
            var s = (len || 1) / this.magnitude();
            this.x = s * this.x;
            this.y = s * this.y;
            return this;
        },
        difference: function (p) {
            return point(this.x - p.x, this.y - p.y);
        },
        // Return the bearing between me and point `p`.
        bearing: function (p) {
            return line(this, p).bearing();
        },
        // Converts rectangular to polar coordinates.
        // An origin can be specified, otherwise it's 0@0.
        toPolar: function (o) {
            o = (o && point(o)) || point(0, 0);
            var x = this.x;
            var y = this.y;
            this.x = sqrt((x - o.x) * (x - o.x) + (y - o.y) * (y - o.y));   // r
            this.y = toRad(o.theta(point(x, y)));
            return this;
        },
        // Rotate point by angle around origin o.
        rotate: function (o, angle) {
            angle = (angle + 360) % 360;
            this.toPolar(o);
            this.y += toRad(angle);
            var p = point.fromPolar(this.x, this.y, o);
            this.x = p.x;
            this.y = p.y;
            return this;
        },
        // Move point on line starting from ref ending at me by
        // distance distance.
        move: function (ref, distance) {
            var theta = toRad(point(ref).theta(this));
            return this.offset(cos(theta) * distance, -sin(theta) * distance);
        },
        // Returns change in angle from my previous position (-dx, -dy) to my new position
        // relative to ref point.
        changeInAngle: function (dx, dy, ref) {
            // Revert the translation and measure the change in angle around x-axis.
            return point(this).offset(-dx, -dy).theta(ref) - this.theta(ref);
        },
        equals: function (p) {
            return this.x === p.x && this.y === p.y;
        },
        snapToGrid: function (gx, gy) {
            this.x = snapToGrid(this.x, gx)
            this.y = snapToGrid(this.y, gy || gx)
            return this;
        },
        // Returns a point that is the reflection of me with
        // the center of inversion in ref point.
        reflection: function (ref) {
            return point(ref).move(this, this.distance(ref));
        }
    };
    // Alternative constructor, from polar coordinates.
    // @param {number} r Distance.
    // @param {number} angle Angle in radians.
    // @param {point} [optional] o Origin.
    point.fromPolar = function (r, angle, o) {
        o = (o && point(o)) || point(0, 0);
        var x = abs(r * cos(angle));
        var y = abs(r * sin(angle));
        var deg = normalizeAngle(toDeg(angle));

        if (deg < 90) y = -y;
        else if (deg < 180) {
            x = -x;
            y = -y;
        }
        else if (deg < 270) x = -x;

        return point(o.x + x, o.y + y);
    };

    // Create a point with random coordinates that fall into the range `[x1, x2]` and `[y1, y2]`.
    point.random = function (x1, x2, y1, y2) {
        return point(floor(random() * (x2 - x1 + 1) + x1), floor(random() * (y2 - y1 + 1) + y1));
    };

    // Line.
    // -----
    function line(p1, p2) {
        if (!(this instanceof line))
            return new line(p1, p2);
        this.start = point(p1);
        this.end = point(p2);
    }

    line.prototype = {
        toString: function () {
            return this.start.toString() + ' ' + this.end.toString();
        },
        // @return {double} length of the line
        length: function () {
            return sqrt(this.squaredLength());
        },
        // @return {integer} length without sqrt
        // @note for applications where the exact length is not necessary (e.g. compare only)
        squaredLength: function () {
            var x0 = this.start.x;
            var y0 = this.start.y;
            var x1 = this.end.x;
            var y1 = this.end.y;
            return (x0 -= x1) * x0 + (y0 -= y1) * y0;
        },
        // @return {point} my midpoint
        midpoint: function () {
            return point((this.start.x + this.end.x) / 2,
                (this.start.y + this.end.y) / 2);
        },
        // @return {point} Point where I'm intersecting l.
        // @see Squeak Smalltalk, LineSegment>>intersectionWith:
        intersection: function (l) {
            var pt1Dir = point(this.end.x - this.start.x, this.end.y - this.start.y);
            var pt2Dir = point(l.end.x - l.start.x, l.end.y - l.start.y);
            var det = (pt1Dir.x * pt2Dir.y) - (pt1Dir.y * pt2Dir.x);
            var deltaPt = point(l.start.x - this.start.x, l.start.y - this.start.y);
            var alpha = (deltaPt.x * pt2Dir.y) - (deltaPt.y * pt2Dir.x);
            var beta = (deltaPt.x * pt1Dir.y) - (deltaPt.y * pt1Dir.x);

            if (det === 0 ||
                alpha * det < 0 ||
                beta * det < 0) {
                // No intersection found.
                return null;
            }
            if (det > 0) {
                if (alpha > det || beta > det) {
                    return null;
                }
            } else {
                if (alpha < det || beta < det) {
                    return null;
                }
            }
            return point(this.start.x + (alpha * pt1Dir.x / det),
                this.start.y + (alpha * pt1Dir.y / det));
        },

        // @return the bearing (cardinal direction) of the line. For example N, W, or SE.
        // @returns {String} One of the following bearings : NE, E, SE, S, SW, W, NW, N.
        bearing: function () {

            var lat1 = toRad(this.start.y);
            var lat2 = toRad(this.end.y);
            var lon1 = this.start.x;
            var lon2 = this.end.x;
            var dLon = toRad(lon2 - lon1);
            var y = sin(dLon) * cos(lat2);
            var x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon);
            var brng = toDeg(atan2(y, x));

            var bearings = ['NE', 'E', 'SE', 'S', 'SW', 'W', 'NW', 'N'];

            var index = brng - 22.5;
            if (index < 0)
                index += 360;
            index = parseInt(index / 45);

            return bearings[index];
        },

        // @return {point} my point at 't' <0,1>
        pointAt: function (t) {
            var x = (1 - t) * this.start.x + t * this.end.x;
            var y = (1 - t) * this.start.y + t * this.end.y;
            return point(x, y);
        }
    };

    // Rectangle.
    // ----------
    function rect(x, y, w, h) {
        if (!(this instanceof rect))
            return new rect(x, y, w, h);
        if (y === undefined) {
            y = x.y;
            w = x.width;
            h = x.height;
            x = x.x;
        }
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    rect.prototype = {
        toString: function () {
            return this.origin().toString() + ' ' + this.corner().toString();
        },
        origin: function () {
            return point(this.x, this.y);
        },
        corner: function () {
            return point(this.x + this.width, this.y + this.height);
        },
        topRight: function () {
            return point(this.x + this.width, this.y);
        },
        bottomLeft: function () {
            return point(this.x, this.y + this.height);
        },
        center: function () {
            return point(this.x + this.width / 2, this.y + this.height / 2);
        },
        // @return {boolean} true if rectangles intersect
        intersect: function (r) {
            var myOrigin = this.origin();
            var myCorner = this.corner();
            var rOrigin = r.origin();
            var rCorner = r.corner();

            if (rCorner.x <= myOrigin.x ||
                rCorner.y <= myOrigin.y ||
                rOrigin.x >= myCorner.x ||
                rOrigin.y >= myCorner.y) return false;
            return true;
        },
        // @return {string} (left|right|top|bottom) side which is nearest to point
        // @see Squeak Smalltalk, Rectangle>>sideNearestTo:
        sideNearestToPoint: function (p) {
            p = point(p);
            var distToLeft = p.x - this.x;
            var distToRight = (this.x + this.width) - p.x;
            var distToTop = p.y - this.y;
            var distToBottom = (this.y + this.height) - p.y;
            var closest = distToLeft;
            var side = 'left';

            if (distToRight < closest) {
                closest = distToRight;
                side = 'right';
            }
            if (distToTop < closest) {
                closest = distToTop;
                side = 'top';
            }
            if (distToBottom < closest) {
                closest = distToBottom;
                side = 'bottom';
            }
            return side;
        },
        // @return {bool} true if point p is insight me
        containsPoint: function (p) {
            p = point(p);
            if (p.x >= this.x && p.x <= this.x + this.width &&
                p.y >= this.y && p.y <= this.y + this.height) {
                return true;
            }
            return false;
        },
        // Algorithm ported from java.awt.Rectangle from OpenJDK.
        // @return {bool} true if rectangle `r` is inside me.
        containsRect: function (r) {
            var nr = rect(r).normalize();
            var W = nr.width;
            var H = nr.height;
            var X = nr.x;
            var Y = nr.y;
            var w = this.width;
            var h = this.height;
            if ((w | h | W | H) < 0) {
                // At least one of the dimensions is negative...
                return false;
            }
            // Note: if any dimension is zero, tests below must return false...
            var x = this.x;
            var y = this.y;
            if (X < x || Y < y) {
                return false;
            }
            w += x;
            W += X;
            if (W <= X) {
                // X+W overflowed or W was zero, return false if...
                // either original w or W was zero or
                // x+w did not overflow or
                // the overflowed x+w is smaller than the overflowed X+W
                if (w >= x || W > w) return false;
            } else {
                // X+W did not overflow and W was not zero, return false if...
                // original w was zero or
                // x+w did not overflow and x+w is smaller than X+W
                if (w >= x && W > w) return false;
            }
            h += y;
            H += Y;
            if (H <= Y) {
                if (h >= y || H > h) return false;
            } else {
                if (h >= y && H > h) return false;
            }
            return true;
        },
        // @return {point} a point on my boundary nearest to p
        // @see Squeak Smalltalk, Rectangle>>pointNearestTo:
        pointNearestToPoint: function (p) {
            p = point(p);
            if (this.containsPoint(p)) {
                var side = this.sideNearestToPoint(p);
                switch (side) {
                    case "right":
                        return point(this.x + this.width, p.y);
                    case "left":
                        return point(this.x, p.y);
                    case "bottom":
                        return point(p.x, this.y + this.height);
                    case "top":
                        return point(p.x, this.y);
                }
            }
            return p.adhereToRect(this);
        },
        // Find point on my boundary where line starting
        // from my center ending in point p intersects me.
        // @param {number} angle If angle is specified, intersection with rotated rectangle is computed.
        intersectionWithLineFromCenterToPoint: function (p, angle) {
            p = point(p);
            var center = point(this.x + this.width / 2, this.y + this.height / 2);
            var result;
            if (angle) p.rotate(center, angle);

            // (clockwise, starting from the top side)
            var sides = [
                line(this.origin(), this.topRight()),
                line(this.topRight(), this.corner()),
                line(this.corner(), this.bottomLeft()),
                line(this.bottomLeft(), this.origin())
            ];
            var connector = line(center, p);

            for (var i = sides.length - 1; i >= 0; --i) {
                var intersection = sides[i].intersection(connector);
                if (intersection !== null) {
                    result = intersection;
                    break;
                }
            }
            if (result && angle) result.rotate(center, -angle);
            return result;
        },
        // Move and expand me.
        // @param r {rectangle} representing deltas
        moveAndExpand: function (r) {
            this.x += r.x;
            this.y += r.y;
            this.width += r.width;
            this.height += r.height;
            return this;
        },
        round: function (decimals) {
            this.x = decimals ? this.x.toFixed(decimals) : round(this.x);
            this.y = decimals ? this.y.toFixed(decimals) : round(this.y);
            this.width = decimals ? this.width.toFixed(decimals) : round(this.width);
            this.height = decimals ? this.height.toFixed(decimals) : round(this.height);
            return this;
        },
        // Normalize the rectangle; i.e., make it so that it has a non-negative width and height.
        // If width < 0 the function swaps the left and right corners,
        // and it swaps the top and bottom corners if height < 0
        // like in http://qt-project.org/doc/qt-4.8/qrectf.html#normalized
        normalize: function () {
            var newx = this.x;
            var newy = this.y;
            var newwidth = this.width;
            var newheight = this.height;
            if (this.width < 0) {
                newx = this.x + this.width;
                newwidth = -this.width;
            }
            if (this.height < 0) {
                newy = this.y + this.height;
                newheight = -this.height;
            }
            this.x = newx;
            this.y = newy;
            this.width = newwidth;
            this.height = newheight;
            return this;
        },
        // Find my bounding box when I'm rotated with the center of rotation in the center of me.
        // @return r {rectangle} representing a bounding box
        bbox: function (angle) {
            var theta = toRad(angle || 0);
            var st = abs(sin(theta));
            var ct = abs(cos(theta));
            var w = this.width * ct + this.height * st;
            var h = this.width * st + this.height * ct;
            return rect(this.x + (this.width - w) / 2, this.y + (this.height - h) / 2, w, h);
        }
    };

    // Ellipse.
    // --------
    function ellipse(c, a, b) {
        if (!(this instanceof ellipse))
            return new ellipse(c, a, b);
        c = point(c);
        this.x = c.x;
        this.y = c.y;
        this.a = a;
        this.b = b;
    }

    ellipse.prototype = {
        toString: function () {
            return point(this.x, this.y).toString() + ' ' + this.a + ' ' + this.b;
        },
        bbox: function () {
            return rect(this.x - this.a, this.y - this.b, 2 * this.a, 2 * this.b);
        },
        // Find point on me where line from my center to
        // point p intersects my boundary.
        // @param {number} angle If angle is specified, intersection with rotated ellipse is computed.
        intersectionWithLineFromCenterToPoint: function (p, angle) {
            p = point(p);
            if (angle) p.rotate(point(this.x, this.y), angle);
            var dx = p.x - this.x;
            var dy = p.y - this.y;
            var result;
            if (dx === 0) {
                result = this.bbox().pointNearestToPoint(p);
                if (angle) return result.rotate(point(this.x, this.y), -angle);
                return result;
            }
            var m = dy / dx;
            var mSquared = m * m;
            var aSquared = this.a * this.a;
            var bSquared = this.b * this.b;
            var x = sqrt(1 / ((1 / aSquared) + (mSquared / bSquared)));

            x = dx < 0 ? -x : x;
            var y = m * x;
            result = point(this.x + x, this.y + y);
            if (angle) return result.rotate(point(this.x, this.y), -angle);
            return result;
        }
    };

    // Bezier curve.
    // -------------
    var bezier = {
        // Cubic Bezier curve path through points.
        // Ported from C# implementation by Oleg V. Polikarpotchkin and Peter Lee
        // (http://www.codeproject.com/KB/graphics/BezierSpline.aspx). @param {array} points Array of points through
        // which the smooth line will go. @return {array} SVG Path commands as an array
        curveThroughPoints: function (points) {
            var controlPoints = this.getCurveControlPoints(points);
            var path = ['M', points[0].x, points[0].y];

            for (var i = 0; i < controlPoints[0].length; i++) {
                path.push('C', controlPoints[0][i].x, controlPoints[0][i].y, controlPoints[1][i].x, controlPoints[1][i].y, points[i + 1].x, points[i + 1].y);
            }
            return path;
        },

        // Get open-ended Bezier Spline Control Points.
        // @param knots Input Knot Bezier spline points (At least two points!).
        // @param firstControlPoints Output First Control points. Array of knots.length - 1 length.
        //  @param secondControlPoints Output Second Control points. Array of knots.length - 1 length.
        getCurveControlPoints: function (knots) {
            var firstControlPoints = [];
            var secondControlPoints = [];
            var n = knots.length - 1;
            var i;

            // Special case: Bezier curve should be a straight line.
            if (n == 1) {
                // 3P1 = 2P0 + P3
                firstControlPoints[0] = point((2 * knots[0].x + knots[1].x) / 3,
                    (2 * knots[0].y + knots[1].y) / 3);
                // P2 = 2P1 – P0
                secondControlPoints[0] = point(2 * firstControlPoints[0].x - knots[0].x,
                    2 * firstControlPoints[0].y - knots[0].y);
                return [firstControlPoints, secondControlPoints];
            }

            // Calculate first Bezier control points.
            // Right hand side vector.
            var rhs = [];

            // Set right hand side X values.
            for (i = 1; i < n - 1; i++) {
                rhs[i] = 4 * knots[i].x + 2 * knots[i + 1].x;
            }
            rhs[0] = knots[0].x + 2 * knots[1].x;
            rhs[n - 1] = (8 * knots[n - 1].x + knots[n].x) / 2.0;
            // Get first control points X-values.
            var x = this.getFirstControlPoints(rhs);

            // Set right hand side Y values.
            for (i = 1; i < n - 1; ++i) {
                rhs[i] = 4 * knots[i].y + 2 * knots[i + 1].y;
            }
            rhs[0] = knots[0].y + 2 * knots[1].y;
            rhs[n - 1] = (8 * knots[n - 1].y + knots[n].y) / 2.0;
            // Get first control points Y-values.
            var y = this.getFirstControlPoints(rhs);

            // Fill output arrays.
            for (i = 0; i < n; i++) {
                // First control point.
                firstControlPoints.push(point(x[i], y[i]));
                // Second control point.
                if (i < n - 1) {
                    secondControlPoints.push(point(2 * knots [i + 1].x - x[i + 1],
                        2 * knots[i + 1].y - y[i + 1]));
                } else {
                    secondControlPoints.push(point((knots[n].x + x[n - 1]) / 2,
                        (knots[n].y + y[n - 1]) / 2));
                }
            }
            return [firstControlPoints, secondControlPoints];
        },

        // Solves a tridiagonal system for one of coordinates (x or y) of first Bezier control points.
        // @param rhs Right hand side vector.
        // @return Solution vector.
        getFirstControlPoints: function (rhs) {
            var n = rhs.length;
            // `x` is a solution vector.
            var x = [];
            var tmp = [];
            var b = 2.0;

            x[0] = rhs[0] / b;
            // Decomposition and forward substitution.
            for (var i = 1; i < n; i++) {
                tmp[i] = 1 / b;
                b = (i < n - 1 ? 4.0 : 3.5) - tmp[i];
                x[i] = (rhs[i] - x[i - 1]) / b;
            }
            for (i = 1; i < n; i++) {
                // Backsubstitution.
                x[n - i - 1] -= tmp[n - i] * x[n - i];
            }
            return x;
        },

        // Solves an inversion problem -- Given the (x, y) coordinates of a point which lies on
        // a parametric curve x = x(t)/w(t), y = y(t)/w(t), ﬁnd the parameter value t
        // which corresponds to that point.
        // @param control points (start, control start, control end, end)
        // @return a function accepts a point and returns t.
        getInversionSolver: function (p0, p1, p2, p3) {
            var pts = arguments;

            function l(i, j) {
                // calculates a determinant 3x3
                // [p.x  p.y  1]
                // [pi.x pi.y 1]
                // [pj.x pj.y 1]
                var pi = pts[i], pj = pts[j];
                return function (p) {
                    var w = (i % 3 ? 3 : 1) * (j % 3 ? 3 : 1);
                    var lij = p.x * (pi.y - pj.y) + p.y * (pj.x - pi.x) + pi.x * pj.y - pi.y * pj.x;
                    return w * lij;
                };
            }

            return function solveInversion(p) {
                var ct = 3 * l(2, 3)(p1);
                var c1 = l(1, 3)(p0) / ct;
                var c2 = -l(2, 3)(p0) / ct;
                var la = c1 * l(3, 1)(p) + c2 * (l(3, 0)(p) + l(2, 1)(p)) + l(2, 0)(p);
                var lb = c1 * l(3, 0)(p) + c2 * l(2, 0)(p) + l(1, 0)(p);
                return lb / (lb - la);
            };
        },

        // Divide a Bezier curve into two at point defined by value 't' <0,1>.
        // Using deCasteljau algorithm. http://math.stackexchange.com/a/317867
        // @param control points (start, control start, control end, end)
        // @return a function accepts t and returns 2 curves each defined by 4 control points.
        getCurveDivider: function (p0, p1, p2, p3) {
            return function divideCurve(t) {
                var l = line(p0, p1).pointAt(t);
                var m = line(p1, p2).pointAt(t);
                var n = line(p2, p3).pointAt(t);
                var p = line(l, m).pointAt(t);
                var q = line(m, n).pointAt(t);
                var r = line(p, q).pointAt(t);
                return [{p0: p0, p1: l, p2: p, p3: r}, {p0: r, p1: q, p2: n, p3: p3}];
            }
        }
    };

    // Scale.
    var scale = {

        // Return the `value` from the `domain` interval scaled to the `range` interval.
        linear: function (domain, range, value) {

            var domainSpan = domain[1] - domain[0];
            var rangeSpan = range[1] - range[0];
            return (((value - domain[0]) / domainSpan) * rangeSpan + range[0]) || 0;
        }
    };

    return {

        toDeg: toDeg,
        toRad: toRad,
        snapToGrid: snapToGrid,
        normalizeAngle: normalizeAngle,
        point: point,
        line: line,
        rect: rect,
        ellipse: ellipse,
        bezier: bezier,
        scale: scale
    }
}));

//      JointJS library.
//      (c) 2011-2013 client IO

if (typeof exports === 'object') {

    var _ = require('lodash');
}

// Global namespace.

var joint = {

    version: '0.9.2',

    // `joint.dia` namespace.
    dia: {},

    // `joint.ui` namespace.
    ui: {},

    // `joint.layout` namespace.
    layout: {},

    // `joint.shapes` namespace.
    shapes: {},

    // `joint.format` namespace.
    format: {},

    // `joint.connectors` namespace.
    connectors: {},

    // `joint.routers` namespace.
    routers: {},

    util: {

        // Return a simple hash code from a string. See
        // http://werxltd.com/wp/2010/05/13/javascript-implementation-of-javas-string-hashcode-method/.
        hashCode: function (str) {

            var hash = 0;
            if (str.length == 0) return hash;
            for (var i = 0; i < str.length; i++) {
                var c = str.charCodeAt(i);
                hash = ((hash << 5) - hash) + c;
                hash = hash & hash; // Convert to 32bit integer
            }
            return hash;
        },

        getByPath: function (obj, path, delim) {

            delim = delim || '.';
            var keys = path.split(delim);
            var key;

            while (keys.length) {
                key = keys.shift();
                if (key in obj) {
                    obj = obj[key];
                } else {
                    return undefined;
                }
            }
            return obj;
        },

        setByPath: function (obj, path, value, delim) {

            delim = delim || '.';

            var keys = path.split(delim);
            var diver = obj;
            var i = 0;

            if (path.indexOf(delim) > -1) {

                for (var len = keys.length; i < len - 1; i++) {
                    // diver creates an empty object if there is no nested object under such a key.
                    // This means that one can populate an empty nested object with setByPath().
                    diver = diver[keys[i]] || (diver[keys[i]] = {});
                }
                diver[keys[len - 1]] = value;
            } else {
                obj[path] = value;
            }
            return obj;
        },

        unsetByPath: function (obj, path, delim) {

            delim = delim || '.';

            // index of the last delimiter
            var i = path.lastIndexOf(delim);

            if (i > -1) {

                // unsetting a nested attribute
                var parent = joint.util.getByPath(obj, path.substr(0, i), delim);

                if (parent) {

                    delete parent[path.slice(i + 1)];
                }

            } else {

                // unsetting a primitive attribute
                delete obj[path];
            }

            return obj;
        },

        flattenObject: function (obj, delim, stop) {

            delim = delim || '.';
            var ret = {};

            for (var key in obj) {
                if (!obj.hasOwnProperty(key)) continue;

                var shouldGoDeeper = typeof obj[key] === 'object';
                if (shouldGoDeeper && stop && stop(obj[key])) {
                    shouldGoDeeper = false;
                }

                if (shouldGoDeeper) {
                    var flatObject = this.flattenObject(obj[key], delim, stop);
                    for (var flatKey in flatObject) {
                        if (!flatObject.hasOwnProperty(flatKey)) continue;

                        ret[key + delim + flatKey] = flatObject[flatKey];
                    }
                } else {
                    ret[key] = obj[key];
                }
            }
            return ret;
        },

        uuid: function () {

            // credit: http://stackoverflow.com/posts/2117523/revisions

            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },

        // Generate global unique id for obj and store it as a property of the object.
        guid: function (obj) {

            this.guid.id = this.guid.id || 1;
            obj.id = (obj.id === undefined ? 'j_' + this.guid.id++ : obj.id);
            return obj.id;
        },

        // Copy all the properties to the first argument from the following arguments.
        // All the properties will be overwritten by the properties from the following
        // arguments. Inherited properties are ignored.
        mixin: function () {

            var target = arguments[0];

            for (var i = 1, l = arguments.length; i < l; i++) {

                var extension = arguments[i];

                // Only functions and objects can be mixined.

                if ((Object(extension) !== extension) && !_.isFunction(extension) &&
                    (extension === null || extension === undefined)) {

                    continue;
                }

                _.each(extension, function (copy, key) {

                    if (this.mixin.deep && (Object(copy) === copy)) {

                        if (!target[key]) {

                            target[key] = _.isArray(copy) ? [] : {};
                        }

                        this.mixin(target[key], copy);
                        return;
                    }

                    if (target[key] !== copy) {

                        if (!this.mixin.supplement || !target.hasOwnProperty(key)) {

                            target[key] = copy;
                        }

                    }

                }, this);
            }

            return target;
        },

        // Copy all properties to the first argument from the following
        // arguments only in case if they don't exists in the first argument.
        // All the function propererties in the first argument will get
        // additional property base pointing to the extenders same named
        // property function's call method.
        supplement: function () {

            this.mixin.supplement = true;
            var ret = this.mixin.apply(this, arguments);
            this.mixin.supplement = false;
            return ret;
        },

        // Same as `mixin()` but deep version.
        deepMixin: function () {

            this.mixin.deep = true;
            var ret = this.mixin.apply(this, arguments);
            this.mixin.deep = false;
            return ret;
        },

        // Same as `supplement()` but deep version.
        deepSupplement: function () {

            this.mixin.deep = this.mixin.supplement = true;
            var ret = this.mixin.apply(this, arguments);
            this.mixin.deep = this.mixin.supplement = false;
            return ret;
        },

        normalizeEvent: function (evt) {

            return (evt.originalEvent && evt.originalEvent.changedTouches && evt.originalEvent.changedTouches.length) ? evt.originalEvent.changedTouches[0] : evt;
        },

        nextFrame: (function () {

            var raf;
            var client = typeof window != 'undefined';

            if (client) {

                raf = window.requestAnimationFrame ||
                    window.webkitRequestAnimationFrame ||
                    window.mozRequestAnimationFrame ||
                    window.oRequestAnimationFrame ||
                    window.msRequestAnimationFrame;

            }

            if (!raf) {

                var lastTime = 0;

                raf = function (callback) {

                    var currTime = new Date().getTime();
                    var timeToCall = Math.max(0, 16 - (currTime - lastTime));
                    var id = setTimeout(function () { callback(currTime + timeToCall); }, timeToCall);
                    lastTime = currTime + timeToCall;
                    return id;

                };
            }

            return client ? _.bind(raf, window) : raf;
        })(),

        cancelFrame: (function () {

            var caf;
            var client = typeof window != 'undefined';

            if (client) {

                caf = window.cancelAnimationFrame ||
                    window.webkitCancelAnimationFrame ||
                    window.webkitCancelRequestAnimationFrame ||
                    window.msCancelAnimationFrame ||
                    window.msCancelRequestAnimationFrame ||
                    window.oCancelAnimationFrame ||
                    window.oCancelRequestAnimationFrame ||
                    window.mozCancelAnimationFrame ||
                    window.mozCancelRequestAnimationFrame;

            }

            caf = caf || clearTimeout;

            return client ? _.bind(caf, window) : caf;
        })(),

        breakText: function (text, size, styles, opt) {

            opt = opt || {};

            var width = size.width;
            var height = size.height;

            var svgDocument = opt.svgDocument || V('svg').node;
            var textElement = V('<text><tspan></tspan></text>').attr(styles || {}).node;
            var textSpan = textElement.firstChild;
            var textNode = document.createTextNode('');

            textSpan.appendChild(textNode);

            svgDocument.appendChild(textElement);

            if (!opt.svgDocument) {

                document.body.appendChild(svgDocument);
            }

            var words = text.split(' ');
            var full = [];
            var lines = [];
            var p;

            for (var i = 0, l = 0, len = words.length; i < len; i++) {

                var word = words[i];

                textNode.data = lines[l] ? lines[l] + ' ' + word : word;

                if (textSpan.getComputedTextLength() <= width) {

                    // the current line fits
                    lines[l] = textNode.data;

                    if (p) {
                        // We were partitioning. Put rest of the word onto next line
                        full[l++] = true;

                        // cancel partitioning
                        p = 0;
                    }

                } else {

                    if (!lines[l] || p) {

                        var partition = !!p;

                        p = word.length - 1;

                        if (partition || !p) {

                            // word has only one character.
                            if (!p) {

                                if (!lines[l]) {

                                    // we won't fit this text within our rect
                                    lines = [];

                                    break;
                                }

                                // partitioning didn't help on the non-empty line
                                // try again, but this time start with a new line

                                // cancel partitions created
                                words.splice(i, 2, word + words[i + 1]);

                                // adjust word length
                                len--;

                                full[l++] = true;
                                i--;

                                continue;
                            }

                            // move last letter to the beginning of the next word
                            words[i] = word.substring(0, p);
                            words[i + 1] = word.substring(p) + words[i + 1];

                        } else {

                            // We initiate partitioning
                            // split the long word into two words
                            words.splice(i, 1, word.substring(0, p), word.substring(p));

                            // adjust words length
                            len++;

                            if (l && !full[l - 1]) {
                                // if the previous line is not full, try to fit max part of
                                // the current word there
                                l--;
                            }
                        }

                        i--;

                        continue;
                    }

                    l++;
                    i--;
                }

                // if size.height is defined we have to check whether the height of the entire
                // text exceeds the rect height
                if (typeof height !== 'undefined') {

                    // get line height as text height / 0.8 (as text height is approx. 0.8em
                    // and line height is 1em. See vectorizer.text())
                    var lh = lh || textElement.getBBox().height * 1.25;

                    if (lh * lines.length > height) {

                        // remove overflowing lines
                        lines.splice(Math.floor(height / lh));

                        break;
                    }
                }
            }

            if (opt.svgDocument) {

                // svg document was provided, remove the text element only
                svgDocument.removeChild(textElement);

            } else {

                // clean svg document
                document.body.removeChild(svgDocument);
            }

            return lines.join('\n');
        },

        imageToDataUri: function (url, callback) {

            if (url.substr(0, 'data:'.length) === 'data:') {
                // No need to convert to data uri if it is already in data uri.

                // This not only convenient but desired. For example, 
                // IE throws a security error if data:image/svg+xml is used to render
                // an image to the canvas and an attempt is made to read out data uri.
                // Now if our image is already in data uri, there is no need to render it to the canvas
                // and so we can bypass this error.

                // Keep the async nature of the function.
                return setTimeout(function () { callback(null, url) }, 0);
            }

            var canvas = document.createElement('canvas');
            var img = document.createElement('img');

            img.onload = function () {

                var ctx = canvas.getContext('2d');

                canvas.width = img.width;
                canvas.height = img.height;

                ctx.drawImage(img, 0, 0);

                try {

                    // Guess the type of the image from the url suffix.
                    var suffix = (url.split('.').pop()) || 'png';
                    // A little correction for JPEGs. There is no image/jpg mime type but image/jpeg.
                    var type = 'image/' + (suffix === 'jpg') ? 'jpeg' : suffix;
                    var dataUri = canvas.toDataURL(type);

                } catch (e) {

                    if (/\.svg$/.test(url)) {
                        // IE throws a security error if we try to render an SVG into the canvas.
                        // Luckily for us, we don't need canvas at all to convert
                        // SVG to data uri. We can just use AJAX to load the SVG string
                        // and construct the data uri ourselves.
                        var xhr = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject('Microsoft.XMLHTTP');
                        xhr.open('GET', url, false);
                        xhr.send(null);
                        var svg = xhr.responseText;

                        return callback(null, 'data:image/svg+xml,' + encodeURIComponent(svg));
                    }

                    console.error(img.src, 'fails to convert', e);
                }

                callback(null, dataUri);
            };

            img.ononerror = function () {

                callback(new Error('Failed to load image.'));
            };

            img.src = url;
        },

        timing: {

            linear: function (t) {
                return t;
            },

            quad: function (t) {
                return t * t;
            },

            cubic: function (t) {
                return t * t * t;
            },

            inout: function (t) {
                if (t <= 0) return 0;
                if (t >= 1) return 1;
                var t2 = t * t, t3 = t2 * t;
                return 4 * (t < .5 ? t3 : 3 * (t - t2) + t3 - .75);
            },

            exponential: function (t) {
                return Math.pow(2, 10 * (t - 1));
            },

            bounce: function (t) {
                for (var a = 0, b = 1; 1; a += b, b /= 2) {
                    if (t >= (7 - 4 * a) / 11) {
                        var q = (11 - 6 * a - 11 * t) / 4;
                        return -q * q + b * b;
                    }
                }
            },

            reverse: function (f) {
                return function (t) {
                    return 1 - f(1 - t)
                }
            },

            reflect: function (f) {
                return function (t) {
                    return .5 * (t < .5 ? f(2 * t) : (2 - f(2 - 2 * t)));
                };
            },

            clamp: function (f, n, x) {
                n = n || 0;
                x = x || 1;
                return function (t) {
                    var r = f(t);
                    return r < n ? n : r > x ? x : r;
                }
            },

            back: function (s) {
                if (!s) s = 1.70158;
                return function (t) {
                    return t * t * ((s + 1) * t - s);
                };
            },

            elastic: function (x) {
                if (!x) x = 1.5;
                return function (t) {
                    return Math.pow(2, 10 * (t - 1)) * Math.cos(20 * Math.PI * x / 3 * t);
                }
            }

        },

        interpolate: {

            number: function (a, b) {
                var d = b - a;
                return function (t) { return a + d * t; };
            },

            object: function (a, b) {
                var s = _.keys(a);
                return function (t) {
                    var i, p, r = {};
                    for (i = s.length - 1; i != -1; i--) {
                        p = s[i];
                        r[p] = a[p] + (b[p] - a[p]) * t;
                    }
                    return r;
                }
            },

            hexColor: function (a, b) {

                var ca = parseInt(a.slice(1), 16), cb = parseInt(b.slice(1), 16);

                var ra = ca & 0x0000ff, rd = (cb & 0x0000ff) - ra;
                var ga = ca & 0x00ff00, gd = (cb & 0x00ff00) - ga;
                var ba = ca & 0xff0000, bd = (cb & 0xff0000) - ba;

                return function (t) {
                    var r = (ra + rd * t) & 0x000000ff;
                    var g = (ga + gd * t) & 0x0000ff00;
                    var b = (ba + bd * t) & 0x00ff0000;
                    return '#' + (1 << 24 | r | g | b ).toString(16).slice(1);
                };
            },

            unit: function (a, b) {

                var r = /(-?[0-9]*.[0-9]*)(px|em|cm|mm|in|pt|pc|%)/;

                var ma = r.exec(a), mb = r.exec(b);
                var p = mb[1].indexOf('.'), f = p > 0 ? mb[1].length - p - 1 : 0;
                var a = +ma[1], d = +mb[1] - a, u = ma[2];

                return function (t) {
                    return (a + d * t).toFixed(f) + u;
                }
            }
        },

        // SVG filters.
        filter: {

            // `x` ... horizontal blur
            // `y` ... vertical blur (optional)
            blur: function (args) {

                var x = _.isFinite(args.x) ? args.x : 2;

                return _.template('<filter><feGaussianBlur stdDeviation="${stdDeviation}"/></filter>', {
                    stdDeviation: _.isFinite(args.y) ? [x, args.y] : x
                });
            },

            // `dx` ... horizontal shift
            // `dy` ... vertical shift
            // `blur` ... blur
            // `color` ... color
            // `opacity` ... opacity
            dropShadow: function (args) {

                var tpl = 'SVGFEDropShadowElement' in window
                    ? '<filter><feDropShadow stdDeviation="${blur}" dx="${dx}" dy="${dy}" flood-color="${color}" flood-opacity="${opacity}"/></filter>'
                    : '<filter><feGaussianBlur in="SourceAlpha" stdDeviation="${blur}"/><feOffset dx="${dx}" dy="${dy}" result="offsetblur"/><feFlood flood-color="${color}"/><feComposite in2="offsetblur" operator="in"/><feComponentTransfer><feFuncA type="linear" slope="${opacity}"/></feComponentTransfer><feMerge><feMergeNode/><feMergeNode in="SourceGraphic"/></feMerge></filter>';

                return _.template(tpl, {
                    dx: args.dx || 0,
                    dy: args.dy || 0,
                    opacity: _.isFinite(args.opacity) ? args.opacity : 1,
                    color: args.color || 'black',
                    blur: _.isFinite(args.blur) ? args.blur : 4
                });
            },

            // `amount` ... the proportion of the conversion. A value of 1 is completely grayscale. A value of 0 leaves
            // the input unchanged.
            grayscale: function (args) {

                var amount = _.isFinite(args.amount) ? args.amount : 1;

                return _.template('<filter><feColorMatrix type="matrix" values="${a} ${b} ${c} 0 0 ${d} ${e} ${f} 0 0 ${g} ${b} ${h} 0 0 0 0 0 1 0"/></filter>', {
                    a: 0.2126 + 0.7874 * (1 - amount),
                    b: 0.7152 - 0.7152 * (1 - amount),
                    c: 0.0722 - 0.0722 * (1 - amount),
                    d: 0.2126 - 0.2126 * (1 - amount),
                    e: 0.7152 + 0.2848 * (1 - amount),
                    f: 0.0722 - 0.0722 * (1 - amount),
                    g: 0.2126 - 0.2126 * (1 - amount),
                    h: 0.0722 + 0.9278 * (1 - amount)
                });
            },

            // `amount` ... the proportion of the conversion. A value of 1 is completely sepia. A value of 0 leaves the
            // input unchanged.
            sepia: function (args) {

                var amount = _.isFinite(args.amount) ? args.amount : 1;

                return _.template('<filter><feColorMatrix type="matrix" values="${a} ${b} ${c} 0 0 ${d} ${e} ${f} 0 0 ${g} ${h} ${i} 0 0 0 0 0 1 0"/></filter>', {
                    a: 0.393 + 0.607 * (1 - amount),
                    b: 0.769 - 0.769 * (1 - amount),
                    c: 0.189 - 0.189 * (1 - amount),
                    d: 0.349 - 0.349 * (1 - amount),
                    e: 0.686 + 0.314 * (1 - amount),
                    f: 0.168 - 0.168 * (1 - amount),
                    g: 0.272 - 0.272 * (1 - amount),
                    h: 0.534 - 0.534 * (1 - amount),
                    i: 0.131 + 0.869 * (1 - amount)
                });
            },

            // `amount` ... the proportion of the conversion. A value of 0 is completely un-saturated. A value of 1
            // leaves the input unchanged.
            saturate: function (args) {

                var amount = _.isFinite(args.amount) ? args.amount : 1;

                return _.template('<filter><feColorMatrix type="saturate" values="${amount}"/></filter>', {
                    amount: 1 - amount
                });
            },

            // `angle` ...  the number of degrees around the color circle the input samples will be adjusted.
            hueRotate: function (args) {

                return _.template('<filter><feColorMatrix type="hueRotate" values="${angle}"/></filter>', {
                    angle: args.angle || 0
                });
            },

            // `amount` ... the proportion of the conversion. A value of 1 is completely inverted. A value of 0 leaves
            // the input unchanged.
            invert: function (args) {

                var amount = _.isFinite(args.amount) ? args.amount : 1;

                return _.template('<filter><feComponentTransfer><feFuncR type="table" tableValues="${amount} ${amount2}"/><feFuncG type="table" tableValues="${amount} ${amount2}"/><feFuncB type="table" tableValues="${amount} ${amount2}"/></feComponentTransfer></filter>', {
                    amount: amount,
                    amount2: 1 - amount
                });
            },

            // `amount` ... proportion of the conversion. A value of 0 will create an image that is completely black. A
            // value of 1 leaves the input unchanged.
            brightness: function (args) {

                return _.template('<filter><feComponentTransfer><feFuncR type="linear" slope="${amount}"/><feFuncG type="linear" slope="${amount}"/><feFuncB type="linear" slope="${amount}"/></feComponentTransfer></filter>', {
                    amount: _.isFinite(args.amount) ? args.amount : 1
                });
            },

            // `amount` ... proportion of the conversion. A value of 0 will create an image that is completely black. A
            // value of 1 leaves the input unchanged.
            contrast: function (args) {

                var amount = _.isFinite(args.amount) ? args.amount : 1;

                return _.template('<filter><feComponentTransfer><feFuncR type="linear" slope="${amount}" intercept="${amount2}"/><feFuncG type="linear" slope="${amount}" intercept="${amount2}"/><feFuncB type="linear" slope="${amount}" intercept="${amount2}"/></feComponentTransfer></filter>', {
                    amount: amount,
                    amount2: .5 - amount / 2
                });
            }
        },

        format: {

            // Formatting numbers via the Python Format Specification Mini-language.
            // See http://docs.python.org/release/3.1.3/library/string.html#format-specification-mini-language.
            // Heavilly inspired by the D3.js library implementation.
            number: function (specifier, value, locale) {

                locale = locale || {

                        currency: ['$', ''],
                        decimal: '.',
                        thousands: ',',
                        grouping: [3]
                    };

                // See Python format specification mini-language:
                // http://docs.python.org/release/3.1.3/library/string.html#format-specification-mini-language.
                // [[fill]align][sign][symbol][0][width][,][.precision][type]
                var re = /(?:([^{])?([<>=^]))?([+\- ])?([$#])?(0)?(\d+)?(,)?(\.-?\d+)?([a-z%])?/i;

                var match = re.exec(specifier);
                var fill = match[1] || ' ';
                var align = match[2] || '>';
                var sign = match[3] || '';
                var symbol = match[4] || '';
                var zfill = match[5];
                var width = +match[6];
                var comma = match[7];
                var precision = match[8];
                var type = match[9];
                var scale = 1;
                var prefix = '';
                var suffix = '';
                var integer = false;

                if (precision) precision = +precision.substring(1);

                if (zfill || fill === '0' && align === '=') {
                    zfill = fill = '0';
                    align = '=';
                    if (comma) width -= Math.floor((width - 1) / 4);
                }

                switch (type) {
                    case 'n':
                        comma = true;
                        type = 'g';
                        break;
                    case '%':
                        scale = 100;
                        suffix = '%';
                        type = 'f';
                        break;
                    case 'p':
                        scale = 100;
                        suffix = '%';
                        type = 'r';
                        break;
                    case 'b':
                    case 'o':
                    case 'x':
                    case 'X':
                        if (symbol === '#') prefix = '0' + type.toLowerCase();
                    case 'c':
                    case 'd':
                        integer = true;
                        precision = 0;
                        break;
                    case 's':
                        scale = -1;
                        type = 'r';
                        break;
                }

                if (symbol === '$') {
                    prefix = locale.currency[0];
                    suffix = locale.currency[1];
                }

                // If no precision is specified for `'r'`, fallback to general notation.
                if (type == 'r' && !precision) type = 'g';

                // Ensure that the requested precision is in the supported range.
                if (precision != null) {
                    if (type == 'g') precision = Math.max(1, Math.min(21, precision));
                    else if (type == 'e' || type == 'f') precision = Math.max(0, Math.min(20, precision));
                }

                var zcomma = zfill && comma;

                // Return the empty string for floats formatted as ints.
                if (integer && (value % 1)) return '';

                // Convert negative to positive, and record the sign prefix.
                var negative = value < 0 || value === 0 && 1 / value < 0 ? (value = -value, '-') : sign;

                var fullSuffix = suffix;

                // Apply the scale, computing it from the value's exponent for si format.
                // Preserve the existing suffix, if any, such as the currency symbol.
                if (scale < 0) {
                    var unit = this.prefix(value, precision);
                    value = unit.scale(value);
                    fullSuffix = unit.symbol + suffix;
                } else {
                    value *= scale;
                }

                // Convert to the desired precision.
                value = this.convert(type, value, precision);

                // Break the value into the integer part (before) and decimal part (after).
                var i = value.lastIndexOf('.');
                var before = i < 0 ? value : value.substring(0, i);
                var after = i < 0 ? '' : locale.decimal + value.substring(i + 1);

                function formatGroup(value) {

                    var i = value.length;
                    var t = [];
                    var j = 0;
                    var g = locale.grouping[0];
                    while (i > 0 && g > 0) {
                        t.push(value.substring(i -= g, i + g));
                        g = locale.grouping[j = (j + 1) % locale.grouping.length];
                    }
                    return t.reverse().join(locale.thousands);
                }

                // If the fill character is not `'0'`, grouping is applied before padding.
                if (!zfill && comma && locale.grouping) {

                    before = formatGroup(before);
                }

                var length = prefix.length + before.length + after.length + (zcomma ? 0 : negative.length);
                var padding = length < width ? new Array(length = width - length + 1).join(fill) : '';

                // If the fill character is `'0'`, grouping is applied after padding.
                if (zcomma) before = formatGroup(padding + before);

                // Apply prefix.
                negative += prefix;

                // Rejoin integer and decimal parts.
                value = before + after;

                return (align === '<' ? negative + value + padding
                        : align === '>' ? padding + negative + value
                        : align === '^' ? padding.substring(0, length >>= 1) + negative + value + padding.substring(length)
                        : negative + (zcomma ? value : padding + value)) + fullSuffix;
            },

            // Formatting string via the Python Format string.
            // See https://docs.python.org/2/library/string.html#format-string-syntax)
            string: function (formatString, value) {
                var fieldDelimiterIndex;
                var fieldDelimiter = '{';
                var endPlaceholder = false;
                var formattedStringArray = [];

                while ((fieldDelimiterIndex = formatString.indexOf(fieldDelimiter)) !== -1) {

                    var pieceFormatedString, formatSpec, fieldName;

                    pieceFormatedString = formatString.slice(0, fieldDelimiterIndex);

                    if (endPlaceholder) {
                        formatSpec = pieceFormatedString.split(":");
                        fieldName = formatSpec.shift().split(".");
                        pieceFormatedString = value;

                        for (var i = 0; i < fieldName.length; i++)
                            pieceFormatedString = pieceFormatedString[fieldName[i]];

                        if (formatSpec.length)
                            pieceFormatedString = this.number(formatSpec, pieceFormatedString);
                    }

                    formattedStringArray.push(pieceFormatedString);

                    formatString = formatString.slice(fieldDelimiterIndex + 1);
                    fieldDelimiter = (endPlaceholder = !endPlaceholder) ? '}' : '{'
                }
                formattedStringArray.push(formatString);

                return formattedStringArray.join('')
            },

            convert: function (type, value, precision) {

                switch (type) {
                    case 'b':
                        return value.toString(2);
                    case 'c':
                        return String.fromCharCode(value);
                    case 'o':
                        return value.toString(8);
                    case 'x':
                        return value.toString(16);
                    case 'X':
                        return value.toString(16).toUpperCase();
                    case 'g':
                        return value.toPrecision(precision);
                    case 'e':
                        return value.toExponential(precision);
                    case 'f':
                        return value.toFixed(precision);
                    case 'r':
                        return (value = this.round(value, this.precision(value, precision))).toFixed(Math.max(0, Math.min(20, this.precision(value * (1 + 1e-15), precision))));
                    default:
                        return value + '';
                }
            },

            round: function (value, precision) {

                return precision
                    ? Math.round(value * (precision = Math.pow(10, precision))) / precision
                    : Math.round(value);
            },

            precision: function (value, precision) {

                return precision - (value ? Math.ceil(Math.log(value) / Math.LN10) : 1);
            },

            prefix: function (value, precision) {

                var prefixes = _.map(['y', 'z', 'a', 'f', 'p', 'n', 'µ', 'm', '', 'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y'], function (d, i) {
                    var k = Math.pow(10, abs(8 - i) * 3);
                    return {
                        scale: i > 8 ? function (d) { return d / k; } : function (d) { return d * k; },
                        symbol: d
                    };
                });

                var i = 0;
                if (value) {
                    if (value < 0) value *= -1;
                    if (precision) value = d3.round(value, this.precision(value, precision));
                    i = 1 + Math.floor(1e-12 + Math.log(value) / Math.LN10);
                    i = Math.max(-24, Math.min(24, Math.floor((i <= 0 ? i + 1 : i - 1) / 3) * 3));
                }
                return prefixes[8 + i / 3];
            }
        }
    }
};

if (typeof exports === 'object') {

    module.exports = joint;
}

//      JointJS, the JavaScript diagramming library.
//      (c) 2011-2013 client IO

if (typeof exports === 'object') {

    var joint = {
        dia: {
            Link: require('./joint.dia.link').Link,
            Element: require('./joint.dia.element').Element
        },
        shapes: require('../plugins/shapes')
    };
    var Backbone = require('backbone');
    var _ = require('lodash');
    var g = require('./geometry');
}

joint.dia.GraphCells = Backbone.Collection.extend({

    initialize: function () {

        // Backbone automatically doesn't trigger re-sort if models attributes are changed later when
        // they're already in the collection. Therefore, we're triggering sort manually here.
        this.on('change:z', this.sort, this);
    },

    model: function (attrs, options) {

        if (attrs.type === 'link') {

            return new joint.dia.Link(attrs, options);
        }

        var module = attrs.type.split('.')[0];
        var entity = attrs.type.split('.')[1];

        if (joint.shapes[module] && joint.shapes[module][entity]) {

            return new joint.shapes[module][entity](attrs, options);
        }

        return new joint.dia.Element(attrs, options);
    },

    // `comparator` makes it easy to sort cells based on their `z` index.
    comparator: function (model) {

        return model.get('z') || 0;
    },

    // Get all inbound and outbound links connected to the cell `model`.
    getConnectedLinks: function (model, opt) {

        opt = opt || {};

        if (_.isUndefined(opt.inbound) && _.isUndefined(opt.outbound)) {
            opt.inbound = opt.outbound = true;
        }

        var links = [];

        this.each(function (cell) {

            var source = cell.get('source');
            var target = cell.get('target');

            if (source && source.id === model.id && opt.outbound) {

                links.push(cell);
            }

            if (target && target.id === model.id && opt.inbound) {

                links.push(cell);
            }
        });

        return links;
    }
});

joint.dia.Graph = Backbone.Model.extend({

    initialize: function () {

        this.set('cells', new joint.dia.GraphCells);

        // Make all the events fired in the `cells` collection available.
        // to the outside world.
        this.get('cells').on('all', this.trigger, this);

        this.get('cells').on('remove', this.removeCell, this);
    },

    toJSON: function () {

        // Backbone does not recursively call `toJSON()` on attributes that are themselves models/collections.
        // It just clones the attributes. Therefore, we must call `toJSON()` on the cells collection explicitely.
        var json = Backbone.Model.prototype.toJSON.apply(this, arguments);
        json.cells = this.get('cells').toJSON();
        return json;
    },

    fromJSON: function (json, opt) {

        if (!json.cells) {

            throw new Error('Graph JSON must contain cells array.');
        }

        this.set(_.omit(json, 'cells'), opt);
        this.resetCells(json.cells, opt);
    },

    clear: function (opt) {

        this.trigger('batch:start');
        this.get('cells').remove(this.get('cells').models, opt);
        this.trigger('batch:stop');
    },

    _prepareCell: function (cell) {

        if (cell instanceof Backbone.Model && _.isUndefined(cell.get('z'))) {

            cell.set('z', this.maxZIndex() + 1, {silent: true});

        } else if (_.isUndefined(cell.z)) {

            cell.z = this.maxZIndex() + 1;
        }

        return cell;
    },

    maxZIndex: function () {

        var lastCell = this.get('cells').last();
        return lastCell ? (lastCell.get('z') || 0) : 0;
    },

    addCell: function (cell, options) {

        if (_.isArray(cell)) {

            return this.addCells(cell, options);
        }

        this.get('cells').add(this._prepareCell(cell), options || {});

        return this;
    },

    addCells: function (cells, options) {

        options = options || {};
        options.position = cells.length;

        _.each(cells, function (cell) {
            options.position--;
            this.addCell(cell, options);
        }, this);

        return this;
    },

    // When adding a lot of cells, it is much more efficient to
    // reset the entire cells collection in one go.
    // Useful for bulk operations and optimizations.
    resetCells: function (cells, opt) {

        this.get('cells').reset(_.map(cells, this._prepareCell, this), opt);

        return this;
    },

    removeCell: function (cell, collection, options) {

        // Applications might provide a `disconnectLinks` option set to `true` in order to
        // disconnect links when a cell is removed rather then removing them. The default
        // is to remove all the associated links.
        if (options && options.disconnectLinks) {

            this.disconnectLinks(cell);

        } else {

            this.removeLinks(cell);
        }

        // Silently remove the cell from the cells collection. Silently, because
        // `joint.dia.Cell.prototype.remove` already triggers the `remove` event which is
        // then propagated to the graph model. If we didn't remove the cell silently, two `remove` events
        // would be triggered on the graph model.
        this.get('cells').remove(cell, {silent: true});
    },

    // Get a cell by `id`.
    getCell: function (id) {

        return this.get('cells').get(id);
    },

    getElements: function () {

        return this.get('cells').filter(function (cell) {

            return cell instanceof joint.dia.Element;
        });
    },

    getLinks: function () {

        return this.get('cells').filter(function (cell) {

            return cell instanceof joint.dia.Link;
        });
    },

    // Get all inbound and outbound links connected to the cell `model`.
    getConnectedLinks: function (model, opt) {

        return this.get('cells').getConnectedLinks(model, opt);
    },

    getNeighbors: function (el) {

        var links = this.getConnectedLinks(el);
        var neighbors = [];
        var cells = this.get('cells');

        _.each(links, function (link) {

            var source = link.get('source');
            var target = link.get('target');

            // Discard if it is a point.
            if (!source.x) {
                var sourceElement = cells.get(source.id);
                if (sourceElement !== el) {

                    neighbors.push(sourceElement);
                }
            }
            if (!target.x) {
                var targetElement = cells.get(target.id);
                if (targetElement !== el) {

                    neighbors.push(targetElement);
                }
            }
        });

        return neighbors;
    },

    // Disconnect links connected to the cell `model`.
    disconnectLinks: function (model) {

        _.each(this.getConnectedLinks(model), function (link) {

            link.set(link.get('source').id === model.id ? 'source' : 'target', g.point(0, 0));
        });
    },

    // Remove links connected to the cell `model` completely.
    removeLinks: function (model) {

        _.invoke(this.getConnectedLinks(model), 'remove');
    },

    // Find all views at given point
    findModelsFromPoint: function (p) {

        return _.filter(this.getElements(), function (el) {
            return el.getBBox().containsPoint(p);
        });
    },

    // Find all views in given area
    findModelsInArea: function (r) {

        return _.filter(this.getElements(), function (el) {
            return el.getBBox().intersect(r);
        });
    },

    // Return the bounding box of all `elements`.
    getBBox: function (elements) {

        var origin = {x: Infinity, y: Infinity};
        var corner = {x: 0, y: 0};

        _.each(elements, function (cell) {

            var bbox = cell.getBBox();
            origin.x = Math.min(origin.x, bbox.x);
            origin.y = Math.min(origin.y, bbox.y);
            corner.x = Math.max(corner.x, bbox.x + bbox.width);
            corner.y = Math.max(corner.y, bbox.y + bbox.height);
        });

        return g.rect(origin.x, origin.y, corner.x - origin.x, corner.y - origin.y);
    }

});

if (typeof exports === 'object') {

    module.exports.Graph = joint.dia.Graph;
}

//      JointJS.
//      (c) 2011-2013 client IO

if (typeof exports === 'object') {

    var joint = {
        util: require('./core').util,
        dia: {
            Link: require('./joint.dia.link').Link
        }
    };
    var Backbone = require('backbone');
    var _ = require('lodash');
}

// joint.dia.Cell base model.
// --------------------------

joint.dia.Cell = Backbone.Model.extend({

    // This is the same as Backbone.Model with the only difference that is uses _.merge
    // instead of just _.extend. The reason is that we want to mixin attributes set in upper classes.
    constructor: function (attributes, options) {

        var defaults;
        var attrs = attributes || {};
        this.cid = _.uniqueId('c');
        this.attributes = {};
        if (options && options.collection) this.collection = options.collection;
        if (options && options.parse) attrs = this.parse(attrs, options) || {};
        if (defaults = _.result(this, 'defaults')) {
            //<custom code>
            // Replaced the call to _.defaults with _.merge.
            attrs = _.merge({}, defaults, attrs);
            //</custom code>
        }
        this.set(attrs, options);
        this.changed = {};
        this.initialize.apply(this, arguments);
    },

    toJSON: function () {

        var defaultAttrs = this.constructor.prototype.defaults.attrs || {};
        var attrs = this.attributes.attrs;
        var finalAttrs = {};

        // Loop through all the attributes and
        // omit the default attributes as they are implicitly reconstructable by the cell 'type'.
        _.each(attrs, function (attr, selector) {

            var defaultAttr = defaultAttrs[selector];

            _.each(attr, function (value, name) {

                // attr is mainly flat though it might have one more level (consider the `style` attribute).
                // Check if the `value` is object and if yes, go one level deep.
                if (_.isObject(value) && !_.isArray(value)) {

                    _.each(value, function (value2, name2) {

                        if (!defaultAttr || !defaultAttr[name] || !_.isEqual(defaultAttr[name][name2], value2)) {

                            finalAttrs[selector] = finalAttrs[selector] || {};
                            (finalAttrs[selector][name] || (finalAttrs[selector][name] = {}))[name2] = value2;
                        }
                    });

                } else if (!defaultAttr || !_.isEqual(defaultAttr[name], value)) {
                    // `value` is not an object, default attribute for such a selector does not exist
                    // or it is different than the attribute value set on the model.

                    finalAttrs[selector] = finalAttrs[selector] || {};
                    finalAttrs[selector][name] = value;
                }
            });
        });

        var attributes = _.cloneDeep(_.omit(this.attributes, 'attrs'));
        //var attributes = JSON.parse(JSON.stringify(_.omit(this.attributes, 'attrs')));
        attributes.attrs = finalAttrs;

        return attributes;
    },

    initialize: function (options) {

        if (!options || !options.id) {

            this.set('id', joint.util.uuid(), {silent: true});
        }

        this._transitionIds = {};

        // Collect ports defined in `attrs` and keep collecting whenever `attrs` object changes.
        this.processPorts();
        this.on('change:attrs', this.processPorts, this);
    },

    processPorts: function () {

        // Whenever `attrs` changes, we extract ports from the `attrs` object and store it
        // in a more accessible way. Also, if any port got removed and there were links that had `target`/`source`
        // set to that port, we remove those links as well (to follow the same behaviour as
        // with a removed element).

        var previousPorts = this.ports;

        // Collect ports from the `attrs` object.
        var ports = {};
        _.each(this.get('attrs'), function (attrs, selector) {

            if (attrs && attrs.port) {

                // `port` can either be directly an `id` or an object containing an `id` (and potentially other data).
                if (!_.isUndefined(attrs.port.id)) {
                    ports[attrs.port.id] = attrs.port;
                } else {
                    ports[attrs.port] = {id: attrs.port};
                }
            }
        });

        // Collect ports that have been removed (compared to the previous ports) - if any.
        // Use hash table for quick lookup.
        var removedPorts = {};
        _.each(previousPorts, function (port, id) {

            if (!ports[id]) removedPorts[id] = true;
        });

        // Remove all the incoming/outgoing links that have source/target port set to any of the removed ports.
        if (this.collection && !_.isEmpty(removedPorts)) {

            var inboundLinks = this.collection.getConnectedLinks(this, {inbound: true});
            _.each(inboundLinks, function (link) {

                if (removedPorts[link.get('target').port]) link.remove();
            });

            var outboundLinks = this.collection.getConnectedLinks(this, {outbound: true});
            _.each(outboundLinks, function (link) {

                if (removedPorts[link.get('source').port]) link.remove();
            });
        }

        // Update the `ports` object.
        this.ports = ports;
    },

    remove: function (options) {

        var collection = this.collection;

        if (collection) {
            collection.trigger('batch:start');
        }

        // First, unembed this cell from its parent cell if there is one.
        var parentCellId = this.get('parent');
        if (parentCellId) {

            var parentCell = this.collection && this.collection.get(parentCellId);
            parentCell.unembed(this);
        }

        _.invoke(this.getEmbeddedCells(), 'remove', options);

        this.trigger('remove', this, this.collection, options);

        if (collection) {
            collection.trigger('batch:stop');
        }

        return this;
    },

    toFront: function () {

        if (this.collection) {

            this.set('z', (this.collection.last().get('z') || 0) + 1);
        }

        return this;
    },

    toBack: function () {

        if (this.collection) {

            this.set('z', (this.collection.first().get('z') || 0) - 1);
        }

        return this;
    },

    embed: function (cell) {

        if (this.get('parent') == cell.id) {

            throw new Error('Recursive embedding not allowed.');

        } else {

            this.trigger('batch:start');

            cell.set('parent', this.id);
            this.set('embeds', _.uniq((this.get('embeds') || []).concat([cell.id])));

            this.trigger('batch:stop');
        }

        return this;
    },

    unembed: function (cell) {

        this.trigger('batch:start');

        var cellId = cell.id;
        cell.unset('parent');

        this.set('embeds', _.without(this.get('embeds'), cellId));

        this.trigger('batch:stop');

        return this;
    },

    getEmbeddedCells: function () {

        // Cell models can only be retrieved when this element is part of a collection.
        // There is no way this element knows about other cells otherwise.
        // This also means that calling e.g. `translate()` on an element with embeds before
        // adding it to a graph does not translate its embeds.
        if (this.collection) {

            return _.map(this.get('embeds') || [], function (cellId) {

                return this.collection.get(cellId);

            }, this);
        }
        return [];
    },

    clone: function (opt) {

        opt = opt || {};

        var clone = Backbone.Model.prototype.clone.apply(this, arguments);

        // We don't want the clone to have the same ID as the original.
        clone.set('id', joint.util.uuid(), {silent: true});
        clone.set('embeds', '');

        if (!opt.deep) return clone;

        // The rest of the `clone()` method deals with embeds. If `deep` option is set to `true`,
        // the return value is an array of all the embedded clones created.

        var embeds = _.sortBy(this.getEmbeddedCells(), function (cell) {
            // Sort embeds that links come before elements.
            return cell instanceof joint.dia.Element;
        });

        var clones = [clone];

        // This mapping stores cloned links under the `id`s of they originals.
        // This prevents cloning a link more then once. Consider a link 'self loop' for example.
        var linkCloneMapping = {};

        _.each(embeds, function (embed) {

            var embedClones = embed.clone({deep: true});

            // Embed the first clone returned from `clone({ deep: true })` above. The first
            // cell is always the clone of the cell that called the `clone()` method, i.e. clone of `embed` in this
            // case.
            clone.embed(embedClones[0]);

            _.each(embedClones, function (embedClone) {

                if (embedClone instanceof joint.dia.Link) {

                    if (embedClone.get('source').id == this.id) {

                        embedClone.prop('source', {id: clone.id});
                    }

                    if (embedClone.get('target').id == this.id) {

                        embedClone.prop('target', {id: clone.id});
                    }

                    linkCloneMapping[embed.id] = embedClone;

                    // Skip links. Inbound/outbound links are not relevant for them.
                    return;
                }

                clones.push(embedClone);

                // Collect all inbound links, clone them (if not done already) and set their target to the
                // `embedClone.id`.
                var inboundLinks = this.collection.getConnectedLinks(embed, {inbound: true});

                _.each(inboundLinks, function (link) {

                    var linkClone = linkCloneMapping[link.id] || link.clone();

                    // Make sure we don't clone a link more then once.
                    linkCloneMapping[link.id] = linkClone;

                    linkClone.prop('target', {id: embedClone.id});
                });

                // Collect all inbound links, clone them (if not done already) and set their source to the
                // `embedClone.id`.
                var outboundLinks = this.collection.getConnectedLinks(embed, {outbound: true});

                _.each(outboundLinks, function (link) {

                    var linkClone = linkCloneMapping[link.id] || link.clone();

                    // Make sure we don't clone a link more then once.
                    linkCloneMapping[link.id] = linkClone;

                    linkClone.prop('source', {id: embedClone.id});
                });

            }, this);

        }, this);

        // Add link clones to the array of all the new clones.
        clones = clones.concat(_.values(linkCloneMapping));

        return clones;
    },

    // A convenient way to set nested properties.
    // This method merges the properties you'd like to set with the ones
    // stored in the cell and makes sure change events are properly triggered.
    // You can either set a nested property with one object
    // or use a property path. 
    // The most simple use case is:
    // `cell.prop('name/first', 'John')` or
    // `cell.prop({ name: { first: 'John' } })`.
    // Nested arrays are supported too:
    // `cell.prop('series/0/data/0/degree', 50)` or
    // `cell.prop({ series: [ { data: [ { degree: 50 } ] } ] })`.
    prop: function (props, value, opt) {

        var delim = '/';

        if (_.isString(props)) {
            // Get/set an attribute by a special path syntax that delimits
            // nested objects by the colon character.

            if (typeof value !== 'undefined') {

                var path = props;
                var pathArray = path.split('/');
                var property = pathArray[0];

                if (pathArray.length == 1) {
                    // Property is not nested. We can simply use `set()`.
                    return this.set(property, value, opt);
                }

                var update = {};
                // Initialize the nested object. Subobjects are either arrays or objects.
                // An empty array is created if the sub-key is an integer. Otherwise, an empty object is created.
                // Note that this imposes a limitation on object keys one can use with Inspector.
                // Pure integer keys will cause issues and are therefore not allowed.
                var initializer = update;
                var prevProperty = property;
                _.each(_.rest(pathArray), function (key) {
                    initializer = initializer[prevProperty] = (_.isFinite(Number(key)) ? [] : {});
                    prevProperty = key;
                });
                // Fill update with the `value` on `path`.
                update = joint.util.setByPath(update, path, value, '/');
                // Merge update with the model attributes.
                var attributes = _.merge({}, this.attributes, update);
                // Finally, set the property to the updated attributes.
                return this.set(property, attributes[property], opt);

            } else {

                return joint.util.getByPath(this.attributes, props, delim);
            }
        }

        return this.set(_.merge({}, this.attributes, props), value);
    },

    // A convenient way to set nested attributes.
    attr: function (attrs, value, opt) {

        var currentAttrs = this.get('attrs');
        var delim = '/';

        if (_.isString(attrs)) {
            // Get/set an attribute by a special path syntax that delimits
            // nested objects by the colon character.

            if (typeof value != 'undefined') {

                var attr = {};
                joint.util.setByPath(attr, attrs, value, delim);
                return this.set('attrs', _.merge({}, currentAttrs, attr), opt);

            } else {

                return joint.util.getByPath(currentAttrs, attrs, delim);
            }
        }

        return this.set('attrs', _.merge({}, currentAttrs, attrs), value, opt);
    },

    // A convenient way to unset nested attributes
    removeAttr: function (path, opt) {

        if (_.isArray(path)) {
            _.each(path, function (p) { this.removeAttr(p, opt); }, this);
            return this;
        }

        var attrs = joint.util.unsetByPath(_.merge({}, this.get('attrs')), path, '/');

        return this.set('attrs', attrs, _.extend({dirty: true}, opt));
    },

    transition: function (path, value, opt, delim) {

        delim = delim || '/';

        var defaults = {
            duration: 100,
            delay: 10,
            timingFunction: joint.util.timing.linear,
            valueFunction: joint.util.interpolate.number
        };

        opt = _.extend(defaults, opt);

        var firstFrameTime = 0;
        var interpolatingFunction;

        var setter = _.bind(function (runtime) {

            var id, progress, propertyValue, status;

            firstFrameTime = firstFrameTime || runtime;
            runtime -= firstFrameTime;
            progress = runtime / opt.duration;

            if (progress < 1) {
                this._transitionIds[path] = id = joint.util.nextFrame(setter);
            } else {
                progress = 1;
                delete this._transitionIds[path];
            }

            propertyValue = interpolatingFunction(opt.timingFunction(progress));

            opt.transitionId = id;

            this.prop(path, propertyValue, opt);

            if (!id) this.trigger('transition:end', this, path);

        }, this);

        var initiator = _.bind(function (callback) {

            this.stopTransitions(path);

            interpolatingFunction = opt.valueFunction(joint.util.getByPath(this.attributes, path, delim), value);

            this._transitionIds[path] = joint.util.nextFrame(callback);

            this.trigger('transition:start', this, path);

        }, this);

        return _.delay(initiator, opt.delay, setter);
    },

    getTransitions: function () {
        return _.keys(this._transitionIds);
    },

    stopTransitions: function (path, delim) {

        delim = delim || '/';

        var pathArray = path && path.split(delim);

        _(this._transitionIds).keys().filter(pathArray && function (key) {

                return _.isEqual(pathArray, key.split(delim).slice(0, pathArray.length));

            }).each(function (key) {

            joint.util.cancelFrame(this._transitionIds[key]);

            delete this._transitionIds[key];

            this.trigger('transition:end', this, key);

        }, this);

        return this;
    },

    // A shorcut making it easy to create constructs like the following:
    // `var el = (new joint.shapes.basic.Rect).addTo(graph)`.
    addTo: function (graph) {

        graph.addCell(this);
        return this;
    },

    // A shortcut for an equivalent call: `paper.findViewByModel(cell)`
    // making it easy to create constructs like the following:
    // `cell.findView(paper).highlight()`
    findView: function (paper) {

        return paper.findViewByModel(this);
    }
});

// joint.dia.CellView base view and controller.
// --------------------------------------------

// This is the base view and controller for `joint.dia.ElementView` and `joint.dia.LinkView`.

joint.dia.CellView = Backbone.View.extend({

    tagName: 'g',

    attributes: function () {

        return {'model-id': this.model.id}
    },

    constructor: function (options) {

        this._configure(options);
        Backbone.View.apply(this, arguments);
    },

    _configure: function (options) {

        if (this.options) options = _.extend({}, _.result(this, 'options'), options);
        this.options = options;
        // Make sure a global unique id is assigned to this view. Store this id also to the properties object.
        // The global unique id makes sure that the same view can be rendered on e.g. different machines and
        // still be associated to the same object among all those clients. This is necessary for real-time
        // collaboration mechanism.
        this.options.id = this.options.id || joint.util.guid(this);
    },

    initialize: function () {

        _.bindAll(this, 'remove', 'update');

        // Store reference to this to the <g> DOM element so that the view is accessible through the DOM tree.
        this.$el.data('view', this);

        this.listenTo(this.model, 'remove', this.remove);
        this.listenTo(this.model, 'change:attrs', this.onChangeAttrs);
    },

    onChangeAttrs: function (cell, attrs, opt) {

        if (opt.dirty) {

            // dirty flag could be set when a model attribute was removed and it needs to be cleared
            // also from the DOM element. See cell.removeAttr().
            return this.render();
        }

        return this.update();
    },

    // Override the Backbone `_ensureElement()` method in order to create a `<g>` node that wraps
    // all the nodes of the Cell view.
    _ensureElement: function () {

        var el;

        if (!this.el) {

            var attrs = _.extend({id: this.id}, _.result(this, 'attributes'));
            if (this.className) attrs['class'] = _.result(this, 'className');
            el = V(_.result(this, 'tagName'), attrs).node;

        } else {

            el = _.result(this, 'el')
        }

        this.setElement(el, false);
    },

    findBySelector: function (selector) {

        // These are either descendants of `this.$el` of `this.$el` itself. 
        // `.` is a special selector used to select the wrapping `<g>` element.
        var $selected = selector === '.' ? this.$el : this.$el.find(selector);
        return $selected;
    },

    notify: function (evt) {

        if (this.paper) {

            var args = Array.prototype.slice.call(arguments, 1);

            // Trigger the event on both the element itself and also on the paper.
            this.trigger.apply(this, [evt].concat(args));

            // Paper event handlers receive the view object as the first argument.
            this.paper.trigger.apply(this.paper, [evt, this].concat(args));
        }
    },

    getStrokeBBox: function (el) {
        // Return a bounding box rectangle that takes into account stroke.
        // Note that this is a naive and ad-hoc implementation that does not
        // works only in certain cases and should be replaced as soon as browsers will
        // start supporting the getStrokeBBox() SVG method.
        // @TODO any better solution is very welcome!

        var isMagnet = !!el;

        el = el || this.el;
        var bbox = V(el).bbox(false, this.paper.viewport);

        var strokeWidth;
        if (isMagnet) {

            strokeWidth = V(el).attr('stroke-width');

        } else {

            strokeWidth = this.model.attr('rect/stroke-width') || this.model.attr('circle/stroke-width') || this.model.attr('ellipse/stroke-width') || this.model.attr('path/stroke-width');
        }

        strokeWidth = parseFloat(strokeWidth) || 0;

        return g.rect(bbox).moveAndExpand({
            x: -strokeWidth / 2,
            y: -strokeWidth / 2,
            width: strokeWidth,
            height: strokeWidth
        });
    },

    getBBox: function () {

        return V(this.el).bbox();
    },

    highlight: function (el) {

        el = !el ? this.el : this.$(el)[0] || this.el;

        V(el).addClass('highlighted');
    },

    unhighlight: function (el) {

        el = !el ? this.el : this.$(el)[0] || this.el;

        V(el).removeClass('highlighted');
    },

    // Find the closest element that has the `magnet` attribute set to `true`. If there was not such
    // an element found, return the root element of the cell view.
    findMagnet: function (el) {

        var $el = this.$(el);

        if ($el.length === 0 || $el[0] === this.el) {

            // If the overall cell has set `magnet === false`, then return `undefined` to
            // announce there is no magnet found for this cell.
            // This is especially useful to set on cells that have 'ports'. In this case,
            // only the ports have set `magnet === true` and the overall element has `magnet === false`.
            var attrs = this.model.get('attrs') || {};
            if (attrs['.'] && attrs['.']['magnet'] === false) {
                return undefined;
            }

            return this.el;
        }

        if ($el.attr('magnet')) {

            return $el[0];
        }

        return this.findMagnet($el.parent());
    },

    // `selector` is a CSS selector or `'.'`. `filter` must be in the special JointJS filter format:
    // `{ name: <name of the filter>, args: { <arguments>, ... }`.
    // An example is: `{ filter: { name: 'blur', args: { radius: 5 } } }`.
    applyFilter: function (selector, filter) {

        var $selected = this.findBySelector(selector);

        // Generate a hash code from the stringified filter definition. This gives us
        // a unique filter ID for different definitions.
        var filterId = filter.name + this.paper.svg.id + joint.util.hashCode(JSON.stringify(filter));

        // If the filter already exists in the document,
        // we're done and we can just use it (reference it using `url()`).
        // If not, create one.
        if (!this.paper.svg.getElementById(filterId)) {

            var filterSVGString = joint.util.filter[filter.name] && joint.util.filter[filter.name](filter.args || {});
            if (!filterSVGString) {
                throw new Error('Non-existing filter ' + filter.name);
            }
            var filterElement = V(filterSVGString);
            // Set the filter area to be 3x the bounding box of the cell
            // and center the filter around the cell.
            filterElement.attr({
                filterUnits: 'objectBoundingBox',
                x: -1, y: -1, width: 3, height: 3
            });
            if (filter.attrs) filterElement.attr(filter.attrs);
            filterElement.node.id = filterId;
            V(this.paper.svg).defs().append(filterElement);
        }

        $selected.each(function () {

            V(this).attr('filter', 'url(#' + filterId + ')');
        });
    },

    // `selector` is a CSS selector or `'.'`. `attr` is either a `'fill'` or `'stroke'`.
    // `gradient` must be in the special JointJS gradient format:
    // `{ type: <linearGradient|radialGradient>, stops: [ { offset: <offset>, color: <color> }, ... ]`.
    // An example is: `{ fill: { type: 'linearGradient', stops: [ { offset: '10%', color: 'green' }, { offset: '50%',
    // color: 'blue' } ] } }`.
    applyGradient: function (selector, attr, gradient) {

        var $selected = this.findBySelector(selector);

        // Generate a hash code from the stringified filter definition. This gives us
        // a unique filter ID for different definitions.
        var gradientId = gradient.type + this.paper.svg.id + joint.util.hashCode(JSON.stringify(gradient));

        // If the gradient already exists in the document,
        // we're done and we can just use it (reference it using `url()`).
        // If not, create one.
        if (!this.paper.svg.getElementById(gradientId)) {

            var gradientSVGString = [
                '<' + gradient.type + '>',
                _.map(gradient.stops, function (stop) {
                    return '<stop offset="' + stop.offset + '" stop-color="' + stop.color + '" stop-opacity="' + (_.isFinite(stop.opacity) ? stop.opacity : 1) + '" />'
                }).join(''),
                '</' + gradient.type + '>'
            ].join('');

            var gradientElement = V(gradientSVGString);
            if (gradient.attrs) {
                gradientElement.attr(gradient.attrs);
            }
            gradientElement.node.id = gradientId;
            V(this.paper.svg).defs().append(gradientElement);
        }

        $selected.each(function () {

            V(this).attr(attr, 'url(#' + gradientId + ')');
        });
    },

    // Construct a unique selector for the `el` element within this view.
    // `selector` is being collected through the recursive call. No value for `selector` is expected when using this
    // method.
    getSelector: function (el, selector) {

        if (el === this.el) {

            return selector;
        }

        var index = $(el).index();

        selector = el.tagName + ':nth-child(' + (index + 1) + ')' + ' ' + (selector || '');

        return this.getSelector($(el).parent()[0], selector + ' ');
    },

    // Interaction. The controller part.
    // ---------------------------------

    // Interaction is handled by the paper and delegated to the view in interest.
    // `x` & `y` parameters passed to these functions represent the coordinates already snapped to the paper grid.
    // If necessary, real coordinates can be obtained from the `evt` event object.

    // These functions are supposed to be overriden by the views that inherit from `joint.dia.Cell`,
    // i.e. `joint.dia.Element` and `joint.dia.Link`.

    pointerdblclick: function (evt, x, y) {

        this.notify('cell:pointerdblclick', evt, x, y);
    },

    pointerclick: function (evt, x, y) {

        this.notify('cell:pointerclick', evt, x, y);
    },

    pointerdown: function (evt, x, y) {

        if (this.model.collection) {
            this.model.trigger('batch:start');
            this._collection = this.model.collection;
        }

        this.notify('cell:pointerdown', evt, x, y);
    },

    pointermove: function (evt, x, y) {

        this.notify('cell:pointermove', evt, x, y);
    },

    pointerup: function (evt, x, y) {

        this.notify('cell:pointerup', evt, x, y);

        if (this._collection) {
            // we don't want to trigger event on model as model doesn't
            // need to be member of collection anymore (remove)
            this._collection.trigger('batch:stop');
            delete this._collection;
        }

    }
});

if (typeof exports === 'object') {

    module.exports.Cell = joint.dia.Cell;
    module.exports.CellView = joint.dia.CellView;
}

//      JointJS library.
//      (c) 2011-2013 client IO

if (typeof exports === 'object') {

    var joint = {
        util: require('./core').util,
        dia: {
            Cell: require('./joint.dia.cell').Cell,
            CellView: require('./joint.dia.cell').CellView
        }
    };
    var Backbone = require('backbone');
    var _ = require('lodash');
}

// joint.dia.Element base model.
// -----------------------------

joint.dia.Element = joint.dia.Cell.extend({

    defaults: {
        position: {x: 0, y: 0},
        size: {width: 1, height: 1},
        angle: 0
    },

    position: function (x, y) {

        this.set('position', {x: x, y: y});
    },

    translate: function (tx, ty, opt) {

        ty = ty || 0;

        if (tx === 0 && ty === 0) {
            // Like nothing has happened.
            return this;
        }

        var position = this.get('position') || {x: 0, y: 0};
        var translatedPosition = {x: position.x + tx || 0, y: position.y + ty || 0};

        if (opt && opt.transition) {

            if (!_.isObject(opt.transition)) opt.transition = {};

            this.transition('position', translatedPosition, _.extend({}, opt.transition, {
                valueFunction: joint.util.interpolate.object
            }));

        } else {

            this.set('position', translatedPosition, opt);

            // Recursively call `translate()` on all the embeds cells.
            _.invoke(this.getEmbeddedCells(), 'translate', tx, ty, opt);
        }

        return this;
    },

    resize: function (width, height) {

        this.trigger('batch:start');
        this.set('size', {width: width, height: height});
        this.trigger('batch:stop');

        return this;
    },

    // Rotate element by `angle` degrees, optionally around `origin` point.
    // If `origin` is not provided, it is considered to be the center of the element.
    // If `absolute` is `true`, the `angle` is considered is abslute, i.e. it is not
    // the difference from the previous angle.
    rotate: function (angle, absolute, origin) {

        if (origin) {

            var center = this.getBBox().center();
            var size = this.get('size');
            var position = this.get('position');
            center.rotate(origin, (this.get('angle') || 0) - angle);
            var dx = center.x - size.width / 2 - position.x;
            var dy = center.y - size.height / 2 - position.y;
            this.trigger('batch:start');
            this.translate(dx, dy);
            this.rotate(angle, absolute);
            this.trigger('batch:stop');

        } else {

            this.set('angle', absolute ? angle : ((this.get('angle') || 0) + angle) % 360);
        }
        return this;
    },

    getBBox: function () {

        var position = this.get('position');
        var size = this.get('size');

        return g.rect(position.x, position.y, size.width, size.height);
    }
});

// joint.dia.Element base view and controller.
// -------------------------------------------

joint.dia.ElementView = joint.dia.CellView.extend({

    className: function () {
        return 'element ' + this.model.get('type').split('.').join(' ');
    },

    initialize: function () {

        _.bindAll(this, 'translate', 'resize', 'rotate');

        joint.dia.CellView.prototype.initialize.apply(this, arguments);

        this.listenTo(this.model, 'change:position', this.translate);
        this.listenTo(this.model, 'change:size', this.resize);
        this.listenTo(this.model, 'change:angle', this.rotate);
    },

    // Default is to process the `attrs` object and set attributes on subelements based on the selectors.
    update: function (cell, renderingOnlyAttrs) {

        var allAttrs = this.model.get('attrs');

        var rotatable = V(this.$('.rotatable')[0]);
        if (rotatable) {

            var rotation = rotatable.attr('transform');
            rotatable.attr('transform', '');
        }

        var relativelyPositioned = [];

        _.each(renderingOnlyAttrs || allAttrs, function (attrs, selector) {

            // Elements that should be updated.
            var $selected = this.findBySelector(selector);

            // No element matched by the `selector` was found. We're done then.
            if ($selected.length === 0) return;

            // Special attributes are treated by JointJS, not by SVG.
            var specialAttributes = ['style', 'text', 'html', 'ref-x', 'ref-y', 'ref-dx', 'ref-dy', 'ref-width', 'ref-height', 'ref', 'x-alignment', 'y-alignment', 'port'];

            // If the `filter` attribute is an object, it is in the special JointJS filter format and so
            // it becomes a special attribute and is treated separately.
            if (_.isObject(attrs.filter)) {

                specialAttributes.push('filter');
                this.applyFilter(selector, attrs.filter);
            }

            // If the `fill` or `stroke` attribute is an object, it is in the special JointJS gradient format and so
            // it becomes a special attribute and is treated separately.
            if (_.isObject(attrs.fill)) {

                specialAttributes.push('fill');
                this.applyGradient(selector, 'fill', attrs.fill);
            }
            if (_.isObject(attrs.stroke)) {

                specialAttributes.push('stroke');
                this.applyGradient(selector, 'stroke', attrs.stroke);
            }

            // Make special case for `text` attribute. So that we can set text content of the `<text>` element
            // via the `attrs` object as well.
            // Note that it's important to set text before applying the rest of the final attributes.
            // Vectorizer `text()` method sets on the element its own attributes and it has to be possible
            // to rewrite them, if needed. (i.e display: 'none')
            if (!_.isUndefined(attrs.text)) {

                $selected.each(function () {

                    V(this).text(attrs.text + '', {lineHeight: attrs.lineHeight});
                });
            }

            // Set regular attributes on the `$selected` subelement. Note that we cannot use the jQuery attr()
            // method as some of the attributes might be namespaced (e.g. xlink:href) which fails with jQuery attr().
            var finalAttributes = _.omit(attrs, specialAttributes);

            $selected.each(function () {

                V(this).attr(finalAttributes);
            });

            // `port` attribute contains the `id` of the port that the underlying magnet represents.
            if (attrs.port) {

                $selected.attr('port', _.isUndefined(attrs.port.id) ? attrs.port : attrs.port.id);
            }

            // `style` attribute is special in the sense that it sets the CSS style of the subelement.
            if (attrs.style) {

                $selected.css(attrs.style);
            }

            if (!_.isUndefined(attrs.html)) {

                $selected.each(function () {

                    $(this).html(attrs.html + '');
                });
            }

            // Special `ref-x` and `ref-y` attributes make it possible to set both absolute or
            // relative positioning of subelements.
            if (!_.isUndefined(attrs['ref-x']) || !_.isUndefined(attrs['ref-y']) || !_.isUndefined(attrs['ref-dx']) || !_.isUndefined(attrs['ref-dy']) || !_.isUndefined(attrs['x-alignment']) || !_.isUndefined(attrs['y-alignment']) || !_.isUndefined(attrs['ref-width']) || !_.isUndefined(attrs['ref-height'])
            ) {

                _.each($selected, function (el, index, list) {
                    var $el = $(el);
                    // copy original list selector to the element
                    $el.selector = list.selector;
                    relativelyPositioned.push($el);
                });
            }

        }, this);

        // We don't want the sub elements to affect the bounding box of the root element when
        // positioning the sub elements relatively to the bounding box.
        //_.invoke(relativelyPositioned, 'hide');
        //_.invoke(relativelyPositioned, 'show');

        // Note that we're using the bounding box without transformation because we are already inside
        // a transformed coordinate system.
        var bbox = this.el.getBBox();

        renderingOnlyAttrs = renderingOnlyAttrs || {};

        _.each(relativelyPositioned, function ($el) {

            // if there was a special attribute affecting the position amongst renderingOnlyAttributes
            // we have to merge it with rest of the element's attributes as they are necessary
            // to update the position relatively (i.e `ref`)
            var renderingOnlyElAttrs = renderingOnlyAttrs[$el.selector];
            var elAttrs = renderingOnlyElAttrs
                ? _.merge({}, allAttrs[$el.selector], renderingOnlyElAttrs)
                : allAttrs[$el.selector];

            this.positionRelative($el, bbox, elAttrs);

        }, this);

        if (rotatable) {

            rotatable.attr('transform', rotation || '');
        }
    },

    positionRelative: function ($el, bbox, elAttrs) {

        var ref = elAttrs['ref'];
        var refX = parseFloat(elAttrs['ref-x']);
        var refY = parseFloat(elAttrs['ref-y']);
        var refDx = parseFloat(elAttrs['ref-dx']);
        var refDy = parseFloat(elAttrs['ref-dy']);
        var yAlignment = elAttrs['y-alignment'];
        var xAlignment = elAttrs['x-alignment'];
        var refWidth = parseFloat(elAttrs['ref-width']);
        var refHeight = parseFloat(elAttrs['ref-height']);

        // `ref` is the selector of the reference element. If no `ref` is passed, reference
        // element is the root element.

        var isScalable = _.contains(_.pluck(_.pluck($el.parents('g'), 'className'), 'baseVal'), 'scalable');

        if (ref) {

            // Get the bounding box of the reference element relative to the root `<g>` element.
            bbox = V(this.findBySelector(ref)[0]).bbox(false, this.el);
        }

        var vel = V($el[0]);

        // Remove the previous translate() from the transform attribute and translate the element
        // relative to the root bounding box following the `ref-x` and `ref-y` attributes.
        if (vel.attr('transform')) {

            vel.attr('transform', vel.attr('transform').replace(/translate\([^)]*\)/g, '').trim() || '');
        }

        function isDefined(x) {
            return _.isNumber(x) && !_.isNaN(x);
        }

        // The final translation of the subelement.
        var tx = 0;
        var ty = 0;

        // 'ref-width'/'ref-height' defines the width/height of the subelement relatively to
        // the reference element size
        // val in 0..1         ref-width = 0.75 sets the width to 75% of the ref. el. width
        // val < 0 || val > 1  ref-height = -20 sets the height to the the ref. el. height shorter by 20

        if (isDefined(refWidth)) {

            if (refWidth >= 0 && refWidth <= 1) {

                vel.attr('width', refWidth * bbox.width);

            } else {

                vel.attr('width', Math.max(refWidth + bbox.width, 0));
            }
        }

        if (isDefined(refHeight)) {

            if (refHeight >= 0 && refHeight <= 1) {

                vel.attr('height', refHeight * bbox.height);

            } else {

                vel.attr('height', Math.max(refHeight + bbox.height, 0));
            }
        }

        // `ref-dx` and `ref-dy` define the offset of the subelement relative to the right and/or bottom
        // coordinate of the reference element.
        if (isDefined(refDx)) {

            if (isScalable) {

                // Compensate for the scale grid in case the elemnt is in the scalable group.
                var scale = V(this.$('.scalable')[0]).scale();
                tx = bbox.x + bbox.width + refDx / scale.sx;

            } else {

                tx = bbox.x + bbox.width + refDx;
            }
        }
        if (isDefined(refDy)) {

            if (isScalable) {

                // Compensate for the scale grid in case the elemnt is in the scalable group.
                var scale = V(this.$('.scalable')[0]).scale();
                ty = bbox.y + bbox.height + refDy / scale.sy;
            } else {

                ty = bbox.y + bbox.height + refDy;
            }
        }

        // if `refX` is in [0, 1] then `refX` is a fraction of bounding box width
        // if `refX` is < 0 then `refX`'s absolute values is the right coordinate of the bounding box
        // otherwise, `refX` is the left coordinate of the bounding box
        // Analogical rules apply for `refY`.
        if (isDefined(refX)) {

            if (refX > 0 && refX < 1) {

                tx = bbox.x + bbox.width * refX;

            } else if (isScalable) {

                // Compensate for the scale grid in case the elemnt is in the scalable group.
                var scale = V(this.$('.scalable')[0]).scale();
                tx = bbox.x + refX / scale.sx;

            } else {

                tx = bbox.x + refX;
            }
        }
        if (isDefined(refY)) {

            if (refY > 0 && refY < 1) {

                ty = bbox.y + bbox.height * refY;

            } else if (isScalable) {

                // Compensate for the scale grid in case the elemnt is in the scalable group.
                var scale = V(this.$('.scalable')[0]).scale();
                ty = bbox.y + refY / scale.sy;

            } else {

                ty = bbox.y + refY;
            }
        }

        var velbbox = vel.bbox(false, this.paper.viewport);
        // `y-alignment` when set to `middle` causes centering of the subelement around its new y coordinate.
        if (yAlignment === 'middle') {

            ty -= velbbox.height / 2;

        } else if (isDefined(yAlignment)) {

            ty += (yAlignment > -1 && yAlignment < 1) ? velbbox.height * yAlignment : yAlignment;
        }

        // `x-alignment` when set to `middle` causes centering of the subelement around its new x coordinate.
        if (xAlignment === 'middle') {

            tx -= velbbox.width / 2;

        } else if (isDefined(xAlignment)) {

            tx += (xAlignment > -1 && xAlignment < 1) ? velbbox.width * xAlignment : xAlignment;
        }

        vel.translate(tx, ty);
    },

    // `prototype.markup` is rendered by default. Set the `markup` attribute on the model if the
    // default markup is not desirable.
    renderMarkup: function () {

        var markup = this.model.markup || this.model.get('markup');

        if (markup) {

            var nodes = V(markup);
            V(this.el).append(nodes);

        } else {

            throw new Error('properties.markup is missing while the default render() implementation is used.');
        }
    },

    render: function () {

        this.$el.empty();

        this.renderMarkup();

        this.update();

        this.resize();
        this.rotate();
        this.translate();

        return this;
    },

    // Scale the whole `<g>` group. Note the difference between `scale()` and `resize()` here.
    // `resize()` doesn't scale the whole `<g>` group but rather adjusts the `box.sx`/`box.sy` only.
    // `update()` is then responsible for scaling only those elements that have the `follow-scale`
    // attribute set to `true`. This is desirable in elements that have e.g. a `<text>` subelement
    // that is not supposed to be scaled together with a surrounding `<rect>` element that IS supposed
    // be be scaled.
    scale: function (sx, sy) {

        // TODO: take into account the origin coordinates `ox` and `oy`.
        V(this.el).scale(sx, sy);
    },

    resize: function () {

        var size = this.model.get('size') || {width: 1, height: 1};
        var angle = this.model.get('angle') || 0;

        var scalable = V(this.$('.scalable')[0]);
        if (!scalable) {
            // If there is no scalable elements, than there is nothing to resize.
            return;
        }
        var scalableBbox = scalable.bbox(true);
        // Make sure `scalableBbox.width` and `scalableBbox.height` are not zero which can happen if the element does
        // not have any content. By making the width/height 1, we prevent HTML errors of the type `scale(Infinity,
        // Infinity)`.
        scalable.attr('transform', 'scale(' + (size.width / (scalableBbox.width || 1)) + ',' + (size.height / (scalableBbox.height || 1)) + ')');

        // Now the interesting part. The goal is to be able to store the object geometry via just `x`, `y`, `angle`,
        // `width` and `height` Order of transformations is significant but we want to reconstruct the object always in
        // the order: resize(), rotate(), translate() no matter of how the object was transformed. For that to work, we
        // must adjust the `x` and `y` coordinates of the object whenever we resize it (because the origin of the
        // rotation changes). The new `x` and `y` coordinates are computed by canceling the previous rotation around
        // the center of the resized object (which is a different origin then the origin of the previous rotation) and
        // getting the top-left corner of the resulting object. Then we clean up the rotation back to what it
        // originally was.

        // Cancel the rotation but now around a different origin, which is the center of the scaled object.
        var rotatable = V(this.$('.rotatable')[0]);
        var rotation = rotatable && rotatable.attr('transform');
        if (rotation && rotation !== 'null') {

            rotatable.attr('transform', rotation + ' rotate(' + (-angle) + ',' + (size.width / 2) + ',' + (size.height / 2) + ')');
            var rotatableBbox = scalable.bbox(false, this.paper.viewport);

            // Store new x, y and perform rotate() again against the new rotation origin.
            this.model.set('position', {x: rotatableBbox.x, y: rotatableBbox.y});
            this.rotate();
        }

        // Update must always be called on non-rotated element. Otherwise, relative positioning
        // would work with wrong (rotated) bounding boxes.
        this.update();
    },

    translate: function (model, changes, opt) {

        var position = this.model.get('position') || {x: 0, y: 0};

        V(this.el).attr('transform', 'translate(' + position.x + ',' + position.y + ')');
    },

    rotate: function () {

        var rotatable = V(this.$('.rotatable')[0]);
        if (!rotatable) {
            // If there is no rotatable elements, then there is nothing to rotate.
            return;
        }

        var angle = this.model.get('angle') || 0;
        var size = this.model.get('size') || {width: 1, height: 1};

        var ox = size.width / 2;
        var oy = size.height / 2;

        rotatable.attr('transform', 'rotate(' + angle + ',' + ox + ',' + oy + ')');
    },

    // Interaction. The controller part.
    // ---------------------------------

    pointerdown: function (evt, x, y) {

        if ( // target is a valid magnet start linking
        evt.target.getAttribute('magnet') &&
        this.paper.options.validateMagnet.call(this.paper, this, evt.target)
        ) {
            this.model.trigger('batch:start');

            var link = this.paper.getDefaultLink(this, evt.target);
            link.set({
                source: {
                    id: this.model.id,
                    selector: this.getSelector(evt.target),
                    port: $(evt.target).attr('port')
                },
                target: {x: x, y: y}
            });

            this.paper.model.addCell(link);

            this._linkView = this.paper.findViewByModel(link);
            this._linkView.startArrowheadMove('target');

        } else {

            this._dx = x;
            this._dy = y;

            joint.dia.CellView.prototype.pointerdown.apply(this, arguments);
        }
    },

    pointermove: function (evt, x, y) {

        if (this._linkView) {

            // let the linkview deal with this event
            this._linkView.pointermove(evt, x, y);

        } else {

            var grid = this.paper.options.gridSize;

            var interactive = _.isFunction(this.options.interactive) ? this.options.interactive(this, 'pointermove') : this.options.interactive;

            if (interactive !== false) {

                var position = this.model.get('position');

                // Make sure the new element's position always snaps to the current grid after
                // translate as the previous one could be calculated with a different grid size.
                this.model.translate(
                    g.snapToGrid(position.x, grid) - position.x + g.snapToGrid(x - this._dx, grid),
                    g.snapToGrid(position.y, grid) - position.y + g.snapToGrid(y - this._dy, grid)
                );
            }

            this._dx = g.snapToGrid(x, grid);
            this._dy = g.snapToGrid(y, grid);

            joint.dia.CellView.prototype.pointermove.apply(this, arguments);
        }
    },

    pointerup: function (evt, x, y) {

        if (this._linkView) {

            // let the linkview deal with this event
            this._linkView.pointerup(evt, x, y);

            delete this._linkView;

            this.model.trigger('batch:stop');

        } else {

            joint.dia.CellView.prototype.pointerup.apply(this, arguments);
        }
    }

});

if (typeof exports === 'object') {

    module.exports.Element = joint.dia.Element;
    module.exports.ElementView = joint.dia.ElementView;
}

//      JointJS diagramming library.
//      (c) 2011-2013 client IO

if (typeof exports === 'object') {

    var joint = {
        dia: {
            Cell: require('./joint.dia.cell').Cell,
            CellView: require('./joint.dia.cell').CellView
        }
    };
    var Backbone = require('backbone');
    var _ = require('lodash');
    var g = require('./geometry');
}

// joint.dia.Link base model.
// --------------------------
joint.dia.Link = joint.dia.Cell.extend({

    // The default markup for links.
    markup: [
        '<path class="connection" stroke="black"/>',
        '<path class="marker-source" fill="black" stroke="black" />',
        '<path class="marker-target" fill="black" stroke="black" />',
        '<path class="connection-wrap"/>',
        '<g class="labels"/>',
        '<g class="marker-vertices"/>',
        '<g class="marker-arrowheads"/>',
        '<g class="link-tools"/>'
    ].join(''),

    labelMarkup: [
        '<g class="label">',
        '<rect />',
        '<text />',
        '</g>'
    ].join(''),

    toolMarkup: [
        '<g class="link-tool">',
        '<g class="tool-remove" event="remove">',
        '<circle r="11" />',
        '<path transform="scale(.8) translate(-16, -16)" d="M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z"/>',
        '<title>Remove link.</title>',
        '</g>',
        '<g class="tool-options" event="link:options">',
        '<circle r="11" transform="translate(25)"/>',
        '<path fill="white" transform="scale(.55) translate(29, -16)" d="M31.229,17.736c0.064-0.571,0.104-1.148,0.104-1.736s-0.04-1.166-0.104-1.737l-4.377-1.557c-0.218-0.716-0.504-1.401-0.851-2.05l1.993-4.192c-0.725-0.91-1.549-1.734-2.458-2.459l-4.193,1.994c-0.647-0.347-1.334-0.632-2.049-0.849l-1.558-4.378C17.165,0.708,16.588,0.667,16,0.667s-1.166,0.041-1.737,0.105L12.707,5.15c-0.716,0.217-1.401,0.502-2.05,0.849L6.464,4.005C5.554,4.73,4.73,5.554,4.005,6.464l1.994,4.192c-0.347,0.648-0.632,1.334-0.849,2.05l-4.378,1.557C0.708,14.834,0.667,15.412,0.667,16s0.041,1.165,0.105,1.736l4.378,1.558c0.217,0.715,0.502,1.401,0.849,2.049l-1.994,4.193c0.725,0.909,1.549,1.733,2.459,2.458l4.192-1.993c0.648,0.347,1.334,0.633,2.05,0.851l1.557,4.377c0.571,0.064,1.148,0.104,1.737,0.104c0.588,0,1.165-0.04,1.736-0.104l1.558-4.377c0.715-0.218,1.399-0.504,2.049-0.851l4.193,1.993c0.909-0.725,1.733-1.549,2.458-2.458l-1.993-4.193c0.347-0.647,0.633-1.334,0.851-2.049L31.229,17.736zM16,20.871c-2.69,0-4.872-2.182-4.872-4.871c0-2.69,2.182-4.872,4.872-4.872c2.689,0,4.871,2.182,4.871,4.872C20.871,18.689,18.689,20.871,16,20.871z"/>',
        '<title>Link options.</title>',
        '</g>',
        '</g>'
    ].join(''),

    // The default markup for showing/removing vertices. These elements are the children of the .marker-vertices
    // element (see `this.markup`). Only .marker-vertex and .marker-vertex-remove element have special meaning. The
    // former is used for dragging vertices (changin their position). The latter is used for removing vertices.
    vertexMarkup: [
        '<g class="marker-vertex-group" transform="translate(<%= x %>, <%= y %>)">',
        '<circle class="marker-vertex" idx="<%= idx %>" r="10" />',
        '<path class="marker-vertex-remove-area" idx="<%= idx %>" d="M16,5.333c-7.732,0-14,4.701-14,10.5c0,1.982,0.741,3.833,2.016,5.414L2,25.667l5.613-1.441c2.339,1.317,5.237,2.107,8.387,2.107c7.732,0,14-4.701,14-10.5C30,10.034,23.732,5.333,16,5.333z" transform="translate(5, -33)"/>',
        '<path class="marker-vertex-remove" idx="<%= idx %>" transform="scale(.8) translate(9.5, -37)" d="M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087 10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z">',
        '<title>Remove vertex.</title>',
        '</path>',
        '</g>'
    ].join(''),

    arrowheadMarkup: [
        '<g class="marker-arrowhead-group marker-arrowhead-group-<%= end %>">',
        '<path class="marker-arrowhead" end="<%= end %>" d="M 26 0 L 0 13 L 26 26 z" />',
        '</g>'
    ].join(''),

    defaults: {

        type: 'link'
    },

    disconnect: function () {

        return this.set({source: g.point(0, 0), target: g.point(0, 0)});
    },

    // A convenient way to set labels. Currently set values will be mixined with `value` if used as a setter.
    label: function (idx, value) {

        idx = idx || 0;

        var labels = this.get('labels') || [];

        // Is it a getter?
        if (arguments.length === 0 || arguments.length === 1) {

            return labels[idx];
        }

        var newValue = _.merge({}, labels[idx], value);

        var newLabels = labels.slice();
        newLabels[idx] = newValue;

        return this.set({labels: newLabels});
    },

    translate: function (tx, ty, opt) {

        var attrs = {};
        var source = this.get('source');
        var target = this.get('target');
        var vertices = this.get('vertices');

        if (!source.id) {
            attrs.source = {x: source.x + tx, y: source.y + ty};
        }

        if (!target.id) {
            attrs.target = {x: target.x + tx, y: target.y + ty};
        }

        if (vertices && vertices.length) {
            attrs.vertices = _.map(vertices, function (vertex) {
                return {x: vertex.x + tx, y: vertex.y + ty};
            });
        }

        return this.set(attrs, opt);
    }
});

// joint.dia.Link base view and controller.
// ----------------------------------------

joint.dia.LinkView = joint.dia.CellView.extend({

    className: function () {
        return _.unique(this.model.get('type').split('.').concat('link')).join(' ');
    },

    options: {

        shortLinkLength: 100
    },

    initialize: function () {

        joint.dia.CellView.prototype.initialize.apply(this, arguments);

        // create methods in prototype, so they can be accessed from any instance and
        // don't need to be create over and over
        if (typeof this.constructor.prototype.watchSource !== 'function') {
            this.constructor.prototype.watchSource = this._createWatcher('source');
            this.constructor.prototype.watchTarget = this._createWatcher('target');
        }

        // `_.labelCache` is a mapping of indexes of labels in the `this.get('labels')` array to
        // `<g class="label">` nodes wrapped by Vectorizer. This allows for quick access to the
        // nodes in `updateLabelPosition()` in order to update the label positions.
        this._labelCache = {};

        // keeps markers bboxes and positions again for quicker access
        this._markerCache = {};

        // bind events
        this.startListening();
    },

    startListening: function () {

        this.listenTo(this.model, 'change:markup', this.render);
        this.listenTo(this.model, 'change:smooth change:manhattan change:router change:connector', this.update);
        this.listenTo(this.model, 'change:toolMarkup', function () {
            this.renderTools().updateToolsPosition();
        });
        this.listenTo(this.model, 'change:labels change:labelMarkup', function () {
            this.renderLabels().updateLabelPositions();
        });
        this.listenTo(this.model, 'change:vertices change:vertexMarkup', function () {
            this.renderVertexMarkers().update();
        });
        this.listenTo(this.model, 'change:source', function (cell, source) {
            this.watchSource(cell, source).update();
        });
        this.listenTo(this.model, 'change:target', function (cell, target) {
            this.watchTarget(cell, target).update();
        });
    },

    // Rendering
    //----------

    render: function () {

        this.$el.empty();

        // A special markup can be given in the `properties.markup` property. This might be handy
        // if e.g. arrowhead markers should be `<image>` elements or any other element than `<path>`s.
        // `.connection`, `.connection-wrap`, `.marker-source` and `.marker-target` selectors
        // of elements with special meaning though. Therefore, those classes should be preserved in any
        // special markup passed in `properties.markup`.
        var children = V(this.model.get('markup') || this.model.markup);

        // custom markup may contain only one children
        if (!_.isArray(children)) children = [children];

        // Cache all children elements for quicker access.
        this._V = {}; // vectorized markup;
        _.each(children, function (child) {
            var c = child.attr('class');
            c && (this._V[$.camelCase(c)] = child);
        }, this);

        // Only the connection path is mandatory
        if (!this._V.connection) throw new Error('link: no connection path in the markup');

        // partial rendering
        this.renderTools();
        this.renderVertexMarkers();
        this.renderArrowheadMarkers();

        V(this.el).append(children);

        // rendering labels has to be run after the link is appended to DOM tree. (otherwise <Text> bbox
        // returns zero values)
        this.renderLabels();

        // start watching the ends of the link for changes
        this.watchSource(this.model, this.model.get('source'))
            .watchTarget(this.model, this.model.get('target'))
            .update();

        return this;
    },

    renderLabels: function () {

        if (!this._V.labels) return this;

        this._labelCache = {};
        var $labels = $(this._V.labels.node).empty();

        var labels = this.model.get('labels') || [];
        if (!labels.length) return this;

        var labelTemplate = _.template(this.model.get('labelMarkup') || this.model.labelMarkup);
        // This is a prepared instance of a vectorized SVGDOM node for the label element resulting from
        // compilation of the labelTemplate. The purpose is that all labels will just `clone()` this
        // node to create a duplicate.
        var labelNodeInstance = V(labelTemplate());

        _.each(labels, function (label, idx) {

            var labelNode = labelNodeInstance.clone().node;
            // Cache label nodes so that the `updateLabels()` can just update the label node positions.
            this._labelCache[idx] = V(labelNode);

            var $text = $(labelNode).find('text');
            var $rect = $(labelNode).find('rect');

            // Text attributes with the default `text-anchor` and font-size set.
            var textAttributes = _.extend({
                'text-anchor': 'middle',
                'font-size': 14
            }, joint.util.getByPath(label, 'attrs/text', '/'));

            $text.attr(_.omit(textAttributes, 'text'));

            if (!_.isUndefined(textAttributes.text)) {

                V($text[0]).text(textAttributes.text + '');
            }

            // Note that we first need to append the `<text>` element to the DOM in order to
            // get its bounding box.
            $labels.append(labelNode);

            // `y-alignment` - center the text element around its y coordinate.
            var textBbox = V($text[0]).bbox(true, $labels[0]);
            V($text[0]).translate(0, -textBbox.height / 2);

            // Add default values.
            var rectAttributes = _.extend({

                fill: 'white',
                rx: 3,
                ry: 3

            }, joint.util.getByPath(label, 'attrs/rect', '/'));

            $rect.attr(_.extend(rectAttributes, {

                x: textBbox.x,
                y: textBbox.y - textBbox.height / 2,  // Take into account the y-alignment translation.
                width: textBbox.width,
                height: textBbox.height
            }));

        }, this);

        return this;
    },

    renderTools: function () {

        if (!this._V.linkTools) return this;

        // Tools are a group of clickable elements that manipulate the whole link.
        // A good example of this is the remove tool that removes the whole link.
        // Tools appear after hovering the link close to the `source` element/point of the link
        // but are offset a bit so that they don't cover the `marker-arrowhead`.

        var $tools = $(this._V.linkTools.node).empty();
        var toolTemplate = _.template(this.model.get('toolMarkup') || this.model.toolMarkup);
        var tool = V(toolTemplate());

        $tools.append(tool.node);

        // Cache the tool node so that the `updateToolsPosition()` can update the tool position quickly.
        this._toolCache = tool;

        return this;
    },

    renderVertexMarkers: function () {

        if (!this._V.markerVertices) return this;

        var $markerVertices = $(this._V.markerVertices.node).empty();

        // A special markup can be given in the `properties.vertexMarkup` property. This might be handy
        // if default styling (elements) are not desired. This makes it possible to use any
        // SVG elements for .marker-vertex and .marker-vertex-remove tools.
        var markupTemplate = _.template(this.model.get('vertexMarkup') || this.model.vertexMarkup);

        _.each(this.model.get('vertices'), function (vertex, idx) {

            $markerVertices.append(V(markupTemplate(_.extend({idx: idx}, vertex))).node);
        });

        return this;
    },

    renderArrowheadMarkers: function () {

        // Custom markups might not have arrowhead markers. Therefore, jump of this function immediately if that's the
        // case.
        if (!this._V.markerArrowheads) return this;

        var $markerArrowheads = $(this._V.markerArrowheads.node);

        $markerArrowheads.empty();

        // A special markup can be given in the `properties.vertexMarkup` property. This might be handy
        // if default styling (elements) are not desired. This makes it possible to use any
        // SVG elements for .marker-vertex and .marker-vertex-remove tools.
        var markupTemplate = _.template(this.model.get('arrowheadMarkup') || this.model.arrowheadMarkup);

        this._V.sourceArrowhead = V(markupTemplate({end: 'source'}));
        this._V.targetArrowhead = V(markupTemplate({end: 'target'}));

        $markerArrowheads.append(this._V.sourceArrowhead.node, this._V.targetArrowhead.node);

        return this;
    },

    // Updating
    //---------

    // Default is to process the `attrs` object and set attributes on subelements based on the selectors.
    update: function () {

        // Update attributes.
        _.each(this.model.get('attrs'), function (attrs, selector) {

            // If the `filter` attribute is an object, it is in the special JointJS filter format and so
            // it becomes a special attribute and is treated separately.
            if (_.isObject(attrs.filter)) {

                this.findBySelector(selector).attr(_.omit(attrs, 'filter'));
                this.applyFilter(selector, attrs.filter);

            } else {

                this.findBySelector(selector).attr(attrs);
            }
        }, this);

        // Path finding
        var vertices = this.route = this.findRoute(this.model.get('vertices') || []);

        // finds all the connection points taking new vertices into account
        this._findConnectionPoints(vertices);

        var pathData = this.getPathData(vertices);

        // The markup needs to contain a `.connection`
        this._V.connection.attr('d', pathData);
        this._V.connectionWrap && this._V.connectionWrap.attr('d', pathData);

        this._translateAndAutoOrientArrows(this._V.markerSource, this._V.markerTarget);

        //partials updates
        this.updateLabelPositions();
        this.updateToolsPosition();
        this.updateArrowheadMarkers();

        delete this.options.perpendicular;

        return this;
    },

    _findConnectionPoints: function (vertices) {

        // cache source and target points
        var sourcePoint, targetPoint, sourceMarkerPoint, targetMarkerPoint;

        var firstVertex = _.first(vertices);

        sourcePoint = this.getConnectionPoint(
            'source', this.model.get('source'), firstVertex || this.model.get('target')
        ).round();

        var lastVertex = _.last(vertices);

        targetPoint = this.getConnectionPoint(
            'target', this.model.get('target'), lastVertex || sourcePoint
        ).round();

        // Move the source point by the width of the marker taking into account
        // its scale around x-axis. Note that scale is the only transform that
        // makes sense to be set in `.marker-source` attributes object
        // as all other transforms (translate/rotate) will be replaced
        // by the `translateAndAutoOrient()` function.
        var cache = this._markerCache;

        if (this._V.markerSource) {

            cache.sourceBBox = cache.sourceBBox || this._V.markerSource.bbox(true);

            sourceMarkerPoint = g.point(sourcePoint).move(
                firstVertex || targetPoint,
                cache.sourceBBox.width * this._V.markerSource.scale().sx * -1
            ).round();
        }

        if (this._V.markerTarget) {

            cache.targetBBox = cache.targetBBox || this._V.markerTarget.bbox(true);

            targetMarkerPoint = g.point(targetPoint).move(
                lastVertex || sourcePoint,
                cache.targetBBox.width * this._V.markerTarget.scale().sx * -1
            ).round();
        }

        // if there was no markup for the marker, use the connection point.
        cache.sourcePoint = sourceMarkerPoint || sourcePoint;
        cache.targetPoint = targetMarkerPoint || targetPoint;

        // make connection points public
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
    },

    updateLabelPositions: function () {

        if (!this._V.labels) return this;

        // This method assumes all the label nodes are stored in the `this._labelCache` hash table
        // by their indexes in the `this.get('labels')` array. This is done in the `renderLabels()` method.

        var labels = this.model.get('labels') || [];
        if (!labels.length) return this;

        var connectionElement = this._V.connection.node;
        var connectionLength = connectionElement.getTotalLength();

        // Firefox returns connectionLength=NaN in odd cases (for bezier curves).
        // In that case we won't update labels at all.
        if (!_.isNaN(connectionLength)) {

            _.each(labels, function (label, idx) {

                var position = label.position;
                position = (position > connectionLength) ? connectionLength : position; // sanity check
                position = (position < 0) ? connectionLength + position : position;
                position = position > 1 ? position : connectionLength * position;

                var labelCoordinates = connectionElement.getPointAtLength(position);

                this._labelCache[idx].attr('transform', 'translate(' + labelCoordinates.x + ', ' + labelCoordinates.y + ')');

            }, this);
        }

        return this;
    },

    updateToolsPosition: function () {

        if (!this._V.linkTools) return this;

        // Move the tools a bit to the target position but don't cover the `sourceArrowhead` marker.
        // Note that the offset is hardcoded here. The offset should be always
        // more than the `this.$('.marker-arrowhead[end="source"]')[0].bbox().width` but looking
        // this up all the time would be slow.

        var scale = '';
        var offset = 40;

        // If the link is too short, make the tools half the size and the offset twice as low.
        if (this.getConnectionLength() < this.options.shortLinkLength) {
            scale = 'scale(.5)';
            offset /= 2;
        }

        var toolPosition = this.getPointAtLength(offset);

        this._toolCache.attr('transform', 'translate(' + toolPosition.x + ', ' + toolPosition.y + ') ' + scale);

        return this;
    },

    updateArrowheadMarkers: function () {

        if (!this._V.markerArrowheads) return this;

        // getting bbox of an element with `display="none"` in IE9 ends up with access violation
        if ($.css(this._V.markerArrowheads.node, 'display') === 'none') return this;

        var sx = this.getConnectionLength() < this.options.shortLinkLength ? .5 : 1;
        this._V.sourceArrowhead.scale(sx);
        this._V.targetArrowhead.scale(sx);

        this._translateAndAutoOrientArrows(this._V.sourceArrowhead, this._V.targetArrowhead);

        return this;
    },

    // Returns a function observing changes on an end of the link. If a change happens and new end is a new model,
    // it stops listening on the previous one and starts listening to the new one.
    _createWatcher: function (endType) {

        function watchEnd(link, end) {

            end = end || {};

            var previousEnd = link.previous(endType) || {};

            // Pick updateMethod this._sourceBboxUpdate or this._targetBboxUpdate.
            var updateEndFunction = this['_' + endType + 'BBoxUpdate'];

            if (this._isModel(previousEnd)) {
                this.stopListening(this.paper.getModelById(previousEnd.id), 'change', updateEndFunction);
            }

            if (this._isModel(end)) {
                // If the observed model changes, it caches a new bbox and do the link update.
                this.listenTo(this.paper.getModelById(end.id), 'change', updateEndFunction);
            }

            _.bind(updateEndFunction, this)({cacheOnly: true});

            return this;
        }

        return watchEnd;
    },

    // It's important to keep both methods (sourceBboxUpdate and targetBboxUpdate) as unique methods
    // because of loop links. We have to be able to determine, which method we want to stop listen to.
    // ListenTo(model, event, handler) as model and event will be identical.
    _sourceBBoxUpdate: function (update) {

        // keep track which end had been changed very last
        this.lastEndChange = 'source';

        update = update || {};
        var end = this.model.get('source');

        if (this._isModel(end)) {

            var selector = this._makeSelector(end);
            var view = this.paper.findViewByModel(end.id);
            var magnetElement = this.paper.viewport.querySelector(selector);

            this.sourceBBox = view.getStrokeBBox(magnetElement);
            this.sourceView = view;
            this.sourceMagnet = magnetElement;

        } else if (end) {
            // the link end is a point ~ rect 1x1
            this.sourceBBox = g.rect(end.x, end.y, 1, 1);
        }

        if (!update.cacheOnly) this.update();
    },

    _targetBBoxUpdate: function (update) {

        // keep track which end had been changed very last
        this.lastEndChange = 'target';

        update = update || {};
        var end = this.model.get('target');

        if (this._isModel(end)) {

            var selector = this._makeSelector(end);
            var view = this.paper.findViewByModel(end.id);
            var magnetElement = this.paper.viewport.querySelector(selector);

            this.targetBBox = view.getStrokeBBox(magnetElement);
            this.targetView = view;
            this.targetMagnet = magnetElement;

        } else if (end) {
            // the link end is a point ~ rect 1x1
            this.targetBBox = g.rect(end.x, end.y, 1, 1);
        }

        if (!update.cacheOnly) this.update();
    },

    _translateAndAutoOrientArrows: function (sourceArrow, targetArrow) {

        // Make the markers "point" to their sticky points being auto-oriented towards
        // `targetPosition`/`sourcePosition`. And do so only if there is a markup for them.
        if (sourceArrow) {
            sourceArrow.translateAndAutoOrient(
                this.sourcePoint,
                _.first(this.route) || this.targetPoint,
                this.paper.viewport
            );
        }

        if (targetArrow) {
            targetArrow.translateAndAutoOrient(
                this.targetPoint,
                _.last(this.route) || this.sourcePoint,
                this.paper.viewport
            );
        }
    },

    removeVertex: function (idx) {

        var vertices = _.clone(this.model.get('vertices'));

        if (vertices && vertices.length) {

            vertices.splice(idx, 1);
            this.model.set('vertices', vertices);
        }

        return this;
    },

    // This method ads a new vertex to the `vertices` array of `.connection`. This method
    // uses a heuristic to find the index at which the new `vertex` should be placed at assuming
    // the new vertex is somewhere on the path.
    addVertex: function (vertex) {

        this.model.set('attrs', this.model.get('attrs') || {});
        var attrs = this.model.get('attrs');

        // As it is very hard to find a correct index of the newly created vertex,
        // a little heuristics is taking place here.
        // The heuristics checks if length of the newly created
        // path is lot more than length of the old path. If this is the case,
        // new vertex was probably put into a wrong index.
        // Try to put it into another index and repeat the heuristics again.

        var vertices = (this.model.get('vertices') || []).slice();
        // Store the original vertices for a later revert if needed.
        var originalVertices = vertices.slice();

        // A `<path>` element used to compute the length of the path during heuristics.
        var path = this._V.connection.node.cloneNode(false);

        // Length of the original path.        
        var originalPathLength = path.getTotalLength();
        // Current path length.
        var pathLength;
        // Tolerance determines the highest possible difference between the length
        // of the old and new path. The number has been chosen heuristically.
        var pathLengthTolerance = 20;
        // Total number of vertices including source and target points.
        var idx = vertices.length + 1;

        // Loop through all possible indexes and check if the difference between
        // path lengths changes significantly. If not, the found index is
        // most probably the right one.
        while (idx--) {

            vertices.splice(idx, 0, vertex);
            V(path).attr('d', this.getPathData(this.findRoute(vertices)));

            pathLength = path.getTotalLength();

            // Check if the path lengths changed significantly.
            if (pathLength - originalPathLength > pathLengthTolerance) {

                // Revert vertices to the original array. The path length has changed too much
                // so that the index was not found yet.
                vertices = originalVertices.slice();

            } else {

                break;
            }
        }

        if (idx === -1) {
            // If no suitable index was found for such a vertex, make the vertex the first one.
            idx = 0;
            vertices.splice(idx, 0, vertex);
        }

        this.model.set('vertices', vertices);

        return idx;
    },

    // Send a token (an SVG element, usually a circle) along the connection path.
    // Example: `paper.findViewByModel(link).sendToken(V('circle', { r: 7, fill: 'green' }).node)`
    // `duration` is optional and is a time in milliseconds that the token travels from the source to the target of the
    // link. Default is `1000`. `callback` is optional and is a function to be called once the token reaches the
    // target.
    sendToken: function (token, duration, callback) {

        duration = duration || 1000;

        V(this.paper.viewport).append(token);
        V(token).animateAlongPath({dur: duration + 'ms', repeatCount: 1}, this._V.connection.node);
        _.delay(function () {
            V(token).remove();
            callback && callback();
        }, duration);
    },

    findRoute: function (oldVertices) {

        var router = this.model.get('router');

        if (!router) {

            if (this.model.get('manhattan')) {
                // backwards compability
                router = {name: 'orthogonal'};
            } else {

                return oldVertices;
            }
        }

        var fn = joint.routers[router.name];

        if (!_.isFunction(fn)) {

            throw 'unknown router: ' + router.name;
        }

        var newVertices = fn.call(this, oldVertices || [], router.args || {}, this);

        return newVertices;
    },

    // Return the `d` attribute value of the `<path>` element representing the link
    // between `source` and `target`.
    getPathData: function (vertices) {

        var connector = this.model.get('connector');

        if (!connector) {

            // backwards compability
            connector = this.model.get('smooth') ? {name: 'smooth'} : {name: 'normal'};
        }

        if (!_.isFunction(joint.connectors[connector.name])) {

            throw 'unknown connector: ' + connector.name;
        }

        var pathData = joint.connectors[connector.name].call(
            this,
            this._markerCache.sourcePoint, // Note that the value is translated by the size
            this._markerCache.targetPoint, // of the marker. (We'r not using this.sourcePoint)
            vertices || (this.model.get('vertices') || {}),
            connector.args || {}, // options
            this
        );

        return pathData;
    },

    // Find a point that is the start of the connection.
    // If `selectorOrPoint` is a point, then we're done and that point is the start of the connection.
    // If the `selectorOrPoint` is an element however, we need to know a reference point (or element)
    // that the link leads to in order to determine the start of the connection on the original element.
    getConnectionPoint: function (end, selectorOrPoint, referenceSelectorOrPoint) {

        var spot;

        // If the `selectorOrPoint` (or `referenceSelectorOrPoint`) is `undefined`, the `source`/`target` of the link
        // model is `undefined`. We want to allow this however so that one can create links such as `var link = new
        // joint.dia.Link` and set the `source`/`target` later.
        selectorOrPoint = selectorOrPoint || {x: 0, y: 0};
        referenceSelectorOrPoint = referenceSelectorOrPoint || {x: 0, y: 0};

        if (this._isPoint(selectorOrPoint)) {

            // If the source is a point, we don't need a reference point to find the sticky point of connection.
            spot = g.point(selectorOrPoint);

        } else {

            // If the source is an element, we need to find a point on the element boundary that is closest
            // to the reference point (or reference element).
            // Get the bounding box of the spot relative to the paper viewport. This is necessary
            // in order to follow paper viewport transformations (scale/rotate).
            // `_sourceBbox` (`_targetBbox`) comes from `_sourceBboxUpdate` (`_sourceBboxUpdate`)
            // method, it exists since first render and are automatically updated
            var spotBbox = end === 'source' ? this.sourceBBox : this.targetBBox;

            var reference;

            if (this._isPoint(referenceSelectorOrPoint)) {

                // Reference was passed as a point, therefore, we're ready to find the sticky point of connection on
                // the source element.
                reference = g.point(referenceSelectorOrPoint);

            } else {

                // Reference was passed as an element, therefore we need to find a point on the reference
                // element boundary closest to the source element.
                // Get the bounding box of the spot relative to the paper viewport. This is necessary
                // in order to follow paper viewport transformations (scale/rotate).
                var referenceBbox = end === 'source' ? this.targetBBox : this.sourceBBox;

                reference = g.rect(referenceBbox).intersectionWithLineFromCenterToPoint(g.rect(spotBbox).center());
                reference = reference || g.rect(referenceBbox).center();
            }

            // If `perpendicularLinks` flag is set on the paper and there are vertices
            // on the link, then try to find a connection point that makes the link perpendicular
            // even though the link won't point to the center of the targeted object.
            if (this.paper.options.perpendicularLinks || this.options.perpendicular) {

                var horizontalLineRect = g.rect(0, reference.y, this.paper.options.width, 1);
                var verticalLineRect = g.rect(reference.x, 0, 1, this.paper.options.height);
                var nearestSide;

                if (horizontalLineRect.intersect(g.rect(spotBbox))) {

                    nearestSide = g.rect(spotBbox).sideNearestToPoint(reference);
                    switch (nearestSide) {
                        case 'left':
                            spot = g.point(spotBbox.x, reference.y);
                            break;
                        case 'right':
                            spot = g.point(spotBbox.x + spotBbox.width, reference.y);
                            break;
                        default:
                            spot = g.rect(spotBbox).center();
                            break;
                    }

                } else if (verticalLineRect.intersect(g.rect(spotBbox))) {

                    nearestSide = g.rect(spotBbox).sideNearestToPoint(reference);
                    switch (nearestSide) {
                        case 'top':
                            spot = g.point(reference.x, spotBbox.y);
                            break;
                        case 'bottom':
                            spot = g.point(reference.x, spotBbox.y + spotBbox.height);
                            break;
                        default:
                            spot = g.rect(spotBbox).center();
                            break;
                    }

                } else {

                    // If there is no intersection horizontally or vertically with the object bounding box,
                    // then we fall back to the regular situation finding straight line (not perpendicular)
                    // between the object and the reference point.

                    spot = g.rect(spotBbox).intersectionWithLineFromCenterToPoint(reference);
                    spot = spot || g.rect(spotBbox).center();
                }

            } else if (this.paper.options.linkConnectionPoint) {

                var view = end === 'target' ? this.targetView : this.sourceView;
                var magnet = end === 'target' ? this.targetMagnet : this.sourceMagnet;

                spot = this.paper.options.linkConnectionPoint(this, view, magnet, reference);

            } else {

                spot = g.rect(spotBbox).intersectionWithLineFromCenterToPoint(reference);
                spot = spot || g.rect(spotBbox).center();
            }
        }

        return spot;
    },

    _isModel: function (end) {

        return end && end.id;
    },

    _isPoint: function (end) {

        return !this._isModel(end);
    },

    _makeSelector: function (end) {

        var selector = '[model-id="' + end.id + '"]';
        // `port` has a higher precendence over `selector`. This is because the selector to the magnet
        // might change while the name of the port can stay the same.
        if (end.port) {
            selector += ' [port="' + end.port + '"]';
        } else if (end.selector) {
            selector += ' ' + end.selector;
        }

        return selector;
    },

    // Public API
    // ----------

    getConnectionLength: function () {

        return this._V.connection.node.getTotalLength();
    },

    getPointAtLength: function (length) {

        return this._V.connection.node.getPointAtLength(length);
    },

    // Interaction. The controller part.
    // ---------------------------------

    _beforeArrowheadMove: function () {

        this.model.trigger('batch:start');

        this._z = this.model.get('z');
        this.model.set('z', Number.MAX_VALUE);

        // Let the pointer propagate throught the link view elements so that
        // the `evt.target` is another element under the pointer, not the link itself.
        this.el.style.pointerEvents = 'none';

        if (this.paper.options.markAvailable) {
            this._markAvailableMagnets();
        }
    },

    _afterArrowheadMove: function () {

        if (this._z) {
            this.model.set('z', this._z);
            delete this._z;
        }

        // Put `pointer-events` back to its original value. See `startArrowheadMove()` for explanation.
        // Value `auto` doesn't work in IE9. We force to use `visiblePainted` instead.
        // See `https://developer.mozilla.org/en-US/docs/Web/CSS/pointer-events`.
        this.el.style.pointerEvents = 'visiblePainted';

        if (this.paper.options.markAvailable) {
            this._unmarkAvailableMagnets();
        }

        this.model.trigger('batch:stop');
    },

    _createValidateConnectionArgs: function (arrowhead) {
        // It makes sure the arguments for validateConnection have the following form:
        // (source view, source magnet, target view, target magnet and link view)
        var args = [];

        args[4] = arrowhead;
        args[5] = this;

        var oppositeArrowhead, i = 0, j = 0;

        if (arrowhead === 'source') {
            i = 2;
            oppositeArrowhead = 'target';
        } else {
            j = 2;
            oppositeArrowhead = 'source';
        }

        var end = this.model.get(oppositeArrowhead);

        if (end.id) {
            args[i] = this.paper.findViewByModel(end.id);
            args[i + 1] = end.selector && args[i].el.querySelector(end.selector);
        }

        function validateConnectionArgs(cellView, magnet) {
            args[j] = cellView;
            args[j + 1] = cellView.el === magnet ? undefined : magnet;
            return args;
        }

        return validateConnectionArgs;
    },

    _markAvailableMagnets: function () {

        var elements = this.paper.model.getElements();
        var validate = this.paper.options.validateConnection;

        _.chain(elements).map(this.paper.findViewByModel, this.paper).each(function (view) {

            var isElementAvailable = view.el.getAttribute('magnet') !== 'false' &&
                validate.apply(this.paper, this._validateConnectionArgs(view, null));

            var availableMagnets = _.filter(view.el.querySelectorAll('[magnet]'), function (magnet) {
                return validate.apply(this.paper, this._validateConnectionArgs(view, magnet));
            }, this);

            if (isElementAvailable) {
                V(view.el).addClass('available-magnet');
            }

            _.each(availableMagnets, function (magnet) {
                V(magnet).addClass('available-magnet');
            });

            if (isElementAvailable || availableMagnets.length) {
                V(view.el).addClass('available-cell');
            }

        }, this);
    },

    _unmarkAvailableMagnets: function () {

        _.each(this.paper.el.querySelectorAll('.available-cell, .available-magnet'), function (magnet) {
            V(magnet).removeClass('available-magnet').removeClass('available-cell');
        });
    },

    startArrowheadMove: function (end) {
        // Allow to delegate events from an another view to this linkView in order to trigger arrowhead
        // move without need to click on the actual arrowhead dom element.
        this._action = 'arrowhead-move';
        this._arrowhead = end;
        this._validateConnectionArgs = this._createValidateConnectionArgs(this._arrowhead);
        this._beforeArrowheadMove();
    },

    pointerdown: function (evt, x, y) {

        joint.dia.CellView.prototype.pointerdown.apply(this, arguments);

        this._dx = x;
        this._dy = y;

        var interactive = _.isFunction(this.options.interactive) ? this.options.interactive(this, 'pointerdown') : this.options.interactive;

        if (interactive === false) return;

        function can(feature) {
            if (!_.isObject(interactive) || interactive[feature] !== false) return true;
            return false;
        }

        var className = evt.target.getAttribute('class');

        switch (className) {

            case 'marker-vertex':
                if (can('vertexMove')) {
                    this._action = 'vertex-move';
                    this._vertexIdx = evt.target.getAttribute('idx');
                }
                break;

            case 'marker-vertex-remove':
            case 'marker-vertex-remove-area':
                if (can('vertexRemove')) {
                    this.removeVertex(evt.target.getAttribute('idx'));
                }
                break;

            case 'marker-arrowhead':
                if (can('arrowheadMove')) {
                    this.startArrowheadMove(evt.target.getAttribute('end'));
                }
                break;

            default:

                var targetParentEvent = evt.target.parentNode.getAttribute('event');

                if (targetParentEvent) {

                    // `remove` event is built-in. Other custom events are triggered on the paper.
                    if (targetParentEvent === 'remove') {
                        this.model.remove();
                    } else {
                        this.paper.trigger(targetParentEvent, evt, this, x, y);
                    }

                } else {

                    if (can('vertexAdd')) {

                        // Store the index at which the new vertex has just been placed.
                        // We'll be update the very same vertex position in `pointermove()`.
                        this._vertexIdx = this.addVertex({x: x, y: y});
                        this._action = 'vertex-move';
                    }
                }
        }

        this.paper.trigger('link:pointerdown', evt, this, x, y);
    },

    pointermove: function (evt, x, y) {

        joint.dia.CellView.prototype.pointermove.apply(this, arguments);

        switch (this._action) {

            case 'vertex-move':

                var vertices = _.clone(this.model.get('vertices'));
                vertices[this._vertexIdx] = {x: x, y: y};
                this.model.set('vertices', vertices);
                break;

            case 'arrowhead-move':

                if (this.paper.options.snapLinks) {

                    // checking view in close area of the pointer

                    var r = this.paper.options.snapLinks.radius || 50;
                    var viewsInArea = this.paper.findViewsInArea({x: x - r, y: y - r, width: 2 * r, height: 2 * r});

                    this._closestView && this._closestView.unhighlight(this._closestEnd.selector);
                    this._closestView = this._closestEnd = null;

                    var pointer = g.point(x, y);
                    var distance, minDistance = Number.MAX_VALUE;

                    _.each(viewsInArea, function (view) {

                        // skip connecting to the element in case '.': { magnet: false } attribute present
                        if (view.el.getAttribute('magnet') !== 'false') {

                            // find distance from the center of the model to pointer coordinates
                            distance = view.model.getBBox().center().distance(pointer);

                            // the connection is looked up in a circle area by `distance < r`
                            if (distance < r && distance < minDistance) {

                                if (this.paper.options.validateConnection.apply(
                                        this.paper, this._validateConnectionArgs(view, null)
                                    )) {
                                    minDistance = distance;
                                    this._closestView = view;
                                    this._closestEnd = {id: view.model.id};
                                }
                            }
                        }

                        view.$('[magnet]').each(_.bind(function (index, magnet) {

                            var bbox = V(magnet).bbox(false, this.paper.viewport);

                            distance = pointer.distance({
                                x: bbox.x + bbox.width / 2,
                                y: bbox.y + bbox.height / 2
                            });

                            if (distance < r && distance < minDistance) {

                                if (this.paper.options.validateConnection.apply(
                                        this.paper, this._validateConnectionArgs(view, magnet)
                                    )) {
                                    minDistance = distance;
                                    this._closestView = view;
                                    this._closestEnd = {
                                        id: view.model.id,
                                        selector: view.getSelector(magnet),
                                        port: magnet.getAttribute('port')
                                    };
                                }
                            }

                        }, this));

                    }, this);

                    this._closestView && this._closestView.highlight(this._closestEnd.selector);

                    this.model.set(this._arrowhead, this._closestEnd || {x: x, y: y});

                } else {

                    // checking views right under the pointer

                    // Touchmove event's target is not reflecting the element under the coordinates as mousemove does.
                    // It holds the element when a touchstart triggered.
                    var target = (evt.type === 'mousemove')
                        ? evt.target
                        : document.elementFromPoint(evt.clientX, evt.clientY);

                    if (this._targetEvent !== target) {
                        // Unhighlight the previous view under pointer if there was one.
                        this._magnetUnderPointer && this._viewUnderPointer.unhighlight(this._magnetUnderPointer);
                        this._viewUnderPointer = this.paper.findView(target);
                        if (this._viewUnderPointer) {
                            // If we found a view that is under the pointer, we need to find the closest
                            // magnet based on the real target element of the event.
                            this._magnetUnderPointer = this._viewUnderPointer.findMagnet(target);

                            if (this._magnetUnderPointer && this.paper.options.validateConnection.apply(
                                    this.paper,
                                    this._validateConnectionArgs(this._viewUnderPointer, this._magnetUnderPointer)
                                )) {
                                // If there was no magnet found, do not highlight anything and assume there
                                // is no view under pointer we're interested in reconnecting to.
                                // This can only happen if the overall element has the attribute `'.': { magnet: false
                                // }`.
                                this._magnetUnderPointer && this._viewUnderPointer.highlight(this._magnetUnderPointer);
                            } else {
                                // This type of connection is not valid. Disregard this magnet.
                                this._magnetUnderPointer = null;
                            }
                        } else {
                            // Make sure we'll delete previous magnet
                            this._magnetUnderPointer = null;
                        }
                    }

                    this._targetEvent = target;

                    this.model.set(this._arrowhead, {x: x, y: y});
                }

                break;
        }

        this._dx = x;
        this._dy = y;
    },

    pointerup: function (evt) {

        joint.dia.CellView.prototype.pointerup.apply(this, arguments);

        if (this._action === 'arrowhead-move') {

            if (this.paper.options.snapLinks) {

                this._closestView && this._closestView.unhighlight(this._closestEnd.selector);
                this._closestView = this._closestEnd = null;

            } else {

                if (this._magnetUnderPointer) {
                    this._viewUnderPointer.unhighlight(this._magnetUnderPointer);
                    // Find a unique `selector` of the element under pointer that is a magnet. If the
                    // `this._magnetUnderPointer` is the root element of the `this._viewUnderPointer` itself,
                    // the returned `selector` will be `undefined`. That means we can directly pass it to the
                    // `source`/`target` attribute of the link model below.
                    this.model.set(this._arrowhead, {
                        id: this._viewUnderPointer.model.id,
                        selector: this._viewUnderPointer.getSelector(this._magnetUnderPointer),
                        port: $(this._magnetUnderPointer).attr('port')
                    });
                }

                delete this._viewUnderPointer;
                delete this._magnetUnderPointer;
                delete this._staticView;
                delete this._staticMagnet;
            }

            this._afterArrowheadMove();
        }

        delete this._action;
    }
});

if (typeof exports === 'object') {

    module.exports.Link = joint.dia.Link;
    module.exports.LinkView = joint.dia.LinkView;
}

//      JointJS library.
//      (c) 2011-2013 client IO

joint.dia.Paper = Backbone.View.extend({

    className: 'paper',

    options: {

        width: 800,
        height: 600,
        origin: {x: 0, y: 0}, // x,y coordinates in top-left corner
        gridSize: 50,
        perpendicularLinks: false,
        elementView: joint.dia.ElementView,
        linkView: joint.dia.LinkView,
        snapLinks: false, // false, true, { radius: value }

        // Marks all available magnets with 'available-magnet' class name and all available cells with
        // 'available-cell' class name. Marks them when dragging a link is started and unmark
        // when the dragging is stopped.
        markAvailable: false,

        // Defines what link model is added to the graph after an user clicks on an active magnet.
        // Value could be the Backbone.model or a function returning the Backbone.model
        // defaultLink: function(elementView, magnet) { return condition ? new customLink1() : new customLink2() }
        defaultLink: new joint.dia.Link,

        // Check whether to add a new link to the graph when user clicks on an a magnet.
        validateMagnet: function (cellView, magnet) {
            return magnet.getAttribute('magnet') !== 'passive';
        },

        // Check whether to allow or disallow the link connection while an arrowhead end (source/target)
        // being changed.
        validateConnection: function (cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
            return (end === 'target' ? cellViewT : cellViewS) instanceof joint.dia.ElementView;
        }
    },

    events: {

        'mousedown': 'pointerdown',
        'dblclick': 'mousedblclick',
        'click': 'mouseclick',
        'touchstart': 'pointerdown',
        'mousemove': 'pointermove',
        'touchmove': 'pointermove'
    },

    constructor: function (options) {

        this._configure(options);
        Backbone.View.apply(this, arguments);
    },

    _configure: function (options) {

        if (this.options) options = _.extend({}, _.result(this, 'options'), options);
        this.options = options;
    },

    initialize: function () {

        _.bindAll(this, 'addCell', 'sortCells', 'resetCells', 'pointerup', 'asyncRenderCells');

        this.svg = V('svg').node;
        this.viewport = V('g').node;

        // Append `<defs>` element to the SVG document. This is useful for filters and gradients.
        V(this.svg).append(V('defs').node);

        V(this.viewport).attr({'class': 'viewport'});

        V(this.svg).append(this.viewport);

        this.$el.append(this.svg);

        this.setOrigin();
        this.setDimensions();

        this.listenTo(this.model, 'add', this.onAddCell);
        this.listenTo(this.model, 'reset', this.resetCells);
        this.listenTo(this.model, 'sort', this.sortCells);

        $(document).on('mouseup touchend', this.pointerup);

        // Hold the value when mouse has been moved: when mouse moved, no click event will be triggered.
        this._mousemoved = false;
    },

    remove: function () {

        $(document).off('mouseup touchend', this.pointerup);

        Backbone.View.prototype.remove.call(this);
    },

    setDimensions: function (width, height) {

        width = this.options.width = width || this.options.width;
        height = this.options.height = height || this.options.height;

        V(this.svg).attr({width: width, height: height});

        this.trigger('resize', width, height);
    },

    setOrigin: function (ox, oy) {

        this.options.origin.x = ox || 0;
        this.options.origin.y = oy || 0;

        V(this.viewport).translate(ox, oy, {absolute: true});

        this.trigger('translate', ox, oy);
    },

    // Expand/shrink the paper to fit the content. Snap the width/height to the grid
    // defined in `gridWidth`, `gridHeight`. `padding` adds to the resulting width/height of the paper.
    // When options { fitNegative: true } it also translates the viewport in order to make all
    // the content visible.
    fitToContent: function (gridWidth, gridHeight, padding, opt) { // alternatively function(opt)

        if (_.isObject(gridWidth)) {
            // first parameter is an option object
            opt = gridWidth;
            gridWidth = opt.gridWidth || 1;
            gridHeight = opt.gridHeight || 1;
            padding = opt.padding || 0;

        } else {

            opt = opt || {};
            gridWidth = gridWidth || 1;
            gridHeight = gridHeight || 1;
            padding = padding || 0;
        }

        // Calculate the paper size to accomodate all the graph's elements.
        var bbox = V(this.viewport).bbox(true, this.svg);

        var currentScale = V(this.viewport).scale();

        bbox.x *= currentScale.sx;
        bbox.y *= currentScale.sy;
        bbox.width *= currentScale.sx;
        bbox.height *= currentScale.sy;

        var calcWidth = Math.max(Math.ceil((bbox.width + bbox.x) / gridWidth), 1) * gridWidth;
        var calcHeight = Math.max(Math.ceil((bbox.height + bbox.y) / gridHeight), 1) * gridHeight;

        var tx = 0;
        var ty = 0;

        if ((opt.allowNewOrigin == 'negative' && bbox.x < 0) || (opt.allowNewOrigin == 'positive' && bbox.x >= 0) || opt.allowNewOrigin == 'any') {
            tx = Math.ceil(-bbox.x / gridWidth) * gridWidth;
            tx += padding;
            calcWidth += tx;
        }

        if ((opt.allowNewOrigin == 'negative' && bbox.y < 0) || (opt.allowNewOrigin == 'positive' && bbox.y >= 0) || opt.allowNewOrigin == 'any') {
            ty = Math.ceil(-bbox.y / gridHeight) * gridHeight;
            ty += padding;
            calcHeight += ty;
        }

        calcWidth += padding;
        calcHeight += padding;

        var dimensionChange = calcWidth != this.options.width || calcHeight != this.options.height;
        var originChange = tx != this.options.origin.x || ty != this.options.origin.y;

        // Change the dimensions only if there is a size discrepency or an origin change
        if (originChange) {
            this.setOrigin(tx, ty);
        }
        if (dimensionChange) {
            this.setDimensions(calcWidth, calcHeight);
        }
    },

    scaleContentToFit: function (opt) {

        var contentBBox = this.getContentBBox();

        if (!contentBBox.width || !contentBBox.height) return;

        opt = opt || {};

        _.defaults(opt, {
            padding: 0,
            preserveAspectRatio: true,
            scaleGrid: null,
            minScale: 0,
            maxScale: Number.MAX_VALUE
            //minScaleX
            //minScaleY
            //maxScaleX
            //maxScaleY
            //fittingBBox
        });

        var padding = opt.padding;

        var minScaleX = opt.minScaleX || opt.minScale;
        var maxScaleX = opt.maxScaleX || opt.maxScale;
        var minScaleY = opt.minScaleY || opt.minScale;
        var maxScaleY = opt.maxScaleY || opt.maxScale;

        var fittingBBox = opt.fittingBBox || ({
                x: this.options.origin.x,
                y: this.options.origin.y,
                width: this.options.width,
                height: this.options.height
            });

        fittingBBox = g.rect(fittingBBox).moveAndExpand({
            x: padding,
            y: padding,
            width: -2 * padding,
            height: -2 * padding
        });

        var currentScale = V(this.viewport).scale();

        var newSx = fittingBBox.width / contentBBox.width * currentScale.sx;
        var newSy = fittingBBox.height / contentBBox.height * currentScale.sy;

        if (opt.preserveAspectRatio) {
            newSx = newSy = Math.min(newSx, newSy);
        }

        // snap scale to a grid
        if (opt.scaleGrid) {

            var gridSize = opt.scaleGrid;

            newSx = gridSize * Math.floor(newSx / gridSize);
            newSy = gridSize * Math.floor(newSy / gridSize);
        }

        // scale min/max boundaries
        newSx = Math.min(maxScaleX, Math.max(minScaleX, newSx));
        newSy = Math.min(maxScaleY, Math.max(minScaleY, newSy));

        this.scale(newSx, newSy);

        var contentTranslation = this.getContentBBox();

        var newOx = fittingBBox.x - contentTranslation.x;
        var newOy = fittingBBox.y - contentTranslation.y;

        this.setOrigin(newOx, newOy);
    },

    getContentBBox: function () {

        var crect = this.viewport.getBoundingClientRect();

        // Using Screen CTM was the only way to get the real viewport bounding box working in both
        // Google Chrome and Firefox.
        var screenCTM = this.viewport.getScreenCTM();

        // for non-default origin we need to take the viewport translation into account
        var viewportCTM = this.viewport.getCTM();

        var bbox = g.rect({
            x: crect.left - screenCTM.e + viewportCTM.e,
            y: crect.top - screenCTM.f + viewportCTM.f,
            width: crect.width,
            height: crect.height
        });

        return bbox;
    },

    createViewForModel: function (cell) {

        var view;

        var type = cell.get('type');
        var module = type.split('.')[0];
        var entity = type.split('.')[1];

        // If there is a special view defined for this model, use that one instead of the default
        // `elementView`/`linkView`.
        if (joint.shapes[module] && joint.shapes[module][entity + 'View']) {

            view = new joint.shapes[module][entity + 'View']({model: cell, interactive: this.options.interactive});

        } else if (cell instanceof joint.dia.Element) {

            view = new this.options.elementView({model: cell, interactive: this.options.interactive});

        } else {

            view = new this.options.linkView({model: cell, interactive: this.options.interactive});
        }

        return view;
    },

    onAddCell: function (cell, graph, options) {

        if (this.options.async && options.async !== false && _.isNumber(options.position)) {

            this._asyncCells = this._asyncCells || [];
            this._asyncCells.push(cell);

            if (options.position == 0) {

                if (this._frameId) throw 'another asynchronous rendering in progress';

                this.asyncRenderCells(this._asyncCells);
                delete this._asyncCells;
            }

        } else {

            this.addCell(cell);
        }
    },

    addCell: function (cell) {

        var view = this.createViewForModel(cell);

        V(this.viewport).append(view.el);
        view.paper = this;
        view.render();

        // This is the only way to prevent image dragging in Firefox that works.
        // Setting -moz-user-select: none, draggable="false" attribute or user-drag: none didn't help.
        $(view.el).find('image').on('dragstart', function () { return false; });
    },

    resetCells: function (cellsCollection) {

        $(this.viewport).empty();

        var cells = cellsCollection.models.slice();

        // Make sure links are always added AFTER elements.
        // They wouldn't find their sources/targets in the DOM otherwise.
        cells.sort(function (a, b) { return a instanceof joint.dia.Link ? 1 : -1; });

        if (this._frameId) {

            joint.util.cancelFrame(this._frameId);
            delete this._frameId;
        }

        if (this.options.async) {

            this.asyncRenderCells(cells);
            // Sort the cells once all elements rendered (see asyncRenderCells()).

        } else {

            _.each(cells, this.addCell, this);

            // Sort the cells in the DOM manually as we might have changed the order they
            // were added to the DOM (see above).
            this.sortCells();
        }
    },

    asyncRenderCells: function (cells) {

        var done = false;

        if (this._frameId) {

            _.each(_.range(this.options.async && this.options.async.batchSize || 50), function () {

                var cell = cells.shift();
                done = !cell;
                if (!done) this.addCell(cell);

            }, this);
        }

        if (done) {

            delete this._frameId;
            this.sortCells();
            this.trigger('render:done');

        } else {

            this._frameId = joint.util.nextFrame(_.bind(function () {
                this.asyncRenderCells(cells);
            }, this));
        }
    },

    sortCells: function () {

        // Run insertion sort algorithm in order to efficiently sort DOM elements according to their
        // associated model `z` attribute.

        var $cells = $(this.viewport).children('[model-id]');
        var cells = this.model.get('cells');

        this.sortElements($cells, function (a, b) {

            var cellA = cells.get($(a).attr('model-id'));
            var cellB = cells.get($(b).attr('model-id'));

            return (cellA.get('z') || 0) > (cellB.get('z') || 0) ? 1 : -1;
        });
    },

    // Highly inspired by the jquery.sortElements plugin by Padolsey.
    // See http://james.padolsey.com/javascript/sorting-elements-with-jquery/.
    sortElements: function (elements, comparator) {

        var $elements = $(elements);

        var placements = $elements.map(function () {

            var sortElement = this;
            var parentNode = sortElement.parentNode;

            // Since the element itself will change position, we have
            // to have some way of storing it's original position in
            // the DOM. The easiest way is to have a 'flag' node:
            var nextSibling = parentNode.insertBefore(
                document.createTextNode(''),
                sortElement.nextSibling
            );

            return function () {

                if (parentNode === this) {
                    throw new Error(
                        "You can't sort elements if any one is a descendant of another."
                    );
                }

                // Insert before flag:
                parentNode.insertBefore(this, nextSibling);
                // Remove flag:
                parentNode.removeChild(nextSibling);

            };
        });

        return Array.prototype.sort.call($elements, comparator).each(function (i) {
            placements[i].call(this);
        });
    },

    scale: function (sx, sy, ox, oy) {

        sy = sy || sx;

        if (_.isUndefined(ox)) {

            ox = 0;
            oy = 0;
        }

        // Remove previous transform so that the new scale is not affected by previous scales, especially
        // the old translate() does not affect the new translate if an origin is specified.
        V(this.viewport).attr('transform', '');

        var oldTx = this.options.origin.x;
        var oldTy = this.options.origin.y;

        // TODO: V.scale() doesn't support setting scale origin. #Fix        
        if (ox || oy || oldTx || oldTy) {

            var newTx = oldTx - ox * (sx - 1);
            var newTy = oldTy - oy * (sy - 1);
            this.setOrigin(newTx, newTy);
        }

        V(this.viewport).scale(sx, sy);

        this.trigger('scale', sx, sy, ox, oy);

        return this;
    },

    rotate: function (deg, ox, oy) {

        // If the origin is not set explicitely, rotate around the center. Note that
        // we must use the plain bounding box (`this.el.getBBox()` instead of the one that gives us
        // the real bounding box (`bbox()`) including transformations).
        if (_.isUndefined(ox)) {

            var bbox = this.viewport.getBBox();
            ox = bbox.width / 2;
            oy = bbox.height / 2;
        }

        V(this.viewport).rotate(deg, ox, oy);
    },

    // Find the first view climbing up the DOM tree starting at element `el`. Note that `el` can also
    // be a selector or a jQuery object.
    findView: function (el) {

        var $el = this.$(el);

        if ($el.length === 0 || $el[0] === this.el) {

            return undefined;
        }

        if ($el.data('view')) {

            return $el.data('view');
        }

        return this.findView($el.parent());
    },

    // Find a view for a model `cell`. `cell` can also be a string representing a model `id`.
    findViewByModel: function (cell) {

        var id = _.isString(cell) ? cell : cell.id;

        var $view = this.$('[model-id="' + id + '"]');
        if ($view.length) {

            return $view.data('view');
        }
        return undefined;
    },

    // Find all views at given point
    findViewsFromPoint: function (p) {

        p = g.point(p);

        var views = _.map(this.model.getElements(), this.findViewByModel);

        return _.filter(views, function (view) {
            return g.rect(V(view.el).bbox(false, this.viewport)).containsPoint(p);
        }, this);
    },

    // Find all views in given area
    findViewsInArea: function (r) {

        r = g.rect(r);

        var views = _.map(this.model.getElements(), this.findViewByModel);

        return _.filter(views, function (view) {
            return r.intersect(g.rect(V(view.el).bbox(false, this.viewport)));
        }, this);
    },

    getModelById: function (id) {

        return this.model.getCell(id);
    },

    snapToGrid: function (p) {

        // Convert global coordinates to the local ones of the `viewport`. Otherwise,
        // improper transformation would be applied when the viewport gets transformed (scaled/rotated). 
        var localPoint = V(this.viewport).toLocalPoint(p.x, p.y);

        return {
            x: g.snapToGrid(localPoint.x, this.options.gridSize),
            y: g.snapToGrid(localPoint.y, this.options.gridSize)
        };
    },

    getDefaultLink: function (cellView, magnet) {

        return _.isFunction(this.options.defaultLink)
            // default link is a function producing link model
            ? this.options.defaultLink.call(this, cellView, magnet)
            // default link is the Backbone model
            : this.options.defaultLink.clone();
    },

    // Interaction.
    // ------------

    mousedblclick: function (evt) {

        evt.preventDefault();
        evt = joint.util.normalizeEvent(evt);

        var view = this.findView(evt.target);
        var localPoint = this.snapToGrid({x: evt.clientX, y: evt.clientY});

        if (view) {

            view.pointerdblclick(evt, localPoint.x, localPoint.y);

        } else {

            this.trigger('blank:pointerdblclick', evt, localPoint.x, localPoint.y);
        }
    },

    mouseclick: function (evt) {

        // Trigger event when mouse not moved.
        if (!this._mousemoved) {

            evt = joint.util.normalizeEvent(evt);

            var view = this.findView(evt.target);
            var localPoint = this.snapToGrid({x: evt.clientX, y: evt.clientY});

            if (view) {

                view.pointerclick(evt, localPoint.x, localPoint.y);

            } else {

                this.trigger('blank:pointerclick', evt, localPoint.x, localPoint.y);
            }
        }

        this._mousemoved = false;
    },

    pointerdown: function (evt) {

        evt = joint.util.normalizeEvent(evt);

        var view = this.findView(evt.target);

        var localPoint = this.snapToGrid({x: evt.clientX, y: evt.clientY});

        if (view) {

            this.sourceView = view;

            view.pointerdown(evt, localPoint.x, localPoint.y);

        } else {

            this.trigger('blank:pointerdown', evt, localPoint.x, localPoint.y);
        }
    },

    pointermove: function (evt) {

        evt.preventDefault();
        evt = joint.util.normalizeEvent(evt);

        if (this.sourceView) {

            // Mouse moved.
            this._mousemoved = true;

            var localPoint = this.snapToGrid({x: evt.clientX, y: evt.clientY});

            this.sourceView.pointermove(evt, localPoint.x, localPoint.y);
        }
    },

    pointerup: function (evt) {

        evt = joint.util.normalizeEvent(evt);

        var localPoint = this.snapToGrid({x: evt.clientX, y: evt.clientY});

        if (this.sourceView) {

            this.sourceView.pointerup(evt, localPoint.x, localPoint.y);

            //"delete sourceView" occasionally throws an error in chrome (illegal access exception)
            this.sourceView = null;

        } else {

            this.trigger('blank:pointerup', evt, localPoint.x, localPoint.y);
        }
    }
});

//      JointJS library.
//      (c) 2011-2013 client IO

if (typeof exports === 'object') {

    var joint = {
        util: require('../src/core').util,
        shapes: {},
        dia: {
            Element: require('../src/joint.dia.element').Element,
            ElementView: require('../src/joint.dia.element').ElementView
        }
    };
    var _ = require('lodash');
}

joint.shapes.basic = {};

joint.shapes.basic.Generic = joint.dia.Element.extend({

    defaults: joint.util.deepSupplement({

        type: 'basic.Generic',
        attrs: {
            '.': {fill: '#FFFFFF', stroke: 'none'}
        }

    }, joint.dia.Element.prototype.defaults)
});

joint.shapes.basic.Rect = joint.shapes.basic.Generic.extend({

    markup: '<g class="rotatable"><g class="scalable"><rect/></g><text/></g>',

    defaults: joint.util.deepSupplement({

        type: 'basic.Rect',
        attrs: {
            'rect': {fill: '#FFFFFF', stroke: 'black', width: 100, height: 60},
            'text': {
                'font-size': 14,
                text: '',
                'ref-x': .5,
                'ref-y': .5,
                ref: 'rect',
                'y-alignment': 'middle',
                'x-alignment': 'middle',
                fill: 'black',
                'font-family': 'Arial, helvetica, sans-serif'
            }
        }

    }, joint.shapes.basic.Generic.prototype.defaults)
});

joint.shapes.basic.Text = joint.shapes.basic.Generic.extend({

    markup: '<g class="rotatable"><g class="scalable"><text/></g></g>',

    defaults: joint.util.deepSupplement({

        type: 'basic.Text',
        attrs: {
            'text': {'font-size': 18, fill: 'black'}
        }

    }, joint.shapes.basic.Generic.prototype.defaults)
});

joint.shapes.basic.Circle = joint.shapes.basic.Generic.extend({

    markup: '<g class="rotatable"><g class="scalable"><circle/></g><text/></g>',

    defaults: joint.util.deepSupplement({

        type: 'basic.Circle',
        size: {width: 60, height: 60},
        attrs: {
            'circle': {fill: '#FFFFFF', stroke: 'black', r: 30, transform: 'translate(30, 30)'},
            'text': {
                'font-size': 14,
                text: '',
                'text-anchor': 'middle',
                'ref-x': .5,
                'ref-y': .5,
                ref: 'circle',
                'y-alignment': 'middle',
                fill: 'black',
                'font-family': 'Arial, helvetica, sans-serif'
            }
        }
    }, joint.shapes.basic.Generic.prototype.defaults)
});

joint.shapes.basic.Image = joint.shapes.basic.Generic.extend({

    markup: '<g class="rotatable"><g class="scalable"><image/></g><text/></g>',

    defaults: joint.util.deepSupplement({

        type: 'basic.Image',
        attrs: {
            'text': {
                'font-size': 14,
                text: '',
                'text-anchor': 'middle',
                'ref-x': .5,
                'ref-dy': 20,
                ref: 'image',
                'y-alignment': 'middle',
                fill: 'black',
                'font-family': 'Arial, helvetica, sans-serif'
            }
        }
    }, joint.shapes.basic.Generic.prototype.defaults)
});

joint.shapes.basic.Path = joint.shapes.basic.Generic.extend({

    markup: '<g class="rotatable"><g class="scalable"><path/></g><text/></g>',

    defaults: joint.util.deepSupplement({

        type: 'basic.Path',
        size: {width: 60, height: 60},
        attrs: {
            'path': {fill: '#FFFFFF', stroke: 'black'},
            'text': {
                'font-size': 14,
                text: '',
                'text-anchor': 'middle',
                'ref-x': .5,
                'ref-dy': 20,
                ref: 'path',
                'y-alignment': 'middle',
                fill: 'black',
                'font-family': 'Arial, helvetica, sans-serif'
            }
        }
    }, joint.shapes.basic.Generic.prototype.defaults)
});

joint.shapes.basic.Rhombus = joint.shapes.basic.Path.extend({

    defaults: joint.util.deepSupplement({

        type: 'basic.Rhombus',
        attrs: {
            'path': {d: 'M 30 0 L 60 30 30 60 0 30 z'},
            'text': {'ref-y': .5}
        }

    }, joint.shapes.basic.Path.prototype.defaults)
});

// PortsModelInterface is a common interface for shapes that have ports. This interface makes it easy
// to create new shapes with ports functionality. It is assumed that the new shapes have
// `inPorts` and `outPorts` array properties. Only these properties should be used to set ports.
// In other words, using this interface, it is no longer recommended to set ports directly through the
// `attrs` object.

// Usage:
// joint.shapes.custom.MyElementWithPorts = joint.shapes.basic.Path.extend(_.extend({},
// joint.shapes.basic.PortsModelInterface, { getPortAttrs: function(portName, index, total, selector, type) { var attrs
// = {}; var portClass = 'port' + index; var portSelector = selector + '>.' + portClass; var portTextSelector =
// portSelector + '>text'; var portCircleSelector = portSelector + '>circle';  attrs[portTextSelector] = { text:
// portName }; attrs[portCircleSelector] = { port: { id: portName || _.uniqueId(type) , type: type } };
// attrs[portSelector] = { ref: 'rect', 'ref-y': (index + 0.5) * (1 / total) };  if (selector === '.outPorts') {
// attrs[portSelector]['ref-dx'] = 0; }  return attrs; } }));
joint.shapes.basic.PortsModelInterface = {

    initialize: function () {

        this.updatePortsAttrs();
        this.on('change:inPorts change:outPorts', this.updatePortsAttrs, this);

        // Call the `initialize()` of the parent.
        this.constructor.__super__.constructor.__super__.initialize.apply(this, arguments);
    },

    updatePortsAttrs: function (eventName) {

        // Delete previously set attributes for ports.
        var currAttrs = this.get('attrs');
        _.each(this._portSelectors, function (selector) {
            if (currAttrs[selector]) delete currAttrs[selector];
        });

        // This holds keys to the `attrs` object for all the port specific attribute that
        // we set in this method. This is necessary in order to remove previously set
        // attributes for previous ports.
        this._portSelectors = [];

        var attrs = {};

        _.each(this.get('inPorts'), function (portName, index, ports) {
            var portAttributes = this.getPortAttrs(portName, index, ports.length, '.inPorts', 'in');
            this._portSelectors = this._portSelectors.concat(_.keys(portAttributes));
            _.extend(attrs, portAttributes);
        }, this);

        _.each(this.get('outPorts'), function (portName, index, ports) {
            var portAttributes = this.getPortAttrs(portName, index, ports.length, '.outPorts', 'out');
            this._portSelectors = this._portSelectors.concat(_.keys(portAttributes));
            _.extend(attrs, portAttributes);
        }, this);

        // Silently set `attrs` on the cell so that noone knows the attrs have changed. This makes sure
        // that, for example, command manager does not register `change:attrs` command but only
        // the important `change:inPorts`/`change:outPorts` command.
        this.attr(attrs, {silent: true});
        // Manually call the `processPorts()` method that is normally called on `change:attrs` (that we just made
        // silent).
        this.processPorts();
        // Let the outside world (mainly the `ModelView`) know that we're done configuring the `attrs` object.
        this.trigger('process:ports');
    },

    getPortSelector: function (name) {

        var selector = '.inPorts';
        var index = this.get('inPorts').indexOf(name);

        if (index < 0) {
            selector = '.outPorts';
            index = this.get('outPorts').indexOf(name);

            if (index < 0) throw new Error("getPortSelector(): Port doesn't exist.");
        }

        return selector + '>g:nth-child(' + (index + 1) + ')>circle';
    }
};

joint.shapes.basic.PortsViewInterface = {

    initialize: function () {

        // `Model` emits the `process:ports` whenever it's done configuring the `attrs` object for ports.
        this.listenTo(this.model, 'process:ports', this.update);

        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
    },

    update: function () {

        // First render ports so that `attrs` can be applied to those newly created DOM elements
        // in `ElementView.prototype.update()`.
        this.renderPorts();
        joint.dia.ElementView.prototype.update.apply(this, arguments);
    },

    renderPorts: function () {

        var $inPorts = this.$('.inPorts').empty();
        var $outPorts = this.$('.outPorts').empty();

        var portTemplate = _.template(this.model.portMarkup);

        _.each(_.filter(this.model.ports, function (p) { return p.type === 'in' }), function (port, index) {

            $inPorts.append(V(portTemplate({id: index, port: port})).node);
        });
        _.each(_.filter(this.model.ports, function (p) { return p.type === 'out' }), function (port, index) {

            $outPorts.append(V(portTemplate({id: index, port: port})).node);
        });
    }
};

joint.shapes.basic.TextBlock = joint.shapes.basic.Generic.extend({

    markup: ['<g class="rotatable"><g class="scalable"><rect/></g><switch>',

        // if foreignObject supported

        '<foreignObject requiredFeatures="http://www.w3.org/TR/SVG11/feature#Extensibility" class="fobj">',
        '<body xmlns="http://www.w3.org/1999/xhtml"><div/></body>',
        '</foreignObject>',

        // else foreignObject is not supported (fallback for IE)
        '<text class="content"/>',

        '</switch></g>'].join(''),

    defaults: joint.util.deepSupplement({

        type: 'basic.TextBlock',

        // see joint.css for more element styles
        attrs: {
            rect: {
                fill: '#ffffff',
                stroke: '#000000',
                width: 80,
                height: 100
            },
            text: {
                fill: '#000000',
                'font-size': 14,
                'font-family': 'Arial, helvetica, sans-serif'
            },
            '.content': {
                text: '',
                ref: 'rect',
                'ref-x': .5,
                'ref-y': .5,
                'y-alignment': 'middle',
                'x-alignment': 'middle'
            }
        },

        content: ''

    }, joint.shapes.basic.Generic.prototype.defaults),

    initialize: function () {

        if (typeof SVGForeignObjectElement !== 'undefined') {

            // foreignObject supported
            this.setForeignObjectSize(this, this.get('size'));
            this.setDivContent(this, this.get('content'));
            this.listenTo(this, 'change:size', this.setForeignObjectSize);
            this.listenTo(this, 'change:content', this.setDivContent);

        }

        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },

    setForeignObjectSize: function (cell, size) {

        // Selector `foreignObject' doesn't work accross all browsers, we'r using class selector instead.
        // We have to clone size as we don't want attributes.div.style to be same object as attributes.size.
        cell.attr({
            '.fobj': _.clone(size),
            div: {style: _.clone(size)}
        });
    },

    setDivContent: function (cell, content) {

        // Append the content to div as html.
        cell.attr({
            div: {
                html: content
            }
        });
    }

});

// TextBlockView implements the fallback for IE when no foreignObject exists and
// the text needs to be manually broken.
joint.shapes.basic.TextBlockView = joint.dia.ElementView.extend({

    initialize: function () {

        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        if (typeof SVGForeignObjectElement === 'undefined') {

            this.noSVGForeignObjectElement = true;

            this.listenTo(this.model, 'change:content', function (cell) {
                // avoiding pass of extra paramters
                this.updateContent(cell);
            });
        }
    },

    update: function (cell, renderingOnlyAttrs) {

        if (this.noSVGForeignObjectElement) {

            var model = this.model;

            // Update everything but the content first.
            var noTextAttrs = _.omit(renderingOnlyAttrs || model.get('attrs'), '.content');
            joint.dia.ElementView.prototype.update.call(this, model, noTextAttrs);

            if (!renderingOnlyAttrs || _.has(renderingOnlyAttrs, '.content')) {
                // Update the content itself.
                this.updateContent(model, renderingOnlyAttrs);
            }

        } else {

            joint.dia.ElementView.prototype.update.call(this, model, renderingOnlyAttrs);
        }
    },

    updateContent: function (cell, renderingOnlyAttrs) {

        // Create copy of the text attributes
        var textAttrs = _.merge({}, (renderingOnlyAttrs || cell.get('attrs'))['.content']);

        delete textAttrs.text;

        // Break the content to fit the element size taking into account the attributes
        // set on the model.
        var text = joint.util.breakText(cell.get('content'), cell.get('size'), textAttrs, {
            // measuring sandbox svg document
            svgDocument: this.paper.svg
        });

        // Create a new attrs with same structure as the model attrs { text: { *textAttributes* }}
        var attrs = joint.util.setByPath({}, '.content', textAttrs, '/');

        // Replace text attribute with the one we just processed.
        attrs['.content'].text = text;

        // Update the view using renderingOnlyAttributes parameter.
        joint.dia.ElementView.prototype.update.call(this, cell, attrs);
    }
});

if (typeof exports === 'object') {

    module.exports = joint.shapes.basic;
}

joint.routers.orthogonal = function () {

    var sourceBBox, targetBBox;

    // Return the direction that one would have to take traveling from `p1` to `p2`.
    // This function assumes the line between `p1` and `p2` is orthogonal.
    function direction(p1, p2) {

        if (p1.y < p2.y && p1.x === p2.x) {
            return 'down';
        } else if (p1.y > p2.y && p1.x === p2.x) {
            return 'up';
        } else if (p1.x < p2.x && p1.y === p2.y) {
            return 'right';
        }
        return 'left';
    }

    function bestDirection(p1, p2, preferredDirection) {

        var directions;

        // This branching determines possible directions that one can take to travel
        // from `p1` to `p2`.
        if (p1.x < p2.x) {

            if (p1.y > p2.y) {
                directions = ['up', 'right'];
            }
            else if (p1.y < p2.y) {
                directions = ['down', 'right'];
            }
            else {
                directions = ['right'];
            }

        } else if (p1.x > p2.x) {

            if (p1.y > p2.y) {
                directions = ['up', 'left'];
            }
            else if (p1.y < p2.y) {
                directions = ['down', 'left'];
            }
            else {
                directions = ['left'];
            }

        } else {

            if (p1.y > p2.y) {
                directions = ['up'];
            }
            else {
                directions = ['down'];
            }
        }

        if (_.contains(directions, preferredDirection)) {
            return preferredDirection;
        }

        var direction = _.first(directions);

        // Should the direction be the exact opposite of the preferred direction,
        // try another one if such direction exists.
        switch (preferredDirection) {
            case 'down':
                if (direction === 'up') return _.last(directions);
                break;
            case 'up':
                if (direction === 'down') return _.last(directions);
                break;
            case 'left':
                if (direction === 'right') return _.last(directions);
                break;
            case 'right':
                if (direction === 'left') return _.last(directions);
                break;
        }
        return direction;
    };

    // Find a vertex in between the vertices `p1` and `p2` so that the route between those vertices
    // is orthogonal. Prefer going the direction determined by `preferredDirection`.
    function findMiddleVertex(p1, p2, preferredDirection) {

        var direction = bestDirection(p1, p2, preferredDirection);
        if (direction === 'down' || direction === 'up') {
            return {x: p1.x, y: p2.y, d: direction};
        }
        return {x: p2.x, y: p1.y, d: direction};
    }

    // Return points that one needs to draw a connection through in order to have a orthogonal link
    // routing from source to target going through `vertices`.
    function findOrthogonalRoute(vertices) {

        vertices = (vertices || []).slice();
        var orthogonalVertices = [];

        var sourceCenter = sourceBBox.center();
        var targetCenter = targetBBox.center();

        if (!vertices.length) {

            if (Math.abs(sourceCenter.x - targetCenter.x) < (sourceBBox.width / 2) ||
                Math.abs(sourceCenter.y - targetCenter.y) < (sourceBBox.height / 2)
            ) {

                vertices = [{
                    x: Math.min(sourceCenter.x, targetCenter.x) +
                    Math.abs(sourceCenter.x - targetCenter.x) / 2,
                    y: Math.min(sourceCenter.y, targetCenter.y) +
                    Math.abs(sourceCenter.y - targetCenter.y) / 2
                }];
            }
        }

        vertices.unshift(sourceCenter);
        vertices.push(targetCenter);

        var orthogonalVertex;
        var lastOrthogonalVertex;
        var vertex;
        var nextVertex;

        // For all the pairs of link model vertices...
        for (var i = 0; i < vertices.length - 1; i++) {

            vertex = vertices[i];
            nextVertex = vertices[i + 1];
            lastOrthogonalVertex = _.last(orthogonalVertices);

            if (i > 0) {
                // Push all the link vertices to the orthogonal route.
                orthogonalVertex = vertex;
                // Determine a direction between the last vertex and the new one.
                // Therefore, each vertex contains the `d` property describing the direction that one
                // would have to take to travel to that vertex.
                orthogonalVertex.d = lastOrthogonalVertex
                    ? direction(lastOrthogonalVertex, vertex)
                    : 'top';

                orthogonalVertices.push(orthogonalVertex);
                lastOrthogonalVertex = orthogonalVertex;
            }

            // Make sure that we don't create a vertex that would go the opposite direction then
            // that of the previous one.
            // Othwerwise, a 'spike' segment would be created which is not desirable.
            // Find a dummy vertex to keep the link orthogonal. Preferably, take the same direction
            // as the previous one.
            var d = lastOrthogonalVertex && lastOrthogonalVertex.d;
            orthogonalVertex = findMiddleVertex(vertex, nextVertex, d);

            // Do not add a new vertex that is the same as one of the vertices already added.
            if (!g.point(orthogonalVertex).equals(g.point(vertex)) && !g.point(orthogonalVertex).equals(g.point(nextVertex))) {

                orthogonalVertices.push(orthogonalVertex);
            }
        }
        return orthogonalVertices;
    };

    return function (vertices) {

        sourceBBox = this.sourceBBox;
        targetBBox = this.targetBBox;

        return findOrthogonalRoute(vertices);
    };

}();

joint.routers.manhattan = (function () {

    'use strict';

    var config = {

        // size of the step to find a route
        step: 10,

        // use of the perpendicular linkView option to connect center of element with first vertex
        perpendicular: true,

        // tells how to divide the paper when creating the elements map
        mapGridSize: 100,

        // should be source or target not to be consider as an obstacle
        excludeEnds: [], // 'source', 'target'

        // should be any element with a certain type not to be consider as an obstacle
        excludeTypes: ['basic.Text'],

        // if number of route finding loops exceed the maximum, stops searching and returns
        // fallback route
        maximumLoops: 500,

        // possible starting directions from an element
        startDirections: ['left', 'right', 'top', 'bottom'],

        // possible ending directions to an element
        endDirections: ['left', 'right', 'top', 'bottom'],

        // specify directions above
        directionMap: {
            right: {x: 1, y: 0},
            bottom: {x: 0, y: 1},
            left: {x: -1, y: 0},
            top: {x: 0, y: -1}
        },

        // maximum change of the direction
        maxAllowedDirectionChange: 1,

        // padding applied on the element bounding boxes
        paddingBox: function () {

            var step = this.step;

            return {
                x: -step,
                y: -step,
                width: 2 * step,
                height: 2 * step
            }
        },

        // an array of directions to find next points on the route
        directions: function () {

            var step = this.step;

            return [
                {offsetX: step, offsetY: 0, cost: step},
                {offsetX: 0, offsetY: step, cost: step},
                {offsetX: -step, offsetY: 0, cost: step},
                {offsetX: 0, offsetY: -step, cost: step}
            ];
        },

        // a penalty received for direction change
        penalties: function () {

            return [0, this.step / 2, this.step];
        },

        // heurestic method to determine the distance between two points
        estimateCost: function (from, to) {

            return from.manhattanDistance(to);
        },

        // a simple route used in situations, when main routing method fails
        // (exceed loops, inaccessible).
        fallbackRoute: function (from, to, opts) {

            // Find an orthogonal route ignoring obstacles.

            var prevDirIndexes = opts.prevDirIndexes || {};

            var point = (prevDirIndexes[from] || 0) % 2
                ? g.point(from.x, to.y)
                : g.point(to.x, from.y);

            return [point, to];
        },

        // if a function is provided, it's used to route the link while dragging an end
        // i.e. function(from, to, opts) { return []; }
        draggingRoute: null
    };

    // reconstructs a route by concating points with their parents
    function reconstructRoute(parents, point) {

        var route = [];
        var prevDiff = {x: 0, y: 0};
        var current = point;
        var parent;

        while ((parent = parents[current])) {

            var diff = parent.difference(current);

            if (!diff.equals(prevDiff)) {

                route.unshift(current);
                prevDiff = diff;
            }

            current = parent;
        }

        route.unshift(current);

        return route;
    };

    // find points around the rectangle taking given directions in the account
    function getRectPoints(bbox, directionList, opts) {

        var step = opts.step;

        var center = bbox.center();

        var startPoints = _.chain(opts.directionMap).pick(directionList).map(function (direction) {

            var x = direction.x * bbox.width / 2;
            var y = direction.y * bbox.height / 2;

            var point = g.point(center).offset(x, y).snapToGrid(step);

            if (bbox.containsPoint(point)) {

                point.offset(direction.x * step, direction.y * step);
            }

            return point;

        }).value();

        return startPoints;
    };

    // returns a direction index from start point to end point
    function getDirection(start, end, dirLen) {

        var dirAngle = 360 / dirLen;

        var q = Math.floor(start.theta(end) / dirAngle);

        return dirLen - q;
    }

    // finds the route between to points/rectangles implementing A* alghoritm
    function findRoute(start, end, map, opt) {

        var startDirections = opt.reversed ? opt.endDirections : opt.startDirections;
        var endDirections = opt.reversed ? opt.startDirections : opt.endDirections;

        // set of points we start pathfinding from
        var startSet = start instanceof g.rect
            ? getRectPoints(start, startDirections, opt)
            : [start];

        // set of points we want the pathfinding to finish at
        var endSet = end instanceof g.rect
            ? getRectPoints(end, endDirections, opt)
            : [end];

        var startCenter = startSet.length > 1 ? start.center() : startSet[0];
        var endCenter = endSet.length > 1 ? end.center() : endSet[0];

        // take into account  only accessible end points
        var endPoints = _.filter(endSet, function (point) {

            var mapKey = g.point(point).snapToGrid(opt.mapGridSize).toString();

            var accesible = _.every(map[mapKey], function (obstacle) {
                return !obstacle.containsPoint(point);
            });

            return accesible;
        });

        if (endPoints.length) {

            var step = opt.step;
            var penalties = opt.penalties;

            // choose the end point with the shortest estimated path cost
            var endPoint = _.chain(endPoints).invoke('snapToGrid', step).min(function (point) {

                return opt.estimateCost(startCenter, point);

            }).value();

            var parents = {};
            var costFromStart = {};
            var totalCost = {};

            // directions
            var dirs = opt.directions;
            var dirLen = dirs.length;
            var dirHalfLen = dirLen / 2;
            var dirIndexes = opt.previousDirIndexes || {};

            // The set of point already evaluated.
            var closeHash = {}; // keeps only information whether a point was evaluated'

            // The set of tentative points to be evaluated, initially containing the start points
            var openHash = {}; // keeps only information whether a point is to be evaluated'
            var openSet = _.chain(startSet).invoke('snapToGrid', step).each(function (point) {

                var key = point.toString();

                costFromStart[key] = 0; // Cost from start along best known path.
                totalCost[key] = opt.estimateCost(point, endPoint);
                dirIndexes[key] = dirIndexes[key] || getDirection(startCenter, point, dirLen);
                openHash[key] = true;

            }).map(function (point) {

                return point.toString();

            }).sortBy(function (pointKey) {

                return totalCost[pointKey];

            }).value();

            var loopCounter = opt.maximumLoops;

            var maxAllowedDirectionChange = opt.maxAllowedDirectionChange;

            // main route finding loop
            while (openSet.length && loopCounter--) {

                var currentKey = openSet[0];
                var currentPoint = g.point(currentKey);

                if (endPoint.equals(currentPoint)) {

                    opt.previousDirIndexes = _.pick(dirIndexes, currentKey);
                    return reconstructRoute(parents, currentPoint);
                }

                // remove current from the open list
                openSet.splice(0, 1);
                openHash[neighborKey] = null;

                // add current to the close list
                closeHash[neighborKey] = true;

                var currentDirIndex = dirIndexes[currentKey];
                var currentDist = costFromStart[currentKey];

                for (var dirIndex = 0; dirIndex < dirLen; dirIndex++) {

                    var dirChange = Math.abs(dirIndex - currentDirIndex);

                    if (dirChange > dirHalfLen) {

                        dirChange = dirLen - dirChange;
                    }

                    // if the direction changed rapidly don't use this point
                    if (dirChange > maxAllowedDirectionChange) {

                        continue;
                    }

                    var dir = dirs[dirIndex];

                    var neighborPoint = g.point(currentPoint).offset(dir.offsetX, dir.offsetY);
                    var neighborKey = neighborPoint.toString();

                    if (closeHash[neighborKey]) {

                        continue;
                    }

                    // is point accesible - no obstacle in the way

                    var mapKey = g.point(neighborPoint).snapToGrid(opt.mapGridSize).toString();

                    var isAccesible = _.every(map[mapKey], function (obstacle) {
                        return !obstacle.containsPoint(neighborPoint);
                    });

                    if (!isAccesible) {

                        continue;
                    }

                    var inOpenSet = _.has(openHash, neighborKey);

                    var costToNeighbor = currentDist + dir.cost;

                    if (!inOpenSet || costToNeighbor < costFromStart[neighborKey]) {

                        parents[neighborKey] = currentPoint;
                        dirIndexes[neighborKey] = dirIndex;
                        costFromStart[neighborKey] = costToNeighbor;

                        totalCost[neighborKey] = costToNeighbor +
                            opt.estimateCost(neighborPoint, endPoint) +
                            penalties[dirChange];

                        if (!inOpenSet) {

                            var openIndex = _.sortedIndex(openSet, neighborKey, function (openKey) {

                                return totalCost[openKey];
                            });

                            openSet.splice(openIndex, 0, neighborKey);
                            openHash[neighborKey] = true;
                        }
                    }
                    ;
                }
                ;
            }
        }

        // no route found ('to' point wasn't either accessible or finding route took
        // way to much calculations)
        return opt.fallbackRoute(startCenter, endCenter, opt);
    };

    // initiation of the route finding
    function router(oldVertices, opt) {

        // resolve some of the options
        opt.directions = _.result(opt, 'directions');
        opt.penalties = _.result(opt, 'penalties');
        opt.paddingBox = _.result(opt, 'paddingBox');

        // enable/disable linkView perpendicular option
        this.options.perpendicular = !!opt.perpendicular;

        // As route changes its shape rapidly when we start finding route from different point
        // it's necessary to start from the element that was not interacted with
        // (the position was changed) at very last.
        var reverseRouting = opt.reversed = (this.lastEndChange === 'source');

        var sourceBBox = reverseRouting ? g.rect(this.targetBBox) : g.rect(this.sourceBBox);
        var targetBBox = reverseRouting ? g.rect(this.sourceBBox) : g.rect(this.targetBBox);

        // expand boxes by specific padding
        sourceBBox.moveAndExpand(opt.paddingBox);
        targetBBox.moveAndExpand(opt.paddingBox);

        // building an elements map

        var link = this.model;
        var graph = this.paper.model;

        // source or target element could be excluded from set of obstacles
        var excludedEnds = _.chain(opt.excludeEnds)
            .map(link.get, link)
            .pluck('id')
            .map(graph.getCell, graph).value();

        var mapGridSize = opt.mapGridSize;

        // builds a map of all elements for quicker obstacle queries (i.e. is a point contained
        // in any obstacle?) (a simplified grid search)
        // The paper is divided to smaller cells, where each of them holds an information which
        // elements belong to it. When we query whether a point is in an obstacle we don't need
        // to go through all obstacles, we check only those in a particular cell.
        var map = _.chain(graph.getElements())
            // remove source and target element if required
            .difference(excludedEnds)
            // remove all elements whose type is listed in excludedTypes array
            .reject(function (element) {
                return _.contains(opt.excludeTypes, element.get('type'));
            })
            // change elements (models) to their bounding boxes
            .invoke('getBBox')
            // expand their boxes by specific padding
            .invoke('moveAndExpand', opt.paddingBox)
            // build the map
            .foldl(function (res, bbox) {

                var origin = bbox.origin().snapToGrid(mapGridSize);
                var corner = bbox.corner().snapToGrid(mapGridSize);

                for (var x = origin.x; x <= corner.x; x += mapGridSize) {
                    for (var y = origin.y; y <= corner.y; y += mapGridSize) {

                        var gridKey = x + '@' + y;

                        res[gridKey] = res[gridKey] || [];
                        res[gridKey].push(bbox);
                    }
                }

                return res;

            }, {}).value();

        // pathfinding

        var newVertices = [];

        var points = _.map(oldVertices, g.point);

        var tailPoint = sourceBBox.center();

        // find a route by concating all partial routes (routes need to go through the vertices)
        // startElement -> vertex[1] -> ... -> vertex[n] -> endElement
        for (var i = 0, len = points.length; i <= len; i++) {

            var partialRoute = null;

            var from = to || sourceBBox;
            var to = points[i];

            if (!to) {

                to = targetBBox;

                // 'to' is not a vertex. If the target is a point (i.e. it's not an element), we
                // might use dragging route instead of main routing method if that is enabled.
                var endingAtPoint = !this.model.get('source').id || !this.model.get('target').id;

                if (endingAtPoint && _.isFunction(opt.draggingRoute)) {
                    // Make sure we passing points only (not rects).
                    var dragFrom = from instanceof g.rect ? from.center() : from;
                    partialRoute = opt.draggingRoute(dragFrom, to.origin(), opt);
                }
            }

            // if partial route has not been calculated yet use the main routing method to find one
            partialRoute = partialRoute || findRoute(from, to, map, opt);

            var leadPoint = _.first(partialRoute);

            if (leadPoint && leadPoint.equals(tailPoint)) {

                // remove the first point if the previous partial route had the same point as last
                partialRoute.shift();
            }

            tailPoint = _.last(partialRoute) || tailPoint;

            newVertices = newVertices.concat(partialRoute);
        }
        ;

        // we might have to reverse the result if we swapped source and target at the beginning
        return reverseRouting ? newVertices.reverse() : newVertices;
    };

    // public function
    return function (vertices, opt, linkView) {

        return router.call(linkView, vertices, _.extend({}, config, opt));
    };

})();

joint.routers.metro = (function () {

    if (!_.isFunction(joint.routers.manhattan)) {

        throw('Metro requires the manhattan router.');
    }

    var config = {

        // cost of a diagonal step (calculated if not defined).
        diagonalCost: null,

        // an array of directions to find next points on the route
        directions: function () {

            var step = this.step;
            var diagonalCost = this.diagonalCost || Math.ceil(Math.sqrt(step * step << 1));

            return [
                {offsetX: step, offsetY: 0, cost: step},
                {offsetX: step, offsetY: step, cost: diagonalCost},
                {offsetX: 0, offsetY: step, cost: step},
                {offsetX: -step, offsetY: step, cost: diagonalCost},
                {offsetX: -step, offsetY: 0, cost: step},
                {offsetX: -step, offsetY: -step, cost: diagonalCost},
                {offsetX: 0, offsetY: -step, cost: step},
                {offsetX: step, offsetY: -step, cost: diagonalCost}
            ];
        },

        // a simple route used in situations, when main routing method fails
        // (exceed loops, inaccessible).
        fallbackRoute: function (from, to, opts) {

            // Find a route which breaks by 45 degrees ignoring all obstacles.

            var theta = from.theta(to);

            var a = {x: to.x, y: from.y};
            var b = {x: from.x, y: to.y};

            if (theta % 180 > 90) {
                var t = a;
                a = b;
                b = t;
            }

            var p1 = (theta % 90) < 45 ? a : b;

            var l1 = g.line(from, p1);

            var alpha = 90 * Math.ceil(theta / 90);

            var p2 = g.point.fromPolar(l1.squaredLength(), g.toRad(alpha + 135), p1);

            var l2 = g.line(to, p2);

            var point = l1.intersection(l2);

            return point ? [point.round(), to] : [to];
        }
    };

    // public function
    return function (vertices, opts, linkView) {

        return joint.routers.manhattan(vertices, _.extend({}, config, opts), linkView);
    };

})();

joint.connectors.normal = function (sourcePoint, targetPoint, vertices) {

    // Construct the `d` attribute of the `<path>` element.
    var d = ['M', sourcePoint.x, sourcePoint.y];

    _.each(vertices, function (vertex) {

        d.push(vertex.x, vertex.y);
    });

    d.push(targetPoint.x, targetPoint.y);

    return d.join(' ');
};

joint.connectors.rounded = function (sourcePoint, targetPoint, vertices, opts) {

    var offset = opts.radius || 10;

    var c1, c2, d1, d2, prev, next;

    // Construct the `d` attribute of the `<path>` element.
    var d = ['M', sourcePoint.x, sourcePoint.y];

    _.each(vertices, function (vertex, index) {

        // the closest vertices
        prev = vertices[index - 1] || sourcePoint;
        next = vertices[index + 1] || targetPoint;

        // a half distance to the closest vertex
        d1 = d2 || g.point(vertex).distance(prev) / 2;
        d2 = g.point(vertex).distance(next) / 2;

        // control points
        c1 = g.point(vertex).move(prev, -Math.min(offset, d1)).round();
        c2 = g.point(vertex).move(next, -Math.min(offset, d2)).round();

        d.push(c1.x, c1.y, 'S', vertex.x, vertex.y, c2.x, c2.y, 'L');
    });

    d.push(targetPoint.x, targetPoint.y);

    return d.join(' ');
};

joint.connectors.smooth = function (sourcePoint, targetPoint, vertices) {

    var d;

    if (vertices.length) {

        d = g.bezier.curveThroughPoints([sourcePoint].concat(vertices).concat([targetPoint]));

    } else {
        // if we have no vertices use a default cubic bezier curve, cubic bezier requires
        // two control points. The two control points are both defined with X as mid way
        // between the source and target points. SourceControlPoint Y is equal to sourcePoint Y
        // and targetControlPointY being equal to targetPointY. Handle situation were
        // sourcePointX is greater or less then targetPointX.
        var controlPointX = (sourcePoint.x < targetPoint.x)
            ? targetPoint.x - ((targetPoint.x - sourcePoint.x) / 2)
            : sourcePoint.x - ((sourcePoint.x - targetPoint.x) / 2);

        d = [
            'M', sourcePoint.x, sourcePoint.y,
            'C', controlPointX, sourcePoint.y, controlPointX, targetPoint.y,
            targetPoint.x, targetPoint.y
        ];
    }

    return d.join(' ');
};
