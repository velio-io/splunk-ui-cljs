(ns splunk-ui-cljs.stories.line-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.line :refer [line]]))


(def ^:export default
  (utils/->default
   {:title     "Line"
    :component line
    :argTypes  {:color       {:type        {:name "string" :required false}
                              :description "A CSS color"
                              :control     "color"}
                :size        {:type        {:name "number" :required false}
                              :description "The with of a line"
                              :control     "number"}
                :orientation {:type         {:name "string" :required false}
                              :description  "Sets the orientation of this line"
                              :defaultValue "horizontal"
                              :control      "select"
                              :options      ["horizontal" "vertical"]}}}))


(defn ^:export line-vertical [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div {:style {:display         "flex"
                     :justify-content "space-around"
                     :width           200}}
       [:p "Lorem"]
       [line (merge params {:orientation "vertical"})]
       [:p "Ipsum"]]])))


(defn ^:export line-horizontal [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [:p
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.
      In dictum metus enim, ac ullamcorper ante condimentum at."]
       [line (merge params {:orientation "horizontal"})]
       [:p
        "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.
      Duis laoreet sit amet mauris eget ullamcorper."]]])))

