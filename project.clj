(defproject thornydev.cmblog "0.1.0"
  :description "weblog based on 10gen Mongo DB course for Java devs"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [com.novemberain/monger "1.4.2"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.1.7"]
                 [ring/ring-jetty-adapter "1.1.7"]
                 [hiccup "1.0.2"]
                 [enlive "1.1.1"]
                 [org.clojure/data.codec "0.1.0"]
                 [org.apache.commons/commons-lang3 "3.1"]
                 [me.raynes/fs "1.4.0"]])
