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
    :argTypes  {:model {}}}))


(def streams
  (r/atom
   {:foo {:actions {:action      :sdo
                    :description {:message "Forward events to children"}
                    :children    [{:action      :increment
                                   :name        "bar"
                                   :description {:message "Increment the :metric field"}
                                   :children    nil}]}}}))


(defn ^:export flow-basic [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [flow {:model streams}]]))