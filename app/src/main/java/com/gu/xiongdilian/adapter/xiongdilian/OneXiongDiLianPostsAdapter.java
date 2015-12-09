package com.gu.xiongdilian.adapter.xiongdilian;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.baselibrary.utils.ScreenUtils;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.activity.xiongdilian.PostDetailActivity;
import com.gu.xiongdilian.config.MyConfig;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.Post;
import com.gu.xiongdilian.utils.TimeUtil;

import java.util.List;

/**
 * @author nate
 * @ClassName: OneXiongDiLianPostsAdapter
 * @Description: 兄弟连帖子列表
 * @date 2015-5-28 下午2:51:01
 */
public class OneXiongDiLianPostsAdapter extends MyBaseAdapter<Post> {

    private Context mContext = null;

    public OneXiongDiLianPostsAdapter(Context context, int resource, List<Post> list) {
        super(context, resource, list);
        this.mContext = context;
    }

    @Override
    public void setConvert(BaseViewHolder viewHolder, final Post post) {
        if (viewHolder.getPosition() == 0) {
            viewHolder.getView(R.id.during_line).setVisibility(View.GONE);
        } else {
            viewHolder.getView(R.id.during_line).setVisibility(View.VISIBLE);
        }

        Account author = post.getAuthor();
        if (TextUtils.isEmpty(author.getAvatar())) {
            viewHolder.setResRoundConerImg(R.id.post_man_head_iv, R.mipmap.default_xiongdilian_headimg, MyConfig.IMG_CORNER_RADIUS);
        } else {
            viewHolder.setRoundCornerImgFromNet(R.id.post_man_head_iv, author.getAvatar(), MyConfig.IMG_CORNER_RADIUS);
        }

        viewHolder.setTextView(R.id.post_author_name_tv, author.getUsername());
        viewHolder.setTextView(R.id.post_create_time_tv,
                TimeUtil.getDescriptionTimeFromTimestamp(TimeUtil.stringToLong(post.getCreatedAt(),
                        TimeUtil.FORMAT_DATE_TIME_SECOND)));
        viewHolder.setTextView(R.id.comment_num_tv, post.getCommentNum() + "");
        viewHolder.setTextView(R.id.praise_num_tv, post.getPariseNum() + "");
        viewHolder.setTextView(R.id.post_title_tv, post.getTitle());
        viewHolder.setMTextView(R.id.post_content_tv, post.getContent());

        List<String> imgs = post.getImgs();
        viewHolder.getView(R.id.post_img_ll).setVisibility(View.GONE);
        viewHolder.getView(R.id.post_iv3).setVisibility(View.INVISIBLE);
        viewHolder.getView(R.id.post_iv2).setVisibility(View.INVISIBLE);
        viewHolder.getView(R.id.post_iv1).setVisibility(View.INVISIBLE);
        if (imgs != null && imgs.size() > 0) {
            // 保证图形为 正方形
            LayoutParams layoutParams =
                    new LayoutParams((ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dp2px(getContext(), 40)) / 3,
                            (ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dp2px(getContext(), 40)) / 3);
            LayoutParams layoutParamsWithMargins =
                    new LayoutParams((ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dp2px(getContext(), 40)) / 3,
                            (ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dp2px(getContext(), 40)) / 3);
            layoutParamsWithMargins.setMargins(ScreenUtils.dp2px(getContext(), 4), 0, 0, 0);
            viewHolder.getView(R.id.post_img_ll).setVisibility(View.VISIBLE);
            int size = imgs.size();
            if (size > 3) {
                size = 3;
            }
            switch (size) {
                case 3:
                    ImageView postImageView3 = viewHolder.getView(R.id.post_iv3);
                    postImageView3.setLayoutParams(layoutParamsWithMargins);
                    postImageView3.setVisibility(View.VISIBLE);
                    viewHolder.setNormalImgPath(R.id.post_iv3, imgs.get(2));
                case 2:
                    ImageView postImageView2 = viewHolder.getView(R.id.post_iv2);
                    postImageView2.setLayoutParams(layoutParamsWithMargins);
                    postImageView2.setVisibility(View.VISIBLE);
                    viewHolder.setNormalImgPath(R.id.post_iv2, imgs.get(1));
                case 1:
                    ImageView postImageView1 = viewHolder.getView(R.id.post_iv1);
                    postImageView1.setLayoutParams(layoutParams);
                    postImageView1.setVisibility(View.VISIBLE);
                    viewHolder.setNormalImgPath(R.id.post_iv1, imgs.get(0));
                    break;
                default:
                    break;
            }
        }

        viewHolder.getView(R.id.one_xiongdilian_post_item_ll).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                go(getContext(), PostDetailActivity.class, bundle);
            }
        });
    }

}
