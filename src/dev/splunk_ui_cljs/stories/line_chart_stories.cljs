(ns splunk-ui-cljs.stories.line-chart-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.line-chart :refer [line-chart]]))


(def ^:export default
  (utils/->default
   {:title     "Line Chart"
    :component line-chart
    :argTypes  {:model              {:type        {:name "collection" :required true}
                                     :description "Collection of rows to be displayed. Could be an atom"
                                     :control     {:type nil}}
                :fields             {:type        {:name "collection" :required true}
                                     :description "Collection of column names. Each item should contain a :name key. Could be an atom"
                                     :control     {:type nil}}
                :total-count        {:type        {:name "number" :required false}
                                     :description "Total number of rows. Used to calculate pagination. Could not match the number of rows in the model collection. Required when :show-pagination option set to true. Could be an atom"
                                     :control     "number"}
                :options            {:type        {:name "map" :required false}
                                     :description "Component formatting options. Available options are: show-pagination, page-length-options, formatting-options, current-table-style, row-numbers, wrap-results. Could be an atom"
                                     :control     {:type nil}}
                :on-options-change  {:type        {:name "function" :required false}
                                     :description "A callback to update Events Viewer table style formatting options"
                                     :control     {:type nil}}
                :request-params     {:type        {:name "map" :required false}
                                     :description "Component request options. Available options are: count, offset, request-total-count. Could be an atom"
                                     :control     {:type nil}}
                :on-params-change   {:type        {:name "function" :required false}
                                     :description "A callback to request new data with updated search request params. Required if using pagination"
                                     :control     {:type nil}}
                :height             {:type        {:name "number" :required false}
                                     :description "Height in pixel or string, defaults to 100%"
                                     :control     "number"}
                :width              {:type        {:name "number" :required false}
                                     :description "Width in pixel or string, defaults to 100%"
                                     :control     "number"}
                :annotation         {:type        {:name "map" :required false}
                                     :description "Additional datasource to display annotations on the chart. Keys of this map should mimic the primary datasource keys: model, fields, request-params, total-count"
                                     :control     {:type nil}}
                :on-legend-click    {:type        {:name "function" :required false}
                                     :description "An event callback, triggered when legend label is clicked"
                                     :control     {:type nil}}
                :on-point-click     {:type        {:name "function" :required false}
                                     :description "An event callback, triggered when chart point is clicked"
                                     :control     {:type nil}}
                :on-point-mouseover {:type        {:name "function" :required false}
                                     :description "An event callback, triggered when chart point is hovered"
                                     :control     {:type nil}}
                :on-point-mouseout  {:type        {:name "function" :required false}
                                     :description "An event callback, triggered when chart point loosing a hover"
                                     :control     {:type nil}}}}))


