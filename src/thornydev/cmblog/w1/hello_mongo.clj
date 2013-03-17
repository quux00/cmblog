(ns thornydev.cmblog.w1.hello-mongo
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(defn -main [& args]
  (mg/connect! {:host "cypher" :port 27017})
  (mg/set-db! (mg/get-db "course"))

  (let [res (mc/find-maps "hello" {})]
    (println (type res))  ;; LazySeq of maps
    (println res))

  (println "------------------")
  (let [res (mc/find-one "hello" {:name #"Mongo"})]
    (println (type res))  ;; BasicDBObject
    (println res))

  (println "------------------")
  (let [curs (mc/find "hello" {:name {"$exists" true}})]
    (println (type curs))  ;; com.mongodb.DBCursor
    (println curs)
    (when (.hasNext curs)
      (println (.next curs))))

  (println "------------------")
  (let [res (mc/find-one-as-map "hello" {:name #"Mongo"})]
    (println (type res))  ;; PersistentArrayMap
    (println res)))

