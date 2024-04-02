(ns splunk-ui-cljs.utils
  (:require
   ["react" :as react]
   [applied-science.js-interop :as j]
   [reagent.core :as r]
   [reagent.impl.util :as util]
   [goog.object :as go]))


(defn assoc-some
  "Associates a key k, with a value v in a map m, if and only if v is not nil."
  ([m k v]
   (if (nil? v) m (assoc m k v)))

  ([m k v & kvs]
   (reduce (fn [m [k v]] (assoc-some m k v))
           (assoc-some m k v)
           (partition 2 kvs))))


(defn atom?
  "Checks if x is an atom like object"
  [x]
  (satisfies? IDeref x))


(defn model->value
  "Takes a value or an atom
   If it's a value, returns it
   If it's a Reagent object that supports IDeref, returns the value inside it by derefing"
  [val-or-atom]
  (if (atom? val-or-atom)
    @val-or-atom
    val-or-atom))


(defn ->js-shallow
  "Convert cljs map into js but only a first level"
  [clj-data]
  (let [js-data (js-obj)]
    (doseq [[k v] clj-data
            :let [key (name k)]]
      (unchecked-set js-data key v))
    js-data))


(defn extract-props [v]
  (let [p (nth v 1 nil)]
    (if (map? p) p)))


(defn component-props
  "Copy of the reagent/component-props function with small addition.
   We wouldn't choose between cljs or js properties but will merge them together instead.
   This is required to handle additional props passed to React.cloneElement function"
  [component]
  (let [props (go/get component "props")]
    (if-some [argv (go/get props "argv")]
      ;; we have to merge props provided by React.cloneElement, otherwise they will be thrown away
      (merge (extract-props argv)
             (util/shallow-obj-to-map props))
      (util/shallow-obj-to-map props))))


(defn value->element
  "Helper function to map reagent components to react elements"
  [value]
  (if (vector? value)
    (r/as-element value)
    value))


(defn uget [value key]
  (if (map? value)
    (get value key)
    (j/get value key)))


(defn find-by
  "Finds the first item in a collection that matches a predicate"
  [key val coll]
  (reduce (fn [_ x]
            (when (= (uget x key) val)
              (reduced x)))
          nil
          coll))


(defn use-callback
  ([func]
   (react/useCallback
    (fn [& args]
      (apply func args)
      js/undefined)
    #js []))

  ([func deps]
   (react/useCallback
    (fn [& args]
      (apply func args)
      js/undefined)
    (to-array deps))))