(ns splunk-ui-cljs.flow
  (:require
   [applied-science.js-interop :as j]
   ["react" :as react]
   [reagent.core :as r]
   [cljs-styled-components.reagent :refer-macros [defstyled defglobalstyle]]
   ["react-flow-renderer" :default ReactFlow
    :refer [Background applyNodeChanges applyEdgeChanges addEdge Handle Position useReactFlow]]
   ["@splunk/react-ui/Menu" :default Menu :refer [Item]]))


(defmulti action-control :type)


(defmethod action-control :input [props]
  [:input#text {:name "text" :className "nodrag"}])


(defmethod action-control :default [props]
  (js/console.error "Unknown stream-control type"))


(defn stream-node [props]
  (let [stream-name (j/get-in props [:data :stream-name])]
    (r/as-element
     [:div
      [:b stream-name]
      [:> Handle {:type     "source"
                  :position Position.Right}]])))


(defn action-node [props]
  (j/let [^:js {:keys [action-name controls]} (j/get props :data)]
    (r/as-element
     [:div
      [:> Handle {:type     "target"
                  :position Position.Left}]
      [:b action-name]

      (into [:div]
            (for [control controls]
              [action-control control]))

      [:> Handle {:type     "source"
                  :position Position.Right}]])))


(def node-types
  #js {:stream stream-node
       :action action-node})


(defglobalstyle
 node-styles
 {".react-flow__node-stream, .react-flow__node-action"
  {:min-width        "120px"
   :padding          "8px 10px"
   :background-color "#e9e9e9"
   :box-shadow       "0 4px 6px -1px rgb(0 0 0 / 15%), 0 2px 4px -1px rgb(0 0 0 / 8%)"
   :border-radius    "2px"
   :text-align       "center"
   :font-size        "12px"
   :font-family      "'Splunk Platform Sans','Splunk Data Sans',Roboto,Droid,'Helvetica Neue',Helvetica,Arial,sans-serif"}})


(defstyled context-menu-node :div
  {:position "absolute"
   :top      #(str (j/get % :top) "px")
   :left     #(str (j/get % :left) "px")
   :right    #(str (j/get % :right) "px")
   :bottom   #(str (j/get % :bottom) "px")
   :z-index  10})


(defn context-menu [{:keys [position close-menu]}]
  (j/let [^:js {:keys [addNodes project]} (useReactFlow)
          {:keys [client-x client-y]} position
          menu-flow-position (project #js {:x client-x :y client-y})
          node-id            (str (random-uuid))
          new-node           {:id       node-id
                              :position menu-flow-position}]
    [context-menu-node position
     [:> Menu
      [:> Item {:onClick (fn []
                           (-> new-node
                               (assoc :type "stream"
                                      :data #js {:stream-name "New stream"})
                               (clj->js)
                               (addNodes))
                           (close-menu))}
       "Create new stream"]
      [:> Item {:onClick (fn []
                           (-> new-node
                               (assoc :type "action"
                                      :data #js {:action-name "New action"})
                               (clj->js)
                               (addNodes))
                           (close-menu))}
       "Create new action"]]]))


(defn flow [{:keys [nodes edges]}]
  (let [nodes               (r/atom (clj->js nodes))
        edges               (r/atom (clj->js edges))
        flow-ref            (react/createRef)
        menu-position       (r/atom nil)
        close-menu          #(reset! menu-position nil)
        open-menu           (fn [event]
                              (j/call event :preventDefault)
                              (j/let [^:js {:keys [width height]} (j/call-in flow-ref [:current :getBoundingClientRect])
                                      ^:js {:keys [clientX clientY]} event]
                                (reset! menu-position
                                        {:top      (and (< clientY (- height 200)) clientY)
                                         :left     (and (< clientX (- width 200)) clientX)
                                         :right    (and (>= clientX (- width 200)) (- width clientX))
                                         :bottom   (and (>= clientY (- height 200)) (- height clientY))
                                         :client-x clientX
                                         :client-y clientY})))
        apply-nodes-changes #(reset! nodes (applyNodeChanges % @nodes))
        apply-edges-changes #(reset! edges (applyEdgeChanges % @edges))
        add-edge-changes    #(reset! edges (addEdge (j/assoc! % :animated true) @edges))]
    (fn [{:keys [width height]
          :or   {width "100%" height 500}}]
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
                       :onPaneClick       close-menu
                       :onPaneContextMenu open-menu
                       :proOptions        {:account         "paid-custom"
                                           :hideAttribution true}}
         [:> Background]
         (when @menu-position
           [:f> context-menu {:position   @menu-position
                              :close-menu close-menu}])]]])))
