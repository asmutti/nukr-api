 (defproject nukr "0.1.0-SNAPSHOT"
   :description "FIXME: write description"
   :dependencies [[org.clojure/clojure "1.8.0"]
                  [metosin/compojure-api "1.1.11"]
                  [ring/ring-mock "0.4.0"]]
   :ring {:handler nukr.handler/app}
   :uberjar-name "server.jar"
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]
                                   [midje "1.9.8"]]
                   :plugins [[lein-ring "0.12.5"]
                             [lein-midje "3.2.1"]]}})
