(ns patients.patient.storage
  (:require [clojure.java.jdbc :as jdbc]))

(defn list-patients [db]
  (jdbc/query db
              ["SELECT * FROM patients ORDER BY inserted_at ASC"]))

(defn get-patient [db id]
  (jdbc/query db
              ["SELECT * FROM patients WHERE id = ? LIMIT 1" id] :result-set-fn first))

(defn create-patient [db attrs]
  (jdbc/insert! db :patients attrs))

(defn update-patient [db id attrs]
  (jdbc/update! db :patients attrs ["id = ?" id]))

(defn delete-patient [db id]
  (jdbc/delete! db :patients ["id = ?" id]))
