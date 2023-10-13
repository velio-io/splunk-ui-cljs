(ns splunk-ui-cljs.label
  (:require
   [reagent.core :as r]
   [splunk-ui-cljs.utils :as utils]
   ["@splunk/react-ui/ControlGroup" :default ControlGroup]))


(def control-group
  (r/adapt-react-class ControlGroup))


(defn label
  "A container for controls that handles various style and accessibility issues.
   - `label` (required) Label text
   - `label-for` (optional) Override the for attribute of the label. See the component description for details.
   - `position` (optional) Place where to place a label. One of left, top
   - `label-width` (optional) When labelPosition is left, the width of the label in pixels or a value with a unit.
   - `layout` (optional) A layout defines how controls are aligned and displayed. The 'fillJoin' layout is not supported by the scp theme.
   - `help` (optional) A help test displayed at the bottom. Could be a string or rect (reagent) element e.g. [:div ...]
   - `status` (optional) Highlight the control group as having an error and optionally provide error text. If error text is provided, displays it below the control. The label will turn red, and the help text will turn red if error text was not provided. Child components will not turn red.
   - `required?` (optional) Sets the control required and adds an asterisk before the label.
   - `size` (optional) The size of the text label. Supported by the enterprise theme only.
   - `tooltip` (optional) Displays a tooltip beside the label.
   - `tooltip-placement` (optional) If a tooltip is provided, sets its default placement."
  [{:keys [layout status help label label-for position label-width
           required? size tooltip tooltip-placement]}]
  (let [component (r/current-component)
        help      (utils/value->element help)
        status    (when (some? status)
                    (keyword status))]
    (into
     [control-group
      (utils/assoc-some
        {:label label}
        :labelFor label-for
        :labelPosition position
        :labelWidth label-width
        :required required?
        :size size
        :tooltip tooltip
        :tooltipDefaultPlacement tooltip-placement
        :controlsLayout layout
        :error (= status :error)
        :help help)]
     ;; we have to convert reagent forms into react elements
     ;; because of ControlGroup internal logic
     ;; ControlGroup will modify its children elements and will provide some additional properties to it
     (map #(let [[comp props] %]
             (r/create-element
              (r/reactify-component comp)
              (utils/->js-shallow props))))
     (r/children component))))
