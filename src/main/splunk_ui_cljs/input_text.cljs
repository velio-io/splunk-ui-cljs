(ns splunk-ui-cljs.input-text
  (:require
   ["react" :as react]
   [reagent.core :as r]
   [goog.object :as go]
   [cljs-styled-components.reagent :refer-macros [defstyled]]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Text" :default Text]
   ["@splunk/react-ui/TextArea" :default TextArea]))


(defstyled text-base Text
  {:width #(let [width (go/get % "$width")]
             (if (number? width)
               (str width "px")
               width))})


(defstyled textarea-base TextArea
  {:width #(let [width (go/get % "$width")]
             (if (number? width)
               (str width "px")
               width))})


(defn make-on-change-handler
  [{:keys [external-state local-state on-change]}]
  (fn []
    (when (fn? on-change)
      (let [has-done-fn? (= 2 (go/get on-change "length"))
            reset-fn     #(reset! external-state @local-state)]
        (if has-done-fn?
          (on-change @local-state reset-fn)
          (do (on-change @local-state)
              (reset-fn)))))))


(defn- input-text-base
  "Base input component implementation
   - `model` (required) Sets the current value of input. Could be an atom holding a string value
   - `on-change` (required) This is equivalent to onInput which is called on keydown, paste, and so on.
      If value is set, this callback is required.
      This must set the value prop to retain the change
   - `change-on-blur?` (optional) Default is true. when true. Invoke :on-change function on blur, otherwise on every change (character by character)
   - `disabled?` (optional) If true, user interaction is disabled
   - `placeholder` (optional) The gray text shown when the input is empty
   - `status` (optional) Highlight the field as having an error. Allowed value - :error
   - `validation-regex` (optional) User input is only accepted if it would result in a string that matches this regular expression
   - `rows` (optional) ONLY applies to 'input-textarea': the number of rows of text to show
   - `width` (optional) Standard CSS width setting for this input
   - `inline` (optional) When true, display as inline-flex with the default width (230px)"
  [{:keys [model]}]
  (let [initial-value   (utils/model->value model)
        external-state  (r/atom initial-value)
        local-state     (r/atom (if (nil? initial-value) "" initial-value))
        input-ref       (react/createRef)
        set-input-value (fn [value]
                          ;; as we're updating atoms there will be some re-rendering cycles
                          ;; value should be updated after all of them
                          ;; 20ms should be enough to wait to skip the current frame (60fps == 16ms)
                          (js/setTimeout
                           #(when-let [input (go/get input-ref "current")]
                              (go/set input "value" value))
                           20))]
    (fn [{:keys [model on-change change-on-blur? on-key-down input-type disabled? placeholder status width
                 validation-regex rows inline append prepend labelledBy labelText id data-attr]
          :or   {disabled? false input-type "text" change-on-blur? true}}]
      (let [disabled?         (utils/model->value disabled?)
            on-change-handler (make-on-change-handler
                               {:external-state external-state
                                :local-state    local-state
                                :on-change      on-change})
            latest-ext-value  (utils/model->value model)
            status            (when (some? status)
                                (keyword status))
            textarea?         (= input-type "textarea")
            base-component    (if textarea? textarea-base text-base)
            data              (when (some? data-attr)
                                (into {} (map (fn [[attr-name attr-val]]
                                                [(str "data-" (name attr-name)) attr-val])
                                              data-attr)))
            base-props        (merge data
                                     {:onChange     (fn [event]
                                                      (let [new-val (go/getValueByKeys event "target" "value")]
                                                        (if (or (not validation-regex) ;; no validation
                                                                (= new-val "") ;; allow to clear the input value
                                                                (and validation-regex ;; has validation and string matches regex
                                                                     (re-find validation-regex new-val)))
                                                          (do (reset! local-state new-val)
                                                              (when-not change-on-blur?
                                                                (on-change-handler)))
                                                          ;; if input didn't pass validation reset value to previous
                                                          (set-input-value @local-state))))
                                      :onBlur       (fn [_event]
                                                      (when (and change-on-blur?
                                                                 (not= @local-state @external-state))
                                                        (on-change-handler)))
                                      :onKeyDown    (fn [event]
                                                      (when (fn? on-key-down)
                                                        (on-key-down event @local-state)))
                                      :defaultValue (str @local-state)
                                      :inputRef     input-ref
                                      :disabled     disabled?
                                      :error        (= status :error)
                                      :$width       width})]
        ;; Has model changed externally?
        (when (and (some? latest-ext-value)
                   (not= @external-state latest-ext-value))
          (reset! external-state latest-ext-value)
          (reset! local-state latest-ext-value)
          (set-input-value latest-ext-value))

        [base-component (utils/assoc-some base-props
                          :inline inline
                          :rowsMin (when textarea? rows)
                          :placeholder (when-not textarea? placeholder)
                          :type (when-not textarea? input-type)
                          :append append
                          :prepend prepend
                          :labelledBy labelledBy
                          :labelText labelText
                          :id id)]))))


(defn input-text
  "An input for text"
  [props]
  [input-text-base
   (assoc props :input-type "text")])


(defn input-number
  "An input for numbers"
  [props]
  [input-text-base
   (assoc props :input-type "number")])


(defn input-password
  "An input for secret values such as passwords"
  [props]
  [input-text-base
   (assoc props :input-type "password")])


(defn input-textarea
  "A multi-line input for text"
  [props]
  [input-text-base
   (-> props
       (assoc :input-type "textarea")
       ;; this prop doesn't make sense for textarea
       (dissoc :validation-regex))])
