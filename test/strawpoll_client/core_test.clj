(ns strawpoll-client.core-test
  (:require
    [bond.james :as bond]
    [clj-http.client :as http]
    [clj-http.fake :refer [with-fake-routes-in-isolation]]
    [clojure.test :as t]
    [strawpoll-client.core :as sut]))


(t/deftest create-poll!-test
  (let [sample-response {:cached                nil
                         :repeatable?           false
                         :protocol-version      {:name  "HTTP"
                                                 :major 1
                                                 :minor 1}
                         :streaming?            true
                         :chunked?              true
                         :reason-phrase         "OK"
                         :headers               {"Server"                    "nginx/1.14.2"
                                                 "Content-Type"              "application/json;charset=UTF-8"
                                                 "X-Frame-Options"           "SAMEORIGIN"
                                                 "Strict-Transport-Security" "max-age=31536000"
                                                 "Connection"                "close"
                                                 "Transfer-Encoding"         "chunked"
                                                 "Date"                      "Fri, 22 Oct 2021 21:37:34 GMT"
                                                 "Vary"                      "Accept-Encoding"}
                         :orig-content-encoding "gzip"
                         :status                200
                         :length                -1
                         :body                  "{\"admin_key\":\"some-key\",\"content_id\":\"jkooopjr5\",\"success\":1}"
                         :trace-redirects       []}
        parsed-body     {:admin_key  "some-key"
                         :content_id "jkooopjr5"
                         :success    1}
        sample-client   {:api-key "my-api-key"}]
    (t/testing "Test to ensure we call the Strawpoll API and to demonstrate the response structure"
      (with-fake-routes-in-isolation
        {"https://strawpoll.com/api/poll" (fn [_] sample-response)}
        (bond/with-spy [http/post]
                       (t/is (= parsed-body (sut/create-poll! sample-client "test-poll" ["1 answer" "2 answer"])))
                       (t/is (= 1 (-> http/post bond/calls count))))))))


(def sample-poll-result
  "A just-in-time created samp[le respons from the API"
  "{\"content\":{\"creator\":{\"avatar_path\":\"/images/avatars/nick-nichols.png\",\"displayname\":\"Nick Nichols\",\"monthly_points\":0,\"username\":\"nick-nichols\"},\"original_deadline\":null,\"comments\":1,\"type\":\"poll\",\"pin\":null,\"title\":\"Test Poll\",\"poll\":{\"reset_at\":null,\"private\":1,\"poll_info\":{\"vpn\":0,\"description\":null,\"captcha\":1,\"ma\":1,\"original_description\":null,\"nsfw\":0,\"co\":1,\"creator_country_name\":\"United States of America\",\"only_reg\":0,\"ma_limit\":null,\"mip\":0,\"image\":null,\"edited_at\":null,\"show_results\":1,\"enter_name\":0,\"creator_country\":\"us\"},\"total_voters\":0,\"title\":\"Test Poll\",\"original_title\":null,\"poll_answers\":[{\"answer\":\"dummy answer\",\"id\":\"ub339by5f4e4\",\"original_answer\":null,\"sorting\":1,\"type\":\"text\",\"votes\":0},{\"answer\":\"worse-answer\",\"id\":\"8xddpzqyq8dw\",\"original_answer\":null,\"sorting\":2,\"type\":\"text\",\"votes\":0}],\"total_votes\":0,\"is_points_eligible\":0,\"is_votable\":1,\"last_vote_at\":null},\"status\":\"active\",\"id\":\"jkooopjr5\",\"has_webhooks\":0,\"deadline\":null,\"media\":null,\"cookie_id\":\"jkooopjr5\",\"created_at\":\"2021-10-22T21:37:34Z\"},\"success\":1}")


