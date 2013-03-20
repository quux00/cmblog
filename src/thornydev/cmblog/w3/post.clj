(ns thornydev.cmblog.w3.post
  (:require [clojure.string :refer [join]]
            [net.cgrand.enlive-html :as h]
            [me.raynes.fs :refer [base-name]]
            [thornydev.cmblog.w3.session-dao :as sessiondao]
            [thornydev.cmblog.w3.blogpost-dao :as postdao])
  (:import (org.apache.commons.lang3 StringEscapeUtils)))

;; ---[ config settings ]--- ;;

(def post-html-path "resources/entry-template.html")
(def redirect-route "/post_not_found")


;; ---[ enlive template ]--- ;;

(defn- escape [s]
  (if (seq s)
    (StringEscapeUtils/escapeHtml4 s)
    ""))

(h/deftemplate post-template (base-name post-html-path) [username post comment-errors]
  ;; page header section
  [:div#usercontrols] (h/set-attr :class (if (seq username) "visible" "invisible"))
  [:span#username]    (h/content (escape username))

  ;; post
  [:h2#post-title]    (h/content (:title post))
  [:span#post-date]   (h/content (str (:date post)))
  [:span#post-author] (h/content (:author post))
  [:span#post-body]   (h/content (:body post))
  [:span#post-tags]   (h/content (join " " (:tags post)))

  ;; existing comments section
  [:div.comment]
  (h/do->
   (h/add-class (if (seq (:comments post)) "visible" "invisible"))
   (h/clone-for [c (:comments post)]
                [:span.comment-author] (h/content (:author c))
                [:span.comment-body]   (h/content (:body c))))

  ;; add a comment section
  [[:input (h/attr= :name "permalink")]] (h/set-attr :value (:permalink post))
  [:span.add-comment-errors]  (h/content (join "<br>" comment-errors)))


;; ---[ compojure fn handlers ]--- ;;

(defn show-post [session-id permalink]
  (if-let [post (postdao/find-by-permalink permalink)]
    (apply str (post-template
                (sessiondao/find-username-by-session-id session-id)
                post nil))
    (ring.util.response/redirect redirect-route)))

(defn add-new-comment [fparams session-id]
  (let [cmt-name (escape (get fparams "comment-name"))
        cmt-email (escape (get fparams "comment-email"))
        cmt-body (escape (get fparams "comment-body"))
        permalink (get fparams "permalink")
        post (postdao/find-by-permalink permalink)]
    ;; debug
    (println "(empty? cmt-name)" (empty? cmt-name))
    (println "(empty? cmt-body)" (empty? cmt-body))
    (flush)
    ;; end debug
    (cond
     (nil? post) (ring.util.response/redirect redirect-route)

     (or (empty? cmt-name) (empty? cmt-body))
     (apply str (post-template
                 (sessiondao/find-username-by-session-id session-id)
                 post ["Post must contain your name and an actual comment"]))

     :else (do
             (postdao/add-post-comment
                cmt-name cmt-email cmt-body permalink)
             (ring.util.response/redirect (str "/post/" permalink)))
     )
    )
  )
