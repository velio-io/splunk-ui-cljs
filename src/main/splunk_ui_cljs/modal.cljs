(ns splunk-ui-cljs.modal
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Modal" :default Modal :refer [Header Body Footer]]))


(def body
  (r/adapt-react-class Body))


(def footer
  (r/adapt-react-class Footer))


(defn header [{:keys [icon subtitle title on-close]}]
  (let [component (r/current-component)
        children  (r/children component)]
    (into
     [:> Header (utils/assoc-some {}
                  :icon (utils/value->element icon)
                  :subtitle subtitle
                  :title title
                  :onRequestClose on-close)]
     children)))


(defn modal
  "Modal renders an overlay that captures the focus of the page.
   - `model` (required) Set to true if the Modal is currently open. Otherwise, set to false. Can be a reagent atom
   - `divider` (optional) Show dividers between header, body and footer.
   - `initial-focus` (optional) Allows focus to be set to a component other than the default. Supports first (first focusable element in the modal), container (focus the modal itself), or a ref.
   - `on-close` (optional) Called when a close event occurs.
     The callback is passed the event and a reason, which is either 'escapeKey' or 'clickAway'.
     Generally, use this callback to toggle the open prop.
     This callback must return focus to the invoking element or other element that follows the logical flow of the application."
  [{:keys [model divider initial-focus on-close]}]
  (let [open      (utils/model->value model)
        component (r/current-component)
        children  (r/children component)]
    (into
     [:> Modal (utils/assoc-some {:open open}
                 :divider divider
                 :initialFocus initial-focus
                 :onRequestClose (when (fn? on-close)
                                   on-close))]
     children)))
