(ns splunk-ui-cljs.stories.layout-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.layout :refer [layout h-box v-box column]]))


(def ^:export default
  (utils/->default
   {:title     "Layout"
    :component layout
    :argTypes  {:divider     {:type         {:name "string" :required false}
                              :description  "Supported in layout component. Show dividers between columns."
                              :defaultValue "none"
                              :control      "select"
                              :options      ["none", "vertical", "horizontal"]}
                :gap         {:type        {:name "number" :required false}
                              :description "Supported in layout component. Set gutter width in pixels. This is propagated down to its children."
                              :control     "number"}
                :align-items {:type         {:name "string" :required false}
                              :description  "Supported in h-box component. Set vertical alignment of columns in a row"
                              :defaultValue "stretch"
                              :control      "select"
                              :options      ["start", "end", "center", "stretch"]}
                :span        {:type         {:name "number" :required false}
                              :description  "Supported in column component. The number of columns the element spans."
                              :defaultValue 1
                              :control      "number"}}}))


(def col-style
  {:border     "1px solid #2662fc"
   :padding    10
   :min-height 80})


(def simple-col-style
  {:border  "1px solid #2662fc"
   :padding 10})


(defn ^:export v-box-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [layout params
       [v-box params
        [:div {:style simple-col-style} "column 1"]
        [:div {:style simple-col-style} "column 2"]
        [:div {:style simple-col-style} "column 3"]
        [:div {:style simple-col-style} "column 4"]]]])))


(defn ^:export h-box-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [layout params
       [h-box params
        [:div {:style col-style} "column 1"]
        [:div {:style col-style} "column 2"]
        [:div {:style col-style} "column 3"]
        [:div {:style col-style} "column 4"]]]])))


(defn ^:export h-box-columns-and-spans [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [layout
     [h-box
      [:div {:style col-style} "1"]
      [:div {:style col-style} "2"]
      [:div {:style col-style} "3"]
      [:div {:style col-style} "4"]
      [:div {:style col-style} "5"]
      [:div {:style col-style} "6"]
      [:div {:style col-style} "7"]
      [:div {:style col-style} "8"]
      [:div {:style col-style} "9"]
      [:div {:style col-style} "10"]
      [:div {:style col-style} "11"]
      [:div {:style col-style} "12"]]
     [h-box
      [column {:span 4 :style col-style} "Span 4"]
      [column {:span 8 :style col-style} "Span 8"]]
     [h-box
      [column {:span 2 :style col-style} "Span 2"]
      [column {:span 5 :style col-style} "Span 5"]
      [column {:span 5 :style col-style} "Span 5"]]
     [h-box
      [column {:span 6 :style col-style} "Span 6"]
      [column {:span 3 :style col-style} "Span 3"]
      [column {:span 3 :style col-style} "Span 3"]]]]))


(defn ^:export h-box-gutters [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [layout {:gap 8}
     [h-box
      [column {:span 6 :style col-style} "1 of 2"]
      [column {:span 6 :style col-style} "2 of 2"]]
     [h-box
      [column {:span 4 :style col-style} "1 of 3"]
      [column {:span 4 :style col-style} "2 of 3"]
      [column {:span 4 :style col-style} "2 of 3"]]]

    [:div {:style {:margin-top 40}}
     [layout {:gap 0}
      [h-box
       [column {:span 6 :style col-style} "1 of 2"]
       [column {:span 6 :style col-style} "2 of 2"]]
      [h-box
       [column {:span 4 :style col-style} "1 of 3"]
       [column {:span 4 :style col-style} "2 of 3"]
       [column {:span 4 :style col-style} "2 of 3"]]]]]))


(defn ^:export h-box-dividers [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [layout {:divider "vertical"}
     [h-box
      [column {:span 6 :style col-style} "1 of 2"]
      [column {:span 6 :style col-style} "2 of 2"]]
     [h-box
      [column {:span 4 :style col-style} "1 of 3"]
      [column {:span 4 :style col-style} "2 of 3"]
      [column {:span 4 :style col-style} "2 of 3"]]]

    [:div {:style {:margin-top 40}}
     [layout {:divider "horizontal"}
      [h-box
       [column {:span 6 :style col-style} "1 of 2"]
       [column {:span 6 :style col-style} "2 of 2"]]
      [h-box
       [column {:span 4 :style col-style} "1 of 3"]
       [column {:span 4 :style col-style} "2 of 3"]
       [column {:span 4 :style col-style} "2 of 3"]]]]]))


(defn ^:export h-box-align-items [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [layout
     [h-box {:align-items "stretch"}
      [column {:span 4 :style simple-col-style} "A short column can stretch to fill the space."]
      [column {:span 4 :style simple-col-style} "A tall"
       [:br] "column defines"
       [:br] "the height"
       [:br] "of the row."]
      [column {:span 4 :style simple-col-style} "Another"
       [:br] "column."]]
     [h-box {:align-items "start"}
      [column {:span 4 :style simple-col-style} "A short column can align to the start (top) of the row."]
      [column {:span 4 :style simple-col-style} "A tall"
       [:br] "column defines"
       [:br] "the height"
       [:br] "of the row."]
      [column {:span 4 :style simple-col-style} "Another"
       [:br] "column."]]
     [h-box {:align-items "end"}
      [column {:span 4 :style simple-col-style} "A short column can align to the end (bottom) of the row."]
      [column {:span 4 :style simple-col-style} "A tall"
       [:br] "column defines"
       [:br] "the height"
       [:br] "of the row."]
      [column {:span 4 :style simple-col-style} "Another"
       [:br] "column."]]
     [h-box {:align-items "center"}
      [column {:span 4 :style simple-col-style} "A short column can align to the center (middle) of the row."]
      [column {:span 4 :style simple-col-style} "A tall"
       [:br] "column defines"
       [:br] "the height"
       [:br] "of the row."]
      [column {:span 4 :style simple-col-style} "Another"
       [:br] "column."]]]]))


(defn ^:export mixed-layout [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [layout
     [h-box
      [column {:style simple-col-style} "left column"]
      [layout
       [v-box
        [:div {:style simple-col-style} "column 1"]
        [:div {:style simple-col-style} "column 2"]
        [:div {:style simple-col-style} "column 3"]
        [:div {:style simple-col-style} "column 4"]]]]
     [h-box
      [column {:style simple-col-style} "left column"]
      [column {:style simple-col-style} "center column"]
      [column {:style simple-col-style} "right column"]]
     [h-box
      [column {:span 5}
       [layout
        [v-box
         [column {:style simple-col-style} "left 1"]
         [column {:style simple-col-style} "left 2"]
         [column {:style simple-col-style} "left 3"]]]]
      [column {:style simple-col-style :span 7} "right column"]]]]))
