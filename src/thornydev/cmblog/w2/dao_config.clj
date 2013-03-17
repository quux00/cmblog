(ns thornydev.cmblog.w2.dao-config
  (:require [monger.core :as mg]))

(defonce mgconx (mg/connect! {:host "cypher" :port 27017}))
(defonce mgcoll (mg/set-db! (mg/get-db "blog")))
(defonce user-coll "users")
(defonce session-coll "sessions")

