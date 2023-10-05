(ns splunk-ui-cljs.stories.input-text-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.input-text :as inputs]
   [splunk-ui-cljs.button :refer [button]]))


(def ^:export default
  (utils/->default
   {:title     "Input text"
    :component inputs/input-text
    :argTypes  {:model            {:type        {:name "text" :required true}
                                   :description "Sets the current value of input. Could be an atom holding a string value"
                                   :control     "text"}
                :on-change        {:type        {:name "function" :required true}
                                   :description "This is equivalent to onInput which is called on keydown, paste, and so on.
                                                 If value is set, this callback is required.
                                                 This must set the value prop to retain the change"
                                   :control     {:type nil}}
                :change-on-blur?  {:type        {:name "boolean" :required false}
                                   :description "Default is true. when true. Invoke :on-change function on blur, otherwise on every change (character by character)"
                                   :control     "boolean"}
                :disabled?        {:type        {:name "boolean" :required false}
                                   :description "If true, user interaction is disabled"
                                   :control     "boolean"}
                :placeholder      {:type        {:name "text" :required false}
                                   :description "The gray text shown when the input is empty"
                                   :control     "text"}
                :status           {:type        {:name "string" :required false}
                                   :description "Highlight the field as having an error"
                                   :control     "select"
                                   :options     ["error"]}
                :validation-regex {:type        {:name "regex" :required false}
                                   :description "User input is only accepted if it would result in a string that matches this regular expression"
                                   :control     "text"}
                :rows             {:type        {:name "number" :required false}
                                   :description "ONLY applies to 'input-textarea': the number of rows of text to show"
                                   :control     "number"}
                :width            {:type        {:name "number" :required false}
                                   :description "Standard CSS width setting for this input"
                                   :control     "number"}
                :inline           {:type        {:name "boolean" :required false}
                                   :description "When true, display as inline-flex with the default width (230px)"
                                   :control     "boolean"}}}))


(defn ^:export input-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-text (merge {:on-change   println
                                 :placeholder "input"}
                                params)]])))


(defn ^:export input-text-with-model [args]
  (let [model (r/atom "initial value")]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-text {:model       model
                          :on-change   (fn [value]
                                         (println "event value" value)
                                         (reset! model value))
                          :placeholder "input with model"
                          :inline      true}]
      [button {:label      "reset input"
               :appearance "toggle"
               :on-click   #(reset! model "initial value")}]])))


(defn ^:export input-text-regex-validation [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [inputs/input-text {:on-change        println
                        :placeholder      "only 1 is allowed"
                        :validation-regex #"^1+$"}]]))


(defn ^:export input-password [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-password (merge {:model       "super password"
                                     :on-change   println
                                     :placeholder "type password"}
                                    params)]])))


(defn ^:export input-textarea [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-textarea (merge {:on-change println
                                     :rows      4}
                                    params)]])))
