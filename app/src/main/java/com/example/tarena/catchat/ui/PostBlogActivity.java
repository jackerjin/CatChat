package com.example.tarena.catchat.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.bean.MyUser;
import com.example.tarena.catchat.bean.Blog;
import com.example.tarena.catchat.thirdparty.NumberProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

public class PostBlogActivity extends BaseActivity {
    @BindView(R.id.ll_imgcontainer)
    LinearLayout llImgContainer;
    @BindView(R.id.et_post_content)
    EditText etContent;
    @BindView(R.id.iv_post_picture1)
    ImageView ivPostPicture1;
    @BindView(R.id.iv_post_picDelete1)
    ImageView ivPostPicDelete1;
    @BindView(R.id.iv_post_picture2)
    ImageView ivPostPicture2;
    @BindView(R.id.iv_post_picDelete2)
    ImageView ivPostPicDelete2;
    @BindView(R.id.iv_post_picture3)
    ImageView ivPostPicture3;
    @BindView(R.id.iv_post_picDelete3)
    ImageView ivPostPicDelete3;
    @BindView(R.id.iv_post_picture4)
    ImageView ivPostPicture4;
    @BindView(R.id.iv_post_picDelete4)
    ImageView ivPostPicDelete4;
    @BindView(R.id.tv_postblog_imagenumber)
    TextView tvImageNumber;
    @BindView(R.id.npb_postblog_progressbar)
    NumberProgressBar npbProgressBar;

    @BindView(R.id.btn_postblog_plus)
    ImageButton btnPlus;
    @BindView(R.id.btn_postblog_picture)
    Button btnPicture;
    @BindView(R.id.btn_postblog_camera)
    Button btnCamera;
    @BindView(R.id.btn_postblog_location)
    Button btnLocation;

    List<ImageView> blogImages;
    List<ImageView> blogDels;

    boolean isExpanded;//默认时为false
    boolean isPosting;//是否有blog正处于上传过程中

    @Override
    public int getLayoutID() {
        return R.layout.activity_post_blog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleTV.setText("新建消息");
        showBackBtn();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        postBlogImages();

        return super.onOptionsItemSelected(item);
    }

    private void postBlogImages() {
        if (blogImages.get(0).getVisibility() == View.INVISIBLE) {
            //blog没有配图
            postBlog("");
            return;
        }
        //要上传的文件路径的数组
        List<String> list = new ArrayList<>();
        for (ImageView iv : blogImages) {
            if (iv.getVisibility() == View.VISIBLE) {
                String tag = (String) iv.getTag();
                list.add(tag);
            }
        }
        final String[] filePaths = list.toArray(new String[list.size()]);
        npbProgressBar.setVisibility(View.VISIBLE);
        BmobFile bf = new BmobFile(new File(filePaths[0]));
        bf.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                toast("上传完成");
            }

