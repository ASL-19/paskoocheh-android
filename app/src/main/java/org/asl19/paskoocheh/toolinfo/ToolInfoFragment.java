package org.asl19.paskoocheh.toolinfo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.asl19.paskoocheh.ActivityUtils;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.PaskoochehApplication;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.categorylist.CategoryListActivity;
import org.asl19.paskoocheh.categorylist.CategoryListFragment;
import org.asl19.paskoocheh.data.source.AmazonRepository;
import org.asl19.paskoocheh.gallery.GalleryActivity;
import org.asl19.paskoocheh.guide.GuideActivity;
import org.asl19.paskoocheh.guide.GuideFragment;
import org.asl19.paskoocheh.installreceiver.InstallFragment;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Faq;
import org.asl19.paskoocheh.pojo.Guide;
import org.asl19.paskoocheh.pojo.Image;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Review;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.pojo.Tutorial;
import org.asl19.paskoocheh.pojo.Version;
import org.asl19.paskoocheh.rating.RatingDialogFragment;
import org.asl19.paskoocheh.rating.RatingDialogPresenter;
import org.asl19.paskoocheh.utils.FileViewer;
import org.asl19.paskoocheh.utils.NonScrollExpandableListView;
import org.asl19.paskoocheh.utils.NonScrollListView;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.inflate;
import static org.asl19.paskoocheh.Constants.BUCKET_NAME;
import static org.asl19.paskoocheh.Constants.TOOL_ID;
import static org.asl19.paskoocheh.categorylist.CategoryListActivity.CATEGORY;
import static org.asl19.paskoocheh.categorylist.CategoryListActivity.TYPE;
import static org.asl19.paskoocheh.gallery.GalleryFragment.IMAGES;
import static org.asl19.paskoocheh.gallery.GalleryFragment.POSITION;
import static org.asl19.paskoocheh.gallery.GalleryFragment.OUINET_GROUP;

public class ToolInfoFragment extends InstallFragment implements ToolInfoContract.ToolInfoView {

