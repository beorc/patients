(ns patients.patients-test
  (:require [clojure.test :refer [deftest testing is]]
            [patients.router :refer [create-router]]
            [patients.db :refer [build-db-spec-from-env create-connection]]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [clojure.pprint :refer [pprint]]
            [ring.mock.request :as mock]))

(def system-config
  {:db-spec (build-db-spec-from-env)
   :service-map  {:env  :test
                  :port 8080}})

(defn create-system [config-options]
  (component/system-map
   :db (create-connection (:db-spec config-options))
   :router (component/using
            (create-router)
            [:db])))

(defmacro with-started-components [bindings & body]
  `(let [~(bindings 0) (component/start ~(bindings 1))]
     (try
       (let* ~(destructure (vec (drop 2 bindings)))
         ~@body)
       (catch Exception e1#)
       (finally
         (component/stop ~(bindings 0))))))

(defprotocol Action
  (do-it [self]))

;; (deftest patients-test
;;   (with-started-components
;;     [system (create-system system-config)
;;      app (:routing-table (:router system))]

    ;; (println "1===================================")
    ;; (let [response (app (mock/request :get "/patients"))]
    ;;   (pprint response)
    ;;   (is (= (:status response) 200))
    ;;   (is (.contains (:body response) "Hello World")))
    ;; (println "2===================================")))

    ;; (testing "POST /patients"
    ;;   (let [response (app (mock/request :post "/patients"))]
    ;;     (is (= (:status response) 200))
    ;;     (is (.contains (:body response) "Hello World"))))

    ;; (testing "PUT /patients/:id"
    ;;   (let [response (app (mock/request :put "/patients"))]
    ;;     (is (= (:status response) 200))
    ;;     (is (.contains (:body response) "Hello World"))))

    ;; (testing "DELETE /patients/:id"
    ;;   (let [response (app (mock/request :delete "/patients"))]
    ;;     (is (= (:status response) 200))
    ;;     (is (.contains (:body response) "Hello World"))))

    ;; (testing "GET /patients"
    ;;   (let [response (app (mock/request :get "/patients"))]
    ;;     (is (= (:status response) 200))
    ;;     (is (.contains (:body response) "Hello World"))))

    ;; (testing "GET /patients/:id"
    ;;   (let [response (app (mock/request :get "/patients/123"))]
    ;;     (is (= (:status response) 200))
    ;;     (is (.contains (:body response) "Hello World"))))

    ;; (testing "not-found route"
    ;;   (let [response (app (mock/request :get "/patients/invalid"))]
    ;;     (is (= (:status response) 404))))))
