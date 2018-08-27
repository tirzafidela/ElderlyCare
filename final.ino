#include <Average.h>
#include <I2Cdev.h>
#include <MPU6050.h>
#include <MAX30100_PulseOximeter.h>
#include <Timer.h>
#include <Wire.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#define HIST_SIZE 200
#define OUTPUT_READABLE_ACCELGYRO

//WiFi Settings
#define FIREBASE_HOST "elderlycare-19d19.firebaseio.com"
#define FIREBASE_AUTH "Ur6BTP7CrmQyNcljzWEkppnDXjyrhkE4RN9BZp2g"
//#define WIFI_SSID "fullhouse"
//#define WIFI_PASSWORD "langsungenteraja"
#define WIFI_SSID "tirzafidela"
#define WIFI_PASSWORD "mynameistirza"
#define DEBUG_ON
#define REPORTING_PERIOD_MS 1000

MPU6050 accelgyro;
int16_t ax, ay, az;
int16_t gx, gy, gz;
uint32_t period = 5 * 60000L;

//MAX30100
const int MAX_addr=0x57;
int countbeat;
uint32_t bpm_period = 60000L;
float maxbpm,minbpm, fcb;

Timer t;
PulseOximeter pox;
uint32_t tsLastReport = 0;
bool status, connect, fallstatus;
float totalbpm=0, hr;

void printBPM() {
  pox.shutdown();
  Serial.print("BPM = ");
  Serial.println(countbeat);
  fcb = (float) countbeat;
  if (countbeat > maxbpm) {
    Firebase.setFloat("maxbpm",fcb);
  } else if (countbeat < minbpm) {
    Firebase.setFloat("minbpm",fcb);
  }
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  JsonObject& bpmTime = root.createNestedObject("timestamp");
  root["val"] = countbeat;
  bpmTime[".sv"] = "timestamp";
  Firebase.push("bpm",root);
  //pox.update();
  if (countbeat < 40) {
    Firebase.push("HRDisorder/Low/",root);
    status = true;
    Firebase.setBool("danger",true);
  } else if (countbeat > 170) {
    Firebase.push("HRDisorder/High/",root);
    status = true;
    Firebase.setBool("danger",true);
  } else {
    status = false;
    Firebase.setBool("danger",false);
  }
  countbeat = 0;
  pox.resume();
}

void onBeatDetected()
{
    Serial.println("Beat!");
    countbeat++;
}

void readBPM() {
  for( uint32_t tStart = millis();  (millis()-tStart) < 300;  ){
    pox.update();
  }
  pox.shutdown();
  pox.resume();
}

void checkConnect(){
  int ldr_val = analogRead(A0);
  Serial.println(ldr_val);
  if(ldr_val < 20) {
    connect = true;
    Firebase.setBool("connect",true);
  } else {
    connect = false;
    Firebase.setBool("connect",false);
  }
}

void checkFall() {
  pox.shutdown();
    if(fallstatus == true) {
      Serial.println("Fall detected");
      StaticJsonBuffer<50> jsonBuffer;
      JsonObject& timeStampObject = jsonBuffer.createObject();
      timeStampObject[".sv"] = "timestamp";
      Firebase.push("fall", timeStampObject);
      Firebase.setInt("fallsum", (Firebase.getInt("fallsum")+1));
      Firebase.setBool("danger",true);      
    } else {
      Firebase.setBool("danger",false);
    }
    fallstatus = false;
   pox.resume();
}

void readMPU() {
    pox.shutdown();
    accelgyro.setDMPEnabled(true);
    for( uint32_t tStart = millis();  (millis()-tStart) < 300;  ){
    //accelgyro.setSleepEnabled(false);
    accelgyro.getAcceleration(&ax, &ay, &az);
    accelgyro.setDMPEnabled(false);
    pox.resume();
    //pox.update();
    Serial.print("a/g:\t");
    Serial.print(ax); Serial.print("\t");
    Serial.print(ay); Serial.print("\t");
    Serial.print(az); Serial.print("\t");
    Serial.println("");
    if(sqrt(pow(ax,2) + pow(ay,2) + pow(az,2)) >= 35000){
      fallstatus = true;
//    } else {
//      fallstatus = false;  
//      //checkFall();
//      //pox.update();
    }
  }
}

void setup() {
    accelgyro.setSleepEnabled(false);
    #if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
        Wire.begin(4,5);
    #elif I2CDEV_IMPLEMENTATION == I2CDEV_BUILTIN_FASTWIRE
        Fastwire::setup(400, true);
    #endif

    Serial.begin(115200);
    delay(2000);

    //Connect to WiFi & Firebase
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting");
    while (WiFi.status() != WL_CONNECTED) {
      Serial.print(".");
      delay(500);
    }
    Serial.println();
    Serial.print("Connected: ");
    Serial.println(WiFi.localIP());
    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    Firebase.setBool("connect",true);
    delay(2000);
    
    //Initialize MPU6050
    Serial.println("Initializing MPU6050...");
    accelgyro.initialize();
    accelgyro.setFullScaleGyroRange(MPU6050_GYRO_FS_2000);
    accelgyro.setFullScaleAccelRange(MPU6050_ACCEL_FS_2);
    Serial.println("Testing device connections...");
    Serial.println(accelgyro.testConnection() ? "MPU6050 connection successful" : "MPU6050 connection failed");
    accelgyro.setDMPEnabled(false);
    
    //Initialize MAX30100
    Serial.print("Initializing pulse oximeter..");
    if (!pox.begin()) {
        Serial.println("FAILED");
        for(;;);
    } else {
        Serial.println("SUCCESS");
    }
    pox.setOnBeatDetectedCallback(onBeatDetected);
    t.every(60000,printBPM);
    t.every(1000,checkFall);
    fallstatus = false;    
}

void loop() {
    readBPM();
    readMPU();
    t.update();
}
