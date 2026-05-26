package com.android.monamie.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.android.monamie.R;
import com.android.monamie.models.TeamMember;
import com.google.android.material.card.MaterialCardView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

public class TeamFragment extends Fragment {

    private ViewPager2 viewPagerTeam;
    private LinearLayout layoutIndicatorsTeam;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        viewPagerTeam = view.findViewById(R.id.viewPagerTeam);
        layoutIndicatorsTeam = view.findViewById(R.id.layoutIndicatorsTeam);
        setupViewPager();
        return view;
    }

    private void setupViewPager() {
        List<TeamMember> members = new ArrayList<>();
        members.add(new TeamMember("Raihan Al - Ayyubi", "24131310114", "Kembarannya Ian", R.drawable.rehan, "Programmer"));
        members.add(new TeamMember("Husein Abdul Aziz", "24131310114", "Expert in pastry and cookie design with 10 years experience.", R.drawable.img_cookie_butter, "Programmer"));
        members.add(new TeamMember("M. Fadhli Wafi", "24131310114", "Expert in pastry and cookie design with 10 years experience.", R.drawable.img_cookie_butter, "Programmer"));
        members.add(new TeamMember("Samsul Alam", "24131310114", "Expert in pastry and cookie design with 10 years experience.", R.drawable.img_cookie_butter, "Programmer"));
        members.add(new TeamMember("Samsul Alam", "24131310114", "Expert in pastry and cookie design with 10 years experience.", R.drawable.img_cookie_butter, "Programmer"));
        members.add(new TeamMember("Samsul Alam", "24131310114", "Expert in pastry and cookie design with 10 years experience.", R.drawable.img_cookie_butter, "Programmer"));

        TeamAdapter adapter = new TeamAdapter(members);
        viewPagerTeam.setAdapter(adapter);

        setupIndicators(members.size());
        updateIndicators(0);

        viewPagerTeam.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });
        
        // Page transformation for a nice stack/peek effect
        viewPagerTeam.setOffscreenPageLimit(3);
        viewPagerTeam.setPageTransformer((page, position) -> {
            float scale = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scale);
            page.setScaleX(scale);
        });
    }

    private void setupIndicators(int count) {
        layoutIndicatorsTeam.removeAllViews();
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageResource(R.drawable.indicator_dot);
            indicators[i].setLayoutParams(params);
            layoutIndicatorsTeam.addView(indicators[i]);
        }
    }

    private void updateIndicators(int index) {
        int childCount = layoutIndicatorsTeam.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicatorsTeam.getChildAt(i);
            if (imageView != null) {
                imageView.setSelected(i == index);
            }
        }
    }

    private class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
        private List<TeamMember> members;

        TeamAdapter(List<TeamMember> members) {
            this.members = members;
        }

        @NonNull
        @Override
        public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_card, parent, false);
            return new TeamViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
            TeamMember member = members.get(position);
            holder.tvName.setText(member.getName());
            holder.tvLocation.setText(member.getLocation());
            holder.tvStats.setText(member.getStats());
            holder.tvDesc.setText(member.getDescription());
            holder.ivPhoto.setImageResource(member.getImageRes());
            
            if (holder.ivPhotoBack != null) {
                holder.ivPhotoBack.setImageResource(member.getImageRes());
            }

            // Reset status kartu ke tampilan depan (front) saat didaur ulang (recycling)
            holder.isFlipped = false;
            holder.cardFront.setAlpha(1f);
            holder.cardFront.setRotationY(0f);
            holder.cardBack.setAlpha(0f);
            holder.cardBack.setRotationY(180f); 

            // Pastikan elevasi (shadow) kembali normal setelah recycle
            float elevation = 8 * holder.itemView.getContext().getResources().getDisplayMetrics().density;
            holder.cardFront.setCardElevation(elevation);
            holder.cardBack.setCardElevation(0f);

            holder.itemView.setOnClickListener(v -> holder.flipCard());

            // Social Media Listeners
            if (holder.ivInstagram != null) {
                holder.ivInstagram.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"));
                    v.getContext().startActivity(intent);
                });
            }

            if (holder.ivGithub != null) {
                holder.ivGithub.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/"));
                    v.getContext().startActivity(intent);
                });
            }

            if (holder.ivLinkedin != null) {
                holder.ivLinkedin.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/"));
                    v.getContext().startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        class TeamViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvLocation, tvStats, tvDesc;
            ImageView ivPhoto, ivPhotoBack, ivInstagram, ivGithub, ivLinkedin;
            MaterialCardView cardFront, cardBack;
            boolean isFlipped = false;

            TeamViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvMemberName);
                tvLocation = itemView.findViewById(R.id.tvMemberLocation);
                tvStats = itemView.findViewById(R.id.tvMemberStats);
                tvDesc = itemView.findViewById(R.id.tvMemberDescription);
                ivPhoto = itemView.findViewById(R.id.ivMemberPhoto);
                ivPhotoBack = itemView.findViewById(R.id.ivMemberPhotoBack);
                ivInstagram = itemView.findViewById(R.id.ivInstagram);
                ivGithub = itemView.findViewById(R.id.ivGithub);
                ivLinkedin = itemView.findViewById(R.id.ivLinkedin);
                cardFront = itemView.findViewById(R.id.cardFront);
                cardBack = itemView.findViewById(R.id.cardBack);
                
                // Set camera distance for smooth 3D flip
                float scale = getResources().getDisplayMetrics().density;
                cardFront.setCameraDistance(8000 * scale);
                cardBack.setCameraDistance(8000 * scale);
            }

            void flipCard() {
                AnimatorSet outAnim, inAnim;
                
                // Disable elevation during animation to hide shadow artifacts
                cardFront.setCardElevation(0);
                cardBack.setCardElevation(0);

                if (!isFlipped) {
                    outAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_right_out);
                    inAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_right_in);
                    outAnim.setTarget(cardFront);
                    inAnim.setTarget(cardBack);
                } else {
                    outAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_left_out);
                    inAnim = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_left_in);
                    outAnim.setTarget(cardBack);
                    inAnim.setTarget(cardFront);
                }

                inAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Re-enable elevation after animation finishes
                        float elevation = 8 * getResources().getDisplayMetrics().density;
                        if (!isFlipped) {
                            cardFront.setCardElevation(elevation);
                            cardBack.setCardElevation(0);
                        } else {
                            cardFront.setCardElevation(0);
                            cardBack.setCardElevation(elevation);
                        }
                    }
                });

                outAnim.start();
                inAnim.start();
                isFlipped = !isFlipped;
            }
        }
    }
}
