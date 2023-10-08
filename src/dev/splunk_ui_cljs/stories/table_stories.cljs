(ns splunk-ui-cljs.stories.table-stories
  (:require
   [reagent.core :as r]
   ["@splunk/themes" :refer [SplunkThemeProvider]]
   ["@splunk/react-ui/Paragraph" :default P]
   [splunk-ui-cljs.stories.utils :as utils]
   [splunk-ui-cljs.table :refer [table] :as splunk.table]))


(def ^:export default
  (utils/->default
   {:title     "Table"
    :component table
    :argTypes  {:model                 {:type        {:name "array" :required true}
                                        :description "Collection of table rows (vector of maps). One element for each row in the table.
                                                      Can contain any data with some special keys :selected, :expanded"
                                        :control     "array"}
                :columns               {:type        {:name "array" :required true}
                                        :description "Collection of table headers (vector of maps).
                                                      Must contain keys :id, :header-label and optional keys - :sort-key, :align, :menu-items, :width"
                                        :control     "array"}
                :stripe-rows           {:type        {:name "boolean" :required false}
                                        :description "Alternate rows are given a darker background to improve readability."
                                        :control     "boolean"}
                :head-type             {:type        {:name "text" :required false}
                                        :description "Sets the table head type:
                                                      - docked: The head is docked against the window
                                                      - fixed : The head is fixed in the table. The table can scroll independently from the head.
                                                      - inline: The head isn't fixed, but can scroll with the rest of the table."
                                        :control     "select"
                                        :options     ["docked" "fixed" "inline"]}
                :inner-style           {:type        {:name "object" :required false}
                                        :description "Style specification for the inner container, which is the scrolling container."
                                        :control     "object"}
                :dock-offset           {:type        {:name "number" :required false}
                                        :description "Sets the offset from the top of the window. Only applies when headType is 'docked'."
                                        :control     "number"}
                :dock-scroll-bar       {:type        {:name "boolean" :required false}
                                        :description "Docks the horizontal scroll bar at the bottom of the window when the bottom of the table is below the viewport."
                                        :control     "boolean"}
                :row-expansion         {:type        {:name "text" :required false}
                                        :description "Adds a column to the table with an expansion button for each row that has expansion content. Supported values:
                                                      - single: Only one row can be expanded at a time. If another expansion button is clicked, the currently expanded row closes and the new one opens.
                                                      - multi: Allows multiple rows to be expanded at the same time.
                                                      - controlled: Allows the expanded state to be externally managed by expanded prop of Row.
                                                      - none: The default with no row expansion."
                                        :control     "select"
                                        :options     ["single" "multi" "controlled" "none"]}
                :on-move-row           {:type        {:name "function" :required false}
                                        :description "An event handler to handle the reorder rows action of Table. The function is passed an options map with from and to indexes."
                                        :control     {:type nil}}
                :on-move-column        {:type        {:name "function" :required false}
                                        :description "An event handler for handle the reorder columns action of Table. The function is passed an options map with from and to indexes."
                                        :control     {:type nil}}
                :on-resize-column      {:type        {:name "function" :required false}
                                        :description "An event handler for resize of columns. The function is passed an event and an options map with column-id, index, and width."
                                        :control     {:type nil}}
                :resizable-fill-layout {:type        {:name "boolean" :required false}
                                        :description "Table will fill parent container. Resizable columns can have a width of auto only with this prop enabled."
                                        :control     "boolean"}
                :on-col-menu-click     {:type        {:name "function" :required false}
                                        :description "In case when a column header have a dropdown with menu items, this function will be called when user clicks on some item. Function will get the item itself as an argument."
                                        :control     {:type nil}}
                :sort-key              {:type        {:name "text" :required false}
                                        :description "The sortKey is passed in the data object to the onSort callback, if provided."
                                        :control     "text"}
                :sort-dir              {:type        {:name "text" :required false}
                                        :description "The current sort direction of this column."
                                        :control     "select"
                                        :options     ["asc" "desc" "none"]}
                :on-sort               {:type        {:name "function" :required false}
                                        :description "A callback invoked when this head cell is clicked. If provided, this HeadCell is sortable and renders the appropriate user interface."
                                        :control     {:type nil}}
                :row-key               {:type        {:name "text" :required false}
                                        :description "The name of the row property or a function which will return a unique value for every row"
                                        :control     "text"}
                :on-row-click          {:type        {:name "function" :required false}
                                        :description "Providing an onClick handler enables focus, hover, and related styles."
                                        :control     {:type nil}}
                :on-row-toggle         {:type        {:name "function" :required false}
                                        :description "An event handler for toggle of the row. resize of columns. The function is passed the original row map."
                                        :control     {:type nil}}
                :on-all-rows-toggle    {:type        {:name "function" :required false}
                                        :description "Callback invoked when a user clicks the row selection toggle in the header."
                                        :control     {:type nil}}
                :expansion-row         {:type        {:name "function" :required false}
                                        :description "Function which returns an optional row that is displayed when this row is expanded, or an array of rows."
                                        :control     {:type nil}}
                :on-expansion          {:type        {:name "function" :required false}
                                        :description "An event handler that triggers when the row expansion element is selected."
                                        :control     {:type nil}}}}))


