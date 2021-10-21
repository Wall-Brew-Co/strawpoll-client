(defproject com.wallbrew/strawpoll-client "1.0.0"
  :description "A clj-http client library for https://strawpoll.com/"
  :url "https://github.com/Wall-Brew-Co/strawpoll-client"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[cheshire "5.10.0"]
                 [clj-http "3.12.3"]
                 [org.clojure/clojure "1.10.0"]]
  :profiles {:uberjar {:aot :all}}
  :min-lein-version "2.5.3")
