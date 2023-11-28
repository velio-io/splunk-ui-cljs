(ns splunk-ui-cljs.stories.code-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.code :refer [code]]))


(def ^:export default
  (utils/->default
   {:title     "Code"
    :component code
    :argTypes  {:model {:type        {:name "boolean" :required true}
                        :description "Sets the current state of a checkbox. Could be an atom holding a boolean value"
                        :control     "boolean"}}}))


(defn ^:export code-basic [args]
  (let [model  (r/atom "(ns my.awesome-ns\n  (:require [clojure.string :as string]))\n\n(println \"Hello Clojure\")\n(println (string/reverse \"Hello Clojure\"))\n")
        params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [code (merge {:model     model
                    :on-update (fn [new-code]
                                 (println new-code))}
                   params)]])))
