(ns thornydev.cmblog.w1.hello-enlive
  (:require [net.cgrand.enlive-html :as h]))

(defn -main [& args]
  (let [tmpl (slurp "resources/w1.tmpl")]
    (-> tmpl
        (h/sniptest [:h1] (h/content "Hello Enlive!"))
        println)))
