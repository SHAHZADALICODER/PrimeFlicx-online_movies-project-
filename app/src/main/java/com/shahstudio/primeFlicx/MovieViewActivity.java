package com.shahstudio.primeFlicx;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class MovieViewActivity extends AppCompatActivity {
	
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private DrawerLayout _drawer;
	private HashMap<String, Object> map = new HashMap<>();
	private boolean isLatestShowing = false;
	private String share = "";
	
	private ArrayList<HashMap<String, Object>> map_list = new ArrayList<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private LinearLayout linear3;
	private ImageView latestMovie;
	private TextView textview2;
	private ListView listview1;
	private BottomNavigationView bottomnavigation1;
	private EditText searchBar;
	private ImageView imageview1;
	private TextView textview1;
	private ImageView latest_visible_button;
	private LinearLayout _drawer_linear1;
	private LinearLayout _drawer_linear3;
	private LinearLayout _drawer_linear2;
	private LinearLayout _drawer_linear4;
	private LinearLayout _drawer_linear5;
	private ImageView _drawer_imageview1;
	private TextView _drawer_textview1;
	
	private DatabaseReference db = _firebase.getReference("shows");
	private ChildEventListener _db_child_listener;
	private FirebaseAuth auth;
	private OnCompleteListener<AuthResult> _auth_create_user_listener;
	private OnCompleteListener<AuthResult> _auth_sign_in_listener;
	private OnCompleteListener<Void> _auth_reset_password_listener;
	private OnCompleteListener<Void> auth_updateEmailListener;
	private OnCompleteListener<Void> auth_updatePasswordListener;
	private OnCompleteListener<Void> auth_emailVerificationSentListener;
	private OnCompleteListener<Void> auth_deleteUserListener;
	private OnCompleteListener<Void> auth_updateProfileListener;
	private OnCompleteListener<AuthResult> auth_phoneAuthListener;
	private OnCompleteListener<AuthResult> auth_googleSignInListener;
	
	private RequestNetwork internet;
	private RequestNetwork.RequestListener _internet_request_listener;
	private Intent intent = new Intent();
	private AlertDialog.Builder dialog;
	private SharedPreferences ds;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.movie_view);
		initialize(_savedInstanceState);
		FirebaseApp.initializeApp(this);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		_app_bar = findViewById(R.id._app_bar);
		_coordinator = findViewById(R.id._coordinator);
		_toolbar = findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		_drawer = findViewById(R.id._drawer);
		ActionBarDrawerToggle _toggle = new ActionBarDrawerToggle(MovieViewActivity.this, _drawer, _toolbar, R.string.app_name, R.string.app_name);
		_drawer.addDrawerListener(_toggle);
		_toggle.syncState();
		
		LinearLayout _nav_view = findViewById(R.id._nav_view);
		
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		latestMovie = findViewById(R.id.latestMovie);
		textview2 = findViewById(R.id.textview2);
		listview1 = findViewById(R.id.listview1);
		bottomnavigation1 = findViewById(R.id.bottomnavigation1);
		searchBar = findViewById(R.id.searchBar);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		latest_visible_button = findViewById(R.id.latest_visible_button);
		_drawer_linear1 = _nav_view.findViewById(R.id.linear1);
		_drawer_linear3 = _nav_view.findViewById(R.id.linear3);
		_drawer_linear2 = _nav_view.findViewById(R.id.linear2);
		_drawer_linear4 = _nav_view.findViewById(R.id.linear4);
		_drawer_linear5 = _nav_view.findViewById(R.id.linear5);
		_drawer_imageview1 = _nav_view.findViewById(R.id.imageview1);
		_drawer_textview1 = _nav_view.findViewById(R.id.textview1);
		auth = FirebaseAuth.getInstance();
		internet = new RequestNetwork(this);
		dialog = new AlertDialog.Builder(this);
		ds = getSharedPreferences("ds", Activity.MODE_PRIVATE);
		
		linear3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (isLatestShowing) {
						isLatestShowing = false;
				}
				else {
						if (!isLatestShowing) {
								isLatestShowing = true;
						}
				}
				if (isLatestShowing) {
						latestMovie.setVisibility(View.VISIBLE);
						latest_visible_button.setImageResource(R.drawable.ic_arrow_drop_down_white);
				}
				else {
						latestMovie.setVisibility(View.GONE);
						latest_visible_button.setImageResource(R.drawable.ic_arrow_drop_up_white);
				}
			}
		});
		
		listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				ds.edit().putString("MOVIE", map_list.get((int)_position).get("MOVIE").toString()).commit();
				intent.setClass(getApplicationContext(), MoviePlayerActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		_db_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				map_list.add(_childValue);
				if (10 < map_list.size()) {
					for(int _repeat23 = 0; _repeat23 < (int)(9); _repeat23++) {
						{
							HashMap<String, Object> _item = new HashMap<>();
							_item.put("data", map_list.get((int)SketchwareUtil.getRandom((int)(0), (int)(map_list.size() - 1))).get("data").toString());
							map_list.add(_item);
						}
						
					}
					listview1.setAdapter(new Listview1Adapter(map_list));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
				else {
					listview1.setAdapter(new Listview1Adapter(map_list));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				map_list.add(_childValue);
				if (10 < map_list.size()) {
					for(int _repeat15 = 0; _repeat15 < (int)(9); _repeat15++) {
						{
							HashMap<String, Object> _item = new HashMap<>();
							_item.put("data", map_list.get((int)SketchwareUtil.getRandom((int)(0), (int)(map_list.size() - 1))).get("data").toString());
							map_list.add(_item);
						}
						
					}
					listview1.setAdapter(new Listview1Adapter(map_list));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
				else {
					listview1.setAdapter(new Listview1Adapter(map_list));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
			}
			
			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {
				
			}
			
			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				map_list.add(_childValue);
				if (10 < map_list.size()) {
					for(int _repeat15 = 0; _repeat15 < (int)(9); _repeat15++) {
						{
							HashMap<String, Object> _item = new HashMap<>();
							_item.put("data", map_list.get((int)SketchwareUtil.getRandom((int)(0), (int)(map_list.size() - 1))).get("data").toString());
							map_list.add(_item);
						}
						
					}
					listview1.setAdapter(new Listview1Adapter(map_list));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
				else {
					listview1.setAdapter(new Listview1Adapter(map_list));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
			}
			
			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		db.addChildEventListener(_db_child_listener);
		
		_internet_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
		
		auth_updateEmailListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_updatePasswordListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_emailVerificationSentListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_deleteUserListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_phoneAuthListener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> task) {
				final boolean _success = task.isSuccessful();
				final String _errorMessage = task.getException() != null ? task.getException().getMessage() : "";
				
			}
		};
		
		auth_updateProfileListener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		auth_googleSignInListener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> task) {
				final boolean _success = task.isSuccessful();
				final String _errorMessage = task.getException() != null ? task.getException().getMessage() : "";
				
			}
		};
		
		_auth_create_user_listener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		_auth_sign_in_listener = new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(Task<AuthResult> _param1) {
				final boolean _success = _param1.isSuccessful();
				final String _errorMessage = _param1.getException() != null ? _param1.getException().getMessage() : "";
				
			}
		};
		
		_auth_reset_password_listener = new OnCompleteListener<Void>() {
			@Override
			public void onComplete(Task<Void> _param1) {
				final boolean _success = _param1.isSuccessful();
				
			}
		};
	}
	
	private void initializeLogic() {
		linear2.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)25, 0xFFFFFFFF));
		latestMovie.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)15, Color.TRANSPARENT));
		linear2.setEnabled(false);
		setTitle("MOVIES");
		isLatestShowing = true;
	}
	
	@Override
	public void onBackPressed() {
		
	}
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.shows_list, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final ImageView movie_or_show_thumbnail = _view.findViewById(R.id.movie_or_show_thumbnail);
			final TextView movie_or_show_name = _view.findViewById(R.id.movie_or_show_name);
			final LinearLayout linear2 = _view.findViewById(R.id.linear2);
			final LinearLayout extra_buttons_layout = _view.findViewById(R.id.extra_buttons_layout);
			final TextView random_num = _view.findViewById(R.id.random_num);
			final TextView tag = _view.findViewById(R.id.tag);
			final ImageView like_button = _view.findViewById(R.id.like_button);
			final ImageView share_button = _view.findViewById(R.id.share_button);
			final ImageView movie_or_show_download_button = _view.findViewById(R.id.movie_or_show_download_button);
			
			Glide.with(getApplicationContext()).load(Uri.parse(_data.get((int)_position).get("THUMBNAIL").toString())).into(movie_or_show_thumbnail);
			movie_or_show_name.setText(_data.get((int)_position).get("TITLE").toString());
			random_num.setText("13X-!7UBE243&66");
			tag.setText("#".concat(_data.get((int)_position).get("TAG").toString()));
			like_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					SketchwareUtil.showMessage(getApplicationContext(), "Like added");
				}
			});
			share_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					share = "Hey Buddy🤩 , I found this App 🤓\nIn this App you can watch any movie in free without Pay.!🔥🔥".concat("\nDownload From Here : ".concat("".concat("\n".concat("watch this movie I am watching right now : ".concat(_data.get((int)_position).get("MOVIE").toString())))));
					Intent i = new Intent(android.content.Intent.ACTION_SEND);
					i.setType("text/plain"); 
					i.putExtra(android.content.Intent.EXTRA_TEXT, share); 
					startActivity(Intent.createChooser(i,"Share using"));
				}
			});
			movie_or_show_download_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					SketchwareUtil.showMessage(getApplicationContext(), "Coming soon");
				}
			});
			
			return _view;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}