(ns gss.ethereum
  (:require [clj-http.client :as http]))


(defn balance
  [wallet]
  (-> (http/get "https://api.blockcypher.com/v1/eth/main/addrs/" wallet "/balance" {:as :json})
      :body
      :final_balance))
