package com.example.quickcompass;

import android.os.Bundle;
import android.app.Activity;
//import android.view.Menu;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color; 

public class MainActivity extends Activity implements SensorEventListener {
	
	private SensorManager mSensorManager; //Instance of device sensor manager
	private Sensor mAccelerometer; //Accelerometer
	private Sensor mMagnetometer; //Geomagnetic Field Sensor
	
	private ImageView image; //Define the compass image that is displayed and rotated
	
	private float currentAzimuth; //Keep track of the Azimuth angle, used for rotating compass
	
	//Store data values read in by sensors
	private float[] mAccelerometerValues;
	private float[] mMagnetometerValues;
	
	//Store the Azimuth degree: degree of rotation around the Z-axis, used for animating compass
	private float mAzimuth;
	private float mPitch;
	private float mRoll;
	
	private final static int size_of_matrix = 9; //Size of matrix for Rotational matrix array
	
	TextView tvAzimuth; //Text view to store Azimuth string
	
	//@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		image = (ImageView) findViewById(R.id.compassImage); //Rotatable compass image
		
		tvAzimuth = (TextView) findViewById(R.id.tvAzimuth); //Textview to display the Azimuth angle
		
		//Initialize the android device sensor capabilities
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
	}
	
	//@Override
	protected void onResume() {
		super.onResume();
		
		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
		
	}
	
	//@Override
	protected void onPause() {
		super.onPause();
		
		//Stop the sensor listener and save battery
		mSensorManager.unregisterListener(this);
	}
	
	//@Override
	public void onSensorChanged(SensorEvent event) {
		
		/**************************COLLECT SYNCED SENSOR VALUES*******************************************************/
		synchronized (MainActivity.this) { //Sync the accelerometer and Geomagnetic field sensor
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				mAccelerometerValues = event.values.clone();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				mMagnetometerValues = event.values.clone();
				break;
			}
			
			if (mAccelerometerValues != null && mMagnetometerValues != null) {
				float[] R = new float [size_of_matrix];
				SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagnetometerValues);
				float[] Orientation = new float [3]; //Stores orientation after sync
				SensorManager.getOrientation(R, Orientation);
				mAzimuth = Orientation[0]; 
		        mPitch = Orientation[1];
		        mRoll = Orientation[2];
			}
		}
		/******************** COLLECT SYNCED SENSOR VALUES************************************************************/
		
		
		/********************ROTATE ANIMATION*************************************************************************/
		
		tvAzimuth.setTextColor(Color.RED); //Set color of display text to red.

		tvAzimuth.setText("Azimuth: " + Float.toString(mAzimuth) + " degrees"); //Display Azimuth angle

		//Animate the compass image to rotate by degrees specified by mAzimuth
		RotateAnimation rotateCompass = new RotateAnimation(currentAzimuth, mAzimuth, Animation.RELATIVE_TO_SELF, 
				                                 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

		
		rotateCompass.setDuration(500); //Duration of animation

		//Reset the animation after the end of the reservation status
		rotateCompass.setFillAfter(true);

		// Start the animation
		image.startAnimation(rotateCompass);
		currentAzimuth = -mAzimuth; //Reset Azimuth angle
		
		/******************ROTATE ANIMATION**************************************************************************/
	}
	
	//@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

}