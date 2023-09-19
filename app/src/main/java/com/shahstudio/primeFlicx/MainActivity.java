package com.shahstudio.primeFlicx;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.File;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

	public final int REQ_CD_PHOTOCLICKER = 101;

	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

	private boolean onLoginPage = false;
	private boolean isPolicyAccepted = false;
	private HashMap<String, Object> map = new HashMap<>();
	private LinearLayout linear2 , linear3 , linear8 , Create_account_button , Login_button;
	private TextView textview1 , textview5 , privacy_policy_button;
	private EditText user_name , phone_number;
	private CheckBox privacy_policy_check;
	private ProgressBar progressbar1;

	private Intent intent = new Intent();
	private Intent photoClicker = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	private File _file_photoClicker;
	private AlertDialog.Builder dialog;
	private DatabaseReference user_database = _firebase.getReference("users");
	private FirebaseAuth auth;

	private SharedPreferences ds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initialize(savedInstanceState);
		FirebaseApp.initializeApp(this);

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
				|| ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
				|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}

	private void initialize(Bundle savedInstanceState) {
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		linear8 = findViewById(R.id.linear8);
		Create_account_button = findViewById(R.id.Create_account_button);
		Login_button = findViewById(R.id.Login_button);
		textview1 = findViewById(R.id.textview1);
		textview5 = findViewById(R.id.textview5);
		user_name = findViewById(R.id.user_name);
		phone_number = findViewById(R.id.phone_number);
		privacy_policy_check = findViewById(R.id.privacy_policy_check);
		privacy_policy_button = findViewById(R.id.privacy_policy_button);
		progressbar1 = findViewById(R.id.progressbar1);

		_file_photoClicker = FileUtil.createNewPictureFile(getApplicationContext());
		Uri _uri_photoClicker;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			_uri_photoClicker = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", _file_photoClicker);
		} else {
			_uri_photoClicker = Uri.fromFile(_file_photoClicker);
		}
		photoClicker.putExtra(MediaStore.EXTRA_OUTPUT, _uri_photoClicker);
		photoClicker.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		dialog = new AlertDialog.Builder(this);
		auth = FirebaseAuth.getInstance();
		ds = getSharedPreferences("ds", Activity.MODE_PRIVATE);

		textview5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				onLoginPage = !onLoginPage;
				if (onLoginPage) {
					linear3.setVisibility(View.VISIBLE);
					linear8.setVisibility(View.GONE);
					Login_button.setVisibility(View.VISIBLE);
					Create_account_button.setVisibility(View.GONE);
					textview5.setText("Don't have any Account?");
					textview1.setText("Login");
				} else {
					linear3.setVisibility(View.VISIBLE);
					linear8.setVisibility(View.VISIBLE);
					Login_button.setVisibility(View.GONE);
					Create_account_button.setVisibility(View.VISIBLE);
					textview5.setText("Already have an account?");
					textview1.setText("Create Account");
				}
			}
		});

		privacy_policy_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				isPolicyAccepted = _param2;
			}
		});

		privacy_policy_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				dialog.setTitle("Privacy Policy");
				dialog.setMessage("Privacy Policy for PrimeFlicx\n\nLast updated: [01/12/2023]\n\n1. INTRODUCTION\nWelcome to PrimeFlicx! This Privacy Policy is designed to help you understand how we collect, use, disclose, and safeguard your personal information while using our app.\n\n2. Information We Collect\n2.1 Personal Information: We may collect certain personal information, including but not limited to:\n- User registration details (e.g., name, email address).\n- Device information (e.g., device type, operating system).\n- Log data (e.g., IP address, app usage statistics).\n\n2.2 Content Usage: We may collect data related to your usage of the app, including the content you access and watch.\n\n3. How We Use Your Information\n3.1 Service Improvement: We use your information to improve and personalize your experience with PrimeFlicx, including content recommendations and app optimization.\n\n4. Data Security\nWe take reasonable measures to protect your data, including encryption and access controls. However, no method of data transmission is entirely secure, and we cannot guarantee absolute security.\n\n5. Third-Party Services\nPrimeFlicx may integrate with third-party services. Please review their privacy policies as we are not responsible for their practices.\n\n6. Data Retention\nWe retain your data as long as necessary for the purposes outlined in this policy or as required by law.\n\n7. User Rights\nYou have the right to access, rectify, or delete your personal information. Contact us at [Insert Contact Information] to exercise these rights.\n\n8. Children's Privacy\nPrimeFlicx is not intended for children under the age of 13. We do not knowingly collect personal information from children.\n\n9. Changes to this Privacy Policy\nWe may update this Privacy Policy from time to time. We will notify you of significant changes through the app or other means.\n\n10. Contact Us\nIf you have any questions or concerns regarding this Privacy Policy, please contact us at [Insert Contact Information].");
				dialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
					}
				});
				dialog.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
					}
				});
				dialog.create().show();
			}
		});
	}

	private void initializeLogic() {
		progressbar1.setVisibility(View.GONE);
		linear2.setBackground(new GradientDrawable() {
			public GradientDrawable getIns(int a, int b) {
				this.setCornerRadius(a);
				this.setColor(b);
				return this;
			}
		}.getIns(10, 0xFF263238));

		linear3.setBackground(new GradientDrawable() {
			public GradientDrawable getIns(int a, int b) {
				this.setCornerRadius(a);
				this.setColor(b);
				return this;
			}
		}.getIns(20, 0xFFFFFFFF));

		linear8.setBackground(new GradientDrawable() {
			public GradientDrawable getIns(int a, int b) {
				this.setCornerRadius(a);
				this.setColor(b);
				return this;
			}
		}.getIns(20, 0xFFFFFFFF));

		Create_account_button.setBackground(new GradientDrawable() {
			public GradientDrawable getIns(int a, int b) {
				this.setCornerRadius(a);
				this.setColor(b);
				return this;
			}
		}.getIns(10, 0xFFE57373));

		Login_button.setBackground(new GradientDrawable() {
			public GradientDrawable getIns(int a, int b) {
				this.setCornerRadius(a);
				this.setColor(b);
				return this;
			}
		}.getIns(10, 0xFFE57373));

		onLoginPage = false;
		isPolicyAccepted = false;
	}

	@Override
	public void onBackPressed() {
		dialog.setTitle("Exit App?");
		dialog.setMessage("Do you want to exit this app without login/signup?");
		dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface _dialog, int _which) {
				finish();
			}
		});
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface _dialog, int _which) {
			}
		});
		dialog.create().show();
	}

	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
}
