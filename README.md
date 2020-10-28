### Springmongo practice project

### Skillset: web, rest, mongodb, gridfs, lombok, sonarqube, test, swagger.

### Endpoint:

* [http://localhost:8080/](http://localhost:8080/)<br>
 -- File upload/delete web page.
 
 * [http://localhost:8080/person/*](http://localhost:8080/person)<br>
 -- Person Crud url
 
  * [http://localhost:8080/personapi/*](http://localhost:8080/personapi)<br>
 -- Person data rest url
 
 
 ### Test procedure:
 
 1. Start sonarqube server.
 
 2. Run gradle clean.
 
 3. Run gradle test jacocotestreport.
 
 4. Run gradle sonarqube.
 
 5. Check [http://localhost:9000](http://localhost:9000)<br>
 
 
 ### Swagger homepage
 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)<br>
 
 
