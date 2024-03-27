package com.aefyr.sai.installerx.resolver.meta.impl;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.asl19.paskoocheh.R;
import com.aefyr.sai.installerx.common.Category;
import com.aefyr.sai.installerx.common.MutableSplitPart;
import com.aefyr.sai.installerx.common.ParserContext;
import com.aefyr.sai.installerx.common.SplitApkSourceMeta;
import com.aefyr.sai.installerx.postprocessing.Postprocessor;
import com.aefyr.sai.installerx.resolver.appmeta.AppMeta;
import com.aefyr.sai.installerx.resolver.appmeta.AppMetaExtractor;
import com.aefyr.sai.installerx.resolver.meta.ApkSourceFile;
import com.aefyr.sai.installerx.resolver.meta.ApkSourceMetaResolutionError;
import com.aefyr.sai.installerx.resolver.meta.ApkSourceMetaResolutionResult;
import com.aefyr.sai.installerx.resolver.meta.Notice;
import com.aefyr.sai.installerx.resolver.meta.SplitApkSourceMetaResolver;
import com.aefyr.sai.installerx.splitmeta.BaseSplitMeta;
import com.aefyr.sai.installerx.splitmeta.FeatureSplitMeta;
import com.aefyr.sai.installerx.splitmeta.SplitMeta;
import com.aefyr.sai.installerx.splitmeta.config.AbiConfigSplitMeta;
import com.aefyr.sai.installerx.splitmeta.config.LocaleConfigSplitMeta;
import com.aefyr.sai.installerx.splitmeta.config.ScreenDestinyConfigSplitMeta;
import com.aefyr.sai.installerx.util.AndroidBinXmlParser;
import com.aefyr.sai.utils.IOUtils;
import com.aefyr.sai.utils.Stopwatch;
import com.aefyr.sai.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultSplitApkSourceMetaResolver implements SplitApkSourceMetaResolver {
    private static final String TAG = "DSASMetaResolver";

    private static final String MANIFEST_FILE = "AndroidManifest.xml";

    public static final String NOTICE_TYPE_NO_XAPK_OBB_SUPPORT = "Notice.DefaultSplitApkSourceMetaResolver.NoXApkObbSupport";

    private final Context mContext;
    private final AppMetaExtractor mAppMetaExtractor;
    private final List<Postprocessor> mPostprocessors = new ArrayList<>();

    public DefaultSplitApkSourceMetaResolver(Context context, AppMetaExtractor appMetaExtractor) {
        mContext = context.getApplicationContext();
        mAppMetaExtractor = appMetaExtractor;
    }

    public void addPostprocessor(Postprocessor postprocessor) {
        mPostprocessors.add(postprocessor);
    }

    @Override
    public ApkSourceMetaResolutionResult resolveFor(ApkSourceFile apkSourceFile) throws Exception {
        Stopwatch sw = new Stopwatch();

        try {
            ApkSourceMetaResolutionResult result = parseViaParsingManifests(apkSourceFile);
            Log.d(TAG, String.format("Resolved meta for %s via parsing manifests in %d ms.", apkSourceFile.getName(), sw.millisSinceStart()));
            return result;
        } catch (Exception e) {
            //TODO alt parse
            throw e;
        }
    }

    private ApkSourceMetaResolutionResult parseViaParsingManifests(ApkSourceFile aApkSourceFile) throws Exception {
        try (ApkSourceFile apkSourceFile = aApkSourceFile) {
            String packageName = null;
            String versionName = null;
            Long versionCode = null;
            boolean seenApk = false;
            boolean seenBaseApk = false;
            boolean seenObb = false;

            ParserContext parserContext = new ParserContext();

            ApkSourceFile.Entry baseApkEntry = null;
            for (ApkSourceFile.Entry entry : apkSourceFile.listEntries()) {
                if (!entry.getName().toLowerCase().endsWith(".apk")) {

                    if ("xapk".equals(Utils.getExtension(apkSourceFile.getName()))
                            && entry.getName().toLowerCase().endsWith(".obb")
                            && !seenObb) {

                        seenObb = true;

                        parserContext.addNotice(new Notice(NOTICE_TYPE_NO_XAPK_OBB_SUPPORT, null, getString(R.string.installerx_notice_xapk)));
                    }

                    continue;
                }


                seenApk = true;
                boolean seenManifestElement = false;

                HashMap<String, String> manifestAttrs = new HashMap<>();

                ByteBuffer manifestBytes = stealManifestFromApk(apkSourceFile.openEntryInputStream(entry));
                if (manifestBytes == null)
                    return createErrorResult(R.string.installerx_dsas_meta_resolver_error_no_manifest, true);

                AndroidBinXmlParser parser = new AndroidBinXmlParser(manifestBytes);
                int eventType = parser.getEventType();
                while (eventType != AndroidBinXmlParser.EVENT_END_DOCUMENT) {

                    if (eventType == AndroidBinXmlParser.EVENT_START_ELEMENT) {
                        if (parser.getName().equals("manifest") && parser.getDepth() == 1 && parser.getNamespace().isEmpty()) {
                            if (seenManifestElement)
                                return createErrorResult(R.string.installerx_dsas_meta_resolver_error_dup_manifest_entry, true);

                            seenManifestElement = true;

                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                if (parser.getAttributeName(i).isEmpty())
                                    continue;

                                String namespace = "" + (parser.getAttributeNamespace(i).isEmpty() ? "" : (parser.getAttributeNamespace(i) + ":"));

                                manifestAttrs.put(namespace + parser.getAttributeName(i), parser.getAttributeStringValue(i));
                            }
                        }
                    }


                    eventType = parser.next();
                }

                if (!seenManifestElement)
                    return createErrorResult(R.string.installerx_dsas_meta_resolver_error_no_manifest_entry, true);

                SplitMeta splitMeta = SplitMeta.from(manifestAttrs);
                if (packageName == null) {
                    packageName = splitMeta.packageName();
                } else {
                    if (!packageName.equals(splitMeta.packageName()))
                        return createErrorResult(R.string.installerx_dsas_meta_resolver_error_pkg_mismatch, true);
                }
                if (versionCode == null) {
                    versionCode = splitMeta.versionCode();
                } else {
                    if (!versionCode.equals(splitMeta.versionCode()))
                        return createErrorResult(R.string.installerx_dsas_meta_resolver_error_version_mismatch, true);
                }

                if (splitMeta instanceof BaseSplitMeta) {
                    if (seenBaseApk)
                        return createErrorResult(R.string.installerx_dsas_meta_resolver_error_multiple_base_apks, true);

                    seenBaseApk = true;
                    baseApkEntry = entry;

                    BaseSplitMeta baseSplitMeta = (BaseSplitMeta) splitMeta;
                    versionName = baseSplitMeta.versionName();
                    parserContext.getOrCreateCategory(Category.BASE_APK, getString(R.string.installerx_category_base_apk), null)
                            .addPart(new MutableSplitPart(splitMeta, entry.getName(), entry.getLocalPath(), baseSplitMeta.packageName(), entry.getSize(), Utils.formatSize(mContext, entry.getSize()), true, true));

                    continue;
                }

                if (splitMeta instanceof FeatureSplitMeta) {
                    FeatureSplitMeta featureSplitMeta = (FeatureSplitMeta) splitMeta;

                    parserContext.getOrCreateCategory(Category.FEATURE, getString(R.string.installerx_category_dynamic_features), null)
                            .addPart(new MutableSplitPart(splitMeta, entry.getName(), entry.getLocalPath(), getString(R.string.installerx_dynamic_feature, featureSplitMeta.module()), entry.getSize(), Utils.formatSize(mContext, entry.getSize()), false, true));
                    continue;
                }

                if (splitMeta instanceof AbiConfigSplitMeta) {
                    AbiConfigSplitMeta abiConfigSplitMeta = (AbiConfigSplitMeta) splitMeta;

                    String name;
                    if (abiConfigSplitMeta.isForModule()) {
                        name = getString(R.string.installerx_split_config_abi_for_module, abiConfigSplitMeta.abi(), abiConfigSplitMeta.module());
                    } else {
                        name = getString(R.string.installerx_split_config_abi_for_base, abiConfigSplitMeta.abi());
                    }

                    parserContext.getOrCreateCategory(Category.CONFIG_ABI, getString(R.string.installerx_category_config_abi), null)
                            .addPart(new MutableSplitPart(splitMeta, entry.getName(), entry.getLocalPath(), name, entry.getSize(), Utils.formatSize(mContext, entry.getSize()), false, false));
                    continue;
                }

                if (splitMeta instanceof LocaleConfigSplitMeta) {
                    LocaleConfigSplitMeta localeConfigSplitMeta = (LocaleConfigSplitMeta) splitMeta;

                    String name;
                    if (localeConfigSplitMeta.isForModule()) {
                        name = getString(R.string.installerx_split_config_locale_for_module, localeConfigSplitMeta.locale().getDisplayName(), localeConfigSplitMeta.module());
                    } else {
                        name = getString(R.string.installerx_split_config_locale_for_base, localeConfigSplitMeta.locale().getDisplayName());
                    }

                    parserContext.getOrCreateCategory(Category.CONFIG_LOCALE, getString(R.string.installerx_category_config_locale), null)
                            .addPart(new MutableSplitPart(splitMeta, entry.getName(), entry.getLocalPath(), name, entry.getSize(), Utils.formatSize(mContext, entry.getSize()), false, false));
                    continue;
                }

                if (splitMeta instanceof ScreenDestinyConfigSplitMeta) {
                    ScreenDestinyConfigSplitMeta screenDestinyConfigSplitMeta = (ScreenDestinyConfigSplitMeta) splitMeta;

                    String name;
                    if (screenDestinyConfigSplitMeta.isForModule()) {
                        name = getString(R.string.installerx_split_config_dpi_for_module, screenDestinyConfigSplitMeta.densityName(), screenDestinyConfigSplitMeta.density(), screenDestinyConfigSplitMeta.module());
                    } else {
                        name = getString(R.string.installerx_split_config_dpi_for_base, screenDestinyConfigSplitMeta.densityName(), screenDestinyConfigSplitMeta.density());
                    }

                    parserContext.getOrCreateCategory(Category.CONFIG_DENSITY, getString(R.string.installerx_category_config_dpi), null)
                            .addPart(new MutableSplitPart(splitMeta, entry.getName(), entry.getLocalPath(), name, entry.getSize(), Utils.formatSize(mContext, entry.getSize()), false, false));
                    continue;
                }

                parserContext.getOrCreateCategory(Category.UNKNOWN, getString(R.string.installerx_category_unknown), null)
                        .addPart(new MutableSplitPart(splitMeta, entry.getName(), entry.getLocalPath(), splitMeta.splitName(), entry.getSize(), Utils.formatSize(mContext, entry.getSize()), false, true));

            }

            if (!seenApk)
                return createErrorResult(R.string.installerx_dsas_meta_resolver_error_no_apks, true);

            if (!seenBaseApk)
                return createErrorResult(R.string.installerx_dsas_meta_resolver_error_no_base_apk, true);


            AppMeta appMeta = mAppMetaExtractor.extract(apkSourceFile, baseApkEntry);
            if (appMeta == null)
                appMeta = new AppMeta();

            appMeta.packageName = packageName;
            appMeta.versionCode = versionCode;
            if (versionName != null)
                appMeta.versionName = versionName;

            parserContext.setAppMeta(appMeta);

            for (Postprocessor postprocessor : mPostprocessors)
                postprocessor.process(parserContext);


            return ApkSourceMetaResolutionResult.success(new SplitApkSourceMeta(parserContext.getAppMeta(), parserContext.sealCategories(), Collections.emptyList(), parserContext.getNotices()));
        }
    }

    private ApkSourceMetaResolutionResult createErrorResult(@StringRes int message, boolean shouldTryToInstallAnyway) {
        return ApkSourceMetaResolutionResult.failure(new ApkSourceMetaResolutionError(getString(message), shouldTryToInstallAnyway));
    }

    private String getString(@StringRes int id) {
        return mContext.getString(id);
    }

    private String getString(@StringRes int id, Object... formatArgs) {
        return mContext.getString(id, formatArgs);
    }

    @Nullable
    private ByteBuffer stealManifestFromApk(InputStream apkInputSteam) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(apkInputSteam)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.getName().equals(MANIFEST_FILE)) {
                    zipInputStream.closeEntry();
                    continue;
                }


                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                IOUtils.copyStream(zipInputStream, buffer);
                return ByteBuffer.wrap(buffer.toByteArray());
            }
        }

        return null;
    }

}
