package com.bluedon.gsm.detector.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bluedon.gsm.detector.R;
import com.bluedon.gsm.detector.data.BSInfo;
import com.bluedon.gsm.detector.utils.LocationConverter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaiduMapFragment extends Fragment {
    private static final String TAG = BaiduMapFragment.class.getName();

    private Unbinder unbinder;

    @BindView(R.id.baidu_map)
    MapView mMapView;

    BaiduMap mBaiduMap;
    LocationClient mLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_baidu_map, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        markMyLocation();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        mLocationClient.stop();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    public void markMyLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
        // 显示出当前位置的小图标
        mBaiduMap.setMyLocationEnabled(true);

        BDLocationListener mListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.e(TAG, "baidu | latitude " + latitude + ", longitude " + longitude);
                // 创建位置数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .direction(100)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();
                // 设置在地图上的位置
                mBaiduMap.setMyLocationData(locData);
                // 将位置显示在地图视图中央
                LatLng ll = new LatLng(latitude, longitude);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16.0f);
                mBaiduMap.animateMapStatus(u);
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        };
        mLocationClient.registerLocationListener(mListener);
        mLocationClient.start();
    }

    public void markCells(List<BSInfo> list) {
        mBaiduMap.clear();
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark);
        List<OverlayOptions> options = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            BSInfo bs = list.get(i);
            LatLng location = LocationConverter.Gps2LatLng(bs.latitude, bs.longitude);
            OverlayOptions option = new MarkerOptions()
                    .position(location)
                    .icon(bitmap)
                    .zIndex(i)
                    .title(bs.type + " | " + bs.desc)
                    .animateType(MarkerOptions.MarkerAnimateType.grow);
            options.add(option);
        }
        mBaiduMap.addOverlays(options);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                InfoWindow window = createInfoWindow(marker);
                mBaiduMap.showInfoWindow(window);
                return true;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private InfoWindow createInfoWindow(Marker marker) {
        LatLng position = marker.getPosition();
        double latitude = position.latitude;
        double longitude = position.longitude;
        View view = View.inflate(getActivity(), R.layout.map_marker_infowindow, null);
        TextView titleView = view.findViewById(R.id.mark_info_title);
        TextView latitudeView = view.findViewById(R.id.mark_info_latitude);
        TextView longitudeView = view.findViewById(R.id.mark_info_longitude);
        titleView.setText(marker.getTitle());
        latitudeView.setText(getString(R.string.title_latitude) + latitude);
        longitudeView.setText(getString(R.string.title_longitude) + longitude);
        return new InfoWindow(BitmapDescriptorFactory.fromView(view), position, -72, new InfoWindow.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                mBaiduMap.hideInfoWindow();
            }
        });
    }
}
