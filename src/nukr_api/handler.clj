(ns nukr-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [nukr-api.validator :as validator]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [nukr-api.database as db]))

(defroutes app-routes
           (POST "operation/add/user" request
             let [user (try
                         (json/read-str (slurp (get-in request [:body])))
                         (catch Exception e nil))]
             (if (validator/user-body? user)
               {:status 200
                :body   (db/create-account user)}
               {status 400
                :body  "Bad Request"}))

           (GET "/user/:id" [id]
             (let account (db/get-account id)
                          if ((not= account nil)
                               {:status 200
                                :body   account}
                               {status: 400
                                :body   "Bad Request"})))
           (POST "/user/:id/addfriend/:fid"
                 (let user (db/add-friend id fid)))



           (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
