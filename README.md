# strawpoll-client

[![Clojars Project](https://img.shields.io/clojars/v/com.wallbrew/strawpoll-client.svg)](https://clojars.org/com.wallbrew/strawpoll-client)
[![cljdoc badge](https://cljdoc.org/badge/com.wallbrew/strawpoll-client)](https://cljdoc.org/d/com.wallbrew/strawpoll-client/CURRENT)
[![Clojure CI](https://github.com/Wall-Brew-Co/strawpoll-client/actions/workflows/clojure.yml/badge.svg)](https://github.com/Wall-Brew-Co/strawpoll-client/actions/workflows/clojure.yml)

A [clj-http](https://github.com/dakrone/clj-http) client to integrate with [Strawpoll.](https://strawpoll.com/en/)

This repository follows the guidelines and standards of the [Wall Brew Open Source Policy.](https://github.com/Wall-Brew-Co/open-source "Our open source guidelines")

## Installation

A deployed copy of the most recent version of [strawpoll-client can be found on clojars.](https://clojars.org/com.wallbrew/strawpoll-client)
To use it, add the following as a dependency in your project.clj file:

[![Clojars Project](https://clojars.org/com.wallbrew/strawpoll-client/latest-version.svg)](https://clojars.org/com.wallbrew/strawpoll-client)

The next time you build your application, [Leiningen](https://leiningen.org/) or [deps.edn](https://clojure.org/guides/deps_and_cli) should pull it automatically.
Alternatively, you may clone or fork the repository to work with it directly.

## Public Functions

### Authorization

Integrating with Strawpoll requires setting up an API Key with your user account.
Strawpoll provides documentation on generating this key in their [API documentation.](https://strawpoll.com/en/api-docs/authentication/)

Since this value is required for each HTTP call to their interface, strawpoll-client has adopted a component/client pattern.
Each of the functions used to interact with Strawpoll expect a client generated by `strawpoll-client.core/->client` as its first argument.

This function expects an option map, with the following key sets:

- `:api-key` - Your Strawpoll API key as a string.
             If this key is missing, the library will attempt to load the Environment variable `STRAWPOLL_API_KEY` and the JVM Property `StrawPollApiKey` in this order.
             All remaining keys in this map will be passed to the underlying [clj-http request function](https://github.com/dakrone/clj-http)

### Poll Lifecycles

As stated, each function will require a client, so we create one first:

```clojure
(:require [strawpoll-client.core :refer :all])

(def my-client (->client {:api-key "My-awesome-secret"}))
```

Now, we will use this client to create a basic poll with the following options:

- Cats
- Dogs
- Rabbits

```clojure
(create-poll! my-client "What is your favorite animal?" ["Cats" "Dogs" "Rabbits"])
;; => {:admin_key "some-key" :content_id "jkooopjr5" :success 1}
```

`create-poll!` makes each of the options from [Strawpoll's API](https://strawpoll.com/en/api-docs/create-poll/) available via an optional option map with the following keys:

- `:public?` - A boolean to determine wether or not the poll will be publicly listed. Defaults to falsey
- `:disallow-comments?` - A boolean to disable commenting on polls. Defaults to falsey
- `:multiple-answers?` - A boolean to indicate wether each voter is allowed to vote on multiple options. Defaults to falsey
- `:multiple-votes-per-ip?` - A boolean to indicate if multiple votes may be recorded from a single IP address. Defaults to falsey
- `:name-required?` - A boolean to indicate if users must enter a name while voting. Defaults to falsey
- `:deadline` - An ISO-8601 Datetime in Zulu Timezone past when the poll will close. Defaults to nil, ergo no closing date will be set
- `:only-registered-users?` - A boolean to indicate if users will be required to have an account with strawpoll.com in order to vote. Defaults to falsey
- `:allow-vpn-users?` - A boolean to indicate if users behind a VPN will be allowed to vote. Defaults to falsey
- `:no-captcha?` - A boolean indicating if voting in the poll will not require solving a captcha. Defaults to falsey, meaning users will have to complete this step

After a while, we accumulate some votes, and we decide to check on the status of our poll.

```clojure
(get-poll-results! my-client "jkooopjr5")
;; => {:content {:creator           {:avatar_path    "/images/avatars/nick-nichols.png"
;;                                                       :displayname    "Nick Nichols"
;;                                                       :monthly_points 0
;;                                                       :username       "nick-nichols"}
;;                                   :original_deadline nil
;;                                   :comments          1
;;                                   :type              "poll"
;;                                   :pin               nil
;;                                   :title             "Test Poll"
;;                                   :poll              {:reset_at           nil
;;                                                       :private            1
;;                                                       :poll_info          {:vpn                  0
;;                                                                            :description          nil
;;                                                                            :captcha              1
;;                                                                            :ma                   1
;;                                                                            :original_description nil
;;                                                                            :nsfw                 0
;;                                                                            :co                   1
;;                                                                            :creator_country_name "United States of America"
;;                                                                            :only_reg             0
;;                                                                            :ma_limit             nil
;;                                                                            :mip                  0
;;                                                                            :image                nil
;;                                                                            :edited_at            nil
;;                                                                            :show_results         1
;;                                                                            :enter_name           0
;;                                                                            :creator_country      "us"}
;;                                                       :total_voters       5850
;;                                                       :title              "What is your favorite animal?"
;;                                                       :original_title     nil
;;                                                       :poll_answers       [{:answer          "Cats"
;;                                                                             :id              "ub339by5f4e4"
;;                                                                             :original_answer nil
;;                                                                             :sorting         1
;;                                                                             :type            "text"
;;                                                                             :votes           50}
;;                                                                            {:answer          "Dogs"
;;                                                                             :id              "8xddpzqyq8dw"
;;                                                                             :original_answer nil
;;                                                                             :sorting         2
;;                                                                             :type            "text"
;;                                                                             :votes           900}
;;                                                                            {:answer          "Rabbits"
;;                                                                             :id              "12fdrhb67po0"
;;                                                                             :original_answer nil
;;                                                                             :sorting         3
;;                                                                             :type            "text"
;;                                                                             :votes           4900}]
;;                                                       :total_votes        0
;;                                                       :is_points_eligible 0
;;                                                       :is_votable         1
;;                                                       :last_vote_at       nil}
;;                                   :status            "active"
;;                                   :id                "jkooopjr5"
;;                                   :has_webhooks      0
;;                                   :deadline          nil
;;                                   :media             nil
;;                                   :cookie_id         "jkooopjr5"
;;                                   :created_at        "2021-10-22T21:37:34Z"}
;;                         :success 1}
```

If we navigate `:content -> :poll -> :poll_answers`, we can see that rabbits have a commanding lead.

When we created our poll, Strawpoll sends us back a map of data.
The `content_id` key of that map contains the string we need to get the poll's state and to delete the poll.
A convenience function has been added to extract this value:

```clojure
(->poll-id (create-poll! my-client "What is your favorite animal?" ["Cats" "Dogs" "Rabbits"]))
;; => "jkooopjr5"
```

Eventually, enough time passes and we decide to delete our poll for good.

```clojure
(delete-poll! my-client "jkooopjr5")
;; {:message "Delete successful!" :success 1}
```

### REPL Driven Development

To try out this library's functionality, load `strawpoll-client.repl` into your REPL.
This namespace has a Rich comment block demonstrating basic usage of each function.
All you need to do is update the API key in `->client`.
Be sure you remove this value before committing this code.
The API key ought to be treated as any application secret would be.

## License

Copyright © 2021-2022 - [Wall Brew Co](https://wallbrew.com/)

This software is provided for free, public use as outlined in the [MIT License](https://github.com/Wall-Brew-Co/strawpoll-client/blob/master/LICENSE)
