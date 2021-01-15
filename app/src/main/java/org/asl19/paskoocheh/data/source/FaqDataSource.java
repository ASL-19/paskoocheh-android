package org.asl19.paskoocheh.data.source;


import androidx.annotation.NonNull;

import org.asl19.paskoocheh.pojo.Faq;

import java.util.List;

public interface FaqDataSource {

    interface GetFaqListCallback {

        void onGetFaqsSuccessful(List<Faq> faqList);

        void onGetFaqsFailed();
    }

    void getToolFaqs(int toolId, GetFaqListCallback callback);

    void saveFaqs(@NonNull final Faq... faqs);

    void clearTable();
}