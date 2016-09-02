(ns slack-weather.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as client]
            [clojure.data.json :as json]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def hook-url
  "https://hooks.slack.com/services/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")

(defn post-to-slack
  [url msg]
  (client/post url {:body (json/write-str msg)
                    :content-type :json}))

(def app
  (wrap-defaults app-routes site-defaults))
