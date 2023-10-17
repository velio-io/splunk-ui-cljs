(ns splunk-ui-cljs.stories.message-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-ui/Button" :default Button]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.message :refer [message message-title message-link]]))


(def ^:export default
  (utils/->default
   {:title     "Message"
    :component message
    :argTypes  {:type       {:type        {:name "string" :required false}
                             :description "Sets the severity or type of this Message."
                             :control     "select"
                             :options     ["info" "success" "warning" "error"]}
                :appearance {:type        {:name "string" :required false}
                             :description "Changes the style of the Message."
                             :control     "select"
                             :options     ["default" "fill"]}
                :on-remove  {:type        {:name "function" :required false}
                             :description "Includes a remove button if set. Always set this prop when using the banner appearance, never set it when using the default appearance."
                             :control     {:type nil}}}}))


(defn ^:export message-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [message {:type "info"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
      [message {:type "warning"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
      [message {:type "error"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
      [message {:type "success"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]])))


(defn ^:export message-fill [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [message {:type "info" :appearance "fill"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
      [message {:type "warning" :appearance "fill"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
      [message {:type "error" :appearance "fill"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]
      [message {:type "success" :appearance "fill"}
       "Lorem ipsum dolor sit amet, consectetur adipiscing elit."]])))


(defn ^:export message-with-title [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [message {:type "info" :appearance "fill"}
       [message-title "Your trial " [:strong "will expire soon"] "."]]
      [message {:type "warning" :appearance "fill"}
       [message-title "Your trial " [:strong "was renewed"] "."]]
      [message {:type "error" :appearance "fill"}
       [message-title "Your trial " [:strong "has expired"] "."]]
      [message {:type "success" :appearance "fill"}
       [message-title "Your trial " [:strong "has expired"] "."]]])))


(defn ^:export message-content [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [message {:type "info" :appearance "fill"}
       [:div
        "A long error example."
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt
        ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
        laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in
        voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat
        non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. "
        [message-link {:to "Overview"}
         "Read more about Splunk UI"]]
       [:div {:style {:flex "0 0 auto"}}
        [:> Button {:appearance "secondary"} "Dismiss"]
        [:> Button {:appearance "primary"} "Action"]]]])))


(defn ^:export message-removable [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [message {:type "info" :appearance "fill" :on-remove (fn [event] (println "hello"))}
       [message-title "Your trial " [:strong "will expire soon"] "."]]
      [message {:type "warning" :appearance "fill" :on-remove (fn [event] (println "hello"))}
       [message-title "Your trial " [:strong "was renewed"] "."]]
      [message {:type "error" :appearance "fill" :on-remove (fn [event] (println "hello"))}
       [message-title "Your trial " [:strong "has expired"] "."]]
      [message {:type "success" :appearance "fill" :on-remove (fn [event] (println "hello"))}
       [message-title "Your trial " [:strong "has expired"] "."]]])))
