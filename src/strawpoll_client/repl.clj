(ns strawpoll-client.repl
  "A Rich comment block demonstrating the lifecycle of a poll"
  (:require [strawpoll-client.core :as straw]))


(comment
  (def client
    (straw/->client {:api-key "your-api-key"}))

  (def my-poll
    (straw/create-poll! client "Test Poll" ["dummy answer" "worse-answer"] {:multiple-answers? true}))
  (def my-poll-id (:content_id my-poll))

  (def poll-state
    (straw/get-poll-results! client my-poll-id))

  (def drop-poll
    (straw/delete-poll! client my-poll-id)))
