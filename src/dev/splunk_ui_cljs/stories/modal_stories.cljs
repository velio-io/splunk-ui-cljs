(ns splunk-ui-cljs.stories.modal-stories
  (:require
   [reagent.core :as r]
   ["react" :as react]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-ui/Button" :default Button]
   ["@splunk/react-icons/enterprise/Dashboard" :default Dashboard]
   ["@splunk/react-ui/Paragraph" :default P]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.modal :refer [modal header body footer]]))


(def ^:export default
  (utils/->default
   {:title     "Modal"
    :component modal
    :argTypes  {:model         {:type        {:name "boolean" :required true}
                                :description "Set to true if the Modal is currently open. Otherwise, set to false. Can be a reagent atom"
                                :control     "boolean"}
                :divider       {:type        {:name "string" :required false}
                                :description "Show dividers between header, body and footer."
                                :control     "select"
                                :options     ["header" "footer" "both" "none"]}
                :initial-focus {:type        {:name "string" :required false}
                                :description "Allows focus to be set to a component other than the default. Supports first (first focusable element in the modal), container (focus the modal itself), or a ref."
                                :control     "select"
                                :options     ["first" "container"]}
                :on-close      {:type        {:name "function" :required false}
                                :description "Called when a close event occurs.
                                              The callback is passed the event and a reason, which is either 'escapeKey' or 'clickAway'.
                                              Generally, use this callback to toggle the open prop.
                                              This callback must return focus to the invoking element or other element that follows the logical flow of the application."
                                :control     {:type nil}}}}))


(defn ^:export modal-basic [args]
  (let [params (-> args utils/->params)
        model  (r/atom false)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:> Button {:onClick #(reset! model true)
                  :label   "Click me"}]

      [modal (merge {:model    model
                     :on-close #(reset! model false)}
                    params)
       [body
        "Just a basic Modal. No thrills at all."]]])))


(defn ^:export modal-all-sections [args]
  (let [params      (-> args utils/->params)
        model       (r/atom false)
        close-modal #(reset! model false)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:> Button {:onClick #(reset! model true)
                  :label   "Click me"}]

      [modal (merge {:model    model
                     :on-close close-modal}
                    params)
       [header {:title    "Header"
                :subtitle "Similar to cards, modals consist of three major parts."
                :on-close close-modal
                :icon     [:> Dashboard {:width  "100%"
                                         :height "100%"}]}]
       [body
        [:> P
         "Lorem ipsum dolor sit amet, consectetur adipiscing elit.
         Quisque vestibulum commodo diam, eu consectetur nulla tincidunt a.
         Maecenas eget fermentum tellus. Nulla suscipit a tellus vel varius.
         Vestibulum eu elit a metus varius venenatis eget ut risus.
         Duis suscipit in arcu volutpat facilisis. Quisque eu dictum metus.
         Aenean commodo cursus sollicitudin. Etiam at posuere ligula.
         Sed sapien massa, laoreet a cursus at, malesuada vel mi.
         Duis maximus orci est, facilisis blandit urna at."]]

       [footer
        [:> Button {:appearance "secondary" :onClick close-modal :label "Cancel"}]
        [:> Button {:appearance "primary" :label "Submit"}]]]])))


(defn counter-message [{:keys [count]}]
  [:span
   "This modal cannot be dismissed by the user. It will be dismissed in " @count " seconds."])


(defn ^:export modal-undismissable [args]
  (let [model    (r/atom false)
        count    (r/atom 0)
        interval (r/atom nil)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:> Button {:onClick (fn []
                             (let [i (js/setInterval #(swap! count dec) 1000)]
                               (reset! count 5)
                               (reset! interval i)
                               (js/setTimeout
                                (fn []
                                  (js/clearInterval @interval)
                                  (reset! model false))
                                5000)
                               (reset! model true)))
                  :label   "Click me"}]

      [modal {:model model}
       [body
        [counter-message {:count count}]]]])))


(defn focus-example [{:keys [model close-modal accept-button]}]
  [modal {:model         model
          :on-close      close-modal
          :initial-focus @accept-button}
   [header {:title    "Header"
            :subtitle "Similar to cards, modals consist of three major parts."
            :on-close close-modal
            :icon     [:> Dashboard {:width  "100%"
                                     :height "100%"}]}]
   [body
    [:> P
     "Lorem ipsum dolor sit amet, consectetur adipiscing elit.
     Quisque vestibulum commodo diam, eu consectetur nulla tincidunt a.
     Maecenas eget fermentum tellus. Nulla suscipit a tellus vel varius.
     Vestibulum eu elit a metus varius venenatis eget ut risus.
     Duis suscipit in arcu volutpat facilisis. Quisque eu dictum metus.
     Aenean commodo cursus sollicitudin. Etiam at posuere ligula.
     Sed sapien massa, laoreet a cursus at, malesuada vel mi.
     Duis maximus orci est, facilisis blandit urna at."]]

   [footer
    [:> Button {:appearance "secondary" :onClick close-modal :label "Cancel"}]
    [:> Button {:appearance "primary" :label "Accept" :elementRef #(reset! accept-button %)}]]])


(defn ^:export modal-initial-focus [args]
  (let [model         (r/atom false)
        close-modal   #(reset! model false)
        accept-button (r/atom nil)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:> Button {:onClick #(reset! model true)
                  :label   "Click me"}]
      [focus-example {:model         model
                      :close-modal   close-modal
                      :accept-button accept-button}]])))
