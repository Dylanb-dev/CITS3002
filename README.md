# CITS3002

MADE IN ECLIPSE IDE

EACH PROGRAM RUNS IN ITS OWN PACKAGE

KEYS/CERTS IN /BIN



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







