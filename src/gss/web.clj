(ns gss.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate]]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]

            [gss.db :as db] 
            [gss.m2x :as m2x]))


(deftemplate spike-detail "templates/spike-detail.html"
  [id]
  [:title] (enlive/content (str "Spike " id))
  [:#wetness] (enlive/content (str (m2x/device-wetness id))))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Heroku"})

(defroutes app
  (GET "/" []
       (splash))
  (GET "/spike/:id" [id] (spike-detail id))

  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (db/connect (env "MONGODB_URI"))
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
