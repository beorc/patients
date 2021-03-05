(ns patients.patient.validator
  (:require [bouncer.core :as b]
            [clojure.pprint :refer [pprint]]
            [bouncer.validators :as v]))

(defn- try-parse-int [val]
  (try
    (when val
      (cond
        (string? val) (Integer. val)
        (number? val) val
        :else val))
    (catch Exception e1# val)))

(defn- try-assoc-int [params key]
  (assoc params key (try-parse-int (get params key))))

(def validator
  {:first_name [v/required v/string]
   :middle_name [v/required v/string]
   :last_name [v/required v/string]
   :policy_number [v/required v/integer]
   :address [v/required v/string]
   :gender [v/required [v/matches #"^m|f$"]]
   :birthday [v/required v/string v/datetime]})

(defn validate [params]
  (b/validate (try-assoc-int params :policy_number) validator))

(defn valid? [params]
  (b/valid? (try-assoc-int params :policy_number) validator))

(defn format-validation-error [[attr errors]]
  [:dt attr (map (fn [error] [:dd error]) errors)])

(defn format-validation-errors [[validation-errors]]
  (let [errors (seq validation-errors)]
    [:div.errors
     [:p "Errors"]
     [:dl (map format-validation-error errors)]]))
