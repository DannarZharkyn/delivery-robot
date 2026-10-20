#include <SoftwareSerial.h>
#include <Wire.h>
#include <math.h>
#include <Servo.h>
#include <Keypad.h>
#include <NewPing.h>
SoftwareSerial mySerial(0, 1);
const int RELAY_PIN  = 29; // the Arduino pin, which connects to the IN pin of relay
const int ROW_NUM    = 4; // four rows
const int COLUMN_NUM = 4; // four columns
char keys[ROW_NUM][COLUMN_NUM] = {
  {'1','2','3', 'A'},
  {'4','5','6', 'B'},
  {'7','8','9', 'C'},
  {'*','0','#', 'D'}
};
сonst byte pin_rows[ROW_NUM] = {31, 32, 33,34}; //connect to the row pinouts of the keypad
const byte pin_column[COLUMN_NUM] = {35, 36, 37, 38}; //connect to the column pinouts of the keypad
Keypad keypad = Keypad( makeKeymap(keys), pin_rows, pin_column, ROW_NUM, COLUMN_NUM );
const int servoPin = 30;
Servo servo;
const String password_1 = "2341"; // change your password here
const String password_2 = "5642";  // change your password here
const String password_3 = "4545";  
String input_password;
#define MAX_DISTANCE 200 
const int trigPin = 48; // Trigger Pin of Ultrasonic Sensor
const int echoPin = 46; // Echo Pin of Ultrasonic Sensor
NewPing sonar(trigPin, echoPin, MAX_DISTANCE);
const int trigPin2 = 50; // Trigger Pin of Ultrasonic Sensor
const int echoPin2 = 52; // Echo Pin of Ultrasonic Sensor
NewPing sonaron(trigPin2, echoPin2, MAX_DISTANCE);
const int trigPin3 = 44; // Trigger Pin of Ultrasonic Sensor
const int echoPin3 = 42; // Echo Pin of Ultrasonic Sensor
NewPing sonarsol(trigPin3, echoPin3, MAX_DISTANCE);
int motor1pin1 = 2;
int motor1pin2 = 3;

int motor2pin1 = 4;
int motor2pin2 = 5;

int motor12pin1 = 8;
int motor12pin2 = 9;

int motor22pin1 = 10;
int motor22pin2 = 11;
void setup() {
  Serial.begin(9600);   
  mySerial.begin(115200); 
  servo.attach(servoPin);
  servo.write(90);
  pinMode(RELAY_PIN, OUTPUT); // initialize pin as an output.
  digitalWrite(RELAY_PIN, LOW);
  pinMode(motor1pin1, OUTPUT);
  pinMode(motor1pin2, OUTPUT);
  pinMode(motor2pin1, OUTPUT);
  pinMode(motor2pin2, OUTPUT);

  pinMode(motor12pin1, OUTPUT);
  pinMode(motor12pin2, OUTPUT);
  pinMode(motor22pin1, OUTPUT);
  pinMode(motor22pin2, OUTPUT);
  delay(500);
}

void loop() {
  int distance = sonar.ping_cm();
  int distanceR=sonarR.ping_cm();
  int distanceL=sonarL.ping_cm();
  char key = keypad.getKey();
        if (key){
    if(key == '*') {
      input_password = "";
    } else if(key == '#') {
      if(input_password == password_1 || input_password == password_2 || input_password == password_3) {
        servo.write(0);
        digitalWrite(RELAY_PIN, HIGH);  
        delay(60000);
        digitalWrite(RELAY_PIN, LOW);
        servo.write(90);
      } else { }
      input_password = ""; // reset the input password
    } else {
      input_password += key; // append new character to input password string
    }
  }
  if (Serial.available()) {
    String data = Serial.readStringUntil('\n');
    int myArr[3];
    char* ptr = strtok((char*)data.c_str(), ",");
    int i = 0;
    while (ptr != NULL) {
      myArr[i] = atoi(ptr);
      ptr = strtok(NULL, ",");
      i++;
    }
    int goal = myArr[0];
    int heading = myArr[1];
    int status = myArr[2];
    if(goal==0){
      loop();
    }
    int turn=heading-goal;
    if(turn<0){
        turn += 360;
      }
    if(heading!=goal){
        go_right();
        delay(1000);
        } else {
    go_forward();
    if(distance<15){
    go_backwards();
    delay(1000);
    if(distanceR>15){
      turn_right();
      delay(4000);
      if(distanceL>15){
        turn_left();
        delay(4000);
      }
      }
    }
   if(status==1){
        stop();
        } else{
          loop();
          }
  }
}
  void go_forward(){
  digitalWrite(motor1pin1, HIGH);
  digitalWrite(motor1pin2, LOW);
  digitalWrite(motor2pin1, HIGH);
  digitalWrite(motor2pin2, LOW);

  digitalWrite(motor12pin1, HIGH);
  digitalWrite(motor12pin2, LOW);
  digitalWrite(motor22pin1, LOW);
  digitalWrite(motor22pin2, HIGH);
};

void go_backwards(){
  digitalWrite(motor1pin1, LOW);
  digitalWrite(motor1pin2, HIGH);
  digitalWrite(motor2pin1, LOW);
  digitalWrite(motor2pin2, HIGH);
  
  digitalWrite(motor12pin1, LOW);
  digitalWrite(motor12pin2, HIGH);
  digitalWrite(motor22pin1, HIGH);
  digitalWrite(motor22pin2, LOW);
};

void go_right(){
  digitalWrite(motor1pin1, LOW);
  digitalWrite(motor1pin2, LOW);
  digitalWrite(motor2pin1, HIGH);
  digitalWrite(motor2pin2, LOW);

  digitalWrite(motor12pin1, HIGH);// FRONT LEFT
  digitalWrite(motor12pin2, LOW);
  digitalWrite(motor22pin1, LOW);
  digitalWrite(motor22pin2, LOW);
};

void go_left(){
  digitalWrite(motor1pin1, HIGH); 
  digitalWrite(motor1pin2, LOW);
  digitalWrite(motor2pin1, LOW);
  digitalWrite(motor2pin2, LOW);

  digitalWrite(motor12pin1, LOW);
  digitalWrite(motor12pin2, LOW);
  digitalWrite(motor22pin1, LOW);
  digitalWrite(motor22pin2, HIGH);
};
void stop(){
   digitalWrite(motor1pin1, LOW);
  digitalWrite(motor1pin2, LOW);
  digitalWrite(motor2pin1, LOW);
  digitalWrite(motor2pin2, LOW);

  digitalWrite(motor12pin1, LOW);
  digitalWrite(motor12pin2, LOW);
  digitalWrite(motor22pin1, LOW);
  digitalWrite(motor22pin2, LOW);
};