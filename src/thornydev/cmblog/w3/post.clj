(ns thornydev.cmblog.w3.homepage
  (:require [clojure.string :refer [join]]
            [net.cgrand.enlive-html :as h]
            [me.raynes.fs :refer [base-name]]
            [thornydev.cmblog.w3.session-dao :as sessiondao]
            [thornydev.cmblog.w3.blogpost-dao :as postdao])
  (:import (org.apache.commons.lang3 StringEscapeUtils)))


(def post-html-path "resources/entry-template.html")
(def redirect-route "/post_not_found")


;; ---[ compojure fn handlers ]--- ;;

(defn show-post [permalink]
  (if-let [post (find-by-permalink permlink)]
    

    (ring.util.response/redirect redirect-route)
    )
  )