(defn ^:export table-basic [args]
  (let [params  (-> args utils/->params)
        model   (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                         {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                         {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                         {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                         {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},])
        columns [{:id :name :header-label "Name"}
                 {:id :age :header-label "Age"}
                 {:id :email :header-label "Email"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model   model
        :columns columns}]])))


(defn ^:export table-sortable-columns [args]
  (let [model   (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                         {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                         {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                         {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                         {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},])
        columns [{:id :name :header-label "Name"}
                 {:id :age :header-label "Age" :sort-key :age}
                 {:id :email :header-label "Email" :sort-key :email}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model   model
        :columns columns
        :row-key :email}]])))


(defn show-row [{:keys [row]}]
  (when (some? @row)
    [:> P {:style {:margin-top 20}}
     "You clicked on row"
     [:pre
      (prn-str @row)]]))


(defn ^:export table-clickable-rows [args]
  (let [model       (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                             {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                             {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                             {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                             {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},])
        columns     [{:id :name :header-label "Name"}
                     {:id :age :header-label "Age"}
                     {:id :email :header-label "Email"}]
        clicked-row (r/atom nil)]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model        model
        :columns      columns
        :row-key      :email
        :on-row-click #(reset! clicked-row %)}]

      [show-row {:row clicked-row}]])))


(defn ^:export table-selectable-rows [args]
  (let [model         (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com" :selected false},
                               {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com" :selected false},
                               {:name "Estevan", :age 56, :email "Aimee7@hotmail.com" :selected false},
                               {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com" :selected false},
                               {:name "Tressa", :age 38, :email "Yadira1@hotmail.com" :selected false},])
        on-toggle     (fn [selected-row]
                        (->> @model
                             (map (fn [row]
                                    (if (= (:email row) (:email selected-row))
                                      (assoc row :selected (not (:selected row)))
                                      row)))
                             (reset! model)))
        on-all-toggle (fn [select]
                        (->> @model
                             (map (fn [row]
                                    (assoc row :selected select)))
                             (reset! model)))
        columns       [{:id :name :header-label "Name"}
                       {:id :age :header-label "Age"}
                       {:id :email :header-label "Email"}]]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model              model
        :columns            columns
        :row-key            :email
        :on-row-toggle      on-toggle
        :on-all-rows-toggle on-all-toggle}]])))


(defn ^:export table-header-dropdown [args]
  (let [model         (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                               {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                               {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                               {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                               {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},])
        columns       (r/atom [{:id :name :header-label "Name"}
                               {:id :age :header-label "Age" :sort-key :age}
                               {:id           :email
                                :header-label "Email"
                                :sort-key     :email
                                :menu-items   [{:id         :left
                                                :label      "Align Left"
                                                :selectable true
                                                :selected   true}
                                               {:id         :right
                                                :label      "Align Right"
                                                :selectable true
                                                :selected   false}]}])
        on-menu-click (fn [{:keys [id]}]
                        (->> @columns
                             (map (fn [col]
                                    (if (not= (:id col) :email)
                                      col
                                      (-> col
                                          (assoc :align (if (= :left id) "left" "right"))
                                          (update :menu-items
                                                  (fn [items]
                                                    (map (fn [item]
                                                           (cond-> (assoc item :selected false)
                                                                   (= (:id item) id)
                                                                   (assoc :selected true)))
                                                         items)))))))
                             (reset! columns)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model             model
        :columns           columns
        :row-key           :email
        :on-col-menu-click on-menu-click}]])))


(defn ^:export table-fixed-header [args]
  (let [model   (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                         {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                         {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                         {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                         {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},
                         {:name "Bernice", :age 41 :email "bernice.Gilbert@gmail.com"},
                         {:name "Adrian", :age 42 :email "adrian7456@gmail.com"},
                         {:name "Ester", :age 43 :email "esternyc@gmail.com"},
                         {:name "Andrew", :age 44 :email "andrew.fillmore2@gmail.com"},
                         {:name "Felix", :age 45 :email "felixfelix@hotmail.com"}])
        columns (r/atom [{:id :name :header-label "Name"}
                         {:id :age :header-label "Age" :sort-key :age}
                         {:id :email :header-label "Email"}])]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model       model
        :columns     columns
        :row-key     :email
        :head-type   "fixed"
        :inner-style {:maxHeight 160}}]])))


