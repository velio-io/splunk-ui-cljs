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
    :argTypes  {:label      {:type        {:name "string" :required false}
                             :description "Applies the text that displays on the button."
                             :control     "text"}
                :appearance {:type         {:name "string" :required false}
                             :description  "Changes the style of the button."
                             :defaultValue "default"
                             :control      "select"
                             :options      ["default", "secondary", "primary", "destructive", "pill", "toggle", "flat"]}
                :disabled?  {:type         {:name "boolean" :required false}
                             :description  "Prevents user from clicking the button."
                             :defaultValue false
                             :control      "boolean"}
                :on-click   {:type        {:name "function" :required false}
                             :description "Prevents callback when the button is disabled."
                             :control     {:type nil}}
                :to         {:type        {:name "string" :required false}
                             :description "Prevents callback when the button is disabled."
                             :control     "text"}}}))


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