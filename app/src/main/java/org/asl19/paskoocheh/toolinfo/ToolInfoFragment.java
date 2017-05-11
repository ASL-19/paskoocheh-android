package org.asl19.paskoocheh.toolinfo;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.data.GetReviewRequest;
import org.asl19.paskoocheh.data.source.ReviewRepository;
import org.asl19.paskoocheh.gallery.GalleryFragment;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.pojo.ReviewList;
import org.asl19.paskoocheh.rating.RatingDialogFragment;
import org.asl19.paskoocheh.rating.RatingDialogPresenter;
import org.asl19.paskoocheh.service.ToolDownloadService;
import org.asl19.paskoocheh.utils.ApkManager;
import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.asl19.paskoocheh.Constants.DOWNLOAD_WIFI;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PACKAGE;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_PREFS;
import static org.asl19.paskoocheh.Constants.PASKOOCHEH_UUID;
import static org.asl19.paskoocheh.Constants.TOOL_ID;
import static org.asl19.paskoocheh.gallery.GalleryFragment.IMAGES;
import static org.asl19.paskoocheh.gallery.GalleryFragment.POSITION;

public class ToolInfoFragment extends Fragment implements ToolInfoContract.ToolInfoView {

    public static final String TAG = ToolInfoFragment.class.getCanonicalName();
    private static final String INACTIVE_STAR = "#CCCCCC";
    private static final String STAR_COLOUR = "#FFA131";

    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.title_info)
    TextView title;
    @BindView(R.id.icon_view)
    ImageView icon;
    @BindView(R.id.rating_bar)
    RatingBar ratingBarTool;
    @BindView(R.id.description_body)
    TextView description;
    @BindView(R.id.toolBody)
    LinearLayout toolBody;
    @BindView(R.id.description_info)
    TextView applicationType;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.read_more)
    TextView readMore;
    @BindView(R.id.read_more_reviews)
    TextView readMoreReviews;
    @BindView(R.id.install_tools)
    LinearLayout buttonsLayout;
    @BindView(R.id.list_review)
    ListView listReview;
    @BindView(R.id.install_button)
    Button installButton;
    @BindView(R.id.update_button)
    Button updateButton;
    @BindView(R.id.uninstall_button)
    Button uninstallButton;
    @BindView(R.id.play_store_button)
    Button playstoreButton;
    @BindView(R.id.review_title)
    TextView reviewMainTitle;
    @BindView(R.id.review_divider)
    View reviewDivider;
    @BindView(R.id.badge_download_count)
    TextView badgeDownloadCount;
    @BindView(R.id.badge_rating_text)
    TextView badgeRatingText;
    @BindView(R.id.badge_category)
    ImageView categoryBadge;
    @BindView(R.id.badge_category_text)
    TextView categoryBadgeText;
    @BindView(R.id.badge_rating_total)
    TextView badgeRatingTotal;

    @Inject
    CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider;

    @Inject
    DynamoDBMapper dynamoDBMapper;

    public static final String RATING = "RATING";
    public static final String DOWNLOAD_COUNT = "DOWNLOAD_COUNT";
    public static final String TOOL = "TOOL";
    private static final String REVIEWS = "REVIEWS";

    private ToolInfoContract.Presenter presenter;
    private Unbinder unbinder;
    private long toolId;
    private AndroidTool tool;
    private String downloadCount;
    private String rating;
    private Integer currentReviews = 0;
    private ReviewList reviewList;

    public static ToolInfoFragment newInstance() {
        return new ToolInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tool_info, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getActivity()).logEvent(Constants.OPEN_PAGE, bundle);

        ((PaskoochehApplication) getActivity().getApplication()).getAmazonComponenet().inject(this);

        if (getArguments() != null) {
            toolId = getArguments().getLong(TOOL);
            rating = getArguments().getString(RATING);
            downloadCount = getArguments().getString(DOWNLOAD_COUNT);
        }

        if (savedInstanceState != null) {
            rating = savedInstanceState.getString(RATING);
            downloadCount = savedInstanceState.getString(DOWNLOAD_COUNT);
            tool = Parcels.unwrap(savedInstanceState.getParcelable(TOOL));
            reviewList = Parcels.unwrap(savedInstanceState.getParcelable(REVIEWS));

            onGetToolSuccessful(tool);
        } else {
            presenter.getTool(toolId);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tool != null) {
            updateButtons();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RATING, rating);
        outState.putString(DOWNLOAD_COUNT, downloadCount);
        outState.putParcelable(TOOL, Parcels.wrap(tool));
        outState.putParcelable(REVIEWS, Parcels.wrap(reviewList));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(ToolInfoContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onGetToolSuccessful(AndroidTool tool) {
        this.tool = tool;

        GetReviewRequest getReviewRequest = new GetReviewRequest();
        getReviewRequest.setAppName(tool.getEnglishName());
        getReviewRequest.setPkgName(tool.getPackageName());
        getReviewRequest.setCognito(cognitoCachingCredentialsProvider.getIdentityId());

        if (reviewList == null) {
            presenter.getToolReviews(getReviewRequest);
        } else {
            onGetReviewListSuccessful(reviewList);
        }

        if (rating == null || rating.isEmpty()) {
            presenter.getRating(tool.getEnglishName());
        } else {
            onGetRatingSuccessful(rating);
        }

        if (downloadCount == null || downloadCount.isEmpty()) {
            presenter.getDownloadCount(tool.getEnglishName());
        } else {
            onGetDownloadCountSuccessful(downloadCount);
        }

        setUpRatingFeedback();

        Picasso.with(getContext())
                .load(tool.getIconUrl())
                .resize(400, 400)
                .centerInside()
                .into(icon);

        title.setText(tool.getName());
        applicationType.setText(tool.getAppType());
        version.setText(tool.getBuildVersion());
        categoryBadge.setImageResource(tool.getBadgeId());
        categoryBadgeText.setText(tool.getAppType());

        updateButtons();

        displayImages();

        description.setText(Html.fromHtml(tool.getDescription()));

        description.post(new Runnable() {
            @Override
            public void run() {
                if (description != null) {
                    if (description.getLineCount() < 15) {
                        readMore.setVisibility(View.GONE);
                    } else {
                        description.setMaxLines(15);
                    }
                }
            }
        });

        buttonsLayout.setVisibility(View.VISIBLE);
        toolBody.setVisibility(View.VISIBLE);
    }


    @Override
    public void onGetToolFailed() {

    }

    @Override
    public void onGetRatingSuccessful(String rating) {
        this.rating = rating;
        badgeRatingText.setText(rating);
    }

    @Override
    public void onGetRatingFailed() {

    }

    @Override
    public void onGetDownloadCountSuccessful(String count) {
        this.downloadCount = count;
        badgeDownloadCount.setText(count);
    }

    @Override
    public void onGetDownlaodCountFailed() {
    }

    @Override
    public void onGetReviewListSuccessful(ReviewList reviewList) {
        this.reviewList = reviewList;

        populateReviews();
    }

    @OnClick(R.id.read_more)
    public void expandDescription() {
        if (readMore.getText().equals(getString(R.string.read_more))) {
            description.setMaxLines(description.getLineCount());
            readMore.setText(getString(R.string.read_less));
        } else {
            description.setMaxLines(15);
            readMore.setText(getString(R.string.read_more));
        }
    }

    @OnClick(R.id.read_more_reviews)
    void populateReviews() {
        if (reviewList.getCount() > 0) {
            reviewMainTitle.setVisibility(View.VISIBLE);
            reviewDivider.setVisibility(View.VISIBLE);
            if (reviewList.getCount() > currentReviews) {
                readMoreReviews.setVisibility(View.VISIBLE);
            }
        }

        currentReviews += 4;
        if (currentReviews > reviewList.getCount()) {
            currentReviews = reviewList.getCount();
        }

        if (currentReviews == reviewList.getCount()) {
            readMoreReviews.setVisibility(View.GONE);
        }

        ToolInfoReviewAdapter toolInfoReviewAdapter = new ToolInfoReviewAdapter(getContext(), reviewList.getAllReviews().subList(0, currentReviews));
        listReview.setAdapter(toolInfoReviewAdapter);
    }

    @Override
    public void onGetReviewListFailed() {

    }

    @Override
    public void onRegisterDownloadSuccessful() {

    }

    @Override
    public void onRegisterDownloadFailed() {

    }

    private void setUpRatingFeedback() {
        LayerDrawable stars = (LayerDrawable) ratingBarTool.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor(STAR_COLOUR), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor(STAR_COLOUR), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.parseColor(INACTIVE_STAR), PorterDuff.Mode.SRC_ATOP);

        ratingBarTool.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, final float rating, boolean fromUser) {
                if (fromUser && rating > 0) {
                    RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putFloat("RATING", rating);
                    bundle.putParcelable("TOOL", Parcels.wrap(tool));
                    ratingDialogFragment.setArguments(bundle);
                    ratingDialogFragment.show(getActivity().getFragmentManager(), ratingDialogFragment.getClass().getName());

                    new RatingDialogPresenter(ratingDialogFragment, new ReviewRepository());

                    ratingBar.setRating(-1);
                }
            }
        });
    }

    private void updateButtons() {
        installButton.setVisibility(GONE);
        playstoreButton.setVisibility(GONE);
        uninstallButton.setVisibility(GONE);
        updateButton.setVisibility(GONE);

        tool.setUpdateAvailable(false);
        tool.setInstalled(false);
        try {
            int installedVersionCode = getContext().getPackageManager().getPackageInfo(tool.getPackageName(), 0).versionCode;
            tool.setInstalled(true);
            if (tool.getVersionCode() > installedVersionCode) {
                tool.setUpdateAvailable(true);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (!tool.isInstalled()) {
            if (!tool.getDownloadUrl().isEmpty()) {
                installButton.setVisibility(VISIBLE);
            } else {
                playstoreButton.setVisibility(VISIBLE);
            }
        } else {
            uninstallButton.setVisibility(VISIBLE);
            if (tool.isUpdateAvailable()) {
                updateButton.setVisibility(VISIBLE);
            }
        }

        if (tool.getPackageName().equals(PASKOOCHEH_PACKAGE)) {
            uninstallButton.setVisibility(GONE);
        }
    }

    @OnClick(R.id.update_button)
    void updateApplication() {
        if (tool.getDownloadUrl().isEmpty()) {
            playStoreRedirect();
        } else {
            installApplication();
        }
    }

    @OnClick(R.id.install_button)
    void installApplication() {

        File toolFile = new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/" + tool.getName() + ".apk");
        String uuid = getContext().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getString(PASKOOCHEH_UUID, "");

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, tool.getToolId().intValue());
        } else if (toolFile.exists()) {
            ApkManager.installPackage(getContext(), tool.getChecksum(), toolFile);
        } else {
            ConnectivityManager connManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            if (!getActivity().getSharedPreferences(PASKOOCHEH_PREFS, Context.MODE_PRIVATE).getBoolean(DOWNLOAD_WIFI, true) || (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)) {
                presenter.registerDownload(uuid, tool.getEnglishName(), dynamoDBMapper);
                Intent intent = new Intent(getActivity(), ToolDownloadService.class);
                intent.putExtra("TOOL", Parcels.wrap(tool));
                getActivity().startService(intent);
                Toast.makeText(getContext(), getString(R.string.queued), Toast.LENGTH_SHORT).show();
            } else if ((activeNetwork != null && activeNetwork.getType() != ConnectivityManager.TYPE_WIFI)) {
                Toast.makeText(getContext(), getString(R.string.connect_wifi), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.play_store_button)
    void playStoreRedirect() {

        String uuid = getContext().getSharedPreferences(
                PASKOOCHEH_PREFS,
                Context.MODE_PRIVATE
        ).getString(PASKOOCHEH_UUID, "");

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
        bundle.putString(TOOL_ID, tool.getEnglishName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.PLAY_STORE, bundle);

        presenter.registerDownload(uuid, tool.getEnglishName(), dynamoDBMapper);

        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + tool.getPackageName())
        );

        getContext().startActivity(browserIntent);
    }

    @OnClick(R.id.uninstall_button)
    void uninstallApplication() {
        ApkManager.uninstallPackage(getContext(), tool.getPackageName());
    }

    /**
     * Send email to mail responder email to get response email with apk.
     */
    @OnClick(R.id.email)
    public void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("*/*");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{tool.getMailResponder()});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.via_email));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.via_email_subject));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.send_email)));

            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
            bundle.putString(TOOL_ID, tool.getEnglishName());
            FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.EMAIL, bundle);
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_email_clients), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Provide share deep link URL through ACTION_SEND intent.
     */
    @OnClick(R.id.share)
    public void shareTool() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
        bundle.putString(TOOL_ID, tool.getEnglishName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.SHARE, bundle);

        String location = "https://paskoocheh.com/tools/" + tool.getToolId().toString() + "/ANDROID";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, location);
        startActivity(share);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean requestGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (requestGranted) {
            Toast.makeText(getContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
            installApplication();
        } else {
            Toast.makeText(getContext(), getString(R.string.required_write), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayImages() {
        for (int i = 0; i < tool.getScreenshots().size(); i++) {

            final Integer position = i;
            final ImageView imageView = new ImageView(getContext());

            int padding = (int) (5 * getResources().getDisplayMetrics().density);
            imageView.setPadding(padding, padding, padding, padding);
            Picasso.with(getContext())
                    .load(Uri.parse(tool.getScreenshots().get(i)))
                    .resize(500, 500)
                    .centerInside()
                    .into(imageView);
            linear.addView(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.SCREEN, TAG);
                    bundle.putString(TOOL_ID, tool.getEnglishName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.GALLERY, bundle);

                    GalleryFragment galleryFragment = new GalleryFragment();
                    bundle = new Bundle();
                    bundle.putInt(POSITION, position);
                    bundle.putStringArrayList(IMAGES, new ArrayList<>(tool.getScreenshots()));
                    galleryFragment.setArguments(bundle);

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.contentFrame, galleryFragment, TAG);
                    ft.addToBackStack(TAG);
                    ft.commit();
                    getActivity().getSupportFragmentManager().executePendingTransactions();
                }
            });
            toolBody.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}