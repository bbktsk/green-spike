(ns gss.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate]]
            [ring.adapter.jetty :as jetty]
            [selmer.parser :as selmer]

            [gss.db :as db]
            [gss.m2x :as m2x]))


(deftemplate spike-detail "templates/spike-detail.html"
  [s]
  [:title] (enlive/content (str "Spike " (:name s)))
  [:#wetness] (enlive/content (:state s)))

(comment deftemplate map "templates/map.html"
         [])

(defn show-spike-detail
  [id]
  (println "spike: " id)
  (let [spike (db/get-spike id)]
    (spike-detail spike)))

(defn show-map
  []
  (let [spikes (db/get-spikes)]
    (selmer/render-file "map.html" {:spikes spikes})))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Heroku"})

(defroutes app
  (GET "/" []
       (show-map))

  (GET "/spike/:id" [id] (show-spike-detail id))

  (route/resources "/")

  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (db/connect (env :mongodb-uri))
  (db/create-testing)
  (selmer/set-resource-path! (clojure.java.io/resource "selmer"))
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
