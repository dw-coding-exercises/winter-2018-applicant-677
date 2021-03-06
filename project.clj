(defproject my-ex "0.1.0-SNAPSHOT"
  :description "An anonymous Democracy Works coding exercise"
  :min-lein-version "2.7.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [clj-http "0.6.0"]]
  :plugins [[lein-ring "0.12.1"]]
  :ring {:handler my-ex.core/handler}
  :aliases {"submit" ["run" "-m" "my-ex.submit"]})
