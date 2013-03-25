(ns thornydev.cmblog.w4.controllers.logout
  (:require [thornydev.cmblog.w4.dao.session-dao :as sessiondao]))


;; ---[ config settings ]--- ;;

(def redirect-route "/login")


;; ---[ helper fns ]--- ;;

(defn- end-session-and-delete-cookie [session-id]
  (sessiondao/end-session session-id)
  (assoc (ring.util.response/redirect redirect-route)
    :cookies {"session" {:value session-id, :max-age 0}}))


;; ---[ compojure handler fn ]--- ;;

(defn process-logout [session-id]
  (if session-id
    (end-session-and-delete-cookie session-id)
    (ring.util.response/redirect redirect-route)))
