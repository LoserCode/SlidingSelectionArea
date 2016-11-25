package com.example.slidingselectionarea;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView text;
    private SlidingView slidingView;
    private CityBean cityBean;
    private ListView province;
    private ListView area;
    private List<CityBean> provinceData = new ArrayList<>();
    private List<CityBean.SubBean> areaData = new ArrayList<>();
    private ProvinceAdapter provinceAdapter;
    private AreaAdapter areaAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.text);
        slidingView = (SlidingView) findViewById(R.id.slideview);
        //省列表
        province = (ListView) findViewById(R.id.province_listview);
        //地区列表
        area = (ListView) findViewById(R.id.area_listview);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        slidingView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        provinceAdapter = new ProvinceAdapter(this, provinceData);
        areaAdapter = new AreaAdapter(this, areaData);
        province.setAdapter(provinceAdapter);
        area.setAdapter(areaAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取Json数据
                String cityString = getJsonString();
                //解析Json
                final List<CityBean> data = getData(cityString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        slidingView.setVisibility(View.VISIBLE);
                        provinceData.addAll(data);
                        provinceAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        province.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                areaData.clear();
                if (provinceData.get(i).getSub().size() > 0) {
                    areaData.addAll(provinceData.get(i).getSub());
                    areaAdapter.notifyDataSetChanged();
                    slidingView.onOpen();
                } else {
                    if (slidingView.getState() == SlidingView.DragState.Open) {
                        slidingView.onClose();
                    }
                }
                if (!TextUtils.isEmpty(provinceData.get(i).getName()))
                    text.setText(provinceData.get(i).getName() + "");
            }
        });

        area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (slidingView.getState() == SlidingView.DragState.Open) {
                    slidingView.onClose();
                }
                if (!TextUtils.isEmpty(areaData.get(i).getName()))
                    text.append(areaData.get(i).getName());
            }
        });

    }

    /**
     * 手动解析Json内容
     *
     * @param cityString
     * @return
     */
    public List<CityBean> getData(String cityString) {
        List<CityBean> data = new ArrayList<>();
        //先将string转换为JsonObject对象
        try {
            JSONArray jsonArray = new JSONArray(cityString);
            for (int i = 0; i < jsonArray.length(); i++) {
                cityBean = new CityBean();
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String name = (String) jsonObject.get("name");
                JSONArray cityArray = jsonObject.getJSONArray("sub");
                cityBean.setName(name);
                List<CityBean.SubBean> areaList = new ArrayList<>();
                for (int j = 0; j < cityArray.length(); j++) {
                    CityBean.SubBean areaBean = new CityBean.SubBean();
                    JSONObject areaObject = (JSONObject) cityArray.get(j);
                    areaBean.setName((String) areaObject.get("name"));
                    areaList.add(areaBean);
                }
                cityBean.setSub(areaList);
                data.add(cityBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 获取本地Json数据
     *
     * @return
     */
    public String getJsonString() {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager manager = getAssets();
        try {
            //创建读取流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    manager.open("city.json")));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
