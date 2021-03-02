FROM clojure:openjdk-17-lein-alpine
WORKDIR /usr/src/app
CMD ["lein", "run"]
