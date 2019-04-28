(ns nukr-api.database)


; consider def or defonce
(def nukr-database
  "social media database"
  (atom {
         :name "Nukr"
         :accounts {}
         }))

;; all accounts are stored in an atom, consistency is guaranteed during a swap! within the atom.
(defn create-account! [id name birthday sex privacy]
  "Creates one account and commit it to the database."
   (let [account
         {:name name
          :birthday birthday
          :sex sex
          :privacy privacy
          :friends #{}}]
     (swap! nukr-database update-in [:accounts] assoc id account)))

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

(defn is-privacy-on? [id]
  "checks if the user has the privacy attribute toggled to true"
  (true? (get-in @example [:accounts id :privacy])))

(defn is-friend? [id1 id2]
  "checks if id2 is friend of id1"
  (contains? (get-in @example [:accounts id1 :friends]) id2))

(defn suggest-friends [id]
  "this method should get an id of a user, look through its friends and return n number
  of friends suggestion."
  (let [:friends (get-in @nukr-database [:accounts id :friends])]
    friends))


(defn get-non-friends-set [id]
  "")

(defn rank-friend-suggestion [id non-friend-set]
  "receives a set with ids of user that aren't friends of 'id', returns a sorted set")

(comment

  (def example
    "for now, accounts are 5 random numbers"
    (atom {
           :name "Nukr"
           :accounts {
                       12345 {
                              :name "Alexandre Mutti"
                              :birthday "20/04/1992"
                              :sex "Male"
                              :privacy false
                              :friends #{12346 19881 15502}
                              }
                       12346 {
                              :name "James Bond"
                              :birthday "01/01/1940"
                              :sex "Male"
                              :privacy false
                              :friends #{12345}
                              }
                       19881 {
                              :name "Thor Odinson"
                              :birthday "01/01/240"
                              :sex "Male"
                              :privacy true
                              :friends #{12345 15502 13923 19392}
                              }
                       15502 {
                              :name "Loki Odinson"
                              :birthday "03/02/240"
                              :sex "Male"
                              :privacy false
                              :friends #{12345 19881}
                              }
                       13923 {
                              :name "Tony Stark"
                              :birthday "03/02/1970"
                              :sex "Male"
                              :privacy true
                              :friends #{19881 19392}
                              }
                       19392 {
                              :name "Steve Rogers"
                              :birthday "05/10/1910"
                              :sex "Male"
                              :privacy false
                              :friends #{13923 19881}
                              }
                       }
           }))

  (let [account
        {:name "Odinson"
         :birthday "20/01/300"
         :sex "Male"
         :privacy false
         :friends #{}}]
    (swap! example update-in [:accounts] assoc 12090 account))

  {:get-account [(get-in @example [:accounts 12094])]}

  {:get-account [(get-in @example [:accounts 12345 :sex])]}




  (create-account 12314 "Loki Odinson" "10/02/300" "Male" true)
  (create-account 12313 "Thor Odinson" "10/02/300" "Male" false)
  (create-account 12354 "Odin Borson" "10/02/-1021" "Male" false)

  ;;atualiza a key :friends dentro de :accounts
  (swap! example update-in [:accounts 12345 :friends] conj 12094)

  (transact! example [(operation 12345 12354) (operation 12354 12345)])

  )