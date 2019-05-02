(ns nukr-api.validator)

(defn user-body? [body]
  (and (false? (nil? body))
       (contains? body "name")
       (contains? body "birthday")
       (contains? body "sex")
       (contains? body "privacy")))