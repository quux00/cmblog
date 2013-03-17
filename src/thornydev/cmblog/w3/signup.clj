(ns thornydev.cmblog.w3.signup
  (:require [net.cgrand.enlive-html :as h]
            [me.raynes.fs :refer [base-name]]
            [thornydev.cmblog.w3.user-dao :as userdao]
            [thornydev.cmblog.w3.session-dao :as sessiondao])
  (:import (org.apache.commons.lang3 StringEscapeUtils)))


;; ---[ config settings ]--- ;;

(def signup-html-path "resources/signup.html")
(def redirect-route "/welcome")


;; ---[ helper fns ]--- ;;

(defn- escape [s]
  (StringEscapeUtils/escapeHtml4 s))

(defn- validate-signup
  "Validates a signup attempt. Returns nil if successful.
   Returns a map of errors if the signup was not validated."
  [username password verify email]
  (merge
   (when-not (re-matches #"^[a-zA-Z0-9_-]{3,20}$" username)
     {:username-error "Invalid username. Try just letters and numbers."})
   (when-not (re-matches #"^.{3,20}$" password)
     {:password-error "Invalid password."})
   (when-not (empty? email)
     (when-not (re-matches #"^[\S]+@[\S]+\.[\S]+$" email)
       {:email-error "Invalid Email Address"}))
   (when (not= password verify)
     {:verify-error "Passwords must match"})))


;; enlive replacement template
(h/deftemplate signup-template (base-name signup-html-path) [error-map username email]
  [:span#username_error] (h/content (:username-error error-map))
  [:span#password_error] (h/content (:password-error error-map))
  [:span#verify_error]   (h/content (:verify-error error-map))
  [:span#email_error]    (h/content (:email-error error-map))
  [[:input (h/attr= :name "username")]] (h/set-attr :value (escape username))
  [[:input (h/attr= :name "email")]]    (h/set-attr :value (escape email)))


(defn- signup-error-output [error-map username email]
  (apply str (signup-template error-map username email)))


(defn- start-session [username]
  (let [session-id (sessiondao/start-session username)]
    (println "session ID is" session-id)
    (assoc (ring.util.response/redirect redirect-route)
        :cookies {"session" session-id})))

(defn- add-user-and-start-session [username password email]
  (if-let [errmsg (userdao/add-user username password email)]
    (signup-error-output {:username-error errmsg} username email)
    (start-session username)))


;; ---[ compojure handler fns ]--- ;;

(defn show-signup-page []
  (slurp signup-html-path))

(defn process-signup [params]
  (let [username (get params "username")
        password (get params "password")
        verify   (get params "verify")
        email    (get params "email")]
    (if-let [validation-errors (validate-signup username password verify email)]
      (signup-error-output validation-errors username email)
      (add-user-and-start-session username password email))))
