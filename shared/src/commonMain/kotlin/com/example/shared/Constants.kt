package com.example.shared

object Constants {

    const val WEB_CLIENT_ID  = "496441513720-f6g2h66egatj2up3r3r9lkbc593icd3l.apps.googleusercontent.com"

    const val PAYPAL_CLIENT_ID = "Ae9yvhzPIDF0yDBWPYW1ZkC9XJ1iCdrJ_k03nsL99XbeqeTg5o4uzlCULUS0lBCby_6dgNJn568M6cLv"

    const val PAYPAL_SECRET_ID = "EAoFdQ0rDRfCXIe14gPNQcvzK0F1oRyhhqf1Yzvd3NNKcyIteL9ExoS-zNa6j6si9vyPg1umUAi35Hzk"

    const val PAYPAL_AUTH_KEY = "$PAYPAL_CLIENT_ID:$PAYPAL_SECRET_ID"


    const val PAYPAL_AUTH_ENDPOINT = "https://api-m.sandbox.paypal.com/v1/oauth2/token"
    const val PAYPAL_CHECKOUT_ENDPOINT = "https://api-m.sandbox.paypal.com/v2/checkout/orders"

    const val RETURN_URL = "com.example.nutritionsports://paypalpay?success=true"
    const val CANCEL_URL = "com.example.nutritionsports://paypalpay?cancel=true"
}