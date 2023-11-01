(ns splunk-ui-cljs.stories.events-viewer-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-ui/Menu" :refer [Item]]
   ["@splunk/react-icons/Information" :default Information]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.table :refer [table]]
   [splunk-ui-cljs.events-viewer :refer [events-viewer]]))


(def ^:export default
  (utils/->default
   {:title     "Events viewer"
    :component events-viewer
    :argTypes  {:model                     {:type        {:name "collection" :required true}
                                            :description "Collection of rows to be displayed. Could be an atom"
                                            :control     {:type nil}}
                :fields                    {:type        {:name "collection" :required true}
                                            :description "Collection of column names. Each item should contain a :name key. Could be an atom"
                                            :control     {:type nil}}
                :total-count               {:type        {:name "number" :required false}
                                            :description "Total number of rows. Used to calculate pagination. Could not match the number of rows in the model collection. Required when :show-pagination option set to true. Could be an atom"
                                            :control     "number"}
                :options                   {:type        {:name "map" :required false}
                                            :description "Component formatting options. Available options are: show-pagination, page-length-options, formatting-options, current-table-style, row-numbers, wrap-results. Could be an atom"
                                            :control     {:type nil}}
                :on-options-change         {:type        {:name "function" :required false}
                                            :description "A callback to update Events Viewer table style formatting options"
                                            :control     {:type nil}}
                :request-params            {:type        {:name "map" :required false}
                                            :description "Component request options. Available options are: count, offset, request-total-count. Could be an atom"
                                            :control     {:type nil}}
                :on-params-change          {:type        {:name "function" :required false}
                                            :description "A callback to request new data with updated search request params. Required if using pagination"
                                            :control     {:type nil}}
                :is-field                  {:type        {:name "function" :required false}
                                            :description "Check if an attribute is a field. Receives a field name"
                                            :control     {:type nil}}
                :on-field-clicked          {:type        {:name "function" :required false}
                                            :description "Callback when the user clicks on the field name in the expanded row"
                                            :control     {:type nil}}
                :on-field-value-clicked    {:type        {:name "function" :required false}
                                            :description "Callback when the user clicks on the field value in the expanded row"
                                            :control     {:type nil}}
                :on-time-clicked           {:type        {:name "function" :required false}
                                            :description "Callback when the user clicks on the row's time value"
                                            :control     {:type nil}}
                :render-event-actions      {:type        {:name "collection" :required false}
                                            :description "Collection of maps, each representing a menu item for event actions dropdown"
                                            :control     {:type nil}}
                :render-field-actions      {:type        {:name "collection" :required false}
                                            :description "Collection of maps, each representing a menu item for field actions dropdown"
                                            :control     {:type nil}}
                :footer-fields             {:type        {:name "collection" :required false}
                                            :description "List of fields names (strings) to show in the event footer"
                                            :control     {:type nil}}
                :field-value-highlight-map {:type        {:name "map" :required false}
                                            :description "Mapping between field names and values that should be highlighted. Could be an atom"
                                            :control     {:type nil}}
                :selection                 {:type        {:name "boolean" :required false}
                                            :description "Flag to turn on/off row selection"
                                            :control     "boolean"}
                :on-toggle-page            {:type        {:name "function" :required false}
                                            :description "Callback when the user selects all rows on the page"
                                            :control     {:type nil}}
                :on-toggle-row             {:type        {:name "function" :required false}
                                            :description "Callback when the user selects a single row"
                                            :control     {:type nil}}
                :actions                   {:type        {:name "collection" :required false}
                                            :description "Collection of action items showed when some rows selected"
                                            :control     {:type nil}}
                :on-action-click           {:type        {:name "function" :required false}
                                            :description "Callback when the user clicks on some action"
                                            :control     {:type nil}}
                :table-styles              {:type        {:name "map" :required false}
                                            :description "Options map to provide a custom table for rendering rows. Available keys are: :key, :label, :component"
                                            :control     {:type nil}}
                :style                     {:type        {:name "map" :required false}
                                            :description "Custom style on visualization in the shape of `{[key: string]: any;}`"
                                            :control     {:type nil}}
                :height                    {:type        {:name "number" :required false}
                                            :description "Height in pixel or string, defaults to 100%"
                                            :control     "number"}
                :width                     {:type        {:name "number" :required false}
                                            :description "Width in pixel or string, defaults to 100%"
                                            :control     "number"}}}))


