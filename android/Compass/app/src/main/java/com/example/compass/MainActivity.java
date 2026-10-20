package com.example.compass;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class MainActivity extends Activity implements SensorEventListener, LocationListener {
    private ImageView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private LocationManager mLocationManager;
    private TextView tvHeading, tvLatitude, tvLongitude;
    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;
    private float number;
    private float number2;
    private int code;
    private int status;
    private int count=0;
    private String email;
    Map<String, Object> data = new HashMap<>();
    // List<Object> dataList = new ArrayList<>();
    float initialLatFloat = 0.0f;
    float initialLongFloat = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.imageViewCompass);
        tvHeading = (TextView) findViewById(R.id.tvHeading);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        databaseRef.child("flat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    number = Float.parseFloat(dataSnapshot.getValue().toString());
                    // Display the number on the screen
                    TextView tvNumber = (TextView) findViewById(R.id.tvNumber);
                    tvNumber.setText("flat: " + Float.toString(number));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        databaseRef.child("flong").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    number2 = Float.parseFloat(dataSnapshot.getValue().toString());
                    // Display the number on the screen
                    TextView tvNumber = (TextView) findViewById(R.id.tvNumber2);
                    tvNumber.setText("flong: " + Float.toString(number2));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        databaseRef.child("code").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    code = Integer.parseInt(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        databaseRef.child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    email = (String) dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int degree = Math.round(event.values[0]);
        tvHeading.setText("Heading: " + degree+ " degrees");
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);
        currentDegree = -degree;
        data.put("heading", degree);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onLocationChanged(Location location) {
        float latitude = (float) location.getLatitude();
        float longitude = (float) location.getLongitude();
        String latString = String.format("%.4f", latitude);
        String longString = String.format("%.4f", longitude);
        float latFloat = Float.parseFloat(latString.replace(",", "."));
        float longFloat = Float.parseFloat(longString.replace(",", "."));
        if (initialLatFloat == 0.0f && initialLongFloat == 0.0f) {
            if (latFloat != 0.0f && longFloat != 0.0f) {
                initialLatFloat = latFloat;
                initialLongFloat = longFloat;
            }
        }
        tvLatitude.setText("Latitude: " + latFloat);
        tvLongitude.setText("Longitude: " + longFloat);
        float distLat = number - latFloat;
        float distLon = number2 -longFloat;
        double heading1 = Math.atan2(distLon, distLat);
        TextView tvdirection = (TextView) findViewById(R.id.tvdirection);
        int direction = (int) (heading1 * (180 / (float) Math.PI));
        if (direction < 0) {
            direction += 360;
        }
        tvdirection.setText("direction: " + (direction));
        data.put("goal", direction);
        // String dir=Float.toString(direction);
        if (number == latFloat && number2 == longFloat) {
            status=1;
            data.put("status", status);

            if(count==0) {
                new SendEmailTask().execute();
                count=count+1;
            }
            float temp = number;
            number = initialLatFloat;
            initialLatFloat = temp;
            temp = number2;
            number2 = initialLongFloat;
            initialLongFloat = temp;
            databaseRef = FirebaseDatabase.getInstance().getReference();
            databaseRef.child("flat:").setValue(number);
            databaseRef.child("flong:").setValue(number2);
        } else {
            status=0;
            data.put("status", status);
        }
        databaseRef.child("direction").setValue(data);
    }

    private class SendEmailTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            final String username = "YOUR_SMTP_EMAIL";
            final String password = "YOUR_VALUE";
            final String recipient = email;

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.mail.ru");
            props.put("mail.smtp.port", "587");

            javax.mail.Session session = javax.mail.Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Тапсырыс келді");
                message.setText("Сіздің тапсырысыңыз келді." +
                        " Сіздің кодты енгізгеннен " +
                        "соң тапсырысты алуға 1 минут уақытыңыз " +
                        "бар. Құпия код:"+code);
                Transport.send(message);

                Log.i(TAG, "Email sent successfully.");

            } catch (MessagingException e) {
                Log.e(TAG, "Failed to send email.", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // You can perform any post-execution actions here, such as displaying a toast message.
        }
    }



    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}}


