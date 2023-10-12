(ns splunk-ui-cljs.tab-layout
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   [goog.object :as go]
   ["@splunk/react-ui/TabLayout" :default TabLayout :refer [Panel]]))


(defn tab-layout
  "A variety of horizontal and vertical tab selection components
   - `model` (optional) The unique identifier of the currently selected tab. Can be a simple string or atom.
   - `on-change` (optional) A callback that receives the event and data (selectedPanelId). If model is set, this callback is required.
   - `tabs` (required) One element in the vector for each tab. Vector of maps or atom
      Typically, each element is a map with
      - :id (required) string
      - :label (required) string
      - :content (required) reagent element
      - :style (optional) styles map
      - :disabled (optional) boolean
      - :icon (optional) reagent element
      - :tooltip (optional) reagent element
   - `default-tab` (optional) Sets the active panel on the initial render. It must match the :id of one of the child (tabs). Only use :default-tab when using TabLayout as an uncontrolled component.
   - `tab-width` (optional) Width of each tab in pixels. Must be greater than 50 pixels. Leave blank for auto width.
   - `layout` (optional) Setting this prop to 'context' creates an appearance without an underline.
   - `appearance` (optional) The unique identifier of the currently selected tab. Can be a simple string or atom.
   - `icon-size` (optional) Size of icon in tab if it has an icon.
   - `auto-activate` (optional) If true, tabs will trigger the on-change callback when they receive focus."
  [{:keys [model on-change tabs default-tab tab-width layout appearance icon-size auto-activate]}]
  (let [selected-tab (utils/model->value model)
        tabs         (utils/model->value tabs)]
    (into
     [:> TabLayout (utils/assoc-some {}
                     :layout layout
                     :appearance appearance
                     :iconSize icon-size
                     :autoActivate auto-activate
                     :defaultActivePanelId default-tab
                     :activePanelId selected-tab
                     :tabWidth tab-width
                     :onChange (when (fn? on-change)
                                 (fn [_event params]
                                   (on-change (go/get params "activePanelId")))))]
     (map (fn [{:keys [id label icon content style disabled tooltip]}]
            [:> Panel (utils/assoc-some
                        {:label   label
                         :panelId id}
                        :style style
                        :disabled disabled
                        :tooltip (if (vector? tooltip)
                                   (r/as-element tooltip)
                                   tooltip)
                        :icon (if (vector? icon)
                                (r/as-element icon)
                                icon))
             (if (fn? content)
               (content)
               content)]))
     tabs)))
