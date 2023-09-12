(ns splunk-ui-cljs.stories.utils
  (:require
   [reagent.core :as r]))


(defn ->params [^js args]
  (js->clj args :keywordize-keys true))


(defn ->reactified [options path]
  (if (get-in options path)
    (update-in options path r/reactify-component)
    options))


(defn ->default [options]
  (-> options
      (->reactified [:component])
      clj->js))
