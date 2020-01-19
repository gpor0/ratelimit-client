## Quick readme

### How to build

Required tools
- Openjdk 11 Amazon Corretto (works also on different flavours)
- Maven 3.6.1 (probably works also on different version)

Build command
```bash
mvn -version
mvn clean package
```
### How to run
```bash
#arg0 is number of threads to be used
#arg1 is server address (defaults to http://localhost:8080)
java -jar target/client.jar arg0 arg1 
```
