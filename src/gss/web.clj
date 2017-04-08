(ns gss.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [environ.core :refer [env]]
            [net.cgrand.enlive-html :as enlive :refer [deftemplate]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [selmer.parser :as selmer]

            [gss.db :as db]
            [gss.m2x :as m2x]))

(def state->desc
  {
   "red" "thirsty",
   "yellow" "could use a sip",
   "green" "just fine",
   "white" "unknown"})

(def state->style {"red" "danger"
                   "yellow" "warning"
                   "green" "success"
                   "white" "info"})

(defn show-spike-detail
  [id thankyou]
  (println "spike: " id)
  (let [spike* (db/get-spike id)
        state (get spike* :state "white")
        spike (assoc spike*
                     :state-desc (state->desc state)
                     :state-style (state->style state)
                     :thankyou thankyou)]
    (selmer/render-file "spike-detail.html" spike)))

(defn show-map
  []
  (let [spikes (db/get-spikes)]
    (selmer/render-file "map.html" {:spikes spikes})))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Heroku"})

(defn doit
  [id params]
  (let [address (get-in params [:form-params "addr"])]
    (db/update-spike-request id address)
    (show-spike-detail id true))
  )

(defroutes app
  (GET "/" []
       (show-map))

  (GET "/spike/:id" [id] (show-spike-detail id false))
  (POST "/spike/:id" [id :as params] (doit id params))
  (GET  "/spikes" [] (db/get-spikes))

  (route/resources "/")

  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (db/connect (env :mongodb-uri))
  (db/create-testing)
  (selmer/set-resource-path! (clojure.java.io/resource "selmer"))
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (-> #'app
                         wrap-json-response
                         wrap-json-body
                         (wrap-defaults (assoc site-defaults :security {:anti-forgery false}))) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
