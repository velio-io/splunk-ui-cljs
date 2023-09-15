(ns splunk-ui-cljs.button
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Button" :default Button]))


(def button-base
  (r/adapt-react-class Button))


(defn button
  "Use Button for clickable actions or as links to another page.
   Button automatically selects the appropriate tag.
   - `label` (optional) Applies the text that displays on the button
   - `appearance` (optional) Changes the style of the button. One of default, secondary, primary, destructive, pill, toggle, flat
   - `disabled?` (optional) Prevents user from clicking the button
   - `on-click` (optional) Callback triggered on user click
   - `to` (optional) Identifies the URL for a link. If set, Splunk UI applies an <a> tag instead of a <button> tag"
  [{:keys [label appearance disabled? on-click to]
    :or   {appearance "default"}}]
  (let [disabled? (utils/model->value disabled?)]
    [button-base
     (utils/assoc-some {:label      label
                        :appearance appearance
                        :disabled   disabled?
                        :onClick    on-click}
       :to to)]))
