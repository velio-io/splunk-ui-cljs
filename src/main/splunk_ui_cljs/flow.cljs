(ns splunk-ui-cljs.flow
  (:require
   [applied-science.js-interop :as j]
   ["react" :as react]
   [clojure.string :as string]
   [reagent.core :as r]
   [cljs-styled-components.reagent :refer-macros [defstyled defglobalstyle]]
   ["react-flow-renderer" :default ReactFlow
    :refer [Background applyNodeChanges applyEdgeChanges addEdge Handle Position useReactFlow]]
   ["@splunk/react-ui/Menu" :default Menu :refer [Item]]
   ["@splunk/react-ui/DefinitionList" :default DL :refer [Term Description]]
   ["@splunk/themes" :refer [variables SplunkThemeProvider]]
   ["@splunk/themes/getTheme" :default get-theme]
   ["@splunk/react-icons/Cross" :default Cross]
   ["@splunk/react-icons/CrossCircle" :default CrossCircle]
   ["@splunk/react-icons/PlusCircle" :default PlusCircle]
   ["@splunk/react-icons/TrashCanCross" :default TrashCanCross]
   ["@splunk/react-icons/Cog" :default Cog]
   ["@splunk/react-icons/FloppyDisk" :default FloppyDisk]
   [splunk-ui-cljs.dropdown :as dropdown]
   [splunk-ui-cljs.input-text :as inputs]
   [splunk-ui-cljs.code :as code]
   [splunk-ui-cljs.label :as label]
   [splunk-ui-cljs.button :as button]))


(def action-controls-by-type
  {"where"              {:type :code}
   "increment"          {:type :no-args}
   "index"              {:type :strings}
   "fixed-event-window" {:type :map :fields [{:field :size :label "Size"}]}
   "default"            {:type :key-vals}})


(defn is-object [value]
  (and (= (type value) js/Object)
       (not (nil? value))
       (not (j/call js/Array :isArray value))))


