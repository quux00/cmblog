(ns thornydev.cmblog.w4.controllers.welcome
  (:require [net.cgrand.enlive-html :as h]
            [me.raynes.fs :refer [base-name]]
            [thornydev.cmblog.w4.util :refer [escape]]            
            [thornydev.cmblog.w4.dao.session-dao :as sessiondao]))


;; ---[ config settings ]--- ;;

(def welcome-html-path "resources/welcome.html")
(def redirect-route "/signup")


;; ---[ helper fns ]--- ;;

(h/deftemplate welcome-template (base-name welcome-html-path) [username]
  [:span#username] (h/content username))


;; ---[ compojure handler fn ]--- ;;

(defn show-welcome-page [session-id]
  (if-let [username (-> session-id
                        sessiondao/find-username-by-session-id)]
    (apply str (welcome-template (escape username)))
    (do (println "welcome() can't identify the user, redirecting to signup")
        (ring.util.response/redirect redirect-route))))
