(ns splunk-ui-cljs.line
  (:require
   [cljs-styled-components.reagent :refer-macros [defstyled]]
   ["@splunk/react-ui/Divider" :default Divider]))


(defstyled line-base Divider
  {:border-color #(unchecked-get % "$color")
   :border-width #(let [size (unchecked-get % "$size")]
                    (if (number? size)
                      (str size "px")
                      size))})


(defn line
  "A non-focusable separator that can be used to provide a horizontal or vertical rule between two sections."
  [{:keys [size color style attr orientation]
    :or   {orientation "horizontal"}}]
  [line-base
   (merge attr
          {:$size       size
           :$color      color
           :style       style
           :orientation orientation})])
