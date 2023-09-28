(ns splunk-ui-cljs.stories.scroll-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-ui/Paragraph" :default P]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.scroll :refer [scroller]]))


(def ^:export default
  (utils/->default
   {:title     "Scroller"
    :component scroller
    :argTypes  {:tag        {:type        {:name "string" :required false}
                             :description "Type of the HTML element to use for component. Defaults to `div`"
                             :control     "text"}
                :width      {:type        {:name "number" :required false}
                             :description "Standard CSS width setting for this component. Could be number or string"
                             :control     "number"}
                :height     {:type        {:name "number" :required false}
                             :description "Standard CSS height setting for this component. Could be number or string"
                             :control     "number"}
                :min-width  {:type        {:name "number" :required false}
                             :description "Standard CSS min-width setting for this component. Could be number or string"
                             :control     "number"}
                :min-height {:type        {:name "number" :required false}
                             :description "Standard CSS min-height setting for this component. Could be number or string"
                             :control     "number"}
                :max-width  {:type        {:name "number" :required false}
                             :description "Standard CSS max-width setting for this component. Could be number or string"
                             :control     "number"}
                :max-height {:type        {:name "number" :required false}
                             :description "Standard CSS max-height setting for this component. Could be number or string"
                             :control     "number"}}}))


(defn ^:export scroller-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [scroller (merge {:width  200
                        :height 200}
                       params)
       [:> P {:style {:background-color "#FFF"}}
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit.
         Quisque vestibulum commodo diam, eu consectetur nulla tincidunt a.
         Maecenas eget fermentum tellus. Nulla suscipit a tellus vel varius.
         Vestibulum eu elit a metus varius venenatis eget ut risus.
         Duis suscipit in arcu volutpat facilisis. Quisque eu dictum metus.
         Aenean commodo cursus sollicitudin. Etiam at posuere ligula.
         Sed sapien massa, laoreet a cursus at, malesuada vel mi.
         Duis maximus orci est, facilisis blandit urna at."]]])))
