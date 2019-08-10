package twitterFeeds;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import java.util.Objects;

public class MetricsProcessor {
    public void collectCollectedUrlsMetric(double dataPoint, String track) {
        final AmazonCloudWatch cw =
                AmazonCloudWatchClientBuilder.defaultClient();

        Dimension dimension = new Dimension()
                .withName("TRACKS")
                .withValue(track);

        MetricDatum datum = new MetricDatum()
                .withMetricName("COLLECTED_URLS")
                .withUnit(StandardUnit.None)
                .withValue(dataPoint)
                .withDimensions(dimension);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("SITE/PROCESSED_URLS")
                .withMetricData(datum);

        PutMetricDataResult response = null;
        try {
            response = cw.putMetricData(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Objects.requireNonNull(response).getSdkHttpMetadata().getHttpStatusCode() != 200) {
            System.out.println("Http request did not succeed");
        }
    }

    public void collectTimeTaken(double timeTakenMs,
                            String metricName) {
        final AmazonCloudWatch cw =
                AmazonCloudWatchClientBuilder.defaultClient();

        MetricDatum datum = new MetricDatum()
                .withMetricName(metricName)
                .withUnit(StandardUnit.None)
                .withValue(timeTakenMs);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("SITE/PROCESSED_URLS")
                .withMetricData(datum);

        PutMetricDataResult response = null;
        try {
            response = cw.putMetricData(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Objects.requireNonNull(response).getSdkHttpMetadata().getHttpStatusCode() != 200) {
            System.out.println("Http request did not succeed");
        }
    }
}