            @Override
            public void onFailure(int i, String s) {
                toast("上传失败");
            }
        });
        BmobFile.uploadBatch(this, filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list1.size() == filePaths.length) {
                    StringBuilder sb = new StringBuilder();
                    for (String s : list1) {
                        sb.append(s).append("&");

                    }
                    npbProgressBar.setVisibility(View.INVISIBLE);
                    postBlog(sb.substring(0, sb.length() - 1));
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                npbProgressBar.setProgress(i3);
            }

            @Override
            public void onError(int i, String s) {
                log(s);
            }
        });
    }

    /**
     * 上传blog
     *
     * @param imgUrls 该blog配图在服务器上保存的地址
     */
    private void postBlog(String imgUrls) {
        Blog blog = new Blog();
        blog.setAuthor(BmobUser.getCurrentUser(this, MyUser.class));
        blog.setContent(etContent.getText().toString());
        blog.setImgUrls(imgUrls);
        blog.setLoveUsers(new ArrayList<String>());
        blog.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                isPosting = false;
                toast("博客发布成功");
                etContent.setText("");
                //隐藏所有图片
                for (int i = 0; i < blogImages.size(); i++) {
                    blogImages.get(i).setVisibility(View.INVISIBLE);
                    blogDels.get(i).setVisibility(View.INVISIBLE);
                }
                tvImageNumber.setText("");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                isPosting = false;
                toastAndLog("博客发布失败", i, s);
            }
        });
    }

    private void initView() {
        blogImages = new ArrayList<ImageView>();
        blogImages.add(ivPostPicture1);
        blogImages.add(ivPostPicture2);
        blogImages.add(ivPostPicture3);
        blogImages.add(ivPostPicture4);
        blogDels = new ArrayList<>();
        blogDels.add(ivPostPicDelete1);
        blogDels.add(ivPostPicDelete2);
        blogDels.add(ivPostPicDelete3);
        blogDels.add(ivPostPicDelete4);
        llImgContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //调整imageContainer中四个Fragment的尺寸
                int width = llImgContainer.getWidth();

                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                        getResources().getDisplayMetrics());
                // (width - 3*margin)/4
                int size = (width - 3 * margin) / 4;
                for (int i = 0; i < 4; i++) {
                    View view = llImgContainer.getChildAt(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                    if (i != 3) {
                        params.setMargins(0, 0, margin, 0);
                    }
                    view.setLayoutParams(params);
                }
                //注销监听器
                llImgContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //让imageContainer按照新设定好的尺寸
                //重新摆放4个子view
                llImgContainer.requestLayout();
            }
        });
    }

    @OnClick(R.id.btn_postblog_plus)
    public void showButtons(View v) {
        if (isExpanded) {
            //所有的按钮都是可见的
            closeButtons();

        } else {
            expandButtons();
        }
    }

    private void expandButtons() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_expand);
        btnPicture.startAnimation(anim);
        btnCamera.startAnimation(anim);
        btnLocation.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                btnPicture.setVisibility(View.VISIBLE);
                btnCamera.setVisibility(View.VISIBLE);
                btnLocation.setVisibility(View.VISIBLE);
                isExpanded = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void closeButtons() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.button_close);
        btnPicture.startAnimation(anim);
        btnCamera.startAnimation(anim);
        btnLocation.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnPicture.setVisibility(View.INVISIBLE);
                btnCamera.setVisibility(View.INVISIBLE);
                btnLocation.setVisibility(View.INVISIBLE);
                isExpanded = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @OnClick(R.id.btn_postblog_picture)
    public void selectPicture(View v) {
        //判断用户是否已经授权，未授权则向用户申请授权，已授权则直接操作
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //注意第二个参数没有双引号
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 101);
        } else {
            // Permission Denied
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 101) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                        null, null, null);
                cursor.moveToNext();
                String filePath = cursor.getString(0);
                showBlogImage(filePath);
            }
            //TODO 拍照返回或地图截图返回，只要得到图片的本地路径
            //通过调用showBlogImage就可以将图片放到blogImgs中显示
        }
    }

    private void showBlogImage(String filePath) {
        for (int i = 0; i < blogImages.size(); i++) {
            ImageView iv = blogImages.get(i);
            if (iv.getVisibility() == View.INVISIBLE) {
                //此时第i个ImageView可以用来显示博客的配图
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                iv.setImageBitmap(bitmap);
                iv.setVisibility(View.VISIBLE);
                iv.setTag(filePath);
                blogDels.get(i).setVisibility(View.VISIBLE);
                tvImageNumber.setText((i + 1) + "/4");
                return;
            }
        }
        toast("最多添加四张图片");

    }

    @OnClick({R.id.iv_post_picDelete1, R.id.iv_post_picDelete2, R.id.iv_post_picDelete3, R.id.iv_post_picDelete4})
    public void deleteBlogImages(View v) {
        switch (v.getId()) {
            case R.id.iv_post_picDelete1:
                deleteBlogImage(0);
                break;
            case R.id.iv_post_picDelete2:
                deleteBlogImage(1);
                break;
            case R.id.iv_post_picDelete3:
                deleteBlogImage(2);
                break;
            case R.id.iv_post_picDelete4:
                deleteBlogImage(3);
                break;
        }
    }

    /**
     * “删除”已添加的博客配图
     *
     * @param idx
     */
    private void deleteBlogImage(int idx) {
        //1)当前blog有几幅配图
        int count = 0;
        for (ImageView image : blogImages) {
            if (image.getVisibility() == View.VISIBLE) {
                count += 1;
            }
        }
        //2)如果用户点击的恰好是最后一副配图的“小红叉”
        //  将显示最后一副配图的Imageview和小红叉隐藏
        if (idx == count - 1) {
            blogImages.get(idx).setVisibility(View.INVISIBLE);
            blogDels.get(idx).setVisibility(View.INVISIBLE);

        } else {
            //3)如用用户点击的不是最后一副配图的“小红叉”
            //  需要将用户点击位置后面的配图依次向前递补
            //  直到最后一副配图再将其隐藏
            for (int i = idx; i < count; i++) {
                if (i == count - 1) {
                    blogImages.get(i).setVisibility(View.INVISIBLE);
                    blogDels.get(i).setVisibility(View.INVISIBLE);

                } else {
                    Drawable drawable = blogImages.get(i + 1).getDrawable();
                    String path = (String) blogImages.get(i + 1).getTag();
                    blogImages.get(i).setImageDrawable(drawable);
                    blogImages.get(i).setTag(path);
                }

            }
        }
        //4)配图删除后，修改配图数量的显示
        if (count == 1) {
            tvImageNumber.setText("");
        } else {
            tvImageNumber.setText((count - 1) + "/4");
        }
    }
}
