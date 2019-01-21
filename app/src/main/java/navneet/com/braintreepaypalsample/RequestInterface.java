package navneet.com.braintreepaypalsample;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Navneet Krishna on 21/01/19.
 */
public interface RequestInterface {

    @GET("add_your_endpoint")
    Call<String> getClientToken();

    @POST("your_payments_endpoint")
    @Headers("Content-Type: application/json")
    Call<ResponseBody> getPayment(@Body JsonObject jsonObject);

}
