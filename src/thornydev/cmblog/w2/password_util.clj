(ns thornydev.cmblog.w2.password-util
  (:require [clojure.data.codec.base64 :as b64])
  (:import (java.security MessageDigest SecureRandom)))

;; initialize and seed at load time
(defonce ^SecureRandom secure-rand (#(doto (SecureRandom/getInstance "SHA1PRNG")
                                       (.nextBytes (byte-array 512)))))

(defn make-password-hash
  ([passw]
     (make-password-hash passw (.nextInt secure-rand)))  
  ([^String passw ^String salt]
      (let [salted-hashed (str passw "," salt)
            digest (doto (MessageDigest/getInstance "MD5")
                     (.update (.getBytes salted-hashed)))
            hashed-bytes (String. (.digest digest) "UTF-8")]
        (-> (b64/encode (.getBytes hashed-bytes))
            (String. "UTF-8")
            (str "," salt)))))

(defn password-match?
  "Salts and hashes unsalted-unhashed passw"
  [unsalted-unhashed-pw salted-hashed-pw]
  (let [salt (last (clojure.string/split salted-hashed-pw #","))]
    (= salted-hashed-pw (make-password-hash unsalted-unhashed-pw salt))))
