(ns splunk-ui-cljs.core
  (:require
   [splunk-ui-cljs.input-text :as splunk.input-text]
   [splunk-ui-cljs.line :as splunk.line]
   [splunk-ui-cljs.button :as splunk.button]
   [splunk-ui-cljs.checkbox :as splunk.checkbox]))


(def input-text splunk.input-text/input-text)
(def input-password splunk.input-text/input-password)
(def input-textarea splunk.input-text/input-textarea)

(def line splunk.line/line)

(def button splunk.button/button)

(def checkbox splunk.checkbox/checkbox)