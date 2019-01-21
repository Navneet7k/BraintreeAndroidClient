package navneet.com.braintreepaypalsample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 123;
    private Button paypal_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paypal_button=(Button)findViewById(R.id.start_payment);

        paypal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startPayment();  -->> add api to generate client token to call startPayment()
                onBraintreeSubmit(getString(R.string.test_client_token)); // -->> added for testing with sample client token, call this method from onResponse() of startPayment() only
            }
        });
    }

    public void startPayment() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("your_base_url")   // http://www.example.com
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<String> call1=request.getClientToken();
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                if (response.isSuccessful() && response!=null) {
                    onBraintreeSubmit(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Oops! Something went wrong!",Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String payment_method_nonce=result.getPaymentMethodNonce().getNonce();
//                submitNonce(payment_method_nonce); -->> create server side api to accept payment amount and PaymentNonce
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    private void submitNonce(String payment_method_nonce) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("add_base_url")  // http://www.example.com
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("AmountDebit", "");
        jsonObject.addProperty("PaymentNonce",payment_method_nonce);

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<ResponseBody> call1=request.getPayment(jsonObject);
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }

        });
    }

    public void onBraintreeSubmit(String token) {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }
}
