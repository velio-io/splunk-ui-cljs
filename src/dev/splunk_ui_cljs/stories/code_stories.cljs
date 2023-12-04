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
    :argTypes  {:model       {:type        {:name "string" :required true}
                              :description "Code listing to show in the code editor. Could be an atom"
                              :control     "text"}
                :on-update   {:type        {:name "function" :required false}
                              :description "Function that will receive code changes as argument"
                              :control     {:type nil}}
                :completions {:type        {:name "collection" :required false}
                              :description "Collection of maps (:label, :type, :info) that will be used as autocompletion suggestions"
                              :control     {:type nil}}
                :width       {:type        {:name "number" :required false}
                              :description "Root element width"
                              :control     "number"}
                :height      {:type        {:name "number" :required false}
                              :description "Root element height"
                              :control     "number"}}}))


(defn ^:export code-basic [args]
  (let [model (r/atom "(ns my.awesome-ns\n  (:require [clojure.string :as string]))\n\n(println \"Hello Clojure\")\n(println (string/reverse \"Hello Clojure\"))\n")]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [code {:model     model
             :on-update (fn [new-code]
                          (println new-code))}]])))


(defn ^:export code-autocomplete [args]
  (let [model (r/atom ";; start typing 'where' or 'increment' \n")]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [code {:model       model
             :on-update   (fn [new-code]
                            (println new-code))
             :completions [{:label "where" :type "function" :info "Filter events based on conditions.\n   Each condition is a vector composed of the function to apply on the field,\n   the field to extract from the event, and the event itself.\n   Multiple conditions can be added by using `:or` or `:and`.\n\n   ```clojure\n   (where [:= :metric 4])\n   ```\n\n   Here, we keep only events where the :metric field is equal to 4.\n\n   ```clojure\n   (where [:and [:= :host \"foo\"]\n                [:> :metric 10])\n  ```\n\n   Here, we keep only events with :host = foo and with :metric > 10"}
                           {:label "increment" :type "function" :info "Increment the event :metric field.\n\n  ```clojure\n  (increment\n    (index [:host]))\n  ```"}]}]])))


(defn ^:export code-basic-override-props [args]
  (let [model  (r/atom "(ns my.awesome-ns\n  (:require [clojure.string :as string]))\n\n(println \"Hello Clojure\")\n(println (string/reverse \"Hello Clojure\"))\n")
        params (-> args utils/->params)]
    (js/setTimeout #(reset! model "(println \"Hello ClojureScript!\")") 2000)
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [code (merge {:model     model
                    :width     500
                    :height    500
                    :on-update (fn [new-code]
                                 (println new-code))}
                   params)]])))
