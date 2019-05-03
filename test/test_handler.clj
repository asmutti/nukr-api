(ns test-handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as cheshire]
            [nukr.handler :refer :all]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))


(deftest http-test

  (testing "Test GET request to /api/:id returns expected response"
    (let [response (app (-> (mock/request :get  "/api/user/12345")))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))))

  (testing "Test GET request to /api/user/12345/friend-suggestion returns expected response"
    (let [response (app (-> (mock/request :get  "/api/user/12345/friend-suggestion")))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))))

  (testing "Test POST request to /api/user/:id/add-friend/:fid returns expected response"
    (let [response (app (-> (mock/request :post  "/api/user/12342/add-friend/12347")))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))))

  (testing "Test POST request to /echo returns expected response"
       (let [user {:id       98989
                   :name     "Peter Parker"
                   :birthday "03/05/1998"
                   :sex      "Male"
                   :privacy  false
                   :friends []
                   }
             response (app (-> (mock/request :post "/api/user/98989")
                               (mock/content-type "application/json")
                               (mock/body (cheshire/generate-string user))))
             body (parse-body (:body response))]
         (is (= (:status response) 200))))

  (testing "Test POST request to /api/user/98989 with missing attribute returns expected response"
    (let [user {:id       98989
                :name     "Peter Parker"
                :sex      "Male"
                :privacy  false
                :friends []
                }
          response (app (-> (mock/request :post "/api/user/98989")
                            (mock/content-type "application/json")
                            (mock/body (cheshire/generate-string user))))
          body (parse-body (:body response))]
      (is (= (:status response) 400))))

  )