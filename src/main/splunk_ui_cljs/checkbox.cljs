(ns splunk-ui-cljs.checkbox
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Switch" :default Switch]))


(def checkbox-base
  (r/adapt-react-class Switch))


(defn checkbox [{:keys [model label on-change appearance disabled? status]
                 :or   {appearance "checkbox"}}]
  (let [selected  (utils/model->value model)
        disabled? (utils/model->value disabled?)]
    [checkbox-base {:onClick    (fn [_event _data]
                                  (when (fn? on-change)
                                    (on-change (not selected))))
                    :disabled   disabled?
                    :appearance appearance
                    :selected   (boolean selected)
                    :error      (= status :error)}
     label]))
