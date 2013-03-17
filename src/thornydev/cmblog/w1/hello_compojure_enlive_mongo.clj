(ns thornydev.cmblog.w1.hello-compojure-enlive-mongo
  (:require [net.cgrand.enlive-html :as h]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [monger.core :as mg]
            [monger.collection :as mc]))

(defn mongo-lookup []
  (:name (mc/find-one-as-map "hello" {:name #"Mongo"} [:name])))

(defn content []
  (-> (slurp "resources/w1.tmpl")
      (h/sniptest [:h1] (h/content (str "Hello " (mongo-lookup))))))

(defroutes approutes
  (GET "/" [] (content))
  (route/not-found "Page not found"))

(defn mongo-conn-init []
  (mg/connect! {:host "cypher" :port 27017})
  (mg/set-db! (mg/get-db "course")))

(defn -main [& args]
  (defonce server (run-jetty #'approutes {:join? false :port 8081}))
  (mongo-conn-init))
