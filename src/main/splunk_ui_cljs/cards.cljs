(ns splunk-ui-cljs.cards
  (:require
   [reagent.core :as r]
   [goog.object :as go]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/Card" :default Card :refer [Header Body Footer]]
   ["@splunk/react-ui/CardLayout" :default CardLayout]))


(def layout
  (r/adapt-react-class CardLayout))


(def body
  (r/adapt-react-class Body))


(def footer
  (r/adapt-react-class Footer))


(defn header
  "- `element-ref` (optional) A React ref which is set to the DOM element when the component mounts and null when it unmounts.
   - `style` (optional) CSS styles map
   - `action-primary` (optional) Adds a primary action to the header. For best results, use an icon-only button style.
   - `actions-secondary` (optional) Adds a secondary actions dropdown menu to the header. Make this prop a Menu.
   - `anchor` (optional) Make the title an anchor so it can be bookmarked with a fragment.
   - `title` (optional) Used as the main heading.
   - `subtitle` (optional) Used as the subheading.
   - `truncate-title` (optional) Do not wrap Title and Subtitle. Long titles will truncate with an ellipsis."
  [{:keys [element-ref style action-primary actions-secondary anchor title subtitle truncate-title]}]
  (let [component (r/current-component)
        children  (r/children component)]
    (into
     [:> Header (utils/assoc-some {}
                  :elementRef element-ref
                  :style style
                  :actionPrimary (utils/value->element action-primary)
                  :actionsSecondary (utils/value->element actions-secondary)
                  :anchor anchor
                  :subtitle subtitle
                  :title title
                  :truncateTitle truncate-title)]
     children)))


(defn card
  "Card displays information that can contain a header, body, and footer.
   - `model` (optional) Returns a value on click. Use when composing or if you have more than one selectable Card.
   - `style` (optional) CSS styles map
   - `element-ref` (optional) A React ref which is set to the DOM element when the component mounts and null when it unmounts.
   - `selected` (optional) Renders Card as selected if set to true. Can be an atom.
   - `show-border` (optional) Includes a border on the Card if set to true.
   - `on-click` (optional) Callback when the Card is clicked.
   - `to` (optional) Takes a URL to go to when the Card is clicked.
   - `open-in-new-context` (optional) To open the to link in a new window, set openInNewContext to true."
  [{:keys [model style element-ref selected show-border on-click to open-in-new-context]}]
  (let [component (r/current-component)
        children  (r/children component)
        value     (utils/model->value model)
        selected  (utils/model->value selected)
        ;; get the props passed by CardLayout
        {:keys [minWidth maxWidth margin]} (utils/component-props component)]
    (into
     [:> Card (utils/assoc-some {}
                :elementRef element-ref
                :style style
                :onClick (when (fn? on-click)
                           (fn [_event params]
                             (on-click (go/get params "value"))))
                :value value
                :to to
                :openInNewContext open-in-new-context
                :selected selected
                :showBorder show-border
                :minWidth minWidth
                :maxWidth maxWidth
                :margin margin)]
     children)))
