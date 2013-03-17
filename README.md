# cmblog

A Clojure weblog based on the Spring 2013 10gen Mongo DB course for Java devs.

Libraries:

* monger for the MongoDB client
* ring with jetty for the appserver
* compojure for basic routing
* enlive for templating

cmblog = Clojure-MongoDB-Blog


Each week's work is divided by a separate directory in the `src/thornydev/cmblog` directory.  They correspond to the homework from the Spring 2013 10gen course: https://education.10gen.com/courses/10gen/M101J/2013_Spring/info

## Usage

Except for week 1, the main method is in cmblog.clj.

    $ lein repl
    user=> (require '[thornydev.cmblog.w2.cmblog :as blog]
    user=> (def svr (blog/-main))  ;; starts the Jetty server
    user=> (.stop @svr)            ;; to stop the Jetty server

The server listens to port 8083, so go to [http://localhost:8083/signup](http://localhost:8083/signup) after starting the server.


## License

Copyright © 2013 Michael Peterson (ported from the 10gen Java code)

Distributed under the Eclipse Public License, the same as Clojure.
