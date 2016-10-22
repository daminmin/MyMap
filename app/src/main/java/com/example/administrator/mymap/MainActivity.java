package com.example.administrator.mymap;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    MapView mMapView = null;
    private BaiduMap mBaiduMap;

    private Context context;

    private LocationClient mLocationClient = null;
    public BDLocationListener mLocationListener = new MyLocationListener();
    private boolean isFirstIn = true;
    private double mLatitude;
    private double mLongitude;

    private BitmapDescriptor mIconLocation;

    private BitmapDescriptor mMarker;
    private RelativeLayout mMarkerly;

    private PendingIntent paIntent;
    private SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        this.context = this;

        mMapView = (MapView) findViewById(R.id.bmapView);

        mBaiduMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);

        initLocation();
        
        initMarker();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo();
                Info info = (Info)extraInfo.getSerializable("info");
                TextView send = (TextView)findViewById(R.id.id_sendmessage);
                TextView name = (TextView)findViewById(R.id.id_name);
                TextView dis = (TextView)findViewById(R.id.id_distance);
                TextView tel = (TextView)findViewById(R.id.id_phonenum);

                send.setText("Ask Where Is The Friends?");
                name.setText(info.getName());
                dis.setText(info.getDistance());
                tel.setText(info.getPhonenumber());

                mMarkerly.setVisibility(View.VISIBLE);
                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerly.setVisibility(View.GONE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        paIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
        smsManager = SmsManager.getDefault();
        findViewById(R.id.id_sendmessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsManager.sendTextMessage("13143125912", null, "Where are you?", paIntent,
                        null);
            }
        });
    }

   private void initMarker() {
        Button btn = (Button) findViewById(R.id.friends);
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOverlays(Info.infos);
            }
        });

       mMarkerly = (RelativeLayout) findViewById(R.id.id_marker);
    }//写到这里

    private void addOverlays(List<Info> infos) {
        mBaiduMap.clear();
        LatLng newlatLng = null;
        Marker marker = null;
        OverlayOptions newoption;
        for (Info info:infos){
            newlatLng = new LatLng(info.getLatitude(),info.getLongitude());
            newoption = new MarkerOptions().position(newlatLng).icon(mMarker).zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(newoption);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info",info);
            marker.setExtraInfo(arg0);
        }

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(newlatLng);
        mBaiduMap.setMapStatus(msu);
    }

    //开始定位
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);

        if(mLocationClient != null && !mLocationClient.isStarted()){
            mLocationClient.requestLocation();
            mLocationClient.start();
        }

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setNeedDeviceDirect(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);

        //初始化方向图标
        mIconLocation = BitmapDescriptorFactory.fromResource(R.drawable.arrow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
            mLocationClient.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    //定位监听器
    private class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location){

            MyLocationData data = new MyLocationData.Builder()//
                    .accuracy(location.getRadius())//
                    .latitude(location.getLatitude())//
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(data);

            MyLocationConfiguration config = new
                    MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                    true,mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);//图标设置完毕

            //经纬度
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();

            if(isFirstIn){
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(msu);

                isFirstIn = false;

                Toast.makeText(context,location.getAddrStr(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
