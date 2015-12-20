#include <ESP8266WiFi.h>

#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>  

char path[] = "/api/powerHistorys";
char host[] = "10.0.204.107";
String content = "";
char character;

String postStr = "";
String postPath = "";
String postHost = "";

// Use WiFiClient class to create TCP connections
WiFiClient client;
WiFiManager wifiManager;

void configModeCallback () {
  Serial.println("Entered config mode");
  Serial.println(WiFi.softAPIP());
}

void setup() {
  Serial.begin(115200);
  delay(10);

  wifiManager.setAPCallback(configModeCallback);
  wifiManager.setAPConfig(IPAddress(192,168,1,1), IPAddress(192,168,1,1), IPAddress(255,255,255,0));

  if(!wifiManager.autoConnect("PowerMeter")) {
    Serial.println("failed to connect and hit timeout");
    ESP.reset();
    delay(1000);
  } 
  
//  Serial.println("connected... :)");
//
//  
//  Serial.println();
//  Serial.print("Connecting to ");
//  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  delay(5000);
 
  postPath = "POST ";
  postPath += path;
  postPath += " HTTP/1.1\n";
         
  postHost = "Host: ";
  postHost += host;
  postHost += "\n";
  
  Serial.println(postPath);
  Serial.println(postHost);
}

void loop() {
  String data;

  while(!Serial.available()){;}
    
  if (client.connect(host, 8080)) {
     while(Serial.available()) {
      character = Serial.read();
      content.concat(character);
     }     
     Serial.println(content);  
  
     postStr = "{\"power\":\"";
     postStr += content;
     postStr += "\"}";
     postStr += "\r\n";
     Serial.println(postStr); 
     
     client.print(postPath);
     client.print(postHost);
     client.print("Connection: close\n");
     client.print("Content-Type: application/json\n");
     client.print("Content-Length: ");
     client.print(postStr.length());
     client.print("\n\n");
     client.print(postStr);   
     content=""; 
  } else {
    Serial.println("Client disconnected.");
    delay(1000);
    return;
  }  
}
