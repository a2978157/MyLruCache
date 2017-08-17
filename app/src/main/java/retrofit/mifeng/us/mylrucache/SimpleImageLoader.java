package retrofit.mifeng.us.mylrucache;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.widget.Toast;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 21903 on 2017/8/17.
 */

public class SimpleImageLoader {
    private static SimpleImageLoader simpleImageLoader = new SimpleImageLoader();
    private LruCache<String, Bitmap> mLruCache;
    private DiskLruCache mDiskLruCache;
    boolean b = false;

    private SimpleImageLoader() {
        //一般定位为android虚拟机内存的1/8
        int i = (int) ((Runtime.getRuntime().maxMemory() / 8));
        //初始化内存缓存
        mLruCache = new LruCache<String, Bitmap>(i) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        String s = Environment.getExternalStorageDirectory() + "/howto";
        File file = new File(s);
        //初始化本地缓存
        try {
            mDiskLruCache = DiskLruCache.open(file, App.versionCode, 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String md5(String s) {
        return "hehe";
    }

    public static SimpleImageLoader getInsance() {
        return simpleImageLoader;
    }

    //下载图片
    public void save(final Activity activity, final String s) {
        Request.Builder builder = new Request.Builder();
        Request build = builder.url(s).get().build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(build);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] bytes = response.body().bytes();
                saveToMemory(activity, s, bytes);
                saveToDisk(s, bytes);
            }
        });
    }

    //缓存内存图片
    public void saveToMemory(final Activity activity, String s, byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (mLruCache.get(md5(s)) == null) {
            mLruCache.put(md5(s), bitmap);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "图片已缓存", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "图片缓存失败", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    //存到本地
    public void saveToDisk(String s, byte[] bytes) {
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(md5(s));
            OutputStream outputStream = edit.newOutputStream(0);
            outputStream.write(bytes);
            edit.commit();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //取本地图片
    public Bitmap getBitmapFromDisk(Activity a, String s) throws IOException {
        String ss = Environment.getExternalStorageDirectory() + "/howto";
        File file = new File(ss);
        //初始化本地缓存
        try {
            mDiskLruCache = DiskLruCache.open(file, App.versionCode, 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(md5(s));
        if (snapshot != null) {
            InputStream inputStream = snapshot.getInputStream(0);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } else {
            Toast.makeText(a, "没有本地图片", Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    //取内存图片
    public Bitmap getBimtampFromMemory(Activity a, String s) {
        Bitmap bitmap;
        String ss = md5(s);
        if (mLruCache != null && mLruCache.size() > 0) {
            bitmap = mLruCache.get(ss);
        } else {
            bitmap = null;
            Toast.makeText(a, "没有本地图片", Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    //删除本地图片
    public void removeFromDisk(Activity a, String s) throws IOException {
        String s1 = md5(s);
        if (mDiskLruCache != null && mDiskLruCache.size() > 0&&b==false) {
            mDiskLruCache.remove(s1);
        } else {
            Toast.makeText(a, "没有本地图片", Toast.LENGTH_SHORT).show();
        }
    }

    //清空本地图片
    public void closeDisk() throws IOException {
        b=true;
        mDiskLruCache.delete();
    }

    //删除内存图片
    public void removeFromMemory(Activity a, String s) {
        if (mLruCache != null && mLruCache.size() > 0) {
            mLruCache.remove(md5(s));
        } else {
            Toast.makeText(a, "没有内存图片", Toast.LENGTH_SHORT).show();
        }
    }
}