(defn deep-merge [object overrides]
  (let [target-keys (j/call js/Object :keys overrides)]
    (reduce (fn [acc key]
              (let [target (j/get object key)
                    source (j/get overrides key)]
                (if (and (is-object target) (is-object source))
                  (j/call js/Object :assign #js {} acc (j/obj key (deep-merge target source)))
                  (j/call js/Object :assign #js {} acc (j/obj key source)))))
            object
            target-keys)))


(defstyled node-action-button :button
  {:color            (j/get variables :contentColorInverted)
   :background-color (j/get variables :contentColorActive)
   :cursor           "pointer"
   :padding          "4px"
   :border           "none"
   :border-radius    "4px"
   "& + &"           {:margin-left "4px"}
   ":hover"          {:box-shadow       "0 0 0 2px #f9f9f9, 0 0 3px 2px rgb(0 0 0 / 25%)"
                      :background-color (j/get variables :backgroundColorFloating)}})


(defn stream-form [{:keys [stream-name]}]
  (let [*stream-state      (r/atom {:name  stream-name
                                    :error nil})
        change-stream-name #(swap! *stream-state assoc :name % :error nil)]
    (fn [{:keys [id status set-nodes get-nodes]}]
      [:form
       {:on-submit
        (fn [event]
          (j/call event :preventDefault)
          (let [{stream-name :name} @*stream-state
                stream-name (some-> stream-name string/trim)]
            (if (empty? stream-name)
              (swap! *stream-state assoc :error "Can't be empty")

              (let [nodes (get-nodes)]
                (->> nodes
                     (map (fn [node]
                            (if (= id (j/get node :id))
                              (->> (j/lit {:data {:status nil :stream-name stream-name}})
                                   (deep-merge node))
                              node)))
                     (to-array)
                     (set-nodes))))))}
       (let [{stream-name :name} @*stream-state
             stream-name-error (:error @*stream-state)]
         [label/label {:label       "Stream name"
                       :label-width 100
                       :status      (when (some? stream-name-error) "error")
                       :help        stream-name-error}
          [inputs/input-text
           {:model           stream-name
            :on-change       change-stream-name
            :change-on-blur? false
            :placeholder     "Enter stream name"}]])

       [:div {:style {:position "absolute"
                      :right    0
                      :bottom   -30}}
        [node-action-button {:type    "button"
                             :onClick (fn [event]
                                        (let [nodes (get-nodes)]
                                          (cond->> nodes
                                                   (= status "new")
                                                   (remove (fn [node]
                                                             (= id (j/get node :id))))
                                                   (= status "editing")
                                                   (map (fn [node]
                                                          (if (= id (j/get node :id))
                                                            (->> (j/lit {:data {:status nil}})
                                                                 (deep-merge node))
                                                            node)))
                                                   :always (to-array)
                                                   :always (set-nodes))))}
         [:> CrossCircle]]
        [node-action-button {:type "submit"}
         [:> FloppyDisk]]]])))


(defstyled flow-node-actions :div
  {:position "absolute"
   :right    0
   :bottom   "-30px"
   :display  "none"})


(defn stream-node [props]
  (j/let [^:js {{:keys [stream-name status]} :data id :id} props
          ^:js {:keys [setNodes getNodes]} (useReactFlow)
          show-form? (or (= status "new") (= status "editing"))]
    (r/as-element
     [:div
      (if show-form?
        [stream-form
         {:id          id
          :stream-name stream-name
          :status      status
          :set-nodes   setNodes
          :get-nodes   getNodes}]

        [:<>
         [:b stream-name]
         [flow-node-actions {:className "flow-node-actions"}
          [node-action-button {:type    "button"
                               :onClick (fn [event]
                                          (let [nodes (getNodes)]
                                            (->> nodes
                                                 (remove (fn [node]
                                                           (= id (j/get node :id))))
                                                 (to-array)
                                                 (setNodes))))}
           [:> TrashCanCross]]
          [node-action-button {:onClick (fn [event]
                                          (let [nodes (getNodes)]
                                            (->> nodes
                                                 (map (fn [node]
                                                        (if (= id (j/get node :id))
                                                          (->> (j/lit {:data {:status "editing"}})
                                                               (deep-merge node))
                                                          node)))
                                                 (to-array)
                                                 (setNodes))))}
           [:> Cog]]]])
      [:> Handle {:type     "source"
                  :position Position.Right}]])))


(defstyled code-container :div
  {:text-align       "left"
   :border-width     "1px"
   :border-style     "solid"
   :border-color     (j/get variables :interactiveColorBorder)
   :border-radius    (j/get variables :borderRadius)
   :background-color (j/get variables :backgroundColorNavigation)
   ":hover"          {:border-color (j/get variables :interactiveColorBorderHover)}})


(defstyled pair-remove-icon :span
  {:margin-left "10px"
   :padding     "2px"
   :color       (j/get variables :statusColorCritical)
   ":hover"     {:cursor "pointer"
                 :color  (j/get variables :statusColorHigh)}})


(defn code-control [{:keys [state]}]
  [code-container
   [code/code {:model     (:value @state)
               :on-update #(swap! state assoc :value %)}]])


(defn strings-control [{:keys [state]}]
  [inputs/input-text
   {:model           (string/join "," (:value @state))
    :on-change       #(swap! state assoc :value (clojure.string/split % #",\s*"))
    :change-on-blur? false
    :placeholder     "single or comma separated list of strings"}])


(defn map-control [{:keys [fields state]}]
  (into [:<>]
    (for [{field-label :label field :field} fields]
      [label/label {:label       field-label
                    :label-width 100}
       [inputs/input-text
        {:model           (get-in @state [:value field])
         :on-change       #(swap! state assoc-in [:value field] %)
         :change-on-blur? false}]])))


(defn key-value-pairs-form [{:keys [state]}]
  (if (empty? (:value @state))
    [:div {:style {:margin     "10px"
                   :text-align "left"}}
     "Enter key-value pair below"]

    [:div {:style {:margin     "10px"
                   :text-align "left"}}
     "Added key-value pairs:"
     (into [:> DL {:style {:margin-top "6px"}}]
       (for [[key value] (:value @state)]
         [:<>
          [:> Term key " : "]
          [:> Description
           [:span value]
           [pair-remove-icon {:onClick #(swap! state update :value dissoc key)}
            [:> Cross]]]]))]))


(defn key-vals-control []
  (let [pair-key   (r/atom "")
        pair-value (r/atom "")]
    (fn [{:keys [state]}]
      [:div
       [key-value-pairs-form
        {:state state}]

       [:div {:style {:display "flex" :gap "4px"}}
        [inputs/input-text
         {:placeholder     "Key"
          :model           pair-key
          :on-change       #(reset! pair-key %)
          :change-on-blur? false}]
        [inputs/input-text
         {:placeholder     "Value"
          :model           pair-value
          :on-change       #(reset! pair-value %)
          :change-on-blur? false}]
        [button/button {:label      "Add"
                        :appearance "toggle"
                        :on-click   #(do (swap! state assoc-in [:value @pair-key] @pair-value)
                                         (reset! pair-key "")
                                         (reset! pair-value ""))}]]])))


(defn action-form [{:keys [action-name action-type action-value]}]
  (let [*action-state      (r/atom {:name   action-name
                                    :type   action-type
                                    :value  action-value
                                    :errors {}})
        change-action-name #(doto *action-state
                              (swap! assoc :name %)
                              (swap! assoc-in [:errors :name] nil))
        change-action-type #(doto *action-state
                              (swap! assoc :type % :value nil)
                              (swap! assoc-in [:errors :type] nil))]
    (fn [{:keys [id status set-nodes get-nodes]}]
      (let [{action-name :name action-type :type :keys [errors]} @*action-state]
        [:form
         {:style {:margin-bottom "12px"}
          :on-submit
          (fn [event]
            (j/call event :preventDefault)
            (let [{action-name :name action-type :type action-value :value} @*action-state
                  action-name (some-> action-name string/trim)]
              (cond
                (empty? action-name)
                (swap! *action-state assoc-in [:errors :name] "Can't be empty")

                (empty? action-type)
                (swap! *action-state assoc-in [:errors :type] "Can't be empty")

                :otherwise
                (let [nodes (get-nodes)]
                  (->> nodes
                       (map (fn [node]
                              (if (= id (j/get node :id))
                                (->> (j/lit {:data {:status       nil
                                                    :action-name  action-name
                                                    :action-type  action-type
                                                    :action-value action-value}})
                                     (deep-merge node))
                                node)))
                       (to-array)
                       (set-nodes))))))}
         (let [action-name-error (:name errors)]
           [label/label {:label       "Action name"
                         :label-width 100
                         :status      (when (some? action-name-error) "error")
                         :help        action-name-error}
            [inputs/input-text
             {:model           action-name
              :on-change       change-action-name
              :change-on-blur? false
              :placeholder     "Enter action name"}]])

         (let [action-type-error (:type errors)]
           [label/label {:label       "Action type"
                         :label-width 100
                         :status      (when (some? action-type-error) "error")
                         :help        action-type-error}
            [dropdown/single-dropdown
             {:on-change change-action-type
              :model     action-type
              :inline    false
              :choices   [{:id "where" :label "where"} ;; code
                          {:id "increment" :label "increment"} ;; no args
                          {:id "index" :label "index"} ;; string or multiple strings
                          {:id "fixed-event-window" :label "fixed event window"} ;; map with fixed keys
                          {:id "default" :label "default"}]}]]) ;; random key value pairs

         (when (some? action-type)
           (let [{control-type :type :as action-props}
                 (-> (get action-controls-by-type action-type)
                     (assoc :state *action-state))]
             (case control-type
               :code [code-control action-props]
               :strings [strings-control action-props]
               :map [map-control action-props]
               :key-vals [key-vals-control action-props]
               nil)))

         [:div {:style {:position "absolute"
                        :right    0
                        :bottom   -30}}
          [node-action-button {:type    "button"
                               :onClick (fn [event]
                                          (let [nodes (get-nodes)]
                                            (cond->> nodes
                                                     (= status "new")
                                                     (remove (fn [node]
                                                               (= id (j/get node :id))))
                                                     (= status "editing")
                                                     (map (fn [node]
                                                            (if (= id (j/get node :id))
                                                              (->> (j/lit {:data {:status nil}})
                                                                   (deep-merge node))
                                                              node)))
                                                     :always (to-array)
                                                     :always (set-nodes))))}
           [:> CrossCircle]]
          [node-action-button {:type "submit"}
           [:> FloppyDisk]]]]))))


(defn action-node [props]
  (j/let [^:js {{:keys [action-name action-type action-value status]} :data id :id} props
          ^:js {:keys [setNodes getNodes]} (useReactFlow)
          show-form? (or (= status "new") (= status "editing"))]
    (r/as-element
     [:div
      [:> Handle {:type     "target"
                  :position Position.Left}]
      (if show-form?
        [action-form
         {:id           id
          :status       status
          :action-name  action-name
          :action-type  action-type
          :action-value action-value
          :set-nodes    setNodes
          :get-nodes    getNodes}]

        [:<>
         [:b action-name]
         [flow-node-actions {:className "flow-node-actions"}
          [node-action-button {:onClick (fn [event]
                                          (let [nodes (getNodes)]
                                            (->> nodes
                                                 (remove (fn [node]
                                                           (= id (j/get node :id))))
                                                 (to-array)
                                                 (setNodes))))}
           [:> TrashCanCross]]
          [node-action-button {:onClick (fn [event]
                                          (let [nodes (getNodes)]
                                            (->> nodes
                                                 (map (fn [node]
                                                        (if (= id (j/get node :id))
                                                          (->> (j/lit {:data {:status "editing"}})
                                                               (deep-merge node))
                                                          node)))
                                                 (to-array)
                                                 (setNodes))))}
           [:> Cog]]]])

      [:> Handle {:type     "source"
                  :position Position.Right}]])))


(def node-types
  #js {:stream stream-node
       :action action-node})


(defglobalstyle node-styles
  {".react-flow__node-stream, .react-flow__node-action"
   {:min-width        "120px"
    :padding          "8px 10px"
    :background-color "#e9e9e9"
    :box-shadow       "0 4px 6px -1px rgb(0 0 0 / 15%), 0 2px 4px -1px rgb(0 0 0 / 8%)"
    :border-radius    "2px"
    :text-align       "center"
    :font-size        "12px"
    :font-family      "'Splunk Platform Sans','Splunk Data Sans',Roboto,Droid,'Helvetica Neue',Helvetica,Arial,sans-serif"}
   ".react-flow__node-stream.selected, .react-flow__node-action.selected"
   {:outline "1px solid #2c2c2c"}
   ".react-flow__node-stream.selected .flow-node-actions, .react-flow__node-action.selected .flow-node-actions"
   {:display "flex"}})


(defstyled context-menu-node :div
  {:position "absolute"
   :top      #(str (j/get % :top) "px")
   :left     #(str (j/get % :left) "px")
   :right    #(str (j/get % :right) "px")
   :bottom   #(str (j/get % :bottom) "px")
   :z-index  10})


(defn new-stream [position]
  (j/lit {:id       (str (random-uuid))
          :type     "stream"
          :data     {:status "new"}
          :position position}))


(defn new-action [position]
  (j/lit {:id       (str (random-uuid))
          :type     "action"
          :data     {:status "new"}
          :position position}))


(defn context-menu [{:keys [position close-menu]}]
  (j/let [^:js {:keys [addNodes project getZoom]} (useReactFlow)
          {:keys [client-x client-y]} position
          zoom          (getZoom)
          ;; 60 and 20 are the width and height of the node
          ;; multiplying by the zoom value is required to set the node center in the specified point
          node-position (project #js {:x (- client-x (* 60 zoom))
                                      :y (- client-y (* 20 zoom))})]
    [context-menu-node position
     [:> Menu
      [:> Item {:onClick (fn []
                           (-> (new-stream node-position)
                               (addNodes))
                           (close-menu))}
       "Create new stream"]
      [:> Item {:onClick (fn []
                           (-> (new-action node-position)
                               (addNodes))
                           (close-menu))}
       "Create new action"]]]))


(defn flow-renderer [{:keys [nodes edges]}]
  (let [nodes               (r/atom (clj->js nodes))
        edges               (r/atom (clj->js edges))
        flow-ref            (react/createRef)
        menu-position       (r/atom nil)
        close-menu          #(reset! menu-position nil)
        open-menu           (fn [event]
                              (j/call event :preventDefault)
                              (j/let [^:js {:keys [width height x y]} (j/call-in flow-ref [:current :getBoundingClientRect])
                                      ^:js {:keys [clientX clientY]} event]
                                (reset! menu-position
                                        {:top      (and (< clientY (- height 200)) clientY)
                                         :left     (and (< clientX (- width 200)) clientX)
                                         :right    (and (>= clientX (- width 200)) (- width clientX))
                                         :bottom   (and (>= clientY (- height 200)) (- height clientY))
                                         :client-x (- clientX x)
                                         :client-y (- clientY y)})))
        apply-nodes-changes #(reset! nodes (applyNodeChanges % @nodes))
        apply-edges-changes #(reset! edges (applyEdgeChanges % @edges))
        add-edge-changes    #(reset! edges (addEdge (j/assoc! % :animated true) @edges))]
    (fn [{:keys [width height]
          :or   {width "100%" height 800}}]
      [:<>
       [node-styles]
       [:div {:style {:width width :height height}}
        [:> ReactFlow {:ref               flow-ref
                       :nodes             @nodes
                       :nodeTypes         node-types
                       :onNodesChange     apply-nodes-changes
                       :edges             @edges
                       :onEdgesChange     apply-edges-changes
                       :onConnect         add-edge-changes
                       :fitView           true
                       :maxZoom           1.4
                       :onPaneClick       close-menu
                       :onPaneContextMenu open-menu
                       :proOptions        {:account         "paid-custom"
                                           :hideAttribution true}}
         [:> Background]
         (when @menu-position
           [:f> context-menu {:position   @menu-position
                              :close-menu close-menu}])]]])))


(defn flow [props]
  [:> SplunkThemeProvider {:density "compact"}
   [flow-renderer props]])