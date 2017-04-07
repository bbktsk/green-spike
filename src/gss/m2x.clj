(ns gss.m2x
  (:require [clj-http.client :as http]
            [environ.core :refer [env]]))


(def api-root "https://api-m2x.att.com/v2/")

(defn hdrs
  []
  {:accept :json
   :as :json
   :s-m2x-key (env :m2x-key)})

(defn device-wetness
  [id]
  (let [resp (http/get (str api-root "devices/" id "/streams/wetness/values?limit=1")
                       (hdrs))]
    (if (:status resp)
      (get-in resp [:body :values 0 :value])
      nil)))
