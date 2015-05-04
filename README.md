# CITS3002

run bank with 

"java -cp ../ Bank.Bank"

from bin/Bank

run collectors in a seperate cmd with 

"java -cp ../ -Djavax.net.ssl.trustStore=publicBank.jks Collectors.Collectors"

from bin/Collectors

Source taken from http://www.herongyang.com/JDK/


# SSL (aka TLS) Overview

SSL/TLS is a server client connection with public key encryption

The server needs a certificate and key for use

The client needs a copy of the certificate to generate its own key

During the handshake the client and server automatically decide on an encryption method and all communication is secure.




# SSL KEY CREATION

Create a Key for the Server with:

keytool -genkeypair -alias NAME -keystore NAME.jks

(follow prompts)

Create a Server Cert with:

keytool -exportcert -alias NAME -file NAME.crt 
   -keystore name.jks -storepass NameJKS
   
Move Server Cert to client folder and generate a key with:

keytool -importcert -alias NAME -file Name.crt 
   -keystore NAMEpublic.jks -storepass NamePublicJKS







