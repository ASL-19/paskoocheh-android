package org.asl19.paskoocheh.toolinfo;


import org.asl19.paskoocheh.data.GetReviewRequest;
import org.asl19.paskoocheh.data.source.DownloadCountDataSource;
import org.asl19.paskoocheh.data.source.RatingDataSource;
import org.asl19.paskoocheh.data.source.ReviewDataSource;
import org.asl19.paskoocheh.data.source.ToolDataSource;
import org.asl19.paskoocheh.pojo.AndroidTool;
import org.asl19.paskoocheh.pojo.ReviewList;

public class ToolInfoPresenter implements ToolInfoContract.Presenter{

    private ToolInfoContract.ToolInfoView toolInfoView;
    private ToolDataSource toolRepository;
    private RatingDataSource ratingRepository;
    private DownloadCountDataSource downloadCountDataRepository;
    private ReviewDataSource reviewRepository;

    public ToolInfoPresenter(
            ToolInfoContract.ToolInfoView toolInfoView,
            ToolDataSource toolRepository,
            RatingDataSource ratingRepository,
            DownloadCountDataSource downloadCountRepository,
            ReviewDataSource reviewRepository
    ) {
        this.toolInfoView = toolInfoView;
        this.toolRepository = toolRepository;
        this.ratingRepository = ratingRepository;
        this.downloadCountDataRepository = downloadCountRepository;
        this.reviewRepository = reviewRepository;

        this.toolInfoView.setPresenter(this);
    }

    @Override
    public void getTool(long toolId) {
        toolRepository.getTool(toolId, new ToolDataSource.GetToolCallback() {
            @Override
            public void onGetToolSuccessful(AndroidTool tool) {
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
    public void getRating(String toolName) {
        ratingRepository.getRating(toolName, new RatingDataSource.GetRatingCallback() {
            @Override
            public void onGetRatingSuccessful(String rating) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetRatingSuccessful(rating);
                }
            }

            @Override
            public void onGetRatingFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetRatingFailed();
                }
            }
        });
    }

    @Override
    public void getDownloadCount(String toolName) {
        downloadCountDataRepository.getDownloadCount(toolName, new DownloadCountDataSource.GetDownloadCountCallback() {
            @Override
            public void onGetDownlaodCountSuccessful(String count) {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetDownloadCountSuccessful(count);
                }
            }

            @Override
            public void onGetDownloadCountFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onGetDownlaodCountFailed();
                }
            }
        });
    }

    @Override
    public void getToolReviews(GetReviewRequest getReviewRequest) {
        reviewRepository.getReviewList(getReviewRequest, new ReviewDataSource.GetReviewListCallback() {
            @Override
            public void onGetReviewsSuccessful(ReviewList reviewList) {
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
    public void registerInstall(final String tool, String uuid) {
        downloadCountDataRepository.registerInstall(tool, uuid, new DownloadCountDataSource.RegisterInstallCallback() {
            @Override
            public void onRegisterInstallSuccessful() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onRegisterInstallSuccessful();
                }
            }

            @Override
            public void onRegisterInstallFailed() {
                if (toolInfoView.isActive()) {
                    toolInfoView.onRegisterInstallFailed();
                }
            }
        });
    }
}
