(ns thornydev.cmblog.w2.example2
  (:use [ring.adapter.jetty             :only [run-jetty]]
        [compojure.core                 :only [defroutes GET POST]]
        [ring.middleware.params         :only [wrap-params]]
        [clojure.pprint                 :only [pprint]]))

(defroutes routes
  (POST "/" [name] (str "Thanks " name))
  (GET  "/" [] "<form method='post' action='/'> What's your name? <input type='text' name='name' /><input type='submit' /></form>"))

(def app (wrap-params routes))

(defn -main []
  (run-jetty #'app {:port 8000, :join? false}) )
