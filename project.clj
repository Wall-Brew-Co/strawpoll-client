(defproject com.wallbrew/strawpoll-client "1.1.0"
  :description "A clj-http client library for https://strawpoll.com/"
  :url "https://github.com/Wall-Brew-Co/strawpoll-client"
  :license {:name         "MIT"
            :url          "https://opensource.org/licenses/MIT"
            :distribution :repo
            :comments     "Same-as all Wall-Brew projects."}
  :scm {:name "git"
        :url  "https://github.com/Wall-Brew-Co/strawpoll-client"}
  :dependencies [[cheshire "5.12.0"]
                 [clj-http "3.12.3"]
                 [org.clojure/clojure "1.11.2"]]
  :plugins [[com.github.clj-kondo/lein-clj-kondo "2024.03.05"]
            [com.wallbrew/lein-sealog "1.2.0"]
            [lein-cljsbuild "1.1.8"]
            [lein-project-version "0.1.0"]
            [mvxcvi/cljstyle "0.16.630"]
            [ns-sort "1.0.3"]]
  :profiles {:uberjar {:aot :all}
             :dev     {:dependencies [[circleci/bond "0.6.0"]
                                      [clj-http-fake "1.0.4"]]}}
  :resource-paths ["resources"]
  :deploy-branches ["master"]
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :username      :env/clojars_user
                                    :password      :env/clojars_pass
                                    :sign-releases false}]]
  :min-lein-version "2.5.3")
