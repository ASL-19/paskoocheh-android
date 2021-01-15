package org.asl19.paskoocheh.toolinfo;


import org.asl19.paskoocheh.data.source.DownloadAndRatingDataSource;
import org.asl19.paskoocheh.data.source.FaqDataSource;
import org.asl19.paskoocheh.data.source.GuideDataSource;
import org.asl19.paskoocheh.data.source.ImagesDataSource;
import org.asl19.paskoocheh.data.source.LocalizedInfoDataSource;
import org.asl19.paskoocheh.data.source.NameDataSource;
import org.asl19.paskoocheh.data.source.ReviewDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.data.source.TutorialDataSource;
import org.asl19.paskoocheh.data.source.VersionDataSource;
import org.asl19.paskoocheh.pojo.DownloadAndRating;
import org.asl19.paskoocheh.pojo.Faq;
import org.asl19.paskoocheh.pojo.Guide;
import org.asl19.paskoocheh.pojo.Images;
import org.asl19.paskoocheh.pojo.LocalizedInfo;
import org.asl19.paskoocheh.pojo.Name;
import org.asl19.paskoocheh.pojo.Review;
import org.asl19.paskoocheh.pojo.Tool;
import org.asl19.paskoocheh.pojo.Tutorial;
import org.asl19.paskoocheh.pojo.Version;

import java.util.List;

public class ToolInfoPresenter implements ToolInfoContract.Presenter {

    private ToolInfoContract.ToolInfoView toolInfoView;
    private VersionDataSource versionRepository;
    private DownloadAndRatingDataSource downloadCountDataRepository;
    private ReviewDataSource reviewRepository;
    private FaqDataSource faqRepository;
    private LocalizedInfoDataSource localizedInfoRepository;
    private GuideDataSource guideRepository;
    private ToolDataSource toolRepository;
    private NameDataSource nameRepository;
    private ImagesDataSource imageRepository;
    private TutorialDataSource tutorialRepository;


    public ToolInfoPresenter(
            ToolInfoContract.ToolInfoView toolInfoView,
            VersionDataSource versionRepository,
            DownloadAndRatingDataSource downloadAndRatingRepository,
            ReviewDataSource reviewRepository,
            FaqDataSource faqRepository,
            LocalizedInfoDataSource localizedInfoRepository,
            GuideDataSource guideRepository,
            ToolDataSource toolRepository,
            NameDataSource nameRepository,
            ImagesDataSource imageRepository,
            TutorialDataSource tutorialRepository

    ) {
        this.toolInfoView = toolInfoView;
        this.versionRepository = versionRepository;
        this.downloadCountDataRepository = downloadAndRatingRepository;
        this.reviewRepository = reviewRepository;
        this.faqRepository = faqRepository;
        this.localizedInfoRepository = localizedInfoRepository;
        this.guideRepository = guideRepository;
        this.toolRepository = toolRepository;
        this.nameRepository = nameRepository;
        this.imageRepository = imageRepository;
        this.tutorialRepository = tutorialRepository;

        this.toolInfoView.setPresenter(this);
    }

    @Override
    public void getVersion(long versionId) {
        versionRepository.getVersion(versionId, new VersionDataSource.GetVersionCallback() {
            @Override
            public void onGetVersionSuccessful(Version version) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetVersionSuccessful(version);
                }
            }

            @Override
            public void onGetVersionFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetVersionFailed();
                }
            }
        });
    }

    public void getDownloadAndRating(long toolId) {
        downloadCountDataRepository.getToolDownloadAndRatings(toolId, new DownloadAndRatingDataSource.GetToolDownloadAndRatingListCallback() {
            @Override
            public void onGetDownloadAndRatingListSuccessful(DownloadAndRating downloadAndRating) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetDownloadAndRatingSuccessfull(downloadAndRating);
                }
            }

            @Override
            public void onGetDownloadAndRatingListFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetDownloadAndRatingFailed();
                }
            }
        });
    }

    @Override
    public void getToolReviews(int toolId) {
        reviewRepository.getReviewList(toolId, new ReviewDataSource.GetReviewListCallback() {
            @Override
            public void onGetReviewsSuccessful(List<Review> reviewList) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetReviewListSuccessful(reviewList);
                }
            }

            @Override
            public void onGetReviewsFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetReviewListFailed();
                }
            }
        });
    }

    @Override
    public void getFaqList(final int toolId) {
        faqRepository.getToolFaqs(toolId, new FaqDataSource.GetFaqListCallback() {
            @Override
            public void onGetFaqsSuccessful(List<Faq> faqList) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetFaqListSuccessful(faqList);
                }
            }

            @Override
            public void onGetFaqsFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetFaqListFailed();
                }
            }
        });
    }

    @Override
    public void getLocalizedInfo(int toolId) {
        localizedInfoRepository.getLocalizedInfo(toolId, new LocalizedInfoDataSource.GetLocalizedInfoCallback() {
            @Override
            public void onGetLocalizedInfoSuccessful(LocalizedInfo localizedInfo) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetLocalizedInfoSuccessful(localizedInfo);
                }
            }

            @Override
            public void onGetLocalizedInfoFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetLocalizedInfoFailed();
                }
            }
        });
    }

    @Override
    public void getGuideList(int toolId) {
        guideRepository.getToolGuides(toolId, new GuideDataSource.GetGuideListCallback() {
            @Override
            public void onGetGuidesSuccessful(List<Guide> guideList) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetGuideListSuccessful(guideList);
                }
            }

            @Override
            public void onGetGuidesFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetGuideListFailed();
                }
            }
        });
    }

    @Override
    public void getTool(int toolId) {
        toolRepository.getTool(toolId, new ToolDataSource.GetToolCallback() {
            @Override
            public void onGetToolSuccessful(Tool tool) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetToolSuccessful(tool);
                }
            }

            @Override
            public void onGetToolFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetToolFailed();
                }
            }
        });
    }

    @Override
    public void getCategoryNames() {
        nameRepository.getNames(new NameDataSource.GetNameListCallback() {
            @Override
            public void onGetNamesSuccessful(List<Name> names) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetCategoryNamesSuccessful(names);
                }
            }

            @Override
            public void onGetNamesFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetCategoryNamesFailed();
                }
            }
        });
    }

    @Override
    public void getVersionImages(int versionId) {
        imageRepository.getVersionImages(versionId, new ImagesDataSource.GetImagesCallback() {
            @Override
            public void onGetImagesSuccessful(Images images) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetVersionImagesSuccessful(images);
                }
            }

            @Override
            public void onGetImagesFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetVersionImagesFailed();
                }
            }
        });
    }

    @Override
    public void getToolImages(int toolId) {
        imageRepository.getToolImages(toolId, new ImagesDataSource.GetImagesCallback() {
            @Override
            public void onGetImagesSuccessful(Images images) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetToolImagesSuccessful(images);
                }
            }

            @Override
            public void onGetImagesFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetToolImagesFailed();
                }
            }
        });
    }

    @Override
    public void getTutorials(int toolId) {
        tutorialRepository.getToolTutorial(toolId, new TutorialDataSource.GetTutorialListCallback() {
            @Override
            public void onGetTutorialsSuccessful(List<Tutorial> tutorials) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetTutorialsSuccessful(tutorials);
                }
            }

            @Override
            public void onGetTutorialsFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetTutorialsFailed();
                }
            }
        });
    }
}
