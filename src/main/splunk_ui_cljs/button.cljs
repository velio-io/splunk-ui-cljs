(ns splunk-ui-cljs.button
  (:require
   [reagent.core :as r]
   ["@splunk/react-ui/Button" :default Button]))


(def button-base
  (r/adapt-react-class Button))


(defn button [{:keys [label appearance disabled on-click]
               :or   {appearance "default"}}]
  [button-base
   {:label      label
    :appearance appearance
    :disabled   disabled
    :onClick    on-click}])
