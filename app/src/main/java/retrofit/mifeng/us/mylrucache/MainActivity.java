package retrofit.mifeng.us.mylrucache;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String uir="http://pic.sc.chinaz.com/files/pic/pic9/201208/xpic6813.jpg";
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button xiazhai = (Button) findViewById(R.id.xiazhai);
        Button neicun = (Button) findViewById(R.id.neicun);
        Button sdk = (Button) findViewById(R.id.sdk);
        Button shanchunei = (Button) findViewById(R.id.shanchunei);
        Button shanchusdk = (Button) findViewById(R.id.shanchusdk);
        Button qingkongsdk = (Button) findViewById(R.id.qingkongsdk);
        iv = (ImageView) findViewById(R.id.iv);
        xiazhai.setOnClickListener(this);
        neicun.setOnClickListener(this);
        sdk.setOnClickListener(this);
        shanchunei.setOnClickListener(this);
        shanchusdk.setOnClickListener(this);
        qingkongsdk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.xiazhai:
                SimpleImageLoader.getInsance().save(this,uir);//下载图片
                break;
            case R.id.neicun://得到缓存内存图片
                Bitmap bimtampFromMemory = SimpleImageLoader.getInsance().getBimtampFromMemory(this,uir);
                iv.setImageBitmap(bimtampFromMemory);
                break;
            case R.id.sdk:
                try {//得到缓存本地图片
                    Bitmap bitmapFromDisk = SimpleImageLoader.getInsance().getBitmapFromDisk(this,uir);
                    iv.setImageBitmap(bitmapFromDisk);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.shanchunei:
                SimpleImageLoader.getInsance().removeFromMemory(this,uir);//删除内存图片
                break;
            case R.id.shanchusdk:
                try {
                    SimpleImageLoader.getInsance().removeFromDisk(this,uir);//删除本地图片
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.qingkongsdk:
                try {//清空本地图片
                    SimpleImageLoader.getInsance().closeDisk();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
