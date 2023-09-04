(ns splunk-ui-cljs.input-text
  (:require [reagent.core :as r]
            ["@splunk/react-ui/Text$default" :as Text]))

(def text (r/adapt-react-class Text))

(defn- input-text-base
  "create input text component
   model = r/atom
   on-change = function to deal with state changes
   input-type = type of input (html5) e.g. [text, password]"
  [{:keys [model on-change input-type disabled?]
     :or {disabled? false input-type "text"}}]
   [text  {:value (when model @model)
           :onChange (fn [e]
                       (let [new-val (.. e -target -value)]
                         (on-change new-val)))
           :type input-type
           :disabled disabled?}])

(defn input-text
  [& args]
  (apply input-text-base :input-type "text" args))

(defn input-password
  [& args]
  (apply input-text-base :input-type "password" args))