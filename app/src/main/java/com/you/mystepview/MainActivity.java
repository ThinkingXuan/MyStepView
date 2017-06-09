package com.you.mystepview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private StepView stepView;
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bt = (Button) findViewById(R.id.bt);
        stepView = (StepView) findViewById(R.id.stepView);
        List<String> steps = Arrays.asList(new String[]{"输入手机", "验证手机", "设置密码", "注册成功"});
        stepView.setSteps(steps);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextStep = stepView.getCurrentStep() + 1;
                if (nextStep > stepView.getStepCount()) {
                    nextStep = 1;
                }
                stepView.selectedStep(nextStep);
            }
        });

    }
}
