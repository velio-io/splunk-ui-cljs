(ns splunk-ui-cljs.flow
  (:require
   [applied-science.js-interop :as j]
   ["react" :as react]
   [clojure.string :as string]
   [reagent.core :as r]
   [reagent.ratom :as ratom]
   [cljs-styled-components.reagent :refer-macros [defstyled defglobalstyle]]
   ["react-flow-renderer" :default ReactFlow
    :refer [Background Handle Position ReactFlowProvider BezierEdge getSimpleBezierPath
            applyNodeChanges applyEdgeChanges addEdge getIncomers getOutgoers useReactFlow]]
   ["@splunk/react-ui/Menu" :default Menu :refer [Item]]
   ["@splunk/react-ui/DefinitionList" :default DL :refer [Term Description]]
   ["@splunk/react-ui/Popover" :default Popover]
   ["@splunk/themes" :refer [variables SplunkThemeProvider]]
   ["@splunk/themes/getTheme" :default get-theme]
   ["@splunk/react-icons/Cross" :default Cross]
   ["@splunk/react-icons/CrossCircle" :default CrossCircle]
   ["@splunk/react-icons/PlusCircle" :default PlusCircle]
   ["@splunk/react-icons/TrashCanCross" :default TrashCanCross]
   ["@splunk/react-icons/Cog" :default Cog]
   ["@splunk/react-icons/FloppyDisk" :default FloppyDisk]
   ["@splunk/react-icons/QuestionCircle" :default QuestionCircle]
   ["elkjs/lib/elk.bundled.js" :default ELK]
   [splunk-ui-cljs.dropdown :as dropdown]
   [splunk-ui-cljs.input-text :as inputs]
   [splunk-ui-cljs.code :as code]
   [splunk-ui-cljs.label :as label]
   [splunk-ui-cljs.button :as button]
   [splunk-ui-cljs.utils :as utils]
   [splunk-ui-cljs.undo-redo :as ur]
   [vsf.action-metadata]
   [vsf.action]))


