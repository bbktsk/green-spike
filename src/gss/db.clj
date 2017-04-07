(ns gss.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [slingshot.slingshot :refer [throw+ try+]]

            [gss.m2x :as m2x]))

(def db (atom nil))
(def conn (atom nil))

(def spikes "spikes")

(defn connect
  [uri]
  (let [uri' (or uri "mongodb://127.0.0.1/green-spike")
        m (mg/connect-via-uri uri')]
    (reset! db (:db m))
    (reset! conn (:conn m))))


(defn create-spike
  [id name lat lng dev eth]
  (mc/insert @db spikes {:_id id
                         :name name
                         :lat lat
                         :lng lng
                         :dev dev
                         :eth eth}))

(defn get-spike
  [id]
  (if-let [spike (mc/find-one-as-map @db spikes {:_id id})]
    spike
    (throw+ (format "Spike %s not found" id))))

(defn get-spikes
  []
  (mc/find-maps @db spikes))

(defn update-spike-wetness
  [spike]
  (let [{:keys [dev _id]} (get-spike spike)
        wetness (m2x/device-wetness dev)]
    (mc/update-by-id @db spikes _id {$set {:wetness wetness}})))

(defn create-testing
  []
  (try+ (create-spike "holin"
                 "Bush next to Holiday Inn"
                 49.184635  16.580268
                 "0904d16113289ea7b0f7d0dcbf8167ed"
                 "02652bfde2ff7f3264138cdb2117021c202ebbe2")
        (catch Object _ nil))
  (try+ (create-spike "spilberk"
                 "Spilberk"
                 49.193982 16.601023
                 "0904d16113289ea7b0f7d0dcbf8167ed"
                 "50e339de78eb57ed8724107e044eb8d0a54b48a8")
        (catch Object _ nil))
  (try+ (create-spike "obilnitrh"
                 "Obilni trh"
                 49.199569, 16.597069
                 "0904d16113289ea7b0f7d0dcbf8167ed"
                 "9f97d33281649c7b38b73ead0c12ccef8f70a2b6")
        (catch Object _ nil)))
