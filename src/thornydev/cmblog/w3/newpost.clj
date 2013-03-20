(ns thornydev.cmblog.w3.newpost
  (:require [clojure.string :refer [join]]
            [net.cgrand.enlive-html :as h]
            [me.raynes.fs :refer [base-name]]
            [thornydev.cmblog.w3.util :refer [escape]]
            [thornydev.cmblog.w3.session-dao :as sessiondao]
            [thornydev.cmblog.w3.blogpost-dao :as postdao]))


;; ---[ config settings ]--- ;;

(def newpost-html-path "resources/newpost-template.html")
(def redirect-route "/login")



;; ---[ enlive template ]--- ;;

(h/deftemplate newpost-template (base-name newpost-html-path) [username post post-error]
  ;; page header section
  [:div#usercontrols] (h/set-attr :class (if (seq username) "visible" "invisible"))
  [:span#username]    (h/content (escape username))

  ;; form body
  [:span#post-error]                   (h/content post-error)
  [[:input (h/attr= :name "subject")]] (h/set-attr :value (:title post))
  [:textarea]                          (h/content (:body post))
  [[:input (h/attr= :name "tags")]]    (h/set-attr :value (join " " (:tags post))))



;; ---[ compojure handler fns ]--- ;;

(defn show-new-post-form [session-id]
  (if-let [username (sessiondao/find-username-by-session-id session-id)]
    (apply str (newpost-template username nil nil))
    (ring.util.response/redirect redirect-route)))

(defn process-new-post []
  (throw (IllegalStateException. "not yet implemented"))
  )
