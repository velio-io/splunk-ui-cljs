(ns splunk-ui-cljs.stories.flow-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.flow :refer [flow]]))


(def ^:export default
  (utils/->default
   {:title     "Flow"
    :component flow
    :argTypes  {:model {:type        {:name "streams-map" :required true}
                        :description "Map of streams definitions to render on canvas.
                                      Can be nil, map or atom. If atom, the component will react to changes in the atom."
                        :control     {:type nil}}}}))


(def streams
  (r/atom
   {:foo  {:actions {:action      :sdo
                     :description {:message "Forward events to children"}
                     :children    [{:action      :increment
                                    :name        "Increment"
                                    :description {:message "Increment the :metric field"}
                                    :children    nil}]}}
    :bar  {:actions {:action      :sdo
                     :description {:message "Forward events to children"}
                     :children    [{:action      :decrement
                                    :name        "Decrement"
                                    :description {:message "Decrement the :metric field"}
                                    :children    nil}
                                   {:action      :increment
                                    :name        "Increment"
                                    :description {:message "Increment the :metric field"}
                                    :children    nil}]}}}))


(defn ^:export flow-basic [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [flow {:model nil}]]))


(defn ^:export flow-initial-nodes [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [flow {:model @streams}]]))


(defn ^:export flow-state-updates [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [flow {:model streams}]]))