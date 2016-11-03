(ns slack-weather.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.string :as str]
            [clojure.core.async :refer [thread]]
            [environ.core :refer [env]]
            [slack-weather.weather :as w])
  (:gen-class))

(def auth-token
  (env :auth-token))

(defroutes app-routes
  (POST "/slack" {:keys [params] :as request}
    (if (and (= "/weather" (:command params))
             (= auth-token (:token params))
             (w/validate-zip (:text params)))
      (do
        (thread (w/weather-to-slack (str/trim (:text params))))
        {:status 200
         :content-type "text/plain"
         :body "Fetching the weather..."})
      {:status 400
       :content-type "text/plain"
       :body "Please enter a valid zip code."}))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))

(defn -main [& args]
  (run-jetty app {:port (Integer/parseInt (env :port))}))
