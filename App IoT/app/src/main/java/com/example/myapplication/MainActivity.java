package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemperature, txtHumidity;

    LabeledSwitch buttonLed, buttonMachine;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemperature = findViewById(R.id.txtTemperature);
        txtHumidity = findViewById(R.id.txtHumidity);
        buttonLed = findViewById(R.id.buttonLed);
        buttonMachine = findViewById(R.id.buttonMachine);

        buttonLed.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true) {
                    sendDataMQTT("Vy2908/feeds/led", "1");
                }
                else {
                    sendDataMQTT("Vy2908/feeds/led", "0");
                }
            }
        });

        buttonMachine.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true) {
                    sendDataMQTT("Vy2908/feeds/machine", "1");
                }
                else {
                    sendDataMQTT("Vy2908/feeds/machine", "0");
                }
            }
        });
        startMQTT();
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }
        catch (MqttException e){
        }
    }
    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "!!" + message.toString());
                if (topic.contains("temperature")){
                    txtTemperature.setText(message.toString() + "°C");
                }
                else if (topic.contains("humidity")){
                    txtHumidity.setText(message.toString() + "%");
                }
                else if (topic.contains("led")){
                    if (message.toString().equals("1")){
                        buttonLed.setOn(true);
                    }
                    else {
                        buttonLed.setOn(false);
                    }
                }
                else if (topic.contains("machine")){
                    if (message.toString().equals("1")){
                        buttonMachine.setOn(true);
                    }
                    else {
                        buttonMachine.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}