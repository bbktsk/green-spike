(ns gss.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [slingshot.slingshot :refer [throw+]]

            [gss.m2x :as m2x])
  (:import org.bson.types.ObjectId))

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
  [name lat lon dev eth]
  (mc/insert @db spikes {:_id (ObjectId.)
                         :name name
                         :lat lat
                         :lon lon
                         :dev dev
                         :eth eth}))

(defn get-spike
  [id]
  (if-let [spike (mc/find-one-as-map @db spikes {:_id (ObjectId. id)})]
    spike
    (throw+ (format "Spike %s not found" id))))

(defn update-spike-wetness
  [spike]
  (let [{:keys [dev _id]} (get-spike spike)
        wetness (m2x/device-wetness dev)]
    mc/update-by-id @db spikes _id {$set {:wetness wetness}}))

(comment
  (create-spike "Bush next to Holiday Inn"
                49.184635  16.580268
                "0904d16113289ea7b0f7d0dcbf8167ed"
                nil))
