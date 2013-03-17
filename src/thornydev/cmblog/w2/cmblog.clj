(ns thornydev.cmblog.w2.cmblog
  (:require [ring.adapter.jetty          :refer [run-jetty]]
            [ring.middleware.params      :refer [wrap-params]]
            [ring.middleware.cookies     :refer [wrap-cookies]]
            [compojure.core              :refer [GET POST defroutes]]
            [compojure.route             :as route]
            [net.cgrand.enlive-html      :as h]
            [thornydev.cmblog.w2.signup  :refer [show-signup-page process-signup]]
            [thornydev.cmblog.w2.welcome :refer [show-welcome-page]]
            [thornydev.cmblog.w2.login   :refer [show-login-page process-login]]
            [thornydev.cmblog.w2.logout  :refer [process-logout]]))

;; directory where the enlive templates live
(def tmpl-dir "resources")

(declare show-home-page handle-error path-to)


;; ---[ Main Compojure Routing Table ]--- ;;

(defroutes approutes
  (GET  "/"        []                     (show-home-page))
  (GET  "/signup"  []                     (show-signup-page))
  (POST "/signup"  {fparams :form-params} (process-signup fparams))
  (GET  "/welcome" {cookies :cookies}     (show-welcome-page cookies))
  (GET  "/login"   []                     (show-login-page))
  (POST "/login"   [username password]    (process-login username password))
  (GET  "/logout"  {cookies :cookies}     (process-logout cookies))
  (GET  "/internal_error" []              (handle-error))
  (route/not-found "Page not found"))


;; ---[ route handler fn ]--- ;;

(defn show-home-page []
  (slurp (path-to "blog-template.html")))

(defn handle-error []
  (-> (path-to "error-template.html")
      slurp 
      (h/sniptest [:span.errmsg] (h/content "System has encountered an error."))))


;; ---[ helper fns ]--- ;;

(defn path-to [tmpl]
  (str tmpl-dir "/" tmpl))


;; ---[ set up the server and app middleware ]--- ;;

(def app (-> approutes wrap-cookies wrap-params))

(defn -main [& args]
  (def server (run-jetty #'app {:join? false :port 8083})))
