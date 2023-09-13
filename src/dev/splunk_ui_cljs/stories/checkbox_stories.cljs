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
    :argTypes  {:model      {:control "radio"
                             :options [true false]}
                :label      {:control "text"}
                :status     {:control "select"
                             :options ["error"]}
                :appearance {:control "select"
                             :options ["checkbox", "toggle"]}
                :disabled?  {:control "radio"
                             :options [true false]}}}))


(defn ^:export checkbox-variants [args]
  (let [checkbox-model (r/atom false)
        toggle-model   (r/atom false)
        params         (-> args utils/->params)
        params         (if (:status params)
                         (update params :status #(when (= % "error") :error)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div {:style {:display "flex" :justify-content "space-around" :width 400}}
       [checkbox (merge {:label     "checkbox"
                         :model     checkbox-model
                         :on-change (fn [selected]
                                      (reset! checkbox-model selected))}
                        params)]
       [checkbox (merge {:label      "toggle"
                         :appearance "toggle"
                         :model      toggle-model
                         :on-change  (fn [selected]
                                       (reset! toggle-model selected))}
                        params)]]

      [:div {:style {:display "flex" :justify-content "space-around" :width 400}}
       [checkbox (merge {:label     "checkbox"
                         :model     checkbox-model
                         :disabled? true
                         :on-change (fn [selected]
                                      (reset! checkbox-model selected))}
                        params)]
       [checkbox (merge {:label      "toggle"
                         :appearance "toggle"
                         :model      toggle-model
                         :disabled?  true
                         :on-change  (fn [selected]
                                       (reset! toggle-model selected))}
                        params)]]

      [:div {:style {:display "flex" :justify-content "space-around" :width 400}}
       [checkbox (merge {:label     "checkbox"
                         :model     checkbox-model
                         :status    :error
                         :on-change (fn [selected]
                                      (reset! checkbox-model selected))}
                        params)]
       [checkbox (merge {:label      "toggle"
                         :appearance "toggle"
                         :model      toggle-model
                         :status     :error
                         :on-change  (fn [selected]
                                       (reset! toggle-model selected))}
                        params)]]])))
