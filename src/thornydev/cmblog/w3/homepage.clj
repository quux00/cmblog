(ns thornydev.cmblog.w3.homepage
  (:require [clojure.string :refer [join]]
            [net.cgrand.enlive-html :as h]
            [me.raynes.fs :refer [base-name]]
            [thornydev.cmblog.w3.session-dao :as sessiondao]
            [thornydev.cmblog.w3.blogpost-dao :as postdao])
  (:import (org.apache.commons.lang3 StringEscapeUtils)))


;; ---[ config settings ]--- ;;

(def welcome-html-path "resources/blog-template.html")

(defn- escape [s]
  (if (seq s)
    (StringEscapeUtils/escapeHtml4 s)
    ""))

(h/deftemplate homepage-template (base-name welcome-html-path) [username posts]
  ;; page header section
  [:div#usercontrols] (h/set-attr :class (if (seq username) "visible" "invisible"))
  [:span#username]    (h/content (escape username))

  ;; body of last 10 posts
  [:div.top-posts]
  (h/do->
   (h/add-class (if (seq posts) "visible" "invisible"))
   (h/clone-for [p posts]
                [:h2 :a] (h/do->
                          (h/set-attr :href (str "/post/" (:permalink p)))
                          (h/content (:title p)))
                [:span.post-date] (h/content (str "Posted " (:date p)))
                [:span.post-author] (h/content (str "By " (:author p)))
                [:a.num-comments] (h/do->
                                   (h/set-attr :href (str "/post/" (:permalink p)))
                                   (h/content (str (count (:comments p)))))
                [:span.post-body] (h/content (:body p))
                [:span.post-tags] (h/content (join " " (:tags p))))))

;; ---[ compojure handler fn ]--- ;;

(defn show-home-page [session-id]
  (let [username (-> session-id
                     sessiondao/find-username-by-session-id)
        posts (postdao/find-by-date-descending 10)]
    (println "posts found:" posts)
    (apply str (homepage-template username posts))))
