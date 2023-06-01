Project setup instructions

This project uses docker containers for test (test-containers), as well as kubernetes for execution so that we do not have to
install anything other than docker for mac or docker for windows. It also requires you to compile with java 17 and maven.
Once you have kubernetes installed, you will need to install helm. If you are running on a mac, "brew install helm" will do the
trick.

This project utilizes the approach of merging changes into existing domain objects pulled from database from dtos, 
versus creating new domain object graph to be persisted directly into the database, for example if you were using ModelMapper.
The thing I don't like about the direct persistence approach is that the client side has the ability to modify anything. If
we use what I am calling the "merge approach", the domain objects can control what aspects of changes that are possible.
The downside is that merging can be somewhat complex. Take a look at the interface, 
com.mjdsoftware.logbook.domain.entities.DomainObjectCollectionEntry and how it's used in code to merge collection changes.

This project is deployed as an Oauth2 "Resource Server". It utilizes keycloak as an Authentication/Authorization Server, and the two
servers interact at runtime.


After getting that prerequisite installed, do the following:

1. Build the project:

```
mvn clean install -P build-docker-image
```

2. Install postgres via a helm chart:
```
cd k8sconfig-local
helm repo add bitnami https://charts.bitnami.com/bitnami  
helm repo update
helm install postgresql  bitnami/postgresql --version 12.1.9 --values=logbook-postgresql-values.yaml
helm install postgresql-keycloak  bitnami/postgresql --version 12.1.9 --values=keycloak-postgresql-values.yaml
```

3. Install kubernetes artifacts: In the k8sconfig-local directory
```
./constructCluster.sh
```

4. In /etc/hosts, add the following entries:
```
127.0.0.1       logbook
127.0.0.1       keycloak-host
```

5. For keycloak, go to http://keycloak-host. Login is logbook-admin/pass. You will need to login and
create a realm called logbook. When creating the realm, import the src/set/resources/test-realm.json. This will create a set of
keycloak users for you.

   ![](docs/keycloakLogin.png)

   ![](docs/masterRealm.png)

   ![](docs/addLogbookRealm.png)


6. Once all pods are running, go to url http://logbook/swagger-ui/index.html to get to swagger. To login you first need to get a
JWT. To do that, you can go to the OauthController (as a convenience) with user athelete/athelete to get a token.
Copy the token, then click the Authenticate button at the top of the Swagger page and enter the copied
token. Then you should be able to interact with the Logbook Controller via swagger.

   ![](docs/getClientToken.png)

   ![](docs/getClientTokenResponse.png)

   ![](docs/authorizeButton.png)


7. Something to note. I have put the actuator on port 8081 in this project. Its not exposed through the ingress, so the
only way you can get to it is by doing a port-forward. For example on logbook, do 
```

   kubectl port-forward logbook-pod-goes-here 8081:8081
   Then go to a browser and enter http://localhost:8081/actuator
   and you should see thew basic actuator response.
   Entering http://localhost:8081/actuator/health in a browser should also work.
   The actuator/health uri is used in the logbook deployment yaml for liveness and readiness probes.
   
   
```

8. Added role based authentication to the OauthAccessController. Now you need to be in a realm role (from keycloak) of ROLE_app_admin to access keycloak users, as well as 
create and delete them. This will be used again later when I add multi-tenancy.

9. To connect to the logbook database:
```
kubectl port-forward postgresql-0 5432:5432
Create a connection to localhost:5432 using pgadmin or your favorite db tool. 
You should now be able to connect to the database running inside of Kubernetes.
```

10. I've added multi-tenancy to the app. Therefore, you now need to create users. User creation can be done
via the UserController REST service. 

    
    a. You need to create one administrative user in keycloak before doing anything else. This user needs to have the role of app_admin.

![](docs/RoleMapping-app_admin.png)

    b. Once this user has been created, you can user this user to log into the app, via the OauthAccessController with this new admin level user.
       See the Swagger interface above.

    c. Once connected as the admin user, you can now create users via the new implementation of UserController. From here its possible to
       create a new user. A representation of this user is created in Keycloak and in the application's database as well. At this point,
       you have the ability to create logbooks for the specific user, as well as other resources. Note that the admin user can do any of this
       activity on behalf of another user. This has been implemented as part of the MethodSecurityService currently.

11. Project next steps:

    -Experiment with MapStruct

    -Import facility (Look at imports from Concept2, etc) -> csv file formats, etc

    -Statistics

    -Helm chart(s) for everything


