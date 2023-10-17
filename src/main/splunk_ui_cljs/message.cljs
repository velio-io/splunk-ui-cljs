(ns splunk-ui-cljs.message
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   [goog.object :as go]
   ["@splunk/react-ui/Message" :default Message :refer [Link]]))


(def message-link
  (r/adapt-react-class Link))


(def message-title
  (r/adapt-react-class (go/get Message "Title")))


(defn message
  "Message renders an alert icon with text.
   - `type` (optional) Sets the severity or type of this Message.
   - `appearance` (optional) Changes the style of the Message.
   - `on-remove` (optional) Includes a remove button if set. Always set this prop when using the banner appearance, never set it when using the default appearance."
  [{:keys [type appearance on-remove]}]
  (let [component (r/current-component)
        children  (r/children component)]
    (into
     [:> Message (utils/assoc-some {}
                   :type type
                   :appearance appearance
                   :onRequestRemove on-remove)]
     children)))
