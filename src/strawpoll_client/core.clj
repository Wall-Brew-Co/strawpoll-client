(ns strawpoll-client.core
  (:require [cheshire.core :as json] ;; required by clj-http
            [clj-http.client :as client]
            [strawpoll-client.impl :as impl]))

(def ^:const strawpoll-api-stem "https://strawpoll.com/api")

(defn ->url
  [& route]
  (str strawpoll-api-stem (apply str route)))

(defn ->client
  [{:keys [clj-http-opts]
    :as   opts}]
  {:api-key       (impl/load-api-key! opts)
   :clj-http-opts clj-http-opts})

(defn create-poll!
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
     (:body (client/post (->url "/poll") request-opts)))))

(defn get-poll-results!
  [{:keys [api-key clj-http-opts]} poll-id]
  (let [request-opts (merge clj-http-opts
                            {:headers      {"API-KEY" api-key}
                             :content-type :json
                             :accept       :json
                             :as           :json})]
    (:body (client/get (->url "/poll/" poll-id) request-opts))))

(defn delete-poll!
  [{:keys [api-key clj-http-opts]} poll-id]
  (let [body {:content_id poll-id}
        request-opts (merge clj-http-opts
                            {:headers      {"API-KEY" api-key}
                             :content-type :json
                             :accept       :json
                             :as           :json
                             :body         (json/generate-string body)})]
    (:body (client/delete (->url "/content/delete") request-opts))))

(comment
  (def client (->client {:api-key "your-api-key"}))
  (def my-poll (create-poll! client "Test Poll" ["dummy answer" "worse-answer"] {:multiple-answers? true}))
  (def my-poll-results {:admin_key  "some-admin-key"
                        :content_id "my-poll-id"
                        :success    1})
  (def poll-state (get-poll-results! client (:content_id my-poll)))
  (def poll-state-results {:content {:creator           {:avatar_path    "/images/avatars/nick-nichols.png"
                                                         :displayname    "Nick Nichols"
                                                         :monthly_points 0
                                                         :username       "nick-nichols"}
                                     :original_deadline nil
                                     :comments          1
                                     :type              "poll"
                                     :pin               nil
                                     :title             "Test Poll"
                                     :poll              {:reset_at           nil
                                                         :private            1
                                                         :poll_info          {:vpn                  0
                                                                              :description          nil
                                                                              :captcha              1
                                                                              :ma                   1
                                                                              :original_description nil
                                                                              :nsfw                 0
                                                                              :co                   1
                                                                              :creator_country_name "United States of America"
                                                                              :only_reg             0
                                                                              :ma_limit             nil
                                                                              :mip                  0
                                                                              :image                nil
                                                                              :edited_at            nil
                                                                              :show_results         1
                                                                              :enter_name           0
                                                                              :creator_country      "us"}
                                                         :total_voters       1
                                                         :title              "Test Poll"
                                                         :original_title     nil
                                                         :poll_answers       [{:answer          "dummy answer"
                                                                               :id              "answer-id-1"
                                                                               :original_answer nil
                                                                               :sorting         1
                                                                               :type            "text"
                                                                               :votes           0}
                                                                              {:answer          "worse-answer"
                                                                               :id              "answer-id-2"
                                                                               :original_answer nil
                                                                               :sorting         2
                                                                               :type            "text"
                                                                               :votes           1}]
                                                         :total_votes        1
                                                         :is_points_eligible 0
                                                         :is_votable         1
                                                         :last_vote_at       "2021-10-21T13:12:31Z"}
                                     :status            "active"
                                     :id                "my-poll-id"
                                     :has_webhooks      0
                                     :deadline          nil
                                     :media             nil
                                     :cookie_id         "my-poll-id"
                                     :created_at        "2021-10-21T13:07:49Z"}
                           :success 1})


  (def drop-poll (delete-poll! client (:content_id my-poll)))
  (def drop-poll-results {:message "Delete successful!"
                          :success 1})
  )
