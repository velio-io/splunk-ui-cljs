(ns splunk-ui-cljs.selection-list
  (:require
   [reagent.core :as r]
   [goog.object :as go]
   [cljs-styled-components.reagent :refer-macros [defstyled]]
   ["@splunk/react-ui/Menu" :default Menu :refer [Item]]
   [splunk-ui-cljs.utils :as utils]))


(defstyled menu-base Menu
  {:height #(let [height (go/get % "$height")]
              (if (number? height)
                (str height "px")
                height))
   :width  #(let [width (go/get % "$width")]
              (if (number? width)
                (str width "px")
                width))})


(defn selection-list
  "Allows the user to select items from a list (single or multi).
   - `model` (required) Sets the current state of a selection list. Could be an atom holding a set of selected values
   - `choices` (required) Vector of maps. Each map represents a choice. Values corresponding to id, label are extracted by the functions :id-fn, :label-fn. Could contain a :truncate property to ellipsis long string
   - `on-change` (required) Called when the checkbox is clicked. Passed the new value of the checkbox
   - `id-fn` (optional) Given an element of :choices, returns its unique identifier (aka id). Defaults to :id
   - `label-fn` (optional) A function which can turn a choice into a displayable label. Will be called for each element in :choices. Given one argument, a choice map, it returns a string. Defaults to :label
   - `multi-select?` (optional) When true, can select multiple items, single item otherwise
   - `required?` (optional) When true, at least one item must be selected
   - `disabled?` (optional) If true, user interaction is disabled
   - `appearance` (optional) Define the look and feel of the item element
   - `width` (optional) A CSS style e.g. '250px'
   - `height` (optional) A CSS style e.g. '250px'"
  [{:keys [model choices on-change multi-select? required? appearance width height id-fn label-fn disabled?]
    :or   {appearance "checkmark"
           required?  false
           disabled?  false
           id-fn      :id
           label-fn   :label}}]
  (let [all-choices      (utils/model->value choices)
        selected-choices (utils/model->value model)
        selected         (if multi-select?
                           selected-choices
                           (-> selected-choices first vector set))
        change-selection (fn [choice-id]
                           (let [selected?        (contains? selected choice-id)
                                 single-selected? (= (count selected) 1)]
                             (if multi-select?
                               (when-not (and selected? required? single-selected?)
                                 (if selected?
                                   (on-change (disj selected choice-id))
                                   (on-change (conj selected choice-id))))
                               (when-not (and selected? required?)
                                 (if selected?
                                   (on-change (disj selected choice-id))
                                   (on-change #{choice-id}))))))
        options          (map (fn [{:keys [truncate] :as choice}]
                                (let [id        (id-fn choice)
                                      label     (label-fn choice)
                                      selected? (contains? selected id)]
                                  ^{:key id}
                                  [:> Item (utils/assoc-some
                                             {:selectable           true
                                              :selected             selected?
                                              :selectableAppearance appearance
                                              :onClick              #(change-selection id)
                                              :disabled             disabled?}
                                             :truncate truncate)
                                   label]))
                              all-choices)]
    (into
     [menu-base
      (utils/assoc-some {}
        :$height height
        :$width width)]
     options)))
