(defproject green-spike-server "1.0.0-SNAPSHOT"
  :description "Green Spike Server"
  :url "http://greenspike.herokuapp.com"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [
                 [org.clojure/clojure "1.9.0-alpha15"]
                 [cheshire "5.7.0"]
                 [clj-http "3.4.1"]
                 [clj-time "0.13.0"]
                 [com.taoensso/timbre "4.8.0"]
                 [compojure "1.5.2"]
                 [enlive "1.1.6"]
                 [environ "1.1.0"]
                 [fipp "0.6.9"]
                 [medley "0.8.4"]
                 [com.novemberain/monger "3.1.0"]
                 [metosin/ring-http-response "0.8.2"]
                 [ring "1.5.1"]
                 [ring/ring-defaults "0.2.3"]
                 [ring/ring-json "0.4.0"]
                 [slingshot "0.12.2"]
                 [selmer "1.10.7"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "green-spike-server-standalone.jar"
  :profiles {:production {:env {:prod true
                                :m2x-key "5fa8ca77601afee8c23ea6f52fd0cb33"}}
             :dev {:env {:prod false
                         :m2x-key "5fa8ca77601afee8c23ea6f52fd0cb33"}}})
