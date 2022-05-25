(defproject com.wallbrew/strawpoll-client "1.0.0"
  :description "A clj-http client library for https://strawpoll.com/"
  :url "https://github.com/Wall-Brew-Co/strawpoll-client"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[cheshire "5.10.0"]
                 [clj-http "3.12.3"]
                 [org.clojure/clojure "1.11.1"]]
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[circleci/bond "0.6.0"]
                                  [clj-http-fake "1.0.3"]]}}
  :min-lein-version "2.5.3")
