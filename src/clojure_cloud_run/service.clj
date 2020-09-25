(ns clojure-cloud-run.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [environ.core :refer [env]]))


(defn about-page
  [request]
  (ring-resp/response (format "Cloud Run - Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(def bruce-lee-quotes
  '("Knowing is not enough, we must apply. Willing is not enough, we must do." 
    "If you spend too much time thinking about a thing, you'll never get it done." 
    "Absorb what is useful, discard what is useless and add what is specifically your own." 
    "Mistakes are always forgivable, if one has the courage to admit them." 
    "If you love life, don't waste time, for time is what life is made up of." 
    "Always be yourself, express yourself, have faith in yourself, do not go out and look for a successful personality and duplicate it."
    "The key to immortality is first living a life worth remembering."
    "You must be shapeless, formless, like water. When you pour water in a cup, it becomes the cup. When you pour water in a bottle, it becomes the bottle. When you pour water in a teapot, it becomes the teapot. Water can drip and it can crash. Become like water my friend."
    "Having no limitation as limitation"
    "If you always put limit on everything you do, physical or anything else. It will spread into your work and into your life. There are no limits. There are only plateaus, and you must not stay there, you must go beyond them."
    "Always be yourself, express yourself, have faith in yourself, do not go out and look for a successful personality and duplicate it."
    "Obey the principles without being bound by them."
    "If you want to learn to swim jump into the water. On dry land no frame of mind is ever going to help you."
    "The less effort, the faster and more powerful you will be."
    "It's not the daily increase but daily decrease. Hack away at the unessential."
    "Simplicity is the key to brilliance."))

(defn quotes-bruce-page [request]
  (let [random-quote (rand-nth bruce-lee-quotes)]
       {:status 200 :body (str "<html><body><style>div {
    display: inline-block;
    overflow: hidden;
    position: relative;
    width: 100%;
}

img {
    pointer-events: none;
    position: absolute;
    width: 100%;
    height: 100%;
    z-index: -1;
} h1 { color: #fff; font-size: 68px; text-align: center; padding: 280px 0; }
</style><img src=\"https://source.unsplash.com/1600x900?zen\">
<h1>" random-quote " - Bruce Lee</h1></body></html>")
        :headers {"Content-Type" "text/html"}}))
 
;; Defines "/" and "/about" routes with their associated :get handlers.
;; The interceptors defined after the verb map (e.g., {:get home-page}
;; apply to / and its children (/about).
(def common-interceptors [(body-params/body-params) http/html-body])

;; Tabular routes
(def routes #{["/" :get (conj common-interceptors `quotes-bruce-page)]
              ["/about" :get (conj common-interceptors `about-page)]})

;; Map-based routes
;(def routes `{"/" {:interceptors [(body-params/body-params) http/html-body]
;                   :get home-page
;                   "/about" {:get about-page}}})

;; Terse/Vector-based routes
;(def routes
;  `[[["/" {:get home-page}
;      ^:interceptors [(body-params/body-params) http/html-body]
;      ["/about" {:get about-page}]]]])


;; Consumed by clojure-cloud-run.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ;; https://github.com/pedestal/pedestal/issues/604
              ::http/host "0.0.0.0"
              
              ::http/port (Integer. (or (env :port) 8080))
              
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})
                                        ;; Alternatively, You can specify you're own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        
