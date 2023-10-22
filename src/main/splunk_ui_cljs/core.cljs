(ns splunk-ui-cljs.core
  (:require
   [splunk-ui-cljs.input-text :as splunk.input-text]
   [splunk-ui-cljs.line :as splunk.line]
   [splunk-ui-cljs.button :as splunk.button]
   [splunk-ui-cljs.checkbox :as splunk.checkbox]
   [splunk-ui-cljs.dropdown :as splunk.dropdown]
   [splunk-ui-cljs.selection-list :as splunk.selection-list]
   [splunk-ui-cljs.label :as splunk.label]
   [splunk-ui-cljs.scroll :as splunk.scroll]
   [splunk-ui-cljs.layout :as splunk.layout]
   [splunk-ui-cljs.table :as splunk.table]
   [splunk-ui-cljs.tab-layout :as splunk.tab-layout]
   [splunk-ui-cljs.message :as splunk.message]
   [splunk-ui-cljs.modal :as splunk.modal]
   [splunk-ui-cljs.cards :as splunk.cards]))


(def input-text splunk.input-text/input-text)
(def input-password splunk.input-text/input-password)
(def input-textarea splunk.input-text/input-textarea)

(def line splunk.line/line)

(def button splunk.button/button)

(def checkbox splunk.checkbox/checkbox)

(def single-dropdown splunk.dropdown/single-dropdown)
(def tag-dropdown splunk.dropdown/tag-dropdown)
(def selection-list splunk.selection-list/selection-list)

(def label splunk.label/label)

(def scroller splunk.scroll/scroller)

(def row splunk.layout/row)
(def column splunk.layout/column)
(def layout splunk.layout/layout)
(def h-box splunk.layout/h-box)
(def v-box splunk.layout/v-box)

(def table-row splunk.table/row)
(def table-cell splunk.table/cell)
(def table splunk.table/table)

(def tab-layout splunk.tab-layout/tab-layout)

(def message splunk.message/message)
(def message-link splunk.message/message-link)
(def message-title splunk.message/message-title)

(def modal splunk.modal/modal)
(def modal-header splunk.modal/header)
(def modal-body splunk.modal/body)
(def modal-footer splunk.modal/footer)

(def card splunk.cards/card)
(def card-header splunk.cards/header)
(def card-body splunk.cards/body)
(def card-footer splunk.cards/footer)
