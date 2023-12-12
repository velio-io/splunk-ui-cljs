(ns splunk-ui-cljs.flow
  (:require
   [reagent.core :as r]
   ["react-flow-renderer" :default ReactFlow :refer [applyNodeChanges applyEdgeChanges]]))


(def initialNodes
  (clj->js [{:id       "input-node"
             :type     "input"
             :data     {:label "Input"}
             :position {:x 50
                        :y 50}}
            {:id       "output-node"
             :type     "output"
             :data     {:label "Output"}
             :position {:x 150
                        :y 150}}]))


(def initialEdges
  (clj->js [{:id       "1-2"
             :source   "input-node"
             :target   "output-node"
             :animated true}]))


(defn flow []
  (let [nodes (r/atom initialNodes)
        edges (r/atom initialEdges)]
    (fn []
      [:div {:style {:width 500 :height 500}}
       [:> ReactFlow {:nodes         @nodes
                      :edges         @edges
                      :onNodesChange #(reset! nodes (applyNodeChanges % @nodes))
                      :onEdgesChange #(reset! edges (applyEdgeChanges % @edges))
                      :fitView       true
                      :proOptions    {:account         "paid-custom"
                                      :hideAttribution true}}]])))
