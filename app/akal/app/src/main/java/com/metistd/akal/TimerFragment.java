package com.metistd.akal;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String UNIT_MINUTES = "minutes";
    public static final String UNIT_SECONDS = "seconds";

    private SoundPlayer soundPlayer;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout iconlayout;
    private LinearLayout clockLayout;
    private LinearLayout clockPickerLayout;

    private ImageView timerIcon;

    //clock elements.
    private TextView txtClockPlayPause;
    private TextView txtClockStop;
    private boolean isTimerOn = false;
    private boolean isTimerRunning = false;
    private int currentMinutes = 0;
    private int currentSeconds = 0;
    private TextView txtTimerDisplay;
    private CountDownTimer countDownTimer;
    private long remainingMilliseconds = 0;

    //clock picker
    private TextView txtPickerAccept;
    private TextView txtPickerCancel;
    private HashMap<Integer, Integer> validMinutesPicks;
    private HashMap<Integer, Integer> validSecondsPicks;
    private HashMap<Integer, TextView> mapPickerMinutes;
    private HashMap<Integer, TextView> mapPickerSeconds;

    public TimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimerFragment newInstance(String param1, String param2) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        soundPlayer.initSounds(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View timerView = inflater.inflate(R.layout.fragment_timer, container, false);

        //timer icon/button
        iconlayout = (LinearLayout) timerView.findViewById(R.id.iconlayout);
        iconlayout.setVisibility(View.VISIBLE);
        timerIcon = (ImageView) timerView.findViewById(R.id.imgTimerIcon);
        timerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TimerFragment", "Activating timer controls");
                //soundPlayer.playSound(getContext(), SoundPlayer.CHIME1);
                showClockPicker();
            }
        });

        //clock picker.
        clockPickerLayout = (LinearLayout) timerView.findViewById(R.id.clockPickerLayout);
        clockPickerLayout.setVisibility(View.INVISIBLE);
        txtPickerCancel = (TextView) timerView.findViewById(R.id.txtClockPickerCancel);
        txtPickerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TimerFragment", "Cancel picker controls");
                cancelPicker(view);
            }
        });

        txtPickerAccept = (TextView) timerView.findViewById(R.id.txtClockPickerAccept);
        txtPickerAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TimerFragment", "Accept new timer pick");
                acceptPicker(view);
            }
        });

        //clock display.
        clockLayout = (LinearLayout) timerView.findViewById(R.id.clocklayout);
        clockLayout.setVisibility(View.INVISIBLE);
        txtTimerDisplay = (TextView) timerView.findViewById(R.id.txtClockDisplay);
        txtClockPlayPause = (TextView) timerView.findViewById(R.id.txtClockPlayPause);
        txtClockStop = (TextView) timerView.findViewById(R.id.txtClockStop);
        txtClockStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer(view);
            }
        });
        txtClockPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer(view);
            }
        });
        validMinutesPicks = new HashMap<Integer, Integer>();
        validMinutesPicks.put(R.id.txtClockControlMinute0, 0);
        validMinutesPicks.put(R.id.txtClockControlMinute1, 1);
        validMinutesPicks.put(R.id.txtClockControlMinute3, 3);
        validMinutesPicks.put(R.id.txtClockControlMinute5, 5);
        validMinutesPicks.put(R.id.txtClockControlMinute7, 7);
        validMinutesPicks.put(R.id.txtClockControlMinute9, 9);
        mapPickerMinutes = new HashMap<Integer, TextView>();
        for (int pick : validMinutesPicks.keySet()) {
            TextView txtPickerMinutes = (TextView) timerView.findViewById(pick);
            mapPickerMinutes.put(pick, txtPickerMinutes);
            final int item = pick;
            ((TextView)mapPickerMinutes.get(pick)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setClockPickerValue(view, UNIT_MINUTES, validMinutesPicks.get(item));
                }
            });
        }

        validSecondsPicks = new HashMap<Integer, Integer>();
        validSecondsPicks.put(R.id.txtClockControlSecond0, 0);
        validSecondsPicks.put(R.id.txtClockControlSecond10, 10);
        validSecondsPicks.put(R.id.txtClockControlSecond20, 20);
        validSecondsPicks.put(R.id.txtClockControlSecond30, 30);
        mapPickerSeconds = new HashMap<Integer, TextView>();
        for (int pick : validSecondsPicks.keySet()) {
            TextView txtPickerSeconds = (TextView) timerView.findViewById(pick);
            mapPickerSeconds.put(pick, txtPickerSeconds);
            final int item = pick;
            ((TextView)mapPickerSeconds.get(pick)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setClockPickerValue(view, UNIT_SECONDS, validSecondsPicks.get(item));
                }
            });
        }

        //return
        return timerView;
    }

    public void showTimerControls() {
        iconlayout.setVisibility(View.INVISIBLE);
        clockPickerLayout.setVisibility(View.INVISIBLE);
        clockLayout.setVisibility(View.VISIBLE);
    }

    public void showClockPicker() {
        iconlayout.setVisibility(View.INVISIBLE);
        clockLayout.setVisibility(View.INVISIBLE);
        clockPickerLayout.setVisibility(View.VISIBLE);
        resetTimerControls();

    }

    public void resetTimerControls() {
        txtTimerDisplay.setText("00:00");
        txtTimerDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPurple));
        txtClockPlayPause.setText("Start");
        txtClockPlayPause.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        txtClockStop.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        allPickersWhite(mapPickerMinutes);
        allPickersWhite(mapPickerSeconds);
    }

    public void closeTimerContols() {
        iconlayout.setVisibility(View.VISIBLE);
        clockLayout.setVisibility(View.INVISIBLE);
        clockPickerLayout.setVisibility(View.INVISIBLE);
        resetTimerControls();
    }

    public void stopTimer(View view) {
        Log.d("TimerFragment", "Stopping timer");
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerOn = false;
        isTimerRunning = false;
        currentMinutes = 0;
        currentSeconds = 0;
        resetTimerControls();
        txtClockPlayPause.setText("Repeat");
        Log.d("TimerFragment", "Closing timer controls");
        closeTimerContols();
    }

    public void startTimer(View view){
        if(isTimerOn){
            if(isTimerRunning){
                //pause
                Log.d("TimerFragment", "Pausing timer: " + remainingMilliseconds );
                countDownTimer.cancel();
                txtClockPlayPause.setText("Start");
                isTimerRunning = false;
            }else{
                //resume.
                Log.d("TimerFragment", "Resuming timer: " + remainingMilliseconds);
                initCountDown(remainingMilliseconds);
                countDownTimer.start();
                txtClockPlayPause.setText("Pause");
                isTimerRunning = true;
            }
        }else{
            //start from scratch.
            Log.d("TimerFragment", "Starting timer. Min: " + currentMinutes + " Secs: " + currentSeconds);
            isTimerOn = true;
            txtClockPlayPause.setText("Pause");
            txtClockPlayPause.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            long runForMillis = ((currentMinutes * 60)  + currentSeconds) * 1000;
            initCountDown(runForMillis);
            countDownTimer.start();
            isTimerRunning = true;
        }
    }

    public void initCountDown(long milliseconds){
        countDownTimer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingMilliseconds = millisUntilFinished;
                String timerString = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                );
                txtTimerDisplay.setText(timerString);
            }

            public void onFinish() {
                txtTimerDisplay.setText("00:00");
                txtClockPlayPause.setText("Repeat");
                isTimerRunning = false;
                isTimerOn = false;
                chime();
            }
        };
    }
    public void chime(){
        final MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.pause();
        soundPlayer.playSound(getContext(), SoundPlayer.CHIME1);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        mainActivity.resume();
                    }
                },
                7000
        );

    }
    public void acceptPicker(View view) {
        if (currentSeconds > 0 || currentMinutes > 0) {
            Log.d("TimerFragment", "Current Timer Pick Min: " + currentMinutes + " Secs: " + currentSeconds);
            String timerString = String.format("%02d:%02d", currentMinutes,currentSeconds);
            txtTimerDisplay.setText(timerString);
            showTimerControls();
        }
    }

    public void cancelPicker(View view) {
        currentMinutes = 0;
        currentSeconds = 0;
        closeTimerContols();
    }

    public void setClockPickerValue(View view, String unit, int value) {
        switch (unit) {
            case UNIT_MINUTES:
                currentMinutes = value;
                allPickersWhite(mapPickerMinutes);
                ((TextView)view).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPurple));
                break;
            case UNIT_SECONDS:
                currentSeconds = value;
                allPickersWhite(mapPickerSeconds);
                ((TextView)view).setTextColor(ContextCompat.getColor(getContext(), R.color.colorPurple));
                break;
            default:
                break;
        }

    }

    public void allPickersWhite(HashMap<Integer, TextView> mapOfElements){
        for(int key : mapOfElements.keySet()){
            mapOfElements.get(key).setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        }
    }


    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
}
