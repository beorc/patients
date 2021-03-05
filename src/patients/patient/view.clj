(ns patients.patient.view
  (:require [patients.layout :refer [render]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [hiccup.page :refer [html5]]
            [hiccup.form :refer :all]))

(defn patient-row [patient]
  [:tr
    [:td (get patient :first_name)]
    [:td (get patient :middle_name)]
    [:td (get patient :last_name)]
    [:td (get patient :gender)]
    [:td (get patient :birthday)]
    [:td (get patient :policy_number)]
    [:td [:a
          {:href (str "/patients/" (get patient :id))}
          "Show"]]])

(defn index [patients]
  (render
    [:h1 "Patients"]
    [:tr
      [:th "First Name"]
      [:th "Middle Name"]
      [:th "Last Name"]
      [:th "Gender"]
      [:th "Birthday"]
      [:th "Policy Number"]]
    (map patient-row patients)))

(defn show [patient]
  (render
    [:h1 "Patient"]
    [:dl
      [:dt "First Name"]
      [:dd (get patient :first_name)]
      [:dt "Middle Name"]
      [:dd (get patient :middle_name)]
      [:dt "Last Name"]
      [:dd (get patient :last_name)]
      [:dt "Gender"]
      [:dd (get patient :gender)]
      [:dt "Birthday"]
      [:dd (get patient :birthday)]
      [:dt "Address"]
      [:dd (get patient :address)]
      [:dt "Policy Number"]]
      [:dd (get patient :policy_number)]
    (form-to [:delete (str "/patients/" (get patient :id))]
            (anti-forgery-field)
            (submit-button "Delete patient"))))

(defn- form [[method path] params errors]
  [:div
   [:p errors]
   (form-to [method path]
            [:p "First name:"]
            (text-field :first_name (get params :first_name ""))
            [:p "Middle name:"]
            (text-field :middle_name (get params :middle_name ""))
            [:p "Last name:"]
            (text-field :last_name (get params :last_name ""))
            [:p "Gender:"]
            (let [options [["Male" "m"] ["Femaile" "f"]]
                  gender (get params :gender "m")
                  selected (if (= gender "") "m" gender)]
              (drop-down :gender options selected))
            [:p "Birthday:"]
            (text-field {:placeholder "YYYY-MM-DD"} :birthday (get params :birthday ""))
            [:p "Address:"]
            (text-field :address (get params :address ""))
            [:p "Policy number:"]
            (text-field :policy_number (get params :policy_number ""))
            (submit-button "Submit"))])

(defn new [params errors]
  (->
    (form [:post "/patients"] params errors)
    (render)))

(defn edit [params errors]
  (->
    (form [:put (str "/patients/" (get params :id))] params errors)
    (render)))

(defn not-found []
  (render [:p "Patient not found"]))
