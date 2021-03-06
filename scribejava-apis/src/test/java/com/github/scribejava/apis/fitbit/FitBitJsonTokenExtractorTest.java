package com.github.scribejava.apis.fitbit;

import com.github.scribejava.core.model.OAuth2AccessTokenErrorResponse;
import com.github.scribejava.core.oauth2.OAuth2Error;
import java.io.IOException;

import org.hamcrest.FeatureMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;

public class FitBitJsonTokenExtractorTest {

    private static final String ERROR_DESCRIPTION = "Authorization code invalid: " +
            "cbb1c11b23209011e89be71201fa6381464dc0af " +
            "Visit https://dev.fitbit.com/docs/oauth2 for more information " +
            "on the Fitbit Web API authorization process.";
    private static final String ERROR_JSON = "{\"errors\":[{\"errorType\":\"invalid_grant\",\"message\":\"" +
            ERROR_DESCRIPTION + "\"}],\"success\":false}";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testErrorExtraction() throws IOException {

        final FitBitJsonTokenExtractor extractor = new FitBitJsonTokenExtractor();

        thrown.expect(OAuth2AccessTokenErrorResponse.class);
        thrown.expect(new ErrorCodeFeatureMatcher(OAuth2Error.INVALID_GRANT));
        thrown.expect(new ErrorDescriptionFeatureMatcher(ERROR_DESCRIPTION));

        extractor.generateError(ERROR_JSON);
    }

    private static class ErrorCodeFeatureMatcher extends FeatureMatcher<OAuth2AccessTokenErrorResponse, OAuth2Error> {

        private ErrorCodeFeatureMatcher(OAuth2Error expected) {
            super(equalTo(expected), "a response with errorCode", "errorCode");
        }

        @Override
        protected OAuth2Error featureValueOf(OAuth2AccessTokenErrorResponse actual) {
            return actual.getError();
        }
    }

    private static class ErrorDescriptionFeatureMatcher extends FeatureMatcher<OAuth2AccessTokenErrorResponse, String> {

        private ErrorDescriptionFeatureMatcher(String expected) {
            super(equalTo(expected), "a response with errorDescription", "errorDescription");
        }

        @Override
        protected String featureValueOf(OAuth2AccessTokenErrorResponse actual) {
            return actual.getErrorDescription();
        }
    }
}
