package com.sdhacks.flyerscanner;

import android.content.Context;
import android.os.StrictMode;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.comprehend.AmazonComprehendClient;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.KeyPhrase;

import java.util.List;

class NLP {
    static List<KeyPhrase> NLP(String text, Context currContext){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        //String text = "The event starts at 6:00 PM on Friday October 25th";

        String accessKey = currContext.getResources().getString(R.string.access_key);
        String secretKey = currContext.getResources().getString(R.string.secret_key);

        //System.setProperty("aws.accessKeyId", accessKey); // DO NOT COMMIT
        //System.setProperty("aws.secretKey", secretKey); // DO NOT COMMIT

        // Create credentials using a provider chain. For more information, see
        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonComprehendClient comprehendClient = new AmazonComprehendClient(credentials);

        //AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();

        //AmazonComprehend comprehendClient =
        //        AmazonComprehendClientBuilder.standard()
        //               .withCredentials(awsCreds)
        //                .withRegion("region")
        //                .build();

        // Call detectKeyPhrases API
        System.out.println("Calling DetectKeyPhrases");
        DetectKeyPhrasesRequest detectKeyPhrasesRequest = new DetectKeyPhrasesRequest().withText(text)
                .withLanguageCode("en");
        DetectKeyPhrasesResult detectKeyPhrasesResult = comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest);
        List<KeyPhrase> keys = detectKeyPhrasesResult.getKeyPhrases();

        //detectKeyPhrasesResult.getKeyPhrases().forEach(System.out::println);
        //System.out.println("End of DetectKeyPhrases\n");
        return keys;
    }


}
