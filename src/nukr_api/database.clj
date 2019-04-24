(ns nukr-api.database)


; consider def or defonce
(def nukr
  "social media database"
  (atom {
         :name "Nukr"
         :accounts #{}
         }))

;; all accounts are stored in an atom, consistency is guaranteed during a swap! within the atom.
(defn create-account [cpf name birthday sex privacy]
   (let [account
         {:cpf cpf
          :name name
          :birthday birthday
          :sex sex
          :privacy privacy
          :friends #{}}]
     (swap! nukr update-in [:accounts] assoc cpf account)))






(comment

  (def example
    "for now, accounts are 5 random numbers"
    (atom {
           :name "Nukr"
           :accounts #{
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
  )