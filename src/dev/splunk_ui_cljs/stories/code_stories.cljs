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
    :argTypes  {:model     {:type        {:name "string" :required true}
                            :description "Code listing to show in the code editor. Could be an atom"
                            :control     "text"}
                :on-update {:type        {:name "function" :required true}
                            :description "Function that will receive code changes as argument"
                            :control     {:type nil}}}}))


(defn ^:export code-basic [args]
  (let [model (r/atom "(ns my.awesome-ns\n  (:require [clojure.string :as string]))\n\n(println \"Hello Clojure\")\n(println (string/reverse \"Hello Clojure\"))\n")]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [code {:model     model
             :on-update (fn [new-code]
                          (println new-code))}]])))


(defn ^:export code-basic-override-props [args]
  (let [model  (r/atom "(ns my.awesome-ns\n  (:require [clojure.string :as string]))\n\n(println \"Hello Clojure\")\n(println (string/reverse \"Hello Clojure\"))\n")
        params (-> args utils/->params)]
    (js/setTimeout #(reset! model "(println \"Hello ClojureScript!\")") 2000)
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [code (merge {:model     model
                    :on-update (fn [new-code]
                                 (println new-code))}
                   params)]])))
