(ns patients.core
  (:gen-class)
  (:require [patients.system :refer [create-system]]
            [patients.db :refer [build-db-spec-from-env]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]))

(defn fetch-port
  ([] (fetch-port 8080))
  ([default-port] (or (env :port) default-port 8080)))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nStarting patients...")

  (-> (create-system
        {:db-spec (build-db-spec-from-env)
         :service-map  {:env  :production
                        :port (fetch-port (first args))}})
      component/start))
