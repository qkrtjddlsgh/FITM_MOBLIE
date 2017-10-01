package crossfit_juan.chk.com.crossfitjuan.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import crossfit_juan.chk.com.crossfitjuan.Common.Constants;
import crossfit_juan.chk.com.crossfitjuan.Common.User;
import crossfit_juan.chk.com.crossfitjuan.DataModel.MarketItem;
import crossfit_juan.chk.com.crossfitjuan.DataModel.NoticeData;
import crossfit_juan.chk.com.crossfitjuan.HttpConnection.CustomThread.ReqHTTPJSONThread;
import crossfit_juan.chk.com.crossfitjuan.R;
import crossfit_juan.chk.com.crossfitjuan.tool.MarketItemViewAdapter;

public class MarketActivity extends AppCompatActivity {


    @BindView(R.id.listview_market)
    ListView listviewMarket;

    private MarketItemViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        ButterKnife.bind(this);
        adapter = new MarketItemViewAdapter();
        listviewMarket.setAdapter(adapter);
        try {
            ReadMarketItemList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listviewMarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent it=new Intent(MarketActivity.this,MarketItemActivity.class);
                it.putExtra("name",adapter.getItemName(position));
                startActivity(it);
            }
        });
    }

    void ReadMarketItemList() throws JSONException {
        JSONObject send_data = new JSONObject();

        send_data.put("id_email", User.getInstance().getData().getUser_email());
        ReqHTTPJSONThread thread = new ReqHTTPJSONThread(Constants.REQ_MARKET_ITEM_LIST, send_data);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException interex) {
            interex.printStackTrace();
        }
        String result = thread.handler.getMsg();
        JSONObject result_data = null;
        JSONArray response=null;
        int result_code = 0;
        try {
            result_data = new JSONObject(result);
            result_code = result_data.getInt("code");
            response = result_data.getJSONArray("response");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(result_code==9999){

            //Log.d("DEBUGYU",response.toString());
            //JSONArray resultArray=response.getJSONArray("result");
            for(int i=0;i<response.length();i++){
                JSONObject hereItem=response.getJSONObject(i);
                MarketItem newItem=new MarketItem();
                String item_isSell_String=hereItem.getString("state");
                if(item_isSell_String.equals("0"))
                    continue;

                String item_image_url=hereItem.getString("main_image");
                String item_title=hereItem.getString("out_content");
                int item_like_cnt=hereItem.getInt("num_of_purchase");
                String item_price=hereItem.getString("price");
                int item_isLike_int=hereItem.getInt("purchase_state");
                String item_name=hereItem.getString("name");
                boolean item_isLike=false;
                if(item_isLike_int!=0)
                    item_isLike=true;
                newItem.setImg_url(item_image_url);
                newItem.setTitle(item_title);
                newItem.setLike(item_isLike);
                newItem.setLike_cnt(item_like_cnt);
                newItem.setPrice(item_price);
                newItem.setName(item_name);

                adapter.addItem(newItem);
            }
            adapter.notifyDataSetChanged();
        }
        else if(result_code==5800){
            Toast.makeText(getApplicationContext(),"잘못된 요청입니다",Toast.LENGTH_LONG).show();
        }

    }


}
