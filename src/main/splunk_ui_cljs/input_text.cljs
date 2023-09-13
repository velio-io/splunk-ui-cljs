(ns splunk-ui-cljs.input-text
  (:require
   ["react" :as react]
   [reagent.core :as r]
   [cljs-styled-components.reagent :refer-macros [defstyled]]
   ["@splunk/react-ui/Text" :default Text]
   ["@splunk/react-ui/TextArea" :default TextArea]))


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


(defstyled text-base Text
  {:width #(let [width (unchecked-get % "$width")]
             (if (number? width)
               (str width "px")
               width))})


(defstyled textarea-base TextArea
  {:width #(let [width (unchecked-get % "$width")]
             (if (number? width)
               (str width "px")
               width))})


(defn- input-text-base
  "create input text component
   model = r/atom
   on-change = function to deal with state changes
   input-type = type of input (html5) e.g. [text, password]"
  [{:keys [model]}]
  (let [initial-value  (model->value model)
        external-state (r/atom initial-value)
        local-state    (r/atom (if (nil? initial-value) "" initial-value))
        input-ref      (react/createRef)
        set-caret      (fn [start end]
                         (when-let [input-element (.-current input-ref)]
                           ;; dangerous hack to prevent jumping caret
                           (js/setTimeout #(.setSelectionRange input-element start end))))]
    (fn [{:keys [model on-change input-type disabled? placeholder status width validation-regex rows]
          :or   {disabled? false input-type "text"}}]
      (let [on-change-handler (fn [new-val]
                                (reset! local-state new-val)

                                (when (fn? on-change)
                                  (let [has-done-fn? (= 2 (.-length ^js/Function on-change))
                                        reset-fn     #(reset! external-state new-val)]
                                    (if has-done-fn?
                                      (on-change new-val reset-fn)
                                      (do (on-change new-val)
                                          (reset-fn))))))
            latest-ext-value  (model->value model)
            textarea?         (= input-type "textarea")
            base-component    (if textarea? textarea-base text-base)
            base-props        {:onChange (fn [e]
                                           (let [new-val (.. e -target -value)]
                                             (when (or
                                                    ;; no validation
                                                    (not validation-regex)
                                                    ;; allow to clear the input value
                                                    (= new-val "")
                                                    ;; has validation and string matches regex
                                                    (and validation-regex (re-find validation-regex new-val)))
                                               (let [caret-start (.. e -target -selectionStart)
                                                     caret-end   (.. e -target -selectionEnd)]
                                                 (on-change-handler new-val)
                                                 (set-caret caret-start caret-end)))))
                               :value    @local-state
                               :inputRef input-ref
                               :disabled disabled?
                               :error    (= status :error)
                               :$width   width}]

        (when (and (some? latest-ext-value)
                   (not= @external-state latest-ext-value)) ;; Has model changed externally?
          (reset! external-state latest-ext-value)
          (reset! local-state latest-ext-value))

        [base-component (assoc-some base-props
                          :rowsMin (when textarea? rows)
                          :placeholder (when-not textarea? placeholder)
                          :type (when-not textarea? input-type))]))))


(defn input-text
  [props]
  [input-text-base
   (assoc props :input-type "text")])


(defn input-password
  [props]
  [input-text-base
   (assoc props :input-type "password")])


(defn input-textarea
  [props]
  [input-text-base
   (-> props
       (assoc :input-type "textarea")
       ;; this prop doesn't make sense for textarea
       (dissoc :validation-regex))])
