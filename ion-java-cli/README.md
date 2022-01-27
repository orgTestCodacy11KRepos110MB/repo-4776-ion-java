# Amazon Ion Java CLI
A Java implementation of CLI where its design document is located in [here](https://github.com/amzn/ion-test-driver#design).

The package is stored under `ion-java/ion-java-cli`.

## Setup
Build ion-java-cli. Note that using -f option for ion-java-cli's `pom.xml`.
```
$ mvn -f ion-java-cli/pom.xml install
```

## Getting Started
Invoking `ion-java-cli-x.y.jar` under `ion-java-cli/target/` directory. <br/> 

For example:
```
java -jar ion-java-cli/target/ion-java-cli-1.0.jar process test_file.ion -f pretty -o output.ion
```