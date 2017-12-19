(ns my-ex.search
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clj-http.client :as client]
            [hiccup.page :refer [html5]]
            [my-ex.home :as home]))

;; Getter for turbovote api. URL should be
;; read from a config instead of hardcoded
(defn get-elections
  [dd]
  (edn/read-string
    (:body
      (client/get "https://api.turbovote.org/elections/upcoming"
                  {:query-params {:district-divisions dd}}))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                ;;
;;                        HTML TEMPLATES                          ;;
;;                                                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Note: These are extremely generic and unformatted. Would take time
;; to do some styling/layout

(defn registration-methods-template
  [{:keys [instructions
           type
           url
           deadline-postmarked
           acceptable-forms]}]
  [:div
   [:h5 "Registration Type"]
   [:p (name type)]
   [:h5 "Registration Instructions"]
   [:p
    (vals instructions)]
   (when url
     [:a {:href url} "Click to Register"])
   [:h6 "Postmark Deadline"]
   [:p (or "N/A" (str deadline-postmarked))]
   [:h6 "Acceptable Forms"]
   [:p
    (apply str
           (for [form acceptable-forms]
             (name (:name form))))]])

(defn voting-methods-template
  [{:keys [instructions
           type
           primary
           excuse-required
           ballot-request-deadline-received
           acceptable-forms]}]
  [:div
   [:h5 "Registration Type"]
   [:p (name type)]
   [:h5 "Primary Voting Method?"]
   [:p (str primary)]
   [:h5 "Excuse Required?"]
   [:p (str excuse-required)]
   [:h5 "Registration Instructions"]
   [:p
    (vals instructions)]
   [:h6 "Postmark Deadline"]
   [:p (or "N/A"
           (str ballot-request-deadline-received))]
   [:h6 "Acceptable Forms"]
   [:p
    (or "N/A"
        (apply str
               (for [form acceptable-forms]
                 (name (:name form)))))]])

(defn divisions-template
  [{:keys [voting-methods
           voter-registration-methods]}]
  (concat [[:h2 "How to Register!"]]
          (map registration-methods-template
               voter-registration-methods)
          [[:h2 "How to Vote!"]]
          (map voting-methods-template
               voting-methods)))

(defn election-template
  [{:keys [district-divisions
           date
           description]}]
  (concat
    [[:div
      [:h2 description]
      [:h3 date]]]
    (map divisions-template
         district-divisions)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;                                                                ;;
;;                        MAIN PAGE FN                            ;;
;;                                                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn page [city state]
  (let [st              (str/lower-case state)
        ci              (str/lower-case city)
        state-ocd-id    (str "ocd-division/country:us/state:" st)
        city-ocd-id     (str state-ocd-id "/place:" ci)
        election-params (str/join ","
                                  [state-ocd-id
                                   city-ocd-id])
        elections       (get-elections election-params)]
    (html5
      (home/header "")
      (map election-template
           elections)
      [:a {:href "/"} "Back to Home"])))
