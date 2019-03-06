# OAuth Provider with Spring Security OAuth
This project provides an [OAuth 1.0a](http://tools.ietf.org/html/rfc5849) endpoint, and was developed as a study case of a single-sign-on server using the OAuth protocol. It can be used as the main user provider for applications with a common users database. The project uses a MySQL database, Spring Framework components, HTML5 and CSS3.

## Configuration
The project dependencies can be resolved through Maven, but there are some configurations regarding the database, OAuth endpoints and consumers.

### Database
To configure the database settings open the file **spring-database.xml** located in **webapp/WEB-INF**, and configure the following parameters:

	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
		<property name="user" value="root" />
		<property name="password" value="root" />
		<property name="driverClass" value="com.mysql.jdbc.Driver" />
		<property name="jdbcUrl" value="jdbc:mysql://localhost/oauth_server" />
		<property name="minPoolSize" value="1" />
		<property name="maxPoolSize" value="10" />
		<property name="acquireIncrement" value="1" />
		<property name="idleConnectionTestPeriod" value="200" />
	</bean>

Just replace the properties **user**, **password**, **driverClass** and **jdbcUrl** to match your server settings. If you change the **driverClass** property, don't forget to change the Hibernate dialect, in the same file.

### OAuth Endpoints
The next configuration steps are related to the OAuth endpoints. The configuration file is the **applicationContext.xml** file, located in the **webapp/WEB-INF** directory. The endpoints are defined as follows:

	<oauth:provider consumer-details-service-ref="consumerDetails"
					token-services-ref="tokenServices"
					request-token-url="/oauth/request_token"
					authenticate-token-url="/oauth/authorize"
					authentication-failed-url="/oauth/confirm_access"
					access-granted-url="/request_token_authorized.jsp"
					access-token-url="/oauth/access_token"
					require10a="false" />

This means that the endpoint to the request token is matched by the URL **/oauth/request_token**, the authentication token to **/oauth/authorize**, the access confirmation endpoint to **/oauth/confirm_access**, the access token to **/oauth/access_token** and when the access is granted the page showed to the user is the **/request_token_authorized.jsp**.

### Consumers
In the **applicationContext.xml** file it is possible to define which consumers can ask for the users authorization, and these consumers are defined in the following lines:

	<oauth:consumer-details-service id="consumerDetails">
		<oauth:consumer name="BillsConsumer" secret="oauth-secret-01"
			key="bills-consumer-key" resourceName="Profile"
			resourceDescription="User profile Data" />
		<oauth:consumer name="ContactsConsumer" secret="oauth-secret-02"
			key="contacts-consumer-key" resourceName="Profile"
			resourceDescription="Your profile personal data" />
	</oauth:consumer-details-service>

Two consumers are defined in this case, which are the two projects developed in the monograph study case. When defining a consumer it is required to set its **name**, **secret**, **key** and the users data and resources that the consumer will use when it's authorized.

## Architecture

The application code is separated in logical packages, using some concepts of MVC and DDD. The following structure defines the project architecture:

* **controller**: Defines all the controllers classes used by the application. A controller will use the Spring IoC container to inject services and delegate the business logic to them. When the data is ready, the controller sets them in the view files.
* **domain.entity**: The entity classes which defines the application domain, represents real world objects used in the project.
* **domain.repository**: Repository classes are responsible of accessing the database through the JPA EntityManager class, and each repository will deal with a domain entity class.
* **domain.service**: A service has one or more repositories, and takes care of the domain business logic. It will use the repositories to make database operations.

There are also the **views** files which are located in the **webapp/WEB-INF/views** folder, and are mainly coded using JSP and pure HTML, with some features of CSS3 and HTML5. These views use the Spring MVC web features, like form binding.

The CSS and Javascript resources are stored in the **webapp/resources** directory, which has just one CSS file that defines the application styles.

## Usage
The OAuth server provides simple features of user management. The main purpose of this project is to act like a single sign-on server, which will have an users database and will be used as the authentication endpoint of the OAuth consumers defined in the monograph. The first thing to do is to authenticate with an admin role, in order to build the user's database. After inserting some new users, the credentials saved can be used to login with the consumers.

When implementing a consumer, there are some endpoints that this server provides, listed below:

* **/oauth/request_token** - Endpoint where the consumer can get a request token
* **/oauth/authorize** - This endpoint will allow a consumer to get the user's data, and obtaing an access token
* **/oauth/confirm_access** - This is the endpoint where the consumer can ask for authorization, usually through a redirect containing the request token, after authorizing the access, the user will be redirected again, to the callback URL
* **/oauth/access_token** - The access token, which will allow the consumer to obtain user data from the server

The server will provide a **/user/info** REST service, which will provide the user profile information, that can be retrieved after getting the access token. This logic is implemented in the **UserController** class, in the **doGetUserInfo()** method.

If the user chooses to not authorize the application, nothing will happen, and the page **/oauth/revoke** will be called, notifying the user that the access was revoked. Also the created tokens will be expired.

The last thing that the consumers should do is to logout the user from the OAuth server, through the page **/logout.do**. This happens because as a SSO server, the user should be logged out of the consumer and also the server, so no sessions will be left through the applications.

## Improvements
As the provider is very simple, there are lots of improvementes that should be made in order to use it as a real SSO server. The following list could be some of them:

* Save the tokens generated by the Spring Security OAuth in a database, this can be made through an implementation of the OAuthProviderTokenServices;
* After having the tokens stored in the database, implement a consumers service, allowing the administrator to have more control on which applications can access and ask for authorization in the OAuth provider. To make this, a service that implements the ConsumerDetailsService should be created;
* When the provider has stored in a database the consumers and the tokens, then the application can list to the users which applications they authorized, allowing him to also revoke the applications access;
* One major improvement is to reimplement the provider using the OAuth 2.0 specification, as soon as it becomes stable, by the date this provider was developed the specs were only in draft.

## Monograph
The full monograph document can be found in my [blog](http://fernandomantoan.com/monografia-2/estudo-de-caso-de-uma-estrutura-de-autenticacao-unica-utilizando-o-protocolo-oauth/) and it's written in Brazilian Portuguese. The result of the monograph are three main applications, stored in github:

* An [OAuth server](https://github.com/fernandomantoan/oauth-provider-sample), which uses [Spring Framework](http://www.springsource.org) with many other libraries;
* A [bills management system](https://github.com/fernandomantoan/oauth-consumer-sample-zf), which uses [Zend Framework](http://framework.zend.com) and Zend_Oauth_Consumer component;
* A [contacts management system](https://github.com/fernandomantoan/oauth-consumer-sample-play), which uses [Play! Framework](http://www.playframework.org) and the OAuth module.