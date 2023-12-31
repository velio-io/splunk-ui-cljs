(ns splunk-ui-cljs.code
  (:require
   [react :as react]
   [reagent.core :as r]
   [goog.object :as go]
   [splunk-ui-cljs.utils :as utils]
   [nextjournal.clojure-mode :as cm-clj]
   ["@codemirror/commands" :refer [history historyKeymap]]
   ["@codemirror/language" :refer [foldGutter syntaxHighlighting defaultHighlightStyle]]
   ["@codemirror/autocomplete" :refer [autocompletion startCompletion completeFromList]]
   ["@codemirror/state" :refer [EditorState]]
   ["@codemirror/view" :as view :refer [EditorView]]))


(def theme
  #js {".cm-content"             #js {:white-space "pre-wrap"
                                      :padding     "10px 0"
                                      :flex        "1 1 0"}
       "&.cm-focused"            #js {:outline "0 !important"}
       ".cm-line"                #js {:padding     "0 9px"
                                      :line-height "1.6"
                                      :font-size   "14px"
                                      :font-family "\"Fira Mono\", monaco, monospace"}
       ".cm-matchingBracket"     #js {:border-bottom "1px solid #31afd0"
                                      :color         "inherit"}
       ".cm-gutters"             #js {:background "transparent"
                                      :border     "none"}
       ".cm-gutterElement"       #js {:margin-left "5px"}
       ;; only show cursor when focused
       ".cm-cursor"              #js {:visibility "hidden"}
       "&.cm-focused .cm-cursor" #js {:visibility "visible"}})


(def extensions
  [(.theme EditorView theme)
   (syntaxHighlighting defaultHighlightStyle)
   (foldGutter)
   (view/drawSelection)
   (.. EditorState -allowMultipleSelections (of true))
   cm-clj/default-extensions
   (.of view/keymap cm-clj/complete-keymap)
   (history)
   (.of view/keymap historyKeymap)])


(defn dispatch-new-doc [{:keys [view local-state]} new-doc]
  (let [old-doc @local-state]
    (when (not= new-doc old-doc)
      (let [doc (go/getValueByKeys @view "state" "doc")]
        (.dispatch ^EditorView @view
                   #js {:changes #js {:from 0 :to (go/get doc "length") :insert new-doc}})))))


(defn handle-model-change [{:keys [model view local-state]}]
  (let [dispatch-doc-fn (partial dispatch-new-doc {:view view :local-state local-state})]
    (if (satisfies? IWatchable model)
      (do (remove-watch model :model-change)
          (add-watch model :model-change #(dispatch-doc-fn %4)))
      (dispatch-doc-fn model))))


(defn code
  "Code editor component
   - `model` (required) Code listing to show in the code editor. Could be an atom
   - `on-change` (optional) Function that will receive code changes as argument
   - `width` (optional) Root element width
   - `height` (optional) Root element height
   Available shortcuts in the editor:
   - barf-backward `⌃ + ⌥ + →` Shrink collection backwards by one form
   - barf-forward `⌃ + ←` or `⌘ + ⇧ + J` Shrink collection forwards by one form
   - kill `⌃ + K`  Remove all forms from cursor to end of line
   - nav-left `⌥ + ←`  Move cursor one unit to the left (shift: selects this region)
   - nav-select-left `⇧ + ⌥ + ←`
   - nav-right `⌥ + →`  Move cursor one unit to the right (shift: selects this region)
   - nav-select-right `⇧ + ⌥ + →`
   - selection-grow `⌥ + ↑` or `⌘ + 1` Grow selections
   - selection-return `⌥ + ↓` or `⌘ + 2` Shrink selections
   - slurp-backward `⌃ + ⌥ + ←`  Grow collection backwards by one form
   - slurp-forward `⌃ + →` or `⌘ + ⇧ + K` Expand collection to include form to the right
   - unwrap `⌥ + S`  Lift contents of collection into parent
   - start-autocompletion `⇧ + space` Open the autocompletion suggestions"
  [{:keys [model on-update completions]}]
  (let [code-ref        (react/createRef nil)
        view            (r/atom nil)
        local-state     (r/atom nil)
        update-fn       (fn [^EditorView view]
                          (let [old-doc @local-state
                                doc     (go/getValueByKeys view "state" "doc")
                                new-doc (.toString doc)]
                            (when (and (fn? on-update) (not= new-doc old-doc))
                              (reset! local-state new-doc)
                              (on-update new-doc))))
        update-listener (.. EditorView -updateListener (of update-fn))
        completions     (when (some? completions)
                          (clj->js completions))
        extensions      (cond-> extensions
                                completions (conj (autocompletion #js {:override #js [(completeFromList completions)]})
                                                  (.of view/keymap #js [#js {:key "Shift-Space" :run startCompletion}]))
                                :always (conj update-listener))
        document        (->> model utils/model->value (reset! local-state))
        editor-config   #js {:doc document :extensions (to-array extensions)}]
    (r/create-class
     {:display-name "code"

      :component-did-mount
      (fn [_]
        (let [element     (go/get code-ref "current")
              state       (.create EditorState editor-config)
              editor-view (new EditorView #js {:state state :parent element})]
          (reset! view editor-view)
          (handle-model-change {:model model :view view :local-state local-state})))

      :component-did-update
      (fn [this old-argv]
        (let [{:keys [model]} (second (r/argv this))
              old-model (-> old-argv second :model)]
          (when (satisfies? IWatchable old-model)
            (remove-watch old-model :model-change))
          (handle-model-change {:model model :view view :local-state local-state})))

      :component-will-unmount
      (fn [_]
        (.destroy ^EditorView @view))

      :reagent-render
      (fn [{:keys [width height]}]
        [:div {:ref    code-ref
               :style  {:width  width
                        :height height}}])})))
