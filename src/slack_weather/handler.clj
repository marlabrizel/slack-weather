(ns slack-weather.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.core.async :refer [thread]]
            [environ.core :refer [env]]))

(defn validate-zip [zip]
  (re-matches #"^\d{5}$" (str/trim zip)))

(defroutes app-routes
  (POST "/slack" {:keys [params] :as request}
    (if (and (= "/weather" (:command params))
             (= auth-token (:token params))
             (validate-zip (:text params)))
      (do
        (thread (weather->slack (str/trim (:text params))))
        {:status 200
         :content-type "text/plain"
         :body "Fetching the weather..."})
      {:status 400
       :content-type "text/plain"
       :body "Please enter a valid zip code."}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def api-key
  (env :api-key))

(def hook-url
  (env :hook-url))

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

(defn weather->string
  "Transforms result of calling `get-weather-by-zip` into a readable forecast."
  [w]
  (str "Forecast for " (:name w) ": " (:temp w)" F and " (:conditions w)
       ". Humidity is at " (:humidity w) "%, with winds of " (:wind w) " mph."))

(defn post-to-slack
  [msg]
  (let [m (merge msg {:username "Weather Bot"
                      :icon_emoji ":sun_small_cloud:"})]
    (client/post hook-url {:body (json/write-str m)
                           :content-type :json})))

(defn weather->slack
  "Posts weather forecast to Slack"
  [zip]
  (let [weather (->
                  zip
                  get-weather-by-zip
                  weather->string)]
    (post-to-slack {:text weather})))

(def app
  (wrap-defaults app-routes site-defaults))