(def vsf-actions
  (ns-publics 'vsf.action))


(def actions-choices
  (->> (vals vsf.action-metadata/actions-controls)
       (sort-by :label)))


(defn is-object
  "Check if the value is an JS object"
  [value]
  (and (= (type value) js/Object)
       (not (nil? value))
       (not (j/call js/Array :isArray value))))


(defn deep-merge
  "Deep merge two JS objects recursively.
   If the value is an object, merge it recursively."
  [object overrides]
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


(defn stream-form
  "Form for editing stream node."
  [{:keys [stream-name]}]
  (let [*stream-state      (r/atom {:name  stream-name
                                    :error nil})
        change-stream-name #(swap! *stream-state assoc :name % :error nil)]
    (fn [{:keys [id status on-stream-save set-nodes get-nodes]}]
      [:form
       {:on-submit
        (fn [event]
          (j/call event :preventDefault)
          (let [{stream-name :name} @*stream-state
                stream-name (some-> stream-name string/trim)]
            ;; validate stream name
            (if (empty? stream-name)
              (swap! *stream-state assoc :error "Can't be empty")

              (let [nodes (get-nodes)]
                (->> nodes
                     (map (fn [node]
                            (if (= id (j/get node :id))
                              ;; update the stream node
                              (let [stream-data  (j/lit {:data {:status nil :stream-name stream-name}})
                                    updated-node (deep-merge node stream-data)]
                                (when (fn? on-stream-save)
                                  (on-stream-save updated-node))
                                updated-node)
                              node)))
                     (to-array)
                     (set-nodes))))))}
       (let [{stream-name :name} @*stream-state
             stream-name-error (:error @*stream-state)]
         [label/label
          {:label       "Stream name"
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
        [node-action-button
         {:type    "button"
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


(defn stream-node
  "Stream node component."
  [props]
  (j/let [^:js {{:keys [stream-name status on-save on-delete]} :data id :id} props
          ^:js {:keys [setNodes getNodes]} (useReactFlow)
          show-form? (or (= status "new") (= status "editing"))]
    (r/as-element
     [:div
      (if show-form?
        [stream-form
         {:id             id
          :stream-name    stream-name
          :status         status
          :on-stream-save on-save
          :set-nodes      setNodes
          :get-nodes      getNodes}]

        [:<>
         [:b stream-name]
         [flow-node-actions {:className "flow-node-actions"}
          [node-action-button {:type    "button"
                               :onClick (fn [event]
                                          (let [nodes (getNodes)]
                                            (->> nodes
                                                 (remove (fn [node]
                                                           (when (= id (j/get node :id))
                                                             (when (fn? on-delete)
                                                               (on-delete node))
                                                             true)))
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


(defstyled control-error-message :div
  {:color      (j/get variables :statusColorHigh)
   :text-align "left"
   :margin-top "6px"
   :max-width  "280px"})


(defn code-control
  "Action control for editing code."
  [{:keys [state]}]
  ;; initialize state value
  (let [value (:value @state)]
    (when (nil? value)
      (swap! state assoc :value ""))
    ;; render function
    (fn [{:keys [state]}]
      (let [{:keys [value errors]} @state]
        [:div
         [code-container
          [code/code {:model     value
                      :on-update #(swap! state assoc :value %)}]]

         (when (some? (:value errors))
           [control-error-message
            (first (:value errors))])]))))


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


(defn strings-control
  "Action control for editing a simple string input."
  [{:keys [state]}]
  ;; initialize state value
  (let [value (:value @state)]
    (when (nil? value)
      (swap! state assoc :value "")))
  ;; render function
  (fn [{:keys [id control-params state]}]
    (let [field-type      (:type control-params)
          formatter       (get field-type-formatters field-type identity)
          input-component (if (= field-type :number)
                            inputs/input-number
                            inputs/input-text)
          {:keys [value errors]} @state]
      [:<>
       ^{:key (str id "-" field-type)}
       [input-component
        {:model           value
         :status          (when (some? (:value errors)) "error")
         :on-change       #(swap! state assoc :value (formatter %))
         :change-on-blur? false}]
       (when (some? (:value errors))
         [control-error-message
          (first (:value errors))])])))


(defn map-control-input
  "Single key-value pair input for rendering inside map-control."
  [{:keys [value error on-change field-type field-label]}]
  (let [formatter       (get field-type-formatters field-type identity)
        input-component (if (= field-type :number)
                          inputs/input-number
                          inputs/input-text)]
    [label/label
     {:label       field-label
      :label-width 100
      :status      (when (some? error) "error")
      :help        (when (some? error) error)}
     [input-component
      {:model           value
       :on-change       #(on-change (formatter %))
       :change-on-blur? false}]]))


(defn map-control
  "Action control for editing a map of key-value pairs (keys are predefined)."
  [{:keys [control-params state]}]
  ;; initialize state value
  (let [value (:value @state)]
    (when (nil? value)
      (->> (:fields control-params)
           (into {} (map #(vector (:field %) nil)))
           (swap! state assoc :value))))
  ;; render function
  (fn [{:keys [id control-params state]}]
    (let [{:keys [value errors]} @state]
      (into [:<>]
        (for [{field-label :label field :field field-type :type} (:fields control-params)
              :let [field-value (get value field)
                    field-error (get-in errors [:value field 0])]]
          ^{:key (str id "-" field "-" field-label)}
          [map-control-input
           {:value       field-value
            :error       field-error
            :on-change   #(swap! state assoc-in [:value field] %)
            :field-type  field-type
            :field-label field-label}])))))


(defn key-value-pairs-form
  "Component for rendering entered key-value pairs."
  [{:keys [state]}]
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


(defn key-vals-control
  "Action control for editing a map of key-value pairs (with inputs for keys)."
  [{:keys [state]}]
  ;; initialize state value
  (let [pair-key            (r/atom "")
        pair-value          (r/atom "")
        key-pair-populated? (ratom/reaction (not (every? not-empty [@pair-key @pair-value])))
        value               (:value @state)]
    (when (nil? value)
      (swap! state assoc :value {}))
    ;; render function
    (fn [{:keys [control-params state]}]
      (let [pairs-number (:pairs-number control-params)
            pairs-count  (count (:value @state))]
        [:div
         [key-value-pairs-form
          {:state state}]

         (when (or (nil? pairs-number)
                   (and (some? pairs-number)
                        (> pairs-number pairs-count)))
           [:div {:style {:display "flex" :gap "4px"}}
            ^{:key (str "pair-key-" pairs-count)}
            [inputs/input-text
             {:placeholder     "Key"
              :model           pair-key
              :on-change       #(reset! pair-key %)
              :change-on-blur? false}]
            ^{:key (str "pair-value-" pairs-count)}
            [inputs/input-text
             {:placeholder     "Value"
              :model           pair-value
              :on-change       #(reset! pair-value %)
              :change-on-blur? false}]
            [button/button {:label      "Add"
                            :appearance "toggle"
                            :disabled?  key-pair-populated?
                            :on-click   #(do (swap! state assoc-in [:value @pair-key] @pair-value)
                                             (reset! pair-key "")
                                             (reset! pair-value ""))}]])]))))


(defn validate-action-params
  "Validate action parameters before saving."
  [action-type action-value]
  (try
    (let [params-format-fn (get-in vsf.action-metadata/actions-controls
                                   [action-type :control-params :format] identity)
          formatted-value  (params-format-fn action-value)
          formatted-value  (if (list? formatted-value)
                             formatted-value ;; multiple positional parameters
                             [formatted-value])
          action-fn        (get vsf-actions (symbol action-type))]
      (apply action-fn formatted-value)
      ;; return nil if no errors
      nil)
    (catch js/Error ex
      (-> (j/get ex :cause)
          :explain-data
          first))))


(defstyled hint-icon :span
  {:color   (j/get variables :contentColorDisabled)
   ":hover" {:cursor "pointer"
             :color  (j/get variables :contentColorMuted)}})


(defn action-hint [{:keys [action-type]}]
  (let [[hint-open? set-hint-open] (react/useState false)
        [anchor set-anchor] (react/useState)
        action-hint (react/useMemo
                     #(get-in vsf.action-metadata/actions-controls [action-type :doc])
                     #js [action-type])
        anchor-ref  (utils/use-callback #(set-anchor %))
        open-hint   (utils/use-callback #(set-hint-open true))
        close-hint  (utils/use-callback #(set-hint-open false))]
    (when action-hint
      [:div
       [hint-icon {:ref          anchor-ref
                   :onMouseEnter open-hint
                   :onMouseLeave close-hint}
        [:> QuestionCircle]]
       [:> Popover {:open           hint-open?
                    :anchor         anchor
                    :onRequestClose close-hint}
        [:div {:style {:padding "10px"}}
         action-hint]]])))


(defstyled action-type-container :div
  {:display       "flex"
   :align-items   "center"
   :margin-bottom "12px"})


(defstyled action-type-label :span
  {:font-size "14px"
   :color     #(if (j/get % :error)
                 (j/get variables :statusColorHigh)
                 (j/get variables :contentColorMuted))})


(defstyled action-type-hint :div
  {:flex-grow 1})


(defstyled action-type-control :div
  {:flex "0 0 173px"})


(defstyled action-type-error-message :div
  {:color      (j/get variables :statusColorHigh)
   :text-align "left"
   :margin-top "6px"})


(defn action-form
  "Form for editing action node."
  [{:keys [action-name action-type action-value]}]
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
    (fn [{:keys [id status on-action-save set-nodes get-nodes]}]
      (let [{action-name :name action-type :type :keys [errors]} @*action-state]
        [:form
         {:style {:margin-bottom "12px"}
          :on-submit
          (fn [event]
            (j/call event :preventDefault)
            (let [{action-name :name action-type :type action-value :value} @*action-state
                  action-name      (some-> action-name string/trim)
                  params-format-fn (get-in vsf.action-metadata/actions-controls
                                           [action-type :control-params :format] identity)
                  value-error      (delay (validate-action-params action-type action-value))]
              (cond
                (empty? action-name)
                (swap! *action-state assoc-in [:errors :name] "Can't be empty")

                (empty? action-type)
                (swap! *action-state assoc-in [:errors :type] "Can't be empty")

                (some? @value-error)
                (swap! *action-state assoc-in [:errors :value] @value-error)

                (and (nil? @value-error) (some? (:value errors)))
                (swap! *action-state assoc-in [:errors :value] nil)

                :otherwise
                (let [nodes (get-nodes)]
                  (->> nodes
                       (map (fn [node]
                              (if (= id (j/get node :id))
                                (let [node-data    (j/lit {:data {:status        nil
                                                                  :action-name   action-name
                                                                  :action-type   action-type
                                                                  :action-value  action-value
                                                                  :action-params params-format-fn}})
                                      updated-node (deep-merge node node-data)]
                                  (when (fn? on-action-save)
                                    (on-action-save updated-node))
                                  updated-node)
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

         (let [action-type-error (:type errors)
               has-error?        (some? action-type-error)]
           [action-type-container
            [action-type-label {:error has-error?}
             "Action type"]

            [action-type-hint
             [:f> action-hint
              {:action-type action-type}]]

            [action-type-control
             [dropdown/single-dropdown
              {:on-change change-action-type
               :model     action-type
               :inline    false
               :status    (when has-error? "error")
               :choices   actions-choices}]
             (when action-type-error
               [action-type-error-message
                action-type-error])]])

         (when (some? action-type)
           (let [{:keys [control-type] :as action-props}
                 (-> (get vsf.action-metadata/actions-controls action-type)
                     (assoc :state *action-state))]
             (case control-type
               :code [code-control action-props]
               :input [strings-control action-props]
               :map [map-control action-props]
               :key-vals [key-vals-control action-props]
               nil)))

         [:div {:style {:position "absolute"
                        :right    0
                        :bottom   -30}}
          [node-action-button
           {:type    "button"
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


(defn action-node
  "Action node component."
  [props]
  (j/let [^:js {{:keys [action-name action-type action-value status on-save on-delete]} :data id :id} props
          ^:js {:keys [setNodes getNodes]} (useReactFlow)
          show-form? (or (= status "new") (= status "editing"))
          leaf?      (get-in vsf.action-metadata/actions-controls [action-type :leaf-action])]
    (r/as-element
     [:div
      [:> Handle {:type     "target"
                  :position Position.Left}]
      (if show-form?
        [action-form
         {:id             id
          :status         status
          :action-name    action-name
          :action-type    action-type
          :action-value   (js->clj action-value :keywordize-keys true)
          :on-action-save on-save
          :set-nodes      setNodes
          :get-nodes      getNodes}]

        [:<>
         [:b action-name]
         [flow-node-actions {:className "flow-node-actions"}
          [node-action-button {:onClick (fn [event]
                                          (let [nodes (getNodes)]
                                            (->> nodes
                                                 (remove (fn [node]
                                                           (when (= id (j/get node :id))
                                                             (when (fn? on-delete)
                                                               (on-delete node))
                                                             true)))
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

      (when-not leaf?
        [:> Handle {:type     "source"
                    :position Position.Right}])])))


(def node-types
  #js {:stream stream-node
       :action action-node})


(defn interactive-edge
  "Custom edge component. Adds a wider transparent line for easier interaction."
  [props]
  (j/let [^:js {:keys [sourceX sourceY targetX targetY style
                       sourcePosition targetPosition markerEnd]} props
          path (getSimpleBezierPath #js {:sourceX        sourceX
                                         :sourceY        sourceY
                                         :sourcePosition sourcePosition
                                         :targetX        targetX
                                         :targetY        targetY
                                         :targetPosition targetPosition})]
    (r/as-element
     [:<>
      [:> BezierEdge
       {:sourceX        sourceX
        :sourceY        sourceY
        :targetX        targetX
        :targetY        targetY
        :sourcePosition sourcePosition
        :targetPosition targetPosition
        :style          style
        :markerEnd      markerEnd}]
      [:path
       {:d             path
        :fill          "none"
        :strokeOpacity 0
        :strokeWidth   16
        :className     "react-flow__edge-interaction"}]])))


(def edge-types
  #js {:interactive interactive-edge})


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
   {:display "flex"}
   ".react-flow__edge-interaction"
   {:cursor "pointer"}})


(defstyled context-menu-node :div
  {:position "absolute"
   :top      #(str (j/get % :top) "px")
   :left     #(str (j/get % :left) "px")
   :right    #(str (j/get % :right) "px")
   :bottom   #(str (j/get % :bottom) "px")
   :z-index  10})


(defn new-stream
  "Create a new stream node."
  [position]
  (j/lit {:id       (str (random-uuid))
          :type     "stream"
          :data     {:status "new"}
          :position position}))


(defn new-action
  "Create a new action node."
  [position]
  (j/lit {:id       (str (random-uuid))
          :type     "action"
          :data     {:status "new"}
          :position position}))


(defn context-menu
  "Component for rendering a context menu."
  [{:keys [position close-menu layout]}]
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


(defn layout-nodes
  "Layout nodes using ELK algorithm."
  [{:keys [nodes edges]}]
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


(defn apply-history-change
  "Apply history change to the flow data.
   Reverse the change if reverse? is true.
   Update the flow data with the new nodes and edges."
  [{:keys [type value]} reverse? *flow-data]
  (let [add    #js [#js {:item value :type "add"}]
        remove #js [#js {:id (j/get value :id) :type "remove"}]]
    (case type
      :add-node
      (swap! *flow-data (fn [{:keys [nodes] :as data}]
                          (->> (applyNodeChanges (if reverse? remove add) nodes)
                               (assoc data :nodes))))
      :remove-node
      (swap! *flow-data (fn [{:keys [nodes] :as data}]
                          (->> (applyNodeChanges (if reverse? add remove) nodes)
                               (assoc data :nodes))))
      :add-edge
      (swap! *flow-data (fn [{:keys [edges] :as data}]
                          (->> (applyEdgeChanges (if reverse? remove add) edges)
                               (assoc data :edges))))
      :remove-edge
      (swap! *flow-data (fn [{:keys [edges] :as data}]
                          (->> (applyEdgeChanges (if reverse? add remove) edges)
                               (assoc data :edges))))
      :do-nothing)))


(defn flow-renderer
  "Main flow renderer component.
   Renders the flow graph with nodes and edges."
  [{:keys [on-update nodes edges width height]
    :or   {width "100%" height 800}}]
  (r/with-let [*flow-data              (r/atom {})
               flow-history            (atom (ur/new-cache))

               on-node-save-handler    (fn [new-node]
                                         (swap! flow-history ur/push {:type :add-node :value new-node})
                                         (let [new-node-id (j/get new-node :id)
                                               {:keys [nodes edges]} @*flow-data]
                                           ;; ensure that latest updates presented in the state
                                           (on-update {:nodes (->> nodes
                                                                   (map (fn [node]
                                                                          (if (= new-node-id (j/get node :id))
                                                                            (deep-merge node new-node)
                                                                            node)))
                                                                   (to-array))
                                                       :edges edges})))

               on-node-delete-handler  (fn [node]
                                         (swap! flow-history ur/push {:type :remove-node :value node})
                                         (on-update @*flow-data))

               _                       (reset! *flow-data {:nodes (->> (or nodes [])
                                                                       (map #(assoc-in % [:data :on-delete] on-node-delete-handler))
                                                                       clj->js)
                                                           :edges (clj->js (or edges []))})

               track-history-shortcuts (fn [event]
                                         (let [cache @flow-history]
                                           (when (j/get event :ctrlKey)
                                             (case (j/get event :key)
                                               "Z" (when (ur/has-next? cache)
                                                     (let [cache'       (ur/redo cache)
                                                           history-item (ur/current cache')]
                                                       (apply-history-change history-item false *flow-data)
                                                       (reset! flow-history cache')))
                                               "z" (when-let [history-item (ur/current cache)]
                                                     (apply-history-change history-item true *flow-data)
                                                     (swap! flow-history ur/undo))
                                               nil))))
               _                       (j/call js/window :addEventListener "keydown" track-history-shortcuts)

               flow-ref                (react/createRef)
               menu-position           (r/atom nil)
               close-menu              #(reset! menu-position nil)

               open-menu               (fn [event]
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

               apply-nodes-changes     (fn [updates]
                                         (let [updates-types (into #{} (map #(j/get % :type)) updates)
                                               {:keys [nodes]} @*flow-data
                                               updated-nodes (cond-> updates
                                                                     (contains? updates-types "add")
                                                                     (j/assoc-in! [0 :item :data :on-save] on-node-save-handler)
                                                                     :always (j/assoc-in! [0 :item :data :on-delete] on-node-delete-handler)
                                                                     :always (applyNodeChanges nodes))]
                                           (swap! *flow-data assoc :nodes updated-nodes)
                                           (when (contains? updates-types "remove")
                                             (let [node-id (j/get (first updates) :id)]
                                               (swap! flow-history ur/push {:type  :remove-node
                                                                            :value (utils/find-by "id" node-id nodes)})
                                               (on-update @*flow-data)))))

               apply-edges-changes     (fn [updates]
                                         (let [updates-types (into #{} (map #(j/get % :type)) updates)
                                               {:keys [edges]} @*flow-data]
                                           (swap! *flow-data (fn [{:keys [edges] :as data}]
                                                               (->> (applyEdgeChanges updates edges)
                                                                    (assoc data :edges))))
                                           (when (contains? updates-types "remove")
                                             (doseq [update updates]
                                               (swap! flow-history ur/push {:type  :remove-edge
                                                                            :value (utils/find-by "id" (j/get update :id) edges)}))
                                             (on-update @*flow-data))))

               connect-source          (atom nil)

               on-new-edge             (fn [new-edge]
                                         (let [{:keys [nodes edges]} @*flow-data
                                               target-node-id      (j/get new-edge :target)
                                               source-node-id      (j/get new-edge :source)
                                               new-edge            (j/assoc! new-edge :type "interactive"
                                                                             :id (str source-node-id "-" target-node-id))
                                               target-node         (->> nodes
                                                                        (filter (fn [node]
                                                                                  (= (j/get node :id) target-node-id)))
                                                                        (first))
                                               target-connections? (getIncomers target-node nodes edges)
                                               defer-updates?      (some? @connect-source)]
                                           (reset! connect-source nil)
                                           (when (empty? target-connections?)
                                             (swap! *flow-data (fn [{:keys [edges] :as data}]
                                                                 (->> (addEdge new-edge edges)
                                                                      (assoc data :edges))))
                                             (swap! flow-history ur/push {:type  :add-edge
                                                                          :value new-edge})
                                             (when (not defer-updates?)
                                               (on-update @*flow-data)))))

               on-connect-start        (fn [_event params]
                                         (let [node-id (j/get params :nodeId)]
                                           (reset! connect-source node-id)))

               ;; workaround for referencing the latest fitView function
               *fit-fn                 (atom nil)]
    (j/let [{:keys [nodes edges]} @*flow-data
            ^:js {:keys [fitView project getZoom addNodes]} (useReactFlow)
            _              (reset! *fit-fn fitView)

            layout         (utils/use-callback
                            #(let [{:keys [nodes edges]} @*flow-data]
                               (-> (layout-nodes {:nodes nodes :edges edges})
                                   (j/call :then
                                           (fn [graph]
                                             (reset! *flow-data graph)
                                             ;; fit the view after the layout updating is done
                                             (js/setTimeout @*fit-fn 10))))))

            on-connect-end (utils/use-callback
                            (fn [event]
                              (when-some [node-id @connect-source]
                                (when (j/call-in event [:target :classList :contains] "react-flow__pane")
                                  (j/let [^:js {:keys [x y]} (j/call-in flow-ref [:current :getBoundingClientRect])
                                          ^:js {:keys [clientX clientY]} event
                                          zoom          (getZoom)
                                          node-position (project
                                                         #js {:x (- (- clientX x) (* 60 zoom))
                                                              :y (- (- clientY y) (* 20 zoom))})
                                          new-node      (new-action node-position)
                                          new-edge      #js {:source node-id
                                                             :target (j/get new-node :id)}]
                                    (addNodes new-node)
                                    (on-new-edge new-edge)))))
                            [project])]

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
                       :edgeTypes         edge-types
                       :onEdgesChange     apply-edges-changes
                       :onConnect         on-new-edge
                       :onConnectStart    on-connect-start
                       :onConnectEnd      on-connect-end
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

    (finally
     (j/call js/window :removeEventListener "keydown" track-history-shortcuts))))


(defn action->flow
  "This function will convert a single action map into a nodes and edges map of shape
   ```
   {:nodes [{:id       [random-uuid]
             :type     \"action\"
             :data     {:action-name \"bar\"
                        :action-type \"increment\"
                        :action-value nil}
             :position {:x 0 :y 0}}]
    :edges [{:id       [input-uuid]-[output-uuid]
             :source   [input-uuid]
             :target   [output-uuid]
             :animated true}]}
   ```"
  [{:keys [nodes edges] :as flow}
   root-id
   {:keys       [children]
    action-type :action
    action-name :name
    :as         action}]
  (if (= action-type :sdo)
    ;; sdo action does nothing, just forward events to children
    (reduce
     (fn [acc action]
       (action->flow acc root-id action))
     flow
     children)
    ;; create a new action node
    (let [action-id        (str (random-uuid))
          action-type'     (name action-type)
          params-parse-fn  (get-in vsf.action-metadata/actions-controls [action-type' :control-params :parse])
          params-format-fn (get-in vsf.action-metadata/actions-controls [action-type' :control-params :format] identity)
          nodes            (conj nodes
                                 {:id       action-id
                                  :type     "action"
                                  :data     {:action-name   action-name
                                             :action-type   action-type'
                                             :action-value  (when (some? params-parse-fn)
                                                              (params-parse-fn action))
                                             :action-params params-format-fn}
                                  :position {:x 0 :y 0}})
          edges            (conj edges
                                 {:id     (str root-id "-" action-id)
                                  :type   "interactive"
                                  :source root-id
                                  :target action-id})
          flow'            {:nodes nodes :edges edges}]
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


(defn flow->action
  "Convert a flow node into an action map."
  [{:keys [nodes edges] :as flow} node]
  (j/let [^:js {{:keys [action-name action-type action-value action-params]} :data} node
          children  (getOutgoers node nodes edges)
          action-fn (get vsf-actions (symbol action-type))]
    (let [actions (map #(flow->action flow %) children)
          actions (if (some? action-value)
                    (let [formatted-value (-> action-value (js->clj :keywordize-keys true) action-params)
                          formatted-value (if (list? formatted-value)
                                            formatted-value ;; multiple positional parameters
                                            [formatted-value])]
                      (concat formatted-value actions))
                    actions)]
      (-> (apply action-fn actions)
          (assoc :name action-name)))))


(defn flow->streams
  "Convert a react-flow graph (flat list) into a streams map with actions (tree)."
  [{:keys [nodes edges] :as flow}]
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


(defn flow
  "Flow graph component."
  [{:keys [model]}]
  (let [streams (utils/model->value model)
        {:keys [nodes edges]} (streams->flow streams)]
    (fn [{:keys [model on-change]}]
      [:> SplunkThemeProvider {:density "compact"}
       [:> ReactFlowProvider
        [:f> flow-renderer
         {:nodes     nodes
          :edges     edges
          :on-update (fn [flow-data]
                       (cond
                         (fn? on-change) (on-change (flow->streams flow-data))
                         (utils/atom? model) (reset! model (flow->streams flow-data))))}]]])))
