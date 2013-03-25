(ns thornydev.cmblog.w4.controllers.login
  (:require [net.cgrand.enlive-html :as h]
            [me.raynes.fs :refer [base-name]]
            [thornydev.cmblog.w4.util :refer [escape]]            
            [thornydev.cmblog.w4.dao.user-dao :as userdao]
            [thornydev.cmblog.w4.dao.session-dao :as sessiondao]))


;; ---[ config settings ]--- ;;

(def login-html-path "resources/login.html")
(def redirect-route "/welcome")


;; ---[ helper fns ]--- ;;

(h/deftemplate login-template (base-name login-html-path) [username]
  [:span.login_error]                   (h/content "Invalid Login")
  [[:input (h/attr= :name "username")]] (h/set-attr :value (escape username))) 

(defn- login-error-output [username]
  (apply str (login-template username)))

(defn- send-to-welcome-page [session-id]
  (assoc (ring.util.response/redirect redirect-route)
    :cookies {"session" session-id}))


;; ---[ compojure handler fns ]--- ;;

(defn process-login [username password]
  (if-let [user (userdao/validate-login username password)]
    (-> (:_id user)
        sessiondao/start-session
        send-to-welcome-page)
    (login-error-output username)))

(defn show-login-page []
  (slurp login-html-path))

