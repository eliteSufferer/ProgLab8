package common.functional;
import java.io.Serializable;

public class Response implements Serializable{
    private String responseBody;
    private ServerResponseCode responseCode;
    private String[] responseBodyArgs;
    private Object object;

    public Response(ServerResponseCode responseCode, String responseBody, String[] responseBodyArgs) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
        this.responseBodyArgs = responseBodyArgs;
    }
    public Response(Object object, ServerResponseCode responseCode){
        this.object = object;
        this.responseCode = responseCode;
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
    public Object getResponseObject(){
        return this.object;
    }
}
