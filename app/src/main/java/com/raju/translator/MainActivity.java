package com.raju.translator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.raju.translator.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TranslatorOptions translatorOptions;
    private Translator translator;
    private ArrayList<AvailableLanguages> availableLanguages;
    private ActivityMainBinding mainBinding;
    private String sourceLangCode = "en";
    private String sourceLang = "English";
    private String destLangCode = "ta";
    private String destLang = "Tamil";
    private String sourceLanguageText = "";
    private String res="";
    private ClipboardManager clipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        clipboardManager= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        disable();
        mainBinding.copy.setVisibility(View.GONE);
        loadLanguages();
        Listeners();
    }

    private void disable() {
        mainBinding.progress.setVisibility(View.VISIBLE);
        mainBinding.options.setVisibility(View.GONE);
        mainBinding.sourceResults.setVisibility(View.GONE);
        mainBinding.results.setVisibility(View.GONE);
    }

    private void enable() {
        mainBinding.progress.setVisibility(View.GONE);
        mainBinding.options.setVisibility(View.VISIBLE);
        mainBinding.sourceResults.setVisibility(View.VISIBLE);
        mainBinding.results.setVisibility(View.VISIBLE);
    }

    private void loadLanguages() {
        availableLanguages = new ArrayList<>();
        List<String> availableLanguageCodes = TranslateLanguage.getAllLanguages();
        for (String code : availableLanguageCodes) {
            String languageTitle = new Locale(code).getDisplayLanguage();
            AvailableLanguages languages = new AvailableLanguages(code, languageTitle);
            availableLanguages.add(languages);
        }
        enable();
    }

    private void Listeners() {
        mainBinding.sourceClose.setOnClickListener(v -> {
            mainBinding.sourceLanguageEt.setText("");
        });

        mainBinding.destClose.setOnClickListener(v -> {
            mainBinding.destinationResult.setText(" ");
        });
        mainBinding.sourceLanguage.setOnClickListener(v -> {
            sourceLanguageChoose();
        });
        mainBinding.destLanguage.setOnClickListener(v -> {
            destLanguageChoose();
        });
        mainBinding.translate.setOnClickListener(v -> {
            validateDate();
        });
        mainBinding.copy.setOnClickListener(v->{
            ClipData myClip = ClipData.newPlainText("result", res);
            clipboardManager.setPrimaryClip(myClip);
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
        });
    }

    private void validateDate() {
        sourceLanguageText = mainBinding.sourceLanguageEt.getText().toString().trim();
        if (sourceLanguageText.isEmpty()) {
            Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_LONG).show();
        } else {
            translateText();
        }
    }

    private void translateText() {
        disable();
        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLangCode)
                .setTargetLanguage(destLangCode)
                .build();
        translator = Translation.getClient(translatorOptions);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        enable();
                        Toast.makeText(MainActivity.this, "Translating...", Toast.LENGTH_SHORT).show();
                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        res=s;
                                        mainBinding.copy.setVisibility(View.VISIBLE);
                                        mainBinding.destinationResult.setText(s);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Translation Failed due to :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        enable();
                        Toast.makeText(MainActivity.this, "Model Download failed due to :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sourceLanguageChoose() {
        PopupMenu popupMenu = new PopupMenu(this, mainBinding.sourceLanguage);
        for (int i = 0; i < availableLanguages.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, availableLanguages.get(i).getLanguageTitle());
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(v -> {
            int pos = v.getItemId();
            sourceLangCode = availableLanguages.get(pos).getLanguageCode();
            sourceLang = availableLanguages.get(pos).getLanguageTitle();
            mainBinding.sourceLanguage.setText(sourceLang);
            mainBinding.sourceLanguageEt.setHint("Enter text in " + sourceLang);
            mainBinding.sourceLanguageText.setText(sourceLang);
            return false;
        });
    }

    private void destLanguageChoose() {
        PopupMenu popupMenu = new PopupMenu(this, mainBinding.destLanguage);
        for (int i = 0; i < availableLanguages.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, availableLanguages.get(i).getLanguageTitle());
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(v -> {
            int pos = v.getItemId();
            destLangCode = availableLanguages.get(pos).getLanguageCode();
            destLang = availableLanguages.get(pos).getLanguageTitle();
            mainBinding.destLanguage.setText(destLang);
            mainBinding.destLanguageText.setText(destLang);
            return false;
        });
    }
}