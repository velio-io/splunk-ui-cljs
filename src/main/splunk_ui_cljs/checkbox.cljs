(ns splunk-ui-cljs.checkbox
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Switch" :default Switch]))


(def checkbox-base
  (r/adapt-react-class Switch))


(defn checkbox
  "An input for boolean values
   - `model` (required) Sets the current state of a checkbox. Could be an atom holding a boolean value
   - `on-change` (required) Called when the checkbox is clicked. Passed the new value of the checkbox
   - `label` (optional) The label shown to the right
   - `appearance` (optional) Define the look and feel of the checkbox element. One of checkbox, toggle
   - `disabled?` (optional) If true, user interaction is disabled
   - `status` (optional) Highlight the field as having an error. Allowed value - :error"
  [{:keys [model label on-change appearance disabled? status]
    :or   {appearance "checkbox"}}]
  (let [selected  (utils/model->value model)
        disabled? (utils/model->value disabled?)
        status    (when (some? status)
                    (keyword status))]
    [checkbox-base {:onClick    (fn [_event _data]
                                  (when (fn? on-change)
                                    (on-change (not selected))))
                    :disabled   disabled?
                    :appearance appearance
                    :selected   (boolean selected)
                    :error      (= status :error)}
     label]))
