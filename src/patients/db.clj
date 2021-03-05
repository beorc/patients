(ns patients.db
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]))

(defn- as-properties [m]
  (let [p (java.util.Properties.)]
    (doseq [[k v] m]
      (.setProperty p (name k) (str v)))
    p))

(defrecord DataSource [datasource]
  java.io.Closeable
  (close [_]
    (.close datasource)))

(defn- create-connection-pool
  "Create a connection pool for the given database spec."
  [{:keys [connection-uri subprotocol subname classname
           excess-timeout idle-timeout
           initial-pool-size minimum-pool-size maximum-pool-size
           test-connection-query
           idle-connection-test-period
           test-connection-on-checkin
           test-connection-on-checkout]
    :or {excess-timeout (* 30 60)
         idle-timeout (* 3 60 60)
         initial-pool-size 3
         minimum-pool-size 3
         maximum-pool-size 15
         test-connection-query nil
         idle-connection-test-period 0
         test-connection-on-checkin false
         test-connection-on-checkout false}
    :as spec}]
  (->DataSource (doto (ComboPooledDataSource.)
    (.setDriverClass classname)
    (.setJdbcUrl (or connection-uri (str "jdbc:" subprotocol ":" subname)))
    (.setProperties (as-properties (dissoc spec
                                           :classname :subprotocol :subname :connection-uri
                                           :naming :delimiters :alias-delimiter
                                           :excess-timeout :idle-timeout
                                           :initial-pool-size :minimum-pool-size :maximum-pool-size
                                           :test-connection-query
                                           :idle-connection-test-period
                                           :test-connection-on-checkin
                                           :test-connection-on-checkout)))
    (.setMaxIdleTimeExcessConnections excess-timeout)
    (.setMaxIdleTime idle-timeout)
    (.setInitialPoolSize initial-pool-size)
    (.setMinPoolSize minimum-pool-size)
    (.setMaxPoolSize maximum-pool-size)
    (.setIdleConnectionTestPeriod idle-connection-test-period)
    (.setTestConnectionOnCheckin test-connection-on-checkin)
    (.setTestConnectionOnCheckout test-connection-on-checkout)
    (.setPreferredTestQuery test-connection-query))))

(defrecord JDBCDatabase [db-spec db]
  component/Lifecycle

  (start [this]
    (if db
      this
      (assoc this :db (create-connection-pool db-spec))))

  (stop [this]
    (if (not db)
      this
      (do
        (.close db)
        (assoc this :db nil)))))

(defn- db-env [name]
  (env (keyword (str "database-" name))))

(defn build-db-spec-from-env []
  {:classname "org.postgresql.Driver"
    :subprotocol "postgresql"
    :user (db-env "username")
    :password (db-env "password")
    :initial-pool-size (Integer. (db-env "initial-pool-size"))
    :min-pool-size (Integer. (db-env "min-pool-size"))
    :max-pool-size (Integer. (db-env "max-pool-size"))
    :subname (db-env "subname")})

(defn build-db-url-from-env []
  (str "jdbc:postgresql:" (db-env "subname") "?user=" (db-env "username") "&password=" (db-env "password")))

(defn create-connection [db-spec]
  (map->JDBCDatabase {:db-spec db-spec}))
