(ns splunk-ui-cljs.stories.checkbox-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.checkbox :refer [checkbox]]))


(def ^:export default
  (utils/->default
   {:title     "Checkbox"
    :component checkbox
    :argTypes  {:model      {:type        {:name "boolean" :required true}
                             :description "Sets the current state of a checkbox. Could be an atom holding a boolean value"
                             :control     "boolean"}
                :on-change  {:type        {:name "function" :required true}
                             :description "Called when the checkbox is clicked. Passed the new value of the checkbox"
                             :control     {:type nil}}
                :label      {:type        {:name "string" :required false}
                             :description "The label shown to the right"
                             :control     "text"}
                :status     {:type        {:name "string" :required false}
                             :description "Highlight the field as having an error"
                             :control     "select"
                             :options     ["error"]}
                :appearance {:type         {:name "string" :required false}
                             :description  "Define the look and feel of the checkbox element"
                             :control      "select"
                             :defaultValue "checkbox"
                             :options      ["checkbox", "toggle"]}
                :disabled?  {:type        {:name "boolean" :required false}
                             :description "If true, user interaction is disabled"
                             :control     "boolean"}}}))


(defn ^:export checkbox-basic [args]
  (let [checkbox-model (r/atom false)
        params         (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [checkbox (merge {:label     "checkbox"
                         :model     checkbox-model
                         :on-change (fn [selected]
                                      (reset! checkbox-model selected))}
                        params)]]])))


(defn ^:export checkbox-variants [args]
  (let [checkbox-model (r/atom false)
        set-checkbox   #(reset! checkbox-model %)
        toggle-model   (r/atom false)
        set-toggle     #(reset! toggle-model %)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div {:style {:display "flex" :justify-content "space-around" :width 400}}
       [checkbox {:label     "checkbox"
                  :model     checkbox-model
                  :on-change set-checkbox}]
       [checkbox {:label      "toggle"
                  :appearance "toggle"
                  :model      toggle-model
                  :on-change  set-toggle}]]

      [:div {:style {:display "flex" :justify-content "space-around" :width 400}}
       [checkbox {:label     "checkbox"
                  :model     checkbox-model
                  :disabled? true
                  :on-change set-checkbox}]
       [checkbox {:label      "toggle"
                  :appearance "toggle"
                  :model      toggle-model
                  :disabled?  true
                  :on-change  set-toggle}]]

      [:div {:style {:display "flex" :justify-content "space-around" :width 400}}
       [checkbox {:label     "checkbox"
                  :model     checkbox-model
                  :status    :error
                  :on-change set-checkbox}]
       [checkbox {:label      "toggle"
                  :appearance "toggle"
                  :model      toggle-model
                  :status     :error
                  :on-change  set-toggle}]]])))
