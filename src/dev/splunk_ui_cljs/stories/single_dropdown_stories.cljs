(ns splunk-ui-cljs.stories.single-dropdown-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.dropdown :as dropdown]))


(def ^:export default
  (utils/->default
   {:title     "Single Dropdown"
    :component dropdown/single-dropdown
    :argTypes  {:model       {:type        {:name "text" :required true}
                              :description "Sets the current value of dropdown.
                                            Could be an atom holding a selected value (id)"
                              :control     "text"}
                :choices     {:type        {:name "vector" :required true}
                              :description "Vector of maps. Each map represents a choice.
                                            Values corresponding to id, label are extracted by the functions :id-fn, :label-fn.
                                            Could contain a :truncate property to ellipsis long string"
                              :control     "array"}
                :label-fn    {:type        {:name "function" :required false}
                              :description "A function which can turn a choice into a displayable label. Will be called for each element in :choices.
                                            Given one argument, a choice map, it returns a string
                                            Defaults to :label"
                              :control     {:type nil}}
                :id-fn       {:type        {:name "function" :required false}
                              :description "Given an element of :choices, returns its unique identifier (aka id). Defaults to :id"
                              :control     {:type nil}}
                :on-change   {:type        {:name "function" :required true}
                              :description "This function is called whenever the selection changes.
                                            Called with one argument, the selected id. See :model"
                              :control     {:type nil}}
                :filter-box? {:type        {:name "boolean" :required false}
                              :description "Determines whether to show the filter box.
                                            When true, the children are automatically filtered based on the label."
                              :control     "boolean"}
                :disabled?   {:type        {:name "boolean" :required false}
                              :description "If true, user interaction is disabled"
                              :control     "boolean"}
                :placeholder {:type        {:name "text" :required false}
                              :description "The gray text shown when the dropdown is empty"
                              :control     "text"}
                :status      {:type        {:name "string" :required false}
                              :description "Highlight the field as having an error"
                              :control     "select"
                              :options     ["error"]}}}))


(defn ^:export single-dropdown-basic [args]
  (let [params (-> args utils/->params)
        model  (r/atom nil)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [dropdown/single-dropdown (merge {:on-change #(reset! model %)
                                        :model     model
                                        :choices   [{:id "first" :label "First"}
                                                    {:id "second" :label "Second"}
                                                    {:id "third" :label "Third"}]}
                                       params)]])))
