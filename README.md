# Service how-to guide

This is a demo project showcasing how to build Machine Learning (ML) based microservices with [Dropwizard](https://www.dropwizard.io/1.3.8/docs/) and [PMML](http://dmg.org/pmml/v4-1/GeneralStructure.html). PMML file encapsulates the predictive ML function, thus there is no need to write any code for it. This creates a clear separation of concerns, where 

- Data Scientists train a predictive function and export it to a PMML file 
- Engineers load the PMML file into an evaluation function (thanks to PMML API) and feed it with inputs from the clients, which is easy to automate.

This code also introduces `PmmlProperty` annotation, which helps to glue service inputs with the PMML function inputs. For example, PMML input defined as 

```xml
<DataField name="x1" optype="continuous" dataType="float"/>
```

is mapped to the relevant DTO (service input) as

```java
public class UrlResource {
	@PmmlProperty(name = "x1")
	@JsonProperty
	@Max(1)
	@Min(-1)
	private int urlLength;
```

and the service's resource will simply

```java
	...
	private final ScoreCalculator<UrlResource> phishingClassifier;
	...
	@POST
	@Timed
	public Map<String, ?> classifyResource(@Valid UrlResource urlResource) {
		return phishingClassifier.calculate(urlResource);
	}
```

This particular demo pretends to perform phishing classification from the length of URLs, which is not a real scenario of course, but works for the demo purposes. Special thanks to [Dr. David Pryce](https://github.com/dtpryce) for building the PMML file for this demo using [sklearn2pmml](https://github.com/jpmml/sklearn2pmml).

Make sure you have Java 8 and Maven 3.2.x installed in order to build the project.

## 1. Build the service

From the command line
```
mvn clean verify
```
The build will produce the `ml-phishing-classification-service-1.0.0-SNAPSHOT-dist.tar.gz` file in the `target/` folder.

## 2. Run the service

Unpack the tar.gz from the `target/` folder.
```
tar -xvzf ml-phishing-classification-service-1.0.0-SNAPSHOT-dist.tar.gz
```

This will produce a local folder `ml-phishing-classification-service/`. Open it
```
cd ml-phishing-classification-service/
```

Start the service
```
./bin/run.sh
```

If you need to configure the service differently, check `etc/` folder for more details.

## 3. Check the service is running

The following folder `src/test/resources` contains a test script. Open the folder and execute
```
./test-endpoints.sh
```

There will be 3 calls to the service

- one to healthcheck endpoint
- one call to the evaluation endpoint with HTTP status 200
- one call to the evaluation endpoint which should trigger validation failure with HTTP status 422

## 4. Testing the service

The following folder `src/test/resources` contains a Python script. Open the folder and execute while the service is running
```
python2 data-validator.py testdata-2.json
```
The Python code as designed to run under Python 2.7
