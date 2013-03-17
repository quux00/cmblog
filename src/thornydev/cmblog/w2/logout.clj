(ns thornydev.cmblog.w2.logout
  (:require [thornydev.cmblog.w2.session-dao :as sessiondao]))


;; ---[ config settings ]--- ;;

(def redirect-route "/login")


;; ---[ helper fns ]--- ;;

(defn- end-session-and-delete-cookie [session-id]
  (sessiondao/end-session session-id)
  (assoc (ring.util.response/redirect redirect-route)
    :cookies {"session" {:value session-id, :max-age 0}}))


;; ---[ compojure handler fn ]--- ;;

(defn process-logout [cookies]
  (if-let [session-id (-> (get "session" cookies) :value)]
    (end-session-and-delete-cookie session-id)
    (ring.util.response/redirect redirect-route)))
