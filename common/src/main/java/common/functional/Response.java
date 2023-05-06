package common.functional;
import java.io.Serializable;

public class Response implements Serializable{
    private String responseBody;
    private ServerResponseCode responseCode;

    public Response(ServerResponseCode responseCode, String responseBody, String[] responseBodyArgs) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
        this.responseBodyArgs = responseBodyArgs;
    }

    /**
     * @return Response body.
     */
    public String getResponseBody() {
        return responseBody;
    }

    public String[] getResponseBodyArgs() {
        return responseBodyArgs;
    }

    @Override
    public String toString() {
        return "Response[" + responseBody + "]";
    }

    public ServerResponseCode getResponseCode() {
        return responseCode;
    }
}
