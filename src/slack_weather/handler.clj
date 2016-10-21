(ns slack-weather.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (route/not-found "Not Found"))

(def api-key
  (env :api-key))

(def hook-url
  "https://hooks.slack.com/services/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")

(defn pull-response-values
  [m val-map]
  (into {} (for [[k v] val-map]
             [k (get-in m (if (sequential? v) v [v]))])))

(defn get-weather-by-zip
  "Returns current weather conditions for the given zipcode"
  [zipcode]
  (-> (str "http://api.openweathermap.org/data/2.5/weather?zip="
           zipcode
           ",us&units=imperial&APPID="
           api-key)
      client/get
      :body
      json/read-str
      (pull-response-values {:name ["name"]
                             :temp ["main" "temp"]
                             :humidity ["main" "humidity"]
                             :wind ["wind" "speed"]
                             :conditions ["weather" 0 "main"]})))

(defn post-to-slack
  [url msg]
  (let [m (merge msg {:username "Weather Bot"
                      :icon_emoji ":sun_small_cloud:"})]
    (client/post url {:body (json/write-str m)
                      :content-type :json})))

(def app
  (wrap-defaults app-routes site-defaults))