    public static final String TAG = ToolInfoFragment.class.getCanonicalName();
    private static final String INACTIVE_STAR = "#CCCCCC";
    private static final String STAR_COLOUR = "#FFB033";

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.screenshot_layout)
    LinearLayout screenshotLayout;
    @BindView(R.id.horizontal_scroll)
    HorizontalScrollView horizontalScrollView;
    @BindView(R.id.screenshot_inner_layout)
    LinearLayout screenshotInnerLayout;
    @BindView(R.id.title_info)
    TextView title;
    @BindView(R.id.categories)
    FlexboxLayout categories;
    @BindView(R.id.icon_view)
    ImageView icon;
    @BindView(R.id.rating_bar)
    RatingBar ratingBarTool;
    @BindView(R.id.description_divider)
    View descriptionDivider;
    @BindView(R.id.description_layout)
    LinearLayout descriptionLayout;
    @BindView(R.id.description_body)
    TextView description;
    @BindView(R.id.toolBody)
    LinearLayout toolBody;
    @BindView(R.id.vendor)
    TextView vendor;
    @BindView(R.id.version)
    TextView versionNumber;
    @BindView(R.id.read_more)
    TextView readMore;
    @BindView(R.id.read_more_reviews)
    TextView readMoreReviews;
    @BindView(R.id.install_tools)
    LinearLayout buttonsLayout;
    @BindView(R.id.list_review)
    RecyclerView listReview;
    @BindView(R.id.install_button)
    Button installButton;
    @BindView(R.id.update_button)
    Button updateButton;
    @BindView(R.id.email)
    Button emailButton;
    @BindView(R.id.play_store_button)
    Button playstoreButton;
    @BindView(R.id.review_layout)
    LinearLayout reviewLayout;
    @BindView(R.id.badge_download_count)
    TextView badgeDownloadCount;
    @BindView(R.id.badge_rating_text)
    TextView badgeRatingText;
    @BindView(R.id.badge_rating_total)
    TextView badgeRatingTotal;
    @BindView(R.id.total)
    TextView ratingTotal;
    @BindView(R.id.rating)
    TextView ratingText;
    @BindView(R.id.guide_divider)
    View guideDivider;
    @BindView(R.id.guide_layout)
    LinearLayout guideLayout;
    @BindView(R.id.expandable_list_guide)
    NonScrollListView guideNonScrollListView;
    @BindView(R.id.expandable_list_tutorial)
    NonScrollExpandableListView tutorialExpandableListView;
    @BindView(R.id.faq_divider)
    View faqDivider;
    @BindView(R.id.faq_layout)
    LinearLayout faqLayout;
    @BindView(R.id.expandable_list)
    NonScrollExpandableListView faqExpandableListView;
    @BindView(R.id.builder)
    TextView builder;
    @BindView(R.id.last_modified)
    TextView lastModified;
    @BindView(R.id.support_layout)
    LinearLayout supportLayout;
    @BindView(R.id.size)
    TextView fileSize;

    public static final String TOOL = "TOOL";

    private ToolInfoContract.Presenter presenter;
    private Unbinder unbinder;
    private Integer currentReviews;
    private List<Review> listOfReviews = new ArrayList<>();
    private List<Review> reviewList;
    private Images images;
    private Picasso toolPicasso;
    private Picasso versionPicasso;
    private ToolInfoReviewAdapter reviewAdapter;

    public static ToolInfoFragment newInstance() {
        return new ToolInfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_tool_info, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, TAG);
        FirebaseAnalytics.getInstance(getActivity()).logEvent(Constants.OPEN_PAGE, bundle);

        ((PaskoochehApplication) getActivity().getApplication()).getAmazonComponenet().inject(this);

        if (getArguments() != null) {
            versionId = getArguments().getInt(TOOL);
            Log.d(TAG, "onCreateView versionId = " + versionId);
        }

        if (version == null || versionId != version.versionCode) {
            presenter.getVersion(versionId);
        } else {
            checkAndUpdateVersionObjectIfTheAppIsInstallableInThisDevice(version, getContext().getPackageName());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (version != null) {
            updateButtons();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
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
    protected void onInstallSuccessUIUpdate() {
        if (updateButton.getVisibility() == VISIBLE) {
            toggleUpdateButton(false);
        } else {
            toggleInstallButton(false);
        }
    }

    @Override
    protected void onInstallFailureUIUpdate() {
        if (updateButton.getVisibility() == VISIBLE) {
            toggleUpdateButton(true);
        } else {
            toggleInstallButton(true);
        }
    }

    private void toggleInstallButton(boolean isEnable) {
        if (isEnable) {
            installButton.setEnabled(true);
            installButton.setBackgroundResource(R.drawable.button_regular);
            installButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            installButton.setBackgroundResource(R.drawable.button_regular_disabled);
            installButton.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonGrey));
            installButton.setEnabled(false);
        }
    }

    private void toggleUpdateButton(boolean isEnable) {
        if (isEnable) {
            updateButton.setEnabled(true);
            updateButton.setBackgroundResource(R.drawable.button_regular_blue);
            updateButton.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
        } else {
            updateButton.setBackgroundResource(R.drawable.button_regular_disabled);
            updateButton.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonGrey));
            updateButton.setEnabled(false);
        }
    }

    @Override
    public void onGetVersionSuccessful(Version version) {
        this.version = version;

        checkAndUpdateVersionObjectIfTheAppIsInstallableInThisDevice(this.version, getContext().getPackageName());

        Log.d(TAG, "onGetVersionSuccessful  version = " + version);
        reviewAdapter = new ToolInfoReviewAdapter(listOfReviews);
        listReview.setAdapter(reviewAdapter);

        int toolId = version.getToolId();
        presenter.getToolReviews(toolId);
        presenter.getDownloadAndRating(toolId);
        presenter.getFaqList(toolId);
        presenter.getLocalizedInfo(toolId);
        presenter.getGuideList(toolId);
        presenter.getTool(toolId);
        presenter.getCategoryNames();
        presenter.getTutorials(toolId);
        presenter.getVersionImages(version.getId());

        setUpRatingFeedback();

        versionNumber.setText(version.getVersionNumber());
        fileSize.setText(Formatter.formatFileSize(getContext(), version.getSize()));

        updateButtons();

        lastModified.setText(version.getReleaseJDate());
        buttonsLayout.setVisibility(VISIBLE);
        toolBody.setVisibility(VISIBLE);
    }

    @Override
    public void onGetVersionFailed() {
        toggleInstallButton(false);

        playstoreButton.setBackgroundResource(R.drawable.button_regular_disabled);
        playstoreButton.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonGrey));
        playstoreButton.setEnabled(false);

        emailButton.setBackgroundResource(R.drawable.button_regular_disabled);
        emailButton.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonGrey));
        emailButton.setEnabled(false);

        updateButton.setVisibility(GONE);
    }

    @Override
    public void onGetDownloadAndRatingSuccessfull(DownloadAndRating downloadAndRating) {
        if (downloadAndRating.getDownloadCount() != null) {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("fa"));
            badgeDownloadCount.setText(String.valueOf(formatter.format(downloadAndRating.getDownloadCount())));
        }

        if (downloadAndRating.getRating() != null) {
            badgeRatingTotal.setText(String.format(getString(R.string.rating), String.valueOf(downloadAndRating.getRatingCount())));
            ratingTotal.setText("(" + downloadAndRating.getRatingCount() + ")");
            badgeRatingText.setText(String.valueOf(downloadAndRating.getRating()));
            ratingText.setText(String.valueOf(downloadAndRating.getRating()));
        }
    }

    @Override
    public void onGetDownloadAndRatingFailed() {
    }

    @Override
    public void onGetReviewListSuccessful(List<Review> reviewList) {
        this.reviewList = reviewList;
        currentReviews = 0;
        listReview.setLayoutManager(new LinearLayoutManager(getContext()));
        listReview.setNestedScrollingEnabled(false);

        populateReviews();
    }

    @OnClick(R.id.read_more)
    public void expandDescription() {
        if (readMore.getText().equals(getString(R.string.read_more))) {
            description.setMaxLines(description.getLineCount());
            readMore.setText(getString(R.string.read_less));
        } else {
            description.setMaxLines(3);
            readMore.setText(getString(R.string.read_more));
        }
    }

    @OnClick(R.id.read_more_reviews)
    void populateReviews() {
        if (reviewList.size() > 0) {
            reviewLayout.setVisibility(VISIBLE);
            if (reviewList.size() > currentReviews) {
                readMoreReviews.setVisibility(VISIBLE);
            }
        }

        currentReviews += 4;
        if (currentReviews > reviewList.size()) {
            currentReviews = reviewList.size();
        }

        if (currentReviews.equals(reviewList.size())) {
            readMoreReviews.setVisibility(GONE);
        }

        listOfReviews.clear();
        listOfReviews.addAll(reviewList.subList(0, currentReviews));
        reviewAdapter.notifyDataSetChanged();

    }

    @Override
    public void onGetReviewListFailed() {

    }

    @Override
    public void onGetFaqListSuccessful(List<Faq> faqList) {
        faqDivider.setVisibility(VISIBLE);
        faqLayout.setVisibility(VISIBLE);
        ToolInfoFaqAdapter adapter = new ToolInfoFaqAdapter(getContext(), new ArrayList<>(faqList));
        faqExpandableListView.setAdapter(adapter);
    }

    @Override
    public void onGetFaqListFailed() {
        faqDivider.setVisibility(GONE);
        faqLayout.setVisibility(GONE);
    }

    @OnClick({R.id.update_button, R.id.install_button})
    void installApplication() {
        /*
        if (updateButton.getVisibility() == VISIBLE) {
            toggleUpdateButton(false);
        } else {
            toggleInstallButton(false);
        }*/

        installApplication(version);
    }

    @Override
    public void onGetLocalizedInfoSuccessful(LocalizedInfo localizedInfo) {
        if (localizedInfo.getName().isEmpty()) {
            title.setText(version.getAppName());
        } else {
            title.setText(localizedInfo.getName());
            version.setAppName(localizedInfo.getName());
        }

        vendor.setText(localizedInfo.getCompany());

        String text = HtmlRenderer.builder().build().render(Parser.builder().build().parse(localizedInfo.getDescription()));
        text = text.replace("<li>", "<p>\u2022 ");
        text = text.replace("</li>", "</p>");
        text = text.replace("<p>", "<p>\u200F");

        String startText = "";
        if (localizedInfo.getLocale().equals("fa")) {
            startText = "\u200f";
        }

        if (text.isEmpty()) {
            descriptionDivider.setVisibility(GONE);
            descriptionLayout.setVisibility(GONE);
        } else {
            description.setText(startText + Html.fromHtml(text));
            description.setMovementMethod(LinkMovementMethod.getInstance());
            description.post(new Runnable() {
                @Override
                public void run() {
                    if (description != null) {
                        if (description.getLineCount() < 4) {
                            readMore.setVisibility(GONE);
                        } else {
                            description.setMaxLines(3);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onGetLocalizedInfoFailed() {
    }

    @Override
    public void onGetGuideListSuccessful(final List<Guide> guides) {
        guideDivider.setVisibility(VISIBLE);
        guideLayout.setVisibility(VISIBLE);
        ToolInfoGuideAdapter adapter = new ToolInfoGuideAdapter(getContext(), new ArrayList<>(guides));
        guideNonScrollListView.setAdapter(adapter);
        guideNonScrollListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), GuideActivity.class);
                intent.putExtra(GuideFragment.GUIDE, guides.get(i).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onGetGuideListFailed() {
        guideDivider.setVisibility(GONE);
        guideLayout.setVisibility(GONE);
    }

    @Override
    public void onGetToolSuccessful(Tool tool) {
        builder.setText(tool.getWebsite());
        supportLayout.setVisibility(VISIBLE);
    }

    @Override
    public void onGetToolFailed() {

    }

    @Override
    public void onGetCategoryNamesSuccessful(List<Name> categoryNames) {
        for (String category: version.getCategories()) {
            for (final Name categoryName: categoryNames) {
                if (categoryName.getCategoryId() == Integer.parseInt(category)) {
                    Button text = (Button) inflate(getContext(), R.layout.button_category, null);
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), CategoryListActivity.class);
                            intent.putExtra(CategoryListFragment.CATEGORY, Parcels.wrap(categoryName));
                            intent.putExtra(TYPE, CATEGORY);
                            getContext().startActivity(intent);
                        }
                    });
                    text.setAllCaps(false);
                    text.setText(categoryName.getFa());
                    if (categoryName.getFa().isEmpty()) {
                        text.setText(categoryName.getEn());
                    }
                    categories.addView(text);
                }
            }

        }
    }

    @Override
    public void onGetCategoryNamesFailed() {

    }

    @Override
    public void onGetVersionImagesSuccessful(Images images) {

        this.images = images;

        getVersionPicasso()
                .load(images.getLogo().isEmpty() ? null : images.getLogo().get(0).getUrl())
                .resize(400, 400)
                .centerInside()
                .into(icon);

        displayImages();

        if (images.getLogo().isEmpty() || images.getScreenshot().isEmpty()) {
            presenter.getToolImages(version.getToolId());
        }

    }

    @Override
    public void onGetVersionImagesFailed() {
    }

    @Override
    public void onGetToolImagesSuccessful(Images toolImages) {
        if (images.getLogo().isEmpty()) {
            getToolPicasso()
                    .load(toolImages.getLogo().isEmpty() ? null : toolImages.getLogo().get(0).getUrl())
                    .resize(400, 400)
                    .centerInside()
                    .into(icon);
        }

        if (images.getScreenshot().isEmpty()) {
            images.setScreenshot(toolImages.getScreenshot());
        }
    }

    @Override
    public void onGetToolImagesFailed() {

    }

    @Override
    public void onGetTutorialsSuccessful(List<Tutorial> tutorials) {
        guideDivider.setVisibility(VISIBLE);
        guideLayout.setVisibility(VISIBLE);
        ToolInfoTutorialAdapter adapter = new ToolInfoTutorialAdapter(getContext(), new ArrayList<>(tutorials));
        tutorialExpandableListView.setAdapter(adapter);
    }

    @Override
    public void onGetTutorialsFailed() {
        tutorialExpandableListView.setVisibility(GONE);
    }

    private void setUpRatingFeedback() {
        LayerDrawable stars = (LayerDrawable) ratingBarTool.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor(STAR_COLOUR), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.parseColor(STAR_COLOUR), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.parseColor(INACTIVE_STAR), PorterDuff.Mode.SRC_ATOP);

        ratingBarTool.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, final float rating, boolean fromUser) {
                if (fromUser && rating > 0 && !saveInstanceStateComplete) {
                    RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putFloat("RATING", rating);
                    bundle.putParcelable(VERSION, Parcels.wrap(version));
                    ratingDialogFragment.setArguments(bundle);
                    ratingDialogFragment.show(getActivity().getFragmentManager(), ratingDialogFragment.getClass().getName());

                    new RatingDialogPresenter(ratingDialogFragment, new AmazonRepository(getContext().getApplicationContext()));

                    ratingBar.setRating(-1);
                }
            }
        });
    }

    private void updateButtons() {
        toggleInstallButton(false);

        playstoreButton.setBackgroundResource(R.drawable.button_regular_disabled);
        playstoreButton.setTextColor(ContextCompat.getColor(getContext(), R.color.buttonGrey));
        playstoreButton.setEnabled(false);

        if (isGooglePlayStoreUrl(version)) {
            playstoreButton.setEnabled(true);
            playstoreButton.setBackgroundResource(R.drawable.button_regular);
            playstoreButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }

        updateButton.setVisibility(GONE);
        version.setUpdateAvailable(false);
        version.setInstalled(false);

        try {
            int installedVersionCode = getContext().getPackageManager().getPackageInfo(version.getPackageName(), 0).versionCode;
            version.setInstalled(true);
            if (version.getVersionCode() > installedVersionCode) {
                version.setUpdateAvailable(true);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (!version.isInstalled()) {
            String s3Url = version.getDownloadVia().getS3();
            String s3FileExtension = s3Url != null && s3Url.startsWith("https://s3.amazonaws.com/" + Constants.BUCKET_NAME) ? FileViewer.getFileExtension(version) : null;

            if ((s3FileExtension != null && !s3FileExtension.isEmpty()) || (!isGooglePlayStoreUrl(version))) {
                toggleInstallButton(true);
            }
        } else {
            if (version.isUpdateAvailable()) {
                installButton.setVisibility(GONE);
                updateButton.setVisibility(VISIBLE);
                toggleUpdateButton(true);
            }
        }
    }

    @OnClick(R.id.play_store_button)
    public void playStoreRedirect() {
        super.playStoreRedirect(version);
    }

    /**
     * Send email to mail responder email to get response email with apk.
     */
    @OnClick(R.id.email)
    public void sendResponderEmail() {
        ActivityUtils.sendEmail(version.getDownloadVia().getEmail(), getString(R.string.via_email), getString(R.string.via_email_subject), getActivity());

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
        bundle.putString(TOOL_ID, version.getAppName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.EMAIL, bundle);
    }

    @OnClick(R.id.app_support)
    public void sendSupportEmail() {
        ActivityUtils.sendEmail(getString(R.string.support_email), "", "", getActivity());

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
        bundle.putString(TOOL_ID, version.getAppName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.EMAIL, bundle);
    }

    @OnClick(R.id.telegram_support)
    public void telegramSupport() {
        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse(getString(R.string.support_telegram)));
        startActivity(telegram);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, ToolInfoFragment.TAG);
        bundle.putString(TOOL_ID, version.getAppName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.SUPPORT, bundle);
    }

    /**
     * Provide share deep link URL through ACTION_SEND intent.
     */
    @OnClick(R.id.to_top)
    public void goToTop() {
        scrollView.smoothScrollTo(0, 0);
    }

    @NonNull
    protected static OkHttpClient getToolPicassoClient() {
        Interceptor addOuinetGroup = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request oldRequest = chain.request();
                // Encoded OkHttp paths always start with a slash.
                String resourcePath = pathComponent(oldRequest.url().encodedPath().substring(1));
                Request newRequest = oldRequest
                        .newBuilder()
                        .addHeader("X-Ouinet-Group", resourcePath)
                        .build();
                return chain.proceed(newRequest);
            }
        };

        return PaskoochehApplication.getInstance()
            .getOkHttpClientBuilder(addOuinetGroup)
            .build();
    }

    @NonNull
    protected static String pathComponent(String pathResource) {
        /*
        Check if pathResource starts with BUCKET_NAME(paskoocheh-repo or paskoocheh-dev|staging-storage),
        if not concatenate with BUCKET_NAME
        */
        if (!pathResource.startsWith(BUCKET_NAME)) {
            pathResource = BUCKET_NAME + "/" + pathResource;
        }
        /*Remove the filename from the path
         */
        int pos = pathResource.lastIndexOf('/');
        if (pos > -1) {
            pathResource = pathResource.substring(0, pos);
        }

        return pathResource;
    }

    @NonNull
    protected Picasso getToolPicasso() {
        // This just uses the generic image group for tool logos
        // (the same ones used in the tool list).
        synchronized (this) {
            if (toolPicasso == null)
                toolPicasso = new Picasso.Builder(getContext())
                    .downloader(new OkHttp3Downloader(getToolPicassoClient()))
                    .build();
        }
        return toolPicasso;
    }

    @NonNull
    protected OkHttpClient getVersionPicassoClient() {
        Interceptor addOuinetGroup = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request()
                        .newBuilder()
                        .addHeader("X-Ouinet-Group", getVersionOuinetGroup())
                        .build();
                return chain.proceed(newRequest);
            }
        };

        return PaskoochehApplication.getInstance()
            .getOkHttpClientBuilder(addOuinetGroup)
            .build();
    }

    @NonNull
    protected String getVersionOuinetGroup() {
        if (version == null)
            return new String();  // TODO: can this happen?

        String s3KeyDir = version.getS3Key().toString();
        /*Remove the filename from the path
         */
        int pos = s3KeyDir.lastIndexOf('/');
        if (pos > -1) {
            s3KeyDir = s3KeyDir.substring(0, pos);
        }

        return version.getS3Bucket().toString()
            + s3KeyDir
            // Not including this (in contrast with version APK URLs) allows
            // sharing screenshots across versions with different version codes,
            // but it doubles the number of groups to be announced.
            + ";version_code=" + version.versionCode.toString()
            ;
    }

    @NonNull
    protected Picasso getVersionPicasso() {
        synchronized (this) {
            if (versionPicasso == null)
                versionPicasso = new Picasso.Builder(getContext())
                    .downloader(new OkHttp3Downloader(getVersionPicassoClient()))
                    .build();
        }
        return versionPicasso;
    }

    private void displayImages() {
        final ArrayList<Image> localImages = new ArrayList<>(images.getScreenshot());
        if (images.screenshot.toArray().length > 0) {
            screenshotLayout.setVisibility(VISIBLE);
            for (int i = 0; i < localImages.size(); i++) {

                final Integer position = i;
                final ImageView imageView = new ImageView(getContext());

                int padding = (int) (5 * getResources().getDisplayMetrics().density);
                imageView.setPadding(padding, padding, padding, padding);
                getVersionPicasso()
                        .load(localImages.get(i).getUrl())
                        .resize(600, 600)
                        .centerInside()
                        .into(imageView);
                screenshotInnerLayout.addView(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.SCREEN, TAG);
                        bundle.putString(TOOL_ID, version.getAppName());
                        FirebaseAnalytics.getInstance(getContext()).logEvent(Constants.GALLERY, bundle);

                        Intent intent = new Intent(getActivity(), GalleryActivity.class);
                        intent.putExtra(POSITION, position);
                        intent.putExtra(IMAGES, Parcels.wrap(localImages));
                        intent.putExtra(OUINET_GROUP, getVersionOuinetGroup());
                        startActivity(intent);
                    }
                });
            }
            horizontalScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    horizontalScrollView.fullScroll(View.FOCUS_RIGHT);
                }
            });
            horizontalScrollView.fullScroll(View.FOCUS_RIGHT);
            toolBody.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
