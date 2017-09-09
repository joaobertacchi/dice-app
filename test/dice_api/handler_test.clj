(ns dice-api.handler-test
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [dice-api.handler :refer :all]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest test-api
  (testing "Test GET request to /api/plus endpoint"
    (let [response (app (-> (mock/request :get "/api/plus?x=2&y=4")))
          body (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8")
      (is (= (:result body) 6)))))

  (testing "Test POST request to /api/echo endpoint"
    (let [pizza {:name "Turtle Pizza"
                :description "Pepperoni pizza"
                :size "L"
                :origin {
                  :country "FI"
                  :city "MyCity"}}
          response (app (-> (mock/request :post "/api/echo")
                            (mock/content-type "application/json")
                            (mock/body  (cheshire/generate-string pizza))))
          body     (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= body pizza)))))