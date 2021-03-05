(ns patients.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]))

(defn render-menu-items [& items]
  [:div (for [item items] [:div.menuitem item])])

(defn render-menu []
  (render-menu-items
    (link-to "/patients" "List patients")
    (link-to "/patients/new" "New patient")))

(defn render [& content]
  (html5
    [:head
      [:title "Patients"]
      (include-css "/css/layout.css")]
    [:body
     (render-menu)
     [:div.content content]]))
