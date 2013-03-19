(ns thornydev.cmblog.w3.cmblog
  (:require [ring.adapter.jetty           :refer [run-jetty]]
            [ring.middleware.params       :refer [wrap-params]]
            [ring.middleware.cookies      :refer [wrap-cookies]]
            [ring.middleware.resource     :refer [wrap-resource]]
            [compojure.core               :refer [GET POST defroutes]]
            [compojure.route              :as route]
            [net.cgrand.enlive-html       :as h]
            [thornydev.cmblog.w3.homepage :refer [show-home-page]]
            [thornydev.cmblog.w3.signup   :refer [show-signup-page process-signup]]
            [thornydev.cmblog.w3.welcome  :refer [show-welcome-page]]
            [thornydev.cmblog.w3.login    :refer [show-login-page process-login]]
            [thornydev.cmblog.w3.logout   :refer [process-logout]]))

;; directory where the enlive templates live
(def tmpl-dir "resources")

(declare handle-error path-to)


;; ---[ Main Compojure Routing Table ]--- ;;

(defroutes approutes
  (GET  "/"        {cookies :cookies}     (show-home-page cookies))
  (GET  "/post/:permalink") [permalink]   (show-post permalink)
  (GET  "/signup"  []                     (show-signup-page))
  (POST "/signup"  {fparams :form-params} (process-signup fparams))
  (GET  "/welcome" {cookies :cookies}     (show-welcome-page cookies))
  (GET  "/login"   []                     (show-login-page))
  (POST "/login"   [username password]    (process-login username password))
  (GET  "/logout"  {cookies :cookies}     (process-logout cookies))
  (GET  "/internal_error" []              (handle-error))
  ;; (route/resources "/") ;; look in resources/public for static resources (e.g., css)
  (route/not-found "Page not found"))


;; ---[ route handler fn ]--- ;;

(defn handle-error []
  (-> (path-to "error-template.html")
      slurp 
      (h/sniptest [:span.errmsg] (h/content "System has encountered an error."))))


;; ---[ helper fns ]--- ;;

(defn path-to [tmpl]
  (str tmpl-dir "/" tmpl))


;; ---[ set up the server and app middleware ]--- ;;

(def app (-> approutes
             (wrap-resource "public")
             wrap-cookies
             wrap-params))

(defn -main [& args]
  (def server (run-jetty #'app {:join? false :port 8083})))
