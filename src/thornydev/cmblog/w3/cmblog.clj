(ns thornydev.cmblog.w3.cmblog
  (:require [ring.adapter.jetty           :refer [run-jetty]]
            [ring.middleware.params       :refer [wrap-params]]
            [ring.middleware.cookies      :refer [wrap-cookies]]
            [ring.middleware.resource     :refer [wrap-resource]]
            [compojure.core               :refer [GET POST defroutes]]
            [compojure.route              :as route]
            [net.cgrand.enlive-html       :as h]
            [thornydev.cmblog.w3.homepage :refer [show-home-page]]
            [thornydev.cmblog.w3.post     :refer [show-post add-new-comment]]
            [thornydev.cmblog.w3.newpost  :refer [show-new-post-form process-new-post]]
            [thornydev.cmblog.w3.signup   :refer [show-signup-page process-signup]]
            [thornydev.cmblog.w3.welcome  :refer [show-welcome-page]]
            [thornydev.cmblog.w3.login    :refer [show-login-page process-login]]
            [thornydev.cmblog.w3.logout   :refer [process-logout]]))

;; directory where the enlive templates live
(def tmpl-dir "resources")

(declare handle-error path-to get-session-id handle-post-not-found)


;; ---[ Main Compojure Routing Table ]--- ;;

(defroutes approutes
  (GET  "/"        {cks :cookies}         (show-home-page (get-session-id cks)))
  (GET  "/signup"  []                     (show-signup-page))
  (POST "/signup"  {fparams :form-params} (process-signup fparams))
  (GET  "/welcome" {cks :cookies}         (show-welcome-page (get-session-id cks)))
  (GET  "/login"   []                     (show-login-page))
  (POST "/login"   [username password]    (process-login username password))
  (GET  "/logout"  {cks :cookies}         (process-logout (get-session-id cks)))
  (GET  "/post/:permalink" [permalink :as {cks :cookies}]
        (show-post (get-session-id cks) permalink))
  (POST "/newcomment" {cks :cookies fparams :form-params}
        (add-new-comment fparams (get-session-id cks)))
  (GET  "/newpost"        {cks :cookies}  (show-new-post-form (get-session-id cks)))
  (POST "/newpost"        []              (println "NOT YET IMPLEMENTED!!!"))
  (GET  "/post_not_found" []              (handle-post-not-found))
  (GET  "/internal_error" []              (handle-error))
  (route/resources "/") ;; look in resources/public for static resources (eg, css)
  (route/not-found "Page not found"))


;; ---[ route handler fn ]--- ;;

(defn handle-error []
  (-> (path-to "error-template.html")
      slurp 
      (h/sniptest [:span.errmsg] (h/content "System has encountered an error."))))

(defn handle-post-not-found []
  (slurp (path-to "post-not-found.html")))

;; ---[ helper fns ]--- ;;

(defn get-session-id [cookies]
  (-> cookies
      (get "session")
      :value))

(defn path-to [tmpl]
  (str tmpl-dir "/" tmpl))


;; ---[ set up the server and app middleware ]--- ;;

(def app (-> approutes
             (wrap-resource "public")
             wrap-cookies
             wrap-params))

(defn -main [& args]
  (def server (run-jetty #'app {:join? false :port 8083})))
