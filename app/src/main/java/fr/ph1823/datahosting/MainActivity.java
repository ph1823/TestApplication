package fr.ph1823.datahosting;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.applovin.mediation.AppLovinExtras;
import com.applovin.mediation.ApplovinAdapter;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.chartboost.sdk.Chartboost;
import com.facebook.ads.AudienceNetworkAds;
import com.google.ads.mediation.adcolony.AdColonyMediationAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.ads.mediation.inmobi.InMobiAdapter;
import com.google.ads.mediation.inmobi.InMobiConsent;
import com.google.ads.mediation.inmobi.InMobiNetworkKeys;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.inmobi.sdk.InMobiSdk;
import com.ironsource.mediationsdk.IronSource;
import com.jirbo.adcolony.AdColonyAdapter;
import com.jirbo.adcolony.AdColonyBundleBuilder;
import com.my.target.common.MyTargetPrivacy;
import com.vungle.mediation.VungleAdapter;
import com.vungle.mediation.VungleConsent;
import com.vungle.mediation.VungleExtrasBuilder;
import com.vungle.mediation.VungleInterstitialAdapter;
import com.vungle.warren.AdConfig;
import com.vungle.warren.Vungle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import theoremreach.com.theoremreach.TheoremReach;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppLovinSdk.initializeSdk(this);

        //Vungle consent EU
        VungleConsent.updateConsentStatus(Vungle.Consent.OPTED_IN,"1.0.0");

        //Set mytarget consent
        MyTargetPrivacy.setUserConsent(true);
        //TapJoy consent

        JSONObject consentObject = new JSONObject();
        try {
            consentObject.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true);
            consentObject.put("gdpr", "1");
        } catch (JSONException exception) {
            return;
        }

        InMobiConsent.updateGDPRConsent(consentObject);

        //AppLovin consent
        AppLovinPrivacySettings.setHasUserConsent(true, this);


        //Adcolony consent
        AdColonyAppOptions appOptions = AdColonyMediationAdapter.getAppOptions();
        //appOptions.setMediationNetwork();
        appOptions.setTestModeEnabled(false);

        //User consent
        appOptions
                .setPrivacyFrameworkRequired(AdColonyAppOptions.GDPR, true)
                .setPrivacyConsentString(AdColonyAppOptions.GDPR, "1");

        AdColonyBundleBuilder.setShowPrePopup(false);
        AdColonyBundleBuilder.setShowPostPopup(false);

        String username = "test";
        appOptions.setUserID(username);
        // Configure AdColony in your launching Activity's onCreate() method so that cached ads can
        // be available as soon as possible.
        AdColony.configure(this.getApplication(),  appOptions,         // activity this
                "app8c78bed759d14e0190"); // list of all your zones set up on the AdColony Dashboard*/

        AppLovinSdk.initializeSdk(this);
        /* IronSource load */
        IronSource.setConsent(true);
        IronSource.setUserId(username);
        IronSource.init(this, "97136205", IronSource.AD_UNIT.OFFERWALL);

        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);

        //Chartboost.setCustomId(username);
        Chartboost.startWithAppId(this, "5d6e6a800fb6b40b25516f28", "edac90d9137ea6139b34e8c7be7c63cc46abf043", (start) -> {});

        //MobfoxSDK.init(this);
        //MobfoxSDK.setCOPPA(false);

        TheoremReach.initWithApiKeyAndUserIdAndActivityContext("2107314bef6c78ff09b096c12290", "16312", this);
        AudienceNetworkAds.initialize(this);

        Bundle inmobi = new Bundle();
        inmobi.putString(InMobiNetworkKeys.COUNTRY, "France");
        //AppLovin extra
        Bundle applovin = new AppLovinExtras.Builder()
                .setMuteAudio(true)
                .build();

        Bundle vungle = new VungleExtrasBuilder(new String[]{"VIDEOR-5228935", "5_DATACOINS-1027247", "DATACOINS_0_20-1837980", "DATACOINS_0_10-8448456", "BANNER1-8916693", "BANNER2-6191888", "BANNER3-2866692", "BANNER4-1632444", "INTERT-2180951", "RANDOMREWARD-9187800"})
                .setUserId(username)
                .setAdOrientation(AdConfig.AUTO_ROTATE)
                .build();

        MobileAds.initialize(this, initializationStatus -> {
            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for (String adapterClass : statusMap.keySet()) {
                AdapterStatus status = statusMap.get(adapterClass);


                assert status != null;
                Log.d("DataHosting", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d State %s",
                        adapterClass, status.getDescription(), status.getLatency(), status.getInitializationState()));
            }
        });

        /*Bundle tapjoy = new TapjoyAdapter.TapjoyExtrasBundleBuilder()
                .setDebug(false)
                .build();*/

       /* Bundle chartboost = new ChartboostAdapter.ChartboostExtrasBundleBuilder()
                .setFramework(Chartboost.CBFramework.CBFrameworkOther, "1.2.3")
                .build();*/

        AdColonyMediationAdapter adapter = new AdColonyMediationAdapter();
        VersionInfo adapterVersion = adapter.getVersionInfo();
        VersionInfo sdkVersion = adapter.getSDKVersionInfo();
// Log the adapter patch version to 3 digits to represent the x.x.x.x versioning
// used by adapters.
        Log.d("TAG", String.format(
                "Adapter version: %d.%d.%03d",
                adapterVersion.getMajorVersion(),
                adapterVersion.getMinorVersion(),
                adapterVersion.getMicroVersion()));
        Log.d("TAG", String.format(
                "SDK version: %d.%d.%d",
                sdkVersion.getMajorVersion(),
                sdkVersion.getMinorVersion(),
                sdkVersion.getMicroVersion()));

        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(InMobiAdapter.class, inmobi)
                .addNetworkExtrasBundle(VungleAdapter.class, vungle)// Rewarded video.
                .addNetworkExtrasBundle(VungleInterstitialAdapter.class, vungle) // Interstitial.
                .addNetworkExtrasBundle(ApplovinAdapter.class, applovin)
                .addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
                .addNetworkExtrasBundle(FacebookMediationAdapter.class, new Bundle())
                .build();
    }


}