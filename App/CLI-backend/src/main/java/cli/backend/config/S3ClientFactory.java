package cli.backend.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3ClientFactory {

    private static final String REGION = "eu-central-1";
    private static final String ACCESS_KEY = System.getenv("AWS_ACCESS_KEY");
    private static final String SECRET_KEY = System.getenv("AWS_SECRET_KEY");

    public static S3Client createS3Client() {
        if (ACCESS_KEY == null || SECRET_KEY == null) {
            throw new IllegalStateException("Set your environment variables for AWS!");
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);

        return S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
