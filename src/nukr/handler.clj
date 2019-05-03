(ns nukr.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [nukr.database :refer :all]
            [schema.core :as s])
  (:import (clojure.lang Keyword)))

(s/defschema User
  {:id                       Long
   :name                     s/Str
   :birthday                 s/Str
   :sex                      s/Str
   :privacy                  s/Bool
   (s/optional-key :friends) #{Long}})

(s/defschema Suggestions
  {Keyword s/Int})

(def app
  (api
    {:swagger
     {:ui   "/"
      :spec "/swagger.json"
      :data {:info {:title       "Nukr-api"
                    :description "Compojure API built as take home challenge
                    for a Software Engineer position at Nubank"}
             :tags [{:name "api", :description "social media endpoints"}]}}}

    (context "/api" []
      :tags ["api"]

      (GET "/user/:id" []
        :return User
        :path-params [id :- Long]
        :summary "returns user from the db"
        (ok (get-account id)))

      (GET "/user/:id/friend-suggestion" []
        :return Suggestions
        :path-params [id :- Long]
        :summary "returns a list of users as suggestion to be friend."
        (ok (get-friends-suggestions id)))

      (POST "/user/:id" []
        :path-params [id :- Long]
        :body [user User]
        :summary "Adds a user to the database, you need to provide the id."
        (ok (create-account id user)))

      (POST "/user/:id/add-friend/:fid" []
        :path-params [id :- Long, fid :- Long]
        :summary "Link two users as friends."
        (ok (add-friend id fid)))
      )))
