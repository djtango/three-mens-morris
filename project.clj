(defproject three-mens-morris "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [midje "1.9.0-alpha6"]]

  :plugins [[lein-midje "3.1.1"]]

  :source-paths ["src" "spec"]

  :repl-options {:init-ns three-mens-morris.core
                 :init (do (require '[midje.repl :as midje.repl])
                           (midje.repl/autotest))})
