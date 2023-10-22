(ns splunk-ui-cljs.stories.cards-stories
  (:require
   [reagent.core :as r]
   ["react" :as react]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-ui/Paragraph" :default P]
   ["@splunk/react-ui/DefinitionList" :default DL :refer [Term Description]]
   ["@splunk/react-ui/Button" :default Button]
   ["@splunk/react-ui/Menu" :default Menu :refer [Item Divider]]
   ["@splunk/react-icons/enterprise/Star" :default Star]
   ["@splunk/react-icons/enterprise/Table" :default Table]
   ["@splunk/react-icons/enterprise/Success" :default Success]
   ["@splunk/react-icons/enterprise/CircleHalfFilled" :default CircleHalfFilled]
   ["@splunk/react-icons/enterprise/Pencil" :default Pencil]
   ["@splunk/react-icons/enterprise/Refresh" :default Refresh]
   ["@splunk/react-icons/enterprise/Clone" :default Clone]
   ["@splunk/react-icons/enterprise/Remove" :default Remove]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.cards :refer [card header body footer layout]]))


(def ^:export default
  (utils/->default
   {:title     "Cards"
    :component card
    :argTypes  {:model               {:type        {:name "any" :required true}
                                      :description "Returns a value on click. Use when composing or if you have more than one selectable Card."
                                      :control     "text"}
                :style               {:type        {:name "map" :required true}
                                      :description "CSS styles map"
                                      :control     {:type nil}}
                :element-ref         {:type        {:name "ref" :required true}
                                      :description "A React ref which is set to the DOM element when the component mounts and null when it unmounts."
                                      :control     {:type nil}}
                :selected            {:type        {:name "boolean" :required true}
                                      :description "Renders Card as selected if set to true."
                                      :control     "boolean"}
                :show-border         {:type        {:name "boolean" :required true}
                                      :description "Includes a border on the Card if set to true."
                                      :control     "boolean"}
                :on-click            {:type        {:name "function" :required true}
                                      :description "Callback when the Card is clicked."
                                      :control     {:type nil}}
                :to                  {:type        {:name "text" :required true}
                                      :description "Takes a URL to go to when the Card is clicked."
                                      :control     "text"}
                :open-in-new-context {:type        {:name "boolean" :required true}
                                      :description "To open the to link in a new window, set openInNewContext to true."
                                      :control     "boolean"}}}))


(defn ^:export card-basic [args]
  (let [params (-> args utils/->params)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [card params
       [header {:title "Title"}]
       [body
        [:> DL {:termWidth 222}
         [:> Term "First Key"]
         [:> Description "Value"]
         [:> Term "Second Key"]
         [:> Description "Value"]
         [:> Term "Third Key"]
         [:> Description "Value"]
         [:> Term "Next Key"]
         [:> Description "Value"]
         [:> Term "Another Key"]
         [:> Description "Value"]
         [:> Term "Last Key"]
         [:> Description "Value"]]]]])))


(defn ^:export card-full [args]
  (let [style {:width 300 :height 400 :margin "0 20px 20px 0"}]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [card {:style style}
        [header {:title    "Title"
                 :subtitle "Subtitle"}]
        [body
         [:> P
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In dictum metus
           enim, ac ullamcorper ante condimentum at. Pellentesque habitant morbi
           tristique senectus et netus et malesuada fames ac turpis egestas. Duis
           laoreet sit amet mauris eget ullamcorper. Nullam eu tempor enim. Etiam non
           enim ac nibh feugiat congue. Nullam neque elit, varius vel rhoncus
           sollicitudin, molestie sit amet sapien. Nunc nibh enim, dictum sit amet nisl
           nec, scelerisque dignissim lacus. Pellentesque placerat pulvinar justo id
           commodo."]]
        [footer
         "Footer"]]

       [card {:style style}
        [header {:title    "This is a card with a really long title!"
                 :subtitle "Full Card Example"}
         [:div {:style {:textAlign "right"
                        :color     "#f1b10e"}}
          [:> Star {:size "24px"}]]]
        [body
         [:> P
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In dictum metus
           enim, ac ullamcorper ante condimentum at. Pellentesque habitant morbi
           tristique senectus et netus et malesuada fames ac turpis egestas. Duis
           laoreet sit amet mauris eget ullamcorper. Nullam eu tempor enim. Etiam non
           enim ac nibh feugiat congue. Nullam neque elit, varius vel rhoncus
           sollicitudin, molestie sit amet sapien. Nunc nibh enim, dictum sit amet nisl
           nec, scelerisque dignissim lacus. Pellentesque placerat pulvinar justo id
           commodo."]]
        [footer {:show-border false}
         [:> Button {:label "Footer Button"}]]]

       [card {:style style}
        [header {:title    "inset"
                 :subtitle "Example"}]
        [body {:inset false
               :style {:color "#3a87ad" :textAlign "center"}}
         [:> Table {:height "255px" :width "295px"}]]
        [footer
         "Footer"]]]])))


(defn ^:export card-with-images [args]
  (let [style {:width 300 :height 400 :margin "0 20px 20px 0"}]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:div
       [card {:style style}
        [body {:style {:background "#d9edf7"
                       :textAlign  "center"
                       :padding    "84px 50px"
                       :color      "#3a87ad"}}
         [:> Success {:height "150px" :width "150px"}]]
        [header {:title    "Blue"
                 :subtitle "Check Mark"}]]

       [card {:style style}
        [header {:title    "Green"
                 :subtitle "Half Circle"}]
        [body {:style {:background "#d0e9be"
                       :textAlign  "center"
                       :padding    "84px 50px"
                       :color      "#65a637"}}
         [:> CircleHalfFilled {:height "150px" :width "150px"}]]]]])))


