(ns thornydev.cmblog.w2.user-dao
  (:require [monger.collection :as mc]
            [monger.result :as mres]
            [thornydev.cmblog.w2.password-util :refer [password-match? make-password-hash]]
            [thornydev.cmblog.w2.dao-config :refer [user-coll]])
  (:import (com.mongodb MongoException$DuplicateKey WriteResult)))

(defn validate-login
  "Takes username and password from user post
   and validates against the database.
   Returns user-map if validated. Returns nil if not."
  [uname passw]
  (if-let [usermap (mc/find-one-as-map user-coll {:_id uname})]
    (when (password-match? passw (:password usermap))
      usermap)
    (println "User" uname "not in database") ;; returns nil, as intended
    ))

(defn add-user
  "Attempts to add the user to the users collection after hashing and salting
   the password. If succeeds returns nil. If fails, returns error msg (string)."
  [^String uname ^String passw ^String email]
  (let [user-map {:_id uname
                   :password (make-password-hash passw)}
        user-map (or (when (seq email) (assoc user-map :email email))
                     user-map)]
    (try
      (let [res (mc/insert user-coll user-map)]
        (when (mres/has-error? res)
          (str "Unable to create user in database: " (.getError res))))
      (catch MongoException$DuplicateKey k
        (str "Username already in use: " uname)))))
