(ns dice-api.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [schema.core :as s]))

(s/defschema Pizza
  {:name s/Str
   (s/optional-key :description) s/Str
   :size (s/enum "L" "M" "S")
   :origin {:country (s/enum "FI" "PO")
            :city s/Str}})

(defn sieve [[xs ps]]
  (let [[p & more] xs]
    [(remove #(zero? (rem % p)) xs) (cons p ps)]))

(defn primes [n]
  (if (< n 2)
    []
    (->> [(range 2 (inc n)) nil]
         (iterate sieve)
         (drop-while #(<= (ffirst %) (Math/sqrt n)))
         first
         (apply concat))))

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Dice-api"
                    :description "Compojure Api example"}
             :tags [{:name "api", :description "some apis"}]}}}

    (context "/api" []
      :tags ["api"]

      (GET "/plus" []
        :return {:result Long}
        :query-params [x :- Long, y :- Long]
        :summary "adds two numbers together"
        (ok {:result (+ x y)}))

      (GET "/primes" []
        :return {:primes [Long]}
        :query-params [max :- (describe Long "Integer non-negative value equal or smaller than 1000")]
        :summary "calculates all primes up to max param"
        (if (<= max 1000)
          (ok {:primes (vec (sort (primes max)))})
          (forbidden {:errors {:max "should be less than or equal to 1000"}})))

      (POST "/echo" []
        :return Pizza
        :body [pizza Pizza]
        :summary "echoes a Pizza"
        (ok pizza)))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty app {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))