(defn clicks-count [{:keys [count]}]
  [:<>
   [header {:title (str "Click count: " @count)}]
   [body "Click me please!"]
   [footer "Click count: " @count]])


(defn ^:export card-clickable [args]
  (let [count (r/atom 0)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [layout {:card-width  300
               :gutter-size 20}
       [card {:to                  "http://www.splunk.com"
              :open-in-new-context true}
        [header {:title    "What is Splunk?"
                 :subtitle "Click to Learn More"}]
        [body
         [:> P
          "Splunk Enterprise makes it simple to collect, analyze and act upon the
           untapped value of the big data generated by your technology infrastructure,
           security systems and business applications—giving you the insights to drive
           operational performance and business results."]
         [:> P
          "Splunk Enterprise monitors and analyzes machine data from any source to
           deliver Operational Intelligence to optimize your IT, security and business
           performance. With intuitive analysis features, machine learning, packaged
           applications and open APIs, Splunk Enterprise is a flexible platform that
           scales from focused use cases to an enterprise-wide analytics backbone."]]
        [footer
         "Splunk"]]

       [card {:on-click #(swap! count inc)}
        [clicks-count {:count count}]]]])))


(defn selectable-cards [{:keys [selected cards]}]
  (into
   [:div]
   (for [c cards
         :let [selected? (contains? @selected c)]]
     [card {:key      c
            :model    c
            :on-click #(if selected?
                         (swap! selected disj c)
                         (swap! selected conj c))
            :selected selected?
            :style    {:margin "0 20px 20px 0"}}
      [header {:title    "Select Me"
               :subtitle "Click to Select"}]
      [body
       [:div {:style {:width :150}}]]
      [footer {:show-border true}
       (if selected?
         "Selected"
         "Unselected")]])))


(defn ^:export card-selectable [args]
  (let [selected (r/atom #{1 3})
        cards    [1 2 3 4 5]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [selectable-cards {:selected selected
                         :cards    cards}]])))


(defn ^:export card-with-actions [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [layout {:card-width  310
             :gutter-size 20}
     [card
      [header {:title             "Title"
               :subtitle          "subtitlesubtitlesubtitle"
               :action-primary    [:> Button {:appearance "secondary"
                                              :icon       (r/as-element
                                                           [:> Pencil {:hideDefaultTooltip true}])}]
               :actions-secondary [:> Menu
                                   [:> Item {:icon (r/as-element [:> Refresh])}
                                    "Refresh"]
                                   [:> Divider]
                                   [:> Item {:icon (r/as-element [:> Clone])}
                                    "Duplicate"]
                                   [:> Item {:icon (r/as-element [:> Remove])}
                                    "Delete"]]}]
      [body
       "Splunk Enterprise makes it simple to collect, analyze and act upon the untapped
        value of the big data generated by your technology infrastructure, security
        systems and business applications."]
      [footer
       [:> Button {:appearance "secondary"} "Label"]
       [:> Button {:appearance "primary"} "Label"]]]

     [card
      [header {:title             "Title"
               :subtitle          "subtitlesubtitlesubtitle"
               :actions-secondary [:> Menu
                                   [:> Item "Favorite"]
                                   [:> Item "Share"]]}]
      [body
       "Splunk Enterprise makes it simple to collect, analyze and act upon the untapped
        value of the big data generated by your technology infrastructure, security
        systems and business applications."]
      [footer
       [:> Button "Action"]]]]]))

