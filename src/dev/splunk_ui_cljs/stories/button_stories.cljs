(ns splunk-ui-cljs.stories.button-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.button :refer [button]]))


(def ^:export default
  (utils/->default
   {:title     "Button"
    :component button
    :argTypes  {:label      {:control "text"}
                :appearance {:control "select"
                             :options ["default", "secondary", "primary", "destructive", "pill", "toggle", "flat"]}
                :disabled   {:control "radio"
                             :options [true false]}}}))


(defn ^:export button-variants [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [:div {:style {:display "flex" :justify-content "space-between"}}
     [button {:label "default"}]
     [button {:label      "secondary"
              :appearance "secondary"}]
     [button {:label      "primary"
              :appearance "primary"}]
     [button {:label      "destructive"
              :appearance "destructive"}]
     [button {:label      "pill"
              :appearance "pill"}]
     [button {:label      "toggle"
              :appearance "toggle"}]
     [button {:label      "flat"
              :appearance "flat"}]]]))
