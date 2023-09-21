(ns splunk-ui-cljs.stories.selection-list-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.selection-list :refer [selection-list]]))


(def ^:export default
  (utils/->default
   {:title     "Selection List"
    :component selection-list
    :argTypes  {:model         {:type        {:name "set" :required true}
                                :description "Sets the current state of a selection list. Could be an atom holding a set of selected values"
                                :control     "array"}
                :choices       {:type        {:name "vector" :required true}
                                :description "Vector of maps. Each map represents a choice.
                                              Values corresponding to id, label
                                              are extracted by the functions :id-fn, :label-fn.
                                              Could contain a :truncate property to ellipsis long string"
                                :control     "array"}
                :on-change     {:type        {:name "function" :required true}
                                :description "Called when the checkbox is clicked. Passed the new value of the checkbox"
                                :control     {:type nil}}
                :id-fn         {:type        {:name "function" :required false}
                                :description "Given an element of :choices, returns its unique identifier (aka id). Defaults to :id"
                                :control     {:type nil}}
                :label-fn      {:type        {:name "function" :required false}
                                :description "A function which can turn a choice into a displayable label. Will be called for each element in :choices.
                                              Given one argument, a choice map, it returns a string.
                                              Defaults to :label"
                                :control     {:type nil}}
                :multi-select? {:type        {:name "boolean" :required false}
                                :description "When true, can select multiple items, single item otherwise"
                                :control     "boolean"}
                :required?     {:type        {:name "boolean" :required false}
                                :description "When true, at least one item must be selected"
                                :control     "boolean"}
                :disabled?     {:type        {:name "boolean" :required false}
                                :description "If true, user interaction is disabled"
                                :control     "boolean"}
                :appearance    {:type         {:name "string" :required false}
                                :description  "Define the look and feel of the item element"
                                :control      "select"
                                :defaultValue "checkmark"
                                :options      ["checkbox", "checkmark"]}
                :width         {:type        {:name "number" :required false}
                                :description "A CSS style e.g. '250px'"
                                :control     "number"}
                :height        {:type        {:name "number" :required false}
                                :description "A CSS style e.g. '250px'"
                                :control     "number"}}}))


(defn ^:export selection-list-multiselect [args]
  (let [checkbox-model (r/atom #{3 1})
        params         (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [selection-list (merge {:model         checkbox-model
                               :on-change     #(reset! checkbox-model %)
                               :choices       [{:id 1 :label "First"}
                                               {:id 2 :label "Second"}
                                               {:id 3 :label "Third"}
                                               {:id 4 :label "Forth"}
                                               {:id 5 :label "Fifth"}
                                               {:id 6 :label "Sixth"}
                                               {:id 7 :label "Seventh"}
                                               {:id 8 :label "Eighth"}]
                               :multi-select? true
                               :width         400
                               :height        200}
                              params)]]])))


(defn ^:export selection-list-required [args]
  (let [checkbox-model (r/atom #{3 1})]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [selection-list {:model     checkbox-model
                        :on-change #(reset! checkbox-model %)
                        :choices   [{:id 1 :label "First"}
                                    {:id 2 :label "Second"}
                                    {:id 3 :label "Third"}
                                    {:id 4 :label "Forth"}
                                    {:id 5 :label "Fifth"}
                                    {:id 6 :label "Sixth"}
                                    {:id 7 :label "Seventh"}
                                    {:id 8 :label "Eighth"}]
                        :required? true}]]])))


(defn ^:export selection-list-as-checkboxes [args]
  (let [checkbox-model (r/atom #{3 1})]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [selection-list {:model      checkbox-model
                        :on-change  #(reset! checkbox-model %)
                        :choices    [{:id 1 :label "First"}
                                     {:id 2 :label "Second"}
                                     {:id 3 :label "Third"}
                                     {:id 4 :label "Forth"}
                                     {:id 5 :label "Fifth"}
                                     {:id 6 :label "Sixth"}
                                     {:id 7 :label "Seventh"}
                                     {:id 8 :label "Eighth"}]
                        :appearance "checkbox"}]]])))


(defn ^:export selection-list-disabled [args]
  (let [checkbox-model (r/atom #{3 1})]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [selection-list {:model      checkbox-model
                        :on-change  #(reset! checkbox-model %)
                        :choices    [{:id 1 :label "First"}
                                     {:id 2 :label "Second"}
                                     {:id 3 :label "Third"}
                                     {:id 4 :label "Forth"}
                                     {:id 5 :label "Fifth"}
                                     {:id 6 :label "Sixth"}
                                     {:id 7 :label "Seventh"}
                                     {:id 8 :label "Eighth"}]
                        :appearance "checkbox"
                        :disabled?  true}]]])))
