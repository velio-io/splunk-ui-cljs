(ns splunk-ui-cljs.stories.input-text-stories
  (:require [reagent.core :as r]
            ["@splunk/themes" :refer [SplunkThemeProvider]]
            [splunk-ui-cljs.stories.utils :as utils]
            [splunk-ui-cljs.input-text :refer [input-text]]))


(def ^:export default
  (utils/->default
   {:title     "Input text"
    :component input-text
    :argTypes  {:placeholder {:control "text"}
                :width       {:control "number"}}}))


(defn on-change [new-value]
  (js/console.log new-value))


(defn ^:export input-text-normal [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [input-text (merge {:on-change   on-change
                          :placeholder "simple input"}
                         params)]])))


(defn ^:export input-text-custom-width [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [input-text (merge {:on-change   on-change
                          :placeholder "input with custom width"
                          :width       400}
                         params)]])))
