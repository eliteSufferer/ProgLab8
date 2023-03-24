package common.functional;
import java.io.Serializable;

public class Response implements Serializable{
    private String responseBody;

    public Response(String responseBody) {
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
}
