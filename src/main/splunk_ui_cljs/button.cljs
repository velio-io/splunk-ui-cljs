(ns splunk-ui-cljs.button
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Button" :default Button]))


(def button-base
  (r/adapt-react-class Button))


(defn button [{:keys [label appearance disabled? on-click to]
               :or   {appearance "default"}}]
  [button-base
   (utils/assoc-some {:label      label
                      :appearance appearance
                      :disabled   disabled?
                      :onClick    on-click}
     :to to)])
