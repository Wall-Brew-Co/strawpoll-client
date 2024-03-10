(ns strawpoll-client.impl
  "Implementation-level functions used throughout the library")


(def ^:const strawpoll-api-stem
  "The Strawpoll API URI stem"
  "https://strawpoll.com/api")


(defn ^:deprecated ->url
  "Append `route` as a suffix to the strawpoll URI stem"
  [& route]
  (str strawpoll-api-stem (apply str route)))


(defn ^:deprecated getenv
  "Wrapper - here to support testing since we can't redef static methods"
  [k]
  (System/getenv k))


(defn ^:deprecated getProperty
  "Wrapper - here to support testing since we can't redef static methods"
  [k]
  (System/getProperty k))


(defn ^:deprecated load-api-key!
  "Attempt to load the Strawpoll API key from `opts`, otherwise check for a matching Environment Variable or JVM Property"
  [opts]
  (if-let [api-key  (or (:api-key opts)
                        (getenv "STRAWPOLL_API_KEY")
                        (getProperty "StrawPollApiKey"))]
    api-key
    (throw (IllegalArgumentException. "strawpoll-client.impl/load-api-key! could not locate your StrawPoll API key"))))
