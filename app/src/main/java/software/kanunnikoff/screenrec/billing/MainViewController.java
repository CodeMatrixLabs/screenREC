/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.kanunnikoff.screenrec.billing;

import android.util.Log;

import com.android.billingclient.api.Purchase;

import java.util.List;

import software.kanunnikoff.screenrec.core.Core;
import software.kanunnikoff.screenrec.ui.MainActivity;


/**
 * Handles control logic of the BaseGamePlayActivity
 */
public class MainViewController {
    private static final String TAG = "MainViewController";

    private final UpdateListener mUpdateListener;
    private MainActivity mActivity;

    // Tracks if we currently own a premium
    private boolean mIsPremium;

    public MainViewController(MainActivity activity) {
        mUpdateListener = new UpdateListener();
        mActivity = activity;
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public boolean isPremiumPurchased() {
        return mIsPremium;
    }

    /**
     * Handler to billing updates
     */
    public class UpdateListener implements BillingManager.BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            mActivity.onBillingManagerSetupFinished();
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case Core.PREMIUM_SKU_ID:
                        Log.d(TAG, "You are Premium! Congratulations!!!");
                        mIsPremium = true;
                        break;
                }
            }

            mActivity.updateUi();
        }
    }
}