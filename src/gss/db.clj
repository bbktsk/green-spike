(ns gss.db
  (:require [monger.core :as mg]))

(def db (atom nil))
(def conn (atom nil))

(defn connect
  [uri]
  (let [{:keys [conn' db']} (mg/connect-via-uri uri)]
    (reset! db db')
    (reset! conn conn')))
