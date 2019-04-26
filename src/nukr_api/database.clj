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
                              :friends #{}
                              }
                       12346 {
                              :name "James Bond"
                              :birthday "01/01/1940"
                              :sex "Male"
                              :privacy false
                              :friends #{}
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