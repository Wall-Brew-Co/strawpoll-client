(ns strawpoll-client.core
  "Basic functions to interact with [Strawpoll](https://strawpoll.com/en/api-docs/)"
  (:require [cheshire.core :as json]
            [clj-http.client :as client]
            [strawpoll-client.impl :as impl]))

(defn ->client
  "Create a client, which is the required first argument to `create-poll!`, `get-poll-results!`, and `delete-poll!`.
   This function expects an option map, with the following key sets:
     - :api-key - The Strawpoll API key for your project.
                  If this key is missing, the library will attempt to load the Environment variable `STRAWPOLL_API_KEY` and the JVM Property `StrawPollApiKey` in this order.
                  If all three values are nil, an exception will be thrown.
                  More information is available [on Strawpoll](https://strawpoll.com/en/api-docs/authentication/)
   All remaining keys will be passed to the underlying [clj-http request function](https://github.com/dakrone/clj-http)"
  [{:keys [clj-http-opts]
    :as   opts}]
  {:api-key       (impl/load-api-key! opts)
   :clj-http-opts clj-http-opts})

(defn create-poll!
  "Create a poll using the provided `client` titled `title` with `answers`.
   `client` is expected to be the return value of `strawpoll-client.core/->client`
   `title` is a string representing the title of the poll you wish to create.
   `answers` is a sequence of strings representing the poll options user will be able to pick from.
   Implements all features described in the [API Documentation](https://strawpoll.com/en/api-docs/create-poll/)

   This behavior may be tuned with an additional argument as an option map with the following keys:
     - `:public?` - A boolean to determine wether or not the poll will be publicly listed. Defaults to falsey
     - `:disallow-comments?` - A boolean to disable commenting on polls. Defaults to falsey
     - `:multiple-answers?` - A boolean to indicate wether each voter is allowed to vote on multiple options. Defaults to falsey
     - `:multiple-votes-per-ip?` - A boolean to indicate if multiple votes may be recorded from a single IP address. Defaults to falsey
     - `:name-required?` - A boolean to indicate if users must enter a name while voting. Defaults to falsey
     - `:deadline` - An ISO-8601 Datetime in Zulu Timezone past when the poll will close. Defaults to nil, ergo no closing date will be set
     - `:only-registered-users?` - A boolean to indicate if users will be required to have an account with strawpoll.com in order to vote. Defaults to falsey
     - `:allow-vpn-users?` - A boolean to indicate if users behind a VPN will be allowed to vote. Defaults to falsey
     - `:no-captcha?` - A boolean indicating if voting in the poll will not require solving a captcha. Defaults to falsey, meaning users will have to complete this step"
  ([client title answers]
   (create-poll! client title answers {}))

  ([{:keys [api-key clj-http-opts]}
    title
    answers
    {:keys [public?
            disallow-comments?
            multiple-answers?
            multiple-votes-per-ip?
            name-required?
            deadline
            only-registered-users?
            allow-vpn-users?
            no-captcha?]}]
   (let [body         {:poll (merge
                              {:title   title
                               :answers answers}
                              (when public? {:priv false})
                              (when disallow-comments? {:co false})
                              (when multiple-answers? {:ma true})
                              (when multiple-votes-per-ip? {:mip true})
                              (when name-required? {:enter_name true})
                              (when deadline {:deadline deadline})
                              (when only-registered-users? {:only_reg true})
                              (when allow-vpn-users? {:vpn true})
                              (when no-captcha? {:captcha false}))}
         request-opts (merge clj-http-opts
                             {:headers      {"API-KEY" api-key}
                              :content-type :json
                              :accept       :json
                              :as           :json
                              :body         (json/generate-string body)})]
      (:body (client/post (impl/->url "/poll") request-opts)))))

(defn ->poll-id
  "A convenience function to extract the identifier of a poll from the return value of `strawpoll-client.core/create-poll!`"
  [create-poll!-response]
  (:content_id create-poll!-response))

(defn get-poll-results!
  "Get the current results of `poll-id` using the provided client.
   The poll-id is encoded in the response of `strawpoll-client.core/create-poll!` as `content_id`, which may be retreived by `strawpoll-client.core/->poll-id`"
  [{:keys [api-key clj-http-opts]} poll-id]
  (let [request-opts (merge clj-http-opts
                            {:headers      {"API-KEY" api-key}
                             :content-type :json
                             :accept       :json
                             :as           :json})]
     (:body (client/get (impl/->url "/poll/" poll-id) request-opts))))

(defn delete-poll!
  "Permanently delete `poll-id` using the provided client.
   The poll-id is encoded in the response of `strawpoll-client.core/create-poll!` as `content_id`, which may be retreived by `strawpoll-client.core/->poll-id`"
  [{:keys [api-key clj-http-opts]} poll-id]
  (let [body {:content_id poll-id}
        request-opts (merge clj-http-opts
                            {:headers      {"API-KEY" api-key}
                             :content-type :json
                             :accept       :json
                             :as           :json
                             :body         (json/generate-string body)})
        resp (:body (client/delete (impl/->url "/content/delete") request-opts))]
    (if (zero? (:success resp))
      (Exception. (str "Failure deleting poll! " (:message resp)))
      resp)))
