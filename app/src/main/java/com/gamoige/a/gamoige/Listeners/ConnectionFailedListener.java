package com.gamoige.a.gamoige.Listeners;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gamoige.a.gamoige.Fragments.ConnectionFragment;
import com.gamoige.a.gamoige.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.example.games.basegameutils.BaseGameUtils;

/**
 * Created by Donsky on 7/18/2017.
 */

public class ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
    private ConnectionFragment connectionFragment;
    private boolean resolving = false;
    public ConnectionFailedListener(ConnectionFragment fragment) {
        connectionFragment = fragment;
    }
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(ConnectionFragment.QE_RO_DAGVENZREVA_IS_TEGI, "dagvenzra: onConnectionFailed");
        if (resolving) {
            // already resolving
            return;
        }
        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
            resolving = true;
            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(connectionFragment.getActivity(),
                    connectionFragment.getConnection(), connectionResult,
                    ConnectionFragment.RC_SIGN_IN, R.string.signin_other_error)) {
                resolving = false;
            }
        // Put code here to display the sign-in button
    }

}
