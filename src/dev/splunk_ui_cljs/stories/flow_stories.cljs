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


(defn ^:export flow-basic [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [flow {:nodes []
                   ;;{:id       "input-node"
                   ;; :type     "stream"
                   ;; :data     {:stream-name "Marketing events"
                   ;;            :status      "new"}
                   ;; :position {:x 0 :y 0}}]
                   ;;{:id       "output-node"
                   ;; :type     "action"
                   ;; :data     {:action-name "Filter"
                   ;;            :status      "new"}
                   ;; :position {:x 180 :y 150}}]
           :edges [{:id       "1-2"
                    :source   "input-node"
                    :target   "output-node"
                    :animated true}]}]]))