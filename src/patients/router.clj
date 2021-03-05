(ns patients.router
  (:require [patients.patient.resource :as patient]
            [com.stuartsierra.component :as component]
            [compojure.core :refer [routes]]
            [compojure.route]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defrecord Router [routing-table db]
  component/Lifecycle

  (start [this]
    (if routing-table ; already started
      this
      (let [db-spec (:db db)]
        (assoc this :routing-table
               (-> (routes
                    (patient/routes db-spec)
                    (compojure.route/not-found "Not Found"))
                   (wrap-resource "public")
                   (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false)))))))

  (stop [this]
    (if (not routing-table) ; already stopped
      this
      (assoc this :routing-table nil))))

(defn create-router []
  (map->Router {}))
