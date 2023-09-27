(ns splunk-ui-cljs.stories.button-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-icons/Bug" :default Bug]
   ["@splunk/react-icons/ArrowClockwise" :default ArrowClockwise]
   ["@splunk/react-icons/ControlPlay" :default ControlPlay]
   ["@splunk/react-icons/ControlPause" :default ControlPause]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.button :refer [button]]))


(def ^:export default
  (utils/->default
   {:title     "Button"
    :component button
    :argTypes  {:label      {:type        {:name "string" :required false}
                             :description "Applies the text that displays on the button"
                             :control     "text"}
                :appearance {:type         {:name "string" :required false}
                             :description  "Changes the style of the button"
                             :defaultValue "default"
                             :control      "select"
                             :options      ["default", "secondary", "primary", "destructive", "pill", "toggle", "flat"]}
                :disabled?  {:type         {:name "boolean" :required false}
                             :description  "Prevents user from clicking the button"
                             :defaultValue false
                             :control      "boolean"}
                :on-click   {:type        {:name "function" :required false}
                             :description "Callback triggered on user click"
                             :control     {:type nil}}
                :to         {:type        {:name "string" :required false}
                             :description "Identifies the URL for a link. If set, Splunk UI applies an <a> tag instead of a <button> tag"
                             :control     "text"}
                :icon       {:type        {:name "string" :required false}
                             :description "Hiccup vector or any valid React element to show the icon"
                             :control     "text"}}}))


(defn ^:export button-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [button (merge {:label "I'm Button"} params)]]])))


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


(defn ^:export button-disabled [args]
  (let [disabled? (r/atom true)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div {:style {:display "flex" :justify-content "space-between" :width 400}}
       [button {:label      "disabled"
                :appearance "primary"
                :disabled?  true}]
       [button {:label      "disabled"
                :appearance "primary"
                :disabled?  disabled?}]
       [button {:label      "toggle state"
                :appearance "toggle"
                :on-click   #(reset! disabled? (not @disabled?))}]]])))


(defn ^:export button-as-link [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [:div
     [button {:label      "link"
              :appearance :primary
              :to         "https://some-link.com"}]]]))


(defn ^:export button-with-icon [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [:div {:style {:display "flex" :justify-content "space-between"}}
     [button {:icon [:> Bug]}]
     [button {:icon [:> ArrowClockwise] :appearance :primary}]
     [button {:icon [:> ControlPlay] :label "Play"}]
     [button {:icon [:> ControlPause] :label "Pause" :appearance :primary}]]]))
