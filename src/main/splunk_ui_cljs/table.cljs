(ns splunk-ui-cljs.table
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   [goog.object :as go]
   ["@splunk/react-ui/Menu" :default Menu :refer [Item]]
   ["@splunk/react-ui/Table" :default Table :refer [Head HeadCell HeadDropdownCell Body Row Cell]]))


(def row
  (r/adapt-react-class Row))


(def cell
  (r/adapt-react-class Cell))


(defn rows-selection [rows]
  (let [selected-rows-num (reduce (fn [total-selected {:keys [selected]}]
                                    (+ total-selected (if selected 1 0)))
                                  0 rows)]
    (cond
      (zero? selected-rows-num) "none"
      (= (count rows) selected-rows-num) "all"
      :otherwise "some")))


(defn table
  "A styled table component.
   - `model` (required) Collection of table rows (vector of maps). One element for each row in the table. Can contain any data with some special keys :selected, :expanded
   - `columns` (required) Collection of table headers (vector of maps). Must contain keys :id, :header-label and optional keys - :sort-key, :align, :menu-items, :width
   - `stripe-rows` (optional) Alternate rows are given a darker background to improve readability.
   - `head-type` (optional) Sets the table head type:
      - docked: The head is docked against the window
      - fixed : The head is fixed in the table. The table can scroll independently from the head.
      - inline: The head isn't fixed, but can scroll with the rest of the table.
   - `inner-style` (optional) Style specification for the inner container, which is the scrolling container.
   - `dock-offset` (optional) Sets the offset from the top of the window. Only applies when headType is 'docked'.
   - `dock-scroll-bar` (optional) Docks the horizontal scroll bar at the bottom of the window when the bottom of the table is below the viewport.
   - `row-expansion` (optional) Adds a column to the table with an expansion button for each row that has expansion content. Supported values:
      - single: Only one row can be expanded at a time. If another expansion button is clicked, the currently expanded row closes and the new one opens.
      - multi: Allows multiple rows to be expanded at the same time.
      - controlled: Allows the expanded state to be externally managed by expanded prop of Row.
      - none: The default with no row expansion.
   - `on-move-row` (optional) An event handler to handle the reorder rows action of Table. The function is passed an options map with from and to indexes.
   - `on-move-column` (optional) An event handler for handle the reorder columns action of Table. The function is passed an options map with from and to indexes.
   - `on-resize-column` (optional) An event handler for resize of columns. The function is passed an event and an options map with column-id, index, and width.
   - `resizable-fill-layout` (optional) Table will fill parent container. Resizable columns can have a width of auto only with this prop enabled.
   - `on-col-menu-click` (optional) In case when a column header have a dropdown with menu items, this function will be called when user clicks on some item. Function will get the item itself as an argument.
   - `sort-key` (optional) The sortKey is passed in the data object to the onSort callback, if provided.
   - `sort-dir` (optional) The current sort direction of this column.
   - `on-sort` (optional) A callback invoked when this head cell is clicked. If provided, this HeadCell is sortable and renders the appropriate user interface.
   - `row-key` (optional) The name of the row property or a function which will return a unique value for every row
   - `on-row-click` (optional) Providing an onClick handler enables focus, hover, and related styles.
   - `on-row-toggle` (optional) An event handler for toggle of the row. resize of columns. The function is passed the original row map.
   - `on-all-rows-toggle` (optional) Callback invoked when a user clicks the row selection toggle in the header.
   - `expansion-row` (optional) Function which returns an optional row that is displayed when this row is expanded, or an array of rows.
   - `on-expansion` (optional) An event handler that triggers when the row expansion element is selected."
  [{:keys [sort-key sort-dir]
    :or   {sort-dir "asc"}}]
  (let [local-state (r/atom {:sort-key sort-key
                             :sort-dir sort-dir})]
    (fn [{:keys [model stripe-rows head-type inner-style dock-offset dock-scroll-bar row-expansion
                 on-move-row on-move-column on-resize-column resizable-fill-layout
                 columns on-col-menu-click sort-key sort-dir on-sort
                 row-key on-row-click on-row-toggle on-all-rows-toggle expansion-row on-expansion]}]
      (let [rows             (utils/model->value model)
            columns          (utils/model->value columns)
            {local-sort-key :sort-key local-sort-dir :sort-dir} @local-state
            sort-key         (or sort-key local-sort-key)
            sort-dir         (or sort-dir local-sort-dir)
            rows-sorted      (cond->> rows
                                      (some? sort-key)
                                      (sort-by sort-key (if (= sort-dir "desc") < >)))
            all-rows-toggle? (fn? on-all-rows-toggle)]

        [:> Table (utils/assoc-some {}
                    :stripeRows stripe-rows
                    :headType head-type
                    :innerStyle inner-style
                    :dockOffset dock-offset
                    :dockScrollBar dock-scroll-bar
                    :rowExpansion row-expansion
                    :resizableFillLayout resizable-fill-layout
                    :onRequestMoveRow (when (fn? on-move-row)
                                        (fn [params]
                                          (on-move-row {:from (go/get params "fromIndex")
                                                        :to   (go/get params "toIndex")})))
                    :onRequestMoveColumn (when (fn? on-move-column)
                                           (fn [params]
                                             (on-move-column {:from (go/get params "fromIndex")
                                                              :to   (go/get params "toIndex")})))
                    :onRequestResizeColumn (when (fn? on-resize-column)
                                             (fn [_event params]
                                               (on-resize-column {:column-id (go/get params "columnId")
                                                                  :index     (go/get params "index")
                                                                  :width     (go/get params "width")})))
                    :onRequestToggleAllRows (when all-rows-toggle?
                                              #(-> (rows-selection rows)
                                                   (not= "all")
                                                   (on-all-rows-toggle)))
                    :rowSelection (when all-rows-toggle?
                                    (rows-selection rows)))
         [:> Head
          (doall
           (for [{:keys [id header-label align menu-items width] col-sort-key :sort-key} columns]
             (let [dropdown-cell? (some? menu-items)
                   cell-props     (cond-> (utils/assoc-some
                                            {:key      id
                                             :columnId id}
                                            :width width
                                            :align align)
                                          (some? col-sort-key)
                                          (merge {:key     (str id "-" col-sort-key)
                                                  :onSort  (fn [_event params]
                                                             (let [new-sort-key  (keyword (go/get params "sortKey"))
                                                                   prev-sort-dir (if (= sort-key new-sort-key)
                                                                                   sort-dir
                                                                                   "none")
                                                                   new-sort-dir  (if (= prev-sort-dir "asc")
                                                                                   "desc"
                                                                                   "asc")]
                                                               (swap! local-state assoc
                                                                      :sort-key new-sort-key
                                                                      :sort-dir new-sort-dir)

                                                               (when (fn? on-sort)
                                                                 (on-sort @local-state))))
                                                  :sortKey col-sort-key
                                                  :sortDir (if (= sort-key col-sort-key)
                                                             sort-dir
                                                             "none")}))]
               (if dropdown-cell?
                 [:> HeadDropdownCell (utils/assoc-some
                                        {:label header-label
                                         :key   id}
                                        :align align)
                  (into
                   [:> Menu]
                   (map (fn [{:keys [label selectable selected] :as item}]
                          [:> Item {:selectable selectable
                                    :selected   selected
                                    :onClick    #(on-col-menu-click item)}
                           label]))
                   menu-items)]

                 [:> HeadCell cell-props
                  header-label]))))]

         (into
          [:> Body]
          (map
           (fn [{:keys [expanded] :as row}]
             (let [expansion (if (fn? expansion-row)
                               (expansion-row row))
                   row-props (utils/assoc-some {:data row}
                               :key (when (some? row-key)
                                      (row-key row))
                               :onClick (when (fn? on-row-click)
                                          (fn [_event _data]
                                            (on-row-click row)))
                               :onRequestToggle (when (fn? on-row-toggle)
                                                  (fn [_event _data]
                                                    (on-row-toggle row)))
                               :selected (:selected row)
                               :expansionRow (if (vector? expansion)
                                               (r/as-element expansion)
                                               expansion)
                               :onExpansion (when (fn? on-expansion)
                                              #(on-expansion row))
                               :expanded expanded)]
               [:> Row row-props
                (doall
                 (for [{:keys [id align]} columns]
                   ^{:key id}
                   [:> Cell (utils/assoc-some {}
                              :align align)
                    (get row id)]))])))
          rows-sorted)]))))