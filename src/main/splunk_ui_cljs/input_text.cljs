(ns splunk-ui-cljs.input-text
  (:require
   [cljs-styled-components.reagent :refer-macros [defstyled]]
   ["@splunk/react-ui/Text" :default Text]))


(defstyled text-base Text
  {:width #(let [width (unchecked-get % "$width")]
             (if (number? width)
               (str width "px")
               width))})


(defn- input-text-base
  "create input text component
   model = r/atom
   on-change = function to deal with state changes
   input-type = type of input (html5) e.g. [text, password]"
  [{:keys [model on-change input-type disabled?
           placeholder status width validation-regex]
    :or   {disabled? false input-type "text"}}]
  (let [controlled? (some? model)]
    [text-base (cond-> {:onChange    (fn [e]
                                       (let [new-val (.. e -target -value)]
                                         (on-change new-val)))
                        :placeholder placeholder
                        :type        input-type
                        :disabled    disabled?
                        :error       (= status :error)
                        :$width      width}
                       controlled? (assoc :value @model))]))


(defn input-text
  [props]
  [input-text-base (assoc props :input-type "text")])


(defn input-password
  [props]
  [input-text-base (assoc props :input-type "password")])
