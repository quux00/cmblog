(ns thornydev.cmblog.w1.hello-ring
  (:require [ring.adapter.jetty :refer :all]
            [hiccup.core :refer :all]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]))

(defn display []
  (html [:h2 "Hello from Ring and Compojure"]))

;; defroutes defines a ring handler ("app")
(defroutes myroutes
  (GET "/" [] (display))
  (route/not-found "Page not found"))

(defonce server (run-jetty #'myroutes {:join? false :port 8081}))