(defn ^:export table-expandable-rows [args]
  (let [model         (r/atom [{:name "Rylan", :email "Angelita_Weimann42@gmail.com"},
                               {:name "Amelia", :email "Dexter.Trantow57@hotmail.com"},
                               {:name "Estevan", :email "Aimee7@hotmail.com"},
                               {:name "Florence", :email "Jarrod.Bernier13@yahoo.com"},
                               {:name "Tressa", :email "Yadira1@hotmail.com"},
                               {:name "Bernice", :email "bernice.Gilbert@gmail.com"},
                               {:name "Adrian", :email "adrian7456@gmail.com"},
                               {:name "Ester", :email "esternyc@gmail.com"},
                               {:name "Andrew", :email "andrew.fillmore2@gmail.com"},
                               {:name "Felix", :email "felixfelix@hotmail.com"}])
        columns       (r/atom [{:id :name :header-label "Name"}
                               {:id :email :header-label "Email"}])
        expansion-row (fn [row]
                        [splunk.table/row {:key (str (:email row) "-expansion")}
                         [splunk.table/cell {:style    {:border-top "none"}
                                             :col-span 2}
                          [:dl
                           [:dt "Name"] [:dd (:name row)]
                           [:dt "Email"] [:dd (:email row)]]]])
        on-expansion  (fn [row]
                        (println "Expanded row for" (:email row)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model         model
        :columns       columns
        :row-key       :email
        :row-expansion "single"
        :expansion-row expansion-row
        :on-expansion  on-expansion}]])))


(defn ^:export table-expandable-rows-controlled [args]
  (let [model         (r/atom [{:name "Rylan", :email "Angelita_Weimann42@gmail.com"},
                               {:name "Amelia", :email "Dexter.Trantow57@hotmail.com"},
                               {:name "Estevan", :email "Aimee7@hotmail.com" :expanded true},
                               {:name "Florence", :email "Jarrod.Bernier13@yahoo.com"},
                               {:name "Tressa", :email "Yadira1@hotmail.com"},
                               {:name "Bernice", :email "bernice.Gilbert@gmail.com"},
                               {:name "Adrian", :email "adrian7456@gmail.com"},
                               {:name "Ester", :email "esternyc@gmail.com"},
                               {:name "Andrew", :email "andrew.fillmore2@gmail.com"},
                               {:name "Felix", :email "felixfelix@hotmail.com"}])
        columns       (r/atom [{:id :name :header-label "Name"}
                               {:id :email :header-label "Email"}])
        expansion-row (fn [row]
                        [splunk.table/row {:key (str (:email row) "-expansion")}
                         [splunk.table/cell {:style    {:border-top "none"}
                                             :col-span 2}
                          [:dl
                           [:dt "Name"] [:dd (:name row)]
                           [:dt "Email"] [:dd (:email row)]]]])
        on-expansion  (fn [{:keys [email]}]
                        (->> @model
                             (map (fn [row]
                                    (if (= email (:email row))
                                      (update row :expanded not)
                                      row)))
                             (reset! model)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model         model
        :columns       columns
        :row-key       :email
        :row-expansion "controlled"
        :expansion-row expansion-row
        :on-expansion  on-expansion}]])))



(defn vec-remove
  "Remove elem in coll by index."
  [coll pos]
  (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))

(defn vec-add
  "Add elem in coll by index."
  [coll pos el]
  (concat (subvec coll 0 pos) [el] (subvec coll pos)))

(defn vec-move
  "Move elem in coll by index"
  [coll pos1 pos2]
  (let [el (nth coll pos1)]
    (if (= pos1 pos2)
      coll
      (into [] (vec-add (vec-remove coll pos1) pos2 el)))))


