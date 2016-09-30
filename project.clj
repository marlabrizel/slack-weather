(defproject slack-weather "0.1.0-SNAPSHOT"
  :description "Slackbot to fetch the weather"
  :url "http://github.com/marlabrizel/slack-weather"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/core.async "0.2.385"]
                 [clj-http "2.2.0"]
                 [environ "1.1.0"]]
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]]
  :ring {:handler slack-weather.handler/app}
  :profiles
  {:dev-common {:dependencies [[javax.servlet/servlet-api "2.5"]
                               [ring/ring-mock "0.3.0"]]}
   :dev-overrides {}
   :dev [:dev-common :dev-overrides]})
