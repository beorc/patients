(ns patients.web
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn parse-port [port]
  (when port
   (cond
     (string? port) (Integer. port)
     (number? port) port
     :else          (throw (Exception. (str "invalid port value: " port))))))

(defrecord Web [service-map runnable-service router]
  component/Lifecycle

  (start [this]
    (if runnable-service ; already started
      this
      (let [port (parse-port (:port service-map))]
        (println (str "\nStarting web server on port: " port))
        (assoc this :runnable-service
          (run-jetty (:routing-table router)
                     (merge service-map
                       {:port port
                        :join? false}))))))

  (stop [this]
    (if (not runnable-service) ; already stopped
      this
      (do (.stop runnable-service)
          (assoc this :runnable-service nil)))))

(defn create-server [service-map]
  (map->Web {:service-map service-map}))
