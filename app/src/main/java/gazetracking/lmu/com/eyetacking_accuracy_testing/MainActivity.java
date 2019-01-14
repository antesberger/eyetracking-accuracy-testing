package gazetracking.lmu.com.eyetacking_accuracy_testing;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    String participant = "empty";
    String startTime = "no-date";
    public int[] BOXES = {1,2,3,4,5,6,7,8,9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            participant = bundle.getString("participant");
            startTime = bundle.getString("startTime");

        if (hasEyetrackingStarted()) {

            writeFileOnInternalStorage(MainActivity.this, "log.txt", "task started");

            Toast.makeText(
                    MainActivity.this,
                    "Try to focus on the displayed cross.",
                    Toast.LENGTH_SHORT
            ).show();

            //shuffle image array
            int index;
            List<Integer> randomIndices = new ArrayList();
            Random random = new Random();
            for (int i = BOXES.length - 1; i > 0; i--) {
                index = random.nextInt(i + 1);
                randomIndices.add(index);

                if (index != i) {
                    BOXES[index] ^= BOXES[i];
                    BOXES[i] ^= BOXES[index];
                    BOXES[index] ^= BOXES[i];
                }
            }

            Handler done = new doneHandler();
            done.sendMessageDelayed(new Message(), 53000);
            for (Integer i = 0; i < 9; i++) {
                Integer j = BOXES[i];
                Handler showHandler = new toggleTarget();
                Handler hideHandler = new toggleTarget();

                Message m1 = new Message();
                m1.what = j;
                Message m2 = new Message();
                m2.what = j;

                showHandler.sendMessageDelayed(m1, 5000 * i + 5000);
                hideHandler.sendMessageDelayed(m2, 5000 * i + 8000);

            }
        } else {
            Intent intent = new Intent(MainActivity.this, StartScreen.class);
            startActivity(intent);
            Toast.makeText(
                    MainActivity.this,
                    "Recheck your ID and make sure that the eyetracking has started",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    class toggleTarget extends Handler {
        public void handleMessage(Message msg) {
            TextView box;
            switch (msg.what) {
                case 1:
                    box = findViewById(R.id.box_1);
                    break;
                case 2:
                    box = findViewById(R.id.box_2);
                    break;
                case 3:
                    box = findViewById(R.id.box_3);
                    break;
                case 4:
                    box = findViewById(R.id.box_4);
                    break;
                case 5:
                    box = findViewById(R.id.box_5);
                    break;
                case 6:
                    box = findViewById(R.id.box_6);
                    break;
                case 7:
                    box = findViewById(R.id.box_7);
                    break;
                case 8:
                    box = findViewById(R.id.box_8);
                    break;
                case 9:
                    box = findViewById(R.id.box_9);
                    break;
                default:
                    box = findViewById(R.id.box_1);
                    break;
            }

            if (box.getVisibility() == View.INVISIBLE) {
                writeFileOnInternalStorage(MainActivity.this, "log.txt", "showing " + msg.what);
                box.setVisibility(View.VISIBLE);
            } else {
                box.setVisibility(View.INVISIBLE);
            }
        }
    }

    class doneHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            writeFileOnInternalStorage(MainActivity.this, "log.txt", "task ended");
            Intent intent = new Intent(MainActivity.this, StartScreen.class);
            startActivity(intent);
            Toast.makeText(
                    MainActivity.this,
                    "Thank you! You're done with this task.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    public boolean hasEyetrackingStarted() {
        File file = new File(MainActivity.this.getFilesDir(), participant + "_" + startTime);
        if(!file.exists()){
            return false;
        } else {
            return true;
        }
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        File file = new File(mcoContext.getFilesDir(), participant + "_" + startTime);

        try{
            File outFile = new File(file, sFileName);
            FileWriter writer = new FileWriter(outFile, true);
            writer.append(timestamp.format(new Date()));
            writer.append("; ");
            writer.append(sBody);
            writer.append("\n");
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();

        }
    };
}