(defn ^:export table-reorder-rows [args]
  (let [model    (r/atom [{:name "Rylan", :email "Angelita_Weimann42@gmail.com"},
                          {:name "Amelia", :email "Dexter.Trantow57@hotmail.com"},
                          {:name "Estevan", :email "Aimee7@hotmail.com" :expanded true},
                          {:name "Florence", :email "Jarrod.Bernier13@yahoo.com"},
                          {:name "Tressa", :email "Yadira1@hotmail.com"},
                          {:name "Bernice", :email "bernice.Gilbert@gmail.com"},
                          {:name "Adrian", :email "adrian7456@gmail.com"},
                          {:name "Ester", :email "esternyc@gmail.com"},
                          {:name "Andrew", :email "andrew.fillmore2@gmail.com"},
                          {:name "Felix", :email "felixfelix@hotmail.com"}])
        columns  (r/atom [{:id :name :header-label "Name"}
                          {:id :email :header-label "Email"}])
        move-row (fn [{:keys [from to]}]
                   (->> (vec-move @model from to)
                        (reset! model)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model       model
        :columns     columns
        :row-key     :email
        :on-move-row move-row}]])))


(defn ^:export table-reorder-columns [args]
  (let [model       (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                             {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                             {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                             {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                             {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},
                             {:name "Bernice", :age 41 :email "bernice.Gilbert@gmail.com"},
                             {:name "Adrian", :age 42 :email "adrian7456@gmail.com"},
                             {:name "Ester", :age 43 :email "esternyc@gmail.com"},
                             {:name "Andrew", :age 44 :email "andrew.fillmore2@gmail.com"},
                             {:name "Felix", :age 45 :email "felixfelix@hotmail.com"}])
        columns     (r/atom [{:id :name :header-label "Name"}
                             {:id :age :header-label "Age" :sort-key :age}
                             {:id :email :header-label "Email"}])
        move-column (fn [{:keys [from to]}]
                      (->> (vec-move @columns from to)
                           (reset! columns)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model          model
        :columns        columns
        :row-key        :email
        :on-move-column move-column}]])))


(defn ^:export table-resize-columns [args]
  (let [model         (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                               {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                               {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                               {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                               {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},
                               {:name "Bernice", :age 41 :email "bernice.Gilbert@gmail.com"},
                               {:name "Adrian", :age 42 :email "adrian7456@gmail.com"},
                               {:name "Ester", :age 43 :email "esternyc@gmail.com"},
                               {:name "Andrew", :age 44 :email "andrew.fillmore2@gmail.com"},
                               {:name "Felix", :age 45 :email "felixfelix@hotmail.com"}])
        columns       (r/atom [{:id :name :header-label "Name" :width 200 :min-width 80}
                               {:id :age :header-label "Age" :width 60 :min-width 40}
                               {:id :email :header-label "Email" :width 400 :min-width 120}])
        resize-column (fn [{:keys [column-id index width]}]
                        (->> @columns
                             (map (fn [col]
                                    (if (= (keyword column-id) (:id col))
                                      (assoc col :width (Math/max width (:min-width col)))
                                      col)))
                             (reset! columns)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [table
       {:model            model
        :columns          columns
        :row-key          :email
        :on-resize-column resize-column}]])))


(defn ^:export table-resize-columns-fill-layout [args]
  (let [model         (r/atom [{:name "Rylan", :age 42, :email "Angelita_Weimann42@gmail.com"},
                               {:name "Amelia", :age 24, :email "Dexter.Trantow57@hotmail.com"},
                               {:name "Estevan", :age 56, :email "Aimee7@hotmail.com"},
                               {:name "Florence", :age 71, :email "Jarrod.Bernier13@yahoo.com"},
                               {:name "Tressa", :age 38, :email "Yadira1@hotmail.com"},
                               {:name "Bernice", :age 41 :email "bernice.Gilbert@gmail.com"},
                               {:name "Adrian", :age 42 :email "adrian7456@gmail.com"},
                               {:name "Ester", :age 43 :email "esternyc@gmail.com"},
                               {:name "Andrew", :age 44 :email "andrew.fillmore2@gmail.com"},
                               {:name "Felix", :age 45 :email "felixfelix@hotmail.com"}])
        columns       (r/atom [{:id :name :header-label "Name" :width 200 :min-width 80}
                               {:id :age :header-label "Age" :width "auto" :min-width 40}
                               {:id :email :header-label "Email" :width "auto" :min-width 120}])
        resize-column (fn [{:keys [column-id index width]}]
                        (->> @columns
                             (map (fn [col]
                                    (if (= (keyword column-id) (:id col))
                                      (assoc col :width (Math/max width (:min-width col)))
                                      col)))
                             (reset! columns)))]
    (r/as-element
     [:> SplunkThemeProvider {:family "prisma" :colorScheme "light"}
      [:section {:style {:display "block" :margin "0 auto" :width "500px"}}
       [table
        {:model                 model
         :columns               columns
         :row-key               :email
         :on-resize-column      resize-column
         :resizable-fill-layout true}]]])))
