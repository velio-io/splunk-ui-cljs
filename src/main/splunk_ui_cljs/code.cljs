(ns splunk-ui-cljs.code
  (:require
   [react :as react]
   [reagent.core :as r]
   [goog.object :as go]
   [splunk-ui-cljs.utils :as utils]
   [nextjournal.clojure-mode :as cm-clj]
   ["@codemirror/commands" :refer [history historyKeymap]]
   ["@codemirror/language" :refer [foldGutter syntaxHighlighting defaultHighlightStyle]]
   ["@codemirror/state" :refer [EditorState Text]]
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
   (history)
   (syntaxHighlighting defaultHighlightStyle)
   (view/drawSelection)
   (foldGutter)
   (.. EditorState -allowMultipleSelections (of true))
   cm-clj/default-extensions
   (.of view/keymap cm-clj/complete-keymap)
   (.of view/keymap historyKeymap)])


(defn code [{:keys [model on-update]}]
  (let [code-ref        (react/createRef nil)
        view            (r/atom nil)
        local-state     (r/atom nil)
        update-fn       (fn [^EditorView view]
                          (let [old-doc @local-state
                                doc     (go/getValueByKeys view "state" "doc")
                                new-doc (.toString doc)]
                            (when (not= new-doc old-doc)
                              (reset! local-state new-doc)
                              (on-update new-doc))))
        update-listener (.. EditorView -updateListener (of update-fn))
        extensions      (-> extensions
                            (conj update-listener)
                            (to-array))
        document        (->> model utils/model->value (reset! local-state))
        editor-config   #js {:doc document :extensions extensions}]
    (r/create-class
     {:display-name "code"

      :component-did-mount
      (fn [_]
        (let [element     (go/get code-ref "current")
              state       (.create EditorState editor-config)
              editor-view (new EditorView #js {:state state :parent element})]
          (reset! view editor-view)))

      :component-will-unmount
      (fn [_]
        (.destroy ^EditorView @view))

      :reagent-render
      (fn [_]
        [:div {:ref code-ref}])})))
