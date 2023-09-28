(ns splunk-ui-cljs.scroll
  (:require
   [reagent.core :as reagent]
   [goog.object :as go]
   [cljs-styled-components.reagent :refer-macros [defstyled]]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Scroll" :default Scroll]))


(defn pixel-or-string [prop]
  (let [prop-name (name prop)]
    #(let [val (go/get % prop-name)]
       (if (number? val)
         (str val "px")
         val))))


(defstyled scroller-base Scroll
  {:width      (pixel-or-string :$width)
   :height     (pixel-or-string :$height)
   :min-width  (pixel-or-string :$min-width)
   :min-height (pixel-or-string :$min-height)
   :max-width  (pixel-or-string :$max-width)
   :max-height (pixel-or-string :$max-height)})


(defn scroller
  "A container that handles scrolling through JavaScript.
  This can be useful for stopping scroll propagation and invoking scroll events externally.
   - `tag` (optional) Type of the HTML element to use for component. Defaults to `div`
   - `width` (optional) Standard CSS width setting for this component. Could be number or string
   - `height` (optional) Standard CSS height setting for this component. Could be number or string
   - `min-width` (optional) Standard CSS min-width setting for this component. Could be number or string
   - `min-height` (optional) Standard CSS min-height setting for this component. Could be number or string
   - `max-width` (optional) Standard CSS max-width setting for this component. Could be number or string
   - `max-height` (optional) Standard CSS max-height setting for this component. Could be number or string"
  [{:keys [tag width height min-width min-height max-width max-height]}]
  (let [component (reagent/current-component)]
    [scroller-base
     (utils/assoc-some {}
       :tagName tag
       :$width width
       :$height height
       :$min-width min-width
       :$min-height min-height
       :$max-width max-width
       :$max-height max-height)
     (reagent/children component)]))
