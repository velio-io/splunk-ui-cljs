(ns splunk-ui-cljs.layout
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/ColumnLayout" :default ColumnLayout :refer [Column Row]]))


(def column-layout
  (r/adapt-react-class ColumnLayout))


(def row
  (r/adapt-react-class Row))


(def column
  (r/adapt-react-class Column))


(defn layout
  "layout provides simple API for laying out content into columns.
   It is based on flexbox and has 12 columns.
   - `divider` (optional) Show dividers between columns.
   - `gap` (optional) Set gutter width in pixels. This is propagated down to its children."
  [{:keys [divider gap]}]
  (let [component (r/current-component)]
    (into
     [column-layout (utils/assoc-some {}
                      :divider divider
                      :gutter gap)]
     (r/children component))))


(defn h-box
  "Represents a single row in the layout context.
   Accepts a number of columns as children (could be an actual `column` component or any other component).
   Should be a direct child of layout component
   - `align-items` (optional) Set vertical alignment of columns in a row."
  [{:keys [align-items]}]
  (let [component (r/current-component)
        {:keys [gutter divider isFirstChild isLastChild]} (utils/component-props component)
        row-props (utils/assoc-some
                    {:gutter       gutter
                     :isFirstChild isFirstChild
                     :isLastChild  isLastChild}
                    :divider divider
                    :alignItems align-items)]
    (into
     [row row-props]
     (map (fn [child]
            (if (vector? child)
              (if (= column (first child))
                child
                ;; wrap child elements in column component
                [column child])
              child)))
     (r/children component))))


(defn v-box
  "Represents a single column in the layout context.
   Designed to wrap a number of rows inside a single column.
   Should be a direct child of layout component"
  [_props]
  (let [component (r/current-component)
        {:keys [gutter divider isFirstChild isLastChild]} (utils/component-props component)
        row-props (utils/assoc-some
                    {:gutter       gutter
                     :isFirstChild isFirstChild
                     :isLastChild  isLastChild}
                    :divider divider)]
    ;; we have to wrap v-box in the row in order to support the layout context properties
    [row row-props
     [column
      (into
       ;; nested layout required to isolate column in its own context
       [layout]
       (map #(vector h-box %))
       (r/children component))]]))
