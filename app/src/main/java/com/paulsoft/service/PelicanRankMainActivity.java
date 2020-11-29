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

package com.paulsoft.service;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Optional;

public class PelicanRankMainActivity extends AppCompatActivity {

    private static final String TAG = "PelicanMainActivity";
    private static final String PARAM_USER_LOGIN = "usrLogin";
    public static final String EMPTY_TEXT = "";
    private PreferencesRepository preferencesRepository;
    private EditText loginField;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        preferencesRepository = new PreferencesRepository(this);
        loginField = findViewById(R.id.login_field);

        Optional<String> login = preferencesRepository.load(PARAM_USER_LOGIN, String.class);
        UiControlHelper.setValue(login.orElse(EMPTY_TEXT), loginField);

    }

    public void startApp(View view) {
        String login = UiControlHelper.getValue(loginField);
        preferencesRepository.save(PARAM_USER_LOGIN, String.class, login);

        Toast.makeText(this, "Saved...", Toast.LENGTH_SHORT).show();
    }

}
