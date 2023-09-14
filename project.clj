(defproject io.velio/splunk-ui-cljs "0.0.1"
  :description "Clojurescript wrapper for the Splunk UI Toolkit"
  ;; this is optional, add what you want or remove it
  :license {:name "Aapche License 2.0"
            :url  "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies
  [[org.clojure/clojure "1.11.1"]
   ;; always use "provided" for Clojure(Script)
   [org.clojure/clojurescript "1.10.520" :scope "provided"]]

  :source-paths
  ["src/main"]

  :plugins
  [[lein-codox "0.10.8"]]

  :codox
  {:output-path "docs"
   :language    :clojurescript
   :metadata    {:doc/format :markdown}})
