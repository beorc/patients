(ns patients.system
  (:require [com.stuartsierra.component :as component]
            [patients.router :refer [create-router]]
            [patients.web :refer [create-server]]
            [patients.db :refer [create-connection]]))

(defn create-system [config-options]
  (component/system-map
   :db (create-connection (:db-spec config-options))
   :router (component/using
            (create-router)
            [:db])
   :web (component/using
            (create-server (:service-map config-options))
            [:router])))
