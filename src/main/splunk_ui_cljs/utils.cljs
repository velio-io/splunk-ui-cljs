(ns splunk-ui-cljs.utils)


(defn assoc-some
  "Associates a key k, with a value v in a map m, if and only if v is not nil."
  ([m k v]
   (if (nil? v) m (assoc m k v)))

  ([m k v & kvs]
   (reduce (fn [m [k v]] (assoc-some m k v))
           (assoc-some m k v)
           (partition 2 kvs))))


(defn model->value
  "Takes a value or an atom
   If it's a value, returns it
   If it's a Reagent object that supports IDeref, returns the value inside it by derefing"
  [val-or-atom]
  (if (satisfies? IDeref val-or-atom)
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