(def sample-fields
  [{:name "host"}
   {:name "source"}
   {:name "sourcetype"}
   {:name "splunk_server"}
   {:name "index"}
   {:name "_raw"}
   {:name "_time"}
   {:name "eventtype"}
   {:name "_eventtype_color"}
   {:name "linecount"}
   {:name "_fulllinecount"}])


(def sample-events
  (concat [{:_time         "2018-04-02T16:33:49.691-07:00"
            :_raw          {:datetime  "07-16-2018 16:38:11.545 -0700",
                            :log_level "INFO",
                            :component "KVStoreServerStats",
                            :data      {:host           "ssingamneni-mbp13:8191",
                                        :version        "3.4.9-splunk",
                                        :process        "mongod",
                                        :pid            21585,
                                        :uptime         20886,
                                        :uptimeMillis   20886042,
                                        :uptimeEstimate 20886,
                                        :localTime      1531784291541}}
            :host          "jbreeden-mbpr15"
            :source        "/Users/jbreeden/splunk/releases-builds/splunk-nightlight/splunk/var/log/splunk/metrics.log"
            :sourcetype    "splunkd"
            :splunk_server "jbreeden-mbpr15"
            :index         "_internal"
            :linecount     "1"}]
          (repeat 15 {:_time         "2018-04-02T16:33:49.691-07:00"
                      :_raw          "04-02-2018 16:33:49.691 -0700 INFO Metrics - group=thruput, name=thruput, instantaneous_kbps=0.8775482519547254, instantaneous_eps=3.5802456751680514, average_kbps=0.7280928568987709, total_k_processed=4794, kb=27.20703125, ev=111, load_average=2.87744140625"
                      :host          "jbreeden-mbpr15"
                      :source        "/Users/jbreeden/splunk/releases-builds/splunk-nightlight/splunk/var/log/splunk/metrics.log"
                      :sourcetype    "splunkd"
                      :splunk_server "jbreeden-mbpr15"
                      :index         "_internal"
                      :linecount     "1"})))


(defn ^:export events-viewer-basic [args]
  (let [options        (r/atom {:show-pagination     true
                                :page-length-options [10, 20, 50]
                                :formatting-options  ["rowNumbers", "wrapResults"]
                                :current-table-style "list"
                                :row-numbers         false
                                :wrap-results        true})
        request-params (r/atom {:count               10
                                :offset              0
                                :request-total-count true})
        model          (r/atom (take 10 sample-events))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [events-viewer
       {:model             model
        :fields            sample-fields
        :total-count       (count sample-events)

        :options           options
        :on-options-change (fn [option]
                             (let [[key value] (first option)]
                               (swap! options assoc key value)))

        :request-params    request-params
        :on-params-change  (fn [{:keys [count offset] :as params}]
                             (reset! request-params params)
                             (->> sample-events
                                  (drop offset)
                                  (take count)
                                  (reset! model)))}]])))


(defn ^:export events-viewer-selection [args]
  (let [options        (r/atom {:show-pagination     true
                                :page-length-options [10, 20, 50]
                                :formatting-options  ["rowNumbers", "wrapResults"]
                                :current-table-style "list"
                                :row-numbers         false
                                :wrap-results        true})
        request-params (r/atom {:count               10
                                :offset              0
                                :request-total-count true})
        model          (r/atom (take 10 sample-events))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [events-viewer
       {:model             model
        :fields            sample-fields
        :total-count       (count sample-events)

        :selection         true
        :actions           [{:label "Add to Notable" :name "addToNotable"}]
        :on-action-click   (fn [action selected-rows]
                             (println action selected-rows))

        :options           options
        :on-options-change (fn [option]
                             (let [[key value] (first option)]
                               (swap! options assoc key value)))

        :request-params    request-params
        :on-params-change  (fn [{:keys [count offset] :as params}]
                             (reset! request-params params)
                             (->> sample-events
                                  (drop offset)
                                  (take count)
                                  (reset! model)))}]])))


