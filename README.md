# Mini Payment Processing App


##  Installation & Setup

###  1. Clone the Repository
```bash
git clone https://github.com/Jonahcb/miniPaymentProcessingApp.git
cd miniPaymentProcessingApp
```

###  2. Set Up Oracle Database
- Follow this video for Mac silicon: https://www.youtube.com/watch?v=uxvoMhkKUPE

###  3. SSL Certificate Configuration (Visa API)
- If using the **real VisaClient**, provide:
  - A Visa-issued **TLS client certificate** (PEM format)
  - A matching **private key**
- Update the file paths in `VisaSSLContextLoader.java`
- For local testing, you can use the **VisaClientSimulator**, which doesn’t require TLS

###  4. Build and Deploy to Apache Tomcat
```bash
mvn clean package
cp target/miniPaymentProcessingApp.war $TOMCAT_HOME/webapps/
```
Start Tomcat and access the app at:  
`http://localhost:8080/miniPaymentProcessingApp`

- Follow this video for Apache Tomcat help: https://www.youtube.com/watch?v=rElJIPRw5iM&t=7046s






