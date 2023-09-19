(ns splunk-ui-cljs.dropdown
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   [goog.object :as go]
   ["@splunk/react-ui/Multiselect" :default Multiselect :as MS]
   ["@splunk/react-ui/Select" :default Select :as S]))


(def select-base
  (r/adapt-react-class Select))


(def select-option-base
  (r/adapt-react-class S/Option))


(def multiselect-base
  (r/adapt-react-class Multiselect))


(def multiselect-option-base
  (r/adapt-react-class MS/Option))


(defn tag-dropdown
  "An input for selecting one or more options from a set.
   - `model` (required) Sets the current value of input. Could be an atom holding a set of selected values (id's)
   - `choices` (required) Vector of maps. Each map represents a choice. Values corresponding to id, label, tag foreground color and tag background color are extracted by the functions :id, :label-fn, :foreground-color & :background-color
   - `on-change` (required) This function is called whenever the selection changes. Called with one argument, the set of selected ids. See :model
   - `label-fn` (optional) A function which can turn a choice into a displayable label. Will be called for each element in :choices. Given one argument, a choice map, it returns a string
   - `compact` (optional) When compact, options are shown as checkboxes and the input is a single line. This is useful when placing the Multiselect in a horizontal bar, such as a filter
   - `filter-box?` (optional) Determines whether to show the filter box. When true, the children are automatically filtered based on the label. Only supported when compact=true.
   - `disabled?` (optional) If true, user interaction is disabled
   - `placeholder` (optional) The gray text shown when the dropdown is empty
   - `status` (optional) Highlight the field as having an error
   - `inline` (optional) When true, display as inline"
  [{:keys [model choices label-fn on-change placeholder status compact filter-box? disabled? inline]
    :or   {disabled?   false
           filter-box? false
           label-fn    :label}}]
  (let [all-choices      (utils/model->value choices)
        selected-choices (utils/model->value model)
        disabled?        (utils/model->value disabled?)
        status           (when (some? status)
                           (keyword status))
        options          (map (fn [{:keys [id background-color foreground-color] :as choice}]
                                ^{:key id}
                                [multiselect-option-base
                                 (utils/assoc-some
                                   {:value id
                                    :label (label-fn choice)}
                                   :selectedBackgroundColor background-color
                                   :selectedForegroundColor foreground-color)])
                              all-choices)]
    (into
     [multiselect-base
      (utils/assoc-some
        {:values   selected-choices
         :onChange (fn [_event params]
                     (on-change (set (go/get params "values"))))
         :error    (= status :error)}
        :placeholder placeholder
        :disabled disabled?
        :compact compact
        :inline inline
        :filter (when compact filter-box?))]
     options)))


(defn single-dropdown
  "An input for selecting an option from a list.
   - `model` (required) Sets the current value of dropdown. Could be an atom holding a selected value (id)
   - `choices` (required) Vector of maps. Each map represents a choice. Values corresponding to id, label are extracted by the functions :id-fn, :label-fn. Could contain a :truncate property to ellipsis long string
   - `label-fn` (optional) A function which can turn a choice into a displayable label. Will be called for each element in :choices. Given one argument, a choice map, it returns a string Defaults to :label
   - `id-fn` (optional) Given an element of :choices, returns its unique identifier (aka id). Defaults to :id
   - `on-change` (required) This function is called whenever the selection changes. Called with one argument, the selected id. See :model
   - `filter-box?` (optional) Determines whether to show the filter box. When true, the children are automatically filtered based on the label.
   - `disabled?` (optional) If true, user interaction is disabled
   - `placeholder` (optional) The gray text shown when the dropdown is empty
   - `status` (optional) Highlight the field as having an error"
  [{:keys [model choices on-change status disabled? placeholder filter-box? id-fn label-fn]
    :or   {disabled?   false
           filter-box? false
           label-fn    :label
           id-fn       :id}}]
  (let [all-choices     (utils/model->value choices)
        selected-choice (utils/model->value model)
        disabled?       (utils/model->value disabled?)
        status          (when (some? status)
                          (keyword status))
        options         (map (fn [{:keys [truncate] :as choice}]
                               (let [id (id-fn choice)]
                                 ^{:key id}
                                 [select-option-base
                                  (utils/assoc-some
                                    {:value id
                                     :label (label-fn choice)}
                                    :truncate truncate)]))
                             all-choices)]
    (into
     [select-base
      (utils/assoc-some
        {:value    selected-choice
         :onChange (fn [_event params]
                     (on-change (go/get params "value")))
         :error    (= status :error)}
        :placeholder placeholder
        :disabled disabled?
        :filter filter-box?)]
     options)))