(defn ^:export line-chart-basic [args]
  (let [sample-fields [{:name "_time"}
                       {:name "count" :type_special "count"}
                       {:name "percent" :type_special "percent"}]
        sample-events [{:_time "2018-05-02T18:10:46.000-07:00" :count "600" :percent "87.966380"}
                       {:_time "2018-05-02T18:11:47.000-07:00" :count "525" :percent "50.381304"}
                       {:_time "2018-05-02T18:12:48.000-07:00" :count "295" :percent "60.023780"}
                       {:_time "2018-05-02T18:13:49.000-07:00" :count "213" :percent "121.183272"}
                       {:_time "2018-05-02T18:15:50.000-07:00" :count "122" :percent "70.250513"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [line-chart
       {:model          sample-events
        :fields         sample-fields
        :total-count    (count sample-events)
        :request-params {:count 10 :offset 0}}]])))


(defn ^:export line-chart-split-series [args]
  (let [sample-fields [{:name "_time"}
                       {:name "count" :type_special "count"}
                       {:name "percent" :type_special "percent"}]
        sample-events [{:_time "2018-05-02T18:10:46.000-07:00" :count "600" :percent "87.966380"}
                       {:_time "2018-05-02T18:11:47.000-07:00" :count "525" :percent "50.381304"}
                       {:_time "2018-05-02T18:12:48.000-07:00" :count "295" :percent "60.023780"}
                       {:_time "2018-05-02T18:13:49.000-07:00" :count "213" :percent "121.183272"}
                       {:_time "2018-05-02T18:15:50.000-07:00" :count "122" :percent "70.250513"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [line-chart
       {:model          sample-events
        :fields         sample-fields
        :total-count    (count sample-events)
        :options        {:showSplitSeries true}
        :request-params {:count 10 :offset 0}}]])))


(defn ^:export line-chart-value-labeling [args]
  (let [sample-fields [{:name "_time" :groupby_rank "0"}
                       {:name "count"}
                       {:name "_span"}]
        sample-events [{:_time "2018-05-02T18:15:46.000-07:00" :count "2" :_span "1"}
                       {:_time "2018-05-02T18:15:47.000-07:00" :count "10" :_span "1"}
                       {:_time "2018-05-02T18:15:48.000-07:00" :count "13" :_span "1"}
                       {:_time "2018-05-02T18:15:49.000-07:00" :count "60" :_span "1"}
                       {:_time "2018-05-02T18:15:50.000-07:00" :count "43" :_span "1"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [line-chart
       {:model          sample-events
        :fields         sample-fields
        :total-count    (count sample-events)
        :options        {:dataValuesDisplay "all"}
        :request-params {:count 10 :offset 0}}]])))


(defn ^:export line-chart-colors [args]
  (let [sample-fields [{:name "_time"}
                       {:name "total count" :type_special "count"}
                       {:name "percent" :type_special "percent"}]
        sample-events [{:_time "2018-05-02T18:10:46.000-07:00" "total count" "600" :percent "87.966380"}
                       {:_time "2018-05-02T18:11:47.000-07:00" "total count" "525" :percent "50.381304"}
                       {:_time "2018-05-02T18:12:48.000-07:00" "total count" "295" :percent "60.023780"}
                       {:_time "2018-05-02T18:13:49.000-07:00" "total count" "213" :percent "121.183272"}
                       {:_time "2018-05-02T18:15:50.000-07:00" "total count" "122" :percent "70.250513"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [line-chart
       {:model          sample-events
        :fields         sample-fields
        :total-count    (count sample-events)
        :options        {:seriesColorsByField {"total count" "#008000" :percent "#FFA500"}}
        :request-params {:count 10 :offset 0}}]])))


(defn ^:export line-chart-outlier-detection [args]
  (let [sample-fields [{:name "_time"}
                       {:name "count"}
                       {:name "upperBound"}
                       {:name "lowerBound"}
                       {:name "isOutlier"}]
        sample-events [{:_time "2017-07-05T00:00:00.000-07:00" :count "43670" :upperBound "80000" :lowerBound "0" :isOutlier "0"}
                       {:_time "2017-07-06T00:00:00.000-07:00" :count "34735" :upperBound "60000" :lowerBound "30000" :isOutlier "0"}
                       {:_time "2017-07-07T00:00:00.000-07:00" :count "153472" :upperBound "208000" :lowerBound "130000" :isOutlier "0"}
                       {:_time "2017-07-08T00:00:00.000-07:00" :count "79423" :upperBound "214000" :lowerBound "178000" :isOutlier "1"}
                       {:_time "2017-07-09T00:00:00.000-07:00" :count "210527" :upperBound "143000" :lowerBound "100000" :isOutlier "1"}
                       {:_time "2017-07-10T00:00:00.000-07:00" :count "165434" :upperBound "180000" :lowerBound "158000" :isOutlier "0"}
                       {:_time "2017-07-11T00:00:00.000-07:00" :count "65382" :upperBound "100000" :lowerBound "60000" :isOutlier "0"}
                       {:_time "2017-07-12T00:00:00.000-07:00" :count "92035" :upperBound "90000" :lowerBound "63000" :isOutlier "1"}
                       {:_time "2017-07-13T00:00:00.000-07:00" :count "54667" :upperBound "76000" :lowerBound "46000" :isOutlier "0"}
                       {:_time "2017-07-14T00:00:00.000-07:00" :count "82035" :upperBound "115000" :lowerBound "75000" :isOutlier "0"}
                       {:_time "2017-07-15T00:00:00.000-07:00" :count "210597" :upperBound "218000" :lowerBound "170000" :isOutlier "0"}
                       {:_time "2017-07-16T00:00:00.000-07:00" :count "95434" :upperBound "179000" :lowerBound "115000" :isOutlier "1"}
                       {:_time "2017-07-17T00:00:00.000-07:00" :count "81617" :upperBound "101000" :lowerBound "50000" :isOutlier "0"}
                       {:_time "2017-07-18T00:00:00.000-07:00" :count "55382" :upperBound "65000" :lowerBound "34000" :isOutlier "0"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [line-chart
       {:model          sample-events
        :fields         sample-fields
        :total-count    (count sample-events)
        :request-params {:count 10 :offset 0}}]])))


(defn ^:export line-chart-annotation [args]
  (let [sample-fields     [{:name "_time" :groupby_rank "0"}
                           {:name "count"}
                           {:name "_span"}]
        sample-events     [{:_time "2018-05-02T18:10:46.000-07:00" :count "2" :_span "1"}
                           {:_time "2018-05-02T18:11:47.000-07:00" :count "10" :_span "1"}
                           {:_time "2018-05-02T18:12:48.000-07:00" :count "13" :_span "1"}
                           {:_time "2018-05-02T18:13:49.000-07:00" :count "60" :_span "1"}
                           {:_time "2018-05-02T18:15:50.000-07:00" :count "43" :_span "1"}]
        annotation-fields [{:name "_time" :groupby_rank "0"}
                           {:name "annotation_label"}
                           {:name "annotation_color"}]
        annotation-events [{:_time            "2018-05-02T18:11:50.000-07:00"
                            :annotation_label "houston, we have a problem"
                            :annotation_color "#f44271"}
                           {:_time            "2018-05-02T18:13:25.000-07:00"
                            :annotation_label "just close the jira"
                            :annotation_color "#f4a941"}
                           {:_time            "2018-05-02T18:14:30.000-07:00"
                            :annotation_label "looking good now"
                            :annotation_color "#41f49a"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [line-chart
       {:model          sample-events
        :fields         sample-fields
        :total-count    (count sample-events)
        :request-params {:count 10 :offset 0}
        :options        {:annotationX     "> annotation|seriesByIndex(0)"
                         :annotationLabel "> annotation|seriesByIndex(1)"
                         :annotationColor "> annotation|seriesByIndex(2)"}

        :annotation     {:model          annotation-events
                         :fields         annotation-fields
                         :total-count    10
                         :request-params {:count 10 :offset 0}}}]])))


(defn ^:export line-chart-events [args]
  (let [sample-fields [{:name "_time"}
                       {:name "count" :type_special "count"}
                       {:name "percent" :type_special "percent"}]
        sample-events [{:_time "2018-05-02T18:10:46.000-07:00" :count "600" :percent "87.966380"}
                       {:_time "2018-05-02T18:11:47.000-07:00" :count "525" :percent "50.381304"}
                       {:_time "2018-05-02T18:12:48.000-07:00" :count "295" :percent "60.023780"}
                       {:_time "2018-05-02T18:13:49.000-07:00" :count "213" :percent "121.183272"}
                       {:_time "2018-05-02T18:15:50.000-07:00" :count "122" :percent "70.250513"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [line-chart
       {:model              sample-events
        :fields             sample-fields
        :total-count        (count sample-events)
        :request-params     {:count 10 :offset 0}
        :on-point-click     (fn [{:keys [name value row]}]
                              (println "you clicked on field" name "with value" value "as part of row" row))
        :on-point-mouseover (fn [{:keys [name value row]}]
                              (println "you hovered field" name "with value" value "as part of row" row))
        :on-point-mouseout  (fn [{:keys [name value row]}]
                              (println "you left field" name "with value" value "as part of row" row))
        :on-legend-click    (fn [{:keys [name]}]
                              (println "you clicked on a legend title:" name))}]])))
