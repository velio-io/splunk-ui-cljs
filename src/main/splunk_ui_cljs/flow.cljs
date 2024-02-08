(ns splunk-ui-cljs.flow
  (:require
   [applied-science.js-interop :as j]
   ["react" :as react]
   [clojure.string :as string]
   [reagent.core :as r]
   [cljs-styled-components.reagent :refer-macros [defstyled defglobalstyle]]
   ["react-flow-renderer" :default ReactFlow
    :refer [Background Handle Position ReactFlowProvider
            applyNodeChanges applyEdgeChanges addEdge getOutgoers useReactFlow]]
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
   ["elkjs/lib/elk.bundled.js" :default ELK]
   [splunk-ui-cljs.dropdown :as dropdown]
   [splunk-ui-cljs.input-text :as inputs]
   [splunk-ui-cljs.code :as code]
   [splunk-ui-cljs.label :as label]
   [splunk-ui-cljs.button :as button]
   [splunk-ui-cljs.utils :as utils]
   [vsf.action]))


(def vsf-actions
  (ns-publics 'vsf.action))


(def vsf-action-types
  {"where"              {:type :code}
   "increment"          {:type :no-args}
   "index"              {:type :strings}
   "fixed-event-window" {:type :map :fields [{:field :size :label "Size" :type :number}]}
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


(def field-type-formatters
  {:number  (fn [value]
              (let [n (js/Number value)]
                (if (js/isNaN n)
                  nil
                  n)))

   :boolean (fn [value]
              (if (string/blank? value)
                nil
                (boolean value)))

   :string  (fn [value]
              (if (string/blank? value)
                nil
                value))})


(defn map-control [{:keys [fields state]}]
  (into [:<>]
    (for [{field-label :label field :field field-type :type} fields]
      (let [formatter       (get field-type-formatters field-type identity)
            input-component (case field-type
                              :number inputs/input-number
                              inputs/input-text)]
        [label/label {:label       field-label
                      :label-width 100}
         [input-component
          {:model           (get-in @state [:value field])
           :on-change       #(swap! state assoc-in [:value field] (formatter %))
           :change-on-blur? false}]]))))


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
                 (-> (get vsf-action-types action-type)
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


(defn context-menu [{:keys [position close-menu layout]}]
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
       "Create new action"]
      [:> Item {:onClick (fn []
                           (layout)
                           (close-menu))}
       "Layout nodes"]]]))


(def elk
  (new ELK))


(def elk-options
  {"elk.algorithm"                             "layered"
   "elk.direction"                             "RIGHT"
   "elk.layered.spacing.nodeNodeBetweenLayers" "100"
   "elk.spacing.nodeNode"                      "80"})


(defn layout-nodes [{:keys [nodes edges]}]
  (let [graph {:id            "root"
               :layoutOptions elk-options
               :children      (map (fn [node]
                                     (j/assoc! node :targetPosition "left" :sourcePosition "right"
                                               :width 120 :height 40))
                                   nodes)
               :edges         edges}]
    (-> (j/call elk :layout (clj->js graph))
        (j/call :then
                (fn [graph]
                  {:nodes (->> (j/get graph :children)
                               (map (fn [node]
                                      (j/let [^:js {:keys [x y]} node]
                                        (j/assoc! node :position #js {:x x :y y}))))
                               (to-array))
                   :edges (j/get graph :edges)})))))


(defn flow-renderer [{:keys [on-update nodes edges width height]
                      :or   {width "100%" height 800}}]
  (r/with-let [*flow-data          (r/atom {:nodes (clj->js nodes) :edges (clj->js edges)})
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
               apply-nodes-changes (fn [updates]
                                     (swap! *flow-data (fn [{:keys [nodes] :as data}]
                                                         (->> (applyNodeChanges updates nodes)
                                                              (assoc data :nodes)))))
               apply-edges-changes (fn [updates]
                                     (swap! *flow-data (fn [{:keys [edges] :as data}]
                                                         (->> (applyEdgeChanges updates edges)
                                                              (assoc data :edges)))))
               add-edge-changes    (fn [new-edge]
                                     (swap! *flow-data (fn [{:keys [edges] :as data}]
                                                         (->> (addEdge (j/assoc! new-edge :animated true) edges)
                                                              (assoc data :edges)))))
               change-handler      (r/track! #(when (fn? on-update)
                                                (let [{:keys [nodes edges]} @*flow-data]
                                                  (on-update {:nodes nodes :edges edges}))))
               ;; workaround for referencing the latest fitView function
               *fit-fn             (atom nil)]
    (j/let [{:keys [nodes edges]} @*flow-data
            ^:js {:keys [fitView]} (useReactFlow)
            _      (reset! *fit-fn fitView)
            layout (react/useCallback
                    (fn []
                      (let [{:keys [nodes edges]} @*flow-data]
                        (-> (layout-nodes {:nodes nodes :edges edges})
                            (j/call :then
                                    (fn [graph]
                                      (reset! *flow-data graph)
                                      ;; fit the view after the layout updating is done
                                      (js/setTimeout @*fit-fn 10))))
                        ;; returning undefined is required for react hook to work properly
                        js/undefined))
                    #js [])]

      ;; initial layout
      (react/useLayoutEffect
       #(layout)
       #js [])

      [:<>
       [node-styles]
       [:div {:style {:width width :height height}}
        [:> ReactFlow {:ref               flow-ref
                       :nodes             nodes
                       :nodeTypes         node-types
                       :onNodesChange     apply-nodes-changes
                       :edges             edges
                       :onEdgesChange     apply-edges-changes
                       :onConnect         add-edge-changes
                       :maxZoom           1.4
                       :fitView           true
                       :onPaneClick       close-menu
                       :onPaneContextMenu open-menu
                       :proOptions        {:account         "paid-custom"
                                           :hideAttribution true}}
         [:> Background]
         (when @menu-position
           [:f> context-menu {:position   @menu-position
                              :close-menu close-menu
                              :layout     layout}])]]])

    ;; cleanup the change handler
    (finally
     (r/dispose! change-handler))))


(defn action->flow
  [{:keys [nodes edges] :as flow}
   root-id
   {:keys [params children] action-type :action action-name :name}]
  (if (= action-type :sdo)
    ;; sdo action does nothing, just forward events to children
    (reduce
     (fn [acc action]
       (action->flow acc root-id action))
     flow
     children)
    ;; create a new action node
    (let [action-id     (str (random-uuid))
          params->value (some->> action-type (get vsf-action-types) :params-fn)
          nodes         (conj nodes
                              {:id       action-id
                               :type     "action"
                               :data     {:action-name  action-name
                                          :action-type  (name action-type)
                                          :action-value (when params->value (params->value params))}
                               :position {:x 0 :y 0}})
          edges         (conj edges
                              {:id       (str root-id "-" action-id)
                               :source   root-id
                               :target   action-id
                               :animated true})
          flow'         {:nodes nodes :edges edges}]
      (if (seq children)
        (reduce
         (fn [acc action]
           (action->flow acc action-id action))
         flow'
         children)
        ;; no children, return the flow data
        flow'))))


(defn streams->flow
  "This function will convert a streams map (map where each key describes a separate stream) of shape
   ```
   {:foo {:actions {:action      :sdo
                    :description {:message \"Forward events to children\"}
                    :children    [{:action      :increment
                                   :name        \"bar\"
                                   :description {:message \"Increment the :metric field\"}
                                   :children    nil}]}}}
   ```
   into a nodes and edges map of shape
   ```
   {:nodes [{:id       [random-uuid]
             :type     \"stream\"
             :data     {:stream-name \"foo\"}
             :position {:x 0 :y 0}}
            {:id       [random-uuid]
             :type     \"action\"
             :data     {:action-name \"bar\"
                        :action-type \"increment\"
                        :action-value nil}
             :position {:x 0 :y 0}}]
    :edges [{:id       [input-uuid]-[output-uuid]
             :source   [input-uuid]
             :target   [output-uuid]
             :animated true}]}
   ```
  "
  [streams]
  (->>
   (for [[stream-name {:keys [actions]}] streams]
     (let [stream-id   (str (random-uuid))
           stream-node {:id       stream-id
                        :type     "stream"
                        :data     {:stream-name (name stream-name)}
                        :position {:x 0 :y 0}}
           flow        {:nodes [stream-node]
                        :edges []}]
       (action->flow flow stream-id actions)))
   (apply merge-with into)))


(defn flow->action [{:keys [nodes edges] :as flow} node]
  (j/let [^:js {{:keys [action-name action-type action-value]} :data} node
          children  (getOutgoers node nodes edges)
          action-fn (get vsf-actions (symbol action-type))]
    (let [actions (map #(flow->action flow %) children)
          actions (if (some? action-value)
                    (concat [action-value] actions)
                    actions)]
      ;; @TODO save action name
      (apply action-fn actions))))


(defn flow->streams [{:keys [nodes edges] :as flow}]
  (let [stream-nodes (filter (fn [node]
                               (= "stream" (j/get node :type)))
                             nodes)]
    (->> stream-nodes
         (map (fn [stream-node]
                (j/let [^:js {{:keys [stream-name]} :data} stream-node
                        children (getOutgoers stream-node nodes edges)]
                  (->> children
                       (map #(flow->action flow %))
                       (apply vsf.action/stream {:name stream-name})))))
         (apply vsf.action/streams))))


(defn flow [{:keys [model]}]
  (let [streams (utils/model->value model)
        {:keys [nodes edges]} (streams->flow streams)]
    (fn [{:keys [model]}]
      [:> SplunkThemeProvider {:density "compact"}
       [:> ReactFlowProvider
        [:f> flow-renderer {:nodes     nodes
                            :edges     edges
                            ;; @TODO limit the number of on-update calls
                            :on-update #(do (swap! model (flow->streams %))
                                            (prn (flow->streams %)))}]]])))