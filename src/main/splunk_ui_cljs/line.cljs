(ns splunk-ui-cljs.line
  (:require
   [goog.object :as go]
   [cljs-styled-components.reagent :refer-macros [defstyled]]
   ["@splunk/react-ui/Divider" :default Divider]))


(defstyled line-base Divider
  {:border-color #(go/get % "$color")
   :border-width #(let [size (go/get % "$size")]
                    (if (number? size)
                      (str size "px")
                      size))})


(defn line
  "A non-focusable separator that can be used to provide a horizontal or vertical rule between two sections.
  - `size` (optional) A CSS color
  - `color` (optional) The with of a line
  - `orientation` (optional) Sets the orientation of this line. One of horizontal, vertical"
  [{:keys [size color orientation style attr]
    :or   {orientation "horizontal"}}]
  [line-base
   (merge attr
          {:$size       size
           :$color      color
           :style       style
           :orientation orientation})])
