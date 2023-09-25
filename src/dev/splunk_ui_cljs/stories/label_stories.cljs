(ns splunk-ui-cljs.stories.label-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.label :refer [label]]
   [splunk-ui-cljs.input-text :as inputs]
   [splunk-ui-cljs.dropdown :as dropdowns]))


(def ^:export default
  (utils/->default
   {:title     "Label"
    :component label
    :argTypes  {:label             {:type        {:name "string" :required true}
                                    :description "Label text"
                                    :control     "text"}
                :label-for         {:type        {:name "string" :required false}
                                    :description "Override the for attribute of the label. See the component description for details."
                                    :control     "text"}
                :position          {:type        {:name "string" :required false}
                                    :description "Place where to place a label. One of left, top"
                                    :control     "select"
                                    :options     ["left" "top"]}
                :label-width       {:type        {:name "number" :required false}
                                    :description "When labelPosition is left, the width of the label in pixels or a value with a unit."
                                    :control     "number"}
                :layout            {:type        {:name "string" :required false}
                                    :description "A layout defines how controls are aligned and displayed. The 'fillJoin' layout is not supported by the scp theme."
                                    :control     "select"
                                    :options     ["fill" "fillJoin" "none" "stack"]}
                :status            {:type        {:name "string" :required false}
                                    :description "Highlight the control group as having an error and optionally provide error text. If error text is provided, displays it below the control. The label will turn red, and the help text will turn red if error text was not provided. Child components will not turn red."
                                    :control     "select"
                                    :options     ["error"]}
                :help              {:type        {:name "string" :required false}
                                    :description "A help test displayed at the bottom. Could be a string or rect (reagent) element e.g. [:div ...]"
                                    :control     "text"}
                :required?         {:type        {:name "boolean" :required false}
                                    :description "Sets the control required and adds an asterisk before the label."
                                    :control     "boolean"}
                :size              {:type        {:name "string" :required false}
                                    :description "The size of the text label. Supported by the enterprise theme only."
                                    :control     "select"
                                    :options     ["small" "medium"]}
                :tooltip           {:type        {:name "string" :required false}
                                    :description "Displays a tooltip beside the label."
                                    :control     "text"}
                :tooltip-placement {:type        {:name "string" :required false}
                                    :description "If a tooltip is provided, sets its default placement."
                                    :control     "select"
                                    :options     ["above" "below" "left" "right" "theme"]}}}))


(defn ^:export label-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [label (merge {:label   "Field Label"
                     :tooltip "Tooltip helps explain the label."
                     :help    "This is the help text."}
                    params)
       [inputs/input-text {:model "hello world"}]]])))


(defn ^:export label-on-top [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [label (merge {:label    "Label Above"
                     :position "top"}
                    params)
       [inputs/input-text {:model "hello world"}]]])))


(defn ^:export label-help-component [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [label (merge {:label "Username"
                     :help  [:span "Help text with a "
                             [:a {:href "http://duckduckgo.com"}
                              "link"]]}
                    params)
       [inputs/input-text {:model ""}]]])))


(defn ^:export label-required [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [label (merge {:label     "Username"
                     :required? true}
                    params)
       [inputs/input-text {:model ""}]]])))


(defn ^:export label-error-status [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [label (merge {:label  "Username"
                     :status "error"}
                    params)
       [inputs/input-text {:model  ""
                           :status "error"}]]])))


(defn ^:export label-fill-layout [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [label {:label "Two controls"}
     [inputs/input-text {:model "hello"}]
     [dropdowns/single-dropdown {:model   1
                                 :choices [{:id 1 :label "one"}
                                           {:id 2 :label "two"}]}]]]))


(defn ^:export label-filljoin-layout [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "enterprise" :colorScheme "light"}
    [label {:label  "Two controls"
            :layout "fillJoin"}
     [inputs/input-text {:model "hello"}]
     [dropdowns/single-dropdown {:model   1
                                 :choices [{:id 1 :label "one"}
                                           {:id 2 :label "two"}]}]]]))
