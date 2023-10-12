(ns splunk-ui-cljs.stories.tab-layout-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-icons/enterprise/List" :default List]
   ["@splunk/react-icons/enterprise/Search" :default Search]
   ["@splunk/react-icons/enterprise/Table" :default Table]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.tab-layout :refer [tab-layout]]))


(def ^:export default
  (utils/->default
   {:title     "Tab Layout"
    :component tab-layout
    :argTypes  {:model         {:type        {:name "text" :required false}
                                :description "The unique identifier of the currently selected tab. Can be a simple string or atom."
                                :control     "text"}
                :on-change     {:type        {:name "function" :required false}
                                :description "A callback that receives the event and data (selectedPanelId). If model is set, this callback is required."
                                :control     {:type nil}}
                :tabs          {:type        {:name "vector" :required true}
                                :description "One element in the vector for each tab.
                                              Typically, each element is a map with :id, :content, :label and optionally :icon, :style, :disabled, :tooltip keys.
                                              Vector of maps or atom"
                                :control     "array"}
                :default-tab   {:type        {:name "text" :required false}
                                :description "Sets the active panel on the initial render. It must match the :id of one of the child (tabs).
                                              Only use :default-tab when using TabLayout as an uncontrolled component."
                                :control     "text"}
                :layout        {:type        {:name "text" :required false}
                                :description "The layout direction for tabs."
                                :control     "select"
                                :options     ["horizontal" "vertical"]}
                :appearance    {:type        {:name "text" :required false}
                                :description "Setting this prop to 'context' creates an appearance without an underline."
                                :control     "select"
                                :options     ["navigation" "context"]}
                :icon-size     {:type        {:name "text" :required false}
                                :description "Size of icon in tab if it has an icon."
                                :control     "select"
                                :options     ["inline" "small" "large"]}
                :auto-activate {:type        {:name "boolean" :required false}
                                :description "If true, tabs will trigger the on-change callback when they receive focus."
                                :control     "boolean"}
                :tab-width     {:type        {:name "number" :required false}
                                :description "Width of each tab in pixels. Must be greater than 50 pixels. Leave blank for auto width."
                                :control     "number"}}}))


(defn ^:export tab-layout-uncontrolled [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [tab-layout {:default-tab "second"
                   :tabs        [{:id      "first"
                                  :label   "First"
                                  :content [:div {:style {:margin 20}} "First Tab"]}
                                 {:id      "second"
                                  :label   "Second"
                                  :content "Second Tab"
                                  :style   {:margin 20}}
                                 {:id      "third"
                                  :label   "Third"
                                  :content [:div {:style {:margin 20}} "Third Tab"]}]}]])))


(defn ^:export tab-layout-controlled [args]
  (let [params (-> args utils/->params)
        model  (r/atom "third")]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [tab-layout {:on-change   #(reset! model %)
                   :model       model
                   :default-tab "second"
                   :tabs        [{:id      "first"
                                  :label   "First"
                                  :content [:div {:style {:margin 20}} "First Tab"]}
                                 {:id      "second"
                                  :label   "Second"
                                  :content "Second Tab"
                                  :style   {:margin 20}}
                                 {:id      "third"
                                  :label   "Third"
                                  :content [:div {:style {:margin 20}} "Third Tab"]}]}]])))


(defn ^:export tab-layout-auto-activate [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [tab-layout {:default-tab   "second"
                   :auto-activate true
                   :tabs          [{:id      "first"
                                    :label   "First"
                                    :content [:div {:style {:margin 20}} "First Tab"]}
                                   {:id      "second"
                                    :label   "Second"
                                    :content "Second Tab"
                                    :style   {:margin 20}}
                                   {:id      "third"
                                    :label   "Third"
                                    :content [:div {:style {:margin 20}} "Third Tab"]}]}]])))


(defn ^:export tab-layout-icons [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [tab-layout {:default-tab "second"
                   :icon-size   "small"
                   :tabs        [{:id      "first"
                                  :label   "First"
                                  :content [:div {:style {:margin 20}} "First Tab"]
                                  :icon    [:> Search {:screenReaderText nil}]}
                                 {:id      "second"
                                  :label   "Second"
                                  :content "Second Tab"
                                  :style   {:margin 20}
                                  :icon    [:> List {:screenReaderText nil}]}
                                 {:id      "third"
                                  :label   "Third"
                                  :content [:div {:style {:margin 20}} "Third Tab"]
                                  :icon    [:> Table {:screenReaderText nil}]}]}]])))


(defn ^:export tab-layout-vertical [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [tab-layout {:default-tab "second"
                   :layout      "vertical"
                   :tabs        [{:id      "first"
                                  :label   "First"
                                  :content [:div {:style {:margin 20}} "First Tab"]}
                                 {:id      "second"
                                  :label   "Second"
                                  :content "Second Tab"
                                  :style   {:margin 20}}
                                 {:id      "third"
                                  :label   "Third"
                                  :content [:div {:style {:margin 20}} "Third Tab"]}]}]])))


(defn ^:export tab-layout-vertical-icons [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [tab-layout {:default-tab "second"
                   :icon-size   "small"
                   :layout      "vertical"
                   :tabs        [{:id      "first"
                                  :label   "First"
                                  :content [:div {:style {:margin 20}} "First Tab"]
                                  :icon    [:> Search {:screenReaderText nil}]}
                                 {:id      "second"
                                  :label   "Second"
                                  :content "Second Tab"
                                  :style   {:margin 20}
                                  :icon    [:> List {:screenReaderText nil}]}
                                 {:id      "third"
                                  :label   "Third"
                                  :content [:div {:style {:margin 20}} "Third Tab"]
                                  :icon    [:> Table {:screenReaderText nil}]}]}]])))


(defn ^:export tab-layout-context [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [tab-layout {:default-tab "second"
                   :appearance  "context"
                   :tabs        [{:id      "first"
                                  :label   "First"
                                  :content [:div {:style {:margin 20}} "First Tab"]}
                                 {:id      "second"
                                  :label   "Second"
                                  :content "Second Tab"
                                  :style   {:margin 20}}
                                 {:id      "third"
                                  :label   "Third"
                                  :content [:div {:style {:margin 20}} "Third Tab"]}]}]])))
