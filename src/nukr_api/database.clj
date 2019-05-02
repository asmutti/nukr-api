(ns nukr-api.database)


; consider def or defonce
(def nukr-database
  "social media database"
  (atom {
         :name     "Nukr"
         :accounts {}
         }))

;; all accounts are stored in an atom, consistency is guaranteed during a swap! within the atom.
(defn add-account! [id name birthday sex privacy]
  "Creates one account and swap! it to the database."
  (let [account
        {:name     name
         :birthday birthday
         :sex      sex
         :privacy  privacy
         :friends  #{}}]
    (swap! nukr-database update-in [:accounts] assoc id account)))

(defn create-account [account]
  "creates the account if the id is not found in the db"
  (if (not (is-id-valid? get-in account [:id]))
    (create-account! (get-in account [:id])
                     (get-in account [:name])
                     (get-in account [:birthday])
                     (get-in account [:sex])
                     (get-in account [:privacy]))
    nil))


(defn operation [id-origin id-dest]
  "updates the database with the new friend id"
  (fn [database]
    (update-in database [:accounts id-origin :friends] conj id-dest)))

;this method returns a new database copy after the new connection between friends are
;successfully made
(defn transact! [database operations]
  (swap! database
         (fn [database]
           (reduce
             (fn [curr-database operation]
               (operation curr-database))
             database
             operations))))

(defn add-friend [id1 id2]
  (if (true? (and (is-id-valid? id1) (is-id-valid? id2)))
    (if (not (is-already-friend? id1 id2))
      (transact! nukr-database
                 [(operation id1 id2)
                  (operation id2 id1)])
      nil)
    nil))

(defn is-already-friend? [id1 id2]
  (contains? (get-in @nukr-database [:accounts id1 :friends]) id2))

(defn is-id-valid? [id]
  (if (some? (get-in @nukr-database [:accounts id]))
    true
    false))

(defn get-account [id]
  "returns an account or nil"
  (get-in @nukr-database [:accounts id]))

(defn is-privacy-on? [id]
  "checks if the user has the privacy attribute toggled to true"
  (true? (get-in @nukr-database [:accounts id :privacy])))

(defn is-friend? [id1 id2]
  "checks if id2 is friend of id1"
  (contains? (get-in @nukr-database [:accounts id1 :friends]) id2))

(defn suggest-friends [id]
  "this method should get an id of a user, look through its friends and return n number
  of friends suggestion."
  (let [:friends (get-in @nukr-database [:accounts id :friends])]
    friends))

(defn get-friends-set [id]
  (get-in @nukr-database [:accounts id :friends]))

(defn count-common-friends [id1 id2]
  "id1 is one user who will get the recommendation
  id2 is one user who is not friend of id1"
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
                        (conj acc j)
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

  (comment

    (def nukr-database
      "for now, accounts are 5 random numbers"
      (atom {
             :name     "Nukr"
             :accounts {
                        12345 {
                               :name     "Alexandre Mutti"
                               :birthday "20/04/1992"
                               :sex      "Male"
                               :privacy  false
                               :friends  #{12346 19881 15502}
                               }
                        12346 {
                               :name     "James Bond"
                               :birthday "01/01/1940"
                               :sex      "Male"
                               :privacy  false
                               :friends  #{12345}
                               }
                        19881 {
                               :name     "Thor Odinson"
                               :birthday "01/01/240"
                               :sex      "Male"
                               :privacy  true
                               :friends  #{12345 15502 13923 19392}
                               }
                        15502 {
                               :name     "Loki Odinson"
                               :birthday "03/02/240"
                               :sex      "Male"
                               :privacy  false
                               :friends  #{12345 19881}
                               }
                        13923 {
                               :name     "Tony Stark"
                               :birthday "03/02/1970"
                               :sex      "Male"
                               :privacy  true
                               :friends  #{19881 19392}
                               }
                        19392 {
                               :name     "Steve Rogers"
                               :birthday "05/10/1910"
                               :sex      "Male"
                               :privacy  false
                               :friends  #{13923 19881}
                               }
                        }
             }))

    (let [account
          {:name     "Odinson"
           :birthday "20/01/300"
           :sex      "Male"
           :privacy  false
           :friends  #{}}]
      (swap! example update-in [:accounts] assoc 12090 account))

    {:get-account [(get-in @nukr-database [:accounts 12094])]}

    {:get-account [(get-in @nukr-database [:accounts 12345 :sex])]}




    (create-account 12314 "Loki Odinson" "10/02/300" "Male" true)
    (create-account 12313 "Thor Odinson" "10/02/300" "Male" false)
    (create-account 12354 "Odin Borson" "10/02/-1021" "Male" false)

    ;;atualiza a key :friends dentro de :accounts
    (swap! nukr-database update-in [:accounts 12345 :friends] conj 12094)

    (transact! nukr-database [(operation 12345 12354) (operation 12354 12345)])

    )