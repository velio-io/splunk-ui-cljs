(ns splunk-ui-cljs.events-viewer
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   [goog.object :as go]
   [clojure.set]
   ["@splunk/react-events-viewer/components/EventsViewer" :default EventsViewer]
   ["@splunk/react-events-viewer/utils/TableStyleSet" :default TableStyleSet]
   ["@splunk/datasource-utils/DataSet" :default DataSet]))


(def options-alias
  {"showPagination"    :show-pagination
   "pageLengthOptions" :page-length-options
   "formattingOptions" :formatting-options
   "currentTableStyle" :current-table-style
   "rowNumbers"        :row-numbers
   "wrapResults"       :wrap-results})


(def params-alias
  {"requestTotalCount" :request-total-count
   "count"             :count
   "offset"            :offset})


(defn events-viewer
  "This is a re-implementation of the EventsViewer from Splunk Enterprise as a React Component
   - model (required) Collection of rows to be displayed. Could be an atom
   - fields (required) Collection of column names. Each item should contain a :name key. Could be an atom
   - total-count (optional) Total number of rows. Used to calculate pagination. Could not match the number of rows in the model collection. Required when :show-pagination option set to true. Could be an atom
   - options (optional) Component formatting options. Available options are: show-pagination, page-length-options, formatting-options, current-table-style, row-numbers, wrap-results. Could be an atom
   - on-options-change (optional) A callback to update Events Viewer table style formatting options
   - request-params (optional) Component request options. Available options are: count, offset, request-total-count. Could be an atom
   - on-params-change (optional) A callback to request new data with updated search request params. Required if using pagination
   - is-field (optional) Check if an attribute is a field. Receives a field name
   - on-field-clicked (optional) Callback when the user clicks on the field name in the expanded row
   - on-field-value-clicked (optional) Callback when the user clicks on the field value in the expanded row
   - on-time-clicked (optional) Callback when the user clicks on the row's time value
   - render-event-actions (optional) Collection of maps, each representing a menu item for event actions dropdown
   - render-field-actions (optional) Collection of maps, each representing a menu item for field actions dropdown
   - footer-fields (optional) List of fields names (strings) to show in the event footer
   - field-value-highlight-map (optional) Mapping between field names and values that should be highlighted. Could be an atom
   - selection (optional) Flag to turn on/off row selection
   - on-toggle-page (optional) Callback when the user selects all rows on the page
   - on-toggle-row (optional) Callback when the user selects a single row
   - actions (optional) Collection of action items showed when some rows selected
   - on-action-click (optional) Callback when the user clicks on some action
   - table-styles (optional) Options map to provide a custom table for rendering rows. Available keys are: :key, :label, :component
   - style (optional) Custom style on visualization in the shape of { [key: string]: any; }
   - height (optional) Height in pixel or string, defaults to 100%
   - width (optional) Width in pixel or string, defaults to 100%"
  [props]
  (let [selected-rows (r/atom #{})]
    (fn [{:keys [model fields total-count options on-options-change request-params on-params-change
                 is-field on-field-clicked on-field-value-clicked on-time-clicked
                 render-event-actions render-field-actions footer-fields field-value-highlight-map
                 selection on-toggle-page on-toggle-row actions on-action-click table-styles
                 style height width]}]
      (let [{:keys [show-pagination page-length-options formatting-options current-table-style row-numbers wrap-results]}
            (utils/model->value options)

            options        (utils/assoc-some {}
                             :showPagination show-pagination
                             :pageLengthOptions page-length-options
                             :formattingOptions formatting-options
                             :currentTableStyle current-table-style
                             :rowNumbers row-numbers
                             :wrapResults wrap-results

                             :tableStyles
                             (when (some? table-styles)
                               (new TableStyleSet
                                    #js [#js {:key               (:key table-styles)
                                              :label             (:label table-styles)
                                              :formattingOptions (or (:formatting-options table-styles) #js [])
                                              :component
                                              (fn [params]
                                                (let [component (:component table-styles)]
                                                  (r/as-element
                                                   [component {:fields            (js->clj (go/get params "fields") :keywordize-keys true)
                                                               :events            (js->clj (go/get params "events") :keywordize-keys true)
                                                               :page-number       (go/get params "pageNumber")
                                                               :get-expansion-row (go/get params "getExpansionRow")}])))}]))

                             :footerFields
                             (when (some? footer-fields)
                               (clj->js footer-fields))

                             :fieldValueHighlightMap
                             (when (some? field-value-highlight-map)
                               (-> field-value-highlight-map utils/model->value clj->js))

                             :renderEventActions
                             (when (some? render-event-actions)
                               (->> render-event-actions
                                    (map (fn [{:keys [event-type-values field-filters event-action-component]}]
                                           {:eventtypeValues      (or event-type-values [])
                                            :fieldFilters         (or field-filters [])
                                            :eventActionComponent (fn [event]
                                                                    (-> event
                                                                        (js->clj :keywordize-keys true)
                                                                        (event-action-component)
                                                                        (utils/value->element)))}))
                                    (clj->js)))

                             :renderFieldActions
                             (when (some? render-field-actions)
                               (->> render-field-actions
                                    (map (fn [[field actions]]
                                           [field (map (fn [{:keys [event-type-values field-action-component]}]
                                                         {:eventtypeValues      (or event-type-values [])
                                                          :fieldActionComponent (fn [event]
                                                                                  (-> event
                                                                                      (js->clj :keywordize-keys true)
                                                                                      (field-action-component)
                                                                                      (utils/value->element)))})
                                                       actions)]))
                                    (into {})
                                    (clj->js))))

            {:keys [count offset request-total-count]}
            (utils/model->value request-params)

            request-params #js {:count             count
                                :offset            offset
                                :requestTotalCount request-total-count}

            data           (-> model utils/model->value clj->js)
            fields         (when (some? fields)
                             (-> fields utils/model->value clj->js))
            dataset        (.fromJSONArray DataSet fields data)

            meta           #js {:totalCount (utils/model->value total-count)}]

        [:> EventsViewer
         (utils/assoc-some {:dataSources     #js {:primary #js {:requestParams request-params
                                                                :data          dataset
                                                                :meta          meta}}
                            :options         options
                            :onOptionsChange (fn [option]
                                               (when (fn? on-options-change)
                                                 (let [key        (first (go/getKeys option))
                                                       value      (go/get option key)
                                                       option-key (get options-alias key key)]
                                                   (on-options-change {option-key value}))))}

           :onRequestParamsChange (when (fn? on-params-change)
                                    (fn [_data-source-type new-params]
                                      (->> (for [key (go/getKeys new-params)
                                                 :let [value     (go/get new-params key)
                                                       param-key (get params-alias key key)]]
                                             [param-key value])
                                           (into {})
                                           (on-params-change))))

           :selection (when selection
                        (utils/assoc-some
                          {:rows (-> selected-rows deref clj->js)

                           :onTogglePage
                           (fn [events pageNumber status]
                             (let [selected @selected-rows]
                               (if (or (= status "all") (= status "some"))
                                 (let [events-to-remove (->> events
                                                             (map-indexed (fn [idx _]
                                                                            {:index      idx
                                                                             :pageNumber pageNumber}))
                                                             (set))]
                                   (->> selected
                                        (remove #(contains? events-to-remove (select-keys % [:index :pageNumber])))
                                        (reset! selected-rows)))

                                 (let [events-to-add (->> events
                                                          (map-indexed (fn [idx event]
                                                                         {:event      (js->clj event :keywordize-keys true)
                                                                          :index      idx
                                                                          :pageNumber pageNumber}))
                                                          (set))]
                                   (swap! selected-rows clojure.set/union events-to-add))))

                             (when (fn? on-toggle-page)
                               (on-toggle-page (map :event @selected-rows))))

                           :onToggleRow
                           (fn [event index pageNumber]
                             (let [event (js->clj event :keywordize-keys true)
                                   item  {:event      event
                                          :index      index
                                          :pageNumber pageNumber}]
                               (if (contains? @selected-rows item)
                                 (swap! selected-rows disj item)
                                 (swap! selected-rows conj item))

                               (when (fn? on-toggle-row)
                                 (on-toggle-row (map :event @selected-rows)))))}

                          :actions (when (some? actions)
                                     (clj->js actions))

                          :onActionClicked (when (fn? on-action-click)
                                             (fn [_event action]
                                               (on-action-click (go/get action "action")
                                                                (map :event @selected-rows))))))
           :onFieldClicked (when (fn? on-field-clicked)
                             (fn [_event params]
                               (on-field-clicked {:field (go/get params "field")
                                                  :value (go/get params "value")})))

           :onFieldValueClicked (when (fn? on-field-value-clicked)
                                  (fn [_event params]
                                    (on-field-value-clicked {:field (go/get params "field")
                                                             :value (go/get params "value")})))

           :onTimeClicked (when (fn? on-time-clicked)
                            (fn [_event params]
                              (on-time-clicked (go/get params "time"))))

           :style style
           :height height
           :width width
           :isField is-field)]))))
