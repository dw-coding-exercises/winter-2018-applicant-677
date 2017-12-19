(ns my-ex.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [hiccup.page :refer [html5]]
            [my-ex.home :as home]
            [my-ex.search :as search]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defroutes app
           (GET "/" [] home/page)
           (POST "/search" [city state] (search/page city state))
           (route/resources "/")
           (route/not-found "Not found"))

(def handler
  (-> app
      (wrap-defaults site-defaults)
      wrap-reload))
