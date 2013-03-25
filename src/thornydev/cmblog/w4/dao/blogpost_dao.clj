(ns thornydev.cmblog.w4.blogpost-dao
  (:require [clojure.string :as str]
            [monger.collection :as mc]
            [monger.query :as mq]
            [monger.result :as mres]
            [monger.operators :refer [$addToSet]]
            [thornydev.cmblog.w4.dao-config :refer [posts-coll]])
  (:import (java.util Date)))


(defn- make-permalink [title]
  (-> title
      (str/replace #"\s+" "_")
      (str/replace #"\W+" "")
      str/lower-case))

(defn find-by-permalink [permalink]
  (mc/find-one-as-map posts-coll {:permalink permalink}))

(defn find-by-date-descending [^long nlimit]
  (mq/with-collection posts-coll
    (mq/find {})
    (mq/sort {:date -1})
    (mq/limit nlimit)))

(defn add-post
  "Inserts a blog post entry into the posts collection.
  Returns the permalink to identify the post if the insert is
  successful.  Throws Exception with error msg if the insert fails."
  [title body vtags username]
  (println "inserting blog entry" title)
  (let [permalink (make-permalink title)
        res (mc/insert posts-coll {:title title
                                   :author username
                                   :body body
                                   :permalink permalink
                                   :tags vtags
                                   :comments []
                                   :date (Date.)})]
    (if (mres/has-error? res)
      (throw (Exception. "Insert of new post failed: " (.getError res)))
      permalink)))


(defn add-post-comment
  "Add a comment with name, body and email (optional) to the post
  identified by +permalink+. Returns nil if successful in updating
  the post.  Returns an error string if not successful."
  [name email body permalink]
  (let [comment (into {:author name :body body}
                      (if (and email (seq email))  {:email email} {}))
        res (mc/update posts-coll
                       {:permalink permalink}
                       {$addToSet {:comments comment}} :multi false)]
    (when (or (mres/has-error? res)
              (not (mres/updated-existing? res)))
      (str "Post record was not updated with comment: "
           (or (.getError res) "Bad permalink?")))))

