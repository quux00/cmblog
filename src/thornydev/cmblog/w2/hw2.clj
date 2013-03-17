(ns thornydev.cmblog.w2.hw2
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]))

(def coll "grades")

(defn get-all-scores [student-id]
  (mc/find-maps coll {:student_id student-id}))

(defn remove-lowest-score [docs]
  (let [student-id (:student_id (first docs))
        lowest-score-doc (first (sort-by :score docs))]
    
    (println "deleting" (:_id lowest-score-doc))
    (mc/remove coll {:_id (:_id lowest-score-doc)})))

(defn -main [& args]
  (mg/connect! {:host "cypher" :port 27017})
  (mg/set-db! (mg/get-db "students"))

  (doseq [doc (mc/aggregate coll [{$group {:_id "$student_id"}}])]
    (-> doc
        :_id
        get-all-scores
        remove-lowest-score)))
