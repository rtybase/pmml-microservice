JAR_NAME=`find lib -name ml-phishing-classification-service*.jar`

java -jar $JAR_NAME server etc/ml-phishing-classification-service.yaml