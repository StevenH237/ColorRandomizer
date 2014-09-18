package com.teamcompleteinsanity.colorrandomizer;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
  // Hey! Thanks for having a look at my code.
  // The package name is "com.teamcompleteinsanity" because of Java convention -
  // you should use your domain name in reverse order (but your email address
  // also works in a pinch, for example "com.gmail.me" for "me@gmail.com")
  // followed by the project name, and all of this in lowercase.
  // "Team Complete Insanity" is a company my friends and I plan to start.
  
  // This code is by no means the perfect example of how this should be run.
  // For example, I am sure there are better ways to handle the OnTextChanged
  // event. Also, I'm not the best at leaving comments, but I'll try to explain
  // how everything below works.
  
  // The objects that will be edited on-screen
  private EditText    redAmountField;
  private SeekBar     redAmountBar;
  private EditText    greenAmountField;
  private SeekBar     greenAmountBar;
  private EditText    blueAmountField;
  private SeekBar     blueAmountBar;
  private Button      randomButton;
  private EditText    colorResult;
  private Button      shareButton;
  private TextView    colorText;
  
  // Whether there are errors in upper segments that should stop it from trying to make a color
  protected boolean redError = false;
  protected boolean greenError = false;
  protected boolean blueError = false;
  
  // And when clearing errors, use this color as the reset value
  protected int defaultColor = 0x0;
  
  // Lastly, a Random object
  private Random r;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Define the instances for the objects
    redAmountField = (EditText) findViewById(R.id.redAmountField);
    redAmountBar = (SeekBar) findViewById(R.id.redAmountBar);
    greenAmountField = (EditText) findViewById(R.id.greenAmountField);
    greenAmountBar = (SeekBar) findViewById(R.id.greenAmountBar);
    blueAmountField = (EditText) findViewById(R.id.blueAmountField);
    blueAmountBar = (SeekBar) findViewById(R.id.blueAmountBar);
    randomButton = (Button) findViewById(R.id.randomizeButton);
    colorResult = (EditText) findViewById(R.id.colorResultField);
    shareButton = (Button) findViewById(R.id.shareButton);
    colorText = (TextView) findViewById(R.id.colorText);
    
    // Get the default color
    defaultColor = redAmountField.getTextColors().getDefaultColor();
    
    // Randomize the opening color
    r = new Random();
    randomizeColor();
    
    // Enact listeners
    redAmountField.addTextChangedListener(new TextSeer(redAmountField, redAmountBar));
    greenAmountField.addTextChangedListener(new TextSeer(greenAmountField, greenAmountBar));
    blueAmountField.addTextChangedListener(new TextSeer(blueAmountField, blueAmountBar));
    colorResult.addTextChangedListener(new TextSeer(colorResult, null));
    redAmountBar.setOnSeekBarChangeListener(new SeekSeer(redAmountBar, redAmountField));
    greenAmountBar.setOnSeekBarChangeListener(new SeekSeer(greenAmountBar, greenAmountField));
    blueAmountBar.setOnSeekBarChangeListener(new SeekSeer(blueAmountBar, blueAmountField));
    randomButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        randomizeColor();
      }
    });
    shareButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, colorResult.getText().toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.sendTo)));
      }
    });
  }
  
  // This method creates a random color, both on startup and when the Random
  // button is tapped.
  private void randomizeColor() {
    int col = r.nextInt(0xFFFFFF) + 0xFF000000;
    String colHex = buildHexString(col, 6);
    colorResult.setText(colHex);
    
    colorizeUp(col);
  }
    
  // This method turns the number "num" into a hex string of at least "digits"
  // length and starting with a # sign. For example, if called as
  // buildHexString(237, 2), the result will be the String "#ED"
  // For my classmates in ITCS-2500, this method is freely usable (try to put
  // my name and the function name in a text field somewhere visible in the
  // app).
  private String buildHexString(int num, int digits) {
    String colHex = Integer.toHexString(num);
    while (colHex.length() < digits) {
      colHex = "0" + colHex;
    }
    return "#" + colHex;
  }
  
  // This function simply sets the color of the preview text field, both the
  // text and background colors.
  private void colorize(int col) {
    colorText.setBackgroundColor(col);
    colorText.setTextColor(col);
  }
  
  // This method takes the red, green, and blue sliders and sets the hex value
  // and preview color accordingly. Note that there's no error checking in this
  // method; error checking is instead done before calling it.
  private void colorizeDown() {
    // Sets the color result field (and preview) based on the red, green, and blue components
    int redValue = Integer.parseInt(redAmountField.getText().toString());
    int greenValue = Integer.parseInt(greenAmountField.getText().toString());
    int blueValue = Integer.parseInt(blueAmountField.getText().toString());
    
    int col = Color.rgb(redValue, greenValue, blueValue);
    
    colorize(col);
    
    String colHex = buildHexString(col, 6);
    colorResult.setText(colHex);
  }
  
  // This function takes the hex value and sets the red, green, and blue
  // sliders and the preview color accordingly. Note that there's no error
  // checking in this method either - same reason.
  private void colorizeUp(int col) {
    // Sets the color components (and preview) based on the text in the hex code text field.    
    colorize(col);
    
    redAmountField.setText(Integer.toString(Color.red(col)));
    redAmountBar.setProgress(Color.red(col));
    greenAmountField.setText(Integer.toString(Color.green(col)));
    greenAmountBar.setProgress(Color.green(col));
    blueAmountField.setText(Integer.toString(Color.blue(col)));
    blueAmountBar.setProgress(Color.blue(col));
  }

  // This function purely exists because of the fact that it is required
  // to.
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  
  
  // This class watches for EditText fields being changed.
  private class TextSeer implements TextWatcher {
    TextView v; // The TextView being watched
    SeekBar b; // The associated SeekBar to change with it
    
    protected TextSeer(TextView view, SeekBar bar) {
      v = view;
      b = bar;
    }
    
    // Required overrides that won't actually do anything
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) { }
    public void onTextChanged(CharSequence s, int start, int before, int count) { }
    
    // Sets whether the textbox is marked as containing an error - if any one
    // of them is, then the program will not attempt to calculate the color
    // from the three values.
    private void setErrorState(TextView v, boolean state) {
      switch (v.getId()) {
        case R.id.redAmountField:
          redError = state;
          break;
        case R.id.greenAmountField:
          greenError = state;
          break;
        case R.id.blueAmountField:
          blueError = state;
          break;
      }
    }
    
    // When text is changed, this function is called.
    @Override
    public void afterTextChanged(Editable s) {
      // I'm using the focus to show which EditText is changed. Without this,
      // the program editing text boxes would call each other recursively.
      // With this line, the execution should only continue if the user changed
      // the value.
      if (getCurrentFocus() != v) return;
      switch (v.getId()) {
        // What to do if it's one of the component colors:
        case R.id.redAmountField:
        case R.id.greenAmountField:
        case R.id.blueAmountField:
          try {
            // Get the color that's in there...
            int val = Integer.parseInt(v.getText().toString());
            // If it's a valid number, but not within range, throw the same
            // error as if it's not a valid number at all. Dirty, but it works.
            if (val > 255 || val < 0) throw new NumberFormatException();
            // If it does that, it will stop executing this code, which means
            // if this code is still being executed, there's no error. Thus,
            // make sure the program doesn't still think there IS one.
            v.setTextColor(defaultColor);
            setErrorState(v, false);
            // Now update the SeekBar to match the EditText value.
            b.setProgress(val);
            // Stop if there's an error on another component...
            if (redError || greenError || blueError) { break; }
            // ... and if not stopped, update the color and hex value.
            colorizeDown();
            break;
          } catch (NumberFormatException ex) {
            // Oh, so there is an error? Then let the program know that.
            v.setTextColor(Color.RED);
            setErrorState(v, true);
            break;
          }
        // Or perhaps it's the hex value EditText being edited.
        case R.id.colorResultField:
          try {
            // Just stop if it's empty. Just stop.
            if (v.getText().toString().isEmpty()) break;
            // Next line will throw the error IllegalArgumentException if the
            // color is invalid. Execution of this block will thereafter stop.
            int col = Color.parseColor(v.getText().toString());
            // If we're still here, then there's no error. Make sure the user
            // knows that (the program doesn't need to for this field).
            v.setTextColor(defaultColor);
            // And update the color and components.
            colorizeUp(col);
            break;
          } catch (IllegalArgumentException ex) {
            // Oh, there IS an error? Alright, let the user know.
            v.setTextColor(Color.RED);
            break;
          }
      }
    }
  }
  
  
  
  private class SeekSeer implements SeekBar.OnSeekBarChangeListener {
    EditText target; // The EditText associated with this SeekBar
    SeekBar bar; // This bar
    
    public SeekSeer(SeekBar b, EditText t) {
      target = t;
      bar = b;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
        boolean fromUser) {
      // I wish afterTextChanged had the fromUser argument. I wouldn't need
      // the dirty hack of using focus to detect user changes.
      if (!fromUser) return;
      target.requestFocus();
      // But, it DOES let us just change the focus and text to make everything
      // else work.
      target.setText(Integer.toString(bar.getProgress()));
    }
    
    // Required overrides that won't actually do anything
    public void onStartTrackingTouch(SeekBar seekBar) { }
    public void onStopTrackingTouch(SeekBar seekBar) { }
    
  }
  
}
