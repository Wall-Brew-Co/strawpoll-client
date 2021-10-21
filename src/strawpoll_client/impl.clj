(ns strawpoll-client.impl)

(defn getenv
  "Wrapper - here to support testing since we can't redef static methods"
  [k]
  (System/getenv k))

(defn getProperty
  "Wrapper - here to support testing since we can't redef static methods"
  [k]
  (System/getProperty k))

(defn load-api-key!
  [opts]
  (if-let [api-key  (or (:api-key opts)
                        (getenv "STRAWPOLL_API_KEY")
                        (getProperty "StrawPollApiKey"))]
    api-key
    (throw (IllegalArgumentException. "strawpoll-client.impl/load-api-key! could not locate your StrawPoll API key"))))
