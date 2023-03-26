package common.functional;
import java.io.Serializable;

public class Response implements Serializable{
    private String responseBody;
    private ServerResponseCode responseCode;

    public Response(ServerResponseCode responseCode, String responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    /**
     * @return Response body.
     */
    public String getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return "Response[" + responseBody + "]";
    }

    public ServerResponseCode getResponseCode() {
        return responseCode;
    }
}
