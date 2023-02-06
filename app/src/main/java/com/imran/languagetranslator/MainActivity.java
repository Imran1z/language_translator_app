package com.imran.languagetranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //these are some variabls which have the same name as in my XML file

    private Spinner SpnrFrom, SpnrTo;
    private TextInputEditText EdtSource;
    private ImageView InptMic;
    private TextView BtnTranslate,TxtViewTranslatedText;


// these are array of languages  we have used in this app i have placed 17 languages you can place any amount you want
    //there are two array fromlanguage and tolanguages

    String [] fromlanguages={"From","English","Hindi","Urdu","Arabic","German","Spanish","Africaans","French"
    ,"Italian","Japanese","Russian","Tamil","Telugu","Bengali","Gujarati","Kannada","Marathi"};
    String [] tolanguages={"To","English","Hindi","Urdu","Arabic","German","Spanish","Africaans","French"
            ,"Italian","Japanese","Russian","Tamil","Telugu","Bengali","Gujarati","Kannada","Marathi"};
    private static final int REQUEST_PERMISSION_CODE=1;
    int languageCode,fromLanguageCode,toLanguageCode=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//here i have initialised those variebls
        SpnrFrom=findViewById(R.id.SpnrFrom);
        SpnrTo=findViewById(R.id.SpnrTo);
        EdtSource=findViewById(R.id.EdtSource);
        InptMic=findViewById(R.id.InptMic);
        BtnTranslate=findViewById(R.id.BtnTranslate);
        TxtViewTranslatedText=findViewById(R.id.TxtViewTranslatedText);






                //This for the from spinner we place an on selected listener on it
                //And set an array adapter on it
        //in this to get the language codes we have used a getlanguagecode method
        // and passed our language array in it


        SpnrFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode=getLanguageCode(fromlanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter fromAdapter=new ArrayAdapter(this,R.layout.spinner_item,fromlanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnrFrom.setAdapter(fromAdapter);



        //This for the To spinner we place an on selected listener on it
        //And set an array adapter on it
        //in this to get the language codes we have used a getlanguagecode method
        // and passed our language array in it
        SpnrTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                toLanguageCode=getLanguageCode(tolanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter=new ArrayAdapter(this,R.layout.spinner_item,tolanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnrTo.setAdapter(toAdapter);



             //this is the Buttton which i have made out of a text view
        // and i have placed an in click listener on it
        // in this if we first make the translated text area as an empty string
        // and checked for the languagecodes we have get form the spinner in the earlier steps
        // after all the conditions we place an else condition for the method which will get our text translated
        BtnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TxtViewTranslatedText.setText("");
                if (EdtSource.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter your text to translate",Toast.LENGTH_SHORT).show();
                }
                else if (fromLanguageCode==0){
                    Toast.makeText(MainActivity.this,"Please select the start language ",Toast.LENGTH_SHORT).show();

                }
                else if(toLanguageCode==0){
                    Toast.makeText(MainActivity.this,"Please select the end language ",Toast.LENGTH_SHORT).show();


                }
                else{
                    translatedText(fromLanguageCode,toLanguageCode,EdtSource.getText().toString());


                }

            }
        });





        InptMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to Translate");

                try {
                    startActivityForResult(intent,REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_PERMISSION_CODE){
            if (resultCode==RESULT_OK && data!=null){
                ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                EdtSource.setText(result.get(0));
            }
        }
    }

    private void translatedText(int fromLanguageCode, int toLanguageCode, String origin){    //source==origin
        TxtViewTranslatedText.setText("Downloading Model...");
        FirebaseTranslatorOptions options=new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator= FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                TxtViewTranslatedText.setText("Processing...");
                translator.translate(origin).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                       TxtViewTranslatedText.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed to download the language model"+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });




    }

//this is the method i have used to get language codes
    public int getLanguageCode(String language){
        int languageCode=0;
        switch (language){
            case "English":
                languageCode= FirebaseTranslateLanguage.EN;
                break;
            case "Hindi":
                languageCode= FirebaseTranslateLanguage.HI;
                break;
            case "Urdu":
                languageCode= FirebaseTranslateLanguage.UR;
                break;
            case "Arabic":
                languageCode= FirebaseTranslateLanguage.AR;
                break;
            case "German":
                languageCode= FirebaseTranslateLanguage.DE;
                break;
            case "Spanish":
                languageCode= FirebaseTranslateLanguage.ES;
                break;
            case "Africaans":
                languageCode= FirebaseTranslateLanguage.AF;
                break;
            case "French":
                languageCode= FirebaseTranslateLanguage.FR;
                break;
            case "Italian":
                languageCode= FirebaseTranslateLanguage.IT;
                break;
            case "Japanese":
                languageCode= FirebaseTranslateLanguage.JA;
                break;
            case "Russian":
                languageCode= FirebaseTranslateLanguage.RU;
                break;
            case "Tamil":
                languageCode= FirebaseTranslateLanguage.TA;
                break;
            case "Telugu":
                languageCode= FirebaseTranslateLanguage.TE;
                break;
            case "Bengali":
                languageCode= FirebaseTranslateLanguage.BN;
                break;
            case "Gujarati":
                languageCode= FirebaseTranslateLanguage.GU;
                break;
            case "Kannada":
                languageCode= FirebaseTranslateLanguage.KN;
                break;
            case "Marathi":
                languageCode= FirebaseTranslateLanguage.MR;
                break;
            default:
                languageCode=0;


        }
        return languageCode;
    }
}