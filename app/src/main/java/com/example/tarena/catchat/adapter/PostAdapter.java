package com.example.tarena.catchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.app.MyApp;
import com.example.tarena.catchat.bean.Blog;
import com.example.tarena.catchat.bean.Comment;
import com.example.tarena.catchat.bean.MyUser;
import com.example.tarena.catchat.listener.onCommentBlogListener;
import com.example.tarena.catchat.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by tarena on 2017/7/7.
 */

public class PostAdapter extends MyBaseAdapter<Blog> {
    onCommentBlogListener listener;

    public void setListener(onCommentBlogListener listener) {
        this.listener = listener;
    }

    public PostAdapter(Context context, List<Blog> datasource) {
        super(context, datasource);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.findfragment_item_layout, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            final Blog blog = getItem(position);







            MyUser author = blog.getAuthor();

            setAvatar(author.getAvatar(), vh.ivAvatar);

            vh.tvUsername.setText(author.getUsername());

            vh.tvContent.setText(blog.getContent());

            //配图的呈现
            vh.rlImageContaienr.removeAllViews();
            String imgUrls = blog.getImgUrls();
            if (!TextUtils.isEmpty(imgUrls.trim())) {
                showBlogImages(imgUrls, vh.rlImageContaienr);
                vh.rlImageContaienr.setVisibility(View.VISIBLE);
            }
//            else {
//                vh.rlImageContaienr.setVisibility(View.GONE);
//            }
            //yyyy-MM-dd HH:mm:ss

            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(blog.getCreatedAt());
            vh.tvTime.setText(TimeUtil.getTime(date.getTime()));
            showBlogComments(position, vh.llCommentContainer);


            vh.tvLove.setText(blog.getLoveUsers().size() + "赞");
            vh.tvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onComment(position,blog);


                }
            });
            vh.tvLove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String username = BmobUser.getCurrentUser(MyApp.CONTEXT).getUsername();
                    //判断当前用户是否点赞
                    if (blog.getLoveUsers().contains(username)) {
                        blog.getLoveUsers().remove(username);
                    } else {
                        blog.getLoveUsers().add(username);
                    }
                    blog.update(MyApp.CONTEXT, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            vh.tvLove.setText(blog.getLoveUsers().size() + "赞");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            });
            vh.ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTapUser(blog.getAuthor());
                }
            });

            return convertView;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("日期格式不正确");
        }
    }


    private void showBlogImages(String imgUrls, RelativeLayout imageContaienr) {
        if (!imgUrls.startsWith("http")){
            return;
        }
        //图1&图2&图3&图4
        String[] urls = imgUrls.split("&");

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        //左外边距(10dp)+右外边距(10dp)+margin(15dp)
        int span = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, context.getResources().getDisplayMetrics());

        int size = (screenWidth-span)/2;

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());

        for(int i=0;i<urls.length;i++){
            final ImageView iv = new ImageView(context);

            iv.setId(i+1);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);

            if(i%2!=0){
                //添加右侧规则
                params.addRule(RelativeLayout.RIGHT_OF, i);
                params.leftMargin = margin;
            }

            if(i>=2){
                //添加下规则
                params.addRule(RelativeLayout.BELOW, i-1);
                params.topMargin = margin;
            }

            iv.setLayoutParams(params);

            //显示图片
            ImageLoader.getInstance().displayImage(urls[i],iv);
            iv.setBackgroundResource(R.drawable.input_bg);
            iv.setPadding(padding, padding, padding, padding);

            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            imageContaienr.addView(iv);
        }
    }

    /**
     * 显示所有该条blog的评论
     *
     * @param position
     * @param commentContainer
     */
    private void showBlogComments(int position, final LinearLayout commentContainer) {

        //删除之前显示的评论内容
        commentContainer.removeAllViews();

        BmobQuery<Comment> query = new BmobQuery<Comment>();
        String blogId = getItem(position).getObjectId();
        query.addWhereEqualTo("blogId", blogId);
        query.findObjects(context, new FindListener<Comment>() {

            @Override
            public void onSuccess(List<Comment> arg0) {
                if (arg0 != null && arg0.size() > 0) {
                    for (Comment comment : arg0) {
                        String content = comment.getUsername() + ":" + comment.getContent();
                        TextView tv = new TextView(context);
                        tv.setText(content);
                        tv.setTextColor(Color.BLUE);
                        tv.setBackgroundColor(Color.WHITE);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, context.getResources().getDisplayMetrics());
                        tv.setPadding(padding, padding, padding, padding);
                        commentContainer.addView(tv);

                    }
                }

            }

            @Override
            public void onError(int arg0, String arg1) {
                Log.d("TAG", arg0 + ":" + arg1);
            }
        });

    }


    public class ViewHolder {
        @BindView(R.id.iv_item_blog_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_item_blog_username)
        TextView tvUsername;
        @BindView(R.id.tv_item_blog_cotnent)
        TextView tvContent;
        @BindView(R.id.tv_item_blog_time)
        TextView tvTime;
        @BindView(R.id.tv_item_blog_love)
        TextView tvLove;
        @BindView(R.id.tv_item_blog_comment)
        TextView tvComment;
        @BindView(R.id.rl_item_blog_imagecontainer)
        RelativeLayout rlImageContaienr;
        @BindView(R.id.ll_item_blog_commentcontainer)
        LinearLayout llCommentContainer;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }


    }
}
