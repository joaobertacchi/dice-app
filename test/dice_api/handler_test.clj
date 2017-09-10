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
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8"))
      (is (= (:result body) 6))))

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
      (is (= body pizza))))
  (testing "Test GET request to /api/primes endpoint"
    (let [primes {:primes [2]}
         response (app (-> (mock/request :get "/api/primes?max=2")
                           (mock/content-type "application/json")
                           (mock/body (cheshire/generate-string primes))))
         body (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8"))
      (is (= body primes)))
    (let [primes {:errors {:max "should be less than or equal to 1000"}}
         response (app (-> (mock/request :get "/api/primes?max=2000")
                           (mock/content-type "application/json")
                           (mock/body (cheshire/generate-string primes))))
         body (parse-body (:body response))]
      (is (= (:status response) 403))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8"))
      (is (= body primes)))
    (let [primes {:primes [2 3 5 7]}
         response (app (-> (mock/request :get "/api/primes?max=10")
                           (mock/content-type "application/json")
                           (mock/body (cheshire/generate-string primes))))
         body (parse-body (:body response))]
      (is (= (:status response) 200))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8"))
      (is (= body primes)))))