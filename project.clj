 (defproject dice-api "0.1.0-SNAPSHOT"
   :description "FIXME: write description"
   :dependencies [[org.clojure/clojure "1.8.0"]
                  [metosin/compojure-api "1.1.11"]]
   :min-lein-version "2.0.0"
   :ring {:handler dice-api.handler/app}
   :uberjar-name "dice-api-0.1.0-standalone.jar"
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                   :plugins [[lein-ring "0.12.0"]]}})
