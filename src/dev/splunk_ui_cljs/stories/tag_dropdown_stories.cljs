(ns splunk-ui-cljs.stories.tag-dropdown-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.dropdown :as dropdown]))


(def ^:export default
  (utils/->default
   {:title     "Tag Dropdown"
    :component dropdown/tag-dropdown
    :argTypes  {:model                 {:type        {:name "set" :required true}
                                        :description "Sets the current value of dropdown.
                                                      Could be an atom holding a set of selected values (id's)"
                                        :control     "array"}
                :choices               {:type        {:name "vector" :required true}
                                        :description "Vector of maps. Each map represents a choice.
                                                      Values corresponding to id, label, tag foreground color and tag background color
                                                      are extracted by the functions :id, :label-fn, :foreground-color & :background-color"
                                        :control     "array"}
                :label-fn              {:type        {:name "function" :required false}
                                        :description "A function which can turn a choice into a displayable label. Will be called for each element in :choices.
                                                      Given one argument, a choice map, it returns a string.
                                                      Defaults to :label"
                                        :control     {:type nil}}
                :on-change             {:type        {:name "function" :required true}
                                        :description "This function is called whenever the selection changes.
                                                      Called with one argument, the set of selected ids. See :model"
                                        :control     {:type nil}}
                :compact               {:type        {:name "boolean" :required false}
                                        :description "When compact, options are shown as checkboxes and the input is a single line.
                                                      This is useful when placing the Multiselect in a horizontal bar, such as a filter"
                                        :control     "boolean"}
                :filter-box?           {:type        {:name "boolean" :required false}
                                        :description "Determines whether to show the filter box.
                                                      When true, the children are automatically filtered based on the label.
                                                      Only supported when compact=true."
                                        :control     "boolean"}
                :disabled?             {:type        {:name "boolean" :required false}
                                        :description "If true, user interaction is disabled"
                                        :control     "boolean"}
                :placeholder           {:type        {:name "text" :required false}
                                        :description "The gray text shown when the dropdown is empty"
                                        :control     "text"}
                :status                {:type        {:name "string" :required false}
                                        :description "Highlight the field as having an error"
                                        :control     "select"
                                        :options     ["error"]}
                :inline                {:type        {:name "boolean" :required false}
                                        :description "When true, display as inline"
                                        :control     "boolean"}}}))


(defn ^:export tag-dropdown-basic [args]
  (let [params (-> args utils/->params)
        model  (r/atom #{})]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [dropdown/tag-dropdown (merge {:on-change #(reset! model %)
                                     :model     model
                                     :choices   [{:id "first" :label "First"}
                                                 {:id "second" :label "Second" :background-color "blue" :foreground-color "white"}
                                                 {:id "third" :label "Third" :background-color "green"}]}
                                    params)]])))