(t/deftest get-poll-results!-test
  (let [sample-response {:cached                nil
                         :repeatable?           false
                         :protocol-version      {:name  "HTTP"
                                                 :major 1
                                                 :minor 1}
                         :streaming?            true
                         :chunked?              true
                         :reason-phrase         "OK"
                         :headers               {"Server"                    "nginx/1.14.2"
                                                 "Content-Type"              "application/json;charset=UTF-8"
                                                 "X-Frame-Options"           "SAMEORIGIN"
                                                 "Strict-Transport-Security" "max-age=31536000"
                                                 "Connection"                "close"
                                                 "Transfer-Encoding"         "chunked"
                                                 "Date"                      "Fri, 22 Oct 2021 21:37:34 GMT"
                                                 "Vary"                      "Accept-Encoding"}
                         :orig-content-encoding "gzip"
                         :status                200
                         :length                -1
                         :body                  sample-poll-result
                         :trace-redirects       []}
        parsed-body     {:content {:creator           {:avatar_path    "/images/avatars/nick-nichols.png"
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
                                                       :total_voters       0
                                                       :title              "Test Poll"
                                                       :original_title     nil
                                                       :poll_answers       [{:answer          "dummy answer"
                                                                             :id              "ub339by5f4e4"
                                                                             :original_answer nil
                                                                             :sorting         1
                                                                             :type            "text"
                                                                             :votes           0}
                                                                            {:answer          "worse-answer"
                                                                             :id              "8xddpzqyq8dw"
                                                                             :original_answer nil
                                                                             :sorting         2
                                                                             :type            "text"
                                                                             :votes           0}]
                                                       :total_votes        0
                                                       :is_points_eligible 0
                                                       :is_votable         1
                                                       :last_vote_at       nil}
                                   :status            "active"
                                   :id                "jkooopjr5"
                                   :has_webhooks      0
                                   :deadline          nil
                                   :media             nil
                                   :cookie_id         "jkooopjr5"
                                   :created_at        "2021-10-22T21:37:34Z"}
                         :success 1}
        sample-client   {:api-key "my-api-key"}]
    (t/testing "Test to ensure we call the Strawpoll API and to demonstrate the response structure"
      (with-fake-routes-in-isolation
        {"https://strawpoll.com/api/poll/jkooopjr5" (fn [_] sample-response)}
        (bond/with-spy [http/get]
                       (t/is (= parsed-body (sut/get-poll-results! sample-client "jkooopjr5")))
                       (t/is (= 1 (-> http/get bond/calls count))))))))


(t/deftest delete-poll!-test
  (let [sample-response {:cached                nil
                         :repeatable?           false
                         :protocol-version      {:name  "HTTP"
                                                 :major 1
                                                 :minor 1}
                         :streaming?            true
                         :chunked?              true
                         :reason-phrase         "OK"
                         :headers               {"Server"                    "nginx/1.14.2"
                                                 "Content-Type"              "application/json;charset=UTF-8"
                                                 "X-Frame-Options"           "SAMEORIGIN"
                                                 "Strict-Transport-Security" "max-age=31536000"
                                                 "Connection"                "close"
                                                 "Transfer-Encoding"         "chunked"
                                                 "Date"                      "Fri, 22 Oct 2021 21:45:37 GMT"
                                                 "Vary"                      "Accept-Encoding"}
                         :orig-content-encoding "gzip"
                         :status                200
                         :length                -1
                         :body                  "{\"message\":\"Delete successful!\",\"success\":1}"
                         :trace-redirects       []}
        parsed-body     {:message "Delete successful!"
                         :success 1}
        sample-client   {:api-key "my-api-key"}]
    (t/testing "Test to ensure we call the Strawpoll API and to demonstrate the response structure"
      (with-fake-routes-in-isolation
        {"https://strawpoll.com/api/content/delete" (fn [_] sample-response)}
        (bond/with-spy [http/delete]
                       (t/is (= parsed-body (sut/delete-poll! sample-client "jkooopjr5")))
                       (t/is (= 1 (-> http/delete bond/calls count))))))))
