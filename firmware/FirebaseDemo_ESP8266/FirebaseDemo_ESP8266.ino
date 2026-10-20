#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <SoftwareSerial.h>

SoftwareSerial SUART(4, 5);

// Replace with your Firebase project credentials
#define FIREBASE_HOST "YOUR_VALUE"
#define FIREBASE_AUTH "YOUR_VALUE"
#define WIFI_SSID "YOUR_VALUE"                                               //WiFi SSID to which you want NodeMCU to connect
#define WIFI_PASSWORD "YOUR_VALUE" 
void setup() {
  Serial.begin(115200);
  SUART.begin(9600);
  WiFi.begin(WIFI_SSID,WIFI_PASSWORD);  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void loop() {
  FirebaseObject direction = Firebase.get("/direction");
  int goal = direction.getInt("goal");
  int heading= direction.getFloat("heading");
  int status = direction.getInt("status");
  int myArr[3];
  myArr[0]=goal;
  myArr[1]=heading;
  myArr[2]=status;
    for(int i=0; i<3; i++){
    SUART.print(myArr[i]);
    SUART.print(",");
  }
  }