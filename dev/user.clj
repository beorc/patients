(ns user
  (:require [patients.system]
            [patients.db :refer [build-db-spec-from-env build-db-url-from-env]]
            [reloaded.repl :refer [system reset stop]]
            [environ.core :refer [env]]
            [ragtime.jdbc]
            [ragtime.repl]))

(def ragtime-config
  {:datastore  (ragtime.jdbc/sql-database {:connection-uri (build-db-url-from-env)})
   :migrations (ragtime.jdbc/load-resources "migrations")})

(defn migrate []
  (ragtime.repl/migrate ragtime-config))

(defn rollback []
  (ragtime.repl/rollback ragtime-config))

(reloaded.repl/set-init! #(patients.system/create-system {:db-spec (build-db-spec-from-env)
                                                          :service-map  {:env  :dev
                                                                         :port 8080}}))
