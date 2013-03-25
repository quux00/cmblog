(ns thornydev.cmblog.w4.session-dao
  (:require [monger.collection :as mc]
            [monger.result :as mres]
            [clojure.data.codec.base64 :as b64]
            [thornydev.cmblog.w4.password-util :refer [secure-rand]]
            [thornydev.cmblog.w4.dao-config :refer [session-coll]])
  (:import (com.mongodb WriteResult)))

(defn start-session [username]
  (let [btary      (byte-array 32)
        _          (.nextBytes secure-rand btary)
        session-id (-> (b64/encode btary)
                       (String. "UTF-8"))]
    (let [res (mc/insert session-coll {:_id session-id
                                       :username username})]
      (when (mres/has-error? res)
          (throw (Exception. (str "Unable to create session in database: " (.getError res))))))
    session-id))

(defn end-session [session-id]
  (mc/remove-by-id session-coll session-id))

(defn get-session [session-id]
  (mc/find-one-as-map session-coll {:_id session-id}))

(defn find-username-by-session-id [session-id]
  (-> session-id get-session :username))
