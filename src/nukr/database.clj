(ns nukr.database)


(comment (def database
           "social media database"
           (atom {
                  :name     "Nukr"
                  :accounts {}
                  })))

(def database
  "Sample database"
  (atom {
         :name     "Nukr"
         :accounts {
                    12341 {
                           :id 12341
                           :name     "Tony Stark"
                           :birthday "20/03/1978"
                           :sex      "Male"
                           :privacy  false
                           :friends  #{12342 12343 12344 12345 12346 12347}
                           }
                    12342 {
                           :id 12342
                           :name     "Stephen Strange"
                           :birthday "01/01/1979"
                           :sex      "Male"
                           :privacy  true
                           :friends  #{12341 12343 12344}
                           }
                    12343 {
                           :id 12343
                           :name     "Bruce Banner"
                           :birthday "01/01/1972"
                           :sex      "Male"
                           :privacy  false
                           :friends  #{12342 12341 12344 12345 12346 12347}
                           }
                    12344 {
                           :id 12344
                           :name     "Thor Odinson"
                           :birthday "01/01/240"
                           :sex      "Male"
                           :privacy  true
                           :friends  #{12345 12346 12347 12343 12342 12341}
                           }
                    12345 {
                           :id 12345
                           :name     "Natasha Romanoff"
                           :birthday "03/02/1984"
                           :sex      "Female"
                           :privacy  false
                           :friends  #{12346 12347 12344 12343 12341}
                           }
                    12346 {
                           :id 12346
                           :name     "Clint Barton"
                           :birthday "03/02/1983"
                           :sex      "Male"
                           :privacy  true
                           :friends  #{12347 12345 12344 12343 12341}
                           }
                    12347 {
                           :id 12347
                           :name     "Steve Rogers"
                           :birthday "05/10/1910"
                           :sex      "Male"
                           :privacy  false
                           :friends  #{12346 12345 12344 12343 12341}
                           }
                    }
         }))

(defn get-database []
  @database)

(defn get-account [id]
  "returns an account or nil"
  (get-in @database [:accounts id]))

(defn is-id-valid? [id]
  (if (some? (get-account id))
    true
    false))

(defn get-friends-set [id]
  (get-in @database [:accounts id :friends]))

(defn is-privacy-on? [id]
  "checks if the user has the privacy attribute toggled to true"
  (true? (get-in @database [:accounts id :privacy])))

(defn is-friend? [id1 id2]
  "checks if id2 is friend of id1"
  (contains? (get-in @database [:accounts id1 :friends]) id2))

(defn is-already-friend? [id1 id2]
  (contains? (get-friends-set id1) id2))

;; all accounts are stored in an atom, consistency is guaranteed during a swap! within the atom.
(defn create-account! [id name birthday sex privacy]
  "Creates one account and swap! it to the database."
  (let [account
        {:id       id
         :name     name
         :birthday birthday
         :sex      sex
         :privacy  privacy
         :friends  #{}}]
    (swap! database update-in [:accounts] assoc id account) account))

(defn create-account [id account]
  "creates the account if the id is not found in the db"
  (if (not (is-id-valid? id))
    (create-account! id
                     (get-in account [:name])
                     (get-in account [:birthday])
                     (get-in account [:sex])
                     (get-in account [:privacy]))
    nil))

;this method returns a new database copy after the new connection between users are
;successfully made
(defn transact! [database operations]
  (swap! database
         (fn [database]
           (reduce
             (fn [curr-database operation]
               (operation curr-database))
             database
             operations))))

(defn operation [id-origin id-dest]
  "updates the database with the new friend id"
  (fn [database]
    (update-in database [:accounts id-origin :friends] conj id-dest)))

(defn prep-transaction [id1 id2]
  (if (true? (and (is-id-valid? id1) (is-id-valid? id2)))
    (if (not (is-already-friend? id1 id2))
      (transact! database
                 [(operation id1 id2)
                  (operation id2 id1)])
      nil)
    nil))

(defn add-friend [id1 id2]
  (if (not= (prep-transaction id1 id2) nil)
    (get-account id1)
    nil))

;; -- methods related to the friend suggestion. --

(defn count-common-friends [id1 id2]
  "id1 is one user who will get the recommendation
  id2 is one user who is not friend of id1
  returns the number of common friends"
  (count
    (clojure.set/intersection
      (get-friends-set id1) (get-friends-set id2)))
  )

(defn get-non-friends-set [id]
  "returns a set of ids that aren't friends of id"
  (reduce (fn [non-friends i]
            (reduce (fn [acc j]
                      (if (and (false? (is-friend? j id))
                               (false? (is-privacy-on? j)))
                        (if (not= id j)                     ;checks if it's not the id itself
                          (conj acc j)
                          acc)
                        acc))
                    non-friends
                    (get-friends-set i)))
          #{}
          (get-friends-set id)))

(defn rank-suggestions [id non-friend-set]
  "returns a map ranking the ids based on common friends with id"
  (reduce (fn [rank i]
            (assoc rank (keyword (str i)) (count-common-friends id i)))
          {}
          non-friend-set))

(defn get-friends-suggestions-sorted [id]
  (sort-by val > (let [non-friend-set (get-non-friends-set id)]
                   (rank-suggestions id non-friend-set))))

(defn get-friends-suggestions [id]
  (let [non-friend-set (get-non-friends-set id)]
    (rank-suggestions id non-friend-set)))
