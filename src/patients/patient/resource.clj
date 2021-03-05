(ns patients.patient.resource
  (:require [patients.patient.storage :as storage]
            [patients.patient.view :as view]
            [patients.patient.validator :as validator]
            [compojure.core :refer [POST PUT DELETE GET]]
            [clojure.pprint :refer [pprint]]
            [ring.util.response]))

(defn response [html status]
  (->
    (ring.util.response/response html)
    (ring.util.response/status status)
    (ring.util.response/content-type "text/html; charset=utf-8")))

(defn not-found []
  (->
   (view/not-found)
   (response 404)))

(defn list-patients [db]
  (let [patients (storage/list-patients db)]
    (->
      (view/index patients)
      (response 200))))

(defn show-patient [db]
  (fn [id]
    (if-let [patient (storage/get-patient db id)]
      (->
        (view/show patient)
        (response 200))
      (not-found))))

(defn new-patient []
  (->
    (view/new {} {})
    (response 200)))

(defn edit-patient [db]
  (fn [id]
    (if-let [patient (storage/get-patient db id)]
      (->
        (view/edit patient)
        (response 200))
      (not-found))))

(defn create-patient [db]
  (fn [params]
    (if (validator/valid? params)
      (let [patient (storage/create-patient db params)]
        (ring.util.response/redirect (str "/patients/" (get patient :id))))
      (let [validation-errors (validator/format-validation-errors (validator/validate params))]
        (->
          (view/new params validation-errors)
          (response 422))))))

(defn update-patient [db]
  (fn [params]
    (if (validator/valid? params)
      (let [patient (storage/update-patient db (:id params) (dissoc params :id))]
        (ring.util.response/redirect (str "/patients/" (get patient :id))))
      (let [validation-errors (validator/format-validation-errors (validator/validate params))]
        (println "=============================")
        (pprint validation-errors)
        (->
         (view/edit params validation-errors)
         (response 422))))))

(defn delete-patient [db]
  (fn [id]
    (storage/delete-patient db id)
    (ring.util.response/redirect "/patients")))

(defn routes [db]
  (compojure.core/routes
   (GET "/patients" [] (list-patients db))
   (POST "/patients" request ((create-patient db) (:params request)))
   (PUT "/patients/:id" request ((update-patient db) (:params request)))
   (GET "/patients/:id/edit" [id] ((edit-patient db) id))
   (GET "/patients/new" [] (new-patient))
   (GET "/patients/:id" [id] ((show-patient db) id))
   (DELETE "/patients/:id" [id] ((delete-patient db) id))))
