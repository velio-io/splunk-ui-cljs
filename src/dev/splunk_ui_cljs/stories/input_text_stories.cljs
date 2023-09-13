(ns splunk-ui-cljs.stories.input-text-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.input-text :as inputs]))


(def ^:export default
  (utils/->default
   {:title     "Input text"
    :component inputs/input-text
    :argTypes  {:placeholder {:control "text"}
                :width       {:control "number"}}}))


(defn on-change [new-value]
  (js/console.log new-value))


(defn ^:export input-text [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-text (merge {:on-change   on-change
                                 :placeholder "simple input"}
                                params)]])))


(defn ^:export input-text-custom-width [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-text (merge {:on-change   on-change
                                 :placeholder "input with custom width"
                                 :width       400}
                                params)]])))


(defn ^:export input-text-regex-validation [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-text (merge {:on-change        on-change
                                 :placeholder      "only 1 is allowed"
                                 :validation-regex #"^1+$"}
                                params)]])))


(defn ^:export input-password [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-password (merge {:on-change   on-change
                                     :placeholder "type password"}
                                    params)]])))


(defn ^:export input-textarea [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [inputs/input-textarea (merge {:on-change on-change
                                     :rows      4}
                                    params)]])))