(defn ^:export events-viewer-event-actions [args]
  (let [events-actions
        [{:event-type-values      []
          :field-filters          []
          :event-action-component (fn [event]
                                    [:> Item {:on-click #(println "Action clicked")}
                                     "Build Event Type"])}
         {:event-type-values      ["*"]
          :field-filters          ["*"]
          :event-action-component (fn [event]
                                    [:> Item
                                     "Extract Fields"])}
         {:event-type-values      []
          :field-filters          ["h*"]
          :event-action-component (fn [event]
                                    [:> Item
                                     "Filtered by `h*` field"])}
         {:field-filters          ["host" "random"]
          :event-action-component (fn [event]
                                    [:> Item
                                     "Filtered by `host` and `random` fields"])}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [events-viewer
       {:model                sample-events
        :fields               sample-fields
        :options              {:show-pagination false}
        :render-event-actions events-actions
        :request-params       {:count 10 :offset 0}}]])))


(defn ^:export events-viewer-field-actions [args]
  (let [field-actions
        {"*"      [{:event-type-values []
                    :field-action-component
                    (fn [{:keys [field value]}]
                      [:> Item {:on-click #(println (str "Action with " value " for " field " was clicked"))}
                       "Common Action 1"])}
                   {:event-type-values []
                    :field-action-component
                    (fn [{:keys [field value]}]
                      [:> Item {:on-click #(println (str "Action with " value " for " field " was clicked"))}
                       "Common Action 2"])}
                   {:event-type-values ["splunkd"]
                    :field-action-component
                    (fn [{:keys [field value]}]
                      [:> Item {:on-click #(println (str "Action with " value " for " field " was clicked"))}
                       "Common Action 3, filtered by `splunkd` eventtypeValues"])}]

         "host"   [{:event-type-values []
                    :field-action-component
                    (fn [{:keys [field value]}]
                      [:> Item {:on-click #(println (str "Host field-specific action with " value " for " field " was clicked"))}
                       "Specific Host Action"])}]

         "source" [{:event-type-values []
                    :field-action-component
                    (fn [{:keys [field value]}]
                      [:> Item {:on-click #(println (str "Source field-specific action with " value " for " field " was clicked"))}
                       "Specific Source Action"])}]

         "s*"     [{:event-type-values []
                    :field-action-component
                    (fn [{:keys [field value]}]
                      [:> Item {:on-click #(println (str "Wildcard action for s* fields with " value " for " field " was clicked"))}
                       "Wildcard action for s* fields"])}]}]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [events-viewer
       {:model                sample-events
        :fields               sample-fields
        :options              {:show-pagination false}
        :render-field-actions field-actions
        :request-params       {:count 10 :offset 0}}]])))


(defn ^:export events-viewer-footer-and-highlights [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [events-viewer
     {:model                     sample-events
      :fields                    sample-fields
      :options                   {:show-pagination false}
      :request-params            {:count 10 :offset 0}
      :footer-fields             ["host" "source" "sourcetype"]
      :field-value-highlight-map {:host       "jbreeden-mbpr15"
                                  :sourcetype "splunkd"}}]]))


(defn ^:export events-viewer-custom-table [args]
  (r/as-element
   [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
    [events-viewer
     {:model          sample-events
      :fields         sample-fields
      :request-params {:count 10 :offset 0}
      :options        {:show-pagination     false
                       :current-table-style "CustomTable"}
      :table-styles   {:key       "CustomTable"
                       :label     "Custom Table"
                       :component (fn [{:keys [fields events page-number get-expansion-row]}]
                                    [table
                                     {:model         (->> events
                                                          (map-indexed (fn [idx event]
                                                                         (let [key (str (:index event) "-" idx "-" page-number)]
                                                                           (-> event
                                                                               (update-vals #(str %))
                                                                               (assoc :key key)
                                                                               (assoc :expansion-row (get-expansion-row (clj->js event) key idx page-number)))))))
                                      :columns       (->> fields
                                                          (remove #(= (:name %) "_raw"))
                                                          (map (fn [{:keys [name]}]
                                                                 {:id           (keyword name)
                                                                  :header-label [:<>
                                                                                 [:div {:style {:fontSize    "20px",
                                                                                                :marginRight "5px"}}
                                                                                  [:> Information]]
                                                                                 name]})))
                                      :row-expansion "single"}])}}]]))
