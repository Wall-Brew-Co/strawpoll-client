(ns strawpoll-client.impl-test
  (:require
    [bond.james :as bond]
    [clojure.test :as t]
    [strawpoll-client.impl :as sut]))


(t/deftest load-bugsnag-api-key!-test
  (t/testing "Test bugsnag api key provider chain"
    (bond/with-stub [[sut/getenv (fn [_] "my-env-key")]
                     [sut/getProperty (fn [_] "my-properties-key")]]
                    (t/is (= "my-api-key" (sut/load-api-key! {:api-key "my-api-key"}))))
    (bond/with-stub [[sut/getenv (fn [_] "my-env-key")]
                     [sut/getProperty (fn [_] "my-properties-key")]]
                    (t/is (= "my-env-key" (sut/load-api-key! {}))))
    (bond/with-stub [[sut/getenv (fn [_] nil)]
                     [sut/getProperty (fn [_] "my-properties-key")]]
                    (t/is (= "my-properties-key" (sut/load-api-key! {}))))
    (bond/with-stub [[sut/getenv (fn [_] nil)]
                     [sut/getProperty (fn [_] nil)]]
                    (t/is (thrown-with-msg? IllegalArgumentException #"could not locate your StrawPoll API key" (sut/load-api-key! {}))))))


(t/deftest ->url-test
  (t/testing "Ensure routes are properly formatted"
    (t/is (= "https://strawpoll.com/api/poll" (sut/->url "/poll")))
    (t/is (= "https://strawpoll.com/api/poll/12345" (sut/->url "/poll/" "12345")))
    (t/is (= "https://strawpoll.com/api/poll/12345" (sut/->url "/poll/" 12345)))
    (t/is (= "https://strawpoll.com/api/content/delete" (sut/->url "/content/delete")))))
