(ns thornydev.cmblog.w1.hello-compojure-enlive
  (:require [net.cgrand.enlive-html :as h]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]))

(defn content []
  (-> (slurp "resources/w1.tmpl")
      (h/sniptest [:h1] (h/content "Hello Ring-Compojure-Enlive!"))))

(defroutes approutes
  (GET "/" [] (content))
  (route/not-found "Page not found"))

(defn -main [& args]
  (defonce server (run-jetty #'approutes {:join? false :port 8081})))
