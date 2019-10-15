package com.wolfmobileapps.zapisy;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

// Google Pay API request configurations "https://developers.google.com/pay/api/android/"


public class GooglePay {

    // ?
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }


    // ustawienie płatności z dostawcą płatności (np DotPay) Zmienić w "gateway" "example" na dostawcę usługi i w getMerchandId na unikalny identyfikator bramy dany przez dostawce płątności
    private static JSONObject getTokenizationSpecification() throws JSONException {
        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
        tokenizationSpecification.put(
                "parameters",
                new JSONObject()
                        .put("gateway", "example")
                        .put("gatewayMerchantId", "exampleGatewayMerchantId"));

        return tokenizationSpecification;
    }

    // wpidac jakie karty są obsługiwane przez dostawcę płatności
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                //.put("AMEX")
                //.put("DISCOVER")
                //.put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    // podpisywanie karty ?
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    // dozwolone formy płatności ?
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");
        cardPaymentMethod.put(
                "parameters",
                new JSONObject()
                        .put("allowedAuthMethods", GooglePay.getAllowedCardAuthMethods())
                        .put("allowedCardNetworks", GooglePay.getAllowedCardNetworks()));

        return cardPaymentMethod;
    }


    // ?
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = GooglePay.getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", GooglePay.getTokenizationSpecification());

        return cardPaymentMethod;
    }


    // wpisanie kwoty i waluty
    private static JSONObject getTransactionInfo(int cena) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", "" + cena);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("currencyCode", "PLN");
        return transactionInfo;
    }

    // wpisać kto pobiera opłatę
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject()
                .put("merchantName", "Zapisy M Group Sebastian Miotk");
    }

    // ?
    public static JSONObject getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = GooglePay.getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));
            return isReadyToPayRequest;
        } catch (JSONException e) {
            return null;
        }
    }

    // mozna dodać różne dane do zapłąty chyba się bedą wyświetlać
    public static JSONObject getPaymentDataRequest(int cena) {
        try {
            JSONObject paymentDataRequest = GooglePay.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(GooglePay.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", GooglePay.getTransactionInfo(cena));
            paymentDataRequest.put("merchantInfo", GooglePay.getMerchantInfo());
            return paymentDataRequest;
        } catch (JSONException e) {
            return null;
        }
    }

}