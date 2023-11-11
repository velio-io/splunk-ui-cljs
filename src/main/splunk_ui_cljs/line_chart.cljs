(ns splunk-ui-cljs.line-chart
  (:require
   [clojure.string :as string]
   [goog.object :as go]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/datasource-utils/DataSet" :default DataSet]
   ["@splunk/visualizations/Line" :default Line]))


(def not-nil?
  (comp not nil?))


(def params-alias
  {"requestTotalCount" :request-total-count
   "count"             :count
   "offset"            :offset})


(defn ->dataset [model fields request-params total-count]
  (let [{:keys [count offset]} (utils/model->value request-params)
        request-params {:count count :offset offset}
        data           (-> model utils/model->value clj->js)
        fields         (when (some? fields)
                         (-> fields utils/model->value clj->js))
        dataset        (.fromJSONArray DataSet fields data)
        meta           {:totalCount (utils/model->value total-count)}]
    {:requestParams request-params
     :data          dataset
     :meta          meta}))


(defn payload->row [payload]
  (->> (for [key (go/getKeys payload)
             :when (string/starts-with? key "row.")
             :let [row-key   (->> key
                                  (re-find #"row\.(.+)\.value")
                                  second
                                  keyword)
                   row-value (go/get payload key)]]
         [row-key row-value])
       (into {})))


(defn line-chart
  "A line chart shows changes of values with respect to another variable, such as time.
   Line charts work well for continuous data rather than discrete values.
   The slope of the line makes it easy to identify trends in the data.
   Line charts also simplify comparisons between multiple data series.
   Use line charts instead of column charts when the data series consists of many data points.
   - `model` (required) Collection of rows to be displayed. Could be an atom
   - `fields` (required) Collection of column names. Each item should contain a :name key. Could be an atom
   - `total-count` (optional) Total number of rows. Used to calculate pagination. Could not match the number of rows in the model collection. Required when :show-pagination option set to true. Could be an atom
   - `options` (optional) Component formatting options. Available options are: show-pagination, page-length-options, formatting-options, current-table-style, row-numbers, wrap-results. Could be an atom
   - `on-options-change` (optional) A callback to update Events Viewer table style formatting options
   - `request-params` (optional) Component request options. Available options are: count, offset, request-total-count. Could be an atom
   - `on-params-change` (optional) A callback to request new data with updated search request params. Required if using pagination
   - `height` (optional) Height in pixel or string, defaults to 100%
   - `width` (optional) Width in pixel or string, defaults to 100%
   - `annotation` (optional) Additional datasource to display annotations on the chart. Keys of this map should mimic the primary datasource keys: model, fields, request-params, total-count
   - `on-legend-click` (optional) An event callback, triggered when legend label is clicked
   - `on-point-click` (optional) An event callback, triggered when chart point is clicked
   - `on-point-mouseover` (optional) An event callback, triggered when chart point is hovered
   - `on-point-mouseout` (optional) An event callback, triggered when chart point loosing a hover"
  [{:keys [model fields total-count options on-options-change
           on-legend-click on-point-click on-point-mouseover on-point-mouseout
           request-params on-params-change height width annotation]}]
  [:> Line
   (utils/assoc-some
     {:dataSources (cond-> {:primary (->dataset model fields request-params total-count)}
                           (some? annotation)
                           (assoc :annotation (->dataset (:model annotation)
                                                         (:fields annotation)
                                                         (:request-params annotation)
                                                         (:total-count annotation))))}
     :options options
     :onOptionsChange (when (fn? on-options-change)
                        (fn [option]
                          (let [key   (first (go/getKeys option))
                                value (go/get option key)]
                            (on-options-change {key value}))))

     :onRequestParamsChange (when (fn? on-params-change)
                              (fn [_data-source-type new-params]
                                (->> (for [key (go/getKeys new-params)
                                           :let [value     (go/get new-params key)
                                                 param-key (get params-alias key key)]]
                                       [param-key value])
                                     (into {})
                                     (on-params-change))))

     :onEventTrigger (fn [params]
                       (let [type    (go/get params "type")
                             payload (go/get params "payload")
                             event   {:name           (go/get payload "name")
                                      :value          (go/get payload "value")
                                      :_span          (go/get payload "_span")
                                      :tooltipContext (go/get payload "tooltipContext")
                                      :row            (payload->row payload)}]
                         (cond
                           (and (= type "legend.click") (some? on-legend-click))
                           (on-legend-click event)

                           (and (= type "point.click") (some? on-point-click))
                           (on-point-click event)

                           (and (= type "point.mouseover") (some? on-point-mouseover))
                           (on-point-mouseover event)

                           (and (= type "point.mouseout") (some? on-point-mouseout))
                           (on-point-mouseout event))))

     :hasEventHandlers (some not-nil? [on-legend-click on-point-click on-point-mouseover on-point-mouseout])

     :height height
     :width width)])
