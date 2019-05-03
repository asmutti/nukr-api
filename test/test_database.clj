(ns test-database
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [nukr.database :refer :all]))

(def user1 {
            :name "Test user"
            :birthday "04/04/2004"
            :sex "Male"
            :privacy false
            })

(def user2 {
             :name "Test user2"
             :birthday "03/03/2003"
             :sex "Female"
             :privacy true
             })

(def user3 {
            :name "Test user3"
            :birthday "03/03/2003"
            :sex "Male"
            :privacy false
            })

(deftest db
  ;create accounts
  (is (not= (create-account 11111 user1) nil))

  (is (not= (create-account 22222 user2) nil))

  (is (not= (create-account 33333 user3) nil))

  (is (= #{} (get-friends-set 11111)))

  (is (not= (add-friend 11111 22222) nil))

  (is (= #{22222} (get-friends-set 11111)))

  (is (not= (add-friend 11111 33333) nil))

  ;test friend suggestion
  (is (= {:33333 1} (get-friends-suggestions 22222)))

  ;should be empty because the profile 22222 has the privacy
  ;toggled to true
  (is (= {} (get-friends-suggestions 33333)))

  (is (= #{22222 33333} (get-friends-set 11111)))

  (is (= (is-privacy-on? 22222)))

  (is (false? (is-privacy-on? 11111)))

  (is (= (is-id-valid? 11111) true))

  (is (= (is-id-valid? 99999) false))

  (is (true? (is-friend? 11111 22222)))

  (is (false? (is-friend? 22222 33333)))

  )
