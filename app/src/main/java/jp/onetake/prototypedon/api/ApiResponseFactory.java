package jp.onetake.prototypedon.api;

public class ApiResponseFactory {
	public static ApiResponse createResponse(ApiRequest request) {
		if (request instanceof RegisterClientRequest) {
			return new RegisterClientResponse();
		} else if (request instanceof AccessTokenRequest) {
			return new AccessTokenResponse();
		} else if (request instanceof VerifyCredentialsRequest) {
			return new VerifyCredentialsResponse();
		}

		return null;
	}
}
